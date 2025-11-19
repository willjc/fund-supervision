<template>
  <div class="app-container">
    <el-form ref="applyForm" :model="applyForm" :rules="applyRules" label-width="140px">

      <!-- 一、注册信息 -->
      <el-card header="📋 一、注册信息" shadow="never" class="section-card">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养老机构名称" prop="institutionName">
              <el-input v-model="applyForm.institutionName" placeholder="请输入养老机构名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="注册资金" prop="registeredCapital">
              <el-input-number v-model="applyForm.registeredCapital" :min="0" :precision="2" style="width: 100%;" />
              <span style="margin-left: 5px;">万元</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="注册地址" prop="registeredAddress">
              <el-input v-model="applyForm.registeredAddress" placeholder="请输入注册地址" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属区域" prop="districtCode">
              <el-select v-model="applyForm.districtCode" placeholder="请选择所属区域" style="width: 100%">
                <el-option
                  v-for="item in districtOptions"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="社会统一信用代码" prop="creditCode">
              <el-input v-model="applyForm.creditCode" placeholder="请输入18位统一信用代码" maxlength="18" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="机构备案号" prop="recordNumber">
              <el-input v-model="applyForm.recordNumber" placeholder="请输入机构备案号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="机构联系人" prop="contactPerson">
              <el-input v-model="applyForm.contactPerson" placeholder="请输入机构联系人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="applyForm.contactPhone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成立日期" prop="establishedDate">
              <el-date-picker
                v-model="applyForm.establishedDate"
                type="date"
                placeholder="选择成立日期"
                style="width: 100%"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="实际经营地址" prop="actualAddress">
              <el-input v-model="applyForm.actualAddress" placeholder="请输入实际经营地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="兴办机构" prop="organizer">
              <el-input v-model="applyForm.organizer" placeholder="请输入兴办机构" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 二、负责人信息 -->
      <el-card header="👤 二、负责人信息" shadow="never" class="section-card">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="responsibleName">
              <el-input v-model="applyForm.responsibleName" placeholder="请输入负责人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="responsibleIdCard">
              <el-input v-model="applyForm.responsibleIdCard" placeholder="请输入负责人身份证号" maxlength="18" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="居住地" prop="responsibleAddress">
              <el-input v-model="applyForm.responsibleAddress" placeholder="请输入负责人居住地" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="responsiblePhone">
              <el-input v-model="applyForm.responsiblePhone" placeholder="请输入负责人联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 三、经营信息 -->
      <el-card header="🏢 三、经营信息" shadow="never" class="section-card">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="养老机构类型" prop="institutionType">
              <el-select v-model="applyForm.institutionType" placeholder="请选择养老机构类型" style="width: 100%">
                <el-option label="养老院" value="nursing_home" />
                <el-option label="养老服务中心" value="service_center" />
                <el-option label="日间照料中心" value="day_care" />
                <el-option label="养老公寓" value="senior_apartment" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="床位数" prop="bedCount">
              <el-input-number v-model="applyForm.bedCount" :min="0" style="width: 100%;" />
              <span style="margin-left: 5px;">张</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="收费区间" prop="feeRange">
              <el-input v-model="applyForm.feeRange" placeholder="如：2000-5000元/月" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="固定资产净额" prop="fixedAssets">
              <el-input-number v-model="applyForm.fixedAssets" :min="0" :precision="2" style="width: 100%;" />
              <span style="margin-left: 5px;">万元</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="监管账户" prop="superviseAccount">
              <el-input v-model="applyForm.superviseAccount" placeholder="请输入监管账户" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="基本账户" prop="bankAccount">
              <el-input v-model="applyForm.bankAccount" placeholder="请输入基本账户" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 四、上传材料 -->
      <el-card header="📎 四、上传材料" shadow="never" class="section-card">
        <el-form-item label="营业执照" prop="businessLicense">
          <el-upload
            class="upload-demo"
            :action="uploadConfig.url"
            :headers="uploadConfig.headers"
            :before-upload="beforeUpload"
            :on-success="(response, file, fileList) => handleUploadSuccess(response, file, fileList, 'businessLicense')"
            :on-error="handleUploadError"
            :on-remove="() => handleRemove('businessLicense')"
            :file-list="businessLicenseFiles"
            list-type="picture"
            :limit="1"
            accept=".jpg,.jpeg,.png"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">请上传营业执照扫描件，支持jpg/png格式，单个文件不超过5MB</div>
          </el-upload>
        </el-form-item>

        <el-form-item label="社会福利机构设置批准证书" prop="approvalCertificate">
          <el-upload
            class="upload-demo"
            :action="uploadConfig.url"
            :headers="uploadConfig.headers"
            :before-upload="beforeUpload"
            :on-success="(response, file, fileList) => handleUploadSuccess(response, file, fileList, 'approvalCertificate')"
            :on-error="handleUploadError"
            :on-remove="() => handleRemove('approvalCertificate')"
            :file-list="approvalCertificateFiles"
            list-type="picture"
            :limit="1"
            accept=".jpg,.jpeg,.png"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">请上传社会福利机构设置批准证书扫描件，支持jpg/png格式，单个文件不超过5MB</div>
          </el-upload>
        </el-form-item>

        <el-form-item label="三方监管协议" prop="supervisionAgreement">
          <el-upload
            class="upload-demo"
            :action="uploadConfig.url"
            :headers="uploadConfig.headers"
            :before-upload="beforeUpload"
            :on-success="(response, file, fileList) => handleUploadSuccess(response, file, fileList, 'supervisionAgreement')"
            :on-error="handleUploadError"
            :on-remove="() => handleRemove('supervisionAgreement')"
            :file-list="supervisionAgreementFiles"
            list-type="picture"
            :limit="1"
            accept=".jpg,.jpeg,.png"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">请上传三方监管协议扫描件，支持jpg/png格式，单个文件不超过5MB</div>
          </el-upload>
        </el-form-item>
      </el-card>

      <!-- 操作按钮 -->
      <div class="button-group">
        <el-button @click="saveDraft" icon="el-icon-document">保存草稿</el-button>
        <el-button type="primary" @click="submitApply" :loading="submitting" icon="el-icon-check">提交申请</el-button>
        <el-button @click="handleReset" icon="el-icon-refresh-left">重置表单</el-button>
      </div>
    </el-form>
  </div>
</template>

<script>
import { submitInstitutionApply, saveDraftApply } from "@/api/pension/institutionApply";
import { getPensionInstitution, getDictData } from "@/api/pension/institution";
import { getToken } from "@/utils/auth";

export default {
  name: "PensionInstitutionApply",
  data() {
    return {
      submitting: false,
      // 区域选项
      districtOptions: [],
      // 文件上传配置
      uploadConfig: {
        url: process.env.VUE_APP_BASE_API + "/common/upload",
        headers: { Authorization: "Bearer " + getToken() }
      },
      // 文件列表
      businessLicenseFiles: [],
      approvalCertificateFiles: [],
      supervisionAgreementFiles: [],
      // 申请表单
      applyForm: {
        // 注册信息
        institutionName: '',
        registeredCapital: null,
        registeredAddress: '',
        districtCode: '',
        creditCode: '',
        recordNumber: '',
        contactPerson: '',
        contactPhone: '',
        establishedDate: null,
        actualAddress: '',
        organizer: '',
        // 负责人信息
        responsibleName: '',
        responsibleIdCard: '',
        responsibleAddress: '',
        responsiblePhone: '',
        // 经营信息
        institutionType: '',
        bedCount: null,
        feeRange: '',
        fixedAssets: null,
        superviseAccount: '',
        bankAccount: '',
        // 上传材料
        businessLicense: '',
        approvalCertificate: '',
        supervisionAgreement: ''
      },
      // 表单校验规则
      applyRules: {
        institutionName: [
          { required: true, message: "养老机构名称不能为空", trigger: "blur" }
        ],
        registeredCapital: [
          { required: true, message: "注册资金不能为空", trigger: "blur" }
        ],
        registeredAddress: [
          { required: true, message: "注册地址不能为空", trigger: "blur" }
        ],
        districtCode: [
          { required: true, message: "所属区域不能为空", trigger: "change" }
        ],
        creditCode: [
          { required: true, message: "社会统一信用代码不能为空", trigger: "blur" },
          { pattern: /^[0-9A-Z]{18}$/, message: "请输入正确的18位统一信用代码", trigger: "blur" }
        ],
        recordNumber: [
          { required: true, message: "机构备案号不能为空", trigger: "blur" }
        ],
        contactPerson: [
          { required: true, message: "机构联系人不能为空", trigger: "blur" }
        ],
        contactPhone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" },
          { pattern: /^1[3456789]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ],
        establishedDate: [
          { required: true, message: "成立日期不能为空", trigger: "change" }
        ],
        actualAddress: [
          { required: true, message: "实际经营地址不能为空", trigger: "blur" }
        ],
        organizer: [
          { required: true, message: "兴办机构不能为空", trigger: "blur" }
        ],
        responsibleName: [
          { required: true, message: "负责人姓名不能为空", trigger: "blur" }
        ],
        responsibleIdCard: [
          { required: true, message: "负责人身份证号不能为空", trigger: "blur" },
          { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: "请输入正确的身份证号", trigger: "blur" }
        ],
        responsibleAddress: [
          { required: true, message: "负责人居住地不能为空", trigger: "blur" }
        ],
        responsiblePhone: [
          { required: true, message: "负责人联系电话不能为空", trigger: "blur" },
          { pattern: /^1[3456789]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ],
        institutionType: [
          { required: true, message: "养老机构类型不能为空", trigger: "change" }
        ],
        bedCount: [
          { required: true, message: "床位数不能为空", trigger: "blur" }
        ],
        feeRange: [
          { required: true, message: "收费区间不能为空", trigger: "blur" }
        ],
        fixedAssets: [
          { required: true, message: "固定资产净额不能为空", trigger: "blur" }
        ],
        superviseAccount: [
          { required: true, message: "监管账户不能为空", trigger: "blur" }
        ],
        bankAccount: [
          { required: true, message: "基本账户不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    // 加载区域数据
    this.loadDistrictData();
    // 检查是否是编辑模式
    const institutionId = this.$route.query.id;
    if (institutionId) {
      this.loadInstitutionData(institutionId);
    }
  },
  methods: {
    // 加载区域数据
    loadDistrictData() {
      getDictData('pension_district').then(response => {
        this.districtOptions = response.data || [];
      }).catch(error => {
        console.error('加载区域数据失败:', error);
        this.$modal.msgError("加载区域数据失败");
      });
    },
    // 加载机构数据
    loadInstitutionData(institutionId) {
      getPensionInstitution(institutionId).then(response => {
        // 复制数据到表单
        const data = response.data;
        this.applyForm = {
          institutionId: data.institutionId,
          institutionName: data.institutionName || '',
          registeredCapital: data.registeredCapital,
          registeredAddress: data.registeredAddress || '',
          districtCode: data.districtCode || '',
          creditCode: data.creditCode || '',
          recordNumber: data.recordNumber || '',
          contactPerson: data.contactPerson || '',
          contactPhone: data.contactPhone || '',
          establishedDate: data.establishedDate,
          actualAddress: data.actualAddress || '',
          organizer: data.organizer || '',
          responsibleName: data.responsibleName || '',
          responsibleIdCard: data.responsibleIdCard || '',
          responsibleAddress: data.responsibleAddress || '',
          responsiblePhone: data.responsiblePhone || '',
          institutionType: data.institutionType || '',
          bedCount: data.bedCount,
          feeRange: data.feeRange || '',
          fixedAssets: data.fixedAssets,
          superviseAccount: data.superviseAccount || '',
          bankAccount: data.bankAccount || '',
          businessLicense: data.businessLicense || '',
          approvalCertificate: data.approvalCertificate || '',
          supervisionAgreement: data.supervisionAgreement || ''
        };

        // 处理文件列表显示
        if (data.businessLicense) {
          this.businessLicenseFiles = [{
            name: '营业执照',
            url: data.businessLicense
          }];
        }
        if (data.approvalCertificate) {
          this.approvalCertificateFiles = [{
            name: '批准证书',
            url: data.approvalCertificate
          }];
        }
        if (data.supervisionAgreement) {
          this.supervisionAgreementFiles = [{
            name: '监管协议',
            url: data.supervisionAgreement
          }];
        }
      });
    },
    // 上传前校验
    beforeUpload(file) {
      const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg';
      const isLt5M = file.size / 1024 / 1024 < 5;

      if (!isImage) {
        this.$modal.msgError('上传文件只能是 JPG/PNG 格式!');
        return false;
      }
      if (!isLt5M) {
        this.$modal.msgError('上传文件大小不能超过 5MB!');
        return false;
      }
      return true;
    },

    // 文件上传成功
    handleUploadSuccess(response, file, fileList, field) {
      if (response.code === 200) {
        this.applyForm[field] = response.url;
        if (field === 'businessLicense') {
          this.businessLicenseFiles = [{ name: response.fileName, url: response.url }];
        } else if (field === 'approvalCertificate') {
          this.approvalCertificateFiles = [{ name: response.fileName, url: response.url }];
        } else if (field === 'supervisionAgreement') {
          this.supervisionAgreementFiles = [{ name: response.fileName, url: response.url }];
        }
        this.$modal.msgSuccess("文件上传成功");
      } else {
        this.$modal.msgError(response.msg || "文件上传失败");
      }
    },

    // 文件上传失败
    handleUploadError(err, file, fileList) {
      console.error("上传错误:", err);
      this.$modal.msgError("文件上传失败，请检查网络连接或联系管理员");
    },

    // 移除文件
    handleRemove(field) {
      this.applyForm[field] = '';
      if (field === 'businessLicense') {
        this.businessLicenseFiles = [];
      } else if (field === 'approvalCertificate') {
        this.approvalCertificateFiles = [];
      } else if (field === 'supervisionAgreement') {
        this.supervisionAgreementFiles = [];
      }
    },

    // 提交申请
    submitApply() {
      // 检查是否上传了所有必需材料
      if (!this.applyForm.businessLicense || !this.applyForm.approvalCertificate || !this.applyForm.supervisionAgreement) {
        this.$modal.msgError("请上传所有必需材料（营业执照、批准证书、三方监管协议）后再提交申请");
        return;
      }

      this.$refs["applyForm"].validate(valid => {
        if (valid) {
          this.submitting = true;
          submitInstitutionApply(this.applyForm).then(response => {
            this.$modal.msgSuccess("申请提交成功，请等待民政部门审批");
            this.resetForm();
          }).catch(() => {
            this.submitting = false;
          }).finally(() => {
            this.submitting = false;
          });
        } else {
          this.$modal.msgError("表单填写有误，请检查必填项和格式要求");
          return false;
        }
      });
    },

    // 保存草稿
    saveDraft() {
      // 草稿不需要验证，允许部分填写
      saveDraftApply(this.applyForm).then(response => {
        this.$modal.msgSuccess("草稿保存成功");
      }).catch(() => {
        this.$modal.msgError("草稿保存失败");
      });
    },

    // 重置表单
    handleReset() {
      this.$confirm('确认要重置表单吗？所有填写的内容将被清空。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.resetForm();
        this.$modal.msgSuccess("表单已重置");
      }).catch(() => {});
    },

    // 重置表单数据
    resetForm() {
      this.applyForm = {
        institutionName: '',
        registeredCapital: null,
        registeredAddress: '',
        districtCode: '',
        creditCode: '',
        recordNumber: '',
        contactPerson: '',
        contactPhone: '',
        establishedDate: null,
        actualAddress: '',
        organizer: '',
        responsibleName: '',
        responsibleIdCard: '',
        responsibleAddress: '',
        responsiblePhone: '',
        institutionType: '',
        bedCount: null,
        feeRange: '',
        fixedAssets: null,
        superviseAccount: '',
        bankAccount: '',
        businessLicense: '',
        approvalCertificate: '',
        supervisionAgreement: ''
      };
      this.businessLicenseFiles = [];
      this.approvalCertificateFiles = [];
      this.supervisionAgreementFiles = [];
      this.submitting = false;
      // 清除表单验证
      this.$nextTick(() => {
        this.$refs["applyForm"].clearValidate();
      });
    }
  }
};
</script>

<style scoped>
.section-card {
  margin-bottom: 20px;
}

.section-card >>> .el-card__header {
  background-color: #f5f7fa;
  font-size: 16px;
  font-weight: bold;
  padding: 15px 20px;
}

.upload-demo {
  margin-top: 10px;
}

.button-group {
  margin-top: 30px;
  text-align: center;
  padding: 20px 0;
  border-top: 1px solid #e4e7ed;
}

.button-group .el-button {
  margin: 0 10px;
  padding: 12px 30px;
  font-size: 14px;
}

.el-upload__tip {
  color: #909399;
  font-size: 12px;
  margin-top: 5px;
}
</style>
