import axios from 'axios'
import { showToast, showDialog, showLoadingToast, closeToast } from 'vant'
import { getToken, removeToken, setToken, setUserInfo } from './auth'
import { autoLogin } from './zhb'
import { useUserStore } from '@/store/modules/user'

// 标记是否正在重新授权中
let isRefreshing = false
// 存储待重试的请求
let failedQueue = []

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API,
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加token
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 处理401重新授权
const handleTokenExpired = async () => {
  if (isRefreshing) {
    // 正在刷新token，将请求加入队列
    return new Promise((resolve, reject) => {
      failedQueue.push({ resolve, reject })
    })
  }

  isRefreshing = true

  try {
    // 显示重新授权提示
    showLoadingToast({
      message: '正在重新授权...',
      forbidClick: true,
      duration: 0
    })

    // 调用郑好办重新授权
    const res = await autoLogin()

    if (res.code === 200 && res.data) {
      // 保存新的token和用户信息
      setToken(res.data.token)
      setUserInfo(res.data.user)

      // 更新store
      const userStore = useUserStore()
      userStore.setToken(res.data.token)
      userStore.setUser(res.data.user)
      if (res.data.elders) {
        userStore.setElders(res.data.elders)
      }

      closeToast()

      // 处理队列中的请求
      failedQueue.forEach(prom => {
        prom.resolve(res.data.token)
      })
      failedQueue = []

      return res.data.token
    } else {
      throw new Error(res.msg || '重新授权失败')
    }
  } catch (error) {
    console.error('重新授权失败:', error)
    closeToast()

    // 显示错误提示
    showToast({
      message: error.message || '重新授权失败，请刷新页面重试',
      duration: 2000
    })

    // 失败后拒绝所有队列中的请求
    failedQueue.forEach(prom => {
      prom.reject(error)
    })
    failedQueue = []

    throw error
  } finally {
    isRefreshing = false
  }
}

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data

    // 如果返回的状态码为200,说明接口请求成功
    if (res.code === 200) {
      return res
    } else if (res.code === 401) {
      // 未授权,自动重新授权
      return handleTokenExpired().then(() => {
        // 重新授权成功，重试原请求
        return service(response.config)
      }).catch(error => {
        return Promise.reject(new Error(res.msg || '未授权'))
      })
    } else {
      // 其他业务错误 - 不在这里显示toast，让具体业务页面处理
      // 这样可以提供更准确的错误信息
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  error => {
    console.error('响应错误:', error)
    let message = '网络错误'

    if (error.response) {
      switch (error.response.status) {
        case 401:
          // HTTP 401,自动重新授权
          return handleTokenExpired().then(() => {
            // 重新授权成功，重试原请求
            return service(error.config)
          }).catch(err => {
            showToast({
              message: '未授权,请重新登录',
              type: 'fail'
            })
            return Promise.reject(err)
          })
        case 403:
          message = '拒绝访问'
          showToast({
            message: message,
            type: 'fail'
          })
          break
        case 404:
          message = '请求错误,未找到该资源'
          showToast({
            message: message,
            type: 'fail'
          })
          break
        case 500:
          // 500错误可能是业务异常，让业务页面处理
          if (error.response.data && error.response.data.msg) {
            message = error.response.data.msg
          } else {
            message = '服务器错误'
            showToast({
              message: message,
              type: 'fail'
            })
          }
          break
        default:
          message = error.response.data?.msg || '网络错误'
          if (!error.response.data?.msg) {
            showToast({
              message: message,
              type: 'fail'
            })
          }
      }
    } else if (error.request) {
      // 请求发出但没有收到响应
      message = '网络连接失败，请检查网络后重试'
      showToast({
        message: message,
        type: 'fail'
      })
    } else {
      // 其他错误
      message = error.message || '请求失败'
      showToast({
        message: message,
        type: 'fail'
      })
    }

    return Promise.reject(error)
  }
)

export default service

/**
 * 用于原生fetch的API请求，自动拼接baseUrl
 * 解决生产环境硬编码 /api/ 前缀导致404的问题
 * @param {string} url - 请求路径，如 '/h5/user/info'
 * @param {object} options - fetch配置项
 * @returns {Promise} fetch返回的Promise
 */
export function fetchApi(url, options = {}) {
  const baseUrl = process.env.VUE_APP_BASE_API || ''

  // 如果URL已经是完整地址，直接使用
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return fetch(url, options)
  }

  // 拼接baseUrl
  const fullUrl = baseUrl + (url.startsWith('/') ? url : '/' + url)
  return fetch(fullUrl, options)
}
