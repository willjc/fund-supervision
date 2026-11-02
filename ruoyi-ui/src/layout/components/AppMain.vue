<template>
  <section class="app-main">
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="cachedViews">
        <router-view v-if="!$route.meta.link" :key="key" />
      </keep-alive>
    </transition>
    <iframe-toggle />
    <copyright />
  </section>
</template>

<script>
import copyright from "./Copyright/index"
import iframeToggle from "./IframeToggle/index"

export default {
  name: 'AppMain',
  components: { iframeToggle, copyright },
  computed: {
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    key() {
      return this.$route.path
    }
  },
  watch: {
    $route() {
      this.addIframe()
    }
  },
  mounted() {
    this.addIframe()
  },
  methods: {
    addIframe() {
      const { name } = this.$route
      if (name && this.$route.meta.link) {
        this.$store.dispatch('tagsView/addIframeView', this.$route)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.app-main {
  /* 56 = navbar */
  min-height: calc(100vh - 56px);
  width: 100%;
  position: relative;
  overflow: hidden;
}

.fixed-header + .app-main {
  overflow-y: auto;
  scrollbar-gutter: auto;
  height: calc(100vh - 56px);
  min-height: 0px;
}

.app-main:has(.copyright) {
  padding-bottom: 36px;
}

.fixed-header + .app-main {
  margin-top: 56px;
}

.hasTagsView {
  .app-main {
    /* 94 = navbar + tags-view = 56 + 38 */
    min-height: calc(100vh - 94px);
  }

  .fixed-header + .app-main {
    margin-top: 94px;
    height: calc(100vh - 94px);
    min-height: 0px;
  }
}
</style>

<style lang="scss">
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background-color: transparent;
}

::-webkit-scrollbar-thumb {
  background-color: #cfd8e3;
  border-radius: 3px;

  &:hover {
    background-color: #b5c2d1;
  }
}
</style>
