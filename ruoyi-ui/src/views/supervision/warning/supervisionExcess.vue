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
      <el-table-column label="当前监管额" align="center" prop="currentSupervision" />
      <el-table-column label="标准监管额" align="center" prop="standardSupervision" />
      <el-table-column label="超额金额" align="center" prop="excessAmount" />
      <el-table-column label="预警等级" align="center" prop="warningLevel" />
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
  name: 'SupervisionExcess',
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
        { warningId: 'W001', institutionName: '阳光养老院', currentSupervision: 150000, standardSupervision: 100000, excessAmount: 50000, warningLevel: '重要', handleStatus: '待处理' },
        { warningId: 'W002', institutionName: '康复中心', currentSupervision: 120000, standardSupervision: 100000, excessAmount: 20000, warningLevel: '一般', handleStatus: '已处理' }
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