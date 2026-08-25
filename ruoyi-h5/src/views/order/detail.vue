<template>
  <div class="order-detail-page h5-page h5-page--constrained">
    <nav class="detail-nav" aria-label="订单详情导航">
      <button type="button" class="detail-nav__back" aria-label="返回上一页" @click="handleBack">
        <van-icon name="arrow-left" />
      </button>
      <h1>订单详情</h1>
      <span class="detail-nav__placeholder" aria-hidden="true"></span>
    </nav>

    <div v-if="loading" class="loading-state">
      <van-loading color="var(--h5-color-primary)">正在加载订单详情</van-loading>
    </div>

    <main v-else-if="order" class="detail-content">
      <section :class="['status-section', `status-section--${order.orderStatus}`]">
        <div class="status-section__main">
          <span class="status-icon" aria-hidden="true">
            <van-icon :name="getStatusIcon(order.orderStatus)" :color="getStatusColor(order.orderStatus)" />
          </span>
          <div>
            <p class="status-label">当前订单状态</p>
            <h2>{{ order.orderStatusText || getStatusText(order.orderStatus) }}</h2>
          </div>
        </div>

        <div class="status-amount">
          <span>订单金额</span>
          <strong><small>¥</small>{{ formatAmount(order.paidAmount || order.orderAmount) }}</strong>
        </div>

        <div class="status-meta">
          <span>订单号</span>
          <strong>{{ order.orderNo || '-' }}</strong>
        </div>

        <p v-if="order.orderStatus === '0' || order.orderStatus === '5'" class="status-tip">
          <van-icon name="info-o" />
          订单可随时支付，无时间限制
        </p>
      </section>

      <section class="institution-card h5-card" aria-labelledby="institution-title">
        <img
          class="institution-cover"
          :src="institutionImage"
          alt="养老机构封面"
          @error="handleInstitutionImageError"
        />
        <div class="institution-info">
          <h2 id="institution-title">{{ order.institutionName || '养老机构' }}</h2>
          <p>
            <van-icon name="location-o" />
            <span>{{ order.institutionAddress || '地址信息待完善' }}</span>
          </p>
          <p>
            <van-icon name="phone-o" />
            <span>{{ order.institutionPhone || '联系电话待完善' }}</span>
          </p>
        </div>
      </section>

      <section class="detail-card h5-card" aria-labelledby="order-info-title">
        <header class="section-heading">
          <span class="section-heading__icon" aria-hidden="true"><van-icon name="description-o" /></span>
          <div>
            <h2 id="order-info-title">订单信息</h2>
            <p>入住人与订单信息</p>
          </div>
        </header>
        <dl class="detail-list">
          <div>
            <dt>老人姓名</dt>
            <dd>{{ order.elderName || '-' }}</dd>
          </div>
          <div>
            <dt>床位信息</dt>
            <dd>{{ order.bedInfo || '未分配' }}</dd>
          </div>
          <div>
            <dt>订单类型</dt>
            <dd>{{ order.orderTypeText || getOrderTypeText(order.orderType) }}</dd>
          </div>
          <div>
            <dt>订单状态</dt>
            <dd>{{ order.orderStatusText || getStatusText(order.orderStatus) }}</dd>
          </div>
          <div>
            <dt>下单时间</dt>
            <dd>{{ formatDate(order.createTime) || '-' }}</dd>
          </div>
        </dl>
      </section>

      <section class="detail-card h5-card" aria-labelledby="fee-title">
        <header class="section-heading">
          <span class="section-heading__icon" aria-hidden="true"><van-icon name="balance-list-o" /></span>
          <div>
            <h2 id="fee-title">费用明细</h2>
            <p>核对费用项目与最终金额</p>
          </div>
        </header>

        <div class="fee-table">
          <div v-if="orderItems.length > 0" class="fee-items">
            <div v-for="(item, index) in orderItems" :key="item.itemId || index" class="fee-row fee-row--item">
              <div class="fee-item-content">
                <span class="fee-name">{{ item.itemName }}</span>
                <span v-if="item.isPriceModified === '1' && item.originalUnitPrice" class="price-change-tag">
                  <span class="original-price">原价 ¥{{ item.originalUnitPrice }}</span>
                  <span class="arrow">→</span>
                  <span class="new-price">现价 ¥{{ item.unitPrice }}</span>
                </span>
                <span v-else class="fee-unit-price">单价 ¥{{ formatAmount(item.unitPrice) }}</span>
                <span v-if="item.itemDescription" class="fee-desc">{{ item.itemDescription }}</span>
              </div>
              <div class="fee-item-right">
                <span class="fee-quantity">数量 ×{{ item.quantity }}</span>
                <strong class="fee-total">¥{{ formatAmount(item.totalAmount) }}</strong>
              </div>
            </div>
          </div>
          <div v-else class="fee-empty">
            <van-icon name="records-o" />
            <span>暂无单独费用项目，以订单总额为准</span>
          </div>

          <div v-if="hasPriceModified" class="price-change-summary">
            <div class="summary-title"><van-icon name="warning-o" /> 价格已调整</div>
            <div v-for="item in modifiedItems" :key="item.itemId" class="summary-item">
              <span>{{ item.itemName }}：¥{{ item.originalUnitPrice }} → ¥{{ item.unitPrice }}</span>
              <strong :class="['price-diff', (item.unitPrice - item.originalUnitPrice) >= 0 ? 'increase' : 'decrease']">
                {{ (item.unitPrice - item.originalUnitPrice) >= 0 ? '+' : '' }}{{ (item.unitPrice - item.originalUnitPrice).toFixed(2) }}元
              </strong>
            </div>
          </div>

          <div class="fee-summary">
            <div class="fee-row total">
              <span class="fee-name">订单总额</span>
              <span class="fee-value">¥{{ formatAmount(order.orderAmount) }}</span>
            </div>
            <div v-if="order.discountAmount && order.discountAmount > 0" class="fee-row discount">
              <span class="fee-name">优惠金额</span>
              <span class="fee-value">-¥{{ formatAmount(order.discountAmount) }}</span>
            </div>
            <div class="fee-row final">
              <span class="fee-name">应付 / 实付金额</span>
              <strong class="fee-value highlight">¥{{ formatAmount(order.paidAmount || order.orderAmount) }}</strong>
            </div>
          </div>
        </div>
      </section>

      <div
        v-if="order.orderStatus === '0' || order.orderStatus === '5' || order.orderStatus === '4' || order.orderStatus === '1'"
        class="action-bar h5-fixed-action-bar"
      >
        <van-button
          v-if="order.orderStatus === '0' || order.orderStatus === '5' || order.orderStatus === '4'"
          plain
          @click="handleCancel"
        >
          取消订单
        </van-button>
        <van-button v-if="order.orderStatus === '0' || order.orderStatus === '5'" type="primary" @click="handlePay">
          立即支付
        </van-button>
        <van-button v-if="order.orderStatus === '1'" type="primary" icon="comment-o" @click="handleReview">
          去评价
        </van-button>
      </div>
    </main>

    <div v-else class="not-found-state">
      <van-empty description="订单不存在或已被删除" image-size="112">
        <van-button plain type="primary" @click="handleBack">返回订单列表</van-button>
      </van-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import dayjs from 'dayjs'
import { getOrderDetail as getOrderDetailApi, getOrderItems, cancelOrder } from '@/api/order'
import { getImageUrl } from '@/utils/image'
import institutionPlaceholder from '@/assets/images/institution-placeholder.svg'

const router = useRouter()
const route = useRoute()

const loading = ref(true)
const order = ref(null)
const orderItems = ref([]) // 订单明细列表

const institutionImage = computed(() => {
  return order.value?.institutionCover ? getImageUrl(order.value.institutionCover) : institutionPlaceholder
})

// 是否有价格修改
const hasPriceModified = computed(() => {
  return orderItems.value.some(item => item.isPriceModified === '1')
})

// 获取被修改的项目列表
const modifiedItems = computed(() => {
  return orderItems.value.filter(item => item.isPriceModified === '1' && item.originalUnitPrice)
})

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

    // 调用后端API取消订单
    const res = await cancelOrder(order.value.orderId)

    if (res.code === 200) {
      showToast('取消成功')
      // 重新加载订单详情以获取最新状态
      await loadOrderDetail()
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

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    '0': '等待付款',
    '1': '支付成功',
    '2': '订单已取消',
    '3': '已退款',
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
    '0': 'var(--h5-color-pending)',
    '1': 'var(--h5-color-success)',
    '2': 'var(--h5-color-text-secondary)',
    '3': 'var(--h5-color-danger)',
    '4': 'var(--h5-color-info)',
    '5': 'var(--h5-color-pending)'
  }
  return colorMap[status] || 'var(--h5-color-text)'
}

const getOrderTypeText = (type) => {
  const typeMap = {
    '1': '入驻订单',
    '2': '续费订单'
  }
  return typeMap[type] || '其他订单'
}

const handleInstitutionImageError = (event) => {
  if (event.currentTarget && event.currentTarget.src !== institutionPlaceholder) {
    event.currentTarget.src = institutionPlaceholder
  }
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

const handleBack = () => {
  router.back()
}

// 页面加载时获取订单详情
onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
  padding-bottom: calc(var(--h5-action-bar-min-height) + var(--h5-safe-area-bottom) + var(--h5-space-5));
}

.detail-nav {
  position: sticky;
  top: 0;
  z-index: var(--h5-z-sticky);
  display: grid;
  grid-template-columns: 44px 1fr 44px;
  align-items: center;
  min-height: calc(var(--h5-header-height) + var(--h5-safe-area-top));
  padding: var(--h5-safe-area-top) var(--h5-page-padding) 0;
  background: var(--h5-color-surface);
  border-bottom: 1px solid var(--h5-color-divider);
}

.detail-nav h1 {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
  text-align: center;
}

.detail-nav__back {
  display: inline-flex;
  width: 44px;
  height: 44px;
  align-items: center;
  justify-content: flex-start;
  color: var(--h5-color-primary);
  font-size: 22px;
  background: transparent;
  border: 0;
  cursor: pointer;
}

.detail-nav__back:focus-visible {
  outline: none;
  box-shadow: var(--h5-shadow-focus);
}

.detail-nav__placeholder {
  width: 44px;
}

.loading-state,
.not-found-state {
  display: flex;
  min-height: 420px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-md);
}

.detail-content {
  display: grid;
  gap: var(--h5-space-3);
  padding: var(--h5-space-3) var(--h5-page-padding);
}

.status-section {
  padding: var(--h5-space-5);
  background: linear-gradient(135deg, var(--h5-color-primary-50), var(--h5-color-surface));
  border: 1px solid var(--h5-color-primary-100);
  border-radius: var(--h5-radius-lg);
  box-shadow: var(--h5-shadow-sm);
}

.status-section__main {
  display: flex;
  align-items: center;
  gap: var(--h5-space-3);
}

.status-icon {
  display: inline-flex;
  flex: 0 0 48px;
  width: 48px;
  height: 48px;
  align-items: center;
  justify-content: center;
  font-size: 27px;
  background: var(--h5-color-surface);
  border: 1px solid var(--h5-color-primary-100);
  border-radius: var(--h5-radius-md);
  box-shadow: var(--h5-shadow-xs);
}

.status-label {
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.status-section h2 {
  margin-top: 2px;
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-xl);
  font-weight: var(--h5-font-weight-bold);
}

.status-amount {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-top: var(--h5-space-5);
  padding-top: var(--h5-space-4);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
  border-top: 1px solid var(--h5-color-primary-100);
}

.status-amount strong {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-3xl);
  font-weight: var(--h5-font-weight-bold);
  font-variant-numeric: tabular-nums;
}

.status-amount small {
  margin-right: 2px;
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-md);
}

.status-meta {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-3);
  margin-top: var(--h5-space-2);
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.status-meta strong {
  color: var(--h5-color-text-secondary);
  font-weight: var(--h5-font-weight-medium);
  text-align: right;
  overflow-wrap: anywhere;
}

.status-tip {
  display: flex;
  align-items: center;
  gap: var(--h5-space-1);
  margin-top: var(--h5-space-3);
  padding: var(--h5-space-2) var(--h5-space-3);
  color: var(--h5-color-pending);
  font-size: var(--h5-font-size-sm);
  background: var(--h5-color-pending-soft);
  border-radius: var(--h5-radius-sm);
}

.institution-card {
  display: flex;
  gap: var(--h5-space-3);
  padding: var(--h5-space-4);
}

.institution-cover {
  flex: 0 0 80px;
  width: 80px;
  height: 80px;
  object-fit: cover;
  background: var(--h5-color-primary-soft);
  border: 1px solid var(--h5-color-border);
  border-radius: var(--h5-radius-md);
}

.institution-info {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  justify-content: center;
  gap: var(--h5-space-2);
}

.institution-info h2 {
  overflow: hidden;
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.institution-info p {
  display: flex;
  align-items: flex-start;
  gap: var(--h5-space-1);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  line-height: var(--h5-line-height-normal);
}

.institution-info p .van-icon {
  flex: 0 0 auto;
  margin-top: 3px;
  color: var(--h5-color-primary);
}

.detail-card {
  padding: var(--h5-space-4);
}

.section-heading {
  display: flex;
  align-items: center;
  gap: var(--h5-space-2);
  padding-bottom: var(--h5-space-3);
  border-bottom: 1px solid var(--h5-color-divider);
}

.section-heading__icon {
  display: inline-flex;
  flex: 0 0 36px;
  width: 36px;
  height: 36px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-primary);
  font-size: 19px;
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-sm);
}

.section-heading h2 {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
}

.section-heading p {
  margin-top: 2px;
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.detail-list {
  display: grid;
}

.detail-list > div {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-4);
  padding: var(--h5-space-3) 0;
  font-size: var(--h5-font-size-md);
  line-height: var(--h5-line-height-normal);
  border-bottom: 1px solid var(--h5-color-divider);
}

.detail-list > div:last-child {
  padding-bottom: 0;
  border-bottom: 0;
}

.detail-list dt {
  flex: 0 0 auto;
  color: var(--h5-color-text-tertiary);
}

.detail-list dd {
  min-width: 0;
  color: var(--h5-color-text-secondary);
  text-align: right;
  overflow-wrap: anywhere;
}

.fee-table {
  padding-top: var(--h5-space-1);
}

.fee-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-3);
  padding: var(--h5-space-3) 0;
}

.fee-row--item {
  border-bottom: 1px solid var(--h5-color-divider);
}

.fee-item-content {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: var(--h5-space-1);
}

.fee-name {
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
}

.fee-row--item .fee-name {
  color: var(--h5-color-text);
  font-weight: var(--h5-font-weight-medium);
}

.fee-unit-price,
.fee-desc,
.fee-quantity {
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
  line-height: var(--h5-line-height-normal);
}

.fee-item-right {
  display: flex;
  flex: 0 0 auto;
  align-items: flex-end;
  flex-direction: column;
  gap: var(--h5-space-1);
}

.fee-total {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
}

.price-change-tag {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: var(--h5-space-1);
  font-size: var(--h5-font-size-sm);
}

.price-change-tag .original-price {
  color: var(--h5-color-text-tertiary);
  text-decoration: line-through;
}

.price-change-tag .arrow {
  color: var(--h5-color-text-tertiary);
}

.price-change-tag .new-price {
  color: var(--h5-color-primary);
  font-weight: var(--h5-font-weight-semibold);
}

.fee-empty {
  display: flex;
  min-height: 88px;
  align-items: center;
  justify-content: center;
  gap: var(--h5-space-2);
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.price-change-summary {
  display: grid;
  gap: var(--h5-space-2);
  margin: var(--h5-space-3) 0;
  padding: var(--h5-space-3);
  color: var(--h5-color-warning);
  background: var(--h5-color-warning-soft);
  border: 1px solid rgba(184, 108, 8, 0.2);
  border-radius: var(--h5-radius-md);
}

.summary-title {
  display: flex;
  align-items: center;
  gap: var(--h5-space-1);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
}

.summary-item {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-2);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  line-height: var(--h5-line-height-normal);
}

.price-diff {
  flex: 0 0 auto;
  font-weight: var(--h5-font-weight-semibold);
}

.price-diff.increase {
  color: var(--h5-color-danger);
}

.price-diff.decrease,
.fee-row.discount .fee-value {
  color: var(--h5-color-success);
}

.fee-summary {
  margin-top: var(--h5-space-1);
  padding-top: var(--h5-space-2);
  border-top: 1px solid var(--h5-color-border);
}

.fee-row.total,
.fee-row.discount {
  padding: var(--h5-space-2) 0;
}

.fee-value {
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
}

.fee-row.final {
  align-items: baseline;
  margin-top: var(--h5-space-1);
  padding-top: var(--h5-space-3);
  border-top: 1px dashed var(--h5-color-border);
}

.fee-value.highlight {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-xl);
  font-weight: var(--h5-font-weight-bold);
  font-variant-numeric: tabular-nums;
}

.action-bar {
  max-width: var(--h5-page-max-width);
  margin: 0 auto;
}

.action-bar :deep(.van-button) {
  flex: 1;
  min-width: 0;
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
}

.not-found-state :deep(.van-button) {
  min-width: 144px;
  min-height: 44px;
  margin-top: var(--h5-space-3);
}

@media (max-width: 359px) {
  .institution-card {
    align-items: flex-start;
  }

  .institution-cover {
    flex-basis: 68px;
    width: 68px;
    height: 68px;
  }
}
</style>
