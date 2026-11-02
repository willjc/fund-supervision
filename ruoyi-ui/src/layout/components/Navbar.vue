<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb v-if="!topNav" id="breadcrumb-container" class="breadcrumb-container" />
    <top-nav v-if="topNav" id="topmenu-container" class="topmenu-container" />

    <div class="right-menu">
      <template v-if="device!=='mobile'">
        <search id="header-search" class="right-menu-item" />

        <!-- 源码地址和文档地址已隐藏 -->
        <!-- <el-tooltip content="源码地址" effect="dark" placement="bottom">
          <ruo-yi-git id="ruoyi-git" class="right-menu-item hover-effect" />
        </el-tooltip>

        <el-tooltip content="文档地址" effect="dark" placement="bottom">
          <ruo-yi-doc id="ruoyi-doc" class="right-menu-item hover-effect" />
        </el-tooltip> -->

        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <el-tooltip content="布局大小" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip>

      </template>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="hover">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar">
          <span class="user-nickname"> {{ nickName }} </span>
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setLayout" v-if="setting">
            <span>布局设置</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from '@/components/TopNav'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import Search from '@/components/HeaderSearch'
// import RuoYiGit from '@/components/RuoYi/Git'
// import RuoYiDoc from '@/components/RuoYi/Doc'

export default {
  emits: ['setLayout'],
  components: {
    Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
    SizeSelect,
    Search
    // RuoYiGit,
    // RuoYiDoc
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device',
      'nickName'
    ]),
    setting: {
      get() {
        return this.$store.state.settings.showSettings
      }
    },
    topNav: {
      get() {
        return this.$store.state.settings.topNav
      }
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    setLayout(event) {
      this.$emit('setLayout')
    },
    logout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          location.href = '/index'
        })
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 56px;
  overflow: hidden;
  position: relative;
  background: #fff;
  border-bottom: 1px solid var(--app-border, #e4eaf1);

  .hamburger-container {
    width: 56px;
    height: 100%;
    line-height: 56px;
    float: left;
    cursor: pointer;
    color: #536273;
    transition: background 0.16s ease;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: #f4f7fb;
    }
  }

  .breadcrumb-container {
    float: left;
    line-height: 56px;
  }

  .topmenu-container {
    position: absolute;
    left: 56px;
    line-height: 56px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    display: flex;
    align-items: center;
    height: 100%;
    padding-right: 12px;
    line-height: 56px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      height: 34px;
      margin-left: 4px;
      padding: 0 8px;
      border-radius: 8px;
      font-size: 17px;
      color: #5b6b7f;
      vertical-align: middle;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.16s ease, color 0.16s ease;

        &:hover {
          color: var(--app-primary, #1f65bd);
          background: #f4f7fb;
        }
      }
    }

    .avatar-container {
      margin-left: 6px;
      padding: 0 4px 0 10px;

      .avatar-wrapper {
        display: flex;
        align-items: center;
        height: 56px;
        cursor: pointer;

        .user-avatar {
          width: 30px;
          height: 30px;
          border-radius: 50%;
          border: 2px solid #eef6ff;
          object-fit: cover;
        }

        .user-nickname {
          margin-left: 8px;
          color: #1b2735;
          font-size: 13px;
          font-weight: 500;
          line-height: 1;
        }

        .el-icon-caret-bottom {
          margin-left: 4px;
          color: #7b8998;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
