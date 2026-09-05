// Run: node src/utils/payout.test.cjs (from ruoyi-ui).
const assert = require('assert')
const path = require('path')
const vm = require('vm')
const babel = require('@babel/core')
const filename = path.join(__dirname, 'payout.js')
const transformed = babel.transformFileSync(filename, {
  babelrc: false,
  configFile: false,
  plugins: ['@babel/plugin-transform-modules-commonjs']
})
const context = { exports: {} }
vm.runInNewContext(transformed.code, context)
const { payoutStatus, canApprovePayout, canRetryPayout } = context.exports

assert.strictEqual(payoutStatus({ status: 'completed' }), '历史记录（未接入银行）')
assert.strictEqual(payoutStatus({ bankEligible: 1, status: 'completed' }), '拨付成功')
assert.strictEqual(payoutStatus({ bankEligible: 1, status: 'processing', manualReview: 1 }), '待人工核查')
assert.strictEqual(payoutStatus({ bankEligible: 1, status: 'returned' }), '已退汇')
assert.strictEqual(payoutStatus({ bankEligible: 1, status: 'unexpected' }), '状态待核实')
assert.strictEqual(canApprovePayout({ bankEligible: 0, status: 'pending' }), false)
assert.strictEqual(canApprovePayout({ bankEligible: 1, status: 'pending' }), true)
assert.strictEqual(canApprovePayout({ bankEligible: 1, status: 'pending', approveTime: '2026-09-05' }), false)
for (const status of ['pending', 'processing', 'completed', 'returned', 'cancelled']) {
  assert.strictEqual(canRetryPayout({ bankEligible: 1, status, bankTransactionId: 10 }), false)
}
assert.strictEqual(canRetryPayout({ bankEligible: 0, status: 'failed', bankTransactionId: 10 }), false)
assert.strictEqual(canRetryPayout({ bankEligible: 1, status: 'failed' }), false)
assert.strictEqual(canRetryPayout({ bankEligible: 1, status: 'failed', bankTransactionId: 10 }), true)
console.log('payout UI status and action guards passed')

const fs = require('fs')
const compiler = require('vue-template-compiler')
function deferred() {
  let resolve
  const promise = new Promise(done => { resolve = done })
  return { promise, resolve }
}
function loadPage(file, api) {
  const script = compiler.parseComponent(fs.readFileSync(path.join(__dirname, '..', 'views', file), 'utf8')).script.content
  const code = babel.transformSync(script, { babelrc: false, configFile: false, plugins: ['@babel/plugin-transform-modules-commonjs'] }).code
  const page = { exports: {}, require: () => api }
  vm.runInNewContext(code, page)
  const component = page.exports.default
  const state = component.data()
  Object.entries(component.methods).forEach(([name, method]) => { if (method) state[name] = method.bind(state) })
  return state
}
async function flush() { for (let i = 0; i < 5; i++) await Promise.resolve() }

async function checkDetailRaces(file) {
  const requests = []
  const api = { listFundTransfer: params => { const request = deferred(); requests.push({ ...request, params }); return request.promise } }
  const state = loadPage(file, api)
  state.detailData = { applyId: 1 }
  state.loadTransferDetails()
  state.transferQuery.pageNum = 2
  state.loadTransferDetails()
  requests[0].resolve({ rows: [{ transferId: 10 }], total: 10 })
  await flush()
  assert.strictEqual(state.transferLoading, true, file + ': stale finally must not finish the current request')
  requests[1].resolve({ rows: [{ transferId: 20 }], total: 20 })
  await flush()
  assert.strictEqual(state.transferDetails[0].transferId, 20)
  assert.strictEqual(state.transferTotal, 20)
  assert.strictEqual(state.transferLoading, false)

  state.loadTransferDetails()
  state.transferQuery.pageNum = 3
  state.loadTransferDetails()
  requests[3].resolve({ rows: [{ transferId: 30 }], total: 30 })
  await flush()
  requests[2].resolve({ rows: [{ transferId: 21 }], total: 21 })
  await flush()
  assert.strictEqual(state.transferDetails[0].transferId, 30, file + ': older page must not overwrite newer page')

  const applications = { 1: deferred(), 2: deferred() }
  api.getFundTransferApply = id => applications[id].promise
  state.handleView({ applyId: 1 })
  state.handleView({ applyId: 2 })
  applications[2].resolve({ data: { applyId: 2 } })
  await flush()
  applications[1].resolve({ data: { applyId: 1 } })
  await flush()
  assert.strictEqual(state.detailData.applyId, 2, file + ': old application must not replace current application')
  assert.strictEqual(requests[requests.length - 1].params.applyId, 2)

  const pendingB = requests[requests.length - 1]
  const applicationC = deferred()
  api.getFundTransferApply = () => applicationC.promise
  state.handleView({ applyId: 3 })
  pendingB.resolve({ rows: [{ transferId: 200 }], total: 200 })
  await flush()
  assert.strictEqual(state.transferDetails.length, 0, file + ': old detail response must be invalidated immediately on switching')
}

async function checkMerchantFields() {
  const writes = []
  const errors = []
  const state = loadPage('supervision/account/supervisionAccount.vue', {
    addBankMerchant: form => { writes.push({ ...form }); return Promise.resolve() }
  })
  state.resetForm = () => {}
  state.$refs = { form: { validate: callback => callback(true) } }
  state.$modal = { msgError: message => errors.push(message), msgSuccess: () => {} }
  state.getList = () => {}
  state.reset()
  state.institutionList = [{ institutionId: 1, institutionName: '机构名称不是账户户名' }]
  state.handleInstitutionChange(1)
  assert.strictEqual(state.form.settlementAccountName, '', 'institution name must not synthesize bank account name')
  Object.assign(state.form, { institutionId: 1, payoutEnabled: 1, basicAccountName: '基本户名', supervisionAgreementNo: '协议编号' })
  state.submitForm()
  assert.strictEqual(writes.length, 0, 'payout enable must require real settlement account name')
  assert.strictEqual(errors.length, 1)
  state.form.settlementAccountName = '银行登记监管户名'
  state.submitForm()
  await flush()
  assert.strictEqual(writes.length, 1)
  assert.strictEqual(writes[0].settlementAccountName, '银行登记监管户名')
  const template = fs.readFileSync(path.join(__dirname, '..', 'views/supervision/account/supervisionAccount.vue'), 'utf8')
  assert(template.includes('v-model="form.institutionId" :disabled="form.configId != null"'), 'existing merchant institution cannot be moved')
}

Promise.all([
  checkDetailRaces('pension/fund/applyList.vue'),
  checkDetailRaces('supervision/fund/transferApprove.vue'),
  checkMerchantFields()
]).then(() => console.log('application/pagination response guards and merchant account-name validation passed')).catch(error => { console.error(error); process.exitCode = 1 })
