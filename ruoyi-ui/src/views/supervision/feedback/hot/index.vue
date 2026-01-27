<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="时间范围">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="反馈类型" prop="feedbackType">
        <el-select v-model="queryParams.feedbackType" placeholder="请选择反馈类型" clearable>
          <el-option
            v-for="dict in dict.type.feedback_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="20" class="mb8">
      <el-col :span="8">
        <el-card class="trend-card">
          <div slot="header">
            <span>热点反馈趋势</span>
          </div>
          <div class="chart-container">
            <div class="chart-placeholder">
              <i class="el-icon-s-data" style="font-size: 48px; color: #ccc;"></i>
              <p>热点反馈趋势图</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="trend-card">
          <div slot="header">
            <span>类型分布</span>
          </div>
          <div class="chart-container">
            <div class="chart-placeholder">
              <i class="el-icon-s-finance" style="font-size: 48px; color: #ccc;"></i>
              <p>热点反馈类型分布图</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="trend-card">
          <div slot="header">
            <span>满意度分析</span>
          </div>
          <div class="chart-container">
            <div class="chart-placeholder">
              <i class="el-icon-s-marketing" style="font-size: 48px; color: #ccc;"></i>
              <p>满意度分析图</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <div slot="header">
        <span>热点反馈列表</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleExport">
          <i class="el-icon-download"></i> 导出
        </el-button>
      </div>

      <el-table v-loading="loading" :data="hotFeedbackList">
        <el-table-column label="排名" type="index" width="60" align="center" />
        <el-table-column label="反馈类型" align="center" prop="feedbackType" width="120">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.feedback_type" :value="scope.row.feedbackType"/>
          </template>
        </el-table-column>
        <el-table-column label="标题" align="center" prop="title" :show-overflow-tooltip="true" />
        <el-table-column label="反馈次数" align="center" prop="count" width="100" sortable>
          <template slot-scope="scope">
            <el-tag :type="getCountType(scope.row.count)">{{ scope.row.count }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="涉及机构" align="center" prop="institutionCount" width="100" />
        <el-table-column label="平均满意度" align="center" prop="avgSatisfaction" width="120">
          <template slot-scope="scope">
            <el-rate
              v-model="scope.row.avgSatisfaction"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}">
            </el-rate>
          </template>
        </el-table-column>
        <el-table-column label="趋势" align="center" prop="trend" width="100">
          <template slot-scope="scope">
            <span :class="getTrendClass(scope.row.trend)">
              <i :class="getTrendIcon(scope.row.trend)"></i>
              {{ scope.row.trend }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="最近反馈" align="center" prop="lastFeedbackTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.lastFeedbackTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="150">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleDetail(scope.row)"
            >详情</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-s-tools"
              @click="handleAnalysis(scope.row)"
            >分析</el-button>
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
    </el-card>
  </div>
</template>

<script>
export default {
  name: "HotFeedback",
  dicts: ['feedback_type'],
  data() {
    return {
      loading: false,
      showSearch: true,
      dateRange: [],
      total: 0,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        feedbackType: null
      },
      // 热点反馈列表
      hotFeedbackList: []
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询热点反馈列表 */
    getList() {
      this.loading = true;
      // 模拟数据
      setTimeout(() => {
        this.hotFeedbackList = [
          {
            feedbackType: '1',
            title: '服务态度问题',
            count: 45,
            institutionCount: 12,
            avgSatisfaction: 3.2,
            trend: '上升',
            lastFeedbackTime: '2025-12-22 14:30'
          },
          {
            feedbackType: '2',
            title: '设施维护不及时',
            count: 38,
            institutionCount: 8,
            avgSatisfaction: 2.8,
            trend: '下降',
            lastFeedbackTime: '2025-12-22 12:15'
          },
          {
            feedbackType: '3',
            title: '餐饮质量改善建议',
            count: 32,
            institutionCount: 15,
            avgSatisfaction: 4.1,
            trend: '平稳',
            lastFeedbackTime: '2025-12-22 10:45'
          },
          {
            feedbackType: '4',
            title: '收费标准透明度',
            count: 28,
            institutionCount: 10,
            avgSatisfaction: 3.5,
            trend: '上升',
            lastFeedbackTime: '2025-12-22 09:20'
          },
          {
            feedbackType: '1',
            title: '医护人员专业性',
            count: 25,
            institutionCount: 6,
            avgSatisfaction: 4.3,
            trend: '下降',
            lastFeedbackTime: '2025-12-21 16:30'
          }
        ];
        this.total = this.hotFeedbackList.length;
        this.loading = false;
      }, 500);
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.$message.info("查看反馈详情功能待实现");
    },
    /** 分析按钮操作 */
    handleAnalysis(row) {
      this.$message.info("反馈分析功能待实现");
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$message.info("导出功能待实现");
    },
    /** 获取次数类型 */
    getCountType(count) {
      if (count >= 40) return 'danger';
      if (count >= 30) return 'warning';
      if (count >= 20) return '';
      return 'info';
    },
    /** 获取趋势样式 */
    getTrendClass(trend) {
      if (trend === '上升') return 'trend-up';
      if (trend === '下降') return 'trend-down';
      return 'trend-stable';
    },
    /** 获取趋势图标 */
    getTrendIcon(trend) {
      if (trend === '上升') return 'el-icon-top';
      if (trend === '下降') return 'el-icon-bottom';
      return 'el-icon-minus';
    }
  }
};
</script>

<style scoped>
.trend-card {
  margin-bottom: 20px;
}

.chart-container {
  height: 200px;
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

.trend-up {
  color: #f56c6c;
  font-weight: bold;
}

.trend-down {
  color: #67c23a;
  font-weight: bold;
}

.trend-stable {
  color: #909399;
}
</style>