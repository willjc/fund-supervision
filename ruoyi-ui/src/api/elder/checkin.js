import request from '@/utils/request'

// 查询老人入住申请列表
export function listCheckIn(query) {
  return request({
    url: '/elder/checkin/list',
    method: 'get',
    params: query
  })
}

// 查询老人入住申请详细
export function getCheckIn(checkInId) {
  return request({
    url: '/elder/checkin/' + checkInId,
    method: 'get'
  })
}

// 新增老人入住申请
export function addCheckIn(data) {
  return request({
    url: '/elder/checkin',
    method: 'post',
    data: data
  })
}

// 修改老人入住申请
export function updateCheckIn(data) {
  return request({
    url: '/elder/checkin',
    method: 'put',
    data: data
  })
}

// 审批入住申请
export function approveCheckIn(data) {
  return request({
    url: '/elder/checkin/approve',
    method: 'put',
    data: data
  })
}

// 删除老人入住申请
export function delCheckIn(checkInId) {
  return request({
    url: '/elder/checkin/' + checkInId,
    method: 'delete'
  })
}

// 导出老人入住申请
export function exportCheckIn(query) {
  return request({
    url: '/elder/checkin/export',
    method: 'post',
    data: query
  })
}