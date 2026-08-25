<template>
  <van-tabbar
    v-model="active"
    class="app-tabbar"
    safe-area-inset-bottom
    @change="onChange"
  >
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
.app-tabbar {
  z-index: 100;
  z-index: var(--h5-z-tabbar);
  border-top: 1px solid #edf1f5;
  border-top: 1px solid var(--h5-color-divider);
  box-shadow: 0 -4px 18px rgba(31, 55, 82, 0.08);
  box-shadow: var(--h5-shadow-top-sm);
}

.app-tabbar :deep(.van-tabbar-item) {
  position: relative;
  min-height: 48px;
  color: #536273;
  color: var(--h5-color-text-secondary);
  font-size: 13px;
  font-size: var(--h5-font-size-sm);
  line-height: var(--h5-line-height-tight);
  transition:
    color var(--h5-motion-fast) var(--h5-ease-standard),
    background-color var(--h5-motion-fast) var(--h5-ease-standard);
}

.app-tabbar :deep(.van-tabbar-item)::before {
  position: absolute;
  top: 0;
  left: 50%;
  width: 24px;
  height: 3px;
  background: transparent;
  border-radius: 0 0 var(--h5-radius-pill) var(--h5-radius-pill);
  transform: translateX(-50%);
  transition: background-color var(--h5-motion-fast) var(--h5-ease-standard);
  content: '';
}

.app-tabbar :deep(.van-tabbar-item__icon) {
  margin-bottom: var(--h5-space-1);
  font-size: 23px;
  transition: transform var(--h5-motion-fast) var(--h5-ease-emphasized);
}

.app-tabbar :deep(.van-tabbar-item__text) {
  font-weight: var(--h5-font-weight-medium);
}

.app-tabbar :deep(.van-tabbar-item--active) {
  color: var(--h5-color-primary);
  background: var(--h5-color-surface);
}

.app-tabbar :deep(.van-tabbar-item--active)::before {
  background: var(--h5-color-primary);
}

.app-tabbar :deep(.van-tabbar-item--active .van-tabbar-item__icon) {
  transform: translateY(-1px);
}

.app-tabbar :deep(.van-tabbar-item--active .van-tabbar-item__text) {
  font-weight: var(--h5-font-weight-semibold);
}

.app-tabbar :deep(.van-tabbar-item:active) {
  background: var(--h5-color-primary-soft);
}

.app-tabbar :deep(.van-tabbar-item:focus-visible) {
  outline: 2px solid #1f65bd;
  outline: 2px solid var(--h5-color-primary);
  outline-offset: -3px;
  border-radius: 8px;
  border-radius: var(--h5-radius-sm);
}
</style>
