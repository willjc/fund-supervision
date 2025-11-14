<template>
  <div class="todo-page">
    <van-nav-bar title="待办事项" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="todo-content">
      <!-- Tab切换 -->
      <van-tabs
        v-model:active="activeTab"
        @change="onTabChange"
        color="#667eea"
        sticky
        offset-top="46"
      >
        <van-tab title="待办" name="pending">
          <div class="todo-list">
            <div
              v-for="item in pendingList"
              :key="item.id"
              class="todo-item"
              @click="handleTodoClick(item)"
            >
              <div class="todo-header">
                <van-tag :type="getTypeColor(item.type)" size="medium">
                  {{ item.type }}
                </van-tag>
                <span class="todo-time">{{ item.createTime }}</span>
              </div>
              <div class="todo-title">{{ item.title }}</div>
              <div class="todo-desc">{{ item.description }}</div>
              <div class="todo-footer">
                <van-button
                  size="small"
                  type="primary"
                  plain
                  @click.stop="handleComplete(item)"
                >
                  标记完成
                </van-button>
              </div>
            </div>

            <div v-if="pendingList.length === 0" class="empty-state">
              <van-empty description="暂无待办事项" />
            </div>
          </div>
        </van-tab>

        <van-tab title="已完成" name="completed">
          <div class="todo-list">
            <div
              v-for="item in completedList"
              :key="item.id"
              class="todo-item completed"
            >
              <div class="todo-header">
                <van-tag type="success" size="medium">
                  {{ item.type }}
                </van-tag>
                <span class="todo-time">{{ item.completeTime }}</span>
              </div>
              <div class="todo-title">{{ item.title }}</div>
              <div class="todo-desc">{{ item.description }}</div>
              <div class="complete-mark">
                <van-icon name="success" size="16" color="#07c160" />
                <span>已完成</span>
              </div>
            </div>

            <div v-if="completedList.length === 0" class="empty-state">
              <van-empty description="暂无已完成事项" />
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
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()

// 活动Tab
const activeTab = ref('pending')

// 待办列表
const pendingList = ref([
  {
    id: 1,
    type: '缴费提醒',
    title: '2月服务费待缴纳',
    description: '您的2月服务费将于2025-02-01到期,请及时缴纳',
    createTime: '2025-01-14 09:00',
    dueDate: '2025-02-01'
  },
  {
    id: 2,
    type: '体检提醒',
    title: '老人定期体检',
    description: '张伟老人本月需进行定期健康检查',
    createTime: '2025-01-13 10:30',
    dueDate: '2025-01-20'
  },
  {
    id: 3,
    type: '预约确认',
    title: '参观预约待确认',
    description: '您预约的参观时间为2025-01-18 10:00,请按时到访',
    createTime: '2025-01-12 15:20',
    dueDate: '2025-01-18'
  }
])

// 已完成列表
const completedList = ref([
  {
    id: 4,
    type: '缴费提醒',
    title: '1月服务费已缴纳',
    description: '您已成功缴纳1月服务费2800元',
    createTime: '2025-01-08 14:00',
    completeTime: '2025-01-10 09:30'
  }
])

// 获取类型颜色
const getTypeColor = (type) => {
  const colorMap = {
    '缴费提醒': 'warning',
    '体检提醒': 'primary',
    '预约确认': 'success',
    '其他': 'default'
  }
  return colorMap[type] || 'default'
}

// Tab切换
const onTabChange = (name) => {
  activeTab.value = name
}

// 点击待办事项
const handleTodoClick = (item) => {
  // 根据类型跳转到相应页面
  if (item.type === '缴费提醒') {
    router.push('/user/expense')
  } else if (item.type === '预约确认') {
    router.push('/user/appointment')
  }
}

// 标记完成
const handleComplete = async (item) => {
  try {
    await showConfirmDialog({
      title: '确认完成',
      message: '确定要标记该事项为已完成吗?'
    })

    // 模拟操作
    await new Promise(resolve => setTimeout(resolve, 300))

    // 移动到已完成列表
    const index = pendingList.value.findIndex(i => i.id === item.id)
    if (index > -1) {
      const completedItem = {
        ...item,
        completeTime: new Date().toLocaleString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit',
          hour: '2-digit',
          minute: '2-digit'
        }).replace(/\//g, '-')
      }
      pendingList.value.splice(index, 1)
      completedList.value.unshift(completedItem)

      showToast('已标记为完成')
    }
  } catch (error) {
    if (error !== 'cancel') {
      showToast('操作失败')
    }
  }
}
</script>

<style scoped>
.todo-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.todo-content {
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

/* 待办列表 */
.todo-list {
  padding: 12px;
  min-height: 400px;
}

.todo-item {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: transform 0.2s;
}

.todo-item:active {
  transform: scale(0.98);
}

.todo-item.completed {
  opacity: 0.85;
}

.todo-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.todo-time {
  font-size: 12px;
  color: #999;
}

.todo-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.todo-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.todo-footer {
  display: flex;
  justify-content: flex-end;
}

.complete-mark {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #07c160;
  font-weight: 500;
}

.empty-state {
  padding: 100px 0;
}
</style>
