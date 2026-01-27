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
      <el-form-item label="划拨周期" prop="transferPeriod">
        <el-input
          v-model="queryParams.transferPeriod"
          placeholder="例如：2025-01"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审批状态" prop="approvalStatus">
        <el-select v-model="queryParams.approvalStatus" placeholder="请选择审批状态" clearable size="small">
          <el-option label="待审批" value="0" />
          <el-option label="已通过" value="1" />
          <el-option label="已拒绝" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="划拨状态" prop="transferStatus">
        <el-select v-model="queryParams.transferStatus" placeholder="请选择划拨状态" clearable size="small">
          <el-option label="待划拨" value="0" />
          <el-option label="已划拨" value="1" />
          <el-option label="已取消" value="2" />
        </el-select>
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
          @click="handleGenerate"
          v-hasPermi="['pension:fundTransfer:generate']"
        >生成划拨单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pension:fundTransfer:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="transferList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="划拨单号" align="center" prop="transferNo" width="180" />
      <el-table-column label="划拨周期" align="center" prop="transferPeriod" width="120" />
      <el-table-column label="划拨金额" align="center" prop="transferAmount" width="120">
        <template slot-scope="scope">
          <span class="text-danger">¥{{ scope.row.transferAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="账户数量" align="center" prop="accountCount" width="100" />
      <el-table-column label="审批状态" align="center" prop="approvalStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.approval_status" :value="scope.row.approvalStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="划拨状态" align="center" prop="transferStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.transfer_status" :value="scope.row.transferStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleApprove(scope.row)"
            v-hasPermi="['pension:fundTransfer:approve']"
            v-if="scope.row.approvalStatus === '0'"
          >审批</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-promotion"
            @click="handleExecute(scope.row)"
            v-hasPermi="['pension:fundTransfer:execute']"
            v-if="scope.row.approvalStatus === '1' && scope.row.transferStatus === '0'"
          >执行划拨</el-button>
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

    <!-- 生成划拨单对话框 -->
    <el-dialog title="生成月度划拨单" :visible.sync="generateOpen" width="600px" append-to-body>
      <el-form ref="generateForm" :model="generateForm" :rules="generateRules" label-width="120px">
        <el-form-item label="划拨周期" prop="transferPeriod">
          <el-date-picker
            v-model="generateForm.transferPeriod"
            type="month"
            placeholder="选择月份"
            value-format="yyyy-MM"
            style="width: 100%;">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="generateForm.remark" type="textarea" placeholder="请输入划拨说明" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitGenerate">确 定</el-button>
        <el-button @click="cancelGenerate">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog title="划拨单审批" :visible.sync="approveOpen" width="600px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" :rules="approveRules" label-width="120px">
        <el-form-item label="划拨单号">
          <el-input v-model="approveForm.transferNo" :disabled="true" />
        </el-form-item>
        <el-form-item label="划拨金额">
          <el-input v-model="approveForm.transferAmount" :disabled="true" />
        </el-form-item>
        <el-form-item label="审批结果" prop="approvalStatus">
          <el-radio-group v-model="approveForm.approvalStatus">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="approvalRemark">
          <el-input v-model="approveForm.approvalRemark" type="textarea" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitApprove">确 定</el-button>
        <el-button @click="cancelApprove">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 划拨详情对话框 -->
    <el-dialog title="划拨单详情" :visible.sync="detailOpen" width="80%" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="划拨单号">{{ detailData.transferNo }}</el-descriptions-item>
        <el-descriptions-item label="划拨周期">{{ detailData.transferPeriod }}</el-descriptions-item>
        <el-descriptions-item label="划拨金额">
          <span class="text-danger">¥{{ detailData.transferAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="账户数量">{{ detailData.accountCount }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <dict-tag :options="dict.type.approval_status" :value="detailData.approvalStatus"/>
        </el-descriptions-item>
        <el-descriptions-item label="划拨状态">
          <dict-tag :options="dict.type.transfer_status" :value="detailData.transferStatus"/>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">划拨明细</el-divider>
      <el-table :data="detailList" border>
        <el-table-column label="序号" type="index" width="50" align="center" />
        <el-table-column label="老人姓名" align="center" prop="elderName" />
        <el-table-column label="账户编号" align="center" prop="accountNo" />
        <el-table-column label="划拨前余额" align="center" prop="beforeBalance">
          <template slot-scope="scope">
            <span>¥{{ scope.row.beforeBalance }}</span>
          </template>
        </el-table-column>
        <el-table-column label="划拨金额" align="center" prop="transferAmount">
          <template slot-scope="scope">
            <span class="text-danger">¥{{ scope.row.transferAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="划拨后余额" align="center" prop="afterBalance">
          <template slot-scope="scope">
            <span>¥{{ scope.row.afterBalance }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === '1'" type="success">成功</el-tag>
            <el-tag v-else-if="scope.row.status === '2'" type="danger">失败</el-tag>
            <el-tag v-else type="info">待处理</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFundTransfer, generateFundTransfer, approveFundTransfer, executeFundTransfer, getFundTransfer } from "@/api/pension/fundTransfer";

export default {
  name: "FundTransfer",
  dicts: ['approval_status', 'transfer_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 资金划拨表格数据
      transferList: [],
      // 弹出层标题
      title: "",
      // 是否显示生成对话框
      generateOpen: false,
      // 是否显示审批对话框
      approveOpen: false,
      // 是否显示详情对话框
      detailOpen: false,
      // 详情数据
      detailData: {},
      // 详情列表
      detailList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        transferNo: null,
        transferPeriod: null,
        approvalStatus: null,
        transferStatus: null,
      },
      // 生成表单参数
      generateForm: {},
      // 审批表单参数
      approveForm: {},
      // 生成表单校验
      generateRules: {
        transferPeriod: [
          { required: true, message: "划拨周期不能为空", trigger: "blur" }
        ]
      },
      // 审批表单校验
      approveRules: {
        approvalStatus: [
          { required: true, message: "审批结果不能为空", trigger: "change" }
        ],
        approvalRemark: [
          { required: true, message: "审批意见不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询资金划拨列表 */
    getList() {
      this.loading = true;
      listFundTransfer(this.queryParams).then(response => {
        this.transferList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {};
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.transferId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 生成划拨单按钮操作 */
    handleGenerate() {
      this.reset();
      this.generateOpen = true;
    },
    /** 提交生成划拨单 */
    submitGenerate() {
      this.$refs["generateForm"].validate(valid => {
        if (valid) {
          generateFundTransfer(this.generateForm).then(response => {
            this.$modal.msgSuccess("生成成功");
            this.generateOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 取消生成 */
    cancelGenerate() {
      this.generateOpen = false;
      this.reset();
    },
    /** 审批按钮操作 */
    handleApprove(row) {
      this.reset();
      this.approveForm = {
        transferId: row.transferId,
        transferNo: row.transferNo,
        transferAmount: row.transferAmount,
        approvalStatus: "1",
        approvalRemark: ""
      };
      this.approveOpen = true;
    },
    /** 提交审批 */
    submitApprove() {
      this.$refs["approveForm"].validate(valid => {
        if (valid) {
          approveFundTransfer(this.approveForm).then(response => {
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
      this.reset();
    },
    /** 执行划拨按钮操作 */
    handleExecute(row) {
      const transferId = row.transferId;
      this.$modal.confirm('是否确认执行划拨单号为"' + row.transferNo + '"的资金划拨？').then(function() {
        return executeFundTransfer(transferId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("划拨成功");
      }).catch(() => {});
    },
    /** 查看详情按钮操作 */
    handleView(row) {
      this.loading = true;
      const transferId = row.transferId;
      getFundTransfer(transferId).then(response => {
        this.detailData = response.data;
        this.detailList = response.data.detailList || [];
        this.detailOpen = true;
        this.loading = false;
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/fundTransfer/export', {
        ...this.queryParams
      }, `fundTransfer_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
