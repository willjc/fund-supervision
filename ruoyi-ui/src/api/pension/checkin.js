import request from '@/utils/request'

// 创建入驻申请(一次性完成:老人信息+床位分配+订单生成)
export function createCheckin(data) {
  return request({
    url: '/pension/checkin/create',
    method: 'post',
    data: data
  })
}

// 查询入住人列表
export function listResident(query) {
  return request({
    url: '/pension/resident/list',
    method: 'get',
    params: query
  })
}

// 查询入住人详情
export function getResident(residentId) {
  return request({
    url: '/pension/resident/' + residentId,
    method: 'get'
  })
}

// 支付订单
export function payOrder(data) {
  return request({
    url: '/pension/order/pay',
    method: 'post',
    data: data
  })
}
