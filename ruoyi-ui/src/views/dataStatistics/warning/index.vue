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
      <el-table-column label="最低余额预警" prop="low_balance_count" width="140" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.low_balance_count > 0" type="danger">{{ scope.row.low_balance_count || 0 }}</el-tag>
          <span v-else>{{ scope.row.low_balance_count || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="大额动账预警" prop="large_transaction_count" width="140" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.large_transaction_count > 0" type="warning">{{ scope.row.large_transaction_count || 0 }}</el-tag>
          <span v-else>{{ scope.row.large_transaction_count || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="余额不足预警" prop="insufficient_balance_count" width="150" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.insufficient_balance_count > 0" type="danger">{{ scope.row.insufficient_balance_count || 0 }}</el-tag>
          <span v-else>{{ scope.row.insufficient_balance_count || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总预警数" prop="total_warning_count" width="110" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.total_warning_count > 0" type="danger">{{ scope.row.total_warning_count || 0 }}</el-tag>
          <span v-else>{{ scope.row.total_warning_count || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="待处理数" prop="pending_count" width="110" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.pending_count > 0" type="warning">{{ scope.row.pending_count || 0 }}</el-tag>
          <span v-else>{{ scope.row.pending_count || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="高额退款数" prop="high_refund_count" width="120" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.high_refund_count > 0" type="warning">{{ scope.row.high_refund_count || 0 }}</el-tag>
          <span v-else>{{ scope.row.high_refund_count || 0 }}</span>
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
import { listWarning, exportWarning } from "@/api/pension/dataStatistics";
import { listAreas } from "@/api/pension/areaStreet";

export default {
  name: "WarningStatistics",
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
      listWarning(this.queryParams).then(response => {
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
      this.$confirm("是否确认导出预警情况数据?", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.downloadMessage = this.$message.info("正在导出，请稍候...");
        return exportWarning(this.queryParams);
      }).then(response => {
        this.downloadMessage.close();
        const blob = new Blob([response], { type: "text/csv;charset=utf-8" });
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = `预警情况_${new Date().getTime()}.csv`;
        link.click();
        this.$message.success("导出成功");
      }).catch(() => {
        this.downloadMessage && this.downloadMessage.close();
      });
    }
  }
};
</script>

<style scoped lang="scss">
.data-statistics {
  .search-card {
    margin-bottom: 16px;
  }
}
</style>
