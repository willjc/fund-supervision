/**
 * 验证手机号
 * @param {String} phone 手机号
 * @returns {Boolean} 是否有效
 */
export function validatePhone(phone) {
  return /^1[3-9]\d{9}$/.test(phone)
}

/**
 * 验证身份证号
 * @param {String} idCard 身份证号
 * @returns {Boolean} 是否有效
 */
export function validateIdCard(idCard) {
  return /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)
}

/**
 * 验证邮箱
 * @param {String} email 邮箱
 * @returns {Boolean} 是否有效
 */
export function validateEmail(email) {
  return /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(email)
}

/**
 * 验证密码强度(至少8位,包含数字和字母)
 * @param {String} password 密码
 * @returns {Boolean} 是否有效
 */
export function validatePassword(password) {
  return /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/.test(password)
}

/**
 * 验证URL
 * @param {String} url URL地址
 * @returns {Boolean} 是否有效
 */
export function validateURL(url) {
  const pattern = new RegExp(
    '^(https?:\\/\\/)?' + // protocol
    '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|' + // domain name
    '((\\d{1,3}\\.){3}\\d{1,3}))' + // OR ip (v4) address
    '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*' + // port and path
    '(\\?[;&a-z\\d%_.~+=-]*)?' + // query string
    '(\\#[-a-z\\d_]*)?$', // fragment locator
    'i'
  )
  return pattern.test(url)
}
