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

// 模拟机构数据(包含价格区间)
const mockInstitutions = [
  {
    institutionId: 1,
    institutionName: '郑州市金水区花园口社区养老服务中心',
    address: '郑州市金水区花园口镇花园路123号',
    contactPhone: '0371-12345678',
    coverImage: 'https://via.placeholder.com/300x200',
    priceRanges: {
      nursing: { min: 1500, max: 3000 },
      meal: { min: 800, max: 1200 },
      bed: { min: 500, max: 1000 },
      diet: { min: 600, max: 900 }
    }
  },
  {
    institutionId: 2,
    institutionName: '郑州市二七区康乐养老院',
    address: '郑州市二七区建设路456号',
    contactPhone: '0371-23456789',
    coverImage: 'https://via.placeholder.com/300x200',
    priceRanges: {
      nursing: { min: 2000, max: 4000 },
      meal: { min: 900, max: 1500 },
      bed: { min: 600, max: 1200 },
      diet: { min: 700, max: 1000 }
    }
  },
  {
    institutionId: 3,
    institutionName: '郑州市中原区福星养老中心',
    address: '郑州市中原区中原路789号',
    contactPhone: '0371-34567890',
    coverImage: 'https://via.placeholder.com/300x200',
    priceRanges: {
      nursing: { min: 1800, max: 3500 },
      meal: { min: 850, max: 1300 },
      bed: { min: 550, max: 1100 },
      diet: { min: 650, max: 950 }
    }
  },
  {
    institutionId: 4,
    institutionName: '郑州市管城区温馨养老服务中心',
    address: '郑州市管城区紫荆山路321号',
    contactPhone: '0371-45678901',
    coverImage: 'https://via.placeholder.com/300x200',
    priceRanges: {
      nursing: { min: 1600, max: 3200 },
      meal: { min: 800, max: 1200 },
      bed: { min: 500, max: 1000 },
      diet: { min: 600, max: 900 }
    }
  },
  {
    institutionId: 5,
    institutionName: '郑州市惠济区夕阳红养老院',
    address: '郑州市惠济区江山路654号',
    contactPhone: '0371-56789012',
    coverImage: 'https://via.placeholder.com/300x200',
    priceRanges: {
      nursing: { min: 2200, max: 4200 },
      meal: { min: 950, max: 1600 },
      bed: { min: 650, max: 1300 },
      diet: { min: 750, max: 1100 }
    }
  },
  {
    institutionId: 6,
    institutionName: '郑州市高新区颐和养老中心',
    address: '郑州市高新区科学大道987号',
    contactPhone: '0371-67890123',
    coverImage: 'https://via.placeholder.com/300x200',
    priceRanges: {
      nursing: { min: 2500, max: 5000 },
      meal: { min: 1000, max: 1800 },
      bed: { min: 700, max: 1500 },
      diet: { min: 800, max: 1200 }
    }
  }
]

// 加载机构列表
const loadInstitutions = async () => {
  try {
    loading.value = true

    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    // 过滤数据
    let filteredList = [...mockInstitutions]

    // 搜索关键词过滤
    if (searchValue.value) {
      filteredList = filteredList.filter(item =>
        item.institutionName.toLowerCase().includes(searchValue.value.toLowerCase()) ||
        item.address.toLowerCase().includes(searchValue.value.toLowerCase())
      )
    }

    // 类型筛选(这里简化处理,实际应该根据机构类型字段过滤)
    // if (filterType.value !== 0) {
    //   filteredList = filteredList.filter(item => item.institutionType === filterType.value)
    // }

    // 排序
    if (filterSort.value === 'price_asc') {
      filteredList.sort((a, b) => a.priceRanges.nursing.min - b.priceRanges.nursing.min)
    } else if (filterSort.value === 'price_desc') {
      filteredList.sort((a, b) => b.priceRanges.nursing.min - a.priceRanges.nursing.min)
    }

    // 分页处理
    const startIndex = (pageNum.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    const newList = filteredList.slice(startIndex, endIndex)

    if (refreshing.value) {
      institutionList.value = newList
      refreshing.value = false
    } else {
      institutionList.value.push(...newList)
    }

    // 判断是否还有更多数据
    if (endIndex >= filteredList.length) {
      finished.value = true
    } else {
      pageNum.value++
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
