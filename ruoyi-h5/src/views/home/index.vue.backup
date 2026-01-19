<template>
  <div class="home-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <van-search
        v-model="searchValue"
        shape="round"
        placeholder="请搜索附近的养老院名称"
        @search="onSearch"
      >
        <template #action>
          <div class="notice-icon" @click="goToNotice">
            <van-badge :content="unreadCount" :show-zero="false">
              <van-icon name="bell-o" size="22" />
            </van-badge>
          </div>
        </template>
      </van-search>
    </div>

    <!-- 轮播图 -->
    <div class="banner">
      <van-swipe
        class="swipe"
        :autoplay="3000"
        :show-indicators="true"
        indicator-color="rgba(255, 255, 255, 0.9)"
      >
        <van-swipe-item v-for="(banner, index) in banners" :key="index">
          <div class="banner-item" @click="onBannerClick(banner)">
            <img
              :src="banner.imageUrl"
              alt="banner"
              class="banner-image"
            />
          </div>
        </van-swipe-item>
      </van-swipe>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-entry">
      <van-grid :column-num="4" :border="false" :clickable="true">
        <van-grid-item
          v-for="item in quickEntries"
          :key="item.name"
          :text="item.text"
          @click="onQuickEntryClick(item)"
        >
          <template #icon>
            <div class="grid-icon" :style="{ backgroundColor: item.color }">
              <van-icon :name="item.icon" size="20" color="#fff" />
            </div>
          </template>
        </van-grid-item>
      </van-grid>
    </div>

    <!-- 优选机构 -->
    <div class="recommend-section">
      <div class="section-header">
        <span class="section-title">优选机构</span>
        <span class="section-more" @click="goToInstitutionList">
          查看更多>
        </span>
      </div>

      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <InstitutionCard
            v-for="institution in institutionList"
            :key="institution.institutionId"
            :institution="institution"
          />
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import InstitutionCard from '@/components/InstitutionCard.vue'
import { getRecommendInstitutions } from '@/api/institution'
import { getBannerList } from '@/api/banner'

const router = useRouter()

// 搜索值
const searchValue = ref('')

// 未读通知数量
const unreadCount = ref(3) // 暂时写死,后续对接API

// 轮播图数据
const banners = ref([])

// 加载轮播图
const loadBanners = async () => {
  try {
    const response = await getBannerList({ position: 1 }) // 1-首页
    if (response.code === 200 && response.data) {
      banners.value = response.data
    }
  } catch (error) {
    console.error('加载轮播图失败:', error)
    // 使用默认数据作为备用
    banners.value = [
      {
        id: 1,
        imageUrl: '/images/banners/banner1.jpg',
        title: '',
        linkType: '1',
        linkValue: '1'
      }
    ]
  }
}

// 快捷入口
const quickEntries = ref([
  { name: 'notice', icon: 'envelop-o', text: '通知公告', path: '/notice/list', color: '#5B8FF9' },
  { name: 'todo', icon: 'records', text: '待办事项', path: '/user/todo', color: '#FFC107' },
  { name: 'elder', icon: 'contact', text: '老人信息', path: '/user/elder', color: '#FF6B6B' },
  { name: 'fee', icon: 'balance-list-o', text: '费用查询', path: '/fee/query', color: '#00BCD4' }
])

// 模拟机构数据
const mockInstitutions = [
  {
    institutionId: 1,
    institutionName: '郑州市金水区花园口社区养老服务中心',
    address: '郑州市金水区花园口社区建设路12号',
    contactPhone: '0371-65432100',
    bedCount: 100,
    institutionType: 'service_center',
    priceRanges: {
      nursing: { min: 1500, max: 3500 },
      meal: { min: 500, max: 900 },
      bed: { min: 400, max: 1500 },
      diet: { min: 200, max: 800 }
    }
  },
  {
    institutionId: 2,
    institutionName: '郑州颐养家园养老院',
    address: '郑州市中原区桐柏路建设路交叉口东200米',
    contactPhone: '0371-66778899',
    bedCount: 150,
    institutionType: 'nursing_home',
    priceRanges: {
      nursing: { min: 2000, max: 4000 },
      meal: { min: 600, max: 1200 },
      bed: { min: 500, max: 2000 },
      diet: { min: 300, max: 1000 }
    }
  },
  {
    institutionId: 3,
    institutionName: '河南省老干部康养中心',
    address: '郑州市管城区商城路与未来路交叉口',
    contactPhone: '0371-88990011',
    bedCount: 200,
    institutionType: 'nursing_home',
    priceRanges: {
      nursing: { min: 2500, max: 5000 },
      meal: { min: 800, max: 1500 },
      bed: { min: 800, max: 2500 },
      diet: { min: 400, max: 1200 }
    }
  },
  {
    institutionId: 4,
    institutionName: '郑州夕阳红托老所',
    address: '郑州市二七区大学路航海路向南500米',
    contactPhone: '0371-55667788',
    bedCount: 80,
    institutionType: 'service_center',
    priceRanges: {
      nursing: { min: 1200, max: 2800 },
      meal: { min: 400, max: 800 },
      bed: { min: 300, max: 1200 },
      diet: { min: 150, max: 600 }
    }
  },
  {
    institutionId: 5,
    institutionName: '郑东新区康乐养老中心',
    address: '郑州市郑东新区龙子湖北路与平安大道交叉口',
    contactPhone: '0371-77889900',
    bedCount: 120,
    institutionType: 'nursing_home',
    priceRanges: {
      nursing: { min: 1800, max: 3800 },
      meal: { min: 550, max: 1000 },
      bed: { min: 450, max: 1800 },
      diet: { min: 250, max: 900 }
    }
  },
  {
    institutionId: 6,
    institutionName: '郑州爱心护理院',
    address: '郑州市惠济区江山路与开元路交叉口',
    contactPhone: '0371-99001122',
    bedCount: 90,
    institutionType: 'nursing_home',
    priceRanges: {
      nursing: { min: 1600, max: 3200 },
      meal: { min: 500, max: 850 },
      bed: { min: 380, max: 1400 },
      diet: { min: 200, max: 750 }
    }
  },
  {
    institutionId: 7,
    institutionName: '高新区幸福之家养老服务站',
    address: '郑州市高新区科学大道与瑞达路交叉口',
    contactPhone: '0371-11223344',
    bedCount: 60,
    institutionType: 'service_center',
    priceRanges: {
      nursing: { min: 1400, max: 3000 },
      meal: { min: 450, max: 750 },
      bed: { min: 350, max: 1300 },
      diet: { min: 180, max: 650 }
    }
  },
  {
    institutionId: 8,
    institutionName: '郑州安康老年公寓',
    address: '郑州市经开区航海东路与第八大街交叉口',
    contactPhone: '0371-22334455',
    bedCount: 110,
    institutionType: 'nursing_home',
    priceRanges: {
      nursing: { min: 1700, max: 3600 },
      meal: { min: 520, max: 950 },
      bed: { min: 420, max: 1600 },
      diet: { min: 220, max: 850 }
    }
  }
]

// 机构列表
const institutionList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)

// 搜索
const onSearch = () => {
  if (!searchValue.value) {
    showToast('请输入搜索关键词')
    return
  }
  router.push({
    path: '/institution',
    query: { keyword: searchValue.value }
  })
}

// 跳转通知页面
const goToNotice = () => {
  router.push('/notice/list')
}

// 轮播图点击
const onBannerClick = (banner) => {
  // linkType: 1-机构详情 2-外部URL
  if (banner.linkType === '1') {
    router.push(`/institution/detail/${banner.linkValue}`)
  } else if (banner.linkType === '2') {
    window.location.href = banner.linkValue
  }
}

// 快捷入口点击
const onQuickEntryClick = (item) => {
  if (item.path) {
    router.push(item.path)
  } else if (item.action === 'callService') {
    showToast('客服功能开发中')
  }
}

// 跳转机构列表
const goToInstitutionList = () => {
  router.push('/institution')
}

// 加载机构列表 (使用真实API)
const loadInstitutions = async () => {
  try {
    loading.value = true

    const response = await getRecommendInstitutions()

    if (response.code === 200 && response.data) {
      // 后端已经返回H5期望格式的数据，直接使用
      if (refreshing.value) {
        institutionList.value = response.data
        refreshing.value = false
      } else {
        institutionList.value = response.data
      }

      // 标记加载完成
      finished.value = true
    } else {
      showToast(response.msg || '获取机构列表失败')
      // API失败时保留mock数据作为备用方案
      if (refreshing.value) {
        institutionList.value = mockInstitutions.slice(0, pageSize.value)
        refreshing.value = false
      } else {
        institutionList.value = mockInstitutions
      }
      finished.value = true
    }
  } catch (error) {
    console.error('获取机构数据失败:', error)
    showToast('加载失败，显示示例数据')
    // 网络错误时使用mock数据
    if (refreshing.value) {
      institutionList.value = mockInstitutions.slice(0, pageSize.value)
      refreshing.value = false
    } else {
      institutionList.value = mockInstitutions
    }
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  pageNum.value = 1
  finished.value = false
  loadInstitutions()
}

// 上拉加载
const onLoad = () => {
  loadInstitutions()
}

onMounted(() => {
  // 页面加载时自动加载轮播图和机构列表
  loadBanners()
  loadInstitutions()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60px;
}

.search-bar {
  background: #fff;
  padding: 8px 0;
}

.search-bar :deep(.van-search__action) {
  padding: 0 12px;
  cursor: pointer;
}

.notice-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.banner {
  background: #fff;
}

.swipe {
  height: 180px;
}

.swipe :deep(.van-swipe__track) {
  height: 100%;
}

.swipe :deep(.van-swipe__indicators) {
  bottom: 15px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
}

.swipe :deep(.van-swipe__indicator) {
  width: 8px;
  height: 8px;
  background-color: rgba(0, 0, 0, 0.4);
  border: 1.5px solid rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  margin: 0 4px;
  transition: all 0.3s;
  opacity: 0.9;
}

.swipe :deep(.van-swipe__indicator--active) {
  width: 20px;
  border-radius: 4px;
  background-color: #fff;
  border-color: #fff;
  opacity: 1;
}

.banner-item {
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: #f5f5f5;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.quick-entry {
  background: #fff;
  margin-bottom: 10px;
  padding: 10px 0;
}

.grid-icon {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 6px;
}

.recommend-section {
  background: #fff;
  padding: 12px 8px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 0 4px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #1989fa;
  position: relative;
  padding-left: 8px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 16px;
  background: #1989fa;
}

.section-more {
  font-size: 13px;
  color: #999;
  display: flex;
  align-items: center;
}
</style>
