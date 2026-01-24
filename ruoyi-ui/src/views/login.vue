<template>
  <div class="login-container">
    <!-- 背���装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 左侧内容区 -->
    <div class="left-content">
      <div class="platform-info">
        <!-- <div class="platform-icon">
          <svg-icon icon-class="tool" class="main-icon" />
        </div> -->
        <h1 class="platform-title">养老机构预收费<br>资金监管平台</h1>
        <div class="feature-list">
          <div class="feature-item">
            <i class="el-icon-monitor"></i>
            <span>全流程监管</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-coin"></i>
            <span>资金安全保障</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-data-analysis"></i>
            <span>智能数据分析</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录表单 -->
    <div class="right-content">
      <div class="login-card">
        <div class="login-header">
          <h2 class="login-title">用户登录</h2>
          <p class="login-subtitle">欢迎访问养老机构资金监管平台</p>
        </div>

        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              type="text"
              auto-complete="off"
              placeholder="请输入账号"
              class="login-input"
            >
              <template slot="prefix">
                <i class="el-icon-user login-input-icon"></i>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入密码"
              class="login-input"
              @keyup.enter.native="handleLogin"
            >
              <template slot="prefix">
                <i class="el-icon-lock login-input-icon"></i>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="code" v-if="captchaEnabled">
            <div class="captcha-wrapper">
              <el-input
                v-model="loginForm.code"
                auto-complete="off"
                placeholder="请输入验证码"
                class="login-input captcha-input"
                @keyup.enter.native="handleLogin"
              >
                <template slot="prefix">
                  <i class="el-icon-key login-input-icon"></i>
                </template>
              </el-input>
              <div class="captcha-img" @click="getCode">
                <img :src="codeUrl" alt="验证码" />
                <div class="captcha-refresh">
                  <i class="el-icon-refresh-right"></i>
                </div>
              </div>
            </div>
          </el-form-item>

          <el-form-item class="login-options">
            <el-checkbox v-model="loginForm.rememberMe" class="remember-checkbox">
              <span class="checkbox-text">记住密码</span>
            </el-checkbox>
          </el-form-item>

          <el-form-item>
            <el-button
              :loading="loading"
              class="login-btn"
              @click.native.prevent="handleLogin"
            >
              <span v-if="!loading">登 录</span>
              <span v-else>登录中...</span>
            </el-button>
          </el-form-item>

          <div class="register-link" v-if="register">
            <span>还没有账号？</span>
            <router-link :to="'/register'">立即注册</router-link>
          </div>
        </el-form>
      </div>
    </div>

    <!-- 底部版权信息 -->
    <div class="login-footer">
      <span>Copyright © 2018-2025 中国电信 All Rights Reserved.</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true,
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
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

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-container {
  min-height: 100vh;
  display: flex;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
  overflow: hidden;

  // 背景装饰圆圈
  .bg-decoration {
    position: absolute;
    width: 100%;
    height: 100%;
    overflow: hidden;

    .circle {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      animation: pulse 8s ease-in-out infinite;

      &.circle-1 {
        width: 500px;
        height: 500px;
        top: -150px;
        left: -150px;
        animation-delay: 0s;
      }

      &.circle-2 {
        width: 400px;
        height: 400px;
        bottom: -100px;
        right: -100px;
        animation-delay: 2s;
      }

      &.circle-3 {
        width: 300px;
        height: 300px;
        top: 50%;
        left: 50%;
        animation-delay: 4s;
      }
    }
  }

  // 左侧内容区
  .left-content {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px;
    position: relative;
    z-index: 1;
    animation: slideIn 0.8s ease-out;

    .platform-info {
      max-width: 500px;

      .platform-icon {
        width: 100px;
        height: 100px;
        background: rgba(255, 255, 255, 0.2);
        border-radius: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 30px;
        backdrop-filter: blur(10px);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);

        .main-icon {
          font-size: 50px;
          color: #fff;
        }
      }

      .platform-title {
        font-size: 48px;
        font-weight: 700;
        color: #fff;
        line-height: 1.3;
        margin: 0 0 20px 0;
        text-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
      }

      .platform-subtitle {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.8);
        margin-bottom: 50px;
        letter-spacing: 2px;
        text-transform: uppercase;
      }

      .feature-list {
        display: flex;
        gap: 30px;

        .feature-item {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 10px;
          padding: 20px;
          background: rgba(255, 255, 255, 0.1);
          border-radius: 16px;
          backdrop-filter: blur(10px);
          transition: all 0.3s ease;

          &:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateY(-5px);
          }

          i {
            font-size: 28px;
            color: #fff;
          }

          span {
            font-size: 14px;
            color: rgba(255, 255, 255, 0.9);
          }
        }
      }
    }
  }

  // 右侧登录区
  .right-content {
    flex: 0 0 450px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px;
    position: relative;
    z-index: 1;
    animation: slideIn 0.8s ease-out 0.2s both;

    .login-card {
      width: 100%;
      max-width: 400px;
      background: rgba(255, 255, 255, 0.95);
      border-radius: 24px;
      padding: 45px 40px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
      backdrop-filter: blur(20px);

      .login-header {
        text-align: center;
        margin-bottom: 40px;

        .login-title {
          font-size: 28px;
          font-weight: 600;
          color: #333;
          margin: 0 0 10px 0;
        }

        .login-subtitle {
          font-size: 14px;
          color: #999;
          margin: 0;
        }
      }

      .login-form {
        .el-form-item {
          margin-bottom: 24px;

          &:last-child {
            margin-bottom: 0;
          }
        }

        .login-input {
          ::v-deep .el-input__inner {
            background: #f5f7fa;
            border: 2px solid transparent;
            border-radius: 12px;
            height: 50px;
            line-height: 50px;
            padding-left: 45px;
            padding-right: 16px;
            font-size: 15px;
            color: #333;
            transition: all 0.3s ease;

            &:hover {
              background: #fff;
              border-color: #e0e0e0;
            }

            &:focus {
              background: #fff;
              border-color: #667eea;
            }

            &::placeholder {
              color: #bbb;
            }
          }

          .login-input-icon {
            color: #999;
            font-size: 16px;
          }
        }

        .captcha-wrapper {
          display: flex;
          gap: 12px;

          .captcha-input {
            flex: 1;
          }

          .captcha-img {
            flex-shrink: 0;
            width: 120px;
            height: 50px;
            border-radius: 12px;
            overflow: hidden;
            cursor: pointer;
            position: relative;
            background: #f5f7fa;
            transition: all 0.3s ease;

            &:hover {
              .captcha-refresh {
                opacity: 1;
              }
            }

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }

            .captcha-refresh {
              position: absolute;
              top: 0;
              left: 0;
              width: 100%;
              height: 100%;
              background: rgba(0, 0, 0, 0.5);
              display: flex;
              align-items: center;
              justify-content: center;
              opacity: 0;
              transition: opacity 0.3s ease;

              i {
                font-size: 24px;
                color: #fff;
              }
            }
          }
        }

        .login-options {
          margin-bottom: 8px;

          ::v-deep .el-form-item__content {
            line-height: normal;
          }

          .remember-checkbox {
            ::v-deep .el-checkbox__label {
              .checkbox-text {
              color: #666;
              font-size: 14px;
              }
            }
          }
        }

        .login-btn {
          width: 100%;
          height: 50px;
          border-radius: 12px;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border: none;
          font-size: 16px;
          font-weight: 500;
          letter-spacing: 4px;
          box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 32px rgba(102, 126, 234, 0.5);
          }

          &:active {
            transform: translateY(0);
          }

          &.is-loading {
            opacity: 0.8;
          }

          span {
            color: #fff;
          }
        }

        .register-link {
          text-align: center;
          margin-top: 20px;

          span {
            color: #999;
            font-size: 14px;
          }

          a {
            color: #667eea;
            font-size: 14px;
            text-decoration: none;
            margin-left: 8px;
            transition: color 0.3s ease;

            &:hover {
              color: #764ba2;
            }
          }
        }
      }
    }
  }

  // 底部版权
  .login-footer {
    position: fixed;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 50px;
    line-height: 50px;
    text-align: center;
    color: rgba(255, 255, 255, 0.7);
    font-size: 13px;
    z-index: 1;
  }
}

// 响应式适配
@media (max-width: 1024px) {
  .login-container {
    flex-direction: column;

    .left-content {
      flex: 0 0 auto;
      padding: 60px 40px 40px;
      min-height: 40vh;

      .platform-info {
        text-align: center;

        .platform-icon {
          margin: 0 auto 30px;
        }

        .platform-title {
          font-size: 36px;
        }

        .feature-list {
          justify-content: center;
        }
      }
    }

    .right-content {
      flex: 1;
      padding: 40px;
    }
  }
}

@media (max-width: 480px) {
  .login-container {
    .left-content {
      padding: 40px 20px;

      .platform-info {
        .platform-icon {
          width: 70px;
          height: 70px;

          .main-icon {
            font-size: 35px;
          }
        }

        .platform-title {
          font-size: 28px;
        }

        .platform-subtitle {
          font-size: 12px;
        }

        .feature-list {
          gap: 15px;

          .feature-item {
            padding: 15px 10px;

            i {
              font-size: 22px;
            }

            span {
              font-size: 12px;
            }
          }
        }
      }
    }

    .right-content {
      padding: 20px;

      .login-card {
        padding: 30px 25px;
      }
    }
  }
}

// Element UI 样式覆盖
::v-deep .el-form-item__error {
  padding-top: 6px;
  font-size: 12px;
}
</style>
