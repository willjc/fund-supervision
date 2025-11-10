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
      <el-table-column label="评级等级" align="center" prop="ratingLevel" width="100">
        <template slot-scope="scope">
          <el-rate
            v-model="scope.row.ratingLevel"
            disabled
            show-score
            text-color="#ff9900"
            score-template="{value}星"
          >
          </el-rate>
        </template>
      </el-table-column>
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
      <el-table-column label="评级机构" align="center" prop="ratingOrg" width="120" />
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
          <el-button
            size="mini"
            type="text"
            icon="el-icon-printer"
            @click="handlePrint(scope.row)"
          >打印</el-button>
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
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="机构名称" prop="institutionName">
              <el-input v-model="form.institutionName" placeholder="请输入机构名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="统一信用代码" prop="creditCode">
              <el-input v-model="form.creditCode" placeholder="请输入统一信用代码" />
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
              <el-input-number v-model="form.serviceScore" :min="0" :max="25" :precision="1" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设施环境 (25分)" prop="facilityScore">
              <el-input-number v-model="form.facilityScore" :min="0" :max="25" :precision="1" style="width: 100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="管理水平 (25分)" prop="managementScore">
              <el-input-number v-model="form.managementScore" :min="0" :max="25" :precision="1" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="安全卫生 (25分)" prop="safetyScore">
              <el-input-number v-model="form.safetyScore" :min="0" :max="25" :precision="1" style="width: 100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="评级机构" prop="ratingOrg">
              <el-input v-model="form.ratingOrg" placeholder="请输入评级机构" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效期" prop="validityPeriod">
              <el-input-number v-model="form.validityPeriod" :min="1" :max="60" style="width: 100%">
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
            v-model="detailData.ratingLevel"
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
        <el-descriptions-item label="评级机构">{{ detailData.ratingOrg }}</el-descriptions-item>
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
        <el-button type="primary" @click="handlePrintDetail">打印证书</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRating, getRating, delRating, addRating, updateRating } from '@/api/supervision/institution'

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
      rules: {
        institutionName: [
          { required: true, message: '机构名称不能为空', trigger: 'blur' }
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
        ratingOrg: [
          { required: true, message: '评级机构不能为空', trigger: 'blur' }
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
        // TODO: 连接真实API
        // this.ratingList = response.rows
        // this.total = response.total
        // this.loading = false

        // 模拟数据
        setTimeout(() => {
          this.ratingList = [
            {
              ratingId: 1,
              institutionName: '幸福养老院',
              creditCode: '91410100MA44XXXX01',
              ratingLevel: 5,
              totalScore: 95.5,
              serviceScore: 24.0,
              facilityScore: 23.5,
              managementScore: 24.5,
              safetyScore: 23.5,
              ratingStatus: '1',
              ratingDate: '2024-12-15',
              expireDate: '2025-12-15',
              ratingOrg: '民政部养老服务机构评级中心',
              ratingRemark: '该机构服务质量优秀，设施完善，管理规范，安全卫生状况良��，综合评定为五星级养老机构。'
            },
            {
              ratingId: 2,
              institutionName: '夕阳红公寓',
              creditCode: '91410100MA44XXXX02',
              ratingLevel: 4,
              totalScore: 82.0,
              serviceScore: 20.5,
              facilityScore: 20.0,
              managementScore: 21.0,
              safetyScore: 20.5,
              ratingStatus: '1',
              ratingDate: '2024-11-20',
              expireDate: '2025-11-20',
              ratingOrg: '民政部养老服务机构评级中心',
              ratingRemark: '该机构服务质量良好，设施基本完善，管理水平较高，安全卫生符合要求，综合评定为四星级养老机构。'
            },
            {
              ratingId: 3,
              institutionName: '康乐养老中心',
              creditCode: '91410100MA44XXXX03',
              ratingLevel: 3,
              totalScore: 75.0,
              serviceScore: 18.5,
              facilityScore: 19.0,
              managementScore: 18.5,
              safetyScore: 19.0,
              ratingStatus: '0',
              ratingDate: '2024-10-10',
              expireDate: '2025-10-10',
              ratingOrg: '民政部养老服务机构评级中心',
              ratingRemark: '该机构基本达到养老服务标准，但在某些方面仍需改进，综合评定为三星级养老机构。'
            }
          ]
          this.total = this.ratingList.length
          this.loading = false
        }, 500)
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        ratingId: null,
        institutionName: null,
        creditCode: null,
        ratingLevel: 3,
        totalScore: 0,
        serviceScore: 0,
        facilityScore: 0,
        managementScore: 0,
        safetyScore: 0,
        ratingDate: null,
        ratingOrg: null,
        validityPeriod: 12,
        ratingRemark: null
      }
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
        this.open = true
        this.title = '修改机构评级'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          // 计算总分
          this.form.totalScore = this.form.serviceScore + this.form.facilityScore +
                                this.form.managementScore + this.form.safetyScore

          // 计算评级等级
          const totalScore = this.form.totalScore
          if (totalScore >= 90) {
            this.form.ratingLevel = 5
          } else if (totalScore >= 80) {
            this.form.ratingLevel = 4
          } else if (totalScore >= 70) {
            this.form.ratingLevel = 3
          } else if (totalScore >= 60) {
            this.form.ratingLevel = 2
          } else {
            this.form.ratingLevel = 1
          }

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
    handlePrint(row) {
      // TODO: 实现打印功能
      this.$modal.msgInfo('打印功能开发中...')
    },
    handlePrintDetail() {
      // TODO: 实现打印功能
      this.$modal.msgInfo('打印功能开发中...')
    },
    getProgressColor(percentage) {
      if (percentage >= 90) return '#67C23A'
      if (percentage >= 80) return '#409EFF'
      if (percentage >= 70) return '#E6A23C'
      return '#F56C6C'
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