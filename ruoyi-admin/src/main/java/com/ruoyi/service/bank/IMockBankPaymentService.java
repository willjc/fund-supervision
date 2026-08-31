package com.ruoyi.service.bank;

import com.ruoyi.domain.bank.BankPaymentCompletionResult;

/**
 * 仅供 mock 银行模式使用的支付完成服务。
 */
public interface IMockBankPaymentService
{
    BankPaymentCompletionResult completeMockPayment(String requestNo, Long userId);
}
