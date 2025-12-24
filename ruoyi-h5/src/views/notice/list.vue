<template>
  <div class="notice-page">
    <van-nav-bar title="通知公告" fixed placeholder left-arrow @click-left="onBack" />

    <!-- 通知列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <div v-if="noticeList.length > 0">
          <div
            v-for="notice in noticeList"
            :key="notice.noticeId"
            class="notice-card"
            @click="goToDetail(notice.noticeId)"
          >
            <div class="notice-header">
              <div class="notice-title">{{ notice.noticeTitle }}</div>
              <van-tag v-if="!notice.isRead" type="danger" size="small">未读</van-tag>
            </div>

            <div class="notice-content">
              {{ notice.noticeContent }}
            </div>

            <div class="notice-footer">
              <div class="notice-type">
                <van-icon name="label-o" />
                <span>{{ getNoticeTypeText(notice.noticeType) }}</span>
              </div>
              <div class="notice-time">
                <van-icon name="clock-o" />
                <span>{{ formatDate(notice.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <van-empty v-else description="暂无通知" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import dayjs from 'dayjs'
import { getNoticeList } from '@/api/notice'

const router = useRouter()

// 列表状态
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 通知列表
const noticeList = ref([])

// 返回上一页
const onBack = () => {
  router.back()
}

// 重置列表
const resetList = () => {
  noticeList.value = []
  finished.value = false
}

// 下拉刷新
const onRefresh = () => {
  resetList()
  onLoad()
  refreshing.value = false
}

// 加载通知列表 (使用真实API)
const onLoad = async () => {
  try {
    loading.value = true

    const response = await getNoticeList()

    if (response.code === 200 && response.data) {
      noticeList.value = response.data
      finished.value = true
    } else {
      showToast(response.msg || '获取通知列表失败')
      finished.value = true
    }
  } catch (error) {
    console.error('获取通知列表失败:', error)
    showToast('获取通知列表失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 查看详情
const goToDetail = (noticeId) => {
  router.push({
    name: 'NoticeDetail',
    params: { id: noticeId }
  })
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
  return dayjs(date).format('MM-DD HH:mm')
}

// 页面加载时获取通知列表
onMounted(() => {
  // 自动加载通知列表
})
</script>

<style scoped>
.notice-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.notice-card {
  background-color: #fff;
  margin: 12px;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 8px;
}

.notice-title {
  flex: 1;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
}

.notice-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notice-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.notice-type,
.notice-time {
  display: flex;
  align-items: center;
  gap: 4px;
}

.notice-type .van-icon,
.notice-time .van-icon {
  font-size: 14px;
}
</style>
