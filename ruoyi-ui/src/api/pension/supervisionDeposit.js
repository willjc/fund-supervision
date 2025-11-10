import request from '@/utils/request'

// 查询押金使用审批列表
export function listApproval(query) {
  return request({
    url: '/pension/supervision/deposit/approval/list',
    method: 'get',
    params: query
  })
}

// 查询所有状态的押金申请
export function listAllDeposit(query) {
  return request({
    url: '/pension/supervision/deposit/approval/all',
    method: 'get',
    params: query
  })
}

// 查询押金申请详细
export function getDepositApply(applyId) {
  return request({
    url: '/pension/supervision/deposit/approval/' + applyId,
    method: 'get'
  })
}

// 审批通过押金使用申请
export function approveDeposit(applyId) {
  return request({
    url: '/pension/supervision/deposit/approval/approve/' + applyId,
    method: 'put'
  })
}

// 审批拒绝押金使用申请
export function rejectDeposit(applyId, data) {
  return request({
    url: '/pension/supervision/deposit/approval/reject/' + applyId,
    method: 'put',
    data: data
  })
}

// 获取审批统计信息
export function getApprovalStatistics() {
  return request({
    url: '/pension/supervision/deposit/approval/statistics',
    method: 'get'
  })
}

// 批量审批
export function batchApprove(applyIds) {
  return request({
    url: '/pension/supervision/deposit/approval/batchApprove',
    method: 'put',
    data: applyIds
  })
}

// 获取今日待处理押金申请
export function getTodayPending() {
  return request({
    url: '/pension/supervision/deposit/approval/todayPending',
    method: 'get'
  })
}

// 获取紧急押金申请
export function getUrgentApplies() {
  return request({
    url: '/pension/supervision/deposit/approval/urgentApplies',
    method: 'get'
  })
}

// 获取大额押金申请
export function getLargeAmountApplies() {
  return request({
    url: '/pension/supervision/deposit/approval/largeAmount',
    method: 'get'
  })
}

// 按申请类型统计
export function getStatisticsByType() {
  return request({
    url: '/pension/supervision/deposit/approval/statisticsByType',
    method: 'get'
  })
}

// 导出押金使用审批
export function exportApproval(query) {
  return request({
    url: '/pension/supervision/deposit/approval/export',
    method: 'post',
    params: query
  })
}
