import request from '@/utils/request'

// 查询区域街道信息列表
export function listAreaStreet(query) {
  return request({
    url: '/pension/area/list',
    method: 'get',
    params: query
  })
}

// 查询区域街道信息详细
export function getAreaStreet(id) {
  return request({
    url: '/pension/area/' + id,
    method: 'get'
  })
}

// 新增区域街道信息
export function addAreaStreet(data) {
  return request({
    url: '/pension/area',
    method: 'post',
    data: data
  })
}

// 修改区域街道信息
export function updateAreaStreet(data) {
  return request({
    url: '/pension/area',
    method: 'put',
    data: data
  })
}

// 删除区域街道信息
export function delAreaStreet(id) {
  return request({
    url: '/pension/area/' + id,
    method: 'delete'
  })
}

// 查询所有区域列表
export function listAreas() {
  return request({
    url: '/pension/area/areas',
    method: 'get'
  })
}

// 根据区域获取街道列表
export function listStreetsByArea(areaCode) {
  return request({
    url: '/pension/area/' + areaCode + '/streets',
    method: 'get'
  })
}

// 导入区域街道数据
export function importAreaStreet(data) {
  return request({
    url: '/pension/area/import',
    method: 'post',
    data: data
  })
}