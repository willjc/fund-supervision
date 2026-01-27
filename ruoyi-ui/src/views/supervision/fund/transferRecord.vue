<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>资金划拨记录（已划拨）</span>
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
        <el-form-item label="划拨类型" prop="transferType">
          <el-select v-model="queryParams.transferType" placeholder="请选择" clearable>
            <el-option label="自动划拨" value="1" />
            <el-option label="手动申请" value="2" />
            <el-option label="特殊划拨" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="老人姓名" prop="elderName">
          <el-input v-model="queryParams.elderName" placeholder="请输入老人姓名" clearable />
        </el-form-item>
        <el-form-item label="划拨日期">
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
        <el-table-column label="划拨单号" prop="transferNo" width="180" />
        <el-table-column label="账单月份" prop="billingMonth" width="120" align="center" />
        <el-table-column label="老人姓名" prop="elderName" width="120" />
        <el-table-column label="归属机构" prop="institutionName" width="180" show-overflow-tooltip />
        <el-table-column label="划拨类型" prop="transferType" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getTransferTypeTag(scope.row.transferType)">
              {{ getTransferTypeLabel(scope.row.transferType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="划拨金额" prop="transferAmount" width="150" align="right">
          <template slot-scope="scope">
            <span class="text-danger" style="font-weight: bold;">-¥{{ formatMoney(scope.row.transferAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="划拨日期" prop="paidTime" width="160" align="center">
          <template slot-scope="scope">
            {{ formatDateTime(scope.row.paidTime) }}
          </template>
        </el-table-column>
        <el-table-column label="监管账户余额" prop="supervisionBalance" width="150" align="right">
          <template slot-scope="scope">
            <span style="color: #409eff; font-weight: bold;">¥{{ formatMoney(scope.row.supervisionBalance) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="基本账户余额" prop="basicBalance" width="150" align="right">
          <template slot-scope="scope">
            <span style="color: #67c23a; font-weight: bold;">¥{{ formatMoney(scope.row.basicBalance) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
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

    <!-- 详情对话框 -->
    <el-dialog title="划拨单详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border v-if="currentDetail">
        <el-descriptions-item label="划拨单号" :span="2">{{ currentDetail.transferNo }}</el-descriptions-item>
        <el-descriptions-item label="账单月份">{{ currentDetail.billingMonth }}</el-descriptions-item>
        <el-descriptions-item label="划拨类型">
          <el-tag :type="getTransferTypeTag(currentDetail.transferType)">
            {{ getTransferTypeLabel(currentDetail.transferType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ currentDetail.elderName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="归属机构">{{ currentDetail.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="划拨金额">
          <span class="text-danger" style="font-weight: bold;">-¥{{ formatMoney(currentDetail.transferAmount) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="划拨日期">{{ formatDateTime(currentDetail.paidTime) }}</el-descriptions-item>
        <el-descriptions-item label="监管账户余额" :span="2">
          <span style="color: #409eff; font-weight: bold;">¥{{ formatMoney(currentDetail.supervisionBalance) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="基本账户余额" :span="2">
          <span style="color: #67c23a; font-weight: bold;">¥{{ formatMoney(currentDetail.basicBalance) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="关联订单" :span="2" v-if="currentDetail.orderNo">
          {{ currentDetail.orderNo }}
        </el-descriptions-item>
        <el-descriptions-item label="备注说明" :span="2">{{ currentDetail.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>
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
      detailOpen: false,
      currentDetail: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionId: null,
        transferType: null,
        elderName: null,
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
      if (this.queryParams.transferType) {
        params.transferType = this.queryParams.transferType
      }
      if (this.queryParams.elderName) {
        params.elderName = this.queryParams.elderName
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
      this.$confirm('是否确认导出所有资金划拨记录?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.success('导出功能开发中')
      })
    },
    formatMoney(value) {
      if (!value && value !== 0) return '0.00'
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    },
    formatDateTime(value) {
      if (!value) return '-'
      const date = new Date(value)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      const second = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`
    },
    getTransferTypeLabel(type) {
      const map = {
        '1': '自动划拨',
        '2': '手动申请',
        '3': '特殊划拨'
      }
      return map[type] || '-'
    },
    getTransferTypeTag(type) {
      const map = {
        '1': 'success',
        '2': 'warning',
        '3': 'danger'
      }
      return map[type] || 'info'
    },
    handleDetail(row) {
      this.currentDetail = row
      this.detailOpen = true
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
