import request from '@/utils/request'

// 查询幻灯片列表
export function listBanner(query) {
  return request({
    url: '/system/banner/list',
    method: 'get',
    params: query
  })
}

// 查询幻灯片详细
export function getBanner(bannerId) {
  return request({
    url: '/system/banner/' + bannerId,
    method: 'get'
  })
}

// 新增幻灯片
export function addBanner(data) {
  return request({
    url: '/system/banner',
    method: 'post',
    data: data
  })
}

// 修改幻��片
export function updateBanner(data) {
  return request({
    url: '/system/banner',
    method: 'put',
    data: data
  })
}

// 删除幻灯片
export function delBanner(bannerId) {
  return request({
    url: '/system/banner/' + bannerId,
    method: 'delete'
  })
}
