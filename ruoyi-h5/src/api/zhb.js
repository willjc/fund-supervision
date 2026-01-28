import request from '@/utils/request'

/**
 * 郑好办登录
 * @param {Object} data 登录信息 {authCode}
 * @returns {Promise}
 */
export function loginByZhengHaoBan(data) {
  return request({
    url: '/h5/user/login/zhb',
    method: 'post',
    data
  })
}
