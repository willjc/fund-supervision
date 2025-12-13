<template>
  <div class="order-detail-page">
    <van-nav-bar
      title="订单详情"
      left-arrow
      @click-left="$router.back()"
      fixed
      placeholder
    />

    <van-loading v-if="loading" class="loading" />

    <div v-else-if="order" class="detail-content">
      <!-- 订单状态 -->
      <div class="status-section">
        <van-icon :name="getStatusIcon(order.orderStatus)" size="48" :color="getStatusColor(order.orderStatus)" />
        <div class="status-text">{{ getStatusText(order.orderStatus) }}</div>

        <!-- 倒计时 -->
        <div v-if="order.orderStatus === '0' && countdown > 0" class="countdown-bar">
          <van-count-down :time="countdown" format="mm分ss秒" @finish="onCountdownFinish">
            <template #default="timeData">
              <span class="countdown-text">剩余支付时间: </span>
              <span class="countdown-time">{{ timeData.minutes }}:{{ timeData.seconds }}</span>
            </template>
          </van-count-down>
        </div>
        <div v-else-if="order.orderStatus === '0'" class="status-tip">
          请尽快完成支付,超时订单将自动取消
        </div>
      </div>

      <!-- 机构信息卡片 -->
      <div class="institution-card">
        <van-image
          width="80"
          height="80"
          :src="order.institutionCover || 'https://via.placeholder.com/80x80'"
          fit="cover"
          round
        />
        <div class="institution-info">
          <div class="institution-name">{{ order.institutionName || '郑州XXXXX养老院' }}</div>
          <div class="institution-address">
            <van-icon name="location-o" size="12" />
            {{ order.institutionAddress || '郑州市金水区花园口镇花园路123号' }}
          </div>
          <div class="institution-phone">
            <van-icon name="phone-o" size="12" />
            {{ order.institutionPhone || '0371-12345678' }}
          </div>
        </div>
      </div>

      <!-- 订单信息 -->
      <van-cell-group title="订单信息" inset>
        <van-cell title="订单编号" :value="order.orderNo" />
        <van-cell title="老人姓名" :value="order.elderName || '-'" />
        <van-cell title="下单时间" :value="formatDate(order.createTime)" />
        <van-cell v-if="order.paymentTime" title="支付时间" :value="formatDate(order.paymentTime)" />
      </van-cell-group>

      <!-- 费用明细 -->
      <van-cell-group title="费用明细" inset>
        <div class="fee-table">
          <div class="fee-row" v-for="(item, index) in order.feeItems || []" :key="index">
            <span class="fee-name">{{ item.name }}</span>
            <span class="fee-value">¥{{ formatAmount(item.amount) }}</span>
          </div>
          <div v-if="order.monthCount" class="fee-row month-info">
            <span class="fee-name">缴费月数</span>
            <span class="fee-value">{{ order.monthCount }}个月</span>
          </div>
          <div class="fee-row total">
            <span class="fee-name">订单总额</span>
            <span class="fee-value">¥{{ formatAmount(order.orderAmount) }}</span>
          </div>
          <div v-if="order.discountAmount && order.discountAmount > 0" class="fee-row discount">
            <span class="fee-name">优惠金额</span>
            <span class="fee-value">-¥{{ formatAmount(order.discountAmount) }}</span>
          </div>
          <div class="fee-row final">
            <span class="fee-name">实付金额</span>
            <span class="fee-value highlight">¥{{ formatAmount(order.paidAmount || order.orderAmount) }}</span>
          </div>
        </div>
      </van-cell-group>

      <!-- 服务周期 -->
      <van-cell-group v-if="order.serviceStartDate" title="服务周期" inset>
        <van-cell title="开始日期" :value="formatDate(order.serviceStartDate, 'YYYY-MM-DD')" />
        <van-cell title="结束日期" :value="formatDate(order.serviceEndDate, 'YYYY-MM-DD')" />
      </van-cell-group>

      <!-- 备注信息 -->
      <van-cell-group v-if="order.remark" title="备注信息" inset>
        <van-cell :value="order.remark" />
      </van-cell-group>

      <!-- 底部操作按钮 -->
      <div class="action-bar">
        <van-button v-if="order.orderStatus === '0'" block plain @click="handleCancel">
          取消订单
        </van-button>
        <van-button v-if="order.orderStatus === '0'" block type="primary" @click="handlePay">
          立即支付
        </van-button>
        <van-button v-if="order.orderStatus === '1'" block @click="handleContact">
          联系客服
        </van-button>
      </div>
    </div>

    <van-empty v-else description="订单不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import dayjs from 'dayjs'
import { getOrderDetail as getOrderDetailApi, processPayment } from '@/api/order'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const order = ref(null)
const countdown = ref(0) // 倒计时毫秒数

// 模拟订单数据
const mockOrderDetail = {
  orderId: 1,
  orderNo: 'ORD202501140001',
  orderStatus: '0', // 0-待支付 1-已支付 2-已取消 3-退款中
  orderType: '1',
  institutionName: '郑州市金水区花园口社区养老服务中���',
  institutionAddress: '郑州市金水区花园口镇花园路123号',
  institutionPhone: '0371-12345678',
  institutionCover: 'https://via.placeholder.com/80x80',
  elderName: '张老先生',
  createTime: '2025-01-14 10:30:00',
  paymentTime: null,
  feeItems: [
    { name: '护理费', amount: 1500 },
    { name: '伙食费', amount: 800 },
    { name: '床位费', amount: 500 }
  ],
  orderAmount: 2800,
  discountAmount: 300,
  paidAmount: 2500,
  serviceStartDate: '2025-02-01',
  serviceEndDate: '2025-03-01',
  remark: '老人有轻度高血压,需要特别关注'
}

// 获取订单详情
const loadOrderDetail = async () => {
  try {
    loading.value = true
    const orderId = route.params.id

    if (!orderId) {
      showToast('订单ID不能为空')
      router.back()
      return
    }

    // 调用真实API获取订单详情
    const response = await getOrderDetailApi(orderId)

    if (response && response.code === 200 && response.data) {
      order.value = response.data

      // 如果是待支付状态,计算倒计时(15分钟)
      if (order.value.orderStatus === '0') {
        const createTime = dayjs(order.value.createTime)
        const expireTime = createTime.add(15, 'minute')
        const now = dayjs()
        const diff = expireTime.diff(now)

        if (diff > 0) {
          countdown.value = diff
        }
      }
    } else {
      const errorMsg = response?.msg || '获取订单详情失败'
      showToast(errorMsg)
      setTimeout(() => {
        router.back()
      }, 1500)
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    let errorMsg = '获取订单详情失败'
    if (error.response && error.response.data && error.response.data.msg) {
      errorMsg = error.response.data.msg
    } else if (error.message) {
      errorMsg = error.message
    }
    showToast(errorMsg)
    setTimeout(() => {
      router.back()
    }, 1500)
  } finally {
    loading.value = false
  }
}

// 倒计时结束
const onCountdownFinish = () => {
  showToast('订单已超时')
  order.value.orderStatus = '2'
}

// 取消订单
const handleCancel = async () => {
  try {
    await showConfirmDialog({
      title: '提示',
      message: '确定要取消该订单吗?'
    })

    // 模拟取消
    await new Promise(resolve => setTimeout(resolve, 500))

    showToast('取消成功')
    order.value.orderStatus = '2'
  } catch (error) {
    // 用户取消了对话框
    if (error !== 'cancel') {
      showToast('取消失败')
    }
  }
}

// 支付订单
const handlePay = () => {
  if (!order.value) {
    showToast('订单信息不存在')
    return
  }

  // 跳转到支付收银台页面
  router.push({
    path: `/payment/cashier/${order.value.orderId}`,
    query: {
      orderNo: order.value.orderNo,
      amount: order.value.paidAmount || order.value.orderAmount,
      elderName: order.value.elderName,
      institutionId: order.value.institutionId
    }
  })
}

// 联系客服
const handleContact = () => {
  showToast('客服功能开发中')
  // TODO: 跳转到客服页面或拨打客服电话
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    '0': '等待付款',
    '1': '支付成功',
    '2': '订单已取消',
    '3': '退款中'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态图标
const getStatusIcon = (status) => {
  const iconMap = {
    '0': 'clock-o',
    '1': 'checked',
    '2': 'close',
    '3': 'refund-o'
  }
  return iconMap[status] || 'info-o'
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    '0': '#ff976a',
    '1': '#07c160',
    '2': '#969799',
    '3': '#ee0a24'
  }
  return colorMap[status] || '#323233'
}

// 获取订单类型文本
const getOrderTypeText = (type) => {
  const typeMap = {
    '1': '床位费',
    '2': '护理费',
    '3': '餐饮费',
    '4': '医疗费',
    '5': '其他'
  }
  return typeMap[type] || '其他'
}

// 格式化日期
const formatDate = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!date) return ''
  return dayjs(date).format(format)
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toFixed(2)
}

// 页面加载时获取订单详情
onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 70px;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.detail-content {
  padding-bottom: 20px;
}

.status-section {
  background-color: #fff;
  padding: 32px 16px;
  text-align: center;
  margin-bottom: 12px;
}

.status-text {
  font-size: 18px;
  font-weight: 500;
  margin-top: 12px;
  color: #323233;
}

.countdown-bar {
  margin-top: 16px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e53 100%);
  border-radius: 20px;
  display: inline-block;
}

.countdown-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
}

.countdown-time {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  margin-left: 4px;
}

.status-tip {
  font-size: 13px;
  color: #969799;
  margin-top: 8px;
}

.institution-card {
  background: #fff;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  gap: 12px;
}

.institution-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.institution-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.institution-address,
.institution-phone {
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.van-cell-group {
  margin-bottom: 12px;
}

.fee-table {
  padding: 16px;
}

.fee-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.fee-row:last-child {
  border-bottom: none;
}

.fee-row.month-info {
  padding-top: 8px;
  font-size: 13px;
}

.fee-row.month-info .fee-name {
  color: #999;
}

.fee-row.month-info .fee-value {
  color: #666;
}

.fee-row.total {
  padding-top: 12px;
  border-top: 1px solid #eee;
  font-weight: 500;
}

.fee-row.discount .fee-value {
  color: #07c160;
}

.fee-row.final {
  padding-top: 12px;
  border-top: 2px solid #eee;
  font-size: 16px;
  font-weight: 500;
}

.fee-name {
  font-size: 14px;
  color: #666;
}

.fee-value {
  font-size: 14px;
  color: #333;
}

.fee-value.highlight {
  font-size: 18px;
  color: #ee0a24;
  font-weight: bold;
}

:deep(.discount) {
  color: #07c160;
}

:deep(.price) {
  color: #ee0a24;
  font-size: 16px;
  font-weight: 500;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 12px;
  padding: 12px 16px;
  background-color: #fff;
  box-shadow: 0 -2px 12px rgba(100, 101, 102, 0.12);
}

.action-bar .van-button {
  flex: 1;
}
</style>
