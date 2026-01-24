<template>
  <div class="vr-container">
    <iframe
      v-if="webViewUrl"
      :src="webViewUrl"
      class="vr-iframe"
      frameborder="0"
      allowfullscreen
    />
    <div v-else class="loading-view">
      <van-loading size="24px" color="#fff">加载中...</van-loading>
    </div>

    <!-- 返回按钮 -->
    <div class="back-btn" @click="onClickLeft">
      <van-icon name="arrow-left" size="20" color="#fff" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()
const route = useRoute()

const vrUrl = ref('')
const webViewUrl = ref('')

const onClickLeft = () => {
  router.back()
}

const loadVR = () => {
  // 转换为完整URL
  const fullUrl = getImageUrl(vrUrl.value)

  // 获取当前页面的base路径
  const baseUrl = window.location.origin

  // 构建VR查看器的URL
  webViewUrl.value = `${baseUrl}/static/vr-viewer.html?imageUrl=${encodeURIComponent(fullUrl)}`

  console.log('VR Viewer URL:', webViewUrl.value)
}

/**
 * 获取图片完整URL - 遵循RuoYi标准
 */
const getImageUrl = (url) => {
  if (!url) return ''

  // 外部链接(http/https开头),直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }

  // RuoYi标准:数据库存储相对路径,前端拼接baseUrl
  const baseUrl = process.env.VUE_APP_BASE_API || ''

  // 如果已包含baseUrl,直接返回
  if (url.indexOf(baseUrl) !== -1) {
    return url
  }

  // 拼接baseUrl + 相对路径
  return baseUrl + (url.startsWith('/') ? url : '/' + url)
}

onMounted(() => {
  const url = route.query.vrUrl
  const id = route.params.id

  if (url) {
    vrUrl.value = decodeURIComponent(url)
    loadVR()
  } else if (id) {
    // 如果有id但没有vrUrl，从详情获取VR
    // 这里暂时提示暂无VR资源
    showToast({
      message: '暂无VR资源',
      position: 'top'
    })
    setTimeout(() => {
      router.back()
    }, 1500)
  } else {
    showToast({
      message: 'VR资源地址缺失',
      position: 'top'
    })
    setTimeout(() => {
      router.back()
    }, 1500)
  }
})
</script>

<style scoped>
.vr-container {
  width: 100%;
  height: 100vh;
  background: #000;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
}

.vr-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.loading-view {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #000;
}

.back-btn {
  position: fixed;
  top: 20px;
  left: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10000;
  transition: background 0.3s;
}

.back-btn:active {
  background: rgba(0, 0, 0, 0.8);
}
</style>
