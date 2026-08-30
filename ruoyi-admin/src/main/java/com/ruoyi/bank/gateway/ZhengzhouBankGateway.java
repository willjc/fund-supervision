package com.ruoyi.bank.gateway;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.ruoyi.common.exception.ServiceException;

/**
 * 真实郑州银行适配器占位。
 * 银行尚未提供可用测试参数、签名密钥和回调联调条件前，显式拒绝真实调用。
 */
@Component
@ConditionalOnProperty(prefix = "bank.integration", name = "mode", havingValue = "zzbank")
public class ZhengzhouBankGateway implements BankGateway
{
    @Override
    public BankResult createPayment(BankPaymentRequest request)
    {
        throw new ServiceException("郑州银行真实网关尚未完成参数验收，禁止发起生产支付");
    }

    @Override
    public BankResult verifyMerchant(String merId, String settlementAccountNo)
    {
        throw new ServiceException("郑州银行测试参数尚未验收，不能验证真实商户号");
    }
}
