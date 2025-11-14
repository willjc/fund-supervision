<template>
  <div class="institution-detail">
    <van-nav-bar title="机构详情" left-arrow @click-left="$router.back()" fixed placeholder>
      <template #right>
        <van-icon
          :name="detail.isFavorite ? 'like' : 'like-o'"
          size="20"
          :color="detail.isFavorite ? '#ee0a24' : '#333'"
          @click="toggleFavorite"
        />
      </template>
    </van-nav-bar>

    <div v-if="loading" class="loading-container">
      <van-loading size="24px">加载中...</van-loading>
    </div>

    <div v-else class="detail-content">
      <!-- 轮播图 -->
      <van-swipe class="swipe" :autoplay="3000" indicator-color="white">
        <van-swipe-item v-for="(image, index) in detail.images" :key="index" @click="previewImages(index)">
          <van-image :src="image" fit="cover" height="200" />
        </van-swipe-item>
      </van-swipe>

      <!-- 基本信息 -->
      <div class="info-card">
        <div class="title">{{ detail.name }}</div>

        <!-- 星级评分区域 -->
        <div class="rating-section">
          <van-rate v-model="detail.rating" :size="14" color="#ffd21e" void-icon="star" void-color="#eee" readonly />
          <span class="rating-info">{{ detail.establishDate }}</span>
          <span class="rating-info">建筑面积{{ detail.buildingArea }}m²</span>
        </div>

        <!-- 认证标签 -->
        <div v-if="detail.certificationTags && detail.certificationTags.length > 0" class="cert-tags">
          <van-tag
            v-for="(tag, index) in detail.certificationTags"
            :key="index"
            plain
            color="#FF6B00"
            text-color="#FF6B00"
            size="medium"
          >
            {{ tag }}
          </van-tag>
        </div>

        <!-- 三个设施卡片 -->
        <div class="facility-cards">
          <div class="facility-card" @click="showFacilityImages('room')">
            <div class="card-icon">🏠</div>
            <div class="card-title">房间设施</div>
            <div class="card-count">({{ detail.roomFacilities?.length || 0 }}) ></div>
          </div>
          <div class="facility-card" @click="showFacilityImages('basic')">
            <div class="card-icon">🏢</div>
            <div class="card-title">基础设施</div>
            <div class="card-count">({{ detail.basicFacilities?.length || 0 }}) ></div>
          </div>
          <div class="facility-card" @click="showFacilityImages('park')">
            <div class="card-icon">🌳</div>
            <div class="card-title">园址设施</div>
            <div class="card-count">({{ detail.parkFacilities?.length || 0 }}) ></div>
          </div>
        </div>

        <!-- 床位信息和地址电话栏 -->
        <div class="bed-contact-section">
          <!-- 床位信息 -->
          <div class="bed-info-box">
            <div class="bed-title">床位数</div>
            <div class="bed-value">{{ detail.availableBeds }}/{{ detail.totalBeds }}</div>
            <div class="bed-desc">可供预约{{ detail.availableBeds }}间,共计{{ detail.totalBeds }}间</div>
          </div>

          <!-- 地址电话栏 -->
          <div class="contact-box">
            <div class="address-text">{{ detail.address }}</div>
            <div class="contact-icons">
              <van-icon name="location-o" size="20" color="#4A9EFF" @click="showToast('地图功能开发中')" />
              <van-icon name="phone-o" size="20" color="#4A9EFF" @click="showToast('电话:' + detail.contactPhone)" />
            </div>
          </div>
        </div>

        <!-- 月参考价格卡片 -->
        <div class="price-card">
          <div class="price-label">月参考价格</div>
          <div class="price-range">¥{{ detail.monthlyPrice }}/月</div>
        </div>

        <!-- 费用信息 -->
        <div v-if="detail.priceList && detail.priceList.length > 0" class="fee-list">
          <div v-for="(item, index) in detail.priceList" :key="index" class="fee-item">
            <span class="fee-label">{{ item.name }}:</span>
            <span class="fee-value">¥{{ item.min }} - ¥{{ item.max }}</span>
          </div>
        </div>
      </div>

      <!-- Tab切换 -->
      <van-tabs v-model:active="activeTab" sticky offset-top="46">
        <van-tab title="机构介绍" name="intro">
          <!-- 生活设施 -->
          <van-cell-group v-if="detail.lifeFacilities && detail.lifeFacilities.length > 0" title="生活设施">
            <div class="facility-grid">
              <div v-for="(facility, index) in detail.lifeFacilities" :key="index" class="facility-item">
                <van-icon :name="facility.icon" size="28" color="#1989fa" />
                <span>{{ facility.name }}</span>
              </div>
            </div>
          </van-cell-group>

          <!-- 医疗设施 -->
          <van-cell-group v-if="detail.medicalFacilities && detail.medicalFacilities.length > 0" title="医疗设施">
            <div class="facility-grid">
              <div v-for="(facility, index) in detail.medicalFacilities" :key="index" class="facility-item">
                <van-icon :name="facility.icon" size="28" color="#07c160" />
                <span>{{ facility.name }}</span>
              </div>
            </div>
          </van-cell-group>

          <!-- 每日服务 -->
          <van-cell-group v-if="detail.dailyServices && detail.dailyServices.length > 0" title="每日服务">
            <div class="service-schedule">
              <div v-for="(service, index) in detail.dailyServices" :key="index" class="schedule-item">
                <div class="schedule-time">{{ service.time }}</div>
                <div class="schedule-content">{{ service.content }}</div>
              </div>
            </div>
          </van-cell-group>

          <!-- 机构介绍 -->
          <van-cell-group title="机构介绍">
            <div class="intro-content">
              {{ detail.description }}
            </div>
          </van-cell-group>
        </van-tab>

        <van-tab title="评价" name="review">
          <div class="review-summary">
            <div class="overall-rating">
              <div class="rating-score">{{ detail.rating }}</div>
              <van-rate v-model="detail.rating" :size="16" color="#ffd21e" void-icon="star" void-color="#eee" readonly />
              <div class="rating-count">{{ detail.reviewCount }}条评价</div>
            </div>
            <div class="rating-details">
              <div class="rating-item">
                <span class="rating-label">环境</span>
                <van-rate v-model="detail.ratingEnvironment" :size="14" color="#ffd21e" void-icon="star" void-color="#eee" readonly />
                <span class="rating-text">{{ detail.ratingEnvironment }}</span>
              </div>
              <div class="rating-item">
                <span class="rating-label">服务</span>
                <van-rate v-model="detail.ratingService" :size="14" color="#ffd21e" void-icon="star" void-color="#eee" readonly />
                <span class="rating-text">{{ detail.ratingService }}</span>
              </div>
              <div class="rating-item">
                <span class="rating-label">价格</span>
                <van-rate v-model="detail.ratingPrice" :size="14" color="#ffd21e" void-icon="star" void-color="#eee" readonly />
                <span class="rating-text">{{ detail.ratingPrice }}</span>
              </div>
            </div>
          </div>

          <!-- 评价列表 -->
          <div v-if="detail.reviews && detail.reviews.length > 0" class="review-list">
            <div v-for="(review, index) in detail.reviews" :key="index" class="review-item">
              <div class="review-header">
                <van-image
                  round
                  width="40"
                  height="40"
                  :src="review.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
                />
                <div class="review-user">
                  <div class="user-name">{{ review.userName }}</div>
                  <van-rate v-model="review.rating" :size="12" color="#ffd21e" void-icon="star" void-color="#eee" readonly />
                </div>
                <div class="review-date">{{ review.createTime }}</div>
              </div>
              <div class="review-content">{{ review.content }}</div>
              <div v-if="review.images && review.images.length > 0" class="review-images">
                <van-image
                  v-for="(img, imgIndex) in review.images"
                  :key="imgIndex"
                  width="80"
                  height="80"
                  :src="img"
                  fit="cover"
                />
              </div>
            </div>
          </div>

          <van-empty v-if="!detail.reviews || detail.reviews.length === 0" description="暂无评价" />
        </van-tab>
      </van-tabs>
    </div>

    <!-- 底部操作栏 -->
    <div class="action-bar">
      <van-button type="default" size="small" @click="freeTrial">
        免费试住
      </van-button>
      <van-button type="warning" size="small" @click="bookVisit">
        预约参观
      </van-button>
      <van-button type="primary" size="small" @click="applyEnter">
        申请入住
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showDialog, showImagePreview } from 'vant'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const activeTab = ref('intro')

// 模拟机构详情数据
const mockDetail = {
  institutionId: 1,
  name: '郑州市金水区花园口社区养老服务中心',
  institutionType: '养老院',
  address: '郑州市金水区花园口镇花园路233号',
  contactPhone: '0371-12345678',
  description: '本机构是经郑州市民政局批准成立的综合性养老服务机构，占地面积5000平方米，建筑面积3000平方米。拥有专业的护理团队和完善的医疗设施，致力于为老年人提供优质的养老服务。机构环境优美，设施齐全，交通便利，是老年人安享晚年的理想之所。',
  images: [
    '/images/banners/banner1.jpg',
    '/images/banners/banner2.jpg',
    '/images/banners/banner3.jpg'
  ],
  videos: [], // 视频列表
  totalBeds: 50,
  availableBeds: 8,
  buildingArea: 1000, // 建筑面积
  establishDate: '2024年3月', // 建立日期
  certificationTags: ['公办养老', '自理', '失智', '失能', '内设医疗机构', '试点机构无忧退费'], // 认证标签
  monthlyPrice: '2800-3500', // 月参考价格
  isFavorite: false, // 是否收藏
  lifeFacilities: [
    { icon: 'service-o', name: '24小时热水' },
    { icon: 'fire-o', name: '中央空调' },
    { icon: 'tv-o', name: '有线电视' },
    { icon: 'wap-home-o', name: '无线网络' },
    { icon: 'home-o', name: '独立卫浴' },
    { icon: 'clock-o', name: '紧急呼叫' },
    { icon: 'shopping-cart-o', name: '洗衣服务' },
    { icon: 'friends-o', name: '活动室' }
  ],
  medicalFacilities: [
    { icon: 'medal-o', name: '医疗室' },
    { icon: 'cluster-o', name: '康复室' },
    { icon: 'manager-o', name: '理疗室' },
    { icon: 'chart-trending-o', name: '健康监测' }
  ],
  // 三个设施卡片数据
  roomFacilities: [
    { name: '单人间', image: 'https://via.placeholder.com/300x200/4A90E2/FFFFFF?text=单人间' },
    { name: '双人间', image: 'https://via.placeholder.com/300x200/7ED321/FFFFFF?text=双人间' },
    { name: '三人间', image: 'https://via.placeholder.com/300x200/F5A623/FFFFFF?text=三人间' },
    { name: '套房', image: 'https://via.placeholder.com/300x200/BD10E0/FFFFFF?text=套房' },
    { name: 'VIP房', image: 'https://via.placeholder.com/300x200/FF6B6B/FFFFFF?text=VIP房' },
    { name: '夫妻房', image: 'https://via.placeholder.com/300x200/4ECDC4/FFFFFF?text=夫妻房' },
    { name: '护理房', image: 'https://via.placeholder.com/300x200/95E1D3/FFFFFF?text=护理房' },
    { name: '观察房', image: 'https://via.placeholder.com/300x200/F38181/FFFFFF?text=观察房' },
    { name: '康复房', image: 'https://via.placeholder.com/300x200/AA96DA/FFFFFF?text=康复房' },
    { name: '临终关怀房', image: 'https://via.placeholder.com/300x200/FCBAD3/FFFFFF?text=临终关怀房' }
  ],
  basicFacilities: [
    { name: '餐厅', image: 'https://via.placeholder.com/300x200/4A90E2/FFFFFF?text=餐厅' },
    { name: '活动室', image: 'https://via.placeholder.com/300x200/7ED321/FFFFFF?text=活动室' },
    { name: '阅览室', image: 'https://via.placeholder.com/300x200/F5A623/FFFFFF?text=阅览室' },
    { name: '棋牌室', image: 'https://via.placeholder.com/300x200/BD10E0/FFFFFF?text=棋牌室' },
    { name: '健身房', image: 'https://via.placeholder.com/300x200/FF6B6B/FFFFFF?text=健身房' },
    { name: '理发室', image: 'https://via.placeholder.com/300x200/4ECDC4/FFFFFF?text=理发室' },
    { name: '洗衣房', image: 'https://via.placeholder.com/300x200/95E1D3/FFFFFF?text=洗衣房' },
    { name: '接待室', image: 'https://via.placeholder.com/300x200/F38181/FFFFFF?text=接待室' },
    { name: '会议室', image: 'https://via.placeholder.com/300x200/AA96DA/FFFFFF?text=会议室' },
    { name: '多功能厅', image: 'https://via.placeholder.com/300x200/FCBAD3/FFFFFF?text=多功能厅' }
  ],
  parkFacilities: [],
  priceList: [
    { name: '试住费', min: 1500, max: 3500 },
    { name: '休假费', min: 500, max: 500 },
    { name: '服务费', min: 400, max: 1500 },
    { name: '膳食费', min: 600, max: 1500 }
  ],
  dailyServices: [
    { time: '06:30', content: '晨间护理、测量生命体征' },
    { time: '07:00', content: '早餐时间' },
    { time: '08:30', content: '晨间活动、健身操' },
    { time: '10:00', content: '上午茶点、休闲活动' },
    { time: '11:30', content: '午餐时间' },
    { time: '12:30', content: '午休时间' },
    { time: '14:30', content: '下午活动、文娱节目' },
    { time: '17:00', content: '晚餐时间' },
    { time: '18:30', content: '晚间散步、娱乐活动' },
    { time: '21:00', content: '晚间护理、就寝准备' }
  ],
  rating: 4.5,
  reviewCount: 128,
  ratingEnvironment: 4.8,
  ratingService: 4.6,
  ratingPrice: 4.2,
  reviews: [
    {
      userName: '张女士',
      avatar: '',
      rating: 5,
      createTime: '2025-01-10',
      content: '环境很好，服务人员态度也很好，老人住得很开心。设施齐全，每天都有丰富的活动，护理人员专业细心。',
      images: [
        'https://via.placeholder.com/100x100/4A90E2/FFFFFF?text=图1',
        'https://via.placeholder.com/100x100/7ED321/FFFFFF?text=图2'
      ]
    },
    {
      userName: '李先生',
      avatar: '',
      rating: 4,
      createTime: '2025-01-08',
      content: '整体不错，饮食营养均衡，医护人员很专业。唯一的建议是希望能增加一些户外活动。',
      images: []
    },
    {
      userName: '王女士',
      avatar: '',
      rating: 5,
      createTime: '2025-01-05',
      content: '非常满意！父亲在这里住了半年，身体状况明显改善。工作人员都很有爱心，把老人照顾得很好。',
      images: [
        'https://via.placeholder.com/100x100/F5A623/FFFFFF?text=图1'
      ]
    }
  ]
}

const detail = ref({})

// 收藏切换
const toggleFavorite = async () => {
  try {
    if (detail.value.isFavorite) {
      // TODO: 调用取消收藏API
      // await unfavoriteInstitution(detail.value.institutionId)
      detail.value.isFavorite = false
      showToast('已取消收藏')
    } else {
      // TODO: 调用收藏API
      // await favoriteInstitution(detail.value.institutionId)
      detail.value.isFavorite = true
      showToast('收藏成功')
    }
  } catch (error) {
    showToast('操作失败')
  }
}

// 显示设施图片
const showFacilityImages = (type) => {
  let facilities = []
  let title = ''

  if (type === 'room') {
    facilities = detail.value.roomFacilities
    title = '房间设施'
  } else if (type === 'basic') {
    facilities = detail.value.basicFacilities
    title = '基础设施'
  } else if (type === 'park') {
    facilities = detail.value.parkFacilities
    title = '园址设施'
  }

  if (facilities.length === 0) {
    showToast('暂无设施图片')
    return
  }

  const images = facilities.map(f => f.image)
  showImagePreview({
    images,
    closeable: true
  })
}

// 轮播图点击放大
const previewImages = (startIndex) => {
  showImagePreview({
    images: detail.value.images,
    startPosition: startIndex,
    closeable: true
  })
}

// 免费试住
const freeTrial = () => {
  router.push({
    name: 'FreeTrialApply',
    params: { institutionId: route.params.id }
  })
}

// 预约参观
const bookVisit = () => {
  router.push({
    name: 'AppointmentBooking',
    params: { institutionId: route.params.id }
  })
}

// 申请入住
const applyEnter = () => {
  router.push({
    name: 'OrderConfirm',
    params: { institutionId: route.params.id }
  })
}

// 加载详情
const loadDetail = async () => {
  try {
    loading.value = true

    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    // 使用模拟数据
    detail.value = mockDetail
  } catch (error) {
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.institution-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 60px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.swipe {
  height: 200px;
}

.info-card {
  background: #fff;
  padding: 16px;
  margin-bottom: 10px;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

/* 星级评分区域 */
.rating-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.rating-info {
  font-size: 12px;
  color: #999;
}

/* 认证标签 */
.cert-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

/* 三个设施卡片 */
.facility-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 16px;
}

.facility-card {
  background: linear-gradient(135deg, #4A9EFF 0%, #67B5FF 100%);
  border-radius: 8px;
  padding: 12px 8px;
  text-align: center;
  color: white;
  box-shadow: 0 2px 8px rgba(74, 158, 255, 0.3);
  cursor: pointer;
  transition: transform 0.2s;
}

.facility-card:active {
  transform: scale(0.95);
}

.card-icon {
  font-size: 24px;
  margin-bottom: 6px;
}

.card-title {
  font-size: 13px;
  margin-bottom: 4px;
  font-weight: 500;
}

.card-count {
  font-size: 12px;
  opacity: 0.9;
}

/* 床位和地址联合区域 */
.bed-contact-section {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.bed-info-box {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  color: white;
}

.bed-title {
  font-size: 13px;
  margin-bottom: 8px;
  opacity: 0.9;
}

.bed-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 6px;
}

.bed-desc {
  font-size: 12px;
  opacity: 0.8;
}

.contact-box {
  flex: 1;
  background: #f7f8fa;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.address-text {
  font-size: 13px;
  color: #333;
  line-height: 1.5;
  margin-bottom: 8px;
  flex: 1;
  display: flex;
  align-items: center;
}

.contact-icons {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.contact-icons .van-icon {
  cursor: pointer;
}

/* 月参考价格卡片 */
.price-card {
  background: linear-gradient(135deg, #FF6B00 0%, #FF8F40 100%);
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(255, 107, 0, 0.25);
}

.price-label {
  font-size: 15px;
  color: white;
  font-weight: 500;
}

.price-range {
  font-size: 24px;
  color: white;
  font-weight: bold;
}

/* 费用信息列表 */
.fee-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.fee-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.fee-label {
  font-size: 12px;
  color: #999;
}

.fee-value {
  font-size: 14px;
  color: #4A9EFF;
  font-weight: 500;
}

/* 设施网格 */
.facility-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 16px;
}

.facility-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.facility-item span {
  font-size: 12px;
  color: #666;
  text-align: center;
}

/* 每日服务 */
.service-schedule {
  padding: 16px;
}

.schedule-item {
  display: flex;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.schedule-item:last-child {
  border-bottom: none;
}

.schedule-time {
  flex-shrink: 0;
  width: 60px;
  font-size: 14px;
  font-weight: 500;
  color: #1989fa;
}

.schedule-content {
  flex: 1;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

/* 机构介绍 */
.intro-content {
  padding: 16px;
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  text-align: justify;
}

/* 评价汇总 */
.review-summary {
  background: #fff;
  padding: 16px;
  margin-bottom: 10px;
}

.overall-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #f5f5f5;
}

.rating-score {
  font-size: 48px;
  font-weight: bold;
  color: #ffd21e;
  line-height: 1;
  margin-bottom: 8px;
}

.rating-count {
  font-size: 13px;
  color: #999;
  margin-top: 8px;
}

.rating-details {
  padding-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rating-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rating-label {
  width: 50px;
  font-size: 14px;
  color: #666;
}

.rating-text {
  font-size: 14px;
  color: #ffd21e;
  font-weight: 500;
  margin-left: 8px;
}

/* 评价列表 */
.review-list {
  background: #fff;
}

.review-item {
  padding: 16px;
  border-bottom: 10px solid #f5f5f5;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.review-user {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.review-date {
  font-size: 12px;
  color: #999;
}

.review-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.review-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* 底部操作栏 */
.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 8px;
  padding: 8px 12px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

.action-bar .van-button {
  flex: 1;
}
</style>
