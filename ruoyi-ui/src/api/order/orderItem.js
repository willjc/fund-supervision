import request from '@/utils/request'

// 查询订单明细列表
export function listOrderItem(query) {
  return request({
    url: '/order/item/list',
    method: 'get',
    params: query
  })
}

// 查询订单明细详细
export function getOrderItem(itemId) {
  return request({
    url: '/order/item/' + itemId,
    method: 'get'
  })
}

// 根据订单ID查询明细列表
export function getOrderItemsByOrderId(orderId) {
  return request({
    url: '/order/item/orderId/' + orderId,
    method: 'get'
  })
}

// 新增订单明细
export function addOrderItem(data) {
  return request({
    url: '/order/item',
    method: 'post',
    data: data
  })
}

// 批量新增订单明细
export function addOrderItemsBatch(data) {
  return request({
    url: '/order/item/batch',
    method: 'post',
    data: data
  })
}

// 修改订单明细
export function updateOrderItem(data) {
  return request({
    url: '/order/item',
    method: 'put',
    data: data
  })
}

// 删除订单明细
export function delOrderItem(itemId) {
  return request({
    url: '/order/item/' + itemId,
    method: 'delete'
  })
}

// 根据订单ID删除明细
export function delOrderItemByOrderId(orderId) {
  return request({
    url: '/order/item/orderId/' + orderId,
    method: 'delete'
  })
}

// 导出订单明细
export function exportOrderItem(query) {
  return request({
    url: '/order/item/export',
    method: 'post',
    params: query
  })
}