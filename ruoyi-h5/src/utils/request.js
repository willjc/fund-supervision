import axios from 'axios'
import { showToast, showDialog } from 'vant'
import { getToken, removeToken } from './auth'

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

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data

    // 如果返回的状态码为200,说明接口请求成功
    if (res.code === 200) {
      return res
    } else if (res.code === 401) {
      // 未授权,清除token并跳转登录
      showDialog({
        title: '提示',
        message: '登录状态已过期,请重新登录'
      }).then(() => {
        removeToken()
        window.location.href = '/login'
      })
      return Promise.reject(new Error(res.msg || '未授权'))
    } else {
      // 其他错误
      showToast({
        message: res.msg || '请求失败',
        type: 'fail'
      })
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  error => {
    console.error('响应错误:', error)
    let message = '网络错误'

    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '未授权,请重新登录'
          removeToken()
          window.location.href = '/login'
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求错误,未找到该资源'
          break
        case 500:
          message = '服务器错误'
          break
        default:
          message = error.response.data.msg || '网络错误'
      }
    }

    showToast({
      message: message,
      type: 'fail'
    })

    return Promise.reject(error)
  }
)

export default service
