package com.ruoyi.bank.gateway;

import java.util.Date;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.exception.ServiceException;

public interface BankGateway
{
    BankResult createPayment(BankPaymentRequest request);

    BankResult queryPayment(BankQueryRequest request);

    BankResult verifyMerchant(String merId, String settlementAccountNo);

    default BankResult refundPayment(BankRefundRequest request)
    {
        throw new ServiceException("原路退款网关未启用");
    }

    /** OBK0071701（ylcntr）监管签约；成功结果的 agreementNo 为银行返回的 jgxyno。 */
    default BankResult signSupervisionContract(BankSupervisionSignRequest request)
    {
        throw new ServiceException("监管签约网关未启用");
    }

    /** OBK0071704（ylqywh）签约维护：携带原协议号更新账户资料，协议号不变，仅返回成功/失败。 */
    default BankResult maintainSupervisionContract(String agreementNo, BankSupervisionSignRequest request)
    {
        throw new ServiceException("监管签约维护网关未启用");
    }

    /** OBK0071705（ylptjy）解约：按原协议号和监管账户解除银行签约。 */
    default BankResult cancelSupervisionContract(String agreementNo, String superviseAccountNo,
            String superviseAccountName)
    {
        throw new ServiceException("监管解约网关未启用");
    }

    default boolean supportsPayout()
    {
        return false;
    }

    default BankResult submitPayout(BankPayoutRequest request)
    {
        throw new ServiceException("监管拨付协议尚未确认，禁止发送 ylzjhb；请先取得银行报文、验签及结果码确认");
    }

    default BankResult queryPayout(BankPayoutRequest request)
    {
        throw new ServiceException("监管拨付查询协议尚未确认，不能查询 ylhzqy");
    }

    default BankResult queryBalance(String accountNo, String accountName)
    {
        throw new ServiceException("监管余额查询协议尚未确认，不能查询 ylyeqy");
    }

    /** OBK0071708（ylmxqy）监管账户交易明细查询；默认未启用。 */
    default java.util.List<BankAccountDetail> queryAccountDetails(String accountNo, String stardt, String enddat,
            String pagenm, String rcrdnm)
    {
        throw new ServiceException("监管明细查询协议尚未确认，不能查询 ylmxqy");
    }

    /** 入站信封开启结果：tranCode/tranSeq 供协议校验，biz 为解密后的业务明文。 */
    final class InboundEnvelope
    {
        public final String tranCode;
        public final String tranSeq;
        public final JSONObject biz;

        public InboundEnvelope(String tranCode, String tranSeq, JSONObject biz)
        {
            this.tranCode = tranCode;
            this.tranSeq = tranSeq;
            this.biz = biz;
        }
    }

    /** 开启银行入站信封（银行调我方，如 OBK0071707）：验签解密通过才返回；默认未启用。 */
    default InboundEnvelope openInboundEnvelope(String rawBody)
    {
        throw new ServiceException("银行通知协议未启用");
    }

    /** 银行通知应答信封（镜像出站）：sign/加密用我方私钥与银行公钥；默认未启用。 */
    default JSONObject buildNotifyResponse(JSONObject biz, Date now) throws Exception
    {
        throw new ServiceException("银行通知协议未启用");
    }
}
