<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>收单对账差异处理</span>
      </div>

      <!-- 批次表格 -->
      <el-row class="mb8">
        <el-button type="primary" size="mini" icon="el-icon-refresh" v-hasPermi="['bank:recon:run']"
          :disabled="merchants.length === 0" @click="runOpen = true">执行对账</el-button>
        <el-button size="mini" icon="el-icon-search" @click="loadRuns">刷新批次</el-button>
      </el-row>
      <el-table v-loading="runsLoading" :data="runs" border @current-change="selectRun" highlight-current-row>
        <el-table-column label="批次号" prop="runId" width="80" />
        <el-table-column label="清算日" prop="clearingDate" width="110" />
        <el-table-column label="商户号" prop="merId" min-width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">{{ statusText(scope.row.status) }}</template>
        </el-table-column>
        <el-table-column label="银行返回" prop="respMessage" min-width="200" show-overflow-tooltip />
        <el-table-column label="对账文件" prop="fileName" min-width="220" show-overflow-tooltip />
        <el-table-column label="总行数" prop="totalRows" width="80" />
        <el-table-column label="匹配" prop="matchedRows" width="80" />
        <el-table-column label="差异" prop="diffRows" width="80" />
        <el-table-column label="执行时间" prop="createTime" width="160" />
      </el-table>

      <!-- 差异表格 -->
      <el-divider content-position="left">差异明细{{ currentRun ? '（批次 ' + currentRun.runId + '）' : '（请先选择批次）' }}</el-divider>
      <el-table v-loading="diffsLoading" :data="diffs" border>
        <el-table-column label="差异类型" width="130">
          <template slot-scope="scope">
            <el-tag :type="diffTagType(scope.row.diffType)" size="mini">{{ diffTypeText(scope.row.diffType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="平台请求号" prop="requestNo" min-width="190" show-overflow-tooltip />
        <el-table-column label="银行流水号" prop="bankSerialNo" min-width="190" show-overflow-tooltip />
        <el-table-column label="银行金额" prop="bankAmount" width="100" />
        <el-table-column label="本地金额" prop="localAmount" width="100" />
        <el-table-column label="本地流水号" prop="localSerialNo" min-width="170" show-overflow-tooltip />
        <el-table-column label="本地状态" prop="localStatus" width="100" />
        <el-table-column label="处理状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="Number(scope.row.handled) === 1 ? 'success' : 'info'" size="mini">
              {{ Number(scope.row.handled) === 1 ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="处理说明" prop="handleRemark" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="90" fixed="right">
          <template slot-scope="scope">
            <el-button v-if="Number(scope.row.handled) !== 1" type="text" size="mini"
              v-hasPermi="['bank:recon:handle']" @click="openHandle(scope.row)">标记已处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 处理弹窗 -->
    <el-dialog title="差异处理" :visible.sync="handleOpen" width="500px" append-to-body>
      <el-form ref="handleForm" :model="handleForm" label-width="90px">
        <el-form-item label="差异类型">{{ diffTypeText(handleForm.diffType) }}</el-form-item>
        <el-form-item label="平台请求号">{{ handleForm.requestNo }}</el-form-item>
        <el-form-item label="处理说明" prop="remark" :rules="[{ required: true, message: '请输入处理说明', trigger: 'blur' }]">
          <el-input v-model="handleForm.remark" type="textarea" maxlength="500" placeholder="核实经过与结论，留审计" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" :loading="handleLoading" @click="submitHandle">确定</el-button>
        <el-button @click="handleOpen = false">取消</el-button>
      </div>
    </el-dialog>

    <!-- 执行对账弹窗 -->
    <el-dialog title="执行对账" :visible.sync="runOpen" width="500px" append-to-body>
      <el-form ref="runForm" :model="runForm" label-width="110px">
        <el-form-item label="商户号" prop="merId" :rules="[{ required: true, message: '请选择商户号', trigger: 'change' }]">
          <el-select v-model="runForm.merId" filterable :loading="merchantsLoading" placeholder="请选择商户" style="width: 100%">
            <el-option v-for="merchant in merchants" :key="merchant.merId"
              :label="(merchant.institutionName ? merchant.institutionName + ' / ' : '') + merchant.merId"
              :value="merchant.merId" />
          </el-select>
          <div v-if="!merchantsLoading && merchants.length === 0" class="form-tip">当前用户没有已启用、已验证的银行商户，无法执行对账</div>
        </el-form-item>
        <el-form-item label="清算日期" prop="clearingDate" :rules="[{ required: true, message: '请输入清算日期', trigger: 'blur' }]">
          <el-date-picker v-model="runForm.clearingDate" type="date" value-format="yyyy-MM-dd" placeholder="yyyy-MM-dd" style="width: 100%" />
        </el-form-item>
        <el-form-item label="指定文件名">
          <el-input v-model="runForm.fileName" placeholder="沙箱人工放文件场景可指定，留空则申请生成" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" :loading="runLoading" @click="submitRun">执行</el-button>
        <el-button @click="runOpen = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listReconRuns, listReconDiffs, handleReconDiff, runRecon, listReconMerchants } from '@/api/pension/bankRecon'

export default {
  name: 'BankRecon',
  data() {
    return {
      runsLoading: false,
      diffsLoading: false,
      merchantsLoading: false,
      merchants: [],
      runs: [],
      diffs: [],
      currentRun: null,
      handleOpen: false,
      handleLoading: false,
      handleForm: {},
      runOpen: false,
      runLoading: false,
      runForm: { merId: '', clearingDate: '', fileName: '' }
    }
  },
  created() {
    this.loadMerchants()
    this.loadRuns()
  },
  methods: {
    loadMerchants() {
      this.merchantsLoading = true
      listReconMerchants().then(response => {
        this.merchants = response.data || []
        if (!this.merchants.some(merchant => merchant.merId === this.runForm.merId)) {
          this.runForm.merId = this.merchants.length === 1 ? this.merchants[0].merId : ''
        }
      }).finally(() => { this.merchantsLoading = false })
    },
    loadRuns() {
      this.runsLoading = true
      listReconRuns(20).then(response => {
        this.runs = response.data || []
      }).finally(() => { this.runsLoading = false })
    },
    selectRun(run) {
      if (!run) return
      this.currentRun = run
      this.diffsLoading = true
      listReconDiffs(run.runId).then(response => {
        this.diffs = response.data || []
      }).finally(() => { this.diffsLoading = false })
    },
    openHandle(row) {
      this.handleForm = { diffId: row.diffId, diffType: row.diffType, requestNo: row.requestNo, remark: '' }
      this.handleOpen = true
    },
    submitHandle() {
      this.$refs.handleForm.validate(valid => {
        if (!valid) return
        this.handleLoading = true
        handleReconDiff({ diffId: String(this.handleForm.diffId), remark: this.handleForm.remark }).then(() => {
          this.$modal.msgSuccess('已标记处理')
          this.handleOpen = false
          this.selectRun(this.currentRun)
        }).finally(() => { this.handleLoading = false })
      })
    },
    submitRun() {
      this.$refs.runForm.validate(valid => {
        if (!valid) return
        this.runLoading = true
        runRecon(this.runForm).then(response => {
          const run = response.data || {}
          if (run.status === 'success') {
            this.$modal.msgSuccess('对账执行完成')
          } else if (run.status === 'no_bill') {
            this.$modal.msgWarning(run.respMessage || '银行尚未生成对账单')
          } else {
            this.$modal.msgError('对账失败：' + (run.respMessage || '未知错误'))
          }
          this.runOpen = false
          this.loadRuns()
          if (run.runId) {
            this.selectRun(run)
          }
        }).finally(() => { this.runLoading = false })
      })
    },
    statusText(status) {
      const map = {
        running: '执行中',
        success: '成功',
        failed: '失败',
        no_bill: '无账单'
      }
      return map[status] || status || '-'
    },
    diffTypeText(type) {
      const map = {
        BANK_ONLY: '银行有本地无',
        LOCAL_ONLY: '本地有银行无',
        AMOUNT_MISMATCH: '金额不符',
        SERIAL_MISMATCH: '流水不符'
      }
      return map[type] || type
    },
    diffTagType(type) {
      const map = {
        BANK_ONLY: 'danger',
        LOCAL_ONLY: 'warning',
        AMOUNT_MISMATCH: 'danger',
        SERIAL_MISMATCH: 'warning'
      }
      return map[type] || 'info'
    }
  }
}
</script>

<style scoped lang="scss">
.form-tip {
  color: #909399;
  font-size: 12px;
  line-height: 20px;
  margin-top: 4px;
}
</style>
