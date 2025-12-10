<template>
  <div class="order-confirm-page">
    <van-nav-bar title="确认订单" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="confirm-content">
      <!-- 入住老人信息 -->
      <van-cell-group inset>
        <van-field
          v-model="formData.elderName"
          label="入住老人"
          readonly
          required
          @click="showElderPicker = true"
        >
          <template #button>
            <van-icon name="arrow-down" />
          </template>
        </van-field>

        <van-field
          v-model="formData.abilityLevel"
          label="老人能力等级"
          readonly
          required
          @click="showAbilityPicker = true"
        >
          <template #button>
            <van-icon name="arrow-down" />
          </template>
        </van-field>

        <van-field
          v-model="formData.roomType"
          label="入住房间类型"
          readonly
          required
          @click="showRoomPicker = true"
        >
          <template #button>
            <van-icon name="arrow-down" />
          </template>
        </van-field>
      </van-cell-group>

      <!-- 选择套餐 -->
      <van-cell-group inset title="选择套餐">
        <van-checkbox-group v-model="formData.packages">
          <van-cell
            v-for="pkg in packageOptions"
            :key="pkg.id"
          >
            <template #title>
              <van-checkbox :name="pkg.id">
                {{ pkg.name }}
              </van-checkbox>
            </template>
            <template #right-icon>
              <span class="package-price">{{ pkg.price }}元/月</span>
            </template>
          </van-cell>
        </van-checkbox-group>
      </van-cell-group>

      <!-- 费用明细 -->
      <van-cell-group inset title="费用明细">
        <van-cell title="床位费" :value="`${bedFee}元/月`" />
        <van-cell title="护理费" :value="`${nursingFee}元/月`" />
        <van-cell title="餐费" :value="`${mealFee}元/月`" />
        <van-cell title="其他费用" :value="`${otherFee}元/月`" />
        <van-cell title="月服务费合计" :value="`${monthlyPrice}元/月`" class="bold-text" />
        <van-cell title="缴纳月数">
          <template #right-icon>
            <div class="month-control">
              <van-button
                icon="minus"
                size="small"
                @click="decreaseMonth"
                :disabled="formData.months <= 1"
              />
              <span class="month-value">{{ formData.months }}</span>
              <van-button
                icon="plus"
                size="small"
                @click="increaseMonth"
              />
            </div>
          </template>
        </van-cell>
        <van-cell title="押金（一次性缴纳）" :value="`${depositAmount}元`" />
        <van-cell title="会员费（一次性缴纳）" :value="`${memberFee}元`" />
        <van-cell title="总金额" class="total-cell">
          <template #right-icon>
            <span class="total-price">{{ totalAmount }}元</span>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 备注 -->
      <van-cell-group inset title="备注">
        <van-field
          v-model="formData.remark"
          type="textarea"
          placeholder="请输入"
          rows="3"
          maxlength="200"
          show-word-limit
        />
      </van-cell-group>

      <!-- 提交订单按钮 -->
      <div class="submit-section">
        <van-button round block type="primary" @click="submitOrder">
          提交订单
        </van-button>
      </div>
    </div>

    <!-- 入住老人选择器 -->
    <van-popup v-model:show="showElderPicker" position="bottom">
      <van-picker
        :columns="elderOptions"
        @confirm="onElderConfirm"
        @cancel="showElderPicker = false"
      />
    </van-popup>

    <!-- 能力等级选择器 -->
    <van-popup v-model:show="showAbilityPicker" position="bottom">
      <van-picker
        :columns="abilityOptions"
        @confirm="onAbilityConfirm"
        @cancel="showAbilityPicker = false"
      />
    </van-popup>

    <!-- 房间类型选择器 -->
    <van-popup v-model:show="showRoomPicker" position="bottom">
      <van-picker
        :columns="roomOptions"
        @confirm="onRoomConfirm"
        @cancel="showRoomPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showLoadingToast } from 'vant'
import dayjs from 'dayjs'
import { getInstitutionDetail } from '@/api/institution'
import { submitOrder as submitOrderApi, getBedPrice as getBedPriceApi } from '@/api/order'

const router = useRouter()
const route = useRoute()

// 表单数据
const formData = ref({
  elderName: '',
  abilityLevel: '自理',
  roomType: '单人间',
  packages: ['meal_normal', 'nursing_self', 'other'],
  months: 1,
  remark: ''
})

// 选择器显示状态
const showElderPicker = ref(false)
const showAbilityPicker = ref(false)
const showRoomPicker = ref(false)

// 床位价格
const bedPrice = ref(500)

// 选项数据
const elderOptions = ref([
  { text: '请选择老人', value: '' },
  { text: '张伟', value: '张伟' },
  { text: '李明', value: '李明' },
  { text: '王芳', value: '王芳' },
  { text: '赵强', value: '赵强' }
])

const abilityOptions = [
  { text: '自理（300元/月）', value: '自理', price: 300 },
  { text: '半自理（800元/月）', value: '半自理', price: 800 },
  { text: '不能自理（1500元/月）', value: '不能自理', price: 1500 },
  { text: '特护（2500元/月）', value: '特护', price: 2500 }
]

const roomOptions = [
  { text: '单人间（独立床位）', value: '单人间' },
  { text: '双人间（两人一间）', value: '双人间' },
  { text: '三人间（三人一间）', value: '三人间' },
  { text: 'VIP房间（豪华床位）', value: 'VIP房间' }
]

// 套餐选项
const packageOptions = [
  { id: 'meal_normal', name: '餐费（普通餐）', price: 900, category: 'meal' },
  { id: 'meal_custom', name: '餐费（定制餐）', price: 1500, category: 'meal' },
  { id: 'other', name: '其他费用（空调费、水电费）', price: 100, category: 'other' }
]

// 押金金额
const depositAmount = ref(10000)

// 会员费
const memberFee = ref(5000)

// 获取护理费用
const nursingFee = computed(() => {
  const ability = abilityOptions.find(item => item.value === formData.value.abilityLevel)
  return ability ? ability.price : 0
})

// 获取床位费用
const bedFee = computed(() => {
  return bedPrice.value
})

// 获取餐费
const mealFee = computed(() => {
  return packageOptions
    .filter(pkg => formData.value.packages.includes(pkg.id) && pkg.category === 'meal')
    .reduce((sum, pkg) => sum + pkg.price, 0)
})

// 获取其他费用
const otherFee = computed(() => {
  return packageOptions
    .filter(pkg => formData.value.packages.includes(pkg.id) && pkg.category === 'other')
    .reduce((sum, pkg) => sum + pkg.price, 0)
})

// 计算月服务费
const monthlyPrice = computed(() => {
  return bedFee.value + nursingFee.value + mealFee.value + otherFee.value
})

// 计算总金额
const totalAmount = computed(() => {
  return depositAmount.value + memberFee.value + monthlyPrice.value * formData.value.months
})

// 获取床位价格
const getBedPrice = async () => {
  if (!route.params.institutionId || !formData.value.roomType) return

  try {
    // 调用真实的API获取床位价格
    const response = await getBedPriceApi(route.params.institutionId, formData.value.roomType)
    bedPrice.value = response.data || 500
  } catch (error) {
    console.error('获取床位价格失败:', error)
    // 使用模拟数据作为备用
    const priceMap = {
      '单人间': 500,
      '双人间': 350,
      '三人间': 280,
      'VIP房间': 1200
    }
    bedPrice.value = priceMap[formData.value.roomType] || 500
  }
}

// 老人选择确认
const onElderConfirm = (value) => {
  formData.value.elderName = value.selectedOptions[0].value
  showElderPicker.value = false
}

// 能力等级确认
const onAbilityConfirm = (value) => {
  formData.value.abilityLevel = value.selectedOptions[0].value
  showAbilityPicker.value = false
}

// 房间类型确认
const onRoomConfirm = (value) => {
  formData.value.roomType = value.selectedOptions[0].value
  showRoomPicker.value = false
  // 获取床位价格
  getBedPrice()
}

// 增加月数
const increaseMonth = () => {
  if (formData.value.months < 12) {
    formData.value.months++
  }
}

// 减少月数
const decreaseMonth = () => {
  if (formData.value.months > 1) {
    formData.value.months--
  }
}

// 提交订单
const submitOrder = async () => {
  // 验证
  if (!formData.value.elderName) {
    showToast('请选择入住老人')
    return
  }
  if (!formData.value.abilityLevel) {
    showToast('请选择能力等级')
    return
  }
  if (!formData.value.roomType) {
    showToast('请选择房间类型')
    return
  }
  if (formData.value.packages.length === 0) {
    showToast('请至少选择一项套餐')
    return
  }

  showLoadingToast({
    message: '正在提交订单...',
    forbidClick: true
  })

  try {
    // 准备订单数据
    const orderData = {
      institutionId: route.params.institutionId,
      elderName: formData.value.elderName,
      abilityLevel: formData.value.abilityLevel,
      roomType: formData.value.roomType,
      packages: formData.value.packages,
      months: formData.value.months,
      remark: formData.value.remark,
      // 费用明细
      costDetails: {
        bedFee: bedFee.value,
        nursingFee: nursingFee.value,
        mealFee: mealFee.value,
        otherFee: otherFee.value,
        monthlyFee: monthlyPrice.value,
        deposit: depositAmount.value,
        memberFee: memberFee.value,
        totalAmount: totalAmount.value
      }
    }

    // 调用真实的订单提交API
    const response = await submitOrderApi(orderData)

    // 获取返回的订单信息
    const { orderId, orderNo } = response.data

    showToast('订单提交成功')

    // 跳转到收银台
    router.push({
      name: 'PaymentCashier',
      params: { orderId: orderId },
      query: {
        orderNo: orderNo,
        amount: totalAmount.value,
        elderName: formData.value.elderName,
        institutionId: route.params.institutionId
      }
    })
  } catch (error) {
    console.error('提交订单失败:', error)
    showToast('提交订单失��，请重试')
  }
}

// 页面加载时获取初始床位价格
onMounted(() => {
  if (formData.value.roomType) {
    getBedPrice()
  }
})
</script>

<style scoped>
.order-confirm-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.confirm-content {
  padding: 12px 0 20px;
}

.van-cell-group {
  margin-bottom: 12px;
}

.package-price {
  font-size: 14px;
  color: #333;
  margin-left: 12px;
}

.month-control {
  display: flex;
  align-items: center;
  gap: 12px;
}

.month-value {
  min-width: 30px;
  text-align: center;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.total-price {
  font-size: 18px;
  font-weight: bold;
  color: #ee0a24;
}

.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: #fff;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
}

.bold-text :deep(.van-cell__value) {
  font-weight: 600;
  color: #323233;
}

.total-cell {
  background-color: #fafafa;
}

.total-cell :deep(.van-cell__title) {
  font-size: 16px;
  font-weight: 600;
}

:deep(.van-checkbox) {
  flex: 1;
}

:deep(.van-checkbox__label) {
  flex: 1;
}
</style>
