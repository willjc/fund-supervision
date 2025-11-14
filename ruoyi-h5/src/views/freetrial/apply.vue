<template>
  <div class="freetrial-apply-page">
    <van-nav-bar title="免费入住" left-arrow @click-left="$router.back()" fixed placeholder />

    <div class="apply-content">
      <!-- 试入住流程 -->
      <div class="process-section">
        <div class="process-title">试入住流程</div>
        <div class="process-steps">
          <div class="process-step">
            <div class="step-icon">
              <van-icon name="edit" size="24" />
            </div>
            <div class="step-text">线上报名</div>
          </div>
          <div class="process-arrow">
            <van-icon name="arrow" />
          </div>
          <div class="process-step">
            <div class="step-icon">
              <van-icon name="checked" size="24" />
            </div>
            <div class="step-text">详评审核</div>
          </div>
          <div class="process-arrow">
            <van-icon name="arrow" />
          </div>
          <div class="process-step">
            <div class="step-icon">
              <van-icon name="home-o" size="24" />
            </div>
            <div class="step-text">审核通过<br/>免费入住</div>
          </div>
        </div>
      </div>

      <!-- 入住人员信息 -->
      <van-form @submit="onSubmit">
        <van-cell-group inset title="入住人员信息">
          <van-field
            v-model="formData.checkInTime"
            label="入住时间"
            placeholder="请选择入住时间"
            readonly
            required
            @click="showDatePicker = true"
          >
            <template #button>
              <van-icon name="calendar-o" />
            </template>
          </van-field>

          <van-field
            v-model="formData.elderName"
            label="入住人姓名"
            placeholder="请输入入住人姓名"
            required
          />

          <van-field
            v-model="formData.gender"
            label="性别"
            readonly
            required
            @click="showGenderPicker = true"
          >
            <template #button>
              <van-icon name="arrow-down" />
            </template>
          </van-field>

          <van-field
            v-model="formData.contactPhone"
            label="联系电话"
            placeholder="请输入联系电话"
            type="tel"
            required
          />

          <van-field
            v-model="formData.idCard"
            label="身份证号"
            placeholder="请输入身份证号"
            required
          />

          <van-field
            v-model="formData.emergencyContact"
            label="应急联系人"
            placeholder="请输入应急联系人"
            required
          />

          <van-field
            v-model="formData.emergencyPhone"
            label="应急电话"
            placeholder="请输入应急电话"
            type="tel"
            required
          />

          <van-field
            v-model="formData.relationship"
            label="与入住人关系"
            placeholder="请输入与入住人关系"
            required
          />
        </van-cell-group>

        <!-- 能力评估 -->
        <van-cell-group inset title="能力评估">
          <van-field
            v-model="formData.communication"
            label="沟通能力"
            readonly
            required
            @click="showAbilityPicker('communication')"
          >
            <template #button>
              <van-icon name="arrow-down" />
            </template>
          </van-field>

          <van-field
            v-model="formData.walking"
            label="走路能力"
            readonly
            required
            @click="showAbilityPicker('walking')"
          >
            <template #button>
              <van-icon name="arrow-down" />
            </template>
          </van-field>

          <van-field
            v-model="formData.dailyLife"
            label="日常生活"
            readonly
            required
            @click="showAbilityPicker('dailyLife')"
          >
            <template #button>
              <van-icon name="arrow-down" />
            </template>
          </van-field>

          <van-field
            v-model="formData.vision"
            label="视力情况"
            readonly
            required
            @click="showAbilityPicker('vision')"
          >
            <template #button>
              <van-icon name="arrow-down" />
            </template>
          </van-field>

          <van-field
            v-model="formData.hearing"
            label="听力情况"
            readonly
            required
            @click="showAbilityPicker('hearing')"
          >
            <template #button>
              <van-icon name="arrow-down" />
            </template>
          </van-field>

          <van-field
            v-model="formData.disease"
            label="患病情况"
            type="textarea"
            placeholder="如患病请输入患病名称及症状"
            rows="3"
            maxlength="200"
            show-word-limit
          />
        </van-cell-group>

        <!-- 体检报告 -->
        <van-cell-group inset title="体检报告">
          <div class="upload-section">
            <div class="upload-tip">请上传入住人一年内体检报告</div>
            <van-uploader v-model="formData.healthReports" :max-count="3" preview-size="80">
              <div class="upload-button">
                <van-icon name="photograph" size="40" />
                <div>上传图片<br/>最多3张</div>
              </div>
            </van-uploader>
          </div>
        </van-cell-group>

        <!-- 提交按钮 -->
        <div class="submit-section">
          <van-button round block type="primary" native-type="submit">
            免费入住申请
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        v-model="selectedDate"
        title="选择入住时间"
        :min-date="minDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>

    <!-- 性别选择器 -->
    <van-popup v-model:show="showGenderPicker" position="bottom">
      <van-picker
        :columns="genderOptions"
        @confirm="onGenderConfirm"
        @cancel="showGenderPicker = false"
      />
    </van-popup>

    <!-- 能力评估选择器 -->
    <van-popup v-model:show="showAbilityPickerDialog" position="bottom">
      <van-picker
        :columns="abilityOptions"
        @confirm="onAbilityConfirm"
        @cancel="showAbilityPickerDialog = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast, showDialog } from 'vant'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

// 表单数据
const formData = ref({
  checkInTime: '',
  elderName: '',
  gender: '',
  contactPhone: '',
  idCard: '',
  emergencyContact: '',
  emergencyPhone: '',
  relationship: '',
  communication: '',
  walking: '',
  dailyLife: '',
  vision: '',
  hearing: '',
  disease: '',
  healthReports: []
})

// 日期选择器
const showDatePicker = ref(false)
const currentDate = new Date()
const selectedDate = ref([
  currentDate.getFullYear().toString(),
  (currentDate.getMonth() + 1).toString().padStart(2, '0'),
  currentDate.getDate().toString().padStart(2, '0')
])
const minDate = ref(new Date())

// 性别选择器
const showGenderPicker = ref(false)
const genderOptions = [
  { text: '男', value: '男' },
  { text: '女', value: '女' }
]

// 能力评估选择器
const showAbilityPickerDialog = ref(false)
const currentAbilityField = ref('')
const abilityOptions = [
  { text: '请选择', value: '' },
  { text: '良好', value: '良好' },
  { text: '一般', value: '一般' },
  { text: '较差', value: '较差' }
]

// 日期确认
const onDateConfirm = ({ selectedValues }) => {
  formData.value.checkInTime = `${selectedValues[0]}-${selectedValues[1].padStart(2, '0')}-${selectedValues[2].padStart(2, '0')}`
  showDatePicker.value = false
}

// 性别确认
const onGenderConfirm = (value) => {
  formData.value.gender = value.selectedOptions[0].text
  showGenderPicker.value = false
}

// 显示能力评估选择器
const showAbilityPicker = (field) => {
  currentAbilityField.value = field
  showAbilityPickerDialog.value = true
}

// 能力评估确认
const onAbilityConfirm = (value) => {
  const selectedValue = value.selectedOptions[0].text
  if (currentAbilityField.value) {
    formData.value[currentAbilityField.value] = selectedValue
  }
  showAbilityPickerDialog.value = false
}

// 提交表单
const onSubmit = async () => {
  // 验证必填字段
  if (!formData.value.checkInTime) {
    showToast('请选择入住时间')
    return
  }
  if (!formData.value.elderName) {
    showToast('请输入入住人姓名')
    return
  }
  if (!formData.value.gender) {
    showToast('请选择性别')
    return
  }
  if (!formData.value.contactPhone) {
    showToast('请输入联系电话')
    return
  }
  if (!formData.value.idCard) {
    showToast('请输入身份证号')
    return
  }
  if (!formData.value.emergencyContact) {
    showToast('请输入应急联系人')
    return
  }
  if (!formData.value.emergencyPhone) {
    showToast('请输入应急电话')
    return
  }
  if (!formData.value.relationship) {
    showToast('请输入与入住人关系')
    return
  }

  // 验证能力评估
  if (!formData.value.communication || formData.value.communication === '请选择') {
    showToast('请评估沟通能力')
    return
  }
  if (!formData.value.walking || formData.value.walking === '请选择') {
    showToast('请评估走路能力')
    return
  }
  if (!formData.value.dailyLife || formData.value.dailyLife === '请选择') {
    showToast('请评估日常生活')
    return
  }
  if (!formData.value.vision || formData.value.vision === '请选择') {
    showToast('请评估视力情况')
    return
  }
  if (!formData.value.hearing || formData.value.hearing === '请选择') {
    showToast('请评估听力情况')
    return
  }

  // 显示协议对话框
  try {
    await showDialog({
      title: '免费试入住须知',
      message: `尊敬的申请人:

欢迎您申请我院免费试入住服务。为了保障您的权益,请仔细阅读以下入住须知:

一、试入住基本规定

1. 试入住期限: 免费试入住期限为7天(自然日),期满后如需继续入住,需办理正式入住手续并缴纳相关费用。

2. 适用对象: 本服务仅适用于年满60周岁、身体健康状况良好、生活能够自理或半自理的老年人。患有精神疾病、传染性疾病或其他不适合集体生活疾病的老年人,恕不接受试入住申请。

3. 申请材料: 申请人需提供本人身份证原件及复印件、近期体检报告(一年内)、紧急联系人信息等相关材料。材料不全或不符合要求的,我院有权拒绝试入住申请。

二、试入住期间服务内容

1. 住宿服务: 我院将根据床位情况安排试入住房间,房间类型可能与正式入住时不同,敬请谅解。

2. 餐饮服务: 提供一日三餐,包括早餐、中餐、晚餐,饮食以营养均衡为原则,如有特殊饮食需求请提前告知。

3. 基础护理: 提供生活照料、健康监测等基础护理服务,具体服务内容根据老人实际情况而定。

4. 文娱活动: 可参加我院组织的各类文体娱乐活动,丰富试入住期间的生活。

三、试入住期间注意事项

1. 安全管理: 试入住期间,老人应遵守我院各项规章制度,不得擅自离院。如需外出,必须征得工作人员同意并办理登记手续。

2. 物品管理: 请妥善保管个人贵重物品,我院对老人个人财物遗失不承担责任。建议不要携带大量现金和贵重物品。

3. 健康状况: 如在试入住期间出现身体不适,应及时告知工作人员。我院将根据情况提供必要的协助,但不承担医疗责任。

4. 陪护规定: 家属可在规定时间内探视,但不得在院内过夜。如需陪护,须提前向我院申请并获得批准。

四、费用说明

1. 免费项目: 试入住期间的床位费、餐费、基础护理费全部免费。

2. 自费项目: 个人生活用品、特殊护理服务、医疗费用等需自行承担。

3. 押金规定: 试入住无需缴纳押金,但如造成我院设施设备损坏,需照价赔偿。

五、试入住评估

1. 能力评估: 试入住期间,我院将对老人的生活自理能力、认知能力、健康状况等进行综合评估。

2. 评估结果: 评估结果将作为是否接受正式入住的重要依据。评估不合格或不适合在我院养老的,我院有权拒绝其正式入住申请。

3. 沟通反馈: 试入住结束后,我院将与申请人及家属进行沟通,告知评估结果和入住建议。

六、入住转化

1. 正式入住: 试入住期满且评估合格后,如愿意正式入住,需在试入住结束后3个工作日内办理入住手续。

2. 费用缴纳: 正式入住需缴纳押金、会员费及首期服务费,具体金额以当时公布的收费标准为准。

3. 床位保留: 试入住床位不作为正式入住床位保留,正式入住时将根据实际情况重新安排房间。

七、其他约定

1. 中途退出: 试入住期间如因个人原因提前退出,需提前1天告知我院,并配合办理相关手续。

2. 违规处理: 如在试入住期间违反我院规章制度,情节严重的,我院有权终止试入住并要求立即离院。

3. 疫情防控: 根据疫情防控要求,试入住前需提供健康码、行程卡等相关证明,并配合我院进行体温检测等防控措施。

4. 信息真实: 申请人应确保所提供的个人信息、健康状况等资料真实准确,如有隐瞒或虚报,我院有权取消试入住资格。

5. 免责声明: 因老人自身疾病、意外伤害等非我院过错原因造成的人身财产损失,我院不承担责任。

八、最终解释权

本须知的最终解释权归本养老机构所有。如有疑问,请咨询我院工作人员。

特此告知,敬请知悉!`,
      confirmButtonText: '我已阅读并同意',
      showCancelButton: true,
      cancelButtonText: '取消',
      closeOnClickOverlay: false,
      className: 'notice-dialog'
    })

    // 模拟提交
    await new Promise(resolve => setTimeout(resolve, 500))

    // 跳转到成功页面
    router.push({
      name: 'FreeTrialSuccess',
      query: {
        elderName: formData.value.elderName,
        checkInTime: formData.value.checkInTime,
        institutionId: route.params.institutionId
      }
    })
  } catch (error) {
    // 用户取消
  }
}
</script>

<style scoped>
.freetrial-apply-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 20px;
}

.apply-content {
  padding-top: 12px;
}

/* 试入住流程 */
.process-section {
  background: #fff;
  padding: 20px 16px;
  margin-bottom: 12px;
}

.process-title {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 20px;
}

.process-steps {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.process-step {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.step-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
}

.step-text {
  font-size: 12px;
  color: #666;
  text-align: center;
  line-height: 1.4;
}

.process-arrow {
  color: #999;
  margin: 0 8px;
}

/* 上传区域 */
.upload-section {
  padding: 16px;
}

.upload-tip {
  font-size: 13px;
  color: #999;
  margin-bottom: 12px;
}

.upload-button {
  width: 80px;
  height: 80px;
  border: 1px dashed #dcdee0;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #969799;
  font-size: 12px;
  text-align: center;
  line-height: 1.4;
}

/* 提交按钮 */
.submit-section {
  padding: 20px 16px;
}

/* 须知对话框样式 */
:deep(.notice-dialog) {
  .van-dialog__message {
    max-height: 400px;
    overflow-y: auto;
    text-align: left;
    font-size: 14px;
    line-height: 1.8;
    white-space: pre-line;
    padding: 20px;
  }
}
</style>
