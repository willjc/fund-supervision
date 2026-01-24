<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
    </div>

    <van-nav-bar
      title="登录"
      fixed
      placeholder
    />

    <div class="login-content">
      <!-- Logo -->
      <div class="logo-section">
        <div class="logo-wrapper">
          <van-icon name="user-circle-o" size="48" color="#fff" />
        </div>
        <div class="app-name">养老机构监管平台</div>
        <div class="app-desc">家属端</div>
      </div>

      <!-- 登录表单 -->
      <div class="login-form">
        <van-form @submit="handleLogin">
          <!-- 账号 -->
          <van-field
            v-model="loginForm.account"
            name="account"
            placeholder="请输入手机号或身份证号"
            clearable
          >
            <template #left-icon>
              <van-icon name="user-o" size="18" color="#999" />
            </template>
          </van-field>

          <!-- 密码 -->
          <van-field
            v-model="loginForm.password"
            type="password"
            name="password"
            placeholder="请输入密码"
            clearable
          >
            <template #left-icon>
              <van-icon name="lock" size="18" color="#999" />
            </template>
          </van-field>

          <!-- 登录按钮 -->
          <div class="submit-btn">
            <van-button
              block
              type="primary"
              native-type="submit"
              :loading="loading"
              round
            >
              登录
            </van-button>
          </div>

          <!-- 登录方式提示 -->
          <div class="login-tips">
            <div class="tips-title">支持的登录方式:</div>
            <div class="tip-item">• 手机号 + 密码 (家属账号)</div>
            <div class="tip-item">• 身份证号 + 密码 (老人账号)</div>
          </div>
        </van-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import { useUserStore } from '@/store/modules/user'
import { setToken } from '@/utils/auth'
import { login } from '@/api/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const loginForm = ref({
  account: '',
  password: ''
})

// 登录处理
const handleLogin = async () => {
  try {
    loading.value = true

    const res = await login({
      account: loginForm.value.account,
      password: loginForm.value.password
    })

    if (res.code === 200) {
      // 保存token
      setToken(res.data.token)
      userStore.setToken(res.data.token)

      // 保存用户信息
      userStore.setUser(res.data.user)

      // 保存关联老人列表
      if (res.data.elders) {
        userStore.setElders(res.data.elders)
      }

      // 根据登录方式显示不同提示
      const loginTypeText = res.data.loginType === 'idcard' ? '身份证号' : '手机号'

      showToast({
        message: `${loginTypeText}登录成功`,
        onClose: () => {
          // 跳转到重定向地址或首页
          const redirect = route.query.redirect || '/user'
          router.replace(redirect)
        }
      })
    } else {
      showToast(res.msg || '登录失败')
    }
  } catch (error) {
    showToast('登录失败,请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.4;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(1.05);
  }
}

.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰圆圈 */
.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: 0;
}

.bg-decoration .circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: pulse 8s ease-in-out infinite;
}

.circle-1 {
  width: 400px;
  height: 400px;
  top: -100px;
  right: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  left: -50px;
  animation-delay: 2s;
}

/* 导航栏 */
:deep(.van-nav-bar) {
  background: transparent;
}

:deep(.van-nav-bar__title) {
  color: #fff;
}

:deep(.van-nav-bar .van-icon) {
  color: #fff;
}

/* 内容区域 */
.login-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 16px;
  position: relative;
  z-index: 1;
}

/* Logo区域 */
.logo-section {
  text-align: center;
  margin-bottom: 24px;
  color: #fff;
}

.logo-wrapper {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.app-name {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.app-desc {
  font-size: 14px;
  opacity: 0.9;
}

/* 登录表单 */
.login-form {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(20px);
}

:deep(.van-field) {
  padding: 10px 0;
  background: transparent;
}

:deep(.van-field__control) {
  background: #f5f7fa;
  border-radius: 12px;
  padding: 0 16px;
  height: 50px;
  line-height: 1.5;
  font-size: 14px;
}

:deep(.van-field__left-icon) {
  margin-right: 10px;
  display: flex;
  align-items: center;
  flex-shrink: 0;
}

:deep(.van-cell__value) {
  background: #f5f7fa;
  border-radius: 12px;
  padding: 0 16px;
  height: 50px;
}

:deep(.van-field__control) input {
  background: #f5f7fa;
}

:deep(.van-cell:after) {
  display: none;
}

/* 登录按钮 */
.submit-btn {
  margin-top: 16px;
}

:deep(.van-button--primary) {
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
  font-size: 16px;
  font-weight: 500;
}

:deep(.van-button--primary:active) {
  opacity: 0.9;
}

/* 登录提示 */
.login-tips {
  margin-top: 16px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  font-size: 12px;
  color: #666;
}

.tips-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

.tip-item {
  line-height: 1.6;
  margin-bottom: 2px;
}
</style>
