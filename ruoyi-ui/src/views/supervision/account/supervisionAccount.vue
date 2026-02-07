<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-primary">
              <i class="el-icon-s-finance"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">监管账户总数</div>
              <div class="stat-value">{{ statistics.totalAccounts }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-success">
              <i class="el-icon-circle-check"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">正常账户</div>
              <div class="stat-value">{{ statistics.normalAccounts }}</div>
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
              <div class="stat-title">待审核</div>
              <div class="stat-value">{{ statistics.pendingAccounts }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div class="stat-card">
            <div class="stat-icon bg-danger">
              <i class="el-icon-close"></i>
            </div>
            <div class="stat-content">
              <div class="stat-title">已关闭</div>
              <div class="stat-value">{{ statistics.closedAccounts }}</div>
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
      <el-form-item label="账户状态" prop="accountStatus">
        <el-select v-model="queryParams.accountStatus" placeholder="请选择账户状态" clearable style="width: 150px">
          <el-option label="待审核" value="0" />
          <el-option label="正常" value="1" />
          <el-option label="冻结" value="2" />
          <el-option label="已关闭" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="开户银行" prop="bankName">
        <el-select v-model="queryParams.bankName" placeholder="请选择开户银行" clearable style="width: 180px">
          <el-option label="中国工商银行" value="ICBC" />
          <el-option label="中国建设银行" value="CCB" />
          <el-option label="中国农业银行" value="ABC" />
          <el-option label="中国银行" value="BOC" />
          <el-option label="招商银行" value="CMB" />
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
          @click="handleAdd"
        >开通账户</el-button>
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
    <el-table v-loading="loading" :data="accountList" border>
      <el-table-column label="机构编号" align="center" prop="institutionId" width="100" />
      <el-table-column label="机构名称" align="center" prop="institutionName" width="200" show-overflow-tooltip />
      <el-table-column label="银行账号" align="center" prop="bankAccount" width="180" />
      <el-table-column label="开户银行" align="center" prop="bankName" width="150" />
      <el-table-column label="开户支行" align="center" prop="bankBranch" width="150" show-overflow-tooltip />
      <el-table-column label="账户类型" align="center" prop="accountType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.accountType === 'supervision'" type="primary">监管账户</el-tag>
          <el-tag v-else-if="scope.row.accountType === 'basic'" type="success">基本账户</el-tag>
          <el-tag v-else type="info">其他账户</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="账户状态" align="center" prop="accountStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.accountStatus === '0'" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.accountStatus === '1'" type="success">正常</el-tag>
          <el-tag v-else-if="scope.row.accountStatus === '2'" type="danger">冻结</el-tag>
          <el-tag v-else type="info">已关闭</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="开户日期" align="center" prop="openDate" width="110" />
      <el-table-column label="签约状态" align="center" prop="contractStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.contractStatus === '1'" type="success" size="small">已签约</el-tag>
          <el-tag v-else type="info" size="small">未签约</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="会员费余额" align="center" prop="memberBalance" width="120">
        <template slot-scope="scope">
          <span style="color: #67C23A; font-weight: bold;">¥{{ formatAmount(scope.row.memberBalance) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280" fixed="right">
        <template slot-scope="scope">
          <el-button v-if="scope.row.accountStatus === '0'" size="mini" type="text" icon="el-icon-check" @click="handleApprove(scope.row)">审核</el-button>
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.accountStatus === '1'" size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">变更</el-button>
          <el-dropdown size="mini" @command="(command) => handleCommand(command, scope.row)">
            <el-button size="mini" type="text">
              更多<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="contract" icon="el-icon-document" v-if="scope.row.contractStatus !== '1'">签约</el-dropdown-item>
              <el-dropdown-item command="freeze" icon="el-icon-lock" v-if="scope.row.accountStatus === '1'">冻结</el-dropdown-item>
              <el-dropdown-item command="unfreeze" icon="el-icon-unlock" v-if="scope.row.accountStatus === '2'">解冻</el-dropdown-item>
              <el-dropdown-item command="close" icon="el-icon-circle-close" v-if="scope.row.accountStatus !== '3'">关闭</el-dropdown-item>
              <el-dropdown-item command="history" icon="el-icon-time">操作记录</el-dropdown-item>
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

    <!-- 开通/变更账户对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="机构名称" prop="institutionId" v-if="!form.id">
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
        <el-form-item label="账户类型" prop="accountType">
          <el-radio-group v-model="form.accountType">
            <el-radio label="supervision">监管账户</el-radio>
            <el-radio label="basic">基本账户</el-radio>
          </el-radio-group>
          <div style="color: #909399; font-size: 12px; margin-top: 5px">
            监管账户：用于存放老人预收费资金，受民政部门监管；基本账户：机构日常经营账户
          </div>
        </el-form-item>
        <el-form-item label="开户银行" prop="bankName">
          <el-select v-model="form.bankName" placeholder="请选择开户银行" style="width: 100%">
            <el-option label="中国工商银行" value="中国工商银行" />
            <el-option label="中国建设银行" value="中国建设银行" />
            <el-option label="中国农业银行" value="中国农业银行" />
            <el-option label="中国银行" value="中国银行" />
            <el-option label="招商银行" value="招商银行" />
            <el-option label="交通银行" value="交通银行" />
            <el-option label="中信银行" value="中信银行" />
          </el-select>
        </el-form-item>
        <el-form-item label="开户支行" prop="bankBranch">
          <el-input v-model="form.bankBranch" placeholder="请输入开户支行" />
        </el-form-item>
        <el-form-item label="银行账号" prop="bankAccount">
          <el-input v-model="form.bankAccount" placeholder="请输入银行账号" maxlength="19" />
        </el-form-item>
        <el-form-item label="账户名称" prop="accountName">
          <el-input v-model="form.accountName" placeholder="请输入账户名称" />
          <div style="color: #909399; font-size: 12px; margin-top: 5px">账户名称应与机构名称一致</div>
        </el-form-item>
        <el-form-item label="联行号" prop="bankCode">
          <el-input v-model="form.bankCode" placeholder="请输入联行号（12位）" maxlength="12" />
        </el-form-item>
        <el-form-item label="开户日期" prop="openDate">
          <el-date-picker
            v-model="form.openDate"
            type="date"
            placeholder="选择开户日期"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="form.contactPerson" placeholder="请输入银行联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="账户用途说明" prop="purpose">
          <el-input v-model="form.purpose" type="textarea" :rows="3" placeholder="请输入账户用途说明" />
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

    <!-- 审核对话框 -->
    <el-dialog title="账户审核" :visible.sync="approveOpen" width="600px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" :rules="approveRules" label-width="100px">
        <el-form-item label="机构名称">
          <el-input v-model="approveForm.institutionName" disabled />
        </el-form-item>
        <el-form-item label="银行账号">
          <el-input v-model="approveForm.bankAccount" disabled />
        </el-form-item>
        <el-form-item label="开户银行">
          <el-input v-model="approveForm.bankName" disabled />
        </el-form-item>
        <el-form-item label="审核结果" prop="approveResult">
          <el-radio-group v-model="approveForm.approveResult">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="approveOpinion">
          <el-input v-model="approveForm.approveOpinion" type="textarea" :rows="4" placeholder="请输入审核意见" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitApprove">确 定</el-button>
        <el-button @click="approveOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="监管账户详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="机构编号">{{ detailData.institutionId }}</el-descriptions-item>
        <el-descriptions-item label="机构名称">{{ detailData.institutionName }}</el-descriptions-item>
        <el-descriptions-item label="账户类型">
          <el-tag v-if="detailData.accountType === 'supervision'" type="primary">监管账户</el-tag>
          <el-tag v-else-if="detailData.accountType === 'basic'" type="success">基本账户</el-tag>
          <el-tag v-else type="info">其他账户</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="账户状态">
          <el-tag v-if="detailData.accountStatus === '0'" type="warning">待审核</el-tag>
          <el-tag v-else-if="detailData.accountStatus === '1'" type="success">正常</el-tag>
          <el-tag v-else-if="detailData.accountStatus === '2'" type="danger">冻结</el-tag>
          <el-tag v-else type="info">已关闭</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="银行账号">{{ detailData.bankAccount }}</el-descriptions-item>
        <el-descriptions-item label="账户名称">{{ detailData.accountName }}</el-descriptions-item>
        <el-descriptions-item label="开户银行">{{ detailData.bankName }}</el-descriptions-item>
        <el-descriptions-item label="开户支行">{{ detailData.bankBranch }}</el-descriptions-item>
        <el-descriptions-item label="联行号">{{ detailData.bankCode }}</el-descriptions-item>
        <el-descriptions-item label="开户日期">{{ detailData.openDate }}</el-descriptions-item>
        <el-descriptions-item label="签约状态">
          <el-tag v-if="detailData.contractStatus === '1'" type="success">已签约</el-tag>
          <el-tag v-else type="info">未签约</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="签约日期">{{ detailData.contractDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detailData.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="账户用途说明" :span="2">{{ detailData.purpose }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ detailData.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 操作记录对话框 -->
    <el-dialog title="账户操作记录" :visible.sync="historyOpen" width="1000px" append-to-body>
      <div style="margin-bottom: 15px">
        <el-tag type="primary">机构：{{ currentInstitutionName }}</el-tag>
        <el-tag type="success" style="margin-left: 10px">账号：{{ currentBankAccount }}</el-tag>
      </div>
      <el-table :data="operationHistory" border>
        <el-table-column label="操作时间" align="center" prop="operateTime" width="160" />
        <el-table-column label="操作类型" align="center" prop="operateType" width="120">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.operateType === 'open'" type="primary" size="small">开通</el-tag>
            <el-tag v-else-if="scope.row.operateType === 'approve'" type="success" size="small">审核</el-tag>
            <el-tag v-else-if="scope.row.operateType === 'edit'" type="warning" size="small">变更</el-tag>
            <el-tag v-else-if="scope.row.operateType === 'freeze'" type="danger" size="small">冻结</el-tag>
            <el-tag v-else-if="scope.row.operateType === 'unfreeze'" type="info" size="small">解冻</el-tag>
            <el-tag v-else-if="scope.row.operateType === 'close'" type="info" size="small">关闭</el-tag>
            <el-tag v-else size="small">{{ scope.row.operateType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作内容" align="center" prop="operateContent" show-overflow-tooltip />
        <el-table-column label="操作人" align="center" prop="operator" width="100" />
        <el-table-column label="操作结果" align="center" prop="operateResult" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.operateResult === '1'" type="success" size="small">成功</el-tag>
            <el-tag v-else type="danger" size="small">失败</el-tag>
          </template>
        </el-table-column>
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
  name: 'AccountMaintenance',
  data() {
    return {
      // 统计数据
      statistics: {
        totalAccounts: 156,
        normalAccounts: 128,
        pendingAccounts: 18,
        closedAccounts: 10
      },
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 账户列表
      accountList: [],
      // 机构列表
      institutionList: [],
      // 操作记录列表
      operationHistory: [],
      // 当前机构名称
      currentInstitutionName: '',
      // 当前银行账号
      currentBankAccount: '',
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 是否显示审核弹出层
      approveOpen: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // 是否显示操作记录弹出层
      historyOpen: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        institutionName: null,
        accountStatus: null,
        bankName: null
      },
      // 表单参数
      form: {},
      // 审核表单参数
      approveForm: {},
      // 详情数据
      detailData: {},
      // 表单校验
      rules: {
        institutionId: [
          { required: true, message: '机构名称不能为空', trigger: 'change' }
        ],
        accountType: [
          { required: true, message: '账户类型不能为空', trigger: 'change' }
        ],
        bankName: [
          { required: true, message: '开户银行不能为空', trigger: 'change' }
        ],
        bankBranch: [
          { required: true, message: '开户支行不能为空', trigger: 'blur' }
        ],
        bankAccount: [
          { required: true, message: '银行账号不能为空', trigger: 'blur' },
          { pattern: /^\d{16,19}$/, message: '请输入正确的银行账号（16-19位数字）', trigger: 'blur' }
        ],
        accountName: [
          { required: true, message: '账户名称不能为空', trigger: 'blur' }
        ],
        openDate: [
          { required: true, message: '开户日期不能为空', trigger: 'change' }
        ],
        contactPerson: [
          { required: true, message: '联系人不能为空', trigger: 'blur' }
        ],
        contactPhone: [
          { required: true, message: '联系电话不能为空', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ]
      },
      approveRules: {
        approveResult: [
          { required: true, message: '审核结果不能为空', trigger: 'change' }
        ],
        approveOpinion: [
          { required: true, message: '审核意见不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getInstitutionList()
  },
  methods: {
    /** 查询账户列表 */
    getList() {
      this.loading = true
      // 模拟数据
      setTimeout(() => {
        this.accountList = [
          {
            id: 1,
            institutionId: 'I10001',
            institutionName: '北京市朝阳区幸福养老院',
            accountType: 'supervision',
            bankAccount: '622202XXXXXXXXXX1234',
            accountName: '北京市朝阳区幸福养老院',
            bankName: '中国工商银行',
            bankBranch: '朝阳支行',
            bankCode: '102100012345',
            accountStatus: '1',
            contractStatus: '1',
            contractDate: '2024-01-20',
            openDate: '2024-01-15',
            contactPerson: '张三',
            contactPhone: '13800138001',
            purpose: '用于存放老人预收费资金，接受民政部门监管',
            createTime: '2024-01-10 10:30:00',
            updateTime: '2024-01-20 14:20:00',
            remark: '监管账户',
            memberBalance: 125000.00
          },
          {
            id: 2,
            institutionId: 'I10002',
            institutionName: '上海市浦东新区康乐养老中心',
            accountType: 'supervision',
            bankAccount: '622202XXXXXXXXXX5678',
            accountName: '上海市浦东新区康乐养老中心',
            bankName: '中国建设银行',
            bankBranch: '浦东支行',
            bankCode: '105100023456',
            accountStatus: '0',
            contractStatus: '0',
            contractDate: null,
            openDate: '2024-02-01',
            contactPerson: '李四',
            contactPhone: '13800138002',
            purpose: '用于存放老人预收费资金，接受民政部门监管',
            createTime: '2024-01-25 15:40:00',
            updateTime: '2024-01-25 15:40:00',
            remark: '待审核',
            memberBalance: 0
          },
          {
            id: 3,
            institutionId: 'I10003',
            institutionName: '广州市天河区爱心养老服务中心',
            accountType: 'basic',
            bankAccount: '622202XXXXXXXXXX9012',
            accountName: '广州市天河区爱心养老服务中心',
            bankName: '中国农业银行',
            bankBranch: '天河支行',
            bankCode: '103100034567',
            accountStatus: '1',
            contractStatus: '1',
            contractDate: '2024-03-15',
            openDate: '2024-03-10',
            contactPerson: '王五',
            contactPhone: '13800138003',
            purpose: '机构日常经营账户',
            createTime: '2024-03-05 09:20:00',
            updateTime: '2024-03-15 11:10:00',
            remark: '基本账户',
            memberBalance: 0
          },
          {
            id: 4,
            institutionId: 'I10004',
            institutionName: '深圳市南山区夕阳红养老院',
            accountType: 'supervision',
            bankAccount: '622202XXXXXXXXXX3456',
            accountName: '深圳市南山区夕阳红养老院',
            bankName: '中国银行',
            bankBranch: '南山支行',
            bankCode: '104100045678',
            accountStatus: '2',
            contractStatus: '1',
            contractDate: '2023-12-10',
            openDate: '2023-12-01',
            contactPerson: '赵六',
            contactPhone: '13800138004',
            purpose: '用于存放老人预收费资金，接受民政部门监管',
            createTime: '2023-11-25 14:15:00',
            updateTime: '2024-10-15 16:30:00',
            remark: '因违规操作已冻结',
            memberBalance: 68000.50
          },
          {
            id: 5,
            institutionId: 'I10005',
            institutionName: '杭州市西湖区福寿康养老中心',
            accountType: 'supervision',
            bankAccount: '622202XXXXXXXXXX7890',
            accountName: '杭州市西湖区福寿康养老中心',
            bankName: '招商银行',
            bankBranch: '西湖支行',
            bankCode: '308100056789',
            accountStatus: '3',
            contractStatus: '1',
            contractDate: '2023-06-15',
            openDate: '2023-06-10',
            contactPerson: '钱七',
            contactPhone: '13800138005',
            purpose: '用于存放老人预收费资金，接受民政部门监管',
            createTime: '2023-06-05 10:20:00',
            updateTime: '2024-09-01 09:30:00',
            remark: '机构已注销，账户已关闭',
            memberBalance: 0
          }
        ]
        this.total = this.accountList.length
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '开通监管账户'
    },
    /** 修改按钮操作 */
    handleEdit(row) {
      this.form = { ...row }
      this.open = true
      this.title = '变更监管账户信息'
    },
    /** 表单重置 */
    reset() {
      this.form = {
        institutionId: null,
        accountType: 'supervision',
        bankName: null,
        bankBranch: null,
        bankAccount: null,
        accountName: null,
        bankCode: null,
        openDate: null,
        contactPerson: null,
        contactPhone: null,
        purpose: null,
        remark: null
      }
      this.resetForm('form')
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          this.$modal.msgSuccess('操作成功，账户信息已提交审核')
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
    /** 审核按钮操作 */
    handleApprove(row) {
      this.approveForm = {
        id: row.id,
        institutionName: row.institutionName,
        bankAccount: row.bankAccount,
        bankName: row.bankName,
        approveResult: '1',
        approveOpinion: ''
      }
      this.approveOpen = true
    },
    /** 提交审核 */
    submitApprove() {
      this.$refs['approveForm'].validate(valid => {
        if (valid) {
          const result = this.approveForm.approveResult === '1' ? '通过' : '驳回'
          this.$modal.msgSuccess('审核' + result + '成功')
          this.approveOpen = false
          this.getList()
        }
      })
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.detailData = { ...row }
      this.detailOpen = true
    },
    /** 更多操作 */
    handleCommand(command, row) {
      switch (command) {
        case 'contract':
          this.$modal.confirm('是否确认为"' + row.institutionName + '"签约三方监管协议？').then(() => {
            this.$modal.msgSuccess('签约成功')
            this.getList()
          })
          break
        case 'freeze':
          this.$modal.confirm('是否确认冻结"' + row.institutionName + '"的监管账户？冻结后该账户将无法进行任何资金操作。').then(() => {
            this.$modal.msgSuccess('冻结成功')
            this.getList()
          })
          break
        case 'unfreeze':
          this.$modal.confirm('是否确认解冻"' + row.institutionName + '"的监管账户？').then(() => {
            this.$modal.msgSuccess('解冻成功')
            this.getList()
          })
          break
        case 'close':
          this.$modal.confirm('是否确认关闭"' + row.institutionName + '"的监管账户？关闭后无法恢复，请谨慎操作！').then(() => {
            this.$modal.msgSuccess('关闭成功')
            this.getList()
          })
          break
        case 'history':
          this.handleHistory(row)
          break
      }
    },
    /** 操作记录 */
    handleHistory(row) {
      this.currentInstitutionName = row.institutionName
      this.currentBankAccount = row.bankAccount
      // 模拟数据
      this.operationHistory = [
        {
          operateTime: '2024-01-20 14:20:00',
          operateType: 'approve',
          operateContent: '审核通过监管账户开通申请',
          operator: '民政局-张三',
          operateResult: '1',
          remark: '资料齐全，符合开通条件'
        },
        {
          operateTime: '2024-01-15 10:30:00',
          operateType: 'open',
          operateContent: '提交监管账户开通申请',
          operator: '机构-李四',
          operateResult: '1',
          remark: '提交开户申请'
        }
      ]
      this.historyOpen = true
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.msgSuccess('导出功能开发中')
    },
    /** 格式化金额 */
    formatAmount(amount) {
      if (!amount && amount !== 0) return '0.00'
      return parseFloat(amount).toFixed(2)
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
