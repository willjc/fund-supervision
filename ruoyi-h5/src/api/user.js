import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data 登录信息
 */
export function login(data) {
  return request({
    url: '/h5/user/login',
    method: 'post',
    data
  })
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
  return request({
    url: '/h5/user/info',
    method: 'get'
  })
}

/**
 * 退出登录
 */
export function logout() {
  return request({
    url: '/h5/user/logout',
    method: 'post'
  })
}

/**
 * 发送验证码
 * @param {String} phone 手机号
 */
export function sendSmsCode(phone) {
  return request({
    url: '/h5/user/sms/send',
    method: 'post',
    data: { phone }
  })
}

/**
 * 验证码登录
 * @param {Object} data 登录信息
 */
export function loginByCode(data) {
  return request({
    url: '/h5/user/login/code',
    method: 'post',
    data
  })
}

/**
 * 修改密码
 * @param {Object} data 密码信息
 */
export function changePassword(data) {
  return request({
    url: '/h5/user/password/change',
    method: 'post',
    data
  })
}
