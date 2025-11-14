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
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'PingFang SC', 'Helvetica Neue', Helvetica, Arial, sans-serif;
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
}
</style>
