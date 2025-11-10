package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.domain.OrderItem;

/**
 * 订单明细Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public interface OrderItemMapper
{
    /**
     * 查询订单明细
     *
     * @param itemId 订单明细主键
     * @return 订单明细
     */
    public OrderItem selectOrderItemByItemId(Long itemId);

    /**
     * 查询订单明细列表
     *
     * @param orderItem 订单明细
     * @return 订单明细集合
     */
    public List<OrderItem> selectOrderItemList(OrderItem orderItem);

    /**
     * 根据订单ID查询明细列表
     *
     * @param orderId 订单ID
     * @return 订单明细集合
     */
    public List<OrderItem> selectOrderItemsByOrderId(Long orderId);

    /**
     * 新增订单明细
     *
     * @param orderItem 订单明细
     * @return 结果
     */
    public int insertOrderItem(OrderItem orderItem);

    /**
     * 修改订单明细
     *
     * @param orderItem 订单明细
     * @return 结果
     */
    public int updateOrderItem(OrderItem orderItem);

    /**
     * 删除订单明细
     *
     * @param itemId 订单明细主键
     * @return 结果
     */
    public int deleteOrderItemByItemId(Long itemId);

    /**
     * 批量删除订单明细
     *
     * @param itemIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOrderItemByItemIds(Long[] itemIds);

    /**
     * 根据订单ID删除明细
     *
     * @param orderId 订单ID
     * @return 结果
     */
    public int deleteOrderItemByOrderId(Long orderId);
}