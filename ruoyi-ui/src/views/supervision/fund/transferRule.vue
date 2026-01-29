<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>划付规则配置</span>
      </div>

      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input
            v-model="queryParams.ruleName"
            placeholder="请输入规则名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            icon="el-icon-plus"
            @click="handleAdd"
            v-hasPermi="['supervision:transferRule:add']"
          >新增</el-button>
        </el-col>
      </el-row>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="ruleList">
        <el-table-column label="规则名称" prop="ruleName" />
        <el-table-column label="划付周期" prop="transferCycle" width="150">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.transferCycle === 'monthly'" type="primary">按月</el-tag>
            <el-tag v-else-if="scope.row.transferCycle === 'quarterly'" type="success">按季度</el-tag>
            <el-tag v-else-if="scope.row.transferCycle === 'yearly'" type="warning">按年</el-tag>
            <span v-else>{{ scope.row.transferCycle }}</span>
          </template>
        </el-table-column>
        <el-table-column label="划付日期" prop="transferDay" width="200">
          <template slot-scope="scope">
            <span v-if="scope.row.transferCycle === 'monthly'" class="date-display">
              每月{{ scope.row.transferDay }}日 {{ scope.row.transferTime || '09:00' }}
              <el-tag v-if="scope.row.transferDay === 1 && scope.row.transferTime === '09:00'" type="success" size="mini" style="margin-left: 5px;">建议</el-tag>
            </span>
            <span v-else-if="scope.row.transferCycle === 'quarterly'" class="date-display">
              每季度第{{ scope.row.transferDay }}天 {{ scope.row.transferTime || '09:00' }}
              <el-tag v-if="scope.row.transferDay === 1 && scope.row.transferTime === '09:00'" type="success" size="mini" style="margin-left: 5px;">建议</el-tag>
            </span>
            <span v-else-if="scope.row.transferCycle === 'yearly'" class="date-display">
              每年第{{ scope.row.transferDay }}天 {{ scope.row.transferTime || '09:00' }}
              <el-tag v-if="scope.row.transferDay === 1 && scope.row.transferTime === '09:00'" type="success" size="mini" style="margin-left: 5px;">建议</el-tag>
            </span>
            <span v-else>{{ scope.row.transferDay }}日 {{ scope.row.transferTime || '09:00' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="执行说明" width="250">
          <template slot-scope="scope">
            <span class="execute-desc">{{ getExecuteDescription(scope.row) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" show-overflow-tooltip />
        <el-table-column label="操作" align="center" width="180">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['supervision:transferRule:edit']"
            >修改</el-button>
            <el-button
              size="mini"
              type="text"
              @click="handleDelete(scope.row)"
              v-hasPermi='["supervision:transferRule:remove"]'
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加/修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="请输入规则名称，如：月度划拨规则" />
        </el-form-item>
        <el-form-item label="划付周期" prop="transferCycle">
          <el-select v-model="form.transferCycle" placeholder="请选择划付周期" @change="handleCycleChange">
            <el-option label="按月划付" value="monthly">
              <div style="display: flex; justify-content: space-between; align-items: center; min-width: 280px;">
                <span>按月划付</span>
                <span style="color: #909399; font-size: 12px;">每月固定日期执行</span>
              </div>
            </el-option>
            <el-option label="按季度划付" value="quarterly">
              <div style="display: flex; justify-content: space-between; align-items: center; min-width: 280px;">
                <span>按季度划付</span>
                <span style="color: #909399; font-size: 12px;">每季度固定日期执行</span>
              </div>
            </el-option>
            <el-option label="按年划付" value="yearly">
              <div style="display: flex; justify-content: space-between; align-items: center; min-width: 280px;">
                <span>按年划付</span>
                <span style="color: #909399; font-size: 12px;">每年固定日期执行</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="划付日期" prop="transferDay">
          <div style="display: flex; align-items: center;">
            <el-input-number
              v-model="form.transferDay"
              :min="transferDayMin"
              :max="transferDayMax"
              controls-position="right"
              style="width: 200px"
            />
            <span style="margin-left: 10px; color: #909399;">{{ transferDayLabel }}</span>
          </div>
          <div style="margin-top: 5px; color: #409EFF; font-size: 12px;">
            {{ transferDayTip }}
          </div>
        </el-form-item>
        <el-form-item label="划付时间" prop="transferTime">
          <el-time-picker
            v-model="form.transferTime"
            format="HH:mm"
            value-format="HH:mm"
            placeholder="选择划付时间"
            style="width: 200px;"
          />
          <span style="margin-left: 10px; color: #909399;">建议选择工作日的上班时间（09:00）</span>
        </el-form-item>
        <el-form-item label="执行说明">
          <span class="execute-desc-form">{{ getFormExecuteDescription() }}</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTransferRule, getTransferRule, addTransferRule, updateTransferRule, delTransferRule } from '@/api/supervision/fund'

export default {
  name: 'TransferRule',
  data() {
    return {
      loading: false,
      showSearch: true,
      ruleList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ruleName: null,
        status: null
      },
      title: '',
      open: false,
      form: {},
      rules: {
        ruleName: [
          { required: true, message: '规则名称不能为空', trigger: 'blur' }
        ],
        transferCycle: [
          { required: true, message: '划付周期不能为空', trigger: 'change' }
        ],
        transferDay: [
          { required: true, message: '划付日期不能为空', trigger: 'blur' }
        ],
        transferTime: [
          { required: true, message: '划付时间不能为空', trigger: 'change' }
        ],
        status: [
          { required: true, message: '状态不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    transferDayMin() {
      if (this.form.transferCycle === 'monthly') return 1
      if (this.form.transferCycle === 'quarterly') return 1
      if (this.form.transferCycle === 'yearly') return 1
      return 1
    },
    transferDayMax() {
      if (this.form.transferCycle === 'monthly') return 28
      if (this.form.transferCycle === 'quarterly') return 90
      if (this.form.transferCycle === 'yearly') return 365
      return 31
    },
    transferDayLabel() {
      if (this.form.transferCycle === 'monthly') return '(1-28日)'
      if (this.form.transferCycle === 'quarterly') return '(1-90天)'
      if (this.form.transferCycle === 'yearly') return '(1-365天)'
      return '(1-31)'
    },
    transferDayTip() {
      const time = this.form.transferTime || '09:00'
      if (this.form.transferCycle === 'monthly') {
        if (this.form.transferDay === 1 && time === '09:00') return '每月1号 09:00 划付（建议）'
        return `每月${this.form.transferDay}号 ${time} 划付`
      }
      if (this.form.transferCycle === 'quarterly') {
        if (this.form.transferDay === 1 && time === '09:00') return '每季度第1天=季度首月1号 09:00 划付（建议）'
        if (this.form.transferDay <= 30) return `每季度第${this.form.transferDay}天=季度首月${this.form.transferDay}号 ${time} 划付`
        return `每季度第${this.form.transferDay}天 ${time} 划付`
      }
      if (this.form.transferCycle === 'yearly') {
        if (this.form.transferDay === 1 && time === '09:00') return '每年第1天=1月1号 09:00 划付（建议）'
        if (this.form.transferDay <= 60) return `每年第${this.form.transferDay}天≈年初${Math.ceil(this.form.transferDay/30)}月${this.form.transferDay%30}号 ${time} 划付`
        return `每年第${this.form.transferDay}天 ${time} 划付`
      }
      return '请选择划付周期'
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listTransferRule(this.queryParams).then(response => {
        this.ruleList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加划付规则'
    },
    handleUpdate(row) {
      this.reset()
      const ruleId = row.ruleId
      getTransferRule(ruleId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改划付规则'
      })
    },
    handleDelete(row) {
      this.$confirm('是否确认删除划付规则配置"' + row.ruleName + '"?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        return delTransferRule(row.ruleId)
      }).then(() => {
        this.getList()
        this.$message.success('删除成功')
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          // 按月建议使用1号
          if (this.form.transferCycle === 'monthly' && this.form.transferDay !== 1) {
            this.$confirm('按月划拨建议使用每月1号，是否继续？', '提示', {
              confirmButtonText: '继续',
              cancelButtonText: '修改为1号',
              type: 'warning'
            }).then(() => {
              this.saveForm()
            }).catch(() => {
              this.form.transferDay = 1
              this.$refs.form.validateField('transferDay')
            })
          } else if (this.form.transferCycle === 'quarterly' && this.form.transferDay !== 1) {
            this.$confirm('按季度划拨建议使用每季度第1天（即季度首月1号），是否继续？', '提示', {
              confirmButtonText: '继续',
              cancelButtonText: '修改为1号',
              type: 'warning'
            }).then(() => {
              this.saveForm()
            }).catch(() => {
              this.form.transferDay = 1
              this.$refs.form.validateField('transferDay')
            })
          } else if (this.form.transferCycle === 'yearly' && this.form.transferDay !== 1) {
            this.$confirm('按年划拨建议使用每年第1天（即1月1号），是否继续？', '提示', {
              confirmButtonText: '继续',
              cancelButtonText: '修改为1号',
              type: 'warning'
            }).then(() => {
              this.saveForm()
            }).catch(() => {
              this.form.transferDay = 1
              this.$refs.form.validateField('transferDay')
            })
          } else {
            this.saveForm()
          }
        }
      })
    },
    saveForm() {
      if (this.form.ruleId != null) {
        updateTransferRule(this.form).then(() => {
          this.$message.success('修改成功')
          this.open = false
          this.getList()
        })
      } else {
        addTransferRule(this.form).then(() => {
          this.$message.success('新增成功')
          this.open = false
          this.getList()
        })
      }
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        ruleId: null,
        ruleName: null,
        transferCycle: 'monthly',
        transferDay: 1,
        transferTime: '09:00',
        status: '0',
        remark: null
      }
      this.resetForm('form')
    },
    handleCycleChange() {
      // 划付周期变化时，调整划付日期到推荐值
      if (this.form.transferCycle === 'monthly' || this.form.transferCycle === 'quarterly' || this.form.transferCycle === 'yearly') {
        this.form.transferDay = 1
      }
    },
    getExecuteDescription(row) {
      const time = row.transferTime || '09:00'
      if (row.transferCycle === 'monthly') {
        return `每月${row.transferDay}日 ${time} 自动划拨资金到基本账户`
      }
      if (row.transferCycle === 'quarterly') {
        if (row.transferDay <= 30) {
          return `每季度第${row.transferDay}天（即季度首月${row.transferDay}日）${time} 自动划拨`
        }
        return `每季度第${row.transferDay}天 ${time} 自动划拨`
      }
      if (row.transferCycle === 'yearly') {
        if (row.transferDay <= 60) {
          const month = Math.ceil(row.transferDay / 30)
          const day = row.transferDay % 30 === 0 ? 30 : row.transferDay % 30
          return `每年第${row.transferDay}天（约${month}月${day}日）${time} 自动划拨`
        }
        return `每年第${row.transferDay}天 ${time} 自动划拨`
      }
      return ''
    },
    getFormExecuteDescription() {
      if (!this.form.transferCycle) return '请先选择划付周期'
      const time = this.form.transferTime || '09:00'
      if (this.form.transferCycle === 'monthly') {
        return `每月${this.form.transferDay}日 ${time} 自动执行划拨`
      }
      if (this.form.transferCycle === 'quarterly') {
        if (this.form.transferDay <= 30) {
          return `每季度第${this.form.transferDay}天（季度首月${this.form.transferDay}日）${time} 执行`
        }
        return `每季度第${this.form.transferDay}天 ${time} 执行`
      }
      if (this.form.transferCycle === 'yearly') {
        if (this.form.transferDay <= 60) {
          const month = Math.ceil(this.form.transferDay / 30)
          const day = this.form.transferDay % 30 === 0 ? 30 : this.form.transferDay % 30
          return `每年第${this.form.transferDay}天（约${month}月${day}日）${time} 执行`
        }
        return `每年第${this.form.transferDay}天 ${time} 执行`
      }
      return ''
    }
  }
}
</script>

<style lang="scss" scoped>
.execute-desc {
  color: #606266;
  font-size: 13px;
}

.execute-desc-form {
  color: #409EFF;
  font-weight: 500;
}

.date-display {
  .el-tag {
    margin-left: 8px;
  }
}
</style>