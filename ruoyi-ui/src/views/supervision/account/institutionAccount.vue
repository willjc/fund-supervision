<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-primary">
              <i class="el-icon-office-building"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">机构总数</div>
              <div class="stat-value">{{ statistics.institutionCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-success">
              <i class="el-icon-bank-card"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">监管账户总余额</div>
              <div class="stat-value">¥{{ statistics.totalBalance }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-warning">
              <i class="el-icon-lock"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">冻结账户数</div>
              <div class="stat-value">{{ statistics.frozenAccountCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-danger">
              <i class="el-icon-warning"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">异常账户数</div>
              <div class="stat-value">{{ statistics.abnormalAccountCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 查询表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="90px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账户状态" prop="accountStatus">
        <el-select v-model="queryParams.accountStatus" placeholder="请选择账户状态" clearable style="width: 150px">
          <el-option label="正常" value="1" />
          <el-option label="冻结" value="2" />
          <el-option label="异常" value="3" />
          <el-option label="已关闭" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="账户类型" prop="accountType">
        <el-select v-model="queryParams.accountType" placeholder="请选择账户类型" clearable style="width: 150px">
          <el-option label="基本账户" value="basic" />
          <el-option label="一般账户" value="general" />
          <el-option label="专用账户" value="special" />
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
          type="success"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleRefreshAll"
        >刷新余额</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="accountList" border style="width: 100%">
      <el-table-column label="机构编号" align="center" prop="institutionId" width="100" />
      <el-table-column label="机构名称" align="center" prop="institutionName" width="200" show-overflow-tooltip />
      <el-table-column label="账户类型" align="center" prop="accountType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.accountType === 'basic'" type="primary">基本账户</el-tag>
          <el-tag v-else-if="scope.row.accountType === 'general'" type="success">一般账户</el-tag>
          <el-tag v-else-if="scope.row.accountType === 'special'" type="warning">专用账户</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="银行账号" align="center" prop="bankAccount" width="180" />
      <el-table-column label="开户银行" align="center" prop="bankName" width="150" show-overflow-tooltip />
      <el-table-column label="账户余额" align="center" prop="balance" width="130">
        <template slot-scope="scope">
          <el-link type="primary" @click="handleViewSubAccounts(scope.row)">
            <span style="font-weight: bold; font-size: 14px">¥{{ scope.row.balance }}</span>
          </el-link>
        </template>
      </el-table-column>
      <el-table-column label="冻结金额" align="center" prop="frozenAmount" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.frozenAmount > 0" style="color: #E6A23C; font-weight: bold">¥{{ scope.row.frozenAmount }}</span>
          <span v-else>¥0</span>
        </template>
      </el-table-column>
      <el-table-column label="可用余额" align="center" prop="availableBalance" width="120">
        <template slot-scope="scope">
          <span style="color: #67C23A; font-weight: bold">¥{{ scope.row.availableBalance }}</span>
        </template>
      </el-table-column>
      <el-table-column label="账户状态" align="center" prop="accountStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.accountStatus === '1'" type="success">正常</el-tag>
          <el-tag v-else-if="scope.row.accountStatus === '2'" type="warning">冻结</el-tag>
          <el-tag v-else-if="scope.row.accountStatus === '3'" type="danger">异常</el-tag>
          <el-tag v-else type="info">已关闭</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="开户日期" align="center" prop="openDate" width="110" />
      <el-table-column label="最后更新" align="center" prop="updateTime" min-width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="260">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-tickets" @click="handleViewTransactions(scope.row)">交易流水</el-button>
          <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)">
            <el-button size="mini" type="text">
              更多<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="subAccounts" icon="el-icon-coin">子账户明细</el-dropdown-item>
              <el-dropdown-item command="freeze" icon="el-icon-lock" v-if="scope.row.accountStatus === '1'">冻结账户</el-dropdown-item>
              <el-dropdown-item command="unfreeze" icon="el-icon-unlock" v-if="scope.row.accountStatus === '2'">解冻账户</el-dropdown-item>
              <el-dropdown-item command="refresh" icon="el-icon-refresh">刷新余额</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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

    <!-- 账户详情对话框 -->
    <el-dialog title="机构账户详情" :visible.sync="detailOpen" width="1000px" append-to-body>
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="基本信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="机构编号">{{ detailData.institutionId }}</el-descriptions-item>
            <el-descriptions-item label="机构名称">{{ detailData.institutionName }}</el-descriptions-item>
            <el-descriptions-item label="账户类型">
              <el-tag v-if="detailData.accountType === 'basic'" type="primary">基本账户</el-tag>
              <el-tag v-else-if="detailData.accountType === 'general'" type="success">一般账户</el-tag>
              <el-tag v-else type="warning">专用账户</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="账户状态">
              <el-tag v-if="detailData.accountStatus === '1'" type="success">正常</el-tag>
              <el-tag v-else-if="detailData.accountStatus === '2'" type="warning">冻结</el-tag>
              <el-tag v-else-if="detailData.accountStatus === '3'" type="danger">异常</el-tag>
              <el-tag v-else type="info">已关闭</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="银行账号">{{ detailData.bankAccount }}</el-descriptions-item>
            <el-descriptions-item label="开户银行">{{ detailData.bankName }}</el-descriptions-item>
            <el-descriptions-item label="开户支行">{{ detailData.bankBranch }}</el-descriptions-item>
            <el-descriptions-item label="开户日期">{{ detailData.openDate }}</el-descriptions-item>
            <el-descriptions-item label="账户余额" :span="2">
              <span style="color: #409EFF; font-weight: bold; font-size: 16px">¥{{ detailData.balance }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="冻结金额">
              <span style="color: #E6A23C; font-weight: bold">¥{{ detailData.frozenAmount || 0 }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="可用余额">
              <span style="color: #67C23A; font-weight: bold">¥{{ detailData.availableBalance }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="最后更新时间" :span="2">{{ detailData.updateTime }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="子账户明细" name="subAccounts">
          <el-table :data="subAccountsList" border>
            <el-table-column label="子账户类型" align="center" prop="subAccountType" width="150">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.subAccountType === 'service'" type="primary">服务费子账户</el-tag>
                <el-tag v-else-if="scope.row.subAccountType === 'deposit'" type="success">押金子账户</el-tag>
                <el-tag v-else-if="scope.row.subAccountType === 'member'" type="warning">会员费子账户</el-tag>
                <el-tag v-else type="info">其他子账户</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="账户余额" align="center" prop="balance" width="150">
              <template slot-scope="scope">
                <span style="color: #409EFF; font-weight: bold">¥{{ scope.row.balance }}</span>
              </template>
            </el-table-column>
            <el-table-column label="冻结金额" align="center" prop="frozenAmount" width="120">
              <template slot-scope="scope">
                <span v-if="scope.row.frozenAmount > 0" style="color: #E6A23C">¥{{ scope.row.frozenAmount }}</span>
                <span v-else>¥0</span>
              </template>
            </el-table-column>
            <el-table-column label="可用余额" align="center" prop="availableBalance" width="150">
              <template slot-scope="scope">
                <span style="color: #67C23A; font-weight: bold">¥{{ scope.row.availableBalance }}</span>
              </template>
            </el-table-column>
            <el-table-column label="老人数量" align="center" prop="elderCount" width="100" />
            <el-table-column label="最后更新" align="center" prop="updateTime" width="160" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="操作记录" name="operations">
          <el-table :data="operationsList" border>
            <el-table-column label="操作时间" align="center" prop="operateTime" width="160" />
            <el-table-column label="操作类型" align="center" prop="operateType" width="120">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.operateType === 'freeze'" type="warning" size="small">冻结</el-tag>
                <el-tag v-else-if="scope.row.operateType === 'unfreeze'" type="success" size="small">解冻</el-tag>
                <el-tag v-else-if="scope.row.operateType === 'modify'" type="primary" size="small">修改</el-tag>
                <el-tag v-else type="info" size="small">{{ scope.row.operateType }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作说明" align="center" prop="operateDesc" show-overflow-tooltip />
            <el-table-column label="操作人" align="center" prop="operator" width="100" />
            <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 子账户明细对话框 -->
    <el-dialog title="子账户明细" :visible.sync="subAccountsOpen" width="1000px" append-to-body>
      <div style="margin-bottom: 15px">
        <el-tag type="primary">机构：{{ currentInstitutionName }}</el-tag>
        <el-tag type="success" style="margin-left: 10px">账户总余额：¥{{ currentTotalBalance }}</el-tag>
      </div>
      <el-table :data="subAccountsList" border>
        <el-table-column label="子账户类型" align="center" prop="subAccountType" width="150">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.subAccountType === 'service'" type="primary">服务费子账户</el-tag>
            <el-tag v-else-if="scope.row.subAccountType === 'deposit'" type="success">押金子账户</el-tag>
            <el-tag v-else-if="scope.row.subAccountType === 'member'" type="warning">会员费子账户</el-tag>
            <el-tag v-else type="info">其他子账户</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="账户余额" align="center" prop="balance" width="150">
          <template slot-scope="scope">
            <span style="color: #409EFF; font-weight: bold">¥{{ scope.row.balance }}</span>
          </template>
        </el-table-column>
        <el-table-column label="冻结金额" align="center" prop="frozenAmount" width="120">
          <template slot-scope="scope">
            <span v-if="scope.row.frozenAmount > 0" style="color: #E6A23C">¥{{ scope.row.frozenAmount }}</span>
            <span v-else>¥0</span>
          </template>
        </el-table-column>
        <el-table-column label="可用余额" align="center" prop="availableBalance" width="150">
          <template slot-scope="scope">
            <span style="color: #67C23A; font-weight: bold">¥{{ scope.row.availableBalance }}</span>
          </template>
        </el-table-column>
        <el-table-column label="老人数量" align="center" prop="elderCount" width="100" />
        <el-table-column label="说明" align="center" prop="description" show-overflow-tooltip />
        <el-table-column label="最后更新" align="center" prop="updateTime" width="160" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="subAccountsOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 交易流水对话框 -->
    <el-dialog title="账户交易流水" :visible.sync="transactionsOpen" width="1200px" append-to-body>
      <div style="margin-bottom: 15px">
        <el-tag type="primary">机构：{{ currentInstitutionName }}</el-tag>
        <el-tag type="success" style="margin-left: 10px">账户余额：¥{{ currentTotalBalance }}</el-tag>
      </div>
      <el-form :model="transactionQuery" ref="transactionQueryForm" size="small" :inline="true" class="mb15">
        <el-form-item label="交易类型">
          <el-select v-model="transactionQuery.transactionType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="收入" value="income" />
            <el-option label="支出" value="expense" />
            <el-option label="冻结" value="freeze" />
            <el-option label="解冻" value="unfreeze" />
          </el-select>
        </el-form-item>
        <el-form-item label="起止日期">
          <el-date-picker
            v-model="transactionQuery.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleTransactionQuery">查询</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="handleTransactionReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="transactionsList" border max-height="400">
        <el-table-column label="交易时间" align="center" prop="transactionTime" width="160" />
        <el-table-column label="交易类型" align="center" prop="transactionType" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.transactionType === 'income'" type="success" size="small">收入</el-tag>
            <el-tag v-else-if="scope.row.transactionType === 'expense'" type="danger" size="small">支出</el-tag>
            <el-tag v-else-if="scope.row.transactionType === 'freeze'" type="warning" size="small">冻结</el-tag>
            <el-tag v-else-if="scope.row.transactionType === 'unfreeze'" type="info" size="small">解冻</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="交易金额" align="center" prop="amount" width="120">
          <template slot-scope="scope">
            <span v-if="scope.row.transactionType === 'income'" style="color: #67C23A; font-weight: bold">+¥{{ scope.row.amount }}</span>
            <span v-else-if="scope.row.transactionType === 'expense'" style="color: #F56C6C; font-weight: bold">-¥{{ scope.row.amount }}</span>
            <span v-else style="font-weight: bold">¥{{ scope.row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="交易后余额" align="center" prop="afterBalance" width="120">
          <template slot-scope="scope">
            <span style="color: #409EFF; font-weight: bold">¥{{ scope.row.afterBalance }}</span>
          </template>
        </el-table-column>
        <el-table-column label="交易对方" align="center" prop="counterparty" width="150" show-overflow-tooltip />
        <el-table-column label="交易摘要" align="center" prop="summary" show-overflow-tooltip />
        <el-table-column label="凭证号" align="center" prop="voucherNo" width="160" />
        <el-table-column label="操作员" align="center" prop="operator" width="100" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleExportTransactions">导出流水</el-button>
        <el-button @click="transactionsOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'InstitutionAccount',
  data() {
    return {
      // 统计数据
      statistics: {
        institutionCount: 156,
        totalBalance: '32,568,900',
        frozenAccountCount: 8,
        abnormalAccountCount: 3
      },
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 账户列表
      accountList: [],
      // 子账户列表
      subAccountsList: [],
      // 操作记录列表
      operationsList: [],
      // 交易流水列表
      transactionsList: [],
      // 当前机构名称
      currentInstitutionName: '',
      // 当前总余额
      currentTotalBalance: 0,
      // 弹出层标题
      title: '',
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示子账户弹出层
      subAccountsOpen: false,
      // 是否显示交易流水弹出层
      transactionsOpen: false,
      // 当前选项卡
      activeTab: 'basic',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        accountStatus: null,
        accountType: null
      },
      // 交易流水查询参数
      transactionQuery: {
        transactionType: null,
        dateRange: null
      },
      // 详情数据
      detailData: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询账户列表 */
    getList() {
      this.loading = true
      // 模拟数据
      setTimeout(() => {
        this.accountList = [
          {
            institutionId: 'I10001',
            institutionName: '北京市朝阳区幸福养老院',
            accountType: 'basic',
            bankAccount: '622202XXXXXXXXXX1234',
            bankName: '中国工商银行',
            bankBranch: '朝阳支行',
            balance: 2856900,
            frozenAmount: 50000,
            availableBalance: 2806900,
            accountStatus: '1',
            openDate: '2024-01-15',
            updateTime: '2024-11-03 14:30:00',
            remark: '基本账户'
          },
          {
            institutionId: 'I10002',
            institutionName: '上海市浦东新区康乐养老中心',
            accountType: 'general',
            bankAccount: '622202XXXXXXXXXX5678',
            bankName: '中国建设银行',
            bankBranch: '浦东支行',
            balance: 4562000,
            frozenAmount: 120000,
            availableBalance: 4442000,
            accountStatus: '2',
            openDate: '2024-02-20',
            updateTime: '2024-11-03 10:15:00',
            remark: '一般账户，因违规操作已冻结'
          },
          {
            institutionId: 'I10003',
            institutionName: '广州市天河区爱心养老服务中心',
            accountType: 'special',
            bankAccount: '622202XXXXXXXXXX9012',
            bankName: '中国农业银行',
            bankBranch: '天河支行',
            balance: 1982000,
            frozenAmount: 0,
            availableBalance: 1982000,
            accountStatus: '1',
            openDate: '2024-03-10',
            updateTime: '2024-11-03 09:45:00',
            remark: '专用账户'
          },
          {
            institutionId: 'I10004',
            institutionName: '深圳市南山区夕阳红养老院',
            accountType: 'basic',
            bankAccount: '622202XXXXXXXXXX3456',
            bankName: '中国银行',
            bankBranch: '南山支行',
            balance: 856000,
            frozenAmount: 0,
            availableBalance: 856000,
            accountStatus: '3',
            openDate: '2023-12-01',
            updateTime: '2024-11-02 16:20:00',
            remark: '账户异常，疑似风险操作'
          },
          {
            institutionId: 'I10005',
            institutionName: '杭州市西湖区福寿康养老中心',
            accountType: 'basic',
            bankAccount: '622202XXXXXXXXXX7890',
            bankName: '招商银行',
            bankBranch: '西湖支行',
            balance: 3268000,
            frozenAmount: 0,
            availableBalance: 3268000,
            accountStatus: '1',
            openDate: '2024-04-15',
            updateTime: '2024-11-03 13:50:00',
            remark: '基本账户'
          }
        ]
        this.total = this.accountList.length
        this.loading = false
      }, 500)
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 刷新全部余额 */
    handleRefreshAll() {
      this.$modal.msgSuccess('正在刷新所有账户余额...')
      this.getList()
    },
    /** 查看子账户明细 */
    handleViewSubAccounts(row) {
      this.currentInstitutionName = row.institutionName
      this.currentTotalBalance = row.balance
      // 模拟数据
      this.subAccountsList = [
        {
          subAccountType: 'service',
          balance: row.balance * 0.6,
          frozenAmount: row.frozenAmount * 0.5,
          availableBalance: row.balance * 0.6 - row.frozenAmount * 0.5,
          elderCount: 45,
          description: '老人缴纳的服务费（床位费、护理费、伙食费等）',
          updateTime: '2024-11-03 14:30:00'
        },
        {
          subAccountType: 'deposit',
          balance: row.balance * 0.3,
          frozenAmount: row.frozenAmount * 0.4,
          availableBalance: row.balance * 0.3 - row.frozenAmount * 0.4,
          elderCount: 45,
          description: '老人缴纳的押金',
          updateTime: '2024-11-03 14:30:00'
        },
        {
          subAccountType: 'member',
          balance: row.balance * 0.1,
          frozenAmount: row.frozenAmount * 0.1,
          availableBalance: row.balance * 0.1 - row.frozenAmount * 0.1,
          elderCount: 32,
          description: '老人缴纳的会员费',
          updateTime: '2024-11-03 14:30:00'
        }
      ]
      this.subAccountsOpen = true
    },
    /** 查看交易流水 */
    handleViewTransactions(row) {
      this.currentInstitutionName = row.institutionName
      this.currentTotalBalance = row.balance
      // 模拟数据
      this.transactionsList = [
        {
          transactionTime: '2024-11-03 10:30:00',
          transactionType: 'income',
          amount: 58600,
          afterBalance: row.balance,
          counterparty: '王XX老人',
          summary: '老人入住缴费（服务费+押金+会员费）',
          voucherNo: 'DD202411030001',
          operator: '张三'
        },
        {
          transactionTime: '2024-11-02 14:20:00',
          transactionType: 'expense',
          amount: 30000,
          afterBalance: row.balance - 58600,
          counterparty: '养老机构基本账户',
          summary: '资金划拨（服务费）',
          voucherNo: 'PB202411020001',
          operator: '系统'
        },
        {
          transactionTime: '2024-11-01 09:15:00',
          transactionType: 'freeze',
          amount: 50000,
          afterBalance: row.balance - 58600 + 30000,
          counterparty: '',
          summary: '账户冻结（风险预警）',
          voucherNo: 'FZ202411010001',
          operator: '李四'
        }
      ]
      this.transactionsOpen = true
    },
    /** 更多操作 */
    handleCommand(command, row) {
      switch (command) {
        case 'subAccounts':
          this.handleViewSubAccounts(row)
          break
        case 'freeze':
          this.$modal.confirm('是否确认冻结"' + row.institutionName + '"的账户？冻结后该机构将无法进行资金操作。').then(() => {
            this.$modal.msgSuccess('冻结成功')
            this.getList()
          })
          break
        case 'unfreeze':
          this.$modal.confirm('是否确认解冻"' + row.institutionName + '"的账户？').then(() => {
            this.$modal.msgSuccess('解冻成功')
            this.getList()
          })
          break
        case 'refresh':
          this.$modal.msgSuccess('正在刷新账户余额...')
          setTimeout(() => {
            this.$modal.msgSuccess('刷新成功')
            this.getList()
          }, 1000)
          break
      }
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.detailData = { ...row }
      // 模拟子账户数据
      this.subAccountsList = [
        {
          subAccountType: 'service',
          balance: row.balance * 0.6,
          frozenAmount: row.frozenAmount * 0.5,
          availableBalance: row.balance * 0.6 - row.frozenAmount * 0.5,
          elderCount: 45,
          updateTime: '2024-11-03 14:30:00'
        },
        {
          subAccountType: 'deposit',
          balance: row.balance * 0.3,
          frozenAmount: row.frozenAmount * 0.4,
          availableBalance: row.balance * 0.3 - row.frozenAmount * 0.4,
          elderCount: 45,
          updateTime: '2024-11-03 14:30:00'
        },
        {
          subAccountType: 'member',
          balance: row.balance * 0.1,
          frozenAmount: row.frozenAmount * 0.1,
          availableBalance: row.balance * 0.1 - row.frozenAmount * 0.1,
          elderCount: 32,
          updateTime: '2024-11-03 14:30:00'
        }
      ]
      // 模拟操作记录
      this.operationsList = [
        {
          operateTime: '2024-11-01 09:15:00',
          operateType: 'freeze',
          operateDesc: '因风险预警冻结账户',
          operator: '李四',
          remark: '检测到异常大额支出'
        },
        {
          operateTime: '2024-10-15 14:30:00',
          operateType: 'modify',
          operateDesc: '修改账户信息',
          operator: '张三',
          remark: '更新联系方式'
        }
      ]
      this.detailOpen = true
      this.activeTab = 'basic'
    },
    /** 交易流水查询 */
    handleTransactionQuery() {
      this.$modal.msgSuccess('查询中...')
    },
    /** 交易流水重置 */
    handleTransactionReset() {
      this.transactionQuery = {
        transactionType: null,
        dateRange: null
      }
      this.handleTransactionQuery()
    },
    /** 导出交易流水 */
    handleExportTransactions() {
      this.$modal.msgSuccess('导出功能开发中')
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.msgSuccess('导出功能开发中')
    }
  }
}
</script>

<style scoped>
.mb20 {
  margin-bottom: 20px;
}

.mb15 {
  margin-bottom: 15px;
}

.mb8 {
  margin-bottom: 8px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon i {
  font-size: 30px;
  color: #fff;
}

.bg-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.bg-success {
  background: linear-gradient(135deg, #56ab2f 0%, #a8e063 100%);
}

.bg-warning {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.bg-danger {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
</style>
