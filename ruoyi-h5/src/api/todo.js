import request from '@/utils/request'

// 获取待办事项列表
export function getTodoList(params) {
  return request({
    url: '/h5/todo/list',
    method: 'get',
    params
  })
}

// 获取待办事项统计
export function getTodoCount() {
  return request({
    url: '/h5/todo/count',
    method: 'get'
  })
}
