<template>
  <div class="login-page">
    <van-nav-bar
      title="登录"
      left-arrow
      @click-left="$router.back()"
      fixed
      placeholder
    />

    <div class="login-content">
      <!-- Logo -->
      <div class="logo-section">
        <van-image
          width="80"
          height="80"
          src="https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg"
          round
        />
        <div class="app-name">养老机构监管平台</div>
        <div class="app-desc">家属端</div>
      </div>

      <!-- 登录表单 -->
      <div class="login-form">
        <van-form @submit="handleLogin">
          <!-- 手机号 -->
          <van-field
            v-model="loginForm.phone"
            name="phone"
            label="手机号"
            placeholder="请输入手机号"
            clearable
            :rules="[
              { required: true, message: '请输入手机号' },
              { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }
            ]"
          >
            <template #left-icon>
              <van-icon name="phone-o" />
            </template>
          </van-field>

          <!-- 密码 -->
          <van-field
            v-model="loginForm.password"
            type="password"
            name="password"
            label="密码"
            placeholder="请输入密码"
            clearable
            :rules="[
              { required: true, message: '请输入密码' }
            ]"
          >
            <template #left-icon>
              <van-icon name="lock" />
            </template>
          </van-field>

          <!-- 验证码登录切换 -->
          <div class="switch-login-type">
            <van-button type="primary" plain size="small" @click="switchLoginType">
              {{ loginType === 'password' ? '验证码登录' : '密码登录' }}
            </van-button>
          </div>

          <!-- 登录按钮 -->
          <div class="submit-btn">
            <van-button
              block
              type="primary"
              native-type="submit"
              :loading="loading"
            >
              登录
            </van-button>
          </div>

          <!-- 其他操作 -->
          <div class="other-actions">
            <span @click="goToRegister">注册账号</span>
            <span @click="goToForgetPassword">忘记密码</span>
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

const loginType = ref('password')
const loading = ref(false)
const loginForm = ref({
  phone: '',
  password: ''
})

// 切换登录方式
const switchLoginType = () => {
  loginType.value = loginType.value === 'password' ? 'code' : 'password'
  showToast('验证码登录功能开发中')
}

// 登录处理
const handleLogin = async () => {
  try {
    loading.value = true

    const res = await login({
      phone: loginForm.value.phone,
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

      showToast({
        message: '登录成功',
        onClose: () => {
          // 跳转到重定向地址或首页
          const redirect = route.query.redirect || '/home'
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

// 跳转注册
const goToRegister = () => {
  showToast('注册功能开发中')
}

// 跳转忘记密码
const goToForgetPassword = () => {
  showToast('忘记密码功能开发中')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-content {
  padding: 40px 20px;
}

.logo-section {
  text-align: center;
  margin-bottom: 40px;
  color: #fff;
}

.app-name {
  font-size: 24px;
  font-weight: bold;
  margin-top: 16px;
}

.app-desc {
  font-size: 14px;
  margin-top: 8px;
  opacity: 0.9;
}

.login-form {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.switch-login-type {
  text-align: right;
  margin-top: 12px;
  margin-bottom: 12px;
}

.submit-btn {
  margin-top: 24px;
}

.other-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.other-actions span {
  cursor: pointer;
}

.other-actions span:hover {
  color: #1989fa;
}
</style>
