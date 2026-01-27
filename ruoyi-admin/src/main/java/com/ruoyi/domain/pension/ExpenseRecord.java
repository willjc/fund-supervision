package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 费用记录对象 expense_record
 *
 * @author ruoyi
 * @date 2025-12-18
 */
public class ExpenseRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long recordId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 账户ID */
    @Excel(name = "账户ID")
    private Long accountId;

    /** 费用类型(deposit:押金,service:服务费,member:会员费,other:其他) */
    @Excel(name = "费用类型", readConverterExp = "deposit=押金,service=服务费,member=会员费,other=其他")
    private String expenseType;

    /** 交易类型(income:收入,expense:支出) */
    @Excel(name = "交易类型", readConverterExp = "income=收入,expense=支出")
    private String transactionType;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal amount;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

    /** 关联ID(如订单ID、押金申请ID等) */
    @Excel(name = "关联ID")
    private Long relatedId;

    /** 关联类型 */
    @Excel(name = "关联类型")
    private String relatedType;

    /** 交易前余额 */
    @Excel(name = "交易前余额")
    private BigDecimal balanceBefore;

    /** 交易后余额 */
    @Excel(name = "交易后余额")
    private BigDecimal balanceAfter;

    /** 老人姓名（非数据库字段，用于显示） */
    private String elderName;

    /** 机构名称（非数据库字段，用于显示） */
    private String institutionName;

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setElderId(Long elderId)
    {
        this.elderId = elderId;
    }

    public Long getElderId()
    {
        return elderId;
    }

    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }

    public Long getAccountId()
    {
        return accountId;
    }

    public void setExpenseType(String expenseType)
    {
        this.expenseType = expenseType;
    }

    public String getExpenseType()
    {
        return expenseType;
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

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getDescription()
    {
        return description;
    }

    public void setRelatedId(Long relatedId)
    {
        this.relatedId = relatedId;
    }

    public Long getRelatedId()
    {
        return relatedId;
    }

    public void setRelatedType(String relatedType)
    {
        this.relatedType = relatedType;
    }

    public String getRelatedType()
    {
        return relatedType;
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

    public void setElderName(String elderName)
    {
        this.elderName = elderName;
    }

    public String getElderName()
    {
        return elderName;
    }

    public void setInstitutionName(String institutionName)
    {
        this.institutionName = institutionName;
    }

    public String getInstitutionName()
    {
        return institutionName;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getCreateTime()
    {
        return super.getCreateTime();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("elderId", getElderId())
            .append("accountId", getAccountId())
            .append("expenseType", getExpenseType())
            .append("transactionType", getTransactionType())
            .append("amount", getAmount())
            .append("description", getDescription())
            .append("relatedId", getRelatedId())
            .append("relatedType", getRelatedType())
            .append("balanceBefore", getBalanceBefore())
            .append("balanceAfter", getBalanceAfter())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}