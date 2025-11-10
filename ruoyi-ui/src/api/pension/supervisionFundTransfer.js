import request from '@/utils/request'

// 查询资金划拨审批列表
export function listApproval(query) {
  return request({
    url: '/pension/supervision/fundTransfer/approval/list',
    method: 'get',
    params: query
  })
}

// 查询所有状态的划拨记录
export function listAllFundTransfer(query) {
  return request({
    url: '/pension/supervision/fundTransfer/approval/all',
    method: 'get',
    params: query
  })
}

// 查询资金划拨详细
export function getFundTransfer(transferId) {
  return request({
    url: '/pension/supervision/fundTransfer/approval/' + transferId,
    method: 'get'
  })
}

// 审批通过资金划拨
export function approveFundTransfer(transferId) {
  return request({
    url: '/pension/supervision/fundTransfer/approval/approve/' + transferId,
    method: 'put'
  })
}

// 审批拒绝资金划拨
export function rejectFundTransfer(transferId, data) {
  return request({
    url: '/pension/supervision/fundTransfer/approval/reject/' + transferId,
    method: 'put',
    data: data
  })
}

// 获取审批统计信息
export function getApprovalStatistics() {
  return request({
    url: '/pension/supervision/fundTransfer/approval/statistics',
    method: 'get'
  })
}

// 批量审批
export function batchApprove(transferIds) {
  return request({
    url: '/pension/supervision/fundTransfer/approval/batchApprove',
    method: 'put',
    data: transferIds
  })
}

// 获取今日待处理划拨
export function getTodayPending() {
  return request({
    url: '/pension/supervision/fundTransfer/approval/todayPending',
    method: 'get'
  })
}

// 获取紧急划拨
export function getUrgentTransfers() {
  return request({
    url: '/pension/supervision/fundTransfer/approval/urgent',
    method: 'get'
  })
}

// 导出资金划拨审批
export function exportApproval(query) {
  return request({
    url: '/pension/supervision/fundTransfer/approval/export',
    method: 'post',
    params: query
  })
}