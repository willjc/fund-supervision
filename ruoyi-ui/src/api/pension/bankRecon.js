import request from '@/utils/request'

// 对账批次列表
export function listReconRuns(limit) {
  return request({ url: '/bank/recon/runs', method: 'get', params: { limit: limit } })
}

// 当前用户可对账的商户
export function listReconMerchants() {
  return request({ url: '/bank/recon/merchants', method: 'get' })
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
