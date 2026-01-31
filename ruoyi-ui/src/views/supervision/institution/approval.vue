<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入"
          clearable
          size="small"
          style="width: 160px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="信用代码" prop="creditCode">
        <el-input
          v-model="queryParams.creditCode"
          placeholder="请输入"
          clearable
          size="small"
          style="width: 160px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable size="small" style="width: 140px">
          <el-option label="待审批" value="0" />
          <el-option label="已入驻" value="1" />
          <el-option label="已驳回" value="2" />
          <el-option label="解除监管" value="3" />
          <el-option label="草稿" value="4" />
          <el-option label="维护中" value="5" />
          <el-option label="维护待审批" value="6" />
        </el-select>
      </el-form-item>
      <el-form-item label="申请时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 220px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始"
          end-placeholder="结束"
          size="small"
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
            <div class="stat-label">已入驻</div>
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
            <div class="stat-label">不通过</div>
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
          v-hasPermi="['pension:institution:approve']"
        >批量通过</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pension:institution:export']"
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
    <el-table v-loading="loading" :data="institutionList" @selection-change="handleSelectionChange" style="width: 100%" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="机构名称" align="center" prop="institutionName" min-width="150" show-overflow-tooltip />
      <el-table-column label="统一信用代码" align="center" prop="creditCode" width="180" />
      <el-table-column label="负责人" align="center" prop="responsibleName" width="120" />
      <el-table-column label="联系人" align="center" prop="contactPerson" width="100" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="120" />
      <el-table-column label="注册资金" align="center" prop="registeredCapital" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.registeredCapital }}万元</span>
        </template>
      </el-table-column>
      <el-table-column label="申请状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pension_institution_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="applyTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审批人" align="center" prop="approveUser" width="100" />
      <el-table-column label="审批时间" align="center" prop="approveTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.approveTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['pension:institution:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleApprove(scope.row)"
            v-if="scope.row.status === '0' || scope.row.status === '6'"
            v-hasPermi="['pension:institution:approve']"
          >通过</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleReject(scope.row)"
            v-if="scope.row.status === '0' || scope.row.status === '6'"
            v-hasPermi="['pension:institution:reject']"
          >不通过</el-button>
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

    <!-- 机构详情对话框 -->
    <el-dialog title="机构详情" :visible.sync="detailOpen" width="1200px" append-to-body>
      <!-- 基本信息 -->
      <el-descriptions :column="2" border>
        <el-descriptions-item label="机构名称">{{ currentInstitution.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="统一信用代码">{{ currentInstitution.creditCode }}</el-descriptions-item>
        <el-descriptions-item label="机构备案号">{{ currentInstitution.recordNumber }}</el-descriptions-item>
        <el-descriptions-item label="注册资金">{{ currentInstitution.registeredCapital }}万元</el-descriptions-item>
        <el-descriptions-item label="注册地址" :span="2">{{ currentInstitution.registeredAddress }}</el-descriptions-item>
        <el-descriptions-item label="所属街道/区域">{{ currentInstitution.street }}</el-descriptions-item>
        <el-descriptions-item label="机构联系人">{{ currentInstitution.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentInstitution.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="成立日期">{{ parseTime(currentInstitution.establishedDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="实际经营地址" :span="2">{{ currentInstitution.actualAddress }}</el-descriptions-item>
        <el-descriptions-item label="兴办机构">{{ currentInstitution.organizer }}</el-descriptions-item>
      </el-descriptions>

      <!-- 负责人信息 -->
      <el-divider content-position="left">负责人信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="负责人姓名">{{ currentInstitution.responsibleName }}</el-descriptions-item>
        <el-descriptions-item label="负责人身份证号">{{ currentInstitution.responsibleIdCard }}</el-descriptions-item>
        <el-descriptions-item label="负责人居住地" :span="2">{{ currentInstitution.responsibleAddress }}</el-descriptions-item>
        <el-descriptions-item label="负责人电话">{{ currentInstitution.responsiblePhone }}</el-descriptions-item>
      </el-descriptions>

      <!-- 经营信息 -->
      <el-divider content-position="left">经营信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="养老机构类型">{{ getInstitutionTypeText(currentInstitution.institutionType) }}</el-descriptions-item>
        <el-descriptions-item label="床位数">{{ currentInstitution.bedCount }}张</el-descriptions-item>
        <el-descriptions-item label="收费区间">{{ currentInstitution.feeRange }}</el-descriptions-item>
        <el-descriptions-item label="固定资产净额">{{ currentInstitution.fixedAssets }}万元</el-descriptions-item>
        <el-descriptions-item label="监管账户">{{ currentInstitution.superviseAccount }}</el-descriptions-item>
        <el-descriptions-item label="基本账户">{{ currentInstitution.bankAccount }}</el-descriptions-item>
      </el-descriptions>

      <!-- 申请状态信息 -->
      <el-divider content-position="left">申请信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请状态">
          <el-tag v-if="currentInstitution.status === '4'" type="info">草稿</el-tag>
          <el-tag v-else-if="currentInstitution.status === '0'" type="warning">待审批</el-tag>
          <el-tag v-else-if="currentInstitution.status === '1'" type="success">已入驻</el-tag>
          <el-tag v-else-if="currentInstitution.status === '2'" type="danger">已驳回</el-tag>
          <el-tag v-else-if="currentInstitution.status === '5'" type="primary">维护中</el-tag>
          <el-tag v-else-if="currentInstitution.status === '6'" type="warning">维护待审批</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ parseTime(currentInstitution.applyTime, '{y}-{m}-{d} {h}:{i}') }}</el-descriptions-item>
        <el-descriptions-item label="审批人" v-if="currentInstitution.approveUser">{{ currentInstitution.approveUser }}</el-descriptions-item>
        <el-descriptions-item label="审批时间" v-if="currentInstitution.approveTime">{{ parseTime(currentInstitution.approveTime, '{y}-{m}-{d} {h}:{i}') }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2" v-if="currentInstitution.approveRemark">{{ currentInstitution.approveRemark }}</el-descriptions-item>
      </el-descriptions>

      <!-- 上传材料 -->
      <el-divider content-position="left">上传材料</el-divider>
      <el-row :gutter="20">
        <el-col :span="8" v-if="currentInstitution.businessLicense">
          <el-card shadow="hover">
            <div slot="header">营业执照</div>
            <el-image
              :src="processImageUrl(currentInstitution.businessLicense)"
              style="width: 100%; height: 200px;"
              fit="contain"
              :preview-src-list="[processImageUrl(currentInstitution.businessLicense)]"
            ></el-image>
          </el-card>
        </el-col>
        <el-col :span="8" v-if="currentInstitution.approvalCertificate">
          <el-card shadow="hover">
            <div slot="header">社会福利机构设置批准证书</div>
            <el-image
              :src="processImageUrl(currentInstitution.approvalCertificate)"
              style="width: 100%; height: 200px;"
              fit="contain"
              :preview-src-list="[processImageUrl(currentInstitution.approvalCertificate)]"
            ></el-image>
          </el-card>
        </el-col>
        <el-col :span="8" v-if="currentInstitution.supervisionAgreement">
          <el-card shadow="hover">
            <div slot="header">三方监管协议</div>
            <el-image
              :src="processImageUrl(currentInstitution.supervisionAgreement)"
              style="width: 100%; height: 200px;"
              fit="contain"
              :preview-src-list="[processImageUrl(currentInstitution.supervisionAgreement)]"
            ></el-image>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!currentInstitution.businessLicense && !currentInstitution.approvalCertificate && !currentInstitution.supervisionAgreement" description="暂无上传材料"></el-empty>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 审批拒绝对话框 -->
    <el-dialog title="不通过" :visible.sync="rejectOpen" width="500px" append-to-body>
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
  </div>
</template>

<script>
import { listApproval, listAllInstitution, getInstitution, approveInstitution, rejectInstitution, getApprovalStatistics, batchApprove, exportApproval } from "@/api/pension/supervisionInstitution";

export default {
  name: "InstitutionApproval",
  dicts: ['pension_institution_status'],
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
      // 机构申请表格数据
      institutionList: [],
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
      // 当前机构信息
      currentInstitution: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        creditCode: null,
        status: "0" // 默认只查询待审批
      },
      // 拒绝表单
      rejectForm: {
        institutionId: null,
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
    /** 处理图片URL，移除硬编码域名并添加API前缀 */
    processImageUrl(url) {
      if (!url) return '';
      // 如果URL包含 http:// 或 https://，提取相对路径
      let cleanUrl = url;
      if (url.startsWith('http://') || url.startsWith('https://')) {
        try {
          const urlObj = new URL(url);
          cleanUrl = urlObj.pathname;
        } catch (e) {
          // 如果URL解析失败，尝试移除域名部分
          cleanUrl = url.replace(/^https?:\/\/[^\/]+/, '');
        }
      }
      // 添加API前缀，让前端代理正确转发到后端
      return process.env.VUE_APP_BASE_API + cleanUrl;
    },
    /** 查询养老机构入驻申请列表 */
    getList() {
      this.loading = true;
      this.addDateRange();

      // 根据是否显示全部选择不同的API
      const apiMethod = this.showAll ? listAllInstitution : listApproval;

      apiMethod(this.queryParams).then(response => {
        this.institutionList = response.rows;
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
      this.ids = selection.map(item => item.institutionId);
      this.multiple = !selection.length;
    },
    /** 查看详情操作 */
    handleDetail(row) {
      const institutionId = row.institutionId;
      getInstitution(institutionId).then(response => {
        this.currentInstitution = response.data;
        this.detailOpen = true;
      });
    },
    /** 审批通过操作 */
    handleApprove(row) {
      const institutionId = row.institutionId;
      const institutionName = row.institutionName;
      const isMaintenance = row.status === '6'; // 是否为维护申请

      const confirmMessage = isMaintenance
        ? `是否确认审批通过"${institutionName}"的维护申请？通过后机构信息将更新为最新内容。`
        : `是否确认审批通过"${institutionName}"的入驻申请？`;

      this.$modal.confirm(confirmMessage).then(() => {
        return approveInstitution(institutionId);
      }).then((response) => {
        this.getList();
        this.getStatistics();
        const successMsg = isMaintenance ? "维护申请审批通过成功" : "入驻申请审批通过成功";
        this.$modal.msgSuccess(response.msg || successMsg);

        // 只有入驻申请才显示生成的账号信息
        if (!isMaintenance && response.data && response.data.loginAccount) {
          this.$alert(`
            <div style="line-height: 1.8;">
              <p><strong>机构登录账号已生成：</strong></p>
              <p>登录账号：<span style="color: #409EFF; font-weight: bold;">${response.data.loginAccount}</span></p>
              <p>初始密码：<span style="color: #F56C6C; font-weight: bold;">${response.data.initialPassword || '123456'}</span></p>
              <p style="color: #67C23A; font-size: 13px; margin-top: 10px;">✓ 监管账户已自动开户</p>
              <p style="color: #909399; font-size: 12px;">请将此账号信息通知机构。</p>
            </div>
          `, '审批成功', {
            dangerouslyUseHTMLString: true,
            type: 'success',
            confirmButtonText: '我知道了'
          });
        }
      }).catch(() => {});
    },
    /** 审批拒绝操作 */
    handleReject(row) {
      this.rejectForm = {
        institutionId: row.institutionId,
        remark: ''
      };
      this.rejectOpen = true;
    },
    /** 提交拒绝 */
    submitReject() {
      this.$refs["rejectForm"].validate(valid => {
        if (valid) {
          rejectInstitution(this.rejectForm.institutionId, this.rejectForm).then(response => {
            this.getList();
            this.getStatistics();
            this.rejectOpen = false;
            this.$modal.msgSuccess("审批不通过成功");
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
      const institutionIds = this.ids;
      const institutionNames = this.institutionList.filter(item => institutionIds.includes(item.institutionId))
        .map(item => item.institutionName).join('、');

      this.$modal.confirm('是否确认批量审批通过以下机构："' + institutionNames + '"？通过后将自动生成登录账号并开通监管账户。').then(() => {
        return batchApprove(institutionIds);
      }).then(response => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess(response.msg);
      }).catch(() => {});
    },
    /** 显示全部切换 */
    handleShowAllChange(value) {
      if (value) {
        this.queryParams.status = null; // 显示全部状态
      } else {
        this.queryParams.status = "0"; // 只显示待审批
      }
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/supervision/institution/approval/export', {
        ...this.queryParams
      }, `机构入驻申请_${new Date().getTime()}.xlsx`)
    },
    /** 获取机构类型文本 */
    getInstitutionTypeText(type) {
      const typeMap = {
        '1': '民办',
        '2': '公办',
        '3': '公建民营'
      };
      return typeMap[type] || type;
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

.stat-icon.supplement {
  background: linear-gradient(135deg, #FFA726, #FFB74D);
}

.stat-icon.rejected {
  background: linear-gradient(135deg, #F56C6C, #F78989);
}

.stat-icon.total {
  background: linear-gradient(135deg, #42A5F5, #66B3FF);
}
</style>