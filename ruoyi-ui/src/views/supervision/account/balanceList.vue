<template>
  <div class="app-container">
    <el-alert title="平台账面余额（非银行实时余额）。预占金额尚未正式扣账，不可重复使用。" type="info" :closable="false" show-icon class="mb8" />
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="账户编号" prop="accountNo">
        <el-input
          v-model="queryParams.accountNo"
          placeholder="请输入账户编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="balanceList">
      <el-table-column label="账户编号" align="center" prop="accountNo" />
      <el-table-column label="老人姓名" align="center" prop="elderName" />
      <el-table-column label="机构" align="center" prop="institutionName" />
      <el-table-column label="账面总余额" align="center" prop="totalBalance" />
      <el-table-column label="服务费预占" align="center" prop="serviceReserved" />
      <el-table-column label="服务费可用" align="center" prop="serviceAvailable" />
      <el-table-column label="押金预占" align="center" prop="depositReserved" />
      <el-table-column label="押金可用" align="center" prop="depositAvailable" />
      <el-table-column label="可用余额" align="center" prop="availableBalance" />
      <el-table-column label="账户状态" align="center" prop="accountStatus">
        <template slot-scope="scope">{{ { '0': '冻结', '1': '正常', '2': '已销户' }[scope.row.accountStatus] || '待核实' }}</template>
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
import { listAccountInfo } from '@/api/pension/accountInfo'
export default {
  name: 'BalanceList',
  data() {
    return {
      loading: true,
      total: 0,
      balanceList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        accountNo: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listAccountInfo(this.queryParams).then(response => {
        this.balanceList = response.rows || []
        this.total = response.total || 0
      }).finally(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    }
  }
}
</script>
