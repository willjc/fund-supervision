import request from '@/utils/request'

// 查询机构账户统计数据
export function getInstitutionStatistics() {
  return request({
    url: '/supervision/account/institution/statistics',
    method: 'get'
  })
}

// 查询机构账户列表
export function listInstitutionAccount(query) {
  return request({
    url: '/supervision/account/institution/list',
    method: 'get',
    params: query
  })
}

// 查询机构账户详细
export function getInstitutionAccount(accountId) {
  return request({
    url: '/supervision/account/institution/detail/' + accountId,
    method: 'get'
  })
}

// 新增机构账户
export function addInstitutionAccount(data) {
  return request({
    url: '/supervision/account/institution/add',
    method: 'post',
    data: data
  })
}

// 修改机构账户
export function updateInstitutionAccount(data) {
  return request({
    url: '/supervision/account/institution/update',
    method: 'put',
    data: data
  })
}

// 删除机构账户
export function delInstitutionAccount(accountId) {
  return request({
    url: '/supervision/account/institution/delete/' + accountId,
    method: 'delete'
  })
}

// 查询会员费管理列表
export function listMemberFee(query) {
  return request({
    url: '/supervision/account/member-fee/list',
    method: 'get',
    params: query
  })
}

// 会员费收取配置
export function configMemberFee(data) {
  return request({
    url: '/supervision/account/member-fee/config',
    method: 'post',
    data: data
  })
}

// 查询会员费配置详情
export function getMemberFeeConfig(configId) {
  return request({
    url: '/supervision/account/member-fee/detail/' + configId,
    method: 'get'
  })
}

// 查询监管账户维护列表
export function listSupervisionAccount(query) {
  return request({
    url: '/supervision/account/supervision/list',
    method: 'get',
    params: query
  })
}

// 监管账户资金划转
export function transferSupervisionFunds(data) {
  return request({
    url: '/supervision/account/supervision/transfer',
    method: 'post',
    data: data
  })
}

// 查询订单管理列表
export function listOrder(query) {
  return request({
    url: '/supervision/account/order/list',
    method: 'get',
    params: query
  })
}

// 查询订单详情
export function getOrderDetail(orderNo) {
  return request({
    url: '/supervision/account/order/detail/' + orderNo,
    method: 'get'
  })
}

// 查询账户余额列表
export function listAccountBalance(query) {
  return request({
    url: '/supervision/account/balance/list',
    method: 'get',
    params: query
  })
}

// 查询余额变动明细
export function getBalanceDetail(accountType, accountId, query) {
  return request({
    url: '/supervision/account/balance/detail/' + accountType + '/' + accountId,
    method: 'get',
    params: query
  })
}