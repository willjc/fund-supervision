import request from '@/utils/request'

// 提交预约
export function submitVisit(data) {
  return request({
    url: '/h5/visit/submit',
    method: 'post',
    data
  })
}

// 获取我的预约列表
export function getMyVisits(params) {
  return request({
    url: '/h5/visit/my',
    method: 'get',
    params
  })
}

// 获取预约详情
export function getVisitDetail(reservationId) {
  return request({
    url: `/h5/visit/${reservationId}`,
    method: 'get'
  })
}

// 取消预约
export function cancelVisit(reservationId) {
  return request({
    url: `/h5/visit/cancel/${reservationId}`,
    method: 'post'
  })
}
