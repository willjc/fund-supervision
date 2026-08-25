<template>
  <div class="expense-page h5-page h5-page--constrained">
    <nav class="expense-nav" aria-label="我的费用导航">
      <button type="button" class="expense-nav__back" aria-label="返回上一页" @click="handleBack">
        <van-icon name="arrow-left" />
      </button>
      <h1>我的费用</h1>
      <span class="expense-nav__placeholder" aria-hidden="true"></span>
    </nav>

    <main class="expense-content">
      <section class="elder-selector h5-card" aria-labelledby="elder-selector-title">
        <button type="button" class="elder-selector__button" @click="showElderPicker = true">
          <span class="elder-selector__icon" aria-hidden="true"><van-icon name="friends-o" /></span>
          <span class="elder-selector__copy">
            <small id="elder-selector-title">当前查询老人</small>
            <strong>{{ selectedElder.name || '请选择老人' }}</strong>
          </span>
          <span class="elder-selector__action">切换 <van-icon name="arrow" /></span>
        </button>
      </section>

      <template v-if="selectedElder.name">
        <section class="account-overview" aria-labelledby="account-title">
          <div class="overview-main">
            <div class="overview-topline">
              <div>
                <p v-if="institutionName" class="institution-name">{{ institutionName }}</p>
                <span id="account-title" class="total-label">账户可用总余额</span>
              </div>
              <span class="account-shield"><van-icon name="shield-o" /> 资金受监管</span>
            </div>
            <strong class="total-amount"><small>¥</small>{{ formatAmount(accountBalance) }}</strong>
            <p class="overview-note">余额以监管账户最新记录为准</p>
          </div>

          <div class="balance-grid" aria-label="账户分类余额">
            <div class="balance-item">
              <span class="balance-item__icon"><van-icon name="balance-o" /></span>
              <span>服务费余额</span>
              <strong>¥{{ formatAmount(serviceAmount) }}</strong>
            </div>
            <div class="balance-item">
              <span class="balance-item__icon"><van-icon name="gold-coin-o" /></span>
              <span>押金余额</span>
              <strong>¥{{ formatAmount(depositAmount) }}</strong>
            </div>
            <div class="balance-item">
              <span class="balance-item__icon"><van-icon name="card-o" /></span>
              <span>会员费余额</span>
              <strong>¥{{ formatAmount(memberAmount) }}</strong>
            </div>
          </div>
        </section>

        <section class="account-actions h5-card" aria-label="账户操作">
          <button type="button" class="account-action" @click="goToDepositManage">
            <span class="account-action__icon" aria-hidden="true"><van-icon name="gold-coin-o" /></span>
            <span>
              <strong>押金使用申请</strong>
              <small>查看申请与审批进度</small>
            </span>
            <van-icon name="arrow" />
          </button>
          <button type="button" class="account-action" @click="goToRefundApply">
            <span class="account-action__icon" aria-hidden="true"><van-icon name="refund-o" /></span>
            <span>
              <strong>申请退款</strong>
              <small>发起服务费、押金或会员费退款</small>
            </span>
            <van-icon name="arrow" />
          </button>
        </section>

        <section class="expense-records h5-card" aria-labelledby="record-title">
          <header class="records-heading">
            <div>
              <h2 id="record-title">资金明细</h2>
              <p>清晰记录每一笔收入与支出</p>
            </div>
            <van-icon name="records-o" />
          </header>

          <van-tabs v-model:active="activeTab" class="expense-tabs" @change="onTabChange">
            <van-tab title="服务费" name="service"></van-tab>
            <van-tab title="押金" name="deposit"></van-tab>
            <van-tab title="会员费" name="member"></van-tab>
          </van-tabs>

          <div class="expense-list">
            <van-list
              v-model:loading="loading"
              :finished="finished"
              finished-text="已经到底了"
              @load="onLoadMore"
            >
              <article v-for="item in expenseList" :key="item.recordId || item.id" class="expense-item">
                <div class="expense-main">
                  <div class="expense-info">
                    <div class="expense-title">{{ item.typeText }} · {{ item.description || '费用变动' }}</div>
                    <time class="expense-time">{{ item.time }}</time>
                  </div>
                  <div class="expense-amount-block">
                    <span :class="['transaction-direction', item.amountClass]">
                      {{ item.transactionType === 'income' ? '收入' : '支出' }}
                    </span>
                    <strong :class="['expense-amount', item.amountClass]">
                      {{ item.transactionType === 'income' ? '+' : '-' }}¥{{ formatAmount(item.amount) }}
                    </strong>
                  </div>
                </div>

                <div class="expense-meta">
                  <span class="transaction-type">
                    <van-icon :name="item.transactionType === 'income' ? 'down' : 'upgrade'" />
                    {{ item.transactionTypeText }}记录
                  </span>
                  <span
                    v-if="item.balanceBefore !== null && item.balanceBefore !== undefined"
                    class="balance-info"
                  >
                    变动前 ¥{{ formatAmount(item.balanceBefore) }}
                    <van-icon name="arrow" />
                    变动后 ¥{{ formatAmount(item.balanceAfter) }}
                  </span>
                </div>
              </article>
            </van-list>

            <van-empty
              v-if="expenseList.length === 0 && !loading"
              class="empty-list"
              description="暂无费用记录"
              image-size="104"
            >
              <p>切换费用分类可查看其他资金明细</p>
            </van-empty>
          </div>
        </section>
      </template>

      <section v-else class="no-elder-selected h5-card">
        <van-empty description="请选择老人后查看账户费用" image-size="112">
          <van-button type="primary" plain @click="showElderPicker = true">选择老人</van-button>
        </van-empty>
      </section>
    </main>

    <van-popup v-model:show="showElderPicker" position="bottom" round safe-area-inset-bottom>
      <van-picker
        title="选择老人"
        :columns="elderOptions"
        @confirm="onElderConfirm"
        @cancel="showElderPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
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
const serviceAmount = ref(0)
const depositAmount = ref(0)
const memberAmount = ref(0)
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
      serviceAmount.value = parseFloat(data.prepaidAmount ?? data.serviceBalance ?? 0)
      depositAmount.value = parseFloat(data.depositBalance || 0)
      memberAmount.value = parseFloat(data.memberBalance || 0)
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
    name: 'RefundApply',
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

const handleBack = () => {
  router.back()
}

// 监听选中老人变化
watch(selectedElder, (newVal) => {
  if (newVal.id) {
    accountBalance.value = 0
    serviceAmount.value = 0
    depositAmount.value = 0
    memberAmount.value = 0
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
  padding-bottom: var(--h5-space-8);
}

.expense-nav {
  position: sticky;
  top: 0;
  z-index: var(--h5-z-sticky);
  display: grid;
  grid-template-columns: 44px 1fr 44px;
  align-items: center;
  min-height: calc(var(--h5-header-height) + var(--h5-safe-area-top));
  padding: var(--h5-safe-area-top) var(--h5-page-padding) 0;
  background: var(--h5-color-surface);
  border-bottom: 1px solid var(--h5-color-divider);
  box-shadow: var(--h5-shadow-xs);
}

.expense-nav h1 {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
  text-align: center;
}

.expense-nav__back {
  display: inline-flex;
  width: 44px;
  height: 44px;
  align-items: center;
  justify-content: flex-start;
  color: var(--h5-color-primary);
  font-size: 22px;
  background: transparent;
  border: 0;
  cursor: pointer;
}

.expense-nav__back:focus-visible,
.elder-selector__button:focus-visible,
.account-action:focus-visible {
  outline: none;
  box-shadow: var(--h5-shadow-focus);
}

.expense-nav__placeholder {
  width: 44px;
}

.expense-content {
  display: grid;
  gap: var(--h5-space-3);
  padding: var(--h5-space-3) var(--h5-page-padding);
}

.elder-selector__button {
  display: flex;
  width: 100%;
  min-height: 72px;
  align-items: center;
  gap: var(--h5-space-3);
  padding: var(--h5-space-3) var(--h5-space-4);
  color: inherit;
  text-align: left;
  background: var(--h5-color-surface);
  border: 0;
  cursor: pointer;
}

.elder-selector__icon {
  display: inline-flex;
  flex: 0 0 42px;
  width: 42px;
  height: 42px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-primary);
  font-size: 22px;
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-md);
}

.elder-selector__copy {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 2px;
}

.elder-selector__copy small {
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.elder-selector__copy strong {
  overflow: hidden;
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.elder-selector__action {
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  gap: 2px;
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
}

.account-overview {
  overflow: hidden;
  background: var(--h5-color-surface);
  border: 1px solid var(--h5-color-primary-100);
  border-radius: var(--h5-radius-lg);
  box-shadow: var(--h5-shadow-md);
}

.overview-main {
  padding: var(--h5-space-5);
  color: var(--h5-color-text-inverse);
  background: linear-gradient(140deg, var(--h5-color-primary-700), var(--h5-color-primary-500));
}

.overview-topline {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-3);
}

.institution-name {
  max-width: 210px;
  margin-bottom: var(--h5-space-1);
  overflow: hidden;
  color: rgba(255, 255, 255, 0.82);
  font-size: var(--h5-font-size-sm);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.total-label {
  color: rgba(255, 255, 255, 0.94);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-medium);
}

.account-shield {
  display: inline-flex;
  flex: 0 0 auto;
  min-height: 28px;
  align-items: center;
  gap: var(--h5-space-1);
  padding: 3px var(--h5-space-2);
  color: var(--h5-color-text-inverse);
  font-size: var(--h5-font-size-sm);
  background: rgba(255, 255, 255, 0.14);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--h5-radius-pill);
}

.total-amount {
  display: block;
  margin-top: var(--h5-space-3);
  font-size: 34px;
  font-weight: var(--h5-font-weight-bold);
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.5px;
}

.total-amount small {
  margin-right: var(--h5-space-1);
  font-size: var(--h5-font-size-xl);
  font-weight: var(--h5-font-weight-semibold);
}

.overview-note {
  margin-top: var(--h5-space-2);
  color: rgba(255, 255, 255, 0.72);
  font-size: var(--h5-font-size-sm);
}

.balance-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  background: var(--h5-color-surface);
}

.balance-item {
  display: flex;
  min-width: 0;
  align-items: center;
  flex-direction: column;
  padding: var(--h5-space-4) var(--h5-space-2);
  text-align: center;
  border-right: 1px solid var(--h5-color-divider);
}

.balance-item:last-child {
  border-right: 0;
}

.balance-item__icon {
  display: inline-flex;
  width: 32px;
  height: 32px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-primary);
  font-size: 18px;
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-sm);
}

.balance-item > span:nth-child(2) {
  margin-top: var(--h5-space-2);
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.balance-item strong {
  max-width: 100%;
  margin-top: 2px;
  overflow: hidden;
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
  font-variant-numeric: tabular-nums;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.account-action {
  display: flex;
  width: 100%;
  min-height: 72px;
  align-items: center;
  gap: var(--h5-space-3);
  padding: var(--h5-space-3) var(--h5-space-4);
  color: inherit;
  text-align: left;
  background: var(--h5-color-surface);
  border: 0;
  border-bottom: 1px solid var(--h5-color-divider);
  cursor: pointer;
}

.account-action:last-child {
  border-bottom: 0;
}

.account-action:active {
  background: var(--h5-color-primary-soft);
}

.account-action__icon {
  display: inline-flex;
  flex: 0 0 40px;
  width: 40px;
  height: 40px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-primary);
  font-size: 20px;
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-md);
}

.account-action > span:nth-child(2) {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 2px;
}

.account-action strong {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
}

.account-action small {
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.account-action > .van-icon {
  color: var(--h5-color-text-tertiary);
}

.records-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--h5-space-3);
  padding: var(--h5-space-4) var(--h5-space-4) var(--h5-space-3);
}

.records-heading h2 {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
}

.records-heading p {
  margin-top: 2px;
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.records-heading > .van-icon {
  color: var(--h5-color-primary);
  font-size: 22px;
}

.expense-tabs :deep(.van-tabs__wrap) {
  height: 48px;
  border-top: 1px solid var(--h5-color-divider);
  border-bottom: 1px solid var(--h5-color-divider);
}

.expense-tabs :deep(.van-tab) {
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-medium);
}

.expense-tabs :deep(.van-tab--active) {
  color: var(--h5-color-primary);
  font-weight: var(--h5-font-weight-semibold);
}

.expense-tabs :deep(.van-tabs__line) {
  width: 28px;
  height: 3px;
  background: var(--h5-color-primary);
}

.expense-list {
  min-height: 220px;
  padding: 0 var(--h5-space-4);
}

.expense-item {
  padding: var(--h5-space-4) 0;
  border-bottom: 1px solid var(--h5-color-divider);
}

.expense-item:last-child {
  border-bottom: 0;
}

.expense-main {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-3);
}

.expense-info {
  min-width: 0;
  flex: 1;
}

.expense-title {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
  line-height: var(--h5-line-height-normal);
}

.expense-time {
  display: block;
  margin-top: var(--h5-space-1);
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.expense-amount-block {
  display: flex;
  flex: 0 0 auto;
  align-items: flex-end;
  flex-direction: column;
  gap: var(--h5-space-1);
}

.transaction-direction {
  display: inline-flex;
  min-height: 24px;
  align-items: center;
  padding: 2px var(--h5-space-2);
  color: var(--h5-color-danger);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  background: var(--h5-color-danger-soft);
  border-radius: var(--h5-radius-pill);
}

.transaction-direction.income {
  color: var(--h5-color-success);
  background: var(--h5-color-success-soft);
}

.expense-amount {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-xl);
  font-weight: var(--h5-font-weight-bold);
  font-variant-numeric: tabular-nums;
  white-space: nowrap;
}

.expense-amount.income {
  color: var(--h5-color-success);
}

.expense-amount.expense {
  color: var(--h5-color-danger);
}

.expense-meta {
  display: grid;
  gap: var(--h5-space-2);
  margin-top: var(--h5-space-3);
  padding: var(--h5-space-2) var(--h5-space-3);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  line-height: var(--h5-line-height-normal);
  background: var(--h5-color-surface-subtle);
  border-radius: var(--h5-radius-sm);
}

.transaction-type,
.balance-info {
  display: flex;
  align-items: center;
  gap: var(--h5-space-1);
}

.transaction-type {
  font-weight: var(--h5-font-weight-medium);
}

.balance-info {
  flex-wrap: wrap;
  color: var(--h5-color-text-tertiary);
}

.empty-list {
  min-height: 300px;
}

.empty-list p {
  margin-top: var(--h5-space-2);
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.no-elder-selected {
  min-height: 360px;
}

.no-elder-selected :deep(.van-button) {
  min-width: 132px;
  min-height: 44px;
  margin-top: var(--h5-space-3);
}

@media (max-width: 359px) {
  .expense-content {
    padding-right: var(--h5-space-3);
    padding-left: var(--h5-space-3);
  }

  .account-shield {
    display: none;
  }

  .balance-item {
    padding-right: var(--h5-space-1);
    padding-left: var(--h5-space-1);
  }
}
</style>
