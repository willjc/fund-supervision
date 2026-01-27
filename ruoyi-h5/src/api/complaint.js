import request from '@/utils/request'

/**
 * 提交投诉
 * @param {Object} data 投诉信息
 */
export function submitComplaint(data) {
  return request({
    url: '/h5/complaint/submit',
    method: 'post',
    data
  })
}

/**
 * 获取我的投诉列表
 */
export function getComplaintList() {
  return request({
    url: '/h5/complaint/list',
    method: 'get'
  })
}

/**
 * 获取投诉详情
 * @param {Number} complaintId 投诉ID
 */
export function getComplaintDetail(complaintId) {
  return request({
    url: '/h5/complaint/detail',
    method: 'get',
    params: { complaintId }
  })
}

/**
 * 上传投诉图片
 * @param {File} file 图片文件
 */
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/h5/complaint/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
