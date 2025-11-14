<template>
  <div class="fee-page">
    <van-nav-bar title="费用查询" fixed placeholder left-arrow @click-left="onBack" />

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <van-dropdown-menu>
        <van-dropdown-item v-model="selectedElder" :options="elderOptions" title="选择老人" />
        <van-dropdown-item v-model="selectedYear" :options="yearOptions" title="年份" />
        <van-dropdown-item v-model="selectedMonth" :options="monthOptions" title="月份" />
      </van-dropdown-menu>
    </div>

    <!-- 费用统计卡片 -->
    <div class="summary-card">
      <div class="summary-item">
        <div class="summary-label">本月应缴</div>
        <div class="summary-value primary">¥{{ totalAmount }}</div>
      </div>
      <div class="summary-item">
        <div class="summary-label">已缴费用</div>
        <div class="summary-value success">¥{{ paidAmount }}</div>
      </div>
      <div class="summary-item">
        <div class="summary-label">待缴费用</div>
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
            :key="fee.feeId"
            class="fee-card"
            @click="goToDetail(fee.feeId)"
          >
            <div class="fee-header">
              <div class="fee-type">
                <van-icon name="bill-o" />
                <span>{{ getFeeTypeText(fee.feeType) }}</span>
              </div>
              <van-tag :type="getStatusType(fee.status)" size="small">
                {{ getStatusText(fee.status) }}
              </van-tag>
            </div>

            <div class="fee-content">
              <div class="fee-row">
                <span class="label">账单月份:</span>
                <span class="value">{{ fee.billMonth }}</span>
              </div>
              <div class="fee-row">
                <span class="label">费用明细:</span>
                <span class="value">{{ fee.feeDetail }}</span>
              </div>
              <div class="fee-row">
                <span class="label">生成时间:</span>
                <span class="value">{{ formatDate(fee.createTime) }}</span>
              </div>
            </div>

            <div class="fee-footer">
              <div class="fee-amount">
                <span class="amount-label">应付金额:</span>
                <span class="amount-value">¥{{ fee.feeAmount }}</span>
              </div>
              <van-button
                v-if="fee.status === '0'"
                size="small"
                type="primary"
                @click.stop="handlePay(fee)"
              >
                立即缴费
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import dayjs from 'dayjs'

const router = useRouter()

// 筛选条件
const selectedElder = ref(1)
const selectedYear = ref('2025')
const selectedMonth = ref('01')

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
const elderOptions = [
  { text: '张三', value: 1 },
  { text: '李四', value: 2 },
  { text: '王五', value: 3 }
]

// 年份选项
const yearOptions = [
  { text: '2025年', value: '2025' },
  { text: '2024年', value: '2024' },
  { text: '2023年', value: '2023' }
]

// 月份选项
const monthOptions = [
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

// 模拟费用数据
const mockFees = [
  {
    feeId: 1,
    feeType: '1',
    billMonth: '2025-01',
    feeDetail: '床位费(1500元) + 护理费(2000元) + 伙食费(800元)',
    feeAmount: 4300.00,
    status: '0',
    createTime: '2025-01-01 00:00:00'
  },
  {
    feeId: 2,
    feeType: '1',
    billMonth: '2024-12',
    feeDetail: '床位费(1500元) + 护理费(2000元) + 伙食费(800元)',
    feeAmount: 4300.00,
    status: '1',
    createTime: '2024-12-01 00:00:00'
  },
  {
    feeId: 3,
    feeType: '2',
    billMonth: '2024-12',
    feeDetail: '体检费用',
    feeAmount: 500.00,
    status: '1',
    createTime: '2024-12-15 10:30:00'
  },
  {
    feeId: 4,
    feeType: '1',
    billMonth: '2024-11',
    feeDetail: '床位费(1500元) + 护理费(2000元) + 伙食费(800元)',
    feeAmount: 4300.00,
    status: '1',
    createTime: '2024-11-01 00:00:00'
  },
  {
    feeId: 5,
    feeType: '3',
    billMonth: '2024-11',
    feeDetail: '秋季衣物添置',
    feeAmount: 600.00,
    status: '1',
    createTime: '2024-11-10 14:20:00'
  }
]

// 计算总费用
const totalAmount = computed(() => {
  const total = mockFees.reduce((sum, fee) => sum + fee.feeAmount, 0)
  return total.toFixed(2)
})

// 计算已缴费用
const paidAmount = computed(() => {
  const paid = mockFees
    .filter(fee => fee.status === '1')
    .reduce((sum, fee) => sum + fee.feeAmount, 0)
  return paid.toFixed(2)
})

// 计算待缴费用
const unpaidAmount = computed(() => {
  const unpaid = mockFees
    .filter(fee => fee.status === '0')
    .reduce((sum, fee) => sum + fee.feeAmount, 0)
  return unpaid.toFixed(2)
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
  onLoad()
  refreshing.value = false
}

// 加载费用列表 (使用模拟数据)
const onLoad = async () => {
  try {
    loading.value = true

    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    // 分页处理
    const startIndex = (pageNum.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    const list = mockFees.slice(startIndex, endIndex)

    if (list.length === 0) {
      finished.value = true
    } else {
      feeList.value = [...feeList.value, ...list]
      pageNum.value++

      // 如果返回数据少于pageSize或已到达最后,说明没有更多了
      if (list.length < pageSize.value || endIndex >= mockFees.length) {
        finished.value = true
      }
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
const goToDetail = (feeId) => {
  showToast('费用详情页面开发中')
  // TODO: 跳转到费用详情页
  // router.push({
  //   name: 'FeeDetail',
  //   params: { id: feeId }
  // })
}

// 立即缴费
const handlePay = (fee) => {
  showToast('支付功能开发中')
  // TODO: 跳转到支付页面
}

// 获取费用类型文本
const getFeeTypeText = (type) => {
  const typeMap = {
    '1': '月度费用',
    '2': '医疗费用',
    '3': '其他费用'
  }
  return typeMap[type] || '其他'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    '0': '待缴费',
    '1': '已缴费',
    '2': '已退款'
  }
  return statusMap[status] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    '0': 'warning',
    '1': 'success',
    '2': 'default'
  }
  return typeMap[status] || 'default'
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
</style>
