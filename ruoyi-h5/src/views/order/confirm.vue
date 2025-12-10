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
          :model-value="bedTypeText"
          label="入住床位类型"
          readonly
          required
          @click="showRoomPicker = true"
        >
          <template #button>
            <van-icon name="arrow-down" />
          </template>
        </van-field>
      </van-cell-group>

      <!-- 护理等级选择 -->
      <van-cell-group inset title="护理等级">
        <van-field
          v-model="careLevelText"
          label="护理等级"
          readonly
          required
          @click="showCareLevelPicker = true"
          placeholder="请选择护理等级"
        >
          <template #button>
            <van-icon name="arrow-down" />
          </template>
        </van-field>
      </van-cell-group>

      <!-- 费用明细 -->
      <van-cell-group inset title="费用明细">
        <van-cell title="床位费" :value="`${bedFee}元/月`" />
        <van-cell title="护理费" :value="`${careFee}元/月`" />
        <van-cell title="服务费合计" :value="`${monthlyPrice}元/月`" class="bold-text" />
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
        <van-cell title="押金（一次性）" :value="`${depositAmount}元`" />
        <van-cell title="会员费（一次性）" :value="`${memberFee}元`" />
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

    <!-- 护理等级选择器 -->
    <van-popup v-model:show="showCareLevelPicker" position="bottom">
      <van-picker
        :columns="careLevelOptions"
        @confirm="onCareLevelConfirm"
        @cancel="showCareLevelPicker = false"
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
import { submitOrder as submitOrderApi, getBedPrice as getBedPriceApi, getElderList } from '@/api/order'

const router = useRouter()
const route = useRoute()

// 表单数据
const formData = ref({
  elderId: null,
  elderName: '',
  abilityLevel: '自理',
  roomType: '1',
  careLevel: '自理',
  months: 1,
  remark: ''
})

// 选择器显示状态
const showElderPicker = ref(false)
const showAbilityPicker = ref(false)
const showRoomPicker = ref(false)
const showCareLevelPicker = ref(false)

// 床位信息
const bedInfo = ref({
  bedFee: 500,
  selfCarePrice: 500,
  halfCarePrice: 800,
  fullCarePrice: 1200,
  memberFee: 5000,
  depositFee: 10000
})

// 护理等级显示文本
const careLevelText = computed(() => {
  const option = careLevelOptions.find(item => item.value === formData.value.careLevel)
  return option ? option.text : '请选择护理等级'
})

// 老人选项数据（从API获取）
const elderOptions = ref([
  { text: '请选择老人', value: null, elderId: null }
])

// 老人列表数据
const elderList = ref([])

const abilityOptions = [
  { text: '自理（300元/月）', value: '自理', price: 300 },
  { text: '半自理（800元/月）', value: '半自理', price: 800 },
  { text: '不能自理（1500元/月）', value: '不能自理', price: 1500 },
  { text: '特护（2500元/月）', value: '特护', price: 2500 }
]

// 护理等级选项（用于费用计算）
const careLevelOptions = [
  { text: '自理', value: '自理' },
  { text: '半护理', value: '半护理' },
  { text: '全护理', value: '全护理' }
]

const roomOptions = [
  { text: '普通床位', value: '1' },
  { text: '豪华床位', value: '2' },
  { text: '医疗床位', value: '3' }
]

// 获取床位费用
const bedFee = computed(() => {
  return bedInfo.value.bedFee
})

// 获取床位类型显示文字
const bedTypeText = computed(() => {
  switch (formData.value.roomType) {
    case '1':
      return '普通床位'
    case '2':
      return '豪华床位'
    case '3':
      return '医疗床位'
    default:
      return '普通床位'
  }
})

// 获取护理费用
const careFee = computed(() => {
  switch (formData.value.careLevel) {
    case '自理':
      return bedInfo.value.selfCarePrice
    case '半护理':
      return bedInfo.value.halfCarePrice
    case '全护理':
      return bedInfo.value.fullCarePrice
    default:
      return bedInfo.value.selfCarePrice
  }
})

// 押金金额
const depositAmount = computed(() => {
  return bedInfo.value.depositFee
})

// 会员费
const memberFee = computed(() => {
  return bedInfo.value.memberFee
})

// 计算月服务费（床位费 + 护理费）
const monthlyPrice = computed(() => {
  return bedFee.value + careFee.value
})

// 计算总金额
const totalAmount = computed(() => {
  return depositAmount.value + memberFee.value + monthlyPrice.value * formData.value.months
})

// 获取老人列表
const fetchElderList = async () => {
  try {
    const response = await getElderList()
    if (response.code === 200 && response.data) {
      elderList.value = response.data

      // 更新选择器选项
      elderOptions.value = [
        { text: '请选择老人', value: null, elderId: null },
        ...response.data.map(elder => ({
          text: `${elder.elderName} (${elder.genderText} ${elder.age}岁 ${elder.careLevelText})`,
          value: elder.elderName,
          elderId: elder.elderId,
          careLevel: elder.careLevel,
          abilityLevel: elder.careLevelText
        }))
      ]
    }
  } catch (error) {
    console.error('获取老人列表失败:', error)
    showToast('获取老人列表失败')
  }
}

// 获取床位价格
const getBedPrice = async () => {
  if (!route.params.institutionId || !formData.value.roomType) return

  try {
    // 调用真实的API获取床位价格信息
    const response = await getBedPriceApi(route.params.institutionId, formData.value.roomType)
    if (response.code === 200 && response.data) {
      // 更新床位信息
      bedInfo.value = {
        bedFee: response.data.bedFee || 500,
        selfCarePrice: response.data.selfCarePrice || 500,
        halfCarePrice: response.data.halfCarePrice || 800,
        fullCarePrice: response.data.fullCarePrice || 1200,
        memberFee: response.data.memberFee || 5000,
        depositFee: response.data.depositFee || 10000
      }
    }
  } catch (error) {
    console.error('获取床位价格失败:', error)

    // 显示更友好的错误提示
    let errorMessage = '获取床位价格信息失败'
    if (error.response && error.response.data && error.response.data.msg) {
      errorMessage = error.response.data.msg
    }
    showToast(errorMessage)

    // 使用默认数据作为备用
    const priceMap = {
      '1': { bedFee: 350, memberFee: 3000, depositFee: 8000 },
      '2': { bedFee: 500, memberFee: 5000, depositFee: 10000 },
      '3': { bedFee: 1200, memberFee: 10000, depositFee: 15000 }
    }
    const defaultPrices = priceMap[formData.value.roomType] || priceMap['1']
    bedInfo.value = {
      bedFee: defaultPrices.bedFee,
      selfCarePrice: 500,
      halfCarePrice: 800,
      fullCarePrice: 1200,
      memberFee: defaultPrices.memberFee,
      depositFee: defaultPrices.depositFee
    }
  }
}

// 老人选择确认
const onElderConfirm = (value) => {
  const selected = value.selectedOptions[0]
  formData.value.elderName = selected.value
  formData.value.elderId = selected.elderId

  // 根据老人信息设置能力等级和护理等级
  if (selected.abilityLevel) {
    formData.value.abilityLevel = selected.abilityLevel
  }
  if (selected.careLevel) {
    // 将数据库的护理等级代码映射到文字
    const careLevelMap = {
      '1': '自理',
      '2': '半护理',
      '3': '全护理'
    }
    formData.value.careLevel = careLevelMap[selected.careLevel] || '自理'
  }

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

// 护理等级确认
const onCareLevelConfirm = (value) => {
  formData.value.careLevel = value.selectedOptions[0].value
  showCareLevelPicker.value = false
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
  if (!formData.value.careLevel) {
    showToast('请选择护理等级')
    return
  }

  showLoadingToast({
    message: '正在提交订单...',
    forbidClick: true
  })

  try {
    // 准备订单数据
    const orderData = {
      institutionId: parseInt(route.params.institutionId),
      elderId: formData.value.elderId, // 使用真实的老人ID
      elderName: formData.value.elderName,
      abilityLevel: formData.value.abilityLevel,
      careLevel: formData.value.careLevel, // 护理等级
      roomType: formData.value.roomType,
      monthCount: formData.value.months, // 后端期望的参数名是monthCount
      remark: formData.value.remark
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

    // 显示更详细的错误信息
    let errorMessage = '提交订单失败，请重试'
    if (error.response && error.response.data && error.response.data.msg) {
      errorMessage = error.response.data.msg
    }

    showToast(errorMessage)
  }
}

// 页面加载时获取初始数据
onMounted(async () => {
  // 获取老人列表
  await fetchElderList()

  // 获取床位价格
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
