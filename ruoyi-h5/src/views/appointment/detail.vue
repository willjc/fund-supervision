<template>
  <div class="appointment-detail">

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
import { getVisitDetail, cancelVisit } from '@/api/visit'
import { getInstitutionDetail } from '@/api/institution'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const reservationId = ref(route.params.id)

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
    '1': '已参观',
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

    const response = await cancelVisit(reservationId.value)
    if (response.code === 200) {
      showToast('已取消预约')
      detail.value.status = '2'
    } else {
      showToast(response.msg || '取消失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      showToast(error.message || '取消失败')
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

    // 获取预约详情
    const response = await getVisitDetail(reservationId.value)
    if (response.code === 200 && response.data) {
      const data = response.data

      // 基本预约信息
      detail.value.appointmentNo = data.reservationNo
      detail.value.status = data.status
      detail.value.visitDate = data.visitDate
      detail.value.visitTime = data.visitTime
      detail.value.visitorName = data.visitorName
      detail.value.visitorPhone = data.visitorPhone
      detail.value.visitorCount = data.visitorCount
      detail.value.remark = data.remark

      // 机构基本信息
      detail.value.institutionInfo.name = data.institutionName
      detail.value.institutionInfo.coverImage = data.institutionCover || ''

      // 如果有机构ID，获取机构详细信息（地址、电话等）
      if (data.institutionId) {
        try {
          const instResponse = await getInstitutionDetail(data.institutionId)
          if (instResponse.code === 200 && instResponse.data) {
            const instData = instResponse.data
            detail.value.institutionInfo.address = instData.actualAddress || instData.address || ''
            detail.value.institutionInfo.contactPhone = instData.contactPhone || ''
            // 如果API返回的cover为空，尝试使用mainPicture
            if (!detail.value.institutionInfo.coverImage) {
              detail.value.institutionInfo.coverImage = instData.mainPicture || ''
            }
          }
        } catch (error) {
          console.error('加载机构详情失败:', error)
          // 机构详情加载失败不影响预约详情展示
        }
      }
    } else {
      showToast(response.msg || '加载失败')
    }
  } catch (error) {
    console.error('加载预约详情失败:', error)
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
