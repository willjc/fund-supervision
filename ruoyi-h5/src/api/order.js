import request from '@/utils/request'

/**
 * 获取订单列表
 * @param {Object} params - 查询参数
 * @param {Number} params.elderId - 老人ID
 * @param {String} params.orderStatus - 订单状态(0待支付 1已支付 2已取消 3已退款)
 * @param {Number} params.pageNum - 页码
 * @param {Number} params.pageSize - 每页数量
 */
export function getOrderList(params) {
  return request({
    url: '/h5/order/list',
    method: 'get',
    params
  })
}

/**
 * 获取订单详情
 * @param {Number} orderId - 订单ID
 */
export function getOrderDetail(orderId) {
  return request({
    url: `/h5/order/${orderId}`,
    method: 'get'
  })
}

/**
 * 取消订单
 * @param {Number} orderId - 订单ID
 */
export function cancelOrder(orderId) {
  return request({
    url: `/h5/order/${orderId}/cancel`,
    method: 'post'
  })
}

/**
 * 删除订单
 * @param {Number} orderId - 订单ID
 */
export function deleteOrder(orderId) {
  return request({
    url: `/h5/order/${orderId}`,
    method: 'delete'
  })
}

/**
 * 获取订单统计
 * @param {Number} elderId - 老人ID
 */
export function getOrderStats(elderId) {
  return request({
    url: '/h5/order/stats',
    method: 'get',
    params: { elderId }
  })
}
