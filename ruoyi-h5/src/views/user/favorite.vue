<template>
  <div class="user-favorite-page">

    <div class="favorite-content">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <!-- 复用机构卡片组件 -->
          <div v-for="institution in favoriteList" :key="institution.favoriteId" class="favorite-item">
            <InstitutionCard :institution="institution" />
            <div class="favorite-actions">
              <van-button plain type="danger" size="small" @click="cancelFavorite(institution)">
                取消收藏
              </van-button>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>

      <van-empty v-if="!loading && favoriteList.length === 0" description="暂无收藏记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import InstitutionCard from '@/components/InstitutionCard.vue'

const router = useRouter()

const favoriteList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)

// 模拟收藏数据
const mockFavorites = [
  {
    favoriteId: 1,
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
    },
    favoriteTime: '2025-01-10'
  },
  {
    favoriteId: 2,
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
    },
    favoriteTime: '2025-01-08'
  },
  {
    favoriteId: 3,
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
    },
    favoriteTime: '2025-01-05'
  }
]

// 下拉刷新
const onRefresh = () => {
  pageNum.value = 1
  finished.value = false
  favoriteList.value = []
  onLoad()
  refreshing.value = false
}

// 加载列表
const onLoad = async () => {
  try {
    loading.value = true

    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 500))

    // 分页处理
    const startIndex = (pageNum.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    const list = mockFavorites.slice(startIndex, endIndex)

    if (list.length === 0) {
      finished.value = true
    } else {
      favoriteList.value = [...favoriteList.value, ...list]
      pageNum.value++

      if (endIndex >= mockFavorites.length) {
        finished.value = true
      }
    }
  } catch (error) {
    showToast('加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 取消收藏
const cancelFavorite = async (institution) => {
  try {
    await showConfirmDialog({
      title: '提示',
      message: '确定要取消收藏吗?'
    })

    // 模拟取消收藏
    await new Promise(resolve => setTimeout(resolve, 500))

    showToast('已取消收藏')

    // 从列表中移除
    const index = favoriteList.value.findIndex(item => item.favoriteId === institution.favoriteId)
    if (index > -1) {
      favoriteList.value.splice(index, 1)
    }
  } catch (error) {
    if (error !== 'cancel') {
      showToast('取消收藏失败')
    }
  }
}

onMounted(() => {
  // 初始加载
})
</script>

<style scoped>
.user-favorite-page {
  min-height: 100vh;
  background-color: #f5f6fc;
  font-family: 'PingFang SC', '苹方-简', sans-serif;
}

.favorite-content {
  padding: 12px;
  padding-bottom: 20px;
}

.favorite-item {
  position: relative;
  margin-bottom: 12px;
}

.favorite-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 10;
}

.favorite-actions :deep(.van-button) {
  background: rgba(255, 255, 255, 0.95);
  border-color: #ff9f9f;
  color: #ff4d4f;
  border-radius: 16px;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.15);
  padding: 0 12px;
  height: 28px;
  font-size: 12px;
}

/* 空状态样式 */
.favorite-content :deep(.van-empty) {
  padding: 60px 0;
}

.favorite-content :deep(.van-empty__description) {
  color: #999;
  font-size: 14px;
}

/* 列表样式 */
.favorite-content :deep(.van-list__finished-text) {
  color: #999;
  font-size: 13px;
  line-height: 50px;
}

/* 下拉刷新样式 */
.favorite-content :deep(.van-pull-refresh__track) {
  min-height: calc(100vh - 46px);
}
</style>
