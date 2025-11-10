<template>
  <div class="app-container">
    <!-- 预警统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-danger">
              <i class="el-icon-warning-outline"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">待处理预警</div>
              <div class="stat-value">{{ statistics.pendingCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-warning">
              <i class="el-icon-coin"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">平均超额金额</div>
              <div class="stat-value">¥{{ statistics.avgExcessAmount | formatAmount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-primary">
              <i class="el-icon-house"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">涉及机构数</div>
              <div class="stat-value">{{ statistics.institutionCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-success">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">涉及老人数</div>
              <div class="stat-value">{{ statistics.elderCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="老人姓名" prop="elderName">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入老人姓名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预警等级" prop="warningLevel">
        <el-select v-model="queryParams.warningLevel" placeholder="请选择预警等级" clearable size="small">
          <el-option label="高" value="高" />
          <el-option label="中" value="中" />
          <el-option label="低" value="低" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择处理状态" clearable size="small">
          <el-option label="待处理" value="待处理" />
          <el-option label="已处理" value="已处理" />
        </el-select>
      </el-form-item>
      <el-form-item label="预警时间" prop="dateRange">
        <el-date-picker
          v-model="queryParams.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          size="small">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="multiple"
          @click="handleBatchProcess"
        >批量处理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="warningList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="预警编号" align="center" prop="warningNo" width="160" />
      <el-table-column label="机构名称" align="center" prop="institutionName" :show-overflow-tooltip="true" />
      <el-table-column label="老人姓名" align="center" prop="elderName" width="100" />
      <el-table-column label="月费用标准" align="center" prop="monthlyFee" width="120">
        <template slot-scope="scope">
          <span>¥{{ scope.row.monthlyFee | formatAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预收金额" align="center" prop="prepaidFee" width="120">
        <template slot-scope="scope">
          <span>¥{{ scope.row.prepaidFee | formatAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="超额金额" align="center" prop="excessAmount" width="120">
        <template slot-scope="scope">
          <span class="text-danger font-bold">¥{{ scope.row.excessAmount | formatAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="超额倍数" align="center" prop="excessMultiple" width="100">
        <template slot-scope="scope">
          <el-tag :type="getExcessTagType(scope.row.excessMultiple)" size="mini">
            {{ scope.row.excessMultiple }}倍
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="预警等级" align="center" prop="warningLevel" width="100">
        <template slot-scope="scope">
          <el-tag :type="getWarningTagType(scope.row.warningLevel)">
            {{ scope.row.warningLevel }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="预警时间" align="center" prop="warningTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.warningTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '待处理'" type="warning">待处理</el-tag>
          <el-tag v-else-if="scope.row.status === '已处理'" type="success">已处理</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleProcess(scope.row)"
            v-if="scope.row.status === '待处理'"
          >处理</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-message"
            @click="handleNotify(scope.row)"
          >通知</el-button>
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

    <!-- 处理预警对话框 -->
    <el-dialog title="处理费用超额预警" :visible.sync="processOpen" width="600px" append-to-body>
      <el-form ref="processForm" :model="processForm" :rules="processRules" label-width="120px">
        <el-form-item label="预警编号">
          <el-input v-model="processForm.warningNo" :disabled="true" />
        </el-form-item>
        <el-form-item label="机构名称">
          <el-input v-model="processForm.institutionName" :disabled="true" />
        </el-form-item>
        <el-form-item label="老人姓名">
          <el-input v-model="processForm.elderName" :disabled="true" />
        </el-form-item>
        <el-form-item label="月费用标准">
          <el-input v-model="processForm.monthlyFee" :disabled="true">
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="预收金额">
          <el-input v-model="processForm.prepaidFee" :disabled="true">
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="超额金额">
          <el-input v-model="processForm.excessAmount" :disabled="true">
            <template slot="prepend">¥</template>
          </el-input>
        </el-form-item>
        <el-form-item label="处理方式" prop="handleType">
          <el-select v-model="processForm.handleType" placeholder="请选择处理方式" style="width: 100%;">
            <el-option label="电话沟通" value="电话沟通" />
            <el-option label="现场检查" value="现场检查" />
            <el-option label="下发通知" value="下发通知" />
            <el-option label="限期整改" value="限期整改" />
            <el-option label="其他方式" value="其他方式" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
          <el-input v-model="processForm.handleResult" type="textarea" :rows="4" placeholder="请输入处理结果" />
        </el-form-item>
        <el-form-item label="后续措施" prop="followUpAction">
          <el-input v-model="processForm.followUpAction" type="textarea" :rows="3" placeholder="请输入后续措施" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitProcess" :loading="processLoading">确 定</el-button>
        <el-button @click="processOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 预警详情对话框 -->
    <el-dialog title="费用超额预警详情" :visible.sync="detailOpen" width="80%" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预警编号">{{ detailData.warningNo }}</el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ detailData.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ detailData.elderName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailData.idCard }}</el-descriptions-item>
        <el-descriptions-item label="月费用标准">
          <span>¥{{ detailData.monthlyFee | formatAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="预收金额">
          <span>¥{{ detailData.prepaidFee | formatAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="超额金额">
          <span class="text-danger font-bold">¥{{ detailData.excessAmount | formatAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="超额倍数">{{ detailData.excessMultiple }}倍</el-descriptions-item>
        <el-descriptions-item label="预警等级">
          <el-tag :type="getWarningTagType(detailData.warningLevel)">
            {{ detailData.warningLevel }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预警时间">{{ parseTime(detailData.warningTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag v-if="detailData.status === '待处理'" type="warning">待处理</el-tag>
          <el-tag v-else-if="detailData.status === '已处理'" type="success">已处理</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人" v-if="detailData.handlerName">{{ detailData.handlerName }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" v-if="detailData.handleTime">{{ parseTime(detailData.handleTime) }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">处理信息</el-divider>
      <el-descriptions :column="1" border v-if="detailData.handleType">
        <el-descriptions-item label="处理方式">{{ detailData.handleType }}</el-descriptions-item>
        <el-descriptions-item label="处理结果">{{ detailData.handleResult }}</el-descriptions-item>
        <el-descriptions-item label="后续措施">{{ detailData.followUpAction }}</el-descriptions-item>
      </el-descriptions>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
        <el-button type="primary" @click="handlePrintDetail">打印预警单</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFeeExcessWarning, getWarning, processWarning } from '@/api/supervision/warning'

export default {
  name: 'FeeExcessWarning',
  filters: {
    formatAmount(value) {
      if (!value) return '0.00';
      return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }
  },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      warningList: [],
      processOpen: false,
      detailOpen: false,
      processLoading: false,
      statistics: {
        pendingCount: 0,
        avgExcessAmount: 0,
        institutionCount: 0,
        elderCount: 0
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        elderName: null,
        warningLevel: null,
        status: null,
        dateRange: null
      },
      processForm: {},
      detailData: {},
      processRules: {
        handleType: [
          { required: true, message: '处理方式不能为空', trigger: 'change' }
        ],
        handleResult: [
          { required: true, message: '处理结果不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getStatistics()
  },
  methods: {
    getList() {
      this.loading = true
      listFeeExcessWarning(this.queryParams).then(response => {
        // TODO: 连接真实API
        // this.warningList = response.rows
        // this.total = response.total
        // this.loading = false

        // 模拟数据
        setTimeout(() => {
          this.warningList = [
            {
              warningId: 1,
              warningNo: 'WJ20250103-001',
              institutionName: '幸福养老院',
              elderName: '张奶奶',
              idCard: '410101194001010001',
              monthlyFee: 3000.00,
              prepaidFee: 50000.00,
              excessAmount: 14000.00,
              excessMultiple: 16.67,
              warningLevel: '高',
              warningTime: '2025-01-03 14:30:00',
              status: '待处理',
              handleType: null,
              handleResult: null,
              followUpAction: null,
              handlerName: null,
              handleTime: null
            },
            {
              warningId: 2,
              warningNo: 'WJ20250103-002',
              institutionName: '夕阳红公寓',
              elderName: '李爷爷',
              idCard: '410101193805150002',
              monthlyFee: 3500.00,
              prepaidFee: 45000.00,
              excessAmount: 3000.00,
              excessMultiple: 10.71,
              warningLevel: '中',
              warningTime: '2025-01-03 13:20:00',
              status: '已处理',
              handleType: '电话沟通',
              handleResult: '已与家属沟通，解释了收费情况，家属表示理解',
              followUpAction: '建议调整收费结构，避免费用超标',
              handlerName: '管理员1',
              handleTime: '2025-01-03 15:00:00'
            },
            {
              warningId: 3,
              warningNo: 'WJ20250102-003',
              institutionName: '康乐养老中心',
              elderName: '王奶奶',
              idCard: '410101194508200003',
              monthlyFee: 2800.00,
              prepaidFee: 40000.00,
              excessAmount: 6400.00,
              excessMultiple: 14.29,
              warningLevel: '高',
              warningTime: '2025-01-02 16:45:00',
              status: '待处理',
              handleType: null,
              handleResult: null,
              followUpAction: null,
              handlerName: null,
              handleTime: null
            }
          ]
          this.total = this.warningList.length
          this.loading = false
        }, 500)
      })
    },
    getStatistics() {
      // TODO: 调用真实API获取统计数据
      this.statistics = {
        pendingCount: 15,
        avgExcessAmount: 7800.50,
        institutionCount: 8,
        elderCount: 23
      }
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
    handleView(row) {
      const warningId = row.warningId
      getWarning(row.warningNo).then(response => {
        this.detailData = row
        this.detailOpen = true
      })
    },
    handleProcess(row) {
      this.processForm = {
        warningId: row.warningId,
        warningNo: row.warningNo,
        institutionName: row.institutionName,
        elderName: row.elderName,
        monthlyFee: row.monthlyFee,
        prepaidFee: row.prepaidFee,
        excessAmount: row.excessAmount,
        handleType: '',
        handleResult: '',
        followUpAction: ''
      }
      this.processOpen = true
    },
    submitProcess() {
      this.$refs['processForm'].validate(valid => {
        if (valid) {
          this.processLoading = true
          processWarning(this.processForm).then(response => {
            // TODO: 连接真实API
            // this.$modal.msgSuccess('处理成功')
            // this.processOpen = false
            // this.getList()
            // this.getStatistics()
            // this.processLoading = false

            // 模拟API调用
            setTimeout(() => {
              this.$modal.msgSuccess('处理成功')
              this.processOpen = false
              this.getList()
              this.getStatistics()
              this.processLoading = false
            }, 1000)
          }).catch(() => {
            this.processLoading = false
          })
        }
      })
    },
    handleBatchProcess() {
      if (this.ids.length === 0) {
        this.$modal.msgError('请选择要处理的数据')
        return
      }
      this.$modal.confirm('确认批量处理选中的' + this.ids.length + '条预警吗？').then(() => {
        // TODO: 实现批量处理
        this.$modal.msgSuccess('批量处理成功')
        this.getList()
        this.getStatistics()
      })
    },
    handleNotify(row) {
      // TODO: 实现通知功能
      this.$modal.msgInfo('通知功能开发中...')
    },
    handleExport() {
      this.download('supervision/warning/fee-excess/export', {
        ...this.queryParams
      }, `fee_excess_warning_${new Date().getTime()}.xlsx`)
    },
    handlePrintDetail() {
      // TODO: 实现打印功能
      this.$modal.msgInfo('打印功能开发中...')
    },
    getExcessTagType(multiple) {
      if (multiple >= 15) return 'danger'
      if (multiple >= 12) return 'warning'
      return 'info'
    },
    getWarningTagType(level) {
      if (level === '高') return 'danger'
      if (level === '中') return 'warning'
      return 'info'
    }
  }
}
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  font-size: 20px;
}

.bg-danger {
  background-color: #F56C6C;
}

.bg-warning {
  background-color: #E6A23C;
}

.bg-primary {
  background-color: #409EFF;
}

.bg-success {
  background-color: #67C23A;
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.text-danger {
  color: #F56C6C;
  font-weight: bold;
}

.font-bold {
  font-weight: bold;
}

.mb20 {
  margin-bottom: 20px;
}
</style>