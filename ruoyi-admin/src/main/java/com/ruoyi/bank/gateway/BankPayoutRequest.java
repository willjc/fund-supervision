package com.ruoyi.bank.gateway;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.ruoyi.common.exception.ServiceException;

/** 后端保存的拨付快照，金额单位为元，不接受前端指定收付款账户。 */
public class BankPayoutRequest
{
    private String requestNo;
    private Date requestTime;
    private String payerAccountNo;
    private String payerAccountName;
    private String payeeAccountNo;
    private String payeeAccountName;
    private String payeeBankNo;
    private boolean crossBank;
    private BigDecimal amount;
    private String remark;

    public void validateQuery()
    {
        require(requestNo, 64, "银行拨付请求号");
        if (requestTime == null)
        {
            throw new ServiceException("银行拨付原请求时间不能为空");
        }
    }

    public void validate()
    {
        validateQuery();
        require(payerAccountNo, 40, "监管账号");
        require(payerAccountName, 256, "监管账户名称");
        require(payeeAccountNo, 32, "收款账号");
        require(payeeAccountName, 256, "收款账户名称");
        if (crossBank || (payeeBankNo != null && !payeeBankNo.trim().isEmpty()))
        {
            require(payeeBankNo, 32, "收款账户联行号");
        }
        if (remark != null && remark.length() > 200)
        {
            throw new ServiceException("拨付备注不能超过200个字符");
        }
        if (amount == null || amount.signum() <= 0)
        {
            throw new ServiceException("拨付金额必须大于0");
        }
        try
        {
            if (amount.setScale(2, RoundingMode.UNNECESSARY).precision() > 18)
            {
                throw new ServiceException("拨付金额超出银行18,2精度限制");
            }
        }
        catch (ArithmeticException e)
        {
            throw new ServiceException("拨付金额最多保留两位小数");
        }
    }

    private void require(String value, int maxLength, String label)
    {
        if (value == null || value.trim().isEmpty() || value.length() > maxLength)
        {
            throw new ServiceException(label + "为空或超过银行字段长度限制");
        }
    }

    public String getRequestNo() { return requestNo; }
    public void setRequestNo(String requestNo) { this.requestNo = requestNo; }
    public Date getRequestTime() { return requestTime; }
    public void setRequestTime(Date requestTime) { this.requestTime = requestTime; }
    public String getPayerAccountNo() { return payerAccountNo; }
    public void setPayerAccountNo(String payerAccountNo) { this.payerAccountNo = payerAccountNo; }
    public String getPayerAccountName() { return payerAccountName; }
    public void setPayerAccountName(String payerAccountName) { this.payerAccountName = payerAccountName; }
    public String getPayeeAccountNo() { return payeeAccountNo; }
    public void setPayeeAccountNo(String payeeAccountNo) { this.payeeAccountNo = payeeAccountNo; }
    public String getPayeeAccountName() { return payeeAccountName; }
    public void setPayeeAccountName(String payeeAccountName) { this.payeeAccountName = payeeAccountName; }
    public String getPayeeBankNo() { return payeeBankNo; }
    public void setPayeeBankNo(String payeeBankNo) { this.payeeBankNo = payeeBankNo; }
    public boolean isCrossBank() { return crossBank; }
    public void setCrossBank(boolean crossBank) { this.crossBank = crossBank; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
