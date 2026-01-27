<template>
  <div class="evaluation-form-page">
    <van-nav-bar title="写评价" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="form-content">
      <!-- 机构信息 -->
      <div class="institution-card">
        <div class="institution-name">{{ institutionName }}</div>
      </div>

      <!-- 评分 -->
      <van-cell-group inset title="服务评分">
        <van-cell center>
          <template #title>
            <div class="rate-section">
              <van-rate
                v-model="formData.rating"
                :size="32"
                color="#ffd21e"
                void-color="#eee"
                allow-half
              />
              <div class="rate-desc">{{ getRateDesc(formData.rating) }}</div>
            </div>
          </template>
        </van-cell>
      </van-cell-group>

      <!-- 评价标签 -->
      <van-cell-group inset title="评价标签(可选)">
        <van-cell>
          <div class="tag-list">
            <van-tag
              v-for="tag in tagOptions"
              :key="tag"
              :type="formData.tags.includes(tag) ? 'primary' : 'default'"
              size="large"
              round
              @click="toggleTag(tag)"
              class="tag-item"
            >
              {{ tag }}
            </van-tag>
          </div>
        </van-cell>
      </van-cell-group>

      <!-- 评价内容 -->
      <van-cell-group inset title="评价内容">
        <van-field
          v-model="formData.content"
          type="textarea"
          placeholder="说说您的使用感受吧~"
          rows="5"
          maxlength="500"
          show-word-limit
          :rules="[{ required: true, message: '请输入评价内容' }]"
        />
      </van-cell-group>

      <!-- 图片上传 -->
      <van-cell-group inset title="上传图片(可选)">
        <van-cell>
          <van-uploader
            v-model="formData.images"
            multiple
            :max-count="6"
            :max-size="5 * 1024 * 1024"
            @oversize="onOversize"
          >
            <template #default>
              <div class="upload-btn">
                <van-icon name="photograph" size="28" color="#999" />
                <div class="upload-text">上传图片</div>
              </div>
            </template>
          </van-uploader>
        </van-cell>
      </van-cell-group>

      <!-- 提交按钮 -->
      <div class="submit-section">
        <van-button
          round
          block
          type="primary"
          @click="submitEvaluation"
          :disabled="!canSubmit"
        >
          提交评价
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()
const route = useRoute()

// 机构名称
const institutionName = ref(route.query.institutionName || '养老机构')

// 表单数据
const formData = ref({
  rating: 5,
  tags: [],
  content: '',
  images: []
})

// 标签选项
const tagOptions = [
  '服务周到',
  '环境优美',
  '设施完善',
  '价格合理',
  '护理专业',
  '交通便利',
  '饮食健康',
  '活动丰富'
]

// 获取评分描述
const getRateDesc = (rating) => {
  if (rating >= 5) return '非常满意'
  if (rating >= 4) return '满意'
  if (rating >= 3) return '一般'
  if (rating >= 2) return '不满意'
  return '非常不满意'
}

// 切换标签
const toggleTag = (tag) => {
  const index = formData.value.tags.indexOf(tag)
  if (index > -1) {
    formData.value.tags.splice(index, 1)
  } else {
    formData.value.tags.push(tag)
  }
}

// 图片超出大小提示
const onOversize = () => {
  showToast('图片大小不能超过5MB')
}

// 是否可以提交
const canSubmit = computed(() => {
  return formData.value.rating > 0 && formData.value.content.trim().length > 0
})

// 提交评价
const submitEvaluation = async () => {
  if (!canSubmit.value) {
    showToast('请完善评价内容')
    return
  }

  try {
    // 模拟提交
    await new Promise(resolve => setTimeout(resolve, 500))

    showToast('评价成功')

    setTimeout(() => {
      router.back()
    }, 1000)
  } catch (error) {
    showToast('提交失败,请重试')
  }
}
</script>

<style scoped>
.evaluation-form-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.form-content {
  padding: 12px 0 20px;
}

.van-cell-group {
  margin-bottom: 12px;
}

/* 机构信息卡片 */
.institution-card {
  background: #fff;
  margin: 0 12px 12px;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.institution-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  text-align: center;
}

/* 评分区域 */
.rate-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 16px 0;
}

.rate-desc {
  font-size: 15px;
  color: #ffd21e;
  font-weight: 500;
}

/* 标签列表 */
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding: 12px 0;
}

.tag-item {
  cursor: pointer;
  transition: all 0.3s;
}

:deep(.van-tag--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
}

/* 上传按钮 */
.upload-btn {
  width: 80px;
  height: 80px;
  background: #f7f8fa;
  border: 1px dashed #dcdee0;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.upload-text {
  font-size: 12px;
  color: #999;
}

:deep(.van-uploader__wrapper) {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

:deep(.van-uploader__preview) {
  width: 80px;
  height: 80px;
}

:deep(.van-uploader__preview-image) {
  border-radius: 8px;
}

/* 提交按钮 */
.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: #fff;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
  z-index: 100;
}

:deep(.van-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.3);
}

:deep(.van-button--disabled) {
  background: #dcdee0;
  box-shadow: none;
}
</style>
