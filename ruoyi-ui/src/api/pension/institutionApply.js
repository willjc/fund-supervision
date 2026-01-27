import request from '@/utils/request'

// 提交机构入驻申请
export function submitInstitutionApply(data) {
  return request({
    url: '/pension/institution/apply/submit',
    method: 'post',
    data: data
  })
}

// 保存申请草稿
export function saveDraftApply(data) {
  return request({
    url: '/pension/institution/apply/draft',
    method: 'post',
    data: data
  })
}

// 获取草稿数据
export function getDraftApply() {
  return request({
    url: '/pension/institution/apply/draft',
    method: 'get'
  })
}

// 查询申请状态
export function getApplyStatus(applyId) {
  return request({
    url: '/pension/institution/apply/status/' + applyId,
    method: 'get'
  })
}

// 提交维护申请
export function submitMaintainApply(data) {
  return request({
    url: '/pension/institution/maintain/submit',
    method: 'put',
    data: data
  })
}

// 保存维护草稿
export function saveMaintainDraft(data) {
  return request({
    url: '/pension/institution/maintain/draft',
    method: 'put',
    data: data
  })
}