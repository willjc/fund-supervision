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
        <el-form-item label="机构名称" prop="institution_name">
          <el-input
            v-model="queryParams.institutionName"
            placeholder="请输入机构名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
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
      <el-table-column label="机构名称" prop="institution_name" min-width="200" show-overflow-tooltip />
      <el-table-column label="区县" prop="district_name" width="120" align="center" />
      <el-table-column label="监管银行" prop="supervise_bank" min-width="150" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.supervise_bank || "-" }}</span>
        </template>
      </el-table-column>
      <el-table-column label="基本银行" prop="basic_bank" min-width="150" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ scope.row.basic_bank || "-" }}</span>
        </template>
      </el-table-column>
      <el-table-column label="联系人" prop="contact_person" width="100" align="center" />
      <el-table-column label="联系电话" prop="contact_phone" width="120" align="center" />
      <el-table-column label="床位数" prop="bed_count" width="90" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.bed_count || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="监管金额(元)" prop="total_supervision_fund" width="140" align="center">
        <template slot-scope="scope">
          <span class="amount-text">{{ formatAmount(scope.row.total_supervision_fund) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="老人数量" prop="elderCount" width="100" align="center">
        <template slot-scope="scope">
          <el-tag type="primary">{{ scope.row.elderCount || 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" width="90" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '1'" type="success">已入驻</el-tag>
          <el-tag v-else-if="scope.row.status === '0'" type="warning">待审批</el-tag>
          <el-tag v-else-if="scope.row.status === '3'" type="danger">已驳回</el-tag>
          <el-tag v-else type="info">{{ scope.row.status || "未知" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="黑名单" prop="blacklistFlag" width="90" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.blacklistFlag === '1'" type="danger">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
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
import { listInstitutionDetail, exportInstitutionList } from "@/api/pension/dataStatistics";
import { listAreas } from "@/api/pension/areaStreet";

export default {
  name: "InstitutionListStatistics",
  data() {
    return {
      loading: false,
      dataList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        districtCode: null,
        institutionName: null
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
      listInstitutionDetail(this.queryParams).then(response => {
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
      this.$confirm("是否确认导出机构列表数据?", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.downloadMessage = this.$message.info("正在导出，请稍候...");
        return exportInstitutionList(this.queryParams);
      }).then(response => {
        this.downloadMessage.close();
        const blob = new Blob([response], { type: "text/csv;charset=utf-8" });
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = `机构列表_${new Date().getTime()}.csv`;
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
  }
}
</style>
