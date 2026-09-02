package com.ruoyi.bank.gateway;

import java.util.Date;

public class BankQueryRequest
{
    private String merId;
    private String originalRequestNo;
    private Date originalRequestTime;
    private String bankSerialNo;

    public String getMerId() { return merId; }
    public void setMerId(String merId) { this.merId = merId; }
    public String getOriginalRequestNo() { return originalRequestNo; }
    public void setOriginalRequestNo(String originalRequestNo) { this.originalRequestNo = originalRequestNo; }
    public Date getOriginalRequestTime() { return originalRequestTime; }
    public void setOriginalRequestTime(Date originalRequestTime) { this.originalRequestTime = originalRequestTime; }
    public String getBankSerialNo() { return bankSerialNo; }
    public void setBankSerialNo(String bankSerialNo) { this.bankSerialNo = bankSerialNo; }
}
