package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监管账户流水对象 supervision_account_log
 *
 * @author ruoyi
 * @date 2025-01-26
 */
public class SupervisionAccountLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long logId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 交易流水号 */
    @Excel(name = "交易流水号")
    private String transactionNo;

    /** 交易类型(收入/支出) */
    @Excel(name = "交易类型")
    private String transactionType;

    /** 交易金额 */
    @Excel(name = "交易金额")
    private BigDecimal amount;

    /** 交易前余额 */
    @Excel(name = "交易前余额")
    private BigDecimal balanceBefore;

    /** 交易后余额 */
    @Excel(name = "交易后余额")
    private BigDecimal balanceAfter;

    /** 交易时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "交易时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date transactionTime;

    /** 业务类型(用户支付/押金划拨/退款等) */
    @Excel(name = "业务类型")
    private String businessType;

    /** 关联订单ID */
    @Excel(name = "关联订单ID")
    private Long relatedOrderId;

    /** 关联划拨ID */
    @Excel(name = "关联划拨ID")
    private Long relatedTransferId;

    /** 业务描述 */
    @Excel(name = "业务描述")
    private String businessDesc;

    /** 交易对手 */
    @Excel(name = "交易对手")
    private String counterparty;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operator;

    // 关联字段（用于展示）
    @Excel(name = "机构名称")
    private String institutionName;

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public Long getLogId()
    {
        return logId;
    }

    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
    }

    public void setTransactionNo(String transactionNo)
    {
        this.transactionNo = transactionNo;
    }

    public String getTransactionNo()
    {
        return transactionNo;
    }

    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }

    public String getTransactionType()
    {
        return transactionType;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setBalanceBefore(BigDecimal balanceBefore)
    {
        this.balanceBefore = balanceBefore;
    }

    public BigDecimal getBalanceBefore()
    {
        return balanceBefore;
    }

    public void setBalanceAfter(BigDecimal balanceAfter)
    {
        this.balanceAfter = balanceAfter;
    }

    public BigDecimal getBalanceAfter()
    {
        return balanceAfter;
    }

    public void setTransactionTime(Date transactionTime)
    {
        this.transactionTime = transactionTime;
    }

    public Date getTransactionTime()
    {
        return transactionTime;
    }

    public void setBusinessType(String businessType)
    {
        this.businessType = businessType;
    }

    public String getBusinessType()
    {
        return businessType;
    }

    public void setRelatedOrderId(Long relatedOrderId)
    {
        this.relatedOrderId = relatedOrderId;
    }

    public Long getRelatedOrderId()
    {
        return relatedOrderId;
    }

    public void setRelatedTransferId(Long relatedTransferId)
    {
        this.relatedTransferId = relatedTransferId;
    }

    public Long getRelatedTransferId()
    {
        return relatedTransferId;
    }

    public void setBusinessDesc(String businessDesc)
    {
        this.businessDesc = businessDesc;
    }

    public String getBusinessDesc()
    {
        return businessDesc;
    }

    public void setCounterparty(String counterparty)
    {
        this.counterparty = counterparty;
    }

    public String getCounterparty()
    {
        return counterparty;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getOperator()
    {
        return operator;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("logId", getLogId())
            .append("institutionId", getInstitutionId())
            .append("transactionNo", getTransactionNo())
            .append("transactionType", getTransactionType())
            .append("amount", getAmount())
            .append("balanceBefore", getBalanceBefore())
            .append("balanceAfter", getBalanceAfter())
            .append("transactionTime", getTransactionTime())
            .append("businessType", getBusinessType())
            .append("relatedOrderId", getRelatedOrderId())
            .append("relatedTransferId", getRelatedTransferId())
            .append("businessDesc", getBusinessDesc())
            .append("counterparty", getCounterparty())
            .append("operator", getOperator())
            .append("createTime", getCreateTime())
            .toString();
    }
}
