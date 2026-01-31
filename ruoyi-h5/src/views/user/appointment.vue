<template>
  <div class="user-appointment-page">

    <div class="appointment-content">
      <!-- 筛选栏 -->
      <van-tabs v-model:active="activeTab" sticky offset-top="46" @change="onTabChange">
        <van-tab title="全部" name="all" />
        <van-tab title="待参观" name="0" />
        <van-tab title="已参观" name="1" />
        <van-tab title="已取消" name="2" />
      </van-tabs>

      <!-- 预约列表 -->
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div v-for="appointment in appointmentList" :key="appointment.appointmentId" class="appointment-item">
            <div class="appointment-header">
              <div class="header-left">
                <van-image
                  width="60"
                  height="60"
                  :src="appointment.institutionCover"
                  fit="cover"
                  round
                />
                <div class="institution-info">
                  <div class="institution-name">{{ appointment.institutionName }}</div>
                  <div class="appointment-no">预约编号: {{ appointment.appointmentNo }}</div>
                </div>
              </div>
              <van-tag :type="getStatusType(appointment.status)">
                {{ getStatusText(appointment.status) }}
              </van-tag>
            </div>

            <div class="appointment-detail">
              <div class="detail-row">
                <van-icon name="clock-o" size="16" />
                <span>参观时间: {{ appointment.visitDate }} {{ appointment.visitTime }}</span>
              </div>
              <div class="detail-row">
                <van-icon name="user-o" size="16" />
                <span>参观人: {{ appointment.visitorName }} ({{ appointment.visitorCount }}人)</span>
              </div>
              <div class="detail-row">
                <van-icon name="phone-o" size="16" />
                <span>联系电话: {{ appointment.visitorPhone }}</span>
              </div>
            </div>

            <div class="appointment-footer">
              <van-button size="small" @click="viewDetail(appointment)">
                查看详情
              </van-button>
              <van-button
                v-if="appointment.status === '0'"
                size="small"
                type="danger"
                plain
                @click="cancelAppointment(appointment.appointmentId)"
              >
                取消预约
              </van-button>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>

      <van-empty v-if="!loading && appointmentList.length === 0" description="暂无预约记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { getMyVisits, cancelVisit } from '@/api/visit'

const router = useRouter()

const activeTab = ref('all')
const appointmentList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const isRefresh = ref(false)

// Tab切换
const onTabChange = () => {
  pageNum.value = 1
  finished.value = false
  appointmentList.value = []
  isRefresh.value = true
  onLoad()
}

// 下拉刷新
const onRefresh = () => {
  pageNum.value = 1
  finished.value = false
  isRefresh.value = true
  onLoad()
}

// 加载列表
const onLoad = async () => {
  try {
    loading.value = true

    // 构建查询参数
    const params = {
      status: activeTab.value === 'all' ? null : activeTab.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }

    const response = await getMyVisits(params)

    if (response.code === 200) {
      const rows = response.data.rows || []

      // 字段映射（后端返回的字段名与前端期望的保持一致）
      const mappedList = rows.map(item => ({
        appointmentId: item.reservationId,
        appointmentNo: item.reservationNo,
        institutionId: item.institutionId,
        institutionName: item.institutionName,
        institutionCover: item.institutionCover || '',
        status: item.status,
        visitDate: item.visitDate,
        visitTime: item.visitTime,
        visitorName: item.visitorName,
        visitorPhone: item.visitorPhone,
        visitorCount: item.visitorCount,
        createTime: item.createTime
      }))

      if (isRefresh.value) {
        appointmentList.value = mappedList
        isRefresh.value = false
      } else {
        appointmentList.value = [...appointmentList.value, ...mappedList]
      }

      // 判断是否加载完成
      finished.value = mappedList.length < pageSize.value

      if (mappedList.length > 0) {
        pageNum.value++
      }
    }
  } catch (error) {
    console.error('加载预约列表失败:', error)
    showToast('加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 查看详情
const viewDetail = (appointment) => {
  router.push({
    name: 'AppointmentDetail',
    params: { id: appointment.appointmentId }
  })
}

// 取消预约
const cancelAppointment = async (appointmentId) => {
  try {
    await showConfirmDialog({
      title: '取消预约',
      message: '确定要取消此预约吗？'
    })

    const response = await cancelVisit(appointmentId)
    if (response.code === 200) {
      showToast('取消成功')
      // 刷新列表
      onRefresh()
    } else {
      showToast(response.msg || '取消失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      showToast(error.message || '取消失败')
    }
  }
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    '0': '待参观',
    '1': '已参观',
    '2': '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    '0': 'warning',
    '1': 'success',
    '2': 'default'
  }
  return typeMap[status] || 'default'
}

onMounted(() => {
  // 初始加载
})
</script>

<style scoped>
.user-appointment-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.appointment-content {
  padding-bottom: 20px;
}

.appointment-item {
  background: #fff;
  margin: 12px;
  border-radius: 8px;
  padding: 16px;
}

.appointment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f5f5f5;
}

.header-left {
  display: flex;
  gap: 12px;
  flex: 1;
}

.institution-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  justify-content: center;
}

.institution-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.appointment-no {
  font-size: 12px;
  color: #999;
}

.appointment-detail {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px 0;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.detail-row .van-icon {
  color: #999;
}

.appointment-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}
</style>
