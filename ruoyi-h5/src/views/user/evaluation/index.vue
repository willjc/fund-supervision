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
                  <div class="order-time">{{ item.orderTime }}</div>
                </div>
              </div>

              <van-button
                type="primary"
                size="small"
                round
                @click="goToWrite(item)"
              >
                去评价
              </van-button>
            </div>

            <div v-if="pendingList.length === 0" class="empty-state">
              <van-empty description="暂无待评价订单" />
            </div>
          </div>
        </van-tab>

        <van-tab title="已评价" name="completed">
          <div class="evaluation-list">
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
                  <div class="evaluation-time">评价时间: {{ item.evaluationTime }}</div>
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
                <div class="evaluation-text">{{ item.content }}</div>
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

            <div v-if="completedList.length === 0" class="empty-state">
              <van-empty description="暂无评价记录" />
            </div>
          </div>
        </van-tab>
      </van-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showImagePreview } from 'vant'

const router = useRouter()

// 活动Tab
const activeTab = ref('pending')

// 待评价列表
const pendingList = ref([
  {
    id: 1,
    institutionName: '郑州市金水区花园口社区养老服务中心',
    institutionImage: 'https://via.placeholder.com/60x60',
    orderNo: 'ORD20250114093000',
    orderTime: '2025-01-14 09:30'
  }
])

// 已评价列表
const completedList = ref([
  {
    id: 2,
    institutionName: '郑州市二七区福寿园养老院',
    institutionImage: 'https://via.placeholder.com/60x60',
    orderNo: 'ORD20250110143000',
    orderTime: '2025-01-10 14:30',
    evaluationTime: '2025-01-12 16:20',
    rating: 5,
    content: '服务很好,工作人员很专业,环境也很干净整洁,老人住得很舒心。',
    images: [
      'https://via.placeholder.com/200x200',
      'https://via.placeholder.com/200x200'
    ]
  }
])

// Tab切换
const onTabChange = (name) => {
  activeTab.value = name
}

// 跳转到写评价页面
const goToWrite = (item) => {
  router.push({
    path: '/user/evaluation/form',
    query: {
      orderId: item.id,
      institutionName: item.institutionName
    }
  })
}

// 预览图片
const previewImages = (images, startPosition) => {
  showImagePreview({
    images,
    startPosition,
    closeable: true
  })
}
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
  gap: 12px;
  margin-bottom: 12px;
}

.institution-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.institution-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.order-info {
  font-size: 13px;
  color: #666;
}

.order-time,
.evaluation-time {
  font-size: 12px;
  color: #999;
}

/* 评价内容卡片 */
.evaluation-content-card {
  background: #f7f8fa;
  border-radius: 8px;
  padding: 12px;
  margin-top: 8px;
}

.evaluation-text {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  margin-top: 8px;
}

.evaluation-images {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  flex-wrap: wrap;
}

.evaluation-images .van-image {
  border-radius: 6px;
  cursor: pointer;
}

.empty-state {
  padding: 100px 0;
}
</style>
