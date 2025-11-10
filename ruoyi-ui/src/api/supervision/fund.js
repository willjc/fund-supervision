import request from '@/utils/request'

// 查询资金记录查看
export function listFundRecord(query) {
  return request({
    url: '/supervision/fund/record/list',
    method: 'get',
    params: query
  })
}

// 查询资金统计概览
export function getFundStatistics(query) {
  return request({
    url: '/supervision/fund/statistics',
    method: 'get',
    params: query
  })
}

// 查询资金流动明细
export function listFundFlowDetail(query) {
  return request({
    url: '/supervision/fund/flow/detail',
    method: 'get',
    params: query
  })
}

// 查询分配规则配置列表
export function listAllocationRule(query) {
  return request({
    url: '/supervision/fund/allocation-rule/list',
    method: 'get',
    params: query
  })
}

// 查询分配规则配置详细
export function getAllocationRule(ruleId) {
  return request({
    url: '/supervision/fund/allocation-rule/detail/' + ruleId,
    method: 'get'
  })
}

// 新增分配规则配置
export function addAllocationRule(data) {
  return request({
    url: '/supervision/fund/allocation-rule/add',
    method: 'post',
    data: data
  })
}

// 修改分配规则配置
export function updateAllocationRule(data) {
  return request({
    url: '/supervision/fund/allocation-rule/update/' + data.ruleId,
    method: 'put',
    data: data
  })
}

// 删除分配规则配置
export function delAllocationRule(ruleId) {
  return request({
    url: '/supervision/fund/allocation-rule/delete/' + ruleId,
    method: 'delete'
  })
}

// 查询规则执行历史
export function listRuleExecutionHistory(ruleId, query) {
  return request({
    url: '/supervision/fund/allocation-rule/history/' + ruleId,
    method: 'get',
    params: query
  })
}