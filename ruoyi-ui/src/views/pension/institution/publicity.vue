<template>
  <div class="app-container">
    <el-card class="publicity-card">
      <div slot="header" class="card-header">
        <div class="header-left">
          <span>机构公示信息维护</span>
        </div>
        <div class="header-right">
          <el-button type="warning" plain size="small" icon="el-icon-setting" @click="handleIconMaintenance">图标维护</el-button>
          <el-button type="primary" size="small" @click="handleSave" :loading="saving">保存信息</el-button>
        </div>
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

        <!-- 设施图片上传 -->
        <el-form-item label="房间设施图片">
          <el-upload
            action="/system/common/upload"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRoomFacilityRemove"
            :on-success="handleRoomFacilityUploadSuccess"
            :file-list="roomFacilityPictures">
            <i class="el-icon-plus"></i>
          </el-upload>
          <div class="upload-tip">上传房间设施图片（单人间、双人间、套间等），最多5张</div>
        </el-form-item>

        <el-form-item label="基础设施图片">
          <el-upload
            action="/system/common/upload"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleBasicFacilityRemove"
            :on-success="handleBasicFacilityUploadSuccess"
            :file-list="basicFacilityPictures">
            <i class="el-icon-plus"></i>
          </el-upload>
          <div class="upload-tip">上传基础设施图片（建筑外观、公共区域、配套设施等），最多5张</div>
        </el-form-item>

        <el-form-item label="园址设施图片">
          <el-upload
            action="/system/common/upload"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleParkFacilityRemove"
            :on-success="handleParkFacilityUploadSuccess"
            :file-list="parkFacilityPictures">
            <i class="el-icon-plus"></i>
          </el-upload>
          <div class="upload-tip">上传园址设施图片（花园、活动场地、休闲区等），最多5张</div>
        </el-form-item>

        <!-- 设施选项 -->
        <el-form-item label="生活设施">
          <el-checkbox-group v-model="selectedLifeFacilities">
            <el-checkbox
              v-for="facility in lifeFacilities"
              :key="facility.id"
              :label="facility.facilityName">
              <svg-icon v-if="facility.iconName" :icon-class="facility.iconName" />
              {{ facility.facilityName }}
            </el-checkbox>
          </el-checkbox-group>
          <div class="form-tip">请选择机构拥有的生活设施</div>
        </el-form-item>

        <el-form-item label="医疗设施">
          <el-checkbox-group v-model="selectedMedicalFacilities">
            <el-checkbox
              v-for="facility in medicalFacilities"
              :key="facility.id"
              :label="facility.facilityName">
              <svg-icon v-if="facility.iconName" :icon-class="facility.iconName" />
              {{ facility.facilityName }}
            </el-checkbox>
          </el-checkbox-group>
          <div class="form-tip">请选择机构拥有的医疗设施</div>
        </el-form-item>

        <!-- 每日服务时间安排 -->
        <el-form-item label="每日服务时间安排">
          <div style="margin-bottom: 15px;">
            <div v-for="(service, index) in dailyServices" :key="index" style="display: flex; align-items: center; margin-bottom: 10px;">
              <el-time-picker
                v-model="service.time"
                placeholder="选择时间"
                format="HH:mm"
                value-format="HH:mm"
                style="width: 120px; margin-right: 10px;"></el-time-picker>
              <el-input
                v-model="service.content"
                placeholder="请输入服务内容"
                style="flex: 1; margin-right: 10px;"></el-input>
              <el-button
                type="danger"
                icon="el-icon-delete"
                size="small"
                @click="removeService(index)">删除</el-button>
            </div>
          </div>
          <el-button
            type="primary"
            icon="el-icon-plus"
            @click="addService"
            size="small">添加服务项</el-button>
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

        <el-form-item label="VR全景图片">
          <el-upload
            action="/system/common/upload"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleVrImageRemove"
            :on-success="handleVrImageUploadSuccess"
            :file-list="vrPicture"
            :limit="1">
            <i class="el-icon-plus"></i>
          </el-upload>
          <div class="upload-tip">建议上传360°全景图片，最多1张</div>
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
import { getLifeFacilities, getMedicalFacilities } from '@/api/pension/facility/icon'

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
        coverImages: null,
        // 新增设施数据字段
        roomFacilities: null,
        basicFacilities: null,
        parkFacilities: null,
        lifeFacilities: null,
        medicalFacilities: null,
        dailyServices: null
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
      vrPicture: [],
      dialogImageUrl: '',
      dialogVisible: false,
      // 新增设施数据变量
      roomFacilityPictures: [],
      basicFacilityPictures: [],
      parkFacilityPictures: [],
      selectedLifeFacilities: [],
      selectedMedicalFacilities: [],
      dailyServices: [],
      // 设施图标配置数据
      lifeFacilities: [],
      medicalFacilities: []
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
  mounted() {
    this.loadFacilityIconConfig()
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
            coverImages: data.coverImages,
            // 新增设施数据字段
            roomFacilities: data.roomFacilities,
            basicFacilities: data.basicFacilities,
            parkFacilities: data.parkFacilities,
            lifeFacilities: data.lifeFacilities,
            medicalFacilities: data.medicalFacilities,
            dailyServices: data.dailyServices,
            vrImage: data.vrImage
          }

          // 处理VR全景图片
          if (data.vrImage) {
            this.vrPicture = [{
              name: 'VR全景图片',
              url: data.vrImage
            }]
          } else {
            this.vrPicture = []
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

          // 处理设施图片列表
          this.handleFacilityImages(data.roomFacilities, this.roomFacilityPictures)
          this.handleFacilityImages(data.basicFacilities, this.basicFacilityPictures)
          this.handleFacilityImages(data.parkFacilities, this.parkFacilityPictures)

          // 处理设施选择数据
          if (data.lifeFacilities) {
            try {
              this.selectedLifeFacilities = JSON.parse(data.lifeFacilities)
            } catch (e) {
              this.selectedLifeFacilities = []
            }
          } else {
            this.selectedLifeFacilities = []
          }

          if (data.medicalFacilities) {
            try {
              this.selectedMedicalFacilities = JSON.parse(data.medicalFacilities)
            } catch (e) {
              this.selectedMedicalFacilities = []
            }
          } else {
            this.selectedMedicalFacilities = []
          }

          if (data.dailyServices) {
            try {
              this.dailyServices = JSON.parse(data.dailyServices)
            } catch (e) {
              this.dailyServices = []
            }
          } else {
            this.dailyServices = []
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
            address: this.form.address,
            // 新增设施数据
            roomFacilities: this.form.roomFacilities,
            basicFacilities: this.form.basicFacilities,
            parkFacilities: this.form.parkFacilities,
            vrImage: this.form.vrImage,
            lifeFacilities: this.selectedLifeFacilities.length > 0 ? JSON.stringify(this.selectedLifeFacilities) : null,
            medicalFacilities: this.selectedMedicalFacilities.length > 0 ? JSON.stringify(this.selectedMedicalFacilities) : null,
            dailyServices: this.dailyServices.length > 0 ? JSON.stringify(this.dailyServices.filter(service => service.time && service.content)) : null
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
    },

    // 设施图片处理方法
    handleRoomFacilityUploadSuccess(response, file, fileList) {
      this.roomFacilityPictures = fileList
      this.form.roomFacilities = JSON.stringify(fileList.map(item => item.url))
      this.$message.success('房间设施图片上传成功')
    },
    handleRoomFacilityRemove(file, fileList) {
      this.roomFacilityPictures = fileList
      this.form.roomFacilities = JSON.stringify(fileList.map(item => item.url))
    },

    handleBasicFacilityUploadSuccess(response, file, fileList) {
      this.basicFacilityPictures = fileList
      this.form.basicFacilities = JSON.stringify(fileList.map(item => item.url))
      this.$message.success('基础设施图片上传成功')
    },
    handleBasicFacilityRemove(file, fileList) {
      this.basicFacilityPictures = fileList
      this.form.basicFacilities = JSON.stringify(fileList.map(item => item.url))
    },

    handleParkFacilityUploadSuccess(response, file, fileList) {
      this.parkFacilityPictures = fileList
      this.form.parkFacilities = JSON.stringify(fileList.map(item => item.url))
      this.$message.success('园址设施图片上传成功')
    },
    handleParkFacilityRemove(file, fileList) {
      this.parkFacilityPictures = fileList
      this.form.parkFacilities = JSON.stringify(fileList.map(item => item.url))
    },

    // VR全景图片处理方法
    handleVrImageUploadSuccess(response, file, fileList) {
      this.vrPicture = fileList
      this.form.vrImage = fileList.length > 0 ? fileList[0].url : ''
      this.$message.success('VR全景图片上传成功')
    },
    handleVrImageRemove(file, fileList) {
      this.vrPicture = fileList
      this.form.vrImage = fileList.length > 0 ? fileList[0].url : ''
    },

    // 每日服务管理方法
    addService() {
      this.dailyServices.push({ time: '', content: '' })
    },
    removeService(index) {
      this.dailyServices.splice(index, 1)
    },

    // 处理设施图片数据
    handleFacilityImages(facilityImages, targetArray) {
      if (facilityImages) {
        try {
          const imageUrls = JSON.parse(facilityImages)
          targetArray.splice(0, targetArray.length, ...imageUrls.map((url, index) => ({
            name: `设施图片${index + 1}`,
            url: url
          })))
        } catch (e) {
          targetArray.splice(0, targetArray.length)
        }
      } else {
        targetArray.splice(0, targetArray.length)
      }
    },

    // 加载设施图标配置
    loadFacilityIconConfig() {
      console.log('开始加载设施图标配置')

      // 并行加载生活和医疗设施
      Promise.all([
        getLifeFacilities(),
        getMedicalFacilities()
      ]).then(([lifeResponse, medicalResponse]) => {
        console.log('生活设施API响应:', lifeResponse)
        console.log('医疗设施API响应:', medicalResponse)

        if (lifeResponse.code === 200) {
          this.lifeFacilities = lifeResponse.data || []
          console.log('生活设施数量:', this.lifeFacilities.length)
          console.log('生活设施详情:', this.lifeFacilities)
        } else {
          this.lifeFacilities = []
          console.error('获取生活设施失败:', lifeResponse.msg)
        }

        if (medicalResponse.code === 200) {
          this.medicalFacilities = medicalResponse.data || []
          console.log('医疗设施数量:', this.medicalFacilities.length)
          console.log('医疗设施详情:', this.medicalFacilities)
        } else {
          this.medicalFacilities = []
          console.error('获取医疗设施失败:', medicalResponse.msg)
        }
      }).catch(error => {
        console.error('加载设施图标配置失败:', error)
        this.$message.error('加载设施图标配置失败')
        this.lifeFacilities = []
        this.medicalFacilities = []
      })
    },

    // 图标维护
    handleIconMaintenance() {
      this.$router.push('/pension/facility/icon')
    }
  }
}
</script>

<style scoped lang="scss">
.publicity-card {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;

  ::v-deep .el-card__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-bottom: none;
    padding: 20px 24px;
  }

  ::v-deep .el-card__body {
    padding: 32px;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .header-left {
    span {
      font-size: 18px;
      font-weight: 600;
      color: #ffffff;
    }
  }

  .header-right {
    display: flex;
    gap: 10px;
  }
}

.el-divider {
  margin: 32px 0 24px 0;

  ::v-deep .el-divider__text {
    font-size: 15px;
    font-weight: 600;
    color: #667eea;
  }

  ::v-deep .el-divider__line {
    background-color: #e8eef5;
  }
}

// 表单项美化
::v-deep .el-form-item__label {
  font-weight: 500;
  color: #606266;
}

::v-deep .el-input__inner,
::v-deep .el-textarea__inner {
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s;

  &:focus {
    border-color: #667eea;
    box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
  }

  &:disabled {
    background-color: #f5f7fa;
  }
}

::v-deep .el-input-number {
  .el-input-number__increase,
  .el-input-number__decrease {
    background-color: #f5f7fa;
    border-color: #dcdfe6;

    &:hover {
      color: #667eea;
    }
  }
}

// 按钮美化
.header-right {
  ::v-deep .el-button--warning {
    background: rgba(255, 255, 255, 0.15);
    border-color: rgba(255, 255, 255, 0.3);
    color: #ffffff;

    &:hover {
      background: rgba(255, 255, 255, 0.25);
      border-color: rgba(255, 255, 255, 0.4);
    }

    &:focus {
      color: #ffffff;
    }
  }

  ::v-deep .el-button--primary {
    background: #ffffff;
    color: #667eea;
    border-color: #ffffff;

    &:hover {
      background: #f0f0f0;
      color: #5568d3;
    }

    &:focus {
      color: #5568d3;
    }
  }

  .el-icon-setting {
    margin-right: 4px;
  }
}

// 图片上传区域美化
::v-deep .el-upload-list--picture-card .el-upload-list__item {
  width: 130px;
  height: 130px;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }
}

::v-deep .el-upload--picture-card {
  width: 130px;
  height: 130px;
  line-height: 130px;
  border-radius: 10px;
  border: 2px dashed #dcdfe6;
  background: #fafbfc;
  transition: all 0.3s;

  &:hover {
    border-color: #667eea;
    background: #f5f3ff;
  }

  .el-icon-plus {
    color: #667eea;
    font-size: 28px;
  }
}

// 复选框美化
::v-deep .el-checkbox {
  margin-right: 20px;
  margin-bottom: 12px;

  .el-checkbox__label {
    font-size: 14px;
    color: #606266;
  }

  .el-checkbox__input.is-checked + .el-checkbox__label {
    color: #667eea;
  }
}

// 开关美化
::v-deep .el-switch {
  &.is-checked {
    .el-switch__core {
      background-color: #667eea;
      border-color: #667eea;
    }
  }
}

// 图片预览对话框
::v-deep .el-dialog {
  border-radius: 12px;
  overflow: hidden;

  .el-dialog__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px 24px;

    .el-dialog__title {
      color: #ffffff;
      font-size: 16px;
      font-weight: 600;
    }

    .el-dialog__headerbtn .el-dialog__close {
      color: #ffffff;
    }
  }

  .el-dialog__body {
    padding: 24px;
  }

  .el-dialog__footer {
    padding: 16px 24px;
    border-top: 1px solid #ebeef5;
  }
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  line-height: 1.4;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 6px;
  line-height: 1.4;
}

// 费用显示区域
.el-form-item {
  margin-bottom: 22px;

  ::v-deep .el-form-item__content {
    line-height: 1;
  }
}

// 服务时间安排
.daily-service-item {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
