<template>
  <div class="page">

    <div v-if="loading" class="loading-container">
      <van-loading size="24px">加载中...</van-loading>
    </div>

    <div v-else class="content">
      <!-- 图片分类列表 -->
      <div class="category-section" v-for="category in imageCategories" :key="category.key">
        <div class="category-title">{{ category.name }}（{{ category.images.length }}）</div>
        <div class="image-grid">
          <div
            class="image-item"
            v-for="(img, index) in category.images"
            :key="index"
            @click="previewImage(category.images, index)"
          >
            <van-image class="image-thumbnail" :src="getImageUrl(img)" fit="cover" />
            <div class="magnify-icon-wrapper">
              <van-icon name="enlarge" class="magnify-icon" />
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <van-empty v-if="imageCategories.length === 0" description="暂无图片" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showImagePreview } from 'vant'
import { getInstitutionImages } from '@/api/institution'

const router = useRouter()
const route = useRoute()

const institutionId = ref('')
const imageCategories = ref([])
const loading = ref(false)

const onClickLeft = () => {
  router.back()
}

const previewImage = (images, index) => {
  // 转换为完整URL
  const fullUrls = images.map(url => getImageUrl(url))
  showImagePreview({
    images: fullUrls,
    startPosition: index,
    closeable: true
  })
}

const loadImages = async () => {
  if (!institutionId.value) {
    showToast({
      message: '机构ID缺失',
      position: 'top'
    })
    return
  }

  loading.value = true
  try {
    const response = await getInstitutionImages(institutionId.value)

    if (response.code === 200) {
      imageCategories.value = response.data || []

      if (imageCategories.value.length === 0) {
        showToast({
          message: '暂无图片',
          position: 'top'
        })
      }
    }
  } catch (error) {
    console.error('获取机构图片失败:', error)
    showToast({
      message: '获取图片失败',
      position: 'top'
    })
  } finally {
    loading.value = false
  }
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
  institutionId.value = route.params.id
  if (institutionId.value) {
    loadImages()
  }
})
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f6fc;
  padding-bottom: 20px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.content {
  padding: 15px 18px;
}

.category-section {
  margin-bottom: 14px;
}

.category-title {
  height: 22px;
  color: #1a1a1a;
  font-size: 16px;
  font-weight: 500;
  font-family: "PingFang SC", "苹方-简", sans-serif;
  text-align: left;
  line-height: 22px;
  margin-bottom: 6px;
}

.image-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.image-item {
  width: 120px;
  height: 75px;
  border-radius: 6px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
}

.image-thumbnail {
  width: 100%;
  height: 100%;
}

.image-thumbnail :deep(.van-image__img) {
  width: 100%;
  height: 100%;
}

.magnify-icon-wrapper {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 20px;
  height: 20px;
  border-radius: 6px 0 6px 0;
  background: rgba(0, 0, 0, 0.66);
  display: flex;
  align-items: center;
  justify-content: center;
}

.magnify-icon {
  color: #fff;
  font-size: 12px;
}
</style>
