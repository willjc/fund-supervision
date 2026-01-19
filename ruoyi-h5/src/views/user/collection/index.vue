<template>
  <div class="collection-page">
    <!-- 导航栏 -->
    <van-nav-bar title="我的收藏" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="collection-content">
      <!-- 收藏列表 -->
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
          @load="onLoad"
        >
          <!-- 收藏卡片列表 -->
          <div v-for="item in collectionList" :key="item.id" class="collection-item">
            <div class="collection-header">
              <div class="header-left">
                <van-image
                  width="60"
                  height="60"
                  :src="item.coverImage || defaultImage"
                  fit="cover"
                  round
                />
                <div class="institution-info">
                  <div class="institution-name">{{ item.name }}</div>
                  <div class="institution-address">{{ item.address }}</div>
                </div>
              </div>
            </div>

            <div class="collection-detail">
              <div class="detail-row">
                <van-icon name="clock-o" size="16" />
                <span>收藏时间: {{ formatTime(item.favoriteTime || item.createTime) }}</span>
              </div>
              <div class="detail-row">
                <van-icon name="gold-coin-o" size="16" />
                <span>价格区间: {{ item.priceRange || '2000-5000元/月' }}</span>
              </div>
              <div class="detail-row">
                <van-icon name="phone-o" size="16" />
                <span>联系电话: {{ item.contactPhone || '暂无' }}</span>
              </div>
            </div>

            <div class="collection-footer">
              <van-button size="small" @click="goToDetail(item)">
                查看详情
              </van-button>
              <van-button
                size="small"
                type="danger"
                plain
                @click="handleDelete(item)"
              >
                取消收藏
              </van-button>
            </div>
          </div>
        </van-list>
      </van-pull-refresh>

      <!-- 空状态 -->
      <div v-if="!loading && collectionList.length === 0" class="empty-state">
        <van-empty description="暂无收藏记录">
          <template #image>
            <van-icon name="star-o" size="60" color="#ddd" />
          </template>
        </van-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { getUserFavoriteList, unfavoriteInstitution } from '@/api/institution'

const router = useRouter()

// 列表相关
const collectionList = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)

const defaultImage = 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'

// 格式化时间
const formatTime = (time) => {
  if (!time) return '暂无'
  try {
    const date = new Date(time)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch (error) {
    console.error('时间格式化失败:', error)
    return time
  }
}

// 加载收藏列表
const loadFavoriteList = async (isRefresh = false) => {
  try {
    loading.value = true

    const response = await getUserFavoriteList({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })

    if (response.code === 200 && response.rows) {
      const newList = response.rows.map(item => ({
        id: item.institutionId,
        favoriteId: item.favoriteId,
        name: item.institutionName || '机构名称',
        coverImage: item.coverImage || item.images,
        address: item.address || item.actualAddress || '地址信息完善中',
        favoriteTime: item.favoriteTime || item.createTime || '',
        priceRange: item.priceRange
          ? `${item.priceRangeMin || item.priceRange}-${item.priceRangeMax || item.priceRange}元/月`
          : (item.price ? `${item.price}元/月起` : '2000-5000元/月'),
        contactPhone: item.contactPhone || item.contactNumber || item.phone || '暂无'
      }))

      if (isRefresh) {
        collectionList.value = newList
      } else {
        collectionList.value = [...collectionList.value, ...newList]
      }

      if (newList.length < pageSize.value) {
        finished.value = true
      }
    } else {
      showToast(response.msg || '获取收藏列表失败')
      finished.value = true
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error)
    showToast('加载失败')
    finished.value = true
  } finally {
    loading.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  pageNum.value = 1
  finished.value = false
  collectionList.value = []
  loadFavoriteList(true).then(() => {
    refreshing.value = false
  })
}

// 上拉加载
const onLoad = () => {
  if (pageNum.value === 1) {
    loadFavoriteList()
  } else {
    pageNum.value++
    loadFavoriteList()
  }
}

// 单个删除
const handleDelete = async (item) => {
  try {
    await showConfirmDialog({
      title: '取消收藏',
      message: `确定要取消收藏「${item.name}」吗？`,
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })

    await unfavoriteInstitution(item.id)

    const index = collectionList.value.findIndex(i => i.id === item.id)
    if (index > -1) {
      collectionList.value.splice(index, 1)
    }

    showToast('已取消收藏')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      showToast(error.response?.data?.msg || '删除失败')
    }
  }
}

// 跳转到详情
const goToDetail = (item) => {
  router.push({
    name: 'InstitutionDetail',
    params: { id: item.id }
  })
}

// 页面加载时获取收藏列表
onMounted(() => {
  loadFavoriteList()
})
</script>

<style scoped>
.collection-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.collection-content {
  padding-bottom: 20px;
}

.collection-item {
  background: #fff;
  margin: 12px;
  border-radius: 8px;
  padding: 16px;
}

.collection-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f5f5f5;
}

.header-left {
  display: flex;
  gap: 12px;
  flex: 1;
}

.institution-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  justify-content: center;
}

.institution-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.institution-address {
  font-size: 12px;
  color: #999;
}

.collection-detail {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px 0;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.detail-row .van-icon {
  color: #999;
}

.collection-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

/* 空状态 */
.empty-state {
  padding: 100px 20px;
  text-align: center;
}

:deep(.van-empty) {
  padding: 0;
}

:deep(.van-empty__description) {
  color: #999;
  font-size: 14px;
  margin-top: 16px;
}
</style>
