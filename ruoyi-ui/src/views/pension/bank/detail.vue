<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>监管账户银行交易明细（OBK0071708，银行对账单口径）</span>
      </div>

      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 12px">
        <div slot="title">使用说明：本页直接查询银行监管账户流水（708）及可用余额（709），用于核对拨付结果，不代表平台内部记账余额。</div>
        <div>拨付测试顺序：先在“拨付管理”立即发送（702），再点“查询银行”（703）确认状态；本页可核对银行实际扣账明细与余额。查询受服务端银行对接开关控制，未开闸时会返回网关拒绝提示。</div>
      </el-alert>

      <!-- 搜索条件 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="养老机构">
          <el-select v-model="queryParams.institutionId" placeholder="请选择机构" clearable style="width: 220px">
            <el-option
              v-for="item in institutionOptions"
              :key="item.institution_id"
              :label="item.institution_name"
              :value="item.institution_id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="交易日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyyMMdd">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">查询银行</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 余额卡片 -->
      <el-alert v-if="balance.status" type="success" :closable="false" show-icon style="margin-bottom: 12px">
        <template slot="title">
          监管户可用余额：
          <span v-if="balance.status === 'SUCCESS'" style="font-weight: bold;">¥{{ formatMoney(balance.availableBalance) }}</span>
          <span v-else>未取得银行余额</span>
          （查询时间 {{ formatDateTime(balance.queriedAt) }}）
        </template>
      </el-alert>

      <!-- 明细表格 -->
      <el-table v-loading="loading" :data="rows" border>
        <el-table-column label="银行记账时间" width="160">
          <template slot-scope="scope">{{ formatBankTime(scope.row) }}</template>
        </el-table-column>
        <el-table-column label="方向" width="70">
          <template slot-scope="scope">
            <el-tag :type="scope.row.amntcd === '1' ? 'success' : 'danger'" size="mini">
              {{ scope.row.amntcd === '1' ? '入账' : '出账' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="金额（元）" width="120">
          <template slot-scope="scope">
            <span :style="{ fontWeight: 'bold', color: scope.row.amntcd === '1' ? '#67c23a' : '#f56c6c' }">
              {{ scope.row.amntcd === '1' ? '+' : '-' }}{{ formatMoney(scope.row.tranam) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="交易后余额（元）" width="140">
          <template slot-scope="scope">¥{{ formatMoney(scope.row.onlnbl) }}</template>
        </el-table-column>
        <el-table-column label="对方账号" prop="toacct" min-width="170" show-overflow-tooltip />
        <el-table-column label="对方户名" prop="toacna" min-width="150" show-overflow-tooltip />
        <el-table-column label="摘要" prop="smrytx" min-width="120" show-overflow-tooltip />
        <el-table-column label="说明" prop="dscrtx" min-width="150" show-overflow-tooltip />
        <el-table-column label="银行流水号" prop="transq" min-width="210" show-overflow-tooltip />
        <el-table-column label="开户行" prop="banknm" min-width="150" show-overflow-tooltip />
      </el-table>
      <div v-if="rows.length" style="margin-top: 12px; text-align: right;">
        <el-button size="mini" :disabled="pagenm <= 1" @click="prevPage">上一页</el-button>
        <span style="margin: 0 10px;">第 {{ pagenm }} 页（本页 {{ rows.length }} 条）</span>
        <el-button size="mini" :disabled="rows.length < pageSize" @click="nextPage">下一页</el-button>
      </div>
      <div v-if="!loading && queried && !rows.length" style="margin-top: 20px; text-align: center; color: #909399;">
        查询区间内没有明细记录（请确认区间覆盖银行有数据的日期）
      </div>
    </el-card>
  </div>
</template>

<script>
import { getUserInstitutions, getBankAccountDetail, getBankAvailableBalance } from '@/api/pension/bank'

export default {
  name: 'BankAccountDetail',
  data() {
    return {
      loading: false,
      queried: false,
      rows: [],
      balance: {},
      institutionOptions: [],
      dateRange: [],
      pagenm: 1,
      pageSize: 10,
      queryParams: { institutionId: null }
    }
  },
  created() {
    this.loadInstitutions()
  },
  methods: {
    loadInstitutions() {
      getUserInstitutions().then(response => {
        this.institutionOptions = response.data || []
        if (this.institutionOptions.length === 1) {
          this.queryParams.institutionId = this.institutionOptions[0].institution_id
        }
      }).catch(error => {
        console.error('获取机构列表失败:', error)
      })
    },
    handleQuery() {
      if (!this.queryParams.institutionId) {
        this.$message.warning('请先选择养老机构')
        return
      }
      if (!this.dateRange || this.dateRange.length !== 2) {
        this.$message.warning('请选择交易日期区间')
        return
      }
      this.pagenm = 1
      this.getList()
    },
    getList() {
      this.loading = true
      const params = {
        stardt: this.dateRange[0],
        enddat: this.dateRange[1],
        pagenm: String(this.pagenm),
        rcrdnm: String(this.pageSize)
      }
      getBankAccountDetail(this.queryParams.institutionId, params).then(response => {
        this.rows = (response.data && response.data.rows) || []
        this.queried = true
      }).catch(error => {
        // 协议未开闸时网关拒绝属于预期提示，原样透出。
        this.rows = []
        this.queried = true
        const msg = (error && error.message) || '查询失败'
        this.$message.error(msg)
      }).finally(() => {
        this.loading = false
      })
      getBankAvailableBalance(this.queryParams.institutionId).then(response => {
        this.balance = response.data || {}
      }).catch(() => {
        this.balance = {}
      })
    },
    prevPage() {
      if (this.pagenm > 1) {
        this.pagenm -= 1
        this.getList()
      }
    },
    nextPage() {
      this.pagenm += 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.queryParams.institutionId = null
      this.rows = []
      this.balance = {}
      this.queried = false
      this.pagenm = 1
    },
    formatBankTime(row) {
      if (!row.trandt) return '-'
      const date = row.trandt
      const time = row.trantm || ''
      if (date.length === 8 && time.length >= 4) {
        return `${date.substring(0, 4)}-${date.substring(4, 6)}-${date.substring(6, 8)} ${time.substring(0, 2)}:${time.substring(2, 4)}`
      }
      return date + ' ' + time
    },
    formatDateTime(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    },
    formatMoney(value) {
      if (value == null) return '0.00'
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }
  }
}
</script>

<style scoped lang="scss">
</style>
