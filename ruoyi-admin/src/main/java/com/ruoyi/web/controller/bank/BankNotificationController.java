package com.ruoyi.web.controller.bank;

import java.util.Date;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.bank.gateway.BankGateway;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.service.bank.impl.BankPayoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 银行通知入口。/returned 按 v2.0 OBK0071707（ylthtz 退汇通知）实现：镜像开启入站信封
 * （银行公钥验签、我方私钥解密），通过后登记退汇事实并触发冲正（recordVerifiedReturn 幂等），
 * 应答标准信封（bizContent=respcd/resptx）。/payment 协议未与银行确认，仍硬性关闭。
 * 不落原始报文，仅记 tranSeq/paylno/处理结果。
 */
@RestController
@RequestMapping("/bank/zzbank/notify")
public class BankNotificationController
{
    private static final Logger log = LoggerFactory.getLogger(BankNotificationController.class);

    @Autowired
    private BankGateway gateway;

    @Autowired
    private BankPayoutService payouts;

    @PostMapping("/payment")
    public ResponseEntity<Void> paymentProtocolNotVerified()
    {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @PostMapping(value = "/returned", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> onReturned(@RequestBody String rawBody)
    {
        BankGateway.InboundEnvelope envelope;
        try
        {
            envelope = openEnvelope(rawBody);
        }
        catch (ServiceException e)
        {
            // 验签/解密失败不返回业务 ACK，也不落原始报文。
            log.warn("银行退汇通知校验失败：{}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
        if (!isReturnNotify(envelope.tranCode))
        {
            log.warn("银行通知交易码不符 tranSeq={} tranCode={}", envelope.tranSeq, envelope.tranCode);
            return ack("9999", "非退汇通知交易码");
        }
        JSONObject biz = envelope.biz == null ? new JSONObject() : envelope.biz;
        String paylno = biz.getString("paylno");
        String retntm = biz.getString("retntm");
        String retnrn = biz.getString("retnrn");
        if (isBlank(paylno) || paylno.length() > 64 || isBlank(retntm) || retntm.length() > 30
                || retnrn == null || retnrn.length() > 256)
        {
            log.warn("银行退汇通知字段不完整 tranSeq={} paylno={}", envelope.tranSeq, paylno);
            return ack("9999", "退汇通知字段不完整");
        }
        try
        {
            payouts.recordVerifiedReturn(paylno, retntm, retnrn);
        }
        catch (ServiceException e)
        {
            log.warn("银行退汇通知登记失败 tranSeq={} paylno={}：{}", envelope.tranSeq, paylno, e.getMessage());
            return ack("9999", "退汇通知登记失败：" + e.getMessage());
        }
        log.info("银行退汇通知已登记 tranSeq={} paylno={}", envelope.tranSeq, paylno);
        return ack("0000", "success");
    }

    /** 信封开启桩缝：测试覆写以跳过加解密；生产走网关镜像验签解密。 */
    protected BankGateway.InboundEnvelope openEnvelope(String rawBody)
    {
        return gateway.openInboundEnvelope(rawBody);
    }

    /** 应答信封构建桩缝：测试覆写以跳过加解密。 */
    protected JSONObject buildResponse(JSONObject biz) throws Exception
    {
        return gateway.buildNotifyResponse(biz, new Date());
    }

    private ResponseEntity<String> ack(String respcd, String resptx)
    {
        JSONObject biz = new JSONObject();
        biz.put("respcd", respcd);
        biz.put("resptx", resptx == null ? "" : resptx.substring(0, Math.min(resptx.length(), 500)));
        try
        {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(buildResponse(biz).toJSONString());
        }
        catch (Exception e)
        {
            log.error("银行退汇通知应答构建失败：{}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isReturnNotify(String tranCode)
    {
        return "ylthtz".equals(tranCode) || "OBK0071707".equals(tranCode);
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }
}
