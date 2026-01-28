/**
 * 郑好办JSAPI工具类
 * 用于在郑好办APP内调用授权相关接口
 */

/* global AlipayJSBridge */

/**
 * 判断是否在郑好办APP环境内
 * @returns {boolean}
 */
export function isZhengHaoBanEnv() {
  return typeof window.AlipayJSBridge !== 'undefined' || /ZhengHaoban/.test(navigator.userAgent)
}

/**
 * 等待AlipayJSBridge注入完成
 * @param {Function} callback 回调函数
 */
export function ready(callback) {
  if (window.AlipayJSBridge) {
    callback && callback()
  } else {
    document.addEventListener('AlipayJSBridgeReady', callback, false)
  }
}

/**
 * 获取用户授权码（郑好办）
 * @param {Object} options 配置参数
 * @param {string} options.moduleId 模块ID
 * @param {string} options.scope 授权范围 userInfo|userDetail
 * @returns {Promise} 返回Promise对象
 */
export function getUserAuthCode(options = {}) {
  return new Promise((resolve, reject) => {
    ready(() => {
      const moduleId = options.moduleId || '413781'
      const scope = options.scope || 'userDetail' // userDetail获取详细信息(含身份证)

      window.AlipayJSBridge.call(
        'userAuth',
        {
          moduleId,
          scope
        },
        (result) => {
          console.log('郑好办授权结果:', result)

          if (result && result.code === 0) {
            resolve(result.data.authCode)
          } else if (result && result.code === 2) {
            // 用户取消授权
            reject(new Error('用户取消授权'))
          } else if (result && result.code === 3) {
            // 用户取消登录
            reject(new Error('用户取消登录'))
          } else {
            reject(new Error(result.message || '授权失败'))
          }
        }
      )
    })
  })
}

/**
 * 关闭当前WebView容器
 */
export function closeWebView() {
  ready(() => {
    window.AlipayJSBridge.call('popWindow')
  })
}

/**
 * 郑好办登录
 * @param {Function} loginApi 后端登录API函数
 * @returns {Promise} 返回登录结果
 */
export async function loginByZhengHaoBan(loginApi) {
  // 1. 获取授权码
  const authCode = await getUserAuthCode()

  // 2. 调用后端接口登录
  const res = await loginApi({ authCode })

  return res
}
