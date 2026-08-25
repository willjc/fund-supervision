<template>
  <div class="home-page h5-page h5-page--constrained h5-page--tabbar">
    <header class="hero-section">
      <van-swipe
        v-if="bannerList.length"
        class="hero-swiper"
        :autoplay="3000"
        :show-indicators="false"
        @change="onBannerChange"
      >
        <van-swipe-item
          v-for="(banner, index) in bannerList"
          :key="index"
          @click="handleBannerClick(banner)"
        >
          <img
            class="hero-image"
            :src="getImageUrl(banner.imageUrl || banner.image) || institutionPlaceholder"
            :alt="banner.title || '养老服务宣传图'"
            @error="handleImageError"
          />
        </van-swipe-item>
      </van-swipe>
      <img
        v-else
        class="hero-image"
        :src="institutionPlaceholder"
        alt="养老服务机构"
      />
      <div class="hero-overlay"></div>
      <div class="hero-copy">
        <span class="hero-eyebrow">郑州市养老服务</span>
        <h1>安心养老，放心选择</h1>
        <p>查机构、看服务、管费用，一站安心办理</p>
      </div>

      <div v-if="bannerList.length > 1" class="banner-indicators" aria-hidden="true">
        <span
          v-for="(item, index) in bannerList"
          :key="index"
          class="indicator-dot"
          :class="{ active: currentBannerIndex === index }"
        ></span>
      </div>

      <button class="hero-search" type="button" @click="goToSearch">
        <van-icon name="search" class="search-icon" />
        <span>搜索机构名称、区域或服务</span>
        <van-icon name="arrow" class="search-arrow" />
      </button>
    </header>

    <main class="home-content">
      <section class="quick-services h5-card" aria-labelledby="quick-services-title">
        <div class="quick-services__header">
          <div>
            <h2 id="quick-services-title">便捷服务</h2>
            <p>常用功能，一步直达</p>
          </div>
        </div>
        <div class="icon-grid">
          <button
            v-for="item in iconList"
            :key="item.key"
            type="button"
            class="icon-item"
            @click="handleIconClick(item)"
          >
            <span class="icon-wrapper" :class="`icon-wrapper--${item.key}`">
              <van-icon :name="item.icon" />
            </span>
            <span class="icon-text">{{ item.name }}</span>
          </button>
        </div>
      </section>

      <button class="notice-card h5-card" type="button" @click="goToNoticeDetail">
        <span class="notice-icon-wrap"><van-icon name="volume-o" /></span>
        <span class="notice-copy">
          <strong>最新通知</strong>
          <span>{{ noticeText || '暂无通知，点击查看通知公告' }}</span>
        </span>
        <van-icon name="arrow" class="notice-arrow" />
      </button>

      <section class="listings-section" aria-labelledby="recommended-title">
        <div class="h5-section-header">
          <div>
            <h2 id="recommended-title" class="h5-section-title">优选养老机构</h2>
            <p class="h5-section-subtitle">综合服务与床位信息，为您安心筛选</p>
          </div>
          <button class="h5-section-action" type="button" @click="goToInstitutionList">
            查看更多 <van-icon name="arrow" />
          </button>
        </div>

        <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
          <van-list
            v-model:loading="loading"
            :finished="finished"
            :finished-text="institutionList.length ? '已展示全部优选机构' : ''"
            @load="onLoad"
          >
            <div class="institution-stack">
              <InstitutionCard
                v-for="item in institutionList"
                :key="item.institutionId"
                :institution="item"
                :navigate-on-click="false"
                @select="goToDetail"
              />
            </div>
            <template #loading>
              <div class="list-feedback">
                <van-loading size="20px">正在加载优选机构</van-loading>
              </div>
            </template>
          </van-list>
          <van-empty
            v-if="!loading && finished && institutionList.length === 0"
            :image="institutionPlaceholder"
            image-size="128"
            description="暂无优选机构，稍后再来看看"
          />
        </van-pull-refresh>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getRecommendInstitutions } from '@/api/institution'
import { getBannerList } from '@/api/banner'
import { getNoticeDetail } from '@/api/notice'
import { getImageUrl } from '@/utils/image'
import InstitutionCard from '@/components/InstitutionCard.vue'
import institutionPlaceholder from '@/assets/images/institution-placeholder.svg'

const router = useRouter()

// 当前轮播图索引
const currentBannerIndex = ref(0)

// 轮播图列表（从后端获取）
const bannerList = ref([])

// 通知文本
const noticeText = ref('')

// 金刚位（4个）
const iconList = ref([
  {
    name: '通知公告',
    icon: 'hotel-o',
    key: 'notice'
  },
  {
    name: '待办事项',
    icon: 'calendar-o',
    key: 'todo'
  },
  {
    name: '老人信息',
    icon: 'goods-collect-o',
    key: 'elder'
  },
  {
    name: '费用查询',
    icon: 'balance-list-o',
    key: 'fee'
  }
])

// 机构列表
const institutionList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 轮播图切换
const onBannerChange = (index) => {
  currentBannerIndex.value = index
}

const handleImageError = (event) => {
  if (event.target.dataset.fallbackApplied) return
  event.target.dataset.fallbackApplied = 'true'
  event.target.src = institutionPlaceholder
}

// 幻灯片点击处理
const handleBannerClick = (banner) => {
  if (!banner.linkValue) {
    return
  }
  if (banner.linkType === '1') {
    // 内部链接
    router.push(banner.linkValue)
  } else {
    // 外部链接，跳转到外部URL
    window.location.href = banner.linkValue
  }
}

// 去除HTML标签，提取纯文本
const stripHtmlTags = (html) => {
  if (!html) return ''
  // 创建临时div元素来解析HTML
  const tmp = document.createElement('div')
  tmp.innerHTML = html
  return tmp.textContent || tmp.innerText || ''
}

// 加载幻灯片
const loadBanners = async () => {
  try {
    const response = await getBannerList()
    if (response.code === 200 && response.data) {
      bannerList.value = response.data
    }
  } catch (error) {
    console.error('获取幻灯片失败:', error)
  }
}

// 加载通知
const loadNotice = async () => {
  try {
    const response = await getNoticeDetail(1)
    if (response.code === 200 && response.data) {
      // 去除HTML标签，只显示纯文本
      noticeText.value = stripHtmlTags(response.data.noticeContent)
    }
  } catch (error) {
    console.error('获取通知失败:', error)
  }
}

// 金刚位点击
const handleIconClick = (item) => {
  switch (item.key) {
    case 'notice':
      router.push('/notice/list')
      break
    case 'todo':
      router.push('/user/todo')
      break
    case 'elder':
      router.push('/user/elder')
      break
    case 'fee':
      router.push('/user/expense')
      break
    default:
      console.log('点击图标:', item.name)
  }
}

// 跳转搜索页
const goToSearch = () => {
  router.push({ name: 'Search' })
}

// 跳转机构列表
const goToInstitutionList = () => {
  router.push('/institution')
}

// 跳转机构详情
const goToDetail = (item) => {
  router.push({
    name: 'InstitutionDetail',
    params: { id: item.institutionId }
  })
}

// 跳转通知详情
const goToNoticeDetail = () => {
  router.push('/notice/detail/1')
}

// 转换机构数据
const transformInstitutionData = (institution) => {
  // 使用生活设施作为标签
  const tags = institution.lifeFacilities || []

  // 获取总费用范围（优先使用总费用）
  let minPrice = 0
  if (institution.priceRanges && institution.priceRanges.total) {
    minPrice = institution.priceRanges.total.min || 0
  } else if (institution.priceRanges && institution.priceRanges.bed) {
    minPrice = institution.priceRanges.bed.min || 0
  }

  return {
    institutionId: institution.institutionId,
    institutionName: institution.institutionName || '未命名机构',
    bedCount: institution.bedCount || 0,
    totalBeds: institution.totalBeds || institution.bedCount || 0,
    availableBeds: institution.availableBeds ?? null,
    institutionNature: institution.institutionNature,
    ratingLevel: institution.ratingLevel,
    address: institution.address || '地址未填写',
    coverImage: getImageUrl(institution.coverImage) || institutionPlaceholder,
    minPrice: minPrice,
    tags: tags.slice(0, 3) // 最多显示3个生活设施标签
  }
}

// 加载机构列表
const loadInstitutions = async () => {
  try {
    loading.value = true

    const response = await getRecommendInstitutions()

    if (response.code === 200 && response.data) {
      // 转换数据格式
      const transformedList = response.data.map(transformInstitutionData)

      if (refreshing.value) {
        institutionList.value = transformedList
        refreshing.value = false
      } else {
        institutionList.value = transformedList
      }

      finished.value = true
    } else {
      showToast(response.msg || '获取机构列表失败')
      finished.value = true
    }
  } catch (error) {
    console.error('获取机构数据失败:', error)
    showToast('加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  finished.value = false
  loadInstitutions()
}

// 上拉加载
const onLoad = () => {
  loadInstitutions()
}

onMounted(() => {
  loadBanners()
  loadNotice()
  loadInstitutions()
})
</script>
<style scoped>
.home-page {
  overflow-x: hidden;
}

.hero-section {
  position: relative;
  height: 232px;
  overflow: visible;
  background: var(--h5-color-primary-800);
}

.hero-swiper,
.hero-image {
  width: 100%;
  height: 100%;
}

.hero-image {
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    linear-gradient(180deg, rgba(16, 44, 78, 0.1) 8%, rgba(16, 44, 78, 0.75) 100%),
    linear-gradient(90deg, rgba(16, 44, 78, 0.54), transparent 75%);
}

.hero-copy {
  position: absolute;
  top: calc(var(--h5-safe-area-top) + var(--h5-space-6));
  left: var(--h5-page-padding);
  z-index: 2;
  max-width: 76%;
  color: var(--h5-color-text-inverse);
  text-shadow: 0 1px 8px rgba(15, 31, 50, 0.26);
}

.hero-eyebrow {
  display: inline-flex;
  min-height: 26px;
  align-items: center;
  padding: 2px var(--h5-space-2);
  margin-bottom: var(--h5-space-2);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  line-height: 22px;
  background: rgba(255, 255, 255, 0.18);
  border: 1px solid rgba(255, 255, 255, 0.34);
  border-radius: var(--h5-radius-pill);
  backdrop-filter: blur(8px);
}

.hero-copy h1 {
  font-size: var(--h5-font-size-display);
  font-weight: var(--h5-font-weight-bold);
  line-height: var(--h5-line-height-tight);
  letter-spacing: 0.02em;
}

.hero-copy p {
  margin-top: var(--h5-space-2);
  font-size: var(--h5-font-size-md);
  line-height: var(--h5-line-height-normal);
  opacity: 0.94;
}

.banner-indicators {
  position: absolute;
  right: var(--h5-page-padding);
  bottom: 54px;
  z-index: 3;
  display: flex;
  align-items: center;
  gap: 6px;
}

.indicator-dot {
  width: 6px;
  height: 6px;
  background: rgba(255, 255, 255, 0.56);
  border-radius: var(--h5-radius-pill);
  transition: width var(--h5-motion-base) var(--h5-ease-standard);
}

.indicator-dot.active {
  width: 20px;
  background: var(--h5-color-surface);
}

.hero-search {
  position: absolute;
  right: var(--h5-page-padding);
  bottom: -24px;
  left: var(--h5-page-padding);
  z-index: 4;
  display: flex;
  min-height: 50px;
  align-items: center;
  gap: var(--h5-space-2);
  padding: 0 var(--h5-space-4);
  color: var(--h5-color-text-tertiary);
  text-align: left;
  background: var(--h5-color-surface);
  border: 1px solid var(--h5-color-divider);
  border-radius: var(--h5-radius-lg);
  box-shadow: var(--h5-shadow-md);
}

.hero-search span {
  flex: 1;
  font-size: var(--h5-font-size-md);
}

.search-icon {
  color: var(--h5-color-primary);
  font-size: 20px;
}

.search-arrow {
  color: var(--h5-color-text-tertiary);
  font-size: 15px;
}

.home-content {
  padding: 42px var(--h5-page-padding) var(--h5-space-6);
}

.quick-services {
  padding: var(--h5-space-4);
}

.quick-services__header {
  margin-bottom: var(--h5-space-3);
}

.quick-services__header h2 {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
}

.quick-services__header p {
  margin-top: var(--h5-space-1);
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: var(--h5-space-2);
}

.icon-item {
  display: flex;
  min-width: 0;
  min-height: 82px;
  align-items: center;
  flex-direction: column;
  justify-content: center;
  gap: var(--h5-space-2);
  color: var(--h5-color-text);
  background: transparent;
  border: 0;
  border-radius: var(--h5-radius-md);
}

.icon-item:active {
  background: var(--h5-color-primary-soft);
}

.icon-wrapper {
  display: inline-flex;
  width: 48px;
  height: 48px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-primary);
  font-size: 24px;
  background: var(--h5-color-primary-soft);
  border: 1px solid var(--h5-color-primary-100);
  border-radius: var(--h5-radius-lg);
}

.icon-wrapper--todo {
  color: var(--h5-color-warning);
  background: var(--h5-color-warning-soft);
  border-color: var(--h5-color-warning-soft);
}

.icon-wrapper--elder {
  color: var(--h5-color-accent);
  background: var(--h5-color-accent-soft);
  border-color: var(--h5-color-accent-soft);
}

.icon-wrapper--fee {
  color: var(--h5-color-info);
  background: var(--h5-color-info-soft);
  border-color: var(--h5-color-info-soft);
}

.icon-text {
  overflow: hidden;
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-card {
  display: flex;
  width: 100%;
  min-height: 64px;
  align-items: center;
  gap: var(--h5-space-3);
  padding: var(--h5-space-3) var(--h5-space-4);
  margin-top: var(--h5-space-3);
  color: var(--h5-color-text);
  text-align: left;
}

.notice-icon-wrap {
  display: inline-flex;
  flex: 0 0 36px;
  height: 36px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-primary);
  font-size: 20px;
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-md);
}

.notice-copy {
  display: flex;
  flex: 1;
  min-width: 0;
  flex-direction: column;
  gap: 2px;
}

.notice-copy strong {
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-semibold);
}

.notice-copy > span {
  overflow: hidden;
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  line-height: 20px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-arrow {
  flex: 0 0 auto;
  color: var(--h5-color-text-tertiary);
}

.listings-section {
  margin-top: var(--h5-space-6);
}

.h5-section-action {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  background: transparent;
  border: 0;
}

.institution-stack {
  display: flex;
  flex-direction: column;
  gap: var(--h5-space-3);
}

.list-feedback {
  display: flex;
  min-height: 72px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

:deep(.van-pull-refresh) {
  overflow: visible;
}

:deep(.van-list__finished-text) {
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

:deep(.van-empty) {
  padding: var(--h5-space-8) 0;
}

:deep(.van-empty__description) {
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
}

@media (max-width: 360px) {
  .hero-copy {
    max-width: 84%;
  }

  .hero-copy h1 {
    font-size: var(--h5-font-size-3xl);
  }

  .home-content {
    padding-right: var(--h5-space-3);
    padding-left: var(--h5-space-3);
  }

  .quick-services {
    padding-right: var(--h5-space-3);
    padding-left: var(--h5-space-3);
  }

  .icon-grid {
    gap: var(--h5-space-1);
  }
}
</style>
