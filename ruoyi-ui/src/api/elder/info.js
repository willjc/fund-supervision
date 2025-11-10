import request from '@/utils/request'

// 查询老人基础信息列表
export function listElder(query) {
  return request({
    url: '/elder/info/list',
    method: 'get',
    params: query
  })
}

// 查询老人基础信息详细
export function getElder(elderId) {
  return request({
    url: '/elder/info/' + elderId,
    method: 'get'
  })
}

// 根据身份证号查询老人信息
export function getElderByIdCard(idCard) {
  return request({
    url: '/elder/info/idcard/' + idCard,
    method: 'get'
  })
}

// 新增老人基础信息
export function addElder(data) {
  return request({
    url: '/elder/info',
    method: 'post',
    data: data
  })
}

// 修改老人基础信息
export function updateElder(data) {
  return request({
    url: '/elder/info',
    method: 'put',
    data: data
  })
}

// 删除老人基础信息
export function delElder(elderId) {
  return request({
    url: '/elder/info/' + elderId,
    method: 'delete'
  })
}

// 导出老人基础信息
export function exportElder(query) {
  return request({
    url: '/elder/info/export',
    method: 'post',
    data: query
  })
}