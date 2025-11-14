import request from '@/utils/request'

/**
 * 获取养老机构列表
 * @param {Object} params 查询参数
 */
export function getInstitutionList(params) {
  return request({
    url: '/h5/institution/list',
    method: 'get',
    params
  })
}

/**
 * 获取养老机构详情
 * @param {Number} id 机构ID
 */
export function getInstitutionDetail(id) {
  return request({
    url: `/h5/institution/${id}`,
    method: 'get'
  })
}

/**
 * 搜索养老机构
 * @param {Object} params 搜索参数
 */
export function searchInstitution(params) {
  return request({
    url: '/h5/institution/search',
    method: 'get',
    params
  })
}

/**
 * 获取推荐机构
 * @param {Object} params 查询参数
 */
export function getRecommendInstitutions(params) {
  return request({
    url: '/h5/institution/recommend',
    method: 'get',
    params
  })
}

/**
 * 申请入住
 * @param {Object} data 申请信息
 */
export function applyEnter(data) {
  return request({
    url: '/h5/institution/apply',
    method: 'post',
    data
  })
}
