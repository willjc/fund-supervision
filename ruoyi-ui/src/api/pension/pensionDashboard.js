import request from '@/utils/request'

// 获取核心统计数据
export function getStatistics() {
  return request({
    url: '/pension/dashboard/statistics',
    method: 'get'
  })
}

// 获取资金数据
export function getFundData() {
  return request({
    url: '/pension/dashboard/fund-data',
    method: 'get'
  })
}

// 获取待处理事项
export function getPendingItems() {
  return request({
    url: '/pension/dashboard/pending-items',
    method: 'get'
  })
}

// 获取申请列表
export function getApplications() {
  return request({
    url: '/pension/dashboard/applications',
    method: 'get'
  })
}
