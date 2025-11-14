<template>
  <div class="user-page">
    <!-- 用户信息卡片 -->
    <div class="user-info-card">
      <div v-if="isLoggedIn" class="logged-in">
        <van-image
          round
          width="60"
          height="60"
          :src="userAvatar"
          fit="cover"
        />
        <div class="user-details">
          <div class="user-name">{{ userInfo.userName || '用户' }}</div>
          <div class="user-phone">{{ hidePhone(userInfo.phone) }}</div>
        </div>
      </div>
      <div v-else class="not-logged-in" @click="goToLogin">
        <van-image
          round
          width="60"
          height="60"
          src="https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg"
          fit="cover"
        />
        <div class="login-text">点击登录</div>
      </div>
    </div>

    <!-- 快捷功能 -->
    <van-cell-group title="快捷功能">
      <van-cell
        title="押金管理"
        icon="balance-o"
        is-link
        @click="goToDeposit"
      />
      <van-cell
        title="我的订单"
        icon="orders-o"
        is-link
        @click="goToOrders"
      />
    </van-cell-group>

    <!-- 常用工具 -->
    <van-cell-group title="常用工具">
      <van-cell
        title="设置"
        icon="setting-o"
        is-link
        @click="goToSettings"
      />
      <van-cell
        title="帮助与反馈"
        icon="question-o"
        is-link
        @click="goToHelp"
      />
      <van-cell
        title="关于我们"
        icon="info-o"
        is-link
        @click="goToAbout"
      />
    </van-cell-group>

    <!-- 退出登录 -->
    <div v-if="isLoggedIn" class="logout-btn">
      <van-button block type="danger" plain @click="handleLogout">
        退出登录
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { showDialog, showToast } from 'vant'
import { useUserStore } from '@/store/modules/user'
import { hidePhone } from '@/utils/format'

const router = useRouter()
const userStore = useUserStore()

// 计算属性
const isLoggedIn = computed(() => userStore.isLoggedIn)
const userInfo = computed(() => userStore.userInfo || {})
const userAvatar = computed(() => userInfo.value.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg')

// 跳转登录
const goToLogin = () => {
  router.push('/login')
}

// 跳转押金管理
const goToDeposit = () => {
  if (!isLoggedIn.value) {
    showToast('请先登录')
    router.push('/login')
    return
  }
  router.push('/deposit')
}

// 跳转订单
const goToOrders = () => {
  if (!isLoggedIn.value) {
    showToast('请先登录')
    router.push('/login')
    return
  }
  showToast('订单功能开发中')
}

// 跳转设置
const goToSettings = () => {
  showToast('设置功能开发中')
}

// 跳转帮助
const goToHelp = () => {
  showToast('帮助功能开发中')
}

// 跳转关于
const goToAbout = () => {
  showToast('关于我们功能开发中')
}

// 退出登录
const handleLogout = () => {
  showDialog({
    title: '提示',
    message: '确认退出登录?',
    showCancelButton: true
  }).then(() => {
    userStore.logout()
    showToast('已退出登录')
  }).catch(() => {
    // 取消
  })
}
</script>

<style scoped>
.user-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60px;
}

.user-info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
  color: #fff;
}

.logged-in, .not-logged-in {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 8px;
}

.user-phone {
  font-size: 14px;
  opacity: 0.9;
}

.login-text {
  font-size: 18px;
  font-weight: bold;
}

.logout-btn {
  margin: 20px 16px;
}
</style>
