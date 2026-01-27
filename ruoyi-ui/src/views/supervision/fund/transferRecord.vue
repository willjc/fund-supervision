<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>资金划付记录</span>
      </div>

      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
        <el-form-item label="养老机构" prop="institutionId">
          <el-select v-model="queryParams.institutionId" placeholder="请选择机构" clearable filterable>
            <el-option
              v-for="item in institutionOptions"
              :key="item.institution_id"
              :label="item.institution_name"
              :value="item.institution_id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="交易类型" prop="transactionType">
          <el-select v-model="queryParams.transactionType" placeholder="请选择" clearable>
            <el-option label="收入" value="收入" />
            <el-option label="支出" value="支出" />
          </el-select>
        </el-form-item>
        <el-form-item label="交易时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button
            type="warning"
            icon="el-icon-download"
            @click="handleExport"
            v-hasPermi="['supervision:transferRecord:export']"
          >导出</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="recordList" border>
        <el-table-column label="流水号" prop="logId" width="100" />
        <el-table-column label="机构名称" prop="institutionName" width="200" show-overflow-tooltip />
        <el-table-column label="交易时间" prop="transactionTime" width="180" />
        <el-table-column label="交易类型" prop="transactionType" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.transactionType === '收入' ? 'success' : 'warning'">
              {{ scope.row.transactionType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="金额" prop="amount" width="150">
          <template slot-scope="scope">
            <span :class="scope.row.transactionType === '收入' ? 'text-success' : 'text-danger'" style="font-weight: bold;">
              {{ scope.row.transactionType === '收入' ? '+' : '-' }}¥{{ formatMoney(scope.row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="业务描述" prop="businessDesc" min-width="200" show-overflow-tooltip />
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script>
import { listTransferRecord, exportTransferRecord, getAllInstitutions } from '@/api/supervision/fund'

export default {
  name: 'TransferRecord',
  data() {
    return {
      loading: false,
      showSearch: true,
      recordList: [],
      total: 0,
      dateRange: [],
      institutionOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionId: null,
        transactionType: null,
        beginTime: null,
        endTime: null
      }
    }
  },
  created() {
    this.loadInstitutions()
    this.getList()
  },
  methods: {
    loadInstitutions() {
      getAllInstitutions().then(response => {
        this.institutionOptions = response.data || []
      }).catch(() => {})
    },
    getList() {
      this.loading = true
      const params = {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize
      }
      if (this.queryParams.institutionId) {
        params.institutionId = this.queryParams.institutionId
      }
      if (this.queryParams.transactionType) {
        params.transactionType = this.queryParams.transactionType
      }
      if (this.dateRange && this.dateRange.length === 2) {
        params.beginTime = this.dateRange[0]
        params.endTime = this.dateRange[1]
      }

      listTransferRecord(params).then(response => {
        this.recordList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleExport() {
      this.$confirm('是否确认导出所有资金划付记录?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('导出功能开发中')
      })
    },
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
.text-success {
  color: #67c23a;
}

.text-danger {
  color: #f56c6c;
}
</style>
