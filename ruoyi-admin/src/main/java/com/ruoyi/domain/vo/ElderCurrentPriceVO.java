package com.ruoyi.domain.vo;

import java.math.BigDecimal;

/**
 * 老人当前价格信息VO
 *
 * @author ruoyi
 * @date 2025-01-19
 */
public class ElderCurrentPriceVO
{
    /** 老人ID */
    private Long elderId;

    /** 床位费 */
    private BigDecimal bedFee;

    /** 床位费原始价格（如果有修改） */
    private BigDecimal bedFeeOriginal;

    /** 床位费是否被修改过 */
    private Boolean bedFeeModified;

    /** 护理费 */
    private BigDecimal careFee;

    /** 护理费原始价格（如果有修改） */
    private BigDecimal careFeeOriginal;

    /** 护理费是否被修改过 */
    private Boolean careFeeModified;

    /** 押金 */
    private BigDecimal depositFee;

    /** 押金原始价格（如果有修改） */
    private BigDecimal depositFeeOriginal;

    /** 押金是否被修改过 */
    private Boolean depositFeeModified;

    /** 会员费 */
    private BigDecimal memberFee;

    /** 会员费原始价格（如果有修改） */
    private BigDecimal memberFeeOriginal;

    /** 会员费是否被修改过 */
    private Boolean memberFeeModified;

    /** 月服务费总计（床位费+护理费） */
    private BigDecimal monthlyFeeTotal;

    /** 最后支付时间 */
    private String lastPaymentTime;

    public Long getElderId() {
        return elderId;
    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }

    public BigDecimal getBedFee() {
        return bedFee;
    }

    public void setBedFee(BigDecimal bedFee) {
        this.bedFee = bedFee;
    }

    public BigDecimal getBedFeeOriginal() {
        return bedFeeOriginal;
    }

    public void setBedFeeOriginal(BigDecimal bedFeeOriginal) {
        this.bedFeeOriginal = bedFeeOriginal;
    }

    public Boolean getBedFeeModified() {
        return bedFeeModified;
    }

    public void setBedFeeModified(Boolean bedFeeModified) {
        this.bedFeeModified = bedFeeModified;
    }

    public BigDecimal getCareFee() {
        return careFee;
    }

    public void setCareFee(BigDecimal careFee) {
        this.careFee = careFee;
    }

    public BigDecimal getCareFeeOriginal() {
        return careFeeOriginal;
    }

    public void setCareFeeOriginal(BigDecimal careFeeOriginal) {
        this.careFeeOriginal = careFeeOriginal;
    }

    public Boolean getCareFeeModified() {
        return careFeeModified;
    }

    public void setCareFeeModified(Boolean careFeeModified) {
        this.careFeeModified = careFeeModified;
    }

    public BigDecimal getDepositFee() {
        return depositFee;
    }

    public void setDepositFee(BigDecimal depositFee) {
        this.depositFee = depositFee;
    }

    public BigDecimal getDepositFeeOriginal() {
        return depositFeeOriginal;
    }

    public void setDepositFeeOriginal(BigDecimal depositFeeOriginal) {
        this.depositFeeOriginal = depositFeeOriginal;
    }

    public Boolean getDepositFeeModified() {
        return depositFeeModified;
    }

    public void setDepositFeeModified(Boolean depositFeeModified) {
        this.depositFeeModified = depositFeeModified;
    }

    public BigDecimal getMemberFee() {
        return memberFee;
    }

    public void setMemberFee(BigDecimal memberFee) {
        this.memberFee = memberFee;
    }

    public BigDecimal getMemberFeeOriginal() {
        return memberFeeOriginal;
    }

    public void setMemberFeeOriginal(BigDecimal memberFeeOriginal) {
        this.memberFeeOriginal = memberFeeOriginal;
    }

    public Boolean getMemberFeeModified() {
        return memberFeeModified;
    }

    public void setMemberFeeModified(Boolean memberFeeModified) {
        this.memberFeeModified = memberFeeModified;
    }

    public BigDecimal getMonthlyFeeTotal() {
        return monthlyFeeTotal;
    }

    public void setMonthlyFeeTotal(BigDecimal monthlyFeeTotal) {
        this.monthlyFeeTotal = monthlyFeeTotal;
    }

    public String getLastPaymentTime() {
        return lastPaymentTime;
    }

    public void setLastPaymentTime(String lastPaymentTime) {
        this.lastPaymentTime = lastPaymentTime;
    }
}
