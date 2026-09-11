package com.ruoyi.bank.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;

/**
 * uTxnRefund 网关用例：请求锚定字段、响应码映射、出网地址守卫。
 * 信封签名/加解密的协议保真由 ZhengzhouBankGatewayTest 的假银行用例覆盖
 * （退款与查单共用同一 post 通道），此处通过覆写 post 注入响应专注业务映射。
 */
class ZhengzhouBankRefundGatewayTest
{
    private BankRefundRequest refundRequest()
    {
        BankRefundRequest request = new BankRefundRequest();
        request.setRequestNo("BR123456789012345678901234567890");
        request.setRequestTime(new Date());
        request.setMerId("8202106040000001");
        request.setMerName("郑州夕阳红集团金水区园区");
        request.setAmount(new BigDecimal("0.05"));
        request.setOriginalRequestNo("BP1A746389E4594FA49A896413E726E7");
        request.setOriginalBankSerialNo("20026091017164457253353180271553");
        request.setOriginalBankTime("20260910171644");
        return request;
    }

    private ZhengzhouBankGateway gatewayWithResponse(JSONObject response,
            AtomicReference<String> tranCode, AtomicReference<JSONObject> body)
    {
        ZhengzhouBankGateway gateway = new ZhengzhouBankGateway()
        {
            @Override
            JSONObject post(String code, JSONObject request, Date now)
            {
                if (tranCode != null)
                {
                    tranCode.set(code);
                }
                if (body != null)
                {
                    body.set(request);
                }
                return response;
            }
        };
        ReflectionTestUtils.setField(gateway, "gatewayUrl", "https://obk.lovingfox.cn:7000/openapi/zfbz/v1");
        ReflectionTestUtils.setField(gateway, "appId", "APP001");
        ReflectionTestUtils.setField(gateway, "appSecret", "SECRET001");
        ReflectionTestUtils.setField(gateway, "clientPrivateKeyPath", "/tmp/unused.key");
        ReflectionTestUtils.setField(gateway, "bankPublicKey", "KEY");
        ReflectionTestUtils.setField(gateway, "allowPrivateGateway", true);
        return gateway;
    }

    @Test
    void shouldAnchorRefundToOriginalPaymentFacts()
    {
        AtomicReference<String> tranCode = new AtomicReference<>();
        AtomicReference<JSONObject> body = new AtomicReference<>();
        JSONObject response = new JSONObject();
        response.put("respCode", "0002");
        response.put("respMsg", "交易受理成功，请稍后查询");
        ZhengzhouBankGateway gateway = gatewayWithResponse(response, tranCode, body);

        BankResult result = gateway.refundPayment(refundRequest());

        assertEquals("uTxnRefund", tranCode.get());
        assertEquals("8202106040000001", body.get().getString("merId"));
        assertEquals("BR123456789012345678901234567890", body.get().getString("txnOrderId"));
        assertEquals("20026091017164457253353180271553", body.get().getString("origRespTxnSsn"));
        assertEquals("20260910171644", body.get().getString("origRespTxnTime"));
        assertEquals("5", body.get().getString("txnAmt"));
        assertEquals("156", body.get().getString("txnCcyType"));
        assertEquals("OBK", body.get().getString("payChl"));
        assertEquals("01", body.get().getString("aesWay"));

        assertEquals("PENDING", result.getStatus());
        assertEquals("0002", result.getResponseCode());
    }

    @Test
    void shouldMapAcceptedRefundToPendingWithBankSerial()
    {
        JSONObject response = new JSONObject();
        response.put("respCode", "0000");
        response.put("respMsg", "受理成功");
        response.put("respTxnSsn", "BANKREFUND001");
        response.put("respTxnTime", "20260911101500");
        ZhengzhouBankGateway gateway = gatewayWithResponse(response, null, null);

        BankResult result = gateway.refundPayment(refundRequest());

        assertEquals("PENDING", result.getStatus());
        assertEquals("BANKREFUND001", result.getBankSerialNo());
    }

    @Test
    void shouldMapSynchronousDefiniteFailure()
    {
        JSONObject response = new JSONObject();
        response.put("respCode", "1088");
        response.put("respMsg", "退款金额超限");
        ZhengzhouBankGateway gateway = gatewayWithResponse(response, null, null);

        BankResult result = gateway.refundPayment(refundRequest());

        assertEquals("FAILED", result.getStatus());
        assertEquals("1088", result.getResponseCode());
    }

    @Test
    void shouldRejectRefundToPrivateGatewayWhenGuardActive()
    {
        ZhengzhouBankGateway gateway = new ZhengzhouBankGateway()
        {
            @Override
            JSONObject post(String code, JSONObject request, Date now)
            {
                throw new IllegalStateException("守卫生效时不应发起请求");
            }
        };
        ReflectionTestUtils.setField(gateway, "gatewayUrl", "http://192.168.1.10:7000/openapi/zfbz/v1");
        ReflectionTestUtils.setField(gateway, "appId", "APP001");
        ReflectionTestUtils.setField(gateway, "appSecret", "SECRET001");
        ReflectionTestUtils.setField(gateway, "clientPrivateKeyPath", "/tmp/unused.key");
        ReflectionTestUtils.setField(gateway, "bankPublicKey", "KEY");
        ReflectionTestUtils.setField(gateway, "allowPrivateGateway", false);

        ServiceException error = assertThrows(ServiceException.class,
                () -> gateway.refundPayment(refundRequest()));
        assertTrue(error.getMessage().contains("私有") || error.getMessage().contains("保留"));
    }

    @Test
    void shouldRejectIncompleteRefundRequest()
    {
        ZhengzhouBankGateway gateway = gatewayWithResponse(new JSONObject(), null, null);
        BankRefundRequest request = refundRequest();
        request.setOriginalBankSerialNo(" ");

        ServiceException error = assertThrows(ServiceException.class,
                () -> gateway.refundPayment(request));
        assertTrue(error.getMessage().contains("原支付银行流水号为空"));
    }
}
