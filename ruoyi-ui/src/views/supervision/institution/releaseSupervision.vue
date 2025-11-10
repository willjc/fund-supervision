<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请状态" prop="applyStatus">
        <el-select v-model="queryParams.applyStatus" placeholder="请选择申请状态" clearable>
          <el-option label="待审批" value="0" />
          <el-option label="已批准" value="1" />
          <el-option label="已驳回" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="申请时间">
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
            <div class="stat-label">已驳回</div>
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
              <i class="el-icon-document"></i>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="multiple"
          @click="handleBatchApprove"
          v-hasPermi="['supervision:release:approve']"
        >批量批准</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supervision:release:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="releaseList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="申请编号" align="center" prop="applyNo" width="160" />
      <el-table-column label="机构名称" align="center" prop="institutionName" min-width="150" show-overflow-tooltip />
      <el-table-column label="统一信用代码" align="center" prop="creditCode" width="180" />
      <el-table-column label="监管资金余额" align="center" width="140">
        <template slot-scope="scope">
          <span style="color: #F56C6C; font-weight: bold; font-size: 14px;">
            ¥{{ formatMoney(scope.row.supervisionBalance || 0) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="解除原因" align="center" prop="releaseReason" min-width="150" show-overflow-tooltip />
      <el-table-column label="申请状态" align="center" prop="applyStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.applyStatus === '0'" type="warning">待审批</el-tag>
          <el-tag v-else-if="scope.row.applyStatus === '1'" type="success">已批准</el-tag>
          <el-tag v-else-if="scope.row.applyStatus === '2'" type="danger">已驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="applyTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审批时间" align="center" prop="approveTime" width="160">
        <template slot-scope="scope">
          <span v-if="scope.row.approveTime">{{ parseTime(scope.row.approveTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['supervision:release:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleApprove(scope.row)"
            v-if="scope.row.applyStatus === '0'"
            v-hasPermi="['supervision:release:approve']"
          >批准</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleReject(scope.row)"
            v-if="scope.row.applyStatus === '0'"
            v-hasPermi="['supervision:release:reject']"
          >驳回</el-button>
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

    <!-- 申请详情对话框 -->
    <el-dialog title="解除监管申请详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="申请编号">{{ currentApplication.applyNo }}</el-descriptions-item>
            <el-descriptions-item label="机构名称">{{ currentApplication.institutionName }}</el-descriptions-item>
            <el-descriptions-item label="统一信用代码">{{ currentApplication.creditCode }}</el-descriptions-item>
            <el-descriptions-item label="法定代表人">{{ currentApplication.legalPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ currentApplication.contactPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentApplication.contactPhone }}</el-descriptions-item>
            <el-descriptions-item label="申请时间">{{ parseTime(currentApplication.applyTime) }}</el-descriptions-item>
            <el-descriptions-item label="申请状态">
              <el-tag v-if="currentApplication.applyStatus === '0'" type="warning">待审批</el-tag>
              <el-tag v-else-if="currentApplication.applyStatus === '1'" type="success">已批准</el-tag>
              <el-tag v-else-if="currentApplication.applyStatus === '2'" type="danger">已驳回</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="解除原因" :span="2">{{ currentApplication.releaseReason }}</el-descriptions-item>
            <el-descriptions-item label="审批意见" :span="2" v-if="currentApplication.approveRemark">
              {{ currentApplication.approveRemark }}
            </el-descriptions-item>
            <el-descriptions-item label="审批时间" v-if="currentApplication.approveTime">
              {{ parseTime(currentApplication.approveTime) }}
            </el-descriptions-item>
            <el-descriptions-item label="审批人" v-if="currentApplication.approver">
              {{ currentApplication.approver }}
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- 资金信息 -->
        <el-tab-pane label="资金信息" name="financial">
          <el-alert
            title="解除监管后，以下资金将从监管账户划拨至机构基本账户"
            type="warning"
            :closable="false"
            style="margin-bottom: 20px;">
          </el-alert>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-card shadow="hover" class="financial-card">
                <div class="financial-content">
                  <div class="financial-icon service">
                    <i class="el-icon-coin"></i>
                  </div>
                  <div class="financial-info">
                    <div class="financial-title">预收服务费</div>
                    <div class="financial-amount">¥{{ formatMoney(currentApplication.serviceFeeBalance || 0) }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="financial-card">
                <div class="financial-content">
                  <div class="financial-icon deposit">
                    <i class="el-icon-lock"></i>
                  </div>
                  <div class="financial-info">
                    <div class="financial-title">预收押金</div>
                    <div class="financial-amount">¥{{ formatMoney(currentApplication.depositBalance || 0) }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="8">
              <el-card shadow="hover" class="financial-card">
                <div class="financial-content">
                  <div class="financial-icon member">
                    <i class="el-icon-star-on"></i>
                  </div>
                  <div class="financial-info">
                    <div class="financial-title">预收会员费</div>
                    <div class="financial-amount">¥{{ formatMoney(currentApplication.memberFeeBalance || 0) }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-descriptions :column="1" border style="margin-top: 20px;">
            <el-descriptions-item label="监管账户总余额">
              <span style="color: #F56C6C; font-size: 20px; font-weight: bold;">
                ¥{{ formatMoney(currentApplication.supervisionBalance || 0) }}
              </span>
            </el-descriptions-item>
            <el-descriptions-item label="监管开户银行">{{ currentApplication.supervisionBank || '暂无信息' }}</el-descriptions-item>
            <el-descriptions-item label="监管账号">{{ currentApplication.supervisionAccount || '暂无信息' }}</el-descriptions-item>
            <el-descriptions-item label="基本账户银行">{{ currentApplication.basicBank || '暂无信息' }}</el-descriptions-item>
            <el-descriptions-item label="基本账号">{{ currentApplication.basicAccount || '暂无信息' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- 附件材料 -->
        <el-tab-pane label="附件材料" name="attachments">
          <el-table :data="attachmentList" border>
            <el-table-column label="附件类型" prop="attachType" width="120" />
            <el-table-column label="文件名称" prop="fileName" min-width="200" />
            <el-table-column label="上传时间" prop="uploadTime" width="160">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.uploadTime, '{y}-{m}-{d} {h}:{i}') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-view"
                  @click="handleViewAttachment(scope.row)"
                >查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
        <el-button
          type="success"
          @click="handleApprove(currentApplication)"
          v-if="currentApplication.applyStatus === '0'"
          v-hasPermi="['supervision:release:approve']"
        >批准</el-button>
        <el-button
          type="danger"
          @click="handleReject(currentApplication)"
          v-if="currentApplication.applyStatus === '0'"
          v-hasPermi="['supervision:release:reject']"
        >驳回</el-button>
      </div>
    </el-dialog>

    <!-- 批准对话框 -->
    <el-dialog title="批准解除监管" :visible.sync="approveOpen" width="600px" append-to-body>
      <el-alert
        title="重要提示"
        type="error"
        description="批准后将解除对该机构的监管，监管账户中的资金将划拨至机构基本账户，此操作不可撤销！"
        :closable="false"
        show-icon
        style="margin-bottom: 20px;">
      </el-alert>

      <el-form ref="approveForm" :model="approveForm" :rules="approveRules" label-width="120px">
        <el-form-item label="机构名称">
          <el-input v-model="approveForm.institutionName" :disabled="true" />
        </el-form-item>
        <el-form-item label="监管资金总额">
          <el-input :value="'¥' + formatMoney(approveForm.supervisionBalance || 0)" :disabled="true" />
        </el-form-item>
        <el-form-item label="划拨目标账户">
          <el-input :value="approveForm.basicAccount" :disabled="true" />
        </el-form-item>
        <el-form-item label="批准意见" prop="approveRemark">
          <el-input v-model="approveForm.approveRemark" type="textarea" :rows="4" placeholder="请输入批准意见" />
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button type="success" @click="submitApprove" :loading="approveLoading">确认批准</el-button>
        <el-button @click="approveOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 驳回对话框 -->
    <el-dialog title="驳回申请" :visible.sync="rejectOpen" width="500px" append-to-body>
      <el-form ref="rejectForm" :model="rejectForm" :rules="rejectRules" label-width="80px">
        <el-form-item label="驳回原因" prop="rejectReason">
          <el-input v-model="rejectForm.rejectReason" type="textarea" :rows="4" placeholder="请输入驳回原因" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="danger" @click="submitReject">确认驳回</el-button>
        <el-button @click="rejectOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listReleaseSupervision, getReleaseSupervision, approveRelease, rejectRelease, getReleaseStatistics } from "@/api/supervision/institution";
import { listAttachment } from "@/api/pension/institution";

export default {
  name: "ReleaseSupervision",
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
      // 解除监管申请列表
      releaseList: [],
      // 统计数据
      statistics: {},
      // 日期范围
      dateRange: [],
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示批准弹出层
      approveOpen: false,
      // 是否显示驳回弹出层
      rejectOpen: false,
      // 批准加载状态
      approveLoading: false,
      // 当前标签页
      activeTab: 'basic',
      // 当前申请信息
      currentApplication: {},
      // 附件列表
      attachmentList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        applyStatus: null
      },
      // 批准表单
      approveForm: {},
      // 批准表单校验
      approveRules: {
        approveRemark: [
          { required: true, message: "批准意见不能为空", trigger: "blur" }
        ]
      },
      // 驳回表单
      rejectForm: {},
      // 驳回表单校验
      rejectRules: {
        rejectReason: [
          { required: true, message: "驳回原因不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getStatistics();
  },
  methods: {
    /** 查询解除监管申请列表 */
    getList() {
      this.loading = true;
      this.addDateRange();

      listReleaseSupervision(this.queryParams).then(response => {
        this.releaseList = response.rows;
        this.total = response.total;
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    /** 查询统计数据 */
    getStatistics() {
      getReleaseStatistics().then(response => {
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
      this.ids = selection.map(item => item.releaseId);
      this.multiple = !selection.length;
    },
    /** 查看详情操作 */
    handleDetail(row) {
      const releaseId = row.releaseId;
      getReleaseSupervision(releaseId).then(response => {
        this.currentApplication = response.data;
        this.detailOpen = true;
        this.activeTab = 'basic';

        // 加载附件列表
        this.loadAttachments(releaseId);
      });
    },
    /** 加载附件列表 */
    loadAttachments(releaseId) {
      listAttachment({ releaseId: releaseId }).then(response => {
        this.attachmentList = response.rows;
      });
    },
    /** 批准操作 */
    handleApprove(row) {
      this.approveForm = {
        releaseId: row.releaseId,
        institutionName: row.institutionName,
        supervisionBalance: row.supervisionBalance,
        basicAccount: row.basicAccount,
        approveRemark: ''
      };
      this.approveOpen = true;
      this.detailOpen = false;
    },
    /** 提交批准 */
    submitApprove() {
      this.$refs["approveForm"].validate(valid => {
        if (valid) {
          this.approveLoading = true;
          approveRelease(this.approveForm.releaseId, this.approveForm).then(response => {
            this.getList();
            this.getStatistics();
            this.approveOpen = false;
            this.approveLoading = false;
            this.$modal.msgSuccess("批准成功，系统已通知银行划拨监管资金至机构基本账户");
          }).catch(() => {
            this.approveLoading = false;
          });
        }
      });
    },
    /** 驳回操作 */
    handleReject(row) {
      this.rejectForm = {
        releaseId: row.releaseId,
        rejectReason: ''
      };
      this.rejectOpen = true;
      this.detailOpen = false;
    },
    /** 提交驳回 */
    submitReject() {
      this.$refs["rejectForm"].validate(valid => {
        if (valid) {
          rejectRelease(this.rejectForm.releaseId, this.rejectForm).then(response => {
            this.getList();
            this.getStatistics();
            this.rejectOpen = false;
            this.$modal.msgSuccess("驳回成功，已通知机构");
          });
        }
      });
    },
    /** 批量批准操作 */
    handleBatchApprove() {
      const releaseIds = this.ids;
      const institutionNames = this.releaseList.filter(item => releaseIds.includes(item.releaseId))
        .map(item => item.institutionName).join('、');

      this.$modal.confirm('是否确认批准以下机构的解除监管申请："' + institutionNames + '"？批准后将划拨监管资金至机构基本账户。').then(() => {
        // TODO: 实现批量批准API
        this.$modal.msgSuccess("批量批准成功");
        this.getList();
        this.getStatistics();
      }).catch(() => {});
    },
    /** 查看附件 */
    handleViewAttachment(row) {
      this.$modal.msgInfo("附件查看功能开发中...");
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supervision/release/export', {
        ...this.queryParams
      }, `解除监管申请_${new Date().getTime()}.xlsx`)
    },
    /** 格式化金额显示 */
    formatMoney(amount) {
      if (!amount) return '0.00';
      return Number(amount).toLocaleString('zh-CN', {
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
  background: linear-gradient(135deg, #F56C6C, #F78989);
}

.stat-icon.total {
  background: linear-gradient(135deg, #42A5F5, #66B3FF);
}

/* 资金信息卡片样式 */
.financial-card {
  height: 100px;
}

.financial-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 15px;
}

.financial-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  margin-right: 15px;
  flex-shrink: 0;
}

.financial-icon.service {
  background: linear-gradient(135deg, #409EFF, #66B3FF);
}

.financial-icon.deposit {
  background: linear-gradient(135deg, #67C23A, #85CE61);
}

.financial-icon.member {
  background: linear-gradient(135deg, #E6A23C, #EEBE77);
}

.financial-info {
  flex: 1;
}

.financial-title {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

.financial-amount {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}
</style>