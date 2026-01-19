import request from '@/utils/request'

// ========== 机构账号管理 ==========

// 查询已创建机构账号列表
export function listInstitutionAccounts(query) {
  return request({
    url: '/supervision/institution/account/list',
    method: 'get',
    params: query
  })
}

// 查询已审核通过的机构列表（用于评级选择）
export function listApprovedInstitutions(query) {
  return request({
    url: '/supervision/institution/account/approved/list',
    method: 'get',
    params: query
  })
}

// 新增单个机构账号
export function addInstitutionAccount(data) {
  return request({
    url: '/supervision/institution/account/add',
    method: 'post',
    data: data
  })
}

// 批量导入机构账号
export function batchImport(data) {
  return request({
    url: '/supervision/institution/batch-import',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 下载Excel导入模板
export function downloadTemplate() {
  return request({
    url: '/supervision/institution/download-template',
    method: 'get',
    responseType: 'blob'
  })
}

// 修改机构基本信息
export function editInstitutionAccount(data) {
  return request({
    url: '/supervision/institution/account/edit',
    method: 'put',
    data: data
  })
}

// 重置机构管理员密码
export function resetPassword(institutionId) {
  return request({
    url: '/supervision/institution/account/resetPassword/' + institutionId,
    method: 'put'
  })
}

// 删除机构账号
export function removeInstitutionAccount(institutionIds) {
  return request({
    url: '/supervision/institution/account/' + institutionIds,
    method: 'delete'
  })
}

// ========== 机构入驻审批 ==========

// 查询机构信息列表
export function listInstitution(query) {
  return request({
    url: '/pension/supervision/institution/approval/list',
    method: 'get',
    params: query
  })
}

// 查询机构信息详细
export function getInstitution(institutionId) {
  return request({
    url: '/pension/supervision/institution/approval/detail/' + institutionId,
    method: 'get'
  })
}

// 新增机构信息(废弃,使用addInstitutionAccount代替)
export function addInstitution(data) {
  return request({
    url: '/supervision/institution/register',
    method: 'post',
    data: data
  })
}

// 修改机构信息
export function updateInstitution(data) {
  return request({
    url: '/supervision/institution/update',
    method: 'put',
    data: data
  })
}

// 删除机构信息
export function delInstitution(institutionId) {
  return request({
    url: '/supervision/institution/delete/' + institutionId,
    method: 'delete'
  })
}

// ========== 机构储备监管审批 ==========

// 查询机构储备监管审批列表
export function listReserve(query) {
  return request({
    url: '/supervision/institution/reserve/list',
    method: 'get',
    params: query
  })
}

// 审批机构储备监管申请
export function approveReserve(data) {
  return request({
    url: '/supervision/institution/reserve/approve',
    method: 'post',
    data: data
  })
}

// ========== 机构评级管理 ==========

// 查询机构评级管理列表
export function listRating(query) {
  return request({
    url: '/supervision/institution/rating/list',
    method: 'get',
    params: query
  })
}

// 查询机构评级详细
export function getRating(ratingId) {
  return request({
    url: '/supervision/institution/rating/' + ratingId,
    method: 'get'
  })
}

// 新增机构评级
export function addRating(data) {
  return request({
    url: '/supervision/institution/rating/add',
    method: 'post',
    data: data
  })
}

// 更新机构评级
export function updateRating(data) {
  return request({
    url: '/supervision/institution/rating/update',
    method: 'put',
    data: data
  })
}

// 删除机构评级
export function delRating(ratingIds) {
  return request({
    url: '/supervision/institution/rating/' + ratingIds,
    method: 'delete'
  })
}

// 导出机构评级数据
export function exportRating(query) {
  return request({
    url: '/supervision/institution/rating/export',
    method: 'post',
    params: query
  })
}

// ========== 黑名单管理 ==========

// 查询黑名单管理列表
export function listBlacklist(query) {
  return request({
    url: '/supervision/institution/blacklist/list',
    method: 'get',
    params: query
  })
}

// 添加黑名单
export function addBlacklist(data) {
  return request({
    url: '/supervision/institution/blacklist/add',
    method: 'post',
    data: data
  })
}

// 移除黑名单
export function removeBlacklist(id) {
  return request({
    url: '/supervision/institution/blacklist/remove/' + id,
    method: 'delete'
  })
}

// ========== 机构入驻申请驳回补充 ==========

// 驳回补充机构入驻申请
export function supplementInstitution(institutionId, data) {
  return request({
    url: '/supervision/institution/supplement/' + institutionId,
    method: 'post',
    data: data
  })
}

// 批量驳回补充
export function batchSupplement(data) {
  return request({
    url: '/supervision/institution/batch-supplement',
    method: 'post',
    data: data
  })
}

// ========== 统计数据 ==========

// 获取机构统计数据
export function getInstitutionStatistics() {
  return request({
    url: '/pension/supervision/institution/approval/statistics',
    method: 'get'
  })
}

// 获取机构老人列表
export function getInstitutionElders(institutionId) {
  return request({
    url: '/pension/supervision/institution/elders/' + institutionId,
    method: 'get'
  })
}

// ========== 解除监管管理 ==========

// 查询机构解除监管申请列表
export function listReleaseSupervision(query) {
  return request({
    url: '/supervision/institution/release/list',
    method: 'get',
    params: query
  })
}

// 查询机构解除监管申请详细
export function getReleaseSupervision(releaseId) {
  return request({
    url: '/supervision/institution/release/' + releaseId,
    method: 'get'
  })
}

// 批准解除监管
export function approveRelease(releaseId, data) {
  return request({
    url: '/supervision/institution/release/approve/' + releaseId,
    method: 'post',
    data: data
  })
}

// 驳回解除监管申请
export function rejectRelease(releaseId, data) {
  return request({
    url: '/supervision/institution/release/reject/' + releaseId,
    method: 'post',
    data: data
  })
}

// 获取解除监管统计数据
export function getReleaseStatistics() {
  return request({
    url: '/supervision/institution/release/statistics',
    method: 'get'
  })
}

// ========== 机构入驻审批 & 维护审批 ==========

// 审批通过机构入驻申请（支持待审批0和维护待审批6）
export function approveInstitution(institutionId) {
  return request({
    url: '/pension/supervision/institution/approval/approve/' + institutionId,
    method: 'put'
  })
}

// 审批驳回机构入驻申请（支持待审批0和维护待审批6）
export function rejectInstitution(institutionId, data) {
  return request({
    url: '/pension/supervision/institution/approval/reject/' + institutionId,
    method: 'put',
    data: data
  })
}

// 批量审批通过
export function batchApproveInstitution(institutionIds) {
  return request({
    url: '/pension/supervision/institution/approval/batchApprove',
    method: 'put',
    data: institutionIds
  })
}
