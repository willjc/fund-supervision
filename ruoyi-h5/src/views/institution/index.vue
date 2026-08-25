<template>
  <div class="institution-page h5-page h5-page--constrained h5-page--tabbar">
    <header class="discovery-header">
      <div class="discovery-heading">
        <div>
          <span class="discovery-eyebrow">养老机构服务名录</span>
          <h1>查找合适的养老机构</h1>
          <p>按区域、照护能力与价格，安心筛选</p>
        </div>
        <span class="verified-mark"><van-icon name="shield-o" /> 监管服务</span>
      </div>
      <div class="search-bar">
        <van-search
          v-model="searchValue"
          placeholder="输入机构名称"
          shape="round"
          :show-action="false"
          @search="onSearch"
        />
        <van-button type="primary" @click="onSearch">搜索</van-button>
      </div>
    </header>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-tabs">
        <!-- 区域街道 -->
        <div
          class="filter-tab"
          :class="{ 'has-badge': filterParams.areaCodes.length > 0 || filterParams.streetNames.length > 0 }"
          @click="showAreaPanel = true"
        >
          <van-icon name="location-o" />
          <span>区域街道</span>
          <span class="filter-badge" v-if="filterParams.areaCodes.length > 0 || filterParams.streetNames.length > 0">
            {{ filterParams.areaCodes.length + filterParams.streetNames.length }}
          </span>
        </div>

        <div class="filter-tab-divider"></div>

        <!-- 筛选 -->
        <div
          class="filter-tab"
          :class="{ 'has-badge': getFilterCount() > 0 }"
          @click="showFilterPanel = true"
        >
          <van-icon name="filter-o" />
          <span>筛选</span>
          <span class="filter-badge" v-if="getFilterCount() > 0">
            {{ getFilterCount() }}
          </span>
        </div>

        <div class="filter-tab-divider"></div>

        <!-- 排序 -->
        <div class="filter-tab" @click="showSortPanel = true">
          <van-icon name="exchange" />
          <span>{{ getSortShortText() }}</span>
        </div>
      </div>
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
          <!-- 机构列表项（卡片+价格区域） -->
          <template v-for="institution in institutionList" :key="institution.institutionId">
            <InstitutionCard
              :institution="institution"
              :navigate-on-click="false"
              @select="goToDetail(institution.institutionId)"
            />

            <!-- 月参考价格区域 -->
            <div
              class="price-detail-card"
              v-if="institution.priceRanges"
            >
              <div class="price-header">
                <span><van-icon name="gold-coin-o" /> 月参考价格</span>
                <span class="price-note">实际费用以机构确认为准</span>
              </div>
              <div class="price-grid">
                <div class="price-item">
                  <span class="price-label">总费用</span>
                  <span class="price-value">¥{{ institution.priceRanges.total?.min || 0 }} ~ ¥{{ institution.priceRanges.total?.max || 0 }}</span>
                </div>
                <div class="price-item">
                  <span class="price-label">床位费</span>
                  <span class="price-value">¥{{ institution.priceRanges.bed?.min || 0 }} ~ ¥{{ institution.priceRanges.bed?.max || 0 }}</span>
                </div>
                <div class="price-item">
                  <span class="price-label">护理费</span>
                  <span class="price-value">¥{{ institution.priceRanges.nursing?.min || 0 }} ~ ¥{{ institution.priceRanges.nursing?.max || 0 }}</span>
                </div>
                <div class="price-item">
                  <span class="price-label">膳食费</span>
                  <span class="price-value">¥{{ institution.priceRanges.diet?.min || 0 }} ~ ¥{{ institution.priceRanges.diet?.max || 0 }}</span>
                </div>
              </div>
            </div>
          </template>
        </van-list>
      </van-pull-refresh>

      <!-- 空状态 -->
      <van-empty
        v-if="!loading && institutionList.length === 0"
        :image="institutionPlaceholder"
        image-size="132"
        description="暂无符合条件的机构"
      />
    </div>

    <!-- 侧边筛选面板 -->
    <van-popup
      v-model:show="showFilterPanel"
      position="right"
      :style="{ width: '85%', height: '100%' }"
    >
      <div class="filter-panel">
        <div class="filter-panel-header">
          <span class="filter-panel-title">筛选条件</span>
          <van-icon name="cross" @click="showFilterPanel = false" />
        </div>

        <div class="filter-panel-content">
          <!-- 机构性质 -->
          <div class="filter-section-panel">
            <div class="filter-section-title">
              <van-icon name="shop-o" />
              机构类型
            </div>
            <van-radio-group v-model="filterParams.institutionType">
              <van-radio
                v-for="item in institutionTypeOptions"
                :key="item.value"
                :name="item.value"
                class="filter-radio-item"
              >
                {{ item.text }}
              </van-radio>
            </van-radio-group>
          </div>

          <!-- 收住类型 -->
          <div class="filter-section-panel">
            <div class="filter-section-title">
              <van-icon name="user-o" />
              收住类型
            </div>
            <van-checkbox-group v-model="filterParams.careLevels">
              <van-checkbox
                v-for="item in careLevelOptions"
                :key="item.value"
                :name="item.value"
                class="filter-checkbox-item"
              >
                {{ item.text }}
              </van-checkbox>
            </van-checkbox-group>
          </div>

          <!-- 机构星级 -->
          <div class="filter-section-panel">
            <div class="filter-section-title">
              <van-icon name="star-o" />
              机构星级
            </div>
            <van-radio-group v-model="filterParams.ratingLevel">
              <van-radio
                v-for="item in ratingOptions"
                :key="item.value"
                :name="item.value"
                class="filter-radio-item"
              >
                {{ item.text }}
              </van-radio>
            </van-radio-group>
          </div>

          <!-- 价格区间 -->
          <div class="filter-section-panel">
            <div class="filter-section-title">
              <van-icon name="gold-coin-o" />
              价格区间
            </div>
            <van-radio-group v-model="filterParams.priceRange">
              <van-radio
                v-for="item in priceRangeOptions"
                :key="item.value"
                :name="item.value"
                class="filter-radio-item"
              >
                {{ item.text }}
              </van-radio>
            </van-radio-group>
          </div>
        </div>

        <div class="filter-panel-footer">
          <van-button block @click="resetFilter">重置</van-button>
          <van-button block type="primary" @click="confirmFilter">
            确定 {{ getFilterCount() > 0 ? `(${getFilterCount()})` : '' }}
          </van-button>
        </div>
      </div>
    </van-popup>

    <!-- 区域街道侧边面板 -->
    <van-popup
      v-model:show="showAreaPanel"
      position="right"
      :style="{ width: '85%', height: '100%' }"
    >
      <div class="filter-panel">
        <div class="filter-panel-header">
          <span class="filter-panel-title">区域街道</span>
          <van-icon name="cross" @click="showAreaPanel = false" />
        </div>

        <div class="filter-panel-content">
          <!-- 区域选择 -->
          <div class="filter-section-panel">
            <div class="filter-section-title">
              <van-icon name="location-o" />
              所属区域（可多选）
            </div>
            <div class="area-grid">
              <div
                v-for="item in districtOptions"
                :key="item.value"
                class="area-grid-item"
                :class="{ 'selected': filterParams.areaCodes.includes(item.value) }"
                @click="toggleArea(item.value)"
              >
                {{ item.text }}
              </div>
            </div>
          </div>

          <!-- 街道选择 -->
          <div class="filter-section-panel" v-if="filterParams.areaCodes.length > 0">
            <div class="filter-section-title">
              <van-icon name="home-o" />
              所属街道（可多选）
            </div>
            <div class="street-list">
              <div
                v-for="street in getAvailableStreets()"
                :key="street"
                class="street-list-item"
                :class="{ 'selected': filterParams.streetNames.includes(street) }"
                @click="toggleStreet(street)"
              >
                {{ street }}
              </div>
            </div>
          </div>
        </div>

        <div class="filter-panel-footer">
          <van-button block @click="resetAreaFilter">重置</van-button>
          <van-button block type="primary" @click="confirmAreaFilterFromPanel">
            确定 {{ (filterParams.areaCodes.length > 0 || filterParams.streetNames.length > 0) ? `(${filterParams.areaCodes.length + filterParams.streetNames.length})` : '' }}
          </van-button>
        </div>
      </div>
    </van-popup>

    <!-- 排序侧边面板 -->
    <van-popup
      v-model:show="showSortPanel"
      position="right"
      :style="{ width: '70%', height: '100%' }"
    >
      <div class="filter-panel">
        <div class="filter-panel-header">
          <span class="filter-panel-title">价格排序</span>
          <van-icon name="cross" @click="showSortPanel = false" />
        </div>

        <div class="filter-panel-content">
          <van-radio-group v-model="sortType">
            <van-radio
              v-for="item in sortOptions"
              :key="item.value"
              :name="item.value"
              class="filter-radio-item-large"
              @click="confirmSort"
            >
              {{ item.text }}
            </van-radio>
          </van-radio-group>
        </div>
      </div>
    </van-popup>

    <!-- 底部导航 -->
    <van-tabbar v-model="activeTab" class="institution-tabbar" fixed safe-area-inset-bottom>
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
import { getInstitutionList } from '@/api/institution'
import { getImageUrl } from '@/utils/image'
import InstitutionCard from '@/components/InstitutionCard.vue'
import institutionPlaceholder from '@/assets/images/institution-placeholder.svg'

const router = useRouter()

// 底部导航
const activeTab = ref(1)

// 搜索值
const searchValue = ref('')

// 筛选面板显示
const showFilterPanel = ref(false)
const showAreaPanel = ref(false)
const showSortPanel = ref(false)

// 排序类型
const sortType = ref('')
const sortOptions = [
  { text: '价格排序', value: '' },
  { text: '价格从低到高', value: 'priceAsc' },
  { text: '价格从高到低', value: 'priceDesc' }
]

// 筛选条件
const filterParams = ref({
  areaCodes: [],        // 区域代码多选
  streetNames: [],      // 街道名称多选
  institutionNature: '',
  institutionType: '',  // 机构类型
  careLevels: [],
  ratingLevel: null,
  institutionName: '',
  priceRange: ''        // 价格区间
})

// 筛选面板引用
// areaFilterRef 已移除，改为侧边面板

// 区域街道数据映射
const areaStreetMap = ref({
  '410102': ['航海西路街道办事处', '三官庙街道办事处', '秦岭路街道办事处', '桐柏路街道办事处', '建设路街道办事处', '汝河路街道办事处', '林山寨街道办事处', '棉纺路街道办事处', '中原西路街道办事处', '绿东村街道办事处', '柳湖街道办事处', '须水街道办事处', '莲湖街道办事处'],
  '410103': ['大学路办事处', '五里堡街道办事处', '德化街街道办事处', '解放路街道办事处', '铭功路街道办事处', '一马路街道办事处', '蜜蜂张街道办事处', '福华街街道办事处', '建中街街道办事处', '淮河路街道办事处', '长江路街道办事处', '京广路街道办事处', '嵩山路街道办事处', '人和路办事处', '大学南路派出所', '马寨镇', '侯寨乡'],
  '410104': ['北下街街道办事处', '西大街街道办事处', '南关街道办事处', '城东路街道办事处', '东大街街道办事处', '二里岗街道办事处', '紫荆山南路街道办事处', '管城街街道办事处', '陇海马路街道办事处', '航海东路街道办事处', '南曹乡', '十八里河镇'],
  '410105': ['兴达路街道办事处', '杨金路街道办事处', '丰庆路街道办事处', '东风路街道办事处', '北林路街道办事处', '国基路街道办事处', '未来路街道办事处', '凤凰台街道办事处', '人民路街道办事处', '经八路街道办事处', '文化路街道办事处', '花园路街道办事处', '大石桥街道办事处', '南阳路街道办事处', '杜岭街道办事处', '南阳新村街道办事处', '黄河路街道办事处'],
  '410106': ['峡窝镇', '济源路街道办事处', '新安西路街道办事处', '工业路街道办事处'],
  '410108': ['长兴路街道办事处', '老鸦陈街道办事处', '新城街道办事处', '迎宾路街道办事处', '刘寨街道办事处', '大河路街道办事处', '古荥镇', '花园口镇'],
  '410122': ['广惠街街道办事处', '青年路街道办事处', '东风路街道办事处', '雁鸣湖镇', '官渡镇', '狼城岗镇', '万滩镇', '白沙镇', '郑庵镇', '姚家镇', '黄店镇', '韩寺镇', '刁家乡', '刘集镇'],
  '410181': ['新华路街道办事处', '杜甫路街道办事处', '紫荆路街道办事处', '永安路街道办事处', '孝义街道办事处', '站街镇', '回郭镇', '涉村镇', '大峪沟镇', '竹林镇', '小关镇', '新中镇', '米河镇', '康店镇', '芝田镇', '河洛镇', '鲁庄镇', '西村镇', '夹津口镇'],
  '410182': ['京城路街道办事处', '索河街道办事处', '乔楼镇', '豫龙镇', '广武镇', '王村镇', '汜水镇', '高山镇', '刘河镇', '崔庙镇', '贾峪镇', '金寨回族乡', '城关乡', '高村乡'],
  '410183': ['新华路街道办事处', '青屏街街道办事处', '西大街街道办事处', '城关镇', '牛店镇', '平陌镇', '超化镇', '苟堂镇', '大隗镇', '刘寨镇', '白寨镇', '岳村镇', '曲梁镇', '来集镇', '米村镇', '袁庄乡'],
  '410184': ['新建路街道办事处', '新华路街道办事处', '新烟街道办事处', '和庄镇', '观音寺镇', '梨河镇', '孟庄镇', '薛店镇', '新村镇', '郭店镇', '龙湖镇'],
  '410185': ['嵩阳街道办事处', '少林街道办事处', '中岳街道办事处', '白坪乡', '君召乡', '石道乡', '东华镇', '大金店镇', '颍阳镇', '卢店镇', '告成镇', '徐庄镇', '大冶镇', '宣化镇', '王村乡'],
  '410171': ['明湖街道办事处', '潮河街道办事处', '京航街道办事处', '前程街道办事处', '九龙镇'],
  '410172': ['石佛镇', '沟赵乡', '梧桐办事处', '枫杨办事处', '双桥办事处'],
  '410173': ['如意湖街道办事处', '博学路街道办事处', '龙子湖街道办事处', '商都路街道办事处', '龙湖办事处', '祭城路街道办事处', '金光路街道办事处']
})

const districtOptions = ref([
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
  { text: '登封市', value: '410185' },
  { text: '经济技术开发区', value: '410171' },
  { text: '高新技术产业开发区', value: '410172' },
  { text: '郑东新区', value: '410173' }
])

const natureOptions = ref([
  { text: '全部', value: '' },
  { text: '民办', value: '1' },
  { text: '公办', value: '2' },
  { text: '公建民营', value: '3' }
])

const institutionTypeOptions = ref([
  { text: '全部', value: '' },
  { text: '养老院', value: 'nursing_home' },
  { text: '养老服务中心', value: 'service_center' },
  { text: '日间照料中心', value: 'day_care' },
  { text: '养老公寓', value: 'senior_apartment' },
  { text: '其他', value: 'other' }
])

const careLevelOptions = ref([
  { text: '自理', value: '1' },
  { text: '半护理', value: '2' },
  { text: '全护理', value: '3' },
  { text: '失能', value: '4' },
  { text: '失智', value: '5' }
])

const ratingOptions = ref([
  { text: '全部', value: null },
  { text: '三星及以上', value: 3 },
  { text: '四星及以上', value: 4 },
  { text: '五星', value: 5 }
])

const priceRangeOptions = ref([
  { text: '全部', value: '' },
  { text: '1500元以下', value: '0-1500' },
  { text: '1500-3000元', value: '1500-3000' },
  { text: '3000-5000元', value: '3000-5000' },
  { text: '5000元以上', value: '5000-999999' }
])

// 列表数据
const institutionList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)

// 获取筛选条件数量
const getFilterCount = () => {
  let count = 0
  if (filterParams.value.institutionNature) count++
  if (filterParams.value.institutionType) count++
  if (filterParams.value.careLevels.length > 0) count++
  if (filterParams.value.ratingLevel !== null) count++
  if (filterParams.value.priceRange) count++
  return count
}

// 获取机构性质文字
const getNatureText = (nature) => {
  const map = {
    '1': '民办',
    '2': '公办',
    '3': '公建民营'
  }
  return map[nature] || ''
}

// 获取机构性质样式类
const getNatureClass = (nature) => {
  const map = {
    '1': 'nature-private',
    '2': 'nature-public',
    '3': 'nature-ppp'
  }
  return map[nature] || 'nature-private'
}

// 区域街道筛选方法
const toggleArea = (areaCode) => {
  const index = filterParams.value.areaCodes.indexOf(areaCode)
  if (index > -1) {
    filterParams.value.areaCodes.splice(index, 1)
    // 清空该区域对应的街道选择
    const areaStreets = areaStreetMap.value[areaCode] || []
    filterParams.value.streetNames = filterParams.value.streetNames.filter(street => !areaStreets.includes(street))
  } else {
    filterParams.value.areaCodes.push(areaCode)
  }
}

const toggleStreet = (streetName) => {
  const index = filterParams.value.streetNames.indexOf(streetName)
  if (index > -1) {
    filterParams.value.streetNames.splice(index, 1)
  } else {
    filterParams.value.streetNames.push(streetName)
  }
}

const getAvailableStreets = () => {
  let streets = []
  for (const areaCode of filterParams.value.areaCodes) {
    const areaStreets = areaStreetMap.value[areaCode] || []
    streets = streets.concat(areaStreets)
  }
  return [...new Set(streets)] // 去重
}

const resetAreaFilter = () => {
  filterParams.value.areaCodes = []
  filterParams.value.streetNames = []
}

// 筛选方法
const resetFilter = () => {
  filterParams.value.areaCodes = []
  filterParams.value.streetNames = []
  filterParams.value.institutionNature = ''
  filterParams.value.institutionType = ''
  filterParams.value.careLevels = []
  filterParams.value.ratingLevel = null
  filterParams.value.priceRange = ''
}

const confirmFilter = () => {
  showFilterPanel.value = false
  onFilterChange()
}

// 获取排序文字
const getSortText = () => {
  const map = {
    '': '价格排序',
    'priceAsc': '价格从低到高',
    'priceDesc': '价格从高到低'
  }
  return map[sortType.value] || '价格排序'
}

// 获取排序简短文字
const getSortShortText = () => {
  const map = {
    '': '排序',
    'priceAsc': '价格↑',
    'priceDesc': '价格↓'
  }
  return map[sortType.value] || '排序'
}

// 确认排序
const confirmSort = () => {
  showSortPanel.value = false
  onFilterChange()
}

// 区域街道面板确认
const confirmAreaFilterFromPanel = () => {
  showAreaPanel.value = false
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
    // 构建查询参数
    const params = {
      pageNum: 1,
      pageSize: 50,
      institutionName: filterParams.value.institutionName || undefined,
      areaCodes: filterParams.value.areaCodes.length > 0 ? filterParams.value.areaCodes : undefined,
      streetNames: filterParams.value.streetNames.length > 0 ? filterParams.value.streetNames : undefined
    }

    // 调用真实API
    const response = await getInstitutionList(params)

    if (response.code === 200 && response.rows) {
      let processedList = response.rows.map(item => ({
        institutionId: item.institutionId,
        institutionName: item.institutionName,
        areaCode: item.areaCode,
        street: item.street,
        institutionNature: item.institutionNature || item.institutionType || '1',
        institutionType: item.institutionType || item.institution_type || '',
        careLevels: item.careLevels || null,
        acceptElderType: item.acceptElderType || null,
        ratingLevel: item.ratingLevel || 3,
        priceRangeMin: item.priceRanges?.total?.min || item.priceRangeMin || 1500,
        priceRangeMax: item.priceRanges?.total?.max || item.priceRangeMax || 3500,
        bedCount: item.bedCount || 50,
        address: item.address || '地址信息完善中',
        coverImage: getImageUrl(item.coverImage) || institutionPlaceholder,
        totalBeds: item.totalBeds || item.bedCount || 50,
        availableBeds: item.availableBeds || 0,
        priceRanges: item.priceRanges || {
          total: { min: 1500, max: 3500 },
          bed: { min: 500, max: 800 },
          nursing: { min: 800, max: 2000 },
          diet: { min: 600, max: 1200 }
        },
        lifeFacilities: item.lifeFacilities || []
      }))

      // 前端筛选：按机构性质筛选
      if (filterParams.value.institutionNature) {
        processedList = processedList.filter(item => item.institutionNature === filterParams.value.institutionNature)
      }

      // 按机构类型筛选
      if (filterParams.value.institutionType) {
        processedList = processedList.filter(item => {
          // 兼容两种格式：数字值(1)和英文代码(nursing_home)
          const itemType = item.institutionType || item.institution_type || ''
          return itemType === filterParams.value.institutionType
        })
      }

      // 按收住类型筛选（兼容 careLevels 和 acceptElderType 两种字段）
      if (filterParams.value.careLevels.length > 0) {
        processedList = processedList.filter(item => {
          // 优先使用 careLevels 字段
          if (item.careLevels) {
            const itemLevels = (item.careLevels || '').split(',')
            return filterParams.value.careLevels.some(level => itemLevels.includes(level))
          }
          // 如果 careLevels 为空，尝试使用 acceptElderType 字段
          if (item.acceptElderType) {
            const itemLevels = (item.acceptElderType || '').split(',')
            return filterParams.value.careLevels.some(level => itemLevels.includes(level))
          }
          // 如果两者都为空，返回 false（不匹配）
          return false
        })
      }

      // 按星级筛选
      if (filterParams.value.ratingLevel) {
        processedList = processedList.filter(item => item.ratingLevel >= filterParams.value.ratingLevel)
      }

      // 按价格区间筛选
      if (filterParams.value.priceRange) {
        const [min, max] = filterParams.value.priceRange.split('-').map(Number)
        processedList = processedList.filter(item => {
          const price = item.priceRanges?.total?.min || item.priceRangeMin || 0
          return price >= min && price <= max
        })
      }

      // 排序
      if (sortType.value === 'priceAsc') {
        processedList.sort((a, b) => a.priceRangeMin - b.priceRangeMin)
      } else if (sortType.value === 'priceDesc') {
        processedList.sort((a, b) => b.priceRangeMin - a.priceRangeMin)
      }

      institutionList.value = processedList
      finished.value = true
    } else {
      throw new Error(response.msg || '数据加载失败')
    }
  } catch (error) {
    console.error('加载失败:', error)
    showToast('加载失败: ' + (error.message || '网络错误'))
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
  overflow-x: hidden;
}

.discovery-header {
  padding:
    calc(var(--h5-safe-area-top) + var(--h5-space-5))
    var(--h5-page-padding)
    var(--h5-space-4);
  color: var(--h5-color-text-inverse);
  background:
    radial-gradient(circle at 88% 12%, rgba(255, 255, 255, 0.18), transparent 30%),
    linear-gradient(145deg, var(--h5-color-primary-700), var(--h5-color-primary-500));
}

.discovery-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--h5-space-3);
}

.discovery-eyebrow {
  display: block;
  margin-bottom: var(--h5-space-1);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  opacity: 0.86;
}

.discovery-heading h1 {
  font-size: var(--h5-font-size-3xl);
  font-weight: var(--h5-font-weight-bold);
  line-height: var(--h5-line-height-tight);
}

.discovery-heading p {
  margin-top: var(--h5-space-2);
  font-size: var(--h5-font-size-sm);
  line-height: var(--h5-line-height-normal);
  opacity: 0.9;
}

.verified-mark {
  display: inline-flex;
  flex: 0 0 auto;
  min-height: 30px;
  align-items: center;
  gap: var(--h5-space-1);
  padding: 3px var(--h5-space-2);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  line-height: 24px;
  background: rgba(255, 255, 255, 0.16);
  border: 1px solid rgba(255, 255, 255, 0.32);
  border-radius: var(--h5-radius-pill);
}

.search-bar {
  display: flex;
  align-items: center;
  gap: var(--h5-space-2);
  padding: var(--h5-space-2);
  margin-top: var(--h5-space-4);
  background: var(--h5-color-surface);
  border-radius: var(--h5-radius-lg);
  box-shadow: var(--h5-shadow-md);
}

.search-bar :deep(.van-search) {
  flex: 1;
  min-width: 0;
  padding: 0;
  background: transparent;
}

.search-bar :deep(.van-search__content) {
  min-height: 42px;
  padding-left: var(--h5-space-3);
  background: var(--h5-color-surface-subtle);
  border: 1px solid var(--h5-color-border);
  border-radius: var(--h5-radius-md);
}

.search-bar :deep(.van-field__control) {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-md);
}

.search-bar :deep(.van-button) {
  flex: 0 0 auto;
  min-width: 64px;
  min-height: 42px;
  padding: 0 var(--h5-space-4);
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
  border-radius: var(--h5-radius-md);
}

.filter-bar {
  position: sticky;
  top: 0;
  z-index: var(--h5-z-sticky);
  padding: var(--h5-space-3) var(--h5-page-padding);
  background: rgba(244, 247, 251, 0.96);
  border-bottom: 1px solid var(--h5-color-divider);
  backdrop-filter: blur(10px);
}

.filter-tabs {
  display: flex;
  align-items: center;
  min-height: 48px;
  padding: var(--h5-space-1);
  background: var(--h5-color-surface);
  border: 1px solid var(--h5-color-border);
  border-radius: var(--h5-radius-md);
  box-shadow: var(--h5-shadow-xs);
}

.filter-tab {
  position: relative;
  display: flex;
  flex: 1;
  min-width: 0;
  min-height: 40px;
  align-items: center;
  justify-content: center;
  gap: var(--h5-space-1);
  padding: var(--h5-space-2);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  border-radius: var(--h5-radius-sm);
  cursor: pointer;
}

.filter-tab .van-icon {
  flex: 0 0 auto;
  font-size: 17px;
}

.filter-tab.has-badge {
  color: var(--h5-color-primary);
  background: var(--h5-color-primary-soft);
}

.filter-tab:active {
  color: var(--h5-color-primary);
  background: var(--h5-color-primary-soft);
}

.filter-badge {
  display: inline-flex;
  min-width: 22px;
  height: 22px;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
  color: var(--h5-color-text-inverse);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-semibold);
  background: var(--h5-color-primary);
  border-radius: var(--h5-radius-pill);
}

.filter-tab-divider {
  width: 1px;
  height: 22px;
  background: var(--h5-color-divider);
}

.institution-list {
  padding: var(--h5-space-1) var(--h5-page-padding) var(--h5-space-6);
}

.institution-list :deep(.institution-card) {
  margin-top: var(--h5-space-3);
}

.price-detail-card {
  padding: var(--h5-space-3) var(--h5-space-4);
  margin-top: var(--h5-space-2);
  margin-bottom: var(--h5-space-4);
  background: var(--h5-color-info-soft);
  border: 1px solid var(--h5-color-primary-100);
  border-radius: var(--h5-radius-md);
}

.price-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--h5-space-2);
  margin-bottom: var(--h5-space-3);
}

.price-header > span:first-child {
  display: inline-flex;
  align-items: center;
  gap: var(--h5-space-1);
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-semibold);
}

.price-note {
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

.price-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--h5-space-2) var(--h5-space-3);
}

.price-item {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 2px;
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

.filter-panel {
  display: flex;
  height: 100%;
  flex-direction: column;
  background: var(--h5-color-page);
}

.filter-panel-header {
  display: flex;
  min-height: 60px;
  align-items: center;
  justify-content: space-between;
  padding:
    calc(var(--h5-safe-area-top) + var(--h5-space-3))
    var(--h5-space-4)
    var(--h5-space-3);
  background: var(--h5-color-surface);
  border-bottom: 1px solid var(--h5-color-divider);
}

.filter-panel-title {
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-xl);
  font-weight: var(--h5-font-weight-semibold);
}

.filter-panel-header .van-icon {
  display: inline-flex;
  width: 40px;
  height: 40px;
  align-items: center;
  justify-content: center;
  color: var(--h5-color-text-secondary);
  font-size: 20px;
  border-radius: var(--h5-radius-md);
  cursor: pointer;
}

.filter-panel-header .van-icon:active {
  color: var(--h5-color-primary);
  background: var(--h5-color-primary-soft);
}

.filter-panel-content {
  flex: 1;
  padding: var(--h5-space-4);
  overflow-y: auto;
  overscroll-behavior: contain;
}

.filter-section-panel {
  padding: var(--h5-space-4);
  margin-bottom: var(--h5-space-3);
  background: var(--h5-color-surface);
  border: 1px solid var(--h5-color-divider);
  border-radius: var(--h5-radius-lg);
}

.filter-section-title {
  display: flex;
  align-items: center;
  gap: var(--h5-space-2);
  margin-bottom: var(--h5-space-3);
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
}

.filter-section-title .van-icon {
  color: var(--h5-color-primary);
  font-size: 18px;
}

.filter-radio-item,
.filter-checkbox-item,
.filter-radio-item-large {
  min-height: 48px;
  padding: var(--h5-space-3) 0;
  border-bottom: 1px solid var(--h5-color-divider);
}

.filter-radio-item:last-child,
.filter-checkbox-item:last-child,
.filter-radio-item-large:last-child {
  border-bottom: 0;
}

.filter-radio-item :deep(.van-radio__label),
.filter-checkbox-item :deep(.van-checkbox__label),
.filter-radio-item-large :deep(.van-radio__label) {
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
  line-height: 22px;
}

.filter-panel-footer {
  display: flex;
  gap: var(--h5-space-3);
  padding:
    var(--h5-space-3)
    max(var(--h5-space-4), var(--h5-safe-area-right))
    calc(var(--h5-space-3) + var(--h5-safe-area-bottom))
    max(var(--h5-space-4), var(--h5-safe-area-left));
  background: var(--h5-color-surface);
  border-top: 1px solid var(--h5-color-divider);
  box-shadow: var(--h5-shadow-top-sm);
}

.filter-panel-footer :deep(.van-button) {
  flex: 1;
  min-height: 44px;
  font-size: var(--h5-font-size-md);
  font-weight: var(--h5-font-weight-semibold);
  border-radius: var(--h5-radius-md);
}

.area-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--h5-space-2);
}

.area-grid-item,
.street-list-item {
  display: flex;
  min-height: 44px;
  align-items: center;
  justify-content: center;
  padding: var(--h5-space-2) var(--h5-space-3);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  line-height: 20px;
  text-align: center;
  background: var(--h5-color-surface-subtle);
  border: 1px solid var(--h5-color-border);
  border-radius: var(--h5-radius-md);
  cursor: pointer;
}

.area-grid-item.selected,
.street-list-item.selected {
  color: var(--h5-color-primary);
  font-weight: var(--h5-font-weight-semibold);
  background: var(--h5-color-primary-soft);
  border-color: var(--h5-color-primary-300);
  box-shadow: var(--h5-shadow-focus);
}

.street-list {
  display: flex;
  flex-direction: column;
  gap: var(--h5-space-2);
}

.street-list-item {
  justify-content: flex-start;
  min-height: 48px;
  text-align: left;
}

.institution-tabbar {
  border-top: 1px solid var(--h5-color-divider);
  box-shadow: var(--h5-shadow-top-sm);
}

.institution-tabbar :deep(.van-tabbar-item) {
  min-height: 48px;
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
}

.institution-tabbar :deep(.van-tabbar-item--active) {
  color: var(--h5-color-primary);
  background: var(--h5-color-surface);
}

.institution-tabbar :deep(.van-tabbar-item__icon) {
  font-size: 23px;
}

:deep(.van-list__finished-text),
:deep(.van-list__loading) {
  color: var(--h5-color-text-tertiary);
  font-size: var(--h5-font-size-sm);
}

:deep(.van-empty__description) {
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-md);
}

@media (max-width: 360px) {
  .discovery-header,
  .filter-bar,
  .institution-list {
    padding-right: var(--h5-space-3);
    padding-left: var(--h5-space-3);
  }

  .verified-mark {
    display: none;
  }

  .price-grid {
    grid-template-columns: 1fr;
  }

  .price-note {
    display: none;
  }
}
</style>
