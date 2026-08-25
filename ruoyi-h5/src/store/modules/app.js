import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    // 当前激活的TabBar标签id
    activeTabBar: 'home',

    // 加载状态
    loading: false,

    // 网络状态
    online: navigator.onLine
  }),

  getters: {
    isLoading: (state) => state.loading,
    isOnline: (state) => state.online
  },

  actions: {
    // 设置TabBar标签id
    setActiveTabBar(tabId) {
      this.activeTabBar = tabId
    },

    // 设置加载状态
    setLoading(loading) {
      this.loading = loading
    },

    // 设置网络状态
    setOnline(online) {
      this.online = online
    }
  }
})
