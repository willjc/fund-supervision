<template>
  <div class="app-container">
    <!-- 资金统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-primary">
              <i class="el-icon-s-finance"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">今日收入</div>
              <div class="stat-value">¥{{ statistics.todayIncome | formatAmount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-success">
              <i class="el-icon-money"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">今日支出</div>
              <div class="stat-value">¥{{ statistics.todayExpenditure | formatAmount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-warning">
              <i class="el-icon-pie-chart"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">总余额</div>
              <div class="stat-value">¥{{ statistics.totalBalance | formatAmount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-info">
              <i class="el-icon-date"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">本月净流入</div>
              <div class="stat-value">¥{{ statistics.monthlyNetFlow | formatAmount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>资金流向趋势</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="refreshChart">刷新</el-button>
          </div>
          <div ref="trendChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>资金类型分布</span>
            <el-button style="float: right; padding: 3px 0" type="text" @click="refreshChart">刷新</el-button>
          </div>
          <div ref="pieChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="资金类型" prop="fundType">
        <el-select v-model="queryParams.fundType" placeholder="请选择资金类型" clearable size="small">
          <el-option label="预收款" value="预收款" />
          <el-option label="押金" value="押金" />
          <el-option label="会员费" value="会员费" />
          <el-option label="政府补贴" value="政府补贴" />
          <el-option label="其他资金" value="其他资金" />
        </el-select>
      </el-form-item>
      <el-form-item label="交易方向" prop="direction">
        <el-select v-model="queryParams.direction" placeholder="请选择交易方向" clearable size="small">
          <el-option label="收入" value="收入" />
          <el-option label="支出" value="支出" />
        </el-select>
      </el-form-item>
      <el-form-item label="金额范围" prop="amountRange">
        <el-input-number
          v-model="queryParams.minAmount"
          placeholder="最小金额"
          :precision="2"
          :min="0"
          size="small"
          style="width: 120px">
        </el-input-number>
        <span style="margin: 0 10px;">-</span>
        <el-input-number
          v-model="queryParams.maxAmount"
          placeholder="最大金额"
          :precision="2"
          :min="0"
          size="small"
          style="width: 120px">
        </el-input-number>
      </el-form-item>
      <el-form-item label="交易时间" prop="dateRange">
        <el-date-picker
          v-model="queryParams.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          size="small">
        </el-date-picker>
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
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出记录</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-printer"
          size="mini"
          @click="handlePrint"
        >打印报表</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="交易编号" align="center" prop="transactionNo" width="160" />
      <el-table-column label="机构名称" align="center" prop="institutionName" :show-overflow-tooltip="true" />
      <el-table-column label="资金类型" align="center" prop="fundType" width="100">
        <template slot-scope="scope">
          <el-tag size="mini" :type="getFundTypeTagType(scope.row.fundType)">
            {{ scope.row.fundType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="交易方向" align="center" prop="direction" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.direction === '收入'" type="success" size="mini">收入</el-tag>
          <el-tag v-else type="danger" size="mini">支出</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="交易金额" align="center" prop="amount" width="120">
        <template slot-scope="scope">
          <span :class="getAmountClass(scope.row.amount, scope.row.direction)">
            ¥{{ scope.row.amount | formatAmount }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="交易前余额" align="center" prop="balanceBefore" width="120">
        <template slot-scope="scope">
          <span>¥{{ scope.row.balanceBefore | formatAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="交易后余额" align="center" prop="balanceAfter" width="120">
        <template slot-scope="scope">
          <span>¥{{ scope.row.balanceAfter | formatAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="交易时间" align="center" prop="transactionTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.transactionTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="摘要" align="center" prop="description" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="操作员" align="center" prop="operator" width="100" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-printer"
            @click="handlePrintRecord(scope.row)"
          >打印</el-button>
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

    <!-- 交易详情对话框 -->
    <el-dialog title="资金交易详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="交易编号">{{ detailData.transactionNo }}</el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ detailData.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="资金类型">{{ detailData.fundType }}</el-descriptions-item>
        <el-descriptions-item label="交易方向">
          <el-tag v-if="detailData.direction === '收入'" type="success">收入</el-tag>
          <el-tag v-else type="danger">支出</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="交易金额">
          <span :class="getAmountClass(detailData.amount, detailData.direction)">
            ¥{{ detailData.amount | formatAmount }}
          </span>
        </el-descriptions-item>
        <el-descriptions-item label="交易前余额">¥{{ detailData.balanceBefore | formatAmount }}</el-descriptions-item>
        <el-descriptions-item label="交易后余额">¥{{ detailData.balanceAfter | formatAmount }}</el-descriptions-item>
        <el-descriptions-item label="交易时间">{{ parseTime(detailData.transactionTime) }}</el-descriptions-item>
        <el-descriptions-item label="交易摘要" :span="2">{{ detailData.description }}</el-descriptions-item>
        <el-descriptions-item label="操作员">{{ detailData.operator }}</el-descriptions-item>
        <el-descriptions-item label="关联单号">{{ detailData.relatedOrderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="对方账户" :span="2">{{ detailData.counterAccount || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
        <el-button type="primary" @click="handlePrintDetail">打印凭证</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFundRecord } from '@/api/supervision/fund'
import * as echarts from 'echarts'

export default {
  name: 'FundRecordList',
  filters: {
    formatAmount(value) {
      if (!value) return '0.00';
      return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }
  },
  data() {
    return {
      loading: true,
      ids: [],
      showSearch: true,
      total: 0,
      recordList: [],
      detailOpen: false,
      statistics: {
        todayIncome: 0,
        todayExpenditure: 0,
        totalBalance: 0,
        monthlyNetFlow: 0
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        fundType: null,
        direction: null,
        minAmount: null,
        maxAmount: null,
        dateRange: null
      },
      detailData: {},
      trendChart: null,
      pieChart: null
    }
  },
  mounted() {
    this.initCharts()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.trendChart) {
      this.trendChart.dispose()
    }
    if (this.pieChart) {
      this.pieChart.dispose()
    }
  },
  methods: {
    getList() {
      this.loading = true
      listFundRecord(this.queryParams).then(response => {
        // TODO: 连接真实API
        // this.recordList = response.rows
        // this.total = response.total
        // this.loading = false

        // 模拟数据
        setTimeout(() => {
          this.recordList = [
            {
              recordId: 1,
              transactionNo: 'TR202501030001',
              institutionName: '幸福养老院',
              fundType: '预收款',
              direction: '收入',
              amount: 50000.00,
              balanceBefore: 1517890.50,
              balanceAfter: 1567890.50,
              transactionTime: '2025-01-03 14:30:00',
              description: '月度预收款-张奶奶',
              operator: '管理员1',
              relatedOrderNo: 'DD20250103001',
              counterAccount: '个人账户-张奶奶',
              remark: '月度服务费预收'
            },
            {
              recordId: 2,
              transactionNo: 'TR202501030002',
              institutionName: '幸福养老院',
              fundType: '会员费',
              direction: '收入',
              amount: 20000.00,
              balanceBefore: 1497890.50,
              balanceAfter: 1517890.50,
              transactionTime: '2025-01-03 10:15:00',
              description: '会员费收取',
              operator: '管理员2',
              relatedOrderNo: null,
              counterAccount: '个人账户-李爷爷',
              remark: '月度会员费'
            },
            {
              recordId: 3,
              transactionNo: 'TR202501030003',
              institutionName: '夕阳红公寓',
              fundType: '押金',
              direction: '收入',
              amount: 30000.00,
              balanceBefore: 957654.32,
              balanceAfter: 987654.32,
              transactionTime: '2025-01-03 09:45:00',
              description: '入住押金-王奶奶',
              operator: '管理员3',
              relatedOrderNo: 'DD20250102002',
              counterAccount: '个人账户-王奶奶',
              remark: '首次入住押金'
            },
            {
              recordId: 4,
              transactionNo: 'TR202501020004',
              institutionName: '康乐养老中心',
              fundType: '预收款',
              direction: '支出',
              amount: -45000.00,
              balanceBefore: 1284567.89,
              balanceAfter: 1239567.89,
              transactionTime: '2025-01-02 16:20:00',
              description: '供应商付款-医疗设备',
              operator: '管理员4',
              relatedOrderNo: 'PO20250102001',
              counterAccount: 'XX医疗器械公司',
              remark: '月度设备采购付款'
            },
            {
              recordId: 5,
              transactionNo: 'TR202501020005',
              institutionName: '幸福养老院',
              fundType: '政府补贴',
              direction: '收入',
              amount: 100000.00,
              balanceBefore: 1417890.50,
              balanceAfter: 1517890.50,
              transactionTime: '2025-01-02 14:00:00',
              description: '养老服务补贴',
              operator: '管理员1',
              relatedOrderNo: null,
              counterAccount: '民政局补贴账户',
              remark: '政府季度养老服务补贴'
            }
          ]
          this.total = this.recordList.length
          this.loading = false
        }, 500)
      })
    },
    getStatistics() {
      // TODO: 调用真实API获取统计数据
      this.statistics = {
        todayIncome: 120000.00,
        todayExpenditure: 45000.00,
        totalBalance: 4234567.89,
        monthlyNetFlow: 285000.00
      }
    },
    initCharts() {
      this.$nextTick(() => {
        this.initTrendChart()
        this.initPieChart()
      })
    },
    initTrendChart() {
      const trendChartDom = this.$refs.trendChart
      if (!trendChartDom) return

      this.trendChart = echarts.init(trendChartDom)
      const option = {
        title: {
          text: '近7天资金流向',
          left: 'center',
          textStyle: {
            fontSize: 14
          }
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['收入', '支出'],
          bottom: 10
        },
        xAxis: {
          type: 'category',
          data: ['1-28', '1-29', '1-30', '1-31', '2-1', '2-2', '2-3']
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            formatter: '¥{value}'
          }
        },
        series: [
          {
            name: '收入',
            type: 'line',
            data: [120000, 150000, 180000, 160000, 200000, 170000, 120000],
            smooth: true,
            itemStyle: {
              color: '#67C23A'
            }
          },
          {
            name: '支出',
            type: 'line',
            data: [80000, 90000, 110000, 95000, 120000, 100000, 45000],
            smooth: true,
            itemStyle: {
              color: '#F56C6C'
            }
          }
        ]
      }
      this.trendChart.setOption(option)
    },
    initPieChart() {
      const pieChartDom = this.$refs.pieChart
      if (!pieChartDom) return

      this.pieChart = echarts.init(pieChartDom)
      const option = {
        title: {
          text: '资金类型分布',
          left: 'center',
          textStyle: {
            fontSize: 14
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
        },
        legend: {
          bottom: 10,
          data: ['预收款', '押金', '会员费', '政府补贴', '其他资金']
        },
        series: [
          {
            name: '资金类型',
            type: 'pie',
            radius: '50%',
            center: ['50%', '45%'],
            data: [
              { value: 450000, name: '预收款' },
              { value: 280000, name: '押金' },
              { value: 150000, name: '会员费' },
              { value: 200000, name: '政府补贴' },
              { value: 120000, name: '其他资金' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }
      this.pieChart.setOption(option)
    },
    refreshChart() {
      this.initTrendChart()
      this.initPieChart()
    },
    handleResize() {
      if (this.trendChart) {
        this.trendChart.resize()
      }
      if (this.pieChart) {
        this.pieChart.resize()
      }
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.recordId)
    },
    handleView(row) {
      this.detailData = row
      this.detailOpen = true
    },
    handleExport() {
      this.download('supervision/fund/record/export', {
        ...this.queryParams
      }, `fund_record_${new Date().getTime()}.xlsx`)
    },
    handlePrint() {
      // TODO: 实现打印功能
      this.$modal.msgInfo('打印功能开发中...')
    },
    handlePrintRecord(row) {
      // TODO: 实现打印功能
      this.$modal.msgInfo('打印功能开发中...')
    },
    handlePrintDetail() {
      // TODO: 实现打印功能
      this.$modal.msgInfo('打印功能开发中...')
    },
    getFundTypeTagType(type) {
      const typeMap = {
        '预收款': 'primary',
        '押金': 'warning',
        '会员费': 'success',
        '政府补贴': 'info',
        '其他资金': ''
      }
      return typeMap[type] || ''
    },
    getAmountClass(amount, direction) {
      if (direction === '收入') {
        return 'text-success'
      } else {
        return 'text-danger'
      }
    }
  }
}
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  font-size: 20px;
}

.bg-primary {
  background-color: #409EFF;
}

.bg-success {
  background-color: #67C23A;
}

.bg-warning {
  background-color: #E6A23C;
}

.bg-info {
  background-color: #909399;
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.text-success {
  color: #67C23A;
  font-weight: bold;
}

.text-danger {
  color: #F56C6C;
  font-weight: bold;
}

.mb20 {
  margin-bottom: 20px;
}
</style>