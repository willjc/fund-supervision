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