<template>
  <div class="appointment-success">
    <van-nav-bar title="预约成功" left-arrow @click-left="goBack" fixed placeholder />

    <div class="success-content">
      <!-- 成功图标 -->
      <div class="success-icon">
        <van-icon name="checked" size="80" color="#07c160" />
      </div>

      <div class="success-title">预约成功!</div>
      <div class="success-subtitle">请按时到访参观</div>

      <!-- 预约信息 -->
      <div class="appointment-info">
        <div class="info-row">
          <span class="label">机构名称:</span>
          <span class="value">{{ appointmentData.institutionName }}</span>
        </div>
        <div class="info-row">
          <span class="label">预约编号:</span>
          <span class="value">{{ appointmentData.appointmentNo }}</span>
        </div>
        <div class="info-row">
          <span class="label">参观时间:</span>
          <span class="value highlight">{{ appointmentData.visitDate }} {{ appointmentData.visitTime }}</span>
        </div>
        <div class="info-row">
          <span class="label">参观人:</span>
          <span class="value">{{ appointmentData.visitorName }}</span>
        </div>
        <div class="info-row">
          <span class="label">联系电话:</span>
          <span class="value">{{ appointmentData.visitorPhone }}</span>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <van-button type="primary" round block @click="viewDetail">
          查看预约详情
        </van-button>
        <van-button round block @click="goHome">
          返回首页
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const appointmentData = ref({
  appointmentNo: '',
  institutionName: '',
  visitDate: '',
  visitTime: '',
  visitorName: '',
  visitorPhone: ''
})

// 查看详情
const viewDetail = () => {
  router.replace({
    name: 'AppointmentDetail',
    params: { id: appointmentData.value.appointmentNo }
  })
}

// 返回首页
const goHome = () => {
  router.replace({ name: 'Home' })
}

// 返回
const goBack = () => {
  router.replace({ name: 'Home' })
}

onMounted(() => {
  // 从路由query获取预约信息
  appointmentData.value = {
    appointmentNo: 'AP' + Date.now(),
    institutionName: route.query.institutionName || '养老机构',
    visitDate: route.query.visitDate || '',
    visitTime: route.query.visitTime || '',
    visitorName: route.query.visitorName || '',
    visitorPhone: route.query.visitorPhone || ''
  }
})
</script>

<style scoped>
.appointment-success {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.success-content {
  padding: 40px 20px;
  text-align: center;
}

.success-icon {
  margin-bottom: 24px;
}

.success-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.success-subtitle {
  font-size: 14px;
  color: #999;
  margin-bottom: 40px;
}

.appointment-info {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  text-align: left;
  margin-bottom: 32px;
}

.info-row {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row:last-child {
  border-bottom: none;
}

.label {
  width: 80px;
  font-size: 14px;
  color: #666;
  flex-shrink: 0;
}

.value {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.value.highlight {
  color: #1989fa;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>
