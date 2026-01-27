<template>
  <div class="app-container">
    <div class="page-title">机构申请划拨</div>

    <el-card style="margin-top: 20px;">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <!-- 选择入住人 -->
        <el-form-item label="选择入住人" prop="elderId">
          <el-select
            v-model="form.elderId"
            placeholder="请选择入住人"
            filterable
            style="width: 100%"
            @change="handleElderChange">
            <el-option
              v-for="elder in elderOptions"
              :key="elder.elderId"
              :label="`${elder.elderName} (${elder.idCard})`"
              :value="elder.elderId">
              <div style="display: flex; justify-content: space-between;">
                <span>{{ elder.elderName }} - {{ elder.idCard }}</span>
                <span style="color: #409eff;">待划拨: {{ getPendingCount(elder.elderId) }}个月</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>

        <!-- 入住人信息展示 -->
        <div v-if="selectedElder" class="resident-info-card">
          <el-descriptions title="入住人信息" :column="3" border size="small">
            <el-descriptions-item label="姓名">{{ selectedElder.elderName }}</el-descriptions-item>
            <el-descriptions-item label="身份证">{{ selectedElder.idCard }}</el-descriptions-item>
            <el-descriptions-item label="护理等级">
              <dict-tag :options="dict.type.elder_care_level" :value="selectedElder.careLevel"/>
            </el-descriptions-item>
            <el-descriptions-item label="床位信息">{{ selectedElder.bedInfo }}</el-descriptions-item>
            <el-descriptions-item label="入住日期">{{ parseTime(selectedElder.checkInDate, '{y}-{m}-{d}') }}</el-descriptions-item>
            <el-descriptions-item label="服务余额">
              <span style="font-size: 16px; font-weight: bold; color: #409eff;">¥{{ formatMoney(selectedElder.serviceBalance) }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <el-divider content-position="left">选择待划拨月份</el-divider>

        <!-- 待划拨划拨单列表 -->
        <div v-if="pendingTransfers.length > 0" class="transfer-selection">
          <el-table
            :data="pendingTransfers"
            border
            @selection-change="handleSelectionChange"
            ref="transferTable">
            <el-table-column type="selection" width="55" align="center" :reserve-selection="true" />
            <el-table-column label="账单月份" prop="billingMonth" width="120" align="center" />
            <el-table-column label="划拨单号" prop="transferNo" width="180" />
            <el-table-column label="划拨金额" prop="transferAmount" width="120" align="center">
              <template slot-scope="scope">
                <span style="color: #f56c6c; font-weight: bold;">¥{{ formatMoney(scope.row.transferAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="划拨日期" prop="transferDate" width="120" align="center">
              <template slot-scope="scope">
                {{ parseTime(scope.row.transferDate, '{y}-{m}-{d}') }}
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else class="empty-tip">
          <el-empty description="请先选择入住人查看待划拨月份" />
        </div>

        <!-- 已选金额统计 -->
        <div v-if="pendingTransfers.length > 0" class="amount-summary">
          <el-row :gutter="20">
            <el-col :span="12">
              <span>已选择 <strong style="color: #409eff;">{{ selectedTransferIds.length }}</strong> 个月</span>
            </el-col>
            <el-col :span="12" style="text-align: right;">
              <span>申请总金额：<strong style="color: #f56c6c; font-size: 18px;">¥{{ formatMoney(totalAmount) }}</strong></span>
            </el-col>
          </el-row>
        </div>

        <el-divider content-position="left">申请信息</el-divider>

        <!-- 申请原因 -->
        <el-form-item label="申请原因" prop="applyReason">
          <el-input
            v-model="form.applyReason"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="请详细说明申请划拨的原因（至少10个字）">
          </el-input>
        </el-form-item>

        <!-- 紧急程度 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="紧急程度" prop="urgencyLevel">
              <el-select v-model="form.urgencyLevel" placeholder="请选择紧急程度" style="width: 100%">
                <el-option label="一般" value="一般">
                  <span style="color: #909399;">正常划拨申请</span>
                </el-option>
                <el-option label="紧急" value="紧急">
                  <span style="color: #E6A23C;">需要优先处理</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="期望划拨日期" prop="expectedUseDate">
              <el-date-picker
                v-model="form.expectedUseDate"
                type="date"
                placeholder="请选择期望划拨日期"
                value-format="yyyy-MM-dd"
                style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 备注 -->
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注信息（可选）" />
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="form-actions">
        <el-button type="primary" size="medium" @click="submitForm" :loading="submitLoading">
          <i class="el-icon-check"></i> 提交申请
        </el-button>
        <el-button size="medium" @click="resetForm">
          <i class="el-icon-refresh-left"></i> 重置
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { listResident } from '@/api/elder/resident'
import { getPendingTransfer, addFundTransferApply } from '@/api/pension/fundTransferApply'

export default {
  name: 'FundTransferApply',
  dicts: ['elder_care_level'],
  data() {
    return {
      submitLoading: false,
      elderOptions: [],
      pendingTransfers: [],
      selectedElder: null,
      selectedTransferIds: [],
      form: {
        elderId: null,
        institutionId: null,
        applyReason: '',
        urgencyLevel: '一般',
        expectedUseDate: null,
        remark: ''
      },
      rules: {
        elderId: [
          { required: true, message: '请选择入住人', trigger: 'change' }
        ],
        applyReason: [
          { required: true, message: '请输入申请原因', trigger: 'blur' },
          { min: 10, message: '申请原因至少10个字', trigger: 'blur' }
        ],
        urgencyLevel: [
          { required: true, message: '请选择紧急程度', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    totalAmount() {
      let total = 0
      for (let id of this.selectedTransferIds) {
        const transfer = this.pendingTransfers.find(t => t.transferId === id)
        if (transfer) {
          total += parseFloat(transfer.transferAmount)
        }
      }
      return total.toFixed(2)
    }
  },
  created() {
    this.loadElderOptions()
  },
  methods: {
    // 加载入住人列表
    loadElderOptions() {
      listResident().then(response => {
        this.elderOptions = response.rows || []
      }).catch(error => {
        console.error('获取入住人列表失败:', error)
      })
    },

    // 选择老人
    handleElderChange(elderId) {
      this.selectedElder = this.elderOptions.find(e => e.elderId === elderId) || null
      this.selectedTransferIds = []
      this.form.institutionId = this.selectedElder?.institutionId

      if (elderId) {
        this.loadPendingTransfers(elderId)
      } else {
        this.pendingTransfers = []
      }
    },

    // 加载待划拨划拨单
    loadPendingTransfers(elderId) {
      getPendingTransfer(elderId).then(response => {
        this.pendingTransfers = response.data || []
      }).catch(error => {
        console.error('获取待划拨划拨单失败:', error)
        this.pendingTransfers = []
      })
    },

    // 获取待划拨月数
    getPendingCount(elderId) {
      const elder = this.elderOptions.find(e => e.elderId === elderId)
      // 这里可以从后端获取，暂时返回占位值
      return elder?.pendingCount || 0
    },

    // 多选变化
    handleSelectionChange(selection) {
      this.selectedTransferIds = selection.map(item => item.transferId)
    },

    // 提交表单
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.selectedTransferIds.length === 0) {
            this.$message.warning('请选择要申请划拨的月份')
            return
          }

          this.submitLoading = true
          const data = {
            ...this.form,
            transferIds: this.selectedTransferIds
          }

          addFundTransferApply(data).then(response => {
            this.$message.success('申请提交成功，等待家属审核')
            this.resetForm()
            this.$router.push('/pension/fund/applyList')
          }).catch(error => {
            this.$message.error('申请提交失败：' + (error.msg || error.message))
          }).finally(() => {
            this.submitLoading = false
          })
        }
      })
    },

    // 重置表单
    resetForm() {
      this.selectedElder = null
      this.pendingTransfers = []
      this.selectedTransferIds = []
      this.$refs.form.resetFields()
    },

    // 格式化金额
    formatMoney(value) {
      if (!value) return '0.00'
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }
  }
}
</script>

<style scoped lang="scss">
.page-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}

.resident-info-card {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.transfer-selection {
  margin-bottom: 20px;
}

.empty-tip {
  text-align: center;
  padding: 40px 0;
}

.amount-summary {
  padding: 15px;
  background-color: #ecf5ff;
  border-radius: 4px;
  margin-bottom: 20px;
}

.form-actions {
  text-align: center;
  margin-top: 20px;
}
</style>
