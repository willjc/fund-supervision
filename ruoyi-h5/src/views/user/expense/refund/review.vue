<template>
  <div class="refund-review-page">

    <div class="review-content">
      <!-- 退款金额信息 -->
      <div class="refund-amount-section">
        <div class="amount-row main-amount">
          <span class="label">退款金额</span>
          <span class="value">¥{{ formatAmount(refundInfo.refundAmount) }}</span>
        </div>

        <div class="amount-detail">
          <div class="detail-row">
            <span class="label">服务费</span>
            <span class="value">¥{{ formatAmount(refundInfo.serviceRefundAmount) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">押金</span>
            <span class="value">¥{{ formatAmount(refundInfo.depositRefundAmount) }}</span>
          </div>
          <div class="detail-row">
            <span class="label">会员费</span>
            <span class="value">¥{{ formatAmount(refundInfo.memberRefundAmount) }}</span>
          </div>
        </div>
      </div>

      <!-- 退款详细信息 -->
      <div class="refund-info-card">
        <div class="info-row">
          <span class="label">退款单号</span>
          <span class="value">{{ refundInfo.refundNo }}</span>
        </div>

        <div class="info-row">
          <span class="label">申请时间</span>
          <span class="value">{{ refundInfo.applyTime }}</span>
        </div>

        <div class="info-row">
          <span class="label">退款方式</span>
          <span class="value">{{ refundInfo.refundMethod }}</span>
        </div>

        <div class="info-row">
          <span class="label">退款原因</span>
          <span class="value">{{ refundInfo.refundReason }}</span>
        </div>

        <div v-if="refundInfo.refundDesc" class="info-row vertical">
          <span class="label">退款说明</span>
          <span class="value">{{ refundInfo.refundDesc }}</span>
        </div>

        <!-- 退款凭证 -->
        <div v-if="refundInfo.images && refundInfo.images.length > 0" class="info-row vertical">
          <span class="label">退款凭证</span>
          <div class="image-list">
            <van-image
              v-for="(img, index) in refundInfo.images"
              :key="index"
              width="80"
              height="80"
              :src="img"
              fit="cover"
              @click="previewImages(index)"
            />
          </div>
        </div>
      </div>

      <!-- 审核结果 (仅当已通过或已拒绝时显示) -->
      <div v-if="refundInfo.status === '1' || refundInfo.status === '2' || refundInfo.status === 'approved' || refundInfo.status === 'rejected'" class="audit-result-card">
        <div class="audit-header">
          <van-icon :name="refundInfo.status === '1' || refundInfo.status === 'approved' ? 'checked' : 'close'" :color="refundInfo.status === '1' || refundInfo.status === 'approved' ? '#07c160' : '#ee0a24'" size="20" />
          <span class="audit-title">{{ refundInfo.status === '1' || refundInfo.status === 'approved' ? '审核通过' : '审核拒绝' }}</span>
        </div>
        <div class="audit-body">
          <div v-if="refundInfo.approver" class="audit-info-row">
            <span class="label">审核人:</span>
            <span class="value">{{ refundInfo.approver }}</span>
          </div>
          <div v-if="refundInfo.approveTime" class="audit-info-row">
            <span class="label">审核时间:</span>
            <span class="value">{{ refundInfo.approveTime }}</span>
          </div>
          <div v-if="(refundInfo.status === '2' || refundInfo.status === 'rejected') && refundInfo.approveRemark" class="audit-reason-row">
            <span class="label">拒绝原因:</span>
            <span class="value reject-reason">{{ refundInfo.approveRemark }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast, showImagePreview } from 'vant'
import { getRefundDetail } from '@/api/refund'

const route = useRoute()
const router = useRouter()

// 退款信息
const refundInfo = ref({
  refundNo: '',
  refundAmount: 0,
  serviceRefundAmount: 0,
  depositRefundAmount: 0,
  memberRefundAmount: 0,
  applyTime: '',
  refundMethod: '',
  refundReason: '',
  refundDesc: '',
  status: '0',
  images: [],
  // 审核信息
  approver: '',
  approveTime: '',
  approveRemark: ''
})

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toFixed(2)
}

// 预览图片
const previewImages = (startPosition) => {
  showImagePreview({
    images: refundInfo.value.images || [],
    startPosition
  })
}

// 加载退款详情
const loadRefundDetail = async () => {
  try {
    showLoadingToast({
      message: '加载中...',
      forbidClick: true,
      duration: 0
    })

    const refundId = route.params.id
    const response = await getRefundDetail(refundId)

    closeToast()

    if (response.code === 200 && response.data) {
      const data = response.data
      refundInfo.value = {
        refundNo: data.refundNo || '',
        refundAmount: data.refundAmount || 0,
        serviceRefundAmount: data.serviceRefundAmount || 0,
        depositRefundAmount: data.depositRefundAmount || 0,
        memberRefundAmount: data.memberRefundAmount || 0,
        applyTime: data.createTime || data.applyTime || '',
        refundMethod: '账户退款', // 默认退款方式
        refundReason: data.refundReason || '',
        refundDesc: data.refundDesc || '',
        status: data.refundStatus || data.status || '0',
        images: data.evidenceImages || [],
        // 审核信息
        approver: data.approver || '',
        approveTime: formatApproveTime(data.approveTime),
        approveRemark: data.approveRemark || ''
      }
    } else {
      showToast(response.msg || '获取退款详情失败')
    }
  } catch (error) {
    closeToast()
    console.error('加载退款详情失败:', error)
    showToast('加载失败，请稍后重试')
  }
}

// 格式化审核时间
const formatApproveTime = (time) => {
  if (!time) return ''
  // 如果是日期对象或时间戳，格式化为字符串
  const date = new Date(time)
  if (isNaN(date.getTime())) return time
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

onMounted(() => {
  loadRefundDetail()
})
</script>

<style scoped>
.refund-review-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 30px;
}

.review-content {
  padding: 12px;
}

/* 退款金额区域 */
.refund-amount-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.amount-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.amount-row.main-amount {
  padding-bottom: 12px;
  margin-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.amount-row.main-amount .label {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.amount-row.main-amount .value {
  font-size: 20px;
  font-weight: bold;
  color: #ee0a24;
}

.amount-detail {
  display: flex;
  justify-content: space-around;
}

.detail-row {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.detail-row .label {
  font-size: 12px;
  color: #999;
}

.detail-row .value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 退款信息卡片 */
.refund-info-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row.vertical {
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.info-row .label {
  font-size: 13px;
  color: #999;
}

.info-row .value {
  font-size: 13px;
  color: #333;
  text-align: right;
  word-break: break-all;
}

.info-row.vertical .value {
  text-align: left;
  line-height: 1.6;
}

.image-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.image-list .van-image {
  border-radius: 6px;
  cursor: pointer;
}

/* 审核结果卡片 */
.audit-result-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.audit-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.audit-title {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.audit-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.audit-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}

.audit-info-row .label {
  color: #999;
}

.audit-info-row .value {
  color: #333;
}

.audit-reason-row {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 10px;
  background-color: #fff5f5;
  border-radius: 6px;
  margin-top: 4px;
}

.audit-reason-row .label {
  font-size: 13px;
  color: #999;
}

.audit-reason-row .value {
  font-size: 13px;
  color: #ee0a24;
  line-height: 1.6;
}
</style>
