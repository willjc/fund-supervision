package com.ruoyi.web.controller.bank;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.bank.gateway.BankGateway;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.service.bank.impl.BankPayoutService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * 707 退汇通知入口测试：信封开启/应答加解密为桩缝（协议镜像加解密由既有收单全链路测试覆盖），
 * 这里验证交易码容错、字段校验、登记失败不 ACK 成功、验签失败不返回业务 ACK。
 */
class BankNotificationControllerTest
{
    private static final class StubController extends BankNotificationController
    {
        JSONObject cannedBiz;
        String cannedTranCode = "ylthtz";
        boolean failOpen;

        @Override
        protected BankGateway.InboundEnvelope openEnvelope(String rawBody)
        {
            if (failOpen)
            {
                throw new ServiceException("银行通知验签失败");
            }
            return new BankGateway.InboundEnvelope(cannedTranCode, "SEQ1", cannedBiz);
        }

        @Override
        protected JSONObject buildResponse(JSONObject biz)
        {
            return biz;
        }
    }

    private static JSONObject notify(String paylno, String retntm, String retnrn)
    {
        JSONObject biz = new JSONObject();
        biz.put("paylno", paylno);
        biz.put("retntm", retntm);
        biz.put("retnrn", retnrn);
        return biz;
    }

    private static JSONObject body(ResponseEntity<String> response)
    {
        return JSONObject.parseObject(response.getBody());
    }

    private static StubController controller(BankPayoutService payouts)
    {
        StubController controller = new StubController();
        ReflectionTestUtils.setField(controller, "payouts", payouts);
        return controller;
    }

    @Test
    void shouldRegisterReturnAndAck0000()
    {
        BankPayoutService payouts = mock(BankPayoutService.class);
        StubController controller = controller(payouts);
        controller.cannedBiz = notify("BT1", "2026-05-09T10:30:00", "收款账户问题退汇");

        ResponseEntity<String> response = controller.onReturned("{}");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("0000", body(response).getString("respcd"));
        verify(payouts).recordVerifiedReturn("BT1", "2026-05-09T10:30:00", "收款账户问题退汇");
    }

    @Test
    void shouldAcceptBothDocumentedTranCodes()
    {
        for (String tranCode : new String[]{"ylthtz", "OBK0071707"})
        {
            BankPayoutService payouts = mock(BankPayoutService.class);
            StubController controller = controller(payouts);
            controller.cannedTranCode = tranCode;
            controller.cannedBiz = notify("BT1", "2026-05-09T10:30:00", "退汇");

            ResponseEntity<String> response = controller.onReturned("{}");

            assertEquals("0000", body(response).getString("respcd"), tranCode);
            verify(payouts).recordVerifiedReturn("BT1", "2026-05-09T10:30:00", "退汇");
        }
    }

    @Test
    void shouldRejectUnknownTranCodeWithoutBooking()
    {
        BankPayoutService payouts = mock(BankPayoutService.class);
        StubController controller = controller(payouts);
        controller.cannedTranCode = "zzzzzz";
        controller.cannedBiz = notify("BT1", "2026-05-09T10:30:00", "退汇");

        ResponseEntity<String> response = controller.onReturned("{}");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("9999", body(response).getString("respcd"));
        verify(payouts, never()).recordVerifiedReturn(any(), any(), any());
    }

    @Test
    void shouldRejectMissingOrOverlongFields()
    {
        JSONObject[] invalid = {
                notify(null, "2026-05-09T10:30:00", "退汇"),
                notify("BT1", null, "退汇"),
                notify("BT1", "2026-05-09T10:30:00", null),
                notify(repeat("X", 65), "2026-05-09T10:30:00", "退汇"),
                notify("BT1", repeat("1", 31), "退汇"),
                notify("BT1", "2026-05-09T10:30:00", repeat("因", 257))
        };
        for (JSONObject biz : invalid)
        {
            BankPayoutService payouts = mock(BankPayoutService.class);
            StubController controller = controller(payouts);
            controller.cannedBiz = biz;

            ResponseEntity<String> response = controller.onReturned("{}");

            assertEquals(200, response.getStatusCodeValue());
            assertEquals("9999", body(response).getString("respcd"));
            verify(payouts, never()).recordVerifiedReturn(any(), any(), any());
        }
    }

    @Test
    void shouldAckFailureWhenRequestUnknownOrInvalid()
    {
        BankPayoutService payouts = mock(BankPayoutService.class);
        doThrow(new ServiceException("银行拨付交易不存在")).when(payouts)
                .recordVerifiedReturn("GHOST", "2026-05-09T10:30:00", "退汇");
        StubController controller = controller(payouts);
        controller.cannedBiz = notify("GHOST", "2026-05-09T10:30:00", "退汇");

        ResponseEntity<String> response = controller.onReturned("{}");

        assertEquals(200, response.getStatusCodeValue());
        JSONObject body = body(response);
        assertEquals("9999", body.getString("respcd"));
        assertTrue(body.getString("resptx").contains("登记失败"));
    }

    @Test
    void verifyFailureShouldNotReturnBusinessAck()
    {
        BankPayoutService payouts = mock(BankPayoutService.class);
        StubController controller = controller(payouts);
        controller.failOpen = true;

        ResponseEntity<String> response = controller.onReturned("{}");

        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(payouts, never()).recordVerifiedReturn(any(), any(), any());
    }

    @Test
    void repeatedNotificationIsAckedAndDelegatesEachTime()
    {
        // 幂等由 BankSettlementMapper.recordReturn 的 return_time IS NULL 守卫保证；
        // 入口层对重复通知同样回 0000，由服务层幂等消化。
        BankPayoutService payouts = mock(BankPayoutService.class);
        StubController controller = controller(payouts);
        controller.cannedBiz = notify("BT1", "2026-05-09T10:30:00", "退汇");

        assertEquals("0000", body(controller.onReturned("{}")).getString("respcd"));
        assertEquals("0000", body(controller.onReturned("{}")).getString("respcd"));
        verify(payouts, times(2)).recordVerifiedReturn("BT1", "2026-05-09T10:30:00", "退汇");
    }

    @Test
    void paymentNotificationStaysClosed()
    {
        assertEquals(503, new BankNotificationController().paymentProtocolNotVerified().getStatusCodeValue());
    }

    private static String repeat(String part, int times)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < times; i++)
        {
            builder.append(part);
        }
        return builder.toString();
    }
}
