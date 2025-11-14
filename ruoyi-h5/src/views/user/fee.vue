<template>
  <div class="user-fee-page">
    <van-nav-bar title="我的费用" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="fee-content">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <van-dropdown-menu>
          <van-dropdown-item v-model="filterType" :options="typeOptions" @change="onFilterChange" />
          <van-dropdown-item v-model="filterStatus" :options="statusOptions" @change="onFilterChange" />
        </van-dropdown-menu>
      </div>

      <!-- 费用列表 -->
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div v-for="fee in feeList" :key="fee.feeId" class="fee-item" @click="viewDetail(fee)">
            <div class="fee-header">
              <div class="fee-title">{{ fee.feeName }}</div>
              <div class="fee-amount" :class="fee.status === '1' ? 'paid' : 'unpaid'">
                ¥{{ formatAmount(fee.amount) }}
              </div>
            </div>
            <div class="fee-info">
              <div class="info-row">
                <span class="label">老人姓名:</span>
                <span class="value">{{ fee.elderName }}</span>
              </div>
              <div class="info-row">
                <span class="label">费用类型:</span>
                <span class="value">{{ getFeeTypeText(fee.feeType) }}</span>
              </div>
              <div class="info-row">
                <span class="label">账单月份:</span>
                <span class="value">{{ fee.billingMonth }}</span>
              </div>
            </div>
            <div class="fee-footer">
              <span class="fee-time">{{ fee.createTime }}</span>
              <van-tag :type="fee.status === '1' ? 'success' : 'warning'">
                {{ getStatusText(fee.status) }}
              </van-tag>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>

      <van-empty v-if="!loading && feeList.length === 0" description="暂无费用记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

// 筛选条件
const filterType = ref(0)
const filterStatus = ref(0)

const typeOptions = [
  { text: '全部类型', value: 0 },
  { text: '护理费', value: 1 },
  { text: '伙食费', value: 2 },
  { text: '床位费', value: 3 },
  { text: '医疗费', value: 4 }
]

const statusOptions = [
  { text: '全部状态', value: 0 },
  { text: '待缴费', value: '0' },
  { text: '已缴费', value: '1' }
]

// 列表数据
const feeList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)

// 模拟费用数据
const mockFees = [
  {
    feeId: 1,
    feeName: '2025年1月护理费',
    feeType: 1,
    elderName: '张老先生',
    amount: 1500,
    status: '0',
    billingMonth: '2025-01',
    createTime: '2025-01-01'
  },
  {
    feeId: 2,
    feeName: '2025年1月伙食费',
    feeType: 2,
    elderName: '张老先生',
    amount: 800,
    status: '1',
    billingMonth: '2025-01',
    createTime: '2025-01-01'
  },
  {
    feeId: 3,
    feeName: '2024年12月护理费',
    feeType: 1,
    elderName: '张老先生',
    amount: 1500,
    status: '1',
    billingMonth: '2024-12',
    createTime: '2024-12-01'
  },
  {
    feeId: 4,
    feeName: '2024年12月床位费',
    feeType: 3,
    elderName: '张老先生',
    amount: 500,
    status: '1',
    billingMonth: '2024-12',
    createTime: '2024-12-01'
  },
  {
    feeId: 5,
    feeName: '2024年11月护理费',
    feeType: 1,
    elderName: '张老先生',
    amount: 1500,
    status: '1',
    billingMonth: '2024-11',
    createTime: '2024-11-01'
  }
]

// 筛选变化
const onFilterChange = () => {
  pageNum.value = 1
  finished.value = false
  feeList.value = []
  onLoad()
}

// 下拉刷新
const onRefresh = () => {
  pageNum.value = 1
  finished.value = false
  feeList.value = []
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
    let filteredList = [...mockFees]

    // 按类型筛选
    if (filterType.value !== 0) {
      filteredList = filteredList.filter(item => item.feeType === filterType.value)
    }

    // 按状态筛选
    if (filterStatus.value !== 0) {
      filteredList = filteredList.filter(item => item.status === filterStatus.value)
    }

    // 分页处理
    const startIndex = (pageNum.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    const list = filteredList.slice(startIndex, endIndex)

    if (list.length === 0) {
      finished.value = true
    } else {
      feeList.value = [...feeList.value, ...list]
      pageNum.value++

      if (endIndex >= filteredList.length) {
        finished.value = true
      }
    }
  } catch (error) {
    showToast('加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 查看详情
const viewDetail = (fee) => {
  showToast('费用详情功能开发中')
}

// 获取费用类型文本
const getFeeTypeText = (type) => {
  const typeMap = {
    1: '护理费',
    2: '伙食费',
    3: '床位费',
    4: '医疗费'
  }
  return typeMap[type] || '其他'
}

// 获取状态文本
const getStatusText = (status) => {
  return status === '1' ? '已缴费' : '待缴费'
}

// 格式化金额
const formatAmount = (amount) => {
  return parseFloat(amount).toFixed(2)
}

onMounted(() => {
  // 初始加载
})
</script>

<style scoped>
.user-fee-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.fee-content {
  padding-bottom: 20px;
}

.filter-bar {
  position: sticky;
  top: 46px;
  z-index: 99;
  background: #fff;
}

.fee-item {
  background: #fff;
  margin: 12px;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.fee-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.fee-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.fee-amount {
  font-size: 18px;
  font-weight: bold;
}

.fee-amount.paid {
  color: #07c160;
}

.fee-amount.unpaid {
  color: #ee0a24;
}

.fee-info {
  padding: 12px 0;
  border-top: 1px solid #f5f5f5;
  border-bottom: 1px solid #f5f5f5;
}

.info-row {
  display: flex;
  padding: 4px 0;
  font-size: 14px;
}

.info-row .label {
  width: 80px;
  color: #999;
  flex-shrink: 0;
}

.info-row .value {
  flex: 1;
  color: #666;
}

.fee-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.fee-time {
  font-size: 12px;
  color: #999;
}
</style>
