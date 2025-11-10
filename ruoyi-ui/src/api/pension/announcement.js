import request from '@/utils/request'

// 查询公告列表
export function listAnnouncement(query) {
  return request({
    url: '/pension/announcement/list',
    method: 'get',
    params: query
  })
}

// 获取公告详情
export function getDetail(id) {
  return request({
    url: `/pension/announcement/detail/${id}`,
    method: 'get'
  })
}

// 标记公告为已读
export function markAsRead(id) {
  return request({
    url: `/pension/announcement/mark-read/${id}`,
    method: 'post'
  })
}

// 下载公告附件
export function downloadAttachment(fileId) {
  return request({
    url: `/pension/announcement/download/${fileId}`,
    method: 'get'
  })
}
