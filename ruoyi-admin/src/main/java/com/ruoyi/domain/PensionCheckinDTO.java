package com.ruoyi.domain;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 养老机构入驻DTO
 *
 * @author ruoyi
 * @date 2025-11-11
 */
public class PensionCheckinDTO
{
    // ========== 老人信息 ==========
    /** 入住人姓名 */
    private String elderName;

    /** 性别(0男 1女 2未知) */
    private String gender;

    /** 身份证号 */
    private String idCard;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    /** 年龄 */
    private Integer age;

    /** 联系电话 */
    private String phone;

    /** 护理等级(1自理 2半护理 3全护理) */
    private String careLevel;

    /** 健康状况 */
    private String healthStatus;

    /** 家庭住址 */
    private String address;

    /** 紧急联系人 */
    private String emergencyContact;

    /** 紧急联系电话 */
    private String emergencyPhone;

    /** 特殊需求 */
    private String specialNeeds;

    // ========== 床位信息 ==========
    /** 床位ID */
    private Long bedId;

    /** 入住日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date checkInDate;

    // ========== 费用信息 ==========
    /** 床位费 */
    private BigDecimal bedFee;

    /** 护理费 */
    private BigDecimal careFee;

    /** 餐费 */
    private BigDecimal mealFee;

    /** 餐费档�� */
    private String mealLevel;

    /** 月服务费(床位费+护理费+餐费，用于存储合计) */
    private BigDecimal monthlyFee;

    /** 入驻月数 */
    private Integer monthCount;

    /** 押金金额 */
    private BigDecimal depositAmount;

    /** 会员费 */
    private BigDecimal memberFee;

    /** 实收总计(可优惠后的最终金额) */
    private BigDecimal finalAmount;

    /** 费用说明 */
    private String feeDescription;

    // ========== 支付信息 ==========
    /** 支付方式(cash现金 card刷卡 scan扫码 later稍后支付) */
    private String paymentMethod;

    /** 备注 */
    private String remark;

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(String careLevel) {
        this.careLevel = careLevel;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getSpecialNeeds() {
        return specialNeeds;
    }

    public void setSpecialNeeds(String specialNeeds) {
        this.specialNeeds = specialNeeds;
    }

    public Long getBedId() {
        return bedId;
    }

    public void setBedId(Long bedId) {
        this.bedId = bedId;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public BigDecimal getBedFee() {
        return bedFee;
    }

    public void setBedFee(BigDecimal bedFee) {
        this.bedFee = bedFee;
    }

    public BigDecimal getCareFee() {
        return careFee;
    }

    public void setCareFee(BigDecimal careFee) {
        this.careFee = careFee;
    }

    public BigDecimal getMealFee() {
        return mealFee;
    }

    public void setMealFee(BigDecimal mealFee) {
        this.mealFee = mealFee;
    }

    public String getMealLevel() {
        return mealLevel;
    }

    public void setMealLevel(String mealLevel) {
        this.mealLevel = mealLevel;
    }

    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(BigDecimal monthlyFee) {
        this.monthlyFee = monthlyFee;
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

    public String getFeeDescription() {
        return feeDescription;
    }

    public void setFeeDescription(String feeDescription) {
        this.feeDescription = feeDescription;
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
