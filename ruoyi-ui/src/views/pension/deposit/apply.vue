<template>
  <div class="app-container">
    <div class="page-title">押金使用申请</div>

    <el-card style="margin-top: 20px;">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <!-- 选择入住人 -->
        <el-form-item label="选择入住人" prop="residentId">
          <el-select
            v-model="form.residentId"
            placeholder="请选择入住人"
            filterable
            style="width: 100%"
            @change="handleResidentChange">
            <el-option
              v-for="resident in residentList"
              :key="resident.residentId"
              :label="`${resident.elderName} - ${resident.bedInfo} (押金余额：￥${formatMoney(resident.depositBalance)})`"
              :value="resident.residentId">
              <div style="display: flex; justify-content: space-between;">
                <span>{{ resident.elderName }} - {{ resident.bedInfo }}</span>
                <span style="color: #67C23A; font-weight: bold;">￥{{ formatMoney(resident.depositBalance) }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <!-- 入住人信息展示 -->
        <div v-if="selectedResident" class="resident-info-card">
          <el-descriptions title="入住人信息" :column="3" border size="small">
            <el-descriptions-item label="姓名">{{ selectedResident.elderName }}</el-descriptions-item>
            <el-descriptions-item label="床位">{{ selectedResident.bedInfo }}</el-descriptions-item>
            <el-descriptions-item label="护理等级">
              <dict-tag :options="dict.type.elder_care_level" :value="selectedResident.careLevel"/>
            </el-descriptions-item>
            <el-descriptions-item label="老人电话">{{ selectedResident.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="紧急联系人">{{ selectedResident.emergencyName || selectedResident.emergencyContact || '-' }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ selectedResident.emergencyPhone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="押金余额">
              <span style="font-size: 16px; font-weight: bold; color: #67C23A;">￥{{ formatMoney(selectedResident.depositBalance) }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="入住日期">{{ parseTime(selectedResident.checkInDate, '{y}-{m}-{d}') }}</el-descriptions-item>
            <el-descriptions-item label="到期日期">{{ parseTime(selectedResident.dueDate, '{y}-{m}-{d}') }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <el-divider content-position="left">申请信息</el-divider>

        <!-- 申请金额和紧急程度 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请金额" prop="amount">
              <el-input-number
                v-model="form.amount"
                :min="0"
                :max="selectedResident ? selectedResident.depositBalance : 0"
                :precision="2"
                style="width: 100%;"
                placeholder="请输入申请金额" />
              <div class="form-tip">
                可用押金余额：￥{{ formatMoney(selectedResident ? selectedResident.depositBalance : 0) }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急程度" prop="urgencyLevel">
              <el-select v-model="form.urgencyLevel" placeholder="请选择紧急程度" style="width: 100%">
                <el-option label="一般" value="一般">
                  <span style="color: #909399;">一般 - 可延后处理</span>
                </el-option>
                <el-option label="紧急" value="紧急">
                  <span style="color: #E6A23C;">紧急 - 需优先处理</span>
                </el-option>
                <el-option label="非常紧急" value="非常紧急">
                  <span style="color: #F56C6C;">非常紧急 - 需立即处理</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 使用事由和期望使用日期 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="使用事由" prop="purpose">
              <el-select v-model="form.purpose" placeholder="请选择使用事由" style="width: 100%">
                <el-option label="医疗费用" value="医疗费用"></el-option>
                <el-option label="个人物品购买" value="个人物品购买"></el-option>
                <el-option label="特殊护理服务" value="特殊护理服务"></el-option>
                <el-option label="其他用途" value="其他用途"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="期望使用日期" prop="expectedUseDate">
              <el-date-picker
                v-model="form.expectedUseDate"
                type="date"
                placeholder="请选择期望使用日期"
                value-format="yyyy-MM-dd"
                style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 申请原因 -->
        <el-form-item label="申请原因" prop="reason">
          <el-input
            v-model="form.reason"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="请详细说明申请使用押金的原因（至少10个字）">
          </el-input>
        </el-form-item>

        <!-- 详细说明 -->
        <el-form-item label="详细说明" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="请说明资金的具体用途和使用计划">
          </el-input>
        </el-form-item>

        <!-- 申请材料上传 -->
        <el-form-item label="申请材料">
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :file-list="fileList"
            :limit="5"
            multiple>
            <el-button size="small" type="primary" icon="el-icon-upload">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">
              支持上传医疗证明、费用清单、发票等相关材料，最多5个文件
            </div>
          </el-upload>
        </el-form-item>

        <!-- 备注 -->
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            maxlength="200"
            show-word-limit
            placeholder="其他需要说明的事项（可选）">
          </el-input>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitApplication" :loading="submitting">提交申请</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { listResident } from "@/api/elder/resident";
import { addDepositUse, getDepositUse, updateDepositUse, checkPendingApply } from "@/api/elder/depositUse";
import { getToken } from "@/utils/auth";

export default {
  name: "DepositApply",
  dicts: ['elder_care_level'],
  data() {
    return {
      submitting: false,
      residentList: [],
      selectedResident: null,
      fileList: [],
      editingApplyId: null, // 编辑模式下的申请ID
      uploadUrl: process.env.VUE_APP_BASE_API + "/common/upload",
      uploadHeaders: {
        Authorization: "Bearer " + getToken()
      },
      form: {
        residentId: null,
        amount: null,
        urgencyLevel: '一般',
        purpose: '',
        expectedUseDate: '',
        reason: '',
        description: '',
        remark: ''
      },
      rules: {
        residentId: [
          { required: true, message: "请选择入住人", trigger: "change" }
        ],
        amount: [
          { required: true, message: "请输入申请金额", trigger: "blur" },
          { type: 'number', min: 0.01, message: "申请金额必须大于0", trigger: "blur" }
        ],
        urgencyLevel: [
          { required: true, message: "请选择紧急程度", trigger: "change" }
        ],
        purpose: [
          { required: true, message: "请选择使用事由", trigger: "change" }
        ],
        expectedUseDate: [
          { required: true, message: "请选择期望使用日期", trigger: "change" }
        ],
        reason: [
          { required: true, message: "请填写申请原因", trigger: "blur" },
          { min: 10, message: "申请原因至少10个字符", trigger: "blur" }
        ],
        description: [
          { required: true, message: "请填写详细说明", trigger: "blur" }
        ]
      }
    };
  },
  async created() {
    // 先加载入住人列表
    await this.loadResidentList();

    // 如果是编辑模式，加载申请数据
    const { applyId, residentId } = this.$route.query;
    if (applyId) {
      this.loadApplyData(applyId);
    } else if (residentId) {
      // 如果从入住人列表跳转过来，获取参数
      this.$nextTick(() => {
        this.form.residentId = parseInt(residentId);
        this.handleResidentChange(parseInt(residentId));
      });
    }
  },
  methods: {
    /** 加载入住人列表 */
    loadResidentList() {
      return listResident({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.residentList = response.rows || [];
      });
    },

    /** 加载申请数据(编辑模式) */
    loadApplyData(applyId) {
      getDepositUse(applyId).then(response => {
        const data = response.data;

        // 填充表单数据
        this.$nextTick(() => {
          this.form = {
            residentId: data.elderId,
            amount: data.applyAmount,
            urgencyLevel: data.urgencyLevel,
            purpose: data.purpose,
            expectedUseDate: data.expectedUseDate,
            reason: data.applyReason,
            description: data.description,
            remark: data.remark || ''
          };

          // 查找并设置选中的入住人
          console.log('查找入住人:', {
            elderId: data.elderId,
            residentListLength: this.residentList.length,
            residentList: this.residentList
          });

          this.selectedResident = this.residentList.find(item => item.elderId === data.elderId);

          if (!this.selectedResident) {
            console.error('未找到匹配的入住人:', data.elderId);
            this.$message.warning('未找到对应的入住人信息，押金余额可能无法显示');
          } else {
            console.log('找到入住人:', this.selectedResident);
          }

          // 解析并设置附件列表
          if (data.attachments) {
            try {
              this.fileList = JSON.parse(data.attachments);
            } catch (e) {
              console.error('解析附件失败:', e);
              this.fileList = [];
            }
          }

          // 保存applyId用于更新
          this.editingApplyId = data.applyId;
        });
      }).catch(error => {
        console.error('加载申请数据失败:', error);
        this.$message.error('加载申请数据失败');
        this.$router.push('/pension/deposit/list');
      });
    },

    /** 入住人变更处理 */
    handleResidentChange(residentId) {
      this.selectedResident = this.residentList.find(item => item.residentId === residentId);
      if (this.selectedResident) {
        // 如果申请金额超过押金余额，自动调整
        if (this.form.amount && this.form.amount > this.selectedResident.depositBalance) {
          this.form.amount = this.selectedResident.depositBalance;
          this.$message.warning('申请金额已自动调整为押金余额');
        }
      }
    },

    /** 文件上传成功 */
    handleUploadSuccess(response, file, fileList) {
      if (response.code === 200) {
        this.fileList.push({
          name: file.name,
          url: response.fileName
        });
        this.$message.success("文件上传成功");
      } else {
        this.$message.error(response.msg || "文件上传失败");
      }
    },

    /** 移除文件 */
    handleRemove(file, fileList) {
      const index = this.fileList.findIndex(f => f.url === file.url || f.url === file.response?.fileName);
      if (index > -1) {
        this.fileList.splice(index, 1);
      }
    },

    /** 格式化金额 */
    formatMoney(value) {
      if (!value) return '0.00';
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    },

    /** 提交申请 */
    submitApplication() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          this.$message.error("请完善表单信息");
          return;
        }

        // 二次确认
        this.$confirm(`确认提交押金使用申请？申请金额：￥${this.formatMoney(this.form.amount)}`, '确认提交', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.submitConfirmed();
        }).catch(() => {
          this.$message.info('已取消提交');
        });
      });
    },

    /** 确认提交 */
    async submitConfirmed() {
      this.submitting = true;

      try {
        // 新增模式下，检查该老人是否有正在审批中的申请
        if (!this.editingApplyId) {
          const checkResult = await checkPendingApply(this.selectedResident.elderId);
          if (checkResult.code === 200 && checkResult.data) {
            // 有正在审批中的申请
            const pendingApply = checkResult.data;
            const statusText = this.getStatusText(pendingApply.applyStatus);
            this.$message.warning(`该老人有正在审批中的申请（申请编号：${pendingApply.applyNo}，状态：${statusText}），请等待审批完成后再提交新申请`);
            this.submitting = false;
            return;
          }
        }

        // 构造提交数据
        const applicationData = {
          elderId: this.selectedResident.elderId,
          institutionId: this.selectedResident.institutionId,
          accountId: this.selectedResident.accountId,
          applyAmount: this.form.amount,
          applyReason: this.form.reason,
          applyType: '押金使用',
          urgencyLevel: this.form.urgencyLevel,
          purpose: this.form.purpose,
          description: this.form.description,
          expectedUseDate: this.form.expectedUseDate,
          attachments: JSON.stringify(this.fileList),
          applyStatus: 'pending_family', // 直接设为待家属审批
          remark: this.form.remark
        };

        // 判断是新增还是编辑
        if (this.editingApplyId) {
          // 编辑模式：更新已有申请
          applicationData.applyId = this.editingApplyId;
          await updateDepositUse(applicationData);
          this.$message.success('申请更新成功，等待老人/家属确认');
          this.$router.push('/pension/deposit/list');
        } else {
          // 新增模式：创建新申请
          applicationData.applyNo = 'DEP' + new Date().getTime();
          await addDepositUse(applicationData);
          this.$message.success('申请提交成功，等待老人/家属确认');
          this.$router.push('/pension/deposit/list');
        }
      } catch (error) {
        console.error('提交失败:', error);
        this.$message.error(error.message || '提交失败，请重试');
      } finally {
        this.submitting = false;
      }
    },

    /** 获取状态文本 */
    getStatusText(status) {
      const statusMap = {
        'draft': '草稿',
        'pending_family': '待家属审批',
        'family_approved': '家属已审批',
        'pending_supervision': '待监管审批',
        'approved': '已通过',
        'rejected': '已拒绝',
        'withdrawn': '已撤回'
      };
      return statusMap[status] || status;
    },

    /** 重置表单 */
    resetForm() {
      this.$refs.form.resetFields();
      this.fileList = [];
      this.selectedResident = null;
    }
  }
};
</script>

<style scoped>
.page-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  padding-bottom: 10px;
}

.resident-info-card {
  margin: 20px 0;
  padding: 15px;
  background-color: #f5f7fa;
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
  line-height: 1.5;
}
</style>
