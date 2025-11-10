import request from '@/utils/request'

// 查询老人账户信息列表
export function listAccountInfo(query) {
  return request({
    url: '/pension/account/list',
    method: 'get',
    params: query
  })
}

// 查询老人账户信息详细
export function getAccountInfo(accountId) {
  return request({
    url: '/pension/account/' + accountId,
    method: 'get'
  })
}

// 根据老人ID查询账户信息
export function getAccountInfoByElderId(elderId) {
  return request({
    url: '/pension/account/elder/' + elderId,
    method: 'get'
  })
}

// 根据机构ID查询账户列表
export function getAccountInfoByInstitutionId(institutionId) {
  return request({
    url: '/pension/account/institution/' + institutionId,
    method: 'get'
  })
}

// 新增老人账户信息
export function addAccountInfo(data) {
  return request({
    url: '/pension/account',
    method: 'post',
    data: data
  })
}

// 修改老人账户信息
export function updateAccountInfo(data) {
  return request({
    url: '/pension/account',
    method: 'put',
    data: data
  })
}

// 删除老人账户信息
export function delAccountInfo(accountId) {
  return request({
    url: '/pension/account/' + accountId,
    method: 'delete'
  })
}

// 统计机构账户余额
export function getBalanceSummary(institutionId) {
  return request({
    url: '/pension/account/balance/summary',
    method: 'get',
    params: { institutionId: institutionId }
  })
}

// 查询余额不足的老人账户
export function getLowBalanceAccounts(months) {
  return request({
    url: '/pension/account/balance/low/' + months,
    method: 'get'
  })
}

// 冻结/解冻账户
export function changeAccountStatus(accountId, accountStatus) {
  return request({
    url: '/pension/account/status/' + accountId + '/' + accountStatus,
    method: 'put'
  })
}

// 销户
export function closeAccount(accountId) {
  return request({
    url: '/pension/account/close/' + accountId,
    method: 'put'
  })
}