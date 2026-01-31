<template>
  <div class="order-detail-page">

    <van-loading v-if="loading" class="loading" />

    <div v-else-if="order" class="detail-content">
      <!-- 订单状态 -->
      <div class="status-section">
        <van-icon :name="getStatusIcon(order.orderStatus)" size="48" :color="getStatusColor(order.orderStatus)" />
        <div class="status-text">{{ getStatusText(order.orderStatus) }}</div>

        <!-- 支付提示：所有订单都可随时支付 -->
        <div v-if="order.orderStatus === '0' || order.orderStatus === '5'" class="status-tip">
          订单可随时支付，无时间限制
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
        <van-cell title="床位信息" :value="order.bedInfo || '未分配'" />
        <van-cell title="老人姓名" :value="order.elderName || '-'" />
        <van-cell title="下单时间" :value="formatDate(order.createTime)" />
        <van-cell v-if="order.paymentTime" title="支付时间" :value="formatDate(order.paymentTime)" />
      </van-cell-group>

      <!-- 费用明细 -->
      <van-cell-group title="费用明细" inset>
        <div class="fee-table">
          <!-- 显示订单明细 -->
          <div class="fee-row" v-for="(item, index) in orderItems" :key="index">
            <div class="fee-item-content">
              <span class="fee-name">{{ item.itemName }}</span>
              <span v-if="item.isPriceModified === '1' && item.originalUnitPrice" class="price-change-tag">
                <span class="original-price">¥{{ item.originalUnitPrice }}</span>
                <span class="arrow">→</span>
                <span class="new-price">¥{{ item.unitPrice }}</span>
              </span>
              <span v-else class="fee-value">¥{{ item.unitPrice }}</span>
              <span class="fee-desc" v-if="item.itemDescription">{{ item.itemDescription }}</span>
            </div>
            <div class="fee-item-right">
              <span class="fee-quantity">×{{ item.quantity }}</span>
              <span class="fee-total">¥{{ formatAmount(item.totalAmount) }}</span>
            </div>
          </div>

          <!-- 价格变更汇总 -->
          <div v-if="hasPriceModified" class="price-change-summary">
            <div class="summary-title">⚠️ 价格已调整</div>
            <div v-for="item in modifiedItems" :key="item.itemId" class="summary-item">
              {{ item.itemName }}：¥{{ item.originalUnitPrice }} → ¥{{ item.unitPrice }}
              <span class="price-diff" :class="(item.unitPrice - item.originalUnitPrice) >= 0 ? 'increase' : 'decrease'">
                {{ (item.unitPrice - item.originalUnitPrice) >= 0 ? '+' : '' }}{{ (item.unitPrice - item.originalUnitPrice).toFixed(2) }}元
              </span>
            </div>
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

      <!-- 支付凭证 -->
      <van-cell-group v-if="order.orderStatus === '1' && order.paymentProof" title="支付凭证" inset>
        <div class="payment-proof">
          <van-image
            :src="order.paymentProof"
            fit="cover"
            @click="previewPaymentProof"
          />
          <div v-if="order.paymentProofRemark" class="proof-remark">
            {{ order.paymentProofRemark }}
          </div>
        </div>
      </van-cell-group>

      <!-- 备注信息 -->
      <van-cell-group v-if="order.remark" title="备注信息" inset>
        <van-cell :value="order.remark" />
      </van-cell-group>

      <!-- 底部操作按钮 -->
      <div class="action-bar">
        <van-button v-if="order.orderStatus === '0' || order.orderStatus === '5'" block plain @click="handleCancel">
          取消订单
        </van-button>
        <van-button v-if="order.orderStatus === '0' || order.orderStatus === '5'" block type="primary" @click="handlePay">
          立即支付
        </van-button>
        <van-button v-if="order.orderStatus === '1'" block type="primary" @click="handleReview">
          去评价
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
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog, showImagePreview } from 'vant'
import dayjs from 'dayjs'
import { getOrderDetail as getOrderDetailApi, processPayment, getOrderItems } from '@/api/order'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const order = ref(null)
const orderItems = ref([]) // 订单明细列表

// 是否有价格修改
const hasPriceModified = computed(() => {
  return orderItems.value.some(item => item.isPriceModified === '1')
})

// 获取被修改的项目列表
const modifiedItems = computed(() => {
  return orderItems.value.filter(item => item.isPriceModified === '1' && item.originalUnitPrice)
})

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

    // 并发调用获取订单详情和订单明细
    const [detailRes, itemsRes] = await Promise.allSettled([
      getOrderDetailApi(orderId),
      getOrderItems(orderId)
    ])

    // 处理订单详情
    if (detailRes.status === 'fulfilled' && detailRes.value && detailRes.value.code === 200 && detailRes.value.data) {
      order.value = detailRes.value.data
    } else {
      const errorMsg = detailRes.status === 'rejected' ? detailRes.reason?.message : (detailRes.value?.msg || '获取订单详情失败')
      showToast(errorMsg)
      setTimeout(() => {
        router.back()
      }, 1500)
      loading.value = false
      return
    }

    // 处理订单明细
    if (itemsRes.status === 'fulfilled' && itemsRes.value && itemsRes.value.code === 200) {
      orderItems.value = itemsRes.value.data || []
    } else {
      console.warn('获取订单明细失败，使用空数组')
      orderItems.value = []
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

// 评价订单
const handleReview = () => {
  if (!order.value) {
    showToast('订单信息不存在')
    return
  }

  // 跳转到评价页面
  router.push({
    path: `/review/submit/${order.value.orderId}`,
    query: {
      institutionName: order.value.institutionName,
      orderAmount: order.value.paidAmount || order.value.orderAmount
    }
  })
}

// 联系客服
const handleContact = () => {
  showToast('客服功能开发中')
  // TODO: 跳转到客服页面或拨打客服电话
}

// 预览支付凭证
const previewPaymentProof = () => {
  if (order.value && order.value.paymentProof) {
    showImagePreview({
      images: [order.value.paymentProof],
      closeable: true,
    })
  }
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    '0': '等待付款',
    '1': '支付成功',
    '2': '订单已取消',
    '3': '退款中',
    '4': '等待机构审核',
    '5': '等待付款'
  }
  return statusMap[status] || '未知状态'
}

// 获取状态图标
const getStatusIcon = (status) => {
  const iconMap = {
    '0': 'clock-o',
    '1': 'checked',
    '2': 'close',
    '3': 'refund-o',
    '4': 'todo-list-o',
    '5': 'clock-o'
  }
  return iconMap[status] || 'info-o'
}

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    '0': '#ff976a',
    '1': '#07c160',
    '2': '#969799',
    '3': '#ee0a24',
    '4': '#1989fa',
    '5': '#ff976a'
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

/* 价格变更相关样式 */
.fee-item-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.fee-desc {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.fee-item-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.fee-quantity {
  color: #999;
  font-size: 13px;
}

.fee-total {
  color: #E6A23C;
  font-weight: bold;
  font-size: 14px;
}

.price-change-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
}

.price-change-tag .original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 12px;
}

.price-change-tag .arrow {
  color: #999;
  font-size: 12px;
}

.price-change-tag .new-price {
  color: #ee0a24;
  font-weight: 500;
}

.price-change-summary {
  margin-top: 12px;
  padding: 12px;
  background-color: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 6px;
}

.price-change-summary .summary-title {
  color: #e6a23c;
  font-weight: 500;
  font-size: 14px;
  margin-bottom: 8px;
}

.price-change-summary .summary-item {
  font-size: 12px;
  color: #606266;
  padding: 4px 0;
}

.price-change-summary .price-diff {
  margin-left: 10px;
  font-weight: 500;
}

.price-change-summary .price-diff.increase {
  color: #f56c6c;
}

.price-change-summary .price-diff.decrease {
  color: #67c23a;
}

/* 支付凭证样式 */
.payment-proof {
  padding: 16px;
  text-align: center;
}

.payment-proof .van-image {
  width: 100%;
  max-width: 300px;
  border-radius: 8px;
  cursor: pointer;
}

.proof-remark {
  margin-top: 12px;
  padding: 8px 12px;
  background-color: #f5f5f5;
  border-radius: 4px;
  font-size: 13px;
  color: #666;
  text-align: left;
}
</style>
