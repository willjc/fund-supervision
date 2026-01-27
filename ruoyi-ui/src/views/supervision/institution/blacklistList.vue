<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="黑名单类型" prop="blacklistType">
        <el-select v-model="queryParams.blacklistType" placeholder="请选择黑名单类型" clearable>
          <el-option label="违规收费" value="违规收费" />
          <el-option label="服务质量差" value="服务质量差" />
          <el-option label="安全隐患" value="安全隐患" />
          <el-option label="虚假宣传" value="虚假宣传" />
          <el-option label="其他违规" value="其他违规" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="生效中" value="1" />
          <el-option label="已解除" value="2" />
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['supervision:institution:blacklist:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['supervision:institution:blacklist:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['supervision:institution:blacklist:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="blacklistList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="机构ID" align="center" prop="institutionId" />
      <el-table-column label="机构名称" align="center" prop="institutionName" />
      <el-table-column label="黑名单类型" align="center" prop="blacklistType">
        <template slot-scope="scope">
          <el-tag :type="getTypeTag(scope.row.blacklistType)">
            {{ scope.row.blacklistType }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="原因描述" align="center" prop="reason" show-overflow-tooltip />
      <el-table-column label="加入时间" align="center" prop="addTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.addTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '1' ? 'danger' : 'success'">
            {{ scope.row.status === '1' ? '生效中' : '已解除' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['supervision:institution:blacklist:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supervision:institution:blacklist:remove']"
          >删除</el-button>
          <el-button
            v-if="scope.row.status === '1'"
            size="mini"
            type="text"
            icon="el-icon-unlock"
            @click="handleRemove(scope.row)"
            v-hasPermi="['supervision:institution:blacklist:remove']"
          >移除</el-button>
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

    <!-- 添加或修改黑名单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="机构名称" prop="institutionName">
          <el-input v-model="form.institutionName" placeholder="请输入机构名称" />
        </el-form-item>
        <el-form-item label="黑名单类型" prop="blacklistType">
          <el-select v-model="form.blacklistType" placeholder="请选择黑名单类型">
            <el-option label="违规收费" value="违规收费" />
            <el-option label="服务质量差" value="服务质量差" />
            <el-option label="安全隐患" value="安全隐患" />
            <el-option label="虚假宣传" value="虚假宣传" />
            <el-option label="其他违规" value="其他违规" />
          </el-select>
        </el-form-item>
        <el-form-item label="原因描述" prop="reason">
          <el-input v-model="form.reason" type="textarea" placeholder="请输入原因描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">生效中</el-radio>
            <el-radio label="2">已解除</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="黑名单详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="机构ID">{{ detailForm.institutionId }}</el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ detailForm.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="黑名单类型">
          <el-tag :type="getTypeTag(detailForm.blacklistType)">
            {{ detailForm.blacklistType }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailForm.status === '1' ? 'danger' : 'success'">
            {{ detailForm.status === '1' ? '生效中' : '已解除' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="加入时间">{{ detailForm.addTime }}</el-descriptions-item>
        <el-descriptions-item label="移除时间">{{ detailForm.removeTime || '未移除' }}</el-descriptions-item>
        <el-descriptions-item label="原因描述" :span="2">{{ detailForm.reason }}</el-descriptions-item>
        <el-descriptions-item label="处理意见" :span="2">{{ detailForm.handleOpinion || '暂无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBlacklist, addBlacklist, removeBlacklist } from '@/api/supervision/institution'

export default {
  name: 'BlacklistList',
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
      // 黑名单表格数据
      blacklistList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        blacklistType: null,
        status: null
      },
      // 表单参数
      form: {},
      // 详情表单
      detailForm: {},
      // 表单校验
      rules: {
        institutionName: [
          { required: true, message: '机构名称不能为空', trigger: 'blur' }
        ],
        blacklistType: [
          { required: true, message: '黑名单类型不能为空', trigger: 'change' }
        ],
        reason: [
          { required: true, message: '原因描述不能为空', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '状态不能为空', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询黑名单列表 */
    getList() {
      this.loading = true
      listBlacklist(this.queryParams).then(response => {
        // 模拟数据
        const mockData = {
          total: 15,
          rows: Array.from({ length: 15 }, (_, i) => ({
            id: 100 + i,
            institutionId: 1000 + i,
            institutionName: ['阳光养老院', '康复中心', '老年之家', '福星养老院', '康乐养老中心'][i % 5] + (i + 1),
            blacklistType: ['违规收费', '服务质量差', '安全隐患', '虚假宣传', '其他违规'][i % 5],
            reason: '多次违规收费，情节严重，经核查属实',
            addTime: this.parseTime(new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000), '{y}-{m}-{d} {h}:{i}:{s}'),
            status: Math.random() > 0.3 ? '1' : '2',
            removeTime: Math.random() > 0.7 ? this.parseTime(new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000), '{y}-{m}-{d} {h}:{i}:{s}') : null,
            handleOpinion: '经整改检查，已符合相关要求，同意移除黑名单'
          }))
        }
        this.blacklistList = mockData.rows
        this.total = mockData.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        institutionId: null,
        institutionName: null,
        blacklistType: null,
        reason: null,
        status: '1'
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加黑名单'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      this.form = { ...row }
      this.open = true
      this.title = '修改黑名单'
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          } else {
            this.form.id = Date.now()
            this.form.institutionId = this.form.institutionId || Math.floor(Math.random() * 9000) + 1000
            this.form.addTime = this.parseTime(new Date(), '{y}-{m}-{d} {h}:{i}:{s}')
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除黑名单编号为"' + ids + '"的数据项？').then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      })
    },
    /** 移除黑名单操作 */
    handleRemove(row) {
      this.$modal.confirm(`确认将"${row.institutionName}"从黑名单中移除吗？`).then(() => {
        this.$modal.msgSuccess('移除成功')
        this.getList()
      })
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.detailForm = { ...row }
      this.detailOpen = true
    },
    /** 获取类型标签颜色 */
    getTypeTag(type) {
      const tagMap = {
        '违规收费': 'danger',
        '服务质量差': 'warning',
        '安全隐患': 'danger',
        '虚假宣传': 'info',
        '其他违规': 'warning'
      }
      return tagMap[type] || 'info'
    }
  }
}
</script>