<template>
  <div class="todo-page">

    <!-- 状态筛选 -->
    <van-tabs v-model:active="activeTab" @change="onTabChange" sticky>
      <van-tab title="全部" name="all" />
      <van-tab title="待处理" name="0" />
      <van-tab title="已完成" name="1" />
    </van-tabs>

    <!-- 待办列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <div v-if="todoList.length > 0">
          <div
            v-for="todo in todoList"
            :key="todo.todoId"
            class="todo-card"
            @click="goToDetail(todo.todoId)"
          >
            <div class="todo-header">
              <div class="todo-title-wrapper">
                <van-checkbox
                  v-model="todo.isCompleted"
                  @click.stop
                  @change="handleCheck(todo)"
                />
                <div class="todo-title" :class="{ completed: todo.isCompleted }">
                  {{ todo.todoTitle }}
                </div>
              </div>
              <van-tag
                :type="getPriorityType(todo.priority)"
                size="small"
              >
                {{ getPriorityText(todo.priority) }}
              </van-tag>
            </div>

            <div class="todo-content" :class="{ completed: todo.isCompleted }">
              {{ todo.todoContent }}
            </div>

            <div class="todo-footer">
              <div class="todo-type">
                <van-icon name="label-o" />
                <span>{{ getTodoTypeText(todo.todoType) }}</span>
              </div>
              <div class="todo-time">
                <van-icon name="clock-o" />
                <span>截止: {{ formatDate(todo.deadline) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <van-empty v-else description="暂无待办事项" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import dayjs from 'dayjs'

const router = useRouter()

// Tab状态
const activeTab = ref('all')

// 列表状态
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 待办列表
const todoList = ref([])

// 分页参数
const pageNum = ref(1)
const pageSize = ref(10)

// 模拟待办数据
const mockTodos = [
  {
    todoId: 1,
    todoTitle: '签署本月服务协议',
    todoContent: '请您在1月15日前完成本月服务协议的签署确认,可通过APP在线签署或到前台现场签署。',
    todoType: '1',
    priority: '1',
    isCompleted: false,
    deadline: '2025-01-15 18:00:00',
    createTime: '2025-01-10 09:00:00'
  },
  {
    todoId: 2,
    todoTitle: '缴纳本月费用',
    todoContent: '本月费用已生成,请及时缴纳。可通过微信、支付宝或银行转账等方式完成支付。',
    todoType: '2',
    priority: '1',
    isCompleted: false,
    deadline: '2025-01-20 23:59:59',
    createTime: '2025-01-13 10:30:00'
  },
  {
    todoId: 3,
    todoTitle: '参加家属座谈会',
    todoContent: '本月家属座谈会将于1月18日下午2点在一楼会议室举行,欢迎您参加。',
    todoType: '3',
    priority: '2',
    isCompleted: false,
    deadline: '2025-01-18 14:00:00',
    createTime: '2025-01-12 15:20:00'
  },
  {
    todoId: 4,
    todoTitle: '更新紧急联系人信息',
    todoContent: '请核对并更新您的紧急联系人信息,确保信息准确有效。',
    todoType: '1',
    priority: '2',
    isCompleted: true,
    deadline: '2025-01-10 18:00:00',
    createTime: '2025-01-05 11:00:00'
  },
  {
    todoId: 5,
    todoTitle: '确认下月护理方案',
    todoContent: '护理部已为老人制定下月护理方案,请您查看并确认。',
    todoType: '4',
    priority: '2',
    isCompleted: false,
    deadline: '2025-01-25 18:00:00',
    createTime: '2025-01-14 09:30:00'
  },
  {
    todoId: 6,
    todoTitle: '查看月度健康报告',
    todoContent: '老人12月健康报告已生成,请查看了解老人身体状况。',
    todoType: '4',
    priority: '3',
    isCompleted: true,
    deadline: '2025-01-08 18:00:00',
    createTime: '2025-01-02 14:00:00'
  },
  {
    todoId: 7,
    todoTitle: '补充老人衣物',
    todoContent: '根据护理员反馈,老人冬季衣物不足,建议补充保暖衣物。',
    todoType: '5',
    priority: '3',
    isCompleted: false,
    deadline: '2025-01-30 18:00:00',
    createTime: '2025-01-08 10:15:00'
  },
  {
    todoId: 8,
    todoTitle: '预约探视时间',
    todoContent: '春节期间探视需提前预约,请提前安排好探视时间。',
    todoType: '3',
    priority: '2',
    isCompleted: true,
    deadline: '2025-01-15 18:00:00',
    createTime: '2025-01-06 16:30:00'
  }
]

// 返回上一页
const onBack = () => {
  router.back()
}

// Tab切换
const onTabChange = () => {
  resetList()
  onLoad()
}

// 重置列表
const resetList = () => {
  todoList.value = []
  pageNum.value = 1
  finished.value = false
}

// 下拉刷新
const onRefresh = () => {
  resetList()
  onLoad()
  refreshing.value = false
}

// 加载待办列表 (使用模拟数据)
const onLoad = async () => {
  try {
    loading.value = true

    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    // 筛选数据
    let filteredTodos = [...mockTodos]

    // 按状态筛选
    if (activeTab.value === '0') {
      filteredTodos = filteredTodos.filter(todo => !todo.isCompleted)
    } else if (activeTab.value === '1') {
      filteredTodos = filteredTodos.filter(todo => todo.isCompleted)
    }

    // 分页处理
    const startIndex = (pageNum.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    const list = filteredTodos.slice(startIndex, endIndex)

    if (list.length === 0) {
      finished.value = true
    } else {
      todoList.value = [...todoList.value, ...list]
      pageNum.value++

      // 如果返回数据少于pageSize或已到达最后,说明没有更多了
      if (list.length < pageSize.value || endIndex >= filteredTodos.length) {
        finished.value = true
      }
    }
  } catch (error) {
    console.error('获取待办列表失败:', error)
    showToast('获取待办列表失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 查看详情
const goToDetail = (todoId) => {
  showToast('待办详情页面开发中')
  // TODO: 跳转到待办详情页
  // router.push({
  //   name: 'TodoDetail',
  //   params: { id: todoId }
  // })
}

// 处理复选框变化
const handleCheck = async (todo) => {
  try {
    // 模拟网络请求
    await new Promise(resolve => setTimeout(resolve, 300))

    // 更新模拟数据中的状态
    const targetTodo = mockTodos.find(t => t.todoId === todo.todoId)
    if (targetTodo) {
      targetTodo.isCompleted = todo.isCompleted
    }

    showToast(todo.isCompleted ? '已完成' : '已取消完成')
  } catch (error) {
    showToast('操作失败')
    // 恢复原状态
    todo.isCompleted = !todo.isCompleted
  }
}

// 获取待办类型文本
const getTodoTypeText = (type) => {
  const typeMap = {
    '1': '协议签署',
    '2': '费用缴纳',
    '3': '活动参与',
    '4': '护理相关',
    '5': '其他事项'
  }
  return typeMap[type] || '其他'
}

// 获取优先级文本
const getPriorityText = (priority) => {
  const priorityMap = {
    '1': '紧急',
    '2': '重要',
    '3': '普通'
  }
  return priorityMap[priority] || '普通'
}

// 获取优先级类型
const getPriorityType = (priority) => {
  const typeMap = {
    '1': 'danger',
    '2': 'warning',
    '3': 'primary'
  }
  return typeMap[priority] || 'primary'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('MM-DD HH:mm')
}

// 页面加载时获取待办列表
onMounted(() => {
  // 自动加载待办列表
})
</script>

<style scoped>
.todo-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.todo-card {
  background-color: #fff;
  margin: 12px;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.todo-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 8px;
}

.todo-title-wrapper {
  flex: 1;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.todo-title {
  flex: 1;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
}

.todo-title.completed {
  color: #999;
  text-decoration: line-through;
}

.todo-content {
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

.todo-content.completed {
  color: #999;
}

.todo-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.todo-type,
.todo-time {
  display: flex;
  align-items: center;
  gap: 4px;
}

.todo-type .van-icon,
.todo-time .van-icon {
  font-size: 14px;
}
</style>
