import request from '@/utils/request'

// 对账批次列表
export function listReconRuns(limit) {
  return request({ url: '/bank/recon/runs', method: 'get', params: { limit: limit } })
}

// 当前用户可对账的商户
export function listReconMerchants() {
  return request({ url: '/bank/recon/merchants', method: 'get' })
}

// 监管对账：可对账的监管账户
export function listSupervisionAccounts() {
  return request({ url: '/bank/supervision/recon/accounts', method: 'get' })
}

// 监管对账批次
export function listSupervisionRuns(limit) {
  return request({ url: '/bank/supervision/recon/runs', method: 'get', params: { limit: limit } })
}

// 执行监管对账（706）
export function runSupervisionRecon(data) {
  return request({ url: '/bank/supervision/recon/run', method: 'post', data: data })
}

// 监管对账重新获取文件
export function retrySupervisionRecon(runId, data) {
  return request({ url: `/bank/supervision/recon/runs/${runId}/retry`, method: 'post', data: data })
}

// 下载监管对账文件
export function downloadSupervisionReconFile(runId) {
  return request({ url: `/bank/supervision/recon/runs/${runId}/file`, method: 'get', responseType: 'blob' })
}

// 批次差异列表
export function listReconDiffs(runId) {
  return request({ url: '/bank/recon/diffs', method: 'get', params: { runId: runId } })
}

// 标记差异已处理（备注必填）
export function handleReconDiff(data) {
  return request({ url: '/bank/recon/diffs/handle', method: 'post', data: data })
}

// 手动执行对账（可带文件名直接下载比对）
export function runRecon(data) {
  return request({ url: '/bank/recon/run', method: 'post', data: data })
}
