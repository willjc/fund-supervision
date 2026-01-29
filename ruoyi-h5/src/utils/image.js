/**
 * 图片URL处理工具
 * 解决生产环境图片URL需要拼接完整域名的问题
 */

/**
 * 处理图片URL，自动拼接baseUrl
 * @param {string} url - 图片URL，可以是相对路径或完整路径
 * @returns {string} 完整的图片URL
 */
export function getImageUrl(url) {
  if (!url) return ''

  // 外部链接直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }

  // 拼接baseUrl
  const baseUrl = process.env.VUE_APP_BASE_API || ''
  return baseUrl + (url.startsWith('/') ? url : '/' + url)
}
