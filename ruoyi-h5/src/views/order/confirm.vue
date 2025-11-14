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

      <!-- 费用计算 -->
      <van-cell-group inset title="费用明细">
        <van-cell title="押金费用(一次性缴纳)" :value="`${depositAmount}元`" />
        <van-cell title="会员费(一次性缴纳)" :value="`${memberFee}元`" />
        <van-cell title="服务费单价" :value="`${monthlyPrice}元/月`" />
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
        <van-cell title="合计">
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
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

// 表单数据
const formData = ref({
  elderName: '张伟',
  abilityLevel: '自理',
  roomType: '单人间(朝全2床位)',
  packages: ['meal_normal', 'nursing_self', 'other', 'bed_single'],
  months: 1,
  remark: ''
})

// 选择器显示状态
const showElderPicker = ref(false)
const showAbilityPicker = ref(false)
const showRoomPicker = ref(false)

// 选项数据
const elderOptions = [
  { text: '张伟', value: '张伟' },
  { text: '李明', value: '李明' },
  { text: '王芳', value: '王芳' },
  { text: '赵强', value: '赵强' }
]
const abilityOptions = [
  { text: '自理', value: '自理' },
  { text: '半自理', value: '半自理' },
  { text: '不能自理', value: '不能自理' },
  { text: '特护', value: '特护' }
]
const roomOptions = [
  { text: '单人间(朝全2床位)', value: '单人间(朝全2床位)' },
  { text: '双人间(朝全4床位)', value: '双人间(朝全4床位)' },
  { text: '三人间(朝全6床位)', value: '三人间(朝全6床位)' },
  { text: 'VIP房间', value: 'VIP房间' }
]

// 套餐选项
const packageOptions = [
  { id: 'meal_normal', name: '餐费(普通餐)', price: 900 },
  { id: 'meal_custom', name: '餐费(定制餐)', price: 1500 },
  { id: 'nursing_self', name: '护理费(自理老人)', price: 300 },
  { id: 'other', name: '其他(空调费、夏季、冬季)', price: 100 },
  { id: 'bed_single', name: '床位费(单人间)', price: 500 }
]

// 押金金额
const depositAmount = ref(10000)

// 会员费
const memberFee = ref(5000)

// 计算月服务费
const monthlyPrice = computed(() => {
  return packageOptions
    .filter(pkg => formData.value.packages.includes(pkg.id))
    .reduce((sum, pkg) => sum + pkg.price, 0)
})

// 计算总金额
const totalAmount = computed(() => {
  return depositAmount.value + memberFee.value + monthlyPrice.value * formData.value.months
})

// 老人选择确认
const onElderConfirm = (value) => {
  formData.value.elderName = value.selectedOptions[0].text
  showElderPicker.value = false
}

// 能力等级确认
const onAbilityConfirm = (value) => {
  formData.value.abilityLevel = value.selectedOptions[0].text
  showAbilityPicker.value = false
}

// 房间类型确认
const onRoomConfirm = (value) => {
  formData.value.roomType = value.selectedOptions[0].text
  showRoomPicker.value = false
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

  // 模拟提交
  await new Promise(resolve => setTimeout(resolve, 500))

  // 生成订单号
  const orderNo = `ORD${dayjs().format('YYYYMMDDHHmmss')}`

  // 跳转到收银台
  router.push({
    name: 'PaymentCashier',
    params: { orderId: orderNo },
    query: {
      amount: totalAmount.value,
      elderName: formData.value.elderName,
      institutionId: route.params.institutionId
    }
  })
}
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

:deep(.van-checkbox) {
  flex: 1;
}

:deep(.van-checkbox__label) {
  flex: 1;
}
</style>
