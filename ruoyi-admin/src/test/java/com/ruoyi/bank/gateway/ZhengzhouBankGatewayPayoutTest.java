package com.ruoyi.bank.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;

/**
 * OBK0071702（ylzjhb 划拨申请）、OBK0071703（ylhzqy 划拨结果查询）、OBK0071709（ylyeqy 余额查询）映射测试：
 * 监管路径、transt 终态映射与成功证据校验。信封加解密通路由收单全链路测试覆盖，
 * 这里桩掉 postTo，不重复造加密夹具。真实发送受 payout-protocol-confirmed 闸门保护。
 */
class ZhengzhouBankGatewayPayoutTest
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

    private StubGateway gateway()
    {
        StubGateway gateway = new StubGateway();
        ReflectionTestUtils.setField(gateway, "gatewayUrl", "https://obk.example.cn:7000/openapi/zfbz/v1");
        ReflectionTestUtils.setField(gateway, "appId", "APP001");
        ReflectionTestUtils.setField(gateway, "appSecret", "SECRET001");
        ReflectionTestUtils.setField(gateway, "clientPrivateKeyPath", "/tmp/unused-test.key");
        ReflectionTestUtils.setField(gateway, "bankPublicKey", "dGVzdA==");
        ReflectionTestUtils.setField(gateway, "allowPrivateGateway", Boolean.TRUE);
        ReflectionTestUtils.setField(gateway, "payoutProtocolConfirmed", Boolean.TRUE);
        return gateway;
    }

    private static BankPayoutRequest request()
    {
        BankPayoutRequest request = new BankPayoutRequest();
        request.setRequestNo("BT20261020001");
        request.setRequestTime(new Date());
        request.setPayerAccountNo("999156009980023893");
        request.setPayerAccountName("郑州市养老机构监管账号");
        request.setPayeeAccountNo("999156009960023894");
        request.setPayeeAccountName("郑州市养老机构监管账号");
        request.setCrossBank(false);
        request.setAmount(new BigDecimal("0.01"));
        request.setRemark("养老资金拨付测试");
        return request;
    }

    private static JSONObject state(String transt)
    {
        JSONObject response = new JSONObject();
        response.put("respcd", "0000");
        response.put("resptx", "成功");
        response.put("trandt", "20261020");
        response.put("trantm", "153000");
        response.put("jgacct", "999156009980023893");
        response.put("jgacna", "郑州市养老机构监管账号");
        response.put("pyeeac", "999156009960023894");
        response.put("pyeena", "郑州市养老机构监管账号");
        response.put("tranam", "0.01");
        response.put("transq", "BANKS20261020001");
        response.put("transt", transt);
        return response;
    }

    @Test
    void shouldSubmitYlzjhbToSupervisionPathAndTreatAcceptanceAsPending()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "0000");
        gateway.canned.put("resptx", "受理成功");

        BankResult result = gateway.submitPayout(request());

        assertEquals("https://obk.example.cn:7000/openapi/ylzjjgin/v1", gateway.capturedUrl);
        assertEquals("OBK0071702", gateway.capturedTranCode);
        assertEquals("BT20261020001", gateway.capturedBody.getString("paylno"));
        assertEquals("999156009980023893", gateway.capturedBody.getString("jgacct"));
        assertEquals("郑州市养老机构监管账号", gateway.capturedBody.getString("jgacna"));
        assertEquals("999156009960023894", gateway.capturedBody.getString("pyeeac"));
        assertEquals("郑州市养老机构监管账号", gateway.capturedBody.getString("pyeena"));
        assertEquals("156", gateway.capturedBody.getString("crcycd"));
        assertEquals(new BigDecimal("0.01"), gateway.capturedBody.getBigDecimal("tranam"));
        assertTrue(gateway.capturedBody.getString("trandt").matches("[0-9]{8}"));
        assertEquals("PENDING", result.getStatus());
        assertEquals("BT20261020001", result.getRequestNo());
        assertEquals("受理成功", result.getResponseMessage());
    }

    @Test
    void shouldMapYlzjhbRefusalToFailed()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "9999");
        gateway.canned.put("resptx", "划拨拒绝");

        BankResult result = gateway.submitPayout(request());

        assertEquals("FAILED", result.getStatus());
        assertEquals("9999", result.getResponseCode());
        assertEquals("划拨拒绝", result.getResponseMessage());
    }

    @Test
    void shouldQueryYlhzqyByPaylnoAndMapTranst1WithFullEvidence()
    {
        StubGateway gateway = gateway();
        gateway.canned = state("1");

        BankResult result = gateway.queryPayout(request());

        assertEquals("OBK0071703", gateway.capturedTranCode);
        assertEquals("BT20261020001", gateway.capturedBody.getString("paylno"));
        assertTrue(gateway.capturedBody.getString("trandt").matches("[0-9]{8}"));
        assertEquals("SUCCESS", result.getStatus());
        assertEquals("BT20261020001", result.getRequestNo());
        assertEquals("BANKS20261020001", result.getBankSerialNo());
        assertEquals(new BigDecimal("0.01"), result.getPaidAmount());
        assertEquals("999156009980023893", result.getPayerAccountNo());
        assertEquals("999156009960023894", result.getPayeeAccountNo());
        assertEquals("20261020153000", result.getBankTransactionTime());
    }

    @Test
    void shouldMapTranstFinalAndIntermediateStates()
    {
        StubGateway gateway = gateway();

        gateway.canned = state("2");
        gateway.canned.put("resptx", "划拨失败");
        BankResult failed = gateway.queryPayout(request());
        assertEquals("FAILED", failed.getStatus());
        assertEquals("TRANST2", failed.getResponseCode());
        assertEquals("BT20261020001", failed.getRequestNo());

        for (String processing : new String[]{"0", "5"})
        {
            gateway.canned = state(processing);
            BankResult pending = gateway.queryPayout(request());
            assertEquals("PENDING", pending.getStatus());
            assertEquals("TRANST" + processing, pending.getResponseCode());
        }

        for (String review : new String[]{"3", "4"})
        {
            gateway.canned = state(review);
            assertEquals("UNKNOWN", gateway.queryPayout(request()).getStatus());
        }

        gateway.canned = state("6");
        gateway.canned.put("resptx", "已退汇");
        BankResult returned = gateway.queryPayout(request());
        assertEquals("RETURNED", returned.getStatus());
        assertEquals("BT20261020001", returned.getRequestNo());
        assertEquals("20261020153000", returned.getBankTransactionTime());
        assertEquals("已退汇", returned.getResponseMessage());

        gateway.canned = state("9");
        assertEquals("UNKNOWN", gateway.queryPayout(request()).getStatus());
    }

    @Test
    void shouldNotBookYlhzqySuccessWithoutEvidence()
    {
        StubGateway gateway = gateway();

        gateway.canned = state("1");
        gateway.canned.remove("transq");
        assertEquals("SERIAL_MISSING", gateway.queryPayout(request()).getResponseCode());

        gateway.canned = state("1");
        gateway.canned.put("tranam", "abc");
        assertEquals("AMOUNT_INVALID", gateway.queryPayout(request()).getResponseCode());

        gateway.canned = state("1");
        gateway.canned.remove("trandt");
        gateway.canned.remove("trantm");
        assertEquals("TIME_MISSING", gateway.queryPayout(request()).getResponseCode());

        gateway.canned = state("1");
        gateway.canned.remove("pyeeac");
        assertEquals("ACCOUNT_MISSING", gateway.queryPayout(request()).getResponseCode());
    }

    @Test
    void queryFailureOrNotFoundStaysUnknownNotFailed()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "9999");
        gateway.canned.put("resptx", "查无此单");

        BankResult result = gateway.queryPayout(request());

        assertEquals("UNKNOWN", result.getStatus());
        assertEquals("9999", result.getResponseCode());
        assertEquals("BT20261020001", result.getRequestNo());
    }

    @Test
    void shouldQueryBalanceFromYlyeqy()
    {
        StubGateway gateway = gateway();
        gateway.canned = new JSONObject();
        gateway.canned.put("respcd", "0000");
        gateway.canned.put("resptx", "成功");
        gateway.canned.put("onlnbl", "200.00");
        gateway.canned.put("acblsp", "188.88");

        BankResult result = gateway.queryBalance("999156009980023893", "郑州市养老机构监管账号");

        assertEquals("OBK0071709", gateway.capturedTranCode);
        assertEquals("999156009980023893", gateway.capturedBody.getString("acctno"));
        assertEquals("郑州市养老机构监管账号", gateway.capturedBody.getString("acctna"));
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(new BigDecimal("188.88"), result.getAvailableBalance());

        gateway.canned.put("acblsp", "abc");
        assertEquals("BALANCE_INVALID",
                gateway.queryBalance("999156009980023893", "郑州市养老机构监管账号").getResponseCode());
    }

    @Test
    void payoutMethodsStayProtectiveUntilProtocolConfirmed()
    {
        StubGateway gateway = gateway();
        ReflectionTestUtils.setField(gateway, "payoutProtocolConfirmed", Boolean.FALSE);
        BankPayoutRequest request = request();

        assertFalse(gateway.supportsPayout());
        assertEquals("监管拨付协议尚未确认，禁止发送 ylzjhb；请先取得银行报文、验签及结果码确认",
                assertThrows(ServiceException.class, () -> gateway.submitPayout(request)).getMessage());
        assertEquals("监管拨付查询协议尚未确认，不能查询 ylhzqy",
                assertThrows(ServiceException.class, () -> gateway.queryPayout(request)).getMessage());
        assertEquals("监管余额查询协议尚未确认，不能查询 ylyeqy",
                assertThrows(ServiceException.class, () -> gateway.queryBalance("PAYER", "监管户")).getMessage());
    }
}
