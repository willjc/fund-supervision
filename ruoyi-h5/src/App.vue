<template>
  <div id="app">
    <router-view v-slot="{ Component }">
      <keep-alive :include="['Home', 'Institution', 'User']">
        <component :is="Component" />
      </keep-alive>
    </router-view>
    <TabBar v-if="showTabBar" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import TabBar from '@/components/TabBar.vue'

const route = useRoute()

// 需要显示TabBar的页面
const tabBarPages = ['Home', 'Institution', 'Order', 'User']

// 判断是否显示TabBar
const showTabBar = computed(() => {
  return tabBarPages.includes(route.name)
})
</script>

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
