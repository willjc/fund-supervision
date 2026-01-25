<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="支付流水号" prop="paymentNo">
        <el-input
          v-model="queryParams.paymentNo"
          placeholder="请输入支付流水号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付方式" prop="paymentMethod">
        <el-select v-model="queryParams.paymentMethod" placeholder="请选择支付方式" clearable>
          <el-option
            v-for="dict in dict.type.payment_method"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="支付状态" prop="paymentStatus">
        <el-select v-model="queryParams.paymentStatus" placeholder="请选择支付状态" clearable>
          <el-option
            v-for="dict in dict.type.payment_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="老人姓名" prop="elderName">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入老人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="支付时间">
        <el-date-picker
          v-model="dateRange"
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
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['payment:record:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-s-data"
          size="mini"
          @click="handleStatistics"
          v-hasPermi="['payment:record:statistics']"
        >统计</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="paymentList" style="width: 100%">
      <el-table-column label="支付流水号" align="center" prop="paymentNo" min-width="180"/>
      <el-table-column label="订单号" align="center" prop="orderNo" min-width="180"/>
      <el-table-column label="老人姓名" align="center" prop="elderName" min-width="100"/>
      <el-table-column label="支付金额" align="center" prop="paymentAmount" min-width="120">
        <template slot-scope="scope">
          <span style="color: #67C23A; font-weight: bold">¥{{ scope.row.paymentAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="支付方式" align="center" prop="paymentMethod" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.payment_method" :value="scope.row.paymentMethod"/>
        </template>
      </el-table-column>
      <el-table-column label="支付状态" align="center" prop="paymentStatus" min-width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.payment_status" :value="scope.row.paymentStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="第三方交易号" align="center" prop="transactionId" min-width="180" show-overflow-tooltip/>
      <el-table-column label="支付时间" align="center" prop="paymentTime" min-width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.paymentTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="100">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 支付记录详情对话框 -->
    <el-dialog title="支付记录详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="支付流水号">{{ detailForm.paymentNo }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ detailForm.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ detailForm.elderName }}</el-descriptions-item>
        <el-descriptions-item label="养老机构">{{ detailForm.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="支付金额">
          <span style="color: #67C23A; font-weight: bold">¥{{ detailForm.paymentAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付方式">
          <dict-tag :options="dict.type.payment_method" :value="detailForm.paymentMethod"/>
        </el-descriptions-item>
        <el-descriptions-item label="支付状态">
          <dict-tag :options="dict.type.payment_status" :value="detailForm.paymentStatus"/>
        </el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ parseTime(detailForm.paymentTime) }}</el-descriptions-item>
        <el-descriptions-item label="第三方交易号" :span="2">{{ detailForm.transactionId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="网关响应" :span="2">{{ detailForm.gatewayResponse || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付凭证" :span="2" v-if="detailForm.paymentProof">
          <el-image
            :src="detailForm.paymentProof"
            :preview-src-list="[detailForm.paymentProof]"
            fit="cover"
            style="width: 100px; height: 100px;"
          />
        </el-descriptions-item>
        <el-descriptions-item label="凭证备注" :span="2" v-if="detailForm.paymentProofRemark">{{ detailForm.paymentProofRemark }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailForm.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailForm.operator || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailForm.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 支付统计对话框 -->
    <payment-statistics ref="paymentStatistics" />

  </div>
</template>

<script>
import { listPayment, getPayment, exportPayment, getPaymentStatistics } from "@/api/order/paymentRecord";
import PaymentStatistics from './components/PaymentStatistics'

export default {
  name: "PaymentRecord",
  dicts: ['payment_method', 'payment_status'],
  components: {
    PaymentStatistics
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 支付记录表格数据
      paymentList: [],
      // 日期范围
      dateRange: [],
      // 是否显示详情对话框
      detailOpen: false,
      // 详情数据
      detailForm: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        paymentNo: null,
        orderNo: null,
        paymentMethod: null,
        paymentStatus: null,
        elderName: null,
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询支付记录列表 */
    getList() {
      this.loading = true;
      this.addDateRange(this.queryParams, this.dateRange);
      listPayment(this.queryParams).then(response => {
        this.paymentList = response.rows;
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
      this.download('payment/record/export', {
        ...this.queryParams
      }, `payment_${new Date().getTime()}.xlsx`)
    },
    /** 查看详情 */
    handleDetail(row) {
      getPayment(row.paymentId).then(response => {
        this.detailForm = response.data;
        this.detailOpen = true;
      });
    },
    /** 支付统计 */
    handleStatistics() {
      this.$refs.paymentStatistics.show(this.queryParams);
    }
  }
};
</script>
