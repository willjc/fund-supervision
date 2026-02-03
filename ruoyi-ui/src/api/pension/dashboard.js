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

// ==================== 机构管理员首页API ====================

// 获取机构首页概览数据（订单统计）
export function getInstitutionOverview() {
  return request({
    url: '/pension/dashboard/institution/overview',
    method: 'get'
  })
}

// 获取机构账户余额汇总
export function getInstitutionBalance() {
  return request({
    url: '/pension/dashboard/institution/balance',
    method: 'get'
  })
}

// 获取机构服务费拨付统计
export function getInstitutionTransfer() {
  return request({
    url: '/pension/dashboard/institution/transfer',
    method: 'get'
  })
}

// 获取机构押金申请统计
export function getInstitutionDeposit() {
  return request({
    url: '/pension/dashboard/institution/deposit',
    method: 'get'
  })
}

// 获取机构入驻人结构分析
export function getResidentStructure() {
  return request({
    url: '/pension/dashboard/institution/resident-structure',
    method: 'get'
  })
}