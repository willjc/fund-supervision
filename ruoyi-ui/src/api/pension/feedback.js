import request from '@/utils/request'

// 查询投诉建议列表
export function listFeedback(query) {
  return request({
    url: '/pension/feedback/list',
    method: 'get',
    params: query
  })
}

// 提交投诉建议
export function submitFeedback(data) {
  return request({
    url: '/pension/feedback/submit',
    method: 'post',
    data: data
  })
}

// 查询反馈详情
export function getDetail(feedbackNo) {
  return request({
    url: `/pension/feedback/detail/${feedbackNo}`,
    method: 'get'
  })
}

// 上传附件
export function uploadAttachment(fileUrl) {
  return request({
    url: '/pension/feedback/upload-attachment',
    method: 'post',
    data: { file: fileUrl }
  })
}
