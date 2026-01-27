import request from '@/utils/request'

/**
 * 获取账户列表
 */
export function getAccountList() {
  return request({
    url: '/h5/deposit/account/list',
    method: 'get'
  })
}

/**
 * 获取账户详情
 * @param {Number} accountId 账户ID
 */
export function getAccountDetail(accountId) {
  return request({
    url: `/h5/deposit/account/${accountId}`,
    method: 'get'
  })
}

/**
 * 获取账户余额
 * @param {Number} accountId 账户ID
 */
export function getAccountBalance(accountId) {
  return request({
    url: `/h5/deposit/account/${accountId}/balance`,
    method: 'get'
  })
}

/**
 * 获取申请列表
 * @param {Object} params 查询参数
 */
export function getApplyList(params) {
  return request({
    url: '/h5/deposit/apply/list',
    method: 'get',
    params
  })
}

/**
 * 获取申请详情
 * @param {Number} applyId 申请ID
 */
export function getApplyDetail(applyId) {
  return request({
    url: `/h5/deposit/apply/${applyId}`,
    method: 'get'
  })
}

/**
 * 提交家属审批
 * @param {Object} data 审批信息
 */
export function submitFamilyApproval(data) {
  return request({
    url: '/h5/deposit/apply/family/approve',
    method: 'post',
    data
  })
}

/**
 * 获取使用记录
 * @param {Object} params 查询参数
 */
export function getUsageRecords(params) {
  return request({
    url: '/h5/deposit/usage/list',
    method: 'get',
    params
  })
}

/**
 * 获取交易记录
 * @param {Object} params 查询参数
 */
export function getTransactionRecords(params) {
  return request({
    url: '/h5/deposit/transaction/list',
    method: 'get',
    params
  })
}
