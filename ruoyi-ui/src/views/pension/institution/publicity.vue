<template>
  <div class="app-container">
    <el-card class="publicity-card">
      <div slot="header" class="card-header">
        <span>机构公示信息维护</span>
        <el-button type="primary" size="small" @click="handleSave" :loading="saving">保存信息</el-button>
      </div>

      <el-form ref="publicityForm" :model="form" :rules="rules" label-width="150px">
        <!-- 基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="机构名称" prop="institutionName">
              <el-input v-model="form.institutionName" disabled></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="统一社会信用代码" prop="creditCode">
              <el-input v-model="form.creditCode" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="法定代表人" prop="legalPerson">
              <el-input v-model="form.legalPerson" placeholder="请输入法定代表人"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="机构地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入机构地址"></el-input>
        </el-form-item>

        <!-- 服务信息 -->
        <el-divider content-position="left">服务信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="核定床位数" prop="approvedBeds">
              <el-input-number v-model="form.approvedBeds" :min="0" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实际入住人数" prop="actualElders">
              <el-input-number v-model="form.actualElders" :min="0" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="服务项目" prop="serviceItems">
          <el-checkbox-group v-model="form.serviceItems">
            <el-checkbox label="生活照料">生活照料</el-checkbox>
            <el-checkbox label="医疗护理">医疗护理</el-checkbox>
            <el-checkbox label="康复训练">康复训练</el-checkbox>
            <el-checkbox label="精神慰藉">精神慰藉</el-checkbox>
            <el-checkbox label="文化娱乐">文化娱乐</el-checkbox>
            <el-checkbox label="临终关怀">临终关怀</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="收费标准" prop="chargeStandard">
          <el-input
            type="textarea"
            v-model="form.chargeStandard"
            :rows="4"
            placeholder="请输入收费标准说明"></el-input>
        </el-form-item>

        <!-- 资质信息 -->
        <el-divider content-position="left">资质信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="营业执照有效期" prop="businessLicenseExpiry">
              <el-date-picker
                v-model="form.businessLicenseExpiry"
                type="date"
                placeholder="选择日期"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="食品经营许可证期限" prop="foodLicenseExpiry">
              <el-date-picker
                v-model="form.foodLicenseExpiry"
                type="date"
                placeholder="选择日期"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 人员信息 -->
        <el-divider content-position="left">人员信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="医生人数" prop="doctorCount">
              <el-input-number v-model="form.doctorCount" :min="0" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="护士人数" prop="nurseCount">
              <el-input-number v-model="form.nurseCount" :min="0" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="护理员人数" prop="caregiverCount">
              <el-input-number v-model="form.caregiverCount" :min="0" style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 设施信息 -->
        <el-divider content-position="left">设施信息</el-divider>
        <el-form-item label="设施设备" prop="facilities">
          <el-input
            type="textarea"
            v-model="form.facilities"
            :rows="4"
            placeholder="请输入设施设备说明（如医疗设备、康复器材、生活设施等）"></el-input>
        </el-form-item>

        <!-- 公示图片 -->
        <el-divider content-position="left">公示图片</el-divider>
        <el-form-item label="机构环境图片">
          <el-upload
            action="/system/common/upload"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :on-success="handleUploadSuccess"
            :file-list="environmentPictures">
            <i class="el-icon-plus"></i>
          </el-upload>
          <div class="upload-tip">建议上传机构环境、设施照片，最多5张</div>
        </el-form-item>

        <!-- 公示状态 -->
        <el-divider content-position="left">公示状态</el-divider>
        <el-form-item label="是否公示" prop="isPublic">
          <el-switch
            v-model="form.isPublic"
            active-text="公开"
            inactive-text="不公开">
          </el-switch>
          <div class="form-tip">开启后，公众可以在平台上查看机构信息</div>
        </el-form-item>

        <el-form-item label="公示说明" prop="publicNotice">
          <el-input
            type="textarea"
            v-model="form.publicNotice"
            :rows="3"
            placeholder="请输入公示说明或备注"></el-input>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 图片预览对话框 -->
    <el-dialog :visible.sync="dialogVisible" append-to-body>
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'InstitutionPublicity',
  data() {
    return {
      saving: false,
      form: {
        institutionName: '',
        creditCode: '',
        legalPerson: '',
        contactPhone: '',
        address: '',
        approvedBeds: 0,
        actualElders: 0,
        serviceItems: [],
        chargeStandard: '',
        businessLicenseExpiry: '',
        foodLicenseExpiry: '',
        doctorCount: 0,
        nurseCount: 0,
        caregiverCount: 0,
        facilities: '',
        isPublic: false,
        publicNotice: ''
      },
      rules: {
        legalPerson: [
          { required: true, message: '请输入法定代表人', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入机构地址', trigger: 'blur' }
        ],
        approvedBeds: [
          { required: true, message: '请输入核定床位数', trigger: 'blur' }
        ]
      },
      environmentPictures: [],
      dialogImageUrl: '',
      dialogVisible: false
    }
  },
  created() {
    this.loadPublicityData()
  },
  methods: {
    // 加载公示信息
    loadPublicityData() {
      // TODO: 调用后端API获取机构公示信息
      // 临时使用模拟数据
      this.form = {
        institutionName: '幸福养老院',
        creditCode: '91410100MA44XXXX01',
        legalPerson: '张三',
        contactPhone: '13800138000',
        address: '郑州市金水区XX路XX号',
        approvedBeds: 200,
        actualElders: 168,
        serviceItems: ['生活照料', '医疗护理', '康复训练'],
        chargeStandard: '护理费：2000-5000元/月\n床位费：800-1500元/月\n餐费：600元/月',
        businessLicenseExpiry: '2026-12-31',
        foodLicenseExpiry: '2025-12-31',
        doctorCount: 5,
        nurseCount: 15,
        caregiverCount: 30,
        facilities: '配备医疗器械、康复器材、无障碍设施等',
        isPublic: true,
        publicNotice: '本机构为民政部门登记备案的合法养老服务机构'
      }
    },
    // 保存公示信息
    handleSave() {
      this.$refs.publicityForm.validate(valid => {
        if (valid) {
          this.saving = true
          // TODO: 调用后端API保存
          setTimeout(() => {
            this.saving = false
            this.$message.success('公示信息保存成功')
          }, 1000)
        }
      })
    },
    // 图片预览
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url
      this.dialogVisible = true
    },
    // 移除图片
    handleRemove(file, fileList) {
      this.environmentPictures = fileList
    },
    // 上传成功
    handleUploadSuccess(response, file, fileList) {
      this.environmentPictures = fileList
      this.$message.success('图片上传成功')
    }
  }
}
</script>

<style scoped lang="scss">
.publicity-card {
  ::v-deep .el-card__header {
    background: #f5f7fa;
    border-bottom: 1px solid #ebeef5;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-divider {
  margin: 30px 0 20px 0;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

::v-deep .el-upload-list--picture-card .el-upload-list__item {
  width: 120px;
  height: 120px;
}

::v-deep .el-upload--picture-card {
  width: 120px;
  height: 120px;
  line-height: 120px;
}
</style>
