import request from '@/utils/request'

// 查询机构评价审核列表
export function listReview(query) {
  return request({
    url: '/supervision/review/list',
    method: 'get',
    params: query
  })
}

// 查询机构评价审核详细
export function getReview(reviewId) {
  return request({
    url: '/supervision/review/' + reviewId,
    method: 'get'
  })
}

// 审核通过
export function approveReview(reviewId, data) {
  return request({
    url: '/supervision/review/approve/' + reviewId,
    method: 'put',
    data: data
  })
}

// 审核拒绝
export function rejectReview(reviewId, data) {
  return request({
    url: '/supervision/review/reject/' + reviewId,
    method: 'put',
    data: data
  })
}

// 批量审核通过
export function batchApprove(reviewIds) {
  return request({
    url: '/supervision/review/batchApprove',
    method: 'put',
    data: reviewIds
  })
}

// 删除机构评价审核
export function delReview(reviewId) {
  return request({
    url: '/supervision/review/' + reviewId,
    method: 'delete'
  })
}

// 获取审核统计信息
export function getReviewStatistics() {
  return request({
    url: '/supervision/review/statistics',
    method: 'get'
  })
}

// 导出机构评价审核
export function exportReview(query) {
  return request({
    url: '/supervision/review/export',
    method: 'post',
    data: query
  })
}