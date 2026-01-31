<template>
  <div class="refund-list-page">

    <!-- Tab切换 -->
    <van-tabs v-model:active="activeTab" @change="onTabChange" sticky>
      <van-tab title="全部" name="all" />
      <van-tab title="待审核" name="pending" />
      <van-tab title="已通过" name="approved" />
      <van-tab title="已拒绝" name="rejected" />
    </van-tabs>

    <!-- 退款列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <div v-if="refundList.length > 0" class="refund-list">
          <div
            v-for="item in refundList"
            :key="item.id"
            class="refund-card"
            @click="goToDetail(item.id)"
          >
            <div class="refund-header">
              <span class="order-no">退款单号: {{ item.refundNo }}</span>
              <van-tag :type="getStatusType(item.status)" size="medium">
                {{ getStatusText(item.status) }}
              </van-tag>
            </div>

            <div class="refund-body">
              <div class="info-row">
                <span class="label">退款金额:</span>
                <span class="value amount">¥{{ item.totalAmount }}</span>
              </div>
              <div class="info-row">
                <span class="label">申请时间:</span>
                <span class="value">{{ item.applyTime }}</span>
              </div>
              <div class="info-row">
                <span class="label">退款原因:</span>
                <span class="value">{{ item.refundReason }}</span>
              </div>
            </div>

            <div class="refund-footer">
              <van-button size="small" @click.stop="goToDetail(item.id)">
                查看详情
              </van-button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <van-empty v-else description="暂无退款记录" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Tab状态
const activeTab = ref('all')

// 列表状态
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 退款列表
const refundList = ref([])

// 模拟数据
const mockRefundList = [
  {
    id: 1,
    refundNo: 'RF202501150001',
    totalAmount: 3000,
    applyTime: '2025-01-15 10:00:00',
    refundReason: '服务质量不满意',
    status: 'pending'
  },
  {
    id: 2,
    refundNo: 'RF202501140002',
    totalAmount: 2500,
    applyTime: '2025-01-14 15:30:00',
    refundReason: '家属要求',
    status: 'approved'
  }
]

// Tab切换
const onTabChange = () => {
  resetList()
  onLoad()
}

// 重置列表
const resetList = () => {
  refundList.value = []
  finished.value = false
}

// 下拉刷新
const onRefresh = () => {
  resetList()
  onLoad()
  refreshing.value = false
}

// 加载列表
const onLoad = async () => {
  try {
    loading.value = true

    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    // 筛选数据
    let filteredList = [...mockRefundList]
    if (activeTab.value !== 'all') {
      filteredList = filteredList.filter(item => item.status === activeTab.value)
    }

    refundList.value = filteredList
    finished.value = true
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

// 跳转详情
const goToDetail = (id) => {
  router.push(`/user/expense/refund/review/${id}`)
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return textMap[status] || '未知'
}
</script>

<style scoped>
.refund-list-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.refund-list {
  padding: 12px;
}

.refund-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.refund-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-size: 13px;
  color: #999;
}

.refund-body {
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  color: #999;
}

.value {
  color: #333;
}

.value.amount {
  font-size: 18px;
  font-weight: bold;
  color: #ee0a24;
}

.refund-footer {
  display: flex;
  justify-content: flex-end;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}
</style>
