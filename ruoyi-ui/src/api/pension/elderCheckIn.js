import request from '@/utils/request'

// 查询老人入住申请列表
export function listElderCheckIn(query) {
  return request({
    url: '/elder/checkin/list',
    method: 'get',
    params: query
  })
}

// 查询老人入住申请详细
export function getElderCheckIn(checkInId) {
  return request({
    url: '/elder/checkin/' + checkInId,
    method: 'get'
  })
}

// 新增老人入住申请
export function addElderCheckIn(data) {
  return request({
    url: '/elder/checkin',
    method: 'post',
    data: data
  })
}

// 修改老人入住申请
export function updateElderCheckIn(data) {
  return request({
    url: '/elder/checkin',
    method: 'put',
    data: data
  })
}

// 删除老人入住申请
export function delElderCheckIn(checkInId) {
  return request({
    url: '/elder/checkin/' + checkInId,
    method: 'delete'
  })
}

// 审批老人入住申请
export function approveElderCheckIn(checkInId, approveStatus, approveOpinion) {
  return request({
    url: '/elder/checkin/approve/' + checkInId,
    method: 'put',
    params: {
      approveStatus: approveStatus,
      approveOpinion: approveOpinion
    }
  })
}

// 导出老人入住申请
export function exportElderCheckIn(query) {
  return request({
    url: '/elder/checkin/export',
    method: 'post',
    params: query
  })
}
