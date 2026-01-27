import request from '@/utils/request'

// 查询退款审批列表
export function listApproval(query) {
  return request({
    url: '/pension/supervision/refund/approval/list',
    method: 'get',
    params: query
  })
}

// 查询所有状态的退款
export function listAllRefund(query) {
  return request({
    url: '/pension/supervision/refund/approval/all',
    method: 'get',
    params: query
  })
}

// 查询退款详细
export function getRefund(refundId) {
  return request({
    url: '/pension/supervision/refund/approval/' + refundId,
    method: 'get'
  })
}

// 审批通过退款
export function approveRefund(refundId) {
  return request({
    url: '/pension/supervision/refund/approval/approve/' + refundId,
    method: 'put'
  })
}

// 审批拒绝退款
export function rejectRefund(refundId, data) {
  return request({
    url: '/pension/supervision/refund/approval/reject/' + refundId,
    method: 'put',
    data: data
  })
}

// 获取审批统计信息
export function getApprovalStatistics() {
  return request({
    url: '/pension/supervision/refund/approval/statistics',
    method: 'get'
  })
}

// 批量审批
export function batchApprove(refundIds) {
  return request({
    url: '/pension/supervision/refund/approval/batchApprove',
    method: 'put',
    data: refundIds
  })
}

// 获取大额退款
export function getLargeAmountRefunds() {
  return request({
    url: '/pension/supervision/refund/approval/largeAmount',
    method: 'get'
  })
}

// 按退款原因统计
export function getStatisticsByReason() {
  return request({
    url: '/pension/supervision/refund/approval/statisticsByReason',
    method: 'get'
  })
}
