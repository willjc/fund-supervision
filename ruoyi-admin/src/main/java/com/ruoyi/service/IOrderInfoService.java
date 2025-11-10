package com.ruoyi.service;

import java.util.List;
import com.ruoyi.domain.OrderInfo;

/**
 * 订单主表Service接口
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public interface IOrderInfoService
{
    /**
     * 查询订单主表
     *
     * @param orderId 订单主表主键
     * @return 订单主表
     */
    public OrderInfo selectOrderInfoByOrderId(Long orderId);

    /**
     * 查询订单主表列表
     *
     * @param orderInfo 订单主表
     * @return 订单主表集合
     */
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo);

    /**
     * 新增订单主表
     *
     * @param orderInfo 订单主表
     * @return 结果
     */
    public int insertOrderInfo(OrderInfo orderInfo);

    /**
     * 修改订单主表
     *
     * @param orderInfo 订单主表
     * @return 结果
     */
    public int updateOrderInfo(OrderInfo orderInfo);

    /**
     * 批量删除订单主表
     *
     * @param orderIds 需要删除的订单主表主键
     * @return 结果
     */
    public int deleteOrderInfoByOrderIds(Long[] orderIds);

    /**
     * 删除订单主表信息
     *
     * @param orderId 订单主表主键
     * @return 结果
     */
    public int deleteOrderInfoByOrderId(Long orderId);

    /**
     * 根据入住申请ID生成订单
     *
     * @param checkInId 入住申请ID
     * @return 结果
     */
    public int generateOrdersByCheckIn(Long checkInId);

    /**
     * 处理订单支付
     *
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     * @return 结果
     */
    public int processOrderPayment(Long orderId, String paymentMethod);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param cancelReason 取消原因
     * @return 结果
     */
    public int cancelOrder(Long orderId, String cancelReason);

    /**
     * 导出订单主表
     *
     * @param orderInfo 查询条件
     * @return 订单主表列表
     */
    public List<OrderInfo> exportOrderInfo(OrderInfo orderInfo);

    /**
     * 根据订单编号查询订单
     *
     * @param orderNo 订单编号
     * @return 订单信息
     */
    public OrderInfo selectOrderInfoByOrderNo(String orderNo);
}