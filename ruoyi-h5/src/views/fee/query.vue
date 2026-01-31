<template>
  <div class="fee-page">

    <!-- 筛选栏 -->
    <div class="filter-bar" v-if="elderOptions.length > 0">
      <van-dropdown-menu>
        <van-dropdown-item v-model="selectedElder" :options="elderOptions" title="选择老人" />
      </van-dropdown-menu>
    </div>

    <!-- 费用统计卡片 -->
    <div class="summary-card">
      <div class="summary-item">
        <div class="summary-label">净额</div>
        <div class="summary-value primary">¥{{ totalAmount }}</div>
      </div>
      <div class="summary-item">
        <div class="summary-label">总收入</div>
        <div class="summary-value success">¥{{ paidAmount }}</div>
      </div>
      <div class="summary-item">
        <div class="summary-label">总支出</div>
        <div class="summary-value danger">¥{{ unpaidAmount }}</div>
      </div>
    </div>

    <!-- 费用明细列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <div v-if="feeList.length > 0">
          <div
            v-for="fee in feeList"
            :key="fee.recordId"
            class="fee-card"
            @click="goToDetail(fee)"
          >
            <div class="fee-header">
              <div class="fee-type">
                <van-icon name="bill-o" />
                <span>{{ getFeeTypeText(fee.expenseType) }}</span>
              </div>
              <van-tag :type="getStatusType(fee.transactionType)" size="small">
                {{ getStatusText(fee.transactionType) }}
              </van-tag>
            </div>

            <div class="fee-content">
              <div class="fee-row">
                <span class="label">费用说明:</span>
                <span class="value">{{ fee.description || '-' }}</span>
              </div>
              <div class="fee-row">
                <span class="label">交易时间:</span>
                <span class="value">{{ formatDate(fee.createTime) }}</span>
              </div>
              <div class="fee-row" v-if="fee.balanceBefore !== null && fee.balanceBefore !== undefined">
                <span class="label">交易前余额:</span>
                <span class="value">¥{{ fee.balanceBefore }}</span>
              </div>
              <div class="fee-row" v-if="fee.balanceAfter !== null && fee.balanceAfter !== undefined">
                <span class="label">交易后余额:</span>
                <span class="value">¥{{ fee.balanceAfter }}</span>
              </div>
            </div>

            <div class="fee-footer">
              <div class="fee-amount">
                <span class="amount-label">金额:</span>
                <span class="amount-value" :class="fee.transactionType === 'income' ? 'income' : 'expense'">
                  {{ fee.transactionType === 'income' ? '+' : '-' }}¥{{ fee.amount }}
                </span>
              </div>
              <van-button
                v-if="fee.relatedType === 'order' && fee.relatedId"
                size="small"
                type="primary"
                @click.stop="handlePay(fee)"
              >
                查看订单
              </van-button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <van-empty v-else description="暂无费用记录" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import dayjs from 'dayjs'
import { getElderList, getExpenseList, getExpenseStatistics } from '@/api/expense'

const router = useRouter()

// 筛选条件
const selectedElder = ref(null)
const selectedYear = ref(new Date().getFullYear().toString())
const selectedMonth = ref((new Date().getMonth() + 1).toString().padStart(2, '0'))

// 列表状态
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 费用列表
const feeList = ref([])

// 分页参数
const pageNum = ref(1)
const pageSize = ref(10)

// 老人选项
const elderOptions = ref([])

// 费用统计
const statistics = ref({
  totalAmount: 0,
  paidAmount: 0,
  unpaidAmount: 0
})

// 年份选项
const yearOptions = [
  { text: '2025年', value: '2025' },
  { text: '2024年', value: '2024' },
  { text: '2023年', value: '2023' }
]

// 月份选项
const monthOptions = [
  { text: '全部', value: '00' },
  { text: '1月', value: '01' },
  { text: '2月', value: '02' },
  { text: '3月', value: '03' },
  { text: '4月', value: '04' },
  { text: '5月', value: '05' },
  { text: '6月', value: '06' },
  { text: '7月', value: '07' },
  { text: '8月', value: '08' },
  { text: '9月', value: '09' },
  { text: '10月', value: '10' },
  { text: '11月', value: '11' },
  { text: '12月', value: '12' }
]

// 计算属性 - 从统计数据获取
const totalAmount = computed(() => {
  const total = statistics.value.netAmount || 0
  return Number(total).toFixed(2)
})

const paidAmount = computed(() => {
  const income = statistics.value.totalIncome || 0
  return Number(income).toFixed(2)
})

const unpaidAmount = computed(() => {
  const expense = statistics.value.totalExpense || 0
  return Number(expense).toFixed(2)
})

// 返回上一页
const onBack = () => {
  router.back()
}

// 重置列表
const resetList = () => {
  feeList.value = []
  pageNum.value = 1
  finished.value = false
}

// 下拉刷新
const onRefresh = () => {
  resetList()
  loadStatistics()
  onLoad()
  refreshing.value = false
}

// 加载老人列表
const loadElderList = async () => {
  try {
    const response = await getElderList()
    if (response.code === 200 && response.data) {
      const elders = response.data
      if (elders.length > 0) {
        elderOptions.value = elders.map(elder => ({
          text: elder.elderName,
          value: elder.elderId
        }))
        // 默认选中第一个老人
        if (!selectedElder.value) {
          selectedElder.value = elders[0].elderId
        }
      } else {
        elderOptions.value = []
        showToast('暂无关联的老人信息')
      }
    } else {
      showToast(response.msg || '获取老人列表失败')
    }
  } catch (error) {
    console.error('获取老人列表失败:', error)
    showToast('获取老人列表失败')
  }
}

// 加载费用统计
const loadStatistics = async () => {
  if (!selectedElder.value) return

  try {
    const response = await getExpenseStatistics(selectedElder.value)
    if (response.code === 200 && response.data) {
      statistics.value = response.data
    }
  } catch (error) {
    console.error('获取费用统计失败:', error)
  }
}

// 加载费用列表
const onLoad = async () => {
  if (!selectedElder.value) {
    finished.value = true
    return
  }

  try {
    loading.value = true

    const response = await getExpenseList(
      selectedElder.value,
      'all', // 全部费用类型
      'all', // 全部交易类型
      pageNum.value,
      pageSize.value
    )

    if (response.code === 200 && response.data) {
      // 后端返回结构: { total, pageNum, pageSize, pages, list }
      const list = response.data.list || []

      if (list.length === 0) {
        finished.value = true
      } else {
        feeList.value = [...feeList.value, ...list]
        pageNum.value++

        if (list.length < pageSize.value) {
          finished.value = true
        }
      }
    } else {
      showToast(response.msg || '获取费用列表失败')
      finished.value = true
    }
  } catch (error) {
    console.error('获取费用列表失败:', error)
    showToast('获取费用列表失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 查看详情
const goToDetail = (fee) => {
  // 如果是订单类型，跳转到订单详情
  if (fee.relatedType === 'order' && fee.relatedId) {
    router.push(`/order/detail/${fee.relatedId}`)
  } else {
    showToast('该费用暂无详情页面')
  }
}

// 立即缴费
const handlePay = (fee) => {
  // 如果是订单类型，跳转到订单详情进行支付
  if (fee.relatedType === 'order' && fee.relatedId) {
    router.push(`/order/detail/${fee.relatedId}`)
  } else {
    showToast('该费用暂不支持在线支付')
  }
}

// 监听老人选择变化
watch(selectedElder, () => {
  resetList()
  loadStatistics()
  onLoad()
})

// 初始化加载
loadElderList()

// 获取费用类型文本
const getFeeTypeText = (type) => {
  const typeMap = {
    'deposit': '押金',
    'service': '服务费',
    'member': '会员费',
    'other': '其他'
  }
  return typeMap[type] || '其他'
}

// 获取状态文本
const getStatusText = (transactionType) => {
  const typeMap = {
    'income': '收入',
    'expense': '支出'
  }
  return typeMap[transactionType] || '未知'
}

// 获取状态类型
const getStatusType = (transactionType) => {
  const typeMap = {
    'income': 'success',
    'expense': 'warning'
  }
  return typeMap[transactionType] || 'default'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}
</script>

<style scoped>
.fee-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.filter-bar {
  background-color: #fff;
  margin-bottom: 12px;
}

.summary-card {
  display: flex;
  background-color: #fff;
  margin: 0 12px 12px;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.summary-item {
  flex: 1;
  text-align: center;
}

.summary-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 20px;
  font-weight: bold;
}

.summary-value.primary {
  color: #1989fa;
}

.summary-value.success {
  color: #07c160;
}

.summary-value.danger {
  color: #ee0a24;
}

.fee-card {
  background-color: #fff;
  margin: 0 12px 12px;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.fee-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.fee-type {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.fee-content {
  margin-bottom: 12px;
}

.fee-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.fee-row:last-child {
  margin-bottom: 0;
}

.fee-row .label {
  color: #999;
}

.fee-row .value {
  color: #333;
  flex: 1;
  text-align: right;
  margin-left: 12px;
}

.fee-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.fee-amount {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.amount-label {
  font-size: 13px;
  color: #999;
}

.amount-value {
  font-size: 20px;
  font-weight: bold;
  color: #ee0a24;
}

.amount-value.income {
  color: #07c160;
}

.amount-value.expense {
  color: #ee0a24;
}
</style>
