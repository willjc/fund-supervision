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
      <el-form-item label="统一信用代码" prop="creditCode">
        <el-input
          v-model="queryParams.creditCode"
          placeholder="请输入统一信用代码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="机构状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择机构状态" clearable>
          <el-option label="正常运营" value="1" />
          <el-option label="停业整顿" value="2" />
          <el-option label="已注销" value="3" />
          <el-option label="预警监控" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="监管账户" prop="hasSupervisionAccount">
        <el-select v-model="queryParams.hasSupervisionAccount" placeholder="监管账户状态" clearable>
          <el-option label="已开户" value="true" />
          <el-option label="未开户" value="false" />
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
            <div class="stat-number">{{ statistics.totalInstitutions || 0 }}</div>
            <div class="stat-label">总机构数</div>
            <div class="stat-icon total">
              <i class="el-icon-office-building"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.normalCount || 0 }}</div>
            <div class="stat-label">正常运营</div>
            <div class="stat-icon normal">
              <i class="el-icon-success"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.warningCount || 0 }}</div>
            <div class="stat-label">预警监控</div>
            <div class="stat-icon warning">
              <i class="el-icon-warning"></i>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.disabledCount || 0 }}</div>
            <div class="stat-label">停业整顿</div>
            <div class="stat-icon disabled">
              <i class="el-icon-remove"></i>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-s-custom"
          size="mini"
          :disabled="multiple"
          @click="handleAddToBlacklist"
          v-hasPermi="['pension:institution:blacklist']"
        >移入黑名单</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-warning-outline"
          size="mini"
          @click="handleWarningInstitutions"
        >预警机构</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pension:institution:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="institutionList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="机构名称" align="center" prop="institutionName" min-width="150" show-overflow-tooltip />
      <el-table-column label="统一信用代码" align="center" prop="creditCode" width="180" />
      <el-table-column label="预收服务费" align="center" width="120">
        <template slot-scope="scope">
          <span style="color: #409EFF; font-weight: bold;">¥{{ formatMoney(scope.row.serviceFeeBalance || 0) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预收押金" align="center" width="120">
        <template slot-scope="scope">
          <span style="color: #67C23A; font-weight: bold;">¥{{ formatMoney(scope.row.depositBalance || 0) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预收会员费" align="center" width="120">
        <template slot-scope="scope">
          <span style="color: #E6A23C; font-weight: bold;">¥{{ formatMoney(scope.row.memberFeeBalance || 0) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="监管账户" align="center" width="130">
        <template slot-scope="scope">
          <div v-if="scope.row.hasSupervisionAccount" style="color: #67C23A;">
            <i class="el-icon-check"></i> 已开户
          </div>
          <div v-else style="color: #F56C6C;">
            <i class="el-icon-close"></i> 未开户
          </div>
        </template>
      </el-table-column>
      <el-table-column label="入驻状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.pension_institution_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="床位使用率" align="center" width="120">
        <template slot-scope="scope">
          <el-progress
            :percentage="getOccupancyRate(scope.row)"
            :color="getOccupancyColor(getOccupancyRate(scope.row))"
            :stroke-width="6"
            :show-text="false"
            style="margin-bottom: 4px;"
          ></el-progress>
          <span style="font-size: 12px;">{{ getOccupancyRate(scope.row) }}%</span>
        </template>
      </el-table-column>
      <el-table-column label="联系人" align="center" prop="contactPerson" width="100" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="120" />
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
            icon="el-icon-edit"
            @click="handleEdit(scope.row)"
            v-hasPermi="['pension:institution:edit']"
          >编辑</el-button>
          <el-dropdown @command="(command) => handleCommand(command, scope.row)" style="margin-left: 5px;">
            <el-button size="mini" type="text">
              更多<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="freeze" v-if="scope.row.status === '1'" v-hasPermi="['pension:institution:freeze']">冻结机构</el-dropdown-item>
              <el-dropdown-item command="unfreeze" v-if="scope.row.status === '2'" v-hasPermi="['pension:institution:unfreeze']">解除冻结</el-dropdown-item>
              <el-dropdown-item command="blacklist" v-if="scope.row.status !== '5'" v-hasPermi="['pension:institution:blacklist']">移入黑名单</el-dropdown-item>
              <el-dropdown-item command="warning" v-hasPermi="['pension:institution:warning']">预警提醒</el-dropdown-item>
              <el-dropdown-item command="account" v-hasPermi="['pension:institution:account']">账户查看</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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
    <el-dialog title="机构详情" :visible.sync="detailOpen" width="1000px" append-to-body>
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="机构名称">{{ currentInstitution.institutionName }}</el-descriptions-item>
            <el-descriptions-item label="统一信用代码">{{ currentInstitution.creditCode }}</el-descriptions-item>
            <el-descriptions-item label="法定代表人">{{ currentInstitution.legalPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ currentInstitution.contactPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentInstitution.contactPhone }}</el-descriptions-item>
            <el-descriptions-item label="注册资金">{{ currentInstitution.registeredCapital }}万元</el-descriptions-item>
            <el-descriptions-item label="注册地址" :span="2">{{ currentInstitution.registeredAddress }}</el-descriptions-item>
            <el-descriptions-item label="实际经营地址" :span="2">{{ currentInstitution.actualAddress }}</el-descriptions-item>
            <el-descriptions-item label="机构状态">
              <dict-tag :options="dict.type.pension_institution_status" :value="currentInstitution.status"/>
            </el-descriptions-item>
            <el-descriptions-item label="机构评级">
              <el-rate
                v-model="currentInstitution.rating"
                disabled
                show-score
                text-color="#ff9900"
                :max="5"
              ></el-rate>
            </el-descriptions-item>
            <el-descriptions-item label="入驻时间">{{ parseTime(currentInstitution.registerTime) }}</el-descriptions-item>
            <el-descriptions-item label="监管账户">
              <span v-if="currentInstitution.hasSupervisionAccount" style="color: #67C23A;">
                <i class="el-icon-check"></i> 已开户
              </span>
              <span v-else style="color: #F56C6C;">
                <i class="el-icon-close"></i> 未开户
              </span>
            </el-descriptions-item>
            <el-descriptions-item label="床位数">{{ currentInstitution.approvedBeds }}张</el-descriptions-item>
            <el-descriptions-item label="入住老人数">{{ currentInstitution.actualElders }}人</el-descriptions-item>
            <el-descriptions-item label="床位使用率">
              <el-progress
                :percentage="getOccupancyRate(currentInstitution)"
                :color="getOccupancyColor(getOccupancyRate(currentInstitution))"
                :stroke-width="10"
                style="width: 200px;"
              ></el-progress>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- 资金信息 -->
        <el-tab-pane label="资金信息" name="financial">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-card shadow="hover" class="financial-card">
                <div class="financial-content">
                  <div class="financial-icon service">
                    <i class="el-icon-coin"></i>
                  </div>
                  <div class="financial-info">
                    <div class="financial-title">预收服务费</div>
                    <div class="financial-amount">¥{{ formatMoney(currentInstitution.serviceFeeBalance || 0) }}</div>
                    <div class="financial-desc">用于服务费用的预收资金</div>
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
                    <div class="financial-amount">¥{{ formatMoney(currentInstitution.depositBalance || 0) }}</div>
                    <div class="financial-desc">老人入住押金监管资金</div>
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
                    <div class="financial-amount">¥{{ formatMoney(currentInstitution.memberFeeBalance || 0) }}</div>
                    <div class="financial-desc">VIP会员费用预收资金</div>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-descriptions :column="1" border style="margin-top: 20px;">
            <el-descriptions-item label="监管账户总余额">
              <span style="color: #409EFF; font-size: 18px; font-weight: bold;">
                ¥{{ formatMoney((currentInstitution.serviceFeeBalance || 0) + (currentInstitution.depositBalance || 0) + (currentInstitution.memberFeeBalance || 0)) }}
              </span>
            </el-descriptions-item>
            <el-descriptions-item label="开户银行">{{ currentInstitution.supervisionBank || '暂无信息' }}</el-descriptions-item>
            <el-descriptions-item label="监管账号">{{ currentInstitution.supervisionAccount || '暂无信息' }}</el-descriptions-item>
            <el-descriptions-item label="账户状态">
              <el-tag v-if="currentInstitution.hasSupervisionAccount" type="success">正常监管</el-tag>
              <el-tag v-else type="danger">未开户</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>

        <!-- 业务信息 -->
        <el-tab-pane label="业务信息" name="business">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="业务范围">{{ currentInstitution.businessScope || '暂无信息' }}</el-descriptions-item>
            <el-descriptions-item label="服务项目">{{ currentInstitution.serviceItems || '暂无信息' }}</el-descriptions-item>
            <el-descriptions-item label="收费标准">{{ currentInstitution.feeStandard || '暂无信息' }}</el-descriptions-item>
            <el-descriptions-item label="医护配置">{{ currentInstitution.medicalStaff || '暂无信息' }}</el-descriptions-item>
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
        <el-button type="primary" @click="handleEdit(currentInstitution)" v-hasPermi="['pension:institution:edit']">编辑信息</el-button>
      </div>
    </el-dialog>

    <!-- 编辑机构对话框 -->
    <el-dialog title="编辑机构信息" :visible.sync="editOpen" width="600px" append-to-body>
      <el-form ref="editForm" :model="editForm" :rules="editRules" label-width="120px">
        <el-form-item label="机构名称" prop="institutionName">
          <el-input v-model="editForm.institutionName" placeholder="请输入机构名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="editForm.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="editForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="实际经营地址" prop="actualAddress">
          <el-input v-model="editForm.actualAddress" type="textarea" placeholder="请输入实际经营地址" />
        </el-form-item>
        <el-form-item label="床位数" prop="approvedBeds">
          <el-input-number v-model="editForm.approvedBeds" :min="1" />
        </el-form-item>
        <el-form-item label="机构评级" prop="rating">
          <el-rate v-model="editForm.rating" :max="5" show-text></el-rate>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitEdit">确 定</el-button>
        <el-button @click="cancelEdit">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInstitution, getInstitution, updateInstitution, getInstitutionStatistics } from "@/api/supervision/institution";
import { listAttachment } from "@/api/pension/institution";

export default {
  name: "InstitutionQueryList",
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
      // 机构表格数据
      institutionList: [],
      // 统计数据
      statistics: {},
      // 弹出层标题
      title: "",
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示编辑弹出层
      editOpen: false,
      // 当前标签页
      activeTab: 'basic',
      // 当前机构信息
      currentInstitution: {},
      // 附件列表
      attachmentList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        creditCode: null,
        status: null,
        hasSupervisionAccount: null
      },
      // 编辑表单
      editForm: {},
      // 编辑表单校验
      editRules: {
        institutionName: [
          { required: true, message: "机构名称不能为空", trigger: "blur" }
        ],
        contactPerson: [
          { required: true, message: "联系人不能为空", trigger: "blur" }
        ],
        contactPhone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getStatistics();
  },
  methods: {
    /** 查询机构信息列表 */
    getList() {
      this.loading = true;
      listInstitution(this.queryParams).then(response => {
        this.institutionList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询统计数据 */
    getStatistics() {
      getInstitutionStatistics().then(response => {
        this.statistics = response.data;
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
      this.ids = selection.map(item => item.institutionId);
      this.multiple = !selection.length;
    },
    /** 查看详情操作 */
    handleDetail(row) {
      const institutionId = row.institutionId;
      getInstitution(institutionId).then(response => {
        this.currentInstitution = response.data;
        this.detailOpen = true;
        this.activeTab = 'basic';

        // 加载附件列表
        this.loadAttachments(institutionId);
      });
    },
    /** 加载附件列表 */
    loadAttachments(institutionId) {
      listAttachment({ institutionId: institutionId }).then(response => {
        this.attachmentList = response.rows;
      });
    },
    /** 编辑操作 */
    handleEdit(row) {
      const institutionId = row.institutionId;
      getInstitution(institutionId).then(response => {
        this.editForm = response.data;
        this.editOpen = true;
        this.detailOpen = false;
      });
    },
    /** 提交编辑 */
    submitEdit() {
      this.$refs["editForm"].validate(valid => {
        if (valid) {
          updateInstitution(this.editForm).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.editOpen = false;
            this.getList();
            this.getStatistics();
          });
        }
      });
    },
    /** 取消编辑 */
    cancelEdit() {
      this.editOpen = false;
      this.resetForm("editForm");
    },
    /** 下拉菜单命令 */
    handleCommand(command, row) {
      switch (command) {
        case 'freeze':
          this.handleFreeze(row);
          break;
        case 'unfreeze':
          this.handleUnfreeze(row);
          break;
        case 'blacklist':
          this.handleAddToBlacklistSingle(row);
          break;
        case 'warning':
          this.handleWarning(row);
          break;
        case 'account':
          this.handleViewAccount(row);
          break;
      }
    },
    /** 冻结机构 */
    handleFreeze(row) {
      this.$modal.confirm('是否确认冻结"' + row.institutionName + '"？').then(() => {
        return updateInstitution({ ...row, status: '2' });
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("冻结成功");
      }).catch(() => {});
    },
    /** 解除冻结 */
    handleUnfreeze(row) {
      this.$modal.confirm('是否确认解除冻结"' + row.institutionName + '"？').then(() => {
        return updateInstitution({ ...row, status: '1' });
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("解除冻结成功");
      }).catch(() => {});
    },
    /** 预警提醒 */
    handleWarning(row) {
      this.$modal.notify({
        title: '预警提醒',
        message: `已向"${row.institutionName}"发送预警提醒通知`,
        type: 'warning'
      });
    },
    /** 查看账户 */
    handleViewAccount(row) {
      this.$modal.msgInfo("账户查看功能开发中...");
    },
    /** 批量移入黑名单 */
    handleAddToBlacklist() {
      const institutionIds = this.ids;
      const institutionNames = this.institutionList.filter(item => institutionIds.includes(item.institutionId))
        .map(item => item.institutionName).join('、');

      this.$prompt('请输入移入黑名单的原因', '批量移入黑名单', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请输入黑名单原因...',
        inputValidator: (value) => {
          if (!value) {
            return '黑名单原因不能为空';
          }
          return true;
        }
      }).then(({ value }) => {
        return this.addToBlacklist(institutionIds, value);
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("移入黑名单成功");
      }).catch(() => {});
    },
    /** 单个移入黑名单 */
    handleAddToBlacklistSingle(row) {
      this.$prompt('请输入将"' + row.institutionName + '"移入黑名单的原因', '移入黑名单', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'textarea',
        inputPlaceholder: '请输入黑名单原因...',
        inputValidator: (value) => {
          if (!value) {
            return '黑名单原因不能为空';
          }
          return true;
        }
      }).then(({ value }) => {
        return this.addToBlacklist([row.institutionId], value);
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("移入黑名单成功");
      }).catch(() => {});
    },
    /** 移入黑名单通用方法 */
    addToBlacklist(institutionIds, reason) {
      // 这里应该调用黑名单API，暂时模拟
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve();
        }, 1000);
      });
    },
    /** 预警机构列表 */
    handleWarningInstitutions() {
      this.queryParams.status = '4';
      this.handleQuery();
    },
    /** 查看附件 */
    handleViewAttachment(row) {
      this.$modal.msgInfo("附件查看功能开发中...");
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supervision/institution/export', {
        ...this.queryParams
      }, `机构信息查询_${new Date().getTime()}.xlsx`)
    },
    /** 计算床位使用率 */
    getOccupancyRate(row) {
      if (!row.approvedBeds || row.approvedBeds === 0) return 0;
      const rate = Math.round((row.actualElders || 0) / row.approvedBeds * 100);
      return Math.min(rate, 100);
    },
    /** 获取床位使用率颜色 */
    getOccupancyColor(rate) {
      if (rate >= 95) return '#F56C6C';      // 红色 - 满员或超员
      else if (rate >= 80) return '#E6A23C'; // 橙色 - 高使用率
      else if (rate >= 50) return '#409EFF'; // 蓝色 - 中等使用率
      else return '#67C23A';                 // 绿色 - 低使用率
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

.stat-icon.total {
  background: linear-gradient(135deg, #42A5F5, #66B3FF);
}

.stat-icon.normal {
  background: linear-gradient(135deg, #4CAF50, #66BB6A);
}

.stat-icon.warning {
  background: linear-gradient(135deg, #FFA726, #FFB74D);
}

.stat-icon.disabled {
  background: linear-gradient(135deg, #F56C6C, #F78989);
}

/* 资金信息卡片样式 */
.financial-card {
  height: 120px;
}

.financial-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 20px;
}

.financial-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  margin-right: 20px;
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
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.financial-amount {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.financial-desc {
  font-size: 12px;
  color: #C0C4CC;
}
</style>