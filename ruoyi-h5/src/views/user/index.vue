<template>
  <div class="user-page">
    <!-- 顶部用户信息区 -->
    <div class="user-header">
      <div class="user-info">
        <van-image
          round
          width="60"
          height="60"
          :src="userAvatar"
          fit="cover"
        />
        <div class="user-details">
          <div class="user-name">{{ userInfo.name || '张丽丽' }}</div>
          <div class="user-phone">{{ userInfo.phone || '15612345678' }}</div>
        </div>
      </div>

      <!-- 快捷统计区 -->
      <div class="quick-stats">
        <div class="stat-item" @click="goToTodo">
          <div class="stat-icon-wrapper todo-icon">
            <van-icon name="star-o" size="26" />
            <van-badge
              v-if="todoCount > 0"
              :content="todoCount"
              class="stat-badge"
              :max="99"
            />
          </div>
          <div class="stat-label">待办事项</div>
        </div>

        <div class="stat-item" @click="goToElder">
          <div class="stat-icon-wrapper elder-icon">
            <van-icon name="friends-o" size="26" />
            <van-badge
              :content="elderCount"
              class="stat-badge"
              :max="99"
            />
          </div>
          <div class="stat-label">老人信息</div>
        </div>

        <div class="stat-item" @click="goToExpense">
          <div class="stat-icon-wrapper expense-icon">
            <van-icon name="credit-pay" size="26" />
          </div>
          <div class="stat-label">我的费用</div>
        </div>
      </div>
    </div>

    <!-- 我的订单 -->
    <div class="section-card">
      <div class="section-header">
        <span class="section-title">我的订单</span>
        <span class="section-more" @click="goToOrders">查看全部 <van-icon name="arrow" /></span>
      </div>
      <div class="order-status-list">
        <div class="status-item" @click="goToOrders('pending')">
          <van-icon name="pending-payment" size="28" color="#ff6b00" />
          <div class="status-label">待付款</div>
        </div>
        <div class="status-item" @click="goToOrders('paid')">
          <van-icon name="paid" size="28" color="#07c160" />
          <div class="status-label">已付款</div>
        </div>
        <div class="status-item" @click="goToOrders('cancelled')">
          <van-icon name="close" size="28" color="#999" />
          <div class="status-label">已取消</div>
        </div>
        <div class="status-item" @click="goToOrders('refund')">
          <van-icon name="refund-o" size="28" color="#1989fa" />
          <div class="status-label">退款</div>
        </div>
      </div>
    </div>

    <!-- 常用工具 -->
    <div class="section-card">
      <div class="section-title">常用工具</div>
      <div class="tool-list">
        <div class="tool-item" @click="goToAppointment">
          <van-icon name="orders-o" size="28" color="#667eea" />
          <div class="tool-label">我的预约</div>
        </div>
        <div class="tool-item" @click="goToCollection">
          <van-icon name="star-o" size="28" color="#667eea" />
          <div class="tool-label">我的收藏</div>
        </div>
        <div class="tool-item" @click="goToEvaluation">
          <van-icon name="comment-o" size="28" color="#667eea" />
          <div class="tool-label">我的评价</div>
        </div>
        <div class="tool-item" @click="goToComplaint">
          <van-icon name="warning-o" size="28" color="#667eea" />
          <div class="tool-label">我要投诉</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

// 模拟用户数据
const userInfo = ref({
  name: '张丽丽',
  phone: '15612345678',
  avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
})

const userAvatar = ref('https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg')

// 统计数据
const todoCount = ref(3)
const elderCount = ref(0)

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
</script>

<style scoped>
.user-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60px;
}

/* 顶部用户信息区 */
.user-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px 16px 24px;
  color: #fff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 4px;
}

.user-phone {
  font-size: 14px;
  opacity: 0.9;
}

/* 快捷统计区 */
.quick-stats {
  display: flex;
  justify-content: space-around;
  padding: 0 20px;
  gap: 16px;
}

.stat-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: transform 0.2s ease;
  flex: 1;
}

.stat-item:active {
  transform: scale(0.95);
}

/* 图标容器 */
.stat-icon-wrapper {
  position: relative;
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.stat-icon-wrapper:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

/* 不同类型图标的背景色 */
.todo-icon {
  background: linear-gradient(135deg, rgba(255, 193, 7, 0.3) 0%, rgba(255, 152, 0, 0.3) 100%);
}

.elder-icon {
  background: linear-gradient(135deg, rgba(76, 175, 80, 0.3) 0%, rgba(56, 142, 60, 0.3) 100%);
}

.expense-icon {
  background: linear-gradient(135deg, rgba(33, 150, 243, 0.3) 0%, rgba(25, 118, 210, 0.3) 100%);
}

.stat-label {
  font-size: 13px;
  opacity: 0.95;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 徽章样式 */
.stat-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  box-shadow: 0 2px 8px rgba(238, 10, 36, 0.3);
  animation: badge-pulse 2s ease-in-out infinite;
}

/* 徽章脉冲动画 */
@keyframes badge-pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

/* 响应式优化 */
@media (max-width: 360px) {
  .quick-stats {
    gap: 12px;
    padding: 0 12px;
  }

  .stat-icon-wrapper {
    width: 48px;
    height: 48px;
  }

  .stat-icon-wrapper :deep(.van-icon) {
    font-size: 22px;
  }

  .stat-label {
    font-size: 12px;
  }
}

/* 区块卡片 */
.section-card {
  background: #fff;
  margin: 12px;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.section-more {
  font-size: 13px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 2px;
  cursor: pointer;
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
}

.status-label {
  font-size: 13px;
  color: #666;
}

/* 工具列表 */
.tool-list {
  display: flex;
  justify-content: space-around;
  margin-top: 16px;
}

.tool-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.tool-label {
  font-size: 13px;
  color: #666;
}
</style>
