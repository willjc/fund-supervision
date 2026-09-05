package com.ruoyi.domain.bank;

import java.math.BigDecimal;
import java.util.Date;

public class BankTransaction
{
    private String bankTime;
    private String environment;
    private String snapshotJson;
    private Integer attemptNo = 1;
    private String bankStatus;
    private String bookingStatus = "PENDING";
    private Date nextQueryTime;
    private Integer queryCount = 0;
    private Integer manualReview = 0;
    private String returnTime;
    private String returnReason;

    public String getBankTime() { return bankTime; }
    public void setBankTime(String value) { bankTime = value; }
    public String getEnvironment() { return environment; }
    public void setEnvironment(String value) { environment = value; }
    public String getSnapshotJson() { return snapshotJson; }
    public void setSnapshotJson(String value) { snapshotJson = value; }
    public Integer getAttemptNo() { return attemptNo; }
    public void setAttemptNo(Integer value) { attemptNo = value; }
    public String getBankStatus() { return bankStatus; }
    public void setBankStatus(String value) { bankStatus = value; }
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String value) { bookingStatus = value; }
    public Date getNextQueryTime() { return nextQueryTime; }
    public void setNextQueryTime(Date value) { nextQueryTime = value; }
    public Integer getQueryCount() { return queryCount; }
    public void setQueryCount(Integer value) { queryCount = value; }
    public Integer getManualReview() { return manualReview; }
    public void setManualReview(Integer value) { manualReview = value; }
    public String getReturnTime() { return returnTime; }
    public void setReturnTime(String value) { returnTime = value; }
    public String getReturnReason() { return returnReason; }
    public void setReturnReason(String value) { returnReason = value; }
    private Long transactionId;
    private String requestNo;
    private String businessType;
    private Long businessId;
    private Long institutionId;
    private String merId;
    private String bankCode;
    private String channelType;
    private BigDecimal amount;
    private String status;
    private String bankSerialNo;
    private String payUrl;
    private String responseCode;
    private String responseMessage;
    private Date completeTime;
    private Date createTime;
    private Date updateTime;

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    public String getRequestNo() { return requestNo; }
    public void setRequestNo(String requestNo) { this.requestNo = requestNo; }
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public Long getBusinessId() { return businessId; }
    public void setBusinessId(Long businessId) { this.businessId = businessId; }
    public Long getInstitutionId() { return institutionId; }
    public void setInstitutionId(Long institutionId) { this.institutionId = institutionId; }
    public String getMerId() { return merId; }
    public void setMerId(String merId) { this.merId = merId; }
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
    public String getChannelType() { return channelType; }
    public void setChannelType(String channelType) { this.channelType = channelType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBankSerialNo() { return bankSerialNo; }
    public void setBankSerialNo(String bankSerialNo) { this.bankSerialNo = bankSerialNo; }
    public String getPayUrl() { return payUrl; }
    public void setPayUrl(String payUrl) { this.payUrl = payUrl; }
    public String getResponseCode() { return responseCode; }
    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }
    public String getResponseMessage() { return responseMessage; }
    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }
    public Date getCompleteTime() { return completeTime; }
    public void setCompleteTime(Date completeTime) { this.completeTime = completeTime; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
