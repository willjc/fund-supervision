<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="预警类型" prop="warningType">
        <el-select v-model="queryParams.warningType" placeholder="请选择预警类型" clearable>
          <el-option label="费用超额" value="费用超额" />
          <el-option label="押金超额" value="押金超额" />
          <el-option label="入驻超额" value="入驻超额" />
          <el-option label="监管超额" value="监管超额" />
          <el-option label="风险保证金" value="风险保证金" />
          <el-option label="大额支付" value="大额支付" />
          <el-option label="突发风险" value="突发风险" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="待处理" value="待处理" />
          <el-option label="已处理" value="已处理" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="warningList">
      <el-table-column label="预警编号" align="center" prop="warningNo" />
      <el-table-column label="预警类型" align="center" prop="warningType">
        <template slot-scope="scope">
          <el-tag :type="getWarningTypeTag(scope.row.warningType)">
            {{ scope.row.warningType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="机构名称" align="center" prop="institutionName" />
      <el-table-column label="预警内容" align="center" prop="warningContent" />
      <el-table-column label="预警级别" align="center" prop="warningLevel">
        <template slot-scope="scope">
          <el-tag :type="scope.row.warningLevel === '高' ? 'danger' : 'warning'">
            {{ scope.row.warningLevel }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="预警时间" align="center" prop="warningTime" width="180" />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '已处理' ? 'success' : 'danger'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="联系人" align="center" prop="contactPerson" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            v-if="scope.row.status === '待处理'"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleProcess(scope.row)"
          >处理</el-button>
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

    <!-- 预警详情对话框 -->
    <el-dialog title="预警详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预警编号">{{ detailData.warningNo }}</el-descriptions-item>
        <el-descriptions-item label="预警类型">{{ detailData.warningType }}</el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ detailData.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="预警级别">
          <el-tag :type="detailData.warningLevel === '高' ? 'danger' : 'warning'">
            {{ detailData.warningLevel }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="预警内容" :span="2">{{ detailData.warningContent }}</el-descriptions-item>
        <el-descriptions-item label="预警时间">{{ detailData.warningTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === '已处理' ? 'success' : 'danger'">
            {{ detailData.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detailData.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 处理预警对话框 -->
    <el-dialog title="处理预警" :visible.sync="processOpen" width="600px" append-to-body>
      <el-form ref="processForm" :model="processForm" :rules="processRules" label-width="120px">
        <el-form-item label="预警编号">
          <el-input v-model="processForm.warningNo" disabled />
        </el-form-item>
        <el-form-item label="处理方式" prop="handleType">
          <el-select v-model="processForm.handleType" placeholder="请选择处理方式">
            <el-option label="电话沟通" value="电话沟通" />
            <el-option label="现场检查" value="现场检查" />
            <el-option label="下发通知" value="下发通知" />
            <el-option label="限期整改" value="限期整改" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
          <el-input v-model="processForm.handleResult" type="textarea" placeholder="请输入处理结果" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="processForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitProcess">确 定</el-button>
        <el-button @click="processOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listWarning, getWarning, processWarning } from '@/api/supervision/warning'

export default {
  name: 'WarningList',
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      warningList: [],
      detailOpen: false,
      detailData: {},
      processOpen: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        warningType: null,
        status: null
      },
      processForm: {
        warningNo: '',
        handleType: '',
        handleResult: '',
        remark: ''
      },
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
  },
  methods: {
    getList() {
      this.loading = true
      listWarning(this.queryParams).then(response => {
        this.warningList = response.rows
        this.total = response.total
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
    handleDetail(row) {
      const warningNo = row.warningNo
      getWarning(warningNo).then(response => {
        this.detailData = response.data
        this.detailOpen = true
      })
    },
    handleProcess(row) {
      this.processForm = {
        warningNo: row.warningNo,
        handleType: '',
        handleResult: '',
        remark: ''
      }
      this.processOpen = true
    },
    submitProcess() {
      this.$refs['processForm'].validate(valid => {
        if (valid) {
          processWarning(this.processForm).then(response => {
            this.$modal.msgSuccess('处理成功')
            this.processOpen = false
            this.getList()
          })
        }
      })
    },
    handleExport() {
      this.download('supervision/warning/export', {
        ...this.queryParams
      }, `warning_${new Date().getTime()}.xlsx`)
    },
    getWarningTypeTag(type) {
      const typeMap = {
        '费用超额': 'danger',
        '押金超额': 'danger',
        '入驻超额': 'danger',
        '监管超额': 'danger',
        '风险保证金': 'warning',
        '大额支付': 'warning',
        '突发风险': 'danger'
      }
      return typeMap[type] || 'info'
    }
  }
}
</script>