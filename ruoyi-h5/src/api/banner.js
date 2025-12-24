import request from '@/utils/request'

/**
 * 获取幻灯片列表
 * @param {Object} params - { position: 1-首页 2-机构页 }
 */
export function getBannerList(params) {
  return request({
    url: '/h5/banner/list',
    method: 'get',
    params
  })
}
