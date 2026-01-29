<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="划拨单号" prop="transferNo">
        <el-input
          v-model="queryParams.transferNo"
          placeholder="请输入划拨单号"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="划拨类型" prop="transferType">
        <el-select v-model="queryParams.transferType" placeholder="请选择划拨类型" clearable size="small">
          <el-option label="全部" value="" />
          <el-option label="自动划拨" value="1" />
          <el-option label="手动划拨" value="2" />
          <el-option label="特殊申请" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="划拨状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable size="small">
          <el-option label="全部" value="" />
          <el-option label="待处理" value="pending" />
          <el-option label="处理中" value="processing" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>
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
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="transferList" style="width: 100%" border>
      <el-table-column label="划拨单号" align="center" prop="transferNo" width="180" />
      <el-table-column label="老人姓名" align="center" prop="elderName" width="100" />
      <el-table-column label="划拨类型" align="center" prop="transferType" width="100">
        <template slot-scope="scope">
          {{ getTransferTypeText(scope.row.transferType) }}
        </template>
      </el-table-column>
      <el-table-column label="划拨金额" align="center" prop="transferAmount" width="120">
        <template slot-scope="scope">
          <span class="text-danger">¥{{ formatMoney(scope.row.transferAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="账单月份" align="center" prop="billingMonth" width="100" />
      <el-table-column label="划拨状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusTagType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="划拨时间" align="center" prop="paidTime" width="160">
        <template slot-scope="scope">
          {{ scope.row.paidTime ? parseTime(scope.row.paidTime, '{y}-{m}-{d} {h}:{i}:{s}') : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="监管账户余额" align="center" prop="supervisionBalance" width="120">
        <template slot-scope="scope">
          <span>¥{{ formatMoney(scope.row.supervisionBalance) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listFundTransferRecord } from "@/api/pension/transactionRecord";
import { addDateRange } from "@/utils/ruoyi";

export default {
  name: "FundTransferRecord",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 资金划拨记录表格数据
      transferList: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        transferNo: null,
        transferType: null,
        elderName: null,
        status: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询资金划拨记录列表 */
    getList() {
      this.loading = true;
      listFundTransferRecord(addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.transferList = response.rows;
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
    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/bank/transfer-record/export', {
        ...this.queryParams
      }, `transfer_record_${new Date().getTime()}.xlsx`)
    },
    /** 获取划拨类型文本 */
    getTransferTypeText(type) {
      const textMap = {
        '1': '自动划拨',
        '2': '手动划拨',
        '3': '特殊申请'
      };
      return textMap[type] || type;
    },
    /** 获取状态文本 */
    getStatusText(status) {
      const textMap = {
        'pending': '待处理',
        'processing': '处理中',
        'completed': '已完成',
        'cancelled': '已取消'
      };
      return textMap[status] || status;
    },
    /** 获取状态标签颜色 */
    getStatusTagType(status) {
      const typeMap = {
        'pending': 'info',
        'processing': 'warning',
        'completed': 'success',
        'cancelled': 'danger'
      };
      return typeMap[status] || '';
    },
    /** 格式化金额 */
    formatMoney(value) {
      if (!value && value !== 0) return '0.00';
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    }
  }
};
</script>

<style scoped>
.text-danger {
  color: #F56C6C;
  font-weight: bold;
}
</style>
