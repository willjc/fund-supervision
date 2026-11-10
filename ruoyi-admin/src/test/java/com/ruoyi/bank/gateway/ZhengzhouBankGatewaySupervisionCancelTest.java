package com.ruoyi.bank.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;

class ZhengzhouBankGatewaySupervisionCancelTest
{
    static class StubGateway extends ZhengzhouBankGateway
    {
        JSONObject canned;
        String capturedUrl;
        String capturedTranCode;
        JSONObject capturedBody;

        @Override
        JSONObject postTo(String targetUrl, String tranCode, JSONObject body, Date now)
        {
            capturedUrl = targetUrl;
            capturedTranCode = tranCode;
            capturedBody = body;
            return canned;
        }
    }

    private StubGateway gateway()
    {
        StubGateway gateway = new StubGateway();
        ReflectionTestUtils.setField(gateway, "gatewayUrl", "https://obk.example.cn:7000/openapi/zfbz/v1");
        ReflectionTestUtils.setField(gateway, "appId", "APP001");
        ReflectionTestUtils.setField(gateway, "appSecret", "SECRET001");
        ReflectionTestUtils.setField(gateway, "clientPrivateKeyPath", "/tmp/unused-test.key");
        ReflectionTestUtils.setField(gateway, "bankPublicKey", "dGVzdA==");
        return gateway;
    }

    @Test
    void shouldPostObkCancellationWithAgreementAndSupervisionAccount()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "0000");
        gateway.canned.put("resptx", "解约成功");

        BankResult result = gateway.cancelSupervisionContract(" AGRE0001 ", "619900001111", "统一监管户名");

        assertEquals("https://obk.example.cn:7000/openapi/ylzjjgin/v1", gateway.capturedUrl);
        assertEquals("OBK0071705", gateway.capturedTranCode);
        assertEquals(3, gateway.capturedBody.size());
        assertEquals("AGRE0001", gateway.capturedBody.getString("jgxyno"));
        assertEquals("619900001111", gateway.capturedBody.getString("jgacct"));
        assertEquals("统一监管户名", gateway.capturedBody.getString("jgacna"));
        assertFalse(gateway.capturedBody.containsKey("banktg"));
        assertEquals("SUCCESS", result.getStatus());
        assertEquals("0000", result.getResponseCode());
        assertEquals("解约成功", result.getResponseMessage());
        assertEquals("AGRE0001", result.getAgreementNo());
    }

    @Test
    void shouldMapCancellationRefusalToFailed()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "9999");
        gateway.canned.put("resptx", "解约失败");

        BankResult result = gateway.cancelSupervisionContract("AGRE0001", "619900001111", "统一监管户名");

        assertEquals("FAILED", result.getStatus());
        assertEquals("9999", result.getResponseCode());
        assertEquals("解约失败", result.getResponseMessage());
    }

    @Test
    void shouldRejectMissingAgreementBeforePosting()
    {
        StubGateway gateway = gateway();

        ServiceException exception = assertThrows(ServiceException.class,
                () -> gateway.cancelSupervisionContract(" ", "619900001111", "统一监管户名"));

        assertEquals("监管协议编号不能为空，无法发起解约", exception.getMessage());
        assertNull(gateway.capturedTranCode);
        assertNull(gateway.capturedBody);
    }
}
