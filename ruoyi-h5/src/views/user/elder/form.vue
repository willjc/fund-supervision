<template>
  <div class="elder-form-page">
    <van-nav-bar
      :title="isEdit ? '修改老人信息' : '新增老人信息'"
      left-arrow
      @click-left="$router.back()"
      fixed
      placeholder
    />

    <div class="form-content">
      <van-form @submit="onSubmit">
        <!-- 照片上传 -->
        <van-cell-group inset title="照片上传">
          <!-- 老人照片 -->
          <div class="upload-item">
            <div class="upload-label">老人照片（1张）</div>
            <div class="upload-area">
              <!-- 已上传图片 -->
              <div v-if="formData.elderPhoto.length > 0" class="image-item">
                <img
                  :src="formData.elderPhoto[0].url"
                  class="uploaded-image"
                  @click="previewImage(formData.elderPhoto[0].url)"
                />
                <div class="delete-btn" @click="onElderPhotoDelete">
                  <van-icon name="cross" size="12" color="#fff" />
                </div>
              </div>
              <!-- 上传按钮 -->
              <div v-else class="upload-btn" @click="triggerUpload('elderPhoto')">
                <van-icon name="photograph" size="24" color="#cecfd7" />
                <span class="upload-text">点击上传</span>
              </div>
              <input
                type="file"
                ref="elderPhotoInput"
                accept="image/*"
                style="display: none"
                @change="onFileChange($event, 'elderPhoto')"
              />
            </div>
          </div>

          <!-- 身份证正面 -->
          <div class="upload-item">
            <div class="upload-label">身份证正面（1张）</div>
            <div class="upload-area">
              <!-- 已上传图片 -->
              <div v-if="formData.idCardFront.length > 0" class="image-item">
                <img
                  :src="formData.idCardFront[0].url"
                  class="uploaded-image"
                  @click="previewImage(formData.idCardFront[0].url)"
                />
                <div class="delete-btn" @click="onIdCardFrontDelete">
                  <van-icon name="cross" size="12" color="#fff" />
                </div>
              </div>
              <!-- 上传按钮 -->
              <div v-else class="upload-btn" @click="triggerUpload('idCardFront')">
                <van-icon name="photograph" size="24" color="#cecfd7" />
                <span class="upload-text">点击上传</span>
              </div>
              <input
                type="file"
                ref="idCardFrontInput"
                accept="image/*"
                style="display: none"
                @change="onFileChange($event, 'idCardFront')"
              />
            </div>
          </div>

          <!-- 身份证反面 -->
          <div class="upload-item">
            <div class="upload-label">身份证反面（1张）</div>
            <div class="upload-area">
              <!-- 已上传图片 -->
              <div v-if="formData.idCardBack.length > 0" class="image-item">
                <img
                  :src="formData.idCardBack[0].url"
                  class="uploaded-image"
                  @click="previewImage(formData.idCardBack[0].url)"
                />
                <div class="delete-btn" @click="onIdCardBackDelete">
                  <van-icon name="cross" size="12" color="#fff" />
                </div>
              </div>
              <!-- 上传按钮 -->
              <div v-else class="upload-btn" @click="triggerUpload('idCardBack')">
                <van-icon name="photograph" size="24" color="#cecfd7" />
                <span class="upload-text">点击上传</span>
              </div>
              <input
                type="file"
                ref="idCardBackInput"
                accept="image/*"
                style="display: none"
                @change="onFileChange($event, 'idCardBack')"
              />
            </div>
          </div>
        </van-cell-group>

        <!-- 基本信息 -->
        <van-cell-group inset title="基本信息">
          <van-field
            v-model="formData.name"
            label="姓名"
            placeholder="请输入老人姓名"
            required
            :rules="[{ required: true, message: '请输入姓名' }]"
          />

          <van-field
            v-model="formData.relation"
            label="与本人关系"
            placeholder="请选择关系"
            readonly
            required
            right-icon="arrow-down"
            @click="showRelationPicker = true"
            :rules="[{ required: true, message: '请选择关系' }]"
          />

          <van-field
            v-model="formData.age"
            type="number"
            label="年龄"
            placeholder="请输入年龄"
            required
            :rules="[{ required: true, message: '请输入年龄' }]"
          />

          <van-field
            v-model="formData.idCard"
            label="身份证号"
            placeholder="请输入身份证号"
            required
            maxlength="18"
            :rules="[
              { required: true, message: '请输入身份证号' },
              { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[\dXx]$/, message: '身份证号格式不正确' }
            ]"
          />

          <van-field
            v-model="formData.phone"
            label="联系电话"
            placeholder="请输入联系电话"
            type="tel"
            maxlength="11"
            :rules="[
              { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }
            ]"
          />

          <van-field
            v-model="formData.address"
            label="居住地址"
            placeholder="请输入居住地址"
            type="textarea"
            rows="2"
            maxlength="100"
            show-word-limit
          />
        </van-cell-group>

        <!-- 健康信息 -->
        <van-cell-group inset title="健康信息">
          <van-field
            v-model="formData.healthStatus"
            label="健康状况"
            placeholder="请选择健康状况"
            readonly
            right-icon="arrow-down"
            @click="showHealthPicker = true"
          />

          <van-field
            v-model="formData.medicalHistory"
            label="既往病史"
            placeholder="请输入既往病史(可选)"
            type="textarea"
            rows="3"
            maxlength="200"
            show-word-limit
          />
        </van-cell-group>

        <!-- 提交按钮 -->
        <div class="submit-section">
          <van-button round block type="primary" native-type="submit">
            {{ isEdit ? '保存修改' : '提交' }}
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 关系选择器 -->
    <van-popup v-model:show="showRelationPicker" position="bottom">
      <van-picker
        :columns="relationOptions"
        @confirm="onRelationConfirm"
        @cancel="showRelationPicker = false"
        :default-index="0"
      />
    </van-popup>

    <!-- 健康状况选择器 -->
    <van-popup v-model:show="showHealthPicker" position="bottom">
      <van-picker
        :columns="healthOptions"
        @confirm="onHealthConfirm"
        @cancel="showHealthPicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showImagePreview } from 'vant'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/store/modules/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 是否为编辑模式
const isEdit = ref(false)

// 表单数据
const formData = ref({
  elderPhoto: [],     // 老人照片
  idCardFront: [],     // 身份证正面
  idCardBack: [],      // 身份证反面
  name: '',
  relation: '',
  relationType: '',    // 关系类型（对应后端）
  age: '',
  idCard: '',
  phone: '',
  address: '',
  healthStatus: '',
  medicalHistory: ''
})

// 选择器显示状态
const showRelationPicker = ref(false)
const showHealthPicker = ref(false)

// 文件输入框ref
const elderPhotoInput = ref(null)
const idCardFrontInput = ref(null)
const idCardBackInput = ref(null)

// 关系选项（对应数据库类型：0:本人 1:子女 2:配偶 3:兄弟姐妹 4:其他亲属 5:朋友）
const relationOptions = [
  { text: '本人', value: '0' },
  { text: '配偶', value: '2' },
  { text: '子女', value: '1' },
  { text: '兄弟姐妹', value: '3' },
  { text: '其他亲属', value: '4' },
  { text: '朋友', value: '5' }
]

// 关系类型映射
const relationTypeMap = {
  '0': '本人',
  '1': '子女',
  '2': '配偶',
  '3': '兄弟姐妹',
  '4': '其他亲属',
  '5': '朋友'
}

// 健康状况选项
const healthOptions = [
  { text: '健康', value: '健康' },
  { text: '良好', value: '良好' },
  { text: '一般', value: '一般' },
  { text: '较差', value: '较差' },
  { text: '需要护理', value: '需要护理' }
]

// 关系选择确认
const onRelationConfirm = (result) => {
  console.log('关系选择参数:', result) // 调试日志

  // 根据控制台日志，van-picker返回格式：
  // {
  //   selectedValues: ['2'],
  //   selectedOptions: [{...}],
  //   selectedIndexes: [1]
  // }

  if (result && result.selectedValues && result.selectedValues.length > 0) {
    const selectedValue = result.selectedValues[0]
    const selectedIndex = result.selectedIndexes[0]
    const selectedOption = result.selectedOptions[0]

    console.log('选中值:', selectedValue)
    console.log('选中索引:', selectedIndex)
    console.log('选中选项:', selectedOption)

    // 优先使用索引获取我们的选项配置
    const ourOption = relationOptions[selectedIndex]
    console.log('我们的配置选项:', ourOption)

    if (ourOption) {
      formData.value.relation = ourOption.text // 显示文本
      formData.value.relationType = ourOption.value // 关系类型
      console.log('设置完成 - relation:', formData.value.relation, 'relationType:', formData.value.relationType)
    } else {
      console.error('未找到对应的配置选项')
    }
  } else {
    console.error('选择参数格式错误')
  }

  showRelationPicker.value = false
}

// 健康状况选择确认
const onHealthConfirm = (value) => {
  formData.value.healthStatus = value.selectedOptions[0].text
  showHealthPicker.value = false
}

// 图片超出大小提示
const onOversize = () => {
  showToast('图片大小不能超过5MB')
}

// 图片删除处理
const onElderPhotoDelete = () => {
  formData.value.elderPhoto = []
}

const onIdCardFrontDelete = () => {
  formData.value.idCardFront = []
}

const onIdCardBackDelete = () => {
  formData.value.idCardBack = []
}

// 触发文件选择
const triggerUpload = (type) => {
  const inputMap = {
    elderPhoto: elderPhotoInput,
    idCardFront: idCardFrontInput,
    idCardBack: idCardBackInput
  }
  inputMap[type].value?.click()
}

// 文件选择处理
const onFileChange = (event, type) => {
  const file = event.target.files[0]
  if (!file) return

  // 检查文件大小
  if (file.size > 5 * 1024 * 1024) {
    showToast('图片大小不能超过5MB')
    return
  }

  // 创建预览URL
  const url = URL.createObjectURL(file)
  const targetArray = type === 'elderPhoto' ? 'elderPhoto' :
                      type === 'idCardFront' ? 'idCardFront' : 'idCardBack'

  formData.value[targetArray] = [{
    url: url,
    file: file,
    isImage: true
  }]

  // 清空input，允许重复选择同一文件
  event.target.value = ''
}

// 图片预览
const previewImage = (url) => {
  showImagePreview({
    images: [url],
    closeable: true
  })
}

// 提交表单
const onSubmit = async (values) => {
  try {
    // 参数验证
    if (!formData.value.name.trim()) {
      showToast('请输入老人姓名')
      return
    }
    if (!formData.value.relationType) {
      showToast('请选择与本人关系')
      return
    }
    if (!formData.value.age) {
      showToast('请输入年龄')
      return
    }
    if (!formData.value.idCard) {
      showToast('请输入身份证号')
      return
    }

    // 上传图片
    const uploadedImages = await uploadImages()

    // 准备提交数据
    const submitData = {
      elderName: formData.value.name.trim(),
      relationType: formData.value.relationType,
      age: formData.value.age,
      idCard: formData.value.idCard,
      phone: formData.value.phone,
      address: formData.value.address,
      healthStatus: formData.value.healthStatus,
      medicalHistory: formData.value.medicalHistory,
      photoPath: uploadedImages.elderPhoto || null,
      idCardFrontPath: uploadedImages.idCardFront || null,
      idCardBackPath: uploadedImages.idCardBack || null
    }

    // 如果是编辑模式,添加elderId
    if (isEdit.value && route.query.id) {
      submitData.elderId = route.query.id
    }

    // 调用后端接口
    const token = getToken()
    console.log('提交表单使用的token:', token) // 调试日志

    // 根据模式选择不同的接口
    const apiUrl = isEdit.value ? '/api/h5/user/updateElder' : '/api/h5/user/addElder'
    console.log('调用接口:', apiUrl, '数据:', submitData)

    const response = await fetch(apiUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': token ? 'Bearer ' + token : ''
      },
      body: JSON.stringify(submitData)
    })

    const result = await response.json()

    if (result.code === 200) {
      showToast(isEdit.value ? '修改成功' : '添加成功')

      // 刷新老人列表数据
      try {
        // 直接调用接口刷新数据，避免 userStore 方法缓存问题
        const token = getToken()
        const refreshResponse = await fetch('/api/h5/user/info', {
          method: 'GET',
          headers: {
            'Authorization': token ? `Bearer ${token}` : ''
          }
        })

        const refreshResult = await refreshResponse.json()
        if (refreshResult.code === 200 && refreshResult.data && refreshResult.data.elders) {
          // 更新 userStore 数据，从 data.elders 获取老人列表
          userStore.setElders(refreshResult.data.elders)
          console.log('老人列表刷新成功，最新数据:', refreshResult.data.elders)
        } else {
          console.error('刷新老人列表接口失败:', refreshResult.msg)
        }
      } catch (error) {
        console.error('刷新老人列表失败:', error)
      }

      setTimeout(() => {
        router.back()
      }, 1000)
    } else {
      showToast(result.msg || '操作失败')
    }
  } catch (error) {
    console.error('提交失败:', error)
    showToast('操作失败，请重试')
  }
}

// 上传图片
const uploadImages = async () => {
  const uploadedImages = {}

  // 上传老人照片
  if (formData.value.elderPhoto.length > 0) {
    const photo = formData.value.elderPhoto[0]
    // 如果有file属性,说明是新上传的图片,需要调用上传接口
    // 如果只有url属性,说明是从数据库加载的图片,直接使用原URL
    if (photo.file) {
      uploadedImages.elderPhoto = await uploadSingleImage(photo)
    } else if (photo.url) {
      uploadedImages.elderPhoto = photo.url
    }
  }

  // 上传身份证正面
  if (formData.value.idCardFront.length > 0) {
    const front = formData.value.idCardFront[0]
    if (front.file) {
      uploadedImages.idCardFront = await uploadSingleImage(front)
    } else if (front.url) {
      uploadedImages.idCardFront = front.url
    }
  }

  // 上传身份证反面
  if (formData.value.idCardBack.length > 0) {
    const back = formData.value.idCardBack[0]
    if (back.file) {
      uploadedImages.idCardBack = await uploadSingleImage(back)
    } else if (back.url) {
      uploadedImages.idCardBack = back.url
    }
  }

  return uploadedImages
}

// 上传单张图片
const uploadSingleImage = async (file) => {
  try {
    const formData = new FormData()
    formData.append('file', file.file)

    const token = getToken()
    console.log('上传图片使用的token:', token) // 调试日志

    const response = await fetch('/api/common/upload', {
      method: 'POST',
      headers: {
        'Authorization': token ? 'Bearer ' + token : ''
      },
      body: formData
    })

    const result = await response.json()
    console.log('图片上传返回结果:', result) // 调试日志

    if (result.code === 200) {
      // 若依框架的文件上传接口，url字段直接在根级别
      if (result.url) {
        console.log('上传成功，图片URL:', result.url)
        return result.url // 返回图片路径
      } else {
        console.error('上传返回数据中没有url字段:', result)
        throw new Error('上传返回数据中没有url字段')
      }
    } else {
      throw new Error(result.msg || '上传失败')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    throw error
  }
}

// 加载老人信息(编辑模式)
const loadElderInfo = async () => {
  if (!route.query.id) return

  isEdit.value = true

  try {
    // 调用后端接口获取老人信息
    const token = getToken()
    const response = await fetch(`/api/h5/user/getElderById?elderId=${route.query.id}`, {
      method: 'GET',
      headers: {
        'Authorization': token ? `Bearer ${token}` : ''
      }
    })

    const result = await response.json()
    console.log('获取老人信息返回:', result)

    if (result.code === 200 && result.data) {
      const { elderInfo, attachments, familyRelation } = result.data

      // 填充表单基本数据
      formData.value = {
        elderPhoto: [],
        idCardFront: [],
        idCardBack: [],
        name: elderInfo.elderName || '',
        relation: relationTypeMap[familyRelation?.relationType || '0'],
        relationType: familyRelation?.relationType || '0',
        age: elderInfo.age ? elderInfo.age.toString() : '',
        idCard: elderInfo.idCard || '',
        phone: elderInfo.phone || '',
        address: elderInfo.address || '',
        healthStatus: elderInfo.healthStatus || '',
        medicalHistory: ''
      }

      // 设置老人照片
      if (elderInfo.photoPath) {
        formData.value.elderPhoto = [{
          url: elderInfo.photoPath,
          file: null,
          isImage: true
        }]
      }

      // 设置身份证照片
      if (attachments && attachments.length > 0) {
        attachments.forEach(attachment => {
          if (attachment.attachmentType === '1') {
            // 身份证正面
            formData.value.idCardFront = [{
              url: attachment.filePath,
              file: null,
              isImage: true
            }]
          } else if (attachment.attachmentType === '2') {
            // 身份证反面
            formData.value.idCardBack = [{
              url: attachment.filePath,
              file: null,
              isImage: true
            }]
          }
        })
      }

      console.log('老人信息加载成功:', formData.value)
    } else {
      showToast(result.msg || '加载失败')
      router.back()
    }
  } catch (error) {
    console.error('加载老人信息失败:', error)
    showToast('加载失败')
    router.back()
  }
}

onMounted(() => {
  loadElderInfo()
})
</script>

<style scoped>
.elder-form-page {
  min-height: 100vh;
  background-color: #f5f6fc;
  padding-bottom: 80px;
}

.form-content {
  padding: 12px;
}

/* 导航栏 */
:deep(.van-nav-bar) {
  background: linear-gradient(135deg, #1281ff 0%, #4fc7ff 100%);
}

:deep(.van-nav-bar__title) {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

:deep(.van-nav-bar .van-icon) {
  color: #fff;
}

:deep(.van-hairline--bottom::after) {
  border: none;
}

/* 卡片组 */
:deep(.van-cell-group) {
  margin-bottom: 12px;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

:deep(.van-cell-group__title) {
  padding: 12px 12px 8px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  background: transparent;
}

/* 表单字段 */
:deep(.van-cell) {
  padding: 12px;
  background: #fff;
  font-size: 15px;
}

:deep(.van-cell::after) {
  border-color: #f0f0f0;
}

:deep(.van-field__label) {
  color: #666;
  font-weight: 500;
  width: 90px;
}

:deep(.van-field__control) {
  color: #333;
  font-size: 15px;
}

:deep(.van-field__control::placeholder) {
  color: #ccc;
}

:deep(.van-field__error-message) {
  color: #ff4d4f;
  font-size: 12px;
}

/* 只读字段样式 */
:deep(.van-field[readonly]) {
  background: #fafafa;
}

:deep(.van-field[readonly] .van-field__control) {
  color: #333;
}

/* 图片上传 */
.upload-item {
  padding: 10px 12px;
  background: #fff;
  margin-bottom: 0;
}

.upload-item:last-child {
  margin-bottom: 0;
}

.upload-label {
  font-size: 14px;
  font-weight: 500;
  color: #666;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.upload-label::before {
  content: '';
  width: 3px;
  height: 14px;
  background: linear-gradient(180deg, #0f73ff 0%, #4fc7ff 100%);
  border-radius: 2px;
}

.upload-section {
  padding: 0;
}

/* 上传区域 */
.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

/* 图片项 */
.image-item {
  position: relative;
  width: 86px;
  height: 86px;
  border-radius: 10px;
  overflow: hidden;
}

.uploaded-image {
  width: 86px;
  height: 86px;
  border-radius: 10px;
  object-fit: cover;
  border: 1px solid #cdced5;
  cursor: pointer;
}

/* 删除按钮 - 右上角圆形 */
.delete-btn {
  position: absolute;
  top: -4px;
  right: -4px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 10;
}

.delete-btn:active {
  background: rgba(0, 0, 0, 0.7);
}

/* 上传按钮 */
.upload-btn {
  width: 86px;
  height: 86px;
  border-radius: 10px;
  border: 1px solid #cdced5;
  background: #fafbff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
}

.upload-btn:active {
  background: #f0f2ff;
}

.upload-btn .upload-text {
  color: #cecfd7;
  font-size: 11px;
}

/* 提交按钮 */
.submit-section {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 12px 16px;
  background: linear-gradient(to top, #fff 80%, rgba(255, 255, 255, 0.9));
  box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.06);
  z-index: 100;
}

:deep(.submit-section .van-button) {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 24px;
  background: linear-gradient(135deg, #1281ff 0%, #4fc7ff 100%);
  border: none;
  box-shadow: 0 6px 20px rgba(18, 129, 255, 0.3);
  transition: all 0.3s ease;
}

:deep(.submit-section .van-button:active) {
  transform: translateY(1px);
  box-shadow: 0 4px 16px rgba(18, 129, 255, 0.25);
}

/* 选择器样式优化 */
:deep(.van-picker) {
  border-radius: 16px 16px 0 0;
}

:deep(.van-picker__toolbar) {
  padding: 12px;
  background: #f8f9fc;
}

:deep(.van-picker__title) {
  font-weight: 600;
  color: #333;
}

:deep(.van-picker__confirm) {
  color: #1281ff;
  font-weight: 600;
}

:deep(.van-picker__cancel) {
  color: #999;
}

/* 必填星号 */
:deep(.van-field--required .van-field__label::before) {
  color: #ff4d4f;
  margin-right: 2px;
}
</style>
