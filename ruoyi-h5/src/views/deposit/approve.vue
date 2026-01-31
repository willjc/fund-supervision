<template>
  <div class="approve-page">

    <div v-if="loading" class="loading-container">
      <van-loading size="24px">加载中...</van-loading>
    </div>

    <div v-else class="approve-content">
      <!-- 申请信息卡片 -->
      <div class="info-card">
        <div class="card-title">申请信息</div>
        <van-cell-group :border="false">
          <van-cell title="老人姓名" :value="applyInfo.elderName" />
          <van-cell title="床位信息" :value="applyInfo.bedInfo" />
          <van-cell title="申请金额">
            <template #value>
              <span class="amount">¥{{ formatMoney(applyInfo.applyAmount) }}</span>
            </template>
          </van-cell>
          <van-cell title="账户余额">
            <template #value>
              <span>¥{{ formatMoney(applyInfo.accountBalance) }}</span>
            </template>
          </van-cell>
          <van-cell title="使用事由" :value="applyInfo.usagePurpose" />
          <van-cell title="申请时间" :value="formatDateTime(applyInfo.createTime)" />
        </van-cell-group>
      </div>

      <!-- 附件材料 -->
      <van-cell-group v-if="applyInfo.attachments && applyInfo.attachments.length > 0" title="附件材料">
        <div class="attachment-list">
          <div
            v-for="(file, index) in applyInfo.attachments"
            :key="index"
            class="attachment-item"
            @click="previewFile(file)"
          >
            <van-icon name="description" size="24" />
            <span class="file-name">{{ file.fileName }}</span>
            <van-button size="small" type="primary" plain>查看</van-button>
          </div>
        </div>
      </van-cell-group>

      <!-- 审批操作 -->
      <div class="approve-section">
        <van-radio-group v-model="approvalResult" direction="horizontal">
          <van-radio name="approved" icon-size="20px">同意</van-radio>
          <van-radio name="rejected" icon-size="20px">拒绝</van-radio>
        </van-radio-group>

        <!-- 拒绝原因 -->
        <div v-if="approvalResult === 'rejected'" class="reject-reason-input">
          <van-field
            v-model="rejectReason"
            rows="3"
            autosize
            type="textarea"
            maxlength="200"
            placeholder="请输入拒绝原因(必填)"
            show-word-limit
          />
        </div>

        <!-- 审批说明 -->
        <van-notice-bar
          left-icon="info-o"
          :scrollable="false"
          wrapable
        >
          请仔细核对申请信息和附件材料,确认无误后提交审批意见
        </van-notice-bar>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="action-bar">
      <van-button block type="primary" size="large" @click="handleSubmit" :loading="submitting">
        提交审批
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { getApplyDetail, submitFamilyApproval } from '@/api/deposit'
import { formatMoney, formatDateTime } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const submitting = ref(false)
const applyInfo = ref({})
const approvalResult = ref('approved')
const rejectReason = ref('')

// 预览文件
const previewFile = (file) => {
  showToast('文件预览功能开发中')
}

// 提交审批
const handleSubmit = async () => {
  // 验证
  if (approvalResult.value === 'rejected' && !rejectReason.value.trim()) {
    showToast('请输入拒绝原因')
    return
  }

  // 确认提示
  const message = approvalResult.value === 'approved'
    ? '确认同意该押金使用申请?'
    : '确认拒绝该押金使用申请?'

  showDialog({
    title: '确认提交',
    message: message,
    showCancelButton: true
  }).then(async () => {
    try {
      submitting.value = true

      const params = {
        applyId: route.params.id,
        approvalResult: approvalResult.value
      }

      // 如果是拒绝,添加拒绝原因
      if (approvalResult.value === 'rejected') {
        params.rejectReason = rejectReason.value
      }

      const res = await submitFamilyApproval(params)

      if (res.code === 200) {
        showToast({
          message: '审批提交成功',
          onClose: () => {
            router.back()
          }
        })
      } else {
        showToast(res.msg || '审批提交失败')
      }
    } catch (error) {
      showToast('审批提交失败')
    } finally {
      submitting.value = false
    }
  }).catch(() => {
    // 取消
  })
}

// 加载申请详情
const loadDetail = async () => {
  try {
    loading.value = true
    const res = await getApplyDetail(route.params.id)

    if (res.code === 200) {
      applyInfo.value = res.data

      // 检查申请状态
      if (res.data.applyStatus !== 'pending_family') {
        showDialog({
          title: '提示',
          message: '该申请不是待家属审批状态,无法审批',
          confirmButtonText: '返回'
        }).then(() => {
          router.back()
        })
      }
    }
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
.approve-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 70px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.approve-content {
  padding-bottom: 12px;
}

.info-card {
  background: #fff;
  margin-bottom: 10px;
  padding: 16px 0 0 0;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  padding: 0 16px 12px;
}

.amount {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b6b;
}

.attachment-list {
  padding: 12px 16px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f7f8fa;
  border-radius: 8px;
  margin-bottom: 8px;
}

.file-name {
  flex: 1;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.approve-section {
  background: #fff;
  padding: 16px;
  margin-bottom: 10px;
}

.approve-section .van-radio-group {
  display: flex;
  gap: 40px;
  margin-bottom: 16px;
}

.reject-reason-input {
  margin-top: 16px;
  margin-bottom: 16px;
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  z-index: 100;
}
</style>
