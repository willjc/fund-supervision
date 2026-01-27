<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-primary">
              <i class="el-icon-office-building"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">机构总数</div>
              <div class="stat-value">{{ statistics.totalInstitutions }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-success">
              <i class="el-icon-check"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">已授权机构</div>
              <div class="stat-value">{{ statistics.authorizedCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-warning">
              <i class="el-icon-warning"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">未授权机构</div>
              <div class="stat-value">{{ statistics.unauthorizedCount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-danger">
              <i class="el-icon-money"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">会员费总收入</div>
              <div class="stat-value">{{ statistics.totalRevenue }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 查询表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="90px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input
          v-model="queryParams.institutionName"
          placeholder="请输入机构名称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="授权状态" prop="authStatus">
        <el-select v-model="queryParams.authStatus" placeholder="请选择授权状态" clearable style="width: 150px">
          <el-option label="已授权" value="1" />
          <el-option label="未授权" value="0" />
          <el-option label="已暂停" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="收费标准" prop="feeStandard">
        <el-select v-model="queryParams.feeStandard" placeholder="请选择收费标准" clearable style="width: 150px">
          <el-option label="标准版" value="standard" />
          <el-option label="高级版" value="advanced" />
          <el-option label="定制版" value="custom" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAuthorize"
        >授权机构</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-setting"
          size="mini"
          @click="handleBatchSetFee"
        >批量设置费用</el-button>
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
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="memberFeeList" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="机构编号" align="center" prop="institutionId" width="100" />
      <el-table-column label="机构名称" align="center" prop="institutionName" width="200" show-overflow-tooltip />
      <el-table-column label="授权状态" align="center" prop="authStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.authStatus === '1'" type="success">已授权</el-tag>
          <el-tag v-else-if="scope.row.authStatus === '0'" type="info">未授权</el-tag>
          <el-tag v-else-if="scope.row.authStatus === '2'" type="warning">已暂停</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收费标准" align="center" prop="feeStandard" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.feeStandard === 'standard'" type="primary">标准版</el-tag>
          <el-tag v-else-if="scope.row.feeStandard === 'advanced'" type="success">高级版</el-tag>
          <el-tag v-else-if="scope.row.feeStandard === 'custom'" type="warning">定制版</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="会员费金额" align="center" prop="memberFee" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.memberFee" style="color: #E6A23C; font-weight: bold">¥{{ scope.row.memberFee }}/年</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="已收会员费" align="center" prop="collectedFee" width="120">
        <template slot-scope="scope">
          <span style="color: #67C23A; font-weight: bold">¥{{ scope.row.collectedFee || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="会员人数" align="center" prop="memberCount" width="100">
        <template slot-scope="scope">
          <el-link type="primary" @click="handleViewMembers(scope.row)">{{ scope.row.memberCount || 0 }}人</el-link>
        </template>
      </el-table-column>
      <el-table-column label="授权日期" align="center" prop="authDate" width="110" />
      <el-table-column label="生效日期" align="center" prop="effectiveDate" width="110" />
      <el-table-column label="到期日期" align="center" prop="expiryDate" width="110">
        <template slot-scope="scope">
          <span v-if="scope.row.expiryDate" :style="getExpiryStyle(scope.row.expiryDate)">{{ scope.row.expiryDate }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="scope.row.authStatus === '0'" size="mini" type="text" icon="el-icon-check" @click="handleAuth(scope.row)">授权</el-button>
          <el-button v-if="scope.row.authStatus === '1'" size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">修改</el-button>
          <el-button v-if="scope.row.authStatus === '1'" size="mini" type="text" icon="el-icon-video-pause" @click="handlePause(scope.row)">暂停</el-button>
          <el-button v-if="scope.row.authStatus === '2'" size="mini" type="text" icon="el-icon-video-play" @click="handleResume(scope.row)">恢复</el-button>
          <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)">
            <el-button size="mini" type="text">
              更多<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="detail" icon="el-icon-view">详情</el-dropdown-item>
              <el-dropdown-item command="history" icon="el-icon-time">收费记录</el-dropdown-item>
              <el-dropdown-item command="revoke" icon="el-icon-delete" v-if="scope.row.authStatus !== '0'">撤销授权</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
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

    <!-- 授权/编辑对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="机构名称" prop="institutionId" v-if="!form.institutionId">
          <el-select v-model="form.institutionId" placeholder="请选择机构" filterable style="width: 100%">
            <el-option
              v-for="item in institutionList"
              :key="item.institutionId"
              :label="item.institutionName"
              :value="item.institutionId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="机构名称" v-else>
          <el-input v-model="form.institutionName" disabled />
        </el-form-item>
        <el-form-item label="收费标准" prop="feeStandard">
          <el-radio-group v-model="form.feeStandard" @change="handleFeeStandardChange">
            <el-radio label="standard">标准版（老人入住时统一收取固定金额）</el-radio>
            <el-radio label="advanced">高级版（根据老人入住时长和服务等级收取）</el-radio>
            <el-radio label="custom">定制版（机构自定义收费规则）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="会员费金额" prop="memberFee" v-if="form.feeStandard === 'standard'">
          <el-input-number v-model="form.memberFee" :min="0" :max="100000" :precision="2" style="width: 200px" />
          <span style="margin-left: 10px">元/年</span>
          <div style="color: #909399; font-size: 12px; margin-top: 5px">说明：老人入住时统一收取此金额作为会员费</div>
        </el-form-item>
        <el-form-item label="收费规则" prop="feeRules" v-if="form.feeStandard === 'advanced'">
          <el-input v-model="form.feeRules" type="textarea" :rows="3" placeholder="例如：入住1年以上：2000元/年；入住3年以上：1500元/年" />
          <div style="color: #909399; font-size: 12px; margin-top: 5px">说明：根据老人入住时长、服务等级等因素设定不同的收费标准</div>
        </el-form-item>
        <el-form-item label="自定义规则" prop="customRules" v-if="form.feeStandard === 'custom'">
          <el-input v-model="form.customRules" type="textarea" :rows="4" placeholder="请输入机构自定义的会员费收费规则" />
          <div style="color: #909399; font-size: 12px; margin-top: 5px">说明：由机构自行制定收费规则，需经民政部门审核批准</div>
        </el-form-item>
        <el-form-item label="生效日期" prop="effectiveDate">
          <el-date-picker
            v-model="form.effectiveDate"
            type="date"
            placeholder="选择生效日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="到期日期" prop="expiryDate">
          <el-date-picker
            v-model="form.expiryDate"
            type="date"
            placeholder="选择到期日期（不填表示永久有效）"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="会员权益说明" prop="benefits">
          <el-input v-model="form.benefits" type="textarea" :rows="3" placeholder="请输入缴纳会员费后老人可享受的权益" />
          <div style="color: #909399; font-size: 12px; margin-top: 5px">例如：优先床位选择权、免费体检、节日礼品等</div>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="会员费收费权限详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="机构编号">{{ detailData.institutionId }}</el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ detailData.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="授权状态">
          <el-tag v-if="detailData.authStatus === '1'" type="success">已授权</el-tag>
          <el-tag v-else-if="detailData.authStatus === '0'" type="info">未授权</el-tag>
          <el-tag v-else-if="detailData.authStatus === '2'" type="warning">已暂停</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="收费标准">
          <el-tag v-if="detailData.feeStandard === 'standard'" type="primary">标准版</el-tag>
          <el-tag v-else-if="detailData.feeStandard === 'advanced'" type="success">高级版</el-tag>
          <el-tag v-else-if="detailData.feeStandard === 'custom'" type="warning">定制版</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="会员费金额">{{ detailData.memberFee ? '¥' + detailData.memberFee + '/年' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="已收会员费">¥{{ detailData.collectedFee || 0 }}</el-descriptions-item>
        <el-descriptions-item label="会员人数">{{ detailData.memberCount || 0 }}人</el-descriptions-item>
        <el-descriptions-item label="授权日期">{{ detailData.authDate }}</el-descriptions-item>
        <el-descriptions-item label="生效日期">{{ detailData.effectiveDate }}</el-descriptions-item>
        <el-descriptions-item label="到期日期">{{ detailData.expiryDate || '永久有效' }}</el-descriptions-item>
        <el-descriptions-item label="收费规则" :span="2" v-if="detailData.feeRules">{{ detailData.feeRules }}</el-descriptions-item>
        <el-descriptions-item label="自定义规则" :span="2" v-if="detailData.customRules">{{ detailData.customRules }}</el-descriptions-item>
        <el-descriptions-item label="会员权益说明" :span="2">{{ detailData.benefits }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 会员列表对话框 -->
    <el-dialog title="会员列表" :visible.sync="membersOpen" width="1000px" append-to-body>
      <el-table :data="membersList" border>
        <el-table-column label="老人姓名" align="center" prop="elderName" width="100" />
        <el-table-column label="身份证号" align="center" prop="idCard" width="180" />
        <el-table-column label="入住床位" align="center" prop="bedNo" width="120" />
        <el-table-column label="入住日期" align="center" prop="checkInDate" width="110" />
        <el-table-column label="会员费金额" align="center" prop="memberFee" width="110">
          <template slot-scope="scope">
            <span style="color: #E6A23C; font-weight: bold">¥{{ scope.row.memberFee }}</span>
          </template>
        </el-table-column>
        <el-table-column label="缴费日期" align="center" prop="paymentDate" width="110" />
        <el-table-column label="会员状态" align="center" prop="memberStatus" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.memberStatus === '1'" type="success">正常</el-tag>
            <el-tag v-else type="info">已失效</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="membersOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 收费记录对话框 -->
    <el-dialog title="会员费收费记录" :visible.sync="historyOpen" width="1000px" append-to-body>
      <div style="margin-bottom: 15px">
        <el-tag type="primary">机构：{{ currentInstitutionName }}</el-tag>
        <el-tag type="success" style="margin-left: 10px">累计收费：¥{{ totalCollected }}</el-tag>
      </div>
      <el-table :data="feeHistory" border>
        <el-table-column label="老人姓名" align="center" prop="elderName" width="100" />
        <el-table-column label="订单编号" align="center" prop="orderNo" width="160" />
        <el-table-column label="会员费金额" align="center" prop="memberFee" width="110">
          <template slot-scope="scope">
            <span style="color: #E6A23C; font-weight: bold">¥{{ scope.row.memberFee }}</span>
          </template>
        </el-table-column>
        <el-table-column label="缴费方式" align="center" prop="paymentMethod" width="100" />
        <el-table-column label="缴费时间" align="center" prop="paymentTime" width="160" />
        <el-table-column label="收费标准" align="center" prop="feeStandard" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.feeStandard === 'standard'" type="primary" size="small">标准版</el-tag>
            <el-tag v-else-if="scope.row.feeStandard === 'advanced'" type="success" size="small">高级版</el-tag>
            <el-tag v-else type="warning" size="small">定制版</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作员" align="center" prop="operator" width="100" />
        <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="historyOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInstitution } from '@/api/supervision/institution'

export default {
  name: 'MemberFee',
  data() {
    return {
      // 统计数据
      statistics: {
        totalInstitutions: 156,
        authorizedCount: 98,
        unauthorizedCount: 58,
        totalRevenue: '1,256,000'
      },
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 总条数
      total: 0,
      // 会员费管理表格数据
      memberFeeList: [],
      // 机构列表
      institutionList: [],
      // 会员列表
      membersList: [],
      // 收费记录
      feeHistory: [],
      // 当前机构名称
      currentInstitutionName: '',
      // 累计收费
      totalCollected: 0,
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示会员列表弹出层
      membersOpen: false,
      // 是否显示收费记录弹出层
      historyOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        authStatus: null,
        feeStandard: null
      },
      // 表单参数
      form: {},
      // 详情数据
      detailData: {},
      // 表单校验
      rules: {
        institutionId: [
          { required: true, message: '机构名称不能为空', trigger: 'change' }
        ],
        feeStandard: [
          { required: true, message: '收费标准不能为空', trigger: 'change' }
        ],
        memberFee: [
          { required: true, message: '会员费金额不能为空', trigger: 'blur' }
        ],
        effectiveDate: [
          { required: true, message: '生效日期不能为空', trigger: 'change' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getInstitutionList()
  },
  methods: {
    /** 查询会员费管理列表 */
    getList() {
      this.loading = true
      // 模拟数据
      setTimeout(() => {
        this.memberFeeList = [
          {
            institutionId: 'I10001',
            institutionName: '北京市朝阳区幸福养老院',
            authStatus: '1',
            feeStandard: 'standard',
            memberFee: 2000,
            collectedFee: 128000,
            memberCount: 64,
            authDate: '2024-01-15',
            effectiveDate: '2024-02-01',
            expiryDate: '2025-12-31',
            benefits: '优先床位选择权、免费体检、节日礼品',
            remark: '标准版会员费'
          },
          {
            institutionId: 'I10002',
            institutionName: '上海市浦东新区康乐养老中心',
            authStatus: '1',
            feeStandard: 'advanced',
            memberFee: null,
            feeRules: '入住1年以上：2000元/年；入住3年以上：1500元/年',
            collectedFee: 285000,
            memberCount: 156,
            authDate: '2024-03-10',
            effectiveDate: '2024-04-01',
            expiryDate: '',
            benefits: '根据入住时长享受不同优惠，长期入住更优惠',
            remark: '高级版会员费'
          },
          {
            institutionId: 'I10003',
            institutionName: '广州市天河区爱心养老服务中心',
            authStatus: '0',
            feeStandard: null,
            memberFee: null,
            collectedFee: 0,
            memberCount: 0,
            authDate: '',
            effectiveDate: '',
            expiryDate: '',
            remark: '未授权'
          },
          {
            institutionId: 'I10004',
            institutionName: '深圳市南山区夕阳红养老院',
            authStatus: '2',
            feeStandard: 'custom',
            memberFee: null,
            customRules: '根据服务等级和房型收取会员费：单人间3000元/年，双人间2000元/年',
            collectedFee: 96000,
            memberCount: 42,
            authDate: '2023-12-01',
            effectiveDate: '2024-01-01',
            expiryDate: '2024-12-31',
            benefits: '根据房型享受不同会员权益',
            remark: '定制版会员费，已暂停'
          },
          {
            institutionId: 'I10005',
            institutionName: '杭州市西湖区福寿康养老中心',
            authStatus: '1',
            feeStandard: 'standard',
            memberFee: 1800,
            collectedFee: 162000,
            memberCount: 90,
            authDate: '2024-02-20',
            effectiveDate: '2024-03-01',
            expiryDate: '',
            benefits: '优先床位选择权、免费体检',
            remark: '标准版会员费'
          }
        ]
        this.total = this.memberFeeList.length
        this.loading = false
      }, 500)
    },
    /** 获取机构列表 */
    getInstitutionList() {
      listInstitution({}).then(response => {
        this.institutionList = response.rows || []
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.institutionId)
    },
    /** 授权机构 */
    handleAuthorize() {
      this.reset()
      this.open = true
      this.title = '授权机构会员费收费权限'
    },
    /** 授权按钮操作 */
    handleAuth(row) {
      this.reset()
      this.form.institutionId = row.institutionId
      this.form.institutionName = row.institutionName
      this.open = true
      this.title = '授权机构会员费收费权限'
    },
    /** 修改按钮操作 */
    handleEdit(row) {
      this.form = { ...row }
      this.open = true
      this.title = '修改会员费收费权限'
    },
    /** 暂停按钮操作 */
    handlePause(row) {
      this.$modal.confirm('是否确认暂停"' + row.institutionName + '"的会员费收费权限？').then(() => {
        this.$modal.msgSuccess('暂停成功')
        this.getList()
      })
    },
    /** 恢复按钮操作 */
    handleResume(row) {
      this.$modal.confirm('是否确认恢复"' + row.institutionName + '"的会员费收费权限？').then(() => {
        this.$modal.msgSuccess('恢复成功')
        this.getList()
      })
    },
    /** 表单重置 */
    reset() {
      this.form = {
        institutionId: null,
        institutionName: null,
        feeStandard: 'standard',
        memberFee: 2000,
        feeRules: null,
        customRules: null,
        effectiveDate: null,
        expiryDate: null,
        benefits: null,
        remark: null
      }
      this.resetForm('form')
    },
    /** 收费标准改变 */
    handleFeeStandardChange(value) {
      if (value === 'standard') {
        this.form.memberFee = 2000
        this.form.feeRules = null
        this.form.customRules = null
      } else if (value === 'advanced') {
        this.form.memberFee = null
        this.form.feeRules = ''
        this.form.customRules = null
      } else if (value === 'custom') {
        this.form.memberFee = null
        this.form.feeRules = null
        this.form.customRules = ''
      }
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          this.$modal.msgSuccess('操作成功')
          this.open = false
          this.getList()
        }
      })
    },
    /** 取消按钮 */
    cancel() {
      this.open = false
      this.reset()
    },
    /** 查看会员列表 */
    handleViewMembers(row) {
      this.currentInstitutionName = row.institutionName
      // 模拟数据
      this.membersList = [
        {
          elderName: '王大爷',
          idCard: '110101195001011234',
          bedNo: 'A区101-1',
          checkInDate: '2024-03-15',
          memberFee: 2000,
          paymentDate: '2024-03-15',
          memberStatus: '1',
          remark: '标准版会员'
        },
        {
          elderName: '李奶奶',
          idCard: '110101195203021234',
          bedNo: 'A区102-2',
          checkInDate: '2024-04-20',
          memberFee: 2000,
          paymentDate: '2024-04-20',
          memberStatus: '1',
          remark: '标准版会员'
        }
      ]
      this.membersOpen = true
    },
    /** 更多操作 */
    handleCommand(command, row) {
      switch (command) {
        case 'detail':
          this.handleDetail(row)
          break
        case 'history':
          this.handleHistory(row)
          break
        case 'revoke':
          this.$modal.confirm('是否确认撤销"' + row.institutionName + '"的会员费收费权限？撤销后该机构将无法向老人收取会员费。').then(() => {
            this.$modal.msgSuccess('撤销成功')
            this.getList()
          })
          break
      }
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.detailData = { ...row }
      this.detailOpen = true
    },
    /** 收费记录 */
    handleHistory(row) {
      this.currentInstitutionName = row.institutionName
      this.totalCollected = row.collectedFee || 0
      // 模拟数据
      this.feeHistory = [
        {
          elderName: '王大爷',
          orderNo: 'DD202403150001',
          memberFee: 2000,
          paymentMethod: '微信支付',
          paymentTime: '2024-03-15 10:30:00',
          feeStandard: 'standard',
          operator: '张三',
          remark: '入住时缴纳'
        },
        {
          elderName: '李奶奶',
          orderNo: 'DD202404200002',
          memberFee: 2000,
          paymentMethod: '支付宝',
          paymentTime: '2024-04-20 14:20:00',
          feeStandard: 'standard',
          operator: '李四',
          remark: '入住时缴纳'
        }
      ]
      this.historyOpen = true
    },
    /** 批量设置费用 */
    handleBatchSetFee() {
      if (this.ids.length === 0) {
        this.$modal.msgWarning('请选择要设置费用的机构')
        return
      }
      this.$modal.msgSuccess('批量设置功能开发中')
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.msgSuccess('导出功能开发中')
    },
    /** 获取到期日期样式 */
    getExpiryStyle(expiryDate) {
      if (!expiryDate) return ''
      const today = new Date()
      const expiry = new Date(expiryDate)
      const remainingDays = Math.floor((expiry - today) / (1000 * 60 * 60 * 24))

      if (remainingDays < 0) {
        return 'color: #F56C6C; font-weight: bold'
      } else if (remainingDays <= 30) {
        return 'color: #E6A23C; font-weight: bold'
      }
      return ''
    }
  }
}
</script>

<style scoped>
.mb20 {
  margin-bottom: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon i {
  font-size: 30px;
  color: #fff;
}

.bg-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.bg-success {
  background: linear-gradient(135deg, #56ab2f 0%, #a8e063 100%);
}

.bg-warning {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.bg-danger {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
</style>
