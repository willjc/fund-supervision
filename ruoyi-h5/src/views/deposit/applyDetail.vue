<template>
  <div class="apply-detail-page">
    <van-nav-bar title="申请详情" left-arrow @click-left="$router.back()" fixed placeholder />

    <div v-if="loading" class="loading-container">
      <van-loading size="24px">加载中...</van-loading>
    </div>

    <div v-else class="detail-content">
      <!-- 老人信息 -->
      <van-cell-group title="老人信息">
        <van-cell title="姓名" :value="detail.elderName" />
        <van-cell title="床位信息" :value="detail.bedInfo" />
        <van-cell title="所属机构" :value="detail.institutionName" />
      </van-cell-group>

      <!-- 申请信息 -->
      <van-cell-group title="申请信息">
        <van-cell title="申请金额">
          <template #value>
            <span class="amount">¥{{ formatMoney(detail.applyAmount) }}</span>
          </template>
        </van-cell>
        <van-cell title="账户余额">
          <template #value>
            <span>¥{{ formatMoney(detail.accountBalance) }}</span>
          </template>
        </van-cell>
        <van-cell title="使用事由" :value="detail.usagePurpose" />
        <van-cell title="申请原因" :value="detail.applyReason" />
        <van-cell title="详细说明" :value="detail.description" />
        <van-cell title="申请状态">
          <template #value>
            <van-tag :type="getStatusType(detail.applyStatus)">
              {{ getStatusText(detail.applyStatus) }}
            </van-tag>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 附件材料 -->
      <van-cell-group v-if="detail.attachments && detail.attachments.length > 0" title="附件材料">
        <div class="attachment-list">
          <div
            v-for="(file, index) in detail.attachments"
            :key="index"
            class="attachment-item"
            @click="previewFile(file)"
          >
            <van-icon :name="getFileIcon(file.name)" size="24" color="#1989fa" />
            <span class="file-name">{{ file.name }}</span>
            <van-icon name="eye-o" size="18" />
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
            <p>{{ formatDateTime(detail.createTime) }}</p>
            <p>申请人: {{ detail.createBy }}</p>
          </van-step>

          <!-- 家属审批 -->
          <van-step>
            <template v-if="detail.familyApprovalTime" #inactive-icon>
              <van-icon
                :name="detail.familyApprovalResult === 'approved' ? 'checked' : 'cross'"
                :color="detail.familyApprovalResult === 'approved' ? '#07c160' : '#ee0a24'"
              />
            </template>
            <h4>家属审批</h4>
            <p v-if="detail.familyApprovalTime">
              {{ formatDateTime(detail.familyApprovalTime) }}
            </p>
            <p v-if="detail.familyApprovalUser">
              审批人: {{ detail.familyApprovalUser }}
            </p>
            <p v-if="detail.familyRejectReason" class="reject-reason">
              拒绝原因: {{ detail.familyRejectReason }}
            </p>
          </van-step>

          <!-- 监管审批 -->
          <van-step>
            <template v-if="detail.supervisionApprovalTime" #inactive-icon>
              <van-icon
                :name="detail.supervisionApprovalResult === 'approved' ? 'checked' : 'cross'"
                :color="detail.supervisionApprovalResult === 'approved' ? '#07c160' : '#ee0a24'"
              />
            </template>
            <h4>监管审批</h4>
            <p v-if="detail.supervisionApprovalTime">
              {{ formatDateTime(detail.supervisionApprovalTime) }}
            </p>
            <p v-if="detail.supervisionApprovalUser">
              审批人: {{ detail.supervisionApprovalUser }}
            </p>
          </van-step>

          <!-- 拨付完成 -->
          <van-step>
            <template v-if="detail.actualAmount" #inactive-icon>
              <van-icon name="checked" color="#07c160" />
            </template>
            <h4>拨付完成</h4>
            <p v-if="detail.disbursementTime">
              {{ formatDateTime(detail.disbursementTime) }}
            </p>
            <p v-if="detail.actualAmount">
              实际拨付: ¥{{ formatMoney(detail.actualAmount) }}
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
import { showToast, showImagePreview, showDialog, showConfirmDialog } from 'vant'
import { getApplyDetail, submitFamilyApproval } from '@/api/deposit'
import { formatMoney, formatDate, formatDateTime } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const detail = ref({})

// 获取当前审批步骤
const getCurrentStep = () => {
  const status = detail.value.applyStatus
  if (status === 'rejected') return -1
  if (detail.value.actualAmount) return 3
  if (status === 'approved') return 3
  if (status === 'pending_supervision' || status === 'family_approved') return 2
  if (status === 'pending_family') return 1
  return 0
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    'pending_family': 'warning',
    'family_approved': 'primary',
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
    'family_approved': '家属已同意',
    'pending_supervision': '待监管审批',
    'approved': '已通过',
    'rejected': '已拒绝'
  }
  return textMap[status] || status
}

// 获取文件图标
const getFileIcon = (fileName) => {
  if (!fileName) return 'description'
  const ext = fileName.toLowerCase().split('.').pop()
  const iconMap = {
    'jpg': 'photo-o',
    'jpeg': 'photo-o',
    'png': 'photo-o',
    'gif': 'photo-o',
    'bmp': 'photo-o',
    'webp': 'photo-o',
    'pdf': 'description',
    'doc': 'description',
    'docx': 'description',
    'xls': 'description',
    'xlsx': 'description',
    'txt': 'notes-o'
  }
  return iconMap[ext] || 'description'
}

// 判断是否为图片
const isImage = (fileName) => {
  if (!fileName) return false
  const ext = fileName.toLowerCase().split('.').pop()
  return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext)
}

// 预览文件
const previewFile = (file) => {
  if (!file || !file.url) {
    showToast('文件地址不存在')
    return
  }

  // 构建完整的文件URL（开发环境通过代理访问，生产环境需要完整URL）
  const fileUrl = file.url.startsWith('http')
    ? file.url
    : `${window.location.origin}${file.url}`

  console.log('预览文件:', file.name, fileUrl)

  // 图片类型使用图片预览
  if (isImage(file.name)) {
    showImagePreview({
      images: [fileUrl],
      closeable: true,
      onClose: () => {
        console.log('关闭图片预览')
      }
    })
    return
  }

  // PDF、Word等文档类型
  const ext = file.name.toLowerCase().split('.').pop()
  if (ext === 'pdf') {
    // PDF可以在浏览器中直接打开
    window.open(fileUrl, '_blank')
  } else if (['doc', 'docx', 'xls', 'xlsx'].includes(ext)) {
    // Office文档提示下载或使用在线预览
    showDialog({
      title: '文档预览',
      message: '是否下载该文档？',
      showCancelButton: true,
      confirmButtonText: '下载',
      cancelButtonText: '取消'
    }).then(() => {
      // 下载文件
      const link = document.createElement('a')
      link.href = fileUrl
      link.download = file.name
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    }).catch(() => {
      // 取消操作
    })
  } else {
    // 其他类型尝试在新窗口打开
    window.open(fileUrl, '_blank')
  }
}

// 同意申请
const handleApprove = async () => {
  try {
    await showConfirmDialog({
      title: '确认审批',
      message: `确认同意该押金使用申请吗？\n申请金额：¥${formatMoney(detail.value.applyAmount)}`
    })

    const res = await submitFamilyApproval({
      applyId: route.params.id,
      approvalResult: 'approved'
    })

    if (res.code === 200) {
      showToast({
        type: 'success',
        message: '审批成功'
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
    // 使用 prompt 获取拒绝原因（更可靠的方式）
    const rejectReason = window.prompt('请输入拒绝原因', '')

    // 用户取消输入
    if (rejectReason === null) {
      return
    }

    // 验证拒绝原因必填
    if (!rejectReason || !rejectReason.trim()) {
      showToast('请输入拒绝原因')
      return
    }

    const res = await submitFamilyApproval({
      applyId: route.params.id,
      approvalResult: 'rejected',
      rejectReason: rejectReason.trim()
    })

    if (res.code === 200) {
      showToast({
        type: 'success',
        message: '审批成功'
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
    const res = await getApplyDetail(route.params.id)

    console.log('申请详情返回数据:', res)

    if (res.code === 200) {
      detail.value = res.data
      console.log('申请详情:', detail.value)
    } else {
      showToast({
        type: 'fail',
        message: res.msg || '加载失败'
      })
    }
  } catch (error) {
    console.error('加载申请详情失败:', error)
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
  cursor: pointer;
  transition: all 0.3s;
}

.attachment-item:active {
  background: #e8e9eb;
  transform: scale(0.98);
}

.file-name {
  flex: 1;
  font-size: 14px;
  color: #323233;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
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
