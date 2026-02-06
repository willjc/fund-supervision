<template>
  <div class="app-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="title">
        <i class="el-icon-coin"></i>
        入住人押金使用申请
      </h2>
    </div>

    <!-- 步骤指示器 -->
    <el-steps :active="activeStep" finish-status="success" align-center style="margin-bottom: 30px;">
      <el-step title="基本信息" description="选择入住人和申请信息"></el-step>
      <el-step title="申请详情" description="填写申请原因和材料"></el-step>
      <el-step title="家属确认" description="老人或家属确认申请"></el-step>
      <el-step title="提交申请" description="提交监管部门审批"></el-step>
    </el-steps>

    <!-- 步骤1：基本信息 -->
    <div v-show="activeStep === 0" class="step-content">
      <el-card shadow="hover">
        <div slot="header" class="card-header">
          <span>选择入住人</span>
        </div>
        <el-form ref="basicForm" :model="basicForm" :rules="basicRules" label-width="120px">
          <el-form-item label="选择入住人" prop="residentId">
            <el-select v-model="basicForm.residentId" placeholder="请选择入住人" filterable style="width: 100%" @change="handleResidentChange">
              <el-option
                v-for="resident in residentList"
                :key="resident.residentId"
                :label="`${resident.elderName} - ${resident.bedInfo} (可用押金：￥${formatMoney(resident.depositBalance)})`"
                :value="resident.residentId">
                <div style="display: flex; justify-content: space-between;">
                  <span>{{ resident.elderName }} - {{ resident.bedInfo }}</span>
                  <span style="color: #67C23A; font-weight: bold;">￥{{ formatMoney(resident.depositBalance) }}</span>
                </div>
              </el-option>
            </el-select>
          </el-form-item>

          <div v-if="selectedResident" class="resident-info">
            <el-descriptions title="入住人信息" :column="2" border>
              <el-descriptions-item label="姓名">{{ selectedResident.elderName }}</el-descriptions-item>
              <el-descriptions-item label="床位信息">{{ selectedResident.bedInfo }}</el-descriptions-item>
              <el-descriptions-item label="护理等级">
                <dict-tag :options="dict.type.elder_care_level" :value="selectedResident.careLevel"/>
              </el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ selectedResident.phone }}</el-descriptions-item>
              <el-descriptions-item label="紧急联系人">{{ selectedResident.emergencyContact }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ selectedResident.emergencyPhone }}</el-descriptions-item>
              <el-descriptions-item label="押金余额">
                <span style="font-size: 16px; font-weight: bold; color: #67C23A;">￥{{ formatMoney(selectedResident.depositBalance) }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="入住日期">{{ parseTime(selectedResident.checkInDate, '{y}-{m}-{d}') }}</el-descriptions-item>
            </el-descriptions>
          </div>

          <el-divider content-position="left">申请信息</el-divider>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="申请金额" prop="amount">
                <el-input-number
                  v-model="basicForm.amount"
                  :min="0"
                  :max="selectedResident ? selectedResident.depositBalance : 0"
                  :precision="2"
                  style="width: 100%;" />
                <div class="form-tip">可用押金余额：￥{{ formatMoney(selectedResident ? selectedResident.depositBalance : 0) }}</div>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="紧急程度" prop="urgencyLevel">
                <el-select v-model="basicForm.urgencyLevel" placeholder="请选择紧急程度" style="width: 100%">
                  <el-option label="一般" value="一般"></el-option>
                  <el-option label="紧急" value="紧急"></el-option>
                  <el-option label="非常紧急" value="非常紧急"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="使用事由" prop="purpose">
                <el-select v-model="basicForm.purpose" placeholder="请选择使用事由" style="width: 100%">
                  <el-option label="医疗费用" value="医疗费用"></el-option>
                  <el-option label="生活用品" value="生活用品"></el-option>
                  <el-option label="特殊护理服务" value="特殊护理服务"></el-option>
                  <el-option label="其他用途" value="其他用途"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="期望使用日期" prop="expectedUseDate">
                <el-date-picker
                  v-model="basicForm.expectedUseDate"
                  type="date"
                  placeholder="请选择期望使用日期"
                  value-format="yyyy-MM-dd"
                  style="width: 100%;">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-card>
    </div>

    <!-- 步骤2：申请详情 -->
    <div v-show="activeStep === 1" class="step-content">
      <el-card shadow="hover">
        <div slot="header" class="card-header">
          <span>申请详情</span>
        </div>
        <el-form ref="detailForm" :model="detailForm" :rules="detailRules" label-width="120px">
          <el-form-item label="使用原因" prop="reason">
            <el-input
              v-model="detailForm.reason"
              type="textarea"
              :rows="4"
              placeholder="请详细说明申请使用押金的原因">
            </el-input>
          </el-form-item>

          <el-form-item label="详细说明" prop="description">
            <el-input
              v-model="detailForm.description"
              type="textarea"
              :rows="3"
              placeholder="请说明资金的具体用途和使用计划">
            </el-input>
          </el-form-item>

          <el-form-item label="申请材料">
            <el-upload
              class="upload-demo"
              action="/api/common/upload"
              :on-success="handleUploadSuccess"
              :on-remove="handleRemove"
              :file-list="fileList"
              multiple>
              <el-button size="small" type="primary">点击上传</el-button>
              <div slot="tip" class="el-upload__tip">支持上传相关证明材料（如医疗证明、费用清单等）</div>
            </el-upload>
          </el-form-item>

          <el-form-item label="备注">
            <el-input
              v-model="detailForm.remark"
              type="textarea"
              :rows="2"
              placeholder="其他需要说明的事项（可选）">
            </el-input>
          </el-form-item>
        </el-form>

        <!-- 申请信息汇总 -->
        <el-divider content-position="left">申请信息汇总</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="入住人">{{ selectedResident.elderName }}</el-descriptions-item>
          <el-descriptions-item label="床位信息">{{ selectedResident.bedInfo }}</el-descriptions-item>
          <el-descriptions-item label="申请金额">
            <span style="font-size: 16px; font-weight: bold; color: #F56C6C;">￥{{ formatMoney(basicForm.amount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="使用事由">{{ basicForm.purpose }}</el-descriptions-item>
          <el-descriptions-item label="紧急程度">{{ basicForm.urgencyLevel }}</el-descriptions-item>
          <el-descriptions-item label="期望使用日期">{{ basicForm.expectedUseDate }}</el-descriptions-item>
          <el-descriptions-item label="使用原因" :span="2">{{ detailForm.reason }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>

    <!-- 步骤3：家属确认 -->
    <div v-show="activeStep === 2" class="step-content">
      <el-card shadow="hover">
        <div slot="header" class="card-header">
          <span>老人/家属确认</span>
        </div>

        <el-alert
          title="请通知老人或家属确认本次押金使用申请"
          type="warning"
          :closable="false"
          style="margin-bottom: 20px;">
        </el-alert>

        <el-form ref="confirmForm" :model="confirmForm" :rules="confirmRules" label-width="120px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="确认人姓名" prop="confirmName">
                <el-input v-model="confirmForm.confirmName" placeholder="请输入确认人姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="与老人关系" prop="confirmRelation">
                <el-select v-model="confirmForm.confirmRelation" placeholder="请选择关系" style="width: 100%">
                  <el-option label="本人" value="本人"></el-option>
                  <el-option label="子女" value="子女"></el-option>
                  <el-option label="配偶" value="配偶"></el-option>
                  <el-option label="父母" value="父母"></el-option>
                  <el-option label="监护人" value="监护人"></el-option>
                  <el-option label="其他" value="其他"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="联系电话" prop="confirmPhone">
                <el-input v-model="confirmForm.confirmPhone" placeholder="请输入联系电话" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="确认方式" prop="confirmMethod">
                <el-select v-model="confirmForm.confirmMethod" placeholder="请选择确认方式" style="width: 100%">
                  <el-option label="现场确认" value="现场确认"></el-option>
                  <el-option label="电话确认" value="电话确认"></el-option>
                  <el-option label="视频确认" value="视频确认"></el-option>
                  <el-option label="签字确认" value="签字确认"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="确认意见" prop="confirmComment">
            <el-input
              v-model="confirmForm.confirmComment"
              type="textarea"
              :rows="3"
              placeholder="请输入确认意见或备注">
            </el-input>
          </el-form-item>

          <el-form-item label="电子签名" prop="signature">
            <div class="signature-box">
              <el-input
                v-model="confirmForm.signature"
                placeholder="请输入确认人姓名作为电子签名">
              </el-input>
              <div class="signature-info">输入确认人姓名即表示电子签名确认</div>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 步骤4：提交申请 -->
    <div v-show="activeStep === 3" class="step-content">
      <el-card shadow="hover">
        <div slot="header" class="card-header">
          <span>提交申请</span>
        </div>

        <el-alert
          title="请确认所有信息无误后提交申请"
          type="info"
          :closable="false"
          style="margin-bottom: 20px;">
        </el-alert>

        <el-descriptions title="申请信息确认" :column="2" border>
          <el-descriptions-item label="申请编号">DEP{{ new Date().getTime() }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ new Date().toLocaleString() }}</el-descriptions-item>
          <el-descriptions-item label="入住人">{{ selectedResident.elderName }}</el-descriptions-item>
          <el-descriptions-item label="床位信息">{{ selectedResident.bedInfo }}</el-descriptions-item>
          <el-descriptions-item label="申请金额">
            <span style="font-size: 18px; font-weight: bold; color: #F56C6C;">￥{{ formatMoney(basicForm.amount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="使用事由">{{ basicForm.purpose }}</el-descriptions-item>
          <el-descriptions-item label="紧急程度">{{ basicForm.urgencyLevel }}</el-descriptions-item>
          <el-descriptions-item label="期望使用日期">{{ basicForm.expectedUseDate }}</el-descriptions-item>
          <el-descriptions-item label="确认人">{{ confirmForm.confirmName }}</el-descriptions-item>
          <el-descriptions-item label="确认关系">{{ confirmForm.confirmRelation }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ confirmForm.confirmPhone }}</el-descriptions-item>
          <el-descriptions-item label="确认方式">{{ confirmForm.confirmMethod }}</el-descriptions-item>
          <el-descriptions-item label="使用原因" :span="2">{{ detailForm.reason }}</el-descriptions-item>
          <el-descriptions-item label="确认意见" :span="2">{{ confirmForm.confirmComment }}</el-descriptions-item>
        </el-descriptions>

        <div class="submit-notice">
          <el-checkbox v-model="submitConfirmed">我确认以上信息真实有效，同意提交监管部门审批</el-checkbox>
        </div>
      </el-card>
    </div>

    <!-- 操作按钮 -->
    <div class="step-actions">
      <el-button v-if="activeStep > 0 && activeStep < 3" @click="prevStep">上一步</el-button>
      <el-button v-if="activeStep < 2" type="primary" @click="nextStep">下一步</el-button>
      <el-button v-if="activeStep === 2" type="primary" @click="goToSubmit">确认信息</el-button>
      <el-button v-if="activeStep === 3" type="primary" @click="submitApplication" :disabled="!submitConfirmed">提交申请</el-button>
    </div>
  </div>
</template>

<script>
import { listResident } from "@/api/elder/resident";
import { addDepositUse } from "@/api/elder/depositUse";

export default {
  name: "ElderDepositApply",
  dicts: ['elder_care_level'],
  data() {
    return {
      activeStep: 0,
      submitConfirmed: false,
      residentList: [],
      selectedResident: null,
      fileList: [],
      // 基本信息表单
      basicForm: {
        residentId: null,
        amount: null,
        urgencyLevel: '',
        purpose: '',
        expectedUseDate: ''
      },
      // 详细信息表单
      detailForm: {
        reason: '',
        description: '',
        remark: ''
      },
      // 确认信息表单
      confirmForm: {
        confirmName: '',
        confirmRelation: '',
        confirmPhone: '',
        confirmMethod: '',
        confirmComment: '',
        signature: ''
      },
      // 表单验证规则
      basicRules: {
        residentId: [
          { required: true, message: "请选择入住人", trigger: "change" }
        ],
        amount: [
          { required: true, message: "请输入申请金额", trigger: "blur" },
          { validator: this.validateAmount, trigger: "blur" }
        ],
        urgencyLevel: [
          { required: true, message: "请选择紧急程度", trigger: "change" }
        ],
        purpose: [
          { required: true, message: "请选择使用事由", trigger: "change" }
        ],
        expectedUseDate: [
          { required: true, message: "请选择期望使用日期", trigger: "change" }
        ]
      },
      detailRules: {
        reason: [
          { required: true, message: "请输入使用原因", trigger: "blur" },
          { min: 10, message: "使用原因至少10个字符", trigger: "blur" }
        ],
        description: [
          { required: true, message: "请输入详细说明", trigger: "blur" }
        ]
      },
      confirmRules: {
        confirmName: [
          { required: true, message: "请输入确认人姓名", trigger: "blur" }
        ],
        confirmRelation: [
          { required: true, message: "请选择与老人关系", trigger: "change" }
        ],
        confirmPhone: [
          { required: true, message: "请输入联系电话", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ],
        confirmMethod: [
          { required: true, message: "请选择确认方式", trigger: "change" }
        ],
        confirmComment: [
          { required: true, message: "请输入确认意见", trigger: "blur" }
        ],
        signature: [
          { required: true, message: "请输入电子签名", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.loadResidentList();
    // 如果从入住人列表跳转过来，获取参数
    const { residentId } = this.$route.query;
    if (residentId) {
      this.basicForm.residentId = parseInt(residentId);
      this.handleResidentChange(residentId);
    }
  },
  methods: {
    /** 加载入住人列表 */
    loadResidentList() {
      listResident({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.residentList = response.rows || [];
      });
    },
    /** 入住人变更处理 */
    handleResidentChange(residentId) {
      this.selectedResident = this.residentList.find(item => item.residentId === residentId);
      // 预填充确认人信息
      if (this.selectedResident) {
        this.confirmForm.confirmName = this.selectedResident.emergencyContact;
        this.confirmForm.confirmPhone = this.selectedResident.emergencyPhone;
        this.confirmForm.confirmRelation = '子女';
      }
    },
    /** 验证金额 */
    validateAmount(rule, value, callback) {
      if (!value || value <= 0) {
        callback(new Error('申请金额必须大于0'));
      } else if (this.selectedResident && value > this.selectedResident.depositBalance) {
        callback(new Error('申请金额不能超过押金余额'));
      } else {
        callback();
      }
    },
    /** 下一步 */
    nextStep() {
      let formName = '';
      if (this.activeStep === 0) formName = 'basicForm';
      else if (this.activeStep === 1) formName = 'detailForm';

      this.$refs[formName].validate(valid => {
        if (valid) {
          this.activeStep++;
        }
      });
    },
    /** 上一步 */
    prevStep() {
      this.activeStep--;
    },
    /** 前往提交页面 */
    goToSubmit() {
      this.$refs.confirmForm.validate(valid => {
        if (valid) {
          this.activeStep++;
        }
      });
    },
    /** 提交申请 */
    submitApplication() {
      if (!this.submitConfirmed) {
        this.$message.warning('请先确认申请信息');
        return;
      }

      // 构造提交数据,字段名与后端实体类保持一致
      const applicationData = {
        // 生成申请编号
        applyNo: 'DEP' + new Date().getTime(),
        // 关联信息
        elderId: this.selectedResident.elderId,
        institutionId: this.selectedResident.institutionId,
        accountId: this.selectedResident.accountId,
        // 申请金额和原因
        applyAmount: this.basicForm.amount,
        applyReason: this.detailForm.reason,
        // 申请类型设为押金使用
        applyType: '押金使用',
        // 新增字段
        urgencyLevel: this.basicForm.urgencyLevel,
        purpose: this.basicForm.purpose,
        description: this.detailForm.description,
        expectedUseDate: this.basicForm.expectedUseDate,
        attachments: JSON.stringify(this.fileList.map(file => ({ name: file.name, url: file.url }))),
        // 申请状态默认为草稿,后端会自动设置
        applyStatus: 'draft',
        // 家属确认信息
        familyConfirmName: this.confirmForm.confirmName,
        familyRelation: this.confirmForm.confirmRelation,
        familyPhone: this.confirmForm.confirmPhone,
        // 备注
        remark: this.detailForm.remark
      };

      addDepositUse(applicationData).then(response => {
        this.$message.success('申请提交成功,等待家属审批');
        this.$router.push('/pension/elder/depositList');
      }).catch(error => {
        console.error('提交申请失败:', error);
        this.$message.error('申请提交失败,请检查信息后重试');
      });
    },
    /** 文件上传成功 */
    handleUploadSuccess(response, file, fileList) {
      this.fileList = fileList;
    },
    /** 移除文件 */
    handleRemove(file, fileList) {
      this.fileList = fileList;
    },
    /** 格式化金额 */
    formatMoney(value) {
      if (!value) return '0.00';
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    }
  }
};
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
  padding: 20px 0;
  border-bottom: 2px solid #f0f0f0;
}

.page-header .title {
  margin: 0;
  font-size: 24px;
  color: #303133;
  font-weight: 600;
}

.page-header .title i {
  margin-right: 10px;
  color: #409EFF;
}

.step-content {
  margin-bottom: 30px;
  min-height: 400px;
}

.card-header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.step-actions {
  text-align: center;
  margin-top: 30px;
}

.step-actions .el-button {
  margin: 0 10px;
  min-width: 100px;
}

.resident-info {
  margin-top: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.el-upload__tip {
  font-size: 12px;
  color: #909399;
}

.signature-box {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
}

.signature-info {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.submit-notice {
  margin-top: 20px;
  padding: 15px;
  background-color: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 4px;
}

.text-primary {
  color: #409EFF;
  font-weight: bold;
}
</style>