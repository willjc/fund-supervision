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
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="护理费(元/月)" prop="nursingFee">
                <el-row :gutter="10">
                  <el-col :span="12">
                    <el-input-number
                      v-model="form.nursingFee.min"
                      :min="0"
                      :precision="2"
                      placeholder="护理费-最低费用"
                      style="width: 100%"></el-input-number>
                  </el-col>
                  <el-col :span="12">
                    <el-input-number
                      v-model="form.nursingFee.max"
                      :min="0"
                      :precision="2"
                      placeholder="护理费-最高费用"
                      style="width: 100%"></el-input-number>
                  </el-col>
                </el-row>
                <div class="form-tip">护理费最低和最高费用</div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="床位费(元/月)" prop="bedFee">
                <el-row :gutter="10">
                  <el-col :span="12">
                    <el-input-number
                      v-model="form.bedFee.min"
                      :min="0"
                      :precision="2"
                      placeholder="床位费-最低费用"
                      style="width: 100%"></el-input-number>
                  </el-col>
                  <el-col :span="12">
                    <el-input-number
                      v-model="form.bedFee.max"
                      :min="0"
                      :precision="2"
                      placeholder="床位费-最高费用"
                      style="width: 100%"></el-input-number>
                  </el-col>
                </el-row>
                <div class="form-tip">床位费最低和最高费用</div>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="膳食费(元/月)" prop="mealFee">
                <el-row :gutter="10">
                  <el-col :span="12">
                    <el-input-number
                      v-model="form.mealFee.min"
                      :min="0"
                      :precision="2"
                      placeholder="膳食费-最低费用"
                      style="width: 100%"></el-input-number>
                  </el-col>
                  <el-col :span="12">
                    <el-input-number
                      v-model="form.mealFee.max"
                      :min="0"
                      :precision="2"
                      placeholder="膳食费-最高费用"
                      style="width: 100%"></el-input-number>
                  </el-col>
                </el-row>
                <div class="form-tip">膳食费最低和最高费用</div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="总费用(元/月)">
                <el-row :gutter="10">
                  <el-col :span="12">
                    <el-input
                      v-model="totalFee.min"
                      readonly
                      placeholder="总费用-最低(自动计算)"
                      style="width: 100%"></el-input>
                  </el-col>
                  <el-col :span="12">
                    <el-input
                      v-model="totalFee.max"
                      readonly
                      placeholder="总费用-最高(自动计算)"
                      style="width: 100%"></el-input>
                  </el-col>
                </el-row>
                <div class="form-tip">总费用自动计算：护理费+床位费+膳食费</div>
              </el-form-item>
            </el-col>
          </el-row>
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
        institutionId: null,
        institutionName: '',
        creditCode: '',
        legalPerson: '',
        contactPhone: '',
        address: '',
        approvedBeds: 0,
        actualElders: 0,
        serviceItems: [],
        // 结构化费用数据
        nursingFee: { min: 0, max: 0 },
        bedFee: { min: 0, max: 0 },
        mealFee: { min: 0, max: 0 },
        // 保留原有字段兼容
        chargeStandard: '',
        businessLicenseExpiry: '',
        foodLicenseExpiry: '',
        doctorCount: 0,
        nurseCount: 0,
        caregiverCount: 0,
        facilities: '',
        isPublic: false,
        publicNotice: '',
        coverImages: null
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
  computed: {
    // 计算总费用
    totalFee() {
      const nursingMin = this.form.nursingFee.min || 0
      const nursingMax = this.form.nursingFee.max || 0
      const bedMin = this.form.bedFee.min || 0
      const bedMax = this.form.bedFee.max || 0
      const mealMin = this.form.mealFee.min || 0
      const mealMax = this.form.mealFee.max || 0

      return {
        min: (nursingMin + bedMin + mealMin).toFixed(2),
        max: (nursingMax + bedMax + mealMax).toFixed(2)
      }
    }
  },
  watch: {
    // 监听费用变化，自动更新总费用显示
    'form.nursingFee.min': {
      handler() {
        this.$forceUpdate()
      },
      deep: true
    },
    'form.nursingFee.max': {
      handler() {
        this.$forceUpdate()
      },
      deep: true
    },
    'form.bedFee.min': {
      handler() {
        this.$forceUpdate()
      },
      deep: true
    },
    'form.bedFee.max': {
      handler() {
        this.$forceUpdate()
      },
      deep: true
    },
    'form.mealFee.min': {
      handler() {
        this.$forceUpdate()
      },
      deep: true
    },
    'form.mealFee.max': {
      handler() {
        this.$forceUpdate()
      },
      deep: true
    }
  },
  created() {
    this.loadPublicityData()
  },
  methods: {
    // 加载公示信息
    loadPublicityData() {
      this.$http.get('/pension/publicity/info').then(response => {
        if (response.code === 200) {
          const data = response.data
          this.form = {
            institutionId: data.institutionId,
            institutionName: data.institutionName,
            creditCode: data.creditCode,
            legalPerson: data.legalPerson,
            contactPhone: data.contactPhone,
            address: data.address,
            approvedBeds: data.approvedBeds,
            actualElders: data.actualElders,
            serviceItems: data.serviceItems,
            // 结构化费用数据
            nursingFee: data.nursingFee || { min: null, max: null },
            bedFee: data.bedFee || { min: null, max: null },
            mealFee: data.mealFee || { min: null, max: null },
            // 保留原有字段
            chargeStandard: data.chargeStandard || '',
            businessLicenseExpiry: data.businessLicenseExpiry,
            foodLicenseExpiry: data.foodLicenseExpiry,
            doctorCount: data.doctorCount,
            nurseCount: data.nurseCount,
            caregiverCount: data.caregiverCount,
            facilities: data.facilities,
            isPublic: data.isPublic,
            publicNotice: data.publicNotice,
            coverImages: data.coverImages
          }

          // 处理图片列表
          if (data.coverImages) {
            try {
              const imageUrls = JSON.parse(data.coverImages)
              this.environmentPictures = imageUrls.map((url, index) => ({
                name: `图片${index + 1}`,
                url: url
              }))
            } catch (e) {
              this.environmentPictures = []
            }
          } else {
            this.environmentPictures = []
          }
        }
      }).catch(error => {
        console.error('加载公示信息失败:', error)
        this.$message.error('加载公示信息失败')
      })
    },
    // 保存公示信息
    handleSave() {
      this.$refs.publicityForm.validate(valid => {
        if (valid) {
          this.saving = true

          // 准备保存的数据
          const saveData = {
            institutionId: this.form.institutionId,
            nursingFee: this.form.nursingFee,
            bedFee: this.form.bedFee,
            mealFee: this.form.mealFee,
            coverImages: this.form.coverImages,
            legalPerson: this.form.legalPerson,
            contactPhone: this.form.contactPhone,
            address: this.form.address
          }

          this.$http.post('/pension/publicity/save', saveData).then(response => {
            if (response.code === 200) {
              this.$message.success('公示信息保存成功')
            } else {
              this.$message.error(response.msg || '保存失败')
            }
          }).catch(error => {
            console.error('保存公示信息失败:', error)
            this.$message.error('保存失败')
          }).finally(() => {
            this.saving = false
          })
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
      // 更新form中的图片URL列表
      this.form.coverImages = JSON.stringify(fileList.map(item => item.url))
    },
    // 上传成功
    handleUploadSuccess(response, file, fileList) {
      this.environmentPictures = fileList
      // 更新form中的图片URL列表
      this.form.coverImages = JSON.stringify(fileList.map(item => item.url))
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
