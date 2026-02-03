<template>
  <div class="app-container data-statistics">
    <!-- 筛选条件 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="区县" prop="districtCode">
          <el-select v-model="queryParams.districtCode" placeholder="请选择区县" clearable>
            <el-option
              v-for="item in districtOptions"
              :key="(item.areaCode || item.area_code) + '_' + (item.areaName || item.area_name)"
              :label="item.areaName || item.area_name"
              :value="item.areaCode || item.area_code"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-download" @click="handleExport">导出CSV</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="dataList" border stripe>
      <el-table-column label="序号" type="index" width="60" align="center" />
      <el-table-column label="区县名称" prop="district_name" min-width="120" align="center" />
      <el-table-column label="总收入(元)" prop="total_income" width="140" align="center">
        <template slot-scope="scope">
          <span class="amount-text income">{{ formatAmount(scope.row.total_income) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总支出(元)" prop="total_outcome" width="140" align="center">
        <template slot-scope="scope">
          <span class="amount-text outcome">{{ formatAmount(scope.row.total_outcome) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="资金流量(元)" prop="total_flow" width="140" align="center">
        <template slot-scope="scope">
          <span class="amount-text">{{ formatAmount(scope.row.total_flow) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="交易数量" prop="transaction_count" width="100" align="center">
        <template slot-scope="scope">
          <el-tag type="primary">{{ scope.row.transaction_count || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="微信支付" prop="wechat_count" width="100" align="center">
        <template slot-scope="scope">
          <el-tag type="success">{{ scope.row.wechat_count || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="支付宝支付" prop="alipay_count" width="110" align="center">
        <template slot-scope="scope">
          <el-tag type="primary">{{ scope.row.alipay_count || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="银行支付" prop="bankCount" width="100" align="center">
        <template slot-scope="scope">
          <el-tag>{{ scope.row.bankCount || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="现金支付" prop="cash_count" width="100" align="center">
        <template slot-scope="scope">
          <el-tag type="info">{{ scope.row.cash_count || 0 }}</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listFund, exportFund } from "@/api/pension/dataStatistics";
import { listAreas } from "@/api/pension/areaStreet";

export default {
  name: "FundStatistics",
  data() {
    return {
      loading: false,
      dataList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        districtCode: null
      },
      districtOptions: []
    };
  },
  created() {
    this.getDistrictOptions();
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listFund(this.queryParams).then(response => {
        this.dataList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    getDistrictOptions() {
      listAreas().then(response => {
        this.districtOptions = response.data || [];
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleExport() {
      this.$confirm("是否确认导出资金情况数据?", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.downloadMessage = this.$message.info("正在导出，请稍候...");
        return exportFund(this.queryParams);
      }).then(response => {
        this.downloadMessage.close();
        const blob = new Blob([response], { type: "text/csv;charset=utf-8" });
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = `资金情况_${new Date().getTime()}.csv`;
        link.click();
        this.$message.success("导出成功");
      }).catch(() => {
        this.downloadMessage && this.downloadMessage.close();
      });
    },
    formatAmount(value) {
      if (value === null || value === undefined || value === "") return "0.00";
      return parseFloat(value).toFixed(2);
    }
  }
};
</script>

<style scoped lang="scss">
.data-statistics {
  .search-card {
    margin-bottom: 16px;
  }

  .amount-text {
    font-family: "Arial", monospace;
    font-weight: 500;

    &.income {
      color: #67c23a;
    }

    &.outcome {
      color: #f56c6c;
    }
  }
}
</style>
