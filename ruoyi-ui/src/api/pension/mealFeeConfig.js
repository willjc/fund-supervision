import request from '@/utils/request'

// 查询餐费配置列表
export function listMeal(query) {
  return request({
    url: '/elder/meal/list',
    method: 'get',
    params: query
  })
}

// 根据机构ID查询启用的餐费配置列表
export function listAvailableMeals(institutionId) {
  return request({
    url: '/elder/meal/available/' + institutionId,
    method: 'get'
  })
}

// 查询餐费配置详细
export function getMeal(configId) {
  return request({
    url: '/elder/meal/' + configId,
    method: 'get'
  })
}

// 新增餐费配置
export function addMeal(data) {
  return request({
    url: '/elder/meal',
    method: 'post',
    data: data
  })
}

// 修改餐费配置
export function updateMeal(data) {
  return request({
    url: '/elder/meal',
    method: 'put',
    data: data
  })
}

// 删除餐费配置
export function delMeal(configIds) {
  return request({
    url: '/elder/meal/' + configIds,
    method: 'delete'
  })
}

// 导出餐费配置
export function exportMeal(query) {
  return request({
    url: '/elder/meal/export',
    method: 'post',
    params: query
  })
}
