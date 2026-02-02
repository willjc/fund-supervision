<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <i class="el-icon-s-home stats-icon" style="color: #409EFF"></i>
            <div class="stats-info">
              <div class="stats-value">{{ totalCount }}</div>
              <div class="stats-label">园区总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <i class="el-icon-time stats-icon" style="color: #E6A23C"></i>
            <div class="stats-info">
              <div class="stats-value">{{ pendingCount }}</div>
              <div class="stats-label">待审批</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <i class="el-icon-success stats-icon" style="color: #67C23A"></i>
            <div class="stats-info">
              <div class="stats-value">{{ approvedCount }}</div>
              <div class="stats-label">已入驻</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stats-card">
          <div class="stats-content">
            <i class="el-icon-error stats-icon" style="color: #F56C6C"></i>
            <div class="stats-info">
              <div class="stats-value">{{ rejectedCount }}</div>
              <div class="stats-label">已驳回</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="园区地址" prop="actualAddress">
          <el-input
            v-model="queryParams.actualAddress"
            placeholder="请输入园区地址"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="申请状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" value="-1" />
            <el-option label="待审批" value="0" />
            <el-option label="已入驻" value="1" />
            <el-option label="已驳回" value="2" />
            <el-option label="维护中" value="5" />
            <el-option label="维护待审批" value="6" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          @click="handleAdd"
        >新增园区申请</el-button>
      </el-col>
    </el-row>

    <!-- 园区列表表格 -->
    <el-table v-loading="loading" :data="campusList" border stripe>
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="机构名称" prop="institutionName" width="200" show-overflow-tooltip />
      <el-table-column label="园区地址" prop="actualAddress" min-width="250" show-overflow-tooltip>
        <template slot-scope="scope">
          <el-tag type="info" size="small" effect="plain">{{ scope.row.actualAddress || '未填写' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="机构类型" prop="institutionType" width="140" align="center">
        <template slot-scope="scope">
          {{ getInstitutionTypeText(scope.row.institutionType) }}
        </template>
      </el-table-column>
      <el-table-column label="床位数" prop="bedCount" width="100" align="center">
        <template slot-scope="scope">
          <span>{{ scope.row.bedCount }}张</span>
        </template>
      </el-table-column>
      <el-table-column label="联系人" prop="contactPerson" width="100" align="center" />
      <el-table-column label="联系电话" prop="contactPhone" width="120" align="center" />
      <el-table-column label="申请状态" prop="status" width="120" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '4' || scope.row.status === null" type="info">草稿</el-tag>
          <el-tag v-else-if="scope.row.status === '0'" type="warning">待审批</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="success">已入驻</el-tag>
          <el-tag v-else-if="scope.row.status === '2'" type="danger">已驳回</el-tag>
          <el-tag v-else-if="scope.row.status === '5'" type="primary">维护中</el-tag>
          <el-tag v-else-if="scope.row.status === '6'" type="warning">维护待审批</el-tag>
          <el-tag v-else type="info">未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" prop="applyTime" width="160" align="center">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            v-if="scope.row.status === '1' || scope.row.status === '5'"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleMaintain(scope.row)"
          >维护</el-button>
          <el-button
            v-if="scope.row.status === '4' || scope.row.status === '2' || scope.row.status === null"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleEdit(scope.row)"
          >编辑</el-button>
          <el-button
            v-if="scope.row.status === '0'"
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleWithdraw(scope.row)"
          >撤回</el-button>
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

    <!-- 园区详情对话框 -->
    <el-dialog title="园区详情" :visible.sync="detailOpen" width="1200px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="机构名称">{{ currentCampus.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="统一信用代码">{{ currentCampus.creditCode }}</el-descriptions-item>
        <el-descriptions-item label="机构备案号">{{ currentCampus.recordNumber }}</el-descriptions-item>
        <el-descriptions-item label="注册资金">{{ currentCampus.registeredCapital }}万元</el-descriptions-item>
        <el-descriptions-item label="注册地址" :span="2">{{ currentCampus.registeredAddress }}</el-descriptions-item>
        <el-descriptions-item label="所属街道/区域">{{ currentCampus.street }}</el-descriptions-item>
        <el-descriptions-item label="机构联系人">{{ currentCampus.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentCampus.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="成立日期">{{ parseTime(currentCampus.establishedDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="实际经营地址" :span="2">{{ currentCampus.actualAddress }}</el-descriptions-item>
        <el-descriptions-item label="兴办机构">{{ currentCampus.organizer }}</el-descriptions-item>
      </el-descriptions>

      <!-- 负责人信息 -->
      <el-divider content-position="left">负责人信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="负责人姓名">{{ currentCampus.responsibleName }}</el-descriptions-item>
        <el-descriptions-item label="负责人身份证号">{{ currentCampus.responsibleIdCard }}</el-descriptions-item>
        <el-descriptions-item label="负责人居住地" :span="2">{{ currentCampus.responsibleAddress }}</el-descriptions-item>
        <el-descriptions-item label="负责人电话">{{ currentCampus.responsiblePhone }}</el-descriptions-item>
      </el-descriptions>

      <!-- 经营信息 -->
      <el-divider content-position="left">经营信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="养老机构类型">{{ getInstitutionTypeText(currentCampus.institutionType) }}</el-descriptions-item>
        <el-descriptions-item label="床位数">{{ currentCampus.bedCount }}张</el-descriptions-item>
        <el-descriptions-item label="收费区间">{{ currentCampus.feeRange }}</el-descriptions-item>
        <el-descriptions-item label="固定资产净额">{{ currentCampus.fixedAssets }}万元</el-descriptions-item>
        <el-descriptions-item label="监管账户开户行">{{ currentCampus.superviseBank }}</el-descriptions-item>
        <el-descriptions-item label="监管账户">{{ currentCampus.superviseAccount }}</el-descriptions-item>
        <el-descriptions-item label="基本账户开户行">{{ currentCampus.basicBank }}</el-descriptions-item>
        <el-descriptions-item label="基本账户">{{ currentCampus.bankAccount }}</el-descriptions-item>
      </el-descriptions>

      <!-- 申请状态信息 -->
      <el-divider content-position="left">申请信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请状态">
          <el-tag v-if="currentCampus.status === '4' || currentCampus.status === null" type="info">草稿</el-tag>
          <el-tag v-else-if="currentCampus.status === '0'" type="warning">待审批</el-tag>
          <el-tag v-else-if="currentCampus.status === '1'" type="success">已入驻</el-tag>
          <el-tag v-else-if="currentCampus.status === '2'" type="danger">已驳回</el-tag>
          <el-tag v-else-if="currentCampus.status === '5'" type="primary">维护中</el-tag>
          <el-tag v-else-if="currentCampus.status === '6'" type="warning">维护待审批</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ parseTime(currentCampus.applyTime, '{y}-{m}-{d} {h}:{i}') }}</el-descriptions-item>
        <el-descriptions-item label="审批人" v-if="currentCampus.approveUser">{{ currentCampus.approveUser }}</el-descriptions-item>
        <el-descriptions-item label="审批时间" v-if="currentCampus.approveTime">{{ parseTime(currentCampus.approveTime, '{y}-{m}-{d} {h}:{i}') }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2" v-if="currentCampus.approveRemark">{{ currentCampus.approveRemark }}</el-descriptions-item>
      </el-descriptions>

      <!-- 上传材料 -->
      <el-divider content-position="left">上传材料</el-divider>
      <el-row :gutter="20">
        <el-col :span="8" v-if="currentCampus.businessLicense">
          <el-card shadow="hover">
            <div slot="header">营业执照</div>
            <el-image
              :src="processImageUrl(currentCampus.businessLicense)"
              style="width: 100%; height: 200px;"
              fit="contain"
              :preview-src-list="[processImageUrl(currentCampus.businessLicense)]"
            ></el-image>
          </el-card>
        </el-col>
        <el-col :span="8" v-if="currentCampus.approvalCertificate">
          <el-card shadow="hover">
            <div slot="header">社会福利机构设置批准证书</div>
            <el-image
              :src="processImageUrl(currentCampus.approvalCertificate)"
              style="width: 100%; height: 200px;"
              fit="contain"
              :preview-src-list="[processImageUrl(currentCampus.approvalCertificate)]"
            ></el-image>
          </el-card>
        </el-col>
        <el-col :span="8" v-if="currentCampus.supervisionAgreement">
          <el-card shadow="hover">
            <div slot="header">三方监管协议</div>
            <el-image
              :src="processImageUrl(currentCampus.supervisionAgreement)"
              style="width: 100%; height: 200px;"
              fit="contain"
              :preview-src-list="[processImageUrl(currentCampus.supervisionAgreement)]"
            ></el-image>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="!currentCampus.businessLicense && !currentCampus.approvalCertificate && !currentCampus.supervisionAgreement" description="暂无上传材料"></el-empty>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listPensionInstitution, getPensionInstitution, updatePensionInstitution, delPensionInstitution } from "@/api/pension/institution";

export default {
  name: "InstitutionApplyList",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 园区列表
      campusList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      detailOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        actualAddress: null,
        status: null
      },
      // 当前园区
      currentCampus: {}
    };
  },
  computed: {
    // 园区总数
    totalCount() {
      return this.campusList.length;
    },
    // 待审批数量
    pendingCount() {
      return this.campusList.filter(item => item.status === '0').length;
    },
    // 已入驻数量
    approvedCount() {
      return this.campusList.filter(item => item.status === '1').length;
    },
    // 已驳回数量
    rejectedCount() {
      return this.campusList.filter(item => item.status === '2').length;
    }
  },
  created() {
    this.getList();
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
    /** 查询园区列表 */
    getList() {
      this.loading = true;
      listPensionInstitution(this.queryParams).then(response => {
        this.campusList = response.rows;
        this.total = response.total;
        this.loading = false;
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
    /** 新增按钮操作 */
    handleAdd() {
      this.$router.push('/pension/institution/apply');
    },
    /** 查看详情 */
    handleDetail(row) {
      this.currentCampus = { ...row };
      this.detailOpen = true;
    },
    /** 编辑按钮操作 */
    handleEdit(row) {
      this.$router.push({
        path: '/pension/institution/apply',
        query: { id: row.institutionId }
      });
    },
    /** 维护信息 */
    handleMaintain(row) {
      // 跳转到apply.vue页面进行完整编辑
      this.$router.push({
        path: '/pension/institution/apply',
        query: { id: row.institutionId }
      });
    },
    /** 撤回申请 */
    handleWithdraw(row) {
      this.$confirm('是否确认撤回该园区的入驻申请？撤回后可以重新提交。', "撤回申请", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        // 将状态改为草稿
        const data = { ...row, status: '4' };
        updatePensionInstitution(data).then(() => {
          this.$modal.msgSuccess("申请已撤回");
          this.getList();
        });
      });
    },
    /** 获取机构类型文本 */
    getInstitutionTypeText(type) {
      const typeMap = {
        '1': '民办机构',
        '2': '公办机构',
        '3': '公建民营',
        'nursing_home': '养老院',
        'service_center': '养老服务中心',
        'day_care': '日间照料中心',
        'senior_apartment': '养老公寓',
        'other': '其他'
      };
      return typeMap[type] || type;
    }
  }
};
</script>

<style scoped>
.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stats-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.stats-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stats-icon {
  font-size: 48px;
  margin-right: 20px;
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.search-card {
  margin-bottom: 20px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>
