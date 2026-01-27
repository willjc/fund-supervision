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
 * 通过订单号获取订单详情
 * @param {String} orderNo - 订单编号
 */
export function getOrderByNo(orderNo) {
  return request({
    url: '/h5/getOrderByNo',
    method: 'get',
    params: { orderNo }
  })
}

/**
 * 提交订单
 * @param {Object} data - 订单数据
 * @param {Number} data.institutionId - 机构ID
 * @param {String} data.elderName - 老人姓名
 * @param {String} data.abilityLevel - 护理等级
 * @param {String} data.roomType - 房间类型
 * @param {Array} data.packages - 套餐列表
 * @param {Number} data.months - 缴纳月数
 * @param {String} data.remark - 备注
 * @param {Object} data.costDetails - 费用明细
 */
export function submitOrder(data) {
  return request({
    url: '/h5/order/submit',
    method: 'post',
    data
  })
}

/**
 * 获取床位价格
 * @param {Number} institutionId - 机构ID
 * @param {String} bedType - 床位类型
 */
export function getBedPrice(institutionId, bedType) {
  return request({
    url: '/h5/bed/optimal-price',
    method: 'get',
    params: {
      institutionId,
      bedType
    }
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
 * 获取老人列表
 */
export function getElderList() {
  return request({
    url: '/h5/elder/list',
    method: 'get'
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

/**
 * 处理支付请求
 * @param {Number} orderId - 订单ID
 * @param {Object} config - 配置参数，如 { paymentMethod: 'alipay' }
 */
export function processPayment(orderId, config) {
  return request({
    url: `/h5/payment/process/${orderId}`,
    method: 'post',
    params: config
  })
}

/**
 * 获取订单明细
 * @param {Number} orderId - 订单ID
 */
export function getOrderItems(orderId) {
  return request({
    url: `/h5/order/${orderId}/items`,
    method: 'get'
  })
}
