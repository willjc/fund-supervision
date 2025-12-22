<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
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
      <el-form-item label="评分范围" prop="scoreRange">
        <el-select v-model="queryParams.scoreRange" placeholder="请选择评分范围" clearable>
          <el-option label="全部" value="" />
          <el-option label="5分" value="5" />
          <el-option label="4-5分" value="4" />
          <el-option label="3-4分" value="3" />
          <el-option label="3分以下" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 总体满意度统计 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="6">
        <el-card class="stat-card excellent">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.excellentCount || 0 }}</div>
            <div class="stat-label">非常满意 (5分)</div>
            <div class="stat-percent">{{ statistics.excellentPercent || 0 }}%</div>
          </div>
          <i class="el-icon-star-on stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card good">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.goodCount || 0 }}</div>
            <div class="stat-label">满意 (4分)</div>
            <div class="stat-percent">{{ statistics.goodPercent || 0 }}%</div>
          </div>
          <i class="el-icon-star-on stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card fair">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.fairCount || 0 }}</div>
            <div class="stat-label">一般 (3分)</div>
            <div class="stat-percent">{{ statistics.fairPercent || 0 }}%</div>
          </div>
          <i class="el-icon-star-on stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card poor">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.poorCount || 0 }}</div>
            <div class="stat-label">不满意 (1-2分)</div>
            <div class="stat-percent">{{ statistics.poorPercent || 0 }}%</div>
          </div>
          <i class="el-icon-star-off stat-icon"></i>
        </el-card>
      </el-col>
    </el-row>

    <!-- 平均评分 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="12">
        <el-card class="avg-score-card">
          <div class="avg-score-content">
            <div class="avg-score-title">总体平均评分</div>
            <div class="avg-score-number">{{ statistics.avgScore || 0 }}</div>
            <el-rate
              v-model="statistics.avgScore"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}">
            </el-rate>
            <div class="avg-score-desc">{{ getScoreDescription(statistics.avgScore) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="trend-card">
          <div slot="header">
            <span>满意度趋势</span>
          </div>
          <div class="chart-container">
            <div class="chart-placeholder">
              <i class="el-icon-data-line" style="font-size: 48px; color: #ccc;"></i>
              <p>满意度趋势图</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 满意度调查列表 -->
    <el-card>
      <div slot="header">
        <span>满意度调查记录</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="handleExport">
          <i class="el-icon-download"></i> 导出报表
        </el-button>
      </div>

      <el-table v-loading="loading" :data="satisfactionList">
        <el-table-column label="调查ID" align="center" prop="surveyId" width="100" />
        <el-table-column label="机构名称" align="center" prop="institutionName" :show-overflow-tooltip="true" />
        <el-table-column label="老人姓名" align="center" prop="elderName" width="100" />
        <el-table-column label="家属姓名" align="center" prop="familyName" width="100" />
        <el-table-column label="服务评分" align="center" prop="serviceScore" width="120">
          <template slot-scope="scope">
            <el-rate
              v-model="scope.row.serviceScore"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}">
            </el-rate>
          </template>
        </el-table-column>
        <el-table-column label="环境评分" align="center" prop="environmentScore" width="120">
          <template slot-scope="scope">
            <el-rate
              v-model="scope.row.environmentScore"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}">
            </el-rate>
          </template>
        </el-table-column>
        <el-table-column label="餐饮评分" align="center" prop="foodScore" width="120">
          <template slot-scope="scope">
            <el-rate
              v-model="scope.row.foodScore"
              disabled
              show-score
              text-color="#ff9900"
              score-template="{value}">
            </el-rate>
          </template>
        </el-table-column>
        <el-table-column label="综合评分" align="center" prop="totalScore" width="120">
          <template slot-scope="scope">
            <el-tag :type="getScoreTagType(scope.row.totalScore)">{{ scope.row.totalScore }}分</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="调查时间" align="center" prop="surveyTime" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.surveyTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
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
              icon="el-icon-edit"
              @click="handleFollowUp(scope.row)"
              v-if="scope.row.totalScore <= 3"
            >跟进</el-button>
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
  name: "SatisfactionSurvey",
  data() {
    return {
      loading: false,
      showSearch: true,
      dateRange: [],
      total: 0,
      // 统计数据
      statistics: {
        excellentCount: 0,
        goodCount: 0,
        fairCount: 0,
        poorCount: 0,
        excellentPercent: 0,
        goodPercent: 0,
        fairPercent: 0,
        poorPercent: 0,
        avgScore: 0
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        scoreRange: null
      },
      // 满意度列表
      satisfactionList: []
    };
  },
  created() {
    this.getList();
    this.loadStatistics();
  },
  methods: {
    /** 查询满意度列表 */
    getList() {
      this.loading = true;
      // 模拟数据
      setTimeout(() => {
        this.satisfactionList = [
          {
            surveyId: 1001,
            institutionName: '郑州市金水区花园口社区养老服务中心',
            elderName: '张大爷',
            familyName: '张三',
            serviceScore: 5,
            environmentScore: 4,
            foodScore: 5,
            totalScore: 4.7,
            surveyTime: '2025-12-22 14:30:00'
          },
          {
            surveyId: 1002,
            institutionName: '郑州市二七区阳光养老院',
            elderName: '李阿姨',
            familyName: '李四',
            serviceScore: 4,
            environmentScore: 3,
            foodScore: 4,
            totalScore: 3.7,
            surveyTime: '2025-12-22 10:15:00'
          },
          {
            surveyId: 1003,
            institutionName: '郑州市管城回族区温馨家园养老院',
            elderName: '王奶奶',
            familyName: '王五',
            serviceScore: 5,
            environmentScore: 5,
            foodScore: 4,
            totalScore: 4.7,
            surveyTime: '2025-12-21 16:45:00'
          },
          {
            surveyId: 1004,
            institutionName: '郑州市中原区康乐养老中心',
            elderName: '刘大爷',
            familyName: '刘六',
            serviceScore: 2,
            environmentScore: 3,
            foodScore: 2,
            totalScore: 2.3,
            surveyTime: '2025-12-21 09:20:00'
          }
        ];
        this.total = this.satisfactionList.length;
        this.loading = false;
      }, 500);
    },
    /** 加载统计数据 */
    loadStatistics() {
      // 模拟统计数据
      this.statistics = {
        excellentCount: 245,
        goodCount: 189,
        fairCount: 67,
        poorCount: 23,
        excellentPercent: 45.6,
        goodPercent: 35.2,
        fairPercent: 12.5,
        poorPercent: 4.3,
        avgScore: 4.2
      };
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
      this.$message.info("查看调查详情功能待实现");
    },
    /** 跟进按钮操作 */
    handleFollowUp(row) {
      this.$message.info("跟进处理功能待实现");
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$message.info("导出报表功能待实现");
    },
    /** 获取评分标签类型 */
    getScoreTagType(score) {
      if (score >= 4.5) return 'success';
      if (score >= 3.5) return '';
      if (score >= 2.5) return 'warning';
      return 'danger';
    },
    /** 获取评分描述 */
    getScoreDescription(score) {
      if (score >= 4.5) return '非常满意';
      if (score >= 3.5) return '满意';
      if (score >= 2.5) return '一般';
      return '不满意';
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

.stat-card.excellent {
  border-left: 4px solid #67c23a;
}

.stat-card.good {
  border-left: 4px solid #409eff;
}

.stat-card.fair {
  border-left: 4px solid #e6a23c;
}

.stat-card.poor {
  border-left: 4px solid #f56c6c;
}

.stat-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100px;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-top: 4px;
}

.stat-percent {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
  margin-top: 4px;
}

.stat-icon {
  font-size: 48px;
  color: #c0c4cc;
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.avg-score-card {
  text-align: center;
  padding: 20px;
}

.avg-score-title {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}

.avg-score-number {
  font-size: 48px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.avg-score-desc {
  font-size: 14px;
  color: #909399;
  margin-top: 10px;
}

.trend-card {
  margin-bottom: 20px;
}

.chart-container {
  height: 250px;
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