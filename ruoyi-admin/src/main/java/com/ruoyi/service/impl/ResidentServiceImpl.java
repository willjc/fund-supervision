package com.ruoyi.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.BedAllocation;
import com.ruoyi.domain.ElderAttachment;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.OrderItem;
import com.ruoyi.domain.PaymentRecord;
import com.ruoyi.domain.RenewDTO;
import com.ruoyi.domain.vo.ElderCurrentPriceVO;
import com.ruoyi.domain.vo.ResidentVO;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.domain.pension.ExpenseRecord;
import com.ruoyi.service.pension.IFundTransferService;
import com.ruoyi.mapper.BedAllocationMapper;
import com.ruoyi.mapper.ElderAttachmentMapper;
import com.ruoyi.mapper.ElderInfoMapper;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.OrderItemMapper;
import com.ruoyi.mapper.PaymentRecordMapper;
import com.ruoyi.mapper.pension.AccountInfoMapper;
import com.ruoyi.mapper.pension.ExpenseRecordMapper;
import com.ruoyi.mapper.pension.FundTransferMapper;
import com.ruoyi.mapper.ResidentMapper;
import com.ruoyi.service.IResidentService;

/**
 * 入住人Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-11
 */
@Service
public class ResidentServiceImpl implements IResidentService
{
    @Autowired
    private ResidentMapper residentMapper;

    @Autowired
    private ElderInfoMapper elderInfoMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private BedAllocationMapper bedAllocationMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private ElderAttachmentMapper elderAttachmentMapper;

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    @Autowired
    private ExpenseRecordMapper expenseRecordMapper;

    @Autowired
    private FundTransferMapper fundTransferMapper;

    @Autowired
    private IFundTransferService fundTransferService;

    /**
     * 查询入住人列表
     *
     * @param queryVO 查询条件
     * @return 入住人列表
     */
    @Override
    public List<ResidentVO> selectResidentList(ResidentVO queryVO)
    {
        List<ResidentVO> list = residentMapper.selectResidentList(queryVO);
        if (list != null && !list.isEmpty()) {
            // 批量查询待划拨数量
            List<Long> elderIds = new java.util.ArrayList<>();
            for (ResidentVO resident : list) {
                if (resident.getElderId() != null) {
                    elderIds.add(resident.getElderId());
                }
            }
            if (!elderIds.isEmpty()) {
                List<java.util.Map<String, Object>> countList = fundTransferMapper.countPendingByElderIds(elderIds);
                if (countList != null) {
                    // 转换为 Map<Long, Integer>
                    Map<Long, Integer> pendingCountMap = new java.util.HashMap<>();
                    for (java.util.Map<String, Object> item : countList) {
                        Long elderId = Long.valueOf(item.get("elder_id").toString());
                        Integer count = Integer.valueOf(item.get("pending_count").toString());
                        pendingCountMap.put(elderId, count);
                    }
                    // 设置到每个老人对象
                    for (ResidentVO resident : list) {
                        Integer count = pendingCountMap.get(resident.getElderId());
                        resident.setPendingCount(count != null ? count : 0);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 查询入住人详细信息
     *
     * @param elderId 老人ID
     * @return 入住人详细信息
     */
    @Override
    public ResidentVO selectResidentDetail(Long elderId)
    {
        ResidentVO residentVO = residentMapper.selectResidentDetail(elderId);
        if (residentVO != null) {
            // 查询订单列表
            List<OrderInfo> orders = residentMapper.selectOrdersByElderId(elderId);

            // 填充每个订单的订单明细
            if (orders != null && !orders.isEmpty()) {
                for (OrderInfo order : orders) {
                    List<OrderItem> items = orderItemMapper.selectOrderItemsByOrderId(order.getOrderId());
                    order.setOrderItems(items);
                }
            }
            residentVO.setOrders(orders);

            // 查询老人照片（从elder_info表）
            ElderInfo elderInfo = elderInfoMapper.selectElderInfoByElderId(elderId);
            if (elderInfo != null) {
                if (elderInfo.getPhotoPath() != null) {
                    residentVO.setPhotoPath(elderInfo.getPhotoPath());
                }
                // 查询身份证照片（从elder_info表）
                if (elderInfo.getIdCardFrontPath() != null) {
                    residentVO.setIdCardFrontPath(elderInfo.getIdCardFrontPath());
                }
                if (elderInfo.getIdCardBackPath() != null) {
                    residentVO.setIdCardBackPath(elderInfo.getIdCardBackPath());
                }
            }
        }
        return residentVO;
    }

    /**
     * 入住人续费
     *
     * @param renewDTO 续费信息
     * @param userId 操作用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int renewResident(RenewDTO renewDTO, Long userId)
    {
        // 1. 查询老人信息
        ElderInfo elderInfo = elderInfoMapper.selectElderInfoByElderId(renewDTO.getElderId());
        if (elderInfo == null) {
            throw new RuntimeException("老人信息不存在");
        }

        // 2. 查询老人的机构ID(从已存在的订单中获取)
        OrderInfo existingOrder = orderInfoMapper.selectOrdersByElderId(renewDTO.getElderId()).stream()
                .findFirst()
                .orElse(null);

        if (existingOrder == null) {
            throw new RuntimeException("无法获取机构信息,请联系管理员");
        }

        // 3. 查询床位分配信息,获取月服务费
        BedAllocation bedAllocation = bedAllocationMapper.selectBedAllocationByElderId(renewDTO.getElderId());
        if (bedAllocation == null) {
            throw new RuntimeException("未找到床位分配信息");
        }

        // 判断是否为用户端支付
        boolean isOnlinePayment = "online".equals(renewDTO.getPaymentMethod());

        // 4. 获取老人当前有效价格（在创建订单之前获取，避免查询到正在创建的订单）
        // 优先使用审核时修改的价格，如果没有则使用床位表的原始价格
        ElderCurrentPriceVO currentPrice = getCurrentPrice(renewDTO.getElderId());
        BigDecimal currentBedFee = currentPrice.getBedFee() != null ? currentPrice.getBedFee() : BigDecimal.ZERO;
        BigDecimal currentCareFee = currentPrice.getCareFee() != null ? currentPrice.getCareFee() : BigDecimal.ZERO;
        BigDecimal currentMealFee = currentPrice.getMealFee() != null ? currentPrice.getMealFee() : BigDecimal.ZERO;
        // 当前月服务费 = 床位费 + 护理费 + 餐费
        BigDecimal monthlyServiceFee = currentBedFee.add(currentCareFee).add(currentMealFee);

        // 5. 计算订单金额
        // 应收总计 = (床位费 + 护理费 + 餐费) × 续费月数 + 补交押金 + 补交会员费
        BigDecimal serviceFeeTotal = BigDecimal.ZERO;
        if (renewDTO.getMonthCount() != null && renewDTO.getMonthCount() > 0) {
            serviceFeeTotal = monthlyServiceFee.multiply(new BigDecimal(renewDTO.getMonthCount()));
        }

        BigDecimal depositAmount = renewDTO.getDepositAmount() != null ? renewDTO.getDepositAmount() : BigDecimal.ZERO;
        BigDecimal memberFee = renewDTO.getMemberFee() != null ? renewDTO.getMemberFee() : BigDecimal.ZERO;
        BigDecimal calculatedTotal = serviceFeeTotal.add(depositAmount).add(memberFee);

        // 实收总计(用户可手动调整)
        BigDecimal finalAmount = renewDTO.getFinalAmount() != null ? renewDTO.getFinalAmount() : calculatedTotal;

        // 优惠金额 = 应收总计 - 实收总计
        BigDecimal discountAmount = calculatedTotal.subtract(finalAmount);
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            discountAmount = BigDecimal.ZERO;
        }

        // 5. 生成订单编号
        String orderNo = "ORD" + System.currentTimeMillis();

        // 6. 创建订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setOrderType("2"); // 订单类型:2-续费
        orderInfo.setElderId(renewDTO.getElderId());
        orderInfo.setInstitutionId(existingOrder.getInstitutionId());
        orderInfo.setOrderAmount(finalAmount); // 订单金额 = 实收总计
        orderInfo.setOriginalAmount(calculatedTotal); // 原始金额 = 应收总计
        orderInfo.setDiscountAmount(discountAmount); // 优惠金额

        // 用户端支付时状态为待支付(0)，其他支付方式直接已支付(1)
        if (isOnlinePayment) {
            orderInfo.setOrderStatus("0"); // 待支付，用户在H5端支付
            orderInfo.setPaymentMethod("online"); // 用户端支付
        } else {
            orderInfo.setOrderStatus("1"); // 已支付
            orderInfo.setPaidAmount(finalAmount);
            orderInfo.setPaymentMethod(renewDTO.getPaymentMethod());
            orderInfo.setPaymentTime(DateUtils.getNowDate());
        }

        orderInfo.setOrderDate(new Date());
        orderInfo.setBillingCycle("月度");
        orderInfo.setRemark(renewDTO.getRemark());
        orderInfo.setCreateTime(DateUtils.getNowDate());
        orderInfo.setCreateBy(SecurityUtils.getUsername());
        orderInfo.setCreatorUserId(userId); // 设置创建者ID，用于H5端支付验证

        // 7. 如果续费月数>0,需要设置服务日期和月数
        // 注意：用户端支付时，不更新到期日期，等用户支付成功后再更新
        if (renewDTO.getMonthCount() != null && renewDTO.getMonthCount() > 0) {
            // 计算服务起始日期(从当前到期日期开始,如果没有则从今天开始)
            Date serviceStartDate = bedAllocation.getDueDate();
            if (serviceStartDate == null) {
                serviceStartDate = new Date();
            }

            // 计算服务结束日期 = 起始日期 + 续费月数
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(serviceStartDate);
            calendar.add(Calendar.MONTH, renewDTO.getMonthCount());
            Date serviceEndDate = calendar.getTime();

            // 设置订单的服务日期和月数
            orderInfo.setServiceStartDate(serviceStartDate);
            orderInfo.setServiceEndDate(serviceEndDate);
            orderInfo.setMonthCount(renewDTO.getMonthCount());

            // 只有非用户端支付才立即更新到期日期
            if (!isOnlinePayment) {
                // 更新床位分配的到期日期
                bedAllocation.setDueDate(serviceEndDate);
                bedAllocation.setUpdateTime(DateUtils.getNowDate());
                bedAllocation.setUpdateBy(SecurityUtils.getUsername());
                bedAllocationMapper.updateBedAllocation(bedAllocation);
            }
        }

        orderInfoMapper.insertOrderInfo(orderInfo);
        Long orderId = orderInfo.getOrderId();

        // 8. 创建订单明细(支持多个明细项)
        // 8.1 如果有服务费续费，分别创建床位费和护理费明细
        if (renewDTO.getMonthCount() != null && renewDTO.getMonthCount() > 0 && serviceFeeTotal.compareTo(BigDecimal.ZERO) > 0) {
            // 使用前面已获取的当前价格明细（床位费和护理费分开）
            BigDecimal bedFee = currentBedFee;
            BigDecimal careFee = currentCareFee;

            // 创建床位费明细
            if (bedFee.compareTo(BigDecimal.ZERO) > 0) {
                OrderItem bedItem = new OrderItem();
                bedItem.setOrderId(orderId);
                bedItem.setOrderNo(orderNo);
                bedItem.setItemType("bed_fee");
                bedItem.setItemName("床位费");
                bedItem.setItemDescription("床位费" + renewDTO.getMonthCount() + "个月");
                bedItem.setUnitPrice(bedFee);
                bedItem.setQuantity(renewDTO.getMonthCount().longValue());
                bedItem.setTotalAmount(bedFee.multiply(new BigDecimal(renewDTO.getMonthCount())));
                bedItem.setServicePeriod(renewDTO.getMonthCount() + "个月");
                bedItem.setCreateTime(DateUtils.getNowDate());
                bedItem.setCreateBy(SecurityUtils.getUsername());

                // 如果价格被修改过，保存原始价格
                if (currentPrice.getBedFeeModified() != null && currentPrice.getBedFeeModified() && currentPrice.getBedFeeOriginal() != null) {
                    bedItem.setIsPriceModified("1");
                    bedItem.setOriginalUnitPrice(currentPrice.getBedFeeOriginal());
                }

                orderItemMapper.insertOrderItem(bedItem);
            }

            // 创建护理费明细
            if (careFee.compareTo(BigDecimal.ZERO) > 0) {
                OrderItem careItem = new OrderItem();
                careItem.setOrderId(orderId);
                careItem.setOrderNo(orderNo);
                careItem.setItemType("care_fee");
                careItem.setItemName("护理费");
                careItem.setItemDescription("护理费" + renewDTO.getMonthCount() + "个月");
                careItem.setUnitPrice(careFee);
                careItem.setQuantity(renewDTO.getMonthCount().longValue());
                careItem.setTotalAmount(careFee.multiply(new BigDecimal(renewDTO.getMonthCount())));
                careItem.setServicePeriod(renewDTO.getMonthCount() + "个月");
                careItem.setCreateTime(DateUtils.getNowDate());
                careItem.setCreateBy(SecurityUtils.getUsername());

                // 如果价格被修改过，保存原始价格
                if (currentPrice.getCareFeeModified() != null && currentPrice.getCareFeeModified() && currentPrice.getCareFeeOriginal() != null) {
                    careItem.setIsPriceModified("1");
                    careItem.setOriginalUnitPrice(currentPrice.getCareFeeOriginal());
                }

                orderItemMapper.insertOrderItem(careItem);
            }

            // 创建餐费明细
            if (currentMealFee.compareTo(BigDecimal.ZERO) > 0) {
                OrderItem mealItem = new OrderItem();
                mealItem.setOrderId(orderId);
                mealItem.setOrderNo(orderNo);
                mealItem.setItemType("meal_fee");
                mealItem.setItemName("餐费");
                mealItem.setItemDescription("餐费" + renewDTO.getMonthCount() + "个月");
                mealItem.setUnitPrice(currentMealFee);
                mealItem.setQuantity(renewDTO.getMonthCount().longValue());
                mealItem.setTotalAmount(currentMealFee.multiply(new BigDecimal(renewDTO.getMonthCount())));
                mealItem.setServicePeriod(renewDTO.getMonthCount() + "个月");
                mealItem.setCreateTime(DateUtils.getNowDate());
                mealItem.setCreateBy(SecurityUtils.getUsername());

                // 如果价格被修改过，保存原始价格
                if (currentPrice.getMealFeeModified() != null && currentPrice.getMealFeeModified() && currentPrice.getMealFeeOriginal() != null) {
                    mealItem.setIsPriceModified("1");
                    mealItem.setOriginalUnitPrice(currentPrice.getMealFeeOriginal());
                }

                orderItemMapper.insertOrderItem(mealItem);
            }
        }

        // 8.2 如果有押金补缴
        if (depositAmount.compareTo(BigDecimal.ZERO) > 0) {
            OrderItem depositItem = new OrderItem();
            depositItem.setOrderId(orderId);
            depositItem.setOrderNo(orderNo);
            depositItem.setItemType("deposit");
            depositItem.setItemName("押金");
            depositItem.setItemDescription("押金补缴");
            depositItem.setUnitPrice(depositAmount);
            depositItem.setQuantity(1L);
            depositItem.setTotalAmount(depositAmount);
            depositItem.setServicePeriod("一次性");
            depositItem.setCreateTime(DateUtils.getNowDate());
            depositItem.setCreateBy(SecurityUtils.getUsername());
            orderItemMapper.insertOrderItem(depositItem);
        }

        // 8.3 如果有会员费补缴
        if (memberFee.compareTo(BigDecimal.ZERO) > 0) {
            OrderItem memberItem = new OrderItem();
            memberItem.setOrderId(orderId);
            memberItem.setOrderNo(orderNo);
            memberItem.setItemType("member_fee");
            memberItem.setItemName("会员费");
            memberItem.setItemDescription("会员卡充值");
            memberItem.setUnitPrice(memberFee);
            memberItem.setQuantity(1L);
            memberItem.setTotalAmount(memberFee);
            memberItem.setServicePeriod("一次性");
            memberItem.setCreateTime(DateUtils.getNowDate());
            memberItem.setCreateBy(SecurityUtils.getUsername());
            orderItemMapper.insertOrderItem(memberItem);
        }

        // 9. 以下操作仅对非用户端支付执行
        if (!isOnlinePayment) {
            // 9.1 创建支付记录
            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setPaymentNo("PAY" + System.currentTimeMillis()); // 支付流水号
            paymentRecord.setOrderId(orderId);
            paymentRecord.setElderId(renewDTO.getElderId());
            paymentRecord.setInstitutionId(existingOrder.getInstitutionId());
            paymentRecord.setPaymentAmount(finalAmount); // 支付金额 = 实收总计
            paymentRecord.setPaymentMethod(renewDTO.getPaymentMethod()); // 支付方式
            paymentRecord.setPaymentStatus("1"); // 支付状态:1-成功
            paymentRecord.setPaymentTime(DateUtils.getNowDate()); // 支付时间
            paymentRecord.setOperator(SecurityUtils.getUsername()); // 操作人
            paymentRecord.setRemark("续费支付");
            paymentRecord.setCreateTime(DateUtils.getNowDate());
            paymentRecord.setCreateBy(SecurityUtils.getUsername());
            paymentRecordMapper.insertPaymentRecord(paymentRecord);

            // 9.2 更新账户余额（如果有充值金额）
            if (finalAmount.compareTo(BigDecimal.ZERO) > 0) {
                // 查询或创建账户
                AccountInfo account = accountInfoMapper.selectAccountInfoByElderId(renewDTO.getElderId());
                if (account == null) {
                    // 创建新账户
                    account = new AccountInfo();
                    account.setElderId(renewDTO.getElderId());
                    account.setServiceBalance(BigDecimal.ZERO);
                    account.setDepositBalance(BigDecimal.ZERO);
                    account.setMemberBalance(BigDecimal.ZERO);
                    account.setTotalBalance(BigDecimal.ZERO);
                    account.setCreateTime(DateUtils.getNowDate());
                    account.setCreateBy(SecurityUtils.getUsername());
                    accountInfoMapper.insertAccountInfo(account);
                }

                // 计算更新前的余额
                BigDecimal oldServiceBalance = account.getServiceBalance() != null ? account.getServiceBalance() : BigDecimal.ZERO;
                BigDecimal oldDepositBalance = account.getDepositBalance() != null ? account.getDepositBalance() : BigDecimal.ZERO;
                BigDecimal oldMemberBalance = account.getMemberBalance() != null ? account.getMemberBalance() : BigDecimal.ZERO;

                // 计算各项金额
                BigDecimal serviceAmount = serviceFeeTotal;
                BigDecimal renewDepositAmount = renewDTO.getDepositAmount() != null ? renewDTO.getDepositAmount() : BigDecimal.ZERO;
                BigDecimal memberAmount = renewDTO.getMemberFee() != null ? renewDTO.getMemberFee() : BigDecimal.ZERO;

                // 更新账户余额
                account.setServiceBalance(oldServiceBalance.add(serviceAmount));
                account.setDepositBalance(oldDepositBalance.add(renewDepositAmount));
                account.setMemberBalance(oldMemberBalance.add(memberAmount));
                account.setTotalBalance(account.getServiceBalance().add(account.getDepositBalance()));
                account.setUpdateTime(DateUtils.getNowDate());
                account.setUpdateBy(SecurityUtils.getUsername());
                accountInfoMapper.updateAccountInfo(account);
            }

            // 9.3 生成费用记录
            if (finalAmount.compareTo(BigDecimal.ZERO) > 0) {
                // 服务费充值记录
                if (serviceFeeTotal.compareTo(BigDecimal.ZERO) > 0) {
                    ExpenseRecord serviceRecord = new ExpenseRecord();
                    serviceRecord.setElderId(renewDTO.getElderId());
                    serviceRecord.setExpenseType("service");
                    serviceRecord.setTransactionType("income");
                    serviceRecord.setAmount(serviceFeeTotal);
                    serviceRecord.setDescription("续费充值：" + renewDTO.getMonthCount() + "个月服务费");
                    serviceRecord.setRelatedId(orderId);
                    serviceRecord.setCreateTime(DateUtils.getNowDate());
                    serviceRecord.setCreateBy(SecurityUtils.getUsername());
                    expenseRecordMapper.insertExpenseRecord(serviceRecord);
                }

                // 押金补缴记录
                BigDecimal depositRecordAmount = renewDTO.getDepositAmount() != null ? renewDTO.getDepositAmount() : BigDecimal.ZERO;
                if (depositRecordAmount.compareTo(BigDecimal.ZERO) > 0) {
                    ExpenseRecord depositRecord = new ExpenseRecord();
                    depositRecord.setElderId(renewDTO.getElderId());
                    depositRecord.setExpenseType("deposit");
                    depositRecord.setTransactionType("income");
                    depositRecord.setAmount(depositRecordAmount);
                    depositRecord.setDescription("押金补缴");
                    depositRecord.setRelatedId(orderId);
                    depositRecord.setCreateTime(DateUtils.getNowDate());
                    depositRecord.setCreateBy(SecurityUtils.getUsername());
                    expenseRecordMapper.insertExpenseRecord(depositRecord);
                }

                // 会员费补缴记录
                BigDecimal memberAmount = renewDTO.getMemberFee() != null ? renewDTO.getMemberFee() : BigDecimal.ZERO;
                if (memberAmount.compareTo(BigDecimal.ZERO) > 0) {
                    ExpenseRecord memberRecord = new ExpenseRecord();
                    memberRecord.setElderId(renewDTO.getElderId());
                    memberRecord.setExpenseType("member");
                    memberRecord.setTransactionType("income");
                    memberRecord.setAmount(memberAmount);
                    memberRecord.setDescription("会员费补缴");
                    memberRecord.setRelatedId(orderId);
                    memberRecord.setCreateTime(DateUtils.getNowDate());
                    memberRecord.setCreateBy(SecurityUtils.getUsername());
                    expenseRecordMapper.insertExpenseRecord(memberRecord);
                }
            }

            // 9.4 生成续费订单的拨付单（线下支付时）
            if (renewDTO.getMonthCount() != null && renewDTO.getMonthCount() > 0) {
                try {
                    // 计算月服务费
                    BigDecimal monthlyFee = serviceFeeTotal
                        .divide(new BigDecimal(renewDTO.getMonthCount()), 2, java.math.RoundingMode.HALF_UP);

                    // 获取服务起始日期
                    Date transferStartDate = orderInfo.getServiceStartDate();
                    if (transferStartDate == null) {
                        transferStartDate = bedAllocation.getDueDate() != null ? bedAllocation.getDueDate() : new Date();
                    }

                    // 生成续费月份的拨付单（从service_start_date所在月份开始）
                    fundTransferService.generateMonthlyTransfersForOrder(
                        orderId,
                        existingOrder.getInstitutionId(),
                        renewDTO.getElderId(),
                        renewDTO.getMonthCount(),
                        transferStartDate,
                        monthlyFee,
                        true // 从当月开始，不跳过首月
                    );
                } catch (Exception e) {
                    // 拨付单生成失败不影响主流程
                    throw new RuntimeException("续费拨付单生成失败：" + e.getMessage());
                }
            }
        }

        return 1;
    }

    /**
     * 查询入住人统计数据
     *
     * @return 统计数据
     */
    @Override
    public Map<String, Object> getResidentStatistics()
    {
        return residentMapper.selectResidentStatistics();
    }

    /**
     * 获取老人当前有效价格
     * 优先从最近已支付订单中获取修改后的价格
     *
     * @param elderId 老人ID
     * @return 当前价格信息
     */
    @Override
    public ElderCurrentPriceVO getCurrentPrice(Long elderId)
    {
        ElderCurrentPriceVO priceVO = new ElderCurrentPriceVO();
        priceVO.setElderId(elderId);

        // 1. 查询床位分配信息，获取原始月服务费
        BedAllocation bedAllocation = bedAllocationMapper.selectBedAllocationByElderId(elderId);
        BigDecimal originalMonthlyFee = bedAllocation != null && bedAllocation.getMonthlyFee() != null
            ? bedAllocation.getMonthlyFee() : BigDecimal.ZERO;

        // 2. 查询最近已支付的订单（获取修改后的价格）
        OrderInfo queryOrder = new OrderInfo();
        queryOrder.setElderId(elderId);
        queryOrder.setOrderStatus("1"); // 已支付
        List<OrderInfo> paidOrders = orderInfoMapper.selectOrderInfoList(queryOrder);

        BigDecimal currentBedFee = null;
        BigDecimal currentCareFee = null;
        BigDecimal currentMealFee = null;
        BigDecimal currentDeposit = null;
        BigDecimal currentMemberFee = null;
        BigDecimal bedFeeOriginal = null;
        BigDecimal careFeeOriginal = null;
        BigDecimal mealFeeOriginal = null;
        BigDecimal depositOriginal = null;
        BigDecimal memberFeeOriginal = null;
        Boolean bedFeeModified = false;
        Boolean careFeeModified = false;
        Boolean mealFeeModified = false;
        Boolean depositModified = false;
        Boolean memberFeeModified = false;
        String lastPaymentTime = null;

        // 遍历订单，获取最近一次支付的价格信息
        if (paidOrders != null && !paidOrders.isEmpty()) {
            // 按优先级排序：
            // 1. 优先选择有价格变更记录的订单（订单明细中is_price_modified='1'的订单）
            // 2. 如果都有或都没有价格变更记录，则按支���时间降序排序
            paidOrders.sort((o1, o2) -> {
                // 首先按支付时间降序排序
                int timeCompare = compareByPaymentTime(o1, o2);
                if (timeCompare != 0) {
                    return timeCompare;
                }
                // 支付时间相同，按订单ID降序排序（ID越大越新）
                return o2.getOrderId().compareTo(o1.getOrderId());
            });

            // 查找有价格变更记录的最新订单
            OrderInfo lastPaidOrder = null;
            for (OrderInfo order : paidOrders) {
                List<OrderItem> items = orderItemMapper.selectOrderItemsByOrderId(order.getOrderId());
                if (items != null && !items.isEmpty()) {
                    boolean hasPriceModified = items.stream()
                        .anyMatch(item -> "1".equals(item.getIsPriceModified()));
                    if (hasPriceModified) {
                        lastPaidOrder = order;
                        break;
                    }
                }
            }
            // 如果没有找到有价格变更记录的订单，使用第一个（支付时间最新的）
            if (lastPaidOrder == null) {
                lastPaidOrder = paidOrders.get(0);
            }
            if (lastPaidOrder.getPaymentTime() != null) {
                lastPaymentTime = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, lastPaidOrder.getPaymentTime());
            }

            // 获取订单明细
            List<OrderItem> items = orderItemMapper.selectOrderItemsByOrderId(lastPaidOrder.getOrderId());
            if (items != null) {
                for (OrderItem item : items) {
                    if ("bed_fee".equals(item.getItemType())) {
                        currentBedFee = item.getUnitPrice();
                        if ("1".equals(item.getIsPriceModified()) && item.getOriginalUnitPrice() != null) {
                            bedFeeOriginal = item.getOriginalUnitPrice();
                            bedFeeModified = true;
                        }
                    } else if ("care_fee".equals(item.getItemType())) {
                        currentCareFee = item.getUnitPrice();
                        if ("1".equals(item.getIsPriceModified()) && item.getOriginalUnitPrice() != null) {
                            careFeeOriginal = item.getOriginalUnitPrice();
                            careFeeModified = true;
                        }
                    } else if ("meal_fee".equals(item.getItemType())) {
                        currentMealFee = item.getUnitPrice();
                        if ("1".equals(item.getIsPriceModified()) && item.getOriginalUnitPrice() != null) {
                            mealFeeOriginal = item.getOriginalUnitPrice();
                            mealFeeModified = true;
                        }
                    } else if ("deposit".equals(item.getItemType())) {
                        currentDeposit = item.getUnitPrice();
                        if ("1".equals(item.getIsPriceModified()) && item.getOriginalUnitPrice() != null) {
                            depositOriginal = item.getOriginalUnitPrice();
                            depositModified = true;
                        }
                    } else if ("member_fee".equals(item.getItemType())) {
                        currentMemberFee = item.getUnitPrice();
                        if ("1".equals(item.getIsPriceModified()) && item.getOriginalUnitPrice() != null) {
                            memberFeeOriginal = item.getOriginalUnitPrice();
                            memberFeeModified = true;
                        }
                    }
                }
            }
        }

        // 3. 如果没有找到订单价格，使用床位分配表的月服务费作为默认值
        if (currentBedFee == null && currentCareFee == null) {
            // 没有历史订单，使用床位分配表的月服务费
            // 这里需要拆分床位费和护理费，暂时按默认比例拆分（床位费60%，护理费40%）
            if (originalMonthlyFee.compareTo(BigDecimal.ZERO) > 0) {
                currentBedFee = originalMonthlyFee.multiply(new BigDecimal("0.6"));
                currentCareFee = originalMonthlyFee.multiply(new BigDecimal("0.4"));
            }
        }

        // 4. 设置返回值
        priceVO.setBedFee(currentBedFee != null ? currentBedFee : BigDecimal.ZERO);
        priceVO.setBedFeeOriginal(bedFeeOriginal);
        priceVO.setBedFeeModified(bedFeeModified);

        priceVO.setCareFee(currentCareFee != null ? currentCareFee : BigDecimal.ZERO);
        priceVO.setCareFeeOriginal(careFeeOriginal);
        priceVO.setCareFeeModified(careFeeModified);

        priceVO.setMealFee(currentMealFee != null ? currentMealFee : BigDecimal.ZERO);
        priceVO.setMealFeeOriginal(mealFeeOriginal);
        priceVO.setMealFeeModified(mealFeeModified);

        priceVO.setDepositFee(currentDeposit != null ? currentDeposit : BigDecimal.ZERO);
        priceVO.setDepositFeeOriginal(depositOriginal);
        priceVO.setDepositFeeModified(depositModified);

        priceVO.setMemberFee(currentMemberFee != null ? currentMemberFee : BigDecimal.ZERO);
        priceVO.setMemberFeeOriginal(memberFeeOriginal);
        priceVO.setMemberFeeModified(memberFeeModified);

        // 计算月服务费总计（床位费+护理费+餐费）
        BigDecimal monthlyTotal = BigDecimal.ZERO;
        if (priceVO.getBedFee() != null) {
            monthlyTotal = monthlyTotal.add(priceVO.getBedFee());
        }
        if (priceVO.getCareFee() != null) {
            monthlyTotal = monthlyTotal.add(priceVO.getCareFee());
        }
        if (priceVO.getMealFee() != null) {
            monthlyTotal = monthlyTotal.add(priceVO.getMealFee());
        }
        priceVO.setMonthlyFeeTotal(monthlyTotal);
        priceVO.setLastPaymentTime(lastPaymentTime);

        return priceVO;
    }

    /**
     * 删除入住人
     *
     * @param residentId 入住人ID(老人ID)
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteResident(Long residentId)
    {
        // 1. 检查是否有未支付的订单
        OrderInfo queryOrder = new OrderInfo();
        queryOrder.setElderId(residentId);
        queryOrder.setOrderStatus("0"); // 未支付
        List<OrderInfo> unpaidOrders = orderInfoMapper.selectOrderInfoList(queryOrder);
        if (unpaidOrders != null && !unpaidOrders.isEmpty()) {
            throw new RuntimeException("该入住人存在未支付订单，无法删除！请先处理未支付订单。");
        }

        // 2. 检查是否有余额
        ResidentVO residentVO = residentMapper.selectResidentDetail(residentId);
        if (residentVO != null) {
            BigDecimal serviceBalance = residentVO.getServiceBalance() != null ? residentVO.getServiceBalance() : BigDecimal.ZERO;
            BigDecimal depositBalance = residentVO.getDepositBalance() != null ? residentVO.getDepositBalance() : BigDecimal.ZERO;
            BigDecimal memberBalance = residentVO.getMemberBalance() != null ? residentVO.getMemberBalance() : BigDecimal.ZERO;

            if (serviceBalance.compareTo(BigDecimal.ZERO) > 0 ||
                depositBalance.compareTo(BigDecimal.ZERO) > 0 ||
                memberBalance.compareTo(BigDecimal.ZERO) > 0) {
                throw new RuntimeException("该入住人存在余额，无法删除！请先办理退费。");
            }
        }

        // 3. 释放床位（将床位分配记录设置为已结束）
        BedAllocation allocation = bedAllocationMapper.selectBedAllocationByElderId(residentId);
        if (allocation != null) {
            if ("0".equals(allocation.getAllocationStatus()) || "1".equals(allocation.getAllocationStatus())) {
                // 将待入住或已入住状态改为已结束
                allocation.setAllocationStatus("2");
                allocation.setCheckOutDate(new Date());
                bedAllocationMapper.updateBedAllocation(allocation);
            }
        }

        // 4. 删除老人信息
        return elderInfoMapper.deleteElderInfoByElderId(residentId);
    }

    /**
     * 查询老人的拨付单列表
     *
     * @param elderId 老人ID
     * @return 拨付单列表
     */
    @Override
    public List<FundTransfer> selectTransfersByElderId(Long elderId)
    {
        return residentMapper.selectTransfersByElderId(elderId);
    }

    /**
     * 按支付时间比较两个订单（降序）
     * 支付时间为null的排在后面
     */
    private int compareByPaymentTime(OrderInfo o1, OrderInfo o2) {
        if (o1.getPaymentTime() == null && o2.getPaymentTime() == null) {
            return 0;
        }
        if (o1.getPaymentTime() == null) {
            return 1; // o1的支付时间为null，排后面
        }
        if (o2.getPaymentTime() == null) {
            return -1; // o2的支付时间为null，排后面
        }
        return o2.getPaymentTime().compareTo(o1.getPaymentTime()); // 降序排序
    }
}
