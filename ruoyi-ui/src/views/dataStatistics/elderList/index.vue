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
        <el-form-item label="老人姓名" prop="elder_name">
          <el-input
            v-model="queryParams.elderName"
            placeholder="请输入老人姓名"
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
      <el-table-column label="老人姓名" prop="elder_name" width="100" align="center" />
      <el-table-column label="性别" prop="gender" width="60" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.gender === "0" ? "女" : "男" }}</span>
        </template>
      </el-table-column>
      <el-table-column label="年龄" prop="age" width="70" align="center" />
      <el-table-column label="身份证号" prop="id_card" width="180" align="center" />
      <el-table-column label="联系电话" prop="phone" width="120" align="center" />
      <el-table-column label="健康状况" prop="health_status" width="100" align="center" />
      <el-table-column label="护理等级" prop="care_level" width="100" align="center" />
      <el-table-column label="所属机构" prop="institution_name" min-width="180" show-overflow-tooltip />
      <el-table-column label="区县" prop="district_name" width="100" align="center" />
      <el-table-column label="入住日期" prop="actual_check_in_date" width="110" align="center">
        <template slot-scope="scope">
          <span>{{ formatDate(scope.row.actual_check_in_date) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="入住状态" prop="check_in_status" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.check_in_status === '0'" type="info">待入住</el-tag>
          <el-tag v-else-if="scope.row.check_in_status === '1'" type="success">已入住</el-tag>
          <el-tag v-else-if="scope.row.check_in_status === '2'" type="danger">已退住</el-tag>
          <el-tag v-else-if="scope.row.check_in_status === '3'" type="warning">请假中</el-tag>
          <el-tag v-else type="info">{{ scope.row.check_in_status || "未知" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="elder_status" width="80" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.elder_status === '0'" type="success">在住</el-tag>
          <el-tag v-else-if="scope.row.elder_status === '2'" type="info">离院</el-tag>
          <el-tag v-else type="warning">{{ scope.row.elder_status || "未知" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="总余额(元)" prop="total_balance" width="120" align="center">
        <template slot-scope="scope">
          <span class="amount-text">{{ formatAmount(scope.row.total_balance) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="服务费(元)" prop="service_balance" width="110" align="center">
        <template slot-scope="scope">
          <span class="amount-text service">{{ formatAmount(scope.row.service_balance) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="押金(元)" prop="deposit_balance" width="110" align="center">
        <template slot-scope="scope">
          <span class="amount-text deposit">{{ formatAmount(scope.row.deposit_balance) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="会员费(元)" prop="member_balance" width="110" align="center">
        <template slot-scope="scope">
          <span class="amount-text member">{{ formatAmount(scope.row.member_balance) }}</span>
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
import { listElderDetail, exportElderList } from "@/api/pension/dataStatistics";
import { listAreas } from "@/api/pension/areaStreet";

export default {
  name: "ElderListStatistics",
  data() {
    return {
      loading: false,
      dataList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        districtCode: null,
        elderName: null
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
      listElderDetail(this.queryParams).then(response => {
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
      this.$confirm("是否确认导出老人列表数据?", "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.downloadMessage = this.$message.info("正在导出，请稍候...");
        return exportElderList(this.queryParams);
      }).then(response => {
        this.downloadMessage.close();
        const blob = new Blob([response], { type: "text/csv;charset=utf-8" });
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = `老人列表_${new Date().getTime()}.csv`;
        link.click();
        this.$message.success("导出成功");
      }).catch(() => {
        this.downloadMessage && this.downloadMessage.close();
      });
    },
    formatAmount(value) {
      if (value === null || value === undefined || value === "") return "0.00";
      return parseFloat(value).toFixed(2);
    },
    formatDate(value) {
      if (!value) return "-";
      return this.parseTime(value, "{y}-{m}-{d}");
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

    &.service {
      color: #409eff;
    }

    &.deposit {
      color: #e6a23c;
    }

    &.member {
      color: #67c23a;
    }
  }
}
</style>
