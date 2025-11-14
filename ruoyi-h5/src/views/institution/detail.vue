<template>
  <div class="institution-detail">
    <van-nav-bar title="机构详情" left-arrow @click-left="$router.back()" fixed placeholder />

    <div v-if="loading" class="loading-container">
      <van-loading size="24px">加载中...</van-loading>
    </div>

    <div v-else class="detail-content">
      <!-- 轮播图 -->
      <van-swipe class="swipe" :autoplay="3000" indicator-color="white">
        <van-swipe-item v-for="(image, index) in detail.images" :key="index">
          <van-image :src="image" fit="cover" height="200" />
        </van-swipe-item>
      </van-swipe>

      <!-- 基本信息 -->
      <div class="info-card">
        <div class="title">{{ detail.name }}</div>
        <div class="tags">
          <van-tag type="primary">{{ detail.institutionType }}</van-tag>
          <van-tag plain>床位 {{ detail.bedCount }}</van-tag>
        </div>
        <div class="price">
          <span class="label">起价:</span>
          <span class="value">¥{{ formatPrice(detail.price) }}</span>
          <span class="unit">/月</span>
        </div>
      </div>

      <!-- 联系方式 -->
      <van-cell-group title="联系方式">
        <van-cell title="联系电话" :value="detail.contactPhone" is-link @click="callPhone" />
        <van-cell title="机构地址" :value="detail.address" is-link @click="viewMap" />
      </van-cell-group>

      <!-- 机构介绍 -->
      <van-cell-group title="机构介绍">
        <div class="intro-content">
          {{ detail.description || '暂无介绍' }}
        </div>
      </van-cell-group>

      <!-- 服务项目 -->
      <van-cell-group title="服务项目">
        <div class="service-list">
          <van-tag
            v-for="(service, index) in detail.services"
            :key="index"
            type="primary"
            plain
            size="large"
          >
            {{ service }}
          </van-tag>
        </div>
      </van-cell-group>

      <!-- 设施环境 -->
      <van-cell-group title="设施环境">
        <div class="facility-grid">
          <div v-for="(facility, index) in detail.facilities" :key="index" class="facility-item">
            <van-icon :name="facility.icon" size="24" />
            <span>{{ facility.name }}</span>
          </div>
        </div>
      </van-cell-group>
    </div>

    <!-- 底部操作栏 -->
    <div class="action-bar">
      <van-button type="default" size="small" @click="callPhone">
        <van-icon name="phone-o" />
        电话咨询
      </van-button>
      <van-button type="primary" size="large" @click="applyEnter">
        申请入住
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { getInstitutionDetail } from '@/api/institution'
import { formatMoney } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const detail = ref({
  name: '',
  institutionType: '',
  bedCount: 0,
  price: 0,
  contactPhone: '',
  address: '',
  description: '',
  images: [],
  services: [],
  facilities: []
})

// 格式化价格
const formatPrice = (price) => {
  return formatMoney(price)
}

// 拨打电话
const callPhone = () => {
  if (detail.value.contactPhone) {
    window.location.href = `tel:${detail.value.contactPhone}`
  } else {
    showToast('暂无联系电话')
  }
}

// 查看地图
const viewMap = () => {
  showToast('地图功能开发中')
}

// 申请入住
const applyEnter = () => {
  showDialog({
    title: '申请入住',
    message: '是否申请入住该养老机构?',
    showCancelButton: true
  }).then(() => {
    showToast('申请成功')
  }).catch(() => {
    // 取消
  })
}

// 加载详情
const loadDetail = async () => {
  try {
    loading.value = true
    const res = await getInstitutionDetail(route.params.id)

    if (res.code === 200) {
      detail.value = res.data

      // 如果没有图片,使用默认图片
      if (!detail.value.images || detail.value.images.length === 0) {
        detail.value.images = ['https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg']
      }

      // 如果没有服务项目,使用默认
      if (!detail.value.services || detail.value.services.length === 0) {
        detail.value.services = ['24小时护理', '营养餐饮', '康复理疗', '文娱活动']
      }

      // 如果没有设施,使用默认
      if (!detail.value.facilities || detail.value.facilities.length === 0) {
        detail.value.facilities = [
          { icon: 'service-o', name: '医疗室' },
          { icon: 'friends-o', name: '活动室' },
          { icon: 'home-o', name: '单人间' },
          { icon: 'fire-o', name: '餐厅' }
        ]
      }
    }
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
  margin-bottom: 8px;
}

.tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.price {
  display: flex;
  align-items: baseline;
}

.price .label {
  font-size: 14px;
  color: #999;
  margin-right: 4px;
}

.price .value {
  font-size: 20px;
  font-weight: bold;
  color: #ff6b6b;
}

.price .unit {
  font-size: 14px;
  color: #999;
  margin-left: 2px;
}

.intro-content {
  padding: 16px;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.service-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 16px;
}

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
}

.action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 12px;
  padding: 12px;
  background: #fff;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

.action-bar .van-button:first-child {
  flex: 0 0 auto;
}

.action-bar .van-button:last-child {
  flex: 1;
}
</style>
