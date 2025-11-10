import request from '@/utils/request'

// 查询支付记录列表
export function listPayment(query) {
  return request({
    url: '/payment/record/list',
    method: 'get',
    params: query
  })
}

// 查询支付记录详细
export function getPayment(paymentId) {
  return request({
    url: '/payment/record/' + paymentId,
    method: 'get'
  })
}

// 根据支付流水号查询支付记录
export function getPaymentByNo(paymentNo) {
  return request({
    url: '/payment/record/paymentNo/' + paymentNo,
    method: 'get'
  })
}

// 新增支付记录
export function addPayment(data) {
  return request({
    url: '/payment/record',
    method: 'post',
    data: data
  })
}

// 修改支付记录
export function updatePayment(data) {
  return request({
    url: '/payment/record',
    method: 'put',
    data: data
  })
}

// 删除支付记录
export function delPayment(paymentId) {
  return request({
    url: '/payment/record/' + paymentId,
    method: 'delete'
  })
}

// 模拟支付处理
export function simulatePayment(orderId, paymentMethod) {
  return request({
    url: '/payment/record/simulate/' + orderId,
    method: 'post',
    data: { paymentMethod: paymentMethod }
  })
}

// 更新支付状态
export function updatePaymentStatus(paymentId, status) {
  return request({
    url: '/payment/record/status/' + paymentId,
    method: 'put',
    data: { paymentStatus: status }
  })
}

// 获取支付统计信息
export function getPaymentStatistics(query) {
  return request({
    url: '/payment/record/statistics',
    method: 'get',
    params: query
  })
}

// 导出支付记录
export function exportPayment(query) {
  return request({
    url: '/payment/record/export',
    method: 'post',
    params: query
  })
}