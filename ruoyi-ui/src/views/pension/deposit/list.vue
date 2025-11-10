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
    <el-table v-loading="loading" :data="depositUseList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="申请编号" align="center" prop="applyNo" width="150" />
      <el-table-column label="入住人姓名" align="center" prop="elderName" width="120" />
      <el-table-column label="床位信息" align="center" prop="bedInfo" width="120" />
      <el-table-column label="申请金额" align="center" prop="amount" width="120">
        <template slot-scope="scope">
          <span style="font-weight: bold; color: #E6A23C;">￥{{ formatMoney(scope.row.amount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="使用事由" align="center" prop="purpose" width="120">
        <template slot-scope="scope">
          <dict-tag :options="purposeOptions" :value="scope.row.purpose"/>
        </template>
      </el-table-column>
      <el-table-column label="紧急程度" align="center" prop="urgencyLevel" width="100">
        <template slot-scope="scope">
          <el-tag :type="getUrgencyType(scope.row.urgencyLevel)">{{ scope.row.urgencyLevel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="期望使用日期" align="center" prop="expectedUseDate" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expectedUseDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="申请状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="拨付状态" align="center" prop="paymentStatus" width="100">
        <template slot-scope="scope">
          <el-tag :type="getPaymentType(scope.row.paymentStatus)">{{ scope.row.paymentStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="createTime" width="100">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
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
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['pension:deposit:edit']"
            v-if="scope.row.status === '待审批' || scope.row.status === '审批中'"
          >编辑</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-back"
            @click="handleWithdraw(scope.row)"
            v-hasPermi="['pension:deposit:edit']"
            v-if="scope.row.status === '待审批' || scope.row.status === '审批中'"
          >撤回</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-money"
            @click="handlePayment(scope.row)"
            v-hasPermi="['pension:deposit:payment']"
            v-if="scope.row.status === '已通过' && scope.row.paymentStatus === '未拨付'"
          >拨付</el-button>
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
    <el-dialog title="押金使用申请详情" :visible.sync="openDetail" width="800px" append-to-body>
      <div v-if="detailData" class="detail-content">
        <!-- 基本信息 -->
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="申请编号">{{ detailData.applyNo }}</el-descriptions-item>
          <el-descriptions-item label="入住人姓名">{{ detailData.elderName }}</el-descriptions-item>
          <el-descriptions-item label="床位信息">{{ detailData.bedInfo }}</el-descriptions-item>
          <el-descriptions-item label="申请金额">
            <span style="font-size: 18px; font-weight: bold; color: #E6A23C;">￥{{ formatMoney(detailData.amount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="使用事由">{{ detailData.purpose }}</el-descriptions-item>
          <el-descriptions-item label="紧急程度">
            <el-tag :type="getUrgencyType(detailData.urgencyLevel)">{{ detailData.urgencyLevel }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="期望使用日期">{{ detailData.expectedUseDate }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
        </el-descriptions>

        <!-- 申请原因 -->
        <el-descriptions title="申请原因" :column="1" border style="margin-top: 20px;">
          <el-descriptions-item label="使用原因">{{ detailData.reason }}</el-descriptions-item>
          <el-descriptions-item label="详细说明">{{ detailData.description || '无' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 确认信息 -->
        <el-descriptions title="确认信息" :column="2" border style="margin-top: 20px;" v-if="detailData.confirmName">
          <el-descriptions-item label="确认人">{{ detailData.confirmName }}</el-descriptions-item>
          <el-descriptions-item label="与老人关系">{{ detailData.confirmRelation }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detailData.confirmPhone }}</el-descriptions-item>
          <el-descriptions-item label="确认方式">{{ detailData.confirmMethod }}</el-descriptions-item>
          <el-descriptions-item label="确认时间" :span="2">{{ parseTime(detailData.confirmTime) }}</el-descriptions-item>
          <el-descriptions-item label="确认意见" :span="2">{{ detailData.confirmComment || '无' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 审批���息 -->
        <el-descriptions title="审批信息" :column="2" border style="margin-top: 20px;" v-if="detailData.approvalTime">
          <el-descriptions-item label="审批状态">
            <el-tag :type="getStatusType(detailData.status)">{{ detailData.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="审批人">{{ detailData.approver || '系统' }}</el-descriptions-item>
          <el-descriptions-item label="审批时间" :span="2">{{ parseTime(detailData.approvalTime) }}</el-descriptions-item>
          <el-descriptions-item label="审批意见" :span="2">{{ detailData.approvalComment || '无' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 拨付信息 -->
        <el-descriptions title="拨付信息" :column="2" border style="margin-top: 20px;" v-if="detailData.paymentTime">
          <el-descriptions-item label="拨付状态">
            <el-tag :type="getPaymentType(detailData.paymentStatus)">{{ detailData.paymentStatus }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="拨付金额">
            <span style="font-weight: bold; color: #67C23A;">￥{{ formatMoney(detailData.paymentAmount || detailData.amount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="拨付时间" :span="2">{{ parseTime(detailData.paymentTime) }}</el-descriptions-item>
          <el-descriptions-item label="拨付备注" :span="2">{{ detailData.paymentRemark || '无' }}</el-descriptions-item>
        </el-descriptions>
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
            <span style="font-size: 16px; font-weight: bold; color: #E6A23C;">￥{{ formatMoney(currentPaymentData.amount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="使用事由">{{ currentPaymentData.purpose }}</el-descriptions-item>
        </el-descriptions>

        <el-form-item label="拨付金额" prop="paymentAmount">
          <el-input-number
            v-model="paymentForm.paymentAmount"
            :min="0"
            :max="currentPaymentData.amount"
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
import { listDepositUse, getDepositUse, withdrawDepositUse, paymentDepositUse } from "@/api/elder/depositUse";
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
      getDepositUse(row.id).then(response => {
        this.detailData = response.data;
        this.openDetail = true;
      });
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.$router.push({
        path: '/pension/deposit/apply',
        query: { id: row.id }
      });
    },

    /** 撤回按钮操作 */
    handleWithdraw(row) {
      this.$modal.confirm('是否确撤回该申请？').then(() => {
        return withdrawDepositUse(row.id);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("撤回成功");
      }).catch(() => {});
    },

    /** 拨付按钮操作 */
    handlePayment(row) {
      this.currentPaymentData = row;
      this.paymentForm = {
        paymentAmount: row.amount,
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
            id: this.currentPaymentData.id,
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

    /** 获取状态标签类型 */
    getStatusType(status) {
      const typeMap = {
        '待审批': 'warning',
        '审批中': '',
        '已通过': 'success',
        '已驳回': 'danger',
        '已撤回': 'info'
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