<template>
  <div class="app-container">
    <!-- 警告提示区域 -->
    <el-alert
      title="危险操作警告"
      type="error"
      description="此操作将清除所有测试数据，包括订单、支付记录、入住记录、家属关联、系统日志等，但会保留admin用户、机构管理员用户、老人基本信息和床位信息。清除后无法恢复，请谨慎操作！"
      show-icon
      :closable="false"
      style="margin-bottom: 20px"
    >
    </el-alert>

    <!-- 当前数据统计 -->
    <el-card class="box-card" style="margin-bottom: 20px">
      <div slot="header" class="clearfix">
        <span style="font-size: 16px; font-weight: bold">当前数据统计</span>
        <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-refresh" @click="loadStatus">刷新</el-button>
      </div>
      <el-row :gutter="20" v-loading="statusLoading">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">用户数量</div>
            <div class="stat-value">{{ stats.userCount || 0 }}</div>
            <div class="stat-desc">测试用户: {{ stats.testUserCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">老人数量</div>
            <div class="stat-value">{{ stats.elderCount || 0 }}</div>
            <div class="stat-desc">已出院: {{ stats.elderDischargedCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">床位数量</div>
            <div class="stat-value">{{ stats.bedTotalCount || 0 }}</div>
            <div class="stat-desc">已占用: {{ stats.bedOccupiedCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">订单数量</div>
            <div class="stat-value">{{ stats.orderCount || 0 }}</div>
            <div class="stat-desc">押金: {{ stats.depositCount || 0 }}</div>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 15px" v-loading="statusLoading">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">账户数量</div>
            <div class="stat-value">{{ stats.accountCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">投诉数量</div>
            <div class="stat-value">{{ stats.complaintCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">预约数量</div>
            <div class="stat-value">{{ stats.reservationCount || 0 }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">保留数据</div>
            <div class="stat-value" style="color: #67C23A">保留</div>
            <div class="stat-desc">admin + 机构管理员</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 操作区域 -->
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span style="font-size: 16px; font-weight: bold">清除操作</span>
      </div>
      <div style="text-align: center; padding: 30px 0">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="medium"
          @click="handleClean"
          v-hasPermi="['system:clean:execute']"
          :loading="cleaning"
        >
          清除所有测试数据
        </el-button>
        <p style="margin-top: 15px; color: #909399; font-size: 13px">
          点击后将清除所有测试数据，保留系统核心数据
        </p>
      </div>
    </el-card>

    <!-- 清除结果 -->
    <el-card class="box-card" v-if="cleanResults.length > 0" style="margin-top: 20px">
      <div slot="header" class="clearfix">
        <span style="font-size: 16px; font-weight: bold">清除结果</span>
        <el-button style="float: right" type="text" icon="el-icon-close" @click="cleanResults = []">关闭</el-button>
      </div>
      <div class="clean-results">
        <div v-for="(result, index) in cleanResults" :key="index" :class="['result-item', result.startsWith('✓') ? 'success' : 'error']">
          {{ result }}
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getTestDataStatus, executeClean } from "@/api/system/cleanTestData";

export default {
  name: "CleanTestData",
  data() {
    return {
      statusLoading: false,
      cleaning: false,
      stats: {},
      cleanResults: []
    };
  },
  created() {
    this.loadStatus();
  },
  methods: {
    /** 加载数据统计 */
    loadStatus() {
      this.statusLoading = true;
      getTestDataStatus().then(response => {
        this.stats = response.data;
      }).finally(() => {
        this.statusLoading = false;
      });
    },

    /** 清除测试数据 */
    handleClean() {
      this.$modal.confirm('确认要清除所有测试数据吗？此操作不可恢复！').then(() => {
        this.cleaning = true;
        this.cleanResults = [];
        return executeClean();
      }).then(response => {
        this.cleanResults = response.data || [];
        this.$modal.msgSuccess("测试数据清除完成");
        this.loadStatus();
      }).catch(() => {
      }).finally(() => {
        this.cleaning = false;
      });
    }
  }
};
</script>

<style scoped>
.stat-item {
  text-align: center;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-desc {
  font-size: 12px;
  color: #c0c4cc;
}

.clean-results {
  max-height: 300px;
  overflow-y: auto;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.result-item {
  padding: 5px 0;
  font-size: 14px;
  line-height: 1.6;
}

.result-item.success {
  color: #67c23a;
}

.result-item.error {
  color: #f56c6c;
}
</style>
