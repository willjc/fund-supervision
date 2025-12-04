<template>
  <div class="app-container">
    <!-- 顶部操作区 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增机构账号</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleBatchImport"
        >批量导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleDownloadTemplate"
        >下载模板</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
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
      <el-form-item label="负责人" prop="contactPerson">
        <el-input
          v-model="queryParams.contactPerson"
          placeholder="请输入负责人姓名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="入驻状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择入驻状态" clearable size="small">
          <el-option label="未申请" value="" />
          <el-option label="待审批" value="0" />
          <el-option label="已入驻" value="1" />
          <el-option label="已驳回" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 账号生成规则说明 -->
    <el-alert
      title="账号生成规则说明"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 20px;">
      <div style="line-height: 1.6;">
        <strong>用户名生成：</strong>jg + 联系电话后6位（如：13800138000 → jg38000，如有重复则加数字后缀）<br>
        <strong>初始密码：</strong>联系电话后6位（如：13800138000 → 38000）<br>
        <strong>默认角色：</strong>机构管理员，可直接登录使用
      </div>
    </el-alert>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="institutionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="机构ID" align="center" prop="institutionId" width="80" />
      <el-table-column label="机构名称" align="center" prop="institutionName" :show-overflow-tooltip="true" min-width="150" />
      <el-table-column label="统一信用代码" align="center" prop="creditCode" min-width="180" />
      <el-table-column label="负责人" align="center" prop="contactPerson" width="100" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="120" />
      <el-table-column label="关联账号" align="center" prop="remark" width="120" />
      <el-table-column label="入驻状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '4'" type="info">草稿</el-tag>
          <el-tag v-else-if="scope.row.status === '0'" type="warning">待审批</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="success">已入驻</el-tag>
          <el-tag v-else-if="scope.row.status === '2'" type="danger">已驳回</el-tag>
          <el-tag v-else type="info">未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >编辑</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-key"
            @click="handleResetPassword(scope.row)"
          >重置密码</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
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

    <!-- 新增机构账号对话框 -->
    <el-dialog :title="form.institutionId ? '修改机构信息' : '新增机构账号'" :visible.sync="openAdd" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="机构名称" prop="institutionName">
              <el-input v-model="form.institutionName" placeholder="请输入机构名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="统一信用代码" prop="creditCode">
              <el-input v-model="form.creditCode" placeholder="请输入统一信用代码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="负责人姓名" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入负责人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="注册资金" prop="registeredCapital">
              <el-input v-model="form.registeredCapital" placeholder="请输入注册资金(万元)" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="法定代表人" prop="legalPerson">
              <el-input v-model="form.legalPerson" placeholder="请输入法定代表人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="注册地址" prop="registeredAddress">
              <el-input v-model="form.registeredAddress" placeholder="请输入注册地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="实际地址" prop="actualAddress">
              <el-input v-model="form.actualAddress" placeholder="请输入实际地址" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="form.contactEmail" placeholder="请输入联系邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="床位数" prop="bedCount">
              <el-input v-model="form.bedCount" placeholder="请输入床位数" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 批量导入对话框 -->
    <el-dialog title="批量导入机构账号" :visible.sync="openImport" width="600px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处,或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">
          <el-link type="primary" @click="handleDownloadTemplate">点击下载模板</el-link>
          <br/>
          <span>仅允许导入xls、xlsx格式文件。</span>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="openImport = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 导入结果对话框 -->
    <el-dialog title="导入结果" :visible.sync="openResult" width="1000px" append-to-body>
      <el-alert
        :title="'导入完成! 共 ' + importResult.totalCount + ' 条,成功 ' + importResult.successCount + ' 条,失败 ' + importResult.failCount + ' 条'"
        :type="importResult.failCount > 0 ? 'warning' : 'success'"
        :closable="false"
        style="margin-bottom: 20px">
      </el-alert>

      <el-tabs v-model="resultTab">
        <!-- 成功列表 -->
        <el-tab-pane label="成功记录" name="success">
          <el-table :data="importResult.successList" style="width: 100%" max-height="400">
            <el-table-column prop="row" label="行号" width="80" />
            <el-table-column prop="institutionName" label="机构名称" width="150" />
            <el-table-column prop="contactPerson" label="负责人" width="100" />
            <el-table-column prop="contactPhone" label="联系电话" width="120" />
            <el-table-column prop="userName" label="账号" width="120" />
            <el-table-column prop="password" label="初始密码" width="120" />
          </el-table>
          <el-button
            v-if="importResult.successList && importResult.successList.length > 0"
            type="success"
            size="mini"
            style="margin-top: 10px"
            @click="exportAccountList"
          >导出账号信息</el-button>
        </el-tab-pane>

        <!-- 失败列表 -->
        <el-tab-pane label="失败记录" name="fail">
          <el-table :data="importResult.failList" style="width: 100%" max-height="400">
            <el-table-column prop="row" label="行号" width="80" />
            <el-table-column prop="institutionName" label="机构名称" width="200" />
            <el-table-column prop="reason" label="失败原因" />
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <div slot="footer" class="dialog-footer">
        <el-button @click="openResult = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listInstitutionAccounts,
  addInstitutionAccount,
  editInstitutionAccount,
  removeInstitutionAccount,
  resetPassword,
  downloadTemplate
} from '@/api/supervision/institution'
import { getToken } from '@/utils/auth'

export default {
  name: 'BatchImport',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 机构表格数据
      institutionList: [],
      // 弹出层标题
      title: '',
      // 是否显示新增对话框
      openAdd: false,
      // 是否显示导入对话框
      openImport: false,
      // 是否显示结果对话框
      openResult: false,
      // 结果标签页
      resultTab: 'success',
      // 导入结果
      importResult: {
        totalCount: 0,
        successCount: 0,
        failCount: 0,
        successList: [],
        failList: []
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        contactPerson: null,
        status: ''
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        institutionName: [
          { required: true, message: '机构名称不能为空', trigger: 'blur' }
        ],
        creditCode: [
          { required: true, message: '统一信用代码不能为空', trigger: 'blur' }
        ],
        contactPerson: [
          { required: true, message: '负责人姓名不能为空', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '联系电话不能为空', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ]
      },
      // 上传参数
      upload: {
        // 是否禁用上传
        isUploading: false,
        // 设置上传的请求头部
        headers: { Authorization: 'Bearer ' + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + '/supervision/institution/batch-import'
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询机构列表 */
    getList() {
      this.loading = true
      listInstitutionAccounts(this.queryParams).then(response => {
        this.institutionList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.openAdd = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        institutionId: null,
        institutionName: null,
        creditCode: null,
        contactPerson: null,
        contactPhone: null,
        registeredCapital: null,
        registeredAddress: null,
        actualAddress: null,
        legalPerson: null,
        contactEmail: null,
        bedCount: null
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.institutionId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.openAdd = true
      this.title = '新增机构账号'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.form = Object.assign({}, row)
      this.openAdd = true
      this.title = '修改机构信息'
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.institutionId != null) {
            // 修改
            editInstitutionAccount(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.openAdd = false
              this.getList()
            })
          } else {
            // 新增
            addInstitutionAccount(this.form).then(response => {
              this.$modal.msgSuccess('创建成功!')
              this.$alert(
                `机构名称: ${response.data.institutionName}<br/>
                 账号: ${response.data.userName}<br/>
                 初始密码: ${response.data.password}<br/>
                 <span style="color: red;">请妥善保管账号密码!</span>`,
                '账号创建成功',
                {
                  dangerouslyUseHTMLString: true,
                  confirmButtonText: '确定'
                }
              )
              this.openAdd = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const institutionIds = row.institutionId || this.ids
      this.$modal.confirm('是否确认删除选中的机构账号?').then(function() {
        return removeInstitutionAccount(institutionIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    /** 重置密码 */
    handleResetPassword(row) {
      this.$modal.confirm('确认要重置该机构管理员的密码吗?').then(() => {
        return resetPassword(row.institutionId)
      }).then(response => {
        this.$alert(
          `新密码: ${response.data.newPassword}<br/>
           <span style="color: red;">请通知机构管理员及时修改密码!</span>`,
          '密码重置成功',
          {
            dangerouslyUseHTMLString: true,
            confirmButtonText: '确定'
          }
        )
      }).catch(() => {})
    },
    /** 批量导入按钮操作 */
    handleBatchImport() {
      this.openImport = true
    },
    /** 下载模板操作 */
    handleDownloadTemplate() {
      downloadTemplate().then(response => {
        const url = window.URL.createObjectURL(new Blob([response]))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', 'institution_import_template.xlsx')
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
      })
    },
    /** 提交上传文件 */
    submitFileForm() {
      this.$refs.upload.submit()
    },
    /** 文件上传中处理 */
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true
    },
    /** 文件上传成功处理 */
    handleFileSuccess(response, file, fileList) {
      this.upload.isUploading = false
      this.openImport = false
      this.$refs.upload.clearFiles()

      if (response.code === 200) {
        this.importResult = response.data
        this.resultTab = 'success'
        this.openResult = true
        this.getList()
      } else {
        this.$modal.msgError(response.msg)
      }
    },
    /** 导出账号信息 */
    exportAccountList() {
      const data = this.importResult.successList
      let content = '机构名称,统一信用代码,负责人,联系电话,账号,初始密码\n'
      data.forEach(item => {
        content += `${item.institutionName},${item.creditCode},${item.contactPerson},${item.contactPhone},${item.userName},${item.password}\n`
      })

      const blob = new Blob(['\uFEFF' + content], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      link.href = URL.createObjectURL(blob)
      link.download = `机构账号信息_${new Date().getTime()}.csv`
      link.click()
    }
  }
}
</script>
