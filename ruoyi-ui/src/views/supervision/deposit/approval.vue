<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="申请单号" prop="applyNo">
        <el-input
          v-model="queryParams.applyNo"
          placeholder="请输入申请单号"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请类型" prop="applyType">
        <el-select v-model="queryParams.applyType" placeholder="请选择申请类型" clearable size="small">
          <el-option
            v-for="dict in dict.type.deposit_apply_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="紧急程度" prop="urgencyLevel">
        <el-select v-model="queryParams.urgencyLevel" placeholder="请选择紧急程度" clearable size="small">
          <el-option
            v-for="dict in dict.type.urgency_level"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="申请状态" prop="applyStatus">
        <el-select v-model="queryParams.applyStatus" placeholder="请选择申请状态" clearable size="small">
          <el-option
            v-for="dict in dict.type.deposit_apply_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
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
            <div class="stat-number">{{ statistics.approvedCount || 0 }}</div>
            <div class="stat-label">已批准</div>
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
            <div class="stat-label">已拒绝</div>
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
            <div class="stat-label">总申请</div>
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
          v-hasPermi="['pension:deposit:approve']"
        >批量审批</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-bell"
          size="mini"
          @click="handleTodayPending"
          v-hasPermi="['pension:deposit:query']"
        >今日待处理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-warning"
          size="mini"
          @click="handleUrgentApplies"
          v-hasPermi="['pension:deposit:query']"
        >紧急申请</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-money"
          size="mini"
          @click="handleLargeAmount"
          v-hasPermi="['pension:deposit:query']"
        >大额申请</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pension:deposit:export']"
        >导出</el-button>
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
    <el-table v-loading="loading" :data="depositList" @selection-change="handleSelectionChange" style="width: 100%" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="申请单号" align="center" prop="applyNo" width="150" />
      <el-table-column label="老人姓名" align="center" prop="elderName" width="100" />
      <el-table-column label="机构名称" align="center" prop="institutionName" min-width="150" show-overflow-tooltip />
      <el-table-column label="申请类型" align="center" prop="applyType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.deposit_apply_type" :value="scope.row.applyType"/>
        </template>
      </el-table-column>
      <el-table-column label="申请金额" align="center" prop="applyAmount" width="120">
        <template slot-scope="scope">
          <span class="text-primary">¥{{ scope.row.applyAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="紧急程度" align="center" prop="urgencyLevel" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.urgency_level" :value="scope.row.urgencyLevel"/>
        </template>
      </el-table-column>
      <el-table-column label="申请状态" align="center" prop="applyStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.deposit_apply_status" :value="scope.row.applyStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="申请原因" align="center" prop="applyReason" min-width="150" show-overflow-tooltip />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审批人" align="center" prop="approver" width="100" />
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
            v-hasPermi="['pension:deposit:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleApprove(scope.row)"
            v-if="scope.row.applyStatus === 'family_approved'"
            v-hasPermi="['pension:deposit:approve']"
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleReject(scope.row)"
            v-if="scope.row.applyStatus === 'family_approved'"
            v-hasPermi="['pension:deposit:reject']"
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

    <!-- 押金申请详情对话框 -->
    <el-dialog title="押金申请详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请单号">{{ currentApply.applyNo }}</el-descriptions-item>
        <el-descriptions-item label="申请类型">
          <dict-tag :options="dict.type.deposit_apply_type" :value="currentApply.applyType"/>
        </el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ currentApply.elderName }}</el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ currentApply.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="申请金额">
          <span class="text-primary">¥{{ currentApply.applyAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="账户余额">
          <span class="text-success">¥{{ currentApply.accountBalance }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="紧急程度">
          <dict-tag :options="dict.type.urgency_level" :value="currentApply.urgencyLevel"/>
        </el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <dict-tag :options="dict.type.deposit_apply_status" :value="currentApply.applyStatus"/>
        </el-descriptions-item>
        <el-descriptions-item label="申请原因" :span="2">{{ currentApply.applyReason }}</el-descriptions-item>

        <!-- 家属审批信息 -->
        <el-descriptions-item label="家属审批时间" v-if="currentApply.familyApproveTime">
          {{ parseTime(currentApply.familyApproveTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="家属审批人" v-if="currentApply.familyConfirmName">
          {{ currentApply.familyConfirmName }}
        </el-descriptions-item>
        <el-descriptions-item label="家属审批意见" :span="2" v-if="currentApply.familyApproveOpinion">
          <span :class="getFamilyApprovalClass(currentApply.familyApproveOpinion)">
            {{ getFamilyApprovalText(currentApply.familyApproveOpinion) }}
          </span>
        </el-descriptions-item>

        <!-- 监管审批信息 -->
        <el-descriptions-item label="创建时间">{{ parseTime(currentApply.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="监管审批人">{{ currentApply.approver || '-' }}</el-descriptions-item>
        <el-descriptions-item label="监管审批时间">{{ parseTime(currentApply.approveTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="监管审批意见" v-if="currentApply.approveRemark">
          {{ currentApply.approveRemark }}
        </el-descriptions-item>
        <el-descriptions-item label="实际使用金额" v-if="currentApply.actualAmount">
          <span class="text-warning">¥{{ currentApply.actualAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="使用时间" v-if="currentApply.useTime">
          {{ parseTime(currentApply.useTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2" v-if="currentApply.remark">{{ currentApply.remark }}</el-descriptions-item>
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

    <!-- 今日待处理对话框 -->
    <el-dialog title="今日待处理押金申请" :visible.sync="todayPendingOpen" width="1200px" append-to-body>
      <el-table :data="todayPendingList" border>
        <el-table-column label="申请单号" prop="applyNo" width="150" />
        <el-table-column label="老人姓名" prop="elderName" width="100" />
        <el-table-column label="机构名称" prop="institutionName" min-width="150" />
        <el-table-column label="申请金额" prop="applyAmount" width="120">
          <template slot-scope="scope">
            <span class="text-primary">¥{{ scope.row.applyAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="申请类型" prop="applyType" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.deposit_apply_type" :value="scope.row.applyType"/>
          </template>
        </el-table-column>
        <el-table-column label="紧急程度" prop="urgencyLevel" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.urgency_level" :value="scope.row.urgencyLevel"/>
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
              v-hasPermi="['pension:deposit:approve']"
            >审批</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 紧急申请对话框 -->
    <el-dialog title="紧急押金申请" :visible.sync="urgentOpen" width="1200px" append-to-body>
      <el-table :data="urgentList" border>
        <el-table-column label="申请单号" prop="applyNo" width="150" />
        <el-table-column label="老人姓名" prop="elderName" width="100" />
        <el-table-column label="机构名称" prop="institutionName" min-width="150" />
        <el-table-column label="申请金额" prop="applyAmount" width="120">
          <template slot-scope="scope">
            <span class="text-danger">¥{{ scope.row.applyAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="申请类型" prop="applyType" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.deposit_apply_type" :value="scope.row.applyType"/>
          </template>
        </el-table-column>
        <el-table-column label="申请原因" prop="applyReason" min-width="150" show-overflow-tooltip />
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
              v-hasPermi="['pension:deposit:approve']"
            >审批</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 大额申请对话框 -->
    <el-dialog title="大额押金申请（>5000元）" :visible.sync="largeAmountOpen" width="1200px" append-to-body>
      <el-table :data="largeAmountList" border>
        <el-table-column label="申请单号" prop="applyNo" width="150" />
        <el-table-column label="老人姓名" prop="elderName" width="100" />
        <el-table-column label="机构名称" prop="institutionName" min-width="150" />
        <el-table-column label="申请金额" prop="applyAmount" width="120">
          <template slot-scope="scope">
            <span class="text-danger">¥{{ scope.row.applyAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="申请类型" prop="applyType" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.deposit_apply_type" :value="scope.row.applyType"/>
          </template>
        </el-table-column>
        <el-table-column label="申请原因" prop="applyReason" min-width="150" show-overflow-tooltip />
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
              v-hasPermi="['pension:deposit:approve']"
            >审批</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listApproval, listAllDeposit, getDepositApply, approveDeposit, rejectDeposit, getApprovalStatistics, batchApprove, getTodayPending, getUrgentApplies, getLargeAmountApplies, exportApproval } from "@/api/pension/supervisionDeposit";

export default {
  name: "DepositApproval",
  dicts: ['deposit_apply_type', 'urgency_level', 'deposit_apply_status'],
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
      // 押金申请表格数据
      depositList: [],
      // 统计数据
      statistics: {},
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
      // 是否显示紧急申请弹出层
      urgentOpen: false,
      // 是否显示大额申请弹出层
      largeAmountOpen: false,
      // 当前押金申请信息
      currentApply: {},
      // 今日待处理列表
      todayPendingList: [],
      // 紧急申请列表
      urgentList: [],
      // 大额申请列表
      largeAmountList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        applyNo: null,
        applyType: null,
        urgencyLevel: null,
        applyStatus: "family_approved" // 默认查询家属已审批（待监管审批）
      },
      // 拒绝表单
      rejectForm: {
        applyId: null,
        approveRemark: ''
      },
      // 拒绝表单校验
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
    /** 查询押金使用审批列表 */
    getList() {
      this.loading = true;

      // 根据是否显示全部选择不同的API
      const apiMethod = this.showAll ? listAllDeposit : listApproval;

      apiMethod(this.queryParams).then(response => {
        this.depositList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询统计数据 */
    getStatistics() {
      getApprovalStatistics().then(response => {
        this.statistics = response.data;
        console.log('统计数据:', response.data); // 调试信息
      });
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
      this.ids = selection.map(item => item.applyId);
      this.multiple = !selection.length;
    },
    /** 查看详情操作 */
    handleDetail(row) {
      const applyId = row.applyId;
      getDepositApply(applyId).then(response => {
        this.currentApply = response.data;
        this.detailOpen = true;
      });
    },
    /** 审批通过操作 */
    handleApprove(row) {
      const applyId = row.applyId;
      const applyNo = row.applyNo;

      this.$modal.confirm('是否确认审批通过申请单号为"' + applyNo + '"的押金使用申请？').then(() => {
        return approveDeposit(applyId);
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("审批通过成功");
        // 关闭可能打开的对话框
        this.todayPendingOpen = false;
        this.urgentOpen = false;
        this.largeAmountOpen = false;
      }).catch(() => {});
    },
    /** 审批拒绝操作 */
    handleReject(row) {
      this.rejectForm = {
        applyId: row.applyId,
        approveRemark: ''
      };
      this.rejectOpen = true;
    },
    /** 提交拒绝 */
    submitReject() {
      this.$refs["rejectForm"].validate(valid => {
        if (valid) {
          rejectDeposit(this.rejectForm.applyId, this.rejectForm).then(response => {
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
      const applyIds = this.ids;
      const applyNos = this.depositList.filter(item => applyIds.includes(item.applyId))
        .map(item => item.applyNo).join('、');

      this.$modal.confirm('是否确认批量审批通过以下申请单："' + applyNos + '"？').then(() => {
        return batchApprove(applyIds);
      }).then(response => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess(response.msg);
      }).catch(() => {});
    },
    /** 显示全部切换 */
    handleShowAllChange(value) {
      if (value) {
        this.queryParams.applyStatus = null; // 显示全部状态
      } else {
        this.queryParams.applyStatus = "family_approved"; // 只显示待监管审批
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
    /** 紧急申请 */
    handleUrgentApplies() {
      getUrgentApplies().then(response => {
        this.urgentList = response.data;
        this.urgentOpen = true;
      });
    },
    /** 大额申请 */
    handleLargeAmount() {
      getLargeAmountApplies().then(response => {
        this.largeAmountList = response.data;
        this.largeAmountOpen = true;
      });
    },
    /** 获取家属审批样式类 */
    getFamilyApprovalClass(opinion) {
      if (!opinion) return '';
      if (opinion.includes('同意') || opinion === 'approved' || opinion === 'agree') {
        return 'text-success';
      } else if (opinion.includes('拒绝') || opinion === 'rejected' || opinion === 'reject') {
        return 'text-danger';
      }
      return '';
    },
    /** 获取家属审批文本 */
    getFamilyApprovalText(opinion) {
      if (!opinion) return '';
      // 如果是简单的同意/拒绝标识，转换为友好文本
      if (opinion === 'approved' || opinion === 'agree') {
        return '同意';
      } else if (opinion === 'rejected' || opinion === 'reject') {
        return '拒绝';
      }
      // 否则返回原始意见
      return opinion;
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/supervision/deposit/approval/export', {
        ...this.queryParams
      }, `押金使用审批_${new Date().getTime()}.xlsx`)
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

.text-success {
  color: #67C23A;
  font-weight: bold;
}

.text-warning {
  color: #E6A23C;
  font-weight: bold;
}

.text-danger {
  color: #F56C6C;
  font-weight: bold;
}
</style>
