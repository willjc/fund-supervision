<template>
  <div class="appointment-detail">
    <van-nav-bar title="预约详情" left-arrow @click-left="$router.back()" fixed placeholder />

    <div v-if="loading" class="loading-container">
      <van-loading size="24px">加载中...</van-loading>
    </div>

    <div v-else class="detail-content">
      <!-- 状态卡片 -->
      <div class="status-card">
        <div class="status-icon">
          <van-icon v-if="detail.status === '0'" name="clock-o" size="48" color="#ff9800" />
          <van-icon v-else-if="detail.status === '1'" name="checked" size="48" color="#07c160" />
          <van-icon v-else name="cross" size="48" color="#ee0a24" />
        </div>
        <div class="status-text">{{ getStatusText(detail.status) }}</div>
      </div>

      <!-- 预约信息 -->
      <van-cell-group title="预约信息" inset>
        <van-cell title="预约编号" :value="detail.appointmentNo" />
        <van-cell title="预约状态" :value="getStatusText(detail.status)" />
        <van-cell title="预约时间" :value="`${detail.visitDate} ${detail.visitTime}`" />
      </van-cell-group>

      <!-- 参观人信息 -->
      <van-cell-group title="参观人信息" inset>
        <van-cell title="姓名" :value="detail.visitorName" />
        <van-cell title="联系电话" :value="detail.visitorPhone" />
        <van-cell title="参观人数" :value="`${detail.visitorCount}人`" />
        <van-cell v-if="detail.remark" title="备注说明" :value="detail.remark" />
      </van-cell-group>

      <!-- 机构信息 -->
      <van-cell-group title="机构信息" inset>
        <div class="institution-card">
          <van-image
            width="60"
            height="60"
            :src="detail.institutionInfo.coverImage"
            fit="cover"
            round
          />
          <div class="institution-info">
            <div class="institution-name">{{ detail.institutionInfo.name }}</div>
            <div class="institution-address">
              <van-icon name="location-o" size="12" />
              {{ detail.institutionInfo.address }}
            </div>
            <div class="institution-phone">
              <van-icon name="phone-o" size="12" />
              {{ detail.institutionInfo.contactPhone }}
            </div>
          </div>
        </div>
      </van-cell-group>

      <!-- 操作按钮 -->
      <div v-if="detail.status === '0'" class="action-bar">
        <van-button type="danger" plain @click="handleCancel">
          取消预约
        </van-button>
        <van-button type="primary" @click="handleCall">
          联系机构
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'

const route = useRoute()
const router = useRouter()

const loading = ref(false)

// 模拟详情数据
const mockDetail = {
  appointmentId: 1,
  appointmentNo: 'AP1736825400000',
  status: '0', // 0-待参观 1-已完成 2-已取消
  visitDate: '2025-01-20',
  visitTime: '10:00',
  visitorName: '张三',
  visitorPhone: '13800138000',
  visitorCount: 2,
  remark: '希望能详细了解护理服务和收费标准',
  institutionInfo: {
    name: '郑州市金水区花园口社区养老服务中心',
    address: '郑州市金水区花园口镇花园路123号',
    contactPhone: '0371-12345678',
    coverImage: 'https://via.placeholder.com/60x60'
  }
}

const detail = ref({
  appointmentNo: '',
  status: '0',
  visitDate: '',
  visitTime: '',
  visitorName: '',
  visitorPhone: '',
  visitorCount: 0,
  remark: '',
  institutionInfo: {
    name: '',
    address: '',
    contactPhone: '',
    coverImage: ''
  }
})

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    '0': '待参观',
    '1': '已完成',
    '2': '已取消'
  }
  return statusMap[status] || '未知'
}

// 取消预约
const handleCancel = async () => {
  try {
    await showConfirmDialog({
      title: '取消预约',
      message: '确定要取消这次预约吗?'
    })

    // 模拟取消
    await new Promise(resolve => setTimeout(resolve, 500))

    showToast('已取消预约')
    detail.value.status = '2'
  } catch (error) {
    if (error !== 'cancel') {
      showToast('取消失败')
    }
  }
}

// 联系机构
const handleCall = () => {
  if (detail.value.institutionInfo.contactPhone) {
    window.location.href = `tel:${detail.value.institutionInfo.contactPhone}`
  }
}

// 加载详情
const loadDetail = async () => {
  try {
    loading.value = true

    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    detail.value = mockDetail
  } catch (error) {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.appointment-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.detail-content {
  padding: 16px;
}

.status-card {
  background: #fff;
  border-radius: 8px;
  padding: 32px;
  text-align: center;
  margin-bottom: 16px;
}

.status-icon {
  margin-bottom: 16px;
}

.status-text {
  font-size: 20px;
  font-weight: 500;
  color: #333;
}

.institution-card {
  display: flex;
  gap: 12px;
  padding: 16px;
}

.institution-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.institution-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.institution-address,
.institution-phone {
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

.action-bar .van-button {
  flex: 1;
}
</style>
