import request from '@/utils/request'

// 根据老人ID查询床位分配信息
export function getBedAllocationByElderId(elderId) {
  return request({
    url: '/pension/bedAllocation/elder/' + elderId,
    method: 'get'
  })
}

// 查询床位分配列表
export function listBedAllocation(query) {
  return request({
    url: '/pension/bedAllocation/list',
    method: 'get',
    params: query
  })
}
