package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 交易流水记录对象 transaction_record
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public class TransactionRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 交易ID */
    private Long transactionId;

    /** 账户ID */
    @Excel(name = "账户ID")
    private Long accountId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 交易流水号 */
    @Excel(name = "交易流水号")
    private String transactionNo;

    /** 交易类型(1入账 2出账) */
    @Excel(name = "交易类型", readConverterExp = "1=入账,2=出账")
    private String transactionType;

    /** 业务类型(1缴费 2月度扣费 3押金使用 4退费) */
    @Excel(name = "业务类型", readConverterExp = "1=缴费,2=月度扣费,3=押金使用,4=退费")
    private String businessType;

    /** 交易金额 */
    @Excel(name = "交易金额")
    private BigDecimal amount;

    /** 交易前余额 */
    @Excel(name = "交易前余额")
    private BigDecimal balanceBefore;

    /** 交易后余额 */
    @Excel(name = "交易后余额")
    private BigDecimal balanceAfter;

    /** 服务费余额 */
    @Excel(name = "服务费余额")
    private BigDecimal serviceBalance;

    /** 押金余额 */
    @Excel(name = "押金余额")
    private BigDecimal depositBalance;

    /** 会员费余额 */
    @Excel(name = "会员费余额")
    private BigDecimal memberBalance;

    /** 交易时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "交易时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date transactionDate;

    /** 关联订单ID */
    @Excel(name = "关联订单ID")
    private Long relatedOrderId;

    /** 关联划拨ID */
    @Excel(name = "关联划拨ID")
    private Long relatedTransferId;

    /** 交易对手 */
    @Excel(name = "交易对手")
    private String counterparty;

    /** 业务描述 */
    @Excel(name = "业务描述")
    private String businessDesc;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operator;

    // 关联字段（用于展示）
    @Excel(name = "老人姓名")
    private String elderName;

    @Excel(name = "机构名称")
    private String institutionName;

    public void setTransactionId(Long transactionId)
    {
        this.transactionId = transactionId;
    }

    public Long getTransactionId()
    {
        return transactionId;
    }

    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }

    public Long getAccountId()
    {
        return accountId;
    }

    public void setElderId(Long elderId)
    {
        this.elderId = elderId;
    }

    public Long getElderId()
    {
        return elderId;
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

    public void setBusinessType(String businessType)
    {
        this.businessType = businessType;
    }

    public String getBusinessType()
    {
        return businessType;
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

    public void setServiceBalance(BigDecimal serviceBalance)
    {
        this.serviceBalance = serviceBalance;
    }

    public BigDecimal getServiceBalance()
    {
        return serviceBalance;
    }

    public void setDepositBalance(BigDecimal depositBalance)
    {
        this.depositBalance = depositBalance;
    }

    public BigDecimal getDepositBalance()
    {
        return depositBalance;
    }

    public void setMemberBalance(BigDecimal memberBalance)
    {
        this.memberBalance = memberBalance;
    }

    public BigDecimal getMemberBalance()
    {
        return memberBalance;
    }

    public void setTransactionDate(Date transactionDate)
    {
        this.transactionDate = transactionDate;
    }

    public Date getTransactionDate()
    {
        return transactionDate;
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

    public void setCounterparty(String counterparty)
    {
        this.counterparty = counterparty;
    }

    public String getCounterparty()
    {
        return counterparty;
    }

    public void setBusinessDesc(String businessDesc)
    {
        this.businessDesc = businessDesc;
    }

    public String getBusinessDesc()
    {
        return businessDesc;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getOperator()
    {
        return operator;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
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
            .append("transactionId", getTransactionId())
            .append("accountId", getAccountId())
            .append("elderId", getElderId())
            .append("institutionId", getInstitutionId())
            .append("transactionNo", getTransactionNo())
            .append("transactionType", getTransactionType())
            .append("businessType", getBusinessType())
            .append("amount", getAmount())
            .append("balanceBefore", getBalanceBefore())
            .append("balanceAfter", getBalanceAfter())
            .append("serviceBalance", getServiceBalance())
            .append("depositBalance", getDepositBalance())
            .append("memberBalance", getMemberBalance())
            .append("transactionDate", getTransactionDate())
            .append("relatedOrderId", getRelatedOrderId())
            .append("relatedTransferId", getRelatedTransferId())
            .append("counterparty", getCounterparty())
            .append("businessDesc", getBusinessDesc())
            .append("operator", getOperator())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}