<template>
  <div class="user-review-page">
    <van-nav-bar title="我的评价" left-arrow @click-left="$router.back()" fixed placeholder />

    <van-tabs v-model:active="activeTab" sticky offset-top="46" @change="onTabChange">
      <van-tab title="待评价" name="pending">
        <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
          <van-list
            v-model:loading="loading"
            :finished="finished"
            finished-text="没有更多了"
            @load="onLoad"
          >
            <div v-for="item in reviewList" :key="item.orderId" class="review-item pending-item">
              <div class="institution-info">
                <van-image
                  width="60"
                  height="60"
                  :src="item.institutionCover"
                  fit="cover"
                  round
                />
                <div class="info-text">
                  <div class="institution-name">{{ item.institutionName }}</div>
                  <div class="order-time">入住时间: {{ item.checkInTime }}</div>
                </div>
              </div>
              <van-button type="primary" size="small" round @click="writeReview(item)">
                去评价
              </van-button>
            </div>
          </van-list>

          <van-empty v-if="!loading && reviewList.length === 0" description="暂无待评价订单" />
        </van-pull-refresh>
      </van-tab>

      <van-tab title="已评价" name="completed">
        <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
          <van-list
            v-model:loading="loading"
            :finished="finished"
            finished-text="没有更多了"
            @load="onLoad"
          >
            <div v-for="item in reviewList" :key="item.reviewId" class="review-item completed-item">
              <div class="institution-info">
                <van-image
                  width="60"
                  height="60"
                  :src="item.institutionCover"
                  fit="cover"
                  round
                />
                <div class="info-text">
                  <div class="institution-name">{{ item.institutionName }}</div>
                  <div class="review-time">评价时间: {{ item.reviewTime }}</div>
                </div>
              </div>

              <div class="review-content">
                <van-rate v-model="item.rating" :size="16" color="#ffd21e" void-icon="star" void-color="#eee" readonly />
                <div class="review-text">{{ item.content }}</div>
                <div v-if="item.images && item.images.length > 0" class="review-images">
                  <van-image
                    v-for="(img, index) in item.images"
                    :key="index"
                    width="80"
                    height="80"
                    :src="img"
                    fit="cover"
                  />
                </div>
              </div>
            </div>
          </van-list>

          <van-empty v-if="!loading && reviewList.length === 0" description="暂无评价记录" />
        </van-pull-refresh>
      </van-tab>
    </van-tabs>

    <!-- 评价弹窗 -->
    <van-popup v-model:show="showReviewDialog" position="bottom" :style="{ height: '80%' }">
      <div class="review-dialog">
        <div class="dialog-header">
          <van-icon name="cross" @click="showReviewDialog = false" />
          <span class="dialog-title">评价机构</span>
          <span class="dialog-submit" @click="submitReview">提交</span>
        </div>

        <div class="dialog-content">
          <div class="rating-section">
            <div class="rating-label">总体评分</div>
            <van-rate v-model="reviewForm.rating" :size="32" color="#ffd21e" void-icon="star" void-color="#eee" />
          </div>

          <van-field
            v-model="reviewForm.content"
            type="textarea"
            placeholder="请输入评价内容(选填)"
            rows="5"
            maxlength="200"
            show-word-limit
          />

          <van-uploader v-model="reviewForm.images" multiple :max-count="3" preview-size="80">
            <van-button icon="photograph" type="primary" size="small">上传图片</van-button>
          </van-uploader>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

const activeTab = ref('pending')
const reviewList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)

// 评价弹窗
const showReviewDialog = ref(false)
const reviewForm = ref({
  orderId: null,
  rating: 5,
  content: '',
  images: []
})

// 模拟待评价数据
const mockPendingReviews = [
  {
    orderId: 1,
    institutionName: '郑州市金水区花园口社区养老服务中心',
    institutionCover: 'https://via.placeholder.com/60x60',
    checkInTime: '2024-12-01'
  },
  {
    orderId: 2,
    institutionName: '郑州颐养家园养老院',
    institutionCover: 'https://via.placeholder.com/60x60',
    checkInTime: '2024-11-15'
  }
]

// 模拟已评价数据
const mockCompletedReviews = [
  {
    reviewId: 1,
    institutionName: '郑州市金水区花园口社区养老服务中心',
    institutionCover: 'https://via.placeholder.com/60x60',
    rating: 5,
    content: '环境很好，服务人员态度也很好，老人住得很开心。设施齐全，每天都有丰富的活动。',
    images: [
      'https://via.placeholder.com/80x80',
      'https://via.placeholder.com/80x80'
    ],
    reviewTime: '2025-01-10'
  },
  {
    reviewId: 2,
    institutionName: '河南省老干部康养中心',
    institutionCover: 'https://via.placeholder.com/60x60',
    rating: 4,
    content: '整体不错，饮食营养均衡，医护人员很专业。',
    images: [],
    reviewTime: '2024-12-20'
  }
]

// Tab切换
const onTabChange = () => {
  pageNum.value = 1
  finished.value = false
  reviewList.value = []
  onLoad()
}

// 下拉刷新
const onRefresh = () => {
  pageNum.value = 1
  finished.value = false
  reviewList.value = []
  onLoad()
  refreshing.value = false
}

// 加载列表
const onLoad = async () => {
  try {
    loading.value = true

    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    const mockData = activeTab.value === 'pending' ? mockPendingReviews : mockCompletedReviews

    // 分页处理
    const startIndex = (pageNum.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    const list = mockData.slice(startIndex, endIndex)

    if (list.length === 0) {
      finished.value = true
    } else {
      reviewList.value = [...reviewList.value, ...list]
      pageNum.value++

      if (endIndex >= mockData.length) {
        finished.value = true
      }
    }
  } catch (error) {
    showToast('加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 写评价
const writeReview = (item) => {
  reviewForm.value = {
    orderId: item.orderId,
    rating: 5,
    content: '',
    images: []
  }
  showReviewDialog.value = true
}

// 提交评价
const submitReview = async () => {
  if (!reviewForm.value.content) {
    showToast('请输入评价内容')
    return
  }

  // 模拟提交
  await new Promise(resolve => setTimeout(resolve, 500))

  showToast('评价成功')
  showReviewDialog.value = false

  // 刷新列表
  activeTab.value = 'completed'
  onRefresh()
}

onMounted(() => {
  // 初始加载
})
</script>

<style scoped>
.user-review-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.review-item {
  background: #fff;
  margin: 12px;
  border-radius: 8px;
  padding: 16px;
}

.pending-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.institution-info {
  display: flex;
  gap: 12px;
  flex: 1;
}

.info-text {
  display: flex;
  flex-direction: column;
  gap: 6px;
  justify-content: center;
}

.institution-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.order-time,
.review-time {
  font-size: 13px;
  color: #999;
}

.completed-item {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-content {
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

.review-text {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 8px 0;
}

.review-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-top: 8px;
}

.review-dialog {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f5f5f5;
}

.dialog-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.dialog-submit {
  font-size: 14px;
  color: #1989fa;
}

.dialog-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.rating-section {
  text-align: center;
  padding: 20px 0;
}

.rating-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
}
</style>
