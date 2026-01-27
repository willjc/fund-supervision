<template>
  <el-dialog title="支付统计" :visible.sync="visible" width="600px" append-to-body>
    <div v-loading="loading">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <div slot="header">
              <span>支付总金额</span>
            </div>
            <div class="statistic-value">
              ¥{{ statistics.totalAmount || '0.00' }}
            </div>
            <div class="statistic-label">
              共 {{ statistics.totalCount || 0 }} 笔支付
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <div slot="header">
              <span>平均支付金额</span>
            </div>
            <div class="statistic-value">
              ¥{{ averageAmount || '0.00' }}
            </div>
            <div class="statistic-label">
              单笔平均金额
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <el-card>
            <div slot="header">
              <span>支付方式分布</span>
            </div>
            <div v-if="methodStatistics.length > 0">
              <el-table :data="methodStatistics" style="width: 100%">
                <el-table-column prop="paymentMethod" label="支付方式">
                  <template slot-scope="scope">
                    <dict-tag :options="dict.type.payment_method" :value="scope.row.paymentMethod"/>
                  </template>
                </el-table-column>
                <el-table-column prop="count" label="支付笔数" width="100"/>
                <el-table-column prop="amount" label="支付金额" width="120">
                  <template slot-scope="scope">
                    ¥{{ scope.row.amount }}
                  </template>
                </el-table-column>
                <el-table-column label="占比" width="100">
                  <template slot-scope="scope">
                    {{ getPercentage(scope.row.amount, statistics.totalAmount) }}%
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-else class="no-data">
              暂无数据
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <el-card>
            <div slot="header">
              <span>支付状态分布</span>
            </div>
            <div v-if="statusStatistics.length > 0">
              <el-table :data="statusStatistics" style="width: 100%">
                <el-table-column prop="paymentStatus" label="支付状态">
                  <template slot-scope="scope">
                    <dict-tag :options="dict.type.payment_status" :value="scope.row.paymentStatus"/>
                  </template>
                </el-table-column>
                <el-table-column prop="count" label="笔数" width="100"/>
                <el-table-column label="占比" width="100">
                  <template slot-scope="scope">
                    {{ getPercentage(scope.row.count, statistics.totalCount) }}%
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-else class="no-data">
              暂无数据
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关 闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getPaymentStatistics } from "@/api/order/paymentRecord";

export default {
  name: "PaymentStatistics",
  dicts: ['payment_method', 'payment_status'],
  data() {
    return {
      visible: false,
      loading: false,
      queryParams: {},
      statistics: {
        totalAmount: 0,
        totalCount: 0
      },
      methodStatistics: [],
      statusStatistics: []
    };
  },
  computed: {
    averageAmount() {
      if (this.statistics.totalCount > 0) {
        return (this.statistics.totalAmount / this.statistics.totalCount).toFixed(2);
      }
      return '0.00';
    }
  },
  methods: {
    show(queryParams) {
      this.visible = true;
      this.queryParams = { ...queryParams };
      this.loadStatistics();
    },
    loadStatistics() {
      this.loading = true;
      getPaymentStatistics(this.queryParams).then(response => {
        const data = response.data;
        this.statistics = {
          totalAmount: data.totalAmount || 0,
          totalCount: data.totalCount || 0
        };

        // 这里可以根据实际API返回的数据结构来处理
        // 目前使用模拟数据展示
        this.methodStatistics = this.generateMethodStatistics();
        this.statusStatistics = this.generateStatusStatistics();

        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    generateMethodStatistics() {
      // 模拟数据，实际应该从后端获取
      return [
        { paymentMethod: 'wechat', count: 45, amount: 125000.00 },
        { paymentMethod: 'alipay', count: 32, amount: 89600.00 },
        { paymentMethod: 'bank', count: 18, amount: 45200.00 },
        { paymentMethod: 'cash', count: 5, amount: 12000.00 }
      ];
    },
    generateStatusStatistics() {
      // 模拟数据，实际应该从后端获取
      return [
        { paymentStatus: '2', count: 98 }, // 成功
        { paymentStatus: '1', count: 2 },  // 处理中
        { paymentStatus: '3', count: 0 }   // 失败
      ];
    },
    getPercentage(value, total) {
      if (total > 0) {
        return ((value / total) * 100).toFixed(1);
      }
      return '0.0';
    }
  }
};
</script>

<style scoped>
.statistic-value {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  text-align: center;
  margin-bottom: 8px;
}

.statistic-label {
  font-size: 14px;
  color: #909399;
  text-align: center;
}

.no-data {
  text-align: center;
  color: #909399;
  padding: 20px 0;
}
</style>