<template>
  <div class="complaint-form-page">
    <van-nav-bar title="新增投诉" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="form-content">
      <van-form @submit="onSubmit">
        <!-- 投诉机构 -->
        <van-cell-group inset title="投诉机构">
          <van-field
            v-model="formData.institutionName"
            label="选择机构"
            placeholder="请选择投诉的机构"
            readonly
            required
            right-icon="arrow-down"
            @click="showInstitutionPicker = true"
            :rules="[{ required: true, message: '请选择投诉机构' }]"
          />
        </van-cell-group>

        <!-- 投诉类型 -->
        <van-cell-group inset title="投诉类型">
          <van-field
            v-model="formData.type"
            label="投诉类型"
            placeholder="请选择投诉类型"
            readonly
            required
            right-icon="arrow-down"
            @click="showTypePicker = true"
            :rules="[{ required: true, message: '请选择投诉类型' }]"
          />
        </van-cell-group>

        <!-- 投诉标题 -->
        <van-cell-group inset title="投诉标题">
          <van-field
            v-model="formData.title"
            placeholder="请输入投诉标题"
            required
            maxlength="50"
            show-word-limit
            :rules="[{ required: true, message: '请输入投诉标题' }]"
          />
        </van-cell-group>

        <!-- 投诉内容 -->
        <van-cell-group inset title="投诉内容">
          <van-field
            v-model="formData.content"
            type="textarea"
            placeholder="请详细描述投诉内容"
            rows="5"
            maxlength="500"
            show-word-limit
            required
            :rules="[{ required: true, message: '请输入投诉内容' }]"
          />
        </van-cell-group>

        <!-- 上传图片 -->
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

        <!-- 联系方式 -->
        <van-cell-group inset title="联系方式">
          <van-field
            v-model="formData.contactName"
            label="联系人"
            placeholder="请输入联系人姓名"
            required
            :rules="[{ required: true, message: '请输入联系人' }]"
          />
          <van-field
            v-model="formData.contactPhone"
            label="联系电话"
            placeholder="请输入联系电话"
            type="tel"
            maxlength="11"
            required
            :rules="[
              { required: true, message: '请输入联系电话' },
              { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }
            ]"
          />
        </van-cell-group>

        <!-- 提交按钮 -->
        <div class="submit-section">
          <van-button round block type="primary" native-type="submit">
            提交投诉
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 机构选择器 -->
    <van-popup v-model:show="showInstitutionPicker" position="bottom">
      <van-picker
        :columns="institutionOptions"
        @confirm="onInstitutionConfirm"
        @cancel="showInstitutionPicker = false"
      />
    </van-popup>

    <!-- 投诉类型选择器 -->
    <van-popup v-model:show="showTypePicker" position="bottom">
      <van-picker
        :columns="typeOptions"
        @confirm="onTypeConfirm"
        @cancel="showTypePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

// 表单数据
const formData = ref({
  institutionName: '',
  type: '',
  title: '',
  content: '',
  images: [],
  contactName: '张丽丽',
  contactPhone: '15612345678'
})

// 选择器显示状态
const showInstitutionPicker = ref(false)
const showTypePicker = ref(false)

// 机构选项
const institutionOptions = [
  { text: '郑州市金水区花园口社区养老服务中心', value: '1' },
  { text: '郑州市二七区福寿园养老院', value: '2' },
  { text: '郑州市中原区康乐养老中心', value: '3' }
]

// 投诉类型选项
const typeOptions = [
  { text: '服务态度', value: '服务态度' },
  { text: '卫生环境', value: '卫生环境' },
  { text: '饮食质量', value: '饮食质量' },
  { text: '收费问题', value: '收费问题' },
  { text: '设施设备', value: '设施设备' },
  { text: '护理质量', value: '护理质量' },
  { text: '其他', value: '其他' }
]

// 机构选择确认
const onInstitutionConfirm = (value) => {
  formData.value.institutionName = value.selectedOptions[0].text
  showInstitutionPicker.value = false
}

// 类型选择确认
const onTypeConfirm = (value) => {
  formData.value.type = value.selectedOptions[0].text
  showTypePicker.value = false
}

// 图片超出大小提示
const onOversize = () => {
  showToast('图片大小不能超过5MB')
}

// 提交表单
const onSubmit = async (values) => {
  try {
    // 模拟提交
    await new Promise(resolve => setTimeout(resolve, 500))

    showToast('投诉提交成功')

    setTimeout(() => {
      router.back()
    }, 1000)
  } catch (error) {
    showToast('提交失败,请重试')
  }
}
</script>

<style scoped>
.complaint-form-page {
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
</style>
