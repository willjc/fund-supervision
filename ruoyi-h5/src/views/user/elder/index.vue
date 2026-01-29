<template>
  <div class="elder-info-page">
    <van-nav-bar title="老人信息" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="elder-list">
      <div v-if="elderList.length === 0" class="empty-state">
        <van-empty description="暂无老人信息" />
      </div>

      <div v-else>
        <div
          v-for="elder in elderList"
          :key="elder.id"
          class="elder-item"
        >
          <div class="elder-header">
            <div class="header-left">
              <van-image
                width="60"
                height="60"
                :src="elder.avatar"
                fit="cover"
                round
              />
              <div class="elder-info">
                <div class="elder-name">{{ elder.name }}</div>
                <div class="elder-idcard">{{ elder.idCard }}</div>
              </div>
            </div>
          </div>

          <div class="elder-detail">
            <div class="detail-row">
              <van-icon name="user-o" size="16" />
              <span>与本人关系: {{ elder.relation }}</span>
            </div>
            <div class="detail-row">
              <van-icon name="calendar-o" size="16" />
              <span>年龄: {{ elder.age }}岁</span>
            </div>
            <div class="detail-row">
              <van-icon name="location-o" size="16" />
              <span>居住地址: {{ elder.address || '暂无' }}</span>
            </div>
            <div class="detail-row">
              <van-icon name="phone-o" size="16" />
              <span>联系电话: {{ elder.contactPhone || '暂无' }}</span>
            </div>
          </div>

          <div class="elder-footer">
            <van-button size="small" @click="handleEdit(elder)">
              修改
            </van-button>
            <van-button
              size="small"
              type="danger"
              plain
              @click="handleDelete(elder)"
            >
              删除
            </van-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增按钮 -->
    <div class="add-button-wrapper">
      <van-button
        round
        block
        class="add-button"
        @click="goToAddElder"
      >
        <van-icon name="plus" size="20" />
        新增
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { useUserStore } from '@/store/modules/user'
import { getToken } from '@/utils/auth'
import { fetchApi } from '@/utils/request'
import { getImageUrl } from '@/utils/image'

const router = useRouter()
const userStore = useUserStore()

// 页面加载时刷新老人列表数据
onMounted(async () => {
  try {
    // 调用 /info 接口获取最新的老人列表
    const token = getToken()
    const response = await fetchApi('/h5/user/info', {
      method: 'GET',
      headers: {
        'Authorization': token ? `Bearer ${token}` : ''
      }
    })

    const result = await response.json()
    if (result.code === 200 && result.data && result.data.elders) {
      // 更新 userStore 中的老人列表数据
      userStore.setElders(result.data.elders)
      console.log('老人信息页面数据刷新成功:', result.data.elders)
    } else {
      console.error('获取老人列表失败:', result.msg)
    }
  } catch (error) {
    console.error('刷新老人列表数据失败:', error)
  }
})

// 老人列表数据 - 从 userStore 获取
const elderList = computed(() => {
  if (!userStore.elders || userStore.elders.length === 0) {
    return []
  }

  // 将后端返回的老人数据转换为页面需要的格式
  return userStore.elders.map(elder => ({
    id: elder.elderId,
    name: elder.elderName,
    relation: getRelationText(elder.relationType),
    age: elder.age || calculateAge(elder.birthDate),
    idCard: elder.idCard,
    address: elder.address || elder.liveAddress || '',
    contactPhone: elder.contactPhone || elder.phoneNumber || elder.phone || '',
    avatar: getImageUrl(elder.photoPath) || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
  }))
})

// 根据关系类型码转换为文字（对应数据库定义：0:本人 1:子女 2:配偶 3:兄弟姐妹 4:其他亲属 5:朋友）
const getRelationText = (relationType) => {
  const relationMap = {
    '0': '本人',
    '1': '子女',
    '2': '配偶',
    '3': '兄弟姐妹',
    '4': '其他亲属',
    '5': '朋友'
  }
  return relationMap[relationType] || '家属'
}

// 根据出生日期计算年龄
const calculateAge = (birthDate) => {
  if (!birthDate) return 0
  const birth = new Date(birthDate)
  const today = new Date()
  let age = today.getFullYear() - birth.getFullYear()
  const monthDiff = today.getMonth() - birth.getMonth()
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birth.getDate())) {
    age--
  }
  return age
}

// 跳转到新增老人页面
const goToAddElder = () => {
  router.push('/user/elder/form')
}

// 修改老人信息
const handleEdit = (elder) => {
  router.push({
    path: '/user/elder/form',
    query: { id: elder.id }
  })
}

// 删除老人信息
const handleDelete = async (elder) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: `确定要删除老人 ${elder.name} 的信息吗?`
    })

    // TODO: 调用后端API删除老人关系
    showToast('删除功能开发中')
  } catch (error) {
    // 用户取消删除
  }
}

// 页面每次进入时都会通过 onMounted 刷新数据，确保显示最新信息
</script>

<style scoped>
.elder-info-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.elder-list {
  padding: 12px;
}

.empty-state {
  margin-top: 100px;
}

.elder-item {
  background: #fff;
  margin: 0 0 12px 0;
  border-radius: 8px;
  padding: 16px;
}

.elder-header {
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

.elder-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  justify-content: center;
}

.elder-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

.elder-idcard {
  font-size: 12px;
  color: #999;
}

.elder-detail {
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

.elder-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f5f5f5;
}

/* 新增按钮 */
.add-button-wrapper {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: #fff;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

.add-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-size: 16px;
  font-weight: 500;
  height: 48px;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
  color: #fff;
}

.add-button :deep(.van-button__content) {
  display: flex;
  align-items: center;
  gap: 6px;
}

.add-button :deep(.van-button__text) {
  color: #fff;
}
</style>
