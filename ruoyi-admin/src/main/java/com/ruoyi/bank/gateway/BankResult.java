package com.ruoyi.bank.gateway;

public class BankResult
{
    private String requestNo;
    private String status;
    private String bankSerialNo;
    private String payUrl;
    private String responseCode;
    private String responseMessage;

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
}
