<template>
  <div class="evaluation-page">
    <van-nav-bar title="我的评价" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="evaluation-content">
      <!-- Tab切换 -->
      <van-tabs
        v-model:active="activeTab"
        @change="onTabChange"
        color="#667eea"
        sticky
        offset-top="46"
      >
        <van-tab title="待评价" name="pending">
          <div class="evaluation-list">
            <!-- 加载状态 -->
            <van-loading v-if="loading && pendingList.length === 0" class="loading-center" />

            <!-- 待评价订单列表 -->
            <div
              v-for="item in pendingList"
              :key="item.id"
              class="evaluation-item"
            >
              <div class="institution-info">
                <van-image
                  width="60"
                  height="60"
                  :src="item.institutionImage"
                  fit="cover"
                  round
                />
                <div class="institution-detail">
                  <div class="institution-name">{{ item.institutionName }}</div>
                  <div class="order-info">订单号: {{ item.orderNo }}</div>
                  <div class="order-time">下单时间: {{ formatTime(item.createTime) }}</div>
                  <div class="order-amount">订单金额: ¥{{ formatAmount(item.orderAmount) }}</div>
                </div>
              </div>

              <div class="evaluation-action">
                <van-button
                  type="primary"
                  size="small"
                  @click="goToReview(item.id, item.institutionName, item.orderAmount)"
                >
                  去评价
                </van-button>
              </div>
            </div>

            <!-- 空状态 -->
            <div v-if="!loading && pendingList.length === 0" class="empty-state">
              <van-empty description="暂无待评价订单" />
            </div>
          </div>
        </van-tab>

        <van-tab title="已评价" name="completed">
          <div class="evaluation-list">
            <!-- 加载状态 -->
            <van-loading v-if="loading && completedList.length === 0" class="loading-center" />

            <!-- 评价列表 -->
            <div
              v-for="item in completedList"
              :key="item.id"
              class="evaluation-item"
            >
              <div class="institution-info">
                <van-image
                  width="60"
                  height="60"
                  :src="item.institutionImage"
                  fit="cover"
                  round
                />
                <div class="institution-detail">
                  <div class="institution-name">{{ item.institutionName }}</div>
                  <div class="order-info">订单号: {{ item.orderNo }}</div>
                  <div class="evaluation-time">评价时间: {{ formatTime(item.evaluationTime) }}</div>
                  <div class="review-status">
                    <van-tag v-if="item.status === 0" type="warning" size="small">待审核</van-tag>
                    <van-tag v-else-if="item.status === 1" type="success" size="small">已通过</van-tag>
                    <van-tag v-else-if="item.status === 2" type="danger" size="small">已拒绝</van-tag>
                  </div>
                </div>
              </div>

              <div class="evaluation-content-card">
                <van-rate
                  v-model="item.rating"
                  readonly
                  :size="16"
                  color="#ffd21e"
                  void-color="#eee"
                />
                <div class="evaluation-text">{{ item.content || '用户暂未填写评价内容' }}</div>
                <div v-if="item.images && item.images.length > 0" class="evaluation-images">
                  <van-image
                    v-for="(img, index) in item.images"
                    :key="index"
                    width="60"
                    height="60"
                    :src="img"
                    fit="cover"
                    @click="previewImages(item.images, index)"
                  />
                </div>
              </div>
            </div>

            <!-- 加载更多 -->
            <div v-if="completedList.length > 0" class="load-more">
              <van-loading v-if="loading" size="16px" />
              <div v-else-if="finished" class="no-more">没有更多了</div>
              <van-button v-else type="primary" plain size="small" @click="onLoadMore">
                加载更多
              </van-button>
            </div>

            <!-- 空状态 -->
            <div v-if="!loading && completedList.length === 0" class="empty-state">
              <van-empty description="暂无评价记录" />
            </div>
          </div>
        </van-tab>
      </van-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showImagePreview, showToast } from 'vant'
import { getUserReviewList, getPendingEvaluationList } from '@/api/review'

const router = useRouter()

// 活动Tab
const activeTab = ref('completed')
const loading = ref(false)
const finished = ref(false)

// 待评价列表 - 暂时为空，后续可以获取已支付但未评价的订单
const pendingList = ref([])

// 已评价列表 - 从数据库获取真实的评价记录
const completedList = ref([])

// 分页信息
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// Tab切换
const onTabChange = (name) => {
  activeTab.value = name
  if (name === 'completed' && completedList.value.length === 0) {
    loadUserReviews()
  } else if (name === 'pending' && pendingList.value.length === 0) {
    loadPendingReviews()
  }
}

// 加载用户评价记录
const loadUserReviews = async (reset = true) => {
  if (loading.value) return

  try {
    loading.value = true

    if (reset) {
      pageNum.value = 1
      finished.value = false
      completedList.value = []
    }

    const response = await getUserReviewList(pageNum.value, pageSize.value)

    if (response.code === 200) {
      const { rows, total: totalCount } = response.data

      // 处理图片数据
      const processedReviews = rows.map(review => {
        let images = []
        if (review.imageList && review.imageList.length > 0) {
          images = review.imageList.map(img => img.url)
        }

        // 处理机构图片 - 统一使用main_picture字段
        let institutionImage = 'https://via.placeholder.com/60x60'
        if (review.institutionImage) {
          institutionImage = review.institutionImage
        }

        // 处理星���评分 - van-rate需要整数，将平均评分四舍五入为整数
        let rating = 5
        if (review.averageRating !== null && review.averageRating !== undefined) {
          const avgRating = Number(review.averageRating)
          if (!isNaN(avgRating) && avgRating >= 1 && avgRating <= 5) {
            rating = Math.round(avgRating) // 四舍五入为最接近的整数
          }
        }

        return {
          id: review.reviewId,
          institutionName: review.institutionName || '养老机构',
          institutionImage: institutionImage,
          orderNo: review.orderNo || review.orderId, // 优先使用orderNo，fallback到orderId
          orderTime: review.createTime,
          evaluationTime: review.createTime,
          rating: rating,
          content: review.content,
          status: review.status, // 0-待审核, 1-已通过, 2-已拒绝
          images: images
        }
      })

      if (reset) {
        completedList.value = processedReviews
      } else {
        completedList.value.push(...processedReviews)
      }

      total.value = totalCount

      if (completedList.value.length >= total.value) {
        finished.value = true
      }
    } else {
      showToast(response.msg || '获取评价记录失败')
    }
  } catch (error) {
    console.error('加载评价记录失败:', error)
    showToast('加载评价记录失败')
  } finally {
    loading.value = false
  }
}

// 加载待评价订单
const loadPendingReviews = async () => {
  if (loading.value) return

  try {
    loading.value = true

    const response = await getPendingEvaluationList()

    if (response.code === 200) {
      const { rows } = response.data

      // 处理待评价订单数据
      const processedOrders = rows.map(order => ({
        id: order.orderId,
        orderNo: order.orderNo,
        institutionName: order.institutionName || '养老机构',
        institutionImage: order.institutionImage || 'https://via.placeholder.com/60x60',
        orderAmount: order.orderAmount,
        createTime: order.createTime
      }))

      pendingList.value = processedOrders
    } else {
      showToast(response.msg || '获取待评价订单失败')
    }
  } catch (error) {
    console.error('加载待评价订单失败:', error)
    showToast('加载待评价订单失败')
  } finally {
    loading.value = false
  }
}

// 跳转到评价页面
const goToReview = (orderId, institutionName, orderAmount) => {
  router.push({
    path: `/review/submit/${orderId}`,
    query: {
      institutionName,
      orderAmount
    }
  })
}

// 加载更多
const onLoadMore = () => {
  if (!finished.value && !loading.value) {
    pageNum.value++
    loadUserReviews(false)
  }
}

// 图片预览
const previewImages = (images, startPosition = 0) => {
  showImagePreview({
    images,
    startPosition,
    closeable: true
  })
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleDateString() + ' ' + date.toLocaleTimeString().slice(0, 5)
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toFixed(2)
}

onMounted(() => {
  if (activeTab.value === 'completed') {
    loadUserReviews()
  }
})
</script>

<style scoped>
.evaluation-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.evaluation-content {
  padding-bottom: 20px;
}

:deep(.van-tabs__wrap) {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

:deep(.van-tab) {
  font-size: 15px;
  font-weight: 500;
}

/* 评价列表 */
.evaluation-list {
  padding: 12px;
  min-height: 400px;
}

.evaluation-item {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.institution-info {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
}

.institution-detail {
  flex: 1;
  margin-left: 12px;
}

.institution-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.order-info {
  font-size: 13px;
  color: #666;
  margin-bottom: 2px;
}

.order-time {
  font-size: 13px;
  color: #666;
  margin-bottom: 2px;
}

.evaluation-time {
  font-size: 13px;
  color: #666;
  margin-bottom: 2px;
}

.review-status {
  margin-top: 4px;
}

.evaluation-content-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px;
}

.evaluation-text {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin-top: 8px;
  margin-bottom: 8px;
}

.evaluation-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.evaluation-images :deep(.van-image) {
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.loading-center {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.load-more {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
}

.no-more {
  font-size: 14px;
  color: #999;
}

.evaluation-action {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.order-amount {
  font-size: 14px;
  color: #ee0a24;
  font-weight: 500;
}
</style>