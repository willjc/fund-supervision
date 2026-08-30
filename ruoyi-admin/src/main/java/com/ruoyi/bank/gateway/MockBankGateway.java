package com.ruoyi.bank.gateway;

import java.math.BigDecimal;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "bank.integration", name = "mode", havingValue = "mock")
public class MockBankGateway implements BankGateway
{
    @Override
    public BankResult createPayment(BankPaymentRequest request)
    {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            return BankResult.failed("MOCK_AMOUNT_INVALID", "支付金额必须大于0");
        }
        return BankResult.pending("MOCK-" + request.getRequestNo(),
                "mock-bank://checkout/" + request.getRequestNo());
    }

    @Override
    public BankResult verifyMerchant(String merId, String settlementAccountNo)
    {
        if (merId == null || settlementAccountNo == null)
        {
            return BankResult.failed("MOCK_MERCHANT_INVALID", "商户号或结算账户为空");
        }
        return BankResult.success("MOCK-VERIFY-" + merId);
    }
}
