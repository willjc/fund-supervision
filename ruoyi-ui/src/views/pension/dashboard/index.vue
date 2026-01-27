<template>
  <div class="pension-dashboard">
    <!-- 核心统计卡片 -->
    <el-row :gutter="20" class="dashboard-header">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.elderCount }}</div>
            <div class="stat-label">在住老人</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <i class="el-icon-s-order"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.orderCount }}</div>
            <div class="stat-label">本月订单</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <i class="el-icon-wallet"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">¥{{ formatMoney(statistics.accountBalance) }}</div>
            <div class="stat-label">账户余额</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%)">
            <i class="el-icon-money"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">¥{{ formatMoney(statistics.monthIncome) }}</div>
            <div class="stat-label">本月收入</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 资金统计 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="dashboard-card">
          <div slot="header" class="card-header">
            <span>资金统计</span>
            <el-button type="text" @click="refreshFundData">刷新</el-button>
          </div>
          <div class="fund-stats">
            <div class="fund-item">
              <span class="fund-label">监管账户余额</span>
              <span class="fund-value">¥{{ formatMoney(fundData.supervisionBalance) }}</span>
            </div>
            <div class="fund-item">
              <span class="fund-label">押金余额</span>
              <span class="fund-value">¥{{ formatMoney(fundData.depositBalance) }}</span>
            </div>
            <div class="fund-item">
              <span class="fund-label">会员余额</span>
              <span class="fund-value">¥{{ formatMoney(fundData.memberBalance) }}</span>
            </div>
            <div class="fund-item">
              <span class="fund-label">今日收入</span>
              <span class="fund-value success">¥{{ formatMoney(fundData.todayIncome) }}</span>
            </div>
            <div class="fund-item">
              <span class="fund-label">今日支出</span>
              <span class="fund-value danger">¥{{ formatMoney(fundData.todayExpense) }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card class="dashboard-card">
          <div slot="header" class="card-header">
            <span>待处理事项</span>
            <el-badge :value="pendingCount" class="badge-item"></el-badge>
          </div>
          <div class="pending-list">
            <div class="pending-item" v-for="item in pendingItems" :key="item.id" @click="handlePending(item)">
              <div class="pending-icon">
                <i :class="item.icon" :style="{color: item.color}"></i>
              </div>
              <div class="pending-content">
                <div class="pending-title">{{ item.title }}</div>
                <div class="pending-time">{{ item.time }}</div>
              </div>
              <div class="pending-count">
                <el-tag :type="item.type" size="small">{{ item.count }}</el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 申请审批进度 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="dashboard-card">
          <div slot="header" class="card-header">
            <span>申请审批进度</span>
            <el-button type="text" @click="viewAllApplications">查看全部</el-button>
          </div>
          <el-table :data="applications" style="width: 100%">
            <el-table-column prop="id" label="申请编号" width="120"></el-table-column>
            <el-table-column prop="type" label="申请类型" width="120"></el-table-column>
            <el-table-column prop="amount" label="申请金额" width="150">
              <template slot-scope="scope">
                ¥{{ formatMoney(scope.row.amount) }}
              </template>
            </el-table-column>
            <el-table-column prop="reason" label="申请原因"></el-table-column>
            <el-table-column prop="createTime" label="申请时间" width="180"></el-table-column>
            <el-table-column prop="status" label="审批状态" width="120">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="viewDetail(scope.row)">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="dashboard-card">
          <div slot="header">快捷操作</div>
          <div class="quick-actions">
            <el-button icon="el-icon-user-solid" @click="goTo('/pension/elder/checkin')">老人入住</el-button>
            <el-button icon="el-icon-s-order" @click="goTo('/pension/order/orderInfo')">创建订单</el-button>
            <el-button icon="el-icon-money" @click="goTo('/pension/deposit/apply')">押金申请</el-button>
            <el-button icon="el-icon-wallet" @click="goTo('/pension/fund/transfer')">资金划拨</el-button>
            <el-button icon="el-icon-s-data" @click="goTo('/pension/bank/supervision')">查看流水</el-button>
            <el-button icon="el-icon-bell" @click="goTo('/pension/announcement')">查看公告</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'PensionDashboard',
  data() {
    return {
      // 核心统计数据
      statistics: {
        elderCount: 0,
        orderCount: 0,
        accountBalance: 0,
        monthIncome: 0
      },
      // 资金数据
      fundData: {
        supervisionBalance: 0,
        depositBalance: 0,
        memberBalance: 0,
        todayIncome: 0,
        todayExpense: 0
      },
      // 待处理事项
      pendingItems: [],
      pendingCount: 0,
      // 申请列表
      applications: []
    }
  },
  created() {
    this.loadDashboardData()
  },
  methods: {
    // 加载仪表板数据
    loadDashboardData() {
      this.loadStatistics()
      this.loadFundData()
      this.loadPendingItems()
      this.loadApplications()
    },
    // 加载统计数据
    loadStatistics() {
      // TODO: 调用后端API获取数据
      // 临时使用模拟数据
      this.statistics = {
        elderCount: 156,
        orderCount: 89,
        accountBalance: 2345678.90,
        monthIncome: 456789.00
      }
    },
    // 加载资金数据
    loadFundData() {
      // TODO: 调用后端API
      this.fundData = {
        supervisionBalance: 1234567.89,
        depositBalance: 456789.00,
        memberBalance: 654321.01,
        todayIncome: 45678.90,
        todayExpense: 12345.67
      }
    },
    // 加载待处理事项
    loadPendingItems() {
      // TODO: 调用后端API
      this.pendingItems = [
        {
          id: 1,
          icon: 'el-icon-money',
          color: '#f56c6c',
          title: '押金使用申请',
          time: '2小时前',
          count: 3,
          type: 'danger'
        },
        {
          id: 2,
          icon: 'el-icon-wallet',
          color: '#e6a23c',
          title: '资金划拨申请',
          time: '5小时前',
          count: 2,
          type: 'warning'
        },
        {
          id: 3,
          icon: 'el-icon-bell',
          color: '#409eff',
          title: '新公告通知',
          time: '1天前',
          count: 1,
          type: 'info'
        }
      ]
      this.pendingCount = this.pendingItems.reduce((sum, item) => sum + item.count, 0)
    },
    // 加载申请列表
    loadApplications() {
      // TODO: 调用后端API
      this.applications = [
        {
          id: 'YJ202501001',
          type: '押金使用',
          amount: 50000,
          reason: '设备维修费用',
          createTime: '2025-01-03 10:30:00',
          status: '待审批'
        },
        {
          id: 'ZJ202501002',
          type: '资金划拨',
          amount: 100000,
          reason: '员工工资发放',
          createTime: '2025-01-02 14:20:00',
          status: '审批中'
        }
      ]
    },
    // 刷新资金数据
    refreshFundData() {
      this.loadFundData()
      this.$message.success('数据已刷新')
    },
    // 处理待办事项
    handlePending(item) {
      if (item.id === 1) {
        this.goTo('/pension/deposit/list')
      } else if (item.id === 2) {
        this.goTo('/pension/fund/record')
      } else if (item.id === 3) {
        this.goTo('/pension/announcement')
      }
    },
    // 查看所有申请
    viewAllApplications() {
      // 根据实际情况跳转到申请列表页面
      this.$message.info('跳转到申请列表')
    },
    // 查看详情
    viewDetail(row) {
      this.$message.info('查看详情: ' + row.id)
    },
    // 页面跳转
    goTo(path) {
      this.$router.push(path)
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
        '待审批': 'warning',
        '审批中': 'primary',
        '已通过': 'success',
        '已驳回': 'danger'
      }
      return typeMap[status] || 'info'
    }
  }
}
</script>

<style scoped lang="scss">
.pension-dashboard {
  padding: 20px;
  background: #f0f2f5;
  min-height: calc(100vh - 84px);
}

.dashboard-header {
  margin-bottom: 0;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 20px 0 rgba(0,0,0,0.1);
  }
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;

  i {
    font-size: 28px;
    color: white;
  }
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.dashboard-card {
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);

  ::v-deep .el-card__header {
    border-bottom: 1px solid #f0f0f0;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.fund-stats {
  .fund-item {
    display: flex;
    justify-content: space-between;
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }
  }

  .fund-label {
    color: #606266;
    font-size: 14px;
  }

  .fund-value {
    font-weight: bold;
    font-size: 16px;
    color: #303133;

    &.success {
      color: #67c23a;
    }

    &.danger {
      color: #f56c6c;
    }
  }
}

.pending-list {
  .pending-item {
    display: flex;
    align-items: center;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 10px;
    background: #f9f9f9;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: #f0f0f0;
    }

    &:last-child {
      margin-bottom: 0;
    }
  }

  .pending-icon {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: white;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;

    i {
      font-size: 20px;
    }
  }

  .pending-content {
    flex: 1;
  }

  .pending-title {
    font-size: 14px;
    color: #303133;
    margin-bottom: 5px;
  }

  .pending-time {
    font-size: 12px;
    color: #909399;
  }
}

.quick-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;

  .el-button {
    flex: 1;
    min-width: 150px;
  }
}

::v-deep .el-table {
  font-size: 14px;
}
</style>
