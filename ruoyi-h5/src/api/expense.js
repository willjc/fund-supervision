import request from '@/utils/request'

/**
 * 获取老人账户余额信息
 * @param {Number} elderId - 老人ID
 */
export function getAccountInfo(elderId) {
  return request({
    url: `/h5/account/${elderId}`,
    method: 'get'
  })
}

/**
 * 获取老人费用明细记录
 * @param {Number} elderId - 老人ID
 * @param {String} type - 费用类型(all/deposit/service/other)
 * @param {Number} pageNum - 页码
 * @param {Number} pageSize - 每页数量
 */
export function getExpenseList(elderId, type = 'all', pageNum = 1, pageSize = 20) {
  return request({
    url: '/h5/expense/list',
    method: 'get',
    params: {
      elderId,
      type,
      pageNum,
      pageSize
    }
  })
}

/**
 * 获取老人列表
 */
export function getElderList() {
  return request({
    url: '/h5/elder/list',
    method: 'get'
  })
}