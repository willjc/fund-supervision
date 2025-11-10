import request from '@/utils/request'

// 获取首页核心业务统计数据
export function getDashboardOverview() {
  return request({
    url: '/pension/bigscreen/dashboard/overview',
    method: 'get'
  })
}

// 获取首页资金监管概览数据
export function getFundsOverview() {
  return request({
    url: '/pension/bigscreen/dashboard/funds-overview',
    method: 'get'
  })
}

// 获取首页机构运营状态数据
export function getOperationsOverview() {
  return request({
    url: '/pension/bigscreen/dashboard/operations-overview',
    method: 'get'
  })
}

// 获取首页风险监控预警数据
export function getRiskOverview() {
  return request({
    url: '/pension/bigscreen/dashboard/risk-overview',
    method: 'get'
  })
}