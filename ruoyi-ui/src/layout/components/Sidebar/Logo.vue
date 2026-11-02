<template>
  <div class="sidebar-logo-container" :class="[collapse ? 'collapse' : '', sideTheme]">
    <router-link class="sidebar-logo-link" to="/">
      <span class="brand-seal" aria-hidden="true">监</span>
      <span v-if="!collapse" class="brand-text">
        <span class="brand-line-1">养老机构预收费</span>
        <span class="brand-line-2">资金监管平台</span>
      </span>
    </router-link>
  </div>
</template>

<script>
export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    sideTheme() {
      return this.$store.state.settings.sideTheme
    }
  }
}
</script>

<style lang="scss" scoped>
.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 64px;
  overflow: hidden;

  .sidebar-logo-link {
    display: flex;
    align-items: center;
    gap: 10px;
    height: 100%;
    padding: 0 16px;
    text-decoration: none;
  }

  .brand-seal {
    flex: 0 0 auto;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border: 1.5px solid #a93b2f;
    border-radius: 7px;
    color: #a93b2f;
    font-family: "Songti SC", "STSong", "Noto Serif CJK SC", serif;
    font-size: 18px;
    font-weight: 700;
    line-height: 1;
    background: #fbeeec;
    box-shadow: inset 0 0 0 2px rgba(169, 59, 47, 0.08);
  }

  .brand-text {
    display: flex;
    flex-direction: column;
    min-width: 0;
    line-height: 1.2;
  }

  .brand-line-1 {
    color: #667085;
    font-size: 10px;
    letter-spacing: 1px;
  }

  .brand-line-2 {
    margin-top: 3px;
    color: #14273f;
    font-size: 15px;
    font-weight: 700;
    letter-spacing: 0.5px;
  }

  &.collapse {
    .sidebar-logo-link {
      justify-content: center;
      padding: 0;
    }

    .brand-seal {
      width: 30px;
      height: 30px;
      font-size: 16px;
    }
  }

  &.theme-dark {
    .brand-line-1 {
      color: rgba(255, 255, 255, 0.55);
    }

    .brand-line-2 {
      color: #ffffff;
    }

    .brand-seal {
      color: #f0b7ac;
      border-color: #c05a4a;
      background: rgba(192, 90, 74, 0.16);
    }
  }
}
</style>
