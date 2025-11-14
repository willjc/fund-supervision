<template>
  <div class="refund-apply-page">
    <van-nav-bar
      title="申请退款"
      left-arrow
      @click-left="$router.back()"
      fixed
      placeholder
    />

    <div class="refund-content">
      <!-- 机构信息卡片 -->
      <div class="institution-card">
        <div class="institution-header">
          <van-image
            width="50"
            height="50"
            :src="institutionInfo.logo"
            fit="cover"
            round
          />
          <div class="institution-info">
            <div class="institution-name">{{ institutionInfo.name }}</div>
            <div class="institution-detail">
              <span>床位数: {{ institutionInfo.bedCount }}</span>
              <span class="divider">|</span>
              <span>评分: {{ institutionInfo.rating }}</span>
            </div>
            <div class="institution-address">
              <van-icon name="location-o" size="12" />
              {{ institutionInfo.address }}
            </div>
          </div>
        </div>
      </div>

      <!-- 退款表单 -->
      <van-form @submit="onSubmit">
        <!-- 服务费退款金额 -->
        <van-field
          v-model="formData.serviceAmount"
          name="serviceAmount"
          label="服务费退款金额"
          placeholder="请输入与机构协商的退款金额"
          type="number"
          :rules="[{ required: true, message: '请输入服务费退款金额' }]"
          input-align="right"
        >
          <template #button>
            <span class="unit">元</span>
          </template>
        </van-field>

        <!-- 押金退款金额 -->
        <van-field
          v-model="formData.depositAmount"
          name="depositAmount"
          label="押金退款金额"
          placeholder="请输入与机构协商的退款金额"
          type="number"
          :rules="[{ required: true, message: '请输入押金退款金额' }]"
          input-align="right"
        >
          <template #button>
            <span class="unit">元</span>
          </template>
        </van-field>

        <!-- 会员费退款金额 -->
        <van-field
          v-model="formData.memberAmount"
          name="memberAmount"
          label="会员费退款金额"
          placeholder="请输入与机构协商的退款金额"
          type="number"
          input-align="right"
        >
          <template #button>
            <span class="unit">元</span>
          </template>
        </van-field>

        <!-- 退款原因选择 -->
        <van-field
          v-model="refundReasonText"
          is-link
          readonly
          name="refundReason"
          label="退款原因"
          placeholder="请选择"
          :rules="[{ required: true, message: '请选择退款原因' }]"
          @click="showReasonPicker = true"
        />

        <!-- 退款说明 -->
        <van-field
          v-model="formData.refundDesc"
          rows="3"
          autosize
          label="退款说明"
          type="textarea"
          maxlength="200"
          placeholder="请输入退款说明,选填"
          show-word-limit
        />

        <!-- 上传凭证 -->
        <van-field name="uploader" label="上传凭证">
          <template #input>
            <van-uploader
              v-model="formData.fileList"
              multiple
              :max-count="3"
              :after-read="afterRead"
            />
          </template>
        </van-field>

        <!-- 总退款金额显示 -->
        <div class="total-refund">
          <span class="label">总退款金额:</span>
          <span class="amount">¥{{ totalRefundAmount }}</span>
        </div>

        <!-- 提交按钮 -->
        <div class="submit-wrapper">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
            color="#667eea"
            :loading="submitLoading"
          >
            提交
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 退款原因选择器 -->
    <van-popup v-model:show="showReasonPicker" position="bottom">
      <van-picker
        :columns="refundReasonOptions"
        @confirm="onReasonConfirm"
        @cancel="showReasonPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showSuccessToast } from 'vant'

const route = useRoute()
const router = useRouter()

// 机构信息(模拟数据)
const institutionInfo = ref({
  name: '郑州市金水区花园口社区养老服务中心',
  logo: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
  bedCount: 80,
  rating: 4.5,
  address: '郑州市金水区花园口街道'
})

// 表单数据
const formData = ref({
  serviceAmount: '',
  depositAmount: '',
  memberAmount: '',
  refundReason: '',
  refundDesc: '',
  fileList: []
})

// 退款原因选项
const refundReasonOptions = [
  { text: '服务质量不满意', value: '1' },
  { text: '家属要求', value: '2' },
  { text: '健康原因', value: '3' },
  { text: '经济原因', value: '4' },
  { text: '其他原因', value: '5' }
]

// 退款原因文本
const refundReasonText = computed(() => {
  const reason = refundReasonOptions.find(item => item.value === formData.value.refundReason)
  return reason ? reason.text : ''
})

// 退款原因选择器显示
const showReasonPicker = ref(false)

// 提交加载状态
const submitLoading = ref(false)

// 总退款金额
const totalRefundAmount = computed(() => {
  const service = parseFloat(formData.value.serviceAmount) || 0
  const deposit = parseFloat(formData.value.depositAmount) || 0
  const member = parseFloat(formData.value.memberAmount) || 0
  return (service + deposit + member).toFixed(2)
})

// 退款原因确认
const onReasonConfirm = (value) => {
  formData.value.refundReason = value.selectedValues[0]
  showReasonPicker.value = false
}

// 文件上传后处理
const afterRead = (file) => {
  console.log('上传文件:', file)
  // TODO: 实际上传文件到服务器
}

// 提交表单
const onSubmit = async (values) => {
  try {
    submitLoading.value = true

    // 验证总金额
    if (parseFloat(totalRefundAmount.value) <= 0) {
      showToast('请至少输入一项退款金额')
      return
    }

    // 模拟提交
    await new Promise(resolve => setTimeout(resolve, 1500))

    showSuccessToast('提交成功')

    // 返回费用页面
    setTimeout(() => {
      router.back()
    }, 1000)
  } catch (error) {
    showToast('提交失败')
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped>
.refund-apply-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.refund-content {
  padding: 12px;
}

/* 机构信息卡片 */
.institution-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.institution-header {
  display: flex;
  gap: 12px;
}

.institution-info {
  flex: 1;
}

.institution-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
}

.institution-detail {
  font-size: 13px;
  color: #999;
  margin-bottom: 4px;
}

.divider {
  margin: 0 8px;
}

.institution-address {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 表单样式 */
.van-form {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.unit {
  color: #999;
  font-size: 14px;
}

/* 总退款金额 */
.total-refund {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  margin-top: 12px;
}

.total-refund .label {
  font-size: 15px;
}

.total-refund .amount {
  font-size: 24px;
  font-weight: bold;
}

/* 提交按钮 */
.submit-wrapper {
  padding: 20px 16px 0;
}

.van-button {
  height: 48px;
  font-size: 16px;
  font-weight: 500;
}
</style>
