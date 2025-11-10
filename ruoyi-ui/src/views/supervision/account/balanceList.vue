<template>
  <div class="app-container">
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
      <el-table-column label="账户类型" align="center" prop="accountType" />
      <el-table-column label="当前余额" align="center" prop="currentBalance" />
      <el-table-column label="可用余额" align="center" prop="availableBalance" />
      <el-table-column label="账户状态" align="center" prop="accountStatus" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-money" @click="handleRecharge(scope.row)">充值</el-button>
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
      this.balanceList = [
        { accountNo: 'ACC20230001', elderName: '张奶奶', accountType: '基本账户', currentBalance: 5000, availableBalance: 4500, accountStatus: '正常' },
        { accountNo: 'ACC20230002', elderName: '李爷爷', accountType: '专项账户', currentBalance: 800, availableBalance: 800, accountStatus: '正常' }
      ]
      this.total = this.balanceList.length
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
    handleRecharge(row) {
      console.log('充值', row)
    }
  }
}
</script>