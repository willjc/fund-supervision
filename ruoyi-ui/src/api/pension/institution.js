import request from '@/utils/request'

// 查询养老机构列表
export function listPensionInstitution(query) {
  return request({
    url: '/pension/institution/list',
    method: 'get',
    params: query
  })
}

// 查询养老机构详细
export function getPensionInstitution(institutionId) {
  return request({
    url: '/pension/institution/' + institutionId,
    method: 'get'
  })
}

// 新增养老机构
export function addPensionInstitution(data) {
  return request({
    url: '/pension/institution',
    method: 'post',
    data: data
  })
}

// 修改养老机构
export function updatePensionInstitution(data) {
  return request({
    url: '/pension/institution',
    method: 'put',
    data: data
  })
}

// 审批机构入驻申请
export function approveInstitution(data) {
  return request({
    url: '/pension/institution/approve',
    method: 'put',
    data: data
  })
}

// 删除养老机构
export function delPensionInstitution(institutionId) {
  return request({
    url: '/pension/institution/' + institutionId,
    method: 'delete'
  })
}

// 获取字典数据
export function getDictData(dictType) {
  return request({
    url: `/system/dict/data/type/${dictType}`,
    method: 'get'
  })
}

// 获取所有机构列表（不分页，用于下拉选择）
export function listAllInstitutions() {
  return request({
    url: '/pension/institution/all',
    method: 'get'
  })
}

// 以下是别名导出,与index.vue中的导入保持一致
export const listInstitution = listPensionInstitution
export const getInstitution = getPensionInstitution
export const addInstitution = addPensionInstitution
export const updateInstitution = updatePensionInstitution
export const delInstitution = delPensionInstitution