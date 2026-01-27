import request from '@/utils/request'

// 获取仪表板概览数据
export function getOverview() {
  return request({
    url: '/pension/supervision/dashboard/overview',
    method: 'get'
  })
}

// 获取待办事项统计
export function getTodos() {
  return request({
    url: '/pension/supervision/dashboard/todos',
    method: 'get'
  })
}

// 获取统计图表数据
export function getCharts() {
  return request({
    url: '/pension/supervision/dashboard/charts',
    method: 'get'
  })
}

// 获取近7天审批趋势
export function getTrends() {
  return request({
    url: '/pension/supervision/dashboard/trends',
    method: 'get'
  })
}
