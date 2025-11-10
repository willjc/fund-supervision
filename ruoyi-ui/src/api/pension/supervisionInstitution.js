import request from '@/utils/request'

// 查询养老机构入驻申请列表
export function listApproval(query) {
  return request({
    url: '/pension/supervision/institution/approval/list',
    method: 'get',
    params: query
  })
}

// 查询所有状态的机构申请
export function listAllInstitution(query) {
  return request({
    url: '/pension/supervision/institution/approval/all',
    method: 'get',
    params: query
  })
}

// 查询养老机构入驻申请详细
export function getInstitution(institutionId) {
  return request({
    url: '/pension/supervision/institution/approval/' + institutionId,
    method: 'get'
  })
}

// 审批通过养老机构入驻申请
export function approveInstitution(institutionId) {
  return request({
    url: '/pension/supervision/institution/approval/approve/' + institutionId,
    method: 'put'
  })
}

// 审批拒绝养老机构入驻申请
export function rejectInstitution(institutionId, data) {
  return request({
    url: '/pension/supervision/institution/approval/reject/' + institutionId,
    method: 'put',
    data: data
  })
}

// 获取审批统计信息
export function getApprovalStatistics() {
  return request({
    url: '/pension/supervision/institution/approval/statistics',
    method: 'get'
  })
}

// 批量审批
export function batchApprove(institutionIds) {
  return request({
    url: '/pension/supervision/institution/approval/batchApprove',
    method: 'put',
    data: institutionIds
  })
}

// 驳回补充养老机构入驻申请
export function supplementInstitution(institutionId, data) {
  return request({
    url: '/pension/supervision/institution/approval/supplement/' + institutionId,
    method: 'put',
    data: data
  })
}

// 批量驳回补充
export function batchSupplement(data) {
  return request({
    url: '/pension/supervision/institution/approval/batchSupplement',
    method: 'put',
    data: data
  })
}

// 导出养老机构入驻申请
export function exportApproval(query) {
  return request({
    url: '/pension/supervision/institution/approval/export',
    method: 'post',
    params: query
  })
}