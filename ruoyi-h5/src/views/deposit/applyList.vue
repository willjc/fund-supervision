<template>
  <div class="apply-list-page">
    <van-nav-bar title="申请列表" left-arrow @click-left="$router.back()" fixed placeholder />

    <!-- 状态标签 -->
    <van-tabs v-model:active="activeTab" @change="onTabChange" sticky offset-top="46px">
      <van-tab title="待审批" name="pending_family" />
      <van-tab title="已通过" name="approved" />
      <van-tab title="已拒绝" name="rejected" />
      <van-tab title="全部" name="all" />
    </van-tabs>

    <!-- 申请列表 -->
    <div class="list-content">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div
            v-for="item in applyList"
            :key="item.applyId"
            class="apply-card"
            @click="viewDetail(item)"
          >
            <div class="card-header">
              <span class="elder-name">{{ item.elderName }}</span>
              <van-tag :type="getStatusType(item.applyStatus)">
                {{ getStatusText(item.applyStatus) }}
              </van-tag>
            </div>

            <div class="card-body">
              <van-cell-group :border="false">
                <van-cell title="申请金额">
                  <template #value>
                    <span class="amount">¥{{ formatMoney(item.applyAmount) }}</span>
                  </template>
                </van-cell>
                <van-cell title="使用事由" :value="item.usagePurpose" />
                <van-cell title="申请时间" :value="formatDate(item.createTime)" />
              </van-cell-group>
            </div>

            <div v-if="item.applyStatus === 'pending_family'" class="card-footer">
              <van-button
                size="small"
                type="default"
                @click.stop="handleReject(item)"
              >
                拒绝
              </van-button>
              <van-button
                size="small"
                type="primary"
                @click.stop="handleApprove(item)"
              >
                同意
              </van-button>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>

      <!-- 空状态 -->
      <van-empty
        v-if="!loading && applyList.length === 0"
        description="暂无申请数据"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getApplyList } from '@/api/deposit'
import { formatMoney, formatDate } from '@/utils/format'

const router = useRouter()

const activeTab = ref('pending_family')
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const applyList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)

// 标签切换
const onTabChange = () => {
  pageNum.value = 1
  finished.value = false
  applyList.value = []
  loadApplyList()
}

// 加载申请列表
const loadApplyList = async () => {
  try {
    loading.value = true

    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }

    // 添加状态筛选
    if (activeTab.value !== 'all') {
      params.applyStatus = activeTab.value
    }

    const res = await getApplyList(params)

    if (res.code === 200) {
      const newList = res.rows || []

      if (refreshing.value) {
        applyList.value = newList
        refreshing.value = false
      } else {
        applyList.value.push(...newList)
      }

      // 判断是否还有更多数据
      if (newList.length < pageSize.value) {
        finished.value = true
      } else {
        pageNum.value++
      }
    }
  } catch (error) {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  pageNum.value = 1
  finished.value = false
  loadApplyList()
}

// 上拉加载
const onLoad = () => {
  loadApplyList()
}

// 查看详情
const viewDetail = (item) => {
  router.push(`/deposit/apply-detail/${item.applyId}`)
}

// 同意申请
const handleApprove = (item) => {
  router.push(`/deposit/approve/${item.applyId}`)
}

// 拒绝申请
const handleReject = (item) => {
  router.push(`/deposit/approve/${item.applyId}`)
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'pending_family': 'warning',
    'family_approved': 'primary',
    'approved': 'success',
    'rejected': 'danger'
  }
  return typeMap[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'pending_family': '待家属审批',
    'family_approved': '家属已同意',
    'pending_supervision': '待监管审批',
    'approved': '已通过',
    'rejected': '已拒绝'
  }
  return textMap[status] || status
}

onMounted(() => {
  loadApplyList()
})
</script>

<style scoped>
.apply-list-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.list-content {
  padding: 12px;
}

.apply-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.elder-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.card-body {
  padding: 0;
}

.amount {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b6b;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
}
</style>
