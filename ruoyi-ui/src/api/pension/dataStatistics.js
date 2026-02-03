import request from '@/utils/request'
import { listAreas } from './areaStreet'

// 获取区县列表（复用areaStreet的listAreas方法）
export function listDistricts() {
  return listAreas();
}

// 总体概览列表
export function listOverall(query) {
  return request({
    url: '/pension/dataStatistics/overall/list',
    method: 'get',
    params: query
  })
}

// 导出总体概览
export function exportOverall(query) {
  return request({
    url: '/pension/dataStatistics/overall/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

// 机构情况列表
export function listInstitution(query) {
  return request({
    url: '/pension/dataStatistics/institution/list',
    method: 'get',
    params: query
  })
}

// 导出机构情况
export function exportInstitution(query) {
  return request({
    url: '/pension/dataStatistics/institution/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

// 资金情况列表
export function listFund(query) {
  return request({
    url: '/pension/dataStatistics/fund/list',
    method: 'get',
    params: query
  })
}

// 导出资金情况
export function exportFund(query) {
  return request({
    url: '/pension/dataStatistics/fund/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

// 预警情况列表
export function listWarning(query) {
  return request({
    url: '/pension/dataStatistics/warning/list',
    method: 'get',
    params: query
  })
}

// 导出预警情况
export function exportWarning(query) {
  return request({
    url: '/pension/dataStatistics/warning/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

// 机构详细列表
export function listInstitutionDetail(query) {
  return request({
    url: '/pension/dataStatistics/institutionList/list',
    method: 'get',
    params: query
  })
}

// 导出机构列表
export function exportInstitutionList(query) {
  return request({
    url: '/pension/dataStatistics/institutionList/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

// 老人详细列表
export function listElderDetail(query) {
  return request({
    url: '/pension/dataStatistics/elderList/list',
    method: 'get',
    params: query
  })
}

// 导出老人列表
export function exportElderList(query) {
  return request({
    url: '/pension/dataStatistics/elderList/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}

// 资金详细列表
export function listFundDetail(query) {
  return request({
    url: '/pension/dataStatistics/fundList/list',
    method: 'get',
    params: query
  })
}

// 导出资金列表
export function exportFundList(query) {
  return request({
    url: '/pension/dataStatistics/fundList/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  })
}
