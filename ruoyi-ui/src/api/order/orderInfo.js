import request from '@/utils/request'

// 查询订单列表
export function listOrder(query) {
  return request({
    url: '/order/info/list',
    method: 'get',
    params: query
  })
}

// 查询订单详细
export function getOrder(orderId) {
  return request({
    url: '/order/info/' + orderId,
    method: 'get'
  })
}

// 根据订单号查询订单
export function getOrderByNo(orderNo) {
  return request({
    url: '/order/info/orderNo/' + orderNo,
    method: 'get'
  })
}

// 新增订单
export function addOrder(data) {
  return request({
    url: '/order/info',
    method: 'post',
    data: data
  })
}

// 修改订单
export function updateOrder(data) {
  return request({
    url: '/order/info',
    method: 'put',
    data: data
  })
}

// 删除订单
export function delOrder(orderId) {
  return request({
    url: '/order/info/' + orderId,
    method: 'delete'
  })
}

// 处理订单支付
export function payOrder(orderId, paymentMethod) {
  return request({
    url: '/order/info/pay/' + orderId,
    method: 'put',
    params: { paymentMethod: paymentMethod }
  })
}

// 取消订单
export function cancelOrder(orderId) {
  return request({
    url: '/order/info/cancel/' + orderId,
    method: 'put'
  })
}

// 根据入住申请生成订单
export function generateOrderByCheckIn(checkInId) {
  return request({
    url: '/order/info/generate/' + checkInId,
    method: 'post'
  })
}

// 导出订单
export function exportOrder(query) {
  return request({
    url: '/order/info/export',
    method: 'post',
    params: query
  })
}

// 审核通过订单
export function approveOrder(data) {
  return request({
    url: '/order/info/approve',
    method: 'put',
    data: data
  })
}

// 审核拒绝订单
export function rejectOrder(data) {
  return request({
    url: '/order/info/reject',
    method: 'put',
    data: data
  })
}

// 线下支付订单
export function offlinePay(orderId, data) {
  return request({
    url: '/order/info/offlinePay/' + orderId,
    method: 'put',
    data: data
  })
}