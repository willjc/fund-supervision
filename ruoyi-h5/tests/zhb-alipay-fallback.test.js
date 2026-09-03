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
  let bridgeParams
  const context = vm.createContext({
    console,
    navigator: { userAgent: 'ZhengHaoban' },
    document: { addEventListener() {} },
    window: {
      location: { href: '' },
      atob: value => Buffer.from(value, 'base64').toString('binary'),
      AlipayJSBridge: {
        call(name, params, callback) {
          assert.equal(name, 'startApp')
          bridgeParams = params
          callback({ error: 10003102, errorMessage: 'UpdateException(102)Business exception' })
        }
      }
    }
  })
  const module = new vm.SourceTextModule(source, { context })
  await module.link(() => { throw new Error('zhb.js 不应依赖其他模块') })
  await module.evaluate()

  await module.namespace.launchZzBankAlipayMiniProgram(payUrl)

  const scheme = `alipays://platformapi/startapp?appId=${encodeURIComponent(launch.appId)}` +
    `&page=${encodeURIComponent(launch.page)}&query=${encodeURIComponent(launch.query)}`
  assert.equal(bridgeParams.appId, launch.appId)
  assert.equal(bridgeParams.param.page, launch.page)
  assert.equal(bridgeParams.param.query, launch.query)
  assert.equal(context.window.location.href,
    `https://ds.alipay.com/?scheme=${encodeURIComponent(scheme)}`)
}

main().then(() => console.log('zhb-alipay-fallback: ok')).catch(error => {
  console.error(error)
  process.exitCode = 1
})
