package com.ruoyi.bank.gateway;

import java.math.BigDecimal;

public class BankResult
{
    private String requestNo;
    private String status;
    private String bankSerialNo;
    private String payUrl;
    private String responseCode;
    private String responseMessage;
    private BigDecimal paidAmount;
    private String bankTransactionTime;
    private String payerAccountNo;
    private String payeeAccountNo;
    private BigDecimal availableBalance;

    public static BankResult unknown(String code, String message)
    {
        BankResult result = failed(code, message);
        result.setStatus("UNKNOWN");
        return result;
    }

    public static BankResult success(String bankSerialNo)
    {
        BankResult result = new BankResult();
        result.setStatus("SUCCESS");
        result.setBankSerialNo(bankSerialNo);
        result.setResponseCode("0000");
        result.setResponseMessage("成功");
        return result;
    }

    public static BankResult failed(String code, String message)
    {
        BankResult result = new BankResult();
        result.setStatus("FAILED");
        result.setResponseCode(code);
        result.setResponseMessage(message);
        return result;
    }

    public static BankResult pending(String bankSerialNo, String payUrl)
    {
        BankResult result = new BankResult();
        result.setStatus("PENDING");
        result.setBankSerialNo(bankSerialNo);
        result.setPayUrl(payUrl);
        result.setResponseCode("ACCEPTED");
        result.setResponseMessage("等待支付结果");
        return result;
    }

    public String getRequestNo() { return requestNo; }
    public void setRequestNo(String requestNo) { this.requestNo = requestNo; }
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
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public String getBankTransactionTime() { return bankTransactionTime; }
    public void setBankTransactionTime(String bankTransactionTime) { this.bankTransactionTime = bankTransactionTime; }
    public String getPayerAccountNo() { return payerAccountNo; }
    public void setPayerAccountNo(String payerAccountNo) { this.payerAccountNo = payerAccountNo; }
    public String getPayeeAccountNo() { return payeeAccountNo; }
    public void setPayeeAccountNo(String payeeAccountNo) { this.payeeAccountNo = payeeAccountNo; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
}
