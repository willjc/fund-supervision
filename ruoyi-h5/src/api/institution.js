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

/**
 * 收藏机构
 * @param {Number} institutionId 机构ID
 */
export function favoriteInstitution(institutionId) {
  return request({
    url: `/h5/favorite/add/${institutionId}`,
    method: 'post'
  })
}

/**
 * 取消收藏机构
 * @param {Number} institutionId 机构ID
 */
export function unfavoriteInstitution(institutionId) {
  return request({
    url: `/h5/favorite/remove/${institutionId}`,
    method: 'delete'
  })
}

/**
 * 检查用户是否已收藏机构
 * @param {Number} institutionId 机构ID
 */
export function checkFavorite(institutionId) {
  return request({
    url: `/h5/favorite/check/${institutionId}`,
    method: 'get'
  })
}

/**
 * 获取用户收藏列表
 * @param {Object} params 查询参数
 */
export function getUserFavoriteList(params) {
  return request({
    url: '/h5/favorite/list',
    method: 'get',
    params
  })
}

/**
 * 获取字典数据
 * @param {String} dictType 字典类型
 */
export function getDictData(dictType) {
  return request({
    url: `/system/dict/data/type/${dictType}`,
    method: 'get'
  })
}

/**
 * 获取机构图片分类列表
 * @param {Number} institutionId 机构ID
 */
export function getInstitutionImages(institutionId) {
  return request({
    url: `/h5/institution/${institutionId}/images`,
    method: 'get'
  })
}
