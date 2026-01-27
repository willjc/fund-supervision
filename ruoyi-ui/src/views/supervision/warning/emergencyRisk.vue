<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="warningList">
      <el-table-column label="预警ID" align="center" prop="warningId" />
      <el-table-column label="机构名称" align="center" prop="institutionName" />
      <el-table-column label="风险类型" align="center" prop="riskType" />
      <el-table-column label="风险等级" align="center" prop="riskLevel" />
      <el-table-column label="影响程度" align="center" prop="impactLevel" />
      <el-table-column label="发生时间" align="center" prop="occurrenceTime" />
      <el-table-column label="处理状态" align="center" prop="handleStatus" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)">处理</el-button>
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
  </div>
</template>

<script>
export default {
  name: 'EmergencyRisk',
  data() {
    return {
      loading: true,
      total: 0,
      warningList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      this.warningList = [
        { warningId: 'W007', institutionName: '福星养老院', riskType: '安全事故', riskLevel: '紧急', impactLevel: '严重', occurrenceTime: '2023-12-01 10:30:00', handleStatus: '处理中' },
        { warningId: 'W008', institutionName: '老年之家', riskType: '财务危机', riskLevel: '重要', impactLevel: '轻微', occurrenceTime: '2023-12-02 14:20:00', handleStatus: '已处理' }
      ]
      this.total = this.warningList.length
      this.loading = false
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
      console.log('详情', row)
    },
    handleUpdate(row) {
      console.log('处理', row)
    }
  }
}
</script>