package com.ruoyi.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.service.IPensionCheckinService;
import com.ruoyi.domain.PensionCheckinDTO;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.BedAllocation;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.OrderItem;
import com.ruoyi.domain.PaymentRecord;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.mapper.ElderInfoMapper;
import com.ruoyi.mapper.BedAllocationMapper;
import com.ruoyi.mapper.BedInfoMapper;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.OrderItemMapper;
import com.ruoyi.mapper.PaymentRecordMapper;
import com.ruoyi.service.pension.IAccountInfoService;

/**
 * 养老机构入驻Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-11
 */
@Service
public class PensionCheckinServiceImpl implements IPensionCheckinService
{
    @Autowired
    private ElderInfoMapper elderInfoMapper;

    @Autowired
    private BedAllocationMapper bedAllocationMapper;

    @Autowired
    private BedInfoMapper bedInfoMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private IAccountInfoService accountInfoService;

    /**
     * 安全获取用户名（处理H5端未登录情况）
     */
    private String getUsernameSafely() {
        try {
            return SecurityUtils.getUsername();
        } catch (Exception e) {
            return "H5用户";
        }
    }

    /**
     * 创建入驻申请
     * 事务控制:所有操作要么全部成功,要么全部失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createCheckin(PensionCheckinDTO dto, Long userId)
    {
        // 获取床位信息和机构ID
        Long bedId = dto.getBedId();
        if (bedId == null) {
            throw new ServiceException("床位ID不能为空");
        }

        // 查询床位信息获取机构ID
        com.ruoyi.domain.BedInfo bedInfo = bedInfoMapper.selectBedInfoByBedId(bedId);
        if (bedInfo == null) {
            throw new ServiceException("床位不存在");
        }
        if (!"0".equals(bedInfo.getBedStatus())) {
            throw new ServiceException("床位已被占用或不可用");
        }
        Long institutionId = bedInfo.getInstitutionId();

        // ========== 1. 检查并创建/复用老人信息记录 ==========
        Long elderId;

        // 先检查身份证号是否已存在
        ElderInfo existingElder = elderInfoMapper.selectElderInfoByIdCard(dto.getIdCard());

        if (existingElder != null) {
            // 身份证号已存在,检查是否已经在住
            if ("1".equals(existingElder.getStatus())) {
                throw new ServiceException("该老人已在住,身份证号: " + dto.getIdCard());
            }
            // 如果是已退住状态,可以重新入住,复用老人ID并更新状态
            elderId = existingElder.getElderId();
            existingElder.setStatus("1"); // 更新为已入住
            existingElder.setUpdateTime(DateUtils.getNowDate());
            existingElder.setUpdateBy(getUsernameSafely());
            elderInfoMapper.updateElderInfo(existingElder);
        } else {
            // 身份证号不存在,创建新老人记录
            ElderInfo elderInfo = new ElderInfo();
            elderInfo.setElderName(dto.getElderName());
            elderInfo.setGender(dto.getGender());
            elderInfo.setIdCard(dto.getIdCard());
            elderInfo.setBirthDate(dto.getBirthDate());
            elderInfo.setAge(dto.getAge() != null ? dto.getAge().longValue() : null);
            elderInfo.setPhone(dto.getPhone());
            elderInfo.setCareLevel(dto.getCareLevel());
            elderInfo.setHealthStatus(dto.getHealthStatus());
            elderInfo.setAddress(dto.getAddress());
            elderInfo.setEmergencyContact(dto.getEmergencyContact());
            elderInfo.setEmergencyPhone(dto.getEmergencyPhone());
            elderInfo.setSpecialNeeds(dto.getSpecialNeeds());
            // 根据支付方式设置状态: 已支付->已入住, 未支付->待入住
            elderInfo.setStatus("later".equals(dto.getPaymentMethod()) ? "0" : "1");
            elderInfo.setCreateTime(DateUtils.getNowDate());
            elderInfo.setCreateBy(getUsernameSafely());

            elderInfoMapper.insertElderInfo(elderInfo);
            elderId = elderInfo.getElderId();
        }

        // ========== 2. 检查并创建老人账户信息 ==========
        AccountInfo existingAccount = accountInfoService.selectAccountInfoByElderId(elderId);
        if (existingAccount == null) {
            // 账户不存在，创建新账户
            try {
                accountInfoService.createAccountInfo(elderId, institutionId, BigDecimal.ZERO);
            } catch (Exception e) {
                throw new ServiceException("创建老人账户失败：" + e.getMessage());
            }
        }
        // 如果账户已存在，复用现有账户

        // ========== 3. 计算服务日期(需要在创建床位分配前计算) ==========
        // 计算服务费小计 = 月服务费 × 入驻月数
        Integer monthCount = dto.getMonthCount() != null ? dto.getMonthCount() : 1;

        // 计算服务起始日期和结束日期
        Date checkInDate = dto.getCheckInDate();
        if (checkInDate == null) {
            checkInDate = new Date();
        }

        // 计算服务结束日期(到期日期) = 入驻日期 + 入驻月数
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(checkInDate);
        calendar.add(Calendar.MONTH, monthCount);
        Date serviceEndDate = calendar.getTime();

        // ========== 4. 创建床位分配记录 ==========
        BedAllocation allocation = new BedAllocation();
        allocation.setElderId(elderId);
        allocation.setBedId(bedId);
        allocation.setInstitutionId(institutionId);
        allocation.setCheckInDate(checkInDate);
        allocation.setDueDate(serviceEndDate);  // ✅ 设置到期日期
        // 根据支付方式设置分配状态: 已支付->在住, 未支付->待入住
        allocation.setAllocationStatus("later".equals(dto.getPaymentMethod()) ? "0" : "1");
        allocation.setMonthlyFee(dto.getMonthlyFee());
        allocation.setDepositAmount(dto.getDepositAmount());

        // 根据支付方式设置押金状态
        if ("later".equals(dto.getPaymentMethod())) {
            allocation.setDepositStatus("0"); // 0-未支付
        } else {
            allocation.setDepositStatus("1"); // 1-已支付
        }

        allocation.setCreateTime(DateUtils.getNowDate());
        allocation.setCreateBy(getUsernameSafely());
        allocation.setRemark(dto.getRemark());

        bedAllocationMapper.insertBedAllocation(allocation);

        // ========== 5. 生成订单编号并创建订单记录 ==========
        String orderNo = "ORD" + System.currentTimeMillis();

        BigDecimal serviceFeeTotal = dto.getMonthlyFee().multiply(new BigDecimal(monthCount));

        // 计算应收总计(优惠前)
        BigDecimal originalAmount = serviceFeeTotal
                .add(dto.getDepositAmount() != null ? dto.getDepositAmount() : BigDecimal.ZERO)
                .add(dto.getMemberFee() != null ? dto.getMemberFee() : BigDecimal.ZERO);

        // 使用前端传来的实收总计(已包含优惠调整)
        BigDecimal finalAmount = dto.getFinalAmount();
        if (finalAmount == null) {
            finalAmount = originalAmount;
        }

        // 计算优惠金额 = 应收总计 - 实收总计
        BigDecimal discountAmount = originalAmount.subtract(finalAmount);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setOrderType("1"); // 订单类型:1-床位费
        orderInfo.setElderId(elderId);
        orderInfo.setCreatorUserId(userId); // 设置下单人用户ID
        orderInfo.setInstitutionId(institutionId);
        orderInfo.setBedId(bedId);
        orderInfo.setMonthCount(monthCount);  // 保存入驻月数
        orderInfo.setOriginalAmount(originalAmount);  // 保存应收总计
        orderInfo.setOrderAmount(finalAmount);  // 保存实收总计
        orderInfo.setDiscountAmount(discountAmount);  // 保存优惠金额
        orderInfo.setPaidAmount(BigDecimal.ZERO);
        orderInfo.setServiceStartDate(checkInDate);  // 服务起始日期
        orderInfo.setServiceEndDate(serviceEndDate);  // 服务结束日期
        orderInfo.setBillingCycle("月度");  // 计费周期

        // 根据支付方式设置订单状态
        if ("later".equals(dto.getPaymentMethod())) {
            orderInfo.setOrderStatus("0"); // 0-未支付
        } else {
            orderInfo.setOrderStatus("1"); // 1-已支付
            orderInfo.setPaidAmount(finalAmount);
            orderInfo.setPaymentTime(DateUtils.getNowDate());
        }

        orderInfo.setPaymentMethod(dto.getPaymentMethod());
        orderInfo.setOrderDate(new Date());
        orderInfo.setRemark(dto.getFeeDescription());

        // 设置床位信息，方便前端显示
        orderInfo.setRoomNumber(bedInfo.getRoomNumber());
        orderInfo.setBedNumber(bedInfo.getBedNumber());

        orderInfo.setCreateTime(DateUtils.getNowDate());
        orderInfo.setCreateBy(getUsernameSafely());

        orderInfoMapper.insertOrderInfo(orderInfo);
        Long orderId = orderInfo.getOrderId();

        // ========== 4. 创建订单明细记录(床位费、护理费、押金、会员费) ==========
        // 4.1 床位费明细
        BigDecimal bedFee = bedInfo.getPrice(); // 床位费
        if (bedFee != null && bedFee.compareTo(BigDecimal.ZERO) > 0) {
            OrderItem bedItem = new OrderItem();
            bedItem.setOrderId(orderId);
            bedItem.setOrderNo(orderNo);
            bedItem.setItemName("床位费");
            bedItem.setItemType("bed_fee");
            bedItem.setItemDescription(bedInfo.getRoomNumber() + "-" + bedInfo.getBedNumber() + "床位费");
            bedItem.setUnitPrice(bedFee);
            bedItem.setQuantity(monthCount.longValue());  // 数量=入驻月数
            bedItem.setTotalAmount(bedFee.multiply(new BigDecimal(monthCount)));  // 小计=床位费×月数
            bedItem.setServicePeriod("月度");
            bedItem.setCreateTime(DateUtils.getNowDate());
            bedItem.setCreateBy(getUsernameSafely());
            orderItemMapper.insertOrderItem(bedItem);
        }

        // 4.2 护理费明细
        BigDecimal careFee = getCareFeeByLevel(bedInfo, dto.getCareLevel()); // 护理费
        if (careFee != null && careFee.compareTo(BigDecimal.ZERO) > 0) {
            OrderItem careItem = new OrderItem();
            careItem.setOrderId(orderId);
            careItem.setOrderNo(orderNo);
            careItem.setItemName("护理费");
            careItem.setItemType("care_fee");
            String careLevelText = getCareLevelText(dto.getCareLevel());
            careItem.setItemDescription(careLevelText + "护理费");
            careItem.setUnitPrice(careFee);
            careItem.setQuantity(monthCount.longValue());  // 数量=入驻月数
            careItem.setTotalAmount(careFee.multiply(new BigDecimal(monthCount)));  // 小计=护理费×月数
            careItem.setServicePeriod("月度");
            careItem.setCreateTime(DateUtils.getNowDate());
            careItem.setCreateBy(getUsernameSafely());
            orderItemMapper.insertOrderItem(careItem);
        }

        // 4.3 押金明细
        if (dto.getDepositAmount() != null && dto.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            OrderItem depositItem = new OrderItem();
            depositItem.setOrderId(orderId);
            depositItem.setOrderNo(orderNo);
            depositItem.setItemName("押金");
            depositItem.setItemType("deposit");
            depositItem.setItemDescription("入住押金");
            depositItem.setUnitPrice(dto.getDepositAmount());
            depositItem.setQuantity(1L);
            depositItem.setTotalAmount(dto.getDepositAmount());
            depositItem.setCreateTime(DateUtils.getNowDate());
            depositItem.setCreateBy(getUsernameSafely());
            orderItemMapper.insertOrderItem(depositItem);
        }

        // 4.4 会员费明细
        if (dto.getMemberFee() != null && dto.getMemberFee().compareTo(BigDecimal.ZERO) > 0) {
            OrderItem memberItem = new OrderItem();
            memberItem.setOrderId(orderId);
            memberItem.setOrderNo(orderNo);
            memberItem.setItemName("会员费");
            memberItem.setItemType("member_fee");
            memberItem.setItemDescription("会员卡充值");
            memberItem.setUnitPrice(dto.getMemberFee());
            memberItem.setQuantity(1L);
            memberItem.setTotalAmount(dto.getMemberFee());
            memberItem.setCreateTime(DateUtils.getNowDate());
            memberItem.setCreateBy(getUsernameSafely());
            orderItemMapper.insertOrderItem(memberItem);
        }

        // ========== 5. 创建支付记录(仅当非"稍后支付"时) ==========
        if (!"later".equals(dto.getPaymentMethod())) {
            PaymentRecord paymentRecord = new PaymentRecord();
            paymentRecord.setPaymentNo("PAY" + System.currentTimeMillis()); // 支付流水号
            paymentRecord.setOrderId(orderId);
            paymentRecord.setElderId(elderId);
            paymentRecord.setInstitutionId(institutionId);
            paymentRecord.setPaymentAmount(finalAmount); // 支付金额 = 实收总计
            paymentRecord.setPaymentMethod(dto.getPaymentMethod()); // 支付方式
            paymentRecord.setPaymentStatus("1"); // 支付状态:1-成功
            paymentRecord.setPaymentTime(DateUtils.getNowDate()); // 支付时间
            paymentRecord.setOperator(getUsernameSafely()); // 操作人
            paymentRecord.setRemark("入住支付");
            paymentRecord.setCreateTime(DateUtils.getNowDate());
            paymentRecord.setCreateBy(getUsernameSafely());
            paymentRecordMapper.insertPaymentRecord(paymentRecord);
        }

        // ========== 6. 更新床位状态为占用 ==========
        bedInfoMapper.updateBedStatus(bedId, "1"); // 1-占用

        return 1;
    }

    /**
     * 根据护理等级获取护理费
     * @param bed 床位信息
     * @param careLevel 护理等级（支持中文：自理/半护理/全护理 或 数字：1/2/3）
     * @return 护理费
     */
    private BigDecimal getCareFeeByLevel(com.ruoyi.domain.BedInfo bed, String careLevel) {
        if (careLevel == null) {
            return bed.getSelfCarePrice() != null ? bed.getSelfCarePrice() : new BigDecimal("500");
        }

        // 支持中文和数字两种格式
        switch (careLevel) {
            case "1":
            case "自理":
                return bed.getSelfCarePrice() != null ? bed.getSelfCarePrice() : new BigDecimal("500");
            case "2":
            case "半护理":
                return bed.getHalfCarePrice() != null ? bed.getHalfCarePrice() : new BigDecimal("800");
            case "3":
            case "全护理":
                return bed.getFullCarePrice() != null ? bed.getFullCarePrice() : new BigDecimal("1200");
            default:
                return bed.getSelfCarePrice() != null ? bed.getSelfCarePrice() : new BigDecimal("500");
        }
    }

    /**
     * 获取护理等级文本
     * @param careLevel 护理等级（1自理 2半护理 3全护理）
     * @return 护理等级文本
     */
    private String getCareLevelText(String careLevel) {
        switch (careLevel) {
            case "1":
                return "自理";
            case "2":
                return "半护理";
            case "3":
                return "全护理";
            default:
                return "自理";
        }
    }
}
