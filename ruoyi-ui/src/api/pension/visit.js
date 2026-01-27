import request from '@/utils/request'

// 查询预约参观列表
export function listVisitReservation(query) {
  return request({
    url: '/pension/visit/list',
    method: 'get',
    params: query
  })
}

// 查询预约参观详细
export function getVisitReservation(reservationId) {
  return request({
    url: '/pension/visit/' + reservationId,
    method: 'get'
  })
}

// 标记预约为已完成
export function completeReservation(data) {
  return request({
    url: '/pension/visit/complete',
    method: 'post',
    data: data
  })
}

// 导出预约参观
export function exportVisitReservation(query) {
  return request({
    url: '/pension/visit/export',
    method: 'post',
    params: query
  })
}
