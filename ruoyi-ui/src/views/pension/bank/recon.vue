<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="recon-header">
        <span>银行对账差异处理</span>
        <el-radio-group v-model="reconType" size="mini" @change="onReconTypeChange">
          <el-radio-button label="supervision">监管对账</el-radio-button>
          <el-radio-button label="acquiring">收单对账</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 监管对账（706） -->
      <div v-if="reconType === 'supervision'">
        <el-row class="mb8">
          <el-select v-model="supervisionAccountKey" filterable placeholder="请选择监管账户"
            :loading="supervisionAccountsLoading" style="width: 360px">
            <el-option v-for="account in supervisionAccounts" :key="accountKey(account)"
              :label="accountLabel(account)" :value="accountKey(account)" />
          </el-select>
          <el-button type="primary" size="mini" icon="el-icon-refresh" v-hasPermi="['bank:recon:run']"
            :disabled="supervisionAccounts.length === 0" @click="openSupervisionRun">执行对账</el-button>
          <el-button size="mini" icon="el-icon-search" @click="loadSupervisionRuns">刷新批次</el-button>
        </el-row>
        <div v-if="!supervisionAccountsLoading && supervisionAccounts.length === 0" class="form-tip">
          当前用户没有已启用、已验证的监管账户，无法执行对账
        </div>

        <el-table v-loading="supervisionRunsLoading" :data="supervisionRuns" border>
          <el-table-column label="批次号" prop="runId" width="80" />
          <el-table-column label="对账日期" width="110">
            <template slot-scope="scope">{{ formatDay(scope.row.trandt) }}</template>
          </el-table-column>
          <el-table-column label="监管账号" prop="accountNo" min-width="180" show-overflow-tooltip />
          <el-table-column label="状态" width="100">
            <template slot-scope="scope">{{ statusText(scope.row.status) }}</template>
          </el-table-column>
          <el-table-column label="银行返回" prop="respMsg" min-width="180" show-overflow-tooltip />
          <el-table-column label="文件名" prop="fileName" min-width="220" show-overflow-tooltip />
          <el-table-column label="大小" width="90">
            <template slot-scope="scope">{{ formatSize(scope.row.fileSize) }}</template>
          </el-table-column>
          <el-table-column label="总行数" prop="totalRows" width="80" />
          <el-table-column label="匹配" prop="matchedRows" width="80" />
          <el-table-column label="差异" prop="diffRows" width="80" />
          <el-table-column label="执行时间" prop="createTime" width="160" />
          <el-table-column label="操作" width="120" fixed="right">
            <template slot-scope="scope">
              <el-button v-if="scope.row.status === 'running' || scope.row.status === 'processing' || scope.row.status === 'failed'" type="text"
                size="mini" v-hasPermi="['bank:recon:run']" @click="retrySupervision(scope.row)">重新获取文件</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-alert class="mt10" type="info" :closable="false" show-icon
          title="已按银行文件与本地 TRANSFER 流水比对；差异只统计数量，明细待后续按需补充。" />
      </div>

      <!-- 收单对账（原有功能） -->
      <div v-else>
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
      </div>
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

    <!-- 监管执行对账弹窗 -->
    <el-dialog title="监管对账" :visible.sync="supervisionRunOpen" width="520px" append-to-body>
      <el-form ref="supervisionRunForm" :model="supervisionRunForm" label-width="110px">
        <el-form-item label="监管账号">
          <el-input :value="selectedSupervisionAccountLabel" disabled />
        </el-form-item>
        <el-form-item label="对账日期" prop="trandt" :rules="[{ required: true, message: '请选择对账日期', trigger: 'blur' }]">
          <el-date-picker v-model="supervisionRunForm.trandt" type="date" value-format="yyyy-MM-dd" placeholder="yyyy-MM-dd" style="width: 100%" />
        </el-form-item>
        <el-form-item label="指定文件名">
          <el-input v-model="supervisionRunForm.fileName" placeholder="沙箱可指定；只填文件名时自动拼 MZYL/downdir/日期/" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" :loading="supervisionRunLoading" @click="submitSupervisionRun">执行</el-button>
        <el-button @click="supervisionRunOpen = false">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listReconRuns, listReconDiffs, handleReconDiff, runRecon, listReconMerchants,
  listSupervisionAccounts, listSupervisionRuns, runSupervisionRecon, retrySupervisionRecon
} from '@/api/pension/bankRecon'

export default {
  name: 'BankRecon',
  data() {
    return {
      reconType: 'supervision',
      // 收单对账
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
      runForm: { merId: '', clearingDate: '', fileName: '' },
      // 监管对账
      supervisionAccountsLoading: false,
      supervisionAccounts: [],
      supervisionAccountKey: '',
      supervisionRunsLoading: false,
      supervisionRuns: [],
      supervisionRunOpen: false,
      supervisionRunLoading: false,
      supervisionRunForm: { trandt: '', fileName: '' }
    }
  },
  computed: {
    selectedSupervisionAccount() {
      return this.supervisionAccounts.find(account => this.accountKey(account) === this.supervisionAccountKey) || null
    },
    selectedSupervisionAccountLabel() {
      const account = this.selectedSupervisionAccount
      return account ? this.accountLabel(account) : ''
    }
  },
  created() {
    this.loadSupervisionAccounts()
    this.loadSupervisionRuns()
  },
  methods: {
    onReconTypeChange(type) {
      if (type === 'supervision') {
        this.loadSupervisionAccounts()
        this.loadSupervisionRuns()
      } else {
        if (this.merchants.length === 0) {
          this.loadMerchants()
        }
        this.loadRuns()
      }
    },
    accountKey(account) {
      return `${account.institutionId}_${account.accountNo}`
    },
    accountLabel(account) {
      return `${account.institutionName || ''} / ${account.accountNo || ''}`
    },
    loadSupervisionAccounts() {
      this.supervisionAccountsLoading = true
      listSupervisionAccounts().then(response => {
        this.supervisionAccounts = response.data || []
        const keys = this.supervisionAccounts.map(account => this.accountKey(account))
        if (!keys.includes(this.supervisionAccountKey)) {
          this.supervisionAccountKey = this.supervisionAccounts.length > 0
            ? this.accountKey(this.supervisionAccounts[0]) : ''
        }
      }).finally(() => { this.supervisionAccountsLoading = false })
    },
    loadSupervisionRuns() {
      this.supervisionRunsLoading = true
      listSupervisionRuns(20).then(response => {
        this.supervisionRuns = response.data || []
      }).finally(() => { this.supervisionRunsLoading = false })
    },
    openSupervisionRun() {
      if (!this.selectedSupervisionAccount) {
        this.$modal.msgWarning('请选择监管账户')
        return
      }
      this.supervisionRunOpen = true
    },
    submitSupervisionRun() {
      this.$refs.supervisionRunForm.validate(valid => {
        if (!valid) return
        const account = this.selectedSupervisionAccount
        if (!account) {
          this.$modal.msgWarning('请选择监管账户')
          return
        }
        this.supervisionRunLoading = true
        runSupervisionRecon({
          institutionId: String(account.institutionId),
          accountNo: account.accountNo,
          trandt: this.supervisionRunForm.trandt,
          fileName: this.supervisionRunForm.fileName
        }).then(response => {
          const run = response.data || {}
          if (run.status === 'success') {
            this.$modal.msgSuccess('对账完成，文件已下载')
          } else if (run.status === 'processing') {
            this.$modal.msgWarning(run.respMsg || '银行已受理，等待对账文件')
          } else {
            this.$modal.msgError('对账失败：' + (run.respMsg || '未知错误'))
          }
          this.supervisionRunOpen = false
          this.loadSupervisionRuns()
        }).finally(() => { this.supervisionRunLoading = false })
      })
    },
    retrySupervision(row) {
      this.supervisionRunsLoading = true
      retrySupervisionRecon(row.runId, {}).then(response => {
        const run = response.data || {}
        if (run.status === 'success') {
          this.$modal.msgSuccess('文件已获取')
        } else if (run.status === 'processing') {
          this.$modal.msgWarning(run.respMsg || '银行仍在对账处理中')
        } else {
          this.$modal.msgError('获取失败：' + (run.respMsg || '未知错误'))
        }
        this.loadSupervisionRuns()
      }).finally(() => { this.supervisionRunsLoading = false })
    },
    // 收单对账
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
    formatDay(value) {
      return value ? String(value).substring(0, 10) : '-'
    },
    formatSize(value) {
      const size = Number(value)
      if (!size) return '-'
      if (size < 1024) return size + ' B'
      if (size < 1024 * 1024) return (size / 1024).toFixed(1) + ' KB'
      return (size / 1024 / 1024).toFixed(1) + ' MB'
    },
    statusText(status) {
      const map = {
        running: '执行中',
        processing: '待文件',
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
.recon-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.form-tip {
  color: #909399;
  font-size: 12px;
  line-height: 20px;
  margin-top: 4px;
}
.mt10 {
  margin-top: 10px;
}
</style>
