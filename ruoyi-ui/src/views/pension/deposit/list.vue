<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="入住人姓名" prop="elderName">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入入住人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="使用事由" prop="purpose">
        <el-select v-model="queryParams.purpose" placeholder="请选择使用事由" clearable>
          <el-option label="医疗费用" value="医疗费用" />
          <el-option label="个人物品购买" value="个人物品购买" />
          <el-option label="特殊护理服务" value="特殊护理服务" />
          <el-option label="其他用途" value="其他用途" />
        </el-select>
      </el-form-item>
      <el-form-item label="紧急程度" prop="urgencyLevel">
        <el-select v-model="queryParams.urgencyLevel" placeholder="请选择紧急程度" clearable>
          <el-option label="一般" value="一般" />
          <el-option label="紧急" value="紧急" />
          <el-option label="非常紧急" value="非常紧急" />
        </el-select>
      </el-form-item>
      <el-form-item label="申请状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择申请状态" clearable>
          <el-option label="待审批" value="待审批" />
          <el-option label="审批中" value="审批中" />
          <el-option label="已通过" value="已通过" />
          <el-option label="已驳回" value="已驳回" />
          <el-option label="已撤回" value="已撤回" />
        </el-select>
      </el-form-item>
      <el-form-item label="拨付状态" prop="paymentStatus">
        <el-select v-model="queryParams.paymentStatus" placeholder="请选择拨付状态" clearable>
          <el-option label="未拨付" value="未拨付" />
          <el-option label="已拨付" value="已拨付" />
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
            <div class="stat-number">{{ statistics.totalCount || 0 }}</div>
            <div class="stat-label">总申请数</div>
            <div class="stat-icon total">
              <i class="el-icon-document"></i>
            </div>
          </div>
        </el-card>
      </el-col>
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
            <div class="stat-number">￥{{ formatMoney(statistics.approvedAmount || 0) }}</div>
            <div class="stat-label">已批准金额</div>
            <div class="stat-icon approved">
              <i class="el-icon-check"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">￥{{ formatMoney(statistics.paidAmount || 0) }}</div>
            <div class="stat-label">已拨付金额</div>
            <div class="stat-icon paid">
              <i class="el-icon-coin"></i>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['pension:deposit:add']"
        >新增申请</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pension:deposit:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="depositUseList"
      @selection-change="handleSelectionChange"
      height="calc(100vh - 450px)"
      border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="申请编号" align="center" prop="applyNo" width="150" />
      <el-table-column label="入住人姓名" align="center" prop="elderName" width="120" />
      <el-table-column label="床位信息" align="center" prop="bedInfo" width="120">
        <template slot-scope="scope">
          <span>{{ scope.row.bedInfo || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="申请金额" align="center" prop="applyAmount" width="120">
        <template slot-scope="scope">
          <span style="font-weight: bold; color: #E6A23C;">￥{{ formatMoney(scope.row.applyAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="使用事由" align="center" prop="purpose" width="120">
        <template slot-scope="scope">
          <span>{{ scope.row.purpose || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="紧急程度" align="center" prop="urgencyLevel" width="100">
        <template slot-scope="scope">
          <el-tag :type="getUrgencyType(scope.row.urgencyLevel)">{{ scope.row.urgencyLevel || '-' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="期望使用日期" align="center" prop="expectedUseDate" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expectedUseDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="申请状态" align="center" prop="applyStatus" width="120">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.applyStatus)">{{ getStatusText(scope.row.applyStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="拨付状态" align="center" prop="actualAmount" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.actualAmount ? 'success' : 'warning'">
            {{ scope.row.actualAmount ? '已拨付' : '未拨付' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="createTime" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['pension:deposit:query']"
          >详情</el-button>

          <!-- 草稿和已撤回状态可以编辑 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pension:deposit:edit']"
            v-if="scope.row.applyStatus === 'draft' || scope.row.applyStatus === 'withdrawn'"
          >编辑</el-button>

          <!-- 待家属审批、家属已审批、待监管审批状态可以撤回 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh-left"
            @click="handleWithdraw(scope.row)"
            v-hasPermi="['pension:deposit:edit']"
            v-if="['pending_family', 'family_approved', 'pending_supervision'].includes(scope.row.applyStatus)"
          >撤回</el-button>

          <!-- 已通过且未拨付可以拨付 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-money"
            @click="handlePayment(scope.row)"
            v-hasPermi="['pension:deposit:payment']"
            v-if="scope.row.applyStatus === 'approved' && !scope.row.actualAmount"
          >拨付</el-button>

          <!-- 草稿和已撤回状态可以删除 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['pension:deposit:delete']"
            v-if="scope.row.applyStatus === 'draft' || scope.row.applyStatus === 'withdrawn'"
            style="color: #F56C6C;"
          >删除</el-button>
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

    <!-- 详情对话框 -->
    <el-dialog title="押金使用申请详情" :visible.sync="openDetail" width="900px" append-to-body>
      <div v-if="detailData" class="detail-content">
        <!-- 审批流程时间线 -->
        <div class="timeline-section" style="margin-bottom: 20px;">
          <h3 style="margin-bottom: 15px; font-size: 16px; color: #303133;">
            <i class="el-icon-time" style="margin-right: 5px;"></i>审批流程
          </h3>
          <el-timeline>
            <!-- 1. 提交申请 (始终显示,已完成) -->
            <el-timeline-item
              :timestamp="parseTime(detailData.createTime, '{y}-{m}-{d} {H}:{i}:{s}')"
              placement="top"
              :color="getCurrentStepColor(1)"
              :icon="getCurrentStepIcon(1)">
              <h4 :style="getCurrentStepStyle(1)">提交申请</h4>
              <p>申请人提交押金使用申请</p>
              <el-tag v-if="getCurrentStep() === 1" type="warning" size="small" style="margin-top: 5px;">
                <i class="el-icon-loading"></i> 当前步骤
              </el-tag>
            </el-timeline-item>

            <!-- 2. 家属审批 (始终显示) -->
            <el-timeline-item
              :timestamp="detailData.familyApproveTime ? parseTime(detailData.familyApproveTime, '{y}-{m}-{d} {H}:{i}:{s}') : ''"
              placement="top"
              :color="getCurrentStepColor(2)"
              :icon="getCurrentStepIcon(2)">
              <h4 :style="getCurrentStepStyle(2)">家属审批</h4>
              <template v-if="detailData.familyApproveTime">
                <p>{{ detailData.familyConfirmName || '家属' }}已确认</p>
                <p v-if="detailData.familyApproveOpinion" style="color: #606266; margin-top: 5px;">
                  意见: {{ detailData.familyApproveOpinion }}
                </p>
              </template>
              <template v-else>
                <p style="color: #909399;">
                  <template v-if="getCurrentStep() === 2">等待家属审批中...</template>
                  <template v-else>待审批</template>
                </p>
              </template>
              <el-tag v-if="getCurrentStep() === 2" type="warning" size="small" style="margin-top: 5px;">
                <i class="el-icon-loading"></i> 当前步骤
              </el-tag>
            </el-timeline-item>

            <!-- 3. 监管审批 (始终显示) -->
            <el-timeline-item
              :timestamp="detailData.approveTime ? parseTime(detailData.approveTime, '{y}-{m}-{d} {H}:{i}:{s}') : ''"
              placement="top"
              :color="getCurrentStepColor(3)"
              :icon="getCurrentStepIcon(3)">
              <h4 :style="getCurrentStepStyle(3)">监管审批</h4>
              <template v-if="detailData.approveTime">
                <p>审批人: {{ detailData.approver || '系统' }}</p>
                <p>审批结果: <el-tag :type="getStatusType(detailData.applyStatus)" size="small">{{ getStatusText(detailData.applyStatus) }}</el-tag></p>
                <p v-if="detailData.approveRemark" style="color: #606266; margin-top: 5px;">
                  意见: {{ detailData.approveRemark }}
                </p>
              </template>
              <template v-else>
                <p style="color: #909399;">
                  <template v-if="getCurrentStep() === 3">等待监管部门审批中...</template>
                  <template v-else>待审批</template>
                </p>
              </template>
              <el-tag v-if="getCurrentStep() === 3" type="warning" size="small" style="margin-top: 5px;">
                <i class="el-icon-loading"></i> 当前步骤
              </el-tag>
            </el-timeline-item>

            <!-- 4. 资金拨付 (始终显示) -->
            <el-timeline-item
              :timestamp="detailData.actualAmount ? parseTime(detailData.useTime, '{y}-{m}-{d} {H}:{i}:{s}') : ''"
              placement="top"
              :color="getCurrentStepColor(4)"
              :icon="getCurrentStepIcon(4)">
              <h4 :style="getCurrentStepStyle(4)">资金拨付</h4>
              <template v-if="detailData.actualAmount">
                <p>拨付金额: <span style="font-weight: bold; color: #67C23A;">￥{{ formatMoney(detailData.actualAmount) }}</span></p>
              </template>
              <template v-else>
                <p style="color: #909399;">
                  <template v-if="getCurrentStep() === 4">等待资金拨付中...</template>
                  <template v-else>待拨付</template>
                </p>
              </template>
              <el-tag v-if="getCurrentStep() === 4" type="warning" size="small" style="margin-top: 5px;">
                <i class="el-icon-loading"></i> 当前步骤
              </el-tag>
            </el-timeline-item>

            <!-- 已撤回/已驳回 特殊状态提示 -->
            <el-timeline-item
              v-if="detailData.applyStatus === 'withdrawn' || detailData.applyStatus === 'rejected'"
              :timestamp="parseTime(detailData.updateTime, '{y}-{m}-{d} {H}:{i}:{s}')"
              placement="top"
              :color="detailData.applyStatus === 'withdrawn' ? '#909399' : '#F56C6C'"
              icon="el-icon-close">
              <h4>{{ detailData.applyStatus === 'withdrawn' ? '申请已撤回' : '申请已驳回' }}</h4>
              <p>{{ detailData.applyStatus === 'withdrawn' ? '申请已被撤回，可重新编辑提交' : '审批未通过，申请已驳回' }}</p>
            </el-timeline-item>
          </el-timeline>
        </div>

        <el-divider></el-divider>

        <!-- 基本信息 -->
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="申请编号">{{ detailData.applyNo }}</el-descriptions-item>
          <el-descriptions-item label="入住人姓名">{{ detailData.elderName }}</el-descriptions-item>
          <el-descriptions-item label="床位信息">{{ detailData.bedInfo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请金额">
            <span style="font-size: 18px; font-weight: bold; color: #E6A23C;">￥{{ formatMoney(detailData.applyAmount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="使用事由">{{ detailData.purpose || '-' }}</el-descriptions-item>
          <el-descriptions-item label="紧急程度">
            <el-tag :type="getUrgencyType(detailData.urgencyLevel)">{{ detailData.urgencyLevel || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="期望使用日期">{{ detailData.expectedUseDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getStatusType(detailData.applyStatus)">{{ getStatusText(detailData.applyStatus) }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 申请原因 -->
        <el-descriptions title="申请原因" :column="1" border style="margin-top: 20px;">
          <el-descriptions-item label="使用原因">{{ detailData.applyReason || '-' }}</el-descriptions-item>
          <el-descriptions-item label="详细说明">{{ detailData.description || '无' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 申请材料 -->
        <div v-if="detailData.attachments" style="margin-top: 20px;">
          <h3 style="margin-bottom: 10px; font-size: 14px; color: #303133;">申请材料</h3>
          <el-tag
            v-for="(file, index) in parseAttachments(detailData.attachments)"
            :key="index"
            style="margin-right: 10px; margin-bottom: 10px; cursor: pointer;"
            @click="handleDownload(file)">
            <i class="el-icon-document"></i> {{ file.name }}
          </el-tag>
          <div v-if="!parseAttachments(detailData.attachments).length" style="color: #909399;">暂无附件</div>
        </div>

      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openDetail = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 拨付对话框 -->
    <el-dialog title="押金拨付" :visible.sync="openPayment" width="600px" append-to-body>
      <el-form ref="paymentForm" :model="paymentForm" :rules="paymentRules" label-width="120px">
        <el-alert
          title="请确认拨付信息"
          type="warning"
          :closable="false"
          style="margin-bottom: 20px;">
        </el-alert>

        <el-descriptions :column="1" border style="margin-bottom: 20px;">
          <el-descriptions-item label="申请编号">{{ currentPaymentData.applyNo }}</el-descriptions-item>
          <el-descriptions-item label="入住人">{{ currentPaymentData.elderName }}</el-descriptions-item>
          <el-descriptions-item label="申请金额">
            <span style="font-size: 16px; font-weight: bold; color: #E6A23C;">￥{{ formatMoney(currentPaymentData.applyAmount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="使用事由">{{ currentPaymentData.purpose }}</el-descriptions-item>
        </el-descriptions>

        <el-form-item label="拨付金额" prop="paymentAmount">
          <el-input-number
            v-model="paymentForm.paymentAmount"
            :min="0"
            :max="currentPaymentData.applyAmount"
            :precision="2"
            style="width: 100%;" />
        </el-form-item>

        <el-form-item label="拨付方式" prop="paymentMethod">
          <el-select v-model="paymentForm.paymentMethod" placeholder="请选择拨付方式" style="width: 100%">
            <el-option label="现金拨付" value="现金拨付"></el-option>
            <el-option label="银行转账" value="银行转账"></el-option>
            <el-option label="支票拨付" value="支票拨付"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="拨付备注">
          <el-input
            v-model="paymentForm.paymentRemark"
            type="textarea"
            :rows="3"
            placeholder="拨付说明（可选）">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openPayment = false">取消</el-button>
        <el-button type="primary" @click="confirmPayment">确认拨付</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDepositUse, getDepositUse, withdrawDepositUse, delDepositUse, paymentDepositUse } from "@/api/elder/depositUse";
import { exportDepositUse } from "@/api/pension/deposit";

export default {
  name: "PensionDepositList",
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
      // 押金使用申请表格数据
      depositUseList: [],
      // 弹出层标题
      title: "",
      // 是否显示详情弹出层
      openDetail: false,
      // 是否显示拨付弹出层
      openPayment: false,
      // 详情数据
      detailData: null,
      // 当前操作数据
      currentPaymentData: {},

      // 统计数据
      statistics: {
        totalCount: 0,
        pendingCount: 0,
        approvedAmount: 0,
        paidAmount: 0
      },

      // 使用事由选项
      purposeOptions: [
        { label: "医疗费用", value: "医疗费用" },
        { label: "个人物品购买", value: "个人物品购买" },
        { label: "特殊护理服务", value: "特殊护理服务" },
        { label: "其他用途", value: "其他用途" }
      ],

      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        elderName: null,
        purpose: null,
        urgencyLevel: null,
        status: null,
        paymentStatus: null
      },

      // 拨付表单数据
      paymentForm: {
        paymentAmount: null,
        paymentMethod: '',
        paymentRemark: ''
      },

      // 拨付表单校验
      paymentRules: {
        paymentAmount: [
          { required: true, message: "请输入拨付金额", trigger: "blur" },
          { type: 'number', min: 0.01, message: "拨付金额必须大于0", trigger: "blur" }
        ],
        paymentMethod: [
          { required: true, message: "请选择拨付方式", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getStatistics();
  },
  methods: {
    /** 查询押金使用申请列表 */
    getList() {
      this.loading = true;
      listDepositUse(this.queryParams).then(response => {
        this.depositUseList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    /** 获取统计数据 */
    getStatistics() {
      // 模拟统计数据，实际应该调用API获取
      this.statistics = {
        totalCount: 45,
        pendingCount: 8,
        approvedAmount: 125000,
        paidAmount: 89000
      };
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

    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.$router.push('/pension/deposit/apply');
    },

    /** 详情按钮操作 */
    handleDetail(row) {
      getDepositUse(row.applyId).then(response => {
        this.detailData = response.data;
        this.openDetail = true;
      });
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.$router.push({
        path: '/pension/deposit/apply',
        query: { applyId: row.applyId }
      });
    },

    /** 撤回按钮操作 */
    handleWithdraw(row) {
      this.$modal.confirm('是否确认撤回该申请？撤回后可重新编辑提交。').then(() => {
        return withdrawDepositUse(row.applyId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("撤回成功，您可以重新编辑该申请");
      }).catch(() => {});
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal.confirm('是否确认删除该申请？删除后数据将无法恢复。').then(() => {
        return delDepositUse(row.applyId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

    /** 拨付按钮操作 */
    handlePayment(row) {
      this.currentPaymentData = row;
      this.paymentForm = {
        paymentAmount: row.applyAmount,
        paymentMethod: '',
        paymentRemark: ''
      };
      this.openPayment = true;
    },

    /** 确认拨付 */
    confirmPayment() {
      this.$refs["paymentForm"].validate(valid => {
        if (valid) {
          const paymentData = {
            applyId: this.currentPaymentData.applyId,
            ...this.paymentForm
          };
          paymentDepositUse(paymentData).then(response => {
            this.$modal.msgSuccess("拨付成功");
            this.openPayment = false;
            this.getList();
          });
        }
      });
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/depositUse/export', {
        ...this.queryParams
      }, `depositUse_${new Date().getTime()}.xlsx`)
    },

    /** 获取紧急程度标签类型 */
    getUrgencyType(urgency) {
      const typeMap = {
        '一般': '',
        '紧急': 'warning',
        '非常紧急': 'danger'
      };
      return typeMap[urgency] || '';
    },

    /** 获取状态文本 */
    getStatusText(status) {
      const statusMap = {
        'draft': '草稿',
        'pending_family': '待家属审批',
        'family_approved': '家属已审批',
        'pending_supervision': '待监管审批',
        'approved': '已通过',
        'rejected': '已驳回',
        'withdrawn': '已撤回'
      };
      return statusMap[status] || status || '-';
    },

    /** 获取状态标签类型 */
    getStatusType(status) {
      const typeMap = {
        'draft': 'info',
        'pending_family': 'warning',
        'family_approved': 'primary',
        'pending_supervision': 'warning',
        'approved': 'success',
        'rejected': 'danger',
        'withdrawn': 'info'
      };
      return typeMap[status] || '';
    },

    /** 获取拨付状态标签类型 */
    getPaymentType(status) {
      const typeMap = {
        '未拨付': 'warning',
        '已拨付': 'success'
      };
      return typeMap[status] || '';
    },

    /** 格式化金额 */
    formatMoney(amount) {
      if (!amount) return '0.00';
      return parseFloat(amount).toFixed(2);
    },

    /** 解析附件 */
    parseAttachments(attachments) {
      if (!attachments) return [];
      try {
        const parsed = JSON.parse(attachments);
        return Array.isArray(parsed) ? parsed : [];
      } catch (e) {
        return [];
      }
    },

    /** 下载附件 */
    handleDownload(file) {
      if (file.url) {
        window.open(process.env.VUE_APP_BASE_API + file.url, '_blank');
      } else {
        this.$message.warning('文件地址不存在');
      }
    },

    /** 格式化时间 */
    parseTime(time, pattern) {
      if (!time) return '';
      if (typeof time === 'object') {
        time = time.getFullYear() + '-' +
               (time.getMonth() + 1).toString().padStart(2, '0') + '-' +
               time.getDate().toString().padStart(2, '0');
      }
      if (!pattern) return time;

      const date = new Date(time);
      const format = {
        y: date.getFullYear(),
        m: (date.getMonth() + 1).toString().padStart(2, '0'),
        d: date.getDate().toString().padStart(2, '0'),
        H: date.getHours().toString().padStart(2, '0'),
        i: date.getMinutes().toString().padStart(2, '0'),
        s: date.getSeconds().toString().padStart(2, '0')
      };

      return pattern.replace(/{([ymdHis])+}/g, (result, key) => {
        return format[key] || '';
      });
    },

    /** 获取当前审批步骤 (1-4) */
    getCurrentStep() {
      const status = this.detailData.applyStatus;

      // 已撤回或已驳回,返回0表示流程中断
      if (status === 'withdrawn' || status === 'rejected') {
        return 0;
      }

      // 已拨付,流程完成
      if (this.detailData.actualAmount) {
        return 0; // 所有步骤都已完成
      }

      // 已通过,等待拨付
      if (status === 'approved') {
        return 4;
      }

      // 家属已审批或待监管审批
      if (status === 'family_approved' || status === 'pending_supervision') {
        return 3;
      }

      // 待家属审批
      if (status === 'pending_family') {
        return 2;
      }

      // 草稿状态
      if (status === 'draft') {
        return 1;
      }

      return 1; // 默认第一步
    },

    /** 获取步骤颜色 */
    getCurrentStepColor(step) {
      const currentStep = this.getCurrentStep();
      const status = this.detailData.applyStatus;

      // 步骤1: 提交申请 (总是已完成)
      if (step === 1) {
        return '#67C23A'; // 绿色-已完成
      }

      // 步骤2: 家属审批
      if (step === 2) {
        if (this.detailData.familyApproveTime) {
          return '#67C23A'; // 绿色-已完成
        } else if (currentStep === 2) {
          return '#E6A23C'; // 橙色-进行中
        } else {
          return '#C0C4CC'; // 灰色-未开始
        }
      }

      // 步骤3: 监管审批
      if (step === 3) {
        if (this.detailData.approveTime) {
          // 已审批,根据结果显示颜色
          return status === 'approved' ? '#67C23A' : '#F56C6C'; // 绿色-通过 / 红色-驳回
        } else if (currentStep === 3) {
          return '#E6A23C'; // 橙色-进行中
        } else {
          return '#C0C4CC'; // 灰色-未开始
        }
      }

      // 步骤4: 资金拨付
      if (step === 4) {
        if (this.detailData.actualAmount) {
          return '#67C23A'; // 绿色-已完成
        } else if (currentStep === 4) {
          return '#E6A23C'; // 橙色-进行中
        } else {
          return '#C0C4CC'; // 灰色-未开始
        }
      }

      return '#C0C4CC'; // 默认灰色
    },

    /** 获取步骤图标 */
    getCurrentStepIcon(step) {
      const currentStep = this.getCurrentStep();
      const status = this.detailData.applyStatus;

      // 步骤1: 总是已完成
      if (step === 1) {
        return 'el-icon-check';
      }

      // 步骤2: 家属审批
      if (step === 2) {
        if (this.detailData.familyApproveTime) {
          return 'el-icon-check'; // 已完成
        } else if (currentStep === 2) {
          return 'el-icon-loading'; // 进行中
        } else {
          return 'el-icon-more'; // 未开始
        }
      }

      // 步骤3: 监管审批
      if (step === 3) {
        if (this.detailData.approveTime) {
          return status === 'approved' ? 'el-icon-check' : 'el-icon-close'; // 通过/驳回
        } else if (currentStep === 3) {
          return 'el-icon-loading'; // 进行中
        } else {
          return 'el-icon-more'; // 未开始
        }
      }

      // 步骤4: 资金拨付
      if (step === 4) {
        if (this.detailData.actualAmount) {
          return 'el-icon-check'; // 已完成
        } else if (currentStep === 4) {
          return 'el-icon-loading'; // 进行中
        } else {
          return 'el-icon-more'; // 未开始
        }
      }

      return 'el-icon-more'; // 默认
    },

    /** 获取步骤标题样式 */
    getCurrentStepStyle(step) {
      const currentStep = this.getCurrentStep();

      // 当前步骤: 加粗+橙色
      if (step === currentStep) {
        return 'font-weight: bold; color: #E6A23C; font-size: 16px;';
      }

      // 已完成步骤: 正常
      if (step < currentStep || currentStep === 0) {
        return 'font-weight: normal; color: #303133;';
      }

      // 未开始步骤: 灰色
      return 'font-weight: normal; color: #909399;';
    },

    /** 下载文件 */
    download(url, params, filename) {
      // 这里可以调用系统内置的下载方法
      console.log('下载文件:', url, params, filename);
    }
  }
};
</script>

<style scoped>
.stat-card {
  height: 100px;
  margin-bottom: 10px;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
  position: relative;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 30px;
  opacity: 0.8;
}

.stat-icon.total {
  color: #409EFF;
}

.stat-icon.pending {
  color: #E6A23C;
}

.stat-icon.approved {
  color: #67C23A;
}

.stat-icon.paid {
  color: #F56C6C;
}

.detail-content {
  max-height: 600px;
  overflow-y: auto;
}
</style>