<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.totalCount || 0 }}</div>
            <div class="stat-label">总反馈数</div>
          </div>
          <i class="el-icon-document stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card pending">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.pendingCount || 0 }}</div>
            <div class="stat-label">待处理</div>
          </div>
          <i class="el-icon-time stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card processed">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.processedCount || 0 }}</div>
            <div class="stat-label">已处理</div>
          </div>
          <i class="el-icon-circle-check stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card resolved">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.resolvedCount || 0 }}</div>
            <div class="stat-label">已解决</div>
          </div>
          <i class="el-icon-success stat-icon"></i>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>反馈类型分布</span>
          </div>
          <div class="chart-container">
            <div class="chart-placeholder">
              <i class="el-icon-pie-chart" style="font-size: 48px; color: #ccc;"></i>
              <p>反馈类型分布图表</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header">
            <span>月度反馈趋势</span>
          </div>
          <div class="chart-container">
            <div class="chart-placeholder">
              <i class="el-icon-line-chart" style="font-size: 48px; color: #ccc;"></i>
              <p>月度反馈趋势图表</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细统计表格 -->
    <el-card>
      <div slot="header">
        <span>详细统计</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleRefresh">
          <i class="el-icon-refresh"></i> 刷新
        </el-button>
      </div>

      <el-table :data="detailStats" style="width: 100%">
        <el-table-column prop="type" label="反馈类型" width="150">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.feedback_type" :value="scope.row.type"/>
          </template>
        </el-table-column>
        <el-table-column prop="count" label="数量" width="100" />
        <el-table-column prop="percentage" label="占比" width="120">
          <template slot-scope="scope">
            {{ scope.row.percentage }}%
          </template>
        </el-table-column>
        <el-table-column prop="avgProcessTime" label="平均处理时间" width="150">
          <template slot-scope="scope">
            {{ scope.row.avgProcessTime }}小时
          </template>
        </el-table-column>
        <el-table-column prop="satisfaction" label="满意度" width="120">
          <template slot-scope="scope">
            <el-rate
              v-model="scope.row.satisfaction"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}">
            </el-rate>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "FeedbackStatistics",
  dicts: ['feedback_type'],
  data() {
    return {
      loading: false,
      // 统计数据
      statistics: {
        totalCount: 0,
        pendingCount: 0,
        processedCount: 0,
        resolvedCount: 0
      },
      // 详细统计数据
      detailStats: []
    };
  },
  created() {
    this.loadStatistics();
  },
  methods: {
    /** 加载统计数据 */
    loadStatistics() {
      this.loading = true;
      // 模拟数据
      setTimeout(() => {
        this.statistics = {
          totalCount: 156,
          pendingCount: 23,
          processedCount: 133,
          resolvedCount: 128
        };

        this.detailStats = [
          {
            type: '1',
            count: 68,
            percentage: 43.6,
            avgProcessTime: 2.5,
            satisfaction: 4.2
          },
          {
            type: '2',
            count: 45,
            percentage: 28.8,
            avgProcessTime: 3.2,
            satisfaction: 4.5
          },
          {
            type: '3',
            count: 28,
            percentage: 17.9,
            avgProcessTime: 1.8,
            satisfaction: 3.8
          },
          {
            type: '4',
            count: 15,
            percentage: 9.7,
            avgProcessTime: 4.1,
            satisfaction: 4.0
          }
        ];

        this.loading = false;
      }, 500);
    },
    /** 刷新数据 */
    handleRefresh() {
      this.loadStatistics();
      this.$message.success("数据已刷新");
    }
  }
};
</script>

<style scoped>
.stat-card {
  border: none;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-card.pending {
  border-left: 4px solid #e6a23c;
}

.stat-card.processed {
  border-left: 4px solid #409eff;
}

.stat-card.resolved {
  border-left: 4px solid #67c23a;
}

.stat-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 80px;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.stat-icon {
  font-size: 48px;
  color: #c0c4cc;
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.chart-card {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chart-placeholder {
  text-align: center;
  color: #909399;
}

.chart-placeholder p {
  margin-top: 10px;
  font-size: 14px;
}
</style>