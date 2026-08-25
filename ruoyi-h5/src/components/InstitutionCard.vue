<template>
  <article
    class="institution-card"
    role="button"
    tabindex="0"
    :aria-label="`查看${institutionName}详情`"
    @click="handleSelect"
    @keydown.enter="handleSelect"
    @keydown.space.prevent="handleSelect"
  >
    <div class="institution-card__media">
      <img
        class="institution-card__image"
        :src="institution.coverImage || institutionPlaceholder"
        :alt="`${institutionName}机构图片`"
        loading="lazy"
        @error="handleImageError"
      />
      <span
        class="institution-card__status"
        :class="`institution-card__status--${availabilityState}`"
      >
        {{ availabilityText }}
      </span>
    </div>

    <div class="institution-card__body">
      <div class="institution-card__heading">
        <h3 class="institution-card__name">{{ institutionName }}</h3>
        <span v-if="natureText" class="institution-card__nature">{{ natureText }}</span>
      </div>

      <div class="institution-card__facts">
        <span class="institution-card__fact">
          <van-icon name="hotel-o" aria-hidden="true" />
          {{ bedSummary }}
        </span>
        <span v-if="ratingText" class="institution-card__fact">
          <van-icon name="star-o" aria-hidden="true" />
          {{ ratingText }}
        </span>
      </div>

      <div class="institution-card__address">
        <van-icon name="location-o" aria-hidden="true" />
        <span>{{ shortAddress }}</span>
      </div>

      <div v-if="displayTags.length" class="institution-card__tags" aria-label="机构特色">
        <span v-for="tag in displayTags" :key="tag" class="institution-card__tag">
          {{ tag }}
        </span>
      </div>

      <div class="institution-card__footer">
        <div class="institution-card__price">
          <template v-if="minimumPrice > 0">
            <span class="institution-card__price-symbol">¥</span>
            <strong>{{ minimumPrice }}</strong>
            <span class="institution-card__price-unit">/月起</span>
          </template>
          <span v-else class="institution-card__price-pending">价格面议</span>
        </div>
        <span class="institution-card__link">查看详情 <van-icon name="arrow" /></span>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import institutionPlaceholder from '@/assets/images/institution-placeholder.svg'

const props = defineProps({
  institution: {
    type: Object,
    required: true
  },
  navigateOnClick: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['select'])
const router = useRouter()

const institutionName = computed(() => (
  props.institution.institutionName || props.institution.name || '未命名机构'
))

const availabilityState = computed(() => {
  const availableBeds = props.institution.availableBeds
  if (availableBeds === undefined || availableBeds === null) return 'unknown'

  const normalizedBeds = Number(availableBeds)
  if (!Number.isFinite(normalizedBeds) || normalizedBeds < 0) return 'unknown'
  return normalizedBeds > 0 ? 'available' : 'full'
})

const availabilityText = computed(() => {
  const textMap = {
    available: '有床位',
    full: '暂无床位',
    unknown: '床位待确认'
  }
  return textMap[availabilityState.value]
})

const bedSummary = computed(() => {
  const availableBeds = Number(props.institution.availableBeds)
  const totalBeds = Number(
    props.institution.totalBeds || props.institution.bedCount || 0
  )

  if (availabilityState.value === 'unknown') {
    return totalBeds > 0 ? `共${totalBeds}床 · 余量待确认` : '床位信息待确认'
  }
  if (availabilityState.value === 'available' && totalBeds > 0) {
    return `余${availableBeds}床 · 共${totalBeds}床`
  }
  if (availabilityState.value === 'available') return `余${availableBeds}床`
  return totalBeds > 0 ? `共${totalBeds}床 · 当前无空余` : '当前暂无空余床位'
})

const natureText = computed(() => {
  const natureMap = {
    '1': '民办',
    '2': '公办',
    '3': '公建民营'
  }
  return props.institution.natureText || natureMap[props.institution.institutionNature] || ''
})

const ratingText = computed(() => {
  const rating = Number(props.institution.ratingLevel || 0)
  return rating > 0 ? `${rating}星机构` : ''
})

const shortAddress = computed(() => {
  const address = props.institution.address || '地址信息完善中'
  return address.length > 36 ? `${address.slice(0, 36)}…` : address
})

const displayTags = computed(() => {
  const source = props.institution.tags || props.institution.lifeFacilities || []
  return source
    .map((tag) => (typeof tag === 'string' ? tag : tag?.name))
    .filter(Boolean)
    .slice(0, 3)
})

const minimumPrice = computed(() => Number(
  props.institution.minPrice ||
  props.institution.priceRanges?.total?.min ||
  props.institution.priceRanges?.bed?.min ||
  props.institution.priceRangeMin ||
  0
))

const handleImageError = (event) => {
  if (event.target.dataset.fallbackApplied) return
  event.target.dataset.fallbackApplied = 'true'
  event.target.src = institutionPlaceholder
}

const handleSelect = () => {
  emit('select', props.institution)
  if (!props.navigateOnClick) return

  router.push({
    name: 'InstitutionDetail',
    params: { id: props.institution.institutionId }
  })
}
</script>

<style scoped>
.institution-card {
  display: flex;
  gap: var(--h5-space-3);
  width: 100%;
  min-height: 146px;
  padding: var(--h5-space-3);
  overflow: hidden;
  color: var(--h5-color-text);
  background: var(--h5-color-surface);
  border: 1px solid var(--h5-color-divider);
  border-radius: var(--h5-radius-lg);
  box-shadow: var(--h5-shadow-sm);
  cursor: pointer;
  transition:
    transform var(--h5-motion-fast) var(--h5-ease-standard),
    box-shadow var(--h5-motion-fast) var(--h5-ease-standard);
}

.institution-card:active {
  box-shadow: var(--h5-shadow-xs);
  transform: translateY(1px);
}

.institution-card:focus {
  outline: 2px solid var(--h5-color-primary);
  outline-offset: 2px;
}

.institution-card__media {
  position: relative;
  flex: 0 0 112px;
  height: 112px;
  overflow: hidden;
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-md);
}

.institution-card__image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.institution-card__status {
  position: absolute;
  right: var(--h5-space-2);
  bottom: var(--h5-space-2);
  min-height: 24px;
  padding: 2px var(--h5-space-2);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  font-weight: var(--h5-font-weight-medium);
  line-height: 20px;
  background: rgba(255, 255, 255, 0.92);
  border-radius: var(--h5-radius-pill);
  box-shadow: var(--h5-shadow-xs);
}

.institution-card__status--available {
  color: var(--h5-color-success);
  background: rgba(234, 247, 241, 0.94);
}

.institution-card__status--full {
  color: var(--h5-color-danger);
  background: rgba(255, 240, 241, 0.94);
}

.institution-card__status--unknown {
  color: var(--h5-color-pending);
  background: rgba(255, 248, 232, 0.94);
}

.institution-card__body {
  display: flex;
  flex: 1;
  flex-direction: column;
  min-width: 0;
}

.institution-card__heading {
  display: flex;
  align-items: flex-start;
  gap: var(--h5-space-2);
}

.institution-card__name {
  display: -webkit-box;
  flex: 1;
  overflow: hidden;
  color: var(--h5-color-text);
  font-size: var(--h5-font-size-lg);
  font-weight: var(--h5-font-weight-semibold);
  line-height: 1.4;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.institution-card__nature {
  flex: 0 0 auto;
  min-height: 24px;
  padding: 2px var(--h5-space-2);
  color: var(--h5-color-primary);
  font-size: var(--h5-font-size-sm);
  line-height: 20px;
  background: var(--h5-color-primary-soft);
  border-radius: var(--h5-radius-pill);
}

.institution-card__facts {
  display: flex;
  flex-wrap: wrap;
  gap: var(--h5-space-2) var(--h5-space-3);
  margin-top: var(--h5-space-1);
}

.institution-card__fact,
.institution-card__address {
  display: flex;
  align-items: center;
  gap: var(--h5-space-1);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  line-height: 20px;
}

.institution-card__fact .van-icon,
.institution-card__address .van-icon {
  flex: 0 0 auto;
  color: var(--h5-color-primary);
  font-size: 15px;
}

.institution-card__address {
  min-width: 0;
  margin-top: var(--h5-space-1);
}

.institution-card__address span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.institution-card__tags {
  display: flex;
  gap: var(--h5-space-1);
  min-width: 0;
  margin-top: var(--h5-space-2);
  overflow: hidden;
}

.institution-card__tag {
  flex: 0 0 auto;
  min-height: 24px;
  padding: 2px var(--h5-space-2);
  color: var(--h5-color-text-secondary);
  font-size: var(--h5-font-size-sm);
  line-height: 20px;
  background: var(--h5-color-surface-subtle);
  border: 1px solid var(--h5-color-divider);
  border-radius: var(--h5-radius-pill);
}

.institution-card__footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: var(--h5-space-2);
  margin-top: auto;
  padding-top: var(--h5-space-2);
}

.institution-card__price {
  display: flex;
  align-items: baseline;
  min-width: 0;
  color: var(--h5-color-danger);
}

.institution-card__price strong {
  font-size: var(--h5-font-size-xl);
  font-weight: var(--h5-font-weight-bold);
  line-height: 1;
}

.institution-card__price-symbol,
.institution-card__price-unit,
.institution-card__price-pending,
.institution-card__link {
  font-size: var(--h5-font-size-sm);
}

.institution-card__price-symbol {
  margin-right: 2px;
  font-weight: var(--h5-font-weight-semibold);
}

.institution-card__price-unit {
  margin-left: 2px;
  color: var(--h5-color-text-secondary);
}

.institution-card__price-pending {
  color: var(--h5-color-text-secondary);
  font-weight: var(--h5-font-weight-medium);
}

.institution-card__link {
  display: inline-flex;
  flex: 0 0 auto;
  align-items: center;
  gap: 2px;
  color: var(--h5-color-primary);
  font-weight: var(--h5-font-weight-medium);
}

@media (max-width: 360px) {
  .institution-card__media {
    flex-basis: 96px;
    height: 108px;
  }

  .institution-card__link {
    display: none;
  }
}
</style>
