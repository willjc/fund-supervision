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
                <van-tag :type="getTypeColor(item.typeText)" size="medium">
                  {{ item.typeText }}
                </van-tag>
                <span class="todo-time">{{ item.createTime }}</span>
              </div>
              <div class="todo-title">{{ item.title }}</div>
              <div class="todo-desc">{{ item.description }}</div>
              <div class="todo-footer">
                <van-button
                  v-if="item.type === 'deposit_approve'"
                  size="small"
                  type="primary"
                  plain
                  @click.stop="handleComplete(item)"
                >
                  去审批
                </van-button>
                <van-button
                  v-else
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
                <van-tag :type="item.resultType || 'success'" size="medium">
                  {{ item.typeText }}
                </van-tag>
                <span class="todo-time">{{ item.completeTime }}</span>
              </div>
              <div class="todo-title">{{ item.title }}</div>
              <div class="todo-desc">{{ item.description }}</div>

              <!-- 审批详情信息 -->
              <div class="approval-details">
                <div class="detail-row">
                  <span class="detail-label">申请时间：</span>
                  <span class="detail-value">{{ item.createTime }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">申请金额：</span>
                  <span class="detail-value amount">¥{{ formatAmount(item.amount) }}</span>
                </div>
                <div v-if="item.elderName" class="detail-row">
                  <span class="detail-label">关联老人：</span>
                  <span class="detail-value">{{ item.elderName }}</span>
                </div>
                <div v-if="item.approveTime" class="detail-row">
                  <span class="detail-label">审批时间：</span>
                  <span class="detail-value">{{ item.approveTime }}</span>
                </div>
                <div v-if="item.approver" class="detail-row">
                  <span class="detail-label">审批人：</span>
                  <span class="detail-value">{{ item.approver }}</span>
                </div>
                <div v-if="item.approveRemark" class="detail-row">
                  <span class="detail-label">审批意见：</span>
                  <span class="detail-value">{{ item.approveRemark }}</span>
                </div>
              </div>

              <div class="complete-info">
                <div class="complete-result">
                  <van-tag :type="item.resultType || 'success'" size="small">
                    {{ item.resultText }}
                  </van-tag>
                </div>
                <div class="complete-mark">
                  <van-icon name="success" size="16" color="#07c160" />
                  <span>已完成</span>
                </div>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast } from 'vant'
import { getTodoList, getCompletedList } from '@/api/todo'

const router = useRouter()

// 活动Tab
const activeTab = ref('pending')

// 待办列表
const pendingList = ref([])

// 已完成列表
const completedList = ref([])

// 加载状态
const loading = ref(false)

// 获取待办列表
const loadTodoList = async () => {
  let toast = null
  try {
    loading.value = true
    toast = showLoadingToast({
      message: '加载中...',
      forbidClick: true,
      duration: 0
    })

    const response = await getTodoList({
      pageNum: 1,
      pageSize: 100
    })

    if (toast) toast.close()

    if (response.code === 200 && response.data) {
      pendingList.value = response.data.rows || []
    } else {
      showToast({
        type: 'fail',
        message: response.msg || '加载失败'
      })
    }
  } catch (error) {
    if (toast) toast.close()
    console.error('加载待办列表失败', error)
    showToast({
      type: 'fail',
      message: '加载失败'
    })
  } finally {
    loading.value = false
  }
}

// 获取已完成列表
const loadCompletedList = async () => {
  let toast = null
  try {
    loading.value = true
    toast = showLoadingToast({
      message: '加载中...',
      forbidClick: true,
      duration: 0
    })

    const response = await getCompletedList({
      pageNum: 1,
      pageSize: 100
    })

    if (toast) toast.close()

    if (response.code === 200 && response.data) {
      completedList.value = response.data.rows || []
    } else {
      showToast({
        type: 'fail',
        message: response.msg || '加载失败'
      })
    }
  } catch (error) {
    if (toast) toast.close()
    console.error('加载已完成列表失败', error)
    showToast({
      type: 'fail',
      message: '加载失败'
    })
  } finally {
    loading.value = false
  }
}

// 获取类型颜色
const getTypeColor = (type) => {
  const colorMap = {
    '押金审批': 'warning',
    '划拨审批': 'danger',
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
  if (name === 'completed' && completedList.value.length === 0) {
    loadCompletedList()
  }
}

// 点击待办事项
const handleTodoClick = (item) => {
  // 根据类型跳转到相应页面
  if (item.type === 'deposit_approve') {
    // 跳转到押金审批详情页
    router.push(item.path)
  } else if (item.type === 'transfer_approve') {
    // 跳转到划拨审批详情页
    router.push(item.path)
  } else if (item.type === '缴费提醒') {
    router.push('/user/expense')
  } else if (item.type === '预约确认') {
    router.push('/user/appointment')
  }
}

// 标记完成（审批类待办不支持标记完成，需要在详情页审批）
const handleComplete = async (item) => {
  // 押金审批和划拨审批类待办不支持标记完成，需要在详情页审批
  if (item.type === 'deposit_approve' || item.type === 'transfer_approve') {
    showToast({
      message: '请进入详情页进行审批'
    })
    router.push(item.path)
    return
  }

  showToast({
    type: 'fail',
    message: '该功能暂未实现'
  })
}

// 格式化金额
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toFixed(2)
}

// 页面加载时获取待办列表
onMounted(() => {
  loadTodoList()
})
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

/* 审批详情样式 */
.approval-details {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 13px;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  color: #666;
  font-weight: 500;
  min-width: 80px;
}

.detail-value {
  color: #333;
  flex: 1;
  text-align: right;
}

.detail-value.amount {
  color: #ee0a24;
  font-weight: 600;
}

.todo-footer {
  display: flex;
  justify-content: flex-end;
}

.complete-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}

.complete-result {
  display: flex;
  align-items: center;
  gap: 8px;
}

.approver-info {
  font-size: 12px;
  color: #666;
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
