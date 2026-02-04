package com.ruoyi.service.impl;

import java.util.List;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigDecimal;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.OrderItemMapper;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.OrderItem;
import com.ruoyi.domain.ElderCheckIn;
import com.ruoyi.domain.PaymentRecord;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.service.IOrderInfoService;
import com.ruoyi.service.IElderCheckInService;
import com.ruoyi.service.IPaymentRecordService;
import com.ruoyi.service.pension.IAccountInfoService;
import com.ruoyi.service.pension.IExpenseRecordService;

/**
 * 订单主表Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@Service
public class OrderInfoServiceImpl implements IOrderInfoService
{
    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private IElderCheckInService elderCheckInService;

    @Autowired
    private IPaymentRecordService paymentRecordService;

    @Autowired
    private IAccountInfoService accountInfoService;

    @Autowired
    private IExpenseRecordService expenseRecordService;

    @Autowired
    private com.ruoyi.service.pension.IFundTransferService fundTransferService;

    /**
     * 查询订单主表
     *
     * @param orderId 订单主表主键
     * @return 订单主表
     */
    @Override
    public OrderInfo selectOrderInfoByOrderId(Long orderId)
    {
        return orderInfoMapper.selectOrderInfoByOrderId(orderId);
    }

    /**
     * 查询订单主表列表
     *
     * @param orderInfo 订单主表
     * @return 订单主表
     */
    @Override
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo)
    {
        return orderInfoMapper.selectOrderInfoList(orderInfo);
    }

    /**
     * 新增订单主表
     *
     * @param orderInfo 订单主表
     * @return 结果
     */
    @Override
    @Transactional
    public int insertOrderInfo(OrderInfo orderInfo)
    {
        // 生成订单号：ORD + 时间戳 + 4位随机数
        String orderNo = "ORD" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
        orderInfo.setOrderNo(orderNo);
        orderInfo.setCreateTime(DateUtils.getNowDate());
        orderInfo.setOrderStatus("1"); // 默认状态为待支付
        orderInfo.setPaidAmount(java.math.BigDecimal.ZERO);
        return orderInfoMapper.insertOrderInfo(orderInfo);
    }

    /**
     * 修改订单主表
     *
     * @param orderInfo 订单主表
     * @return 结果
     */
    @Override
    @Transactional
    public int updateOrderInfo(OrderInfo orderInfo)
    {
        orderInfo.setUpdateTime(DateUtils.getNowDate());

        // 如果是审核通过操作（状态变为5），需要更新订单明细中的价格
        if ("5".equals(orderInfo.getOrderStatus()) && orderInfo.getRemark() != null) {
            updateOrderItemPricesOnAudit(orderInfo);
        }

        return orderInfoMapper.updateOrderInfo(orderInfo);
    }

    /**
     * 审核通过时更新订单明细价格
     * 从remark字段解析新价格，更新到order_item表
     *
     * @param orderInfo 订单信息
     */
    private void updateOrderItemPricesOnAudit(OrderInfo orderInfo) {
        List<OrderItem> items = orderItemMapper.selectOrderItemsByOrderId(orderInfo.getOrderId());
        if (items == null || items.isEmpty()) {
            return;
        }

        String remark = orderInfo.getRemark();

        // 解析新价格
        BigDecimal newBedFee = parsePriceFromRemark(remark, "床位费");
        BigDecimal newCareFee = parsePriceFromRemark(remark, "护理费");
        BigDecimal newMealFee = parsePriceFromRemark(remark, "餐费");
        BigDecimal newDepositFee = parsePriceFromRemark(remark, "押金");
        BigDecimal newMemberFee = parsePriceFromRemark(remark, "会员费");
        Integer newMonthCount = parseMonthCountFromRemark(remark);

        // 检查是否已存在餐费明细
        boolean hasMealFeeItem = items.stream().anyMatch(item -> "meal_fee".equals(item.getItemType()));

        // 如果有餐费但没有对应明细，创建餐费明细
        if (newMealFee != null && newMealFee.compareTo(BigDecimal.ZERO) > 0 && !hasMealFeeItem) {
            OrderItem mealItem = new OrderItem();
            mealItem.setOrderId(orderInfo.getOrderId());
            mealItem.setOrderNo(orderInfo.getOrderNo());
            mealItem.setItemName("餐费");
            mealItem.setItemType("meal_fee");
            mealItem.setItemDescription("餐费-" + parseMealLevelFromRemark(remark));
            mealItem.setUnitPrice(newMealFee);
            mealItem.setOriginalUnitPrice(newMealFee);
            mealItem.setQuantity(newMonthCount != null ? newMonthCount.longValue() : 1L);
            mealItem.setTotalAmount(newMealFee.multiply(new BigDecimal(newMonthCount != null ? newMonthCount : 1)));
            mealItem.setServicePeriod("月度");
            mealItem.setIsPriceModified("0");
            mealItem.setCreateBy(orderInfo.getUpdateBy());
            mealItem.setCreateTime(DateUtils.getNowDate());
            orderItemMapper.insertOrderItem(mealItem);
            // 重新加载明细列表
            items = orderItemMapper.selectOrderItemsByOrderId(orderInfo.getOrderId());
        }

        for (OrderItem item : items) {
            BigDecimal newPrice = null;
            boolean priceChanged = false;

            // 根据item_type确定新价格
            if ("bed_fee".equals(item.getItemType()) && newBedFee != null) {
                newPrice = newBedFee;
                priceChanged = true;
            } else if ("care_fee".equals(item.getItemType()) && newCareFee != null) {
                newPrice = newCareFee;
                priceChanged = true;
            } else if ("deposit".equals(item.getItemType()) && newDepositFee != null) {
                newPrice = newDepositFee;
                priceChanged = true;
            } else if ("member_fee".equals(item.getItemType()) && newMemberFee != null) {
                newPrice = newMemberFee;
                priceChanged = true;
            } else if ("meal_fee".equals(item.getItemType()) && newMealFee != null) {
                newPrice = newMealFee;
                priceChanged = true;
            }

            // 如果价格发生变化，更新订单明细
            if (priceChanged && newPrice != null) {
                // 保存原始单价（如果还没保存过）
                if (item.getOriginalUnitPrice() == null) {
                    item.setOriginalUnitPrice(item.getUnitPrice());
                }

                // 更新新单价
                item.setUnitPrice(newPrice);

                // 重新计算总额
                int quantity = item.getQuantity() != null ? item.getQuantity().intValue() : 1;
                // 如果是床位费、护理费或餐费，且修改了月数，需要更新数量
                if ((newMonthCount != null && newMonthCount > 0) &&
                    ("bed_fee".equals(item.getItemType()) || "care_fee".equals(item.getItemType()) || "meal_fee".equals(item.getItemType()))) {
                    quantity = newMonthCount;
                    item.setQuantity((long) quantity);
                }
                item.setTotalAmount(newPrice.multiply(new BigDecimal(quantity)));

                // 标记价格已修改
                item.setIsPriceModified("1");
                item.setUpdateBy(orderInfo.getUpdateBy());
                item.setUpdateTime(DateUtils.getNowDate());

                orderItemMapper.updateOrderItem(item);
            }
        }
    }

    /**
     * 从remark中解析价格
     *
     * @param remark 费用说明
     * @param feeType 费用类型（床位费、护理费、押金、会员费）
     * @return 解析出的价格
     */
    private BigDecimal parsePriceFromRemark(String remark, String feeType) {
        if (remark == null || !remark.contains(feeType + "：")) {
            return null;
        }
        Pattern pattern = Pattern.compile(feeType + "：([\\d.]+)元");
        Matcher matcher = pattern.matcher(remark);
        if (matcher.find()) {
            try {
                return new BigDecimal(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 从remark中解析缴费月数
     *
     * @param remark 费用说明
     * @return 缴费月数
     */
    private Integer parseMonthCountFromRemark(String remark) {
        if (remark == null || !remark.contains("缴费月数：")) {
            return null;
        }
        Pattern pattern = Pattern.compile("缴费月数：(\\d+)个月");
        Matcher matcher = pattern.matcher(remark);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 从remark中解析餐费档次
     *
     * @param remark 费用说明
     * @return 餐费档次
     */
    private String parseMealLevelFromRemark(String remark) {
        if (remark == null || !remark.contains("餐费档次：")) {
            return "";
        }
        Pattern pattern = Pattern.compile("餐费档次：(\\S+)");
        Matcher matcher = pattern.matcher(remark);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * 批量删除订单主表
     *
     * @param orderIds 需要删除的订单主表主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteOrderInfoByOrderIds(Long[] orderIds)
    {
        return orderInfoMapper.deleteOrderInfoByOrderIds(orderIds);
    }

    /**
     * 删除订单主表信���
     *
     * @param orderId 订单主表主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteOrderInfoByOrderId(Long orderId)
    {
        return orderInfoMapper.deleteOrderInfoByOrderId(orderId);
    }

    /**
     * 根据入住申请ID生成订单
     *
     * @param checkInId 入住申请ID
     * @return 结果
     */
    @Override
    @Transactional
    public int generateOrdersByCheckIn(Long checkInId)
    {
        ElderCheckIn checkIn = elderCheckInService.selectElderCheckInByCheckInId(checkInId);
        if (checkIn == null || !"1".equals(checkIn.getCheckInStatus())) {
            return 0;
        }

        // 生成床位费订单
        if (checkIn.getBedId() != null) {
            OrderInfo bedOrder = new OrderInfo();
            bedOrder.setOrderNo(generateOrderNo("BED"));
            bedOrder.setOrderType("1"); // 床位费
            bedOrder.setElderId(checkIn.getElderId());
            bedOrder.setInstitutionId(checkIn.getInstitutionId());
            bedOrder.setCheckInId(checkInId);
            bedOrder.setBedId(checkIn.getBedId());
            bedOrder.setOrderAmount(checkIn.getMonthlyFee());
            bedOrder.setOrderDate(new Date());
            bedOrder.setServiceStartDate(checkIn.getActualCheckInDate());
            bedOrder.setBillingCycle("monthly");
            bedOrder.setDueDate(checkIn.getActualCheckInDate());
            bedOrder.setCreateBy("system");
            orderInfoMapper.insertOrderInfo(bedOrder);
        }

        // 生成护理费订单
        OrderInfo careOrder = new OrderInfo();
        careOrder.setOrderNo(generateOrderNo("CARE"));
        careOrder.setOrderType("2"); // 护理费
        careOrder.setElderId(checkIn.getElderId());
        careOrder.setInstitutionId(checkIn.getInstitutionId());
        careOrder.setCheckInId(checkInId);
        careOrder.setOrderAmount(new java.math.BigDecimal("1500.00")); // 护理费固定金额
        careOrder.setOrderDate(new Date());
        careOrder.setServiceStartDate(checkIn.getActualCheckInDate());
        careOrder.setBillingCycle("monthly");
        careOrder.setDueDate(checkIn.getActualCheckInDate());
        careOrder.setCreateBy("system");
        orderInfoMapper.insertOrderInfo(careOrder);

        return 1;
    }

    /**
     * 处理订单支付
     *
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     * @return 结果
     */
    @Override
    @Transactional
    public int processOrderPayment(Long orderId, String paymentMethod)
    {
        OrderInfo orderInfo = orderInfoMapper.selectOrderInfoByOrderId(orderId);
        if (orderInfo == null || !"0".equals(orderInfo.getOrderStatus())) {
            return 0;
        }

        // 更新订单状态为已支付
        orderInfo.setOrderStatus("1");
        orderInfo.setPaidAmount(orderInfo.getOrderAmount());
        orderInfo.setPaymentMethod(paymentMethod);
        orderInfo.setPaymentTime(new Date());
        orderInfo.setUpdateTime(DateUtils.getNowDate());

        return orderInfoMapper.updateOrderInfo(orderInfo);
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param cancelReason 取消原因
     * @return 结果
     */
    @Override
    @Transactional
    public int cancelOrder(Long orderId, String cancelReason)
    {
        OrderInfo orderInfo = orderInfoMapper.selectOrderInfoByOrderId(orderId);
        if (orderInfo == null || !"0".equals(orderInfo.getOrderStatus())) {
            return 0;
        }

        orderInfo.setOrderStatus("2"); // 已取消
        orderInfo.setRemark(cancelReason);
        orderInfo.setUpdateTime(DateUtils.getNowDate());

        return orderInfoMapper.updateOrderInfo(orderInfo);
    }

    /**
     * 导出订单主表
     *
     * @param orderInfo 查询条件
     * @return 订单主表列表
     */
    @Override
    public List<OrderInfo> exportOrderInfo(OrderInfo orderInfo)
    {
        return orderInfoMapper.selectOrderInfoList(orderInfo);
    }

    /**
     * 根据订单编号查询订单
     *
     * @param orderNo 订单编号
     * @return 订单信息
     */
    @Override
    public OrderInfo selectOrderInfoByOrderNo(String orderNo)
    {
        return orderInfoMapper.selectOrderInfoByOrderNo(orderNo);
    }

    /**
     * 线下支付订单（管理端使用）
     *
     * @param orderId 订单ID
     * @param paymentMethod 支付方式（现金/刷卡/扫码）
     * @param paymentProof 支付凭证图片URL
     * @param paymentProofRemark 支付凭证备注
     * @return 结果
     */
    @Override
    @Transactional
    public int offlinePayOrder(Long orderId, String paymentMethod, String paymentProof, String paymentProofRemark)
    {
        OrderInfo orderInfo = orderInfoMapper.selectOrderInfoByOrderId(orderId);
        if (orderInfo == null) {
            return 0;
        }

        // 只允许待支付状态（0或5）的订单进行线下支付
        if (!"0".equals(orderInfo.getOrderStatus()) && !"5".equals(orderInfo.getOrderStatus())) {
            return 0;
        }

        // 更新订单状态为已支付
        orderInfo.setOrderStatus("1");
        orderInfo.setPaidAmount(orderInfo.getOrderAmount());
        orderInfo.setPaymentMethod(paymentMethod);
        orderInfo.setPaymentTime(new Date());
        orderInfo.setPaymentProof(paymentProof);
        orderInfo.setPaymentProofRemark(paymentProofRemark);
        orderInfo.setUpdateTime(DateUtils.getNowDate());

        int result = orderInfoMapper.updateOrderInfo(orderInfo);

        // 创建支付记录
        if (result > 0) {
            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setPaymentNo("PAY" + System.currentTimeMillis());
            paymentRecord.setOrderId(orderId);
            paymentRecord.setOrderNo(orderInfo.getOrderNo());
            paymentRecord.setElderId(orderInfo.getElderId());
            paymentRecord.setInstitutionId(orderInfo.getInstitutionId());
            paymentRecord.setPaymentAmount(orderInfo.getOrderAmount());
            paymentRecord.setPaymentMethod(paymentMethod);
            paymentRecord.setPaymentStatus("1"); // 成功
            paymentRecord.setPaymentTime(new Date());
            paymentRecord.setTransactionId("OFFLINE" + System.currentTimeMillis());
            paymentRecord.setGatewayResponse("线下支付");
            paymentRecord.setPaymentProof(paymentProof);
            paymentRecord.setPaymentProofRemark(paymentProofRemark);
            paymentRecord.setOperator("admin");

            paymentRecordService.insertPaymentRecord(paymentRecord);

            // 更新老人账户余额和创建费用记录
            updateAccountAndCreateExpenseRecord(orderInfo);
        }

        return result;
    }

    /**
     * 更新老人账户余额并创建费用记录
     * 严格按照订单明细（order_item）分配费用
     */
    private void updateAccountAndCreateExpenseRecord(OrderInfo orderInfo) {
        try {
            // 获取或创建老人账户
            AccountInfo account = accountInfoService.selectAccountInfoByElderId(orderInfo.getElderId());
            if (account == null) {
                account = accountInfoService.createAccountInfo(
                    orderInfo.getElderId(),
                    orderInfo.getInstitutionId(),
                    BigDecimal.ZERO
                );
            }

            // 从订单明细获取费用分配
            BigDecimal serviceAmount = BigDecimal.ZERO;
            BigDecimal depositAmount = BigDecimal.ZERO;
            BigDecimal memberAmount = BigDecimal.ZERO;
            BigDecimal totalAmount = orderInfo.getOrderAmount() != null ? orderInfo.getOrderAmount() : BigDecimal.ZERO;

            // 查询订单明细
            List<OrderItem> orderItems = orderItemMapper.selectOrderItemsByOrderId(orderInfo.getOrderId());

            if (orderItems != null && !orderItems.isEmpty()) {
                // 严格按照订单明细项的类型分配金额
                for (OrderItem item : orderItems) {
                    BigDecimal itemTotal = item.getTotalAmount() != null ? item.getTotalAmount() : BigDecimal.ZERO;
                    String itemType = item.getItemType();

                    if ("deposit".equals(itemType)) {
                        // 押金类型
                        depositAmount = depositAmount.add(itemTotal);
                    } else if ("member_fee".equals(itemType)) {
                        // 会员费类型
                        memberAmount = memberAmount.add(itemTotal);
                    } else {
                        // 其他类型（床位费、护理费、餐饮费、医疗费等）归入服务费
                        serviceAmount = serviceAmount.add(itemTotal);
                    }
                }
            } else {
                // 如果没有订单明细，全部归入服务费（不使用比例分配）
                serviceAmount = totalAmount;
            }

            // 记录更新前的余额
            BigDecimal balanceBefore = account.getTotalBalance() != null ? account.getTotalBalance() : BigDecimal.ZERO;
            BigDecimal serviceBefore = account.getServiceBalance() != null ? account.getServiceBalance() : BigDecimal.ZERO;
            BigDecimal depositBefore = account.getDepositBalance() != null ? account.getDepositBalance() : BigDecimal.ZERO;
            BigDecimal memberBefore = account.getMemberBalance() != null ? account.getMemberBalance() : BigDecimal.ZERO;

            // 更新账户余额
            account.setTotalBalance(balanceBefore.add(totalAmount));
            account.setServiceBalance(serviceBefore.add(serviceAmount));
            account.setDepositBalance(depositBefore.add(depositAmount));
            account.setMemberBalance(memberBefore.add(memberAmount));
            account.setUpdateTime(new Date());
            account.setRemark(account.getRemark() + " | 线下支付订单" + orderInfo.getOrderNo() + "增加余额" + totalAmount + "元");

            accountInfoService.updateAccountInfo(account);

            // 创建费用记录
            int recordResult = expenseRecordService.createOrderExpenseRecords(
                account.getElderId(),
                account.getAccountId(),
                orderInfo.getOrderId(),
                orderInfo.getOrderType(),
                depositAmount,
                serviceAmount,
                memberAmount,
                BigDecimal.ZERO,
                balanceBefore,
                account.getTotalBalance()
            );

            // 如果是入驻订单，生成拨付单
            if (recordResult > 0 && "1".equals(orderInfo.getOrderType())) {
                generateTransfersForOrder(orderInfo, serviceAmount);
            }

        } catch (Exception e) {
            // 记录错误但不影响支付流程
            throw new RuntimeException("更新账户余��失败: " + e.getMessage(), e);
        }
    }

    /**
     * 为订单生成拨付单
     *
     * @param orderInfo 订单信息
     * @param serviceAmount 服务费金额
     */
    private void generateTransfersForOrder(OrderInfo orderInfo, BigDecimal serviceAmount) {
        try {
            // 计算首月服务费
            BigDecimal firstMonthServiceFee = calculateFirstMonthServiceFee(orderInfo.getOrderId());
            if (firstMonthServiceFee.compareTo(BigDecimal.ZERO) <= 0) {
                // 如果无法从明细计算，使用传入的服务费作为首月服务费
                firstMonthServiceFee = serviceAmount;
            }

            // 生成首月服务费立即划拨的拨付单（已完成状态）
            createFirstMonthTransfer(orderInfo, firstMonthServiceFee);

            // 生成后续月份的拨付单（从次月开始）
            Integer monthCount = orderInfo.getMonthCount() != null ? orderInfo.getMonthCount() : 1;
            if (monthCount > 1) {
                fundTransferService.generateMonthlyTransfersForOrder(
                    orderInfo.getOrderId(),
                    orderInfo.getInstitutionId(),
                    orderInfo.getElderId(),
                    monthCount - 1, // 首月已处理，生成剩余月份
                    orderInfo.getServiceStartDate() != null ? orderInfo.getServiceStartDate() : new Date(),
                    firstMonthServiceFee, // 使用首月服务费作为月费参考
                    false // 从次月开始
                );
            }
        } catch (Exception e) {
            // 生成拨付单失败不影响主流程
            // 可以记录日志
        }
    }

    /**
     * 创建首月服务费立即划拨的拨付单
     *
     * @param orderInfo 订单信息
     * @param amount 划拨金额
     */
    private void createFirstMonthTransfer(OrderInfo orderInfo, BigDecimal amount) {
        com.ruoyi.domain.pension.FundTransfer transfer = new com.ruoyi.domain.pension.FundTransfer();
        transfer.setInstitutionId(orderInfo.getInstitutionId());
        transfer.setElderId(orderInfo.getElderId());
        transfer.setOrderId(orderInfo.getOrderId());

        // 生成拨付单号
        String transferNo = "TRF" + System.currentTimeMillis() + "FM" + String.format("%02d", (int)(Math.random() * 100));
        transfer.setTransferNo(transferNo);
        transfer.setTransferType("1"); // 自动划拨
        transfer.setTransferAmount(amount);
        transfer.setTransferDate(new Date());

        // 设置当前月份为划拨周期
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM");
        String currentPeriod = sdf.format(new Date());
        transfer.setTransferPeriod(currentPeriod);
        transfer.setBillingMonth(currentPeriod);

        transfer.setElderCount(1);
        transfer.setTransferStatus("1"); // 已完成
        transfer.setIsPaid("1"); // 已划拨
        transfer.setStatus("completed"); // 已完成
        transfer.setExecuteUser("system");
        transfer.setExecuteTime(new Date());
        transfer.setPaidTime(new Date()); // 设置实际划拨时间
        transfer.setPaidMethod("auto");
        transfer.setCreateBy("system");
        transfer.setCreateTime(new Date());
        transfer.setRemark("首月服务费立即划拨-" + orderInfo.getOrderNo());

        fundTransferService.insertFundTransfer(transfer);
    }

    /**
     * 计算首月服务费
     * 根据订单明细中的床位费、护理费和餐费计算首月费用
     *
     * @param orderId 订单ID
     * @return 首月服务费金额
     */
    private BigDecimal calculateFirstMonthServiceFee(Long orderId) {
        try {
            List<OrderItem> orderItems = orderItemMapper.selectOrderItemsByOrderId(orderId);
            BigDecimal firstMonthFee = BigDecimal.ZERO;

            if (orderItems != null && !orderItems.isEmpty()) {
                for (OrderItem item : orderItems) {
                    String itemType = item.getItemType();
                    BigDecimal totalAmount = item.getTotalAmount() != null ? item.getTotalAmount() : BigDecimal.ZERO;
                    Integer quantity = item.getQuantity() != null ? item.getQuantity().intValue() : 1;

                    if ("bed_fee".equals(itemType) || "care_fee".equals(itemType) || "meal_fee".equals(itemType)) {
                        // 计算单月费用：总金额 / 数量（月份数）
                        if (quantity > 0) {
                            BigDecimal monthlyFee = totalAmount.divide(new BigDecimal(quantity), 2, BigDecimal.ROUND_HALF_UP);
                            firstMonthFee = firstMonthFee.add(monthlyFee);
                        }
                    }
                }
            }

            return firstMonthFee;
        } catch (Exception e) {
            // 计算失败时返回0，避免影响主流程
            return BigDecimal.ZERO;
        }
    }

    /**
     * 生成订单编号
     *
     * @param prefix 前缀
     * @return 订单编号
     */
    private String generateOrderNo(String prefix) {
        return "ORD" + prefix + DateUtils.dateTimeNow("yyyyMMddHHmmss");
    }
}