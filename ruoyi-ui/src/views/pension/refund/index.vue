<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>退款管理</span>
      </div>

      <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="机构名称" prop="institutionName">
          <el-input v-model="queryParams.institutionName" placeholder="请输入机构名称" clearable />
        </el-form-item>
        <el-form-item label="老人姓名" prop="elderName">
          <el-input v-model="queryParams.elderName" placeholder="请输入老人姓名" clearable />
        </el-form-item>
        <el-form-item label="退款状态" prop="refundStatus">
          <el-select v-model="queryParams.refundStatus" placeholder="请选择" clearable>
            <el-option label="待审核" value="0"></el-option>
            <el-option label="已通过" value="1"></el-option>
            <el-option label="已拒绝" value="2"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="dataList" border>
        <el-table-column label="退款单号" prop="refundNo" width="190" />
        <el-table-column label="养老机构" prop="institutionName" show-overflow-tooltip />
        <el-table-column label="退款老人" prop="elderName" width="100" />
        <el-table-column label="退款原因" prop="refundReason" show-overflow-tooltip width="120" />
        <el-table-column label="退款金额" prop="refundAmount" width="100">
          <template slot-scope="scope">
            <span style="color: #ee0a24; font-weight: 500;">¥{{ formatAmount(scope.row.refundAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="明细金额" width="180">
          <template slot-scope="scope">
            <div class="detail-amounts">
              <span v-if="scope.row.serviceRefundAmount">服务费: ¥{{ formatAmount(scope.row.serviceRefundAmount) }}</span>
              <span v-if="scope.row.depositRefundAmount">押金: ¥{{ formatAmount(scope.row.depositRefundAmount) }}</span>
              <span v-if="scope.row.memberRefundAmount">会员费: ¥{{ formatAmount(scope.row.memberRefundAmount) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" prop="createTime" width="160" />
        <el-table-column label="状态" prop="refundStatus" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.refundStatus)" size="small">{{ getStatusText(scope.row.refundStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="handleView(scope.row)">查看</el-button>
            <el-button type="text" size="small" @click="handleApprove(scope.row)" v-if="scope.row.refundStatus === '0'">审核</el-button>
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
    <el-dialog title="退款详情" :visible.sync="detailVisible" width="800px" append-to-body>
      <el-descriptions :column="2" border v-if="detailData.refundId">
        <el-descriptions-item label="退款单号">{{ detailData.refundNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.refundStatus)">{{ getStatusText(detailData.refundStatus) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="养老机构" :span="2">{{ detailData.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="退款老人">{{ detailData.elderName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款原因">{{ detailData.refundReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款说明" :span="2">{{ detailData.refundDesc || '暂无说明' }}</el-descriptions-item>

        <!-- 退款金额明细 -->
        <el-descriptions-item label="服务费退款">¥{{ formatAmount(detailData.serviceRefundAmount) }}</el-descriptions-item>
        <el-descriptions-item label="押金退款">¥{{ formatAmount(detailData.depositRefundAmount) }}</el-descriptions-item>
        <el-descriptions-item label="会员费退款">¥{{ formatAmount(detailData.memberRefundAmount) }}</el-descriptions-item>
        <el-descriptions-item label="退款总额">
          <span style="color: #ee0a24; font-weight: bold; font-size: 16px;">¥{{ formatAmount(detailData.refundAmount) }}</span>
        </el-descriptions-item>

        <!-- 上传凭证 -->
        <el-descriptions-item label="上传凭证" :span="2" v-if="detailData.evidenceImages && detailData.evidenceImages.length > 0">
          <div class="image-preview-list">
            <el-image
              v-for="(img, idx) in detailData.evidenceImages"
              :key="idx"
              :src="img"
              :preview-src-list="detailData.evidenceImages"
              fit="cover"
              style="width: 100px; height: 100px; margin-right: 10px; border-radius: 4px;"
            />
          </div>
        </el-descriptions-item>

        <el-descriptions-item label="申请时间">{{ detailData.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核人">{{ detailData.approver || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="审核时间" :span="2">{{ detailData.approveTime || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2">{{ detailData.approveRemark || '暂无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleApprove(detailData)" v-if="detailData.refundStatus === '0'">审核</el-button>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog title="审核退款" :visible.sync="approveVisible" width="600px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" label-width="120px">
        <el-form-item label="退款单号">
          <el-input v-model="approveForm.refundNo" disabled />
        </el-form-item>
        <el-form-item label="养老机构">
          <el-input v-model="approveForm.institutionName" disabled />
        </el-form-item>
        <el-form-item label="退款老人">
          <el-input v-model="approveForm.elderName" disabled />
        </el-form-item>
        <el-form-item label="退款原因">
          <el-input v-model="approveForm.refundReason" disabled />
        </el-form-item>

        <!-- 金额明细 -->
        <el-divider content-position="left">退款金额明细</el-divider>
        <el-form-item label="服务费退款">
          <span class="amount-text">¥{{ formatAmount(approveForm.serviceRefundAmount) }}</span>
        </el-form-item>
        <el-form-item label="押金退款">
          <span class="amount-text">¥{{ formatAmount(approveForm.depositRefundAmount) }}</span>
        </el-form-item>
        <el-form-item label="会员费退款">
          <span class="amount-text">¥{{ formatAmount(approveForm.memberRefundAmount) }}</span>
        </el-form-item>
        <el-form-item label="退款总额">
          <span class="amount-total">¥{{ formatAmount(approveForm.refundAmount) }}</span>
        </el-form-item>

        <!-- 退款说明 -->
        <el-divider content-position="left">申请说明</el-divider>
        <el-form-item label="退款说明">
          <div class="desc-text">{{ approveForm.refundDesc || '暂无说明' }}</div>
        </el-form-item>

        <!-- 上传凭证 -->
        <el-form-item label="上传凭证" v-if="approveForm.evidenceImages && approveForm.evidenceImages.length > 0">
          <div class="image-preview-list">
            <el-image
              v-for="(img, idx) in approveForm.evidenceImages"
              :key="idx"
              :src="img"
              :preview-src-list="approveForm.evidenceImages"
              fit="cover"
              style="width: 80px; height: 80px; margin-right: 10px; border-radius: 4px;"
            />
          </div>
        </el-form-item>

        <!-- 审核操作 -->
        <el-divider content-position="left">审核操作</el-divider>
        <el-form-item label="审核结果">
          <el-radio-group v-model="approveForm.approveResult">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" v-if="approveForm.approveResult === '2'">
          <el-input
            type="textarea"
            v-model="approveForm.approveRemark"
            :rows="3"
            placeholder="请输入拒绝原因"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="approveVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitApprove" :loading="submitting">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listApproval, getRefund, approveRefund, rejectRefund } from '@/api/pension/supervisionRefund'

export default {
  name: 'RefundManagement',
  data() {
    return {
      loading: false,
      submitting: false,
      dataList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        elderName: null,
        refundStatus: null
      },
      detailVisible: false,
      detailData: {},
      approveVisible: false,
      approveForm: {
        refundId: null,
        refundNo: '',
        institutionName: '',
        elderName: '',
        refundReason: '',
        refundDesc: '',
        serviceRefundAmount: 0,
        depositRefundAmount: 0,
        memberRefundAmount: 0,
        refundAmount: 0,
        evidenceImages: [],
        approveResult: '1',
        approveRemark: ''
      }
    }
  },
  created() {
    // 从路由参数获取初始状态
    if (this.$route.query.status) {
      this.queryParams.refundStatus = this.$route.query.status
    }
    this.getList()
  },
  methods: {
    // 查询列表
    getList() {
      this.loading = true
      listApproval(this.queryParams).then(response => {
        this.dataList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    // 搜索
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    // 重置
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.handleQuery()
    },
    // 查看详情
    handleView(row) {
      getRefund(row.refundId).then(response => {
        this.detailData = response.data
        // 处理凭证图片
        if (this.detailData.evidenceImages) {
          try {
            this.detailData.evidenceImages = JSON.parse(this.detailData.evidenceImages)
          } catch (e) {
            this.detailData.evidenceImages = []
          }
        } else {
          this.detailData.evidenceImages = []
        }
        this.detailVisible = true
      })
    },
    // 审核退款
    handleApprove(row) {
      getRefund(row.refundId).then(response => {
        const data = response.data
        this.approveForm = {
          refundId: data.refundId,
          refundNo: data.refundNo,
          institutionName: data.institutionName,
          elderName: data.elderName,
          refundReason: data.refundReason,
          refundDesc: data.refundDesc,
          serviceRefundAmount: data.serviceRefundAmount || 0,
          depositRefundAmount: data.depositRefundAmount || 0,
          memberRefundAmount: data.memberRefundAmount || 0,
          refundAmount: data.refundAmount || 0,
          evidenceImages: [],
          approveResult: '1',
          approveRemark: ''
        }
        // 处理凭证图片
        if (data.evidenceImages) {
          try {
            this.approveForm.evidenceImages = JSON.parse(data.evidenceImages)
          } catch (e) {
            this.approveForm.evidenceImages = []
          }
        }
        this.approveVisible = true
      })
    },
    // 提交审核
    submitApprove() {
      const data = {
        approveRemark: this.approveForm.approveRemark
      }

      this.submitting = true

      const request = this.approveForm.approveResult === '1'
        ? approveRefund(this.approveForm.refundId)
        : rejectRefund(this.approveForm.refundId, data)

      request.then(response => {
        this.submitting = false
        this.approveVisible = false
        this.$message.success('审核成功')
        this.getList()
      }).catch(() => {
        this.submitting = false
      })
    },
    // 格式化金额
    formatAmount(amount) {
      if (!amount && amount !== 0) return '0.00'
      return parseFloat(amount).toFixed(2)
    },
    // 获取状态文本
    getStatusText(status) {
      const map = {
        '0': '待审核',
        '1': '已通过',
        '2': '已拒绝'
      }
      return map[status] || '未知'
    },
    // 获取状态类型
    getStatusType(status) {
      const map = {
        '0': 'warning',
        '1': 'success',
        '2': 'danger'
      }
      return map[status] || 'info'
    }
  }
}
</script>

<style scoped lang="scss">
.detail-amounts {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #666;

  span {
    white-space: nowrap;
  }
}

.image-preview-list {
  display: flex;
  flex-wrap: wrap;
}

.amount-text {
  font-size: 14px;
  color: #333;
}

.amount-total {
  font-size: 18px;
  font-weight: bold;
  color: #ee0a24;
}

.desc-text {
  white-space: pre-wrap;
  color: #666;
  line-height: 1.5;
}
</style>
