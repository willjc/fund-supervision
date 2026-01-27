package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.domain.OrderInfo;

/**
 * 订单主表Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public interface OrderInfoMapper
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
     * 删除订单主表
     *
     * @param orderId 订单主表主键
     * @return 结果
     */
    public int deleteOrderInfoByOrderId(Long orderId);

    /**
     * 批量删除订单主表
     *
     * @param orderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOrderInfoByOrderIds(Long[] orderIds);

    /**
     * 根据入住申请ID查询订单
     *
     * @param checkInId 入住申请ID
     * @return 订单列表
     */
    public List<OrderInfo> selectOrdersByCheckInId(Long checkInId);

    /**
     * 根据老人ID查询订单
     *
     * @param elderId 老人ID
     * @return 订单列表
     */
    public List<OrderInfo> selectOrdersByElderId(Long elderId);

    /**
     * 根据订单编号查询订单
     *
     * @param orderNo 订单编号
     * @return 订单信息
     */
    public OrderInfo selectOrderInfoByOrderNo(String orderNo);

    /**
     * 更新订单支付状态
     *
     * @param orderId 订单ID
     * @param orderStatus 订单状态
     * @param paymentMethod 支付方式
     * @param paymentTime 支付时间
     * @return 结果
     */
    public int updateOrderPaymentStatus(Long orderId, String orderStatus, String paymentMethod, java.util.Date paymentTime);
}