<template>
  <div class="collection-page">
    <van-nav-bar title="我的收藏" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="collection-content">
      <!-- 搜索栏 -->
      <div class="search-section">
        <van-search
          v-model="searchKeyword"
          placeholder="搜索机构名称"
          @search="onSearch"
        />
      </div>

      <!-- 批量操作栏 -->
      <div class="batch-section">
        <van-checkbox v-model="checkAll" @change="onCheckAllChange">全选</van-checkbox>
        <van-button
          v-if="selectedIds.length > 0"
          type="danger"
          size="small"
          plain
          @click="batchDelete"
        >
          批量删除({{ selectedIds.length }})
        </van-button>
      </div>

      <!-- 收藏列表 -->
      <div class="collection-list">
        <div
          v-for="item in filteredList"
          :key="item.id"
          class="collection-item"
        >
          <van-checkbox
            v-model="item.checked"
            @change="onItemCheck"
            class="item-checkbox"
          />

          <div class="item-content" @click="goToDetail(item)">
            <van-image
              width="100"
              height="80"
              :src="item.coverImage"
              fit="cover"
              radius="8"
            />

            <div class="item-info">
              <div class="item-name">{{ item.name }}</div>
              <div class="item-address">
                <van-icon name="location-o" size="12" />
                {{ item.address }}
              </div>
              <div class="item-meta">
                <van-tag type="primary" size="medium">{{ item.level }}</van-tag>
                <span class="item-price">¥{{ item.price }}/月起</span>
              </div>
            </div>
          </div>

          <van-icon
            name="delete-o"
            size="20"
            color="#ee0a24"
            class="delete-icon"
            @click="handleDelete(item)"
          />
        </div>

        <div v-if="filteredList.length === 0" class="empty-state">
          <van-empty :description="searchKeyword ? '未找到相关收藏' : '暂无收藏'" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()

// 搜索关键词
const searchKeyword = ref('')

// 全选状态
const checkAll = ref(false)

// 收藏列表
const collectionList = ref([
  {
    id: 1,
    name: '郑州市金水区花园口社区养老服务中心',
    coverImage: 'https://via.placeholder.com/200x160',
    address: '郑州市金水区花园口镇花园路123号',
    level: '三级养老机构',
    price: 2800,
    checked: false
  },
  {
    id: 2,
    name: '郑州市二七区福寿园养老院',
    coverImage: 'https://via.placeholder.com/200x160',
    address: '郑州市二七区建设路456号',
    level: '二级养老机构',
    price: 3200,
    checked: false
  },
  {
    id: 3,
    name: '郑州市中原区康乐养老中心',
    coverImage: 'https://via.placeholder.com/200x160',
    address: '郑州市中原区桐柏路789号',
    level: '三级养老机构',
    price: 2500,
    checked: false
  }
])

// 过滤后的列表
const filteredList = computed(() => {
  if (!searchKeyword.value) {
    return collectionList.value
  }

  return collectionList.value.filter(item =>
    item.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

// 选中的ID列表
const selectedIds = computed(() => {
  return collectionList.value.filter(item => item.checked).map(item => item.id)
})

// 搜索
const onSearch = () => {
  // 搜索逻辑已通过computed实现
}

// 全选切换
const onCheckAllChange = (checked) => {
  filteredList.value.forEach(item => {
    item.checked = checked
  })
}

// 单项勾选
const onItemCheck = () => {
  checkAll.value = filteredList.value.every(item => item.checked)
}

// 单个删除
const handleDelete = async (item) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: `确定要取消收藏 ${item.name} 吗?`
    })

    // 模拟删除
    await new Promise(resolve => setTimeout(resolve, 300))

    const index = collectionList.value.findIndex(i => i.id === item.id)
    if (index > -1) {
      collectionList.value.splice(index, 1)
      showToast('已取消收藏')
    }
  } catch (error) {
    if (error !== 'cancel') {
      showToast('删除失败')
    }
  }
}

// 批量删除
const batchDelete = async () => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: `确定要批量删除 ${selectedIds.value.length} 项收藏吗?`
    })

    // 模拟删除
    await new Promise(resolve => setTimeout(resolve, 300))

    collectionList.value = collectionList.value.filter(item => !item.checked)
    checkAll.value = false
    showToast('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      showToast('删除失败')
    }
  }
}

// 跳转到详情
const goToDetail = (item) => {
  router.push({
    path: '/institution/detail',
    query: { id: item.id }
  })
}
</script>

<style scoped>
.collection-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.collection-content {
  padding-top: 0;
}

/* 搜索区域 */
.search-section {
  background: #fff;
  padding: 8px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 批量操作栏 */
.batch-section {
  background: #fff;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 12px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.batch-section :deep(.van-checkbox__label) {
  font-size: 14px;
  color: #333;
}

/* 收藏列表 */
.collection-list {
  padding: 0 12px;
  min-height: 400px;
}

.collection-item {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-checkbox {
  flex-shrink: 0;
}

.item-content {
  flex: 1;
  display: flex;
  gap: 12px;
  cursor: pointer;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.item-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.item-address {
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-price {
  font-size: 14px;
  color: #ff6b00;
  font-weight: 500;
}

.delete-icon {
  flex-shrink: 0;
  cursor: pointer;
}

.empty-state {
  padding: 100px 0;
}
</style>
