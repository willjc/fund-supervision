import request from '@/utils/request'

// 查询资金划拨申请列表
export function listFundTransferApply(query) {
  return request({
    url: '/pension/fundTransferApply/list',
    method: 'get',
    params: query
  })
}

// 查询资金划拨申请详情
export function getFundTransferApply(applyId) {
  return request({
    url: '/pension/fundTransferApply/' + applyId,
    method: 'get'
  })
}

// 查询老人的待划拨划拨单列表
export function getPendingTransfer(elderId) {
  return request({
    url: '/pension/fundTransferApply/pendingTransfer/' + elderId,
    method: 'get'
  })
}

// 新增资金划拨申请
export function addFundTransferApply(data) {
  return request({
    url: '/pension/fundTransferApply',
    method: 'post',
    data: data
  })
}

// 修改资金划拨申请
export function updateFundTransferApply(data) {
  return request({
    url: '/pension/fundTransferApply',
    method: 'put',
    data: data
  })
}

// 删除资金划拨申请
export function delFundTransferApply(applyIds) {
  return request({
    url: '/pension/fundTransferApply/' + applyIds,
    method: 'delete'
  })
}

// 家属审批
export function familyApprove(data) {
  return request({
    url: '/pension/fundTransferApply/familyApprove',
    method: 'put',
    data: data
  })
}

// 监管审批
export function supervisionApprove(data) {
  return request({
    url: '/pension/fundTransferApply/supervisionApprove',
    method: 'put',
    data: data
  })
}

// 执行划拨
export function executeTransfer(applyId) {
  return request({
    url: '/pension/fundTransferApply/execute/' + applyId,
    method: 'put'
  })
}
