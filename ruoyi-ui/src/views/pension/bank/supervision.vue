<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>监管账户与基本账户流水</span>
      </div>

      <!-- 搜索条件 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="交易时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="交易类型">
          <el-select v-model="queryParams.transactionType" placeholder="请选择" clearable>
            <el-option label="全部" value=""></el-option>
            <el-option label="收入" value="收入"></el-option>
            <el-option label="支出" value="支出"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-download" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>

      <!-- 交易流水表格 -->
      <el-table v-loading="loading" :data="dataList" border style="margin-top: 20px">
        <el-table-column label="交易流水号" prop="transactionNo" width="180"></el-table-column>
        <el-table-column label="交易时间" prop="transactionTime" width="180"></el-table-column>
        <el-table-column label="交易类型" prop="transactionType" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.transactionType === '转入' ? 'success' : 'warning'">
              {{ scope.row.transactionType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="账户类型" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.transactionType === '转入' ? 'primary' : 'warning'">
              {{ scope.row.transactionType === '转入' ? '监管账户' : '基本账户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="交易金额" prop="amount" width="150">
          <template slot-scope="scope">
            <span :class="scope.row.transactionType === '转入' ? 'text-success' : 'text-danger'">
              {{ scope.row.transactionType === '转入' ? '+' : '-' }}¥{{ formatMoney(scope.row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="监管账户余额" prop="balance" width="150">
          <template slot-scope="scope">
            <span style="font-weight: bold;">¥{{ formatMoney(scope.row.balance) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="业务类型" prop="businessType" width="120">
          <template slot-scope="scope">
            {{ scope.row.businessType || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="交易说明" prop="description" show-overflow-tooltip></el-table-column>
        <el-table-column label="对方户名" prop="counterpartyName" width="200"></el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 详情对话框 -->
      <el-dialog title="账户流水详情" :visible.sync="detailOpen" width="700px" append-to-body>
        <el-descriptions :column="2" border v-if="currentDetail">
          <el-descriptions-item label="交易流水号">{{ currentDetail.transactionNo }}</el-descriptions-item>
          <el-descriptions-item label="交易时间">{{ currentDetail.transactionTime }}</el-descriptions-item>
          <el-descriptions-item label="交易类型">
            <el-tag :type="currentDetail.transactionType === '转入' ? 'success' : 'warning'">
              {{ currentDetail.transactionType }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="账户类型">
            <el-tag :type="currentDetail.transactionType === '转入' ? 'primary' : 'warning'">
              {{ currentDetail.transactionType === '转入' ? '监管账户' : '基本账户' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="业务类型">{{ currentDetail.businessType || '-' }}</el-descriptions-item>
          <el-descriptions-item label="交易金额">
            <span :class="currentDetail.transactionType === '转入' ? 'text-success' : 'text-danger'" style="font-weight: bold;">
              {{ currentDetail.transactionType === '转入' ? '+' : '-' }}¥{{ formatMoney(currentDetail.amount) }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="监管账户余额">
            <span style="font-weight: bold;">¥{{ formatMoney(currentDetail.balance) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="交易前余额" :span="2">
            ¥{{ formatMoney(currentDetail.balanceBefore) }}
          </el-descriptions-item>
          <el-descriptions-item label="交易说明" :span="2">{{ currentDetail.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="对方账户">{{ currentDetail.counterparty || '-' }}</el-descriptions-item>
          <el-descriptions-item label="操作人">{{ currentDetail.operator || '-' }}</el-descriptions-item>
          <el-descriptions-item label="关联划拨单" :span="2" v-if="currentDetail.relatedTransferId">
            {{ currentDetail.relatedTransferId }}
          </el-descriptions-item>
          <el-descriptions-item label="关联订单" :span="2" v-if="currentDetail.relatedOrderId">
            {{ currentDetail.relatedOrderId }}
          </el-descriptions-item>
        </el-descriptions>
        <div slot="footer" class="dialog-footer">
          <el-button @click="detailOpen = false">关闭</el-button>
        </div>
      </el-dialog>

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
import { getSupervisionList, getSupervisionDetail } from '@/api/pension/bank'

export default {
  name: 'BankSupervision',
  data() {
    return {
      loading: false,
      dataList: [],
      total: 0,
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        transactionType: null
      },
      detailOpen: false,
      currentDetail: null
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询列表
    getList() {
      this.loading = true
      const params = {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize
      }
      if (this.dateRange && this.dateRange.length === 2) {
        params.beginTime = this.dateRange[0]
        params.endTime = this.dateRange[1]
      }
      if (this.queryParams.transactionType) {
        params.transactionType = this.queryParams.transactionType === '转入' ? '收入' : '支出'
      }

      getSupervisionList(params).then(response => {
        this.dataList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(error => {
        console.error('查询监管账户流水失败:', error)
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
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 导出
    handleExport() {
      this.$message.success('导出功能开发中')
    },
    // 查看详情
    handleDetail(row) {
      getSupervisionDetail(row.logId).then(response => {
        this.currentDetail = response.data
        this.detailOpen = true
      }).catch(error => {
        console.error('获取详情失败:', error)
      })
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
.text-success {
  color: #67c23a;
}

.text-danger {
  color: #f56c6c;
}
</style>
