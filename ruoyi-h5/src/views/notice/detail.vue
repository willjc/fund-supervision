<template>
  <div class="notice-detail-page">
    <van-nav-bar
      title="通知详情"
      left-arrow
      @click-left="onBack"
      fixed
      placeholder
    />

    <van-loading v-if="loading" class="loading" />

    <div v-else-if="notice" class="detail-content">
      <!-- 标题区域 -->
      <div class="header-section">
        <h1 class="notice-title">{{ notice.noticeTitle }}</h1>
        <div class="notice-meta">
          <van-tag :type="getNoticeTypeTag(notice.noticeType)">
            {{ getNoticeTypeText(notice.noticeType) }}
          </van-tag>
          <span class="notice-time">{{ formatDate(notice.createTime) }}</span>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="content-section">
        <div class="notice-content" v-html="notice.noticeContent"></div>
      </div>
    </div>

    <van-empty v-else description="通知不存在或已删除" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import dayjs from 'dayjs'
import { getNoticeDetail } from '@/api/notice'

const router = useRouter()
const route = useRoute()

// 加载状态
const loading = ref(true)

// 通知详情
const notice = ref(null)

// 返回上一页
const onBack = () => {
  router.back()
}

// 获取通知类型标签颜色
const getNoticeTypeTag = (type) => {
  const tagMap = {
    '1': 'primary',    // 通知
    '2': 'success'     // 公告
  }
  return tagMap[type] || 'default'
}

// 获取通知类型文本
const getNoticeTypeText = (type) => {
  const typeMap = {
    '1': '通知',
    '2': '公告'
  }
  return typeMap[type] || '其他'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY年MM月DD日 HH:mm')
}

// 处理内容中的图片路径
const processImageUrls = (content) => {
  if (!content) return ''

  // 移除 /dev-api 前缀，保留 /profile 路径
  // 数据库存储: <img src="/dev-api/profile/upload/xxx.jpg"
  // 转换为: <img src="/profile/upload/xxx.jpg"
  return content.replace(
    /<img([^>]*?)src=(["'])\/dev-api\/(profile\/[^"']*)\2/gi,
    '<img$1src=$2/$3$2'
  )
}

// 加载通知详情
const loadNoticeDetail = async () => {
  try {
    loading.value = true

    const noticeId = route.params.id
    if (!noticeId) {
      showToast('通知ID不存在')
      return
    }

    const response = await getNoticeDetail(noticeId)

    if (response.code === 200 && response.data) {
      // 处理图片路径
      const processedData = {
        ...response.data,
        noticeContent: processImageUrls(response.data.noticeContent)
      }
      notice.value = processedData
    } else {
      showToast(response.msg || '获取通知详情失败')
    }
  } catch (error) {
    console.error('获取通知详情失败:', error)
    showToast('获取通知详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadNoticeDetail()
})
</script>

<style scoped>
.notice-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.detail-content {
  padding-bottom: 20px;
}

/* 标题区域 */
.header-section {
  background-color: #fff;
  padding: 20px 16px;
  margin-bottom: 12px;
}

.notice-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  line-height: 1.5;
  margin: 0 0 12px 0;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: #999;
}

.notice-time {
  flex: 1;
}

/* 内容区域 */
.content-section {
  background-color: #fff;
  padding: 20px 16px;
  margin-bottom: 12px;
}

.notice-content {
  font-size: 15px;
  color: #666;
  line-height: 1.8;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.notice-content :deep(img) {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 12px 0;
}

.notice-content :deep(p) {
  margin: 12px 0;
}

.notice-content :deep(h1),
.notice-content :deep(h2),
.notice-content :deep(h3),
.notice-content :deep(h4),
.notice-content :deep(h5),
.notice-content :deep(h6) {
  margin: 16px 0 12px;
  font-weight: 600;
  color: #333;
}
</style>
