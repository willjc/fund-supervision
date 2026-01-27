import request from '@/utils/request'

// 查询余额预警列表
export function listBalanceWarning(query) {
  return request({
    url: '/pension/balanceWarning/list',
    method: 'get',
    params: query
  })
}

// 查询余额预警详细
export function getBalanceWarning(warningId) {
  return request({
    url: '/pension/balanceWarning/' + warningId,
    method: 'get'
  })
}

// 根据机构ID查询预警记录
export function getBalanceWarningByInstitutionId(institutionId) {
  return request({
    url: '/pension/balanceWarning/institution/' + institutionId,
    method: 'get'
  })
}

// 获取预警统计数据
export function getWarningStatistics(institutionId) {
  return request({
    url: '/pension/balanceWarning/statistics',
    method: 'get',
    params: {
      institutionId: institutionId
    }
  })
}

// 获取严重预警账户
export function getCriticalWarnings(institutionId) {
  return request({
    url: '/pension/balanceWarning/critical',
    method: 'get',
    params: {
      institutionId: institutionId
    }
  })
}

// 新增余额预警
export function addBalanceWarning(data) {
  return request({
    url: '/pension/balanceWarning',
    method: 'post',
    data: data
  })
}

// 修改余额预警
export function updateBalanceWarning(data) {
  return request({
    url: '/pension/balanceWarning',
    method: 'put',
    data: data
  })
}

// 删除余额预警
export function delBalanceWarning(warningId) {
  return request({
    url: '/pension/balanceWarning/' + warningId,
    method: 'delete'
  })
}

// 批量处理预警
export function batchHandleWarnings(data) {
  return request({
    url: '/pension/balanceWarning/batchHandle',
    method: 'post',
    data: data
  })
}

// 发送预警通知
export function sendWarningNotification(warningId) {
  return request({
    url: '/pension/balanceWarning/notify/' + warningId,
    method: 'post'
  })
}

// 自动生成预警记录
export function autoGenerateWarnings() {
  return request({
    url: '/pension/balanceWarning/autoGenerate',
    method: 'post'
  })
}

// 清除已处理预警
export function clearHandledWarnings(institutionId) {
  return request({
    url: '/pension/balanceWarning/clear',
    method: 'delete',
    params: {
      institutionId: institutionId
    }
  })
}