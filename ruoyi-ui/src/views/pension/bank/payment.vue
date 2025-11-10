<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>收单交易流水</span>
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
        <el-form-item label="支付方式">
          <el-select v-model="queryParams.paymentMethod" placeholder="请选择" clearable>
            <el-option label="微信支付" value="微信支付"></el-option>
            <el-option label="支付宝" value="支付宝"></el-option>
            <el-option label="银行卡" value="银行卡"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="订单号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-download" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-cards">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">今日交易笔数</div>
            <div class="stat-value">{{ stats.todayCount }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">今日交易金额</div>
            <div class="stat-value">¥{{ formatMoney(stats.todayAmount) }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">本月交易笔数</div>
            <div class="stat-value">{{ stats.monthCount }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">本月交易金额</div>
            <div class="stat-value">¥{{ formatMoney(stats.monthAmount) }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 交易流水表格 -->
      <el-table v-loading="loading" :data="dataList" border style="margin-top: 20px">
        <el-table-column label="订单号" prop="orderNo" width="180"></el-table-column>
        <el-table-column label="支付流水号" prop="transactionNo" width="200"></el-table-column>
        <el-table-column label="支付时间" prop="paymentTime" width="180"></el-table-column>
        <el-table-column label="支付金额" prop="amount" width="120">
          <template slot-scope="scope">
            ¥{{ formatMoney(scope.row.amount) }}
          </template>
        </el-table-column>
        <el-table-column label="支付方式" prop="paymentMethod" width="120"></el-table-column>
        <el-table-column label="支付渠道" prop="paymentChannel" width="120"></el-table-column>
        <el-table-column label="手续费" prop="fee" width="100">
          <template slot-scope="scope">
            ¥{{ formatMoney(scope.row.fee) }}
          </template>
        </el-table-column>
        <el-table-column label="实际到账" prop="actualAmount" width="120">
          <template slot-scope="scope">
            ¥{{ formatMoney(scope.row.actualAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="订单状态" prop="status" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" show-overflow-tooltip></el-table-column>
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
  name: 'BankPayment',
  data() {
    return {
      loading: false,
      dataList: [],
      total: 0,
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        paymentMethod: null,
        orderNo: null
      },
      stats: {
        todayCount: 28,
        todayAmount: 45678.90,
        monthCount: 856,
        monthAmount: 1234567.89
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
            orderNo: 'DD202501030001',
            transactionNo: 'WX202501030001234567',
            paymentTime: '2025-01-03 10:30:25',
            amount: 3000,
            paymentMethod: '微信支付',
            paymentChannel: '微信',
            fee: 18,
            actualAmount: 2982,
            status: '已完成',
            remark: '老人月度护理费'
          },
          {
            orderNo: 'DD202501030002',
            transactionNo: 'ZFB202501030002345678',
            paymentTime: '2025-01-03 09:15:10',
            amount: 2500,
            paymentMethod: '支付宝',
            paymentChannel: '支付宝',
            fee: 15,
            actualAmount: 2485,
            status: '已完成',
            remark: '老人餐费'
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
    },
    // 获取状态类型
    getStatusType(status) {
      const typeMap = {
        '已完成': 'success',
        '处理中': 'warning',
        '已取消': 'info',
        '失败': 'danger'
      }
      return typeMap[status] || 'info'
    }
  }
}
</script>

<style scoped lang="scss">
.stats-cards {
  margin-bottom: 20px;

  .stat-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px;
    border-radius: 8px;
    color: white;
    text-align: center;
  }

  .stat-label {
    font-size: 14px;
    margin-bottom: 10px;
    opacity: 0.9;
  }

  .stat-value {
    font-size: 24px;
    font-weight: bold;
  }

  .el-col:nth-child(2) .stat-card {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }

  .el-col:nth-child(3) .stat-card {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }

  .el-col:nth-child(4) .stat-card {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  }
}
</style>
