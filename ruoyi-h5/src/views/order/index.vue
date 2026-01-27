<template>
  <div class="order-page">
    <van-nav-bar title="我的订单" left-arrow @click-left="handleBack" fixed placeholder />

    <!-- 搜索栏 -->
    <div class="search-bar">
      <van-search
        v-model="searchValue"
        placeholder="搜索我的订单"
        @search="onSearch"
      />
    </div>

    <!-- 订单状态Tab -->
    <van-tabs v-model:active="activeTab" @change="onTabChange" sticky>
      <van-tab title="全部" name="all" />
      <van-tab title="待审核" name="4" />
      <van-tab title="待付款" name="pending" />
      <van-tab title="已支付" name="1" />
      <van-tab title="已取消" name="2" />
      <van-tab title="退款" name="3" />
    </van-tabs>

    <!-- 订单列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <!-- 订单列表 (非退款tab) -->
      <van-list
        v-if="activeTab !== '3'"
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <div v-if="orderList.length > 0">
          <div
            v-for="order in orderList"
            :key="order.orderId"
            class="order-card"
            @click="goToDetail(order.orderId)"
          >
            <!-- 订单头部 -->
            <div class="order-header">
              <div class="institution-name">
                <van-icon name="shop-o" />
                {{ order.institutionName || '郑州XXXXX养老院' }}
              </div>
              <div :class="['order-status', `status-${order.orderStatus}`]">
                {{ getStatusText(order.orderStatus) }}
              </div>
            </div>

            <!-- 订单信息 -->
            <div class="order-body">
              <div class="order-info">
                <div class="info-row">
                  <span class="label">订单号:</span>
                  <span class="value">{{ order.orderNo }}</span>
                </div>
                <div class="info-row">
                  <span class="label">订单类型:</span>
                  <span class="value">
                    {{ getOrderTypeText(order.orderType) }}
                    <van-tag v-if="order.orderType === '2'" type="primary" size="mini" style="margin-left: 6px;">
                      续费订单
                    </van-tag>
                  </span>
                </div>
                <div class="info-row">
                  <span class="label">下单时间:</span>
                  <span class="value">{{ formatDate(order.createTime) }}</span>
                </div>
                <div class="info-row amount">
                  <span class="label">应付金额:</span>
                  <span class="value price">¥ {{ formatAmount(order.orderAmount) }}</span>
                </div>
              </div>
            </div>

            <!-- 订单操作 -->
            <div class="order-footer">
              <van-button
                v-if="order.orderStatus === '4'"
                size="small"
                plain
                @click.stop="handleCancel(order)"
              >
                取消订单
              </van-button>
              <van-button
                v-if="order.orderStatus === '0' || order.orderStatus === '5'"
                size="small"
                type="primary"
                @click.stop="handlePay(order)"
              >
                立即付款
              </van-button>
              <van-button
                v-if="order.orderStatus === '1'"
                size="small"
                @click.stop="goToDetail(order.orderId)"
              >
                查看详情
              </van-button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <van-empty
          v-else
          description="暂无订单"
        />
      </van-list>

      <!-- 退款列表 (退款tab) -->
      <div v-if="activeTab === '3'" class="refund-list-container">
        <van-loading v-if="loading" type="spinner" color="#667eea" />
        <div v-else-if="refundList.length > 0">
          <div
            v-for="refund in refundList"
            :key="refund.id"
            class="refund-card"
          >
            <!-- 退款头部 -->
            <div class="refund-header">
              <div class="refund-no">
                <van-icon name="bill-o" />
                {{ refund.refundNo }}
              </div>
              <div :class="['refund-status', `status-${refund.refundStatus}`]">
                {{ refund.statusText }}
              </div>
            </div>

            <!-- 退款信息 -->
            <div class="refund-body">
              <div class="refund-info">
                <div class="info-row">
                  <span class="label">养老机构:</span>
                  <span class="value">{{ refund.institutionName || '养老机构' }}</span>
                </div>
                <div class="info-row">
                  <span class="label">退款老人:</span>
                  <span class="value">{{ refund.elderName || '未知老人' }}</span>
                </div>
                <div class="info-row">
                  <span class="label">退款原因:</span>
                  <span class="value">{{ refund.refundReason || '-' }}</span>
                </div>
                <div class="info-row amount">
                  <span class="label">退款总额:</span>
                  <span class="value price">¥ {{ formatAmount(refund.refundAmount) }}</span>
                </div>
                <div class="refund-detail" v-if="refund.serviceRefundAmount || refund.depositRefundAmount || refund.memberRefundAmount">
                  <span class="detail-item" v-if="refund.serviceRefundAmount">
                    服务费: ¥{{ formatAmount(refund.serviceRefundAmount) }}
                  </span>
                  <span class="detail-item" v-if="refund.depositRefundAmount">
                    押金: ¥{{ formatAmount(refund.depositRefundAmount) }}
                  </span>
                  <span class="detail-item" v-if="refund.memberRefundAmount">
                    会员费: ¥{{ formatAmount(refund.memberRefundAmount) }}
                  </span>
                </div>
                <div class="info-row time">
                  <span class="label">申请时间:</span>
                  <span class="value">{{ refund.createTime || '-' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 退款空状态 -->
        <van-empty
          v-else-if="!loading"
          description="暂无退款记录"
        />
      </div>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { useUserStore } from '@/store/modules/user'
import { getOrderList, cancelOrder, processPayment } from '@/api/order'
import { getRefundList } from '@/api/refund'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 搜索关键词
const searchValue = ref('')

// Tab状态
const activeTab = ref('all')

// 列表状态
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 订单列表
const orderList = ref([])

// 退款列���
const refundList = ref([])

// 分页参数
const pageNum = ref(1)
const pageSize = ref(10)

// 老人ID（后续需要从登录信息或路由参数中获取）
const elderId = ref(null)

// 模拟订单数据（备用）
const mockOrders = [
  {
    orderId: 1,
    orderNo: 'ORD202501140001',
    institutionName: '郑州市金水区花园口社区养老服务中心',
    orderType: '1',
    orderStatus: '0',
    orderAmount: 2500.00,
    createTime: '2025-01-14 10:30:00'
  },
  {
    orderId: 2,
    orderNo: 'ORD202501130002',
    institutionName: '郑州颐养家园养老院',
    orderType: '2',
    orderStatus: '1',
    orderAmount: 3200.00,
    createTime: '2025-01-13 14:20:00'
  },
  {
    orderId: 3,
    orderNo: 'ORD202501120003',
    institutionName: '河南省老干部康养中心',
    orderType: '3',
    orderStatus: '1',
    orderAmount: 1200.00,
    createTime: '2025-01-12 09:15:00'
  },
  {
    orderId: 4,
    orderNo: 'ORD202501110004',
    institutionName: '郑州夕阳红托老所',
    orderType: '1',
    orderStatus: '2',
    orderAmount: 1800.00,
    createTime: '2025-01-11 16:45:00'
  },
  {
    orderId: 5,
    orderNo: 'ORD202501100005',
    institutionName: '郑东新区康乐养老中心',
    orderType: '2',
    orderStatus: '1',
    orderAmount: 2800.00,
    createTime: '2025-01-10 11:30:00'
  },
  {
    orderId: 6,
    orderNo: 'ORD202501090006',
    institutionName: '郑州爱心护理院',
    orderType: '4',
    orderStatus: '3',
    orderAmount: 1500.00,
    createTime: '2025-01-09 13:20:00'
  },
  {
    orderId: 7,
    orderNo: 'ORD202501080007',
    institutionName: '高新区幸福之家养老服务站',
    orderType: '1',
    orderStatus: '1',
    orderAmount: 2200.00,
    createTime: '2025-01-08 10:10:00'
  },
  {
    orderId: 8,
    orderNo: 'ORD202501070008',
    institutionName: '郑州安康老年公寓',
    orderType: '3',
    orderStatus: '1',
    orderAmount: 950.00,
    createTime: '2025-01-07 15:30:00'
  },
  {
    orderId: 9,
    orderNo: 'ORD202501060009',
    institutionName: '郑州市金水区花园口社区养老服务中心',
    orderType: '2',
    orderStatus: '0',
    orderAmount: 3000.00,
    createTime: '2025-01-06 09:45:00'
  },
  {
    orderId: 10,
    orderNo: 'ORD202501050010',
    institutionName: '郑州颐养家园养老院',
    orderType: '5',
    orderStatus: '1',
    orderAmount: 800.00,
    createTime: '2025-01-05 14:00:00'
  }
]

// Tab切换
const onTabChange = () => {
  resetList()
  onLoad()
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
          institutionName: order.institutionName || '养老机构'
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
      console.error('错误���情:', {
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
        errorMessage = error.response.data?.msg || `��务器错误(${error.response.status})`
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

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    '0': '待付款',
    '1': '已支付',
    '2': '已取消',
    '3': '退款中',
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

// 返回上一页
const handleBack = () => {
  router.back()
}

// 页面加载时获取订单列表
onMounted(async () => {
  // 检查用户登录状态
  if (!userStore.isLoggedIn) {
    showToast('请��登录')
    return
  }

  // 直接加载订单列表 - 根据当前登录用户查询订单，不需要关联老人信息
  await onLoad()
})
</script>

<style scoped>
.order-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 50px;
}

.search-bar {
  background-color: #fff;
  padding: 8px 0;
}

.order-card {
  background-color: #fff;
  margin: 12px;
  border-radius: 8px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f5f5f5;
}

.institution-name {
  font-size: 15px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.order-status {
  font-size: 14px;
  font-weight: 500;
}

.status-0 {
  color: #ff976a;
}

.status-1 {
  color: #07c160;
}

.status-2 {
  color: #969799;
}

.status-3 {
  color: #ee0a24;
}

.order-body {
  padding: 12px 16px;
}

.order-info .info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.order-info .info-row:last-child {
  margin-bottom: 0;
}

.order-info .label {
  color: #969799;
}

.order-info .value {
  color: #323233;
}

.order-info .amount {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed #ebedf0;
}

.order-info .price {
  color: #ee0a24;
  font-size: 18px;
  font-weight: 500;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #f5f5f5;
}

/* 退款列表 */
.refund-list-container {
  min-height: 300px;
}

.refund-card {
  background-color: #fff;
  margin: 12px;
  border-radius: 8px;
  overflow: hidden;
}

.refund-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f5f5f5;
}

.refund-no {
  font-size: 15px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #667eea;
}

.refund-status {
  font-size: 14px;
  font-weight: 500;
}

.refund-status.status-0 {
  color: #ff976a;
}

.refund-status.status-1 {
  color: #07c160;
}

.refund-status.status-2 {
  color: #ee0a24;
}

.refund-body {
  padding: 12px 16px;
}

.refund-info .info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.refund-info .info-row:last-child {
  margin-bottom: 0;
}

.refund-info .info-row.amount {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed #ebedf0;
}

.refund-info .info-row.time {
  margin-top: 4px;
  font-size: 12px;
}

.refund-info .label {
  color: #969799;
}

.refund-info .value {
  color: #323233;
}

.refund-info .price {
  color: #ee0a24;
  font-size: 18px;
  font-weight: 500;
}

.refund-detail {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
  padding: 8px 0;
}

.refund-detail .detail-item {
  background-color: #f5f5f5;
  color: #666;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}
</style>
