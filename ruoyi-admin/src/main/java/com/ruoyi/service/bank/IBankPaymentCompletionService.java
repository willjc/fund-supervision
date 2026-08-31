package com.ruoyi.service.bank;

import com.ruoyi.domain.bank.BankPaymentCompletionResult;

/**
 * 银行支付完成后的统一入账入口。同步支付结果、模拟回调和真实银行回调必须复用此服务。
 */
public interface IBankPaymentCompletionService
{
    BankPaymentCompletionResult completePayment(String requestNo, String bankSerialNo,
            String responseCode, String responseMessage, String operator);
}
