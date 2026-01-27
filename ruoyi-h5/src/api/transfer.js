import request from '@/utils/request'

/**
 * 获取划拨申请详情
 * @param {Number} applyId 申请ID
 */
export function getTransferDetail(applyId) {
  return request({
    url: `/h5/deposit/transfer/detail/${applyId}`,
    method: 'get'
  })
}

/**
 * 提交划拨申请家属审批
 * @param {Object} data 审批信息
 */
export function submitTransferApproval(data) {
  return request({
    url: '/h5/deposit/transfer/family/approve',
    method: 'post',
    data
  })
}
