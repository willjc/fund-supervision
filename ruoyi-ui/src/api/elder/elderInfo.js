import request from '@/utils/request'

// 查询老人信息详细
export function getElderInfo(elderId) {
  return request({
    url: '/pension/elder/info/' + elderId,
    method: 'get'
  })
}

// 新增老人信息
export function addElderInfo(data) {
  return request({
    url: '/pension/elder/info',
    method: 'post',
    data: data
  })
}

// 修改老人信息
export function updateElderInfo(data) {
  return request({
    url: '/pension/elder/info',
    method: 'put',
    data: data
  })
}

// 删除老人信息
export function delElderInfo(elderId) {
  return request({
    url: '/pension/elder/info/' + elderId,
    method: 'delete'
  })
}
