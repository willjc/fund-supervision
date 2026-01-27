import request from '@/utils/request'

// 查询投诉建议列表
export function listFeedback(query) {
  return request({
    url: '/pension/feedback/list',
    method: 'get',
    params: query
  })
}

// 查询投诉详情
export function getFeedbackDetail(complaintId) {
  return request({
    url: `/pension/feedback/detail/${complaintId}`,
    method: 'get'
  })
}

// 处理投诉
export function handleFeedback(data) {
  return request({
    url: '/pension/feedback/handle',
    method: 'post',
    data: data
  })
}

// 获取投诉统计信息
export function getStatistics() {
  return request({
    url: '/pension/feedback/statistics',
    method: 'get'
  })
}
