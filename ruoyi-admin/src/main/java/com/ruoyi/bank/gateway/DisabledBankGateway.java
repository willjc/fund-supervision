package com.ruoyi.bank.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.ruoyi.common.exception.ServiceException;

@Component
@ConditionalOnProperty(prefix = "bank.integration", name = "mode", havingValue = "disabled", matchIfMissing = true)
public class DisabledBankGateway implements BankGateway
{
    @Override
    public BankResult createPayment(BankPaymentRequest request)
    {
        throw new ServiceException("银行支付尚未启用，请先完成郑州银行测试环境配置");
    }

    @Override
    public BankResult queryPayment(BankQueryRequest request)
    {
        throw new ServiceException("银行支付尚未启用，不能查询交易状态");
    }

    @Override
    public BankResult verifyMerchant(String merId, String settlementAccountNo)
    {
        throw new ServiceException("银行对接尚未启用，不能验证商户号");
    }
}
