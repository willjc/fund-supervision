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
import com.ruoyi.mapper.ElderInfoMapper;
import com.ruoyi.mapper.BedAllocationMapper;
import com.ruoyi.mapper.BedInfoMapper;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.OrderItemMapper;

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
            existingElder.setUpdateBy(SecurityUtils.getUsername());
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
            elderInfo.setCreateBy(SecurityUtils.getUsername());

            elderInfoMapper.insertElderInfo(elderInfo);
            elderId = elderInfo.getElderId();
        }

        // ========== 2. 计算服务日期(需要在创建床位分配前计算) ==========
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

        // ========== 3. 创建床位分配记录 ==========
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
        allocation.setCreateBy(SecurityUtils.getUsername());
        allocation.setRemark(dto.getRemark());

        bedAllocationMapper.insertBedAllocation(allocation);

        // ========== 4. 生成订单编号并创建订单记录 ==========
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
        orderInfo.setCreateTime(DateUtils.getNowDate());
        orderInfo.setCreateBy(SecurityUtils.getUsername());

        orderInfoMapper.insertOrderInfo(orderInfo);
        Long orderId = orderInfo.getOrderId();

        // ========== 4. 创建订单明细记录(服务费、押金、会员费) ==========
        // 4.1 服务费明细
        if (dto.getMonthlyFee() != null && dto.getMonthlyFee().compareTo(BigDecimal.ZERO) > 0) {
            OrderItem serviceItem = new OrderItem();
            serviceItem.setOrderId(orderId);
            serviceItem.setOrderNo(orderNo);
            serviceItem.setItemName("月服务费");
            serviceItem.setItemType("service_fee");
            serviceItem.setItemDescription(monthCount + "个月服务费");
            serviceItem.setUnitPrice(dto.getMonthlyFee());
            serviceItem.setQuantity(monthCount.longValue());  // 数量=入驻月数
            serviceItem.setTotalAmount(serviceFeeTotal);  // 小计=月服务费×月数
            serviceItem.setServicePeriod("月度");
            serviceItem.setCreateTime(DateUtils.getNowDate());
            serviceItem.setCreateBy(SecurityUtils.getUsername());
            orderItemMapper.insertOrderItem(serviceItem);
        }

        // 4.2 押金明细
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
            depositItem.setCreateBy(SecurityUtils.getUsername());
            orderItemMapper.insertOrderItem(depositItem);
        }

        // 4.3 会员费明细
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
            memberItem.setCreateBy(SecurityUtils.getUsername());
            orderItemMapper.insertOrderItem(memberItem);
        }

        // ========== 5. 更新床位状态为占用 ==========
        bedInfoMapper.updateBedStatus(bedId, "1"); // 1-占用

        return 1;
    }
}
