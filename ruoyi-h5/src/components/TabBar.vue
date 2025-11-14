<template>
  <van-tabbar v-model="active" @change="onChange">
    <van-tabbar-item name="home" icon="wap-home-o">首页</van-tabbar-item>
    <van-tabbar-item name="institution" icon="shop-o">机构</van-tabbar-item>
    <van-tabbar-item name="order" icon="orders-o">订单</van-tabbar-item>
    <van-tabbar-item name="user" icon="user-o">我的</van-tabbar-item>
  </van-tabbar>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAppStore } from '@/store/modules/app'

const router = useRouter()
const route = useRoute()
const appStore = useAppStore()

// 当前激活的标签
const active = ref('home')

// 监听路由变化
watch(() => route.name, (newName) => {
  if (newName) {
    active.value = newName.toLowerCase()
  }
}, { immediate: true })

// 切换标签
const onChange = (name) => {
  router.push({ name: name.charAt(0).toUpperCase() + name.slice(1) })
  appStore.setActiveTabBar(name)
}
</script>

<style scoped>
/* 可以自定义TabBar样式 */
</style>
