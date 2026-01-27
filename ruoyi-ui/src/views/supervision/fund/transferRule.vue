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
        <el-table-column label="划付周期" prop="transferCycle" width="120">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.transferCycle === 'monthly'" type="primary">按月</el-tag>
            <el-tag v-else-if="scope.row.transferCycle === 'quarterly'" type="success">按季度</el-tag>
            <el-tag v-else-if="scope.row.transferCycle === 'yearly'" type="warning">按年</el-tag>
            <span v-else>{{ scope.row.transferCycle }}</span>
          </template>
        </el-table-column>
        <el-table-column label="划付日期" prop="transferDay" width="150">
          <template slot-scope="scope">
            <span v-if="scope.row.transferCycle === 'monthly'">每月{{ scope.row.transferDay }}日</span>
            <span v-else-if="scope.row.transferCycle === 'quarterly'">每季度第{{ scope.row.transferDay }}天</span>
            <span v-else-if="scope.row.transferCycle === 'yearly'">每年第{{ scope.row.transferDay }}天</span>
            <span v-else>{{ scope.row.transferDay }}</span>
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
              v-hasPermi="['supervision:transferRule:remove']"
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
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="划付周期" prop="transferCycle">
          <el-select v-model="form.transferCycle" placeholder="请选择划付周期">
            <el-option label="按月" value="monthly" />
            <el-option label="按季度" value="quarterly" />
            <el-option label="按年" value="yearly" />
          </el-select>
        </el-form-item>
        <el-form-item label="划付日期" prop="transferDay">
          <el-input-number v-model="form.transferDay" :min="1" :max="31" />
          <span style="margin-left: 10px; color: #909399;">1-31之间的数字</span>
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
        status: [
          { required: true, message: '状态不能为空', trigger: 'blur' }
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
        }
      })
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
        transferDay: 15,
        status: '0',
        remark: null
      }
      this.resetForm('form')
    }
  }
}
</script>
