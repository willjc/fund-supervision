<template>
  <div class="elder-page">
    <van-nav-bar title="老人信息" fixed placeholder left-arrow @click-left="onBack" />

    <!-- 搜索栏 -->
    <div class="search-bar">
      <van-search
        v-model="searchValue"
        placeholder="搜索老人姓名"
        @search="onSearch"
      />
    </div>

    <!-- 老人列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <div v-if="elderList.length > 0">
          <div
            v-for="elder in elderList"
            :key="elder.elderId"
            class="elder-card"
            @click="goToDetail(elder.elderId)"
          >
            <div class="elder-header">
              <div class="elder-avatar">
                <van-image
                  :src="elder.avatar || defaultAvatar"
                  width="60"
                  height="60"
                  round
                  fit="cover"
                />
              </div>
              <div class="elder-info">
                <div class="elder-name">{{ elder.elderName }}</div>
                <div class="elder-meta">
                  <span>{{ elder.age }}岁</span>
                  <span class="divider">|</span>
                  <span>{{ getGenderText(elder.gender) }}</span>
                  <span class="divider">|</span>
                  <span>{{ elder.idCard }}</span>
                </div>
                <div class="elder-institution">
                  <van-icon name="home-o" />
                  <span>{{ elder.institutionName }}</span>
                </div>
              </div>
            </div>

            <div class="elder-footer">
              <div class="status-info">
                <van-tag :type="getStatusType(elder.status)" size="small">
                  {{ getStatusText(elder.status) }}
                </van-tag>
                <span class="check-in-date">入住时间: {{ formatDate(elder.checkInDate) }}</span>
              </div>
              <van-icon name="arrow" />
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <van-empty v-else description="暂无老人信息" />
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import dayjs from 'dayjs'

const router = useRouter()

// 搜索关键词
const searchValue = ref('')

// 列表状态
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 老人列表
const elderList = ref([])

// 分页参数
const pageNum = ref(1)
const pageSize = ref(10)

// 默认头像
const defaultAvatar = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

// 模拟老人数据
const mockElders = [
  {
    elderId: 1,
    elderName: '张三',
    age: 75,
    gender: '1',
    idCard: '410***********1234',
    institutionName: '郑州市金水区花园口社区养老服务中心',
    status: '1',
    checkInDate: '2023-06-15',
    avatar: ''
  },
  {
    elderId: 2,
    elderName: '李四',
    age: 82,
    gender: '2',
    idCard: '410***********5678',
    institutionName: '郑州颐养家园养老院',
    status: '1',
    checkInDate: '2022-09-20',
    avatar: ''
  },
  {
    elderId: 3,
    elderName: '王五',
    age: 68,
    gender: '1',
    idCard: '410***********9012',
    institutionName: '河南省老干部康养中心',
    status: '1',
    checkInDate: '2024-01-10',
    avatar: ''
  }
]

// 返回上一页
const onBack = () => {
  router.back()
}

// 搜索
const onSearch = () => {
  resetList()
  onLoad()
}

// 重置列表
const resetList = () => {
  elderList.value = []
  pageNum.value = 1
  finished.value = false
}

// 下拉刷新
const onRefresh = () => {
  resetList()
  onLoad()
  refreshing.value = false
}

// 加载老人列表 (使用模拟数据)
const onLoad = async () => {
  try {
    loading.value = true

    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    // 筛选数据
    let filteredElders = [...mockElders]

    // 按姓名搜索
    if (searchValue.value) {
      filteredElders = filteredElders.filter(elder =>
        elder.elderName.includes(searchValue.value)
      )
    }

    // 分页处理
    const startIndex = (pageNum.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    const list = filteredElders.slice(startIndex, endIndex)

    if (list.length === 0) {
      finished.value = true
    } else {
      elderList.value = [...elderList.value, ...list]
      pageNum.value++

      // 如果返回数据少于pageSize或已到达最后,说明没有更多了
      if (list.length < pageSize.value || endIndex >= filteredElders.length) {
        finished.value = true
      }
    }
  } catch (error) {
    console.error('获取老人列表失败:', error)
    showToast('获取老人列表失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 查看详情
const goToDetail = (elderId) => {
  showToast('老人详情页面开发中')
  // TODO: 跳转到老人详情页
  // router.push({
  //   name: 'ElderDetail',
  //   params: { id: elderId }
  // })
}

// 获取性别文本
const getGenderText = (gender) => {
  return gender === '1' ? '男' : '女'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    '1': '在住',
    '2': '请假',
    '3': '已退住'
  }
  return statusMap[status] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    '1': 'success',
    '2': 'warning',
    '3': 'default'
  }
  return typeMap[status] || 'default'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD')
}
</script>

<style scoped>
.elder-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.search-bar {
  background-color: #fff;
  padding: 8px 0;
}

.elder-card {
  background-color: #fff;
  margin: 12px;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.elder-header {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.elder-avatar {
  flex-shrink: 0;
}

.elder-info {
  flex: 1;
  min-width: 0;
}

.elder-name {
  font-size: 18px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.elder-meta {
  font-size: 14px;
  color: #666;
  margin-bottom: 6px;
}

.elder-meta .divider {
  margin: 0 8px;
  color: #ddd;
}

.elder-institution {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
}

.elder-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.check-in-date {
  font-size: 12px;
  color: #999;
}
</style>
