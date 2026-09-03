const assert = require('assert/strict')
const fs = require('fs')
const path = require('path')
const vm = require('vm')

async function main() {
  const source = fs.readFileSync(path.join(__dirname, '../src/utils/zhb.js'), 'utf8')
  const launch = {
    appId: '2021002182634333',
    page: 'pages/index/index',
    query: JSON.stringify({ txnType: '1007', txnAmt: '1' })
  }
  const payUrl = `zzbank-alipay://${Buffer.from(JSON.stringify(launch)).toString('base64url')}`
  let bridgeCalls = 0
  const context = vm.createContext({
    console,
    navigator: { userAgent: 'ZhengHaoban' },
    document: { addEventListener() {} },
    window: {
      location: { href: '' },
      atob: value => Buffer.from(value, 'base64').toString('binary'),
      AlipayJSBridge: {
        call() {
          bridgeCalls++
        }
      }
    }
  })
  const module = new vm.SourceTextModule(source, { context })
  await module.link(() => { throw new Error('zhb.js 不应依赖其他模块') })
  await module.evaluate()

  const launchPromise = module.namespace.launchZzBankAlipayMiniProgram(payUrl)
  assert.equal(bridgeCalls, 0, '银行支付宝小程序不能交给郑好办 mPaaS startApp')
  await launchPromise

  const scheme = `alipays://platformapi/startapp?appId=${launch.appId}` +
    `&page=${launch.page}&query=${encodeURIComponent(launch.query)}`
  assert.equal(context.window.location.href,
    `https://ds.alipay.com/?scheme=${encodeURIComponent(scheme)}`)
}

main().then(() => console.log('zhb-alipay-scheme: ok')).catch(error => {
  console.error(error)
  process.exitCode = 1
})
