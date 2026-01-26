<template>
  <div class="complaint-form-page">
    <van-nav-bar title="新增投诉" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="form-content" v-if="!loading">
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
            v-model="formData.complaintType"
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
        <van-cell-group inset title="上传图片（可选）">
          <van-cell>
            <van-uploader
              v-model="imageList"
              :after-read="handleAfterRead"
              :before-delete="handleBeforeDelete"
              :max-count="6"
              :max-size="5 * 1024 * 1024"
              preview-size="80px"
              multiple
            />
            <div class="upload-tip">支持jpg、png格式，单张不超过5MB，最多6张</div>
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
          <van-button round block type="primary" native-type="submit" :loading="submitting">
            提交投诉
          </van-button>
        </div>
      </van-form>
    </div>

    <van-skeleton v-else title :row="10" />

    <!-- 机构选择器 -->
    <van-popup v-model:show="showInstitutionPicker" position="bottom" round>
      <van-picker
        :columns="institutionOptions"
        :loading="institutionLoading"
        @confirm="onInstitutionConfirm"
        @cancel="showInstitutionPicker = false"
      />
    </van-popup>

    <!-- 投诉类型选择器 -->
    <van-popup v-model:show="showTypePicker" position="bottom" round>
      <van-picker
        :columns="typeOptions"
        @confirm="onTypeConfirm"
        @cancel="showTypePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog, closeToast, showLoadingToast } from 'vant'
import { submitComplaint, uploadImage } from '@/api/complaint'
import { getInstitutionList } from '@/api/institution'
import { getUserInfo } from '@/api/user'

const router = useRouter()

// 表单数据
const formData = ref({
  institutionId: null,
  institutionName: '',
  complaintType: '',
  title: '',
  content: '',
  images: [],
  contactName: '',
  contactPhone: ''
})

// 图片列表
const imageList = ref([])

// 选择器显示状态
const showInstitutionPicker = ref(false)
const showTypePicker = ref(false)

// 加载状态
const loading = ref(true)
const institutionLoading = ref(false)
const submitting = ref(false)

// 机构选项
const institutionOptions = ref([])

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

// 初始化 - 获取用户信息和机构列表
onMounted(async () => {
  await Promise.all([loadUserInfo(), loadInstitutions()])
  loading.value = false
})

// 获取用户信息
const loadUserInfo = async () => {
  try {
    const response = await getUserInfo()
    if (response.code === 200 && response.data && response.data.user) {
      const user = response.data.user
      // 优先使用真实姓名，其次昵称
      formData.value.contactName = user.realName || user.nickName || ''
      formData.value.contactPhone = user.phonenumber || ''
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取机构列表
const loadInstitutions = async () => {
  try {
    institutionLoading.value = true
    const response = await getInstitutionList()
    if (response.code === 200 && response.rows) {
      institutionOptions.value = response.rows.map(item => ({
        text: item.institutionName,
        value: item.institutionId
      }))
    }
  } catch (error) {
    console.error('获取机构列表失败:', error)
    showToast('获取机构列表失败')
  } finally {
    institutionLoading.value = false
  }
}

// 机构选择确认
const onInstitutionConfirm = (value) => {
  formData.value.institutionId = value.selectedOptions[0].value
  formData.value.institutionName = value.selectedOptions[0].text
  showInstitutionPicker.value = false
}

// 类型选择确认
const onTypeConfirm = (value) => {
  formData.value.complaintType = value.selectedOptions[0].value
  showTypePicker.value = false
}

// 图片上传后的处理 - 真正上传到服务器获取URL
const handleAfterRead = async (file) => {
  // 处理多文件上传
  const files = Array.isArray(file) ? file : [file]

  for (const item of files) {
    item.status = 'uploading'
    item.message = '上传中...'

    try {
      // 调用后端上传接口
      const response = await uploadImage(item.file)

      if (response.code === 200 && response.url) {
        // 上传成功，保存服务器返回的URL
        item.serverUrl = response.url
        item.status = 'done'
        item.message = ''
        // 使用服务器URL作为显示地址
        item.url = response.url
      } else {
        item.status = 'failed'
        item.message = '上传失败'
        showToast(response.msg || '图片上传失败')
      }
    } catch (error) {
      console.error('图片上传失败:', error)
      item.status = 'failed'
      item.message = '上传失败'
      showToast('图片上传失败，请��试')
    }
  }
}

// 图片删除前的处理
const handleBeforeDelete = (file) => {
  return new Promise((resolve) => {
    showConfirmDialog({
      title: '提示',
      message: '确定要删除这张图片吗？'
    }).then(() => {
      resolve(true)
    }).catch(() => {
      resolve(false)
    })
  })
}

// 提交表单
const onSubmit = async () => {
  // 验证必填项
  if (!formData.value.institutionId) {
    showToast('请选择投诉机构')
    return
  }
  if (!formData.value.complaintType) {
    showToast('请选择投诉类型')
    return
  }
  if (!formData.value.title || !formData.value.title.trim()) {
    showToast('请输入投诉标题')
    return
  }
  if (!formData.value.content || !formData.value.content.trim()) {
    showToast('请输入投诉内容')
    return
  }
  if (!formData.value.contactName || !formData.value.contactName.trim()) {
    showToast('请输入联系人')
    return
  }
  if (!formData.value.contactPhone || !/^1[3-9]\d{9}$/.test(formData.value.contactPhone)) {
    showToast('请输入正确的联系电话')
    return
  }

  try {
    submitting.value = true
    showLoadingToast({
      message: '提交中...',
      forbidClick: true,
      duration: 0
    })

    // 构建图片数据 - 使用服务器返回的URL
    const complaintImages = imageList.value
      .filter(file => file.serverUrl) // 只取已上传成功的图片
      .map(file => file.serverUrl)

    const submitData = {
      institutionId: formData.value.institutionId,
      complaintType: formData.value.complaintType,
      title: formData.value.title,
      content: formData.value.content,
      images: complaintImages.length > 0 ? JSON.stringify(complaintImages) : null,
      contactName: formData.value.contactName,
      contactPhone: formData.value.contactPhone
    }

    const response = await submitComplaint(submitData)

    if (response.code === 200) {
      showToast('投诉提交成功')
      setTimeout(() => {
        router.back()
      }, 1000)
    } else {
      showToast(response.msg || '提交失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    showToast('提交失败,请重试')
  } finally {
    submitting.value = false
    closeToast()
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

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
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
