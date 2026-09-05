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
