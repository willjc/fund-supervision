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
      <el-form-item label="审核状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择审核状态" clearable>
          <el-option
            v-for="dict in dict.type.review_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="评价用户" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入评价用户"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评价时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="multiple"
          @click="handleApprove"
          v-hasPermi="['supervision:review:approve']"
        >批量通过</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-close"
          size="mini"
          :disabled="multiple"
          @click="handleReject"
          v-hasPermi="['supervision:review:reject']"
        >批量拒绝</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['supervision:review:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['supervision:review:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb8">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.totalCount || 0 }}</div>
            <div class="stat-label">总评价数</div>
          </div>
          <i class="el-icon-document stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card pending">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.pendingCount || 0 }}</div>
            <div class="stat-label">待审核</div>
          </div>
          <i class="el-icon-time stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card approved">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.approvedCount || 0 }}</div>
            <div class="stat-label">已通过</div>
          </div>
          <i class="el-icon-circle-check stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card rejected">
          <div class="stat-content">
            <div class="stat-number">{{ statistics.rejectedCount || 0 }}</div>
            <div class="stat-label">已拒绝</div>
          </div>
          <i class="el-icon-circle-close stat-icon"></i>
        </el-card>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="reviewList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="评价ID" align="center" prop="reviewId" />
      <el-table-column label="机构名称" align="center" prop="institutionName" />
      <el-table-column label="评价用户" align="center" prop="userName" />
      <el-table-column label="老人姓名" align="center" prop="elderName" />
      <el-table-column label="订单号" align="center" prop="orderNo" />
      <el-table-column label="评分" align="center" width="180">
        <template slot-scope="scope">
          <el-rate
            v-model="scope.row.averageRating"
            disabled
            show-score
            text-color="#ff9900"
            score-template="{value}"
          >
          </el-rate>
        </template>
      </el-table-column>
      <el-table-column label="评价内容" align="center" prop="content" :show-overflow-tooltip="true" />
      <el-table-column label="审核状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.review_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="评价时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核人" align="center" prop="reviewBy" />
      <el-table-column label="审核时间" align="center" prop="reviewTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.reviewTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['supervision:review:query']"
          >详情</el-button>
          <el-button
            v-if="scope.row.status === '0'"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleSingleApprove(scope.row)"
            v-hasPermi="['supervision:review:approve']"
          >通过</el-button>
          <el-button
            v-if="scope.row.status === '0'"
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleSingleReject(scope.row)"
            v-hasPermi="['supervision:review:reject']"
          >拒绝</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['supervision:review:remove']"
          >删除</el-button>
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

    <!-- 评价详情对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="机构名称">
              <el-input v-model="form.institutionName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评价用户">
              <el-input v-model="form.userName" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="老人姓名">
              <el-input v-model="form.elderName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单号">
              <el-input v-model="form.orderNo" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="综合评分">
              <el-rate
                v-model="form.averageRating"
                disabled
                show-score
                text-color="#ff9900"
                score-template="{value}分"
              >
              </el-rate>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="评价内容">
          <el-input v-model="form.content" type="textarea" :rows="3" disabled />
        </el-form-item>
        <el-form-item label="评价图片" v-if="form.images && JSON.parse(form.images || '[]').length > 0">
          <el-image
            v-for="(image, index) in JSON.parse(form.images || '[]')"
            :key="index"
            style="width: 100px; height: 100px; margin-right: 10px;"
            :src="image.url"
            :preview-src-list="[image.url]"
            fit="cover"
          />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-radio-group v-model="form.status" disabled>
            <el-radio :label="0">待审核</el-radio>
            <el-radio :label="1">已通过</el-radio>
            <el-radio :label="2">已拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" v-if="form.reviewRemark">
          <el-input v-model="form.reviewRemark" type="textarea" :rows="2" disabled />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="审核人">
              <el-input v-model="form.reviewBy" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审核时间">
              <el-input v-model="form.reviewTime" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog :title="reviewTitle" :visible.sync="reviewOpen" width="500px" append-to-body>
      <el-form ref="reviewForm" :model="reviewForm" :rules="reviewRules" label-width="80px">
        <el-form-item label="审核操作">
          <el-radio-group v-model="reviewForm.action">
            <el-radio :label="1">通过</el-radio>
            <el-radio :label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="remark">
          <el-input
            v-model="reviewForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见，拒绝审核时必填"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReview">确 定</el-button>
        <el-button @click="cancelReview">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listReview, getReview, approveReview, rejectReview, batchApprove, delReview, getReviewStatistics } from "@/api/supervision/review";

export default {
  name: "Review",
  dicts: ['review_status'],
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
      // 机构评价审核表格数据
      reviewList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 审核对话框标题
      reviewTitle: "",
      // 是否显示审核对话框
      reviewOpen: false,
      // 日期范围
      dateRange: [],
      // 统计数据
      statistics: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        status: null,
        userName: null,
      },
      // 表单参数
      form: {},
      // 审核表单参数
      reviewForm: {
        action: 1,
        remark: ''
      },
      // 表单校验
      rules: {
      },
      // 审核表单校验
      reviewRules: {
        remark: [
          { required: true, message: "审核意见不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getStatistics();
  },
  methods: {
    /** 查询机构评价审核列表 */
    getList() {
      this.loading = true;
      listReview(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.reviewList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 获取统计信息 */
    getStatistics() {
      getReviewStatistics().then(response => {
        if (response.code === 200 && response.data) {
          this.statistics = response.data;
        }
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        reviewId: null,
        institutionName: null,
        userName: null,
        elderName: null,
        orderNo: null,
        averageRating: null,
        content: null,
        images: null,
        status: null,
        reviewRemark: null,
        reviewBy: null,
        reviewTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.reviewId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.reset();
      const reviewId = row.reviewId || this.ids
      getReview(reviewId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "评价详情";
      });
    },
    /** 单个审核通过 */
    handleSingleApprove(row) {
      this.reviewForm.action = 1;
      this.reviewForm.remark = '';
      this.reviewOpen = true;
      this.reviewTitle = "审核通过";
      this.currentReviewId = row.reviewId;
    },
    /** 单个审核拒绝 */
    handleSingleReject(row) {
      this.reviewForm.action = 2;
      this.reviewForm.remark = '';
      this.reviewOpen = true;
      this.reviewTitle = "审核拒绝";
      this.currentReviewId = row.reviewId;
    },
    /** 批量审核通过 */
    handleApprove() {
      const reviewIds = this.ids;
      this.$modal.confirm('是否确认批量通过选中的评价？').then(() => {
        return batchApprove(reviewIds);
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("批量审核通过成功");
      }).catch(() => {});
    },
    /** 批量审核拒绝 */
    handleReject() {
      this.reviewForm.action = 2;
      this.reviewForm.remark = '';
      this.reviewOpen = true;
      this.reviewTitle = "批量审核拒绝";
    },
    /** 提交审核 */
    submitReview() {
      this.$refs["reviewForm"].validate(valid => {
        if (valid) {
          if (this.reviewForm.action === 2 && !this.reviewForm.remark.trim()) {
            this.$modal.msgError("拒绝审核时必须填写审核意见");
            return;
          }

          if (this.ids.length > 0) {
            // 批量审核
            this.$modal.confirm(`是否确认批量${this.reviewForm.action === 1 ? '通过' : '拒绝'}选中的评价？`).then(() => {
              if (this.reviewForm.action === 1) {
                return batchApprove(this.ids);
              } else {
                // 批量拒绝需要逐个处理
                return Promise.all(this.ids.map(id =>
                  rejectReview(id, { reviewRemark: this.reviewForm.remark })
                ));
              }
            }).then(() => {
              this.getList();
              this.getStatistics();
              this.$modal.msgSuccess(`批量${this.reviewForm.action === 1 ? '通过' : '拒绝'}成功`);
              this.reviewOpen = false;
            }).catch(() => {});
          } else {
            // 单个审核
            if (this.reviewForm.action === 1) {
              approveReview(this.currentReviewId, { reviewRemark: this.reviewForm.remark }).then(response => {
                this.$modal.msgSuccess("审核通过成功");
                this.getList();
                this.getStatistics();
                this.reviewOpen = false;
              });
            } else {
              rejectReview(this.currentReviewId, { reviewRemark: this.reviewForm.remark }).then(response => {
                this.$modal.msgSuccess("审核拒绝成功");
                this.getList();
                this.getStatistics();
                this.reviewOpen = false;
              });
            }
          }
        }
      });
    },
    // 取消审核
    cancelReview() {
      this.reviewOpen = false;
      this.reviewForm = {
        action: 1,
        remark: ''
      };
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const reviewIds = row.reviewId || this.ids;
      this.$modal.confirm('是否确认删除机构评价审核编号为"' + reviewIds + '"的数据项？').then(() => {
        return delReview(reviewIds);
      }).then(() => {
        this.getList();
        this.getStatistics();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('supervision/review/export', {
        ...this.queryParams
      }, `机构评价审核_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style scoped>
.stat-card {
  border: none;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-card.pending {
  border-left: 4px solid #e6a23c;
}

.stat-card.approved {
  border-left: 4px solid #67c23a;
}

.stat-card.rejected {
  border-left: 4px solid #f56c6c;
}

.stat-content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 80px;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.stat-icon {
  font-size: 48px;
  color: #c0c4cc;
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}
</style>