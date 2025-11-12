package com.ruoyi.domain;

import java.math.BigDecimal;

/**
 * 续费DTO
 *
 * @author ruoyi
 * @date 2025-11-12
 */
public class RenewDTO
{
    /** 老人ID */
    private Long elderId;

    /** 老人姓名 */
    private String elderName;

    /** 续费月数(可为0,仅补缴押金或会员费) */
    private Integer monthCount;

    /** 补交押金金额 */
    private BigDecimal depositAmount;

    /** 补交会员费 */
    private BigDecimal memberFee;

    /** 实收总计 */
    private BigDecimal finalAmount;

    /** 支付方式: cash-现金, card-刷卡, scan-扫码 */
    private String paymentMethod;

    /** 备注 */
    private String remark;

    public Long getElderId() {
        return elderId;
    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public Integer getMonthCount() {
        return monthCount;
    }

    public void setMonthCount(Integer monthCount) {
        this.monthCount = monthCount;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public BigDecimal getMemberFee() {
        return memberFee;
    }

    public void setMemberFee(BigDecimal memberFee) {
        this.memberFee = memberFee;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
