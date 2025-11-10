<template>
  <div class="app-container">
    <!-- 搜索表单 -->
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
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="划拨类型" prop="transferType">
        <el-select v-model="queryParams.transferType" placeholder="请选择划拨类型" clearable size="small">
          <el-option label="自动划拨" value="1" />
          <el-option label="手动划拨" value="2" />
          <el-option label="特殊申请" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="划拨状态" prop="transferStatus">
        <el-select v-model="queryParams.transferStatus" placeholder="请选择划拨状态" clearable size="small">
          <el-option label="待处理" value="0" />
          <el-option label="成功" value="1" />
          <el-option label="失败" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="划拨期间">
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

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.pendingCount || 0 }}</div>
            <div class="stat-label">待处理</div>
            <div class="stat-icon pending">
              <i class="el-icon-time"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.approvedCount || 0 }}</div>
            <div class="stat-label">成功</div>
            <div class="stat-icon approved">
              <i class="el-icon-check"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.rejectedCount || 0 }}</div>
            <div class="stat-label">失败</div>
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
            <div class="stat-label">总划拨</div>
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
          v-hasPermi="['pension:fundTransfer:approve']"
        >批量审批</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-bell"
          size="mini"
          @click="handleTodayPending"
          v-hasPermi="['pension:fundTransfer:query']"
        >今日待处理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-warning"
          size="mini"
          @click="handleUrgentTransfers"
          v-hasPermi="['pension:fundTransfer:query']"
        >紧急划拨</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pension:fundTransfer:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-switch
          v-model="showAll"
          active-text="显示全部"
          inactive-text="仅待处理"
          @change="handleShowAllChange"
        >
        </el-switch>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="fundTransferList" @selection-change="handleSelectionChange" style="width: 100%" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="划拨单号" align="center" prop="transferNo" width="150" />
      <el-table-column label="机构名称" align="center" prop="institutionName" min-width="150" show-overflow-tooltip />
      <el-table-column label="划拨类型" align="center" prop="transferType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.transfer_type" :value="scope.row.transferType"/>
        </template>
      </el-table-column>
      <el-table-column label="划拨金额" align="center" prop="transferAmount" width="120">
        <template slot-scope="scope">
          <span class="text-primary">¥{{ scope.row.transferAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="涉及老人" align="center" prop="elderCount" width="90" />
      <el-table-column label="划拨期间" align="center" prop="transferPeriod" width="120" />
      <el-table-column label="划拨状态" align="center" prop="transferStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.transfer_status" :value="scope.row.transferStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="划拨日期" align="center" prop="transferDate" width="110">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.transferDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审批人" align="center" prop="approveUser" width="100" />
      <el-table-column label="审批时间" align="center" prop="approveTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.approveTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['pension:fundTransfer:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleApprove(scope.row)"
            v-if="scope.row.transferStatus === '0'"
            v-hasPermi="['pension:fundTransfer:approve']"
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleReject(scope.row)"
            v-if="scope.row.transferStatus === '0'"
            v-hasPermi="['pension:fundTransfer:reject']"
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

    <!-- 划拨详情对话框 -->
    <el-dialog title="划拨详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="划拨单号">{{ currentTransfer.transferNo }}</el-descriptions-item>
        <el-descriptions-item label="划拨类型">
          <dict-tag :options="dict.type.transfer_type" :value="currentTransfer.transferType"/>
        </el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ currentTransfer.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="划拨金额">
          <span class="text-primary">¥{{ currentTransfer.transferAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="涉及老人">{{ currentTransfer.elderCount }}人</el-descriptions-item>
        <el-descriptions-item label="划拨期间">{{ currentTransfer.transferPeriod }}</el-descriptions-item>
        <el-descriptions-item label="划拨状态">
          <dict-tag :options="dict.type.transfer_status" :value="currentTransfer.transferStatus"/>
        </el-descriptions-item>
        <el-descriptions-item label="划拨日期">{{ parseTime(currentTransfer.transferDate) }}</el-descriptions-item>
        <el-descriptions-item label="银行订单号" :span="2">{{ currentTransfer.bankOrderNo }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(currentTransfer.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ currentTransfer.approveUser }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ parseTime(currentTransfer.approveTime) }}</el-descriptions-item>
        <el-descriptions-item label="失败原因" v-if="currentTransfer.transferStatus === '2'">
          {{ currentTransfer.failureReason }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentTransfer.remark }}</el-descriptions-item>
      </el-descriptions>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 审批拒绝对话框 -->
    <el-dialog title="审批拒绝" :visible.sync="rejectOpen" width="500px" append-to-body>
      <el-form ref="rejectForm" :model="rejectForm" :rules="rejectRules" label-width="80px">
        <el-form-item label="拒绝原因" prop="remark">
          <el-input v-model="rejectForm.remark" type="textarea" :rows="4" placeholder="请输入拒绝原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReject">确 定</el-button>
        <el-button @click="cancelReject">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 今日待处理对话框 -->
    <el-dialog title="今日待处理划拨" :visible.sync="todayPendingOpen" width="1000px" append-to-body>
      <el-table :data="todayPendingList" border>
        <el-table-column label="划拨单号" prop="transferNo" width="150" />
        <el-table-column label="机构名称" prop="institutionName" min-width="150" />
        <el-table-column label="划拨金额" prop="transferAmount" width="120">
          <template slot-scope="scope">
            <span class="text-primary">¥{{ scope.row.transferAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="划拨类型" prop="transferType" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.transfer_type" :value="scope.row.transferType"/>
          </template>
        </el-table-column>
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
              v-hasPermi="['pension:fundTransfer:approve']"
            >审批</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 紧急划拨对话框 -->
    <el-dialog title="紧急划拨（金额>5万）" :visible.sync="urgentOpen" width="1000px" append-to-body>
      <el-table :data="urgentList" border>
        <el-table-column label="划拨单号" prop="transferNo" width="150" />
        <el-table-column label="机构名称" prop="institutionName" min-width="150" />
        <el-table-column label="划拨金额" prop="transferAmount" width="120">
          <template slot-scope="scope">
            <span class="text-danger">¥{{ scope.row.transferAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="划拨类型" prop="transferType" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.transfer_type" :value="scope.row.transferType"/>
          </template>
        </el-table-column>
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
              v-hasPermi="['pension:fundTransfer:approve']"
            >审批</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listApproval, listAllFundTransfer, getFundTransfer, approveFundTransfer, rejectFundTransfer, getApprovalStatistics, batchApprove, getTodayPending, getUrgentTransfers, exportApproval } from "@/api/pension/supervisionFundTransfer";

export default {
  name: "FundTransferApproval",
  dicts: ['transfer_type', 'transfer_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 资金划拨表格数据
      fundTransferList: [],
      // 统计数据
      statistics: {},
      // 日期范围
      dateRange: [],
      // 显示全部数据
      showAll: false,
      // 弹出层标题
      title: "",
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示拒绝弹出层
      rejectOpen: false,
      // 是否显示今日待处理弹出层
      todayPendingOpen: false,
      // 是否显示紧急划拨弹出层
      urgentOpen: false,
      // 当前划拨信息
      currentTransfer: {},
      // 今日待处理列表
      todayPendingList: [],
      // 紧急划拨列表
      urgentList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        transferNo: null,
        institutionName: null,
        transferType: null,
        transferStatus: "0" // 默认只查询待处理
      },
      // 拒绝表单
      rejectForm: {
        transferId: null,
        remark: ''
      },
      // 拒绝表单校验
      rejectRules: {
        remark: [
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
    /** 查询资金划拨审批列表 */
    getList() {
      this.loading = true;
      this.addDateRange();

      // 根据是否显示全部选择不同的API
      const apiMethod = this.showAll ? listAllFundTransfer : listApproval;

      apiMethod(this.queryParams).then(response => {
        this.fundTransferList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询统计数据 */
    getStatistics() {
      getApprovalStatistics().then(response => {
        this.statistics = response.data;
      });
    },
    // 添加日期范围
    addDateRange() {
      if (this.dateRange && this.dateRange.length === 2) {
        this.queryParams.beginTime = this.dateRange[0];
        this.queryParams.endTime = this.dateRange[1];
      }
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.transferId);
      this.multiple = !selection.length;
    },
    /** 查看详情操作 */
    handleDetail(row) {
      const transferId = row.transferId;
      getFundTransfer(transferId).then(response => {
        this.currentTransfer = response.data;
        this.detailOpen = true;
      });
    },
    /** 审批通过操作 */
    handleApprove(row) {
      const transferId = row.transferId;
      const transferNo = row.transferNo;

      this.$modal.confirm('是否确认审批通过划拨单号为"' + transferNo + '"的申请？').then(() => {
        return approveFundTransfer(transferId);
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("审批通过成功");
      }).catch(() => {});
    },
    /** 审批拒绝操作 */
    handleReject(row) {
      this.rejectForm = {
        transferId: row.transferId,
        remark: ''
      };
      this.rejectOpen = true;
    },
    /** 提交拒绝 */
    submitReject() {
      this.$refs["rejectForm"].validate(valid => {
        if (valid) {
          rejectFundTransfer(this.rejectForm.transferId, this.rejectForm).then(response => {
            this.getList();
            this.getStatistics();
            this.rejectOpen = false;
            this.$modal.msgSuccess("审批拒绝成功");
          });
        }
      });
    },
    /** 取消拒绝 */
    cancelReject() {
      this.rejectOpen = false;
      this.resetForm("rejectForm");
    },
    /** 批量审批操作 */
    handleBatchApprove() {
      const transferIds = this.ids;
      const transferNos = this.fundTransferList.filter(item => transferIds.includes(item.transferId))
        .map(item => item.transferNo).join('、');

      this.$modal.confirm('是否确认批量审批通过以下划拨单："' + transferNos + '"？').then(() => {
        return batchApprove(transferIds);
      }).then(response => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess(response.msg);
      }).catch(() => {});
    },
    /** 显示全部切换 */
    handleShowAllChange(value) {
      if (value) {
        this.queryParams.transferStatus = null; // 显示全部状态
      } else {
        this.queryParams.transferStatus = "0"; // 只显示待处理
      }
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 今日待处理 */
    handleTodayPending() {
      getTodayPending().then(response => {
        this.todayPendingList = response.data;
        this.todayPendingOpen = true;
      });
    },
    /** 紧急划拨 */
    handleUrgentTransfers() {
      getUrgentTransfers().then(response => {
        this.urgentList = response.data;
        this.urgentOpen = true;
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/supervision/fundTransfer/approval/export', {
        ...this.queryParams
      }, `资金划拨审批_${new Date().getTime()}.xlsx`)
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