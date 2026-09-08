// Run: node ruoyi-h5/tests/order-confirm-money.cjs
const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')
const vm = require('node:vm')
const source = fs.readFileSync(path.join(__dirname, '../src/views/order/confirm.vue'), 'utf8')
const format = fs.readFileSync(path.join(__dirname, '../src/utils/format.js'), 'utf8')
const calculation = source.slice(source.indexOf('const monthlyPrice ='), source.indexOf('// 获取老人列表'))
const formatFunction = format.match(/export function formatMoney[\s\S]*?\n}/)[0].replace('export ', '')
for (const [fee, deposit, member, months, monthly, total] of [
  [0.1, 0, 0, 1, '0.30', '0.30'],
  [0.01, 0.02, 0.02, 2, '0.03', '0.10'],
  ['0.10', '0.20', '0.20', 2, '0.30', '1.00'],
  [1000, 500, 1000, 2, '3000.00', '7500.00'],
  [0, 0, 0, 1, '0.00', '0.00']
]) {
  const context = {
    computed: fn => ({ value: fn() }),
    bedFee: { value: fee }, careFee: { value: fee }, mealFee: { value: fee },
    depositAmount: { value: deposit }, memberFee: { value: member },
    formData: { value: { months } }
  }
  const result = vm.runInNewContext(`${formatFunction}\n${calculation}\n;[monthlyPrice.value, totalAmount.value, formatMoney(monthlyPrice.value), formatMoney(totalAmount.value)]`, context)
  assert.equal(result[0], Number(monthly))
  assert.equal(result[1], Number(total))
  assert.equal(result[2], monthly)
  assert.equal(result[3], total)
}
assert.ok(source.includes('{{ formatMoney(totalAmount) }}'))
assert.ok(source.includes('${formatMoney(monthlyPrice)}元/月'))
console.log('Order confirmation money: 5 cases passed')
