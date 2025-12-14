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
import com.ruoyi.domain.vo.ResidentVO;
import com.ruoyi.mapper.BedAllocationMapper;
import com.ruoyi.mapper.ElderAttachmentMapper;
import com.ruoyi.mapper.ElderInfoMapper;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.OrderItemMapper;
import com.ruoyi.mapper.PaymentRecordMapper;
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

    /**
     * 查询入住人列表
     *
     * @param queryVO 查询条件
     * @return 入住人列表
     */
    @Override
    public List<ResidentVO> selectResidentList(ResidentVO queryVO)
    {
        return residentMapper.selectResidentList(queryVO);
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
            residentVO.setOrders(residentMapper.selectOrdersByElderId(elderId));

            // 查询老人照片（从elder_info表）
            ElderInfo elderInfo = elderInfoMapper.selectElderInfoByElderId(elderId);
            if (elderInfo != null && elderInfo.getPhotoPath() != null) {
                residentVO.setPhotoPath(elderInfo.getPhotoPath());
            }

            // 查询身份证照片（从elder_attachment表）
            // 查询身份证正面（attachment_type = '1'）
            ElderAttachment frontAttachment = elderAttachmentMapper.selectAttachmentByElderIdAndType(elderId, "1");
            if (frontAttachment != null && frontAttachment.getFilePath() != null) {
                residentVO.setIdCardFrontPath(frontAttachment.getFilePath());
            }

            // 查询身份证反面（attachment_type = '2'）
            ElderAttachment backAttachment = elderAttachmentMapper.selectAttachmentByElderIdAndType(elderId, "2");
            if (backAttachment != null && backAttachment.getFilePath() != null) {
                residentVO.setIdCardBackPath(backAttachment.getFilePath());
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

        // 4. 计算订单金额
        // 应收总计 = 月服务费 × 续费月数 + 补交押金 + 补交会员费
        BigDecimal serviceFeeTotal = BigDecimal.ZERO;
        if (renewDTO.getMonthCount() != null && renewDTO.getMonthCount() > 0) {
            serviceFeeTotal = bedAllocation.getMonthlyFee().multiply(new BigDecimal(renewDTO.getMonthCount()));
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
        orderInfo.setPaidAmount(finalAmount); // 续费直接支付
        orderInfo.setOriginalAmount(calculatedTotal); // 原始金额 = 应收总计
        orderInfo.setDiscountAmount(discountAmount); // 优惠金额
        orderInfo.setOrderStatus("1"); // 1-已支付
        orderInfo.setPaymentMethod(renewDTO.getPaymentMethod());
        orderInfo.setPaymentTime(DateUtils.getNowDate());
        orderInfo.setOrderDate(new Date());
        orderInfo.setBillingCycle("月度");
        orderInfo.setRemark(renewDTO.getRemark());
        orderInfo.setCreateTime(DateUtils.getNowDate());
        orderInfo.setCreateBy(SecurityUtils.getUsername());

        // 7. 如果续费月数>0,需要设置服务日期和月数,并更新到期日期
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

            // 更新床位分配的到期日期
            bedAllocation.setDueDate(serviceEndDate);
            bedAllocation.setUpdateTime(DateUtils.getNowDate());
            bedAllocation.setUpdateBy(SecurityUtils.getUsername());
            bedAllocationMapper.updateBedAllocation(bedAllocation);
        }

        orderInfoMapper.insertOrderInfo(orderInfo);
        Long orderId = orderInfo.getOrderId();

        // 8. 创建订单明细(支持多个明细项)
        // 8.1 如果有服务费续费
        if (renewDTO.getMonthCount() != null && renewDTO.getMonthCount() > 0 && serviceFeeTotal.compareTo(BigDecimal.ZERO) > 0) {
            OrderItem serviceItem = new OrderItem();
            serviceItem.setOrderId(orderId);
            serviceItem.setOrderNo(orderNo);
            serviceItem.setItemType("service_fee");
            serviceItem.setItemName("月服务费");
            serviceItem.setItemDescription(renewDTO.getMonthCount() + "个月服务费");
            serviceItem.setUnitPrice(bedAllocation.getMonthlyFee());
            serviceItem.setQuantity(renewDTO.getMonthCount().longValue());
            serviceItem.setTotalAmount(serviceFeeTotal);
            serviceItem.setCreateTime(DateUtils.getNowDate());
            serviceItem.setCreateBy(SecurityUtils.getUsername());
            orderItemMapper.insertOrderItem(serviceItem);
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
            memberItem.setCreateTime(DateUtils.getNowDate());
            memberItem.setCreateBy(SecurityUtils.getUsername());
            orderItemMapper.insertOrderItem(memberItem);
        }

        // 9. 创建支付记录
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
}
