<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-primary">
              <i class="el-icon-document-copy"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">待审批</div>
              <div class="stat-value">{{ statistics.pendingCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-success">
              <i class="el-icon-check"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">已通过</div>
              <div class="stat-value">{{ statistics.approvedCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-warning">
              <i class="el-icon-s-promotion"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">待划拨</div>
              <div class="stat-value">{{ statistics.pendingTransferCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-info">
              <i class="el-icon-coin"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">本月划拨总额</div>
              <div class="stat-value">¥{{ statistics.monthlyTotal | formatAmount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

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
      <el-form-item label="划拨周期" prop="transferPeriod">
        <el-date-picker
          v-model="queryParams.transferPeriod"
          type="month"
          placeholder="选择月份"
          clearable
          size="small"
          value-format="yyyy-MM"
          style="width: 160px;">
        </el-date-picker>
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

    <!-- 操作按钮 -->
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

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="transferList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="划拨单号" align="center" prop="transferNo" width="180" />
      <el-table-column label="划拨周期" align="center" prop="transferPeriod" width="120" />
      <el-table-column label="申请金额" align="center" prop="applyAmount" width="120">
        <template slot-scope="scope">
          <span class="text-primary">¥{{ scope.row.applyAmount | formatAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审批金额" align="center" prop="approvedAmount" width="120">
        <template slot-scope="scope">
          <span class="text-success">¥{{ scope.row.approvedAmount | formatAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="账户数量" align="center" prop="accountCount" width="100" />
      <el-table-column label="审批状态" align="center" prop="approvalStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.approvalStatus === '0'" type="warning">待审批</el-tag>
          <el-tag v-else-if="scope.row.approvalStatus === '1'" type="success">已通过</el-tag>
          <el-tag v-else-if="scope.row.approvalStatus === '2'" type="danger">已拒绝</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="划拨状态" align="center" prop="transferStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.transferStatus === '0'" type="warning">待划拨</el-tag>
          <el-tag v-else-if="scope.row.transferStatus === '1'" type="success">已划拨</el-tag>
          <el-tag v-else-if="scope.row.transferStatus === '2'" type="info">已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="300">
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
            icon="el-icon-edit"
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
          <el-button
            size="mini"
            type="text"
            icon="el-icon-document"
            @click="handlePrint(scope.row)"
            v-if="scope.row.transferStatus === '1'"
          >打印</el-button>
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
        <el-form-item label="划拨类型" prop="transferType">
          <el-select v-model="generateForm.transferType" placeholder="请选择划拨类型" style="width: 100%;">
            <el-option label="常规月度划拨" value="monthly" />
            <el-option label="特殊划拨" value="special" />
          </el-select>
        </el-form-item>
        <el-form-item label="划拨说明">
          <el-input v-model="generateForm.remark" type="textarea" :rows="3" placeholder="请输入划拨说明" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitGenerate" :loading="generateLoading">确 定</el-button>
        <el-button @click="cancelGenerate">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog title="划拨单审批" :visible.sync="approveOpen" width="600px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" :rules="approveRules" label-width="120px">
        <el-form-item label="划拨单号">
          <el-input v-model="approveForm.transferNo" :disabled="true" />
        </el-form-item>
        <el-form-item label="申请金额">
          <el-input v-model="approveForm.applyAmount" :disabled="true">
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="审批金额" prop="approvedAmount">
          <el-input-number v-model="approveForm.approvedAmount" :min="0" :precision="2" style="width: 100%;">
            <template slot="prepend">¥</template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="审批结果" prop="approvalStatus">
          <el-radio-group v-model="approveForm.approvalStatus">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="approvalRemark">
          <el-input v-model="approveForm.approvalRemark" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitApprove" :loading="approveLoading">确 定</el-button>
        <el-button @click="cancelApprove">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 划拨详情对话框 -->
    <el-dialog title="划拨单详情" :visible.sync="detailOpen" width="90%" append-to-body>
      <el-descriptions :column="3" border class="mb20">
        <el-descriptions-item label="划拨单号">{{ detailData.transferNo }}</el-descriptions-item>
        <el-descriptions-item label="划拨周期">{{ detailData.transferPeriod }}</el-descriptions-item>
        <el-descriptions-item label="划拨类型">{{ detailData.transferType }}</el-descriptions-item>
        <el-descriptions-item label="申请金额">
          <span class="text-primary">¥{{ detailData.applyAmount | formatAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="审批金额">
          <span class="text-success">¥{{ detailData.approvedAmount | formatAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="账户数量">{{ detailData.accountCount }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag v-if="detailData.approvalStatus === '0'" type="warning">待审批</el-tag>
          <el-tag v-else-if="detailData.approvalStatus === '1'" type="success">已通过</el-tag>
          <el-tag v-else-if="detailData.approvalStatus === '2'" type="danger">已拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="划拨状态">
          <el-tag v-if="detailData.transferStatus === '0'" type="warning">待划拨</el-tag>
          <el-tag v-else-if="detailData.transferStatus === '1'" type="success">已划拨</el-tag>
          <el-tag v-else-if="detailData.transferStatus === '2'" type="info">已取消</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detailData.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ detailData.approverName }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ parseTime(detailData.approveTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">划拨明细</el-divider>
      <el-table :data="detailList" border max-height="400">
        <el-table-column label="序号" type="index" width="50" align="center" />
        <el-table-column label="老人姓名" align="center" prop="elderName" width="100" />
        <el-table-column label="账户编号" align="center" prop="accountNo" width="140" />
        <el-table-column label="账户类型" align="center" prop="accountType" width="100">
          <template slot-scope="scope">
            <el-tag size="mini" type="info">{{ scope.row.accountType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="划拨前余额" align="center" prop="beforeBalance" width="120">
          <template slot-scope="scope">
            <span>¥{{ scope.row.beforeBalance | formatAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="申请金额" align="center" prop="applyAmount" width="120">
          <template slot-scope="scope">
            <span class="text-primary">¥{{ scope.row.applyAmount | formatAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="审批金额" align="center" prop="approvedAmount" width="120">
          <template slot-scope="scope">
            <span class="text-success">¥{{ scope.row.approvedAmount | formatAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="划拨后余额" align="center" prop="afterBalance" width="120">
          <template slot-scope="scope">
            <span>¥{{ scope.row.afterBalance | formatAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === '1'" type="success" size="mini">成功</el-tag>
            <el-tag v-else-if="scope.row.status === '2'" type="danger" size="mini">失败</el-tag>
            <el-tag v-else type="info" size="mini">待处理</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" min-width="120" />
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
        <el-button type="primary" @click="handlePrintDetail" v-if="detailData.transferStatus === '1'">打印划拨单</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFundTransfer, generateFundTransfer, approveFundTransfer, executeFundTransfer, getFundTransfer } from "@/api/pension/fundTransfer";

export default {
  name: "FundTransfer",
  filters: {
    formatAmount(value) {
      if (!value) return '0.00';
      return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }
  },
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
      // 生成加载状态
      generateLoading: false,
      // 审批加载状态
      approveLoading: false,
      // 详情数据
      detailData: {},
      // 详情列表
      detailList: [],
      // 统计数据
      statistics: {
        pendingCount: 0,
        approvedCount: 0,
        pendingTransferCount: 0,
        monthlyTotal: 0
      },
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
      generateForm: {
        transferPeriod: '',
        transferType: 'monthly',
        remark: ''
      },
      // 审批表单参数
      approveForm: {},
      // 生成表单校验
      generateRules: {
        transferPeriod: [
          { required: true, message: "划拨周期不能为空", trigger: "blur" }
        ],
        transferType: [
          { required: true, message: "划拨类型不能为空", trigger: "change" }
        ]
      },
      // 审批表单校验
      approveRules: {
        approvedAmount: [
          { required: true, message: "审批金额不能为空", trigger: "blur" }
        ],
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
    this.getStatistics();
  },
  methods: {
    /** 查询资金划拨列表 */
    getList() {
      this.loading = true;
      // TODO: 调用真实API
      // listFundTransfer(this.queryParams).then(response => {
      //   this.transferList = response.rows;
      //   this.total = response.total;
      //   this.loading = false;
      // });

      // 模拟数据
      setTimeout(() => {
        this.transferList = [
          {
            transferId: 1,
            transferNo: 'HB202501001',
            transferPeriod: '2025-01',
            applyAmount: 125680.50,
            approvedAmount: 120000.00,
            accountCount: 45,
            approvalStatus: '0',
            transferStatus: '0',
            createTime: '2025-01-03 10:30:00',
            transferType: '常规月度划拨'
          },
          {
            transferId: 2,
            transferNo: 'HB202501002',
            transferPeriod: '2025-01',
            applyAmount: 89750.00,
            approvedAmount: 89750.00,
            accountCount: 32,
            approvalStatus: '1',
            transferStatus: '0',
            createTime: '2025-01-03 09:15:00',
            transferType: '常规月度划拨'
          },
          {
            transferId: 3,
            transferNo: 'HB202412001',
            transferPeriod: '2024-12',
            applyAmount: 156320.80,
            approvedAmount: 156320.80,
            accountCount: 58,
            approvalStatus: '1',
            transferStatus: '1',
            createTime: '2024-12-28 14:20:00',
            transferType: '常规月度划拨'
          }
        ];
        this.total = this.transferList.length;
        this.loading = false;
      }, 500);
    },

    /** 获取统计数据 */
    getStatistics() {
      // TODO: 调用真实API获取统计数据
      this.statistics = {
        pendingCount: 3,
        approvedCount: 15,
        pendingTransferCount: 5,
        monthlyTotal: 366070.80
      };
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
          this.generateLoading = true;
          // TODO: 调用真实API
          // generateFundTransfer(this.generateForm).then(response => {
          //   this.$modal.msgSuccess("生成成功");
          //   this.generateOpen = false;
          //   this.getList();
          //   this.getStatistics();
          // }).catch(() => {
          //   this.generateLoading = false;
          // });

          // 模拟API调用
          setTimeout(() => {
            this.$modal.msgSuccess("生成成功");
            this.generateOpen = false;
            this.getList();
            this.getStatistics();
            this.generateLoading = false;
          }, 1000);
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
        applyAmount: row.applyAmount,
        approvedAmount: row.applyAmount,
        approvalStatus: "1",
        approvalRemark: ""
      };
      this.approveOpen = true;
    },

    /** 提交审批 */
    submitApprove() {
      this.$refs["approveForm"].validate(valid => {
        if (valid) {
          this.approveLoading = true;
          // TODO: 调用真实API
          // approveFundTransfer(this.approveForm).then(response => {
          //   this.$modal.msgSuccess("审批成功");
          //   this.approveOpen = false;
          //   this.getList();
          //   this.getStatistics();
          // }).catch(() => {
          //   this.approveLoading = false;
          // });

          // 模拟API调用
          setTimeout(() => {
            this.$modal.msgSuccess("审批成功");
            this.approveOpen = false;
            this.getList();
            this.getStatistics();
            this.approveLoading = false;
          }, 1000);
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
      this.$modal.confirm('是否确认执行划拨单号为"' + row.transferNo + '"的资金划拨？').then(() => {
        // TODO: 调用真实API
        // return executeFundTransfer(transferId);

        // 模拟API调用
        return Promise.resolve();
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("划拨成功");
      }).catch(() => {});
    },

    /** 查看详情按钮操作 */
    handleView(row) {
      this.loading = true;
      const transferId = row.transferId;
      // TODO: 调用真实API
      // getFundTransfer(transferId).then(response => {
      //   this.detailData = response.data;
      //   this.detailList = response.data.detailList || [];
      //   this.detailOpen = true;
      //   this.loading = false;
      // });

      // 模拟数据
      setTimeout(() => {
        this.detailData = row;
        this.detailList = [
          {
            elderName: '张奶奶',
            accountNo: 'ACC001',
            accountType: '个人账户',
            beforeBalance: 15680.50,
            applyAmount: 2800.00,
            approvedAmount: 2800.00,
            afterBalance: 12880.50,
            status: '1',
            remark: '月度生活费用'
          },
          {
            elderName: '李爷爷',
            accountNo: 'ACC002',
            accountType: '个人账户',
            beforeBalance: 22500.00,
            applyAmount: 2800.00,
            approvedAmount: 2800.00,
            afterBalance: 19700.00,
            status: '1',
            remark: '月度生活费用'
          }
        ];
        this.detailOpen = true;
        this.loading = false;
      }, 500);
    },

    /** 打印划拨单 */
    handlePrint(row) {
      // TODO: 实现打印功能
      this.$modal.msgInfo("打印功能开发中...");
    },

    /** 打印详情 */
    handlePrintDetail() {
      // TODO: 实现打印功能
      this.$modal.msgInfo("打印功能开发中...");
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

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  font-size: 20px;
}

.bg-primary {
  background-color: #409EFF;
}

.bg-success {
  background-color: #67C23A;
}

.bg-warning {
  background-color: #E6A23C;
}

.bg-info {
  background-color: #909399;
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.text-primary {
  color: #409EFF;
  font-weight: bold;
}

.text-success {
  color: #67C23A;
  font-weight: bold;
}

.text-danger {
  color: #F56C6C;
  font-weight: bold;
}

.text-warning {
  color: #E6A23C;
  font-weight: bold;
}

.mb20 {
  margin-bottom: 20px;
}
</style>