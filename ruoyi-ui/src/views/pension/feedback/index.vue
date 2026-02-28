<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>投诉建议管理</span>
      </div>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="机构名称" prop="institutionName">
          <el-input v-model="queryParams.institutionName" placeholder="请输入机构名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="处理中" value="1"></el-option>
            <el-option label="已处理" value="2"></el-option>
            <el-option label="已拒绝" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="dataList" border>
        <el-table-column label="投诉编号" prop="complaintNo" width="150" />
        <el-table-column label="机构名称" prop="institutionName" show-overflow-tooltip />
        <el-table-column label="投诉标题" prop="title" show-overflow-tooltip />
        <el-table-column label="投诉类型" prop="complaintType" width="100" />
        <el-table-column label="联系人" prop="submitter" width="100" />
        <el-table-column label="联系电话" prop="contact" width="120" />
        <el-table-column label="提交时间" prop="submitTime" width="160" />
        <el-table-column label="状态" prop="status" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">{{ scope.row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="text" size="small" @click="handleHandle(scope.row)" v-if="scope.row.status === '1'">处理</el-button>
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
    <el-dialog title="投诉详情" :visible.sync="detailVisible" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="投诉编号">{{ detailData.complaintNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ detailData.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="机构名称" :span="2">{{ detailData.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="投诉类型">{{ detailData.complaintType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.contact }}</el-descriptions-item>
        <el-descriptions-item label="投诉标题" :span="2">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="投诉内容" :span="2">{{ detailData.content }}</el-descriptions-item>
        <el-descriptions-item label="图片附件" :span="2" v-if="detailData.images && detailData.images.length > 0">
          <div class="image-preview-list">
            <el-image
              v-for="(img, idx) in detailData.images"
              :key="idx"
              :src="img"
              :preview-src-list="detailData.images"
              fit="cover"
              style="width: 100px; height: 100px; margin-right: 10px; border-radius: 4px;"
            />
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ parseTime(detailData.submitTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detailData.handleUserName || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="回复内容" :span="2">{{ detailData.replyContent || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" :span="2">{{ detailData.handleTime ? parseTime(detailData.handleTime) : '暂无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleHandle(detailData)" v-if="detailData.status === '1'">处理投诉</el-button>
      </div>
    </el-dialog>

    <!-- 处理投诉对话框 -->
    <el-dialog title="处理投诉" :visible.sync="handleVisible" width="600px" append-to-body>
      <el-form ref="handleForm" :model="handleForm" :rules="handleRules" label-width="100px">
        <el-form-item label="投诉编号">
          <el-input v-model="handleForm.complaintNo" disabled />
        </el-form-item>
        <el-form-item label="投诉标题">
          <el-input v-model="handleForm.title" disabled />
        </el-form-item>
        <el-form-item label="处理状态" prop="status">
          <el-radio-group v-model="handleForm.status">
            <el-radio label="2">已处理</el-radio>
            <el-radio label="0">已拒绝</el-radio>
            <el-radio label="1">处理中</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="回复内容" prop="replyContent">
          <el-input
            type="textarea"
            v-model="handleForm.replyContent"
            :rows="6"
            placeholder="请输入回复内容（将显示给用户）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="handleVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitHandle" :loading="submitting">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFeedback, getFeedbackDetail, handleFeedback } from '@/api/pension/feedback'

export default {
  name: 'FeedbackManagement',
  data() {
    return {
      loading: false,
      submitting: false,
      dataList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        status: null
      },
      detailVisible: false,
      detailData: {},
      handleVisible: false,
      handleForm: {
        complaintId: null,
        complaintNo: '',
        title: '',
        status: '2',
        replyContent: ''
      },
      handleRules: {
        status: [
          { required: true, message: '请选择处理状态', trigger: 'change' }
        ],
        replyContent: [
          { required: true, message: '请输入回复内容', trigger: 'blur' },
          { min: 5, message: '回复内容至少5个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询列表
    getList() {
      this.loading = true
      listFeedback(this.queryParams).then(response => {
        this.dataList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    // 重置
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        status: null
      }
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 查看详情
    handleView(row) {
      getFeedbackDetail(row.complaintId).then(response => {
        this.detailData = response.data
        this.detailVisible = true
      })
    },
    // 处理投诉
    handleHandle(row) {
      this.handleForm.complaintId = row.complaintId
      this.handleForm.complaintNo = row.complaintNo
      this.handleForm.title = row.title
      this.handleForm.status = '2'
      this.handleForm.replyContent = ''
      this.handleVisible = true
      this.$nextTick(() => {
        this.$refs.handleForm && this.$refs.handleForm.clearValidate()
      })
    },
    // 提交处理
    submitHandle() {
      this.$refs.handleForm.validate(valid => {
        if (valid) {
          this.submitting = true
          handleFeedback({
            complaintId: this.handleForm.complaintId,
            status: this.handleForm.status,
            replyContent: this.handleForm.replyContent
          }).then(response => {
            this.submitting = false
            this.handleVisible = false
            this.$message.success('处理成功')
            this.getList()
          }).catch(() => {
            this.submitting = false
          })
        }
      })
    },
    // 获取状态类型
    getStatusType(status) {
      const typeMap = {
        '0': 'info',
        '1': 'warning',
        '2': 'success'
      }
      return typeMap[status] || 'info'
    }
  }
}
</script>

<style scoped>
.image-preview-list {
  display: flex;
  flex-wrap: wrap;
}
</style>
