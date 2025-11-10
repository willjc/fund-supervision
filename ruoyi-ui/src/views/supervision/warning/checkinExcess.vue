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
      <el-table-column label="当前入住数" align="center" prop="currentCheckin" />
      <el-table-column label="核定容量" align="center" prop="approvedCapacity" />
      <el-table-column label="超额人数" align="center" prop="excessCount" />
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
  name: 'CheckinExcess',
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
        { warningId: 'W003', institutionName: '康乐养老中心', currentCheckin: 120, approvedCapacity: 100, excessCount: 20, warningLevel: '重要', handleStatus: '待处理' },
        { warningId: 'W004', institutionName: '金色年华', currentCheckin: 85, approvedCapacity: 80, excessCount: 5, warningLevel: '一般', handleStatus: '已处理' }
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