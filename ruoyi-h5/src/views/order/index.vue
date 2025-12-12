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
      <van-tab title="待付款" name="0" />
      <van-tab title="已支付" name="1" />
      <van-tab title="已取消" name="2" />
      <van-tab title="退款" name="3" />
    </van-tabs>

    <!-- 订单列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
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
                  <span class="value">{{ getOrderTypeText(order.orderType) }}</span>
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
                v-if="order.orderStatus === '0'"
                size="small"
                plain
                @click.stop="handleCancel(order)"
              >
                取消订单
              </van-button>
              <van-button
                v-if="order.orderStatus === '0'"
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
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { useUserStore } from '@/store/modules/user'
import { getOrderList, cancelOrder } from '@/api/order'
import dayjs from 'dayjs'

const router = useRouter()
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

// 加载订单列表 (使用真实API，强制使用当前用户身份)
const onLoad = async () => {
  try {
    loading.value = true

    // 获取当前用户的老人信息
    const currentElder = userStore.currentElder
    const userElders = userStore.elders || []

    if (!currentElder && userElders.length === 0) {
      showToast('请先关联老人信息')
      finished.value = true
      return
    }

    // 构建请求参数 - 强制使用当前用户的老人信息
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }

    // 使用userStore中的当前老人ID，忽略前端可能修改的参数
    if (currentElder && currentElder.elderId) {
      params.elderId = currentElder.elderId
      elderId.value = currentElder.elderId
    }

    // 如果选择了订单状态（非"全部"），添加到参数中
    if (activeTab.value !== 'all') {
      params.orderStatus = activeTab.value
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

// 查看详情
const goToDetail = (orderId) => {
  router.push({
    name: 'OrderDetail',
    params: { id: orderId }
  })
}

// 取消订单 (使用模拟数据)
const handleCancel = async (order) => {
  try {
    await showConfirmDialog({
      title: '提示',
      message: '确定要取消该订单吗?'
    })

    // 模拟取消成功
    await new Promise(resolve => setTimeout(resolve, 300))

    // 更新模拟数据中的订单状态
    const targetOrder = mockOrders.find(o => o.orderId === order.orderId)
    if (targetOrder) {
      targetOrder.orderStatus = '2' // 2-已取消
    }

    showToast('取消成功')
    // 刷新列表
    resetList()
    onLoad()
  } catch (error) {
    // 用户取消了对话框
    if (error !== 'cancel') {
      showToast('取消失败')
    }
  }
}

// 立即付款
const handlePay = (order) => {
  showToast('支付功能开发中')
  // TODO: 跳转到支付页面
  // router.push({
  //   name: 'OrderPay',
  //   params: { id: order.orderId }
  // })
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    '0': '待付款',
    '1': '已支付',
    '2': '已取消',
    '3': '退款中'
  }
  return statusMap[status] || '未知状态'
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

  // 如果userStore中没有老人信息，尝试获取
  if (!userStore.currentElder && userStore.elders.length === 0) {
    try {
      await userStore.fetchElders()
    } catch (error) {
      console.error('获取老人列表失败:', error)
    }
  }

  // 使用userStore中的老人信息，而不是路由参数
  const currentElder = userStore.currentElder
  if (currentElder && currentElder.elderId) {
    elderId.value = currentElder.elderId
  }

  // 加载订单列表
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
</style>
