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
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="blacklistList">
      <el-table-column label="机构ID" align="center" prop="institutionId" />
      <el-table-column label="机构名称" align="center" prop="institutionName" />
      <el-table-column label="原因描述" align="center" prop="reason" show-overflow-tooltip min-width="200" />
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

    <!-- 详情对话框 -->
    <el-dialog title="黑名单详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="机构ID">{{ detailForm.institutionId }}</el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ detailForm.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailForm.status === '1' ? 'danger' : 'success'">
            {{ detailForm.status === '1' ? '生效中' : '已解除' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="加入时间">{{ detailForm.addTime }}</el-descriptions-item>
        <el-descriptions-item label="移除时间" :span="2">{{ detailForm.removeTime || '未移除' }}</el-descriptions-item>
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
import { listBlacklist, removeFromBlacklist } from '@/api/supervision/institution'

export default {
  name: 'BlacklistList',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 黑名单表格数据
      blacklistList: [],
      // 是否显示详情弹出层
      detailOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        status: null
      },
      // 详情表单
      detailForm: {}
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
        this.blacklistList = response.rows
        this.total = response.total
        this.loading = false
      })
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
    /** 移除黑名单操作 */
    handleRemove(row) {
      this.$modal.confirm(`确认将"${row.institutionName}"从黑名单中移除吗？`).then(() => {
        return removeFromBlacklist(row.id)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('移除成功')
      }).catch(() => {})
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.detailForm = { ...row }
      this.detailOpen = true
    }
  }
}
</script>
