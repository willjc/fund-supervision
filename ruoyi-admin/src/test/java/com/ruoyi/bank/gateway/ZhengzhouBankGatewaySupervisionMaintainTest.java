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

class ZhengzhouBankGatewaySupervisionMaintainTest
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

    private static BankSupervisionSignRequest request()
    {
        BankSupervisionSignRequest request = new BankSupervisionSignRequest();
        request.setSuperviseAccountNo("619900001111");
        request.setSuperviseAccountName("统一监管户名");
        request.setPayeeAccountNo("618800002222");
        request.setPayeeAccountName("统一监管户名");
        request.setCrossBank(1);
        request.setPayeeBankNo("313491000011");
        request.setUnifiedCreditCode("91410100MA9XXXXX00");
        return request;
    }

    @Test
    void shouldPostObkMaintenanceWithOriginalAgreementAndCurrentAccounts()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "0000");
        gateway.canned.put("resptx", "成功");

        BankResult result = gateway.maintainSupervisionContract(" JGXY0001 ", request());

        assertEquals("https://obk.example.cn:7000/openapi/ylzjjgin/v1", gateway.capturedUrl);
        assertEquals("OBK0071704", gateway.capturedTranCode);
        assertEquals("JGXY0001", gateway.capturedBody.getString("jgxyno"));
        assertEquals("619900001111", gateway.capturedBody.getString("jgacct"));
        assertEquals("统一监管户名", gateway.capturedBody.getString("jgacna"));
        assertEquals("618800002222", gateway.capturedBody.getString("hbskno"));
        assertEquals("统一监管户名", gateway.capturedBody.getString("hbskna"));
        assertEquals("313491000011", gateway.capturedBody.getString("hbbank"));
        assertEquals("91410100MA9XXXXX00", gateway.capturedBody.getString("socode"));
        assertFalse(gateway.capturedBody.containsKey("banktg"));
        assertEquals("SUCCESS", result.getStatus());
        assertEquals("JGXY0001", result.getAgreementNo());
    }

    @Test
    void shouldMapMaintenanceRefusalToFailed()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "9999");
        gateway.canned.put("resptx", "维护失败");

        BankResult result = gateway.maintainSupervisionContract("JGXY0001", request());

        assertEquals("FAILED", result.getStatus());
        assertEquals("9999", result.getResponseCode());
        assertEquals("维护失败", result.getResponseMessage());
    }

    @Test
    void shouldRejectOversizedAgreementBeforePosting()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "0000");

        ServiceException exception = assertThrows(ServiceException.class,
                () -> gateway.maintainSupervisionContract("JGXY12345678901234567", request()));

        assertEquals("监管协议编号长度不能超过20位", exception.getMessage());
        assertNull(gateway.capturedTranCode);
        assertNull(gateway.capturedBody);
    }

    @Test
    void shouldRejectMaintenanceWithoutAgreementBeforePosting()
    {
        StubGateway gateway = gateway();

        ServiceException exception = assertThrows(ServiceException.class,
                () -> gateway.maintainSupervisionContract(" ", request()));

        assertEquals("监管协议编号不能为空，请先完成监管签约", exception.getMessage());
        assertNull(gateway.capturedTranCode);
        assertNull(gateway.capturedBody);
    }
}
