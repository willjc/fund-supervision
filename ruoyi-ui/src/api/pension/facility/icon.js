import request from '@/utils/request'

// 查询设施图标配置列表
export function listFacilityIconConfig(query) {
  return request({
    url: '/pension/facility/icon/list',
    method: 'get',
    params: query
  })
}

// 查询设施图标配置详细
export function getFacilityIconConfig(id) {
  return request({
    url: '/pension/facility/icon/' + id,
    method: 'get'
  })
}

// 新增设施图标配置
export function addFacilityIconConfig(data) {
  return request({
    url: '/pension/facility/icon',
    method: 'post',
    data: data
  })
}

// 修改设施图标配置
export function updateFacilityIconConfig(data) {
  return request({
    url: '/pension/facility/icon',
    method: 'put',
    data: data
  })
}

// 删除设施图标配置
export function delFacilityIconConfig(id) {
  return request({
    url: '/pension/facility/icon/' + id,
    method: 'delete'
  })
}