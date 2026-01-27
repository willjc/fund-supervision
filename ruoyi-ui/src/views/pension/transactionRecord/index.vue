<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="交易单号" prop="transactionNo">
        <el-input
          v-model="queryParams.transactionNo"
          placeholder="请输入交易单号"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账户编号" prop="accountNo">
        <el-input
          v-model="queryParams.accountNo"
          placeholder="请输入账户编号"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="老人姓名" prop="elderName">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入老人姓名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="交易类型" prop="transactionType">
        <el-select v-model="queryParams.transactionType" placeholder="请选择交易类型" clearable size="small">
          <el-option label="充值" value="1" />
          <el-option label="划拨" value="2" />
          <el-option label="退费" value="3" />
          <el-option label="消费" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="交易时间">
        <el-date-picker
          v-model="dateRange"
          size="small"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pension:transaction:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-pie-chart"
          size="mini"
          @click="handleStatistics"
        >统计分析</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="transactionList" style="width: 100%" border>
      <el-table-column label="交易单号" align="center" prop="transactionNo" width="180" />
      <el-table-column label="账户编号" align="center" prop="accountNo" width="150" />
      <el-table-column label="老人姓名" align="center" prop="elderName" width="120" />
      <el-table-column label="交易类型" align="center" prop="transactionType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.transactionType === '1'" type="success">充值</el-tag>
          <el-tag v-else-if="scope.row.transactionType === '2'" type="primary">划拨</el-tag>
          <el-tag v-else-if="scope.row.transactionType === '3'" type="warning">退费</el-tag>
          <el-tag v-else-if="scope.row.transactionType === '4'" type="info">消费</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="交易金额" align="center" prop="amount" width="120">
        <template slot-scope="scope">
          <span :class="scope.row.transactionType === '1' || scope.row.transactionType === '3' ? 'text-success' : 'text-danger'">
            {{ scope.row.transactionType === '1' || scope.row.transactionType === '3' ? '+' : '-' }}¥{{ scope.row.amount }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="交易前余额" align="center" prop="balanceBefore" width="120">
        <template slot-scope="scope">
          <span>¥{{ scope.row.balanceBefore }}</span>
        </template>
      </el-table-column>
      <el-table-column label="交易后余额" align="center" prop="balanceAfter" width="120">
        <template slot-scope="scope">
          <span>¥{{ scope.row.balanceAfter }}</span>
        </template>
      </el-table-column>
      <el-table-column label="交易时间" align="center" prop="transactionDate" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.transactionDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人" align="center" prop="createBy" width="100" />
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 统计分析对话框 -->
    <el-dialog title="交易统计分析" :visible.sync="statisticsOpen" width="800px" append-to-body>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header" class="clearfix">
              <span>交易类型统计</span>
            </div>
            <div id="transactionTypeChart" style="height: 300px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header" class="clearfix">
              <span>交易金额统计</span>
            </div>
            <div class="statistics-item">
              <div class="label">总充值金额：</div>
              <div class="value text-success">¥{{ statisticsData.totalRecharge || 0 }}</div>
            </div>
            <div class="statistics-item">
              <div class="label">总划拨金额：</div>
              <div class="value text-danger">¥{{ statisticsData.totalTransfer || 0 }}</div>
            </div>
            <div class="statistics-item">
              <div class="label">总退费金额：</div>
              <div class="value text-warning">¥{{ statisticsData.totalRefund || 0 }}</div>
            </div>
            <div class="statistics-item">
              <div class="label">总消费金额：</div>
              <div class="value text-info">¥{{ statisticsData.totalConsume || 0 }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button @click="statisticsOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTransactionRecord } from "@/api/pension/transactionRecord";
import { addDateRange } from "@/utils/ruoyi";

export default {
  name: "TransactionRecord",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 交易记录表格数据
      transactionList: [],
      // 日期范围
      dateRange: [],
      // 是否显示统计对话框
      statisticsOpen: false,
      // 统计数据
      statisticsData: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        transactionNo: null,
        accountNo: null,
        elderName: null,
        transactionType: null,
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询交易记录列表 */
    getList() {
      this.loading = true;
      listTransactionRecord(addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.transactionList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 统计分析按钮操作 */
    handleStatistics() {
      // 计算统计数据
      this.statisticsData = {
        totalRecharge: 0,
        totalTransfer: 0,
        totalRefund: 0,
        totalConsume: 0
      };

      this.transactionList.forEach(item => {
        const amount = parseFloat(item.transactionAmount) || 0;
        switch(item.transactionType) {
          case '1':
            this.statisticsData.totalRecharge += amount;
            break;
          case '2':
            this.statisticsData.totalTransfer += amount;
            break;
          case '3':
            this.statisticsData.totalRefund += amount;
            break;
          case '4':
            this.statisticsData.totalConsume += amount;
            break;
        }
      });

      // 保留两位小数
      this.statisticsData.totalRecharge = this.statisticsData.totalRecharge.toFixed(2);
      this.statisticsData.totalTransfer = this.statisticsData.totalTransfer.toFixed(2);
      this.statisticsData.totalRefund = this.statisticsData.totalRefund.toFixed(2);
      this.statisticsData.totalConsume = this.statisticsData.totalConsume.toFixed(2);

      this.statisticsOpen = true;

      // 延迟渲染图表
      this.$nextTick(() => {
        this.renderChart();
      });
    },
    /** 渲染图表 */
    renderChart() {
      // 这里可以使用 ECharts 等图表库渲染图表
      // 由于需要引入 ECharts，这里仅做示例
      console.log('渲染交易类型统计图表');
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/transaction/export', {
        ...this.queryParams
      }, `transaction_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style scoped>
.statistics-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}
.statistics-item:last-child {
  border-bottom: none;
}
.statistics-item .label {
  font-size: 14px;
  color: #606266;
}
.statistics-item .value {
  font-size: 18px;
  font-weight: bold;
}
.text-success {
  color: #67C23A;
}
.text-danger {
  color: #F56C6C;
}
.text-warning {
  color: #E6A23C;
}
.text-info {
  color: #909399;
}
</style>
