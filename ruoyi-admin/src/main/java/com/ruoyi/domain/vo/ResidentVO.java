package com.ruoyi.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.PaymentRecord;
import com.ruoyi.common.annotation.Excel;

/**
 * 入住人列表视图对象
 *
 * @author ruoyi
 * @date 2025-11-11
 */
public class ResidentVO
{
    /** 老人ID(作为residentId) */
    @Excel(name = "老人ID")
    private Long residentId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 入住人姓名 */
    @Excel(name = "姓名")
    private String elderName;

    /** 性别(0男 1女 2未知) */
    @Excel(name = "性别", readConverterExp = "0=男,1=女,2=未知")
    private String gender;

    /** 年龄 */
    @Excel(name = "年龄")
    private Long age;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthDate;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String phone;

    /** 床位信息(房间号-床位号) */
    @Excel(name = "床位信息")
    private String bedInfo;

    /** 入住状态(1已入住 2已退住 3请假中) */
    @Excel(name = "入住状态", readConverterExp = "1=已入住,2=已退住,3=请假中")
    private String checkInStatus;

    /** 护理等级(1自理 2半护理 3全护理) */
    @Excel(name = "护理等级", readConverterExp = "1=自理,2=半护理,3=全护理")
    private String careLevel;

    /** 服务费余额 */
    @Excel(name = "服务费余额")
    private BigDecimal serviceBalance;

    /** 押金余额 */
    @Excel(name = "押金余额")
    private BigDecimal depositBalance;

    /** 会员余额 */
    @Excel(name = "会员余额")
    private BigDecimal memberBalance;

    /** 入住日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入住日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date checkInDate;

    /** 到期日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "到期日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dueDate;

    /** 紧急联系人 */
    @Excel(name = "紧急联系人")
    private String emergencyContact;

    /** 家庭住址 */
    @Excel(name = "家庭住址")
    private String address;

    /** 月服务费 */
    @Excel(name = "月服务费")
    private BigDecimal monthlyFee;

    /** 紧急联系人姓名 */
    @Excel(name = "紧急联系人姓名")
    private String emergencyName;

    /** 紧急联系人电话 */
    @Excel(name = "紧急联系人电话")
    private String emergencyPhone;

    /** 紧急联系人关系 */
    @Excel(name = "紧急联系人关系")
    private String emergencyRelation;

    /** 健康状况 */
    @Excel(name = "健康状况")
    private String healthStatus;

    /** 特殊需求 */
    @Excel(name = "特殊需求")
    private String specialNeeds;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 老人照片路径 */
    private String photoPath;

    /** 身份证正面照片路径 */
    private String idCardFrontPath;

    /** 身份证反面照片路径 */
    private String idCardBackPath;

    /** 创建时��� */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 是否有未支付订单 */
    private Boolean hasUnpaidOrder;

    /** 所属机构ID */
    private Long institutionId;

    /** 所属机构名称 */
    @Excel(name = "所属机构")
    private String institutionName;

    /** 账户ID */
    private Long accountId;

    /** 当前用户ID(用于数据权限过滤) */
    private Long currentUserId;

    /** 订单列表 */
    private List<OrderInfo> orders;

    /** 支付记录列表 */
    private List<PaymentRecord> payments;

    /** 待划拨月数 */
    private Integer pendingCount;

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBedInfo() {
        return bedInfo;
    }

    public void setBedInfo(String bedInfo) {
        this.bedInfo = bedInfo;
    }

    public String getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(String checkInStatus) {
        this.checkInStatus = checkInStatus;
    }

    public String getCareLevel() {
        return careLevel;
    }

    public void setCareLevel(String careLevel) {
        this.careLevel = careLevel;
    }

    public BigDecimal getServiceBalance() {
        return serviceBalance;
    }

    public void setServiceBalance(BigDecimal serviceBalance) {
        this.serviceBalance = serviceBalance;
    }

    public BigDecimal getDepositBalance() {
        return depositBalance;
    }

    public void setDepositBalance(BigDecimal depositBalance) {
        this.depositBalance = depositBalance;
    }

    public BigDecimal getMemberBalance() {
        return memberBalance;
    }

    public void setMemberBalance(BigDecimal memberBalance) {
        this.memberBalance = memberBalance;
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

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(BigDecimal monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public String getEmergencyName() {
        return emergencyName;
    }

    public void setEmergencyName(String emergencyName) {
        this.emergencyName = emergencyName;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getEmergencyRelation() {
        return emergencyRelation;
    }

    public void setEmergencyRelation(String emergencyRelation) {
        this.emergencyRelation = emergencyRelation;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getSpecialNeeds() {
        return specialNeeds;
    }

    public void setSpecialNeeds(String specialNeeds) {
        this.specialNeeds = specialNeeds;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getHasUnpaidOrder() {
        return hasUnpaidOrder;
    }

    public void setHasUnpaidOrder(Boolean hasUnpaidOrder) {
        this.hasUnpaidOrder = hasUnpaidOrder;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public List<OrderInfo> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderInfo> orders) {
        this.orders = orders;
    }

    public List<PaymentRecord> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentRecord> payments) {
        this.payments = payments;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getIdCardFrontPath() {
        return idCardFrontPath;
    }

    public void setIdCardFrontPath(String idCardFrontPath) {
        this.idCardFrontPath = idCardFrontPath;
    }

    public String getIdCardBackPath() {
        return idCardBackPath;
    }

    public void setIdCardBackPath(String idCardBackPath) {
        this.idCardBackPath = idCardBackPath;
    }

    public Integer getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(Integer pendingCount) {
        this.pendingCount = pendingCount;
    }
}
