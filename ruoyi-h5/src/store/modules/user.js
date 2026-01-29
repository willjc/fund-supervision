import { defineStore } from 'pinia'
import { getUserInfo, setUserInfo, clearAuth, getToken } from '@/utils/auth'
import { fetchApi } from '@/utils/request'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: getUserInfo() || null,
    token: null,
    elders: [], // 关联的老人列表
    currentElder: null // 当前选择的老人
  }),

  getters: {
    // 是否已登录
    isLoggedIn: (state) => !!state.token || !!state.userInfo,

    // 用户名
    userName: (state) => state.userInfo?.userName || '',

    // 用户昵称
    nickName: (state) => state.userInfo?.nickName || '',

    // 真实姓名
    realName: (state) => state.userInfo?.realName || '',

    // 用户头像
    avatar: (state) => state.userInfo?.avatar || '',

    // 用户手机号
    phonenumber: (state) => state.userInfo?.phonenumber || '',

    // 是否有关联老人
    hasElders: (state) => state.elders && state.elders.length > 0,

    // 当前老人姓名
    currentElderName: (state) => state.currentElder?.elderName || ''
  },

  actions: {
    // 设置用户信息
    setUser(userInfo) {
      this.userInfo = userInfo
      setUserInfo(userInfo)
    },

    // 设置Token
    setToken(token) {
      this.token = token
    },

    // 设置关联老人列表
    setElders(elders) {
      this.elders = elders || []
      // 如果有老人且当前老人未设置,自动设置第一个老人为当前老人
      if (this.elders.length > 0 && !this.currentElder) {
        this.currentElder = this.elders[0]
      }
    },

    // 设置当前老人
    setCurrentElder(elder) {
      this.currentElder = elder
    },

    // 退出登录
    logout() {
      this.userInfo = null
      this.token = null
      this.elders = []
      this.currentElder = null
      clearAuth()
    },

    // 更新用户信息
    updateUserInfo(info) {
      this.userInfo = { ...this.userInfo, ...info }
      setUserInfo(this.userInfo)
    },

    // 获取老人列表
    async fetchElders() {
      try {
        const token = getToken()
        const response = await fetchApi('/h5/user/getEldersByUserId', {
          method: 'GET',
          headers: {
            'Authorization': token ? `Bearer ${token}` : ''
          }
        })

        const result = await response.json()
        if (result.code === 200 && result.data) {
          this.setElders(result.data)
          return result.data
        } else {
          throw new Error(result.msg || '获取老人列表失败')
        }
      } catch (error) {
        console.error('获取老人列表失败:', error)
        return []
      }
    }
  }
})
