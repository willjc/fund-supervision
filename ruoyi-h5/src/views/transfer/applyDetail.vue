<template>
  <div class="apply-detail-page">
    <van-nav-bar title="划拨申请详情" left-arrow @click-left="$router.back()" fixed placeholder />

    <div v-if="loading" class="loading-container">
      <van-loading size="24px">加载中...</van-loading>
    </div>

    <div v-else class="detail-content">
      <!-- 老人信息 -->
      <van-cell-group title="老人信息">
        <van-cell title="姓名" :value="detail.elderName" />
        <van-cell title="所属机构" :value="detail.institutionName" />
      </van-cell-group>

      <!-- 申请信息 -->
      <van-cell-group title="申请信息">
        <van-cell title="申请单号" :value="detail.applyNo" />
        <van-cell title="申请金额">
          <template #value>
            <span class="amount">¥{{ formatMoney(detail.applyAmount) }}</span>
          </template>
        </van-cell>
        <van-cell title="申请原因" :value="detail.applyReason" />
        <van-cell title="紧急程度">
          <template #value>
            <van-tag :type="detail.urgencyLevel === '紧急' ? 'danger' : 'warning'">
              {{ detail.urgencyLevel || '一般' }}
            </van-tag>
          </template>
        </van-cell>
        <van-cell title="期望划拨日期" :value="detail.expectedUseDate" />
        <van-cell title="申请时间" :value="detail.createTime" />
        <van-cell title="申请状态">
          <template #value>
            <van-tag :type="getStatusType(detail.applyStatus)">
              {{ getStatusText(detail.applyStatus) }}
            </van-tag>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 划拨明细 -->
      <van-cell-group v-if="transferDetails && transferDetails.length > 0" title="划拨明细">
        <div class="detail-list">
          <div
            v-for="(item, index) in transferDetails"
            :key="index"
            class="detail-item"
          >
            <div class="detail-header">
              <span class="month">{{ item.billingMonth }}</span>
              <span class="amount">¥{{ formatMoney(item.transferAmount) }}</span>
            </div>
          </div>
        </div>
      </van-cell-group>

      <!-- 审批流程 -->
      <van-cell-group title="审批流程">
        <van-steps direction="vertical" :active="getCurrentStep()">
          <!-- 提交申请 -->
          <van-step>
            <template #inactive-icon>
              <van-icon name="checked" color="#07c160" />
            </template>
            <h4>提交申请</h4>
            <p>{{ detail.createTime }}</p>
          </van-step>

          <!-- 家属审批 -->
          <van-step>
            <template v-if="detail.familyApproveTime" #inactive-icon>
              <van-icon
                :name="detail.applyStatus === 'approved' || detail.applyStatus === 'pending_supervision' ? 'checked' : 'cross'"
                :color="detail.applyStatus === 'rejected' ? '#ee0a24' : '#07c160'"
              />
            </template>
            <h4>家属审批</h4>
            <p v-if="detail.familyApproveTime">
              {{ detail.familyApproveTime }}
            </p>
            <p v-if="detail.familyConfirmName">
              审批人: {{ detail.familyConfirmName }}
            </p>
            <p v-if="detail.familyApproveOpinion && detail.applyStatus === 'rejected'" class="reject-reason">
              拒绝原因: {{ detail.familyApproveOpinion }}
            </p>
          </van-step>

          <!-- 监管审批 -->
          <van-step>
            <template v-if="detail.approveTime" #inactive-icon>
              <van-icon
                :name="detail.applyStatus === 'approved' ? 'checked' : 'cross'"
                :color="detail.applyStatus === 'approved' ? '#07c160' : '#ee0a24'"
              />
            </template>
            <h4>监管审批</h4>
            <p v-if="detail.approveTime">
              {{ detail.approveTime }}
            </p>
            <p v-if="detail.approver">
              审批人: {{ detail.approver }}
            </p>
            <p v-if="detail.approveRemark && detail.applyStatus === 'rejected'" class="reject-reason">
              审批意见: {{ detail.approveRemark }}
            </p>
          </van-step>

          <!-- 划拨完成 -->
          <van-step>
            <template v-if="detail.applyStatus === 'approved'" #inactive-icon>
              <van-icon name="checked" color="#07c160" />
            </template>
            <h4>划拨完成</h4>
            <p v-if="detail.useTime">
              {{ detail.useTime }}
            </p>
            <p v-if="detail.actualAmount">
              划拨金额: ¥{{ formatMoney(detail.actualAmount) }}
            </p>
          </van-step>
        </van-steps>
      </van-cell-group>
    </div>

    <!-- 底部操作栏 -->
    <div v-if="detail.applyStatus === 'pending_family'" class="action-bar">
      <van-button type="default" size="large" @click="handleReject">
        拒绝
      </van-button>
      <van-button type="primary" size="large" @click="handleApprove">
        同意
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { getTransferDetail, submitTransferApproval } from '@/api/transfer'
import { formatMoney } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const detail = ref({})
const transferDetails = ref([])

// 获取当前审批步骤
const getCurrentStep = () => {
  const status = detail.value.applyStatus
  if (status === 'rejected') return -1
  if (status === 'approved' && detail.value.useTime) return 3
  if (status === 'approved') return 2
  if (status === 'pending_supervision') return 2
  if (status === 'pending_family') return 1
  return 0
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'pending_family': 'warning',
    'pending_supervision': 'primary',
    'approved': 'success',
    'rejected': 'danger'
  }
  return typeMap[status] || 'default'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'pending_family': '待家属审批',
    'pending_supervision': '待监管审批',
    'approved': '已通过',
    'rejected': '已拒绝'
  }
  return textMap[status] || status
}

// 同意申请
const handleApprove = async () => {
  try {
    await showConfirmDialog({
      title: '确认审批',
      message: `确认同意该资金划拨申请吗？\n申请金额：¥${formatMoney(detail.value.applyAmount)}`
    })

    const res = await submitTransferApproval({
      applyId: route.params.id,
      approvalResult: 'approved'
    })

    if (res.code === 200) {
      showToast({
        type: 'success',
        message: '审批成功，等待监管部门审批'
      })
      // 刷新详情
      await loadDetail()
    } else {
      showToast({
        type: 'fail',
        message: res.msg || '审批失败'
      })
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审批失败:', error)
      showToast({
        type: 'fail',
        message: error.message || '审批失败'
      })
    }
  }
}

// 拒绝申请
const handleReject = async () => {
  try {
    // 使用 prompt 获取拒绝原因
    const rejectReason = window.prompt('请输入拒绝原因', '')

    if (rejectReason === null) {
      return
    }

    if (!rejectReason || !rejectReason.trim()) {
      showToast('请输入拒绝原因')
      return
    }

    const res = await submitTransferApproval({
      applyId: route.params.id,
      approvalResult: 'rejected',
      rejectReason: rejectReason.trim()
    })

    if (res.code === 200) {
      showToast({
        type: 'success',
        message: '已拒绝申请'
      })
      // 刷新详情
      await loadDetail()
    } else {
      showToast({
        type: 'fail',
        message: res.msg || '审批失败'
      })
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审批失败:', error)
      showToast({
        type: 'fail',
        message: error.message || '审批失败'
      })
    }
  }
}

// 加载详情
const loadDetail = async () => {
  try {
    loading.value = true
    const res = await getTransferDetail(route.params.id)

    if (res.code === 200) {
      detail.value = res.data
      transferDetails.value = res.data.transferDetails || []
    } else {
      showToast({
        type: 'fail',
        message: res.msg || '加载失败'
      })
    }
  } catch (error) {
    console.error('加载详情失败:', error)
    showToast({
      type: 'fail',
      message: error.message || '加载失败'
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.apply-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.detail-content {
  padding-bottom: 12px;
}

.amount {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b6b;
}

.detail-list {
  padding: 12px 16px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
  margin-bottom: 8px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.detail-header .month {
  font-size: 14px;
  color: #333;
}

.detail-header .amount {
  font-size: 16px;
  font-weight: bold;
  color: #ff6b6b;
}

.reject-reason {
  color: #ee0a24;
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
