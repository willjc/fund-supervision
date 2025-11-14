<template>
  <div class="refund-review-page">
    <van-nav-bar
      title="退款审核"
      left-arrow
      @click-left="$router.back()"
      fixed
      placeholder
    />

    <div class="review-content">
      <!-- 机构信息卡片 -->
      <div class="institution-card">
        <div class="institution-header">
          <van-image
            width="60"
            height="60"
            :src="refundInfo.institutionLogo"
            fit="cover"
          />
          <div class="institution-info">
            <div class="institution-name">{{ refundInfo.institutionName }}</div>
            <div class="institution-detail">
              床位数: {{ refundInfo.bedCount }} (可住{{ refundInfo.availableBedsText }})
            </div>
            <div class="institution-address">
              详细地址: {{ refundInfo.address }}
            </div>
          </div>
        </div>
      </div>

      <!-- 退款金额信息 -->
      <div class="refund-amount-section">
        <div class="amount-row main-amount">
          <span class="label">退款金额</span>
          <span class="value">{{ refundInfo.totalAmount }}元</span>
        </div>

        <div class="amount-detail">
          <div class="detail-row">
            <span class="label">押金</span>
            <span class="value">{{ refundInfo.depositAmount }}元</span>
          </div>
          <div class="detail-row">
            <span class="label">会员费</span>
            <span class="value">{{ refundInfo.memberAmount }}元</span>
          </div>
          <div class="detail-row">
            <span class="label">服务费</span>
            <span class="value">{{ refundInfo.serviceAmount }}元</span>
          </div>
        </div>
      </div>

      <!-- 退款详细信息 -->
      <div class="refund-info-card">
        <div class="info-row">
          <span class="label">订单编号</span>
          <span class="value">{{ refundInfo.orderNo }}</span>
        </div>

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

        <div class="info-row vertical">
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

      <!-- 底部按钮(仅用户端查看) -->
      <div v-if="refundInfo.status === 'pending'" class="action-button">
        <van-button
          round
          block
          type="primary"
          color="#ee0a24"
          @click="handleCancel"
        >
          取消退款
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showSuccessToast, showConfirmDialog, showImagePreview } from 'vant'

const route = useRoute()
const router = useRouter()

// 退款信息
const refundInfo = ref({
  institutionName: '郑州市金水区花园口社区养老服务中心',
  institutionLogo: 'https://via.placeholder.com/60x60',
  bedCount: 80,
  availableBedsText: '可住位置贰拾捌张床位',
  address: '郑州在郑州郑州郑州郑州让计划郑州在郑州郑州郑州',
  totalAmount: 3000,
  depositAmount: 1000,
  memberAmount: 1000,
  serviceAmount: 1000,
  orderNo: '748645136541354313',
  refundNo: '748645136541354313',
  applyTime: '2025-09-09 12:12:12',
  refundMethod: '原路返回',
  refundReason: '原因原因原因原因原因原因原因原因',
  refundDesc: '说明',
  status: 'pending', // 'pending' | 'approved' | 'rejected'
  images: [
    'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
    'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
  ]
})

// 预览图片
const previewImages = (startPosition) => {
  showImagePreview({
    images: refundInfo.value.images,
    startPosition
  })
}

// 取消退款
const handleCancel = async () => {
  try {
    await showConfirmDialog({
      title: '取消退款',
      message: '确定要取消退款申请吗?'
    })

    // 模拟取消
    await new Promise(resolve => setTimeout(resolve, 1000))

    showSuccessToast('已取消')

    setTimeout(() => {
      router.back()
    }, 1000)
  } catch (error) {
    // 用户取消
  }
}

// 加载退款详情
const loadRefundDetail = async () => {
  try {
    const refundId = route.params.id
    // TODO: 调用API获取退款详情
    console.log('加载退款详情:', refundId)
  } catch (error) {
    showToast('加载失败')
  }
}

onMounted(() => {
  loadRefundDetail()
})
</script>

<style scoped>
.refund-review-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.review-content {
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
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
}

.institution-detail {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.institution-address {
  font-size: 11px;
  color: #999;
  line-height: 1.5;
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

/* 底部按钮 */
.action-button {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: #fff;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

.van-button {
  height: 48px;
  font-size: 16px;
  font-weight: 500;
}
</style>
