<template>
  <div class="institution-page">
    <!-- 导航栏 -->
    <van-nav-bar title="养老机构" fixed placeholder />

    <!-- 搜索栏 -->
    <van-search
      v-model="searchValue"
      shape="round"
      placeholder="搜索养老机构"
      @search="onSearch"
    />

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <van-dropdown-menu>
        <van-dropdown-item v-model="filterType" :options="typeOptions" @change="onFilterChange" />
        <van-dropdown-item v-model="filterArea" :options="areaOptions" @change="onFilterChange" />
        <van-dropdown-item v-model="filterSort" :options="sortOptions" @change="onFilterChange" />
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
          <InstitutionCard
            v-for="institution in institutionList"
            :key="institution.institutionId"
            :institution="institution"
          />
        </van-list>
      </van-pull-refresh>

      <!-- 空状态 -->
      <van-empty
        v-if="!loading && institutionList.length === 0"
        description="暂无机构数据"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { showToast } from 'vant'
import InstitutionCard from '@/components/InstitutionCard.vue'
import { getInstitutionList } from '@/api/institution'

const route = useRoute()

// 搜索值
const searchValue = ref('')

// 筛选条件
const filterType = ref(0)
const filterArea = ref(0)
const filterSort = ref('default')

// 筛选选项
const typeOptions = [
  { text: '全部类型', value: 0 },
  { text: '养老院', value: 1 },
  { text: '护理院', value: 2 },
  { text: '福利院', value: 3 }
]

const areaOptions = [
  { text: '全部区域', value: 0 },
  { text: '市辖区', value: 1 },
  { text: '郊区', value: 2 }
]

const sortOptions = [
  { text: '默认排序', value: 'default' },
  { text: '价格从低到高', value: 'price_asc' },
  { text: '价格从高到低', value: 'price_desc' }
]

// 列表数据
const institutionList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)

// 搜索
const onSearch = () => {
  pageNum.value = 1
  finished.value = false
  institutionList.value = []
  loadInstitutions()
}

// 筛选变化
const onFilterChange = () => {
  pageNum.value = 1
  finished.value = false
  institutionList.value = []
  loadInstitutions()
}

// 加载机构列表
const loadInstitutions = async () => {
  try {
    loading.value = true

    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      status: 'operating'
    }

    // 添加搜索关键词
    if (searchValue.value) {
      params.name = searchValue.value
    }

    // 添加筛选条件
    if (filterType.value !== 0) {
      params.institutionType = filterType.value
    }

    if (filterArea.value !== 0) {
      params.area = filterArea.value
    }

    // 添加排序条件
    if (filterSort.value !== 'default') {
      params.orderByColumn = 'price'
      params.isAsc = filterSort.value === 'price_asc' ? 'asc' : 'desc'
    }

    const res = await getInstitutionList(params)

    if (res.code === 200) {
      const newList = res.rows || []

      if (refreshing.value) {
        institutionList.value = newList
        refreshing.value = false
      } else {
        institutionList.value.push(...newList)
      }

      // 判断是否还有更多数据
      if (newList.length < pageSize.value) {
        finished.value = true
      } else {
        pageNum.value++
      }
    }
  } catch (error) {
    showToast('加载失败')
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
  // 从路由获取搜索关键词
  if (route.query.keyword) {
    searchValue.value = route.query.keyword
  }
  // 加载数据
  loadInstitutions()
})
</script>

<style scoped>
.institution-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60px;
}

.filter-bar {
  position: sticky;
  top: 46px;
  z-index: 99;
  background: #fff;
}

.institution-list {
  padding: 12px;
}
</style>
