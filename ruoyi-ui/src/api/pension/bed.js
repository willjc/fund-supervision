import request from '@/utils/request'

// 查询床位列表
export function listBedInfo(query) {
  return request({
    url: '/elder/bed/list',
    method: 'get',
    params: query
  })
}

// 查询床位详细
export function getBedInfo(bedId) {
  return request({
    url: '/elder/bed/' + bedId,
    method: 'get'
  })
}

// 新增床位
export function addBedInfo(data) {
  return request({
    url: '/elder/bed',
    method: 'post',
    data: data
  })
}

// 修改床位
export function updateBedInfo(data) {
  return request({
    url: '/elder/bed',
    method: 'put',
    data: data
  })
}

// 删除床位
export function delBedInfo(bedId) {
  return request({
    url: '/elder/bed/' + bedId,
    method: 'delete'
  })
}

// 导出床位
export function exportBedInfo(query) {
  return request({
    url: '/elder/bed/export',
    method: 'post',
    params: query
  })
}
