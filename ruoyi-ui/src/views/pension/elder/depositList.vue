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
          v-hasPermi="['elder:deposit:add']"
        >新增申请</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['elder:deposit:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="depositList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="申请编号" align="center" prop="applyNo" width="150" />
      <el-table-column label="入住人姓名" align="center" prop="elderName" width="100" />
      <el-table-column label="床位信息" align="center" prop="bedInfo" width="120" />
      <el-table-column label="申请金额" align="center" prop="amount" width="120">
        <template slot-scope="scope">
          <span class="amount-text">￥{{ formatMoney(scope.row.amount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="使用事由" align="center" prop="purpose" width="120" />
      <el-table-column label="紧急程度" align="center" prop="urgencyLevel" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.urgencyLevel === '非常紧急'" type="danger">{{ scope.row.urgencyLevel }}</el-tag>
          <el-tag v-else-if="scope.row.urgencyLevel === '紧急'" type="warning">{{ scope.row.urgencyLevel }}</el-tag>
          <el-tag v-else type="info">{{ scope.row.urgencyLevel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="拨付状态" align="center" prop="paymentStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.paymentStatus === '已拨付'" type="success">{{ scope.row.paymentStatus }}</el-tag>
          <el-tag v-else type="info">{{ scope.row.paymentStatus || '未拨付' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="确认人" align="center" prop="confirmName" width="100" />
      <el-table-column label="审批人" align="center" prop="approver" width="100" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['elder:deposit:query']"
          >详情</el-button>
          <el-button
            v-if="scope.row.status === '待审批'"
            size="mini"
            type="text"
            icon="el-icon-back"
            @click="handleCancel(scope.row)"
            v-hasPermi="['elder:deposit:cancel']"
          >撤回</el-button>
          <el-button
            v-if="scope.row.status === '已通过' && scope.row.paymentStatus === '未拨付'"
            size="mini"
            type="text"
            icon="el-icon-money"
            @click="handlePayment(scope.row)"
            v-hasPermi="['elder:deposit:payment']"
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
    <el-dialog title="押金使用申请详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <div class="deposit-detail">
        <el-descriptions title="基本信息" :column="2" border>
          <el-descriptions-item label="申请编号">{{ detailData.applyNo }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ detailData.createTime }}</el-descriptions-item>
          <el-descriptions-item label="入住人姓名">{{ detailData.elderName }}</el-descriptions-item>
          <el-descriptions-item label="床位信息">{{ detailData.bedInfo }}</el-descriptions-item>
          <el-descriptions-item label="申请金额">
            <span class="amount-text large">￥{{ formatMoney(detailData.amount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="使用事由">{{ detailData.purpose }}</el-descriptions-item>
          <el-descriptions-item label="紧急程度">{{ detailData.urgencyLevel }}</el-descriptions-item>
          <el-descriptions-item label="期望使用日期">{{ detailData.expectedUseDate }}</el-descriptions-item>
          <el-descriptions-item label="申请状态">
            <el-tag :type="getStatusType(detailData.status)">{{ detailData.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="拨付状态">
            <el-tag v-if="detailData.paymentStatus === '已拨付'" type="success">{{ detailData.paymentStatus }}</el-tag>
            <el-tag v-else type="info">{{ detailData.paymentStatus || '未拨付' }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="申请详情" :column="1" border style="margin-top: 20px;">
          <el-descriptions-item label="使用原因">{{ detailData.reason }}</el-descriptions-item>
          <el-descriptions-item label="详细说明">{{ detailData.description }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ detailData.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="确认信息" :column="2" border style="margin-top: 20px;">
          <el-descriptions-item label="确认人">{{ detailData.confirmName }}</el-descriptions-item>
          <el-descriptions-item label="与老人关系">{{ detailData.confirmRelation }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detailData.confirmPhone }}</el-descriptions-item>
          <el-descriptions-item label="确认方式">{{ detailData.confirmMethod }}</el-descriptions-item>
          <el-descriptions-item label="确认意见" :span="2">{{ detailData.confirmComment }}</el-descriptions-item>
          <el-descriptions-item label="电子签名" :span="2">{{ detailData.signature }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="审批信息" :column="2" border style="margin-top: 20px;" v-if="detailData.status !== '待审批'">
          <el-descriptions-item label="审批人">{{ detailData.approver || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批时间">{{ detailData.approveTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批结果" :span="2">
            <el-tag v-if="detailData.status === '已通过'" type="success">{{ detailData.status }}</el-tag>
            <el-tag v-else-if="detailData.status === '已驳回'" type="danger">{{ detailData.status }}</el-tag>
            <el-tag v-else-if="detailData.status === '已撤回'" type="warning">{{ detailData.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="审批意见" :span="2">{{ detailData.approveRemark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-descriptions title="拨付信息" :column="2" border style="margin-top: 20px;" v-if="detailData.paymentStatus === '已拨付'">
          <el-descriptions-item label="拨付金额">
            <span class="amount-text">￥{{ formatMoney(detailData.paymentAmount || detailData.amount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="拨付时间">{{ detailData.paymentTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="拨付方式">{{ detailData.paymentMethod || '-' }}</el-descriptions-item>
          <el-descriptions-item label="操作人">{{ detailData.paymentOperator || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 附件材料 -->
        <div v-if="detailData.attachments && detailData.attachments.length > 0" style="margin-top: 20px;">
          <h4>附件材料</h4>
          <div class="attachment-list">
            <div v-for="(attachment, index) in detailData.attachments" :key="index" class="attachment-item">
              <i class="el-icon-document"></i>
              <a href="#" @click.prevent="previewAttachment(attachment)">{{ attachment.name || '附件' + (index + 1) }}</a>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 拨付对话框 -->
    <el-dialog title="押金拨付" :visible.sync="paymentOpen" width="500px" append-to-body>
      <el-form ref="paymentForm" :model="paymentForm" :rules="paymentRules" label-width="100px">
        <el-form-item label="拨付金额" prop="amount">
          <el-input-number v-model="paymentForm.amount" :min="0" :max="paymentData.amount" :precision="2" style="width: 100%;" />
          <div class="form-tip">批准金额：￥{{ formatMoney(paymentData.amount) }}</div>
        </el-form-item>
        <el-form-item label="拨付方式" prop="paymentMethod">
          <el-select v-model="paymentForm.paymentMethod" placeholder="请选择拨付方式" style="width: 100%">
            <el-option label="现金" value="现金"></el-option>
            <el-option label="银行转账" value="银行转账"></el-option>
            <el-option label="支票" value="支票"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="收款人" prop="payee">
          <el-input v-model="paymentForm.payee" placeholder="请输入收款人姓名" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="paymentForm.remark" type="textarea" placeholder="请输入备注信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitPayment">确 定</el-button>
        <el-button @click="paymentOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDepositUse, getDepositUse, cancelDepositUse, paymentDepositUse } from "@/api/elder/depositUse";

export default {
  name: "ElderDepositList",
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
      depositList: [],
      // 统计数据
      statistics: {},
      // 弹出层标题
      title: "",
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示拨付弹出层
      paymentOpen: false,
      // 详情数据
      detailData: {},
      // 拨付数据
      paymentData: {},
      // 拨付表单
      paymentForm: {
        amount: null,
        paymentMethod: '',
        payee: '',
        remark: ''
      },
      // 拨付表单校验
      paymentRules: {
        amount: [
          { required: true, message: "拨付金额不能为空", trigger: "blur" }
        ],
        paymentMethod: [
          { required: true, message: "请选择拨付方式", trigger: "change" }
        ],
        payee: [
          { required: true, message: "请输入收款人", trigger: "blur" }
        ]
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        elderName: null,
        purpose: null,
        urgencyLevel: null,
        status: null,
        paymentStatus: null
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
        this.depositList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询统计数据 */
    getStatistics() {
      // TODO: 调用统计API
      this.statistics = {
        totalCount: 45,
        pendingCount: 8,
        approvedAmount: 125000.00,
        paidAmount: 89000.00
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$router.push('/elder/depositApply');
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      const id = row.id;
      getDepositUse(id).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },
    /** 撤回按钮操作 */
    handleCancel(row) {
      this.$confirm('是否确认撤回该申请？撤回后需要重新申请', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return cancelDepositUse(row.id);
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("撤回成功");
      }).catch(() => {});
    },
    /** 拨付按钮操作 */
    handlePayment(row) {
      this.paymentData = row;
      this.paymentForm = {
        amount: row.amount,
        paymentMethod: '',
        payee: row.elderName,
        remark: ''
      };
      this.paymentOpen = true;
    },
    /** 提交拨付 */
    submitPayment() {
      this.$refs["paymentForm"].validate(valid => {
        if (valid) {
          const paymentData = {
            id: this.paymentData.id,
            ...this.paymentForm
          };
          paymentDepositUse(paymentData).then(response => {
            this.$modal.msgSuccess("拨付成功");
            this.paymentOpen = false;
            this.getList();
            this.getStatistics();
          });
        }
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('elder/deposit/export', {
        ...this.queryParams
      }, `押金使用申请_${new Date().getTime()}.xlsx`)
    },
    /** 预览附件 */
    previewAttachment(attachment) {
      // TODO: 实现附件预览功能
      this.$message.info('附件预览功能待实现');
    },
    /** 获取状态类型 */
    getStatusType(status) {
      const typeMap = {
        '待审批': 'warning',
        '审批中': 'primary',
        '已通过': 'success',
        '已驳回': 'danger',
        '已撤回': 'info'
      };
      return typeMap[status] || 'info';
    },
    /** 格式化金额 */
    formatMoney(value) {
      if (!value) return '0.00';
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
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
  font-size: 24px;
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

.stat-icon.total {
  background: linear-gradient(135deg, #42A5F5, #66B3FF);
}

.stat-icon.pending {
  background: linear-gradient(135deg, #FF6B6B, #FF8E8E);
}

.stat-icon.approved {
  background: linear-gradient(135deg, #4CAF50, #66BB6A);
}

.stat-icon.paid {
  background: linear-gradient(135deg, #9C27B0, #BA68C8);
}

.deposit-detail {
  max-height: 600px;
  overflow-y: auto;
}

.amount-text {
  color: #F56C6C;
  font-weight: bold;
}

.amount-text.large {
  font-size: 18px;
}

.attachment-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.attachment-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.attachment-item i {
  margin-right: 8px;
  color: #909399;
}

.attachment-item a {
  color: #409EFF;
  text-decoration: none;
}

.attachment-item a:hover {
  color: #66b1ff;
  text-decoration: underline;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style>