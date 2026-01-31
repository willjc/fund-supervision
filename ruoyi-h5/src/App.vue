<template>
  <div id="app">
    <!-- 全屏登录Loading遮罩 -->
    <div v-if="isLoggingIn" class="login-loading-overlay">
      <div class="login-loading-content">
        <van-loading size="36px" color="#1281ff">正在登录...</van-loading>
        <p class="login-loading-text">正在通过郑好办授权登录</p>
        <p class="login-loading-hint">请稍候，约需3-10秒</p>
      </div>
    </div>

    <router-view v-slot="{ Component }">
      <keep-alive :include="['Home', 'Institution', 'User']">
        <component :is="Component" />
      </keep-alive>
    </router-view>
    <TabBar v-if="showTabBar && !isLoggingIn" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import TabBar from '@/components/TabBar.vue'

const route = useRoute()

// 登录状态 - 提供给路由守卫使用
const isLoggingIn = ref(false)

// 暴露方法给路由守卫调用
window.setAppLoggingIn = (status) => {
  isLoggingIn.value = status
}

// 需要显示TabBar的页面
const tabBarPages = ['Home', 'Institution', 'Order', 'User']

// 判断是否显示TabBar
const showTabBar = computed(() => {
  return tabBarPages.includes(route.name)
})
</script>

<style scoped>
/* 全屏登录Loading遮罩 */
.login-loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f6fc 0%, #e8ecf7 100%);
  z-index: 9999;
}

.login-loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(18, 129, 255, 0.15);
  min-width: 280px;
}

.login-loading-text {
  margin-top: 24px;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.login-loading-hint {
  margin-top: 8px;
  font-size: 13px;
  color: #999;
}
</style>

<style>
/* 苹方字体声明 */
@font-face {
  font-family: "PingFang SC";
  src: url('@/assets/fonts/PingFang Regular_0.ttf') format('truetype');
  font-weight: normal;
  font-style: normal;
}

@font-face {
  font-family: "PingFang SC";
  src: url('@/assets/fonts/PingFang Light_0.ttf') format('truetype');
  font-weight: 300;
  font-style: normal;
}

@font-face {
  font-family: "PingFang SC";
  src: url('@/assets/fonts/PingFang Medium_0.ttf') format('truetype');
  font-weight: 500;
  font-style: normal;
}

@font-face {
  font-family: "PingFang SC";
  src: url('@/assets/fonts/PingFang Bold_0.ttf') format('truetype');
  font-weight: 700;
  font-style: normal;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "PingFang SC", "苹方-简", sans-serif;
}

#app {
  font-family: "PingFang SC", "苹方-简", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #333;
  background-color: #f5f5f5;
  min-height: 100vh;
}

/* 全局样式 */
body {
  margin: 0;
  padding: 0;
  font-family: "PingFang SC", "苹方-简", sans-serif;
}
</style>
