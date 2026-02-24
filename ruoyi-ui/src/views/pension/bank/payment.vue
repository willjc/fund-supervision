<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>收单交易流水</span>
      </div>

      <!-- 搜索条件 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="养老机构">
          <el-select v-model="queryParams.institutionId" placeholder="请选择机构" clearable style="width: 200px" @change="onInstitutionChange">
            <el-option
              v-for="item in institutionOptions"
              :key="item.institution_id"
              :label="item.institution_name"
              :value="item.institution_id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="交易时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="yyyy-MM-dd">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select v-model="queryParams.paymentMethod" placeholder="请选择" clearable>
            <el-option label="微信支付" value="微信支付"></el-option>
            <el-option label="支付宝" value="支付宝"></el-option>
            <el-option label="银行卡" value="银行卡"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="订单号">
          <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          <el-button type="success" icon="el-icon-download" @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>

      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stats-cards">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">今日收入笔数</div>
            <div class="stat-value">{{ queryParams.institutionId ? stats.todayCount : '-' }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">今日监管账户收入</div>
            <div class="stat-value">{{ queryParams.institutionId ? '¥' + formatMoney(stats.todayAmount) : '-' }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">本月收入笔数</div>
            <div class="stat-value">{{ queryParams.institutionId ? stats.monthCount : '-' }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-label">本月监管账户收入</div>
            <div class="stat-value">{{ queryParams.institutionId ? '¥' + formatMoney(stats.monthAmount) : '-' }}</div>
          </div>
        </el-col>
      </el-row>

      <!-- 交易流水表格 -->
      <el-table v-loading="loading" :data="dataList" border style="margin-top: 20px">
        <el-table-column label="订单号" prop="orderNo" width="180"></el-table-column>
        <el-table-column label="支付流水号" prop="transactionNo" width="200"></el-table-column>
        <el-table-column label="支付时间" prop="paymentTime" width="180"></el-table-column>
        <el-table-column label="支付金额" prop="amount" width="120">
          <template slot-scope="scope">
            ¥{{ formatMoney(scope.row.amount) }}
          </template>
        </el-table-column>
        <el-table-column label="支付方式" prop="paymentMethod" width="120"></el-table-column>
        <el-table-column label="支付渠道" prop="paymentChannel" width="120"></el-table-column>
        <el-table-column label="手续费" prop="fee" width="100">
          <template slot-scope="scope">
            ¥{{ formatMoney(scope.row.fee) }}
          </template>
        </el-table-column>
        <el-table-column label="实际到账" prop="actualAmount" width="120">
          <template slot-scope="scope">
            ¥{{ formatMoney(scope.row.actualAmount) }}
          </template>
        </el-table-column>
        <el-table-column label="监管账户余额" prop="supervisionBalance" width="150">
          <template slot-scope="scope">
            <span style="color: #67C23A; font-weight: bold;">¥{{ formatMoney(scope.row.supervisionBalance) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" prop="status" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="text" @click="handleDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 详情对话框 -->
      <el-dialog title="收单交易详情" :visible.sync="detailOpen" width="700px" append-to-body>
        <el-descriptions :column="2" border v-if="currentDetail">
          <el-descriptions-item label="支付单号">{{ currentDetail.paymentNo }}</el-descriptions-item>
          <el-descriptions-item label="支付时间">{{ currentDetail.paymentTime }}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{ currentDetail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ currentDetail.paymentMethod }}</el-descriptions-item>
          <el-descriptions-item label="支付金额">
            <span style="font-weight: bold; color: #409EFF;">¥{{ formatMoney(currentDetail.amount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="实际到账">
            <span style="font-weight: bold; color: #67C23A;">¥{{ formatMoney(currentDetail.actualAmount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentDetail.status)">{{ currentDetail.status }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="交易流水号">{{ currentDetail.transactionNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="关联老人" :span="2">{{ currentDetail.elderName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="关联机构" :span="2">{{ currentDetail.institutionName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="支付凭证" :span="2" v-if="currentDetail.paymentProof">
            <el-image
              v-if="currentDetail.paymentProof"
              :src="paymentProofUrl"
              :preview-src-list="[paymentProofUrl]"
              style="width: 100px; height: 100px; cursor: pointer;"
              fit="cover">
            </el-image>
          </el-descriptions-item>
          <el-descriptions-item label="凭证备注" :span="2" v-if="currentDetail.paymentProofRemark">
            {{ currentDetail.paymentProofRemark }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentDetail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
        <div slot="footer" class="dialog-footer">
          <el-button @click="detailOpen = false">关闭</el-button>
        </div>
      </el-dialog>

      <!-- 分页 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script>
import { getPaymentList, getPaymentStatistics, getPaymentDetail, getUserInstitutions } from '@/api/pension/bank'

export default {
  name: 'BankPayment',
  data() {
    return {
      loading: false,
      dataList: [],
      total: 0,
      dateRange: [],
      institutionOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionId: null,
        paymentMethod: null,
        orderNo: null
      },
      stats: {
        todayCount: 0,
        todayAmount: 0,
        monthCount: 0,
        monthAmount: 0
      },
      detailOpen: false,
      currentDetail: null,
      baseUrl: process.env.VUE_APP_BASE_API
    }
  },
  computed: {
    // 获取支付凭证完整URL
    paymentProofUrl() {
      if (!this.currentDetail || !this.currentDetail.paymentProof) return '';
      const url = this.currentDetail.paymentProof;
      // 如果是完整URL（http/https开头），直接返回
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
      }
      // 否则拼接API前缀
      return this.baseUrl + url;
    }
  },
  created() {
    this.loadInstitutions()
    // 初始不加载统计数据，需要选择机构后才加载
    // this.getStatistics()
    this.getList()
  },
  methods: {
    // 加载机构列表
    loadInstitutions() {
      getUserInstitutions().then(response => {
        this.institutionOptions = response.data || []
      }).catch(error => {
        console.error('获取机构列表失败:', error)
      })
    },
    // 机构选择变化时刷新统计
    onInstitutionChange(val) {
      // 刷新统计数��（选择了机构才加载，清空选择则重置统计）
      if (val) {
        this.getStatistics(val)
      } else {
        this.resetStats()
      }
    },
    // 获取统计信息
    getStatistics(institutionId) {
      const params = {
        institutionId: institutionId
      }
      getPaymentStatistics(params).then(response => {
        this.stats = response.data || { todayCount: 0, todayAmount: 0, monthCount: 0, monthAmount: 0 }
      }).catch(error => {
        console.error('获取统计信息失败:', error)
      })
    },
    // 重置统计数据
    resetStats() {
      this.stats = {
        todayCount: 0,
        todayAmount: 0,
        monthCount: 0,
        monthAmount: 0
      }
    },
    // 查询列表
    getList() {
      this.loading = true
      const params = {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize
      }
      if (this.queryParams.institutionId) {
        params.institutionId = this.queryParams.institutionId
      }
      if (this.dateRange && this.dateRange.length === 2) {
        params.beginTime = this.dateRange[0]
        params.endTime = this.dateRange[1]
      }
      if (this.queryParams.paymentMethod) {
        params.paymentMethod = this.queryParams.paymentMethod
      }
      if (this.queryParams.orderNo) {
        params.orderNo = this.queryParams.orderNo
      }

      getPaymentList(params).then(response => {
        this.dataList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(error => {
        console.error('查询收单交易流水失败:', error)
        this.loading = false
      })
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
      // 只有选择了机构才刷新统计数据
      if (this.queryParams.institutionId) {
        this.getStatistics(this.queryParams.institutionId)
      } else {
        this.resetStats()
      }
    },
    // 重置
    resetQuery() {
      this.dateRange = []
      this.$refs.queryForm.resetFields()
      this.queryParams.pageNum = 1
      this.getList()
      // 重置后清空统计数据
      this.resetStats()
    },
    // 导出
    handleExport() {
      this.$message.success('导出功能开发中')
    },
    // 查看详情
    handleDetail(row) {
      getPaymentDetail(row.paymentId).then(response => {
        this.currentDetail = response.data
        this.detailOpen = true
      }).catch(error => {
        console.error('获取详情失败:', error)
      })
    },
    // 格式化金额
    formatMoney(value) {
      if (!value) return '0.00'
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    },
    // 获取状态类型
    getStatusType(status) {
      const typeMap = {
        '已完成': 'success',
        '处理中': 'warning',
        '已取消': 'info',
        '失败': 'danger'
      }
      return typeMap[status] || 'info'
    }
  }
}
</script>

<style scoped lang="scss">
.stats-cards {
  margin-bottom: 20px;

  .stat-card {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 20px;
    border-radius: 8px;
    color: white;
    text-align: center;
  }

  .stat-label {
    font-size: 14px;
    margin-bottom: 10px;
    opacity: 0.9;
  }

  .stat-value {
    font-size: 24px;
    font-weight: bold;
  }

  .el-col:nth-child(2) .stat-card {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }

  .el-col:nth-child(3) .stat-card {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }

  .el-col:nth-child(4) .stat-card {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  }
}
</style>
