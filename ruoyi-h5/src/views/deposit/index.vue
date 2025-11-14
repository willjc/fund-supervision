<template>
  <div class="deposit-page">
    <van-nav-bar title="押金管理" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="content">
      <!-- 快捷入口 -->
      <van-grid :column-num="2" :border="false">
        <van-grid-item
          icon="balance-list-o"
          text="押金余额"
          @click="goToBalance"
        />
        <van-grid-item
          icon="orders-o"
          text="申请列表"
          @click="goToApplyList"
        />
      </van-grid>

      <!-- 功能说明 -->
      <van-cell-group title="功能说明">
        <div class="info-content">
          <p>1. 查看押金余额和使用记录</p>
          <p>2. 查看机构提交的押金使用申请</p>
          <p>3. 对押金使用申请进行审批(同意/拒绝)</p>
          <p>4. 查看审批历史记录</p>
        </div>
      </van-cell-group>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

// 跳转到余额页面
const goToBalance = () => {
  router.push('/deposit/balance')
}

// 跳转到申请列表
const goToApplyList = () => {
  router.push('/deposit/apply-list')
}
</script>

<style scoped>
.deposit-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.content {
  padding-top: 12px;
}

.info-content {
  padding: 16px;
  font-size: 14px;
  color: #666;
  line-height: 2;
}

.info-content p {
  margin: 0;
}
</style>
