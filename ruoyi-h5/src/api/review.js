import request from '@/utils/request'

/**
 * 查询订单评价状态
 * @param {String} orderId - 订单ID
 */
export function getReviewByOrderId(orderId) {
  return request({
    url: `/h5/review/order/${orderId}`,
    method: 'get'
  })
}

/**
 * 提交评价
 * @param {Object} data - 评价数据
 */
export function submitReview(data) {
  return request({
    url: '/h5/review/create',
    method: 'post',
    data: data
  })
}

/**
 * 获取机构评价列表（仅显示已通过的）
 * @param {Number} institutionId - 机构ID
 * @param {Number} pageNum - 页码
 * @param {Number} pageSize - 每页数量
 */
export function getReviewList(institutionId, pageNum = 1, pageSize = 10) {
  return request({
    url: '/h5/review/list/' + institutionId,
    method: 'get',
    params: {
      pageNum,
      pageSize
    }
  })
}

/**
 * 获取机构评价统计信息
 * @param {Number} institutionId - 机构ID
 */
export function getReviewStatistics(institutionId) {
  return request({
    url: `/h5/review/statistics/${institutionId}`,
    method: 'get'
  })
}

/**
 * 获取机构最新评价
 * @param {Number} institutionId - 机构ID
 * @param {Number} limit - 限制数量，默认5条
 */
export function getLatestReviews(institutionId, limit = 5) {
  return request({
    url: `/h5/review/latest/${institutionId}`,
    method: 'get',
    params: {
      limit
    }
  })
}

/**
 * 获取用户的评价记录
 * @param {Number} pageNum - 页码
 * @param {Number} pageSize - 每页数量
 */
export function getUserReviewList(pageNum = 1, pageSize = 10) {
  return request({
    url: '/h5/review/user/list',
    method: 'get',
    params: {
      pageNum,
      pageSize
    }
  })
}