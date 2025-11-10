<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>监管账户交易流水</span>
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
            <el-option label="转入" value="转入"></el-option>
            <el-option label="转出" value="转出"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-download" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>

      <!-- 账户信息卡片 -->
      <el-row :gutter="20" class="account-info">
        <el-col :span="8">
          <div class="info-card">
            <div class="info-label">账户余额</div>
            <div class="info-value">¥{{ formatMoney(accountInfo.balance) }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-card">
            <div class="info-label">本月收入</div>
            <div class="info-value success">¥{{ formatMoney(accountInfo.monthIncome) }}</div>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-card">
            <div class="info-label">本月支出</div>
            <div class="info-value danger">¥{{ formatMoney(accountInfo.monthExpense) }}</div>
          </div>
        </el-col>
      </el-row>

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
        <el-table-column label="交易金额" prop="amount" width="150">
          <template slot-scope="scope">
            <span :class="scope.row.transactionType === '转入' ? 'text-success' : 'text-danger'">
              {{ scope.row.transactionType === '转入' ? '+' : '-' }}¥{{ formatMoney(scope.row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="账户余额" prop="balance" width="150">
          <template slot-scope="scope">
            ¥{{ formatMoney(scope.row.balance) }}
          </template>
        </el-table-column>
        <el-table-column label="交易说明" prop="description" show-overflow-tooltip></el-table-column>
        <el-table-column label="对方户名" prop="counterpartyName" width="200"></el-table-column>
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
      accountInfo: {
        balance: 1234567.89,
        monthIncome: 456789.00,
        monthExpense: 234567.00
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询列表
    getList() {
      this.loading = true
      // TODO: 调用后端API
      setTimeout(() => {
        this.dataList = [
          {
            transactionNo: 'LSH202501030001',
            transactionTime: '2025-01-03 10:30:25',
            transactionType: '转入',
            amount: 50000,
            balance: 1234567.89,
            description: '老人家属缴费',
            counterpartyName: '张三'
          },
          {
            transactionNo: 'LSH202501030002',
            transactionTime: '2025-01-03 09:15:10',
            transactionType: '转出',
            amount: 20000,
            balance: 1184567.89,
            description: '划拨到运营账户',
            counterpartyName: '运营账户'
          }
        ]
        this.total = 2
        this.loading = false
      }, 500)
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
.account-info {
  margin-bottom: 20px;

  .info-card {
    background: #f5f7fa;
    padding: 20px;
    border-radius: 8px;
    text-align: center;
  }

  .info-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 10px;
  }

  .info-value {
    font-size: 24px;
    font-weight: bold;
    color: #303133;

    &.success {
      color: #67c23a;
    }

    &.danger {
      color: #f56c6c;
    }
  }
}

.text-success {
  color: #67c23a;
}

.text-danger {
  color: #f56c6c;
}
</style>
