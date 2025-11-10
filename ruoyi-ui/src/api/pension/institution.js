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