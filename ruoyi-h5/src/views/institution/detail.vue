<template>
  <div class="institution-detail h5-page h5-page--constrained">
    <div v-if="loading" class="loading-container">
      <van-loading size="24px">正在加载机构信息</van-loading>
    </div>

    <div v-else class="detail-content">
      <header class="hero-section" @click="handleHeaderClick">
        <img
          class="hero-image"
          :src="headerImage || institutionPlaceholder"
          :alt="`${detail.name || detail.institutionName || '养老机构'}图片`"
          @error="handleHeaderImageError"
        />
        <div class="hero-overlay"></div>

        <div class="hero-controls">
          <button type="button" class="hero-control" aria-label="返回" @click.stop="onClickLeft">
            <van-icon name="arrow-left" />
          </button>
          <button type="button" class="hero-control" aria-label="分享" @click.stop="onShare">
            <van-icon name="share-o" />
          </button>
        </div>

        <div class="image-tabs" aria-label="机构图片分类">
          <button
            v-for="item in imageTabs"
            :key="item.key"
            type="button"
            class="image-tab-item"
            :class="{ active: activeImageTab === item.key }"
            :aria-pressed="activeImageTab === item.key"
            @click.stop="switchImageTab(item.key)"
          >
            {{ item.name }}
          </button>
        </div>

        <button type="button" class="view-all-btn" @click.stop="viewAllImages">
          <van-icon name="photo-o" />
          <span>全部图片</span>
          <van-icon name="arrow" />
        </button>

        <span v-if="activeImageTab === 'vr'" class="vr-hint">
          <van-icon name="video-o" />
          点击画面进入 VR 看房
        </span>
      </header>

      <main class="detail-main">
        <section class="institution-info-card h5-card">
          <div class="institution-info-header">
            <div class="institution-heading">
              <span v-if="detail.isFavorite" class="favorite-mark">
                <van-icon name="star" /> 已收藏
              </span>
              <h1>{{ detail.name || detail.institutionName || '机构名称完善中' }}</h1>
            </div>
            <div class="institution-price">
              <template v-if="detail.priceRanges?.total?.min">
                <span class="price-symbol">¥</span>
                <strong>{{ detail.priceRanges.total.min }}</strong>
                <span class="price-unit">/月起</span>
              </template>
              <span v-else class="price-pending">价格面议</span>
            </div>
          </div>

          <div class="institution-facts">
            <div class="fact-item">
              <span class="fact-value">{{ detail.buildingArea || 0 }}<small>m²</small></span>
              <span class="fact-label">建筑面积</span>
            </div>
            <div class="fact-item">
              <span class="fact-value">{{ detail.bedCount || 0 }}<small>床</small></span>
              <span class="fact-label">机构床位</span>
            </div>
            <div class="fact-item">
              <span class="fact-value">{{ detail.registeredCapital || 0 }}<small>万</small></span>
              <span class="fact-label">注册资本</span>
            </div>
            <div class="fact-item">
              <span class="fact-value fact-value--date">{{ detail.establishDate || '-' }}</span>
              <span class="fact-label">成立时间</span>
            </div>
          </div>
        </section>

        <section class="location-card h5-card">
          <div class="info-row">
            <span class="info-row__icon"><van-icon name="user-o" /></span>
            <div class="info-row__content">
              <span class="info-row__label">收住对象</span>
              <span class="info-row__value">
                {{ detail.acceptElderType || detail.careLevelsText || '信息完善中' }}
              </span>
            </div>
          </div>
          <div class="info-row">
            <span class="info-row__icon"><van-icon name="location-o" /></span>
            <div class="info-row__content">
              <span class="info-row__label">机构地址</span>
              <span class="info-row__value">
                {{ detail.address || detail.actualAddress || '地址信息完善中' }}
              </span>
            </div>
          </div>
        </section>

        <section class="price-section">
          <div class="section-heading">
            <div>
              <h2>月参考价格</h2>
              <p>以下为费用区间，实际价格以机构确认为准</p>
            </div>
            <van-icon name="gold-coin-o" />
          </div>
          <div class="price-grid">
            <div class="price-item price-item--primary">
              <span class="price-label">总费用</span>
              <span class="price-value">
                ¥{{ detail.priceRanges?.total?.min || 1500 }} ~ ¥{{ detail.priceRanges?.total?.max || 3500 }}
              </span>
            </div>
            <div class="price-item">
              <span class="price-label">床位费</span>
              <span class="price-value">
                ¥{{ detail.priceRanges?.total?.min || detail.priceRanges?.bed?.min || 500 }} ~ ¥{{ detail.priceRanges?.total?.max || detail.priceRanges?.bed?.max || 800 }}
              </span>
            </div>
            <div class="price-item">
              <span class="price-label">护理费</span>
              <span class="price-value">
                ¥{{ detail.priceRanges?.nursing?.min || 800 }} ~ ¥{{ detail.priceRanges?.nursing?.max || 2000 }}
              </span>
            </div>
            <div class="price-item">
              <span class="price-label">膳食费</span>
              <span class="price-value">
                ¥{{ detail.priceRanges?.diet?.min || 600 }} ~ ¥{{ detail.priceRanges?.diet?.max || 1200 }}
              </span>
            </div>
          </div>
        </section>
      </main>

      <van-tabs v-model:active="activeTab" class="detail-tabs" sticky offset-top="46">
        <van-tab title="机构介绍" name="intro">
          <van-cell-group
            v-if="detail.lifeFacilities && detail.lifeFacilities.length > 0"
            title="生活设施"
          >
            <div class="facility-grid">
              <div v-for="(facility, index) in detail.lifeFacilities" :key="index" class="facility-item">
                <span class="facility-icon-wrap">
                  <svg-icon :icon-class="facility.icon" class="facility-icon" />
                </span>
                <span>{{ facility.name }}</span>
              </div>
            </div>
          </van-cell-group>

          <van-cell-group
            v-if="detail.medicalFacilities && detail.medicalFacilities.length > 0"
            title="医疗设施"
          >
            <div class="facility-grid">
              <div v-for="(facility, index) in detail.medicalFacilities" :key="index" class="facility-item">
                <span class="facility-icon-wrap facility-icon-wrap--medical">
                  <svg-icon :icon-class="facility.icon" class="facility-icon medical" />
                </span>
                <span>{{ facility.name }}</span>
              </div>
            </div>
          </van-cell-group>

          <van-cell-group
            v-if="detail.dailyServices && detail.dailyServices.length > 0"
            title="每日服务"
          >
            <div class="service-schedule">
              <div v-for="(service, index) in detail.dailyServices" :key="index" class="schedule-item">
                <div class="schedule-time">{{ service.time }}</div>
                <div class="schedule-content">{{ service.content }}</div>
              </div>
            </div>
          </van-cell-group>

          <van-cell-group title="机构介绍">
            <div class="intro-content">
              {{ detail.description || '机构介绍正在完善中' }}
            </div>
          </van-cell-group>
        </van-tab>

        <van-tab title="评价" name="review">
          <div v-if="reviewLoading" class="review-loading">
            <van-loading size="20px">加载评价中</van-loading>
          </div>

          <template v-else-if="reviewList.length > 0">
            <div class="review-summary h5-card">
              <div class="overall-rating">
                <strong class="rating-score">{{ detail.rating || 0 }}</strong>
                <div class="rating-meta">
                  <van-rate
                    v-model="detail.rating"
                    :size="18"
                    color="var(--h5-color-warning)"
                    void-icon="star"
                    void-color="var(--h5-color-border-strong)"
                    readonly
                  />
                  <span>{{ detail.reviewCount || 0 }} 条真实评价</span>
                </div>
              </div>
            </div>

            <div class="review-list h5-card">
              <article
                v-for="(review, index) in reviewList"
                :key="review.reviewId || index"
                class="review-item"
              >
                <div class="review-header">
                  <img
                    class="review-avatar"
                    :src="review.avatar || userAvatarPlaceholder"
                    :alt="`${review.userName || '用户'}头像`"
                    @error="handleImageError($event, userAvatarPlaceholder)"
                  />
                  <div class="review-user">
                    <div class="user-name">{{ review.userName }}</div>
                    <van-rate
                      v-model="review.rating"
                      :size="14"
                      color="var(--h5-color-warning)"
                      void-icon="star"
                      void-color="var(--h5-color-border-strong)"
                      readonly
                    />
                  </div>
                  <time class="review-date">{{ review.createTime }}</time>
                </div>
                <p class="review-content">{{ review.content }}</p>
                <div v-if="review.images && review.images.length > 0" class="review-images">
                  <img
                    v-for="(img, imgIndex) in review.images"
                    :key="imgIndex"
                    class="review-image"
                    :src="img || institutionPlaceholder"
                    alt="评价图片"
                    loading="lazy"
                    @error="handleImageError"
                    @click="previewReviewImage(review.images, imgIndex)"
                  />
                </div>
              </article>
            </div>
          </template>

          <div v-else class="no-reviews h5-card">
            <van-empty description="暂无评价，入住体验后可以分享感受" image-size="96" />
          </div>
        </van-tab>
      </van-tabs>

      <div class="action-bar h5-fixed-action-bar">
        <van-button round plain icon="star-o" @click="toggleFavorite">
          {{ detail.isFavorite ? '已收藏' : '收藏' }}
        </van-button>
        <van-button round plain icon="phone-o" @click="makeCall">电话</van-button>
        <van-button round plain type="primary" icon="calendar-o" @click="bookVisit">预约</van-button>
        <van-button round type="primary" icon="home-o" @click="applyEnter">入住</van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showDialog, showImagePreview } from 'vant'
import { getInstitutionDetail, favoriteInstitution, unfavoriteInstitution, checkFavorite } from '@/api/institution'
import { getReviewList, getReviewStatistics } from '@/api/review'
import { getToken } from '@/utils/auth'
import institutionPlaceholder from '@/assets/images/institution-placeholder.svg'
import userAvatarPlaceholder from '@/assets/images/user-avatar-placeholder.svg'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const activeTab = ref('intro')
const activeImageTab = ref('main') // 默认选中主图
const selectedFacilityType = ref('room')

// 图片类型标签
const imageTabs = ref([
  { key: 'vr', name: 'VR' },
  { key: 'main', name: '主图' },
  { key: 'environment', name: '环境' },
  { key: 'room', name: '房间' },
  { key: 'basic', name: '设施' },
  { key: 'park', name: '园址' }
])

// 各类型图片数据
const imageData = ref({
  vr: [],
  main: [],
  environment: [],
  room: [],
  basic: [],
  park: []
})

// 顶部显示的图片
const headerImage = ref(institutionPlaceholder)

// 评价相关数据
const reviewList = ref([])
const reviewStatistics = ref({})
const reviewLoading = ref(false)

const detail = ref({})

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return institutionPlaceholder

  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }

  const baseUrl = process.env.VUE_APP_BASE_API || ''
  if (url.indexOf(baseUrl) !== -1) {
    return url
  }

  return baseUrl + (url.startsWith('/') ? url : '/' + url)
}

const handleHeaderImageError = () => {
  headerImage.value = institutionPlaceholder
}

const handleImageError = (event, fallback = institutionPlaceholder) => {
  if (event.target.dataset.fallbackApplied) return
  event.target.dataset.fallbackApplied = 'true'
  event.target.src = fallback
}

// 切换图片标签
const switchImageTab = (key) => {
  activeImageTab.value = key

  const images = imageData.value[key] || []
  if (images.length > 0) {
    headerImage.value = getImageUrl(images[0])
  } else {
    headerImage.value = institutionPlaceholder
  }
}

// 处理顶部图片区域点击
const handleHeaderClick = () => {
  if (activeImageTab.value === 'vr' && imageData.value.vr.length > 0) {
    const vrUrl = encodeURIComponent(imageData.value.vr[0])
    router.push({
      name: 'InstitutionVR',
      params: { id: route.params.id },
      query: { vrUrl }
    })
  } else if (activeImageTab.value === 'vr') {
    showToast('暂无VR资源')
  }
}

// 查看全部图片
const viewAllImages = () => {
  router.push({
    name: 'InstitutionImages',
    params: { id: route.params.id }
  })
}

// 收藏切换
const toggleFavorite = async () => {
  try {
    const token = getToken()
    if (!token) {
      await showDialog({
        title: '登录提示',
        message: '请先登录后再进行收藏操作',
        confirmButtonText: '去登录',
        cancelButtonText: '取消'
      }).then(() => {
        router.push({
          path: '/login',
          query: { redirect: route.fullPath }
        })
      }).catch(() => {
        // 用户取消
      })
      return
    }

    if (detail.value.isFavorite) {
      await showDialog({
        title: '取消收藏',
        message: `确定要取消收藏「${detail.value.name}」吗？`,
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })

      await unfavoriteInstitution(detail.value.institutionId)
      detail.value.isFavorite = false
      showToast('已取消收藏')
    } else {
      await favoriteInstitution(detail.value.institutionId)
      detail.value.isFavorite = true
      showToast('收藏成功')
    }
  } catch (error) {
    if (error.message && error.message.includes('cancel')) {
      return
    }
    console.error('收藏操作失败:', error)

    const errorMsg = error.response?.data?.msg || error.message || '操作失败'

    if (errorMsg.includes('已经收藏') || errorMsg.includes('已收藏')) {
      showToast('您已经收藏过该机构了')
      detail.value.isFavorite = true
    } else {
      showToast(errorMsg)
    }
  }
}

// 检查收藏状态
const checkFavoriteStatus = async () => {
  try {
    const response = await checkFavorite(detail.value.institutionId)
    if (response.code === 200) {
      detail.value.isFavorite = response.data.isFavorited
    }
  } catch (error) {
    console.error('检查收藏状态失败', error)
    detail.value.isFavorite = false
  }
}

// 预览评价图片
const previewReviewImage = (images, startIndex = 0) => {
  showImagePreview({
    images: images,
    startPosition: startIndex,
    closeable: true
  })
}

// 返回上一页
const onClickLeft = () => {
  router.back()
}

// 分享
const onShare = () => {
  showToast('分享功能开发中')
}

// 电话咨询
const makeCall = () => {
  window.location.href = `tel:${detail.value.contactPhone}`
}

// 申请入住
const applyEnter = () => {
  router.push({
    name: 'OrderConfirm',
    params: { institutionId: route.params.id }
  })
}

// 预约参观
const bookVisit = () => {
  router.push({
    name: 'AppointmentBooking',
    params: { institutionId: route.params.id }
  })
}

// 加载评价数据
const loadReviews = async () => {
  if (!detail.value.institutionId) return

  try {
    reviewLoading.value = true

    const [listResponse, statsResponse] = await Promise.all([
      getReviewList(detail.value.institutionId, 1, 10),
      getReviewStatistics(detail.value.institutionId)
    ])

    if (listResponse.code === 200) {
      const data = listResponse.data || {}
      const rows = data.rows || []

      if (rows.length > 0) {
        reviewList.value = rows.map(review => {
          try {
            const parsedImages = review.images
              ? JSON.parse(review.images || '[]').map(img => img.url || img)
              : []

            return {
              reviewId: review.reviewId,
              userName: review.userName || '匿名用户',
              avatar: '',
              rating: Math.round(review.averageRating || 0),
              createTime: review.reviewTime || review.createTime,
              content: review.content,
              images: parsedImages
            }
          } catch (error) {
            return {
              reviewId: review.reviewId,
              userName: review.userName || '匿名用户',
              avatar: '',
              rating: Math.round(review.averageRating || 0),
              createTime: review.reviewTime || review.createTime,
              content: review.content,
              images: []
            }
          }
        })
      } else {
        reviewList.value = []
      }
    } else {
      reviewList.value = []
    }

    if (statsResponse.code === 200 && statsResponse.data) {
      reviewStatistics.value = statsResponse.data
      detail.value.rating = reviewStatistics.value.averageRating || 0
      detail.value.reviewCount = reviewStatistics.value.totalCount || reviewList.value.length
    }

  } catch (error) {
    console.error('加载评价数据失败:', error)
  } finally {
    reviewLoading.value = false
  }
}

// 护理等级转文字
const careLevelsMap = {
  '1': '自理',
  '2': '半护理',
  '3': '全护理',
  '4': '失能',
  '5': '失智'
}

const getCareLevelsText = (careLevels) => {
  if (!careLevels) return ''
  const levels = careLevels.split(',')
  return levels.map(level => careLevelsMap[level] || level).join('、')
}

// 加载详情
const loadDetail = async () => {
  try {
    loading.value = true

    const response = await getInstitutionDetail(route.params.id)

    detail.value = {
      ...response.data,
      institutionId: response.data.institutionId || response.data.id || route.params.id,
      isFavorite: false,
      rating: response.data.rating || 0,
      reviews: response.data.reviews || [],
      roomFacilities: response.data.roomFacilities || [],
      basicFacilities: response.data.basicFacilities || [],
      parkFacilities: response.data.parkFacilities || [],
      lifeFacilities: response.data.lifeFacilities || [],
      medicalFacilities: response.data.medicalFacilities || [],
      dailyServices: response.data.dailyServices || [],
      // 添加护理等级文字
      careLevelsText: getCareLevelsText(response.data.careLevels)
    }

    // 处理图片分类数据
    if (response.data.imageCategories) {
      response.data.imageCategories.forEach(category => {
        if (Object.prototype.hasOwnProperty.call(imageData.value, category.key)) {
          imageData.value[category.key] = category.images || []
        }
      })
    }

    // 设置默认顶部图片
    if (imageData.value.main.length > 0) {
      headerImage.value = getImageUrl(imageData.value.main[0])
      activeImageTab.value = 'main'
    } else if (imageData.value.vr.length > 0) {
      headerImage.value = getImageUrl(imageData.value.vr[0])
      activeImageTab.value = 'vr'
    }

    await checkFavoriteStatus()
    await loadReviews()
  } catch (error) {
    console.error('加载机构详情失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDetail()
})

</script>

<style scoped>
.institution-detail {
  overflow-x: hidden;
}

.detail-content {
  padding-bottom: calc(var(--h5-action-bar-min-height) + var(--h5-safe-area-bottom) + var(--h5-space-6));
}

.loading-container {
  display: flex;
  min-height: 100vh;
  min-height: 100dvh;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-text-tertiary);
}

.hero-section {
  position: relative;
  height: 264px;
  overflow: hidden;
  background: var(--h5-color-primary-soft);
  cursor: pointer;
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    linear-gradient(180deg, rgba(15, 31, 50, 0.52) 0%, transparent 34%),
    linear-gradient(180deg, transparent 54%, rgba(15, 31, 50, 0.68) 100%);
}

.hero-controls {
  position: absolute;
  top: calc(var(--h5-safe-area-top) + var(--h5-space-3));
  right: var(--h5-page-padding);
  left: var(--h5-page-padding);
  z-index: 3;
  display: flex;
  justify-content: space-between;
}

.hero-control {
  display: inline-flex;
  width: 42px;
  height: 42px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-text-inverse);
  font-size: 22px;
  background: rgba(15, 31, 50, 0.56);
  border: 1px solid rgba(255, 255, 255, 0.28);
  border-radius: 50%;
  backdrop-filter: blur(8px);
}

.hero-control:active {
  background: rgba(15, 31, 50, 0.76);
}

.image-tabs {
  position: absolute;
  bottom: var(--h5-space-4);
  left: var(--h5-page-padding);
  z-index: 3;
  display: flex;
  max-width: calc(100% - 142px);
  gap: var(--h5-space-1);
  padding: var(--h5-space-1);
  overflow-x: auto;
  background: rgba(15, 31, 50, 0.56);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: var(--h5-radius-pill);
  backdrop-filter: blur(8px);
  scrollbar-width: none;
}

.image-tabs::-webkit-scrollbar {
  display: none;
}

.image-tab-item {
  flex: 0 0 auto;
  min-width: 44px;
  min-height: 32px;
  padding: 4px var(--h5-space-2);
  color: rgba(255, 255, 255, 0.84);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  line-height: 24px;
  background: transparent;
  border: 0;
  border-radius: var(--h5-radius-pill);
}

.image-tab-item.active {
  color: var(--h5-color-primary-800);
  background: var(--h5-color-surface);
}

.view-all-btn {
  position: absolute;
  right: var(--h5-page-padding);
  bottom: var(--h5-space-4);
  z-index: 3;
  display: inline-flex;
  min-height: 42px;
  align-items: center;
  gap: var(--h5-space-1);
  padding: var(--h5-space-2) var(--h5-space-3);
  color: var(--h5-color-text-inverse);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  background: rgba(15, 31, 50, 0.62);
  border: 1px solid rgba(255, 255, 255, 0.24);
  border-radius: var(--h5-radius-pill);
  backdrop-filter: blur(8px);
}

.vr-hint {
  position: absolute;
  bottom: 68px;
  left: 50%;
  z-index: 3;
  display: inline-flex;
  min-height: 32px;
  align-items: center;
  gap: var(--h5-space-1);
  padding: var(--h5-space-1) var(--h5-space-3);
  color: var(--h5-color-text-inverse);
  font-size: var(--h5-font-size-sm);
  background: rgba(15, 31, 50, 0.62);
  border-radius: var(--h5-radius-pill);
  transform: translateX(-50%);
  white-space: nowrap;
}

.detail-main {
  position: relative;
  z-index: 4;
  padding: 0 var(--h5-page-padding);
  margin-top: -12px;
}

.institution-info-card {
  padding: var(--h5-space-4);
  box-shadow: var(--h5-shadow-md);
}

.institution-info-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-3);
}

.institution-heading {
  flex: 1;
  min-width: 0;
}

.favorite-mark {
  display: inline-flex;
  min-height: 26px;
  align-items: center;
  gap: var(--h5-space-1);
  padding: 2px var(--h5-space-2);
  margin-bottom: var(--h5-space-2);
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  line-height: 22px;
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-pill);
}

.institution-heading h1 {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-2xl);
  font-weight: var(--h5-font-weight-bold);
  line-height: 1.4;
}

.institution-price {
  display: flex;
  flex: 0 0 auto;
  align-items: baseline;
  color: var(--h5-color-danger);
}

.price-symbol {
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-semibold);
}

.institution-price strong {
  font-size: var(--h5-font-size-3xl);
  font-weight: var(--h5-font-weight-bold);
  line-height: 1;
}

.price-unit {
  margin-left: 2px;
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
}

.price-pending {
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-medium);
}

.institution-facts {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  margin-top: var(--h5-space-4);
  padding: var(--h5-space-3) 0;
  background: var(--h5-color-surface-subtle);
  border: 1px solid var(--h5-color-divider);
  border-radius: var(--h5-radius-md);
}

.fact-item {
  position: relative;
  display: flex;
  min-width: 0;
  align-items: center;
  flex-direction: column;
  gap: var(--h5-space-1);
  padding: 0 var(--h5-space-2);
  text-align: center;
}

.fact-item + .fact-item::before {
  position: absolute;
  top: 10%;
  bottom: 10%;
  left: 0;
  width: 1px;
  background: var(--h5-color-divider);
  content: '';
}

.fact-value {
  overflow: hidden;
  max-width: 100%;
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
  line-height: 22px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.fact-value small {
  margin-left: 2px;
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-regular);
}

.fact-value--date {
  font-size: var(--h5-font-size-sm);
}

.fact-label {
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
  line-height: 20px;
}

.location-card {
  padding: 0 var(--h5-space-4);
  margin-top: var(--h5-space-3);
  box-shadow: none;
}

.info-row {
  display: flex;
  min-height: 72px;
  align-items: flex-start;
  gap: var(--h5-space-3);
  padding: var(--h5-space-3) 0;
}

.info-row + .info-row {
  border-top: 1px solid var(--h5-color-divider);
}

.info-row__icon {
  display: inline-flex;
  flex: 0 0 38px;
  height: 38px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-primary);
  font-size: 19px;
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-md);
}

.info-row__content {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: var(--h5-space-1);
}

.info-row__label {
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.info-row__value {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-md);
  line-height: 22px;
}

.price-section {
  padding: var(--h5-space-4);
  margin-top: var(--h5-space-3);
  background: var(--h5-color-info-soft);
  border: 1px solid var(--h5-color-primary-100);
  border-radius: var(--h5-radius-lg);
}

.section-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-3);
  margin-bottom: var(--h5-space-4);
}

.section-heading h2 {
  color: var(--h5-color-primary-800);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
}

.section-heading p {
  margin-top: var(--h5-space-1);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
}

.section-heading > .van-icon {
  color: var(--h5-color-primary);
  font-size: 24px;
}

.price-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--h5-space-2);
}

.price-item {
  display: flex;
  min-width: 0;
  min-height: 64px;
  flex-direction: column;
  justify-content: center;
  gap: var(--h5-space-1);
  padding: var(--h5-space-2) var(--h5-space-3);
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid var(--h5-color-primary-100);
  border-radius: var(--h5-radius-md);
}

.price-item--primary {
  background: var(--h5-color-surface);
  border-color: var(--h5-color-primary-200);
}

.price-label {
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
}

.price-value {
  overflow: hidden;
  color: var(--h5-color-primary-700);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-semibold);
  line-height: 20px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-tabs {
  margin-top: var(--h5-space-4);
}

.facility-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: var(--h5-space-4) var(--h5-space-2);
  padding: var(--h5-space-4);
}

.facility-item {
  display: flex;
  min-width: 0;
  align-items: center;
  flex-direction: column;
  gap: var(--h5-space-2);
  text-align: center;
}

.facility-icon-wrap {
  display: inline-flex;
  width: 44px;
  height: 44px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-primary);
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-md);
}

.facility-icon-wrap--medical {
  color: var(--h5-color-success);
  background: var(--h5-color-success-soft);
}

.facility-icon {
  width: 24px;
  height: 24px;
  color: currentColor;
}

.facility-item > span:last-child {
  overflow: hidden;
  max-width: 100%;
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  line-height: 20px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.service-schedule {
  padding: 0 var(--h5-space-4) var(--h5-space-2);
}

.schedule-item {
  display: flex;
  gap: var(--h5-space-3);
  padding: var(--h5-space-3) 0;
  border-bottom: 1px solid var(--h5-color-divider);
}

.schedule-item:last-child {
  border-bottom: 0;
}

.schedule-time {
  flex: 0 0 72px;
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
}

.schedule-content,
.intro-content {
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
  line-height: var(--h5-line-height-relaxed);
}

.intro-content {
  padding: 0 var(--h5-space-4) var(--h5-space-5);
  white-space: pre-line;
}

.review-summary,
.review-list,
.no-reviews {
  margin: var(--h5-space-3) var(--h5-page-padding);
}

.review-summary {
  padding: var(--h5-space-4);
  box-shadow: none;
}

.overall-rating {
  display: flex;
  align-items: center;
  gap: var(--h5-space-4);
}

.rating-score {
  color: var(--h5-color-warning);
  font-size: 36px;
  font-weight: var(--h5-font-weight-bold);
  line-height: 1;
}

.rating-meta {
  display: flex;
  flex-direction: column;
  gap: var(--h5-space-2);
}

.rating-meta span {
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
}

.review-list {
  overflow: hidden;
  box-shadow: none;
}

.review-item {
  padding: var(--h5-space-4);
}

.review-item + .review-item {
  border-top: 1px solid var(--h5-color-divider);
}

.review-header {
  display: flex;
  align-items: center;
  gap: var(--h5-space-3);
}

.review-avatar {
  flex: 0 0 44px;
  width: 44px;
  height: 44px;
  object-fit: cover;
  border: 1px solid var(--h5-color-border);
  border-radius: 50%;
}

.review-user {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: var(--h5-space-1);
}

.user-name {
  overflow: hidden;
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.review-date {
  flex: 0 0 auto;
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.review-content {
  margin-top: var(--h5-space-3);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
  line-height: var(--h5-line-height-relaxed);
  white-space: pre-line;
}

.review-images {
  display: flex;
  flex-wrap: wrap;
  gap: var(--h5-space-2);
  margin-top: var(--h5-space-3);
}

.review-image {
  width: 84px;
  height: 84px;
  object-fit: cover;
  border: 1px solid var(--h5-color-divider);
  border-radius: var(--h5-radius-md);
  cursor: pointer;
}

.review-loading {
  display: flex;
  min-height: 180px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-text-tertiary);
}

.no-reviews {
  box-shadow: none;
}

.action-bar {
  max-width: var(--h5-page-max-width);
  margin: 0 auto;
}

.action-bar :deep(.van-button) {
  flex: 1;
  min-width: 0;
  min-height: 44px;
  padding: 0 var(--h5-space-2);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-semibold);
}

.action-bar :deep(.van-button--plain) {
  background: var(--h5-color-surface);
}

.detail-tabs :deep(.van-tabs__wrap) {
  height: 50px;
  border-top: 1px solid var(--h5-color-divider);
  border-bottom: 1px solid var(--h5-color-divider);
  box-shadow: var(--h5-shadow-xs);
}

.detail-tabs :deep(.van-tabs__nav) {
  padding: 0 var(--h5-page-padding);
  background: var(--h5-color-surface);
}

.detail-tabs :deep(.van-tab) {
  min-height: 48px;
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-medium);
}

.detail-tabs :deep(.van-tab--active) {
  color: var(--h5-color-primary);
  font-weight: var(--h5-font-weight-semibold);
}

.detail-tabs :deep(.van-tabs__line) {
  width: 32px;
  height: 3px;
  background: var(--h5-color-primary);
  border-radius: var(--h5-radius-pill);
}

.detail-tabs :deep(.van-cell-group) {
  margin: var(--h5-space-3) var(--h5-page-padding);
  overflow: hidden;
  background: var(--h5-color-surface);
  border: 1px solid var(--h5-color-divider);
  border-radius: var(--h5-radius-lg);
  box-shadow: none;
}

.detail-tabs :deep(.van-cell-group__title) {
  padding: var(--h5-space-4) var(--h5-space-4) var(--h5-space-3);
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
}

.detail-tabs :deep(.van-tabs__content),
.detail-tabs :deep(.van-tab__panel) {
  background: transparent;
}

:deep(.van-empty__description) {
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
}

@media (max-width: 360px) {
  .hero-section {
    height: 244px;
  }

  .image-tabs {
    max-width: calc(100% - 126px);
  }

  .detail-main {
    padding-right: var(--h5-space-3);
    padding-left: var(--h5-space-3);
  }

  .institution-info-header {
    flex-direction: column;
  }

  .institution-facts {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: var(--h5-space-3) 0;
  }

  .fact-item:nth-child(3)::before {
    display: none;
  }

  .price-grid {
    grid-template-columns: 1fr;
  }

  .facility-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .action-bar {
    gap: var(--h5-space-2);
    padding-right: var(--h5-space-3);
    padding-left: var(--h5-space-3);
  }

  .action-bar :deep(.van-button) {
    padding: 0 var(--h5-space-1);
  }
}
</style>
