<template>
  <div class="renew-page">
    <div v-if="loading" class="loading-container">
      <van-loading type="spinner" color="#667eea" size="40" />
      <p class="loading-text">加载中...</p>
    </div>

    <div v-else-if="renewInfo" class="renew-content">
      <!-- 老人信息卡片 -->
      <van-cell-group inset class="info-card">
        <van-cell center>
          <template #title>
            <div class="elder-name">
              <van-icon name="user-o" />
              {{ renewInfo.elderName }}
            </div>
          </template>
        </van-cell>
        <van-cell title="当前床位" :value="renewInfo.bedInfo" />
        <van-cell title="机构名称" :value="renewInfo.institutionName" />
        <van-cell title="当前到期日期" :value="renewInfo.currentDueDateText" />
      </van-cell-group>

      <!-- 当前价格卡片 -->
      <van-cell-group inset class="info-card">
        <van-cell title="当前费用" center>
          <template #value>
            <span class="price-highlight">¥ {{ formatAmount(renewInfo.monthlyFeeTotal) }}/月</span>
          </template>
        </van-cell>
        <van-cell title="床位费" :value="'¥' + formatAmount(renewInfo.bedFee)" />
        <van-cell title="护理费" :value="renewInfo.careLevelText + ' ¥' + formatAmount(renewInfo.careFee)" />
        <van-cell title="餐费" :value="'¥' + formatAmount(renewInfo.mealFee)" />
      </van-cell-group>

      <!-- 账户余额卡片 -->
      <van-cell-group inset class="info-card">
        <van-cell title="服务费余额" :value="'¥' + formatAmount(renewInfo.serviceBalance)" />
        <van-cell title="押金余额" :value="'¥' + formatAmount(renewInfo.depositBalance)" />
        <van-cell title="会员余额" :value="'¥' + formatAmount(renewInfo.memberBalance)" />
      </van-cell-group>

      <!-- 续费表单 -->
      <van-form @submit="handleSubmit" ref="renewForm">
        <van-cell-group inset class="form-card">
          <van-cell center title="续费月数" required>
            <template #value>
              <van-stepper
                v-model="formData.monthCount"
                min="1"
                max="120"
                @change="calculateTotal"
              />
            </template>
          </van-cell>

          <van-cell
            title="护理等级"
            is-link
            :value="getCareLevelText(formData.careLevel)"
            @click="showCareLevelPicker = true"
          />

          <van-cell
            title="餐费档次"
            is-link
            :value="getMealLevelText(formData.mealLevelCode)"
            @click="showMealLevelPicker = true"
          />

          <van-cell
            title="补交押金"
            :value="'¥' + formatAmount(formData.depositAmount)"
          >
            <template #right-icon>
              <van-checkbox v-model="hasDeposit" @change="handleDepositChange" />
            </template>
          </van-cell>

          <van-cell
            v-if="hasDeposit"
            title="押金金额"
          >
            <template #value>
              <van-stepper
                v-model="formData.depositAmount"
                min="0"
                :step="100"
                @change="calculateTotal"
              />
            </template>
          </van-cell>

          <van-cell
            title="补交会员费"
            :value="'¥' + formatAmount(formData.memberFee)"
          >
            <template #right-icon>
              <van-checkbox v-model="hasMember" @change="handleMemberChange" />
            </template>
          </van-cell>

          <van-cell
            v-if="hasMember"
            title="会员费金额"
          >
            <template #value>
              <van-stepper
                v-model="formData.memberFee"
                min="0"
                :step="100"
                @change="calculateTotal"
              />
            </template>
          </van-cell>

          <van-cell
            title="备注"
            :value="formData.remark"
            is-link
            @click="showRemarkInput = true"
          />
        </van-cell-group>

        <!-- 费用汇总 -->
        <van-cell-group inset class="summary-card">
          <van-cell title="服务费小计" :value="'¥' + formatAmount(serviceFeeTotal)" />
          <van-cell v-if="formData.depositAmount > 0" title="补交押金" :value="'¥' + formatAmount(formData.depositAmount)" />
          <van-cell v-if="formData.memberFee > 0" title="补交会员费" :value="'¥' + formatAmount(formData.memberFee)" />
          <van-cell title="应收总计" :value="'¥' + formatAmount(calculatedTotal)" />
          <van-cell
            title="新到期日期"
            :value="newDueDateText"
            label="续费后"
          />
        </van-cell-group>

        <!-- 提交按钮 -->
        <div class="submit-section">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
            :loading="submitting"
            loading-text="提交中..."
          >
            提交续费
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 护理等级选择器 -->
    <van-popup v-model:show="showCareLevelPicker" position="bottom">
      <van-picker
        :columns="careLevelOptions"
        @confirm="onCareLevelConfirm"
        @cancel="showCareLevelPicker = false"
      />
    </van-popup>

    <!-- 餐费档次选择器 -->
    <van-popup v-model:show="showMealLevelPicker" position="bottom">
      <van-picker
        :columns="mealLevelOptions"
        @confirm="onMealLevelConfirm"
        @cancel="showMealLevelPicker = false"
      />
    </van-popup>

    <!-- 备注输入框 -->
    <van-popup v-model:show="showRemarkInput" position="bottom">
      <van-field
        v-model="formData.remark"
        type="textarea"
        placeholder="请输入备注信息（可选）"
        rows="3"
        autosize
      />
      <van-button block @click="showRemarkInput = false">确定</van-button>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showConfirmDialog, showLoadingToast, closeToast } from 'vant'
import { getRenewInfo, submitRenewOrder } from '@/api/order'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

// 获取路由参数
const elderId = ref(route.query.elderId)

// 页面状态
const loading = ref(true)
const submitting = ref(false)
const renewInfo = ref(null)

// 表单数据
const formData = ref({
  elderId: elderId.value,
  monthCount: 1,
  careLevel: null,
  mealLevelCode: null,
  depositAmount: 0,
  memberFee: 0,
  remark: ''
})

// 复选框状态
const hasDeposit = ref(false)
const hasMember = ref(false)

// 弹窗状态
const showCareLevelPicker = ref(false)
const showMealLevelPicker = ref(false)
const showRemarkInput = ref(false)

// 护理等级选项
const careLevelOptions = [
  { text: '自理', value: '1' },
  { text: '半护理', value: '2' },
  { text: '全护理', value: '3' }
]

// 餐费档次选项（从接口获取）
const mealLevelOptions = computed(() => {
  if (!renewInfo.value?.mealConfigList) return []
  return renewInfo.value.mealConfigList.map(item => ({
    text: item.mealLevel + ' - ¥' + item.price,
    value: item.mealLevelCode
  }))
})

// 计算属性
const serviceFeeTotal = computed(() => {
  if (!renewInfo.value) return 0
  // 月服务费 = 床位费 + 护理费 + 餐费
  const monthlyFee = renewInfo.value.bedFee + renewInfo.value.careFee + renewInfo.value.mealFee
  return monthlyFee * formData.value.monthCount
})

const calculatedTotal = computed(() => {
  return serviceFeeTotal.value + formData.value.depositAmount + formData.value.memberFee
})

const newDueDateText = computed(() => {
  if (!renewInfo.value?.currentDueDate) return ''
  const dueDate = dayjs(renewInfo.value.currentDueDate)
  const newDate = dueDate.add(formData.value.monthCount, 'month')
  return newDate.format('YYYY-MM-DD')
})

// 获取续费信息
const loadRenewInfo = async () => {
  try {
    loading.value = true
    const response = await getRenewInfo(elderId.value)
    if (response.code === 200 && response.data) {
      renewInfo.value = response.data
      // 初始化表单数据
      formData.value.careLevel = response.data.careLevel
      formData.value.mealLevelCode = response.data.mealLevelCode
      // depositAmount 和 memberFee 保持为 0，只在用户勾选复选框时才赋值
    } else {
      showToast(response.msg || '获取续费信息失败')
    }
  } catch (error) {
    console.error('获取续费信息失败:', error)
    showToast('获取续费信息失败')
  } finally {
    loading.value = false
  }
}

// 计算总价
const calculateTotal = () => {
  // 触发计算属性更新
}

// 押金复选框变化
const handleDepositChange = (checked) => {
  if (checked) {
    formData.value.depositAmount = renewInfo.value?.depositFee || 0
  } else {
    formData.value.depositAmount = 0
  }
  calculateTotal()
}

// 会员费复选框变化
const handleMemberChange = (checked) => {
  if (checked) {
    formData.value.memberFee = renewInfo.value?.memberFee || 0
  } else {
    formData.value.memberFee = 0
  }
  calculateTotal()
}

// 护理等级确认
const onCareLevelConfirm = ({ selectedOptions }) => {
  formData.value.careLevel = selectedOptions[0].value
  showCareLevelPicker.value = false
  // 更新价格（这里应该重新获取价格，暂时简化处理）
}

// 餐费档次确认
const onMealLevelConfirm = ({ selectedOptions }) => {
  formData.value.mealLevelCode = selectedOptions[0].value
  showMealLevelPicker.value = false
  // 更新价格
}

// 获取护理等级文本
const getCareLevelText = (value) => {
  const option = careLevelOptions.find(item => item.value === value)
  return option ? option.text : '请选择'
}

// 获取餐费档次文本
const getMealLevelText = (value) => {
  const option = mealLevelOptions.value.find(item => item.value === value)
  return option ? option.text : '请选择'
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount && amount !== 0) return '0.00'
  return parseFloat(amount).toFixed(2)
}

// 提交续费
const handleSubmit = async () => {
  // 验证月数
  if (!formData.value.monthCount || formData.value.monthCount < 1) {
    showToast('请选择续费月数')
    return
  }

  try {
    submitting.value = true
    const response = await submitRenewOrder({
      elderId: formData.value.elderId,
      monthCount: formData.value.monthCount,
      careLevel: formData.value.careLevel,
      mealLevelCode: formData.value.mealLevelCode,
      depositAmount: formData.value.depositAmount,
      memberFee: formData.value.memberFee,
      remark: formData.value.remark
    })

    if (response.code === 200 && response.data) {
      showToast('续费订单创建成功')
      // 跳转到支付页面
      setTimeout(() => {
        router.push({
          path: `/payment/cashier/${response.data.orderId}`,
          query: {
            orderNo: response.data.orderNo,
            amount: response.data.orderAmount
          }
        })
      }, 500)
    } else {
      showToast(response.msg || '提交失败')
    }
  } catch (error) {
    console.error('提交续费失败:', error)
    showToast('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (elderId.value) {
    loadRenewInfo()
  } else {
    showToast('缺少老人信息')
    router.back()
  }
})
</script>

<style scoped>
.renew-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-top: 100px;
}

.loading-text {
  margin-top: 16px;
  color: #999;
  font-size: 14px;
}

.info-card {
  margin: 12px;
  border-radius: 8px;
  overflow: hidden;
}

.form-card {
  margin: 12px;
  border-radius: 8px;
  overflow: hidden;
}

.summary-card {
  margin: 12px;
  border-radius: 8px;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.summary-card :deep(.van-cell) {
  background: transparent;
  color: #fff;
}

.summary-card :deep(.van-cell__value) {
  color: #fff;
  font-weight: bold;
}

.summary-card :deep(.van-cell__label) {
  color: rgba(255, 255, 255, 0.7);
}

.elder-name {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 16px;
  font-weight: 500;
}

.price-highlight {
  color: #667eea;
  font-size: 18px;
  font-weight: bold;
}

.submit-section {
  padding: 16px;
}
</style>
