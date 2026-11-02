<template>
  <div class="app-container dashboard">
    <header class="workspace-head">
      <div class="workspace-title-group">
        <p class="workspace-eyebrow">监管工作台</p>
        <h1 class="workspace-title">{{ isInstitutionManager ? '机构运营概览' : '全市资金监管概览' }}</h1>
      </div>
      <p class="workspace-meta">
        <span class="meta-dot"></span>{{ todayLabel }} · 数据实时汇总
      </p>
    </header>

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
        <div class="section-head">
          <div>
            <p class="section-eyebrow">资金看板</p>
            <h2 class="section-title">账户余额</h2>
          </div>
          <span class="section-note">监管专户与平台账户实时汇总</span>
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

      <!-- 入住人结构分析 -->
      <div class="structure-section">
        <div class="section-head">
          <div>
            <p class="section-eyebrow">入住画像</p>
            <h2 class="section-title">入住人结构分析</h2>
          </div>
          <span class="section-note">按在住老人统计</span>
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
        <!-- 入住机构 -->
        <div class="info-card">
          <div class="card-icon blue"><i class="el-icon-office-building"></i></div>
          <div class="card-body">
            <div class="card-title">入住机构</div>
            <div class="card-value">{{ supervisionData.overview.institutionCount || 0 }} 家</div>
          </div>
        </div>

        <!-- 入住老人 -->
        <div class="info-card">
          <div class="card-icon green"><i class="el-icon-user-solid"></i></div>
          <div class="card-body">
            <div class="card-title">入住老人</div>
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
        <div class="section-head">
          <div>
            <p class="section-eyebrow">资金看板</p>
            <h2 class="section-title">账户余额汇总</h2>
          </div>
          <span class="section-note">全市机构账户合计</span>
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

      <!-- 入住人结构分析 -->
      <div class="structure-section">
        <div class="section-head">
          <div>
            <p class="section-eyebrow">入住画像</p>
            <h2 class="section-title">入住人结构分析</h2>
          </div>
          <span class="section-note">按在住老人统计</span>
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
      todayLabel: '',

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
  created() {
    this.todayLabel = new Intl.DateTimeFormat('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      weekday: 'long'
    }).format(new Date())
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

        // 入住人结构分析
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

        // 入住人结构分析
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
      const colors = ['#1f65bd', '#18815d', '#b86c08']
      return colors[index] || '#8b98a8'
    },

    /**
     * 获取年龄颜色
     */
    getAgeColor(index) {
      const colors = ['#1f65bd', '#18815d', '#b86c08', '#a93b2f']
      return colors[index] || '#8b98a8'
    },

    /**
     * 获取护理等级颜色
     */
    getCareColor(index) {
      const colors = ['#18815d', '#b86c08', '#a93b2f']
      return colors[index] || '#8b98a8'
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
  background: transparent;
}

/* 工作台页头 */
.workspace-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #e4eaf1;
}

.workspace-eyebrow {
  margin: 0 0 8px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.24em;
  color: #1f65bd;
}

.workspace-title {
  margin: 0;
  font-family: "Songti SC", "Noto Serif SC", STSong, SimSun, serif;
  font-size: 26px;
  font-weight: 700;
  line-height: 1.3;
  letter-spacing: 0.02em;
  color: #14273f;
}

.workspace-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 2px;
  font-size: 12px;
  color: #667085;
  font-variant-numeric: tabular-nums;

  .meta-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #18815d;
    box-shadow: 0 0 0 3px rgba(24, 129, 93, 0.14);
  }
}

/* 指标卡 */
.info-cards {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.info-card {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 18px 20px;
  overflow: hidden;
  background: #ffffff;
  border: 1px solid #e4eaf1;
  border-radius: 12px;
  box-shadow: 0 3px 12px rgba(31, 55, 82, 0.05);
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;

  &::before {
    content: "";
    position: absolute;
    top: 20px;
    bottom: 20px;
    left: 0;
    width: 3px;
    border-radius: 0 3px 3px 0;
    background: #1f65bd;
  }

  &:nth-child(2)::before {
    background: #18815d;
  }

  &:nth-child(3)::before {
    background: #b86c08;
  }

  &:nth-child(4)::before {
    background: #a93b2f;
  }

  &:hover {
    border-color: #d7e0ea;
    box-shadow: 0 8px 24px rgba(31, 55, 82, 0.1);
    transform: translateY(-2px);
  }
}

.card-icon {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 10px;

  i {
    font-size: 20px;
  }

  &.blue {
    background: #eef6ff;

    i {
      color: #1f65bd;
    }
  }

  &.green {
    background: #eaf7f1;

    i {
      color: #18815d;
    }
  }

  &.orange {
    background: #fff6e6;

    i {
      color: #b86c08;
    }
  }

  &.cyan {
    background: #eef1f5;

    i {
      color: #3f5a78;
    }
  }
}

.card-body {
  flex: 1;
  min-width: 0;
}

.card-title {
  margin-bottom: 6px;
  font-size: 12px;
  letter-spacing: 0.04em;
  color: #667085;
}

.card-value {
  margin-bottom: 4px;
  font-family: var(--app-font-data, "SF Mono", "Roboto Mono", Menlo, Consolas, monospace);
  font-size: 24px;
  font-weight: 600;
  line-height: 1.3;
  color: #14273f;
  font-variant-numeric: tabular-nums;
}

.card-sub {
  font-size: 12px;
  color: #8b98a8;
  font-variant-numeric: tabular-nums;
}

/* 余额看板：账册封面 */
.balance-section {
  position: relative;
  padding: 24px 26px;
  margin-bottom: 20px;
  overflow: hidden;
  color: #eef3f8;
  background:
    radial-gradient(520px 320px at 82% 0%, rgba(48, 82, 118, 0.5) 0%, rgba(48, 82, 118, 0) 65%),
    linear-gradient(140deg, #16293f 0%, #0e1d2f 70%, #0b1725 100%);
  border-radius: 14px;

  &::after {
    content: "";
    position: absolute;
    top: -140px;
    right: -60px;
    width: 320px;
    height: 320px;
    border: 1px solid rgba(255, 255, 255, 0.07);
    border-radius: 50%;
    box-shadow: 0 0 0 42px rgba(255, 255, 255, 0.02);
    pointer-events: none;
  }
}

.section-head {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.section-eyebrow {
  margin: 0 0 6px;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.28em;
  color: rgba(224, 232, 240, 0.5);
}

.section-title {
  margin: 0;
  font-family: "Songti SC", "Noto Serif SC", STSong, SimSun, serif;
  font-size: 18px;
  font-weight: 700;
  line-height: 1.3;
  letter-spacing: 0.04em;
  color: #f2f6fa;
}

.section-note {
  font-size: 11px;
  color: rgba(224, 232, 240, 0.45);
}

.balance-grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
}

.balance-item {
  padding: 4px 24px;
  border-left: 1px solid rgba(255, 255, 255, 0.12);

  &:first-child {
    padding-left: 0;
    border-left: none;
  }

  &:nth-child(2) .balance-label::before {
    background: #e8b46a;
  }

  &:nth-child(3) .balance-label::before {
    background: #cbd6e2;
  }

  &:nth-child(4) .balance-label::before {
    background: #7fd6b6;
  }
}

.balance-label {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-bottom: 8px;
  font-size: 12px;
  color: rgba(224, 232, 240, 0.55);

  &::before {
    content: "";
    width: 5px;
    height: 5px;
    border-radius: 50%;
    background: #9ec8f6;
  }
}

.balance-amount {
  font-family: var(--app-font-data, "SF Mono", "Roboto Mono", Menlo, Consolas, monospace);
  font-size: 26px;
  font-weight: 600;
  line-height: 1.3;
  letter-spacing: 0.01em;
  color: #f2f6fa;
  font-variant-numeric: tabular-nums;

  &.primary {
    color: #9ec8f6;
  }

  &.warning {
    color: #e8b46a;
  }

  &.info {
    color: #cbd6e2;
  }

  &.success {
    color: #7fd6b6;
  }
}

/* 结构分析 */
.structure-section {
  margin-top: 4px;

  .section-head {
    margin-bottom: 14px;
  }

  .section-eyebrow {
    color: #1f65bd;
  }

  .section-title {
    color: #14273f;
  }

  .section-note {
    color: #8b98a8;
  }
}

.structure-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.structure-box {
  padding: 20px;
  background: #ffffff;
  border: 1px solid #e4eaf1;
  border-radius: 12px;
  box-shadow: 0 3px 12px rgba(31, 55, 82, 0.05);

  .structure-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 18px;
    padding-bottom: 12px;
    font-size: 14px;
    font-weight: 600;
    color: #1b2735;
    border-bottom: 1px solid #edf1f5;

    &::after {
      content: "占比";
      font-size: 11px;
      font-weight: 400;
      letter-spacing: 0.08em;
      color: #8b98a8;
    }
  }
}

.structure-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.progress-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.progress-label {
  min-width: 64px;
  font-size: 13px;
  color: #536273;
}

.progress-bar {
  flex: 1;
  height: 6px;
  overflow: hidden;
  background: #eef2f7;
  border-radius: 999px;
}

.progress-fill {
  height: 100%;
  border-radius: 999px;
  transition: width 0.6s cubic-bezier(0.2, 0.8, 0.2, 1);
}

.progress-value {
  min-width: 44px;
  font-family: var(--app-font-data, "SF Mono", "Roboto Mono", Menlo, Consolas, monospace);
  font-size: 12px;
  font-weight: 600;
  text-align: right;
  color: #1b2735;
  font-variant-numeric: tabular-nums;
}

@media (max-width: 1100px) {
  .info-cards {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .balance-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    row-gap: 20px;
  }

  .balance-item {
    &:nth-child(odd) {
      padding-left: 0;
      border-left: none;
    }
  }

  .structure-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .workspace-head {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .info-cards {
    grid-template-columns: 1fr;
  }

  .balance-grid {
    grid-template-columns: 1fr;
    row-gap: 0;
  }

  .balance-item {
    padding: 14px 0;
    border-left: none;
    border-top: 1px solid rgba(255, 255, 255, 0.12);

    &:first-child {
      border-top: none;
    }
  }

  .section-head {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .info-card,
  .progress-fill {
    transition: none;
  }

  .info-card:hover {
    transform: none;
  }
}
</style>
