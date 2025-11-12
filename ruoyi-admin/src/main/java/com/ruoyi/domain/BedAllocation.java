package com.ruoyi.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 床位分配记录对象 bed_allocation
 *
 * @author ruoyi
 * @date 2025-11-11
 */
public class BedAllocation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分配记录ID */
    private Long allocationId;

    /** 老人ID */
    private Long elderId;

    /** 床位ID */
    private Long bedId;

    /** 机构ID */
    private Long institutionId;

    /** 入住日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date checkInDate;

    /** 到期日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;

    /** 退住日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date checkOutDate;

    /** 分配状态(1在住 2已退住) */
    private String allocationStatus;

    /** 月费用(元) */
    private BigDecimal monthlyFee;

    /** 押金状态(0未支付 1已支付 2已退还) */
    private String depositStatus;

    /** 押金金额(元) */
    private BigDecimal depositAmount;

    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }

    public Long getElderId() {
        return elderId;
    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }

    public Long getBedId() {
        return bedId;
    }

    public void setBedId(Long bedId) {
        this.bedId = bedId;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getAllocationStatus() {
        return allocationStatus;
    }

    public void setAllocationStatus(String allocationStatus) {
        this.allocationStatus = allocationStatus;
    }

    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(BigDecimal monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public String getDepositStatus() {
        return depositStatus;
    }

    public void setDepositStatus(String depositStatus) {
        this.depositStatus = depositStatus;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }
}
