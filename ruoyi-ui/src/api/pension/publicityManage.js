import request from '@/utils/request'

// 查询机构公示信息列表
export function listPublicity(query) {
  return request({
    url: '/pension/publicity/list',
    method: 'get',
    params: query
  })
}

// 查询机构公示信息详细
export function getPublicity(publicId) {
  return request({
    url: '/pension/publicity/' + publicId,
    method: 'get'
  })
}

// 根据机构ID查询公示信息
export function getPublicityByInstitutionId(institutionId) {
  return request({
    url: '/pension/publicity/institution/' + institutionId,
    method: 'get'
  })
}

// 新增机构公示信息
export function addPublicity(data) {
  return request({
    url: '/pension/publicity',
    method: 'post',
    data: data
  })
}

// 修改机构公示信息
export function updatePublicity(data) {
  return request({
    url: '/pension/publicity',
    method: 'put',
    data: data
  })
}

// 删除机构公示信息
export function delPublicity(publicIds) {
  return request({
    url: '/pension/publicity/' + publicIds,
    method: 'delete'
  })
}

// 发布公示
export function publishPublicity(publicId) {
  return request({
    url: '/pension/publicity/publish/' + publicId,
    method: 'put'
  })
}

// 取消公示
export function unpublishPublicity(publicId) {
  return request({
    url: '/pension/publicity/unpublish/' + publicId,
    method: 'put'
  })
}

// 批量发布公示
export function batchPublish(publicIds) {
  return request({
    url: '/pension/publicity/batchPublish',
    method: 'put',
    data: publicIds
  })
}

// 导出机构公示信息
export function exportPublicity(query) {
  return request({
    url: '/pension/publicity/export',
    method: 'post',
    params: query
  })
}
