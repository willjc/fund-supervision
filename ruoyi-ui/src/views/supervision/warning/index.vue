<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="预警类型" prop="warningType">
        <el-select v-model="queryParams.warningType" placeholder="请选择预警类型" clearable>
          <el-option label="预收费用超额" value="1" />
          <el-option label="押金超额" value="2" />
          <el-option label="入住人数超额" value="3" />
          <el-option label="预收总额超额" value="4" />
          <el-option label="风险保证金超低" value="5" />
          <el-option label="大额支出" value="6" />
          <el-option label="交易对方风险" value="7" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="待处理" value="0" />
          <el-option label="已处理" value="1" />
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
      <el-table-column label="预警时间" align="center" width="180">
        <template slot-scope="scope">
          {{ formatDateTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="机构名称" align="center" prop="institutionName" width="250" />
      <el-table-column label="预警事项" align="center" prop="warningType" min-width="150">
        <template slot-scope="scope">
          <el-tag :type="getWarningTypeTag(scope.row.warningTypeCode)">
            {{ scope.row.warningType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="预警内容" align="center" prop="warningContent" min-width="250" show-overflow-tooltip />
      <el-table-column label="机构联系人" align="center" prop="contactPerson" width="120" />
      <el-table-column label="联系方式" align="center" prop="contactPhone" width="130" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '已处理' ? 'success' : 'danger'">
            {{ scope.row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150" fixed="right">
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
    <el-dialog title="预警详情" :visible.sync="detailOpen" width="700px" append-to-body>
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
        <el-descriptions-item label="预警时间">{{ formatDateTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === '已处理' ? 'success' : 'danger'">
            {{ detailData.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="机构联系人">{{ detailData.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item v-if="detailData.status === '已处理'" label="处理人">{{ detailData.handler }}</el-descriptions-item>
        <el-descriptions-item v-if="detailData.status === '已处理'" label="处理时间">{{ formatDateTime(detailData.handleTime) }}</el-descriptions-item>
        <el-descriptions-item v-if="detailData.status === '已处理'" label="处理备注" :span="2">{{ detailData.handleRemark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 处理预警对话框 -->
    <el-dialog title="处理预警" :visible.sync="processOpen" width="500px" append-to-body>
      <el-form ref="processForm" :model="processForm" label-width="100px">
        <el-form-item label="预警编号">
          <el-input v-model="processForm.warningNo" disabled />
        </el-form-item>
        <el-form-item label="预警内容">
          <div class="warning-content">{{ processForm.warningContent }}</div>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="processForm.handleRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入处理备注（选填）"
          />
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
        warningContent: '',
        handleRemark: ''
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
        warningContent: row.warningContent,
        handleRemark: ''
      }
      this.processOpen = true
    },
    submitProcess() {
      const data = {
        warningNo: this.processForm.warningNo,
        handleRemark: this.processForm.handleRemark
      }
      processWarning(data).then(response => {
        this.$modal.msgSuccess('处理成功')
        this.processOpen = false
        this.getList()
      })
    },
    handleExport() {
      this.download('supervision/warning/export', {
        ...this.queryParams
      }, `warning_${new Date().getTime()}.xlsx`)
    },
    getWarningTypeTag(typeCode) {
      const typeMap = {
        '1': 'danger',
        '2': 'warning',
        '3': 'danger',
        '4': 'warning',
        '5': 'danger',
        '6': 'warning',
        '7': 'danger'
      }
      return typeMap[typeCode] || 'info'
    },
    formatDateTime(dateTime) {
      if (!dateTime) return ''
      // 处理ISO格式时间字符串
      const date = new Date(dateTime)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    }
  }
}
</script>

<style scoped>
.warning-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
}
</style>
