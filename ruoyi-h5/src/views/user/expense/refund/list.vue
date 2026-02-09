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
                <span class="value amount">¥{{ formatAmount(item.totalAmount) }}</span>
              </div>
              <div class="info-row">
                <span class="label">申请时间:</span>
                <span class="value">{{ item.applyTime }}</span>
              </div>
              <div class="info-row">
                <span class="label">退款原因:</span>
                <span class="value">{{ item.refundReason }}</span>
              </div>
              <!-- 驳回原因 -->
              <div v-if="item.status === '2' || item.status === 'rejected'" class="info-row reject-reason">
                <span class="label">驳回原因:</span>
                <span class="value reject-text">{{ item.approveRemark || '未填写驳回原因' }}</span>
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
import { showToast } from 'vant'
import { getRefundList } from '@/api/refund'

const router = useRouter()

// Tab状态
const activeTab = ref('all')

// 列表状态
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 退款列表
const refundList = ref([])

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

    // 调用真实API获取退款列表
    const response = await getRefundList()

    if (response.code === 200 && response.data) {
      let filteredList = response.data

      // 根据 tab 筛选状态
      if (activeTab.value !== 'all') {
        // 后端返回的状态是 '0', '1', '2'，需要转换为前端的状态
        const statusMap = {
          '0': 'pending',
          '1': 'approved',
          '2': 'rejected'
        }
        const targetStatus = activeTab.value
        filteredList = filteredList.filter(item => {
          const itemStatus = statusMap[item.refundStatus] || item.refundStatus
          return itemStatus === targetStatus
        })
      }

      // 转换数据格式
      refundList.value = filteredList.map(item => {
        const statusMap = {
          '0': 'pending',
          '1': 'approved',
          '2': 'rejected'
        }
        return {
          id: item.id,
          refundNo: item.refundNo,
          totalAmount: item.refundAmount || item.totalAmount || 0,
          applyTime: item.createTime || item.applyTime,
          refundReason: item.refundReason,
          status: statusMap[item.refundStatus] || item.refundStatus || item.status,
          // 驳回原因
          approveRemark: item.approveRemark || '',
          // 审批人
          approver: item.approver || '',
          // 审批时间
          approveTime: item.approveTime || ''
        }
      })

      finished.value = true
    } else {
      showToast(response.msg || '获取退款列表失败')
      finished.value = true
    }
  } catch (error) {
    console.error('加载退款列表失败:', error)
    showToast('加载失败，请稍后重试')
    finished.value = true
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
    '0': 'warning',
    '1': 'success',
    '2': 'danger',
    pending: 'warning',
    approved: 'success',
    rejected: 'danger'
  }
  return typeMap[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    '0': '待审核',
    '1': '已通过',
    '2': '已拒绝',
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝'
  }
  return textMap[status] || '未知'
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toFixed(2)
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

/* 驳回原因样式 */
.reject-reason {
  padding: 8px 0;
  margin-top: 4px;
  background-color: #fff5f5;
  border-radius: 4px;
}

.reject-text {
  color: #ee0a24;
  flex: 1;
  text-align: right;
  word-break: break-all;
}
</style>
