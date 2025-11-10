import request from '@/utils/request'

// 获取押金余额
export function getDepositBalance() {
  return request({
    url: '/pension/deposit/balance',
    method: 'get'
  })
}

// 查询押金申请列表
export function listDeposit(query) {
  return request({
    url: '/pension/deposit/list',
    method: 'get',
    params: query
  })
}

// 提交押金使用申请
export function applyDeposit(data) {
  return request({
    url: '/pension/deposit/apply',
    method: 'post',
    data: data
  })
}

// 撤销押金申请
export function cancelApply(applyNo) {
  return request({
    url: `/pension/deposit/cancel/${applyNo}`,
    method: 'delete'
  })
}

// 查询申请详情
export function getDetail(applyNo) {
  return request({
    url: `/pension/deposit/detail/${applyNo}`,
    method: 'get'
  })
}
