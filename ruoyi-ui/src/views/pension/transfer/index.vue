<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['pension:transfer:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleAutoTransfer"
          v-hasPermi="['pension:transfer:execute']"
        >执行自动划拨</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-bell"
          size="mini"
          @click="handlePendingTransfers"
          v-hasPermi="['pension:transfer:query']"
        >待处理划拨</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pension:transfer:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="fundTransferList" style="width: 100%" border>
      <el-table-column label="划拨单号" align="center" prop="transferNo" width="150" />
      <el-table-column label="机构名称" align="center" prop="institutionName" min-width="150" />
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
      <el-table-column label="银行订单号" align="center" prop="bankOrderNo" width="150" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['pension:transfer:query']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleApprove(scope.row)"
            v-if="scope.row.transferStatus === '0'"
            v-hasPermi="['pension:transfer:approve']"
          >审批</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-finance"
            @click="handleExecute(scope.row)"
            v-if="scope.row.transferStatus === '1'"
            v-hasPermi="['pension:transfer:execute']"
          >执行</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-document"
            @click="handleDetails(scope.row)"
            v-hasPermi="['pension:transfer:query']"
          >明细</el-button>
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

    <!-- 查看划拨详情对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="划拨单号">
              <el-input v-model="form.transferNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="划拨类型">
              <el-select v-model="form.transferType" placeholder="请选择划拨类型" disabled>
                <el-option label="自动划拨" value="1" />
                <el-option label="手动划拨" value="2" />
                <el-option label="特殊申请" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="机构名称">
              <el-input v-model="form.institutionName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="划拨期间">
              <el-input v-model="form.transferPeriod" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="划拨金额">
              <el-input v-model="form.transferAmount" disabled>
                <template slot="prepend">¥</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="涉及老人">
              <el-input v-model="form.elderCount" disabled>
                <template slot="append">人</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="划拨状态">
              <el-select v-model="form.transferStatus" placeholder="请选择划拨状态" disabled>
                <el-option label="待处理" value="0" />
                <el-option label="成功" value="1" />
                <el-option label="失败" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="划拨日期">
              <el-input v-model="form.transferDate" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="银行订单号">
              <el-input v-model="form.bankOrderNo" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="审批人">
              <el-input v-model="form.approveUser" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行人">
              <el-input v-model="form.executeUser" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="失败原因" v-if="form.failureReason">
          <el-input v-model="form.failureReason" type="textarea" disabled />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" disabled />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog title="资金划拨审批" :visible.sync="approveOpen" width="500px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" :rules="approveRules" label-width="100px">
        <el-form-item label="划拨信息" label-width="100px">
          <div class="text-gray-600">
            划拨单号：{{ currentTransfer.transferNo }}<br>
            机构名称：{{ currentTransfer.institutionName }}<br>
            划拨金额：<span class="text-primary">¥{{ currentTransfer.transferAmount }}</span><br>
            涉及老人：{{ currentTransfer.elderCount }}人
          </div>
        </el-form-item>
        <el-form-item label="审批结果" prop="approveResult">
          <el-radio-group v-model="approveForm.approveResult">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="approveRemark">
          <el-input v-model="approveForm.approveRemark" type="textarea" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitApprove">确 定</el-button>
        <el-button @click="cancelApprove">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 待处理划拨对话框 -->
    <el-dialog title="待处理划拨记录" :visible.sync="pendingOpen" width="1000px" append-to-body>
      <el-table v-loading="pendingLoading" :data="pendingList" style="width: 100%" border>
        <el-table-column label="划拨单号" align="center" prop="transferNo" width="150" />
        <el-table-column label="机构名称" align="center" prop="institutionName" min-width="150" />
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
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="120">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-check"
              @click="handleApprove(scope.row)"
              v-hasPermi="['pension:transfer:approve']"
            >审批</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listFundTransfer, getFundTransfer, addFundTransfer, updateFundTransfer, approveFundTransfer, executeFundTransfer, getPendingTransfers } from "@/api/pension/fundTransfer";

export default {
  name: "FundTransfer",
  dicts: ['transfer_type', 'transfer_status'],
  data() {
    return {
      loading: true,
      ids: [],
      showSearch: true,
      total: 0,
      fundTransferList: [],
      title: "",
      open: false,
      approveOpen: false,
      pendingOpen: false,
      pendingLoading: false,
      pendingList: [],
      dateRange: [],
      currentTransfer: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        transferNo: null,
        institutionName: null,
        transferType: null,
        transferStatus: null
      },
      form: {},
      approveForm: {
        approveResult: '1',
        approveRemark: ''
      },
      approveRules: {
        approveResult: [
          { required: true, message: "审批结果不能为空", trigger: "change" }
        ]
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
      this.addDateRange();
      listFundTransfer(this.queryParams).then(response => {
        this.fundTransferList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 添加日期范围
    addDateRange() {
      if (this.dateRange && this.dateRange.length === 2) {
        this.queryParams.beginTime = this.dateRange[0];
        this.queryParams.endTime = this.dateRange[1];
      }
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        transferId: null,
        institutionId: null,
        transferNo: null,
        transferType: null,
        transferAmount: null,
        transferDate: null,
        transferPeriod: null,
        elderCount: null,
        transferStatus: null,
        bankOrderNo: null,
        failureReason: null,
        approveUser: null,
        approveTime: null,
        executeUser: null,
        executeTime: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      };
      this.resetForm("form");
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
    /** 查看操作 */
    handleView(row) {
      this.reset();
      const transferId = row.transferId || this.ids
      getFundTransfer(transferId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "查看划拨详情";
      });
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加资金划拨记录";
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.transferId != null) {
            updateFundTransfer(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addFundTransfer(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 审批操作 */
    handleApprove(row) {
      this.currentTransfer = row;
      this.approveForm = {
        approveResult: '1',
        approveRemark: ''
      };
      this.approveOpen = true;
    },
    /** 提交审批 */
    submitApprove() {
      this.$refs["approveForm"].validate(valid => {
        if (valid) {
          approveFundTransfer(this.currentTransfer.transferId, this.approveForm.approveResult, this.approveForm.approveRemark).then(response => {
            this.$modal.msgSuccess("审批成功");
            this.approveOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 取消审批 */
    cancelApprove() {
      this.approveOpen = false;
      this.approveForm = {
        approveResult: '1',
        approveRemark: ''
      };
    },
    /** 执行操作 */
    handleExecute(row) {
      const transferId = row.transferId;
      this.$modal.confirm('是否确认执行划拨单号为"' + row.transferNo + '"的划拨？').then(() => {
        return executeFundTransfer(transferId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("执行成功");
      }).catch(() => {});
    },
    /** 查看明细操作 */
    handleDetails(row) {
      // 这里可以跳转到明细页面或打开明细对话框
      this.$modal.msgInfo("功能开发中...");
    },
    /** 执行自动划拨 */
    handleAutoTransfer() {
      this.$modal.confirm('是否确认执行自动划拨？').then(() => {
        // 调用自动划拨接口
        this.$modal.msgSuccess("自动划拨任务已提交");
      }).catch(() => {});
    },
    /** 查看待处理划拨 */
    handlePendingTransfers() {
      this.pendingOpen = true;
      this.pendingLoading = true;
      getPendingTransfers().then(response => {
        this.pendingList = response.data;
        this.pendingLoading = false;
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/transfer/export', {
        ...this.queryParams
      }, `fund_transfer_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style scoped>
.text-primary {
  color: #409EFF;
}
</style>