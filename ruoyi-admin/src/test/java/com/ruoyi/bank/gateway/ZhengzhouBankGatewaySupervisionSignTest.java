package com.ruoyi.bank.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;

/**
 * OBK0071701（ylcntr 监管签约）映射测试：监管路径推导、信封 tranCode、
 * jg、hb 业务字段与 respcd/jgxyno 结果映射。信封加解密通路由收单全链路测试覆盖，
 * 这里桩掉 postTo，不重复造加密夹具。
 */
class ZhengzhouBankGatewaySupervisionSignTest
{
    /** 同包桩：捕获目标 URL 与报文，返回预置回文。 */
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

    private static final String SUPERVISE_ACCOUNT = "619900001111";

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
        request.setSuperviseAccountNo(SUPERVISE_ACCOUNT);
        request.setSuperviseAccountName("监管账户户名");
        request.setPayeeAccountNo("618800002222");
        request.setPayeeAccountName("基本账户户名");
        request.setCrossBank(1);
        request.setPayeeBankNo("313491000011");
        request.setUnifiedCreditCode("91410100MA9XXXXX00");
        return request;
    }

    @Test
    void shouldPostObkSignToSupervisionPathWithMappedFields()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "0000");
        gateway.canned.put("resptx", "成功");
        gateway.canned.put("jgxyno", "JGXY0001");

        BankResult result = gateway.signSupervisionContract(request());

        assertEquals("https://obk.example.cn:7000/openapi/ylzjjgin/v1", gateway.capturedUrl);
        assertEquals("OBK0071701", gateway.capturedTranCode);
        assertEquals(SUPERVISE_ACCOUNT, gateway.capturedBody.getString("jgacct"));
        assertEquals("监管账户户名", gateway.capturedBody.getString("jgacna"));
        assertEquals("2", gateway.capturedBody.getString("banktg"));
        assertEquals("618800002222", gateway.capturedBody.getString("hbskno"));
        assertEquals("基本账户户名", gateway.capturedBody.getString("hbskna"));
        assertEquals("313491000011", gateway.capturedBody.getString("hbbank"));
        assertEquals("91410100MA9XXXXX00", gateway.capturedBody.getString("socode"));
        assertEquals("SUCCESS", result.getStatus());
        assertEquals("JGXY0001", result.getAgreementNo());
    }

    @Test
    void shouldMapBankRefusalToFailedWithoutAgreement()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "9999");
        gateway.canned.put("resptx", "签约失败");

        BankResult result = gateway.signSupervisionContract(request());

        assertEquals("FAILED", result.getStatus());
        assertEquals("9999", result.getResponseCode());
        assertEquals("签约失败", result.getResponseMessage());
    }

    @Test
    void shouldNotTreatSuccessWithoutAgreementNumberAsSigned()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "0000");
        gateway.canned.put("resptx", "成功");

        BankResult result = gateway.signSupervisionContract(request());

        assertEquals("UNKNOWN", result.getStatus());
        assertEquals("AGREEMENT_MISSING", result.getResponseCode());
    }

    @Test
    void banktgShouldDeriveFromCrossBankAndConfiguredUrlShouldWin()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "0000");
        gateway.canned.put("jgxyno", "JGXY0001");

        BankSupervisionSignRequest request = request();
        request.setCrossBank(0);
        gateway.signSupervisionContract(request);
        assertEquals("1", gateway.capturedBody.getString("banktg"));

        ReflectionTestUtils.setField(gateway, "supervisionGatewayUrl",
                "https://obk.example.cn:7000/openapi/custom-yl/v1");
        gateway.signSupervisionContract(request);
        assertEquals("https://obk.example.cn:7000/openapi/custom-yl/v1", gateway.capturedUrl);
    }

    @Test
    void shouldRejectSignWithoutDerivableSupervisionUrl()
    {
        StubGateway gateway = gateway();
        ReflectionTestUtils.setField(gateway, "gatewayUrl", "https://obk.example.cn:7000/openapi/custom/v1");
        assertThrows(ServiceException.class, () -> gateway.signSupervisionContract(request()));
    }
}
