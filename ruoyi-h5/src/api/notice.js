import request from '@/utils/request'

/**
 * 获取通知公告列表
 */
export function getNoticeList() {
  return request({
    url: '/h5/notice/list',
    method: 'get'
  })
}

/**
 * 获取通知公告详情
 * @param {Number} noticeId 公告ID
 */
export function getNoticeDetail(noticeId) {
  return request({
    url: `/h5/notice/${noticeId}`,
    method: 'get'
  })
}
