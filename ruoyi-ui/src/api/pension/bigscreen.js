import request from '@/utils/request'

// 获取养老机构分布统计
export function getInstitutionDistribution() {
  return request({
    url: '/pension/bigscreen/institution/distribution',
    method: 'get'
  })
}

// 获取机构数量统计
export function getInstitutionStatistics() {
  return request({
    url: '/pension/bigscreen/institution/statistics',
    method: 'get'
  })
}

// 获取机构等级分布
export function getInstitutionLevels() {
  return request({
    url: '/pension/bigscreen/institution/levels',
    method: 'get'
  })
}

// 获取床位使用情况
export function getBedUsage() {
  return request({
    url: '/pension/bigscreen/bed/usage',
    method: 'get'
  })
}

// 获取老人年龄分布
export function getElderAgeDistribution() {
  return request({
    url: '/pension/bigscreen/elder/age-distribution',
    method: 'get'
  })
}

// 获取实时入住数据
export function getRealtimeData() {
  return request({
    url: '/pension/bigscreen/realtime',
    method: 'get'
  })
}

// ==================== 资金监管相关接口 ====================

// 获取资金监管总览数据
export function getFundOverview() {
  return request({
    url: '/pension/bigscreen/fund/overview',
    method: 'get'
  })
}

// 获取资金流向数据（桑基图格式）
export function getFundFlow() {
  return request({
    url: '/pension/bigscreen/fund/flow',
    method: 'get'
  })
}

// 获取各机构资金分布
export function getFundInstitutionDistribution() {
  return request({
    url: '/pension/bigscreen/fund/institution-distribution',
    method: 'get'
  })
}

// 获取资金趋势数据（近30天）
export function getFundTrends() {
  return request({
    url: '/pension/bigscreen/fund/trends',
    method: 'get'
  })
}

// 获取资金审批数据
export function getFundApprovalStats() {
  return request({
    url: '/pension/bigscreen/fund/approval-stats',
    method: 'get'
  })
}

// 获取预警资金数据
export function getFundWarnings() {
  return request({
    url: '/pension/bigscreen/fund/warnings',
    method: 'get'
  })
}

// ==================== 预警相关接口 ====================

// 获取预警总览数据
export function getWarningOverview() {
  return request({
    url: '/pension/bigscreen/warning/overview',
    method: 'get'
  })
}

// 获取预警类型分布
export function getWarningTypes() {
  return request({
    url: '/pension/bigscreen/warning/types',
    method: 'get'
  })
}

// 获取预警趋势数据
export function getWarningTrends() {
  return request({
    url: '/pension/bigscreen/warning/trends',
    method: 'get'
  })
}

// 获取机构预警排行
export function getWarningInstitutionRank() {
  return request({
    url: '/pension/bigscreen/warning/institution-rank',
    method: 'get'
  })
}

// 获取实时预警列表
export function getWarningRealtime() {
  return request({
    url: '/pension/bigscreen/warning/realtime',
    method: 'get'
  })
}

// 获取预警处理统计
export function getWarningProcessStats() {
  return request({
    url: '/pension/bigscreen/warning/process-stats',
    method: 'get'
  })
}