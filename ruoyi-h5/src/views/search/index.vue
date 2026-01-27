<template>
  <div class="search-page">
    <!-- 搜索头部 -->
    <div class="search-header">
      <van-icon name="arrow-left" class="back-icon" @click="goBack" />
      <van-search
        v-model="searchValue"
        placeholder="搜索你想要的机构"
        shape="round"
        @search="onSearch"
        autofocus
      />
    </div>

    <!-- 搜索历史 -->
    <div class="search-history" v-if="searchHistory.length > 0 && !hasSearched">
      <div class="history-header">
        <span class="history-title">搜索历史</span>
        <van-icon name="delete-o" class="delete-icon" @click="clearHistory" />
      </div>
      <div class="history-tags">
        <van-tag
          v-for="(item, index) in searchHistory"
          :key="index"
          plain
          type="primary"
          size="medium"
          @click="searchByHistory(item)"
        >
          {{ item }}
        </van-tag>
      </div>
    </div>

    <!-- 热门搜索 -->
    <div class="hot-search" v-if="!hasSearched">
      <div class="hot-header">
        <span class="hot-title">热门搜索</span>
      </div>
      <div class="hot-tags">
        <van-tag
          v-for="(item, index) in hotSearchList"
          :key="index"
          plain
          :type="index < 3 ? 'danger' : 'default'"
          size="medium"
          @click="searchByHot(item)"
        >
          {{ item }}
        </van-tag>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="search-results" v-if="hasSearched">
      <div class="result-count" v-if="searchResults.length > 0">
        找到 {{ searchResults.length }} 个相关机构
      </div>
      <div class="no-result" v-else>
        <van-empty description="未找到相关机构" />
      </div>

      <!-- 机构列表 -->
      <div
        class="listing-card"
        v-for="item in searchResults"
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
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getRecommendInstitutions } from '@/api/institution'

const router = useRouter()

// 搜索值
const searchValue = ref('')

// 是否已搜索
const hasSearched = ref(false)

// 搜索结果
const searchResults = ref([])

// 搜索历史
const searchHistory = ref(['养老院', '护理中心', '康养'])

// 热门搜索
const hotSearchList = ref([
  '养老院',
  '护理中心',
  '康养',
  '日间照料',
  '老年公寓',
  '康复中心'
])

const defaultImage = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

// 返回
const goBack = () => {
  router.back()
}

// 搜索
const onSearch = async () => {
  if (!searchValue.value.trim()) {
    showToast('请输入搜索关键词')
    return
  }

  // 添加到搜索历史
  addToHistory(searchValue.value)

  try {
    const response = await getRecommendInstitutions()

    if (response.code === 200 && response.data) {
      // 过滤匹配的机构
      const keyword = searchValue.value.toLowerCase()
      searchResults.value = response.data
        .filter(item => {
          const name = (item.institutionName || '').toLowerCase()
          const address = (item.actualAddress || item.registeredAddress || '').toLowerCase()
          return name.includes(keyword) || address.includes(keyword)
        })
        .map(transformInstitutionData)

      hasSearched.value = true
    } else {
      showToast(response.msg || '搜索失败')
    }
  } catch (error) {
    console.error('搜索失败:', error)
    showToast('搜索失败')
  }
}

// 添加到搜索历史
const addToHistory = (keyword) => {
  // 删除已存在的相同关键词
  const index = searchHistory.value.indexOf(keyword)
  if (index > -1) {
    searchHistory.value.splice(index, 1)
  }
  // 添加到开头
  searchHistory.value.unshift(keyword)
  // 最多保留10条
  if (searchHistory.value.length > 10) {
    searchHistory.value = searchHistory.value.slice(0, 10)
  }
  // 保存到本地存储
  localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value))
}

// 清除搜索历史
const clearHistory = () => {
  searchHistory.value = []
  localStorage.removeItem('searchHistory')
  showToast('已清除搜索历史')
}

// 通过历史搜索
const searchByHistory = (keyword) => {
  searchValue.value = keyword
  onSearch()
}

// 通过热门搜索
const searchByHot = (keyword) => {
  searchValue.value = keyword
  onSearch()
}

// 转换机构数据
const transformInstitutionData = (institution) => {
  // 获取评级文本
  const getRatingText = (level) => {
    const ratingMap = {
      1: '一星级',
      2: '二星级',
      3: '三星级',
      4: '四星级',
      5: '五星级'
    }
    return ratingMap[level] || '未评级'
  }

  // 获取机构类型文本
  const getTypeText = (type) => {
    const typeMap = {
      '1': '养老院',
      '2': '护理院',
      '3': '养老服务中心'
    }
    return typeMap[type] || '养老机构'
  }

  // 生成标签
  const tags = []
  if (institution.ratingLevel) {
    tags.push(getRatingText(institution.ratingLevel))
  }
  if (institution.institutionType) {
    tags.push(getTypeText(institution.institutionType))
  }

  return {
    institutionId: institution.institutionId,
    institutionName: institution.institutionName || '未命名机构',
    bedCount: institution.bedCount || 0,
    address: institution.actualAddress || institution.registeredAddress || '地址未填写',
    coverImage: institution.coverImages,
    minPrice: institution.priceRangeMin || 0,
    tags: tags.slice(0, 3) // 最多显示3个标签
  }
}

// 跳转机构详情
const goToDetail = (item) => {
  router.push({
    name: 'InstitutionDetail',
    params: { id: item.institutionId }
  })
}

// 加载搜索历史
const loadSearchHistory = () => {
  const history = localStorage.getItem('searchHistory')
  if (history) {
    try {
      searchHistory.value = JSON.parse(history)
    } catch (e) {
      console.error('解析搜索历史失败:', e)
    }
  }
}

// 初始化
loadSearchHistory()
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background-color: #f5f6fc;
  padding-bottom: 20px;
}

/* 搜索头部 */
.search-header {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-icon {
  font-size: 20px;
  margin-right: 10px;
  cursor: pointer;
  color: #333;
}

.search-header :deep(.van-search) {
  flex: 1;
  padding: 0;
}

/* 搜索历史 */
.search-history {
  background: #fff;
  margin: 10px 12px;
  padding: 15px;
  border-radius: 8px;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.history-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.delete-icon {
  font-size: 18px;
  color: #999;
  cursor: pointer;
}

.history-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 热门搜索 */
.hot-search {
  background: #fff;
  margin: 10px 12px;
  padding: 15px;
  border-radius: 8px;
}

.hot-header {
  margin-bottom: 12px;
}

.hot-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 搜索结果 */
.search-results {
  padding: 0 12px;
}

.result-count {
  padding: 10px 0;
  font-size: 14px;
  color: #666;
  text-align: center;
}

.no-result {
  padding: 40px 0;
}

/* 机构卡片样式（复用首页样式） */
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
