<template>
  <div class="order-page h5-page h5-page--constrained">
    <div class="order-page__top">
      <header class="order-heading">
        <div>
          <p class="order-heading__eyebrow">养老服务订单</p>
          <h1>我的订单</h1>
        </div>
        <span class="order-heading__icon" aria-hidden="true">
          <van-icon name="orders-o" />
        </span>
      </header>

      <div class="search-bar">
        <van-search
          v-model="searchValue"
          shape="round"
          placeholder="搜索订单号或养老机构"
          @search="onSearch"
        />
      </div>

      <van-tabs v-model:active="activeTab" class="order-tabs" @change="onTabChange">
        <van-tab title="全部" name="all" />
        <van-tab title="待审核" name="4" />
        <van-tab title="待付款" name="pending" />
        <van-tab title="已支付" name="1" />
        <van-tab title="已取消" name="2" />
        <van-tab title="退款" name="3" />
      </van-tabs>
    </div>

    <van-pull-refresh v-model="refreshing" class="order-content" @refresh="onRefresh">
      <van-list
        v-if="activeTab !== '3'"
        v-model:loading="loading"
        :finished="finished"
        finished-text="已经到底了"
        @load="onLoad"
      >
        <div v-if="orderList.length > 0" class="order-list">
          <article v-for="order in orderList" :key="order.orderId" class="order-card h5-card">
            <button
              type="button"
              class="order-card__main"
              :aria-label="`查看订单 ${order.orderNo || ''} 详情`"
              @click="goToDetail(order.orderId)"
            >
              <div class="order-card__header">
                <div class="institution-name">
                  <span class="institution-name__icon" aria-hidden="true">
                    <van-icon name="shop-o" />
                  </span>
                  <span>{{ order.institutionName || '养老机构' }}</span>
                </div>
                <span :class="['order-status', `status-${order.orderStatus}`]">
                  {{ order.orderStatusText || getStatusText(order.orderStatus) }}
                </span>
              </div>

              <div class="order-card__body">
                <div class="order-meta">
                  <div class="meta-row">
                    <span class="meta-label">订单编号</span>
                    <span class="meta-value meta-value--number">{{ order.orderNo || '-' }}</span>
                  </div>
                  <div class="meta-row">
                    <span class="meta-label">订单类型</span>
                    <span class="meta-value">
                      {{ getOrderTypeText(order.orderType) }}
                      <span v-if="order.orderType === '2'" class="renew-tag">续费</span>
                    </span>
                  </div>
                  <div class="meta-row">
                    <span class="meta-label">下单时间</span>
                    <time class="meta-value">{{ formatDate(order.createTime) || '-' }}</time>
                  </div>
                </div>

                <div class="amount-summary">
                  <span>应付金额</span>
                  <strong><small>¥</small>{{ formatAmount(order.orderAmount) }}</strong>
                </div>
              </div>

              <span class="detail-cue">
                查看订单详情
                <van-icon name="arrow" />
              </span>
            </button>

            <footer
              v-if="order.orderStatus === '0' || order.orderStatus === '5' || order.orderStatus === '4' || order.orderStatus === '1'"
              class="order-card__actions"
            >
              <van-button
                v-if="order.orderStatus === '0' || order.orderStatus === '5' || order.orderStatus === '4'"
                plain
                @click="handleCancel(order)"
              >
                取消订单
              </van-button>
              <van-button
                v-if="order.orderStatus === '0' || order.orderStatus === '5'"
                type="primary"
                @click="handlePay(order)"
              >
                立即付款
              </van-button>
              <van-button v-if="order.orderStatus === '1'" plain type="primary" @click="handleRenew(order)">
                续费
              </van-button>
              <van-button v-if="order.orderStatus === '1'" @click="goToDetail(order.orderId)">
                查看详情
              </van-button>
            </footer>
          </article>
        </div>

        <van-empty
          v-else-if="!loading"
          class="order-empty"
          description="暂无符合条件的订单"
          image-size="104"
        >
          <p class="empty-hint">订单提交后会在这里展示进度</p>
        </van-empty>
      </van-list>

      <section v-else class="refund-list-container" aria-label="退款记录">
        <div v-if="loading" class="state-loading">
          <van-loading type="spinner" color="var(--h5-color-primary)">正在加载退款记录</van-loading>
        </div>

        <div v-else-if="refundList.length > 0" class="refund-list">
          <article
            v-for="refund in refundList"
            :key="refund.refundId || refund.id || refund.refundNo"
            class="refund-card h5-card"
          >
            <div class="refund-header">
              <div class="refund-no">
                <span class="refund-no__icon" aria-hidden="true"><van-icon name="bill-o" /></span>
                <div>
                  <span class="refund-no__label">退款编号</span>
                  <strong>{{ refund.refundNo || '-' }}</strong>
                </div>
              </div>
              <span :class="['refund-status', `status-${refund.refundStatus}`]">
                {{ refund.statusText || '处理中' }}
              </span>
            </div>

            <div class="refund-body">
              <div class="refund-info">
                <div class="info-row">
                  <span class="label">养老机构</span>
                  <span class="value">{{ refund.institutionName || '养老机构' }}</span>
                </div>
                <div class="info-row">
                  <span class="label">退款老人</span>
                  <span class="value">{{ refund.elderName || '未知老人' }}</span>
                </div>
                <div class="info-row">
                  <span class="label">退款原因</span>
                  <span class="value">{{ refund.refundReason || '-' }}</span>
                </div>
                <div class="info-row amount">
                  <span class="label">退款总额</span>
                  <strong class="price"><small>¥</small>{{ formatAmount(refund.refundAmount) }}</strong>
                </div>
                <div
                  v-if="refund.serviceRefundAmount || refund.depositRefundAmount || refund.memberRefundAmount"
                  class="refund-detail"
                >
                  <span v-if="refund.serviceRefundAmount" class="detail-item">
                    服务费 ¥{{ formatAmount(refund.serviceRefundAmount) }}
                  </span>
                  <span v-if="refund.depositRefundAmount" class="detail-item">
                    押金 ¥{{ formatAmount(refund.depositRefundAmount) }}
                  </span>
                  <span v-if="refund.memberRefundAmount" class="detail-item">
                    会员费 ¥{{ formatAmount(refund.memberRefundAmount) }}
                  </span>
                </div>
                <div class="info-row time">
                  <span class="label">申请时间</span>
                  <time class="value">{{ refund.createTime || '-' }}</time>
                </div>
                <div
                  v-if="(refund.refundStatus === '2' || refund.refundStatus === 2) && refund.approveRemark"
                  class="reject-reason"
                >
                  <strong>驳回原因</strong>
                  <span>{{ refund.approveRemark }}</span>
                </div>
              </div>
            </div>
          </article>
        </div>

        <van-empty v-else description="暂无退款记录" image-size="104">
          <p class="empty-hint">退款申请提交后会在这里展示进度</p>
        </van-empty>
      </section>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { useUserStore } from '@/store/modules/user'
import { getOrderList, cancelOrder } from '@/api/order'
import { getRefundList } from '@/api/refund'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const ROUTE_TAB_ALIASES = Object.freeze({
  pending: 'pending',
  paid: '1',
  cancelled: '2',
  refund: '3'
})
const VALID_ORDER_TABS = new Set(['all', '1', '2', '3', '4', 'pending'])

const normalizeQueryValue = (value) => Array.isArray(value) ? value[0] : value

const resolveRouteTab = (query) => {
  for (const key of ['status', 'tab']) {
    const rawValue = normalizeQueryValue(query[key])
    if (rawValue === undefined || rawValue === null || rawValue === '') continue

    const value = String(rawValue)
    const mappedValue = ROUTE_TAB_ALIASES[value] || value
    if (VALID_ORDER_TABS.has(mappedValue)) return mappedValue
  }

  return 'all'
}

// 搜索关键词
const searchValue = ref('')

// Tab状态
const activeTab = ref(resolveRouteTab(route.query))
const loadedTab = ref(activeTab.value)
const isMounted = ref(false)

// 列表状态
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 订单列表
const orderList = ref([])

// 退款列表
const refundList = ref([])

// 分页参数
const pageNum = ref(1)
const pageSize = ref(10)


const loadTabIfChanged = (nextTab) => {
  if (nextTab === loadedTab.value) return

  loadedTab.value = nextTab
  resetList()
  onLoad()
}

// Tab切换
const onTabChange = (name) => {
  if (!isMounted.value) {
    loadedTab.value = name
    return
  }

  loadTabIfChanged(name)
}

// 搜索
const onSearch = () => {
  resetList()
  onLoad()
}

// 重置列表
const resetList = () => {
  orderList.value = []
  pageNum.value = 1
  finished.value = false
}

// 下拉刷新
const onRefresh = () => {
  resetList()
  onLoad()
  refreshing.value = false
}

// 加载订单列表 (根据当前登录用户查询订单)
const onLoad = async () => {
  // 如果是退款tab，加载退款列表
  if (activeTab.value === '3' || activeTab.value === 'refund') {
    await loadRefundList()
    return
  }

  try {
    loading.value = true

    // 构建请求参数 - 根据当前登录用户查询
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }

    // 如果选择了订单状态（非"全部"），添加到参数中
    // pending 表示待付款，包括状态 '0'（续费订单待支付）和 '5'（入驻订单审核通过待付款）
    if (activeTab.value !== 'all') {
      params.orderStatus = activeTab.value
    }

    // 如果有搜索关键词，添加到参数中
    if (searchValue.value) {
      params.searchValue = searchValue.value
    }

    // 调用真实API获取订单列表
    const response = await getOrderList(params)

    if (response.code === 200 && response.data) {
      const { rows = [], total = 0 } = response.data

      if (rows.length === 0) {
        finished.value = true
      } else {
        // 映射API返回的字段到前端所需的字段
        const mappedOrders = rows.map(order => ({
          orderId: order.orderId,
          orderNo: order.orderNo,
          orderType: order.orderType,
          orderTypeText: order.orderTypeText || getOrderTypeText(order.orderType),
          orderStatus: order.orderStatus,
          orderStatusText: order.orderStatusText || getStatusText(order.orderStatus),
          orderAmount: order.orderAmount,
          createTime: order.createTime,
          institutionName: order.institutionName || '养老机构',
          elderId: order.elderId
        }))

        orderList.value = [...orderList.value, ...mappedOrders]
        pageNum.value++

        // 判断是否还有更多数据
        if (rows.length < pageSize.value) {
          finished.value = true
        }
      }
    } else {
      showToast(response.msg || '获取订单列表失败')
      finished.value = true
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    showToast('获取订单列表失败，请稍后重试')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 加载退款列表
const loadRefundList = async () => {
  try {
    loading.value = true

    const response = await getRefundList()

    if (response.code === 200 && response.data) {
      refundList.value = response.data
      finished.value = true
    } else {
      showToast(response.msg || '获取退款列表失败')
      finished.value = true
    }
  } catch (error) {
    console.error('获取退款列表失败:', error)
    showToast('获取退款列表失败，请稍后重试')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 查看详情
const goToDetail = (orderId) => {
  router.push({
    name: 'OrderDetail',
    params: { id: orderId }
  })
}

// 取消订单
const handleCancel = async (order) => {
  try {
    await showConfirmDialog({
      title: '提示',
      message: '确定要取消该订单吗?'
    })

    showToast({
      message: '正在取消订单...',
      forbidClick: true,
      duration: 500
    })

    console.log('开始取消订单，订单ID:', order.orderId)
    console.log('订单信息:', JSON.stringify(order, null, 2))

    // 调用取消订单API
    const response = await cancelOrder(order.orderId)
    console.log('取消订单API响应:', response)

    if (response.code === 200) {
      showToast('取消成功')
      // 刷新列表
      resetList()
      onLoad()
    } else {
      console.error('取消订单失败:', response)
      showToast(response.msg || '取消失败')
    }
  } catch (error) {
    // 用户取消了对话框
    if (error !== 'cancel') {
      console.error('取消订单异常:', error)
      console.error('错误详情:', {
        message: error.message,
        response: error.response,
        request: error.request,
        config: error.config
      })

      // 提供更具体的错误信息
      let errorMessage = '取消失败，请重试'
      if (error.response) {
        // 服务器返回了错误状态码
        console.error('响应错误:', error.response.status, error.response.data)
        errorMessage = error.response.data?.msg || `服务器错误(${error.response.status})`
      } else if (error.request) {
        // 请求发出但没有收到响应
        console.error('网络请求失败:', error.request)
        errorMessage = '网络连接失败，请检查网络后重试'
      } else {
        // 其他错误
        console.error('请求配置错误:', error.config)
        errorMessage = error.message || '未知错误'
      }

      showToast(errorMessage)
    }
  }
}

// 立即付款
const handlePay = (order) => {
  if (!order || !order.orderId) {
    showToast('订单信息不存在')
    return
  }

  // 跳转到支付收银台页面
  router.push({
    path: `/payment/cashier/${order.orderId}`,
    query: {
      orderNo: order.orderNo,
      amount: order.orderAmount,
      elderName: order.elderName || '',
      institutionId: order.institutionId
    }
  })
}

// 续费按钮操作
const handleRenew = (order) => {
  if (!order || !order.elderId) {
    showToast('无法获取老人信息')
    return
  }

  // 跳转到续费页面
  router.push({
    path: '/order/renew',
    query: {
      elderId: order.elderId,
      orderId: order.orderId
    }
  })
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    '0': '待付款',
    '1': '已支付',
    '2': '已取消',
    '3': '已退款',
    '4': '待审核',
    '5': '待付款'
  }
  return statusMap[status] || '未知状态'
}

// 获取订单类型文本
const getOrderTypeText = (type) => {
  const typeMap = {
    '1': '入驻订单',
    '2': '续费订单'
  }
  return typeMap[type] || '其他订单'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toFixed(2)
}

watch(
  () => [route.query.status, route.query.tab],
  () => {
    const nextTab = resolveRouteTab(route.query)
    if (nextTab === activeTab.value) return

    activeTab.value = nextTab
    if (isMounted.value) {
      loadTabIfChanged(nextTab)
    } else {
      loadedTab.value = nextTab
    }
  }
)

// 页面加载时由 van-list 触发首屏订单请求；退款页没有 van-list，需要主动加载
onMounted(async () => {
  isMounted.value = true

  // 检查用户登录状态
  if (!userStore.isLoggedIn) {
    showToast('请先登录')
    return
  }

  if (activeTab.value === '3') {
    await onLoad()
  }
})
</script>

<style scoped>
.order-page {
  padding-bottom: var(--h5-space-6);
}

.order-page__top {
  position: sticky;
  top: 0;
  z-index: var(--h5-z-sticky);
  padding-top: var(--h5-safe-area-top);
  background: var(--h5-color-surface);
  border-bottom: 1px solid var(--h5-color-divider);
  box-shadow: var(--h5-shadow-xs);
}

.order-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 72px;
  padding: var(--h5-space-3) var(--h5-page-padding) var(--h5-space-2);
  background: linear-gradient(135deg, var(--h5-color-primary-50), var(--h5-color-surface) 70%);
}

.order-heading__eyebrow {
  margin-bottom: 2px;
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
}

.order-heading h1 {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-2xl);
  font-weight: var(--h5-font-weight-bold);
  line-height: var(--h5-line-height-tight);
}

.order-heading__icon {
  display: inline-flex;
  width: 40px;
  height: 40px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-primary);
  font-size: 23px;
  background: var(--h5-color-primary-soft);
  border: 1px solid var(--h5-color-primary-100);
  border-radius: var(--h5-radius-md);
}

.search-bar {
  padding: var(--h5-space-1) var(--h5-space-3) var(--h5-space-2);
}

.search-bar :deep(.van-search) {
  padding: 0;
  background: transparent;
}

.search-bar :deep(.van-search__content) {
  min-height: 44px;
  background: var(--h5-color-surface-subtle);
  border: 1px solid var(--h5-color-border);
}

.order-tabs :deep(.van-tabs__wrap) {
  height: 48px;
}

.order-tabs :deep(.van-tabs__nav) {
  padding: 0 var(--h5-space-2);
  background: var(--h5-color-surface);
}

.order-tabs :deep(.van-tab) {
  min-width: 64px;
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
}

.order-tabs :deep(.van-tab--active) {
  color: var(--h5-color-primary);
  font-weight: var(--h5-font-weight-semibold);
}

.order-tabs :deep(.van-tabs__line) {
  width: 24px;
  height: 3px;
  background: var(--h5-color-primary);
  border-radius: var(--h5-radius-pill);
}

.order-content {
  min-height: 460px;
}

.order-list,
.refund-list {
  display: grid;
  gap: var(--h5-space-3);
  padding: var(--h5-space-3) var(--h5-page-padding);
}

.order-card,
.refund-card {
  border-radius: var(--h5-radius-lg);
}

.order-card__main {
  display: block;
  width: 100%;
  color: inherit;
  text-align: left;
  background: transparent;
  border: 0;
  cursor: pointer;
}

.order-card__main:focus-visible {
  outline: none;
  box-shadow: inset var(--h5-shadow-focus);
}

.order-card__header,
.refund-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-3);
  padding: var(--h5-space-4);
  border-bottom: 1px solid var(--h5-color-divider);
}

.institution-name {
  display: flex;
  min-width: 0;
  align-items: center;
  gap: var(--h5-space-2);
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
  line-height: var(--h5-line-height-normal);
}

.institution-name > span:last-child {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.institution-name__icon,
.refund-no__icon {
  display: inline-flex;
  flex: 0 0 32px;
  width: 32px;
  height: 32px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-primary);
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-sm);
}

.order-status,
.refund-status {
  display: inline-flex;
  flex: 0 0 auto;
  min-height: 26px;
  align-items: center;
  padding: 3px var(--h5-space-2);
  color: var(--h5-color-info);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-semibold);
  line-height: 20px;
  background: var(--h5-color-info-soft);
  border-radius: var(--h5-radius-pill);
}

.status-0,
.status-5 {
  color: var(--h5-color-pending);
  background: var(--h5-color-pending-soft);
}

.status-1 {
  color: var(--h5-color-success);
  background: var(--h5-color-success-soft);
}

.status-2 {
  color: var(--h5-color-text-secondary);
  background: var(--h5-color-surface-subtle);
}

.refund-status.status-2 {
  color: var(--h5-color-danger);
  background: var(--h5-color-danger-soft);
}

.status-3 {
  color: var(--h5-color-danger);
  background: var(--h5-color-danger-soft);
}

.status-4 {
  color: var(--h5-color-info);
  background: var(--h5-color-info-soft);
}

.order-card__body {
  padding: var(--h5-space-4);
}

.order-meta {
  display: grid;
  gap: var(--h5-space-2);
}

.meta-row,
.refund-info .info-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-4);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
  line-height: var(--h5-line-height-normal);
}

.meta-label,
.refund-info .label {
  flex: 0 0 auto;
  color: var(--h5-color-text-tertiary);
}

.meta-value,
.refund-info .value {
  min-width: 0;
  color: var(--h5-color-text-secondary);
  text-align: right;
  overflow-wrap: anywhere;
}

.meta-value--number {
  font-variant-numeric: tabular-nums;
}

.renew-tag {
  display: inline-flex;
  margin-left: var(--h5-space-1);
  padding: 1px 6px;
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-sm);
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-pill);
}

.amount-summary {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: var(--h5-space-3);
  margin-top: var(--h5-space-3);
  padding-top: var(--h5-space-3);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
  border-top: 1px dashed var(--h5-color-border);
}

.amount-summary strong,
.refund-info .price {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-xl);
  font-weight: var(--h5-font-weight-bold);
  font-variant-numeric: tabular-nums;
}

.amount-summary small,
.refund-info .price small {
  margin-right: 2px;
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-sm);
}

.detail-cue {
  display: flex;
  min-height: 40px;
  align-items: center;
  justify-content: flex-end;
  gap: var(--h5-space-1);
  padding: 0 var(--h5-space-4);
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  border-top: 1px solid var(--h5-color-divider);
}

.order-card__actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: var(--h5-space-2);
  padding: var(--h5-space-3) var(--h5-space-4);
  background: var(--h5-color-surface-subtle);
  border-top: 1px solid var(--h5-color-divider);
}

.order-card__actions :deep(.van-button) {
  min-width: 104px;
  min-height: 42px;
  padding: 0 var(--h5-space-4);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
  border-radius: var(--h5-radius-md);
}

.refund-list-container {
  min-height: 360px;
}

.state-loading {
  display: flex;
  min-height: 240px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-md);
}

.refund-no {
  display: flex;
  min-width: 0;
  align-items: center;
  gap: var(--h5-space-2);
}

.refund-no > div {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 2px;
}

.refund-no__label {
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.refund-no strong {
  overflow: hidden;
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.refund-body {
  padding: var(--h5-space-4);
}

.refund-info {
  display: grid;
  gap: var(--h5-space-2);
}

.refund-info .info-row.amount {
  align-items: baseline;
  margin-top: var(--h5-space-1);
  padding-top: var(--h5-space-3);
  border-top: 1px dashed var(--h5-color-border);
}

.refund-info .info-row.time {
  margin-top: var(--h5-space-1);
  font-size: var(--h5-font-size-sm);
}

.refund-detail {
  display: flex;
  flex-wrap: wrap;
  gap: var(--h5-space-2);
  padding: var(--h5-space-2) 0;
}

.refund-detail .detail-item {
  padding: var(--h5-space-1) var(--h5-space-2);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  background: var(--h5-color-surface-subtle);
  border: 1px solid var(--h5-color-divider);
  border-radius: var(--h5-radius-sm);
}

.reject-reason {
  display: grid;
  gap: var(--h5-space-1);
  margin-top: var(--h5-space-1);
  padding: var(--h5-space-3);
  color: var(--h5-color-danger);
  font-size: var(--h5-font-size-sm);
  line-height: var(--h5-line-height-normal);
  background: var(--h5-color-danger-soft);
  border: 1px solid rgba(197, 61, 70, 0.18);
  border-radius: var(--h5-radius-md);
}

.order-empty,
.refund-list-container :deep(.van-empty) {
  min-height: 320px;
}

.empty-hint {
  margin-top: var(--h5-space-2);
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

@media (max-width: 359px) {
  .order-card__actions {
    align-items: stretch;
    flex-direction: column;
  }

  .order-card__actions :deep(.van-button) {
    width: 100%;
  }
}
</style>
