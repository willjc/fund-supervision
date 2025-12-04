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
        <van-dropdown-item title="区域街道" ref="areaFilterRef">
          <div class="filter-content-enhanced">
            <!-- 选中状态提示 -->
            <div class="selected-summary" v-if="filterParams.areaCodes.length > 0 || filterParams.streetNames.length > 0">
              <van-notice-bar
                :text="`已选择 ${filterParams.areaCodes.length} 个区域，${filterParams.streetNames.length} 个街道`"
                color="#1989fa"
                background="#ecf5ff"
                :scrollable="false"
              />
            </div>

            <!-- 区域选择 -->
            <div class="filter-section-enhanced">
              <div class="section-header">
                <van-icon name="location-o" />
                <span class="section-title">所属区域</span>
                <span class="section-count">({{ filterParams.areaCodes.length }}/{{ districtOptions.length }})</span>
              </div>
              <div class="filter-options-grid">
                <div
                  v-for="item in districtOptions"
                  :key="item.value"
                  class="area-item"
                  :class="{ 'selected': filterParams.areaCodes.includes(item.value) }"
                  @click="toggleArea(item.value)"
                >
                  <span class="area-text">{{ item.text }}</span>
                  <van-icon v-if="filterParams.areaCodes.includes(item.value)" name="success" class="check-icon" />
                </div>
              </div>
            </div>

            <!-- 街道选择 -->
            <div class="filter-section-enhanced" v-if="filterParams.areaCodes.length > 0">
              <div class="section-header">
                <van-icon name="home-o" />
                <span class="section-title">所属街道</span>
                <span class="section-count">({{ filterParams.streetNames.length }}/{{ getAvailableStreets().length }})</span>
              </div>
              <div class="street-container">
                <div class="street-tip">
                  <van-icon name="info-o" size="14" />
                  <span>可选择多个街道进行精准筛选</span>
                </div>
                <div class="filter-options-scroll">
                  <div
                    v-for="street in getAvailableStreets()"
                    :key="street"
                    class="street-item"
                    :class="{ 'selected': filterParams.streetNames.includes(street) }"
                    @click="toggleStreet(street)"
                  >
                    <span class="street-text">{{ street }}</span>
                    <van-icon v-if="filterParams.streetNames.includes(street)" name="success" class="check-icon" />
                  </div>
                </div>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="filter-actions-enhanced">
              <van-button
                plain
                type="info"
                size="small"
                @click="resetAreaFilter"
                icon="replay"
              >
                重置
              </van-button>
              <van-button
                type="primary"
                size="small"
                @click="confirmAreaFilter"
                icon="success"
              >
                确定 {{ (filterParams.areaCodes.length > 0 || filterParams.streetNames.length > 0) ? `(${filterParams.areaCodes.length + filterParams.streetNames.length})` : '' }}
              </van-button>
            </div>
          </div>
        </van-dropdown-item>
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
                  床位数：{{ institution.availableBeds || 0 }}/{{ institution.totalBeds || 0 }}(可定床位数/总床位数)
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
                  <span class="price-value">¥{{ institution.priceRanges?.total?.min || institution.priceRangeMin }} ~ ¥{{ institution.priceRanges?.total?.max || institution.priceRangeMax }}</span>
                </div>
                <div class="price-item">
                  <span class="price-label">床位费:</span>
                  <span class="price-value">¥{{ institution.priceRanges?.bed?.min || 500 }} ~ ¥{{ institution.priceRanges?.bed?.max || 800 }}</span>
                </div>
                <div class="price-item">
                  <span class="price-label">护理费:</span>
                  <span class="price-value">¥{{ institution.priceRanges?.nursing?.min || 800 }} ~ ¥{{ institution.priceRanges?.nursing?.max || 2000 }}</span>
                </div>
                <div class="price-item">
                  <span class="price-label">膳食费:</span>
                  <span class="price-value">¥{{ institution.priceRanges?.diet?.min || 600 }} ~ ¥{{ institution.priceRanges?.diet?.max || 1200 }}</span>
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
import { getInstitutionList } from '@/api/institution'

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
  areaCodes: [],        // 区域代码多选
  streetNames: [],      // 街道名称多选
  institutionNature: '',
  careLevels: [],
  medicalCondition: '',
  ratingLevel: null,
  institutionName: ''
})

// 筛选面板引用
const filterRef = ref(null)
const areaFilterRef = ref(null)

// 下拉选项数据
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

const confirmAreaFilter = () => {
  areaFilterRef.value?.toggle()
  onFilterChange()
}

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
  filterParams.value.areaCodes = []
  filterParams.value.streetNames = []
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
    // 构建查询参数
    const params = {
      pageNum: 1,
      pageSize: 50, // 加载更多数据
      institutionName: filterParams.value.institutionName || undefined,
      areaCodes: filterParams.value.areaCodes.length > 0 ? filterParams.value.areaCodes : undefined,
      streetNames: filterParams.value.streetNames.length > 0 ? filterParams.value.streetNames : undefined
    }

    // 调用真实API
    const response = await getInstitutionList(params)

    if (response.code === 200 && response.rows) {
      // 后端已返回H5期望格式的数据，直接使用
      let processedList = response.rows.map(item => ({
        institutionId: item.institutionId,
        institutionName: item.institutionName,
        areaCode: item.areaCode,
        street: item.street,
        institutionNature: item.institutionType,
        careLevels: '1,2,3', // 默认值
        medicalCondition: '1', // 默认值
        ratingLevel: 5, // 默认值
        priceRangeMin: item.priceRanges?.total?.min || 1500,
        priceRangeMax: item.priceRanges?.total?.max || 3500,
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
        }
      }))

      // 前端筛选：按机构性质筛选
      if (filterParams.value.institutionNature) {
        processedList = processedList.filter(item => item.institutionNature === filterParams.value.institutionNature)
      }

      // 按收住类型筛选
      if (filterParams.value.careLevels.length > 0) {
        processedList = processedList.filter(item => {
          const itemLevels = item.careLevels.split(',')
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

/* 动画效果 */
.area-item,
.street-item {
  position: relative;
  overflow: hidden;
}

.area-item::after,
.street-item::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(25, 137, 250, 0.1);
  transform: translate(-50%, -50%);
  transition: width 0.3s ease, height 0.3s ease;
}

.area-item:active::after,
.street-item:active::after {
  width: 100px;
  height: 100px;
}
</style>
