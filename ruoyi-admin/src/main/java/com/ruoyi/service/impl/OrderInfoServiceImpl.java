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
import com.ruoyi.service.IOrderInfoService;
import com.ruoyi.service.IElderCheckInService;

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
        BigDecimal newDepositFee = parsePriceFromRemark(remark, "押金");
        BigDecimal newMemberFee = parsePriceFromRemark(remark, "会员费");
        Integer newMonthCount = parseMonthCountFromRemark(remark);

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
                // 如果是床位费或护理费，且修改了月数，需要更新数量
                if ((newMonthCount != null && newMonthCount > 0) &&
                    ("bed_fee".equals(item.getItemType()) || "care_fee".equals(item.getItemType()))) {
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
     * 生成订单编号
     *
     * @param prefix 前缀
     * @return 订单编号
     */
    private String generateOrderNo(String prefix) {
        return "ORD" + prefix + DateUtils.dateTimeNow("yyyyMMddHHmmss");
    }
}