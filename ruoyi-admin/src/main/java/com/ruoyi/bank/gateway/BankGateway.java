package com.ruoyi.bank.gateway;

import com.ruoyi.common.exception.ServiceException;

public interface BankGateway
{
    BankResult createPayment(BankPaymentRequest request);

    BankResult queryPayment(BankQueryRequest request);

    BankResult verifyMerchant(String merId, String settlementAccountNo);

    default boolean supportsPayout()
    {
        return false;
    }

    default BankResult submitPayout(BankPayoutRequest request)
    {
        throw new ServiceException("监管拨付协议尚未确认，禁止发送 ylzjhb；请先取得银行报文、验签及结果码确认");
    }

    default BankResult queryPayout(BankPayoutRequest request)
    {
        throw new ServiceException("监管拨付查询协议尚未确认，不能查询 ylhzqy");
    }

    default BankResult queryBalance(String accountNo, String accountName)
    {
        throw new ServiceException("监管余额查询协议尚未确认，不能查询 ylyeqy");
    }
}
