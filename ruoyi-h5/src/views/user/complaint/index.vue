<template>
  <div class="complaint-page">

    <div class="complaint-content">
      <!-- 投诉列表 -->
      <div class="complaint-list">
        <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
          <van-list
            v-model:loading="loading"
            :finished="finished"
            finished-text="没有更多了"
            @load="onLoad"
          >
            <div
              v-for="item in complaintList"
              :key="item.id"
              class="complaint-item"
              @click="goToDetail(item)"
            >
              <div class="complaint-header">
                <van-tag :type="getStatusType(item.status)" size="medium">
                  {{ item.statusText }}
                </van-tag>
                <span class="complaint-time">{{ item.createTime }}</span>
              </div>

              <div class="complaint-title">{{ item.title }}</div>

              <div class="complaint-institution">
                <van-icon name="shop-o" size="14" />
                {{ item.institutionName }}
              </div>

              <div class="complaint-desc">{{ item.content }}</div>

              <div v-if="item.replyContent" class="complaint-reply">
                <div class="reply-label">
                  <van-icon name="comment-o" size="14" />
                  回复
                </div>
                <div class="reply-content">{{ item.replyContent }}</div>
              </div>

              <!-- 有图片时显示图片预览 -->
              <div v-if="item.images && item.images.length > 0" class="complaint-images">
                <van-image
                  v-for="(img, idx) in item.images.slice(0, 3)"
                  :key="idx"
                  width="60"
                  height="60"
                  :src="img"
                  fit="cover"
                  @click.stop="previewImages(item.images, idx)"
                />
                <div v-if="item.images.length > 3" class="more-images">
                  +{{ item.images.length - 3 }}
                </div>
              </div>
            </div>
          </van-list>
        </van-pull-refresh>

        <div v-if="complaintList.length === 0 && !loading" class="empty-state">
          <van-empty description="暂无投诉记录" />
        </div>
      </div>

      <!-- 新增投诉按钮 -->
      <div class="add-button-wrapper">
        <van-button
          round
          block
          class="add-button"
          @click="goToAdd"
        >
          <van-icon name="plus" size="20" />
          新增投诉
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showImagePreview } from 'vant'
import { getComplaintList } from '@/api/complaint'

const router = useRouter()

// 投诉列表
const complaintList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'rejected': 'danger',
    'processing': 'primary',
    'completed': 'success'
  }
  return typeMap[status] || 'default'
}

// 加载投诉列表
const loadComplaintList = async () => {
  try {
    const response = await getComplaintList()
    if (response.code === 200) {
      complaintList.value = response.data || []
      finished.value = true
    } else {
      showToast(response.msg || '获取投诉列表失败')
    }
  } catch (error) {
    console.error('获取投诉列表失败:', error)
    showToast('获取投诉列表失败')
  } finally {
    loading.value = false
  }
}

// 下拉刷新
const onRefresh = async () => {
  finished.value = false
  await loadComplaintList()
  refreshing.value = false
}

// 上拉加载
const onLoad = () => {
  if (!refreshing.value) {
    loadComplaintList()
  }
}

// 预览图片
const previewImages = (images, index) => {
  showImagePreview({
    images: images,
    startPosition: index,
    closeable: true
  })
}

// 跳转到详情
const goToDetail = (item) => {
  // 可以实现投诉详情页
  console.log('查看投诉详情', item)
}

// 跳转到新增投诉
const goToAdd = () => {
  router.push('/user/complaint/form')
}

// 初始化加载
loadComplaintList()
</script>

<style scoped>
.complaint-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.complaint-content {
  padding: 12px;
}

/* 投诉列表 */
.complaint-list {
  min-height: 400px;
}

.complaint-item {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: transform 0.2s;
}

.complaint-item:active {
  transform: scale(0.98);
}

.complaint-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.complaint-time {
  font-size: 12px;
  color: #999;
}

.complaint-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.complaint-institution {
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 12px;
}

.complaint-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.complaint-reply {
  background: #f7f8fa;
  border-radius: 8px;
  padding: 12px;
  border-left: 3px solid #667eea;
  margin-bottom: 8px;
}

.reply-label {
  font-size: 13px;
  color: #667eea;
  font-weight: 500;
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.reply-content {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

/* 投诉图片 */
.complaint-images {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.complaint-images :deep(.van-image) {
  border-radius: 6px;
  overflow: hidden;
}

.more-images {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 12px;
  border-radius: 6px;
}

.empty-state {
  padding: 100px 0;
}

/* 新增按钮 */
.add-button-wrapper {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: #fff;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

.add-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-size: 16px;
  font-weight: 500;
  height: 48px;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

:deep(.van-button__content) {
  display: flex;
  align-items: center;
  gap: 6px;
}

:deep(.van-button__text) {
  color: #fff;
}
</style>
