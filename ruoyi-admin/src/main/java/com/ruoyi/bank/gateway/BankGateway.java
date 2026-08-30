package com.ruoyi.bank.gateway;

public interface BankGateway
{
    BankResult createPayment(BankPaymentRequest request);

    BankResult verifyMerchant(String merId, String settlementAccountNo);
}
