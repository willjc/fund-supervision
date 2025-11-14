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
        <div v-if="order.orderStatus === '0'" class="status-tip">
          请尽快完成支付,超时订单将自动取消
        </div>
      </div>

      <!-- 订单信息 -->
      <van-cell-group title="订单信息" inset>
        <van-cell title="订单编号" :value="order.orderNo" />
        <van-cell title="订单类型" :value="getOrderTypeText(order.orderType)" />
        <van-cell title="养老机构" :value="order.institutionName || '郑州XXXXX养老院'" />
        <van-cell title="老人姓名" :value="order.elderName || '-'" />
        <van-cell title="下单时间" :value="formatDate(order.createTime)" />
        <van-cell v-if="order.paymentTime" title="支付时间" :value="formatDate(order.paymentTime)" />
      </van-cell-group>

      <!-- 费用明细 -->
      <van-cell-group title="费用明细" inset>
        <van-cell title="订单金额" :value="`¥ ${formatAmount(order.orderAmount)}`" />
        <van-cell v-if="order.discountAmount && order.discountAmount > 0" title="优惠金额" :value="`-¥ ${formatAmount(order.discountAmount)}`" value-class="discount" />
        <van-cell title="实付金额" :value="`¥ ${formatAmount(order.paidAmount || order.orderAmount)}`" value-class="price" />
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
import { getOrderDetail, cancelOrder } from '@/api/order'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const order = ref(null)

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

    const res = await getOrderDetail(orderId)

    if (res.code === 200) {
      order.value = res.data
    } else {
      showToast(res.msg || '获取订单详情失败')
      // 延迟返回,让用户看到提示
      setTimeout(() => {
        router.back()
      }, 1500)
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    showToast('获取订单详情失败')
    setTimeout(() => {
      router.back()
    }, 1500)
  } finally {
    loading.value = false
  }
}

// 取消订单
const handleCancel = async () => {
  try {
    await showConfirmDialog({
      title: '提示',
      message: '确定要取消该订单吗?'
    })

    const res = await cancelOrder(order.value.orderId)

    if (res.code === 200) {
      showToast('取消成功')
      // 刷新订单详情
      loadOrderDetail()
    } else {
      showToast(res.msg || '取消失败')
    }
  } catch (error) {
    // 用户取消了对话框
    if (error !== 'cancel') {
      showToast('取消失败')
    }
  }
}

// 支付订单
const handlePay = () => {
  showToast('支付功能开发中')
  // TODO: 跳转到支付页面
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

.status-tip {
  font-size: 13px;
  color: #969799;
  margin-top: 8px;
}

.van-cell-group {
  margin-bottom: 12px;
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
