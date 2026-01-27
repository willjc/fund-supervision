import request from '@/utils/request'

// 查询预警列表
export function listWarning(query) {
  return request({
    url: '/supervision/warning/list',
    method: 'get',
    params: query
  })
}

// 查询预警详细
export function getWarning(warningNo) {
  return request({
    url: '/supervision/warning/detail/' + warningNo,
    method: 'get'
  })
}

// 处理预警
export function processWarning(data) {
  return request({
    url: '/supervision/warning/handle',
    method: 'post',
    data: data
  })
}

// 查询费用超额预警
export function listFeeExcessWarning(query) {
  return request({
    url: '/supervision/warning/fee-excess',
    method: 'get',
    params: query
  })
}

// 查询押金超额预警
export function listDepositExcessWarning(query) {
  return request({
    url: '/supervision/warning/deposit-excess',
    method: 'get',
    params: query
  })
}

// 查询入驻超额预警
export function listCheckinExcessWarning(query) {
  return request({
    url: '/supervision/warning/checkin-excess',
    method: 'get',
    params: query
  })
}

// 查询监管超额预警
export function listSupervisionExcessWarning(query) {
  return request({
    url: '/supervision/warning/supervision-excess',
    method: 'get',
    params: query
  })
}

// 查询风险保证金超额预警
export function listRiskDepositExcessWarning(query) {
  return request({
    url: '/supervision/warning/risk-deposit-excess',
    method: 'get',
    params: query
  })
}

// 查询大额支付预警
export function listLargePaymentWarning(query) {
  return request({
    url: '/supervision/warning/large-payment',
    method: 'get',
    params: query
  })
}

// 查询突发风险预警
export function listEmergencyRiskWarning(query) {
  return request({
    url: '/supervision/warning/emergency-risk',
    method: 'get',
    params: query
  })
}