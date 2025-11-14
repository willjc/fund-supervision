<template>
  <div class="payment-cashier-page">
    <van-nav-bar title="支付收银台" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="cashier-content">
      <!-- 支付金额 -->
      <div class="amount-section">
        <div class="amount-label">订单金额</div>
        <div class="amount-value">¥{{ formatAmount(paymentAmount) }}</div>
        <div class="countdown-tip">
          <van-icon name="clock-o" size="14" color="#ff6b00" />
          <span>剩余支付时间 </span>
          <van-count-down
            v-if="countdown > 0"
            :time="countdown"
            format="mm:ss"
            @finish="onCountdownFinish"
          >
            <template #default="timeData">
              <span class="countdown-text">
                {{ String(timeData.minutes).padStart(2, '0') }}:{{ String(timeData.seconds).padStart(2, '0') }}
              </span>
            </template>
          </van-count-down>
        </div>
      </div>

      <!-- 支付方式选择 -->
      <div class="payment-methods">
        <div class="payment-title">选择支付方式</div>
        <van-radio-group v-model="selectedPaymentMethod">
          <van-cell
            v-for="method in paymentMethods"
            :key="method.value"
            clickable
            @click="selectedPaymentMethod = method.value"
          >
            <template #icon>
              <div class="payment-icon">
                <van-icon :name="method.icon" :color="method.color" size="28" />
              </div>
            </template>
            <template #title>
              <span class="payment-name">{{ method.name }}</span>
            </template>
            <template #right-icon>
              <van-radio :name="method.value" checked-color="#5b5fc7" />
            </template>
          </van-cell>
        </van-radio-group>
      </div>

      <!-- 确认支付按钮 -->
      <div class="confirm-section">
        <van-button
          round
          block
          class="confirm-button"
          @click="confirmPayment"
        >
          确认支付
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

// 支付金额
const paymentAmount = ref(0)

// 倒计时(15分钟 = 900000毫秒)
const countdown = ref(900000)

// 选中的支付方式
const selectedPaymentMethod = ref('alipay')

// 支付方式列表
const paymentMethods = [
  {
    value: 'alipay',
    name: '支付宝支付',
    icon: 'balance-pay',
    color: '#1989fa'
  },
  {
    value: 'wechat',
    name: '微信支付',
    icon: 'wechat-pay',
    color: '#07c160'
  }
]

// 格式化金额
const formatAmount = (amount) => {
  return parseFloat(amount).toFixed(2)
}

// 倒计时结束
const onCountdownFinish = () => {
  showToast('支付超时,订单已取消')
  setTimeout(() => {
    router.push({ name: 'Order' })
  }, 1500)
}

// 确认支付
const confirmPayment = async () => {
  const loadingToast = showLoadingToast({
    message: '支付处理中...',
    forbidClick: true,
    duration: 0
  })

  try {
    // 模拟支付处理
    await new Promise(resolve => setTimeout(resolve, 1500))

    closeToast()

    // 跳转到支付成功页面
    router.push({
      name: 'PaymentSuccess',
      query: {
        orderNo: route.params.orderId,
        amount: paymentAmount.value,
        paymentMethod: selectedPaymentMethod.value,
        elderName: route.query.elderName
      }
    })
  } catch (error) {
    closeToast()
    showToast('支付失败,请重试')
  }
}

onMounted(() => {
  // 从路由参数获取支付金额
  paymentAmount.value = route.query.amount || 3999.00

  // 模拟倒计时开始时间(从15分钟开始)
  countdown.value = 15 * 60 * 1000
})
</script>

<style scoped>
.payment-cashier-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 80px;
}

.cashier-content {
  padding: 12px;
}

/* 支付金额区域 */
.amount-section {
  background: #fff;
  border-radius: 12px;
  padding: 32px 20px;
  text-align: center;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.amount-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.amount-value {
  font-size: 48px;
  font-weight: bold;
  color: #ff6b00;
  margin-bottom: 20px;
  letter-spacing: 1px;
}

.countdown-tip {
  font-size: 13px;
  color: #666;
  background: #fff3e0;
  padding: 8px 16px;
  border-radius: 20px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: 1px solid #ffe0b2;
}

.countdown-text {
  font-weight: 600;
  color: #ff6b00;
  font-family: 'Courier New', monospace;
  margin-left: 4px;
}

/* 支付方式 */
.payment-methods {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.payment-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  padding: 0 4px 12px;
}

.payment-methods :deep(.van-cell-group) {
  background: transparent;
}

.payment-methods :deep(.van-radio-group) {
  background: transparent;
}

.payment-icon {
  margin-right: 12px;
  display: flex;
  align-items: center;
}

.payment-name {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.van-cell {
  padding: 16px;
}

.van-cell:not(:last-child)::after {
  border-bottom: 1px solid #f0f0f0;
}

/* 确认支付按钮 */
.confirm-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: #fff;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

.confirm-button {
  background: linear-gradient(135deg, #ff8a00 0%, #ff6b00 100%);
  border: none;
  font-size: 16px;
  font-weight: 500;
  height: 48px;
  box-shadow: 0 4px 16px rgba(255, 107, 0, 0.3);
}

:deep(.van-button__text) {
  color: #fff;
}

:deep(.van-radio__icon--checked .van-icon) {
  background-color: #ff6b00;
  border-color: #ff6b00;
}
</style>
