<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-warning">
              <i class="el-icon-warning"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">押金超额预警</div>
              <div class="stat-value">{{ statistics.warningCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-success">
              <i class="el-icon-circle-check"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">已处理</div>
              <div class="stat-value">{{ statistics.handledCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-info">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">待处理</div>
              <div class="stat-value">{{ statistics.pendingCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-primary">
              <i class="el-icon-coin"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">平均超额率</div>
              <div class="stat-value">{{ statistics.avgExcessRate }}%</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 查询表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input v-model="queryParams.institutionName" placeholder="请输入机构名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="预警等级" prop="warningLevel">
        <el-select v-model="queryParams.warningLevel" placeholder="请选择预警等级" clearable>
          <el-option label="一般" value="1" />
          <el-option label="重要" value="2" />
          <el-option label="紧急" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态" prop="handleStatus">
        <el-select v-model="queryParams.handleStatus" placeholder="请选择处理状态" clearable>
          <el-option label="待处理" value="1" />
          <el-option label="处理中" value="2" />
          <el-option label="已处理" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleHandle" :disabled="single">批量处理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-download" size="mini" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="warningList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="预警ID" align="center" prop="warningId" width="100" />
      <el-table-column label="机构名称" align="center" prop="institutionName" width="150" />
      <el-table-column label="当前押金" align="center" prop="currentDeposit" width="120">
        <template slot-scope="scope">
          <span class="money-text">¥{{ scope.row.currentDeposit.toLocaleString() }}</span>
        </template>
      </el-table-column>
      <el-table-column label="标准押金" align="center" prop="standardDeposit" width="120">
        <template slot-scope="scope">
          <span class="money-text">¥{{ scope.row.standardDeposit.toLocaleString() }}</span>
        </template>
      </el-table-column>
      <el-table-column label="超额金额" align="center" prop="excessAmount" width="120">
        <template slot-scope="scope">
          <span class="money-text text-danger">¥{{ scope.row.excessAmount.toLocaleString() }}</span>
        </template>
      </el-table-column>
      <el-table-column label="超额率" align="center" prop="excessRate" width="100">
        <template slot-scope="scope">
          <el-tag :type="getExcessRateType(scope.row.excessRate)" size="small">{{ scope.row.excessRate }}%</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="预警等级" align="center" prop="warningLevel" width="100">
        <template slot-scope="scope">
          <el-tag :type="getWarningLevelType(scope.row.warningLevel)" size="small">{{ getWarningLevelText(scope.row.warningLevel) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="预警时间" align="center" prop="warningTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.warningTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="handleStatus" width="100">
        <template slot-scope="scope">
          <el-tag :type="getHandleStatusType(scope.row.handleStatus)" size="small">{{ getHandleStatusText(scope.row.handleStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.handleStatus === '1'" size="mini" type="text" icon="el-icon-edit" @click="handleHandle(scope.row)">处理</el-button>
          <el-button size="mini" type="text" icon="el-icon-message" @click="handleNotify(scope.row)">通知</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 预警处理对话框 -->
    <el-dialog :title="handleTitle" :visible.sync="handleOpen" width="600px" append-to-body>
      <el-form ref="handleForm" :model="handleForm" :rules="handleRules" label-width="100px">
        <el-form-item label="预警ID" prop="warningId">
          <el-input v-model="handleForm.warningId" disabled />
        </el-form-item>
        <el-form-item label="机构名称" prop="institutionName">
          <el-input v-model="handleForm.institutionName" disabled />
        </el-form-item>
        <el-form-item label="处理方式" prop="handleType">
          <el-select v-model="handleForm.handleType" placeholder="请选择处理方式">
            <el-option label="电话沟通" value="电话沟通" />
            <el-option label="现场检查" value="现场检查" />
            <el-option label="书面通知" value="书面通知" />
            <el-option label="约谈负责人" value="约谈负责人" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
          <el-select v-model="handleForm.handleResult" placeholder="请选择处理结果">
            <el-option label="已退还超额押金" value="已退还超额押金" />
            <el-option label="已调整收费标准" value="已调整收费标准" />
            <el-option label="已提供合理解释" value="已提供合理解释" />
            <el-option label="进一步调查" value="进一步调查" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理意见" prop="handleOpinion">
          <el-input v-model="handleForm.handleOpinion" type="textarea" placeholder="请输入处理意见" :rows="4" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitHandleForm">确 定</el-button>
        <el-button @click="handleOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="押金超额预警详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预警ID">{{ detailForm.warningId }}</el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ detailForm.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="当前押金">¥{{ detailForm.currentDeposit ? detailForm.currentDeposit.toLocaleString() : 0 }}</el-descriptions-item>
        <el-descriptions-item label="标准押金">¥{{ detailForm.standardDeposit ? detailForm.standardDeposit.toLocaleString() : 0 }}</el-descriptions-item>
        <el-descriptions-item label="超额金额">
          <span class="text-danger">¥{{ detailForm.excessAmount ? detailForm.excessAmount.toLocaleString() : 0 }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="超额率">
          <el-tag :type="getExcessRateType(detailForm.excessRate)">{{ detailForm.excessRate }}%</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预警等级">
          <el-tag :type="getWarningLevelType(detailForm.warningLevel)">{{ getWarningLevelText(detailForm.warningLevel) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预警时间">{{ detailForm.warningTime }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag :type="getHandleStatusType(detailForm.handleStatus)">{{ getHandleStatusText(detailForm.handleStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ detailForm.handleTime || '未处理' }}</el-descriptions-item>
        <el-descriptions-item label="处理方式" :span="2">{{ detailForm.handleType || '未处理' }}</el-descriptions-item>
        <el-descriptions-item label="处理意见" :span="2">{{ detailForm.handleOpinion || '暂无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'DepositExcess',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      warningList: [],
      statistics: {
        warningCount: 0,
        handledCount: 0,
        pendingCount: 0,
        avgExcessRate: 0
      },
      handleTitle: '',
      handleOpen: false,
      detailOpen: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        warningLevel: null,
        handleStatus: null
      },
      handleForm: {},
      detailForm: {},
      handleRules: {
        handleType: [
          { required: true, message: '处理方式不能为空', trigger: 'change' }
        ],
        handleResult: [
          { required: true, message: '处理结果不能为空', trigger: 'change' }
        ],
        handleOpinion: [
          { required: true, message: '处理意见不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const mockData = {
        total: 48,
        rows: Array.from({ length: 10 }, (_, i) => ({
          warningId: 1000 + i,
          institutionName: ['阳光养老院', '康复中心', '老年之家', '福星养老院', '康乐养老中心', '金色年华', '乐享养老', '温馨家园'][i % 8],
          currentDeposit: Math.floor(Math.random() * 50000) + 10000,
          standardDeposit: Math.floor(Math.random() * 20000) + 8000,
          warningLevel: ['1', '2', '3'][Math.floor(Math.random() * 3)],
          warningTime: this.parseTime(new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000), '{y}-{m}-{d} {h}:{i}:{s}'),
          handleStatus: ['1', '2', '3'][Math.floor(Math.random() * 3)],
          handleTime: Math.random() > 0.5 ? this.parseTime(new Date(Date.now() - Math.random() * 3 * 24 * 60 * 60 * 1000), '{y}-{m}-{d} {h}:{i}:{s}') : null,
          handleType: Math.random() > 0.5 ? ['电话沟通', '现场检查', '书面通知', '约谈负责人'][Math.floor(Math.random() * 4)] : null,
          handleResult: Math.random() > 0.5 ? ['已退还超额押金', '已调整收费标准', '已提供合理解释', '进一步调查'][Math.floor(Math.random() * 4)] : null,
          handleOpinion: Math.random() > 0.5 ? '已与机构负责人沟通，机构承诺在3个工作日内退还超额收取的押金。' : null
        }))
      }
      mockData.rows.forEach(item => {
        item.excessAmount = item.currentDeposit - item.standardDeposit
        item.excessRate = Math.round((item.excessAmount / item.standardDeposit) * 100)
      })
      this.warningList = mockData.rows
      this.total = mockData.total
      this.statistics = {
        warningCount: mockData.total,
        handledCount: Math.floor(mockData.total * 0.6),
        pendingCount: Math.floor(mockData.total * 0.4),
        avgExcessRate: Math.round(mockData.rows.reduce((sum, item) => sum + item.excessRate, 0) / mockData.rows.length)
      }
      this.loading = false
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
      this.ids = selection.map(item => item.warningId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleHandle(row) {
      const warningData = row.id ? [row] : this.warningList.filter(item => this.ids.includes(item.warningId))
      if (warningData.length === 1) {
        this.handleForm = { ...warningData[0] }
        this.handleTitle = '处理押金超额预警'
      } else {
        this.handleForm = { warningId: this.ids.join(', '), institutionName: '批量处理' }
        this.handleTitle = '批量处理押金超额预警'
      }
      this.handleOpen = true
    },
    submitHandleForm() {
      this.$refs['handleForm'].validate(valid => {
        if (valid) {
          this.$modal.msgSuccess('处理成功')
          this.handleOpen = false
          this.getList()
        }
      })
    },
    handleDetail(row) {
      this.detailForm = { ...row }
      this.detailOpen = true
    },
    handleNotify(row) {
      this.$modal.msgSuccess('通知发送成功')
    },
    handleExport() {
      this.$modal.msgSuccess('导出成功')
    },
    getExcessRateType(rate) {
      if (rate >= 50) return 'danger'
      if (rate >= 20) return 'warning'
      return 'info'
    },
    getWarningLevelType(level) {
      const typeMap = { '1': 'info', '2': 'warning', '3': 'danger' }
      return typeMap[level] || 'info'
    },
    getWarningLevelText(level) {
      const textMap = { '1': '一般', '2': '重要', '3': '紧急' }
      return textMap[level] || '未知'
    },
    getHandleStatusType(status) {
      const typeMap = { '1': 'danger', '2': 'warning', '3': 'success' }
      return typeMap[status] || 'info'
    },
    getHandleStatusText(status) {
      const textMap = { '1': '待处理', '2': '处理中', '3': '已处理' }
      return textMap[status] || '未知'
    }
  }
}
</script>

<style scoped>
.money-text {
  font-family: 'Courier New', monospace;
  font-weight: 600;
}

.text-danger {
  color: #f56c6c;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  margin-right: 15px;
}

.bg-warning { background: linear-gradient(135deg, #ff9800, #f57c00); }
.bg-success { background: linear-gradient(135deg, #4caf50, #388e3c); }
.bg-info { background: linear-gradient(135deg, #2196f3, #1976d2); }
.bg-primary { background: linear-gradient(135deg, #3f51b5, #303f9f); }

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}
</style>