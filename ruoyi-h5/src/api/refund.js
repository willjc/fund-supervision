import request from '@/utils/request'

/**
 * 申请退款
 * @param {Object} data - 退款数据
 */
export function applyRefund(data) {
  return request({
    url: '/h5/refund/apply',
    method: 'post',
    data
  })
}

/**
 * 获取退款详情
 * @param {Number} refundId - 退款ID
 */
export function getRefundDetail(refundId) {
  return request({
    url: `/h5/refund/${refundId}`,
    method: 'get'
  })
}

/**
 * 获取退款列表
 * @param {Object} params - 查询参数
 */
export function getRefundList(params) {
  return request({
    url: '/h5/refund/list',
    method: 'get',
    params
  })
}

/**
 * 审核退款
 * @param {Number} refundId - 退款ID
 * @param {Object} data - 审核数据
 */
export function reviewRefund(refundId, data) {
  return request({
    url: `/h5/refund/${refundId}/review`,
    method: 'post',
    data
  })
}

/**
 * 取消退款申请
 * @param {Number} refundId - 退款ID
 */
export function cancelRefund(refundId) {
  return request({
    url: `/h5/refund/${refundId}/cancel`,
    method: 'post'
  })
}
