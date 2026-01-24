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
            <!-- 统一样式的机构卡片 -->
            <div
              class="listing-card"
              @click="goToDetail(institution.institutionId)"
            >
              <img
                class="listing-image"
                :src="institution.coverImage || defaultImage"
                mode="aspectFill"
              />
              <div class="listing-info">
                <div class="listing-header">
                  <span class="listing-title">{{ institution.institutionName }}</span>
                  <span class="listing-nature" :class="getNatureClass(institution.institutionNature)">
                    {{ getNatureText(institution.institutionNature) }}
                  </span>
                </div>
                <div class="listing-status">
                  <span
                    class="status-text"
                    :class="{ available: institution.availableBeds > 0 }"
                  >
                    {{ institution.availableBeds > 0 ? '有床位' : '暂无床位' }}
                  </span>
                  <span class="status-divider">|</span>
                  <span class="status-count">共{{ institution.totalBeds || institution.bedCount || 0 }}床</span>
                </div>
                <span class="listing-address">{{ institution.address }}</span>
                <div class="listing-tags" v-if="institution.lifeFacilities && institution.lifeFacilities.length > 0">
                  <span class="tag" v-for="(tag, tagIndex) in institution.lifeFacilities.slice(0, 3)" :key="tagIndex">
                    {{ tag }}
                  </span>
                </div>
                <div class="listing-price" v-if="institution.priceRanges">
                  <span class="price-number">{{ institution.priceRanges.bed?.min || institution.priceRanges.total?.min || 0 }}</span>
                  <span class="price-unit">元</span>
                  <span class="price-suffix">/月起</span>
                </div>
              </div>
            </div>

            <!-- 月参考价格区域 -->
            <div
              class="price-detail-card"
              v-if="institution.priceRanges"
            >
              <div class="price-header">月参考价格</div>
              <div class="price-grid">
                <div class="price-item">
                  <span class="price-label">总费用:</span>
                  <span class="price-value">¥{{ institution.priceRanges.total?.min || 0 }} ~ ¥{{ institution.priceRanges.total?.max || 0 }}</span>
                </div>
                <div class="price-item">
                  <span class="price-label">床位费:</span>
                  <span class="price-value">¥{{ institution.priceRanges.bed?.min || 0 }} ~ ¥{{ institution.priceRanges.bed?.max || 0 }}</span>
                </div>
                <div class="price-item">
                  <span class="price-label">护理费:</span>
                  <span class="price-value">¥{{ institution.priceRanges.nursing?.min || 0 }} ~ ¥{{ institution.priceRanges.nursing?.max || 0 }}</span>
                </div>
                <div class="price-item">
                  <span class="price-label">膳食费:</span>
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
              机构性质
            </div>
            <van-radio-group v-model="filterParams.institutionNature">
              <van-radio
                v-for="item in natureOptions"
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

          <!-- 医疗条件 -->
          <div class="filter-section-panel">
            <div class="filter-section-title">
              <van-icon name="medicine-o" />
              医疗条件
            </div>
            <van-radio-group v-model="filterParams.medicalCondition">
              <van-radio
                v-for="item in medicalOptions"
                :key="item.value"
                :name="item.value"
                class="filter-radio-item"
              >
                {{ item.text }}
              </van-radio>
            </van-radio-group>
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
import { getInstitutionList } from '@/api/institution'

const router = useRouter()
const defaultImage = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

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
  careLevels: [],
  medicalCondition: '',
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
  if (filterParams.value.careLevels.length > 0) count++
  if (filterParams.value.medicalCondition) count++
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
  filterParams.value.careLevels = []
  filterParams.value.medicalCondition = ''
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
        careLevels: item.careLevels || '1,2,3',
        medicalCondition: item.medicalCondition || '1',
        ratingLevel: item.ratingLevel || 3,
        priceRangeMin: item.priceRanges?.total?.min || item.priceRangeMin || 1500,
        priceRangeMax: item.priceRanges?.total?.max || item.priceRangeMax || 3500,
        bedCount: item.bedCount || 50,
        address: item.address || '地址信息完善中',
        coverImage: item.coverImage || '',
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

      // 按收住类型筛选
      if (filterParams.value.careLevels.length > 0) {
        processedList = processedList.filter(item => {
          const itemLevels = (item.careLevels || '').split(',')
          return filterParams.value.careLevels.some(level => itemLevels.includes(level))
        })
      }

      // 按医疗条件筛选
      if (filterParams.value.medicalCondition) {
        processedList = processedList.filter(item => item.medicalCondition === filterParams.value.medicalCondition)
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
  min-height: 100vh;
  background-color: #f5f6fc;
  padding-bottom: 60px;
}

/* 搜索栏 */
.search-bar {
  display: flex;
  align-items: center;
  padding: 10px 12px;
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

/* 筛选栏 */
.filter-bar {
  background: #fff;
  border-bottom: 1px solid #eee;
  /* padding: 12px; */
}

.filter-tabs {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 8px;
  padding: 4px;
}

.filter-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  color: #666;
  font-size: 14px;
}

.filter-tab .van-icon {
  font-size: 16px;
  margin-right: 4px;
}

.filter-tab span {
  font-size: 14px;
}

.filter-tab.has-badge {
  color: #1989fa;
}

.filter-tab:active {
  background: rgba(25, 137, 250, 0.1);
  border-radius: 6px;
}

.filter-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  background: #1989fa;
  color: #fff;
  font-size: 10px;
  border-radius: 8px;
  margin-left: 4px;
}

.filter-tab-divider {
  width: 1px;
  height: 16px;
  background: #ddd;
}

/* 机构列表 */
.institution-list {
  padding: 8px 12px 15px;
}

/* 统一样式的机构卡片（参考首页） */
.listing-card {
  display: flex;
  background-color: #fff;
  padding: 8px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 10px;
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

/* 机构性质标签 */
.listing-nature {
  font-size: 10px;
  color: #fff;
  padding: 2px 6px;
  border-radius: 4px;
  margin-left: 8px;
  flex-shrink: 0;
}

/* .nature-private {
  background: linear-gradient(135deg, #5B8FF9 0%, #3d7bd9 100%);
} */

.nature-public {
  background: linear-gradient(135deg, #07c160 0%, #06ad56 100%);
}

.nature-ppp {
  background: linear-gradient(135deg, #ff976a 0%, #f37b1d 100%);
}

.listing-status {
  display: flex;
  align-items: center;
  margin-bottom: 3px;
}

.status-text {
  font-size: 12px;
  color: #999;
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
  color: #999;
}

.listing-address {
  font-size: 12px;
  color: #333;
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
}

.price-unit {
  font-size: 14px;
  color: #e5252b;
  font-weight: normal;
  line-height: 20px;
  margin-left: 2px;
}

.price-suffix {
  font-size: 12px;
  color: #999;
  font-weight: normal;
  line-height: 20px;
  margin-left: 2px;
}

/* 月参考价格区域 */
.price-detail-card {
  background: #e8f4fc;
  border-radius: 8px;
  padding: 10px 12px;
  margin-bottom: 10px;
  margin-top: -8px;
}

.price-header {
  font-size: 12px;
  color: #1989fa;
  margin-bottom: 8px;
  font-weight: 500;
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
  margin-left: 4px;
}

/* 增强的筛选样式 */
.filter-content-enhanced {
  max-height: 70vh;
  overflow-y: auto;
  padding: 16px;
}

.selected-summary {
  margin-bottom: 16px;
}

.filter-section-enhanced {
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.section-header .van-icon {
  color: #1989fa;
  margin-right: 8px;
  font-size: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #323233;
  flex: 1;
}

.section-count {
  font-size: 12px;
  color: #969799;
  background: #f7f8fa;
  padding: 2px 6px;
  border-radius: 8px;
}

/* 区域选择网格 */
.filter-options-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.area-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border: 1px solid #ebedf0;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
  min-height: 40px;
}

.area-item:hover {
  border-color: #1989fa;
  background: #f0f8ff;
}

.area-item.selected {
  border-color: #1989fa;
  background: #ecf5ff;
  box-shadow: 0 2px 4px rgba(25, 137, 250, 0.15);
}

.area-text {
  font-size: 13px;
  color: #323233;
  flex: 1;
  text-align: center;
}

.area-item.selected .area-text {
  color: #1989fa;
  font-weight: 500;
}

.check-icon {
  color: #07c160;
  font-size: 14px;
  margin-left: 4px;
}

/* 街道选择容器 */
.street-container {
  background: #fafafa;
  border-radius: 8px;
  padding: 12px;
  border: 1px solid #ebedf0;
}

.street-tip {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  padding: 8px 12px;
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 6px;
  font-size: 12px;
  color: #fa8c16;
}

.street-tip .van-icon {
  margin-right: 6px;
}

.filter-options-scroll {
  max-height: 200px;
  overflow-y: auto;
  padding-right: 4px;
}

.filter-options-scroll::-webkit-scrollbar {
  width: 4px;
}

.filter-options-scroll::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.filter-options-scroll::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.street-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 10px;
  margin-bottom: 6px;
  border: 1px solid #ebedf0;
  border-radius: 6px;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
}

.street-item:hover {
  border-color: #1989fa;
  background: #f0f8ff;
}

.street-item.selected {
  border-color: #1989fa;
  background: #ecf5ff;
  box-shadow: 0 1px 3px rgba(25, 137, 250, 0.1);
}

.street-text {
  font-size: 13px;
  color: #323233;
  flex: 1;
  line-height: 1.3;
}

.street-item.selected .street-text {
  color: #1989fa;
  font-weight: 500;
}

/* 增强的操作按钮 */
.filter-actions-enhanced {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.filter-actions-enhanced .van-button {
  flex: 1;
  border-radius: 8px;
  font-weight: 500;
}

/* 侧边筛选面板 */
.filter-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #f5f5f5;
}

.filter-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.filter-panel-title {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
}

.filter-panel-header .van-icon {
  font-size: 20px;
  color: #969799;
  cursor: pointer;
}

.filter-panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.filter-section-panel {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.filter-section-title {
  display: flex;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 12px;
}

.filter-section-title .van-icon {
  color: #1989fa;
  margin-right: 8px;
  font-size: 16px;
}

.filter-radio-item,
.filter-checkbox-item {
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}

.filter-radio-item:last-child,
.filter-checkbox-item:last-child {
  border-bottom: none;
}

.filter-panel-footer {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-top: 1px solid #eee;
}

.filter-panel-footer .van-button {
  flex: 1;
  border-radius: 8px;
  font-weight: 500;
}

/* 区域街道面板样式 */
.area-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  /* gap: 8px; */
}

.area-grid-item {
  padding: 12px 8px;
  text-align: center;
  border: 1px solid #ebedf0;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 13px;
}

.area-grid-item:hover {
  border-color: #1989fa;
  background: #f0f8ff;
}

.area-grid-item.selected {
  border-color: #1989fa;
  background: #ecf5ff;
  color: #1989fa;
  font-weight: 500;
}

.street-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.street-list-item {
  padding: 12px 16px;
  border: 1px solid #ebedf0;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
}

.street-list-item:hover {
  border-color: #1989fa;
  background: #f0f8ff;
}

.street-list-item.selected {
  border-color: #1989fa;
  background: #ecf5ff;
  color: #1989fa;
  font-weight: 500;
}

.filter-radio-item-large {
  padding: 16px 0;
  font-size: 15px;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .filter-options-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .area-text {
    font-size: 12px;
  }

  .street-text {
    font-size: 12px;
  }
}
</style>
