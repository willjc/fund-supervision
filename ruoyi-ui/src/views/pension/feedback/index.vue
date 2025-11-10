<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>投诉建议管理</span>
      </div>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="类型">
          <el-select v-model="queryParams.type" placeholder="请选择" clearable>
            <el-option label="投诉" value="投诉"></el-option>
            <el-option label="建议" value="建议"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="待处理" value="待处理"></el-option>
            <el-option label="处理中" value="处理中"></el-option>
            <el-option label="已完成" value="已完成"></el-option>
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
          <el-button type="primary" icon="el-icon-plus" @click="handleAdd">提交反馈</el-button>
        </el-col>
      </el-row>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="dataList" border>
        <el-table-column label="编号" prop="feedbackNo" width="120"></el-table-column>
        <el-table-column label="类型" prop="type" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === '投诉' ? 'danger' : 'primary'" size="small">
              {{ scope.row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="标题" prop="title" show-overflow-tooltip></el-table-column>
        <el-table-column label="内容" prop="content" show-overflow-tooltip></el-table-column>
        <el-table-column label="提交人" prop="submitter" width="100"></el-table-column>
        <el-table-column label="提交时间" prop="submitTime" width="180"></el-table-column>
        <el-table-column label="状态" prop="status" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" append-to-body>
      <el-form ref="feedbackForm" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="反馈类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="投诉">投诉</el-radio>
            <el-radio label="建议">建议</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题"></el-input>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            type="textarea"
            v-model="form.content"
            :rows="6"
            placeholder="请详细描述您的投诉或建议"></el-input>
        </el-form-item>
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系电话或邮箱"></el-input>
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            action="/system/common/upload"
            :on-success="handleUploadSuccess"
            :on-remove="handleRemove"
            :file-list="fileList">
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">支持上传图片或文档作为证明材料</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="反馈详情" :visible.sync="detailVisible" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="编号">{{ detailData.feedbackNo }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="detailData.type === '投诉' ? 'danger' : 'primary'">{{ detailData.type }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ detailData.content }}</el-descriptions-item>
        <el-descriptions-item label="提交人">{{ detailData.submitter }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ detailData.contact }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detailData.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ detailData.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理意见" :span="2">{{ detailData.handleComment || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" :span="2">{{ detailData.handleTime || '暂无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
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
        type: null,
        status: null
      },
      dialogVisible: false,
      dialogTitle: '',
      form: {
        type: '建议',
        title: '',
        content: '',
        contact: '',
        attachments: []
      },
      rules: {
        type: [
          { required: true, message: '请选择反馈类型', trigger: 'change' }
        ],
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' },
          { min: 10, message: '内容至少10个字符', trigger: 'blur' }
        ],
        contact: [
          { required: true, message: '请输入联系方式', trigger: 'blur' }
        ]
      },
      fileList: [],
      detailVisible: false,
      detailData: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 查询列表
    getList() {
      this.loading = true
      // TODO: 调用后端API
      setTimeout(() => {
        this.dataList = [
          {
            feedbackNo: 'FB202501001',
            type: '投诉',
            title: '服务态度问题',
            content: '护理员服务态度不好，希望改进',
            submitter: '张三',
            contact: '13800138000',
            submitTime: '2025-01-03 10:30:00',
            status: '已完成',
            handleComment: '已对相关人员进行培训教育',
            handleTime: '2025-01-04 14:00:00'
          },
          {
            feedbackNo: 'FB202501002',
            type: '建议',
            title: '增加文娱活动',
            content: '建议增加老年人的文娱活动项目',
            submitter: '李四',
            contact: '13900139000',
            submitTime: '2025-01-02 09:15:00',
            status: '处理中',
            handleComment: null,
            handleTime: null
          }
        ]
        this.total = 2
        this.loading = false
      }, 500)
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    // 重置
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 新增
    handleAdd() {
      this.dialogTitle = '提交反馈'
      this.form = {
        type: '建议',
        title: '',
        content: '',
        contact: '',
        attachments: []
      }
      this.fileList = []
      this.dialogVisible = true
    },
    // 提交表单
    submitForm() {
      this.$refs.feedbackForm.validate(valid => {
        if (valid) {
          this.submitting = true
          // TODO: 调用后端API
          setTimeout(() => {
            this.submitting = false
            this.dialogVisible = false
            this.$message.success('提交成功')
            this.getList()
          }, 1000)
        }
      })
    },
    // 查看详情
    handleView(row) {
      this.detailData = row
      this.detailVisible = true
    },
    // 文件上传成功
    handleUploadSuccess(response, file, fileList) {
      this.fileList = fileList
      this.form.attachments.push(response.url)
    },
    // 移除文件
    handleRemove(file, fileList) {
      this.fileList = fileList
    },
    // 获取状态类型
    getStatusType(status) {
      const typeMap = {
        '待处理': 'warning',
        '处理中': 'primary',
        '已完成': 'success'
      }
      return typeMap[status] || 'info'
    }
  }
}
</script>
