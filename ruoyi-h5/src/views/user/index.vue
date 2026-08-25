<template>
  <div class="user-page h5-page h5-page--constrained">
    <header class="user-header">
      <div class="user-header__topline">
        <div>
          <p>养老服务个人中心</p>
          <h1>我的</h1>
        </div>
        <span class="verified-badge"><van-icon name="shield-o" /> 安心服务</span>
      </div>

      <button type="button" class="user-profile" aria-label="查看个人资料" @click="goToProfile">
        <van-image round width="68" height="68" :src="userAvatar" fit="cover" class="user-avatar" />
        <span class="user-details">
          <strong>{{ userInfo.name || '用户' }}</strong>
          <span>{{ maskedPhone }}</span>
          <small>查看并完善个人资料</small>
        </span>
        <span class="user-profile__arrow" aria-hidden="true"><van-icon name="arrow" /></span>
      </button>
    </header>

    <main class="user-content">
      <section class="shortcut-card h5-card" aria-labelledby="shortcut-title">
        <h2 id="shortcut-title" class="h5-sr-only">常用快捷入口</h2>
        <button type="button" class="shortcut-item shortcut-item--todo" @click="goToTodo">
          <span class="shortcut-icon" aria-hidden="true"><van-icon name="todo-list-o" /></span>
          <span class="shortcut-copy">
            <strong>待办事项</strong>
            <small>查看审核与提醒</small>
          </span>
          <span class="shortcut-value">{{ todoCount > 0 ? todoCount : 0 }}<small>项</small></span>
        </button>

        <button type="button" class="shortcut-item shortcut-item--elder" @click="goToElder">
          <span class="shortcut-icon" aria-hidden="true"><van-icon name="friends-o" /></span>
          <span class="shortcut-copy">
            <strong>老人信息</strong>
            <small>维护入住人档案</small>
          </span>
          <span class="shortcut-value">{{ elderCount }}<small>人</small></span>
        </button>

        <button type="button" class="shortcut-item shortcut-item--expense" @click="goToExpense">
          <span class="shortcut-icon" aria-hidden="true"><van-icon name="balance-list-o" /></span>
          <span class="shortcut-copy">
            <strong>我的费用</strong>
            <small>查询余额与明细</small>
          </span>
          <span class="shortcut-enter">查看 <van-icon name="arrow" /></span>
        </button>
      </section>

      <section class="section-card h5-card" aria-labelledby="order-section-title">
        <header class="section-header">
          <div>
            <h2 id="order-section-title">我的订单</h2>
            <p>按状态快速查看服务订单</p>
          </div>
          <button type="button" class="section-more" @click="goToOrders">
            全部订单 <van-icon name="arrow" />
          </button>
        </header>

        <div class="order-status-list">
          <button type="button" class="status-item status-item--pending" @click="goToOrders('pending')">
            <span class="status-icon-wrapper" aria-hidden="true"><van-icon name="pending-payment" /></span>
            <span class="status-label">待付款</span>
          </button>
          <button type="button" class="status-item status-item--paid" @click="goToOrders('paid')">
            <span class="status-icon-wrapper" aria-hidden="true"><van-icon name="paid" /></span>
            <span class="status-label">已付款</span>
          </button>
          <button type="button" class="status-item status-item--cancelled" @click="goToOrders('cancelled')">
            <span class="status-icon-wrapper" aria-hidden="true"><van-icon name="close" /></span>
            <span class="status-label">已取消</span>
          </button>
          <button type="button" class="status-item status-item--refund" @click="goToOrders('refund')">
            <span class="status-icon-wrapper" aria-hidden="true"><van-icon name="refund-o" /></span>
            <span class="status-label">退款</span>
          </button>
        </div>
      </section>

      <section class="section-card h5-card" aria-labelledby="tool-section-title">
        <header class="section-header">
          <div>
            <h2 id="tool-section-title">常用工具</h2>
            <p>预约、收藏与服务反馈</p>
          </div>
        </header>

        <div class="tool-list">
          <button type="button" class="tool-item tool-item--appointment" @click="goToAppointment">
            <span class="tool-icon-wrapper" aria-hidden="true"><van-icon name="calendar-o" /></span>
            <span class="tool-label">我的预约</span>
          </button>
          <button type="button" class="tool-item tool-item--collection" @click="goToCollection">
            <span class="tool-icon-wrapper" aria-hidden="true"><van-icon name="star-o" /></span>
            <span class="tool-label">我的收藏</span>
          </button>
          <button type="button" class="tool-item tool-item--evaluation" @click="goToEvaluation">
            <span class="tool-icon-wrapper" aria-hidden="true"><van-icon name="comment-o" /></span>
            <span class="tool-label">我的评价</span>
          </button>
          <button type="button" class="tool-item tool-item--complaint" @click="goToComplaint">
            <span class="tool-icon-wrapper" aria-hidden="true"><van-icon name="warning-o" /></span>
            <span class="tool-label">我要投诉</span>
          </button>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { getTodoCount } from '@/api/todo'
import { getElderList } from '@/api/expense'
import { getImageUrl } from '@/utils/image'
import userAvatarPlaceholder from '@/assets/images/user-avatar-placeholder.svg'

const router = useRouter()
const userStore = useUserStore()

// 从 store 获取用户信息
const userInfo = computed(() => ({
  name: userStore.nickName || userStore.userName || '用户',
  phone: userStore.phonenumber || ''
}))

const userAvatar = computed(() => userStore.avatar ? getImageUrl(userStore.avatar) : userAvatarPlaceholder)

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
      todoCount.value = response.data.pendingCount || 0
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
  router.push({
    name: 'Order',
    query: status ? { status } : {}
  })
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
  padding-bottom: var(--h5-space-6);
}

.user-header {
  position: relative;
  overflow: hidden;
  padding: calc(var(--h5-safe-area-top) + var(--h5-space-5)) var(--h5-page-padding) var(--h5-space-6);
  color: var(--h5-color-text-inverse);
  background: linear-gradient(145deg, var(--h5-color-primary-800), var(--h5-color-primary-600));
}

.user-header::before,
.user-header::after {
  position: absolute;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 50%;
  content: '';
}

.user-header::before {
  top: -56px;
  right: -36px;
  width: 158px;
  height: 158px;
}

.user-header::after {
  right: 54px;
  bottom: -80px;
  width: 124px;
  height: 124px;
}

.user-header__topline {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-3);
}

.user-header__topline p {
  color: var(--h5-color-text-inverse);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
}

.user-header__topline h1 {
  margin-top: 2px;
  font-size: var(--h5-font-size-3xl);
  font-weight: var(--h5-font-weight-bold);
  line-height: var(--h5-line-height-tight);
}

.verified-badge {
  display: inline-flex;
  min-height: 30px;
  align-items: center;
  gap: var(--h5-space-1);
  padding: var(--h5-space-1) var(--h5-space-2);
  color: var(--h5-color-text-inverse);
  font-size: var(--h5-font-size-sm);
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: var(--h5-radius-pill);
}

.user-profile {
  position: relative;
  z-index: 1;
  display: flex;
  width: 100%;
  min-height: 96px;
  align-items: center;
  gap: var(--h5-space-3);
  margin-top: var(--h5-space-5);
  padding: var(--h5-space-3);
  color: var(--h5-color-text-inverse);
  text-align: left;
  background: rgba(255, 255, 255, 0.13);
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: var(--h5-radius-lg);
  box-shadow: 0 8px 24px rgba(17, 65, 118, 0.16);
  cursor: pointer;
  backdrop-filter: blur(8px);
}

.user-profile:focus-visible {
  outline: 2px solid var(--h5-color-text-inverse);
  outline-offset: 2px;
}

.user-avatar {
  flex: 0 0 auto;
  overflow: hidden;
  background: var(--h5-color-primary-100);
  border: 3px solid rgba(255, 255, 255, 0.86);
  box-shadow: 0 4px 14px rgba(17, 65, 118, 0.22);
}

.user-details {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
}

.user-details strong {
  overflow: hidden;
  font-size: var(--h5-font-size-xl);
  font-weight: var(--h5-font-weight-bold);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-details > span {
  margin-top: 2px;
  color: var(--h5-color-text-inverse);
  font-size: var(--h5-font-size-md);
}

.user-details small {
  margin-top: var(--h5-space-1);
  color: var(--h5-color-text-inverse);
  font-size: var(--h5-font-size-sm);
}

.user-profile__arrow {
  display: inline-flex;
  flex: 0 0 32px;
  width: 32px;
  height: 32px;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.14);
  border-radius: 50%;
}

.user-content {
  display: grid;
  gap: var(--h5-space-3);
  padding: var(--h5-space-3) var(--h5-page-padding);
}

.shortcut-card {
  margin-top: calc(var(--h5-space-6) * -1);
}

.shortcut-item {
  display: flex;
  width: 100%;
  min-height: 74px;
  align-items: center;
  gap: var(--h5-space-3);
  padding: var(--h5-space-3) var(--h5-space-4);
  color: inherit;
  text-align: left;
  background: var(--h5-color-surface);
  border: 0;
  border-bottom: 1px solid var(--h5-color-divider);
  cursor: pointer;
}

.shortcut-item:last-child {
  border-bottom: 0;
}

.shortcut-item:active,
.status-item:active,
.tool-item:active,
.section-more:active {
  background: var(--h5-color-primary-soft);
}

.shortcut-item:focus-visible,
.status-item:focus-visible,
.tool-item:focus-visible,
.section-more:focus-visible {
  outline: none;
  box-shadow: inset var(--h5-shadow-focus);
}

.shortcut-icon {
  display: inline-flex;
  flex: 0 0 42px;
  width: 42px;
  height: 42px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-primary);
  font-size: 22px;
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-md);
}

.shortcut-item--todo .shortcut-icon {
  color: var(--h5-color-pending);
  background: var(--h5-color-pending-soft);
}

.shortcut-item--elder .shortcut-icon {
  color: var(--h5-color-success);
  background: var(--h5-color-success-soft);
}

.shortcut-copy {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 2px;
}

.shortcut-copy strong {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
}

.shortcut-copy small {
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.shortcut-value {
  flex: 0 0 auto;
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-xl);
  font-weight: var(--h5-font-weight-bold);
}

.shortcut-value small {
  margin-left: 2px;
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-regular);
}

.shortcut-enter {
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  gap: 2px;
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
}

.section-card {
  padding: var(--h5-space-4);
}

.section-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-3);
  margin-bottom: var(--h5-space-4);
}

.section-header h2 {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
}

.section-header p {
  margin-top: 2px;
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.section-more {
  display: inline-flex;
  min-height: 36px;
  align-items: center;
  gap: 2px;
  padding: var(--h5-space-1) var(--h5-space-2);
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  background: transparent;
  border: 0;
  border-radius: var(--h5-radius-sm);
  cursor: pointer;
}

.order-status-list,
.tool-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--h5-space-2);
}

.status-item,
.tool-item {
  display: flex;
  min-width: 0;
  min-height: 88px;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: var(--h5-space-2);
  color: inherit;
  background: var(--h5-color-surface-subtle);
  border: 1px solid var(--h5-color-divider);
  border-radius: var(--h5-radius-md);
  cursor: pointer;
}

.status-icon-wrapper,
.tool-icon-wrapper {
  display: inline-flex;
  width: 40px;
  height: 40px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-primary);
  font-size: 21px;
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-md);
}

.status-item--pending .status-icon-wrapper {
  color: var(--h5-color-pending);
  background: var(--h5-color-pending-soft);
}

.status-item--paid .status-icon-wrapper {
  color: var(--h5-color-success);
  background: var(--h5-color-success-soft);
}

.status-item--cancelled .status-icon-wrapper {
  color: var(--h5-color-text-secondary);
  background: var(--h5-color-surface);
}

.status-item--refund .status-icon-wrapper {
  color: var(--h5-color-info);
  background: var(--h5-color-info-soft);
}

.status-label,
.tool-label {
  overflow: hidden;
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tool-item--collection .tool-icon-wrapper {
  color: var(--h5-color-pending);
  background: var(--h5-color-pending-soft);
}

.tool-item--evaluation .tool-icon-wrapper {
  color: var(--h5-color-success);
  background: var(--h5-color-success-soft);
}

.tool-item--complaint .tool-icon-wrapper {
  color: var(--h5-color-danger);
  background: var(--h5-color-danger-soft);
}

@media (max-width: 359px) {
  .user-header {
    padding-right: var(--h5-space-3);
    padding-left: var(--h5-space-3);
  }

  .user-content {
    padding-right: var(--h5-space-3);
    padding-left: var(--h5-space-3);
  }

  .order-status-list,
  .tool-list {
    gap: var(--h5-space-1);
  }
}
</style>
