import request from '@/utils/request'

// 查询预警规则配置列表
export function listWarningConfig(query) {
  return request({
    url: '/supervision/warningConfig/list',
    method: 'get',
    params: query
  })
}

// 查询预警规则配置详细
export function getWarningConfig(ruleId) {
  return request({
    url: '/supervision/warningConfig/' + ruleId,
    method: 'get'
  })
}

// 根据规则编码获取配置
export function getWarningConfigByCode(ruleCode) {
  return request({
    url: '/supervision/warningConfig/code/' + ruleCode,
    method: 'get'
  })
}

// 新增预警规则配置
export function addWarningConfig(data) {
  return request({
    url: '/supervision/warningConfig',
    method: 'post',
    data: data
  })
}

// 修改预警规则配置
export function updateWarningConfig(data) {
  return request({
    url: '/supervision/warningConfig',
    method: 'put',
    data: data
  })
}

// 批量修改预警规则配置
export function batchUpdateWarningConfig(data) {
  return request({
    url: '/supervision/warningConfig/batch',
    method: 'put',
    data: data
  })
}

// 删除预警规则配置
export function delWarningConfig(ruleId) {
  return request({
    url: '/supervision/warningConfig/' + ruleId,
    method: 'delete'
  })
}
