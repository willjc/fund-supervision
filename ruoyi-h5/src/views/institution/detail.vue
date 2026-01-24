<template>
  <div class="institution-detail">
    <!-- 导航栏 -->
    <van-nav-bar
      :title="detail.name || '机构详情'"
      left-text="返回"
      left-arrow
      @click-left="onClickLeft"
      fixed
      placeholder
    >
      <template #right>
        <van-icon
          name="share-o"
          size="18"
          @click="onShare"
        />
      </template>
    </van-nav-bar>

    <div v-if="loading" class="loading-container">
      <van-loading size="24px">加载中</van-loading>
    </div>

    <div v-else class="detail-content">
      <!-- 顶部图片区域 -->
      <div class="header-section" @click="handleHeaderClick">
        <van-image class="header-bg" :src="headerImage" fit="cover" />
        <div class="header-overlay"></div>

        <!-- 图片类型切换 -->
        <div class="image-tabs">
          <div
            class="image-tab-item"
            :class="{ active: activeImageTab === item.key }"
            v-for="item in imageTabs"
            :key="item.key"
            @click.stop="switchImageTab(item.key)"
          >
            <span class="image-tab-text" :class="{ active: activeImageTab === item.key }">{{ item.name }}</span>
          </div>
        </div>

        <!-- 查看全部按钮 -->
        <div class="view-all-btn" @click.stop="viewAllImages">
          <span class="view-all-text">查看全部</span>
          <van-icon name="arrow" class="view-all-arrow" />
        </div>
      </div>

      <!-- 机构信息卡片 -->
      <div class="institution-info-card">
        <div class="institution-info-header">
          <span class="institution-title">{{ detail.name }}</span>
          <div class="institution-price">
            <span class="price-number">{{ detail.priceRangeMin || 1000 }}</span>
            <span class="price-unit">元/月起</span>
          </div>
        </div>

        <!-- 机构详细信息 -->
        <div class="institution-detail-card">
          <div class="institution-detail-item">
            <div class="detail-value">
              <span class="detail-number">{{ detail.buildingArea || 0 }}</span>
              <span class="detail-unit">m²</span>
            </div>
            <span class="detail-label">建筑面积</span>
          </div>
          <div class="institution-detail-item">
            <span class="detail-value-text">{{ detail.bedCount || 0 }}</span>
            <span class="detail-label">床位数</span>
          </div>
          <div class="institution-detail-item">
            <span class="detail-value-text">{{ detail.registeredCapital || 0 }}万</span>
            <span class="detail-label">注册资本</span>
          </div>
          <div class="institution-detail-item">
            <span class="detail-value-text">{{ detail.establishDate || '-' }}</span>
            <span class="detail-label">成立时间</span>
          </div>
        </div>
      </div>

      <!-- 收住对象和地址信息 -->
      <div class="location-card">
        <div class="location-content">
          <div class="location-left">
            <div class="location-header">
              <van-icon name="user-o" class="location-icon" />
              <span class="location-name">收住对象</span>
            </div>
            <div class="location-text">{{ detail.acceptElderType || detail.careLevelsText || '暂无信息' }}</div>
          </div>
        </div>
        <div class="address-row">
          <van-icon name="location-o" class="location-icon" />
          <span class="address-text">{{ detail.address || detail.actualAddress || '地址信息完善中' }}</span>
        </div>
      </div>

      <!-- 月参考价格区 -->
      <div class="price-section">
        <div class="price-header">月参考价格</div>
        <div class="price-grid">
          <div class="price-item">
            <span class="price-label">总费用</span>
            <span class="price-value">¥{{ detail.priceRanges?.total?.min || 1500 }} ~ ¥{{ detail.priceRanges?.total?.max || 3500 }}</span>
          </div>
          <div class="price-item">
            <span class="price-label">床位费</span>
            <span class="price-value">¥{{ detail.priceRanges?.bed?.min || 500 }} ~ ¥{{ detail.priceRanges?.bed?.max || 800 }}</span>
          </div>
          <div class="price-item">
            <span class="price-label">护理费</span>
            <span class="price-value">¥{{ detail.priceRanges?.nursing?.min || 800 }} ~ ¥{{ detail.priceRanges?.nursing?.max || 2000 }}</span>
          </div>
          <div class="price-item">
            <span class="price-label">膳食费</span>
            <span class="price-value">¥{{ detail.priceRanges?.diet?.min || 600 }} ~ ¥{{ detail.priceRanges?.diet?.max || 1200 }}</span>
          </div>
        </div>
      </div>

      <!-- Tab切换 -->
      <van-tabs v-model:active="activeTab" sticky offset-top="46">
        <van-tab title="机构介绍" name="intro">
          <!-- 生活设施 -->
          <van-cell-group v-if="detail.lifeFacilities && detail.lifeFacilities.length > 0" title="生活设施">
            <div class="facility-grid">
              <div v-for="(facility, index) in detail.lifeFacilities" :key="index" class="facility-item">
                <svg-icon :icon-class="facility.icon" class="facility-icon" />
                <span>{{ facility.name }}</span>
              </div>
            </div>
          </van-cell-group>

          <!-- 医疗设施 -->
          <van-cell-group v-if="detail.medicalFacilities && detail.medicalFacilities.length > 0" title="医疗设施">
            <div class="facility-grid">
              <div v-for="(facility, index) in detail.medicalFacilities" :key="index" class="facility-item">
                <svg-icon :icon-class="facility.icon" class="facility-icon medical" />
                <span>{{ facility.name }}</span>
              </div>
            </div>
          </van-cell-group>

          <!-- 每日服务 -->
          <van-cell-group v-if="detail.dailyServices && detail.dailyServices.length > 0" title="每日服务">
            <div class="service-schedule">
              <div v-for="(service, index) in detail.dailyServices" :key="index" class="schedule-item">
                <div class="schedule-time">{{ service.time }}</div>
                <div class="schedule-content">{{ service.content }}</div>
              </div>
            </div>
          </van-cell-group>

          <!-- 机构介绍 -->
          <van-cell-group title="机构介绍">
            <div class="intro-content">
              {{ detail.description }}
            </div>
          </van-cell-group>
        </van-tab>

        <van-tab title="评价" name="review">
          <!-- 评价统计摘要 -->
          <div class="review-summary">
            <div class="overall-rating">
              <div class="rating-score">{{ detail.rating || 0 }}</div>
              <van-rate v-model="detail.rating" :size="16" color="#ffd21e" void-icon="star" void-color="#eee" readonly />
              <div class="rating-count">{{ detail.reviewCount || 0 }}条评价</div>
            </div>

            <!-- 加载状态 -->
            <div v-if="reviewLoading" class="review-loading">
              <van-loading size="20px">加载评价中...</van-loading>
            </div>

            <!-- 评价统计信息 -->
            <div v-if="reviewStatistics.totalCount > 0" class="rating-breakdown">
              <div class="rating-item">
                <span class="rating-label">综合</span>
                <van-rate v-model="detail.rating" :size="12" color="#ffd21e" void-icon="star" void-color="#eee" readonly />
                <span class="rating-score">{{ detail.rating || 0 }}</span>
              </div>
            </div>

            <!-- 评价列表 -->
            <div v-if="reviewList && reviewList.length > 0" class="review-list">
              <div v-for="(review, index) in reviewList" :key="review.reviewId || index" class="review-item">
                <div class="review-header">
                  <van-image
                    round
                    width="40"
                    height="40"
                    :src="review.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
                  />
                  <div class="review-user">
                    <div class="user-name">{{ review.userName }}</div>
                    <van-rate v-model="review.rating" :size="12" color="#ffd21e" void-icon="star" void-color="#eee" readonly />
                  </div>
                  <div class="review-date">{{ review.createTime }}</div>
                </div>
                <div class="review-content">{{ review.content }}</div>
                <div v-if="review.images && review.images.length > 0" class="review-images">
                  <van-image
                    v-for="(img, imgIndex) in review.images"
                    :key="imgIndex"
                    width="80"
                    height="80"
                    :src="img"
                    fit="cover"
                    @click="previewReviewImage(review.images, imgIndex)"
                  />
                </div>
              </div>
            </div>

            <!-- 暂无评价 -->
            <div v-else-if="!reviewLoading" class="no-reviews">
              <van-empty description="暂无评价" image-size="80" />
            </div>
          </div>
        </van-tab>
      </van-tabs>

      <!-- 底部操作栏 -->
      <div class="action-bar">
        <van-button round plain @click="toggleFavorite">
          {{ detail.isFavorite ? '已收藏' : '收藏' }}
        </van-button>
        <van-button round plain @click="makeCall">
          电话
        </van-button>
        <van-button round plain type="warning" @click="bookVisit">
          预约
        </van-button>
        <van-button round type="primary" @click="applyEnter">
          入住
        </van-button>
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
const headerImage = ref('/images/default-institution.png')

// 评价相关数据
const reviewList = ref([])
const reviewStatistics = ref({})
const reviewLoading = ref(false)

const detail = ref({})

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return '/images/default-institution.png'

  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }

  const baseUrl = process.env.VUE_APP_BASE_API || ''
  if (url.indexOf(baseUrl) !== -1) {
    return url
  }

  return baseUrl + (url.startsWith('/') ? url : '/' + url)
}

// 切换图片标签
const switchImageTab = (key) => {
  activeImageTab.value = key

  const images = imageData.value[key] || []
  if (images.length > 0) {
    headerImage.value = getImageUrl(images[0])
  } else {
    headerImage.value = '/images/default-institution.png'
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
      detail.value.rating = reviewStatistics.value.averageRating || 4.5
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
      rating: response.data.rating || 4.5,
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
  min-height: 100vh;
  background-color: #f5f6fc;
  padding-bottom: 60px;
  font-family: 'PingFang SC', '苹方-简', sans-serif;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

/* ========== 顶部图片区域 ========== */
.header-section {
  width: 100%;
  height: 200px;
  position: relative;
  overflow: hidden;
}

.header-bg {
  width: 100%;
  height: 100%;
}

.header-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.image-tabs {
  position: absolute;
  bottom: 50px;
  left: 12px;
  display: flex;
  padding: 2px 2px;
  border-radius: 4px;
  opacity: 1;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
}

.image-tab-item {
  padding: 0 8px;
  border-radius: 3px;
  height: 20px;
  line-height: 20px;
}

.image-tab-item.active {
  background: linear-gradient(0deg, #38a9ff 0%, #0f73ff 100%);
  backdrop-filter: blur(6px);
}

.image-tab-text {
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
  font-weight: normal;
}

.image-tab-text.active {
  color: #ffffff;
}

.view-all-btn {
  position: absolute;
  bottom: 50px;
  right: 12px;
  padding: 6px 10px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  gap: 2px;
  cursor: pointer;
}

.view-all-text {
  color: rgba(255, 255, 255, 0.8);
  font-size: 11px;
}

.view-all-arrow {
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
}

/* ========== 机构信息卡片 ========== */
.institution-info-card {
  width: calc(100% - 24px);
  border-radius: 10px;
  background: #ffffff;
  margin: -30px auto 12px;
  padding: 14px;
  box-sizing: border-box;
  position: relative;
  z-index: 1;
}

.institution-info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.institution-title {
  font-size: 16px;
  font-weight: 500;
  color: #1a1a1a;
}

.institution-price {
  display: flex;
  align-items: baseline;
}

.price-number {
  height: 24px;
  color: #e5252b;
  font-size: 22px;
  font-weight: 600;
}

.price-unit {
  height: 20px;
  color: #e5252b;
  font-size: 14px;
}

/* 机构详细信息卡片 */
.institution-detail-card {
  padding: 10px;
  border-radius: 5px;
  background: #f7f9fa;
  display: flex;
  align-items: center;
  justify-content: space-around;
}

.institution-detail-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.detail-value {
  display: flex;
  align-items: baseline;
  justify-content: center;
}

.detail-number {
  color: #333333;
  font-size: 15px;
  font-weight: 500;
}

.detail-unit {
  color: #333333;
  font-size: 13px;
}

.detail-value-text {
  color: #333333;
  font-size: 15px;
  font-weight: 500;
}

.detail-label {
  color: #888888;
  font-size: 11px;
  margin-top: 4px;
}

/* ========== 收住对象和地址信息 ========== */
.location-card {
  width: calc(100% - 24px);
  border-radius: 10px;
  background: #ffffff;
  margin: 0 auto 12px;
  padding: 14px;
  box-sizing: border-box;
}

.location-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.location-left {
  flex: 1;
}

.location-header {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.location-icon {
  color: #1281ff;
  font-size: 14px;
  margin-right: 6px;
}

.location-name {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a1a;
}

.location-text {
  font-size: 13px;
  color: #666666;
  line-height: 1.5;
}

.address-row {
  display: flex;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

.address-row .location-icon {
  margin-right: 6px;
}

.address-text {
  flex: 1;
  font-size: 13px;
  color: #333333;
}

/* ========== 月参考价格区 ========== */
.price-section {
  width: calc(100% - 24px);
  background: linear-gradient(135deg, #ebf6ff 0%, #f0f9ff 100%);
  border-radius: 10px;
  padding: 12px;
  margin: 0 auto 12px;
  box-shadow: 0 2px 8px rgba(18, 129, 255, 0.08);
}

.price-header {
  font-size: 13px;
  font-weight: 500;
  color: #1281ff;
  margin-bottom: 8px;
}

.price-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
}

.price-item {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
}

.price-label {
  color: #666;
  white-space: nowrap;
}

.price-value {
  color: #1281ff;
  font-weight: 600;
  white-space: nowrap;
}

/* ========== 其他原有样式 ========== */
/* 设施网格 */
.facility-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 16px;
}

.facility-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.facility-icon {
  width: 28px;
  height: 28px;
  color: #1281ff;
}

.facility-icon.medical {
  color: #00d9a5;
}

.facility-item span {
  font-size: 12px;
  color: #666;
  text-align: center;
}

/* 服务时间表 */
.service-schedule {
  padding: 16px;
}

.schedule-item {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.schedule-item:last-child {
  border-bottom: none;
}

.schedule-time {
  width: 80px;
  font-size: 14px;
  font-weight: 500;
  color: #1281ff;
  flex-shrink: 0;
}

.schedule-content {
  flex: 1;
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

/* 机构介绍 */
.intro-content {
  padding: 16px;
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}

/* 评价摘要 */
.review-summary {
  background: #fff;
  padding: 16px;
  margin-bottom: 12px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.overall-rating {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.rating-score {
  font-size: 32px;
  font-weight: bold;
  color: #ffb800;
}

.rating-count {
  font-size: 14px;
  color: #666;
}

.rating-breakdown {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rating-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.rating-label {
  width: 40px;
  font-size: 14px;
  color: #666;
}

/* 评价列表 */
.review-list {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.review-item {
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.review-user {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.review-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.review-loading {
  display: flex;
  justify-content: center;
  padding: 20px;
  color: #999;
}

.no-reviews {
  padding: 20px;
}

/* 底部操作栏 */
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 10px;
  padding: 10px 14px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

.action-bar .van-button {
  flex: 1;
  font-size: 13px;
  height: 36px;
}

.action-bar .van-button--primary {
  background: linear-gradient(135deg, #1281ff 0%, #4fc7ff 100%);
  border: none;
  font-weight: 500;
}

.action-bar .van-button--warning {
  background: linear-gradient(135deg, #ffb800 0%, #ffc832 100%);
  border: none;
  font-weight: 500;
}

.action-bar .van-button--plain {
  border-color: #e0e0e0;
  color: #666;
  font-weight: 500;
}

/* Vant 组件样式覆盖 */
:deep(.van-tabs__nav) {
  background: #fff;
  padding: 0 12px;
}

:deep(.van-tab) {
  color: #666;
  font-size: 15px;
  font-weight: 500;
}

:deep(.van-tab--active) {
  color: #1281ff;
}

:deep(.van-tabs__line) {
  display: none;
}

:deep(.van-cell-group__title) {
  color: #1a1a1a;
  font-size: 16px;
  font-weight: 500;
  padding: 12px 16px 8px;
}

:deep(.van-cell-group) {
  background: #fff;
  margin: 10px 12px;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

:deep(.van-cell-group .van-cell__right-icon) {
  display: none;
}

:deep(.van-cell::after) {
  display: none;
}

:deep(.van-tabs__content) {
  background: transparent;
}

:deep(.van-tab__panel) {
  background: transparent;
}

:deep(.van-nav-bar) {
  background: #fff;
}

:deep(.van-nav-bar__title) {
  font-size: 17px;
  font-weight: 500;
  color: #1a1a1a;
}

:deep(.van-hairline--bottom::after) {
  border-color: #f0f0f0;
}
</style>
