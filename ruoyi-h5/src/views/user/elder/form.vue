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
            <div class="upload-section">
              <van-uploader
                v-model="formData.elderPhoto"
                :max-count="1"
                :max-size="5 * 1024 * 1024"
                @oversize="onOversize"
                @delete="onElderPhotoDelete"
              >
                <template #default>
                  <div class="upload-placeholder">
                    <van-icon name="photograph" size="32" color="#999" />
                    <div class="upload-text">老人照片</div>
                  </div>
                </template>
              </van-uploader>
            </div>
          </div>

          <!-- 身份证正面 -->
          <div class="upload-item">
            <div class="upload-label">身份证正面（1张）</div>
            <div class="upload-section">
              <van-uploader
                v-model="formData.idCardFront"
                :max-count="1"
                :max-size="5 * 1024 * 1024"
                @oversize="onOversize"
                @delete="onIdCardFrontDelete"
              >
                <template #default>
                  <div class="upload-placeholder">
                    <van-icon name="idcard" size="32" color="#999" />
                    <div class="upload-text">身份证正面</div>
                  </div>
                </template>
              </van-uploader>
            </div>
          </div>

          <!-- 身份证反面 -->
          <div class="upload-item">
            <div class="upload-label">身份证反面（1张）</div>
            <div class="upload-section">
              <van-uploader
                v-model="formData.idCardBack"
                :max-count="1"
                :max-size="5 * 1024 * 1024"
                @oversize="onOversize"
                @delete="onIdCardBackDelete"
              >
                <template #default>
                  <div class="upload-placeholder">
                    <van-icon name="idcard" size="32" color="#999" />
                    <div class="upload-text">身份证反面</div>
                  </div>
                </template>
              </van-uploader>
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
import { showToast } from 'vant'
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
  background-color: #f5f5f5;
  padding-bottom: 80px;
}

.form-content {
  padding: 12px 0 20px;
}

.van-cell-group {
  margin-bottom: 12px;
}

/* 图片上传 */
.upload-item {
  margin-bottom: 16px;
}

.upload-item:last-child {
  margin-bottom: 0;
}

.upload-label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}

.upload-section {
  padding: 16px;
}

.upload-placeholder {
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
