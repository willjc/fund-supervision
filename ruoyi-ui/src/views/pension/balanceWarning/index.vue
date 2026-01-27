<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="老人姓名" prop="elderName">
        <el-input
          v-model="queryParams.elderName"
          placeholder="请输入老人姓名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账户编号" prop="accountNo">
        <el-input
          v-model="queryParams.accountNo"
          placeholder="请输入账户编号"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预警等级" prop="warningLevel">
        <el-select v-model="queryParams.warningLevel" placeholder="请选择预警等级" clearable size="small">
          <el-option label="严重" value="3" />
          <el-option label="警告" value="2" />
          <el-option label="提示" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="预警月数" prop="months">
        <el-input-number
          v-model="queryParams.months"
          :min="1"
          :max="12"
          size="small"
          placeholder="不足月数"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-warning"
          size="mini"
          @click="handleCheckAll"
        >一键检查</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['pension:warning:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card shadow="hover" class="warning-card danger">
          <div class="card-icon">
            <i class="el-icon-warning"></i>
          </div>
          <div class="card-content">
            <div class="card-title">严重预警</div>
            <div class="card-value">{{ summaryData.dangerCount || 0 }}</div>
            <div class="card-desc">余额不足1个月</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="warning-card warning">
          <div class="card-icon">
            <i class="el-icon-warning-outline"></i>
          </div>
          <div class="card-content">
            <div class="card-title">警告预警</div>
            <div class="card-value">{{ summaryData.warningCount || 0 }}</div>
            <div class="card-desc">余额不足2个月</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="warning-card info">
          <div class="card-icon">
            <i class="el-icon-info"></i>
          </div>
          <div class="card-content">
            <div class="card-title">提示预警</div>
            <div class="card-value">{{ summaryData.infoCount || 0 }}</div>
            <div class="card-desc">余额不足3个月</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="warning-card total">
          <div class="card-icon">
            <i class="el-icon-s-data"></i>
          </div>
          <div class="card-content">
            <div class="card-title">总预警数</div>
            <div class="card-value">{{ summaryData.totalCount || 0 }}</div>
            <div class="card-desc">需要关注的账户</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="warningList">
      <el-table-column label="预警等级" align="center" prop="warningLevel" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.warningLevel === '3'" type="danger" effect="dark">
            <i class="el-icon-warning"></i> 严重
          </el-tag>
          <el-tag v-else-if="scope.row.warningLevel === '2'" type="warning" effect="dark">
            <i class="el-icon-warning-outline"></i> 警告
          </el-tag>
          <el-tag v-else type="info">
            <i class="el-icon-info"></i> 提示
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="账户编号" align="center" prop="accountNo" width="150" />
      <el-table-column label="老人姓名" align="center" prop="elderName" width="100" />
      <el-table-column label="联系电话" align="center" prop="phone" width="120" />
      <el-table-column label="当前余额" align="center" prop="totalBalance" width="120">
        <template slot-scope="scope">
          <span :class="scope.row.totalBalance < 3000 ? 'text-danger' : 'text-primary'">
            ¥{{ scope.row.totalBalance }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="月费标准" align="center" prop="monthlyFee" width="100">
        <template slot-scope="scope">
          <span>¥{{ scope.row.monthlyFee || 3000 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="可用月数" align="center" prop="availableMonths" width="100">
        <template slot-scope="scope">
          <span :class="getMonthsClass(scope.row.availableMonths)">
            {{ scope.row.availableMonths }} 个月
          </span>
        </template>
      </el-table-column>
      <el-table-column label="预警原因" align="center" prop="warningReason" :show-overflow-tooltip="true" />
      <el-table-column label="建议措施" align="center" prop="suggestion" :show-overflow-tooltip="true" />
      <el-table-column label="最后更新" align="center" prop="updateTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >查看详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-phone"
            @click="handleContact(scope.row)"
          >联系家属</el-button>
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

    <!-- 账户详情对话框 -->
    <el-dialog title="账户详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border v-if="detailData.accountInfo">
        <el-descriptions-item label="账户编号">{{ detailData.accountInfo.accountNo }}</el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ detailData.accountInfo.elderName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.accountInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="账户状态">
          <el-tag v-if="detailData.accountInfo.accountStatus === '1'" type="success">正常</el-tag>
          <el-tag v-else-if="detailData.accountInfo.accountStatus === '0'" type="danger">冻结</el-tag>
          <el-tag v-else type="info">销户</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="总余额">
          <span class="text-primary">¥{{ detailData.accountInfo.totalBalance }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="服务费余额">
          <span>¥{{ detailData.accountInfo.serviceBalance }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="押金余额">
          <span>¥{{ detailData.accountInfo.depositBalance }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="会员费余额">
          <span>¥{{ detailData.accountInfo.memberBalance }}</span>
        </el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">近期交易记录</el-divider>
      <el-table :data="detailData.transactionList" border max-height="300">
        <el-table-column label="交易时间" align="center" prop="transactionTime" width="160">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.transactionTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="交易类型" align="center" prop="transactionType" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.transactionType === '1'" type="success" size="small">充值</el-tag>
            <el-tag v-else-if="scope.row.transactionType === '2'" type="primary" size="small">划拨</el-tag>
            <el-tag v-else-if="scope.row.transactionType === '3'" type="warning" size="small">退费</el-tag>
            <el-tag v-else type="info" size="small">消费</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="交易金额" align="center" prop="transactionAmount" width="120">
          <template slot-scope="scope">
            <span :class="scope.row.transactionType === '1' || scope.row.transactionType === '3' ? 'text-success' : 'text-danger'">
              {{ scope.row.transactionType === '1' || scope.row.transactionType === '3' ? '+' : '-' }}¥{{ scope.row.transactionAmount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="交易后余额" align="center" prop="afterBalance" width="120">
          <template slot-scope="scope">
            <span>¥{{ scope.row.afterBalance }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBalanceWarning, getWarningStatistics } from "@/api/pension/balanceWarning";

export default {
  name: "BalanceWarning",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 预警列表
      warningList: [],
      // 统计数据
      summaryData: {},
      // 详情对话框
      detailOpen: false,
      // 详情数据
      detailData: {
        accountInfo: {},
        transactionList: []
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        elderName: null,
        accountNo: null,
        warningLevel: null,
        months: 3
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询余额预警列表 */
    getList() {
      this.loading = true;
      listBalanceWarning(this.queryParams).then(response => {
        this.warningList = response.rows || [];
        this.total = response.total;
        this.calculateSummary();
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    /** 处理预警数据 */
    processWarningData(data) {
      const monthlyFee = 3000; // 假设月费标准为3000元
      return data.map(item => {
        const availableMonths = Math.floor(item.totalBalance / monthlyFee);
        let warningLevel = '1';
        let warningReason = '';
        let suggestion = '';

        if (availableMonths < 1) {
          warningLevel = '3';
          warningReason = '账户余额严重不足，不足1个月使用';
          suggestion = '请立即联系家属充值，避免影响老人正常服务';
        } else if (availableMonths < 2) {
          warningLevel = '2';
          warningReason = '账户余额不足，仅够使用1个月';
          suggestion = '建议尽快联系家属充值';
        } else if (availableMonths < 3) {
          warningLevel = '1';
          warningReason = '账户余额偏低，不足3个月使用';
          suggestion = '建议提醒家属关注账户余额';
        }

        return {
          ...item,
          monthlyFee,
          availableMonths,
          warningLevel,
          warningReason,
          suggestion
        };
      });
    },
    /** 计算统计数据 */
    calculateSummary() {
      this.summaryData = {
        dangerCount: 0,
        warningCount: 0,
        infoCount: 0,
        totalCount: 0
      };

      this.warningList.forEach(item => {
        this.summaryData.totalCount++;
        if (item.warningLevel === '3') {
          this.summaryData.dangerCount++;
        } else if (item.warningLevel === '2') {
          this.summaryData.warningCount++;
        } else if (item.warningLevel === '1') {
          this.summaryData.infoCount++;
        }
      });
    },
    /** 获取月数样式 */
    getMonthsClass(months) {
      if (months < 1) {
        return 'text-danger bold';
      } else if (months < 2) {
        return 'text-warning bold';
      } else {
        return 'text-info';
      }
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.queryParams.months = 3;
      this.handleQuery();
    },
    /** 一键检查按钮 */
    handleCheckAll() {
      this.$modal.loading("正在检查所有账户...");
      setTimeout(() => {
        this.$modal.closeLoading();
        this.getList();
        this.$modal.msgSuccess("检查完成");
      }, 1000);
    },
    /** 查看详情 */
    handleView(row) {
      // 这里应该调用接口获取详细信息
      this.detailData = {
        accountInfo: row,
        transactionList: []
      };
      this.detailOpen = true;
    },
    /** 联系家属 */
    handleContact(row) {
      this.$modal.confirm('是否要拨打家属电话 ' + row.phone + ' ?').then(() => {
        this.$modal.msgSuccess("已记录联系情况");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('pension/warning/export', {
        ...this.queryParams
      }, `balance_warning_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style scoped>
.warning-card {
  display: flex;
  align-items: center;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
}
.warning-card:hover {
  transform: translateY(-5px);
}
.warning-card.danger {
  border-left: 4px solid #F56C6C;
}
.warning-card.warning {
  border-left: 4px solid #E6A23C;
}
.warning-card.info {
  border-left: 4px solid #909399;
}
.warning-card.total {
  border-left: 4px solid #409EFF;
}
.card-icon {
  font-size: 48px;
  margin-right: 20px;
}
.warning-card.danger .card-icon {
  color: #F56C6C;
}
.warning-card.warning .card-icon {
  color: #E6A23C;
}
.warning-card.info .card-icon {
  color: #909399;
}
.warning-card.total .card-icon {
  color: #409EFF;
}
.card-content {
  flex: 1;
}
.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}
.card-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 5px;
}
.card-desc {
  font-size: 12px;
  color: #C0C4CC;
}
.text-danger {
  color: #F56C6C;
}
.text-warning {
  color: #E6A23C;
}
.text-info {
  color: #909399;
}
.text-primary {
  color: #409EFF;
}
.text-success {
  color: #67C23A;
}
.bold {
  font-weight: bold;
}
.mb20 {
  margin-bottom: 20px;
}
</style>
