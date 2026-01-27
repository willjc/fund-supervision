<template>
  <div class="payment-success-page">
    <van-nav-bar title="支付成功" left-arrow @click-left="goHome" fixed placeholder />

    <div class="success-content">
      <!-- 成功图标 -->
      <div class="success-icon">
        <van-icon name="checked" size="60" color="#fff" />
      </div>

      <!-- 成功标题 -->
      <div class="success-title">订单支付成功</div>

      <!-- 订单金额 -->
      <div class="order-amount">订单金额: ¥{{ formatAmount(paymentAmount) }}</div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <van-button
          round
          block
          class="primary-button"
          @click="viewOrderDetail"
        >
          查看订单详情
        </van-button>
        <van-button
          round
          block
          plain
          class="secondary-button"
          @click="goHome"
        >
          返回首页
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { getOrderByNo } from '@/api/order'

const router = useRouter()
const route = useRoute()

const paymentAmount = ref(0)
const orderNo = ref('')

// 格式化金额
const formatAmount = (amount) => {
  return parseFloat(amount).toFixed(2)
}

// 查看订单详情
const viewOrderDetail = async () => {
  if (!orderNo.value) {
    showToast('订单编号不存在')
    return
  }

  try {
    showLoadingToast({
      message: '加载中...',
      forbidClick: true,
      duration: 0
    })

    // 通过订单号获取订单信息
    const response = await getOrderByNo(orderNo.value)

    closeToast()

    if (response && response.code === 200 && response.data) {
      // 使用订单ID跳转到详情页
      const orderId = response.data.orderId
      router.push({
        name: 'OrderDetail',
        params: { id: orderId }
      })
    } else {
      showToast(response?.msg || '获取订单信息失败')
    }
  } catch (error) {
    closeToast()
    console.error('获取订单信息失败:', error)
    showToast(error.response?.data?.msg || '获取订单信息失败')
  }
}

// 返回首页
const goHome = () => {
  router.push({ name: 'Home' })
}

onMounted(() => {
  // 从路由参数获取信息
  paymentAmount.value = route.query.amount || 0
  orderNo.value = route.query.orderNo || ''
})
</script>

<style scoped>
.payment-success-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.success-content {
  padding: 80px 20px 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 成功图标 */
.success-icon {
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #7b7ed6 0%, #5b5fc7 100%);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(91, 95, 199, 0.4);
}

/* 成功标题 */
.success-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 16px;
}

/* 订单金额 */
.order-amount {
  font-size: 16px;
  color: #666;
  margin-bottom: 60px;
}

/* 操作按钮 */
.action-buttons {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.primary-button {
  background: linear-gradient(135deg, #7b7ed6 0%, #5b5fc7 100%);
  border: none;
  font-size: 16px;
  font-weight: 500;
  height: 48px;
  box-shadow: 0 4px 12px rgba(91, 95, 199, 0.4);
}

.primary-button :deep(.van-button__text) {
  color: #fff;
}

.secondary-button {
  border: 1px solid #5b5fc7;
  color: #5b5fc7;
  font-size: 16px;
  font-weight: 500;
  height: 48px;
  background: #fff;
}

.secondary-button :deep(.van-button__text) {
  color: #5b5fc7;
}
</style>
