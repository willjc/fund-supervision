package com.ruoyi.domain.bank;

import java.math.BigDecimal;
import java.util.Date;

public class BankPaymentCompletionResult
{
    private boolean alreadyProcessed;
    private Long orderId;
    private String orderNo;
    private BigDecimal paidAmount;
    private Date paymentTime;

    public static BankPaymentCompletionResult completed(Long orderId, String orderNo,
            BigDecimal paidAmount, Date paymentTime, boolean alreadyProcessed)
    {
        BankPaymentCompletionResult result = new BankPaymentCompletionResult();
        result.setOrderId(orderId);
        result.setOrderNo(orderNo);
        result.setPaidAmount(paidAmount);
        result.setPaymentTime(paymentTime);
        result.setAlreadyProcessed(alreadyProcessed);
        return result;
    }

    public boolean isAlreadyProcessed() { return alreadyProcessed; }
    public void setAlreadyProcessed(boolean alreadyProcessed) { this.alreadyProcessed = alreadyProcessed; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public Date getPaymentTime() { return paymentTime; }
    public void setPaymentTime(Date paymentTime) { this.paymentTime = paymentTime; }
}
