<template>
  <div class="review-submit-page">

    <div class="content" v-if="!loading">
      <!-- 机构信息 -->
      <div class="institution-info">
        <h3>{{ institutionName }}</h3>
        <p class="order-info">订单金额：¥{{ orderAmount }}</p>
      </div>

      <!-- 评分区域 -->
      <div class="rating-section">
        <div class="rating-item">
          <div class="rating-label">总体评分</div>
          <van-rate
            v-model="review.rating"
            :size="30"
            color="#ffd21e"
            void-icon="star"
            void-color="#eee"
          />
          <span class="rating-text">{{ getRatingText(review.rating) }}</span>
        </div>
      </div>

      <!-- 评价内容 -->
      <div class="content-section">
        <van-field
          v-model="review.content"
          type="textarea"
          placeholder="分享您在养老机构的服务体验，您的评价对其他家属很重要..."
          :rows="6"
          maxlength="500"
          show-word-limit
          class="content-field"
        />
      </div>

      <!-- 图片上传 -->
      <div class="upload-section">
        <div class="upload-title">上传图片（最多9张）</div>
        <van-uploader
          v-model="imageList"
          :after-read="handleAfterRead"
          :before-delete="handleBeforeDelete"
          :max-count="9"
          :max-size="5 * 1024 * 1024"
          preview-size="80px"
          multiple
        />
        <div class="upload-tip">支持jpg、png格式，单张不超过5MB</div>
      </div>

      <!-- 提交按钮 -->
      <div class="submit-section">
        <van-button
          type="primary"
          block
          size="large"
          :loading="submitting"
          @click="handleSubmit"
          :disabled="!canSubmit"
        >
          提交评价
        </van-button>
      </div>
    </div>

    <van-loading v-else class="loading" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { submitReview, getReviewByOrderId } from '@/api/review'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
const orderId = ref(route.params.orderId)
const institutionName = ref(route.query.institutionName || '')
const orderAmount = ref(route.query.orderAmount || '')

// 评价数据
const review = ref({
  rating: 5,
  content: '',
  imageList: []
})

// 图片列表
const imageList = ref([])

// 计算是否可以提交
const canSubmit = computed(() => {
  return review.value.rating > 0 && review.value.content.trim().length > 0
})

// 获取评分文本
const getRatingText = (rating) => {
  const texts = ['', '非常差', '较差', '一般', '满意', '非常满意']
  return texts[rating] || ''
}

// 图片上传后的处理
const handleAfterRead = (file) => {
  // 这里可以添加图片压缩逻辑
  file.status = 'uploading'
  file.message = '上传中...'

  // 模拟上传成功
  setTimeout(() => {
    file.status = 'done'
    file.url = file.content // 实际项目中这里应该是服务器返回的URL
  }, 1000)
}

// 图片删除前的处理
const handleBeforeDelete = (file, detail) => {
  return new Promise((resolve) => {
    showConfirmDialog({
      title: '提示',
      message: '确定要删除这张图片吗？'
    }).then(() => {
      resolve(true)
    }).catch(() => {
      resolve(false)
    })
  })
}

// 检查是否已经评价过
const checkExistingReview = async () => {
  try {
    const response = await getReviewByOrderId(orderId.value)
    if (response.code === 200 && response.data) {
      showToast('该订单已经评价过了')
      router.back()
    }
  } catch (error) {
    console.error('检查评价状态失败:', error)
  }
}

// 提交评价
const handleSubmit = async () => {
  if (!canSubmit.value) {
    showToast('请完善评价内容')
    return
  }

  try {
    submitting.value = true

    // 构建图片数据
    const reviewImages = imageList.value.map((file, index) => ({
      name: file.name || `image_${index + 1}.jpg`,
      url: file.url || file.content,
      uid: Date.now() + '_' + index
    }))

    // 构建提交数据 - 将单个评分复制到三个字段，保持数据库兼容性
    const rating = review.value.rating
    const submitData = {
      orderId: orderId.value,
      environmentRating: rating,
      serviceRating: rating,
      priceRating: rating,
      content: review.value.content.trim(),
      imageList: reviewImages
    }

    const response = await submitReview(submitData)

    if (response.code === 200) {
      showToast('评价提交成功，等待审核')
      setTimeout(() => {
        router.push('/user/evaluation')
      }, 1500)
    } else {
      showToast(response.msg || '评价提交失败')
    }
  } catch (error) {
    console.error('提交评价失败:', error)
    showToast('评价提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  // 检查订单是否可以评价
  await checkExistingReview()
  loading.value = false
})
</script>

<style scoped>
.review-submit-page {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-top: 46px;
}

.content {
  padding: 16px;
  padding-bottom: 100px;
}

.institution-info {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.institution-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #323233;
}

.order-info {
  margin: 0;
  font-size: 14px;
  color: #969799;
}

.rating-section {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.rating-item {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.rating-item:last-child {
  margin-bottom: 0;
}

.rating-label {
  width: 80px;
  font-size: 14px;
  color: #323233;
}

.rating-text {
  margin-left: 12px;
  font-size: 12px;
  color: #969799;
}

.content-section {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.content-field {
  border: none;
  padding: 0;
}

.upload-section {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.upload-title {
  font-size: 14px;
  color: #323233;
  margin-bottom: 12px;
}

.upload-tip {
  font-size: 12px;
  color: #969799;
  margin-top: 8px;
}

.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  background: #fff;
  box-shadow: 0 -2px 12px rgba(100, 101, 102, 0.12);
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}
</style>