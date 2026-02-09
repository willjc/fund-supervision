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
    url: '/h5/refund/detail',
    method: 'get',
    params: { refundId }
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

/**
 * 上传退款凭证图片
 * @param {File} file 图片文件
 */
export function uploadRefundImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/h5/refund/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
