<template>
  <div class="search-page h5-page h5-page--constrained">
    <header class="search-header">
      <button class="back-button" type="button" aria-label="返回" @click="goBack">
        <van-icon name="arrow-left" />
      </button>
      <van-search
        v-model="searchValue"
        placeholder="搜索机构名称、地址"
        shape="round"
        autofocus
        clearable
        @search="onSearch"
      />
      <button class="search-submit" type="button" @click="onSearch">搜索</button>
    </header>

    <main class="search-content">
      <section v-if="searching" class="search-loading search-loading--standalone h5-card" aria-live="polite">
        <van-loading size="22px">正在查找机构</van-loading>
      </section>

      <section v-if="!searching && searchHistory.length > 0 && !hasSearched" class="search-block h5-card">
        <div class="search-block__header">
          <div>
            <h2>搜索历史</h2>
            <p>快速继续最近的查找</p>
          </div>
          <button class="clear-button" type="button" @click="clearHistory">
            <van-icon name="delete-o" /> 清空
          </button>
        </div>
        <div class="chip-list">
          <button
            v-for="item in searchHistory"
            :key="item"
            class="search-chip"
            type="button"
            @click="searchByHistory(item)"
          >
            <van-icon name="clock-o" />
            {{ item }}
          </button>
        </div>
      </section>

      <section v-if="!searching && !hasSearched" class="search-block h5-card">
        <div class="search-block__header">
          <div>
            <h2>热门搜索</h2>
            <p>大家都在关注的养老服务</p>
          </div>
          <span class="hot-mark"><van-icon name="fire-o" /> 热门</span>
        </div>
        <div class="chip-list">
          <button
            v-for="(item, index) in hotSearchList"
            :key="item"
            class="search-chip"
            :class="{ 'search-chip--hot': index < 3 }"
            type="button"
            @click="searchByHot(item)"
          >
            <span class="chip-index">{{ index + 1 }}</span>
            {{ item }}
          </button>
        </div>
      </section>

      <section v-if="!searching && hasSearched" class="search-results" aria-live="polite">
        <div v-if="searchResults.length" class="result-header">
          <div>
            <h2>搜索结果</h2>
            <p>“{{ searchValue }}”相关机构</p>
          </div>
          <span class="result-count">共 {{ searchResults.length }} 家</span>
        </div>

        <div v-if="searchResults.length" class="institution-stack">
          <InstitutionCard
            v-for="item in searchResults"
            :key="item.institutionId"
            :institution="item"
            :navigate-on-click="false"
            @select="goToDetail"
          />
        </div>
        <div v-else class="no-result h5-card">
          <van-empty
            :image="institutionPlaceholder"
            image-size="132"
            description="暂未找到相关机构"
          >
            <p class="empty-tip">换个机构名称、区域或服务关键词试试</p>
          </van-empty>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getInstitutionList } from '@/api/institution'
import { getImageUrl } from '@/utils/image'
import InstitutionCard from '@/components/InstitutionCard.vue'
import institutionPlaceholder from '@/assets/images/institution-placeholder.svg'

const router = useRouter()

// 搜索值
const searchValue = ref('')

// 是否已搜索
const hasSearched = ref(false)

// 搜索结果
const searchResults = ref([])
const searching = ref(false)
let searchRequestSequence = 0

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

// 返回
const goBack = () => {
  router.back()
}

// 搜索
const onSearch = async () => {
  const keywordSnapshot = searchValue.value.trim()
  if (!keywordSnapshot) {
    showToast('请输入搜索关键词')
    return
  }

  // 添加到搜索历史
  addToHistory(keywordSnapshot)

  const requestId = ++searchRequestSequence
  searching.value = true

  try {
    const response = await getInstitutionList({
      pageNum: 1,
      pageSize: 10000
    })

    if (requestId !== searchRequestSequence) return

    if (response.code === 200 && Array.isArray(response.rows)) {
      // 过滤匹配的机构
      const keyword = keywordSnapshot.toLowerCase()
      searchResults.value = response.rows
        .filter(item => {
          const name = (item.institutionName || '').toLowerCase()
          const addresses = [item.address, item.actualAddress, item.registeredAddress]
            .filter(Boolean)
            .map(address => String(address).toLowerCase())
          return name.includes(keyword) || addresses.some(address => address.includes(keyword))
        })
        .map(transformInstitutionData)
      hasSearched.value = true
    } else {
      hasSearched.value = true
      searchResults.value = []
      showToast(response.msg || '搜索失败')
    }
  } catch (error) {
    if (requestId !== searchRequestSequence) return

    hasSearched.value = true
    searchResults.value = []
    console.error('搜索失败:', error)
    showToast('搜索失败')
  } finally {
    if (requestId === searchRequestSequence) {
      searching.value = false
    }
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

  const coverImages = institution.coverImage || institution.coverImages
  const coverImage = Array.isArray(coverImages) ? coverImages[0] : coverImages

  return {
    institutionId: institution.institutionId,
    institutionName: institution.institutionName || '未命名机构',
    bedCount: institution.bedCount || 0,
    totalBeds: institution.totalBeds || institution.bedCount || 0,
    availableBeds: institution.availableBeds ?? null,
    institutionNature: institution.institutionNature,
    ratingLevel: institution.ratingLevel,
    address: institution.address || institution.actualAddress || institution.registeredAddress || '地址未填写',
    coverImage: getImageUrl(coverImage) || institutionPlaceholder,
    minPrice: institution.priceRanges?.total?.min || institution.priceRangeMin || 0,
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

watch(searchValue, () => {
  if (!hasSearched.value) return
  hasSearched.value = false
  searchResults.value = []
})

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
  overflow-x: hidden;
}

.search-header {
  position: sticky;
  top: 0;
  z-index: var(--h5-z-sticky);
  display: flex;
  min-height: calc(64px + var(--h5-safe-area-top));
  align-items: center;
  gap: var(--h5-space-2);
  padding:
    calc(var(--h5-space-2) + var(--h5-safe-area-top))
    var(--h5-page-padding)
    var(--h5-space-2);
  background: var(--h5-color-surface);
  border-bottom: 1px solid var(--h5-color-divider);
  box-shadow: var(--h5-shadow-xs);
}

.back-button,
.search-submit,
.clear-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-text-secondary);
  background: transparent;
  border: 0;
}

.back-button {
  flex: 0 0 40px;
  width: 40px;
  height: 40px;
  font-size: 22px;
  border-radius: var(--h5-radius-md);
}

.back-button:active,
.clear-button:active {
  color: var(--h5-color-primary);
  background: var(--h5-color-primary-soft);
}

.search-header :deep(.van-search) {
  flex: 1;
  min-width: 0;
  padding: 0;
  background: transparent;
}

.search-header :deep(.van-search__content) {
  min-height: 42px;
  padding-left: var(--h5-space-3);
  background: var(--h5-color-surface-subtle);
  border: 1px solid var(--h5-color-border);
  border-radius: var(--h5-radius-md);
}

.search-header :deep(.van-field__control) {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-md);
}

.search-submit {
  flex: 0 0 auto;
  min-width: 44px;
  min-height: 40px;
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
  border-radius: var(--h5-radius-sm);
}

.search-submit:active {
  background: var(--h5-color-primary-soft);
}

.search-content {
  padding: var(--h5-space-4) var(--h5-page-padding) var(--h5-space-8);
}

.search-block {
  padding: var(--h5-space-4);
}

.search-block + .search-block {
  margin-top: var(--h5-space-3);
}

.search-block__header,
.result-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-3);
  margin-bottom: var(--h5-space-4);
}

.search-block__header h2,
.result-header h2 {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
  line-height: var(--h5-line-height-tight);
}

.search-block__header p,
.result-header p {
  margin-top: var(--h5-space-1);
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.clear-button {
  gap: var(--h5-space-1);
  min-height: 36px;
  padding: 0 var(--h5-space-2);
  font-size: var(--h5-font-size-sm);
  border-radius: var(--h5-radius-sm);
}

.hot-mark,
.result-count {
  display: inline-flex;
  flex: 0 0 auto;
  min-height: 28px;
  align-items: center;
  gap: var(--h5-space-1);
  padding: 2px var(--h5-space-2);
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  line-height: 24px;
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-pill);
}

.chip-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--h5-space-2);
}

.search-chip {
  display: inline-flex;
  min-height: 40px;
  align-items: center;
  gap: 6px;
  padding: var(--h5-space-2) var(--h5-space-3);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  line-height: 22px;
  background: var(--h5-color-surface-subtle);
  border: 1px solid var(--h5-color-border);
  border-radius: var(--h5-radius-pill);
}

.search-chip .van-icon {
  color: var(--h5-color-text-tertiary);
  font-size: 15px;
}

.search-chip--hot {
  color: var(--h5-color-primary);
  background: var(--h5-color-primary-soft);
  border-color: var(--h5-color-primary-100);
}

.search-chip:active {
  color: var(--h5-color-primary);
  background: var(--h5-color-primary-100);
  border-color: var(--h5-color-primary-200);
}

.chip-index {
  display: inline-flex;
  width: 20px;
  height: 20px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-text-inverse);
  font-size: var(--h5-font-size-sm);
  background: var(--h5-color-primary);
  border-radius: 50%;
}

.search-results {
  min-height: 280px;
}

.result-header {
  align-items: center;
}

.institution-stack {
  display: flex;
  flex-direction: column;
  gap: var(--h5-space-3);
}

.search-loading {
  display: flex;
  min-height: 240px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.search-loading--standalone {
  margin: var(--h5-space-4) 0;
  border: 1px solid var(--h5-color-divider);
}

.no-result {
  padding: var(--h5-space-4);
  box-shadow: none;
}

.empty-tip {
  margin-top: var(--h5-space-2);
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
  text-align: center;
}

:deep(.van-empty__description) {
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
}

@media (max-width: 360px) {
  .search-header,
  .search-content {
    padding-right: var(--h5-space-3);
    padding-left: var(--h5-space-3);
  }

  .search-submit {
    min-width: 40px;
  }
}
</style>
