import dayjs from 'dayjs'

/**
 * 格式化金额
 * @param {Number} amount 金额(分)
 * @param {Number} decimals 小数位数
 * @returns {String} 格式化后的金额
 */
export function formatMoney(amount, decimals = 2) {
  if (!amount && amount !== 0) return '0.00'
  const num = parseFloat(amount) / 100
  return num.toFixed(decimals)
}

/**
 * 格式化日期
 * @param {String|Date} date 日期
 * @param {String} format 格式
 * @returns {String} 格式化后的日期
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return ''
  return dayjs(date).format(format)
}

/**
 * 格式化日期时间
 * @param {String|Date} date 日期时间
 * @returns {String} 格式化后的日期时间
 */
export function formatDateTime(date) {
  return formatDate(date, 'YYYY-MM-DD HH:mm:ss')
}

/**
 * 格式化时间(相对时间)
 * @param {String|Date} date 日期时间
 * @returns {String} 相对时间描述
 */
export function formatRelativeTime(date) {
  if (!date) return ''

  const now = dayjs()
  const target = dayjs(date)
  const diffMinutes = now.diff(target, 'minute')
  const diffHours = now.diff(target, 'hour')
  const diffDays = now.diff(target, 'day')

  if (diffMinutes < 1) {
    return '刚刚'
  } else if (diffMinutes < 60) {
    return `${diffMinutes}分钟前`
  } else if (diffHours < 24) {
    return `${diffHours}小时前`
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return formatDate(date)
  }
}

/**
 * 隐藏手机号中间四位
 * @param {String} phone 手机号
 * @returns {String} 隐藏后的手机号
 */
export function hidePhone(phone) {
  if (!phone) return ''
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/**
 * 隐藏身份证号中间部分
 * @param {String} idCard 身份证号
 * @returns {String} 隐藏后的身份证号
 */
export function hideIdCard(idCard) {
  if (!idCard) return ''
  return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
}

/**
 * 千位分隔符
 * @param {Number} num 数字
 * @returns {String} 格式化后的数字
 */
export function formatNumber(num) {
  if (!num && num !== 0) return '0'
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}
