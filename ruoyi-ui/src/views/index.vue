<template>
  <div class="app-container dashboard">
    <!-- 机构管理员专属首页 -->
    <div v-if="isInstitutionManager" class="institution-dashboard">

      <!-- 数据统计卡片区域 -->
      <div class="info-cards">
        <!-- 订单统计 -->
        <div class="info-card">
          <div class="card-icon blue"><i class="el-icon-document"></i></div>
          <div class="card-body">
            <div class="card-title">今日订单</div>
            <div class="card-value">{{ institutionData.orderStats.orderCount || 0 }} 笔</div>
            <div class="card-sub">¥{{ formatNumber(institutionData.orderStats.orderAmount) }}</div>
          </div>
        </div>

        <!-- 服务费拨付 -->
        <div class="info-card">
          <div class="card-icon green"><i class="el-icon-bank-card"></i></div>
          <div class="card-body">
            <div class="card-title">本月拨付</div>
            <div class="card-value">¥{{ formatNumber(institutionData.transferStats.transferAmount) }}</div>
            <div class="card-sub">{{ institutionData.transferStats.transferCount || 0 }} 笔</div>
          </div>
        </div>

        <!-- 押��申请 -->
        <div class="info-card">
          <div class="card-icon orange"><i class="el-icon-edit-outline"></i></div>
          <div class="card-body">
            <div class="card-title">待审批</div>
            <div class="card-value">{{ institutionData.depositStats.pending.count || 0 }} 笔</div>
            <div class="card-sub">¥{{ formatNumber(institutionData.depositStats.pending.amount) }}</div>
          </div>
        </div>

        <!-- 已批准 -->
        <div class="info-card">
          <div class="card-icon cyan"><i class="el-icon-circle-check"></i></div>
          <div class="card-body">
            <div class="card-title">已批准</div>
            <div class="card-value">{{ institutionData.depositStats.approved.count || 0 }} 笔</div>
            <div class="card-sub">¥{{ formatNumber(institutionData.depositStats.approved.amount) }}</div>
          </div>
        </div>
      </div>

      <!-- 账户余额区域 -->
      <div class="balance-section">
        <div class="section-title">
          <i class="el-icon-wallet"></i> 账户余额
        </div>
        <div class="balance-grid">
          <div class="balance-item">
            <div class="balance-label">服务费余额</div>
            <div class="balance-amount primary">¥{{ formatNumber(institutionData.balanceStats.serviceBalance) }}</div>
          </div>
          <div class="balance-item">
            <div class="balance-label">押金余额</div>
            <div class="balance-amount warning">¥{{ formatNumber(institutionData.balanceStats.depositBalance) }}</div>
          </div>
          <div class="balance-item">
            <div class="balance-label">会员费余额</div>
            <div class="balance-amount info">¥{{ formatNumber(institutionData.balanceStats.memberBalance) }}</div>
          </div>
          <div class="balance-item">
            <div class="balance-label">基本户余额</div>
            <div class="balance-amount success">¥{{ formatNumber(institutionData.balanceStats.basicAccountBalance) }}</div>
          </div>
        </div>
      </div>

      <!-- 入驻人结构分析 -->
      <div class="structure-section">
        <div class="section-title">
          <i class="el-icon-user"></i> 入驻人结构分析
        </div>
        <div class="structure-grid">
          <!-- 性别分布 -->
          <div class="structure-box">
            <div class="structure-title">性别分布</div>
            <div class="structure-list">
              <div v-for="(item, index) in institutionData.residentStructure.gender" :key="'gender-'+index" class="progress-item">
                <span class="progress-label">{{ item.name }}</span>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: getPercentage(item.value, getTotalGender()) + '%', background: getGenderColor(index) }"></div>
                </div>
                <span class="progress-value">{{ item.value }}人</span>
              </div>
            </div>
          </div>

          <!-- 年龄分布 -->
          <div class="structure-box">
            <div class="structure-title">年龄分布</div>
            <div class="structure-list">
              <div v-for="(item, index) in institutionData.residentStructure.age" :key="'age-'+index" class="progress-item">
                <span class="progress-label">{{ item.ageGroup }}</span>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: getPercentage(item.count, getTotalAge()) + '%', background: getAgeColor(index) }"></div>
                </div>
                <span class="progress-value">{{ item.count }}人</span>
              </div>
            </div>
          </div>

          <!-- 护理等级分布 -->
          <div class="structure-box">
            <div class="structure-title">护理等级</div>
            <div class="structure-list">
              <div v-for="(item, index) in institutionData.residentStructure.careLevel" :key="'care-'+index" class="progress-item">
                <span class="progress-label">{{ item.name }}</span>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: getPercentage(item.value, getTotalCareLevel()) + '%', background: getCareColor(index) }"></div>
                </div>
                <span class="progress-value">{{ item.value }}人</span>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>

    <!-- 超管/监管部门简化首页 -->
    <div v-else class="supervision-dashboard">
      <!-- 数据统计卡片区域 -->
      <div class="info-cards">
        <!-- 入驻机构 -->
        <div class="info-card">
          <div class="card-icon blue"><i class="el-icon-office-building"></i></div>
          <div class="card-body">
            <div class="card-title">入驻机构</div>
            <div class="card-value">{{ supervisionData.overview.institutionCount || 0 }} 家</div>
          </div>
        </div>

        <!-- 入驻老人 -->
        <div class="info-card">
          <div class="card-icon green"><i class="el-icon-user-solid"></i></div>
          <div class="card-body">
            <div class="card-title">入驻老人</div>
            <div class="card-value">{{ supervisionData.overview.elderCount || 0 }} 人</div>
          </div>
        </div>

        <!-- 账户总额 -->
        <div class="info-card">
          <div class="card-icon orange"><i class="el-icon-wallet"></i></div>
          <div class="card-body">
            <div class="card-title">账户总额</div>
            <div class="card-value">¥{{ formatNumber(supervisionData.balance.totalBalance) }}</div>
          </div>
        </div>

        <!-- 预警总数 -->
        <div class="info-card">
          <div class="card-icon cyan"><i class="el-icon-warning-outline"></i></div>
          <div class="card-body">
            <div class="card-title">未处理预警</div>
            <div class="card-value">{{ supervisionData.overview.totalWarnings || 0 }} 条</div>
            <div class="card-sub">今日新增 {{ supervisionData.overview.todayWarnings || 0 }} 条</div>
          </div>
        </div>
      </div>

      <!-- 账户余额区域 -->
      <div class="balance-section">
        <div class="section-title">
          <i class="el-icon-wallet"></i> 账户余额汇总
        </div>
        <div class="balance-grid">
          <div class="balance-item">
            <div class="balance-label">服务费余额</div>
            <div class="balance-amount primary">¥{{ formatNumber(supervisionData.balance.serviceBalance) }}</div>
          </div>
          <div class="balance-item">
            <div class="balance-label">押金余额</div>
            <div class="balance-amount warning">¥{{ formatNumber(supervisionData.balance.depositBalance) }}</div>
          </div>
          <div class="balance-item">
            <div class="balance-label">会员费余额</div>
            <div class="balance-amount info">¥{{ formatNumber(supervisionData.balance.memberBalance) }}</div>
          </div>
          <div class="balance-item">
            <div class="balance-label">基本户余额</div>
            <div class="balance-amount success">¥{{ formatNumber(supervisionData.balance.basicAccountBalance) }}</div>
          </div>
        </div>
      </div>

      <!-- 入驻人结构分析 -->
      <div class="structure-section">
        <div class="section-title">
          <i class="el-icon-user"></i> 入驻人结构分析
        </div>
        <div class="structure-grid">
          <!-- 性别分布 -->
          <div class="structure-box">
            <div class="structure-title">性别分布</div>
            <div class="structure-list">
              <div v-for="(item, index) in supervisionData.residentStructure.gender" :key="'gender-'+index" class="progress-item">
                <span class="progress-label">{{ item.name }}</span>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: getPercentage(item.value, getTotalGender()) + '%', background: getGenderColor(index) }"></div>
                </div>
                <span class="progress-value">{{ item.value }}人</span>
              </div>
            </div>
          </div>

          <!-- 年龄分布 -->
          <div class="structure-box">
            <div class="structure-title">年龄分布</div>
            <div class="structure-list">
              <div v-for="(item, index) in supervisionData.residentStructure.age" :key="'age-'+index" class="progress-item">
                <span class="progress-label">{{ item.ageGroup }}</span>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: getPercentage(item.count, getTotalAge()) + '%', background: getAgeColor(index) }"></div>
                </div>
                <span class="progress-value">{{ item.count }}人</span>
              </div>
            </div>
          </div>

          <!-- 护理等级分布 -->
          <div class="structure-box">
            <div class="structure-title">护理等级</div>
            <div class="structure-list">
              <div v-for="(item, index) in supervisionData.residentStructure.careLevel" :key="'care-'+index" class="progress-item">
                <span class="progress-label">{{ item.name }}</span>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: getPercentage(item.value, getTotalCareLevel()) + '%', background: getCareColor(index) }"></div>
                </div>
                <span class="progress-value">{{ item.value }}人</span>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import {
  getInstitutionOverview,
  getInstitutionBalance,
  getInstitutionTransfer,
  getInstitutionDeposit,
  getResidentStructure,
  getSupervisionOverview,
  getSupervisionBalance,
  getSupervisionStructure
} from '@/api/pension/dashboard'

export default {
  name: "Dashboard",
  data() {
    return {
      // 是否为机构管理员
      isInstitutionManager: false,

      // 机构管理员首页数据
      institutionData: {
        orderStats: {
          orderCount: 0,
          orderAmount: 0
        },
        balanceStats: {
          accountCount: 0,
          serviceBalance: 0,
          depositBalance: 0,
          memberBalance: 0,
          totalBalance: 0,
          basicAccountBalance: 0
        },
        transferStats: {
          transferCount: 0,
          transferAmount: 0
        },
        depositStats: {
          pending: { count: 0, amount: 0 },
          approved: { count: 0, amount: 0 },
          rejected: { count: 0, amount: 0 }
        },
        residentStructure: {
          gender: [],
          age: [],
          careLevel: []
        }
      },

      // 超管/监管部门首页数据
      supervisionData: {
        overview: {
          institutionCount: 0,
          elderCount: 0,
          totalWarnings: 0,
          todayWarnings: 0
        },
        balance: {
          serviceBalance: 0,
          depositBalance: 0,
          memberBalance: 0,
          basicAccountBalance: 0,
          totalBalance: 0
        },
        residentStructure: {
          gender: [],
          age: [],
          careLevel: []
        }
      }
    }
  },
  mounted() {
    this.detectRoleAndLoad()
  },
  beforeDestroy() {
  },
  methods: {
    /**
     * 检测用户角色并加载对应首页数据
     */
    detectRoleAndLoad() {
      const roles = this.$store.getters.roles
      // 判断是否为机构管理员
      if (roles.includes('jigoumanage')) {
        this.isInstitutionManager = true
        this.loadInstitutionDashboard()
      } else {
        this.loadSupervisionDashboard()
      }
    },

    /**
     * 加载机构管理员首页数据
     */
    async loadInstitutionDashboard() {
      try {
        const [overview, balance, transfer, deposit, structure] = await Promise.all([
          getInstitutionOverview(),
          getInstitutionBalance(),
          getInstitutionTransfer(),
          getInstitutionDeposit(),
          getResidentStructure()
        ])

        // 订单统计
        if (overview.data && overview.data.orderStats) {
          this.institutionData.orderStats = overview.data.orderStats
        }

        // 账户余额
        if (balance.data) {
          this.institutionData.balanceStats = {
            ...balance.data.accountBalance,
            basicAccountBalance: balance.data.basicAccountBalance || 0
          }
        }

        // 服务费拨付
        if (transfer.data) {
          this.institutionData.transferStats = transfer.data
        }

        // 押金申请统计
        if (deposit.data) {
          this.institutionData.depositStats = deposit.data
        }

        // 入驻人结构分析
        if (structure.data) {
          this.institutionData.residentStructure = structure.data
        }

      } catch (error) {
        console.error('加载机构首页数据失败:', error)
      }
    },

    /**
     * 加载超管/监管部门首页数据
     */
    async loadSupervisionDashboard() {
      try {
        const [overview, balance, structure] = await Promise.all([
          getSupervisionOverview(),
          getSupervisionBalance(),
          getSupervisionStructure()
        ])

        // 概览数据
        if (overview.data) {
          this.supervisionData.overview = overview.data
        }

        // 账户余额
        if (balance.data) {
          this.supervisionData.balance = balance.data
        }

        // 入驻人结构分析
        if (structure.data) {
          this.supervisionData.residentStructure = structure.data
        }

      } catch (error) {
        console.error('加载超管首页数据失败:', error)
      }
    },

    // ==================== 机构首页辅助方法 ====================

    /**
     * 计算百分比
     */
    getPercentage(value, total) {
      if (!total || total === 0) return 0
      return Math.round((value / total) * 100)
    },

    /**
     * 获取性别总数
     */
    getTotalGender() {
      const source = this.isInstitutionManager ? this.institutionData.residentStructure.gender : this.supervisionData.residentStructure.gender
      return source.reduce((sum, item) => sum + (item.value || 0), 0)
    },

    /**
     * 获取年龄分布总数
     */
    getTotalAge() {
      const source = this.isInstitutionManager ? this.institutionData.residentStructure.age : this.supervisionData.residentStructure.age
      return source.reduce((sum, item) => sum + (item.count || 0), 0)
    },

    /**
     * 获取护理等级总数
     */
    getTotalCareLevel() {
      const source = this.isInstitutionManager ? this.institutionData.residentStructure.careLevel : this.supervisionData.residentStructure.careLevel
      return source.reduce((sum, item) => sum + (item.value || 0), 0)
    },

    /**
     * 获取性别颜色
     */
    getGenderColor(index) {
      const colors = ['#409EFF', '#67C23A', '#E6A23C']
      return colors[index] || '#909399'
    },

    /**
     * 获取年龄颜色
     */
    getAgeColor(index) {
      const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C']
      return colors[index] || '#909399'
    },

    /**
     * 获取护理等级颜色
     */
    getCareColor(index) {
      const colors = ['#67C23A', '#E6A23C', '#F56C6C']
      return colors[index] || '#909399'
    },

    formatNumber(num) {
      if (!num) return '0'
      return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    },

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

  // 机构首页新增卡片样式
  &.order {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  &.amount {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }

  &.transfer {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }

  &.transfer-count {
    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  }

  &.service {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  }

  &.deposit {
    background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  }

  &.member {
    background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
  }

  &.basic {
    background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  }

  &.pending-apply {
    background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  }

  &.approved-apply {
    background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%);
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

.stat-sub {
  font-size: 12px;
  opacity: 0.8;
  margin-top: 4px;
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

// 机构首页结构分析��域样式
.structure-section {
  margin-top: 20px;
}

.structure-row {
  margin-bottom: 0;
}

.structure-card {
  .el-card__header {
    background: #fafafa;
    border-bottom: 1px solid #ebeef5;
  }

  .chart-container {
    height: 250px;
  }
}

// 小尺寸结构分析卡片
.structure-card-small {
  height: 200px;

  .el-card__header {
    background: #fafafa;
    border-bottom: 1px solid #ebeef5;
    padding: 10px 15px;
  }

  .el-card__body {
    padding: 10px;
    height: calc(100% - 45px);
  }

  .chart-container-sm {
    height: 150px;
  }
}

.card-header-sm {
  font-size: 13px;
  font-weight: bold;
  color: #303133;

  i {
    margin-right: 4px;
    color: #409EFF;
  }
}

// ==================== 机构首页简化布局样式 ====================

// 信息卡片区域
.info-cards {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.info-card {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 18px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  }
}

.card-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  flex-shrink: 0;

  i {
    font-size: 22px;
    color: white;
  }

  &.blue {
    background: linear-gradient(135deg, #409EFF 0%, #5DADE2 100%);
  }

  &.green {
    background: linear-gradient(135deg, #67C23A 0%, #85E060 100%);
  }

  &.orange {
    background: linear-gradient(135deg, #E6A23C 0%, #F5B041 100%);
  }

  &.cyan {
    background: linear-gradient(135deg, #36D1DC 0%, #5B9BD5 100%);
  }
}

.card-body {
  flex: 1;
  min-width: 0;
}

.card-title {
  font-size: 13px;
  color: #909399;
  margin-bottom: 6px;
}

.card-value {
  font-size: 22px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.card-sub {
  font-size: 12px;
  color: #C0C4CC;
}

// 余额区域
.balance-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  .section-title {
    font-size: 15px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 15px;

    i {
      margin-right: 6px;
      color: #409EFF;
    }
  }
}

.balance-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.balance-item {
  .balance-label {
    font-size: 13px;
    color: #909399;
    margin-bottom: 8px;
  }

  .balance-amount {
    font-size: 20px;
    font-weight: bold;

    &.primary {
      color: #409EFF;
    }

    &.warning {
      color: #E6A23C;
    }

    &.success {
      color: #67C23A;
    }

    &.info {
      color: #909399;
    }
  }
}

// 结构分析区域
.structure-section {
  margin-top: 20px;

  .section-title {
    font-size: 15px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 15px;

    i {
      margin-right: 6px;
      color: #409EFF;
    }
  }
}

.structure-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.structure-box {
  background: white;
  border-radius: 8px;
  padding: 18px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  .structure-title {
    font-size: 14px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 15px;
  }
}

.structure-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.progress-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.progress-label {
  min-width: 60px;
  font-size: 13px;
  color: #606266;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #F2F6FC;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.5s ease;
}

.progress-value {
  min-width: 50px;
  text-align: right;
  font-size: 13px;
  font-weight: bold;
  color: #303133;
}
</style>