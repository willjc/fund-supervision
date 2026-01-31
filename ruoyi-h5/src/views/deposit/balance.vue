<template>
  <div class="balance-page">

    <div class="content">
      <!-- 余额卡片 -->
      <div class="balance-card">
        <div class="card-title">账户总余额</div>
        <div class="balance-amount">¥{{ formatMoney(totalBalance) }}</div>
        <div class="balance-tips">押金余额受民政部门监管</div>
      </div>

      <!-- 账户列表 -->
      <van-cell-group title="老人账户列表">
        <van-cell
          v-for="account in accountList"
          :key="account.accountId"
          :title="account.elderName"
          :label="`身份证: ${hideIdCard(account.idCard)}`"
          is-link
          @click="viewAccountDetail(account)"
        >
          <template #value>
            <div class="account-balance">
              ¥{{ formatMoney(account.balance) }}
            </div>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 空状态 -->
      <van-empty
        v-if="!loading && accountList.length === 0"
        description="暂无账户数据"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getAccountList } from '@/api/deposit'
import { formatMoney } from '@/utils/format'
import { hideIdCard } from '@/utils/format'

const router = useRouter()

const loading = ref(false)
const accountList = ref([])

// 计算总余额
const totalBalance = computed(() => {
  return accountList.value.reduce((sum, account) => sum + (account.balance || 0), 0)
})

// 查看账户详情
const viewAccountDetail = (account) => {
  showToast('账户详情功能开发中')
}

// 加载账户列表
const loadAccounts = async () => {
  try {
    loading.value = true
    const res = await getAccountList()

    if (res.code === 200) {
      accountList.value = res.data || []
    }
  } catch (error) {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAccounts()
})
</script>

<style scoped>
.balance-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.content {
  padding-top: 12px;
}

.balance-card {
  margin: 12px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: #fff;
  text-align: center;
}

.card-title {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 12px;
}

.balance-amount {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.balance-tips {
  font-size: 12px;
  opacity: 0.8;
}

.account-balance {
  font-size: 16px;
  font-weight: bold;
  color: #ff6b6b;
}
</style>
