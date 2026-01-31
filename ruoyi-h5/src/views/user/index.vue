<template>
  <div class="user-page">
    <!-- 顶部用户信息区 -->
    <div class="user-header">
      <div class="header-decoration"></div>
      <div class="user-info" @click="goToProfile">
        <van-image
          round
          width="64"
          height="64"
          :src="userAvatar"
          fit="cover"
          class="user-avatar"
        />
        <div class="user-details">
          <div class="user-name">{{ userInfo.name || '用户' }}</div>
          <div class="user-phone">{{ maskedPhone }}</div>
        </div>
        <van-icon name="arrow" class="user-arrow" />
      </div>
    </div>

    <!-- 快捷菜单列表 -->
    <div class="menu-list-card">
      <div class="menu-item" @click="goToTodo">
        <div class="menu-left">
          <div class="menu-icon" style="--icon-color: #FFC107;">
            <van-icon name="star-o" size="20" color="#fff" />
          </div>
          <span class="menu-text">待办事项</span>
        </div>
        <div class="menu-right">
          <span class="menu-count">{{ todoCount > 0 ? todoCount : 0 }}</span>
          <van-icon name="arrow" class="menu-arrow" />
        </div>
      </div>

      <div class="menu-item" @click="goToElder">
        <div class="menu-left">
          <div class="menu-icon" style="--icon-color: #FF6B6B;">
            <van-icon name="friends-o" size="20" color="#fff" />
          </div>
          <span class="menu-text">老人信息</span>
        </div>
        <div class="menu-right">
          <span class="menu-count">{{ elderCount }}人</span>
          <van-icon name="arrow" class="menu-arrow" />
        </div>
      </div>

      <div class="menu-item" @click="goToExpense">
        <div class="menu-left">
          <div class="menu-icon" style="--icon-color: #00BCD4;">
            <van-icon name="balance-list-o" size="20" color="#fff" />
          </div>
          <span class="menu-text">我的费用</span>
        </div>
        <div class="menu-right">
          <van-icon name="arrow" class="menu-arrow" />
        </div>
      </div>
    </div>

    <!-- 我的订单 -->
    <div class="section-card">
      <div class="section-header">
        <span class="section-title">我的订单</span>
        <span class="section-more" @click="goToOrders">
          <span class="more-text">更多</span>
          <van-icon name="arrow" class="more-arrow" />
        </span>
      </div>
      <div class="order-status-list">
        <div class="status-item" @click="goToOrders('pending')">
          <div class="status-icon-wrapper" style="--icon-color: #ff6b00;">
            <van-icon name="pending-payment" size="20" color="#fff" />
          </div>
          <div class="status-label">待付款</div>
        </div>
        <div class="status-item" @click="goToOrders('paid')">
          <div class="status-icon-wrapper" style="--icon-color: #07c160;">
            <van-icon name="paid" size="20" color="#fff" />
          </div>
          <div class="status-label">已付款</div>
        </div>
        <div class="status-item" @click="goToOrders('cancelled')">
          <div class="status-icon-wrapper" style="--icon-color: #999999;">
            <van-icon name="close" size="20" color="#fff" />
          </div>
          <div class="status-label">已取消</div>
        </div>
        <div class="status-item" @click="goToOrders('refund')">
          <div class="status-icon-wrapper" style="--icon-color: #1989fa;">
            <van-icon name="refund-o" size="20" color="#fff" />
          </div>
          <div class="status-label">退款</div>
        </div>
      </div>
    </div>

    <!-- 常用工具 -->
    <div class="section-card">
      <div class="section-header">
        <span class="section-title">常用工具</span>
      </div>
      <div class="tool-list">
        <div class="tool-item" @click="goToAppointment">
          <div class="tool-icon-wrapper" style="--icon-color: #5B8FF9;">
            <van-icon name="orders-o" size="22" color="#fff" />
          </div>
          <div class="tool-label">我的预约</div>
        </div>
        <div class="tool-item" @click="goToCollection">
          <div class="tool-icon-wrapper" style="--icon-color: #FFC107;">
            <van-icon name="star-o" size="22" color="#fff" />
          </div>
          <div class="tool-label">我的收藏</div>
        </div>
        <div class="tool-item" @click="goToEvaluation">
          <div class="tool-icon-wrapper" style="--icon-color: #FF6B6B;">
            <van-icon name="comment-o" size="22" color="#fff" />
          </div>
          <div class="tool-label">我的评价</div>
        </div>
        <div class="tool-item" @click="goToComplaint">
          <div class="tool-icon-wrapper" style="--icon-color: #764ba2;">
            <van-icon name="warning-o" size="22" color="#fff" />
          </div>
          <div class="tool-label">我要投诉</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { getTodoCount } from '@/api/todo'
import { getElderList } from '@/api/expense'

const router = useRouter()
const userStore = useUserStore()

// 从 store 获取用户信息
const userInfo = computed(() => ({
  name: userStore.nickName || userStore.userName || '用户',
  phone: userStore.phonenumber || '',
  avatar: userStore.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
}))

const userAvatar = computed(() => userStore.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg')

// 手机号脱敏
const maskedPhone = computed(() => {
  const phone = userInfo.value.phone
  if (!phone) return '未绑定手机'
  if (phone.length === 11) {
    return phone.substring(0, 3) + '****' + phone.substring(7)
  }
  return phone
})

// 统计数据 - 直接从API获取，不存储
const todoCount = ref(0)
const elderCount = ref(0)

// 加载待办数量
const loadTodoCount = async () => {
  try {
    const response = await getTodoCount()
    if (response.code === 200 && response.data) {
      todoCount.value = response.data.totalCount || 0
    }
  } catch (error) {
    console.error('获取待办数量失败', error)
  }
}

// 加载老人数量（直接从数据库获取）
const loadElderCount = async () => {
  try {
    const response = await getElderList()
    if (response.code === 200 && response.data) {
      elderCount.value = response.data?.length || 0
    }
  } catch (error) {
    console.error('获取老人数量失败', error)
  }
}

// 跳转个人资料
const goToProfile = () => {
  router.push('/user/profile')
}

// 跳转待办事项
const goToTodo = () => {
  router.push('/user/todo')
}

// 跳转老人信息
const goToElder = () => {
  router.push('/user/elder')
}

// 跳转我的费用
const goToExpense = () => {
  router.push('/user/expense')
}

// 跳转订单
const goToOrders = (status = '') => {
  if (status) {
    router.push(`/user/order?status=${status}`)
  } else {
    router.push('/user/order')
  }
}

// 跳转我的预约
const goToAppointment = () => {
  router.push('/user/appointment')
}

// 跳转我的收藏
const goToCollection = () => {
  router.push('/user/collection')
}

// 跳转我的评价
const goToEvaluation = () => {
  router.push('/user/evaluation')
}

// 跳转我要投诉
const goToComplaint = () => {
  router.push('/user/complaint')
}

// 页面加载时获取待办数量和老人数量
onMounted(async () => {
  loadTodoCount()
  await loadElderCount()
})
</script>

<style scoped>
.user-page {
  min-height: 100vh;
  background-color: #f5f6fc;
  padding-bottom: 20px;
}

/* 顶部用户信息区 */
.user-header {
  position: relative;
  background: linear-gradient(180deg, #0f73ff 0%, #4fc7ff 100%);
  padding: 50px 24px 32px 16px;
  color: #fff;
  overflow: hidden;
}

.header-decoration {
  position: absolute;
  top: -50px;
  right: -50px;
  width: 150px;
  height: 150px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.user-info {
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  z-index: 1;
  transition: opacity 0.2s ease;
}

.user-info:active {
  opacity: 0.8;
}

.user-avatar {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  flex-shrink: 0;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 6px;
  font-family: 'PingFang SC', '苹方-简', sans-serif;
}

.user-phone {
  font-size: 14px;
  opacity: 0.9;
}

.user-arrow {
  font-size: 18px;
  opacity: 0.8;
}

/* 快捷菜单列表卡片 */
.menu-list-card {
  background: #fff;
  margin: 12px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.2s ease;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:active {
  background: #f8f8f8;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.menu-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--icon-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.menu-text {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.menu-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.menu-count {
  font-size: 13px;
  color: #999;
}

.menu-arrow {
  font-size: 14px;
  color: #c8c9cc;
}

/* 区块卡片 */
.section-card {
  background: #fff;
  margin: 12px;
  padding: 16px 12px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-left: 4px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: #1a1a1a;
  position: relative;
  padding-left: 10px;
  font-family: 'PingFang SC', '苹方-简', sans-serif;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 14px;
  background: linear-gradient(180deg, #0f73ff 0%, #4fc7ff 100%);
  border-radius: 2px;
}

.section-more {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 0;
}

.section-more .more-text {
  font-size: 13px;
  color: #999;
  margin-right: 2px;
}

.section-more .more-arrow {
  font-size: 12px;
  color: #999;
}

/* 订单状态列表 */
.order-status-list {
  display: flex;
  justify-content: space-around;
}

.status-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.status-item:active {
  transform: scale(0.95);
}

.status-icon-wrapper {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--icon-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.status-label {
  font-size: 12px;
  color: #333;
}

/* 工具列表 */
.tool-list {
  display: flex;
  justify-content: space-around;
}

.tool-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px;
  transition: transform 0.2s ease;
}

.tool-item:active {
  transform: scale(0.95);
}

.tool-icon-wrapper {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--icon-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tool-label {
  font-size: 12px;
  color: #333;
}

/* 响应式优化 */
@media (max-width: 360px) {
  .user-header {
    padding: 35px 12px 16px;
  }

  .menu-item {
    padding: 14px 12px;
  }
}
</style>
