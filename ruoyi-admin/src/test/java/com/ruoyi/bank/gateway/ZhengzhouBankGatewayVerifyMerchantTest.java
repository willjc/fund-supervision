package com.ruoyi.bank.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;

class ZhengzhouBankGatewayVerifyMerchantTest
{
    static class StubGateway extends ZhengzhouBankGateway
    {
        ServiceException failure;
        JSONObject response;

        @Override
        JSONObject postTo(String targetUrl, String tranCode, JSONObject body, Date now)
        {
            if (failure != null)
            {
                throw failure;
            }
            return response;
        }
    }

    private StubGateway gateway()
    {
        StubGateway gateway = new StubGateway();
        ReflectionTestUtils.setField(gateway, "gatewayUrl", "https://obk.example.cn/openapi/zfbz/v1");
        ReflectionTestUtils.setField(gateway, "appId", "APP001");
        ReflectionTestUtils.setField(gateway, "appSecret", "SECRET001");
        ReflectionTestUtils.setField(gateway, "clientPrivateKeyPath", "/tmp/unused-test.key");
        ReflectionTestUtils.setField(gateway, "bankPublicKey", "dGVzdA==");
        return gateway;
    }

    @Test
    void shouldTreatOuterQueryNotFoundAsMerchantVerificationSuccess()
    {
        StubGateway gateway = gateway();
        gateway.failure = new ServiceException("郑州银行网关拒绝请求：UERS001025 交易受理失败，查无此交易");

        BankResult result = gateway.verifyMerchant("8202106040000001", "619900001111");

        assertEquals("SUCCESS", result.getStatus());
    }

    @Test
    void shouldKeepInnerQueryNotFoundAsMerchantVerificationSuccess()
    {
        StubGateway gateway = gateway();
        gateway.response = new JSONObject();
        gateway.response.put("respCode", "0000");
        gateway.response.put("origRespCode", "1025");
        gateway.response.put("origRespMsg", "交易受理失败，查无此交易");

        BankResult result = gateway.verifyMerchant("8202106040000001", "619900001111");

        assertEquals("SUCCESS", result.getStatus());
    }

    @Test
    void shouldPropagateOtherGatewayErrors()
    {
        StubGateway gateway = gateway();
        gateway.failure = new ServiceException("郑州银行网关拒绝请求：UERS002000 交易受理失败，商户合同已中止");

        assertThrows(ServiceException.class,
                () -> gateway.verifyMerchant("8202106040000001", "619900001111"));
    }
}
