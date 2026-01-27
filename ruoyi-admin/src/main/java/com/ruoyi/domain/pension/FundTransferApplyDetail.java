package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资金划拨申请明细对象 fund_transfer_apply_detail
 *
 * @author ruoyi
 * @date 2026-01-28
 */
public class FundTransferApplyDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long detailId;

    /** 申请ID */
    @Excel(name = "申请ID")
    private Long applyId;

    /** 划拨单ID */
    @Excel(name = "划拨单ID")
    private Long transferId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 划拨金额 */
    @Excel(name = "划拨金额")
    private BigDecimal transferAmount;

    /** 账单月份 */
    @Excel(name = "账单月份")
    private String billingMonth;

    /** 划拨单号（关联字段） */
    @Excel(name = "划拨单号")
    private String transferNo;

    /** 老人姓名（关联字段） */
    @Excel(name = "老人姓名")
    private String elderName;

    public void setDetailId(Long detailId)
    {
        this.detailId = detailId;
    }

    public Long getDetailId()
    {
        return detailId;
    }

    public void setApplyId(Long applyId)
    {
        this.applyId = applyId;
    }

    public Long getApplyId()
    {
        return applyId;
    }

    public void setTransferId(Long transferId)
    {
        this.transferId = transferId;
    }

    public Long getTransferId()
    {
        return transferId;
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

    public void setBillingMonth(String billingMonth)
    {
        this.billingMonth = billingMonth;
    }

    public String getBillingMonth()
    {
        return billingMonth;
    }

    public String getTransferNo() {
        return transferNo;
    }

    public void setTransferNo(String transferNo) {
        this.transferNo = transferNo;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }
}
