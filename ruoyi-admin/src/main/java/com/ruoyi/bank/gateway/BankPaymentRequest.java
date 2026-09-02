package com.ruoyi.bank.gateway;

import java.math.BigDecimal;
import java.util.Date;

public class BankPaymentRequest
{
    private String requestNo;
    private Long businessId;
    private Long institutionId;
    private String merId;
    private BigDecimal amount;
    private String channelType;
    private String subject;
    private Date requestTime;

    public String getRequestNo() { return requestNo; }
    public void setRequestNo(String requestNo) { this.requestNo = requestNo; }
    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }
    public Long getInstitutionId() { return institutionId; }
    public void setInstitutionId(Long institutionId) { this.institutionId = institutionId; }
    public String getMerId() { return merId; }
    public void setMerId(String merId) { this.merId = merId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getChannelType() { return channelType; }
    public void setChannelType(String channelType) { this.channelType = channelType; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public Date getRequestTime() { return requestTime; }
    public void setRequestTime(Date requestTime) { this.requestTime = requestTime; }
}
