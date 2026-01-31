<template>
  <div class="refund-apply-page">

    <div class="form-content" v-if="!loading">
      <!-- 老人和机构信息 -->
      <van-cell-group inset title="退款信息">
        <van-cell title="退款老人" :value="elderName" />
        <van-cell title="养老机构" :value="institutionName" />
      </van-cell-group>

      <!-- 退款金额 -->
      <van-cell-group inset title="退款金额">
        <van-field
          v-model="formData.serviceRefundAmount"
          label="服务费退款"
          placeholder="请输入服务费退款金额"
          type="number"
          @input="calculateTotal"
        >
          <template #button>
            <span class="unit">元</span>
          </template>
        </van-field>
        <van-field
          v-model="formData.depositRefundAmount"
          label="押金退款"
          placeholder="请输入押金退款金额"
          type="number"
          @input="calculateTotal"
        >
          <template #button>
            <span class="unit">元</span>
          </template>
        </van-field>
        <van-field
          v-model="formData.memberRefundAmount"
          label="会员费退款"
          placeholder="请输入会员费退款金额"
          type="number"
          @input="calculateTotal"
        >
          <template #button>
            <span class="unit">元</span>
          </template>
        </van-field>
        <van-cell>
          <template #title>
            <span class="total-label">退款总额</span>
          </template>
          <template #value>
            <span class="total-amount">¥{{ formatAmount(totalAmount) }}</span>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 退款原因 -->
      <van-cell-group inset title="退款原因">
        <van-field
          v-model="formData.refundReason"
          label="退款原因"
          placeholder="请选择退款原因"
          readonly
          right-icon="arrow-down"
          @click="showReasonPicker = true"
          :rules="[{ required: true, message: '请选择退款原因' }]"
        />
      </van-cell-group>

      <!-- 退款说明 -->
      <van-cell-group inset title="退款说明">
        <van-field
          v-model="formData.refundDesc"
          type="textarea"
          placeholder="请详细描述退款说明"
          rows="3"
          maxlength="500"
          show-word-limit
        />
      </van-cell-group>

      <!-- 上传凭证 -->
      <van-cell-group inset title="上传凭证（最多3张）">
        <van-cell>
          <van-uploader
            v-model="imageList"
            :after-read="handleAfterRead"
            :before-delete="handleBeforeDelete"
            :max-count="3"
            :max-size="5 * 1024 * 1024"
            preview-size="80px"
            multiple
          />
          <div class="upload-tip">支持jpg、png格式，单张不超过5MB，最多3张</div>
        </van-cell>
      </van-cell-group>

      <!-- 提交按钮 -->
      <div class="submit-section">
        <van-button round block type="primary" native-type="submit" :loading="submitting" @click="onSubmit">
          提交申请
        </van-button>
      </div>
    </div>

    <van-skeleton v-else title :row="10" />

    <!-- 退款原因选择器 -->
    <van-popup v-model:show="showReasonPicker" position="bottom" round>
      <van-picker
        :columns="reasonOptions"
        @confirm="onReasonConfirm"
        @cancel="showReasonPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { applyRefund, uploadRefundImage } from '@/api/refund'
import { getAccountInfo } from '@/api/expense'
import { getInstitutionList } from '@/api/institution'

const router = useRouter()
const route = useRoute()

// 表单数据
const formData = ref({
  elderId: null,
  institutionId: null,
  institutionName: '',
  serviceRefundAmount: '',
  depositRefundAmount: '',
  memberRefundAmount: '',
  refundReason: '',
  refundDesc: ''
})

// 显示数据
const elderName = ref('')
const institutionName = ref('')

// 图片列表
const imageList = ref([])

// 选择器显示状态
const showReasonPicker = ref(false)

// 加载状态
const loading = ref(true)
const submitting = ref(false)

// 总退款金额
const totalAmount = ref('0.00')

// 机构选项
const institutionList = ref([])

// 退款原因选项
const reasonOptions = [
  { text: '服务质量不满意', value: '服务质量不满意' },
  { text: '家属要求', value: '家属要求' },
  { text: '健康原因', value: '健康原因' },
  { text: '经济原因', value: '经济原因' },
  { text: '其他原因', value: '其他原因' }
]

// 初始化
onMounted(async () => {
  await loadData()
  loading.value = false
})

// 加载数据
const loadData = async () => {
  const queryElderId = route.query.elderId
  const queryElderName = route.query.elderName

  if (!queryElderId) {
    showToast('缺少老人信息')
    setTimeout(() => router.back(), 1500)
    return
  }

  formData.value.elderId = queryElderId
  elderName.value = queryElderName || '未知老人'

  // 获取账户信息，从中获取机构ID
  try {
    const response = await getAccountInfo(queryElderId)
    if (response.code === 200 && response.data) {
      formData.value.institutionId = response.data.institutionId
      institutionName.value = response.data.institutionName || '养老机构'
    }
  } catch (error) {
    console.error('获取账户信息失败:', error)
  }
}

// 计算总金额
const calculateTotal = () => {
  const service = parseFloat(formData.value.serviceRefundAmount) || 0
  const deposit = parseFloat(formData.value.depositRefundAmount) || 0
  const member = parseFloat(formData.value.memberRefundAmount) || 0
  totalAmount.value = (service + deposit + member).toFixed(2)
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount || amount === '0.00') return '0.00'
  return amount
}

// 原因选择确认
const onReasonConfirm = (value) => {
  formData.value.refundReason = value.selectedOptions[0].value
  showReasonPicker.value = false
}

// 图片上传后的处理
const handleAfterRead = async (file) => {
  const files = Array.isArray(file) ? file : [file]

  for (const item of files) {
    item.status = 'uploading'
    item.message = '上传中...'

    try {
      const response = await uploadRefundImage(item.file)

      if (response.code === 200 && response.url) {
        item.serverUrl = response.url
        item.status = 'done'
        item.message = ''
        item.url = response.url
      } else {
        item.status = 'failed'
        item.message = '上传失败'
        showToast(response.msg || '图片上传失败')
      }
    } catch (error) {
      console.error('图片上传失败:', error)
      item.status = 'failed'
      item.message = '上传失败'
      showToast('图片上传失败，请重试')
    }
  }
}

// 图片删除前的处理
const handleBeforeDelete = (file) => {
  return new Promise((resolve) => {
    showToast('删除图片')
    resolve(true)
  })
}

// 提交表单
const onSubmit = async () => {
  // 验证必填项
  if (!formData.value.elderId) {
    showToast('缺少老人信息')
    return
  }
  if (!formData.value.institutionId) {
    showToast('缺少机构信息')
    return
  }

  const serviceAmount = parseFloat(formData.value.serviceRefundAmount) || 0
  const depositAmount = parseFloat(formData.value.depositRefundAmount) || 0
  const memberAmount = parseFloat(formData.value.memberRefundAmount) || 0
  const total = serviceAmount + depositAmount + memberAmount

  if (total <= 0) {
    showToast('退款金额必须大于0')
    return
  }

  if (serviceAmount < 0 || depositAmount < 0 || memberAmount < 0) {
    showToast('退款金额不能为负数')
    return
  }

  if (!formData.value.refundReason) {
    showToast('请选择退款原因')
    return
  }

  try {
    submitting.value = true
    showLoadingToast({
      message: '提交中...',
      forbidClick: true,
      duration: 0
    })

    // 构建图片数据
    const images = imageList.value
      .filter(file => file.serverUrl)
      .map(file => file.serverUrl)

    const submitData = {
      elderId: formData.value.elderId,
      institutionId: formData.value.institutionId,
      serviceRefundAmount: serviceAmount,
      depositRefundAmount: depositAmount,
      memberRefundAmount: memberAmount,
      refundReason: formData.value.refundReason,
      refundDesc: formData.value.refundDesc,
      evidenceImages: images.length > 0 ? images.join(',') : null
    }

    const response = await applyRefund(submitData)

    if (response.code === 200) {
      showToast('退款申请提交成功')
      setTimeout(() => {
        router.replace({
          path: '/order',
          query: { tab: '3' } // 跳转到退款tab
        })
      }, 1000)
    } else {
      showToast(response.msg || '提交失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    showToast('提交失败，请重试')
  } finally {
    submitting.value = false
    closeToast()
  }
}
</script>

<style scoped>
.refund-apply-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.form-content {
  padding: 12px 0 20px;
}

.van-cell-group {
  margin-bottom: 12px;
}

.unit {
  color: #999;
  font-size: 14px;
}

.total-label {
  font-weight: 500;
  color: #333;
}

.total-amount {
  font-size: 18px;
  font-weight: bold;
  color: #ee0a24;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: #fff;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

:deep(.van-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}
</style>
