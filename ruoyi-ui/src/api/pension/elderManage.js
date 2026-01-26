import request from '@/utils/request'

// 查询老人档案列表
export function listElderManage(query) {
  return request({
    url: '/pension/elder/manage/list',
    method: 'get',
    params: query
  })
}

// 查询老人档案详细
export function getElderManage(elderId) {
  return request({
    url: '/pension/elder/manage/' + elderId,
    method: 'get'
  })
}

// 新增老人档案
export function addElderManage(data) {
  return request({
    url: '/pension/elder/manage',
    method: 'post',
    data: data
  })
}

// 修改老人档案
export function updateElderManage(data) {
  return request({
    url: '/pension/elder/manage',
    method: 'put',
    data: data
  })
}

// 删除老人档案
export function delElderManage(elderIds) {
  return request({
    url: '/pension/elder/manage/' + elderIds,
    method: 'delete'
  })
}

// 导出老人档案
export function exportElderManage(query) {
  return request({
    url: '/pension/elder/manage/export',
    method: 'post',
    params: query
  })
}

// 获取老人档案选项(用于下拉选择)
export function getElderOptions() {
  return request({
    url: '/pension/elder/manage/options',
    method: 'get'
  })
}
