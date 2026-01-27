<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>预约参观管理</span>
      </div>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="待参观" value="0"></el-option>
            <el-option label="已完成" value="1"></el-option>
            <el-option label="已取消" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="预约编号">
          <el-input v-model="queryParams.reservationNo" placeholder="请输入预约编号" clearable></el-input>
        </el-form-item>
        <el-form-item label="参观人">
          <el-input v-model="queryParams.visitorName" placeholder="请输入参观人姓名" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="warning"
            icon="el-icon-download"
            @click="handleExport"
            v-hasPermi="['pension:visit:export']"
          >导出</el-button>
        </el-col>
      </el-row>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="dataList" border>
        <el-table-column label="预约编号" prop="reservationNo" width="180"></el-table-column>
        <el-table-column label="机构名称" prop="institutionName" show-overflow-tooltip></el-table-column>
        <el-table-column label="参观人" prop="visitorName" width="100"></el-table-column>
        <el-table-column label="联系电话" prop="visitorPhone" width="120"></el-table-column>
        <el-table-column label="参观日期" prop="visitDate" width="120">
          <template slot-scope="scope">
            {{ parseTime(scope.row.visitDate, '{y}-{m}-{d}') }}
          </template>
        </el-table-column>
        <el-table-column label="参观时间" prop="visitTime" width="100"></el-table-column>
        <el-table-column label="参观人数" prop="visitorCount" width="80"></el-table-column>
        <el-table-column label="状态" prop="status" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180">
          <template slot-scope="scope">
            {{ parseTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              icon="el-icon-view"
              @click="handleView(scope.row)"
              v-hasPermi="['pension:visit:query']"
            >查看</el-button>
            <el-button
              v-if="scope.row.status === '0'"
              type="text"
              size="small"
              icon="el-icon-check"
              @click="handleComplete(scope.row)"
              v-hasPermi="['pension:visit:complete']"
            >标记完成</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog title="预约详情" :visible.sync="detailVisible" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预约编号" :span="2">{{ detailData.reservationNo }}</el-descriptions-item>
        <el-descriptions-item label="机构名称" :span="2">{{ detailData.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="参观人姓名">{{ detailData.visitorName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.visitorPhone }}</el-descriptions-item>
        <el-descriptions-item label="预计到访日期">{{ parseTime(detailData.visitDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="预计到访时间">{{ detailData.visitTime }}</el-descriptions-item>
        <el-descriptions-item label="参观人数">{{ detailData.visitorCount }}人</el-descriptions-item>
        <el-descriptions-item label="当前状态">
          <el-tag :type="getStatusType(detailData.status)">
            {{ getStatusText(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注说明" :span="2">{{ detailData.remark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="预约用户">{{ detailData.userName }}</el-descriptions-item>
        <el-descriptions-item label="处理人" v-if="detailData.handleUser">{{ detailData.handleUser }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" v-if="detailData.handleTime">
          {{ parseTime(detailData.handleTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="处理备注" :span="2" v-if="detailData.handleRemark">
          {{ detailData.handleRemark }}
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <!-- 标记完成对话框 -->
    <el-dialog title="标记预约完成" :visible.sync="completeVisible" width="500px" append-to-body>
      <el-form ref="completeForm" :model="completeForm" label-width="100px">
        <el-form-item label="预约编号">
          <span>{{ completeForm.reservationNo }}</span>
        </el-form-item>
        <el-form-item label="参观人">
          <span>{{ completeForm.visitorName }}</span>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input
            type="textarea"
            v-model="completeForm.handleRemark"
            :rows="4"
            placeholder="请输入处理备注（选填）"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="completeVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitComplete" :loading="submitting">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listVisitReservation, getVisitReservation, completeReservation, exportVisitReservation } from "@/api/pension/visit";

export default {
  name: 'VisitReservationManagement',
  data() {
    return {
      loading: false,
      submitting: false,
      dataList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        status: null,
        reservationNo: null,
        visitorName: null
      },
      detailVisible: false,
      detailData: {},
      completeVisible: false,
      completeForm: {
        reservationId: null,
        reservationNo: '',
        visitorName: '',
        handleRemark: ''
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询预约列表 */
    getList() {
      this.loading = true;
      listVisitReservation(this.queryParams).then(response => {
        this.dataList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 查看详情 */
    handleView(row) {
      const reservationId = row.reservationId;
      getVisitReservation(reservationId).then(response => {
        this.detailData = response.data;
        this.detailVisible = true;
      });
    },
    /** 标记完成 */
    handleComplete(row) {
      this.completeForm = {
        reservationId: row.reservationId,
        reservationNo: row.reservationNo,
        visitorName: row.visitorName,
        handleRemark: ''
      };
      this.completeVisible = true;
    },
    /** 提交标记完成 */
    submitComplete() {
      this.$refs.completeForm.validate(valid => {
        if (valid) {
          this.submitting = true;
          completeReservation(this.completeForm).then(response => {
            this.$modal.msgSuccess("标记成功");
            this.completeVisible = false;
            this.getList();
            this.submitting = false;
          }).catch(() => {
            this.submitting = false;
          });
        }
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有预约数据?').then(() => {
        this.loading = true;
        return exportVisitReservation(this.queryParams);
      }).then(response => {
        this.$modal.msgSuccess("导出成功");
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    /** 获取状态标签类型 */
    getStatusType(status) {
      switch (status) {
        case '0':
          return 'warning'; // 待参观 - 黄色
        case '1':
          return 'success'; // 已完成 - 绿色
        case '2':
          return 'info';    // 已取消 - 灰色
        default:
          return '';
      }
    },
    /** 获取状态文本 */
    getStatusText(status) {
      switch (status) {
        case '0':
          return '待参观';
        case '1':
          return '已完成';
        case '2':
          return '已取消';
        default:
          return '未知';
      }
    }
  }
};
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}
</style>
