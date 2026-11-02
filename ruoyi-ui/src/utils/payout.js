// 历史本地完成记录不能作为真实银行到账凭证。
export function payoutStatus(row = {}) {
  if (Number(row.bankEligible) !== 1) return '历史记录（未接入银行）'
  if (Number(row.manualReview) === 1) return '待人工核查'
  const labels = { pending: '待拨付', processing: '银行处理中', completed: '拨付成功', failed: '拨付失败', returned: '已退汇', cancelled: '已取消' }
  return labels[row.status] || '状态待核实'
}

export function canApprovePayout(row = {}) {
  return Number(row.bankEligible) === 1 && row.status === 'pending' && !row.approveTime
}

export function canRetryPayout(row = {}) {
  return Number(row.bankEligible) === 1 && row.status === 'failed' && !!row.bankTransactionId
}

// 月度自动拨付不得早于划拨日期发送；手动申请、首月拨付不受限制。
export function isPayoutDue(row = {}, today = new Date()) {
  if (!row.sourceKey || String(row.sourceKey).indexOf('MONTH:') !== 0
      || row.paidMethod === 'manual' || !row.transferDate) return true
  const due = String(row.transferDate).slice(0, 10)
  const now = [today.getFullYear(), String(today.getMonth() + 1).padStart(2, '0'), String(today.getDate()).padStart(2, '0')].join('-')
  return due <= now
}

export function canSendPayout(row = {}, today = new Date()) {
  return Number(row.bankEligible) === 1 && row.status === 'pending'
    && !!row.approveTime && isPayoutDue(row, today)
}
