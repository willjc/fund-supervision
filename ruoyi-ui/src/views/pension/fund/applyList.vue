<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>划拨申请列表</span>
      </div>

      <!-- 搜索表单 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch">
        <el-form-item label="申请单号" prop="applyNo">
          <el-input
            v-model="queryParams.applyNo"
            placeholder="请输入申请单号"
            clearable
            size="small"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="申请状态" prop="applyStatus">
          <el-select v-model="queryParams.applyStatus" placeholder="请选择状态" clearable size="small">
            <el-option label="全部" value="" />
            <el-option label="待家属确认" value="pending_family" />
            <el-option label="待监管审核" value="pending_supervision" />
            <el-option label="已批准" value="approved" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="已完成" value="completed" />
          </el-select>
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
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['pension:fundTransferApply:add']"
          >新增申请</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="applyList" border>
        <el-table-column label="申请单号" prop="applyNo" width="180" />
        <el-table-column label="老人姓名" prop="elderName" width="100" />
        <el-table-column label="床位信息" prop="bedInfo" width="150" show-overflow-tooltip />
        <el-table-column label="申请金额" prop="applyAmount" width="120" align="center">
          <template slot-scope="scope">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ formatMoney(scope.row.applyAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="紧急程度" prop="urgencyLevel" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.urgencyLevel === '紧急'" type="warning">紧急</el-tag>
            <el-tag v-else type="info">一般</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请状态" prop="applyStatus" width="120" align="center">
          <template slot-scope="scope">
            <dict-tag :options="statusOptions" :value="scope.row.applyStatus"/>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" prop="createTime" width="160" align="center">
          <template slot-scope="scope">
            {{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleView(scope.row)"
            >详情</el-button>
            <el-button
              v-if="scope.row.applyStatus === 'pending_supervision'"
              size="mini"
              type="text"
              icon="el-icon-check"
              @click="handleSupervisionApprove(scope.row)"
              v-hasPermi="['pension:fundTransferApply:supervisionApprove']"
            >审批</el-button>
            <el-button
              v-if="scope.row.applyStatus === 'approved'"
              size="mini"
              type="text"
              icon="el-icon-s-promotion"
              @click="handleExecute(scope.row)"
              v-hasPermi="['pension:fundTransferApply:execute']"
            >执行</el-button>
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

    <!-- 详情对话框 -->
    <el-dialog title="申请详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions v-if="detailData" :column="2" border>
        <el-descriptions-item label="申请单号">{{ detailData.applyNo }}</el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ detailData.elderName }}</el-descriptions-item>
        <el-descriptions-item label="申请金额">
          <span style="color: #f56c6c; font-weight: bold;">¥{{ formatMoney(detailData.applyAmount) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="紧急程度">
          <el-tag :type="detailData.urgencyLevel === '紧急' ? 'warning' : 'info'">{{ detailData.urgencyLevel }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <dict-tag :options="statusOptions" :value="detailData.applyStatus"/>
        </el-descriptions-item>
        <el-descriptions-item label="申请原因" :span="2">{{ detailData.applyReason }}</el-descriptions-item>
        <el-descriptions-item label="期望划拨日期">
          {{ detailData.expectedUseDate || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ parseTime(detailData.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
        </el-descriptions-item>
        <el-descriptions-item label="家属确认" :span="2">
          {{ detailData.familyConfirmName || '未确认' }}
        </el-descriptions-item>
        <el-descriptions-item label="监管审核" :span="2">
          {{ detailData.approver || '未审核' }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog title="划拨申请审批" :visible.sync="approveOpen" width="600px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" :rules="approveRules" label-width="120px">
        <el-form-item label="申请单号">
          <el-input v-model="approveForm.applyNo" disabled />
        </el-form-item>
        <el-form-item label="申请金额">
          <el-input v-model="approveForm.applyAmount" disabled />
        </el-form-item>
        <el-form-item label="审批结果" prop="approved">
          <el-radio-group v-model="approveForm.approved">
            <el-radio :label="true">通过</el-radio>
            <el-radio :label="false">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="remark">
          <el-input v-model="approveForm.remark" type="textarea" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitApprove">确 定</el-button>
        <el-button @click="approveOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFundTransferApply, getFundTransferApply, supervisionApprove, executeTransfer } from '@/api/pension/fundTransferApply'

export default {
  name: 'FundTransferApplyList',
  data() {
    return {
      loading: false,
      showSearch: true,
      applyList: [],
      total: 0,
      detailOpen: false,
      approveOpen: false,
      detailData: null,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        applyNo: null,
        applyStatus: ''
      },
      approveForm: {
        applyId: null,
        applyNo: '',
        applyAmount: '',
        approved: true,
        remark: ''
      },
      approveRules: {
        remark: [
          { required: true, message: '请输入审批意见', trigger: 'blur' }
        ]
      },
      statusOptions: [
        { label: '待家属确认', value: 'pending_family', color: 'info' },
        { label: '待监管审核', value: 'pending_supervision', color: 'warning' },
        { label: '已批准', value: 'approved', color: 'success' },
        { label: '已拒绝', value: 'rejected', color: 'danger' },
        { label: '已完成', value: 'completed', color: 'success' }
      ]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listFundTransferApply(this.queryParams).then(response => {
        this.applyList = response.rows || []
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
      this.$router.push('/pension/fund/apply')
    },
    handleView(row) {
      getFundTransferApply(row.applyId).then(response => {
        this.detailData = response.data
        this.detailOpen = true
      })
    },
    handleSupervisionApprove(row) {
      this.approveForm = {
        applyId: row.applyId,
        applyNo: row.applyNo,
        applyAmount: row.applyAmount,
        approved: true,
        remark: ''
      }
      this.approveOpen = true
    },
    submitApprove() {
      this.$refs.approveForm.validate(valid => {
        if (valid) {
          const data = {
            applyId: this.approveForm.applyId,
            approved: this.approveForm.approved,
            remark: this.approveForm.remark
          }
          supervisionApprove(data).then(response => {
            this.$message.success('审批成功')
            this.approveOpen = false
            this.getList()
          })
        }
      })
    },
    handleExecute(row) {
      this.$confirm('确认执行此划拨申请吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        executeTransfer(row.applyId).then(response => {
          this.$message.success('执行成功')
          this.getList()
        })
      })
    },
    formatMoney(value) {
      if (!value) return '0.00'
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }
  }
}
</script>

<style scoped lang="scss">
.page-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}
</style>
