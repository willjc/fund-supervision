<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>监管账户划拨流水（已划拨）</span>
      </div>

      <!-- 搜索条件 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="养老机构">
          <el-select v-model="queryParams.institutionId" placeholder="请选择机构" clearable style="width: 200px">
            <el-option
              v-for="item in institutionOptions"
              :key="item.institution_id"
              :label="item.institution_name"
              :value="item.institution_id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="划拨日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="划拨类型">
          <el-select v-model="queryParams.transferType" placeholder="请选择" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="自动划拨" value="1"></el-option>
            <el-option label="手动划拨" value="2"></el-option>
            <el-option label="特殊申请" value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="老人姓名">
          <el-input v-model="queryParams.elderName" placeholder="请输入老人姓名" clearable style="width: 150px"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-download" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>

      <!-- 拨付单流水表格 -->
      <el-table v-loading="loading" :data="dataList" border style="margin-top: 20px">
        <el-table-column label="账单月份" prop="billingMonth" width="110"></el-table-column>
        <el-table-column label="划拨单号" prop="transferNo" width="190" show-overflow-tooltip></el-table-column>
        <el-table-column label="归属机构" prop="institutionName" width="170" show-overflow-tooltip></el-table-column>
        <el-table-column label="老人姓名" prop="elderName" width="110"></el-table-column>
        <el-table-column label="关联订单" prop="orderNo" width="170" show-overflow-tooltip></el-table-column>
        <el-table-column label="划拨金额" prop="transferAmount" width="140">
          <template slot-scope="scope">
            <span style="font-weight: bold; color: #f56c6c;">-¥{{ formatMoney(scope.row.transferAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="划拨类型" prop="transferType" width="100">
          <template slot-scope="scope">
            {{ getTransferTypeText(scope.row.transferType) }}
          </template>
        </el-table-column>
        <el-table-column label="划拨日期" prop="paidTime" width="160">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.paidTime) }}
          </template>
        </el-table-column>
        <el-table-column label="监管账户余额" prop="supervisionBalance" width="140">
          <template slot-scope="scope">
            <span style="font-weight: bold;">¥{{ formatMoney(scope.row.supervisionBalance) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="基本账户余额" prop="basicBalance" width="140">
          <template slot-scope="scope">
            <span style="font-weight: bold; color: #67c23a;">¥{{ formatMoney(scope.row.basicBalance) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="150" show-overflow-tooltip></el-table-column>
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
import { getSupervisionList, getUserInstitutions } from '@/api/pension/bank'

export default {
  name: 'BankSupervision',
  data() {
    return {
      loading: false,
      dataList: [],
      total: 0,
      dateRange: [],
      institutionOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionId: null,
        transferType: null,
        elderName: null
      }
    }
  },
  created() {
    this.loadInstitutions()
    this.getList()
  },
  methods: {
    // 加载机构列表
    loadInstitutions() {
      getUserInstitutions().then(response => {
        this.institutionOptions = response.data || []
      }).catch(error => {
        console.error('获取机构列表失败:', error)
      })
    },
    // 查询列表
    getList() {
      this.loading = true
      const params = {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize
      }
      if (this.queryParams.institutionId) {
        params.institutionId = this.queryParams.institutionId
      }
      if (this.dateRange && this.dateRange.length === 2) {
        params.beginTime = this.dateRange[0]
        params.endTime = this.dateRange[1]
      }
      if (this.queryParams.transferType) {
        params.transferType = this.queryParams.transferType
      }
      if (this.queryParams.elderName) {
        params.elderName = this.queryParams.elderName
      }

      getSupervisionList(params).then(response => {
        this.dataList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(error => {
        console.error('查询划拨流水失败:', error)
        this.loading = false
      })
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    // 重置
    resetQuery() {
      this.dateRange = []
      this.queryParams.institutionId = null
      this.queryParams.transferType = null
      this.queryParams.elderName = null
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 导出
    handleExport() {
      this.$message.success('导出功能开发中')
    },
    // 获取划拨类型文本
    getTransferTypeText(type) {
      const textMap = {
        '1': '自动划拨',
        '2': '手动划拨',
        '3': '特殊申请'
      }
      return textMap[type] || type
    },
    // 格式化日期时间
    formatDateTime(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    // 格式化金额
    formatMoney(value) {
      if (!value && value !== 0) return '0.00'
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
