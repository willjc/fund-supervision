<template>
  <div class="dashboard-container">
    <el-row :gutter="20" class="mb20">
      <!-- 标题区域 -->
      <el-col :span="24">
        <div class="dashboard-header">
          <h2>民政监管数据大屏</h2>
          <div class="header-time">{{ currentTime }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 核心统计卡片区 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card card-blue">
          <div class="stat-icon"><i class="el-icon-office-building"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.institution ? overview.institution.total : 0 }}</div>
            <div class="stat-label">养老机构总数</div>
            <div class="stat-detail">
              待审批: {{ overview.institution ? overview.institution.pending : 0 }} |
              运营中: {{ overview.institution ? overview.institution.approved : 0 }}
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card shadow="hover" class="stat-card card-green">
          <div class="stat-icon"><i class="el-icon-user"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.elder ? overview.elder.total : 0 }}</div>
            <div class="stat-label">入住老人总数</div>
            <div class="stat-detail">
              在住: {{ overview.elder ? overview.elder.active : 0 }}
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card shadow="hover" class="stat-card card-orange">
          <div class="stat-icon"><i class="el-icon-money"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ formatAmount(overview.transfer ? overview.transfer.totalAmount : 0) }}</div>
            <div class="stat-label">资金划拨总额(万元)</div>
            <div class="stat-detail">
              待审: {{ overview.transfer ? overview.transfer.pending : 0 }} |
              已批: {{ overview.transfer ? overview.transfer.approved : 0 }}
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card shadow="hover" class="stat-card card-red">
          <div class="stat-icon"><i class="el-icon-refresh-left"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ formatAmount(overview.refund ? overview.refund.totalAmount : 0) }}</div>
            <div class="stat-label">退款总额(万元)</div>
            <div class="stat-detail">
              待审: {{ overview.refund ? overview.refund.pending : 0 }} |
              成功: {{ overview.refund ? overview.refund.success : 0 }}
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card shadow="hover" class="stat-card card-purple">
          <div class="stat-icon"><i class="el-icon-wallet"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.deposit ? overview.deposit.pending : 0 }}</div>
            <div class="stat-label">押金申请</div>
            <div class="stat-detail">
              待审: {{ overview.deposit ? overview.deposit.pending : 0 }} |
              已批: {{ overview.deposit ? overview.deposit.approved : 0 }}
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="4">
        <el-card shadow="hover" class="stat-card card-warning">
          <div class="stat-icon"><i class="el-icon-warning"></i></div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.warning ? overview.warning.active : 0 }}</div>
            <div class="stat-label">待处理预警</div>
            <div class="stat-detail">
              未处理: {{ overview.warning ? overview.warning.active : 0 }} |
              已处理: {{ overview.warning ? overview.warning.resolved : 0 }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待办事项区域 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="24">
        <el-card shadow="hover" class="todo-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-s-order"></i> 待办事项</span>
            <span class="total-count">总计: {{ todos.totalCount || 0 }} 项</span>
          </div>
          <el-row :gutter="15">
            <el-col :span="4">
              <div class="todo-item" @click="goToInstitutionApproval">
                <div class="todo-number">{{ todos.institutionCount || 0 }}</div>
                <div class="todo-label">机构审批</div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="todo-item" @click="goToTransferApproval">
                <div class="todo-number">{{ todos.transferCount || 0 }}</div>
                <div class="todo-label">资金划拨</div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="todo-item" @click="goToRefundApproval">
                <div class="todo-number">{{ todos.refundCount || 0 }}</div>
                <div class="todo-label">退款审批</div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="todo-item" @click="goToDepositApproval">
                <div class="todo-number">{{ todos.depositCount || 0 }}</div>
                <div class="todo-label">押金申请</div>
              </div>
            </el-col>
            <el-col :span="4">
              <div class="todo-item" @click="goToWarning">
                <div class="todo-number">{{ todos.warningCount || 0 }}</div>
                <div class="todo-label">余额预警</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-pie-chart"></i> 机构状态分布</span>
          </div>
          <div ref="institutionChart" style="height: 300px"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-pie-chart"></i> 资金划拨类型</span>
          </div>
          <div ref="transferChart" style="height: 300px"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-pie-chart"></i> 预警级别分布</span>
          </div>
          <div ref="warningChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getOverview, getTodos, getCharts } from "@/api/pension/supervisionDashboard";
import * as echarts from 'echarts';

export default {
  name: "SupervisionDashboard",
  data() {
    return {
      // 当前时间
      currentTime: '',
      // 概览数据
      overview: {},
      // 待办事项
      todos: {},
      // 图表数据
      charts: {},
      // 定时器
      timer: null
    };
  },
  created() {
    this.updateTime();
    this.loadData();
    // 每30秒刷新一次数据
    this.timer = setInterval(() => {
      this.loadData();
    }, 30000);
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
  methods: {
    /** 更新当前时间 */
    updateTime() {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      const hour = String(now.getHours()).padStart(2, '0');
      const minute = String(now.getMinutes()).padStart(2, '0');
      const second = String(now.getSeconds()).padStart(2, '0');
      this.currentTime = `${year}-${month}-${day} ${hour}:${minute}:${second}`;

      setTimeout(this.updateTime, 1000);
    },
    /** 加载所有数据 */
    loadData() {
      this.loadOverview();
      this.loadTodos();
      this.loadCharts();
    },
    /** 加载概览数据 */
    loadOverview() {
      getOverview().then(response => {
        this.overview = response.data;
      });
    },
    /** 加载待办事项 */
    loadTodos() {
      getTodos().then(response => {
        this.todos = response.data;
      });
    },
    /** 加载图表数据 */
    loadCharts() {
      getCharts().then(response => {
        this.charts = response.data;
        this.$nextTick(() => {
          this.renderInstitutionChart();
          this.renderTransferChart();
          this.renderWarningChart();
        });
      });
    },
    /** 渲染机构状态图表 */
    renderInstitutionChart() {
      const chart = echarts.init(this.$refs.institutionChart);
      const option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          bottom: '0%',
          left: 'center'
        },
        series: [
          {
            name: '机构状态',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '20',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.charts.institutionByStatus || []
          }
        ]
      };
      chart.setOption(option);
    },
    /** 渲染资金划拨图表 */
    renderTransferChart() {
      const chart = echarts.init(this.$refs.transferChart);
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
        },
        legend: {
          bottom: '0%',
          left: 'center'
        },
        series: [
          {
            name: '划拨类型',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '20',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.charts.transferByType || []
          }
        ]
      };
      chart.setOption(option);
    },
    /** 渲染预警级别图表 */
    renderWarningChart() {
      const chart = echarts.init(this.$refs.warningChart);
      const option = {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          bottom: '0%',
          left: 'center'
        },
        series: [
          {
            name: '预警级别',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '20',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.charts.warningByLevel || []
          }
        ],
        color: ['#67C23A', '#E6A23C', '#F56C6C']
      };
      chart.setOption(option);
    },
    /** 格式化金额(转为万元) */
    formatAmount(amount) {
      if (!amount) return '0.00';
      return (amount / 10000).toFixed(2);
    },
    /** 跳转到机构审批 */
    goToInstitutionApproval() {
      this.$router.push('/supervision/institution/approval');
    },
    /** 跳转到资金划拨审批 */
    goToTransferApproval() {
      this.$router.push('/supervision/fundTransfer/approval');
    },
    /** 跳转到退款审批 */
    goToRefundApproval() {
      this.$router.push('/supervision/refund/approval');
    },
    /** 跳转到押金审批 */
    goToDepositApproval() {
      this.$router.push('/supervision/deposit/approval');
    },
    /** 跳转到预警管理 */
    goToWarning() {
      this.$router.push('/pension/warning');
    }
  }
};
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 84px);
}

.dashboard-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px 30px;
  border-radius: 8px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-header h2 {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
}

.header-time {
  font-size: 18px;
  font-weight: normal;
}

.stat-card {
  height: 140px;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  position: absolute;
  right: 20px;
  top: 20px;
  font-size: 50px;
  opacity: 0.3;
}

.stat-info {
  padding: 20px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-detail {
  font-size: 12px;
  color: #606266;
}

.card-blue { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }
.card-blue .stat-label, .card-blue .stat-detail { color: rgba(255,255,255,0.8); }

.card-green { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; }
.card-green .stat-label, .card-green .stat-detail { color: rgba(255,255,255,0.8); }

.card-orange { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); color: white; }
.card-orange .stat-label, .card-orange .stat-detail { color: rgba(255,255,255,0.8); }

.card-red { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); color: white; }
.card-red .stat-label, .card-red .stat-detail { color: rgba(255,255,255,0.8); }

.card-purple { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); color: white; }
.card-purple .stat-label, .card-purple .stat-detail { color: rgba(255,255,255,0.8); }

.card-warning { background: linear-gradient(135deg, #30cfd0 0%, #330867 100%); color: white; }
.card-warning .stat-label, .card-warning .stat-detail { color: rgba(255,255,255,0.8); }

.todo-card {
  background: white;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.total-count {
  font-size: 14px;
  color: #909399;
}

.todo-item {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.todo-item:hover {
  background: #ecf5ff;
  transform: translateY(-3px);
}

.todo-number {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.todo-label {
  font-size: 14px;
  color: #606266;
}

.mb20 {
  margin-bottom: 20px;
}
</style>
