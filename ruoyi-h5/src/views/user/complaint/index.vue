<template>
  <div class="complaint-page">
    <van-nav-bar title="我要投诉" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="complaint-content">
      <!-- 投诉列表 -->
      <div class="complaint-list">
        <div
          v-for="item in complaintList"
          :key="item.id"
          class="complaint-item"
          @click="goToDetail(item)"
        >
          <div class="complaint-header">
            <van-tag :type="getStatusType(item.status)" size="medium">
              {{ getStatusText(item.status) }}
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
        </div>

        <div v-if="complaintList.length === 0" class="empty-state">
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

const router = useRouter()

// 投诉列表
const complaintList = ref([
  {
    id: 1,
    title: '服务态度问题',
    institutionName: '郑州市金水区花园口社区养老服务中心',
    content: '工作人员服务态度较差,对老人不够耐心,希望改进。',
    status: 'pending', // pending-待处理, processing-处理中, completed-已完成
    createTime: '2025-01-14 10:30',
    replyContent: ''
  },
  {
    id: 2,
    title: '卫生环境问题',
    institutionName: '郑州市二七区福寿园养老院',
    content: '房间卫生打扫不及时,走廊有异味,需要加强卫生管理。',
    status: 'completed',
    createTime: '2025-01-10 15:20',
    replyContent: '感谢您的反馈,我们已经加强了卫生管理,并对相关人员进行了培训,欢迎您再次监督。'
  }
])

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'processing': 'primary',
    'completed': 'success'
  }
  return typeMap[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'pending': '待处理',
    'processing': '处理中',
    'completed': '已完成'
  }
  return textMap[status] || '未知'
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
