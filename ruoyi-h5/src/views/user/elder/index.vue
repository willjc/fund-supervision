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
          class="elder-card"
        >
          <div class="elder-main">
            <van-image
              round
              width="60"
              height="60"
              :src="elder.avatar"
              fit="cover"
            />
            <div class="elder-info">
              <div class="elder-name-row">
                <span class="elder-name">{{ elder.name }}</span>
                <van-tag :type="getRelationTagType(elder.relation)" size="medium">
                  {{ elder.relation }}
                </van-tag>
              </div>
              <div class="elder-detail">年龄: {{ elder.age }}岁</div>
              <div class="elder-detail">身份证号: {{ elder.idCard }}</div>
            </div>
          </div>

          <div class="elder-actions">
            <van-button
              size="small"
              type="primary"
              color="#667eea"
              @click="handleEdit(elder)"
            >
              修改
            </van-button>
            <van-button
              size="small"
              type="danger"
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()

// 老人列表数据
const elderList = ref([])

// 模拟数据
const mockElderList = [
  {
    id: 1,
    name: '张伟',
    relation: '本人',
    age: 88,
    idCard: '430222188025656565',
    avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
  }
]

// 获取关系标签类型
const getRelationTagType = (relation) => {
  const typeMap = {
    '本人': 'primary',
    '父亲': 'success',
    '母亲': 'warning',
    '其他': 'default'
  }
  return typeMap[relation] || 'default'
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

    // 模拟删除操作
    await new Promise(resolve => setTimeout(resolve, 500))

    elderList.value = elderList.value.filter(item => item.id !== elder.id)
    showToast('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      showToast('删除失败')
    }
  }
}

// 加载老人列表
const loadElderList = async () => {
  try {
    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 300))

    elderList.value = mockElderList
  } catch (error) {
    showToast('加载失败')
  }
}

onMounted(() => {
  loadElderList()
})
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

/* 老人卡片 */
.elder-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.elder-main {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.elder-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.elder-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.elder-name {
  font-size: 17px;
  font-weight: 500;
  color: #333;
}

.elder-detail {
  font-size: 13px;
  color: #666;
}

/* 操作按钮 */
.elder-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.elder-actions .van-button {
  min-width: 70px;
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
}

:deep(.van-button__content) {
  display: flex;
  align-items: center;
  gap: 6px;
}

:deep(.van-button__text) {
  color: #fff;
}
</style>
