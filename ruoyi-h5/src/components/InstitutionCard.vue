<template>
  <div class="institution-card" @click="goToDetail">
    <div class="card-main">
      <div class="card-image">
        <van-image
          :src="institution.coverImage || defaultImage"
          fit="cover"
          width="100"
          height="100"
        />
        <div class="image-placeholder">机构图片</div>
      </div>

      <div class="card-content">
        <div class="card-title">{{ institution.institutionName || institution.name }}</div>

        <div class="card-meta">
          <span>床位数：{{ institution.availableBeds || 0 }}/{{ institution.totalBeds || 0 }}(可定床位数/总床位数)</span>
        </div>

        <div class="card-info">
          <div class="info-item">
            <van-icon name="location-o" size="14" />
            <span>详细地址: {{ getShortAddress(institution.address) }}</span>
          </div>
          <div class="info-item">
            <van-icon name="phone-o" size="14" />
            <span>联系电话: {{ institution.contactPhone }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-if="institution.priceRanges" class="card-price">
      <div class="price-grid">
        <div class="price-item">
          <span class="price-name">总费用:</span>
          <span class="price-range">¥ {{ institution.priceRanges.total.min }} ~ ¥ {{ institution.priceRanges.total.max }}</span>
        </div>
        <div class="price-item">
          <span class="price-name">床位费:</span>
          <span class="price-range">¥ {{ institution.priceRanges.bed.min }} ~ ¥ {{ institution.priceRanges.bed.max }}</span>
        </div>
        <div class="price-item">
          <span class="price-name">护理费:</span>
          <span class="price-range">¥ {{ institution.priceRanges.nursing.min }} ~ ¥ {{ institution.priceRanges.nursing.max }}</span>
        </div>
        <div class="price-item">
          <span class="price-name">膳食费:</span>
          <span class="price-range">¥ {{ institution.priceRanges.diet.min }} ~ ¥ {{ institution.priceRanges.diet.max }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  institution: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const defaultImage = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

// 跳转到详情页
const goToDetail = () => {
  router.push({
    name: 'InstitutionDetail',
    params: { id: props.institution.institutionId }
  })
}

// 截取地址
const getShortAddress = (address) => {
  if (!address) return '地址未提供'
  // 截取前30个字符
  return address.length > 30 ? address.substring(0, 30) + '...' : address
}
</script>

<style scoped>
.institution-card {
  background: #fff;
  border-radius: 10px;
  overflow: hidden;
  margin: 0 0 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  font-family: 'PingFang SC', '苹方-简', sans-serif;
  transition: all 0.2s ease;
}

.institution-card:active {
  transform: scale(0.99);
}

.card-main {
  display: flex;
  padding: 12px;
  gap: 12px;
}

.card-image {
  position: relative;
  flex-shrink: 0;
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, #f5f6fc 0%, #e9ecef 100%);
  border-radius: 8px;
  overflow: hidden;
}

.card-image :deep(.van-image) {
  display: block;
  width: 100%;
  height: 100%;
}

.image-placeholder {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(18, 129, 255, 0.7);
  color: #fff;
  font-size: 11px;
  text-align: center;
  padding: 3px 0;
  font-weight: 500;
}

.card-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.card-title {
  font-size: 15px;
  font-weight: 500;
  color: #1a1a1a;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.card-meta {
  font-size: 12px;
  color: #1281ff;
  line-height: 1.4;
  font-weight: 500;
}

.card-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  font-size: 12px;
  color: #666;
  line-height: 1.5;
}

.info-item .van-icon {
  margin-right: 4px;
  margin-top: 1px;
  color: #1281ff;
  flex-shrink: 0;
}

.info-item span {
  flex: 1;
  word-break: break-all;
}

.card-price {
  background: linear-gradient(135deg, #ebf6ff 0%, #f0f9ff 100%);
  padding: 12px;
  border-top: none;
}

.price-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 12px;
}

.price-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  flex: 0 0 calc(50% - 6px);
}

.price-name {
  color: #666;
  white-space: nowrap;
  flex-shrink: 0;
  font-size: 12px;
}

.price-range {
  color: #1281ff;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 12px;
}
</style>
