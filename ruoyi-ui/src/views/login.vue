<template>
  <div class="login-page">
    <!-- 品牌区：深墨底 + 账册刻线 + 朱砂印 -->
    <section class="login-brand">
      <div class="brand-grid" aria-hidden="true"></div>

      <header class="brand-top">
        <span class="brand-seal">监</span>
        <div class="brand-wordmark">
          <span class="brand-org">郑州市养老资金监管</span>
          <span class="brand-en">Fund Supervision</span>
        </div>
      </header>

      <div class="brand-main">
        <p class="brand-eyebrow"><span class="eyebrow-rule"></span>民政监管 · 机构服务 · 家属查询</p>
        <h1 class="brand-title">养老机构预收费<br>资金监管平台</h1>
        <p class="brand-desc">
          机构准入、资金归集、支用核验、风险处置，全流程留痕，
          让每一笔养老钱都看得见去处。
        </p>

        <ol class="brand-flow">
          <li>
            <span class="flow-index">01</span>
            <span class="flow-label">机构准入</span>
            <span class="flow-note">备案与资质核验</span>
          </li>
          <li>
            <span class="flow-index">02</span>
            <span class="flow-label">资金归集</span>
            <span class="flow-note">预收费专户监管</span>
          </li>
          <li>
            <span class="flow-index">03</span>
            <span class="flow-label">支用核验</span>
            <span class="flow-note">划拨申请逐笔审批</span>
          </li>
          <li>
            <span class="flow-index">04</span>
            <span class="flow-label">风险处置</span>
            <span class="flow-note">异动预警与对账</span>
          </li>
        </ol>
      </div>

      <footer class="brand-foot">
        <span>郑州市民政局 · 资金监管专用</span>
        <span class="brand-code">Admin Console</span>
      </footer>

      <span class="brand-watermark" aria-hidden="true">监管</span>
    </section>

    <!-- 表单区 -->
    <section class="login-panel">
      <div class="panel-inner">
        <div class="mobile-brand">
          <span class="brand-seal">监</span>
          <span class="mobile-brand-name">养老资金监管平台</span>
        </div>

        <header class="panel-head">
          <p class="panel-eyebrow"><span class="panel-eyebrow-rule"></span>统一身份认证</p>
          <h2 class="panel-title">登录监管平台</h2>
          <p class="panel-subtitle">请使用平台分配的账号登录</p>
        </header>

        <el-form
          ref="loginForm"
          :model="loginForm"
          :rules="loginRules"
          label-position="top"
          class="login-form"
        >
          <el-form-item prop="username" label="账号">
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

          <el-form-item prop="password" label="密码">
            <el-input
              v-model="loginForm.password"
              type="password"
              auto-complete="off"
              placeholder="请输入密码"
              class="login-input"
              show-password
              @keyup.enter.native="handleLogin"
            >
              <template slot="prefix">
                <i class="el-icon-lock login-input-icon"></i>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="code" label="验证码" v-if="captchaEnabled">
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
              <button
                type="button"
                class="captcha-img"
                aria-label="点击刷新验证码"
                @click="getCode"
              >
                <img :src="codeUrl" alt="验证码" />
                <span class="captcha-refresh"><i class="el-icon-refresh-right"></i></span>
              </button>
            </div>
          </el-form-item>

          <div class="login-options">
            <el-checkbox v-model="loginForm.rememberMe" class="remember-checkbox">
              <span class="checkbox-text">记住密码</span>
            </el-checkbox>
            <span class="options-hint">忘记密码请联系平台管理员</span>
          </div>

          <el-button
            :loading="loading"
            class="login-btn"
            @click.native.prevent="handleLogin"
          >
            <span v-if="!loading">登录平台</span>
            <span v-else>正在登录...</span>
          </el-button>

          <div class="register-link" v-if="register">
            <span>还没有账号？</span>
            <router-link :to="'/register'">立即注册</router-link>
          </div>

          <div class="security-note">
            <i class="el-icon-lock"></i>
            <span>本平台仅限授权人员使用，所有操作均留有审计记录</span>
          </div>
        </el-form>
      </div>

      <footer class="panel-foot">
        <span>Copyright © 2018-2026 养老资金监管平台</span>
      </footer>
    </section>
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

<style lang="scss" scoped>
@keyframes login-rise {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes login-fade {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.login-page {
  display: flex;
  min-height: 100vh;
  background: #f4f7fb;
  background: var(--app-page, #f4f7fb);
  font-family: var(--app-font, -apple-system, BlinkMacSystemFont, "PingFang SC", "Microsoft YaHei", sans-serif);
}

/* 左侧品牌区 */
.login-brand {
  position: relative;
  flex: 1 1 58%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 40px;
  padding: clamp(40px, 5.5vw, 80px);
  overflow: hidden;
  color: #f2f6fa;
  background:
    radial-gradient(720px 520px at 78% 12%, rgba(45, 78, 112, 0.55) 0%, rgba(45, 78, 112, 0) 62%),
    linear-gradient(155deg, #16293f 0%, #0e1d2f 58%, #0b1725 100%);

  .brand-grid {
    position: absolute;
    inset: 0;
    pointer-events: none;

    &::before {
      content: "";
      position: absolute;
      inset: 0;
      background-image: repeating-linear-gradient(
        90deg,
        rgba(255, 255, 255, 0.028) 0,
        rgba(255, 255, 255, 0.028) 1px,
        transparent 1px,
        transparent 96px
      );
    }

    &::after {
      content: "";
      position: absolute;
      right: -180px;
      bottom: -180px;
      width: 680px;
      height: 680px;
      background-image: repeating-radial-gradient(
        circle at center,
        rgba(255, 255, 255, 0.03) 0,
        rgba(255, 255, 255, 0.03) 1px,
        transparent 1px,
        transparent 44px
      );
      border-radius: 50%;
    }
  }

  .brand-top {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
    gap: 14px;
    animation: login-rise 0.55s cubic-bezier(0.2, 0, 0, 1) both;

    .brand-seal {
      width: 38px;
      height: 38px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      font-family: "Songti SC", "Noto Serif SC", STSong, serif;
      font-size: 21px;
      font-weight: 700;
      color: #e6a89c;
      background: rgba(192, 90, 74, 0.12);
      border: 1px solid #c05a4a;
      border-radius: 8px;
    }

    .brand-wordmark {
      display: flex;
      flex-direction: column;
      gap: 2px;

      .brand-org {
        font-size: 14px;
        font-weight: 600;
        letter-spacing: 0.08em;
        color: #eef3f8;
      }

      .brand-en {
        font-size: 10px;
        letter-spacing: 0.24em;
        text-transform: uppercase;
        color: rgba(224, 232, 240, 0.5);
      }
    }
  }

  .brand-main {
    position: relative;
    z-index: 1;
    max-width: 620px;
    animation: login-rise 0.6s cubic-bezier(0.2, 0, 0, 1) 0.08s both;

    .brand-eyebrow {
      display: flex;
      align-items: center;
      gap: 12px;
      margin: 0 0 24px;
      font-size: 12px;
      letter-spacing: 0.22em;
      color: rgba(224, 232, 240, 0.62);

      .eyebrow-rule {
        width: 22px;
        height: 2px;
        background: #c05a4a;
      }
    }

    .brand-title {
      margin: 0;
      font-family: "Songti SC", "Noto Serif SC", STSong, SimSun, serif;
      font-size: clamp(38px, 4vw, 58px);
      font-weight: 700;
      line-height: 1.28;
      letter-spacing: 0.06em;
      color: #f7fafc;
      text-shadow: 0 2px 24px rgba(0, 0, 0, 0.25);
    }

    .brand-desc {
      max-width: 460px;
      margin: 24px 0 0;
      font-size: 14px;
      line-height: 1.9;
      color: rgba(224, 232, 240, 0.62);
    }

    .brand-flow {
      display: grid;
      grid-template-columns: 1fr 1fr;
      margin: 44px 0 0;
      padding: 0;
      list-style: none;

      li {
        display: grid;
        grid-template-columns: 32px 1fr;
        grid-template-rows: auto auto;
        column-gap: 12px;
        padding: 18px 0;
        border-top: 1px solid rgba(255, 255, 255, 0.12);

        &:nth-child(odd) {
          padding-right: 28px;
          border-right: 1px solid rgba(255, 255, 255, 0.12);
        }

        &:nth-child(even) {
          padding-left: 28px;
        }
      }

      .flow-index {
        grid-row: 1 / 3;
        font-family: "Songti SC", "Noto Serif SC", STSong, serif;
        font-size: 15px;
        font-weight: 600;
        color: #c05a4a;
        line-height: 1.5;
      }

      .flow-label {
        font-size: 15px;
        font-weight: 600;
        letter-spacing: 0.04em;
        color: #eef3f8;
        line-height: 1.5;
      }

      .flow-note {
        margin-top: 2px;
        font-size: 12px;
        color: rgba(224, 232, 240, 0.5);
        line-height: 1.6;
      }
    }
  }

  .brand-foot {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    padding-top: 20px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
    font-size: 11px;
    letter-spacing: 0.12em;
    color: rgba(224, 232, 240, 0.42);
    animation: login-rise 0.6s cubic-bezier(0.2, 0, 0, 1) 0.16s both;

    .brand-code {
      letter-spacing: 0.24em;
      text-transform: uppercase;
    }
  }

  .brand-watermark {
    position: absolute;
    right: -28px;
    bottom: -56px;
    z-index: 0;
    font-family: "Songti SC", "Noto Serif SC", STSong, serif;
    font-size: 220px;
    font-weight: 700;
    line-height: 1;
    letter-spacing: 0.05em;
    color: rgba(192, 90, 74, 0.07);
    writing-mode: vertical-rl;
    user-select: none;
    pointer-events: none;
    animation: login-fade 0.9s ease-out 0.3s both;
  }
}

/* 右侧表单区 */
.login-panel {
  position: relative;
  flex: 0 0 clamp(420px, 42%, 560px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: clamp(32px, 5vw, 64px);
  background: #f8fafc;
  border-left: 1px solid #e4eaf1;
  border-left: 1px solid var(--app-border, #e4eaf1);

  .panel-inner {
    width: 100%;
    max-width: 372px;
    margin: 0 auto;
    animation: login-rise 0.6s cubic-bezier(0.2, 0, 0, 1) 0.12s both;
  }

  .mobile-brand {
    display: none;
    align-items: center;
    gap: 12px;
    margin-bottom: 36px;

    .brand-seal {
      width: 34px;
      height: 34px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      font-family: "Songti SC", "Noto Serif SC", STSong, serif;
      font-size: 19px;
      font-weight: 700;
      color: #c05a4a;
      background: #fbeeec;
      border: 1px solid #c05a4a;
      border-radius: 8px;
    }

    .mobile-brand-name {
      font-family: "Songti SC", "Noto Serif SC", STSong, serif;
      font-size: 18px;
      font-weight: 700;
      letter-spacing: 0.04em;
      color: #14273f;
    }
  }

  .panel-head {
    margin-bottom: 32px;

    .panel-eyebrow {
      display: flex;
      align-items: center;
      gap: 10px;
      margin: 0 0 14px;
      font-size: 11px;
      font-weight: 600;
      letter-spacing: 0.28em;
      color: #1f65bd;
      color: var(--app-primary, #1f65bd);

      .panel-eyebrow-rule {
        width: 16px;
        height: 2px;
        background: currentColor;
      }
    }

    .panel-title {
      margin: 0;
      font-size: 30px;
      font-weight: 600;
      line-height: 1.3;
      letter-spacing: 0.02em;
      color: #14273f;
    }

    .panel-subtitle {
      margin: 10px 0 0;
      font-size: 13px;
      color: #667085;
    }
  }

  .login-form {
    ::v-deep .el-form-item {
      margin-bottom: 20px;
    }

    ::v-deep .el-form-item__label {
      padding-bottom: 8px;
      font-size: 13px;
      font-weight: 600;
      line-height: 1.4;
      color: #3f4e60;
    }

    ::v-deep .el-form-item.is-required .el-form-item__label::before {
      display: none;
    }

    ::v-deep .el-form-item__error {
      padding-top: 5px;
      font-size: 12px;
      color: #c53d46;
    }

    .login-input {
      ::v-deep .el-input__inner {
        height: 46px;
        line-height: 46px;
        padding-left: 40px;
        padding-right: 14px;
        font-size: 14px;
        color: #1b2735;
        background: #ffffff;
        border: 1px solid #dfe6ef;
        border-radius: 8px;
        transition: border-color 0.2s ease, box-shadow 0.2s ease;

        &::placeholder {
          color: #9aa6b2;
        }

        &:hover {
          border-color: #c6d2e0;
        }

        &:focus {
          border-color: #1f65bd;
          border-color: var(--app-primary, #1f65bd);
          box-shadow: 0 0 0 3px rgba(31, 101, 189, 0.12);
        }
      }

      ::v-deep .el-input__prefix {
        left: 13px;
        display: flex;
        align-items: center;
      }

      .login-input-icon {
        font-size: 15px;
        color: #8b98a8;
      }

      ::v-deep .el-input__suffix {
        display: flex;
        align-items: center;
        color: #8b98a8;
      }
    }

    .captcha-wrapper {
      display: flex;
      gap: 10px;

      .captcha-input {
        flex: 1;
        min-width: 0;
      }

      .captcha-img {
        position: relative;
        flex: 0 0 112px;
        height: 46px;
        padding: 0;
        overflow: hidden;
        cursor: pointer;
        background: #ffffff;
        border: 1px solid #dfe6ef;
        border-radius: 8px;
        transition: border-color 0.2s ease, box-shadow 0.2s ease;

        img {
          display: block;
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .captcha-refresh {
          position: absolute;
          inset: 0;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #ffffff;
          background: rgba(20, 39, 63, 0.52);
          opacity: 0;
          transition: opacity 0.2s ease;

          i {
            font-size: 20px;
          }
        }

        &:hover,
        &:focus-visible {
          border-color: #1f65bd;
          border-color: var(--app-primary, #1f65bd);
          box-shadow: 0 0 0 3px rgba(31, 101, 189, 0.1);
          outline: none;

          .captcha-refresh {
            opacity: 1;
          }
        }
      }
    }

    .login-options {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 12px;
      margin: 4px 0 24px;

      .remember-checkbox {
        ::v-deep .el-checkbox__label {
          padding-left: 6px;
          font-size: 13px;
          color: #536273;
        }

        ::v-deep .el-checkbox__inner {
          border-radius: 4px;
        }
      }

      .options-hint {
        font-size: 12px;
        color: #8b98a8;
      }
    }

    .login-btn {
      width: 100%;
      height: 46px;
      font-size: 15px;
      font-weight: 500;
      letter-spacing: 0.18em;
      color: #ffffff;
      background: #1f65bd;
      background: var(--app-primary, #1f65bd);
      border: none;
      border-radius: 8px;
      box-shadow: 0 8px 20px rgba(31, 101, 189, 0.22);
      transition: background-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;

      &:hover,
      &:focus {
        color: #ffffff;
        background: #1a5299;
        background: var(--app-primary-hover, #1a5299);
        box-shadow: 0 10px 24px rgba(31, 101, 189, 0.3);
        transform: translateY(-1px);
      }

      &:active {
        transform: translateY(0);
        box-shadow: 0 6px 16px rgba(31, 101, 189, 0.24);
      }

      &.is-loading {
        opacity: 0.85;
        transform: none;
      }

      span {
        color: #ffffff;
      }
    }

    .register-link {
      margin-top: 18px;
      text-align: center;
      font-size: 13px;
      color: #8b98a8;

      a {
        margin-left: 6px;
        color: #1f65bd;
        color: var(--app-primary, #1f65bd);
        text-decoration: none;

        &:hover {
          color: #1a5299;
          text-decoration: underline;
        }
      }
    }

    .security-note {
      display: flex;
      align-items: flex-start;
      gap: 8px;
      margin-top: 28px;
      padding-top: 18px;
      border-top: 1px solid #edf1f5;
      font-size: 12px;
      line-height: 1.7;
      color: #8b98a8;

      i {
        margin-top: 2px;
        font-size: 13px;
        color: #a4b0bf;
      }
    }
  }

  .panel-foot {
    position: absolute;
    right: 0;
    bottom: 22px;
    left: 0;
    text-align: center;
    font-size: 12px;
    color: #9aa6b2;
  }
}

@media (max-width: 1200px) {
  .login-brand {
    flex-basis: 54%;
    padding: 40px;

    .brand-main .brand-title {
      font-size: clamp(34px, 4vw, 46px);
    }
  }

  .login-panel {
    flex-basis: clamp(400px, 46%, 520px);
    padding: 40px;
  }
}

@media (max-width: 960px) {
  .login-page {
    display: block;
  }

  .login-brand {
    display: none;
  }

  .login-panel {
    min-height: 100vh;
    padding: 48px 24px 72px;
    background: #f4f7fb;
    background: var(--app-page, #f4f7fb);
    border-left: none;

    .panel-inner {
      max-width: 400px;
    }

    .mobile-brand {
      display: flex;
    }
  }
}

@media (max-width: 480px) {
  .login-panel {
    padding: 36px 20px 64px;

    .panel-head {
      margin-bottom: 26px;

      .panel-title {
        font-size: 26px;
      }
    }

    .login-form {
      .login-options {
        flex-wrap: wrap;

        .options-hint {
          width: 100%;
          text-align: right;
        }
      }

      .captcha-wrapper .captcha-img {
        flex-basis: 100px;
      }
    }
  }
}

@media (prefers-reduced-motion: reduce) {
  .login-brand .brand-top,
  .login-brand .brand-main,
  .login-brand .brand-foot,
  .login-brand .brand-watermark,
  .login-panel .panel-inner {
    animation: none;
  }

  .login-form .login-btn {
    transition: none;

    &:hover,
    &:focus,
    &:active {
      transform: none;
    }
  }
}
</style>
