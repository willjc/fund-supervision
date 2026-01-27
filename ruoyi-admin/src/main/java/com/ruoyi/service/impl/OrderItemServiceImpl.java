package com.ruoyi.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.OrderItemMapper;
import com.ruoyi.domain.OrderItem;
import com.ruoyi.service.IOrderItemService;

/**
 * 订单明细Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@Service
public class OrderItemServiceImpl implements IOrderItemService
{
    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 查询订单明细
     *
     * @param itemId 订单明细主键
     * @return 订单明细
     */
    @Override
    public OrderItem selectOrderItemByItemId(Long itemId)
    {
        return orderItemMapper.selectOrderItemByItemId(itemId);
    }

    /**
     * 查询订单明细列表
     *
     * @param orderItem 订单明细
     * @return 订单明细
     */
    @Override
    public List<OrderItem> selectOrderItemList(OrderItem orderItem)
    {
        return orderItemMapper.selectOrderItemList(orderItem);
    }

    /**
     * 根据订单ID查询明细列表
     *
     * @param orderId 订单ID
     * @return 订单明细集合
     */
    @Override
    public List<OrderItem> selectOrderItemsByOrderId(Long orderId)
    {
        return orderItemMapper.selectOrderItemsByOrderId(orderId);
    }

    /**
     * 新增订单明细
     *
     * @param orderItem 订单明细
     * @return 结果
     */
    @Override
    public int insertOrderItem(OrderItem orderItem)
    {
        orderItem.setCreateTime(DateUtils.getNowDate());
        return orderItemMapper.insertOrderItem(orderItem);
    }

    /**
     * 批量新增订单明细
     *
     * @param orderItems 订单明细列表
     * @return 结果
     */
    @Override
    public int insertOrderItems(List<OrderItem> orderItems)
    {
        int result = 0;
        for (OrderItem item : orderItems) {
            item.setCreateTime(DateUtils.getNowDate());
            result += orderItemMapper.insertOrderItem(item);
        }
        return result;
    }

    /**
     * 修改订单明细
     *
     * @param orderItem 订单明细
     * @return 结果
     */
    @Override
    public int updateOrderItem(OrderItem orderItem)
    {
        orderItem.setUpdateTime(DateUtils.getNowDate());
        return orderItemMapper.updateOrderItem(orderItem);
    }

    /**
     * 批量删除订单明细
     *
     * @param itemIds 需要删除的订单明细主键
     * @return 结果
     */
    @Override
    public int deleteOrderItemByItemIds(Long[] itemIds)
    {
        return orderItemMapper.deleteOrderItemByItemIds(itemIds);
    }

    /**
     * 删除订单明细信息
     *
     * @param itemId 订单明细主键
     * @return 结果
     */
    @Override
    public int deleteOrderItemByItemId(Long itemId)
    {
        return orderItemMapper.deleteOrderItemByItemId(itemId);
    }

    /**
     * 根据订单ID删除明细
     *
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    public int deleteOrderItemByOrderId(Long orderId)
    {
        return orderItemMapper.deleteOrderItemByOrderId(orderId);
    }
}