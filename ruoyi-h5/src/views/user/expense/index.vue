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
            <div class="total-label">账户余额</div>
            <div class="total-amount">¥{{ formatAmount(accountBalance) }}</div>
            <div class="account-info">
              <span>押金: ¥{{ formatAmount(depositAmount) }}</span>
              <span class="divider">|</span>
              <span>预存: ¥{{ formatAmount(prepaidAmount) }}</span>
            </div>
          </div>
        </div>

        <!-- 费用类型Tab -->
        <van-tabs
          v-model:active="activeTab"
          @change="onTabChange"
          class="expense-tabs"
          color="#667eea"
        >
          <van-tab title="押金" name="deposit"></van-tab>
          <van-tab title="服务费" name="service"></van-tab>
          <van-tab title="其他" name="other"></van-tab>
        </van-tabs>

        <!-- 费用明细列表 -->
        <div class="expense-list">
          <div
            v-for="item in expenseList"
            :key="item.id"
            class="expense-item"
          >
            <div class="expense-main">
              <div class="expense-info">
                <div class="expense-title">{{ item.title }}</div>
                <div class="expense-time">{{ item.time }}</div>
              </div>
              <div class="expense-amount" :class="{ 'income': item.type === 'income' }">
                {{ item.type === 'income' ? '+' : '-' }}¥{{ formatAmount(item.amount) }}
              </div>
            </div>
            <div class="expense-desc">{{ item.description }}</div>

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

          <div v-if="expenseList.length === 0" class="empty-list">
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 选中的老人
const selectedElder = ref({})

// 老人选择器显示
const showElderPicker = ref(false)

// 活动Tab
const activeTab = ref('deposit')

// 老人选项
const elderOptions = [
  { text: '张伟', value: '1' },
  { text: '李明', value: '2' },
  { text: '王芳', value: '3' }
]

// 账户余额
const accountBalance = ref(35800)

// 押金金额
const depositAmount = ref(10000)

// 预存金额
const prepaidAmount = ref(25800)

// 模拟费用数据
const mockExpenseData = {
  deposit: [
    {
      id: 1,
      title: '押金缴纳',
      time: '2025-01-10 10:30',
      amount: 10000,
      type: 'expense',
      description: '入住押金',
      canRefund: true
    }
  ],
  service: [
    {
      id: 2,
      title: '服务费扣款',
      time: '2025-01-12 09:00',
      amount: 2800,
      type: 'expense',
      description: '2025年1月服务费(餐费+护理费+床位费)',
      canRefund: true
    },
    {
      id: 3,
      title: '服务费充值',
      time: '2025-01-08 14:20',
      amount: 5600,
      type: 'income',
      description: '充值2个月服务费',
      canRefund: false
    }
  ],
  other: [
    {
      id: 4,
      title: '空调费',
      time: '2025-01-05 16:00',
      amount: 100,
      type: 'expense',
      description: '2025年1月空调使用费',
      canRefund: false
    }
  ]
}

// 费用列表
const expenseList = computed(() => {
  if (!selectedElder.value.name) return []
  return mockExpenseData[activeTab.value] || []
})

// 格式化金额
const formatAmount = (amount) => {
  return parseFloat(amount).toFixed(2)
}

// 老人选择确认
const onElderConfirm = (value) => {
  selectedElder.value = {
    name: value.selectedOptions[0].text,
    id: value.selectedOptions[0].value
  }
  showElderPicker.value = false
}

// Tab切换
const onTabChange = (name) => {
  activeTab.value = name
}

// 申请退款
const handleRefund = (item) => {
  router.push({
    path: '/user/expense/refund/apply',
    query: {
      expenseId: item.id,
      elderName: selectedElder.value.name,
      elderInfo: JSON.stringify(selectedElder.value)
    }
  })
}
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
