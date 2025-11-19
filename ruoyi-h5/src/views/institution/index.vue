<template>
  <div class="institution-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <van-search
        v-model="searchValue"
        placeholder="请输入机构名称"
        shape="square"
        :show-action="false"
      />
      <van-button type="primary" size="small" @click="onSearch">搜索</van-button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <van-dropdown-menu>
        <van-dropdown-item v-model="filterParams.districtCode" :options="districtOptions" @change="onFilterChange" />
        <van-dropdown-item title="筛选" ref="filterRef">
          <div class="filter-content">
            <div class="filter-section">
              <div class="filter-title">机构性质</div>
              <div class="filter-options">
                <van-tag
                  v-for="item in natureOptions"
                  :key="item.value"
                  :type="filterParams.institutionNature === item.value ? 'primary' : 'default'"
                  @click="selectNature(item.value)"
                >
                  {{ item.text }}
                </van-tag>
              </div>
            </div>
            <div class="filter-section">
              <div class="filter-title">收住类型</div>
              <div class="filter-options">
                <van-tag
                  v-for="item in careLevelOptions"
                  :key="item.value"
                  :type="filterParams.careLevels.includes(item.value) ? 'primary' : 'default'"
                  @click="toggleCareLevel(item.value)"
                >
                  {{ item.text }}
                </van-tag>
              </div>
            </div>
            <div class="filter-section">
              <div class="filter-title">医疗条件</div>
              <div class="filter-options">
                <van-tag
                  v-for="item in medicalOptions"
                  :key="item.value"
                  :type="filterParams.medicalCondition === item.value ? 'primary' : 'default'"
                  @click="selectMedical(item.value)"
                >
                  {{ item.text }}
                </van-tag>
              </div>
            </div>
            <div class="filter-section">
              <div class="filter-title">机构星级</div>
              <div class="filter-options">
                <van-tag
                  v-for="item in ratingOptions"
                  :key="item.value"
                  :type="filterParams.ratingLevel === item.value ? 'primary' : 'default'"
                  @click="selectRating(item.value)"
                >
                  {{ item.text }}
                </van-tag>
              </div>
            </div>
            <div class="filter-actions">
              <van-button size="small" @click="resetFilter">重置</van-button>
              <van-button type="primary" size="small" @click="confirmFilter">确定</van-button>
            </div>
          </div>
        </van-dropdown-item>
        <van-dropdown-item v-model="sortType" :options="sortOptions" @change="onFilterChange" />
      </van-dropdown-menu>
    </div>

    <!-- 机构列表 -->
    <div class="institution-list">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <div
            v-for="institution in institutionList"
            :key="institution.institutionId"
            class="institution-card"
            @click="goToDetail(institution.institutionId)"
          >
            <div class="card-main">
              <div class="card-image">
                <van-image
                  :src="institution.coverImage || defaultImage"
                  fit="cover"
                  width="80"
                  height="80"
                />
                <div class="image-label">机构图片</div>
              </div>
              <div class="card-content">
                <div class="card-title">{{ institution.institutionName }}</div>
                <div class="card-info">
                  床位数: {{ institution.bedCount || 0 }} (可定床位数/总床位数)
                </div>
                <div class="card-address">
                  详细地址: <van-icon name="location-o" /> {{ institution.address }}
                </div>
              </div>
            </div>
            <div class="card-price">
              <div class="price-header">月参考价格</div>
              <div class="price-grid">
                <div class="price-item">
                  <span class="price-label">总费用:</span>
                  <span class="price-value">¥{{ institution.priceRangeMin }} ~ ¥{{ institution.priceRangeMax }}</span>
                </div>
                <div class="price-item">
                  <span class="price-label">床位费:</span>
                  <span class="price-value">¥500 ~ ¥500</span>
                </div>
                <div class="price-item">
                  <span class="price-label">护理费:</span>
                  <span class="price-value">¥400 ~ ¥1500</span>
                </div>
                <div class="price-item">
                  <span class="price-label">膳食费:</span>
                  <span class="price-value">¥600 ~ ¥1500</span>
                </div>
              </div>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>

      <!-- 空状态 -->
      <van-empty
        v-if="!loading && institutionList.length === 0"
        description="暂无符合条件的机构"
      />
    </div>

    <!-- 底部导航 -->
    <van-tabbar v-model="activeTab" fixed>
      <van-tabbar-item icon="home-o" to="/index">首页</van-tabbar-item>
      <van-tabbar-item icon="apps-o" to="/institution">机构</van-tabbar-item>
      <van-tabbar-item icon="orders-o" to="/order">订单</van-tabbar-item>
      <van-tabbar-item icon="user-o" to="/my">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()
const defaultImage = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

// 底部导航
const activeTab = ref(1)

// 搜索值
const searchValue = ref('')

// 排序类型
const sortType = ref('')
const sortOptions = [
  { text: '价格排序', value: '' },
  { text: '价格从低到高', value: 'priceAsc' },
  { text: '价格从高到低', value: 'priceDesc' }
]

// 筛选条件
const filterParams = ref({
  districtCode: '',
  institutionNature: '',
  careLevels: [],
  medicalCondition: '',
  ratingLevel: null,
  institutionName: ''
})

// 筛选面板引用
const filterRef = ref(null)

// 下拉选项数据
const districtOptions = ref([
  { text: '选择区域', value: '' },
  { text: '中原区', value: '410102' },
  { text: '二七区', value: '410103' },
  { text: '管城回族区', value: '410104' },
  { text: '金水区', value: '410105' },
  { text: '上街区', value: '410106' },
  { text: '惠济区', value: '410108' },
  { text: '中牟县', value: '410122' },
  { text: '巩义市', value: '410181' },
  { text: '荥阳市', value: '410182' },
  { text: '新密市', value: '410183' },
  { text: '新郑市', value: '410184' },
  { text: '登封市', value: '410185' }
])

const natureOptions = ref([
  { text: '全部', value: '' },
  { text: '民办', value: '1' },
  { text: '公办', value: '2' },
  { text: '公建民营', value: '3' }
])

const careLevelOptions = ref([
  { text: '自理', value: '1' },
  { text: '半护理', value: '2' },
  { text: '全护理', value: '3' },
  { text: '失能', value: '4' },
  { text: '失智', value: '5' }
])

const medicalOptions = ref([
  { text: '全部', value: '' },
  { text: '内设医疗机构', value: '1' },
  { text: '与医疗机构合作', value: '2' },
  { text: '自营医疗机构', value: '3' },
  { text: '无医养结合', value: '4' }
])

const ratingOptions = ref([
  { text: '全部', value: null },
  { text: '三星及以上', value: 3 },
  { text: '四星及以上', value: 4 },
  { text: '五星', value: 5 }
])

// 列表数据
const institutionList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 模拟数据
const mockInstitutions = [
  {
    institutionId: 1,
    institutionName: '郑州市金水区花园口社区沁心养老服务中心',
    districtCode: '410105',
    institutionNature: '1',
    careLevels: '1,2,3',
    medicalCondition: '1',
    ratingLevel: 5,
    priceRangeMin: 1500,
    priceRangeMax: 3500,
    bedCount: 50,
    address: '郑州市金水区花园路233号',
    coverImage: ''
  },
  {
    institutionId: 2,
    institutionName: '郑州市金水区花园口社区沁心养老服务中心',
    districtCode: '410105',
    institutionNature: '2',
    careLevels: '1,2',
    medicalCondition: '2',
    ratingLevel: 4,
    priceRangeMin: 1500,
    priceRangeMax: 3500,
    bedCount: 50,
    address: '郑州市金水区花园路233号',
    coverImage: ''
  },
  {
    institutionId: 3,
    institutionName: '郑州市金水区花园口社区沁心养老服务中心',
    districtCode: '410103',
    institutionNature: '3',
    careLevels: '2,3,4',
    medicalCondition: '1',
    ratingLevel: 3,
    priceRangeMin: 1500,
    priceRangeMax: 3500,
    bedCount: 50,
    address: '郑州市金水区花园路233号',
    coverImage: ''
  }
]

// 筛选方法
const selectNature = (value) => {
  filterParams.value.institutionNature = filterParams.value.institutionNature === value ? '' : value
}

const toggleCareLevel = (value) => {
  const index = filterParams.value.careLevels.indexOf(value)
  if (index > -1) {
    filterParams.value.careLevels.splice(index, 1)
  } else {
    filterParams.value.careLevels.push(value)
  }
}

const selectMedical = (value) => {
  filterParams.value.medicalCondition = filterParams.value.medicalCondition === value ? '' : value
}

const selectRating = (value) => {
  filterParams.value.ratingLevel = filterParams.value.ratingLevel === value ? null : value
}

const resetFilter = () => {
  filterParams.value.institutionNature = ''
  filterParams.value.careLevels = []
  filterParams.value.medicalCondition = ''
  filterParams.value.ratingLevel = null
}

const confirmFilter = () => {
  filterRef.value?.toggle()
  onFilterChange()
}

// 筛选变化
const onFilterChange = () => {
  institutionList.value = []
  finished.value = false
  loadInstitutions()
}

// 搜索
const onSearch = () => {
  filterParams.value.institutionName = searchValue.value
  onFilterChange()
}

// 加载机构列表
const loadInstitutions = async () => {
  if (loading.value || finished.value) return

  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 300))

    let filteredList = [...mockInstitutions]

    // 按区域筛选
    if (filterParams.value.districtCode) {
      filteredList = filteredList.filter(item => item.districtCode === filterParams.value.districtCode)
    }

    // 按机构性质筛选
    if (filterParams.value.institutionNature) {
      filteredList = filteredList.filter(item => item.institutionNature === filterParams.value.institutionNature)
    }

    // 按收住类型筛选
    if (filterParams.value.careLevels.length > 0) {
      filteredList = filteredList.filter(item => {
        const itemLevels = item.careLevels.split(',')
        return filterParams.value.careLevels.some(level => itemLevels.includes(level))
      })
    }

    // 按医疗条件筛选
    if (filterParams.value.medicalCondition) {
      filteredList = filteredList.filter(item => item.medicalCondition === filterParams.value.medicalCondition)
    }

    // 按星级筛选
    if (filterParams.value.ratingLevel) {
      filteredList = filteredList.filter(item => item.ratingLevel >= filterParams.value.ratingLevel)
    }

    // 按名称搜索
    if (filterParams.value.institutionName) {
      filteredList = filteredList.filter(item =>
        item.institutionName.includes(filterParams.value.institutionName)
      )
    }

    // 排序
    if (sortType.value === 'priceAsc') {
      filteredList.sort((a, b) => a.priceRangeMin - b.priceRangeMin)
    } else if (sortType.value === 'priceDesc') {
      filteredList.sort((a, b) => b.priceRangeMin - a.priceRangeMin)
    }

    institutionList.value = filteredList
    finished.value = true
  } catch (error) {
    console.error('加载失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  finished.value = false
  refreshing.value = true
  institutionList.value = []
  loadInstitutions().then(() => {
    refreshing.value = false
  })
}

// 上拉加载
const onLoad = () => {
  loadInstitutions()
}

// 跳转详情
const goToDetail = (id) => {
  router.push({ name: 'InstitutionDetail', params: { id } })
}

onMounted(() => {
  loadInstitutions()
})
</script>

<style scoped>
.institution-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60px;
}

.search-bar {
  display: flex;
  align-items: center;
  padding: 10px;
  background: #fff;
  gap: 10px;
}

.search-bar .van-search {
  flex: 1;
  padding: 0;
}

.search-bar .van-button {
  flex-shrink: 0;
}

.filter-bar {
  background: #fff;
  border-bottom: 1px solid #eee;
}

.filter-content {
  padding: 16px;
}

.filter-section {
  margin-bottom: 16px;
}

.filter-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
}

.filter-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-options .van-tag {
  cursor: pointer;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.institution-list {
  padding: 10px;
}

.institution-card {
  background: #fff;
  border-radius: 4px;
  margin-bottom: 10px;
  overflow: hidden;
}

.card-main {
  display: flex;
  padding: 12px;
  gap: 12px;
}

.card-image {
  position: relative;
  flex-shrink: 0;
  width: 80px;
  height: 80px;
  background: #f5f5f5;
}

.image-label {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 10px;
  text-align: center;
  padding: 2px 0;
}

.card-content {
  flex: 1;
  min-width: 0;
}

.card-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-info {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.card-address {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 2px;
}

.card-price {
  background: #e8f4fc;
  padding: 10px 12px;
}

.price-header {
  font-size: 12px;
  color: #1989fa;
  margin-bottom: 8px;
}

.price-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 6px;
}

.price-item {
  font-size: 12px;
}

.price-label {
  color: #666;
}

.price-value {
  color: #1989fa;
}
</style>
