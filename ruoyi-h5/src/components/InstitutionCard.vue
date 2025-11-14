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
          <span>开设: 8-50 (可容纳50人/运营机构)</span>
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
          <span class="price-name">护理费:</span>
          <span class="price-range">¥ {{ institution.priceRanges.nursing.min }} ~ ¥ {{ institution.priceRanges.nursing.max }}</span>
        </div>
        <div class="price-item">
          <span class="price-name">伙食费:</span>
          <span class="price-range">¥ {{ institution.priceRanges.meal.min }} ~ ¥ {{ institution.priceRanges.meal.max }}</span>
        </div>
        <div class="price-item">
          <span class="price-name">床位费:</span>
          <span class="price-range">¥ {{ institution.priceRanges.bed.min }} ~ ¥ {{ institution.priceRanges.bed.max }}</span>
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
  border-radius: 4px;
  overflow: hidden;
  margin: 0 0 12px;
  border: 1px solid #e5e5e5;
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
  background: #f5f5f5;
  border-radius: 4px;
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
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 12px;
  text-align: center;
  padding: 4px 0;
}

.card-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.card-title {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.card-meta {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

.card-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  font-size: 12px;
  color: #666;
  line-height: 1.5;
}

.info-item .van-icon {
  margin-right: 3px;
  margin-top: 1px;
  color: #999;
  flex-shrink: 0;
}

.info-item span {
  flex: 1;
  word-break: break-all;
}

.card-price {
  background: #e8f4f8;
  padding: 10px 12px;
  border-top: 1px solid #e5e5e5;
}

.price-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 8px;
}

.price-item {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  flex: 0 0 calc(50% - 4px);
}

.price-name {
  color: #666;
  white-space: nowrap;
  flex-shrink: 0;
  font-size: 12px;
}

.price-range {
  color: #1989fa;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 12px;
}
</style>
