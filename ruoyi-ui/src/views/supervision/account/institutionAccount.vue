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
              <div class="stat-value">{{ statistics.institutionCount || 0 }}</div>
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
              <div class="stat-value">¥{{ formatNumber(statistics.totalSupervisionBalance || 0) }}</div>
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
              <div class="stat-value">{{ statistics.frozenAccountCount || 0 }}</div>
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
              <div class="stat-value">{{ statistics.abnormalAccountCount || 0 }}</div>
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
          <el-option label="正常" value="0" />
          <el-option label="冻结" value="2" />
          <el-option label="异常" value="3" />
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
      <el-table-column label="监管账户" align="center" prop="superviseAccount" width="160" />
      <el-table-column label="监管开户行" align="center" prop="superviseBank" width="150" show-overflow-tooltip />
      <el-table-column label="基本账户" align="center" prop="bankAccount" width="160" />
      <el-table-column label="基本开户行" align="center" prop="basicBank" width="150" show-overflow-tooltip />
      <el-table-column label="监管账户余额" align="center" prop="supervisionBalance" width="140">
        <template slot-scope="scope">
          <span style="color: #409EFF; font-weight: bold; font-size: 14px">¥{{ formatNumber(scope.row.supervisionBalance || 0) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预收费余额" align="center" prop="prepaidBalance" width="120">
        <template slot-scope="scope">
          <span style="font-weight: bold">¥{{ formatNumber(scope.row.prepaidBalance || 0) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="押金余额" align="center" prop="depositBalance" width="120">
        <template slot-scope="scope">
          <span style="font-weight: bold">¥{{ formatNumber(scope.row.depositBalance || 0) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="账户状态" align="center" prop="accountStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.accountStatus === '0'" type="success">正常</el-tag>
          <el-tag v-else-if="scope.row.accountStatus === '2'" type="warning">冻结</el-tag>
          <el-tag v-else-if="scope.row.accountStatus === '3'" type="danger">异常</el-tag>
          <el-tag v-else type="info">未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-tickets" @click="handleFlow(scope.row)">流水</el-button>
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
    <el-dialog title="机构账户详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="机构编号">{{ detailData.institutionId }}</el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ detailData.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="监管账户">{{ detailData.superviseAccount || '-' }}</el-descriptions-item>
        <el-descriptions-item label="监管开户行">{{ detailData.superviseBank || '-' }}</el-descriptions-item>
        <el-descriptions-item label="基本账户">{{ detailData.bankAccount || '-' }}</el-descriptions-item>
        <el-descriptions-item label="基本开户行">{{ detailData.basicBank || '-' }}</el-descriptions-item>
        <el-descriptions-item label="监管账户余额">
          <span style="color: #409EFF; font-weight: bold; font-size: 16px">¥{{ formatNumber(detailData.supervisionBalance || 0) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="账户状态">
          <el-tag v-if="detailData.accountStatus === '0'" type="success">正常</el-tag>
          <el-tag v-else-if="detailData.accountStatus === '2'" type="warning">冻结</el-tag>
          <el-tag v-else-if="detailData.accountStatus === '3'" type="danger">异常</el-tag>
          <el-tag v-else type="info">未知</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预收费余额">
          <span style="font-weight: bold">¥{{ formatNumber(detailData.prepaidBalance || 0) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="押金余额">
          <span style="font-weight: bold">¥{{ formatNumber(detailData.depositBalance || 0) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核通过时间" :span="2">{{ detailData.approveTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 账户流水对话框 -->
    <el-dialog title="账户流水" :visible.sync="flowOpen" width="1200px" append-to-body @close="handleFlowDialogClose">
      <el-tabs v-model="activeFlowTab" @tab-click="handleFlowTabClick">
        <!-- 收单流水 Tab -->
        <el-tab-pane label="收单流水" name="payment">
          <el-table v-loading="paymentLoading" :data="paymentList" border style="margin-top: 10px">
            <el-table-column label="订单号" prop="orderNo" width="170" show-overflow-tooltip />
            <el-table-column label="支付时间" prop="paymentTime" width="160" />
            <el-table-column label="支付金额" prop="amount" width="110" align="right">
              <template slot-scope="scope">
                <span style="color: #67C23A; font-weight: bold">¥{{ formatNumber(scope.row.amount) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="支付方式" prop="paymentMethod" width="100" />
            <el-table-column label="监管账户余额" prop="supervisionBalance" width="130" align="right">
              <template slot-scope="scope">
                <span style="color: #409EFF; font-weight: bold">¥{{ formatNumber(scope.row.supervisionBalance) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="订单状态" prop="status" width="90">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === '已完成' ? 'success' : scope.row.status === '处理中' ? 'warning' : scope.row.status === '失败' ? 'danger' : 'info'" size="small">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <pagination
            v-show="paymentTotal > 0"
            :total="paymentTotal"
            :page.sync="paymentQuery.pageNum"
            :limit.sync="paymentQuery.pageSize"
            @pagination="getPaymentFlowData"
            style="margin-top: 10px"
          />
        </el-tab-pane>

        <!-- 监管账户流水 Tab -->
        <el-tab-pane label="监管账户流水" name="supervision">
          <el-table v-loading="supervisionLoading" :data="supervisionList" border style="margin-top: 10px">
            <el-table-column label="账单月份" prop="billingMonth" width="100" />
            <el-table-column label="划拨单号" prop="transferNo" width="180" show-overflow-tooltip />
            <el-table-column label="老人姓名" prop="elderName" width="110" />
            <el-table-column label="划拨金额" prop="transferAmount" width="120" align="right">
              <template slot-scope="scope">
                <span style="color: #F56C6C; font-weight: bold">-¥{{ formatNumber(scope.row.transferAmount) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="划拨类型" prop="transferType" width="100">
              <template slot-scope="scope">
                {{ getTransferTypeText(scope.row.transferType) }}
              </template>
            </el-table-column>
            <el-table-column label="划拨日期" prop="paidTime" width="160">
              <template slot-scope="scope">
                {{ formatDateTime(scope.row.paidTime) }}
              </template>
            </el-table-column>
            <el-table-column label="监管账户余额" prop="supervisionBalance" width="120" align="right">
              <template slot-scope="scope">
                <span>¥{{ formatNumber(scope.row.supervisionBalance) }}</span>
              </template>
            </el-table-column>
          </el-table>
          <pagination
            v-show="supervisionTotal > 0"
            :total="supervisionTotal"
            :page.sync="supervisionQuery.pageNum"
            :limit.sync="supervisionQuery.pageSize"
            @pagination="getSupervisionFlowData"
            style="margin-top: 10px"
          />
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button @click="flowOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInstitutionAccount, getInstitutionStatistics } from '@/api/supervision/account'
import { getPaymentList, getSupervisionList } from '@/api/pension/bank'

export default {
  name: 'InstitutionAccount',
  data() {
    return {
      // 统计数据
      statistics: {
        institutionCount: 0,
        totalSupervisionBalance: 0,
        frozenAccountCount: 0,
        abnormalAccountCount: 0
      },
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 账户列表
      accountList: [],
      // 当前选择的机构
      currentInstitution: null,
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示流水弹出层
      flowOpen: false,
      // 详情数据
      detailData: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        accountStatus: null
      },
      // 流水相关
      activeFlowTab: 'payment',
      // 收单流水数据
      paymentList: [],
      paymentTotal: 0,
      paymentLoading: false,
      paymentQuery: {
        pageNum: 1,
        pageSize: 10
      },
      // 监管流水数据
      supervisionList: [],
      supervisionTotal: 0,
      supervisionLoading: false,
      supervisionQuery: {
        pageNum: 1,
        pageSize: 10
      }
    }
  },
  created() {
    this.getStatistics()
    this.getList()
  },
  methods: {
    /** 获取统计数据 */
    getStatistics() {
      getInstitutionStatistics().then(response => {
        this.statistics = response.data || {
          institutionCount: 0,
          totalSupervisionBalance: 0,
          frozenAccountCount: 0,
          abnormalAccountCount: 0
        }
      }).catch(error => {
        console.error('获取统计数据失败:', error)
      })
    },
    /** 查询账户列表 */
    getList() {
      this.loading = true
      const params = {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize
      }
      if (this.queryParams.institutionName) {
        params.institutionName = this.queryParams.institutionName
      }
      if (this.queryParams.accountStatus) {
        params.accountStatus = this.queryParams.accountStatus
      }

      listInstitutionAccount(params).then(response => {
        this.accountList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(error => {
        console.error('查询机构账户列表失败:', error)
        this.loading = false
      })
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
      this.getStatistics()
      this.getList()
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.detailData = { ...row }
      this.detailOpen = true
    },
    /** 流水按钮操作 */
    handleFlow(row) {
      this.currentInstitution = row
      this.activeFlowTab = 'payment'
      this.paymentQuery.pageNum = 1
      this.supervisionQuery.pageNum = 1
      this.flowOpen = true
      // 默认加载收单流水
      this.getPaymentFlowData()
    },
    /** 关闭流水对话框 */
    handleFlowDialogClose() {
      this.paymentList = []
      this.supervisionList = []
      this.paymentTotal = 0
      this.supervisionTotal = 0
    },
    /** Tab切换 */
    handleFlowTabClick(tab) {
      if (tab.name === 'payment' && this.paymentList.length === 0) {
        this.getPaymentFlowData()
      } else if (tab.name === 'supervision' && this.supervisionList.length === 0) {
        this.getSupervisionFlowData()
      }
    },
    /** 获取收单流水数据 */
    getPaymentFlowData() {
      if (!this.currentInstitution) return

      this.paymentLoading = true
      const params = {
        pageNum: this.paymentQuery.pageNum,
        pageSize: this.paymentQuery.pageSize,
        institutionId: this.currentInstitution.institutionId
      }

      getPaymentList(params).then(response => {
        this.paymentList = response.rows || []
        this.paymentTotal = response.total || 0
        this.paymentLoading = false
      }).catch(error => {
        console.error('获取收单流水失败:', error)
        this.paymentLoading = false
      })
    },
    /** 获取监管账户流水数据 */
    getSupervisionFlowData() {
      if (!this.currentInstitution) return

      this.supervisionLoading = true
      const params = {
        pageNum: this.supervisionQuery.pageNum,
        pageSize: this.supervisionQuery.pageSize,
        institutionId: this.currentInstitution.institutionId
      }

      getSupervisionList(params).then(response => {
        this.supervisionList = response.rows || []
        this.supervisionTotal = response.total || 0
        this.supervisionLoading = false
      }).catch(error => {
        console.error('获取监管流水失败:', error)
        this.supervisionLoading = false
      })
    },
    /** 获取划拨类型文本 */
    getTransferTypeText(type) {
      const textMap = {
        '1': '自动划拨',
        '2': '手动划拨',
        '3': '特殊申请'
      }
      return textMap[type] || type
    },
    /** 格式化日期时间 */
    formatDateTime(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.msgSuccess('导出功能开发中')
    },
    /** 格式化数字（添加千分位） */
    formatNumber(value) {
      if (!value && value !== 0) return '0.00'
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }
  }
}
</script>

<style scoped>
.mb20 {
  margin-bottom: 20px;
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
