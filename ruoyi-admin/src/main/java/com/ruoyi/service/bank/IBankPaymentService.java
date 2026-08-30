package com.ruoyi.service.bank;

import java.math.BigDecimal;

import com.ruoyi.bank.gateway.BankResult;

public interface IBankPaymentService
{
    BankResult createPayment(Long orderId, Long institutionId, BigDecimal amount,
                             String channelType, String subject);
}
