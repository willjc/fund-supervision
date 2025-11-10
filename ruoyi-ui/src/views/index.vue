<template>
  <div class="app-container dashboard">
    <!-- 核心业务统计卡片 -->
    <el-row :gutter="20" class="statistics-cards">
      <el-col :span="4">
        <div class="stat-card institution">
          <div class="stat-icon">
            <i class="el-icon-office-building"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ overview.institutionCount }}</div>
            <div class="stat-label">监管机构总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card elderly">
          <div class="stat-icon">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ overview.elderlyCount }}</div>
            <div class="stat-label">在院老人总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card funds">
          <div class="stat-icon">
            <i class="el-icon-money"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">¥{{ formatNumber(overview.totalFunds) }}</div>
            <div class="stat-label">监管资金总额</div>
          </div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card beds">
          <div class="stat-icon">
            <i class="el-icon-house"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ overview.bedUsageRate }}%</div>
            <div class="stat-label">床位使用率</div>
          </div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card warnings">
          <div class="stat-icon">
            <i class="el-icon-warning"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ overview.todayWarnings }}</div>
            <div class="stat-label">今日预警</div>
          </div>
        </div>
      </el-col>
      <el-col :span="4">
        <div class="stat-card applications">
          <div class="stat-icon">
            <i class="el-icon-document"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ overview.pendingApplications }}</div>
            <div class="stat-label">待处理申请</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 主要内容区域 -->
    <el-row :gutter="20" class="main-content">
      <!-- 左侧：资金监管概览 -->
      <el-col :span="8">
        <el-card class="funds-overview">
          <div slot="header" class="card-header">
            <span><i class="el-icon-money"></i> 资金监管概览</span>
            <el-button type="text" @click="refreshFunds">刷新</el-button>
          </div>

          <!-- 资金趋势图 -->
          <div id="fundTrendChart" class="chart-container"></div>

          <!-- 各机构资金余额 -->
          <div class="institution-funds">
            <h4>各机构资金余额</h4>
            <div v-for="item in fundsOverview.institutionFunds" :key="item.name" class="fund-item">
              <div class="fund-info">
                <span class="fund-name">{{ item.name }}</span>
                <span class="fund-amount">¥{{ formatNumber(item.balance) }}</span>
              </div>
              <div class="fund-change" :class="item.change >= 0 ? 'positive' : 'negative'">
                {{ item.change >= 0 ? '+' : '' }}{{ formatNumber(item.change) }}
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 中间：机构运营状态 -->
      <el-col :span="8">
        <el-card class="operations-overview">
          <div slot="header" class="card-header">
            <span><i class="el-icon-data-line"></i> 机构运营状态</span>
            <el-button type="text" @click="refreshOperations">刷新</el-button>
          </div>

          <!-- 审批进度 -->
          <div class="approval-progress">
            <h4>今日申请审批</h4>
            <el-row :gutter="10">
              <el-col :span="8">
                <div class="progress-item pending">
                  <div class="progress-number">{{ operationsOverview.pendingApplications }}</div>
                  <div class="progress-label">待审批</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="progress-item approved">
                  <div class="progress-number">{{ operationsOverview.approvedToday }}</div>
                  <div class="progress-label">已通过</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="progress-item rejected">
                  <div class="progress-number">{{ operationsOverview.rejectedToday }}</div>
                  <div class="progress-label">已拒绝</div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 床位使用情况 -->
          <div class="bed-usage">
            <h4>床位使用情况</h4>
            <div v-for="item in operationsOverview.bedUsage" :key="item.name" class="bed-item">
              <div class="bed-info">
                <span class="bed-name">{{ item.name }}</span>
                <span class="bed-rate">{{ item.rate }}%</span>
              </div>
              <el-progress :percentage="item.rate" :color="getProgressColor(item.rate)"></el-progress>
              <div class="bed-detail">{{ item.used }}/{{ item.total }} 床位</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：风险监控预警 -->
      <el-col :span="8">
        <el-card class="risk-overview">
          <div slot="header" class="card-header">
            <span><i class="el-icon-warning-outline"></i> 风险监控预警</span>
            <el-button type="text" @click="refreshRisk">刷新</el-button>
          </div>

          <!-- 预警等级分布 -->
          <div class="warning-levels">
            <h4>预警等级分布</h4>
            <el-row :gutter="10">
              <el-col :span="8">
                <div class="warning-level high">
                  <div class="level-number">{{ riskOverview.warningLevels.high }}</div>
                  <div class="level-label">高风险</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="warning-level medium">
                  <div class="level-number">{{ riskOverview.warningLevels.medium }}</div>
                  <div class="level-label">中风险</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="warning-level low">
                  <div class="level-number">{{ riskOverview.warningLevels.low }}</div>
                  <div class="level-label">低风险</div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 待处理预警列表 -->
          <div class="pending-warnings">
            <h4>待处理预警</h4>
            <div v-for="warning in riskOverview.pendingWarnings" :key="warning.id" class="warning-item">
              <div class="warning-header">
                <span class="warning-institution">{{ warning.institution }}</span>
                <el-tag :type="getWarningTagType(warning.level)" size="mini">{{ warning.level }}</el-tag>
              </div>
              <div class="warning-content">{{ warning.content }}</div>
              <div class="warning-footer">
                <span class="warning-time">{{ warning.time }}</span>
                <el-tag :type="getStatusTagType(warning.status)" size="mini">{{ warning.status }}</el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部：快捷操作和实时信息 -->
    <el-row :gutter="20" class="bottom-section">
      <!-- 快捷操作 -->
      <el-col :span="12">
        <el-card class="quick-actions">
          <div slot="header" class="card-header">
            <span><i class="el-icon-s-operation"></i> 快捷操作</span>
          </div>
          <el-row :gutter="15">
            <el-col :span="6">
              <div class="action-item" @click="goToInstitutionApproval">
                <i class="el-icon-document-checked"></i>
                <span>机构审批</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="action-item" @click="goToWarningCenter">
                <i class="el-icon-warning-outline"></i>
                <span>预警处理</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="action-item" @click="goToBigScreen('institution')">
                <i class="el-icon-bell"></i>
                <span>机构大屏</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="action-item" @click="goToBigScreen('fund')">
                <i class="el-icon-coin"></i>
                <span>资金大屏</span>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="15" style="margin-top: 15px;">
            <el-col :span="6">
              <div class="action-item" @click="goToBigScreen('warning')">
                <i class="el-icon-bell"></i>
                <span>预警大屏</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="action-item" @click="downloadReport">
                <i class="el-icon-download"></i>
                <span>报表下载</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="action-item" @click="goToBedManagement">
                <i class="el-icon-house"></i>
                <span>床位管理</span>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="action-item" @click="goToElderlyManagement">
                <i class="el-icon-user-solid"></i>
                <span>老人管理</span>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

      <!-- 大额资金异动 -->
      <el-col :span="12">
        <el-card class="large-transactions">
          <div slot="header" class="card-header">
            <span><i class="el-icon-coin"></i> 大额资金异动</span>
            <el-button type="text" @click="refreshTransactions">刷新</el-button>
          </div>
          <div v-for="transaction in fundsOverview.largeTransactions" :key="transaction.time" class="transaction-item">
            <div class="transaction-info">
              <div class="transaction-institution">{{ transaction.institution }}</div>
              <div class="transaction-amount" :class="transaction.type === '收入' ? 'income' : 'expense'">
                {{ transaction.type }} ¥{{ formatNumber(transaction.amount) }}
              </div>
            </div>
            <div class="transaction-footer">
              <span class="transaction-time">{{ transaction.time }}</span>
              <el-tag :type="transaction.status === '正常' ? 'success' : 'warning'" size="mini">
                {{ transaction.status }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDashboardOverview, getFundsOverview, getOperationsOverview, getRiskOverview } from '@/api/pension/dashboard'

export default {
  name: "Dashboard",
  data() {
    return {
      overview: {
        institutionCount: 0,
        elderlyCount: 0,
        totalFunds: 0,
        bedUsageRate: 0,
        todayWarnings: 0,
        pendingApplications: 0
      },
      fundsOverview: {
        fundTrend: [],
        institutionFunds: [],
        largeTransactions: []
      },
      operationsOverview: {
        pendingApplications: 0,
        approvedToday: 0,
        rejectedToday: 0,
        bedUsage: [],
        elderlyTrend: []
      },
      riskOverview: {
        warningLevels: { high: 0, medium: 0, low: 0 },
        pendingWarnings: [],
        highRiskInstitutions: []
      },
      fundChart: null
    }
  },
  mounted() {
    this.loadDashboardData()
    this.initTimer()
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
    if (this.fundChart) {
      this.fundChart.dispose()
    }
  },
  methods: {
    formatNumber(num) {
      if (!num) return '0'
      return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    },

    async loadDashboardData() {
      try {
        const [overviewRes, fundsRes, operationsRes, riskRes] = await Promise.all([
          getDashboardOverview(),
          getFundsOverview(),
          getOperationsOverview(),
          getRiskOverview()
        ])

        this.overview = overviewRes.data
        this.fundsOverview = fundsRes.data
        this.operationsOverview = operationsRes.data
        this.riskOverview = riskRes.data

        this.$nextTick(() => {
          this.initFundChart()
        })
      } catch (error) {
        console.error('加载首页数据失败:', error)
        this.$message.error('加载数据失败')
      }
    },

    initFundChart() {
      if (!this.fundChart) {
        this.fundChart = echarts.init(document.getElementById('fundTrendChart'))
      }

      const dates = this.fundsOverview.fundTrend.map(item => item.date.substring(5))
      const incomeData = this.fundsOverview.fundTrend.map(item => item.income / 10000);
      const expenseData = this.fundsOverview.fundTrend.map(item => item.expense / 10000);

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['收入', '支出'],
          top: 0
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates
        },
        yAxis: {
          type: 'value',
          name: '金额(万元)'
        },
        series: [
          {
            name: '收入',
            type: 'line',
            smooth: true,
            data: incomeData,
            itemStyle: {
              color: '#67C23A'
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: 'rgba(103, 194, 58, 0.3)'
                }, {
                  offset: 1, color: 'rgba(103, 194, 58, 0.05)'
                }]
              }
            }
          },
          {
            name: '支出',
            type: 'line',
            smooth: true,
            data: expenseData,
            itemStyle: {
              color: '#F56C6C'
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: 'rgba(245, 108, 108, 0.3)'
                }, {
                  offset: 1, color: 'rgba(245, 108, 108, 0.05)'
                }]
              }
            }
          }
        ]
      }

      this.fundChart.setOption(option)
    },

    initTimer() {
      this.timer = setInterval(() => {
        this.loadDashboardData()
      }, 30000) // 30秒自动刷新
    },

    refreshFunds() {
      this.loadDashboardData()
    },

    refreshOperations() {
      this.loadDashboardData()
    },

    refreshRisk() {
      this.loadDashboardData()
    },

    refreshTransactions() {
      this.loadDashboardData()
    },

    getProgressColor(rate) {
      if (rate >= 90) return '#F56C6C'
      if (rate >= 80) return '#E6A23C'
      return '#67C23A'
    },

    getWarningTagType(level) {
      switch (level) {
        case '高': return 'danger'
        case '中': return 'warning'
        case '低': return 'info'
        default: return 'info'
      }
    },

    getStatusTagType(status) {
      switch (status) {
        case '待处理': return 'danger'
        case '处理中': return 'warning'
        case '已处理': return 'success'
        default: return 'info'
      }
    },

    // 快捷操作方法
    goToInstitutionApproval() {
      this.$router.push('/pension/institution/approval')
    },

    goToWarningCenter() {
      this.$router.push('/pension/warning/center')
    },

    goToBigScreen(type) {
      const screenUrls = {
        'institution': '/screen/institution-distribution.html',
        'fund': '/screen/fund-supervision.html',
        'warning': '/screen/warning-monitor.html'
      }
      if (screenUrls[type]) {
        window.open(screenUrls[type], '_blank')
      }
    },

    downloadReport() {
      this.$message.info('报表下载功能开发中...')
    },

    goToBedManagement() {
      this.$router.push('/pension/bed/management')
    },

    goToElderlyManagement() {
      this.$router.push('/pension/elderly/management')
    }
  }
}
</script>

<style scoped lang="scss">
.dashboard {
  padding: 20px;
  background-color: #f5f5f5;
}

.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 10px;
  padding: 20px;
  color: white;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-2px);
  }

  &.institution {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  &.elderly {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }

  &.funds {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }

  &.beds {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  }

  &.warnings {
    background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  }

  &.applications {
    background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
  }
}

.stat-icon {
  font-size: 32px;
  margin-right: 15px;
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.main-content {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  color: #303133;

  i {
    margin-right: 5px;
    color: #409EFF;
  }
}

.chart-container {
  height: 200px;
  margin-bottom: 20px;
}

.institution-funds {
  h4 {
    margin-bottom: 10px;
    color: #303133;
  }
}

.fund-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.fund-info {
  flex: 1;
}

.fund-name {
  font-size: 14px;
  color: #303133;
  display: block;
}

.fund-amount {
  font-size: 12px;
  color: #909399;
  font-weight: bold;
}

.fund-change {
  font-size: 12px;
  font-weight: bold;

  &.positive {
    color: #67C23A;
  }

  &.negative {
    color: #F56C6C;
  }
}

.approval-progress {
  margin-bottom: 20px;

  h4 {
    margin-bottom: 10px;
    color: #303133;
  }
}

.progress-item {
  text-align: center;
  padding: 15px;
  border-radius: 8px;
  color: white;

  &.pending {
    background: linear-gradient(135deg, #E6A23C 0%, #F56C6C 100%);
  }

  &.approved {
    background: linear-gradient(135deg, #67C23A 0%, #409EFF 100%);
  }

  &.rejected {
    background: linear-gradient(135deg, #F56C6C 0%, #E6A23C 100%);
  }
}

.progress-number {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 5px;
}

.progress-label {
  font-size: 12px;
}

.bed-usage {
  h4 {
    margin-bottom: 10px;
    color: #303133;
  }
}

.bed-item {
  margin-bottom: 15px;

  .bed-info {
    display: flex;
    justify-content: space-between;
    margin-bottom: 5px;
  }

  .bed-name {
    font-size: 14px;
    color: #303133;
  }

  .bed-rate {
    font-size: 14px;
    font-weight: bold;
    color: #409EFF;
  }

  .bed-detail {
    font-size: 12px;
    color: #909399;
    margin-top: 5px;
  }
}

.warning-levels {
  margin-bottom: 20px;

  h4 {
    margin-bottom: 10px;
    color: #303133;
  }
}

.warning-level {
  text-align: center;
  padding: 15px;
  border-radius: 8px;
  color: white;

  &.high {
    background: linear-gradient(135deg, #F56C6C 0%, #E6A23C 100%);
  }

  &.medium {
    background: linear-gradient(135deg, #E6A23C 0%, #F7BA2A 100%);
  }

  &.low {
    background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  }
}

.level-number {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 5px;
}

.level-label {
  font-size: 12px;
}

.pending-warnings {
  h4 {
    margin-bottom: 10px;
    color: #303133;
  }
}

.warning-item {
  background: #fef5e7;
  border: 1px solid #f39c12;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 10px;

  .warning-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 5px;
  }

  .warning-institution {
    font-weight: bold;
    color: #303133;
  }

  .warning-content {
    font-size: 14px;
    color: #E6A23C;
    margin-bottom: 8px;
  }

  .warning-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .warning-time {
    font-size: 12px;
    color: #909399;
  }
}

.quick-actions {
  .el-card__body {
    padding: 20px;
  }
}

.action-item {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #409EFF 0%, #36D1DC 100%);
  border-radius: 8px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }

  i {
    font-size: 24px;
    display: block;
    margin-bottom: 8px;
  }

  span {
    font-size: 14px;
  }
}

.large-transactions {
  .transaction-item {
    border-left: 4px solid #409EFF;
    padding: 12px;
    margin-bottom: 10px;
    background: #f8f9fa;
    border-radius: 0 6px 6px 0;

    .transaction-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 5px;
    }

    .transaction-institution {
      font-weight: bold;
      color: #303133;
    }

    .transaction-amount {
      font-weight: bold;

      &.income {
        color: #67C23A;
      }

      &.expense {
        color: #F56C6C;
      }
    }

    .transaction-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .transaction-time {
      font-size: 12px;
      color: #909399;
    }
  }
}

.el-card {
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);

  .el-card__header {
    background: #fafafa;
    border-bottom: 1px solid #ebeef5;
  }
}
</style>