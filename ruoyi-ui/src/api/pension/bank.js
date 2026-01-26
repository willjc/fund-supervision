import request from '@/utils/request'

// 获取账户信息
export function getAccountInfo() {
  return request({
    url: '/pension/bank/account-info',
    method: 'get'
  })
}

// 查询监管账户交易流水
export function getSupervisionList(query) {
  return request({
    url: '/pension/bank/supervision/list',
    method: 'get',
    params: query
  })
}

// 获取监管账户流水详情
export function getSupervisionDetail(logId) {
  return request({
    url: '/pension/bank/supervision/detail/' + logId,
    method: 'get'
  })
}

// 查询收单交易流水
export function getPaymentList(query) {
  return request({
    url: '/pension/bank/payment/list',
    method: 'get',
    params: query
  })
}

// 获取收单交易详情
export function getPaymentDetail(paymentId) {
  return request({
    url: '/pension/bank/payment/detail/' + paymentId,
    method: 'get'
  })
}

// 获取收单交易统计
export function getPaymentStatistics() {
  return request({
    url: '/pension/bank/payment/statistics',
    method: 'get'
  })
}

// 导出交易流水
export function exportTransactions(type) {
  return request({
    url: '/pension/bank/export',
    method: 'get',
    params: { type: type }
  })
}
