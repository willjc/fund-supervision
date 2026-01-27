import request from '@/utils/request'

// 查询床位信息列表
export function listBed(query) {
  return request({
    url: '/elder/bed/list',
    method: 'get',
    params: query
  })
}

// 查询可用床位列表
export function listAvailableBeds(institutionId) {
  return request({
    url: '/elder/bed/available/' + institutionId,
    method: 'get'
  })
}

// 查询床位信息详细
export function getBed(bedId) {
  return request({
    url: '/elder/bed/' + bedId,
    method: 'get'
  })
}

// 新增床位信息
export function addBed(data) {
  return request({
    url: '/elder/bed',
    method: 'post',
    data: data
  })
}

// 修改床位信息
export function updateBed(data) {
  return request({
    url: '/elder/bed',
    method: 'put',
    data: data
  })
}

// 删除床位信息
export function delBed(bedId) {
  return request({
    url: '/elder/bed/' + bedId,
    method: 'delete'
  })
}

// 导出床位信息
export function exportBed(query) {
  return request({
    url: '/elder/bed/export',
    method: 'post',
    data: query
  })
}