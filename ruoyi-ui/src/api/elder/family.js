import request from '@/utils/request'

// 查询家属列表
export function listFamily(elderId) {
  return request({
    url: '/elder/family/list',
    method: 'get',
    params: { elderId }
  })
}

// 新增家属
export function addFamily(data) {
  return request({
    url: '/elder/family',
    method: 'post',
    data: data
  })
}

// 修改家属
export function updateFamily(data) {
  return request({
    url: '/elder/family',
    method: 'put',
    data: data
  })
}

// 删除家属
export function delFamily(familyId) {
  return request({
    url: '/elder/family/' + familyId,
    method: 'delete'
  })
}
