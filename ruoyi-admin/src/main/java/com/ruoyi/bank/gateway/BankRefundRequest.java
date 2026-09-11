package com.ruoyi.bank.gateway;

import java.math.BigDecimal;
import java.util.Date;

import com.ruoyi.common.exception.ServiceException;

/**
 * uTxnRefund 原路退款请求。锚定原支付交易靠 originalBankSerialNo(origRespTxnSsn)
 * 与 originalBankTime(origRespTxnTime)，二者均来自原支付 bank_transaction 的银行事实。
 */
public class BankRefundRequest
{
    private String requestNo;
    private Date requestTime;
    private String merId;
    private String merName;
    private BigDecimal amount;
    private String originalRequestNo;
    private String originalBankSerialNo;
    private String originalBankTime;

    public void validate()
    {
        if (requestNo == null || requestNo.length() < 10 || requestNo.length() > 32)
        {
            throw new ServiceException("退款请求号长度必须在10到32之间");
        }
        if (requestTime == null)
        {
            throw new ServiceException("退款请求时间为空");
        }
        if (merId == null || merId.trim().isEmpty())
        {
            throw new ServiceException("退款商户号为空");
        }
        if (amount == null || amount.signum() <= 0)
        {
            throw new ServiceException("退款金额必须大于零");
        }
        if (originalRequestNo == null || originalRequestNo.trim().isEmpty())
        {
            throw new ServiceException("原支付请求号为空，无法原路退款");
        }
        if (originalBankSerialNo == null || originalBankSerialNo.trim().isEmpty())
        {
            throw new ServiceException("原支付银行流水号为空，无法原路退款");
        }
        if (originalBankTime == null || !originalBankTime.matches("[0-9]{14}"))
        {
            throw new ServiceException("原支付银行交易时间无效，无法原路退款");
        }
    }

    public String getRequestNo() { return requestNo; }
    public void setRequestNo(String requestNo) { this.requestNo = requestNo; }
    public Date getRequestTime() { return requestTime; }
    public void setRequestTime(Date requestTime) { this.requestTime = requestTime; }
    public String getMerId() { return merId; }
    public void setMerId(String merId) { this.merId = merId; }
    public String getMerName() { return merName; }
    public void setMerName(String merName) { this.merName = merName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getOriginalRequestNo() { return originalRequestNo; }
    public void setOriginalRequestNo(String originalRequestNo) { this.originalRequestNo = originalRequestNo; }
    public String getOriginalBankSerialNo() { return originalBankSerialNo; }
    public void setOriginalBankSerialNo(String originalBankSerialNo) { this.originalBankSerialNo = originalBankSerialNo; }
    public String getOriginalBankTime() { return originalBankTime; }
    public void setOriginalBankTime(String originalBankTime) { this.originalBankTime = originalBankTime; }
}
