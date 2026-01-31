import request from '@/utils/request'

// 获取测试数据统计
export function getTestDataStatus() {
  return request({
    url: '/system/cleanTestData/status',
    method: 'get'
  })
}

// 清除测试数据
export function executeClean() {
  return request({
    url: '/system/cleanTestData/execute',
    method: 'post'
  })
}
