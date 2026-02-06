<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评级等级" prop="ratingLevel">
        <el-select v-model="queryParams.ratingLevel" placeholder="请选择评级等级" clearable>
          <el-option label="五星级" value="5" />
          <el-option label="四星级" value="4" />
          <el-option label="三星级" value="3" />
          <el-option label="二星级" value="2" />
          <el-option label="一星级" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="评级状态" prop="ratingStatus">
        <el-select v-model="queryParams.ratingStatus" placeholder="请选择评级状态" clearable>
          <el-option label="有效" value="1" />
          <el-option label="已过期" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增评级</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-star-on"
          size="mini"
          :disabled="multiple"
          @click="handleBatchUpdate"
        >批量更新</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ratingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="机构名称" align="center" prop="institutionName" :show-overflow-tooltip="true" />
      <el-table-column label="统一信用代码" align="center" prop="creditCode" width="180" />
      <el-table-column label="总分" align="center" prop="totalScore" width="80">
        <template slot-scope="scope">
          <span class="text-success font-bold">{{ scope.row.totalScore }}</span>
        </template>
      </el-table-column>
      <el-table-column label="服务质量" align="center" prop="serviceScore" width="80" />
      <el-table-column label="设施环境" align="center" prop="facilityScore" width="80" />
      <el-table-column label="管理水平" align="center" prop="managementScore" width="80" />
      <el-table-column label="安全卫生" align="center" prop="safetyScore" width="80" />
      <el-table-column label="评级状态" align="center" prop="ratingStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.ratingStatus === '1'" type="success">有效</el-tag>
          <el-tag v-else type="danger">已过期</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="评级日期" align="center" prop="ratingDate" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.ratingDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="有效期至" align="center" prop="expireDate" width="120">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expireDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
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

    <!-- 新增/修改评级对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="140px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="机构名称" prop="institutionId">
              <el-select
                v-model="form.institutionId"
                placeholder="请选择机构"
                style="width: 100%"
                filterable
                remote
                :remote-method="searchInstitutions"
                :loading="institutionLoading"
                @change="handleInstitutionChange"
              >
                <el-option
                  v-for="item in institutionOptions"
                  :key="item.institutionId"
                  :label="item.institutionName"
                  :value="item.institutionId"
                >
                  <span style="float: left">{{ item.institutionName }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">{{ item.creditCode }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="统一信用代码" prop="creditCode">
              <el-input v-model="form.creditCode" placeholder="自动载入" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="评级等级" prop="ratingLevel">
              <el-rate
                v-model="form.ratingLevel"
                :max="5"
                show-text
                :texts="['一星', '二星', '三星', '四星', '五星']"
              >
              </el-rate>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评级日期" prop="ratingDate">
              <el-date-picker
                v-model="form.ratingDate"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">评分项目</el-divider>
        <el-row>
          <el-col :span="12">
            <el-form-item label="服务质量 (25分)" prop="serviceScore">
              <el-input-number v-model="form.serviceScore" :min="0" :max="25" :precision="1" style="width: 150px" @change="calculateTotalScore"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设施环境 (25分)" prop="facilityScore">
              <el-input-number v-model="form.facilityScore" :min="0" :max="25" :precision="1" style="width: 150px" @change="calculateTotalScore"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="管理水平 (25分)" prop="managementScore">
              <el-input-number v-model="form.managementScore" :min="0" :max="25" :precision="1" style="width: 150px" @change="calculateTotalScore"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="安全卫生 (25分)" prop="safetyScore">
              <el-input-number v-model="form.safetyScore" :min="0" :max="25" :precision="1" style="width: 150px" @change="calculateTotalScore"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="总分 (100分)" prop="totalScore">
              <el-input v-model="form.totalScore" placeholder="自动计算" readonly>
                <template slot="append">分</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效期(月)" prop="validityPeriod">
              <el-input-number v-model="form.validityPeriod" :min="1" :max="60" style="width: 150px">
                <template slot="append">月</template>
              </el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="评级意见" prop="ratingRemark">
              <el-input v-model="form.ratingRemark" type="textarea" :rows="4" placeholder="请输入评级意见" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="评级详情" :visible.sync="detailOpen" width="80%" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="机构名称">{{ detailData.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="统一信用代码">{{ detailData.creditCode }}</el-descriptions-item>
        <el-descriptions-item label="评级等级">
          <el-rate
            :value="Number(detailData.ratingLevel) || 0"
            :max="5"
            disabled
            show-score
            text-color="#ff9900"
            score-template="{value}星"
          >
          </el-rate>
        </el-descriptions-item>
        <el-descriptions-item label="总分">
          <span class="text-success font-bold">{{ detailData.totalScore }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="评级日期">{{ parseTime(detailData.ratingDate) }}</el-descriptions-item>
        <el-descriptions-item label="有效期至">{{ parseTime(detailData.expireDate) }}</el-descriptions-item>
        <el-descriptions-item label="评级状态">
          <el-tag v-if="detailData.ratingStatus === '1'" type="success">有效</el-tag>
          <el-tag v-else type="danger">已过期</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">详细评分</el-divider>
      <el-row :gutter="20" class="mb20">
        <el-col :span="6" v-for="item in scoreDetails" :key="item.name">
          <el-card class="score-card">
            <div class="score-title">{{ item.name }}</div>
            <div class="score-value">{{ item.score }}/{{ item.maxScore }}</div>
            <el-progress :percentage="(item.score / item.maxScore) * 100" :color="getProgressColor((item.score / item.maxScore) * 100)"></el-progress>
          </el-card>
        </el-col>
      </el-row>

      <el-divider content-position="left">评级意见</el-divider>
      <div class="rating-remark">{{ detailData.ratingRemark }}</div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRating, getRating, delRating, addRating, updateRating, exportRating, listApprovedInstitutions } from '@/api/supervision/institution'

export default {
  name: 'RatingList',
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      ratingList: [],
      title: '',
      open: false,
      detailOpen: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        ratingLevel: null,
        ratingStatus: null
      },
      form: {},
      detailData: {},
      scoreDetails: [],
      institutionOptions: [],
      institutionLoading: false,
      rules: {
        institutionId: [
          { required: true, message: '请选择机构', trigger: 'change' }
        ],
        creditCode: [
          { required: true, message: '统一信用代码不能为空', trigger: 'blur' }
        ],
        ratingLevel: [
          { required: true, message: '评级等级不能为空', trigger: 'change' }
        ],
        serviceScore: [
          { required: true, message: '服务质量评分不能为空', trigger: 'blur' }
        ],
        facilityScore: [
          { required: true, message: '设施环境评分不能为空', trigger: 'blur' }
        ],
        managementScore: [
          { required: true, message: '管理水平评分不能为空', trigger: 'blur' }
        ],
        safetyScore: [
          { required: true, message: '安全卫生评分不能为空', trigger: 'blur' }
        ],
        ratingDate: [
          { required: true, message: '评级日期不能为空', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listRating(this.queryParams).then(response => {
        this.ratingList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        ratingId: null,
        institutionId: null,
        institutionName: null,
        creditCode: null,
        ratingLevel: 3,
        totalScore: 0,
        serviceScore: 0,
        facilityScore: 0,
        managementScore: 0,
        safetyScore: 0,
        ratingDate: null,
        validityPeriod: 12,
        ratingRemark: null
      }
      this.institutionOptions = []
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.ratingId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增机构评级'
      // 自动加载前20个机构供选择
      this.institutionLoading = true
      listApprovedInstitutions({ pageSize: 20 }).then(response => {
        this.institutionOptions = response.rows
        this.institutionLoading = false
      }).catch(() => {
        this.institutionLoading = false
      })
    },
    handleView(row) {
      const ratingId = row.ratingId
      this.detailData = row
      this.scoreDetails = [
        { name: '服务质量', score: row.serviceScore, maxScore: 25 },
        { name: '设施环境', score: row.facilityScore, maxScore: 25 },
        { name: '管理水平', score: row.managementScore, maxScore: 25 },
        { name: '安全卫生', score: row.safetyScore, maxScore: 25 }
      ]
      this.detailOpen = true
    },
    handleUpdate(row) {
      this.reset()
      const ratingId = row.ratingId
      getRating(ratingId).then(response => {
        this.form = response.data
        // 编辑时也需要加载机构选项
        this.institutionLoading = true
        listApprovedInstitutions({ pageSize: 20 }).then(response => {
          this.institutionOptions = response.rows
          this.institutionLoading = false
        }).catch(() => {
          this.institutionLoading = false
        })
        this.open = true
        this.title = '修改机构评级'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          // 确保评分数据为数字类型
          const serviceScore = parseFloat(this.form.serviceScore) || 0
          const facilityScore = parseFloat(this.form.facilityScore) || 0
          const managementScore = parseFloat(this.form.managementScore) || 0
          const safetyScore = parseFloat(this.form.safetyScore) || 0

          // 计算总分
          this.form.totalScore = (serviceScore + facilityScore + managementScore + safetyScore).toFixed(1)

          if (this.form.ratingId != null) {
            updateRating(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            })
          } else {
            addRating(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const ratingIds = row.ratingId || this.ids
      this.$modal.confirm('是否确认删除评级编号为"' + ratingIds + '"的数据项？').then(function() {
        return delRating(ratingIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleBatchUpdate() {
      if (this.ids.length === 0) {
        this.$modal.msgError('请选择要更新的数据')
        return
      }
      this.$modal.confirm('确认批量更新选中的' + this.ids.length + '条数据吗？').then(() => {
        // TODO: 实现批量更新
        this.$modal.msgSuccess('批量更新成功')
        this.getList()
      })
    },
    handleExport() {
      this.download('supervision/institution/rating/export', {
        ...this.queryParams
      }, `rating_${new Date().getTime()}.xlsx`)
    },
    getProgressColor(percentage) {
      if (percentage >= 90) return '#67C23A'
      if (percentage >= 80) return '#409EFF'
      if (percentage >= 70) return '#E6A23C'
      return '#F56C6C'
    },
    // 搜索机构
    searchInstitutions(query) {
      this.institutionLoading = true
      // 如果没有输入查询条件，显示所有机构；否则按名称搜索
      const params = query ? { institutionName: query, pageSize: 20 } : { pageSize: 20 }
      listApprovedInstitutions(params).then(response => {
        this.institutionOptions = response.rows
        this.institutionLoading = false
      }).catch(() => {
        this.institutionLoading = false
      })
    },
    // 机构选择变化
    handleInstitutionChange(institutionId) {
      if (institutionId) {
        const selectedInstitution = this.institutionOptions.find(item => item.institutionId === institutionId)
        if (selectedInstitution) {
          this.form.institutionName = selectedInstitution.institutionName
          this.form.creditCode = selectedInstitution.creditCode
        }
      } else {
        this.form.institutionName = null
        this.form.creditCode = null
      }
    },
    // 计算总分
    calculateTotalScore() {
      const serviceScore = parseFloat(this.form.serviceScore) || 0
      const facilityScore = parseFloat(this.form.facilityScore) || 0
      const managementScore = parseFloat(this.form.managementScore) || 0
      const safetyScore = parseFloat(this.form.safetyScore) || 0

      const totalScore = serviceScore + facilityScore + managementScore + safetyScore
      this.form.totalScore = totalScore.toFixed(1)
    }
  }
}
</script>

<style scoped>
.score-card {
  text-align: center;
  margin-bottom: 20px;
}

.score-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.score-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 10px;
}

.font-bold {
  font-weight: bold;
}

.text-success {
  color: #67C23A;
}

.mb20 {
  margin-bottom: 20px;
}

.rating-remark {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
  color: #606266;
}
</style>