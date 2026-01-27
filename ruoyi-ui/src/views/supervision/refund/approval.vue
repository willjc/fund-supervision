<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="退款单号" prop="refundNo">
        <el-input
          v-model="queryParams.refundNo"
          placeholder="请输入退款单号"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="退款状态" prop="refundStatus">
        <el-select v-model="queryParams.refundStatus" placeholder="请选择退款状态" clearable size="small">
          <el-option label="待处理" value="0" />
          <el-option label="已成功" value="1" />
          <el-option label="已失败" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.pendingCount || 0 }}</div>
            <div class="stat-label">待审批</div>
            <div class="stat-icon pending">
              <i class="el-icon-time"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.successCount || 0 }}</div>
            <div class="stat-label">已成功</div>
            <div class="stat-icon approved">
              <i class="el-icon-check"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.failedCount || 0 }}</div>
            <div class="stat-label">已失败</div>
            <div class="stat-icon rejected">
              <i class="el-icon-close"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.totalCount || 0 }}</div>
            <div class="stat-label">总退款</div>
            <div class="stat-icon total">
              <i class="el-icon-s-finance"></i>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="multiple"
          @click="handleBatchApprove"
          v-hasPermi="['pension:refund:approve']"
        >批量审批</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-money"
          size="mini"
          @click="handleLargeAmount"
          v-hasPermi="['pension:refund:query']"
        >大额退款</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-switch
          v-model="showAll"
          active-text="显示全部"
          inactive-text="仅待审批"
          @change="handleShowAllChange"
        >
        </el-switch>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="refundList" @selection-change="handleSelectionChange" style="width: 100%" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="退款单号" align="center" prop="refundNo" width="150" />
      <el-table-column label="老人姓名" align="center" prop="elderName" width="100" />
      <el-table-column label="机构名称" align="center" prop="institutionName" min-width="150" show-overflow-tooltip />
      <el-table-column label="退款金额" align="center" prop="refundAmount" width="120">
        <template slot-scope="scope">
          <span class="text-primary">¥{{ scope.row.refundAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="退款原因" align="center" prop="refundReason" min-width="150" show-overflow-tooltip />
      <el-table-column label="退款状态" align="center" prop="refundStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.refundStatus === '0'" type="warning">待处理</el-tag>
          <el-tag v-else-if="scope.row.refundStatus === '1'" type="success">已成功</el-tag>
          <el-tag v-else-if="scope.row.refundStatus === '2'" type="danger">已失败</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审批人" align="center" prop="approver" width="100" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['pension:refund:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleApprove(scope.row)"
            v-if="scope.row.refundStatus === '0'"
            v-hasPermi="['pension:refund:approve']"
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleReject(scope.row)"
            v-if="scope.row.refundStatus === '0'"
            v-hasPermi="['pension:refund:reject']"
          >拒绝</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 退款详情对话框 -->
    <el-dialog title="退款详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="退款单号">{{ currentRefund.refundNo }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">
          <span class="text-primary">¥{{ currentRefund.refundAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ currentRefund.elderName }}</el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ currentRefund.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="订单编号">{{ currentRefund.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="支付流水号">{{ currentRefund.paymentNo }}</el-descriptions-item>
        <el-descriptions-item label="退款状态">
          <el-tag v-if="currentRefund.refundStatus === '0'" type="warning">待处理</el-tag>
          <el-tag v-else-if="currentRefund.refundStatus === '1'" type="success">已成功</el-tag>
          <el-tag v-else-if="currentRefund.refundStatus === '2'" type="danger">已失败</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="退款方式">{{ currentRefund.refundMethod }}</el-descriptions-item>
        <el-descriptions-item label="退款原因" :span="2">{{ currentRefund.refundReason }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(currentRefund.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ currentRefund.approver }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ parseTime(currentRefund.approveTime) }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" v-if="currentRefund.approveRemark">
          {{ currentRefund.approveRemark }}
        </el-descriptions-item>
      </el-descriptions>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 审批拒绝对话框 -->
    <el-dialog title="审批拒绝" :visible.sync="rejectOpen" width="500px" append-to-body>
      <el-form ref="rejectForm" :model="rejectForm" :rules="rejectRules" label-width="80px">
        <el-form-item label="拒绝原因" prop="approveRemark">
          <el-input v-model="rejectForm.approveRemark" type="textarea" :rows="4" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReject">确 定</el-button>
        <el-button @click="cancelReject">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 大额退款对话框 -->
    <el-dialog title="大额退款（>10000元）" :visible.sync="largeAmountOpen" width="1200px" append-to-body>
      <el-table :data="largeAmountList" border>
        <el-table-column label="退款单号" prop="refundNo" width="150" />
        <el-table-column label="老人姓名" prop="elderName" width="100" />
        <el-table-column label="机构名称" prop="institutionName" min-width="150" />
        <el-table-column label="退款金额" prop="refundAmount" width="120">
          <template slot-scope="scope">
            <span class="text-danger">¥{{ scope.row.refundAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="退款原因" prop="refundReason" min-width="150" show-overflow-tooltip />
        <el-table-column label="创建时间" prop="createTime" width="160">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-check"
              @click="handleApprove(scope.row)"
              v-hasPermi="['pension:refund:approve']"
            >审批</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listApproval, listAllRefund, getRefund, approveRefund, rejectRefund, getApprovalStatistics, batchApprove, getLargeAmountRefunds } from "@/api/pension/supervisionRefund";

export default {
  name: "RefundApproval",
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      refundList: [],
      statistics: {},
      showAll: false,
      detailOpen: false,
      rejectOpen: false,
      largeAmountOpen: false,
      currentRefund: {},
      largeAmountList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        refundNo: null,
        refundStatus: "0"
      },
      rejectForm: {
        refundId: null,
        approveRemark: ''
      },
      rejectRules: {
        approveRemark: [
          { required: true, message: "拒绝原因不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getStatistics();
  },
  methods: {
    getList() {
      this.loading = true;
      const apiMethod = this.showAll ? listAllRefund : listApproval;
      apiMethod(this.queryParams).then(response => {
        this.refundList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    getStatistics() {
      getApprovalStatistics().then(response => {
        this.statistics = response.data;
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
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.refundId);
      this.multiple = !selection.length;
    },
    handleDetail(row) {
      getRefund(row.refundId).then(response => {
        this.currentRefund = response.data;
        this.detailOpen = true;
      });
    },
    handleApprove(row) {
      this.$modal.confirm('是否确认审批通过退款单号为"' + row.refundNo + '"的退款？').then(() => {
        return approveRefund(row.refundId);
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("审批通过成功");
        this.largeAmountOpen = false;
      }).catch(() => {});
    },
    handleReject(row) {
      this.rejectForm = {
        refundId: row.refundId,
        approveRemark: ''
      };
      this.rejectOpen = true;
    },
    submitReject() {
      this.$refs["rejectForm"].validate(valid => {
        if (valid) {
          rejectRefund(this.rejectForm.refundId, this.rejectForm).then(response => {
            this.getList();
            this.getStatistics();
            this.rejectOpen = false;
            this.$modal.msgSuccess("审批拒绝成功");
          });
        }
      });
    },
    cancelReject() {
      this.rejectOpen = false;
      this.resetForm("rejectForm");
    },
    handleBatchApprove() {
      const refundIds = this.ids;
      this.$modal.confirm('是否确认批量审批通过选中的退款？').then(() => {
        return batchApprove(refundIds);
      }).then(response => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess(response.msg);
      }).catch(() => {});
    },
    handleShowAllChange(value) {
      if (value) {
        this.queryParams.refundStatus = null;
      } else {
        this.queryParams.refundStatus = "0";
      }
      this.queryParams.pageNum = 1;
      this.getList();
    },
    handleLargeAmount() {
      getLargeAmountRefunds().then(response => {
        this.largeAmountList = response.data;
        this.largeAmountOpen = true;
      });
    }
  }
};
</script>

<style scoped>
.stat-card {
  height: 100px;
}

.stat-content {
  position: relative;
  height: 100%;
  padding: 20px;
  display: flex;
  align-items: center;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-left: 15px;
}

.stat-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #FF6B6B, #FF8E8E);
}

.stat-icon.approved {
  background: linear-gradient(135deg, #4CAF50, #66BB6A);
}

.stat-icon.rejected {
  background: linear-gradient(135deg, #FFA726, #FFB74D);
}

.stat-icon.total {
  background: linear-gradient(135deg, #42A5F5, #66B3FF);
}

.text-primary {
  color: #409EFF;
  font-weight: bold;
}

.text-danger {
  color: #F56C6C;
  font-weight: bold;
}
</style>
