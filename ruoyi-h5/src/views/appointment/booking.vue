<template>
  <div class="appointment-booking">
    <van-nav-bar title="预约参观" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="booking-content">
      <!-- 机构信息 -->
      <div class="institution-info">
        <van-image
          width="80"
          height="80"
          :src="institutionInfo.coverImage"
          fit="cover"
          round
        />
        <div class="info-text">
          <div class="info-name">{{ institutionInfo.name }}</div>
          <div class="info-address">
            <van-icon name="location-o" size="12" />
            {{ institutionInfo.address }}
          </div>
        </div>
      </div>

      <!-- 预约表单 -->
      <van-form @submit="onSubmit">
        <van-cell-group title="预约信息" inset>
          <van-field
            v-model="formData.visitorName"
            name="visitorName"
            label="参观人姓名"
            placeholder="请输入参观人姓名"
            :rules="[{ required: true, message: '请输入参观人姓名' }]"
          />
          <van-field
            v-model="formData.visitorPhone"
            name="visitorPhone"
            type="tel"
            label="联系电话"
            placeholder="请输入联系电话"
            :rules="[
              { required: true, message: '请输入联系电话' },
              { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号' }
            ]"
          />
          <van-field
            v-model="formData.visitDate"
            name="visitDate"
            label="参观日期"
            placeholder="请选择参观日期"
            readonly
            right-icon="arrow"
            @click="showDatePicker = true"
            :rules="[{ required: true, message: '请选择参观日期' }]"
          />
          <van-field
            v-model="formData.visitTime"
            name="visitTime"
            label="参观时间"
            placeholder="请选择参观时间"
            readonly
            right-icon="arrow"
            @click="showTimePicker = true"
            :rules="[{ required: true, message: '请选择参观时间' }]"
          />
          <van-field
            v-model="formData.visitorCount"
            name="visitorCount"
            type="number"
            label="参观人数"
            placeholder="请输入参观人数"
            :rules="[{ required: true, message: '请输入参观人数' }]"
          />
          <van-field
            v-model="formData.remark"
            name="remark"
            type="textarea"
            label="备注说明"
            placeholder="请输入备注说明(选填)"
            rows="3"
            maxlength="200"
            show-word-limit
          />
        </van-cell-group>

        <div class="submit-bar">
          <van-button round block type="primary" native-type="submit">
            提交预约
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        v-model="currentDate"
        title="选择日期"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>

    <!-- 时间选择器 -->
    <van-popup v-model:show="showTimePicker" position="bottom">
      <van-time-picker
        v-model="currentTime"
        title="选择时间"
        :columns-type="['hour', 'minute']"
        @confirm="onTimeConfirm"
        @cancel="showTimePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'

const route = useRoute()
const router = useRouter()

// 机构信息
const institutionInfo = ref({
  name: '郑州市金水区花园口社区养老服务中心',
  address: '郑州市金水区花园口镇花园路123号',
  coverImage: 'https://via.placeholder.com/80x80'
})

// 表单数据
const formData = ref({
  visitorName: '',
  visitorPhone: '',
  visitDate: '',
  visitTime: '',
  visitorCount: 1,
  remark: ''
})

// 日期时间选择器
const showDatePicker = ref(false)
const showTimePicker = ref(false)
const currentDate = ref(['2025', '01', '14'])
const currentTime = ref(['09', '00'])

// 日期范围
const minDate = new Date()
const maxDate = new Date()
maxDate.setMonth(maxDate.getMonth() + 3)

// 确认日期
const onDateConfirm = ({ selectedValues }) => {
  formData.value.visitDate = selectedValues.join('-')
  showDatePicker.value = false
}

// 确认时间
const onTimeConfirm = ({ selectedValues }) => {
  formData.value.visitTime = selectedValues.join(':')
  showTimePicker.value = false
}

// 提交预约
const onSubmit = async (values) => {
  try {
    await showConfirmDialog({
      title: '确认预约',
      message: `确认预约 ${formData.value.visitDate} ${formData.value.visitTime} 参观吗?`
    })

    // 模拟提交
    await new Promise(resolve => setTimeout(resolve, 500))

    showToast('预约成功')

    // 跳转到成功页面
    router.replace({
      name: 'AppointmentSuccess',
      query: {
        institutionName: institutionInfo.value.name,
        visitDate: formData.value.visitDate,
        visitTime: formData.value.visitTime,
        visitorName: formData.value.visitorName,
        visitorPhone: formData.value.visitorPhone
      }
    })
  } catch (error) {
    if (error !== 'cancel') {
      showToast('预约失败')
    }
  }
}

onMounted(() => {
  // 可以从路由参数获取机构信息
  if (route.params.institutionId) {
    // 这里应该调用API获取机构信息
  }
})
</script>

<style scoped>
.appointment-booking {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.booking-content {
  padding: 16px;
}

.institution-info {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

.info-text {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}

.info-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.info-address {
  font-size: 13px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.submit-bar {
  padding: 16px;
}
</style>
