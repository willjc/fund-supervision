<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="老人姓名" prop="elderName">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入老人姓名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账户状态" prop="accountStatus">
        <el-select v-model="queryParams.accountStatus" placeholder="请选择账户状态" clearable size="small">
          <el-option label="正常" value="1" />
          <el-option label="冻结" value="0" />
          <el-option label="销户" value="2" />
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
          type="success"
          plain
          icon="el-icon-money"
          size="mini"
          @click="handleBalanceSummary"
        >余额统计</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-warning"
          size="mini"
          @click="handleLowBalance"
        >余额预警</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="accountInfoList">
      <el-table-column label="账户编号" align="center" prop="accountNo" width="150" />
      <el-table-column label="老人姓名" align="center" prop="elderName" />
      <el-table-column label="身份证号" align="center" prop="idCard" width="180" />
      <el-table-column label="机构名称" align="center" prop="institutionName" />
      <el-table-column label="总余额" align="center" prop="totalBalance">
        <template slot-scope="scope">
          <span class="text-primary">¥{{ scope.row.totalBalance }}</span>
        </template>
      </el-table-column>
      <el-table-column label="服务费余额" align="center" prop="serviceBalance">
        <template slot-scope="scope">
          <span>¥{{ scope.row.serviceBalance }}</span>
        </template>
      </el-table-column>
      <el-table-column label="押金余额" align="center" prop="depositBalance">
        <template slot-scope="scope">
          <span>¥{{ scope.row.depositBalance }}</span>
        </template>
      </el-table-column>
      <el-table-column label="会员费余额" align="center" prop="memberBalance">
        <template slot-scope="scope">
          <span>¥{{ scope.row.memberBalance }}</span>
        </template>
      </el-table-column>
      <el-table-column label="账户状态" align="center" prop="accountStatus">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.account_status" :value="scope.row.accountStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['pension:account:query']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-if="scope.row.accountStatus === '1'"
            v-hasPermi="['pension:account:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-lock"
            @click="handleFreeze(scope.row)"
            v-if="scope.row.accountStatus === '1'"
            v-hasPermi="['pension:account:edit']"
          >冻结</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-unlock"
            @click="handleUnfreeze(scope.row)"
            v-if="scope.row.accountStatus === '0'"
            v-hasPermi="['pension:account:edit']"
          >解冻</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleClose(scope.row)"
            v-if="scope.row.accountStatus === '1'"
            v-hasPermi="['pension:account:remove']"
          >销户</el-button>
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

    <!-- 查看账户详情对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="账户编号">
              <el-input v-model="form.accountNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账户状态">
              <el-select v-model="form.accountStatus" placeholder="请选择账户状态" :disabled="!isEdit">
                <el-option label="正常" value="1" />
                <el-option label="冻结" value="0" />
                <el-option label="销户" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="老人姓名">
              <el-input v-model="form.elderName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号">
              <el-input v-model="form.idCard" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="机构名称">
              <el-input v-model="form.institutionName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账户名称">
              <el-input v-model="form.accountName" :disabled="!isEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="总余额">
              <el-input v-model="form.totalBalance" disabled>
                <template slot="prepend">¥</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="服务费余额">
              <el-input v-model="form.serviceBalance" disabled>
                <template slot="prepend">¥</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="押金余额">
              <el-input v-model="form.depositBalance" disabled>
                <template slot="prepend">¥</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="会员费余额">
              <el-input v-model="form.memberBalance" disabled>
                <template slot="prepend">¥</template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :disabled="!isEdit" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-if="isEdit">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 余额统计对话框 -->
    <el-dialog title="机构账户余额统计" :visible.sync="summaryOpen" width="600px" append-to-body>
      <div v-loading="summaryLoading">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card class="box-card">
              <div class="text-center">
                <div class="text-2xl font-bold text-primary">¥{{ balanceSummary.totalBalanceSum || 0 }}</div>
                <div class="text-gray-500">总余额</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="box-card">
              <div class="text-center">
                <div class="text-2xl font-bold text-success">¥{{ balanceSummary.serviceBalanceSum || 0 }}</div>
                <div class="text-gray-500">服务费余额</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="box-card">
              <div class="text-center">
                <div class="text-2xl font-bold text-warning">¥{{ balanceSummary.depositBalanceSum || 0 }}</div>
                <div class="text-gray-500">押金余额</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 20px">
          <el-col :span="12">
            <el-card class="box-card">
              <div class="text-center">
                <div class="text-xl font-bold text-info">¥{{ balanceSummary.memberBalanceSum || 0 }}</div>
                <div class="text-gray-500">会员费余额</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="box-card">
              <div class="text-center">
                <div class="text-xl font-bold text-info">{{ balanceSummary.accountCount || 0 }}</div>
                <div class="text-gray-500">账户数量</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-dialog>

    <!-- 余额预警对话框 -->
    <el-dialog title="余额不足预警账户" :visible.sync="lowBalanceOpen" width="900px" append-to-body>
      <el-table v-loading="lowBalanceLoading" :data="lowBalanceList">
        <el-table-column label="老人姓名" align="center" prop="elderName" />
        <el-table-column label="身份证号" align="center" prop="idCard" width="180" />
        <el-table-column label="机构名称" align="center" prop="institutionName" />
        <el-table-column label="服务费余额" align="center" prop="serviceBalance">
          <template slot-scope="scope">
            <span class="text-danger">¥{{ scope.row.serviceBalance }}</span>
          </template>
        </el-table-column>
        <el-table-column label="总余额" align="center" prop="totalBalance">
          <template slot-scope="scope">
            <span>¥{{ scope.row.totalBalance }}</span>
          </template>
        </el-table-column>
        <el-table-column label="账户状态" align="center" prop="accountStatus">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.account_status" :value="scope.row.accountStatus"/>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="lowBalanceOpen = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAccountInfo, getAccountInfo, updateAccountInfo, changeAccountStatus, closeAccount, getBalanceSummary, getLowBalanceAccounts } from "@/api/pension/accountInfo";

export default {
  name: "AccountInfo",
  dicts: ['account_status'],
  data() {
    return {
      loading: true,
      ids: [],
      showSearch: true,
      total: 0,
      accountInfoList: [],
      title: "",
      open: false,
      isEdit: false,
      summaryOpen: false,
      summaryLoading: false,
      lowBalanceOpen: false,
      lowBalanceLoading: false,
      balanceSummary: {},
      lowBalanceList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        elderName: null,
        institutionName: null,
        accountStatus: null
      },
      form: {},
      rules: {
        accountName: [
          { required: true, message: "账户名称不能为空", trigger: "blur" }
        ],
        accountStatus: [
          { required: true, message: "账户状态不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询老人账户信息列表 */
    getList() {
      this.loading = true;
      listAccountInfo(this.queryParams).then(response => {
        this.accountInfoList = response.rows;
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
      this.form = {
        accountId: null,
        elderId: null,
        institutionId: null,
        accountNo: null,
        accountName: null,
        accountStatus: null,
        totalBalance: null,
        serviceBalance: null,
        depositBalance: null,
        memberBalance: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      };
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
    /** 查看操作 */
    handleView(row) {
      this.reset();
      const accountId = row.accountId || this.ids
      getAccountInfo(accountId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "查看账户详情";
        this.isEdit = false;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const accountId = row.accountId || this.ids
      getAccountInfo(accountId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改账户信息";
        this.isEdit = true;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateAccountInfo(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    /** 冻结操作 */
    handleFreeze(row) {
      const accountId = row.accountId;
      this.$modal.confirm('是否确认冻结老人"' + row.elderName + '"的账户？').then(() => {
        return changeAccountStatus(accountId, '0');
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("冻结成功");
      }).catch(() => {});
    },
    /** 解冻操作 */
    handleUnfreeze(row) {
      const accountId = row.accountId;
      this.$modal.confirm('是否确认解冻老人"' + row.elderName + '"的账户？').then(() => {
        return changeAccountStatus(accountId, '1');
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("解冻成功");
      }).catch(() => {});
    },
    /** 销户操作 */
    handleClose(row) {
      const accountId = row.accountId;
      this.$modal.confirm('确认销户老人"' + row.elderName + '"的账户？销户后无法恢复！').then(() => {
        return closeAccount(accountId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("销户成功");
      }).catch(() => {});
    },
    /** 余额统计 */
    handleBalanceSummary() {
      this.summaryOpen = true;
      this.summaryLoading = true;
      // 这里默认查询所有机构，实际应该让用户选择机构
      getBalanceSummary(null).then(response => {
        this.balanceSummary = response.data;
        this.summaryLoading = false;
      });
    },
    /** 余额预警 */
    handleLowBalance() {
      this.lowBalanceOpen = true;
      this.lowBalanceLoading = true;
      // 默认查询余额不足2个月的账户
      getLowBalanceAccounts(2).then(response => {
        this.lowBalanceList = response.data;
        this.lowBalanceLoading = false;
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/account/export', {
        ...this.queryParams
      }, `account_info_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style scoped>
.text-primary {
  color: #409EFF;
}
.text-success {
  color: #67C23A;
}
.text-warning {
  color: #E6A23C;
}
.text-danger {
  color: #F56C6C;
}
.text-info {
  color: #909399;
}
.text-2xl {
  font-size: 1.5rem;
  line-height: 2rem;
}
.text-xl {
  font-size: 1.25rem;
  line-height: 1.75rem;
}
.font-bold {
  font-weight: 700;
}
.text-center {
  text-align: center;
}
.text-gray-500 {
  color: #6b7280;
}
.box-card {
  margin-bottom: 20px;
}
</style>