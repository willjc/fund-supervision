package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资金划拨明细对象 fund_transfer_detail
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public class FundTransferDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long detailId;

    /** 划拨ID */
    @Excel(name = "划拨ID")
    private Long transferId;

    /** 账户ID */
    @Excel(name = "账户ID")
    private Long accountId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 划拨金额 */
    @Excel(name = "划拨金额")
    private BigDecimal transferAmount;

    /** 服务费 */
    @Excel(name = "服务费")
    private BigDecimal serviceFee;

    /** 护理费 */
    @Excel(name = "护理费")
    private BigDecimal careFee;

    /** 伙食费 */
    @Excel(name = "伙食费")
    private BigDecimal foodFee;

    /** 其他费用 */
    @Excel(name = "其他费用")
    private BigDecimal otherFee;

    /** 结算月份(YYYY-MM) */
    @Excel(name = "结算月份")
    private String billingMonth;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // 关联字段（用于展示）
    @Excel(name = "老人姓名")
    private String elderName;

    @Excel(name = "身份证号")
    private String idCard;

    public void setDetailId(Long detailId)
    {
        this.detailId = detailId;
    }

    public Long getDetailId()
    {
        return detailId;
    }

    public void setTransferId(Long transferId)
    {
        this.transferId = transferId;
    }

    public Long getTransferId()
    {
        return transferId;
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

    public void setTransferAmount(BigDecimal transferAmount)
    {
        this.transferAmount = transferAmount;
    }

    public BigDecimal getTransferAmount()
    {
        return transferAmount;
    }

    public void setServiceFee(BigDecimal serviceFee)
    {
        this.serviceFee = serviceFee;
    }

    public BigDecimal getServiceFee()
    {
        return serviceFee;
    }

    public void setCareFee(BigDecimal careFee)
    {
        this.careFee = careFee;
    }

    public BigDecimal getCareFee()
    {
        return careFee;
    }

    public void setFoodFee(BigDecimal foodFee)
    {
        this.foodFee = foodFee;
    }

    public BigDecimal getFoodFee()
    {
        return foodFee;
    }

    public void setOtherFee(BigDecimal otherFee)
    {
        this.otherFee = otherFee;
    }

    public BigDecimal getOtherFee()
    {
        return otherFee;
    }

    public void setBillingMonth(String billingMonth)
    {
        this.billingMonth = billingMonth;
    }

    public String getBillingMonth()
    {
        return billingMonth;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("detailId", getDetailId())
            .append("transferId", getTransferId())
            .append("accountId", getAccountId())
            .append("elderId", getElderId())
            .append("transferAmount", getTransferAmount())
            .append("serviceFee", getServiceFee())
            .append("careFee", getCareFee())
            .append("foodFee", getFoodFee())
            .append("otherFee", getOtherFee())
            .append("billingMonth", getBillingMonth())
            .append("createTime", getCreateTime())
            .toString();
    }
}