import request from '@/utils/request'

// 查询资金划拨记录列表
export function listFundTransfer(query) {
  return request({
    url: '/pension/fundTransfer/list',
    method: 'get',
    params: query
  })
}

// 查询资金划拨记录详细
export function getFundTransfer(transferId) {
  return request({
    url: '/pension/fundTransfer/' + transferId,
    method: 'get'
  })
}

// 根据机构ID查询划拨记录
export function getFundTransferByInstitutionId(institutionId) {
  return request({
    url: '/pension/fundTransfer/institution/' + institutionId,
    method: 'get'
  })
}

// 新增资金划拨记录
export function addFundTransfer(data) {
  return request({
    url: '/pension/fundTransfer',
    method: 'post',
    data: data
  })
}

// 修改资金划拨记录
export function updateFundTransfer(data) {
  return request({
    url: '/pension/fundTransfer',
    method: 'put',
    data: data
  })
}

// 删除资金划拨记录
export function delFundTransfer(transferId) {
  return request({
    url: '/pension/fundTransfer/' + transferId,
    method: 'delete'
  })
}

// 审批资金划拨
export function approveFundTransfer(transferId, approveResult, approveRemark) {
  return request({
    url: '/pension/fundTransfer/approve',
    method: 'put',
    data: {
      transferId: transferId,
      approvalStatus: approveResult,
      approvalRemark: approveRemark
    }
  })
}

// 执行资金划拨
export function executeFundTransfer(transferId) {
  return request({
    url: '/pension/fundTransfer/execute/' + transferId,
    method: 'put'
  })
}

// 查询待处理的划拨记录
export function getPendingTransfers() {
  return request({
    url: '/pension/fundTransfer/pending',
    method: 'get'
  })
}

// 生成月度划拨单
export function generateFundTransfer(data) {
  return request({
    url: '/pension/fundTransfer/generate',
    method: 'post',
    data: data
  })
}

// 手动发起划拨申请
export function applyManualTransfer(data) {
  return request({
    url: '/pension/transfer/apply',
    method: 'post',
    data: data
  })
}

// 执行自动划拨
export function executeAutoTransfer() {
  return request({
    url: '/pension/fundTransfer/auto',
    method: 'post'
  })
}

// 统计划拨金额
export function getTransferStatistics(institutionId, startDate, endDate) {
  return request({
    url: '/pension/transfer/statistics',
    method: 'get',
    params: {
      institutionId: institutionId,
      startDate: startDate,
      endDate: endDate
    }
  })
}