<template>
  <div class="expense-page">
    <van-nav-bar title="我的费用" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="expense-content">
      <!-- 老人选择 -->
      <div class="elder-selector">
        <van-cell
          title="选择老人"
          :value="selectedElder.name || '请选择老人'"
          is-link
          @click="showElderPicker = true"
        />
      </div>

      <!-- 费用总览卡片 -->
      <div v-if="selectedElder.name" class="expense-overview">
        <div class="overview-header">
          <div class="header-gradient">
            <div class="institution-name" v-if="institutionName">{{ institutionName }}</div>
            <div class="total-label">账户余额</div>
            <div class="total-amount">¥{{ formatAmount(accountBalance) }}</div>
            <div class="account-info">
              <span>押金: ¥{{ formatAmount(depositAmount) }}</span>
              <span class="divider">|</span>
              <span>预存: ¥{{ formatAmount(prepaidAmount) }}</span>
            </div>
          </div>
        </div>

        <!-- 押金管理入口 -->
        <div class="deposit-manage-entry">
          <van-button
            block
            plain
            type="primary"
            size="small"
            icon="gold-coin-o"
            @click="goToDepositManage"
          >
            押金使用申请管理
          </van-button>
        </div>

        <!-- 申��退款入口 -->
        <div class="refund-entry">
          <van-button
            block
            plain
            type="primary"
            size="small"
            icon="refund-o"
            @click="goToRefundApply"
          >
            申请退款
          </van-button>
        </div>

        <!-- 费用类型Tab -->
        <van-tabs
          v-model:active="activeTab"
          @change="onTabChange"
          class="expense-tabs"
          color="#667eea"
        >
          <van-tab title="服务费" name="service"></van-tab>
          <van-tab title="押金" name="deposit"></van-tab>
          <van-tab title="会员费" name="member"></van-tab>
        </van-tabs>

        <!-- 费用明细列表 -->
        <div class="expense-list">
          <van-list
            v-model:loading="loading"
            :finished="finished"
            finished-text="没有更多了"
            @load="onLoadMore"
          >
            <div
              v-for="item in expenseList"
              :key="item.recordId"
              class="expense-item"
            >
              <div class="expense-main">
                <div class="expense-info">
                  <div class="expense-title">{{ item.typeText }} - {{ item.description || '暂无描述' }}</div>
                  <div class="expense-time">{{ item.time }}</div>
                </div>
                <div class="expense-amount" :class="item.amountClass">
                  {{ item.transactionType === 'income' ? '+' : '-' }}¥{{ formatAmount(item.amount) }}
                </div>
              </div>
              <div class="expense-meta">
                <span class="transaction-type">{{ item.transactionTypeText }}</span>
                <span v-if="item.balanceBefore !== null && item.balanceBefore !== undefined" class="balance-info">
                  余额: {{ formatAmount(item.balanceBefore) }} → {{ formatAmount(item.balanceAfter) }}
                </span>
              </div>

              <!-- 退款按钮 -->
              <div v-if="item.canRefund" class="expense-actions">
                <van-button
                  plain
                  size="small"
                  type="primary"
                  class="refund-btn"
                  @click="handleRefund(item)"
                >
                  申请退款
                </van-button>
              </div>
            </div>
          </van-list>

          <div v-if="expenseList.length === 0 && !loading" class="empty-list">
            <van-empty description="暂无费用记录" />
          </div>
        </div>
      </div>

      <div v-else class="no-elder-selected">
        <van-empty description="请先选择老人" />
      </div>
    </div>

    <!-- 老人选择器 -->
    <van-popup v-model:show="showElderPicker" position="bottom">
      <van-picker
        :columns="elderOptions"
        @confirm="onElderConfirm"
        @cancel="showElderPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getAccountInfo, getExpenseList, getElderList } from '@/api/expense'
import dayjs from 'dayjs'

const router = useRouter()

// 选中的老人
const selectedElder = ref({})

// 老人选择器显示
const showElderPicker = ref(false)

// 活动Tab
const activeTab = ref('service')

// 老人选项
const elderOptions = ref([])

// 账户余额信息
const accountBalance = ref(0)
const depositAmount = ref(0)
const prepaidAmount = ref(0)
const institutionName = ref('')

// 费用列表
const expenseList = ref([])

// 加载状态
const loading = ref(false)
const finished = ref(false)

// 分页参数
const pageNum = ref(1)
const pageSize = ref(10)

// 加载老人列表
const loadElderList = async () => {
  try {
    const response = await getElderList()
    if (response.code === 200 && Array.isArray(response.data)) {
      elderOptions.value = response.data.map(elder => ({
        text: elder.elderName || '未知老人',
        value: elder.elderId?.toString() || ''
      })).filter(item => item.value) // 过滤掉无效的选项
    } else {
      elderOptions.value = []
    }
  } catch (error) {
    console.error('获取老人列表失败:', error)
    showToast('获取老人列表失败')
    elderOptions.value = [] // 确保始终是数组
  }
}

// 加载账户信息
const loadAccountInfo = async (elderId) => {
  if (!elderId) return

  try {
    const response = await getAccountInfo(elderId)
    if (response.code === 200 && response.data) {
      const data = response.data
      accountBalance.value = parseFloat(data.totalBalance || 0)
      depositAmount.value = parseFloat(data.depositBalance || 0)
      prepaidAmount.value = parseFloat(data.prepaidAmount || 0)
      institutionName.value = data.institutionName || ''

      // 如果老人没有账户，显示提示信息
      if (!data.hasAccount) {
        showToast('该老人暂未创建账户信息，请先办理入住手续')
      }
    }
  } catch (error) {
    console.error('获取账户信息失败:', error)
    showToast('获取账户信息失败')
  }
}

// 加载费用明细
const loadExpenseList = async (reset = false) => {
  if (!selectedElder.value.id) return

  try {
    loading.value = true

    if (reset) {
      expenseList.value = []
      pageNum.value = 1
      finished.value = false
    }

    const typeMap = {
      'service': 'service',
      'deposit': 'deposit',
      'member': 'member',
      'other': 'other'
    }

    const response = await getExpenseList(
      selectedElder.value.id,
      typeMap[activeTab.value] || 'all',
      'all', // 交易类型，默认显示全部
      pageNum.value,
      pageSize.value
    )

    if (response.code === 200 && response.data) {
      const { list = [], total = 0 } = response.data

      if (reset) {
        expenseList.value = list.map(item => ({
          ...item,
          time: item.createTime ? dayjs(item.createTime).format('YYYY-MM-DD HH:mm') : '暂无时间',
          typeText: getExpenseTypeText(item.expenseType),
          transactionTypeText: getTransactionTypeText(item.transactionType),
          amountClass: item.transactionType === 'income' ? 'income' : 'expense'
        }))
      } else {
        expenseList.value = [...expenseList.value, ...list.map(item => ({
          ...item,
          time: item.createTime ? dayjs(item.createTime).format('YYYY-MM-DD HH:mm') : '暂无时间',
          typeText: getExpenseTypeText(item.expenseType),
          transactionTypeText: getTransactionTypeText(item.transactionType),
          amountClass: item.transactionType === 'income' ? 'income' : 'expense'
        }))]
      }

      pageNum.value++

      if (list.length < pageSize.value || expenseList.value.length >= total) {
        finished.value = true
      }
    }
  } catch (error) {
    console.error('获取费用明细失败:', error)
    showToast('获取费用明细失败')
  } finally {
    loading.value = false
  }
}

// 格式化金额
const formatAmount = (amount) => {
  if (amount === null || amount === undefined || amount === '') {
    return '0.00'
  }
  const num = parseFloat(amount)
  return isNaN(num) ? '0.00' : num.toFixed(2)
}

// 获取费用类型显示文本
const getExpenseTypeText = (type) => {
  const typeMap = {
    'deposit': '押金',
    'service': '服务费',
    'member': '会员费',
    'other': '其他'
  }
  return typeMap[type] || '其他'
}

// 获取交易类型显示文本
const getTransactionTypeText = (type) => {
  const typeMap = {
    'income': '收入',
    'expense': '支出'
  }
  return typeMap[type] || '支出'
}

// 老人选择确认
const onElderConfirm = (value) => {
  // 添加安全检查
  if (!value || !value.selectedOptions || !value.selectedOptions[0]) {
    showToast('选择老人失败，请重试')
    return
  }

  selectedElder.value = {
    name: value.selectedOptions[0].text || '未知老人',
    id: value.selectedOptions[0].value
  }
  showElderPicker.value = false

  // 加载选中老人的账户信息和费用明细
  if (selectedElder.value.id) {
    loadAccountInfo(selectedElder.value.id)
    loadExpenseList(true)
  }
}

// Tab切换
const onTabChange = (name) => {
  activeTab.value = name
  // 切换Tab时重新加载费用明细
  loadExpenseList(true)
}

// 申请退款
const handleRefund = (item) => {
  router.push({
    path: '/user/expense/refund/apply',
    query: {
      expenseId: item.id,
      elderName: selectedElder.value.name,
      elderId: selectedElder.value.id,
      amount: item.amount
    }
  })
}

// 跳转押金管理
const goToDepositManage = () => {
  if (!selectedElder.value.id) {
    showToast('请先选择老人')
    return
  }

  router.push({
    path: '/deposit/apply-list',
    query: {
      elderId: selectedElder.value.id,
      elderName: selectedElder.value.name
    }
  })
}

// 跳转申请退款
const goToRefundApply = () => {
  if (!selectedElder.value.id) {
    showToast('请先选择老人')
    return
  }

  router.push({
    path: '/user/refund/apply',
    query: {
      elderId: selectedElder.value.id,
      elderName: selectedElder.value.name
    }
  })
}

// 页面加载时获取老人列表
const initPage = async () => {
  await loadElderList()
}

// 添加加载更多功能
const onLoadMore = () => {
  if (!finished.value && !loading.value && selectedElder.value.id) {
    loadExpenseList(false)
  }
}

// 监听选中老人变化
watch(selectedElder, (newVal) => {
  if (newVal.id) {
    accountBalance.value = 0
    depositAmount.value = 0
    prepaidAmount.value = 0
    institutionName.value = ''
    expenseList.value = []
  }
}, { deep: true })

// 页面加载时初始化
onMounted(async () => {
  await initPage()
})
</script>

<style scoped>
.expense-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.expense-content {
  padding-top: 12px;
}

/* 老人选择 */
.elder-selector {
  margin: 0 12px 12px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 费用总览卡片 */
.expense-overview {
  background: #fff;
  margin: 0 12px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.overview-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 24px 20px;
  color: #fff;
}

.header-gradient {
  text-align: center;
}

.total-label {
  font-size: 14px;
  opacity: 0.95;
  margin-bottom: 8px;
}

.total-amount {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 12px;
  letter-spacing: 1px;
}

.account-info {
  font-size: 13px;
  opacity: 0.9;
  display: flex;
  justify-content: center;
  gap: 8px;
  align-items: center;
}

.divider {
  opacity: 0.5;
}

/* 押金管理入口 */
.deposit-manage-entry {
  padding: 12px 16px 16px;
}

.deposit-manage-entry :deep(.van-button) {
  border-color: #667eea;
  color: #667eea;
  font-weight: 500;
}

.deposit-manage-entry :deep(.van-button):active {
  background-color: #f5f6ff;
}

/* 退款入口 */
.refund-entry {
  padding: 0 16px 16px;
}

.refund-entry :deep(.van-button) {
  border-color: #ff976a;
  color: #ff976a;
  font-weight: 500;
}

.refund-entry :deep(.van-button):active {
  background-color: #fff5f0;
}

/* 机构名称 */
.institution-name {
  font-size: 13px;
  opacity: 0.85;
  margin-bottom: 4px;
  text-align: center;
}

/* Tab */
.expense-tabs {
  background: #fff;
}

:deep(.van-tabs__wrap) {
  border-bottom: 1px solid #f0f0f0;
}

:deep(.van-tab) {
  font-size: 15px;
  font-weight: 500;
}

/* 费用列表 */
.expense-list {
  padding: 12px 16px;
  min-height: 200px;
}

.expense-item {
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.expense-item:last-child {
  border-bottom: none;
}

.expense-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.expense-info {
  flex: 1;
}

.expense-title {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
}

.expense-time {
  font-size: 13px;
  color: #999;
}

.expense-amount {
  font-size: 18px;
  font-weight: 600;
  color: #ee0a24;
  white-space: nowrap;
}

.expense-amount.income {
  color: #07c160;
}

.expense-desc {
  font-size: 13px;
  color: #666;
  padding-left: 0;
}

.expense-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
}

.transaction-type {
  background-color: #f5f5f5;
  color: #666;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
}

.balance-info {
  color: #999;
  font-size: 12px;
}

.expense-amount.expense {
  color: #ee0a24;
}

.expense-amount.income {
  color: #07c160;
}

/* 退款操作按钮 */
.expense-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #f0f0f0;
}

.refund-btn {
  color: #667eea !important;
  border-color: #667eea !important;
}

.empty-list {
  padding: 60px 0;
}

.no-elder-selected {
  padding: 100px 0;
}
</style>
