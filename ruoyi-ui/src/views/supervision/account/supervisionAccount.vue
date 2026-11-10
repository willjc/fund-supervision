<template>
  <div class="app-container">
    <el-alert title="银行商户配置" type="info" :closable="false" show-icon class="mb16">
      <div slot="description">
        每家养老机构可配置多个 merId；结算账户固定取“监管账户管理”中的监管账户，划拨目标固定取基本账户。
        新配置默认停用，待郑州银行测试环境验证通过后才能启用。拨付需另行登记银行已办理的监管签约资料；登记或启用机构开关不代表银行协议已验收，真实拨付仍受服务端总开关及协议确认控制。
      </div>
    </el-alert>

    <el-collapse v-model="guideOpen" class="mb16">
      <el-collapse-item name="guide" title="测试配置四步">
        <ol class="guide-list">
          <li><b>核对机构账户</b>：在“维护机构账户”确认监管账户、基本账户、开户行及银行登记户名已维护；户名必须与银行真实登记一致，不能使用机构名称自动合成。</li>
          <li><b>登记商户号</b>：新增商户号，填写银行分配的 merId；账号、开户行、户名自动从机构档案带出，无需手填。跨行拨付必须填写收款行联行号。银行已办理过签约的，同时补录原监管协议编号。</li>
          <li><b>银行验证并签约</b>：点击“验证”检查商户配置；尚未取得协议编号的配置可点击“发起签约”发起 701 监管签约。若银行返回“监管账户已签约”（mzcnck008），说明银行侧协议已存在，本系统不会重复签约，请向银行核实原协议编号后补录。验证状态为“已验证”后才能启用。</li>
          <li><b>开启机构拨付开关</b>：编辑配置将“启用状态”和“机构拨付开关”设为开启。随后到“拨付管理”对已审批的待拨付单点击“立即发送”（702），再用“查询银行”（703）确认结果。</li>
        </ol>
        <div class="form-tip">联调前请确认服务端已开启拨付总开关；页面开关只控制单个机构，不能替代银行协议验收。</div>
      </el-collapse-item>
    </el-collapse>

    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="90px">
      <el-form-item label="机构名称" prop="institutionName">
        <el-input v-model="queryParams.institutionName" placeholder="请输入机构名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="银行商户号" prop="merId">
        <el-input v-model="queryParams.merId" placeholder="请输入 merId" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="环境" prop="environment">
        <el-select v-model="queryParams.environment" placeholder="全部" clearable>
          <el-option label="测试环境" value="sandbox" />
          <el-option label="生产环境" value="prod" />
        </el-select>
      </el-form-item>
      <el-form-item label="启用状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="停用" value="0" />
          <el-option label="启用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" v-hasPermi="['supervision:account:supervision']" @click="handleAdd">新增商户号</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button plain icon="el-icon-bank-card" size="mini" @click="$router.push('/supervision/account/institutionAccount')">维护机构账户</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="merchantList" border>
      <el-table-column label="机构" prop="institutionName" min-width="190" show-overflow-tooltip />
      <el-table-column label="merId" prop="merId" min-width="160" show-overflow-tooltip />
      <el-table-column label="银行商户名称" prop="merchantName" min-width="180" show-overflow-tooltip />
      <el-table-column label="监管结算账户" min-width="170">
        <template slot-scope="scope">{{ maskAccount(scope.row.settlementAccountNo) }}</template>
      </el-table-column>
      <el-table-column label="基本账户" min-width="170">
        <template slot-scope="scope">{{ maskAccount(scope.row.basicAccountNo) }}</template>
      </el-table-column>
      <el-table-column label="接入渠道" prop="channelType" width="100" align="center" />
      <el-table-column label="默认" width="70" align="center">
        <template slot-scope="scope"><el-tag v-if="scope.row.isDefault === '1'" type="primary" size="small">默认</el-tag></template>
      </el-table-column>
      <el-table-column label="环境" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.environment === 'prod' ? 'danger' : 'warning'" size="small">
            {{ scope.row.environment === 'prod' ? '生产' : '测试' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="银行验证" width="100" align="center">
        <template slot-scope="scope">
          <el-tooltip :content="scope.row.verifyMessage || '尚未验证'" placement="top">
            <el-tag v-if="scope.row.verifyStatus === '1'" type="success" size="small">已验证</el-tag>
            <el-tag v-else-if="scope.row.verifyStatus === '2'" type="danger" size="small">失败</el-tag>
            <el-tag v-else type="info" size="small">待验证</el-tag>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="启用状态" width="90" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'info'" size="small">
            {{ scope.row.status === '1' ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="机构拨付开关" width="115" align="center">
        <template slot-scope="scope">{{ Number(scope.row.payoutEnabled) === 1 ? '机构已开启' : '关闭' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right" align="center">
        <template slot-scope="scope">
          <el-button type="text" size="mini" icon="el-icon-connection" v-hasPermi="['supervision:account:supervision']" @click="handleVerify(scope.row)">验证</el-button>
          <el-button v-if="!scope.row.supervisionAgreementNo" type="text" size="mini" icon="el-icon-link" v-hasPermi="['supervision:account:supervision']" @click="handleSign(scope.row)">发起签约</el-button>
          <el-button type="text" size="mini" icon="el-icon-edit" v-hasPermi="['supervision:account:supervision']" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" size="mini" icon="el-icon-delete" v-hasPermi="['supervision:account:supervision']" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="680px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="130px">
        <el-form-item label="养老机构" prop="institutionId">
          <el-select v-model="form.institutionId" :disabled="form.configId != null" filterable placeholder="请选择机构" style="width: 100%" @change="handleInstitutionChange">
            <el-option v-for="item in institutionList" :key="item.institutionId" :label="item.institutionName" :value="item.institutionId" />
          </el-select>
        </el-form-item>
        <el-form-item label="银行商户号" prop="merId">
          <el-input v-model.trim="form.merId" placeholder="请输入郑州银行分配的 merId" maxlength="64" />
        </el-form-item>
        <el-form-item label="银行商户名称" prop="merchantName">
          <el-input v-model.trim="form.merchantName" placeholder="默认使用机构名称" maxlength="200" />
        </el-form-item>
        <el-form-item label="监管账户">
          <el-input :value="selectedInstitution.superviseAccount || '机构档案尚未配置'" disabled />
        </el-form-item>
        <el-form-item label="监管开户行">
          <el-input :value="selectedInstitution.superviseBank || '机构档案尚未配置'" disabled />
        </el-form-item>
        <el-form-item label="基本账户">
          <el-input :value="selectedInstitution.bankAccount || '机构档案尚未配置'" disabled />
        </el-form-item>
        <el-form-item label="基本开户行">
          <el-input :value="selectedInstitution.basicBank || '机构档案尚未配置'" disabled />
        </el-form-item>
        <el-form-item label="银行登记户名">
          <el-input :value="selectedInstitution.accountHolderName || '机构档案尚未配置'" disabled />
          <div class="form-tip">监管账户与基本账户共用，自动从机构档案带出；如需修改请到「维护机构账户」。</div>
        </el-form-item>
        <el-form-item label="跨行拨付" prop="crossBank">
          <el-radio-group v-model="form.crossBank"><el-radio :label="0">本行</el-radio><el-radio :label="1">跨行</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="收款行联行号" prop="basicBankCode">
          <el-input v-model.trim="form.basicBankCode" maxlength="64" placeholder="跨行拨付必填，按银行资料登记" />
        </el-form-item>
        <el-form-item label="监管协议编号" prop="supervisionAgreementNo">
          <el-input v-model.trim="form.supervisionAgreementNo" maxlength="64" placeholder="签约成功时由银行返回，也可补录银行已办理的原编号" />
          <div class="form-tip">若银行提示已签约但此处为空，请向银行核实原协议编号后手工补录，不要重复发起签约。</div>
        </el-form-item>
        <el-form-item label="机构拨付开关" prop="payoutEnabled">
          <el-radio-group v-model="form.payoutEnabled"><el-radio :label="0">关闭</el-radio><el-radio :label="1" :disabled="form.verifyStatus !== '1'">开启</el-radio></el-radio-group>
          <div class="form-tip">默认关闭。开启前须银行验证通过，且银行登记户名、监管协议编号齐全；更改账户、户名或跨行信息会重置验证与签约状态。</div>
        </el-form-item>
        <el-form-item label="接入渠道" prop="channelType">
          <el-select v-model="form.channelType" style="width: 100%">
            <el-option label="普通 H5" value="H5" />
            <el-option label="微信小程序" value="WX_MINI" />
            <el-option label="支付宝小程序" value="ALI_MINI" />
          </el-select>
        </el-form-item>
        <el-form-item label="银行环境" prop="environment">
          <el-radio-group v-model="form.environment">
            <el-radio label="sandbox">测试环境</el-radio>
            <el-radio label="prod">生产环境</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="启用状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">停用</el-radio>
            <el-radio label="1" :disabled="form.verifyStatus !== '1'">启用</el-radio>
          </el-radio-group>
          <div class="form-tip">只有银行验证状态为“已验证”的配置才能启用。</div>
        </el-form-item>
        <el-form-item label="默认商户号" prop="isDefault">
          <el-radio-group v-model="form.isDefault">
            <el-radio label="0">否</el-radio>
            <el-radio label="1">是</el-radio>
          </el-radio-group>
          <div class="form-tip">同一机构只有一个默认商户号，支付未指定 merId 时使用默认项。</div>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可填写银行申请批次、联系人等非敏感信息" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInstitution } from '@/api/supervision/institution'
import { listBankMerchant, getBankMerchant, addBankMerchant, updateBankMerchant, delBankMerchant, verifyBankMerchant, signBankMerchantContract } from '@/api/supervision/account'

export default {
  name: 'SupervisionAccount',
  data() {
    return {
      loading: false,
      total: 0,
      merchantList: [],
      institutionList: [],
      selectedInstitution: {},
      guideOpen: [],
      open: false,
      title: '',
      queryParams: { pageNum: 1, pageSize: 10, institutionName: null, merId: null, environment: null, status: null },
      form: {},
      rules: {
        institutionId: [{ required: true, message: '请选择养老机构', trigger: 'change' }],
        merId: [{ required: true, message: '请输入银行商户号', trigger: 'blur' }],
        channelType: [{ required: true, message: '请选择接入渠道', trigger: 'change' }],
        environment: [{ required: true, message: '请选择银行环境', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
    this.getInstitutionList()
  },
  // keep-alive 缓存的标签页切回时 created 不重跑，需在此重新拉取，避免机构账户停留在打开瞬间的旧快照
  activated() {
    this.getList()
    this.getInstitutionList()
  },
  methods: {
    getList() {
      this.loading = true
      listBankMerchant(this.queryParams).then(response => {
        this.merchantList = response.rows || []
        this.total = response.total || 0
      }).finally(() => { this.loading = false })
    },
    getInstitutionList() {
      return listInstitution({ pageNum: 1, pageSize: 1000 }).then(response => { this.institutionList = response.rows || [] })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    reset() {
      this.form = { configId: null, institutionId: null, merId: '', merchantName: '', channelType: 'H5', environment: 'sandbox', verifyStatus: '0', isDefault: '0', status: '0', settlementAccountName: '', basicAccountName: '', basicBankCode: '', crossBank: 0, supervisionAgreementNo: '', payoutEnabled: 0, remark: '' }
      this.selectedInstitution = {}
      this.resetForm('form')
    },
    handleAdd() {
      this.reset()
      this.getInstitutionList()
      this.title = '新增郑州银行商户号'
      this.open = true
    },
    handleEdit(row) {
      this.reset()
      Promise.all([getBankMerchant(row.configId), this.getInstitutionList()]).then(([response]) => {
        this.form = { ...this.form, ...response.data }
        this.form.crossBank = Number(this.form.crossBank) === 1 ? 1 : 0
        this.form.payoutEnabled = Number(this.form.payoutEnabled) === 1 ? 1 : 0
        this.handleInstitutionChange(this.form.institutionId)
        this.title = '编辑郑州银行商户号'
        this.open = true
      })
    },
    handleInstitutionChange(institutionId) {
      this.selectedInstitution = this.institutionList.find(item => item.institutionId === institutionId) || {}
      if (!this.form.merchantName && this.selectedInstitution.institutionName) this.form.merchantName = this.selectedInstitution.institutionName
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (Number(this.form.payoutEnabled) === 1) {
          const missing = []
          if (!this.selectedInstitution.accountHolderName) missing.push('银行登记户名（到维护机构账户填写）')
          if (!this.form.supervisionAgreementNo) missing.push('监管协议编号')
          if (Number(this.form.crossBank) === 1 && !this.form.basicBankCode) missing.push('收款行联行号')
          if (missing.length) {
            this.$modal.msgError('启用机构拨付前请先填写：' + missing.join('、'))
            return
          }
        }
        const request = this.form.configId ? updateBankMerchant(this.form) : addBankMerchant(this.form)
        request.then(response => {
          this.$modal.msgSuccess((response && response.msg) || (this.form.configId ? '修改成功' : '新增成功'))
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除商户号“' + row.merId + '”的绑定吗？').then(() => delBankMerchant(row.configId)).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleVerify(row) {
      this.$modal.confirm('确认在当前银行环境验证商户号“' + row.merId + '”吗？').then(() => verifyBankMerchant(row.configId)).then(() => {
        this.$modal.msgSuccess('商户号验证通过')
        this.getList()
      }).catch(() => {})
    },
    handleSign(row) {
      this.$modal.confirm('确认向郑州银行发起监管签约（ylcntr）吗？同一监管账户银行只允许签约一次，重复发起会被拒绝；若银行此前已签约，请取消后直接补录原协议编号').then(() => signBankMerchantContract(row.configId)).then(response => {
        this.$modal.msgSuccess((response && response.msg) || '监管签约完成')
        this.getList()
      }).catch(() => {})
    },
    cancel() {
      this.open = false
      this.reset()
    },
    maskAccount(account) {
      if (!account) return '-'
      if (account.length <= 8) return account
      return account.slice(0, 4) + ' **** **** ' + account.slice(-4)
    }
  }
}
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
.mb8 { margin-bottom: 8px; }
.form-tip { color: #909399; font-size: 12px; margin-top: 4px; }
.guide-list { margin: 0; padding-left: 20px; line-height: 2; color: #606266; }
</style>
