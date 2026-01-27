import request from '@/utils/request'

/**
 * 获取幻灯片列表
 */
export function getBannerList() {
  return request({
    url: '/h5/banner/list',
    method: 'get'
  })
}
