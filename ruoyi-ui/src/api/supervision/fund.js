import request from '@/utils/request'

// ==================== 划付规则配置接口 ====================

// 查询划付规则配置列表
export function listTransferRule(query) {
  return request({
    url: '/supervision/fund/transfer-rule/list',
    method: 'get',
    params: query
  })
}

// 查询划付规则配置详细
export function getTransferRule(ruleId) {
  return request({
    url: '/supervision/fund/transfer-rule/' + ruleId,
    method: 'get'
  })
}

// 新增划付规则配置
export function addTransferRule(data) {
  return request({
    url: '/supervision/fund/transfer-rule',
    method: 'post',
    data: data
  })
}

// 修改划付规则配置
export function updateTransferRule(data) {
  return request({
    url: '/supervision/fund/transfer-rule',
    method: 'put',
    data: data
  })
}

// 删除划付规则配置
export function delTransferRule(ruleIds) {
  return request({
    url: '/supervision/fund/transfer-rule/' + ruleIds,
    method: 'delete'
  })
}

// ==================== 资金划付记录接口 ====================

// 查询资金划付记录列表
export function listTransferRecord(query) {
  return request({
    url: '/supervision/fund/transfer-record/list',
    method: 'get',
    params: query
  })
}

// 导出资金划付记录
export function exportTransferRecord(query) {
  return request({
    url: '/supervision/fund/transfer-record/export',
    method: 'post',
    params: query
  })
}

// 获取所有机构列表
export function getAllInstitutions() {
  return request({
    url: '/supervision/fund/institutions/all',
    method: 'get'
  })
}
