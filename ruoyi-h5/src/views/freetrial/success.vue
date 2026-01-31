<template>
  <div class="freetrial-success-page">

    <div class="success-content">
      <!-- 成功图标 -->
      <div class="success-icon">
        <van-icon name="checked" size="80" color="#fff" />
      </div>

      <!-- 成功标题 -->
      <div class="success-title">恭喜您，报名成功!</div>

      <!-- 提示信息 -->
      <div class="success-tip">
        服务团队将在2个工作日内审核您的资料<br/>
        通过审核后，即可开始免费试入住之旅!
      </div>

      <!-- 报名信息 -->
      <div class="info-card">
        <div class="info-row">
          <span class="info-label">报名编号</span>
          <span class="info-value">{{ reportNo }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">入住人</span>
          <span class="info-value">{{ elderName }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">报名时间</span>
          <span class="info-value">{{ applyTime }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">入住时间</span>
          <span class="info-value">{{ checkInTime }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">入住机构</span>
          <span class="info-value">{{ institutionName }}</span>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <van-button round block type="primary" @click="viewDetail">
          查看详情
        </van-button>
        <van-button round block plain type="primary" @click="goHome">
          返回首页
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

const reportNo = ref('')
const elderName = ref('')
const checkInTime = ref('')
const applyTime = ref('')
const institutionName = ref('郑州市金水区花园口社区养老服务中心')

// 生成报名编号
const generateReportNo = () => {
  const timestamp = dayjs().format('YYYYMMDDHHmmss')
  const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0')
  return `ZHR${timestamp}${random}`
}

// 查看详情
const viewDetail = () => {
  showToast('详情功能开发中')
}

// 返回首页
const goHome = () => {
  router.push({ name: 'Home' })
}

onMounted(() => {
  // 从路由参数获取信息
  elderName.value = route.query.elderName || '张三'
  checkInTime.value = route.query.checkInTime || dayjs().format('YYYY-MM-DD')
  applyTime.value = dayjs().format('YYYY-MM-DD HH:mm:ss')
  reportNo.value = generateReportNo()
})
</script>

<style scoped>
.freetrial-success-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #1989fa 0%, #f5f5f5 40%);
  padding-bottom: 20px;
}

.success-content {
  padding: 40px 20px 20px;
}

/* 成功图标 */
.success-icon {
  width: 100px;
  height: 100px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

/* 成功标题 */
.success-title {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  text-align: center;
  margin-bottom: 12px;
}

/* 提示信息 */
.success-tip {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  text-align: center;
  line-height: 1.6;
  margin-bottom: 32px;
}

/* 信息卡片 */
.info-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 14px;
  color: #999;
  flex-shrink: 0;
}

.info-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  text-align: right;
  flex: 1;
  margin-left: 12px;
  word-break: break-all;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>
