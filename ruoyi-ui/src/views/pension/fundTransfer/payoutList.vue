<template>
  <div class="payout-list">
    <el-alert title="拨付以银行最终结果为准：受理成功不等于拨付成功，处理中或超时只能查询原请求；历史已完成记录已隔离，不会自动补发。" type="info" :closable="false" show-icon class="mb8" />
    <el-form ref="queryForm" :model="queryParams" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="划拨单号" prop="transferNo"><el-input v-model="queryParams.transferNo" clearable @keyup.enter.native="handleQuery" /></el-form-item>
      <el-form-item label="拨付状态" prop="status">
        <el-select v-model="queryParams.status" clearable><el-option v-for="item in statuses" :key="item.value" :label="item.label" :value="item.value" /></el-select>
      </el-form-item>
      <el-form-item><el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button><el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row class="mb8">
      <el-button size="mini" plain icon="el-icon-download" v-hasPermi="['pension:fundTransfer:export']" @click="handleExport">导出</el-button>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>
    <el-table v-loading="loading" :data="transferList" border>
      <el-table-column label="划拨单号" prop="transferNo" min-width="190" />
      <el-table-column label="老人" prop="elderName" width="100" />
      <el-table-column label="月份" prop="billingMonth" width="100" />
      <el-table-column label="拨付日期" width="110"><template slot-scope="scope">{{ formatDate(scope.row.transferDate) }}</template></el-table-column>
      <el-table-column label="拨付金额（元）" prop="transferAmount" width="130" />
      <el-table-column label="拨付状态" min-width="180"><template slot-scope="scope">{{ payoutStatus(scope.row) }}</template></el-table-column>
      <el-table-column label="银行流水" prop="bankOrderNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="失败/核查原因" prop="failureReason" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" width="280" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="mini" v-hasPermi="['pension:fundTransfer:query']" @click="handleView(scope.row)">详情</el-button>
          <el-button v-if="canApprovePayout(scope.row)" type="text" size="mini" v-hasPermi="['pension:fundTransfer:approve']" @click="handleApprove(scope.row)">审批</el-button>
          <el-button v-if="canSendPayout(scope.row)" type="text" size="mini" v-hasPermi="['pension:fundTransfer:execute']" @click="handleExecute(scope.row)">立即发送</el-button>
          <el-button v-if="scope.row.bankTransactionId" type="text" size="mini" v-hasPermi="['pension:fundTransfer:query']" @click="handleBankQuery(scope.row)">查询银行</el-button>
          <el-button v-if="canRetryPayout(scope.row)" type="text" size="mini" v-hasPermi="['pension:fundTransfer:execute']" @click="handleRetry(scope.row)">失败重试</el-button>
          <span v-if="showNotDue(scope.row)" class="not-due">未到 {{ formatDate(scope.row.transferDate) }}</span>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    <el-dialog title="拨付明细" :visible.sync="detailOpen" width="780px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="划拨单号">{{ detailData.transferNo }}</el-descriptions-item>
        <el-descriptions-item label="拨付状态">{{ payoutStatus(detailData) }}</el-descriptions-item>
        <el-descriptions-item label="金额（元）">{{ detailData.transferAmount }}</el-descriptions-item>
        <el-descriptions-item label="月份">{{ detailData.billingMonth || detailData.transferPeriod }}</el-descriptions-item>
        <el-descriptions-item label="拨付日期">{{ formatDate(detailData.transferDate) }}</el-descriptions-item>
        <el-descriptions-item label="银行流水">{{ detailData.bankOrderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ detailData.approveTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="失败/核查原因" :span="2">{{ detailData.failureReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="银行请求号">{{ bankResult.requestNo || '尚未查询' }}</el-descriptions-item>
        <el-descriptions-item label="本地记账状态">{{ bankResult.bookingStatus || '尚未查询' }}</el-descriptions-item>
        <el-descriptions-item label="银行记账时间">{{ bankResult.bankTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="银行状态">{{ bankResult.bankStatus || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退汇时间">{{ bankResult.returnTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退汇原因">{{ bankResult.returnReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="银行可用余额（元）">{{ bankBalance.status === 'SUCCESS' && bankBalance.availableBalance != null ? bankBalance.availableBalance : '未取得银行余额' }}</el-descriptions-item>
        <el-descriptions-item label="余额查询时间">{{ bankBalance.queriedAt || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button v-hasPermi="['pension:fundTransfer:query']" @click="handleBalance">查询银行余额</el-button>
        <el-button v-if="detailData.bankTransactionId" v-hasPermi="['pension:fundTransfer:query']" @click="handleBankQuery(detailData)">查询拨付结果</el-button>
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>
    <el-dialog title="拨付审批" :visible.sync="approveOpen" width="500px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" label-width="90px">
        <el-form-item label="划拨单号">{{ approveForm.transferNo }}</el-form-item>
        <el-form-item label="审批结果"><el-radio-group v-model="approveForm.result"><el-radio label="1">通过</el-radio><el-radio label="2">拒绝</el-radio></el-radio-group></el-form-item>
        <el-form-item label="审批意见" prop="remark" :rules="[{required: true, message: '请输入审批意见', trigger: 'blur'}]"><el-input v-model="approveForm.remark" type="textarea" maxlength="500" /></el-form-item>
      </el-form>
      <div slot="footer"><el-button type="primary" :loading="approveLoading" @click="submitApprove">确定</el-button><el-button @click="approveOpen = false">取消</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listFundTransfer, getFundTransfer, approveFundTransfer } from '@/api/pension/fundTransfer'
import { queryPayout, submitPayout, retryPayout, getBankAvailableBalance } from '@/api/pension/bank'
import { payoutStatus, canApprovePayout, canSendPayout, canRetryPayout, isPayoutDue } from '@/utils/payout'

export default {
  name: 'PayoutList',
  props: { institutionId: { type: [Number, String], default: null } },
  data() {
    return {
      loading: false, showSearch: true, transferList: [], total: 0,
      queryParams: { pageNum: 1, pageSize: 10, institutionId: null, transferNo: null, status: null },
      statuses: [{ value: 'pending', label: '待拨付' }, { value: 'processing', label: '银行处理中' }, { value: 'completed', label: '已完成' }, { value: 'failed', label: '拨付失败' }, { value: 'returned', label: '已退汇' }, { value: 'cancelled', label: '已取消' }],
      detailOpen: false, detailData: {}, bankResult: {}, bankBalance: {},
      approveOpen: false, approveLoading: false, approveForm: {}
    }
  },
  watch: {
    institutionId: {
      immediate: true,
      handler(value) { this.queryParams.institutionId = value; this.queryParams.pageNum = 1; this.getList() }
    }
  },
  methods: {
    payoutStatus, canApprovePayout, canSendPayout, canRetryPayout,
    showNotDue(row) {
      return Number(row.bankEligible) === 1 && row.status === 'pending' && !!row.approveTime && !isPayoutDue(row)
    },
    formatDate(value) {
      return value ? String(value).slice(0, 10) : '-'
    },
    getList() {
      if (!this.queryParams.institutionId) { this.transferList = []; this.total = 0; return }
      this.loading = true
      return listFundTransfer(this.queryParams).then(response => {
        this.transferList = response.rows || []
        this.total = response.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleView(row) {
      this.bankResult = {}; this.bankBalance = {}
      getFundTransfer(row.transferId).then(response => { this.detailData = response.data; this.detailOpen = true })
    },
    handleApprove(row) { this.approveForm = { transferId: row.transferId, transferNo: row.transferNo, result: '1', remark: '' }; this.approveOpen = true },
    submitApprove() {
      this.$refs.approveForm.validate(valid => {
        if (!valid) return
        this.approveLoading = true
        approveFundTransfer(this.approveForm.transferId, this.approveForm.result, this.approveForm.remark).then(() => {
          this.$modal.msgSuccess(this.approveForm.result === '1' ? '已批准，待银行拨付' : '已拒绝')
          this.approveOpen = false; this.getList(); this.$emit('changed')
        }).finally(() => { this.approveLoading = false })
      })
    },
    handleExecute(row) {
      this.$modal.confirm('确认立即向银行发送拨付单“' + row.transferNo + '”？发送后请点击“查询银行”确认最终结果，切勿重复发送。').then(() => submitPayout(row.transferId)).then(response => {
        this.$modal.msgSuccess(response.msg || '已向银行发起拨付请求，请查询最终结果')
        this.getList(); this.$emit('changed')
      }).catch(() => {})
    },
    handleBankQuery(row) {
      queryPayout(row.transferId).then(response => {
        this.bankResult = response.data || {}
        this.bankBalance = {}
        this.detailData = { ...this.bankResult.transfer, manualReview: this.bankResult.manualReview }
        this.detailOpen = true
        this.getList(); this.$emit('changed')
      })
    },
    handleRetry(row) {
      this.$modal.confirm('仅明确失败的拨付允许新建请求重试，确认重试“' + row.transferNo + '”？').then(() => retryPayout(row.transferId)).then(() => {
        this.$modal.msgSuccess('重试已提交，请查询银行最终结果'); this.getList(); this.$emit('changed')
      }).catch(() => {})
    },
    handleBalance() {
      this.bankBalance = {}
      getBankAvailableBalance(this.detailData.institutionId).then(response => { this.bankBalance = response.data || {} })
    },
    handleExport() { this.download('pension/fundTransfer/export', { ...this.queryParams }, 'fundTransfer_' + Date.now() + '.xlsx') }
  }
}
</script>

<style scoped>
.not-due { margin-left: 8px; color: #b86c08; font-size: 12px; }
</style>
