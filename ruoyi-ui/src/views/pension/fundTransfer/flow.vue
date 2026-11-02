<template>
  <div v-loading="loading" class="payout-flow">
    <el-alert v-if="error" :title="error" type="error" :closable="false" show-icon class="mb16" />
    <template v-if="flow.institution">
      <section class="flow-hero">
        <div class="hero-copy">
          <p class="eyebrow">PAYOUT RUNBOOK</p>
          <h1>{{ flow.institution.institutionName }}</h1>
          <p class="hero-sub">开户登记 → 商户绑定 → 银行验证 → 701 签约 → 收单入账 → 生成拨付单 → 审批 → 702 发送 / 703 查单 → 708/709 对账。按顺序完成，任何一步未就绪都不应发送资金。</p>
        </div>
        <div class="gate-strip">
          <div v-for="gate in gateList" :key="gate.label" class="gate" :class="{ on: gate.on }">
            <span>{{ gate.label }}</span>
            <b>{{ gate.on ? '就绪' : '未就绪' }}</b>
          </div>
        </div>
      </section>

      <el-alert v-if="blockedReasons.length && Number(counts.awaitingSend) > 0" type="warning" :closable="false" show-icon class="mb16"
        :title="'有 ' + counts.awaitingSend + ' 笔审批通过待发送，但拨付闸门未就绪：' + blockedReasons.join('、')" />

      <el-row :gutter="16">
        <el-col :xs="24" :lg="16">
          <div class="flow-steps">
            <article v-for="step in steps" :key="step.no" class="flow-step" :class="'is-' + step.state">
              <div class="step-rail"><span class="step-no">{{ step.no }}</span></div>
              <div class="step-card">
                <div class="step-head">
                  <div>
                    <h3>{{ step.title }}</h3>
                    <p>{{ step.desc }}</p>
                  </div>
                  <span class="state-pill" :class="step.state">{{ stateLabel(step.state) }}</span>
                </div>
                <div class="step-body">
                  <span class="step-detail">{{ step.detail }}</span>
                  <span v-for="metric in step.metrics" :key="metric.label" class="metric">
                    <b>{{ metric.value }}</b>{{ metric.label }}
                  </span>
                </div>
                <div v-if="step.link || step.tab" class="step-actions">
                  <el-button v-if="step.link" type="text" size="mini" @click="open(step.link.path)">{{ step.link.text }} <i class="el-icon-arrow-right" /></el-button>
                  <el-button v-if="step.tab" type="text" size="mini" @click="$emit('open-documents')">{{ step.tab }} <i class="el-icon-arrow-right" /></el-button>
                </div>
              </div>
            </article>
          </div>
        </el-col>

        <el-col :xs="24" :lg="8">
          <div class="side-card">
            <div class="side-head">
              <p class="eyebrow">TODAY</p>
              <h2>当前待办</h2>
            </div>
            <div class="todo-grid">
              <div class="todo" :class="{ hot: Number(counts.awaitingApproval) > 0 }">
                <b>{{ counts.awaitingApproval }}</b><span>待审批</span>
              </div>
              <div class="todo" :class="{ hot: Number(counts.awaitingSend) > 0 }">
                <b>{{ counts.awaitingSend }}</b><span>待发送</span>
              </div>
              <div class="todo" :class="{ hot: Number(counts.processing) > 0 }">
                <b>{{ counts.processing }}</b><span>银行处理中</span>
              </div>
              <div class="todo" :class="{ hot: Number(counts.unresolvedTransfers) > 0 }">
                <b>{{ counts.unresolvedTransfers }}</b><span>未决交易</span>
              </div>
            </div>
            <el-button class="todo-action" type="primary" plain size="mini" @click="$emit('open-documents')">进入拨付单据</el-button>
          </div>

          <div class="side-card">
            <div class="side-head">
              <p class="eyebrow">FLOW CHECK</p>
              <h2>流程对照结论</h2>
            </div>
            <ul class="check-list">
              <li v-for="item in fixes" :key="item"><i class="el-icon-circle-check" />{{ item }}</li>
            </ul>
          </div>

          <div class="side-card">
            <div class="side-head">
              <p class="eyebrow">MANUAL</p>
              <h2>仍需人工确认</h2>
            </div>
            <ul class="manual-list">
              <li v-for="item in manualNotes" :key="item"><i class="el-icon-warning-outline" />{{ item }}</li>
            </ul>
          </div>
        </el-col>
      </el-row>
    </template>
    <el-empty v-else-if="!loading && !error" description="请选择养老机构查看拨付流程" />
  </div>
</template>

<script>
import { getPayoutFlow } from '@/api/pension/bank'

const STATE_LABELS = { done: '已完成', pending: '待完成', blocked: '前置未完成', attention: '待处理', active: '处理中', info: '持续核对' }

export default {
  name: 'PayoutFlow',
  props: { institutionId: { type: [Number, String], default: null } },
  data() {
    return { loading: false, error: '', flow: {} }
  },
  computed: {
    counts() {
      return { ...(this.flow.transfers || {}), ...(this.flow.transactions || {}) }
    },
    gateList() {
      const gates = this.flow.gates || {}
      return [
        { label: '平台拨付开关', on: !!gates.platformSwitch },
        { label: '监管协议验收', on: !!gates.protocolConfirmed },
        { label: '商户验证与启用', on: !!gates.merchantReady },
        { label: '机构拨付开关', on: !!gates.merchantPayoutEnabled }
      ]
    },
    blockedReasons() {
      const gates = this.flow.gates || {}
      const reasons = []
      if (!gates.platformSwitch) reasons.push('平台拨付总开关关闭')
      if (!gates.protocolConfirmed) reasons.push('监管协议未验收')
      if (!gates.merchantReady) reasons.push('商户未验证或未启用')
      if (!gates.merchantPayoutEnabled) reasons.push('机构拨付开关未开启')
      if (!gates.agreementSigned) reasons.push('监管签约编号缺失')
      return reasons
    },
    fixes() {
      return [
        '月度自动拨付增加到期校验：未到划拨日期不能手动提前发送，失败重试和手动申请除外。',
        '流程页集中展示平台开关、协议验收、商户状态和机构拨付开关，发送前即可判断闸门是否齐全。',
        '“流程总览”和“拨付单据”分视图，历史本地记录与新银行拨付分别标注，不再混用同一语义。',
        '账户或银行绑定变更会使原验证、签约和拨付开关失效，必须重新验证签约后才能发送。',
        '处理中或超时只查询原请求；仅银行明确失败且本地已记账才允许新建重试。',
        '退汇自动冲正预占并回写拨付单，避免银行资金与平台余额、单据状态不一致。'
      ]
    },
    manualNotes() {
      return [
        '自动发送任务保持暂停，由操作员核对闸门和单据后逐笔发送。',
        '708 银行明细与 709 可用余额需按日人工对账，不以平台内部余额代替银行结果。',
        '真实拨付需银行测试商户、监管协议和网络条件全部验收后再开闸。'
      ]
    },
    steps() {
      const inst = this.flow.institution || {}
      const merchant = this.flow.merchant || {}
      const gates = this.flow.gates || {}
      const n = key => Number((this.flow.transfers || {})[key] || 0)
      const t = key => Number((this.flow.transactions || {})[key] || 0)
      const verified = merchant.verifyStatus === '1'
      const enabled = merchant.status === '1'
      const accountReady = !!inst.accountConfigured
      const paidSuccess = t('successfulPayments')
      const paidPending = t('pendingPayments')
      const eligible = n('bankEligibleTotal')
      const awaitingApproval = n('awaitingApproval')
      const awaitingSend = n('awaitingSend')
      const processing = n('processing')
      const completed = n('completed')
      const returned = n('returned')
      const unresolved = t('unresolvedTransfers')

      return [
        {
          no: '01', title: '开立监管账户与基本账户',
          desc: '在机构账户管理中登记监管专户和基本账户的真实户名、账号与开户行，相当于银行开户。',
          state: accountReady ? 'done' : 'blocked',
          detail: accountReady
            ? '监管户 ' + (inst.superviseAccount || '-') + ' · ' + (inst.superviseBank || '开户行未填') + '；基本户 ' + (inst.bankAccount || '-') + ' · ' + (inst.basicBank || '开户行未填')
            : '监管账户或基本账户尚未登记完整，后续商户绑定和签约无法开始。',
          link: { text: '账户管理', path: '/supervision/account/institutionAccount' }
        },
        {
          no: '02', title: '绑定银行商户号',
          desc: '登记 merId、监管账户户名、基本账户户名、跨行联行号和签约编号，并设为默认绑定。',
          state: merchant.configured ? 'done' : accountReady ? 'pending' : 'blocked',
          detail: merchant.configured
            ? 'merId ' + (merchant.merId || '-') + ' · ' + (merchant.environment === 'prod' ? '生产环境' : '测试环境') + ' · 默认绑定 ' + (merchant.isDefault === '1' ? '是' : '否')
            : '尚未配置商户绑定，无法进行银行验证和拨付。',
          metrics: merchant.configured ? [{ label: '商户状态', value: enabled ? '启用' : '未启用' }] : [],
          link: { text: '银行商户配置', path: '/supervision/account/supervisionAccount' }
        },
        {
          no: '03', title: '银行环境验证',
          desc: '保存绑定后由后端向银行发起环境验证，确认商户号、账户和联行号在目标环境可用。',
          state: verified ? 'done' : merchant.configured ? 'pending' : 'blocked',
          detail: verified
            ? '银行验证已通过' + (merchant.lastVerifiedTime ? ' · ' + merchant.lastVerifiedTime : '')
            : (merchant.verifyMessage || '保存配置后自动发起验证；验证未通过时不能启用。'),
          link: { text: '查看验证状态', path: '/supervision/account/supervisionAccount' }
        },
        {
          no: '04', title: '监管协议签约（701）',
          desc: '监管户户名与基本户户名一致时发起 701 签约，签约编号回写后拨付才具备协议基础。',
          state: merchant.agreementSigned ? 'done' : verified ? 'pending' : 'blocked',
          detail: merchant.agreementSigned
            ? '签约编号 ' + (merchant.agreementNo || '-') + (merchant.payoutEnabled === 1 ? ' · 机构拨付开关已开启' : ' · 机构拨付开关未开启')
            : '尚未取得监管签约编号，不能开启机构拨付开关。',
          link: { text: '去签约/查看', path: '/supervision/account/supervisionAccount' }
        },
        {
          no: '05', title: '开启拨付闸门',
          desc: '平台总开关、监管协议验收、商户验证启用和机构拨付开关必须同时就绪。',
          state: gates.canSubmit ? 'done' : (gates.merchantReady && gates.agreementSigned ? 'pending' : 'blocked'),
          detail: blockedReasons.length ? '未就绪：' + blockedReasons.join('、') : '全部闸门已就绪，可对已审批单据发起 702。',
          link: { text: '检查商户配置', path: '/supervision/account/supervisionAccount' }
        },
        {
          no: '06', title: '收单入账',
          desc: '老人通过银行支付成功后，资金进入监管专户，系统据此形成可拨付额度；本地历史完成记录不算银行到账。',
          state: paidSuccess > 0 ? 'done' : paidPending > 0 ? 'active' : 'pending',
          detail: paidSuccess > 0
            ? '已有 ' + paidSuccess + ' 笔银行支付成功，可生成对应拨付单。'
            : '等待银行支付成功；支付未确认前不会生成可拨付额度。',
          metrics: [{ label: '成功支付', value: paidSuccess }, { label: '待确认支付', value: paidPending }],
          link: { text: '订单管理', path: '/supervision/account/orderList' }
        },
        {
          no: '07', title: '生成拨付单',
          desc: '支付成功后生成首月拨付单，后续月份按规则生成；只有 bankEligible=1 的单据能进入银行拨付。',
          state: eligible > 0 ? 'done' : paidSuccess > 0 ? 'pending' : 'blocked',
          detail: eligible > 0
            ? '当前有 ' + eligible + ' 笔可拨付单据，其中 ' + awaitingApproval + ' 笔待审批。'
            : '尚未生成银行可拨付单据，旧历史单据不会自动补发。',
          metrics: [{ label: '可拨付单', value: eligible }, { label: '待审批', value: awaitingApproval }],
          tab: '查看拨付单据'
        },
        {
          no: '08', title: '审批拨付单',
          desc: '审批只把单据置为可发送，不扣款；未审批的单据不能向银行发送。',
          state: awaitingApproval > 0 ? 'attention' : eligible > 0 ? 'done' : 'pending',
          detail: awaitingApproval > 0
            ? '有 ' + awaitingApproval + ' 笔待审批，请在“拨付单据”中逐笔审批。'
            : '没有待审批单据。',
          metrics: [{ label: '待审批', value: awaitingApproval }],
          tab: '去审批'
        },
        {
          no: '09', title: '发送与查单（702/703）',
          desc: '发送前先查 709 可用余额并预占账户余额；702 受理不代表成功，最终状态以 703 查询为准。',
          state: (awaitingSend > 0 || unresolved > 0) ? 'attention' : processing > 0 ? 'active' : (completed + returned > 0 ? 'done' : 'pending'),
          detail: awaitingSend > 0
            ? '有 ' + awaitingSend + ' 笔审批通过待发送' + (gates.canSubmit ? '，可逐笔立即发送。' : '，但拨付闸门未就绪。')
            : '处理中或超时只能查询原请求；银行明确失败且本地记账后才允许重试。',
          metrics: [
            { label: '待发送', value: awaitingSend },
            { label: '处理中', value: processing },
            { label: '未决交易', value: unresolved }
          ],
          tab: '去发送/查单'
        },
        {
          no: '10', title: '对账与退汇（708/709/707）',
          desc: '按日期核对监管账户银行流水和可用余额；收到退汇通知时冲正预占并回写拨付单。',
          state: completed > 0 || returned > 0 ? 'info' : 'pending',
          detail: '成功 ' + completed + ' 笔 · 退汇 ' + returned + ' 笔 · 历史已完成（未接入银行）' + n('legacyCompleted') + ' 笔；历史记录不会自动补发。',
          metrics: [{ label: '拨付成功', value: completed }, { label: '已退汇', value: returned }],
          link: { text: '银行明细查询', path: '/pension/bank/detail' }
        }
      ]
    }
  },
  watch: {
    institutionId: { immediate: true, handler() { this.load() } }
  },
  methods: {
    load() {
      this.flow = {}
      this.error = ''
      if (!this.institutionId) return
      this.loading = true
      getPayoutFlow(this.institutionId).then(response => {
        this.flow = response.data || {}
      }).catch(error => {
        this.error = (error && error.message) || '拨付流程加载失败'
      }).finally(() => { this.loading = false })
    },
    stateLabel(state) { return STATE_LABELS[state] || state },
    open(path) { this.$router.push(path) }
  }
}
</script>

<style scoped>
.payout-flow { color: #14273f; }
.mb16 { margin-bottom: 16px; }
.flow-hero { display: flex; flex-wrap: wrap; gap: 20px; align-items: flex-end; justify-content: space-between; padding: 22px 24px; margin-bottom: 16px; background: linear-gradient(135deg, #ffffff 0%, #eef4fb 100%); border: 1px solid #e3eaf3; border-radius: 14px; }
.hero-copy { max-width: 720px; }
.eyebrow { margin: 0 0 6px; color: #1f65bd; font-size: 11px; font-weight: 700; letter-spacing: .18em; }
.flow-hero h1 { margin: 0 0 8px; font-family: "Songti SC", "STSong", serif; font-size: 28px; line-height: 1.2; }
.hero-sub { margin: 0; color: #5b6b80; font-size: 13px; line-height: 1.8; }
.gate-strip { display: grid; grid-template-columns: repeat(2, minmax(150px, 1fr)); gap: 8px; }
.gate { padding: 9px 12px; background: #fff; border: 1px solid #e3eaf3; border-radius: 10px; }
.gate span { display: block; color: #7a889b; font-size: 12px; }
.gate b { color: #c53d46; font-size: 13px; }
.gate.on b { color: #18815d; }
.flow-steps { position: relative; }
.flow-step { display: flex; gap: 14px; }
.step-rail { position: relative; display: flex; flex-direction: column; align-items: center; width: 34px; flex: none; }
.step-rail::after { content: ""; flex: 1; width: 2px; background: #dce6f2; margin: 4px 0 -12px; }
.flow-step:last-child .step-rail::after { display: none; }
.step-no { display: flex; align-items: center; justify-content: center; width: 34px; height: 34px; border-radius: 50%; background: #eaf1fa; color: #1f65bd; font-weight: 700; font-size: 12px; letter-spacing: .04em; }
.flow-step.is-done .step-no { background: #e4f4ed; color: #18815d; }
.flow-step.is-blocked .step-no { background: #fbeaec; color: #c53d46; }
.flow-step.is-attention .step-no { background: #fdf1e0; color: #b86c08; }
.step-card { flex: 1; margin-bottom: 12px; padding: 16px 18px; background: #fff; border: 1px solid #e3eaf3; border-radius: 12px; box-shadow: 0 6px 18px rgba(20, 39, 63, .04); }
.step-head { display: flex; gap: 12px; align-items: flex-start; justify-content: space-between; }
.step-head h3 { margin: 0 0 5px; font-size: 15px; line-height: 1.4; }
.step-head p { margin: 0; color: #5b6b80; font-size: 12.5px; line-height: 1.7; }
.state-pill { flex: none; padding: 3px 10px; border-radius: 20px; background: #eef2f7; color: #637083; font-size: 12px; }
.state-pill.done { background: #e4f4ed; color: #18815d; }
.state-pill.blocked { background: #fbeaec; color: #c53d46; }
.state-pill.attention { background: #fdf1e0; color: #b86c08; }
.state-pill.active { background: #e6effa; color: #1f65bd; }
.state-pill.info { background: #eef4fb; color: #1f65bd; }
.step-body { display: flex; flex-wrap: wrap; gap: 8px 16px; align-items: center; margin-top: 10px; }
.step-detail { flex: 1 1 100%; color: #7a889b; font-size: 12.5px; line-height: 1.7; }
.metric { color: #7a889b; font-size: 12px; }
.metric b { margin-right: 4px; color: #14273f; font-size: 14px; }
.step-actions { margin-top: 4px; }
.side-card { margin-bottom: 16px; padding: 18px; background: #fff; border: 1px solid #e3eaf3; border-radius: 14px; box-shadow: 0 6px 18px rgba(20, 39, 63, .04); }
.side-head h2 { margin: 0 0 12px; font-family: "Songti SC", "STSong", serif; font-size: 18px; }
.todo-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; }
.todo { padding: 12px; background: #f7fafd; border: 1px solid #eef3f9; border-radius: 10px; }
.todo b { display: block; font-size: 24px; line-height: 1.2; }
.todo span { color: #7a889b; font-size: 12px; }
.todo.hot { background: #fdf7ee; border-color: #f3e3c8; }
.todo.hot b { color: #b86c08; }
.todo-action { width: 100%; margin-top: 12px; }
.check-list, .manual-list { margin: 0; padding: 0; list-style: none; }
.check-list li, .manual-list li { display: flex; gap: 8px; padding: 6px 0; color: #5b6b80; font-size: 12.5px; line-height: 1.65; }
.check-list i { color: #18815d; margin-top: 3px; }
.manual-list i { color: #b86c08; margin-top: 3px; }
@media (max-width: 768px) {
  .flow-hero { padding: 18px; }
  .gate-strip { grid-template-columns: 1fr; width: 100%; }
  .step-card { padding: 14px; }
}
</style>
