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
        <!-- 图片上传 -->
        <van-cell-group inset title="照片上传">
          <div class="upload-section">
            <van-uploader
              v-model="formData.images"
              multiple
              :max-count="3"
              :max-size="5 * 1024 * 1024"
              @oversize="onOversize"
            >
              <template #default>
                <div class="upload-placeholder">
                  <van-icon name="photograph" size="32" color="#999" />
                  <div class="upload-text">上传照片(最多3张)</div>
                </div>
              </template>
            </van-uploader>
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

const router = useRouter()
const route = useRoute()

// 是否为编辑模式
const isEdit = ref(false)

// 表单数据
const formData = ref({
  images: [],
  name: '',
  relation: '',
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

// 关系选项
const relationOptions = [
  { text: '本人', value: '本人' },
  { text: '父亲', value: '父亲' },
  { text: '母亲', value: '母亲' },
  { text: '配偶', value: '配偶' },
  { text: '子女', value: '子女' },
  { text: '其他', value: '其他' }
]

// 健康状况选项
const healthOptions = [
  { text: '健康', value: '健康' },
  { text: '良好', value: '良好' },
  { text: '一般', value: '一般' },
  { text: '较差', value: '较差' },
  { text: '需要护理', value: '需要护理' }
]

// 关系选择确认
const onRelationConfirm = (value) => {
  formData.value.relation = value.selectedOptions[0].text
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

// 提交表单
const onSubmit = async (values) => {
  try {
    // 模拟提交
    await new Promise(resolve => setTimeout(resolve, 500))

    showToast(isEdit.value ? '修改成功' : '添加成功')

    setTimeout(() => {
      router.back()
    }, 1000)
  } catch (error) {
    showToast('操作失败,请重试')
  }
}

// 加载老人信息(编辑模式)
const loadElderInfo = async () => {
  if (!route.query.id) return

  isEdit.value = true

  try {
    // 模拟加载数据
    await new Promise(resolve => setTimeout(resolve, 300))

    // 填充表单数据
    formData.value = {
      images: [],
      name: '张伟',
      relation: '本人',
      age: '88',
      idCard: '430222188025656565',
      phone: '13800138000',
      address: '河南省郑州市金水区花园路123号',
      healthStatus: '良好',
      medicalHistory: '高血压'
    }
  } catch (error) {
    showToast('加载失败')
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
