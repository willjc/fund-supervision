import request from '@/utils/request'

// 查询交易流水记录列表
export function listTransactionRecord(query) {
  return request({
    url: '/pension/transaction/list',
    method: 'get',
    params: query
  })
}

// 查询交易流水记录详细
export function getTransactionRecord(transactionId) {
  return request({
    url: '/pension/transaction/' + transactionId,
    method: 'get'
  })
}

// 根据账户ID查询交易记录
export function getTransactionRecordByAccountId(accountId) {
  return request({
    url: '/pension/transaction/account/' + accountId,
    method: 'get'
  })
}

// 根据机构ID查询交易记录
export function getTransactionRecordByInstitutionId(institutionId) {
  return request({
    url: '/pension/transaction/institution/' + institutionId,
    method: 'get'
  })
}

// 新增交易流水记录
export function addTransactionRecord(data) {
  return request({
    url: '/pension/transaction',
    method: 'post',
    data: data
  })
}

// 修改交易流水记录
export function updateTransactionRecord(data) {
  return request({
    url: '/pension/transaction',
    method: 'put',
    data: data
  })
}

// 删除交易流水记录
export function delTransactionRecord(transactionId) {
  return request({
    url: '/pension/transaction/' + transactionId,
    method: 'delete'
  })
}

// 查询交易统计
export function getTransactionStatistics(accountId, businessType, startDate, endDate) {
  return request({
    url: '/pension/transaction/statistics',
    method: 'get',
    params: {
      accountId: accountId,
      businessType: businessType,
      startDate: startDate,
      endDate: endDate
    }
  })
}

// 批量创建交易记录
export function batchCreateTransactionRecords(data) {
  return request({
    url: '/pension/transaction/batch',
    method: 'post',
    data: data
  })
}

// 获取今日交易汇总
export function getTodayTransactionSummary() {
  return request({
    url: '/pension/transaction/today',
    method: 'get'
  })
}

// 查询异常交易记录
export function getAbnormalTransactions() {
  return request({
    url: '/pension/transaction/abnormal',
    method: 'get'
  })
}

// ==================== 资金划拨记录接口 ====================

// 查询资金划拨记录列表（所有状态）
export function listFundTransferRecord(query) {
  return request({
    url: '/pension/bank/transfer-record/list',
    method: 'get',
    params: query
  })
}