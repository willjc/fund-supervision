import request from '@/utils/request'

// 查询入住人列表
export function listResident(query) {
  return request({
    url: '/pension/resident/list',
    method: 'get',
    params: query
  })
}

// 入住人续费
export function renewResident(data) {
  return request({
    url: '/pension/resident/renew',
    method: 'post',
    data: data
  })
}

// 查询入住人详细
export function getResident(elderId) {
  return request({
    url: '/pension/resident/detail/' + elderId,
    method: 'get'
  })
}

// 删除入住人
export function delResident(residentId) {
  return request({
    url: '/pension/resident/delete/' + residentId,
    method: 'delete'
  })
}

// 入住人退费
export function refundResident(data) {
  return request({
    url: '/pension/resident/refund',
    method: 'post',
    data: data
  })
}

// 押金使用申请
export function applyDepositUse(data) {
  return request({
    url: '/pension/resident/depositUse',
    method: 'post',
    data: data
  })
}

// 导出入住人
export function exportResident(query) {
  return request({
    url: '/elder/resident/export',
    method: 'get',
    params: query
  })
}

// 下载导入模板
export function downloadTemplate() {
  return request({
    url: '/elder/resident/template',
    method: 'get'
  })
}

// 批量导入入住人
export function importResident(data) {
  return request({
    url: '/elder/resident/import',
    method: 'post',
    data: data
  })
}