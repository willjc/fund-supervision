<template>
  <div class="home-page">
    <!-- 轮播图区域 -->
    <div class="banner-section">
      <van-swipe
        class="banner-swiper"
        :autoplay="3000"
        :show-indicators="false"
        @change="onBannerChange"
      >
        <van-swipe-item v-for="(banner, index) in bannerList" :key="index">
          <img class="banner-image" :src="banner.image" mode="widthFix" />
        </van-swipe-item>
      </van-swipe>

      <!-- 自定义指示点 -->
      <div class="banner-indicators">
        <div
          class="indicator-dot"
          v-for="(item, index) in bannerList"
          :key="index"
          :class="{ active: currentBannerIndex === index }"
        ></div>
      </div>

      <!-- 悬浮搜索栏 -->
      <div class="search-section" @click="goToSearch">
        <div class="search-bar">
          <van-icon name="search" class="search-icon" />
          <span class="search-placeholder">搜索你想要的机构</span>
        </div>
      </div>
    </div>

    <!-- 金刚位（4个） -->
    <div class="icon-grid">
      <div
        class="icon-item"
        v-for="(item, index) in iconList"
        :key="index"
        @click="handleIconClick(item)"
      >
        <div class="icon-wrapper" :style="{ backgroundColor: item.color }">
          <van-icon :name="item.icon" size="24" color="#fff" />
        </div>
        <span class="icon-text">{{ item.name }}</span>
      </div>
    </div>

    <!-- 通知栏 -->
    <div class="notice-wrapper">
      <div class="notice-section"></div>
      <div class="notice-content">
        <van-icon name="volume-o" class="notice-icon" />
        <div class="notice-text-scroll">
          <span class="notice-label">最新通知：</span>
          <span class="notice-text">欢迎使用养老机构监管平台，为您提供优质养老服务</span>
        </div>
      </div>
    </div>

    <!-- 优选机构列表 -->
    <div class="listings-section">
      <!-- 标题和更多按钮 -->
      <div class="section-header">
        <div class="section-title">优选机构</div>
        <div class="section-more" @click="goToInstitutionList">
          <span class="more-text">更多</span>
          <van-icon name="arrow" class="more-arrow" />
        </div>
      </div>

      <!-- 机构卡片列表 -->
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div
            class="listing-card"
            v-for="item in institutionList"
            :key="item.institutionId"
            @click="goToDetail(item)"
          >
            <img
              class="listing-image"
              :src="item.coverImage || defaultImage"
              mode="aspectFill"
            />
            <div class="listing-info">
              <div class="listing-header">
                <span class="listing-title">{{ item.institutionName }}</span>
                <span class="listing-distance">附近</span>
              </div>
              <div class="listing-status">
                <span
                  class="status-text"
                  :class="{ available: item.bedCount > 0 }"
                >
                  {{ item.bedCount > 0 ? '有床位' : '暂无床位' }}
                </span>
                <span class="status-divider">|</span>
                <span class="status-count">共{{ item.bedCount }}床</span>
              </div>
              <span class="listing-address">{{ item.address }}</span>
              <div class="listing-tags">
                <span class="tag" v-for="(tag, tagIndex) in item.tags" :key="tagIndex">
                  {{ tag }}
                </span>
              </div>
              <div class="listing-price">
                <span class="price-number">{{ item.minPrice }}</span>
                <span class="price-unit">元</span>
                <span class="price-suffix">/月起</span>
              </div>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getRecommendInstitutions } from '@/api/institution'

const router = useRouter()

// 当前轮播图索引
const currentBannerIndex = ref(0)

// 轮播图列表（静态图片）
const bannerList = ref([
  {
    image: 'https://picsum.photos/750/375?random=1'
  },
  {
    image: 'https://picsum.photos/750/375?random=2'
  },
  {
    image: 'https://picsum.photos/750/375?random=3'
  }
])

// 金刚位（4个）
const iconList = ref([
  {
    name: '通知公告',
    icon: 'hotel-o',
    key: 'notice',
    color: '#5B8FF9'
  },
  {
    name: '待办事项',
    icon: 'calendar-o',
    key: 'todo',
    color: '#FFC107'
  },
  {
    name: '老人信息',
    icon: 'goods-collect-o',
    key: 'elder',
    color: '#FF6B6B'
  },
  {
    name: '费用查询',
    icon: 'balance-list-o',
    key: 'fee',
    color: '#00BCD4'
  }
])

// 机构列表
const institutionList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

const defaultImage = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

// 轮播图切换
const onBannerChange = (index) => {
  currentBannerIndex.value = index
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
      router.push('/fee/query')
      break
    default:
      console.log('点击图标:', item.name)
  }
}

// 跳转搜索页
const goToSearch = () => {
  router.push('/search')
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

// 转换机构数据
const transformInstitutionData = (institution) => {
  // 使用生活设施作为标签
  const tags = institution.lifeFacilities || []

  // 获取床位费范围（如果没有则使用总费用）
  let minPrice = 0
  if (institution.priceRanges && institution.priceRanges.bed) {
    minPrice = institution.priceRanges.bed.min || 0
  } else if (institution.priceRanges && institution.priceRanges.total) {
    minPrice = institution.priceRanges.total.min || 0
  }

  return {
    institutionId: institution.institutionId,
    institutionName: institution.institutionName || '未命名机构',
    bedCount: institution.bedCount || institution.availableBeds || 0,
    address: institution.address || '地址未填写',
    coverImage: institution.coverImage || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
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
  loadInstitutions()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: #f5f6fc;
  padding-bottom: 20px;
}

/* 轮播图区域 */
.banner-section {
  position: relative;
  width: 100%;
  margin-bottom: 10px;
}

.banner-swiper {
  width: 100%;
  height: 125px; /* 250rpx ≈ 125px */
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 自定义指示点 */
.banner-indicators {
  position: absolute;
  bottom: 24px;
  right: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  z-index: 100;
}

.indicator-dot {
  width: 5px;
  height: 5px;
  opacity: 1;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
}

.indicator-dot.active {
  background: #ffffff;
}

/* 悬浮搜索栏 */
.search-section {
  position: absolute;
  bottom: -20px;
  left: 0;
  right: 0;
  padding: 0 12px;
  z-index: 10;
}

.search-bar {
  width: 100%;
  max-width: 351px;
  height: 40px;
  border-radius: 10px;
  opacity: 1;
  background: #ffffff;
  display: flex;
  align-items: center;
  padding: 0 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin: 0 auto;
  cursor: pointer;
}

.search-icon {
  width: 16px;
  height: 16px;
  margin-right: 10px;
  color: #999;
}

.search-placeholder {
  font-size: 14px;
  color: #999;
}

/* 金刚位（4个，1行4列） */
.icon-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  padding: 15px 12px;
  background: #ffffff;
  margin: 32px 12px 10px;
  border-radius: 10px;
  opacity: 1;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.icon-wrapper {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 5px;
}

.icon-text {
  font-size: 12px;
  color: #333333;
  text-align: center;
  line-height: 20px;
}

/* 通知栏 */
.notice-wrapper {
  width: 351px;
  height: 45px;
  border-radius: 10px;
  opacity: 1;
  background: #ffffff;
  margin: 12px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.notice-section {
  width: 216px;
  height: 42px;
  margin-left: 2px;
  border-radius: 9px;
  opacity: 1;
  background: linear-gradient(90deg, #ebf6ff 0%, rgba(235, 246, 255, 0) 100%);
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
}

.notice-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 18px;
  position: relative;
  z-index: 2;
}

.notice-icon {
  font-size: 22px;
  color: #1281ff;
  margin-right: 8px;
  flex-shrink: 0;
}

.notice-text-scroll {
  flex: 1;
  white-space: nowrap;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.notice-label {
  font-size: 13px;
  font-weight: 500;
  color: #1281ff;
  font-family: 'PingFang SC', '苹方-简', sans-serif;
  flex-shrink: 0;
}

.notice-text {
  font-size: 13px;
  color: #333333;
  font-family: 'PingFang SC', '苹方-简', sans-serif;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 机构列表区域 */
.listings-section {
  padding: 8px 12px 15px;
}

/* 标题和更多按钮容器 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  position: relative;
}

.section-title {
  font-size: 18px;
  font-weight: 500;
  color: #1a1a1a;
  position: relative;
  padding-left: 8px;
  font-family: 'PingFang SC', '苹方-简', sans-serif;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 16px;
  background: linear-gradient(180deg, #0f73ff 0%, #4fc7ff 100%);
  border-radius: 2px;
}

.section-more {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 0;
}

.section-more .more-text {
  font-size: 14px;
  color: #999;
  margin-right: 2px;
}

.section-more .more-arrow {
  font-size: 12px;
  color: #999;
}

/* 机构卡片 */
.listing-card {
  display: flex;
  background-color: #fff;
  padding: 8px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 15px;
  cursor: pointer;
}

.listing-image {
  width: 96px;
  height: 119px;
  border-radius: 8px;
  margin-right: 12px;
  flex-shrink: 0;
  object-fit: cover;
}

.listing-info {
  flex: 1;
  padding: 4px;
  display: flex;
  flex-direction: column;
}

.listing-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2px;
}

.listing-title {
  font-size: 16px;
  font-weight: 500;
  color: #1a1a1a;
  line-height: 20px;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.listing-distance {
  font-size: 12px;
  color: #999999;
  line-height: 20px;
  margin-left: 75px;
}

.listing-status {
  display: flex;
  align-items: center;
  margin-bottom: 3px;
}

.status-text {
  font-size: 12px;
  color: #999;
  font-family: 'PingFang SC', '苹方-简', sans-serif;
}

.status-text.available {
  color: #207fff;
  font-weight: 500;
}

.status-divider {
  color: #cfcfcf;
  font-size: 12px;
  margin: 0 5px;
}

.status-count {
  font-size: 12px;
  color: #999999;
  font-family: 'PingFang SC', '苹方-简', sans-serif;
}

.listing-address {
  font-size: 12px;
  color: #333333;
  line-height: 20px;
  margin-bottom: 3px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.listing-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-bottom: 6px;
}

.tag {
  font-size: 10px;
  color: #4c617d;
  line-height: 16px;
  background-color: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
}

.listing-price {
  display: flex;
  align-items: baseline;
  margin-top: auto;
}

.price-number {
  font-size: 15px;
  color: #e5252b;
  font-weight: 700;
  line-height: 20px;
  font-family: 'HarmonyOSSansSC', sans-serif;
}

.price-unit {
  font-size: 14px;
  color: #e5252b;
  font-weight: normal;
  line-height: 20px;
  font-family: 'PingFang SC', '苹方-简', sans-serif;
  margin-left: 2px;
}

.price-suffix {
  font-size: 14px;
  color: #000000;
  font-weight: normal;
  line-height: 20px;
  font-family: 'PingFang SC', '苹方-简', sans-serif;
  margin-left: 2px;
}
</style>
