package com.ruoyi.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 老人入住申请对象 elder_check_in
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public class ElderCheckIn extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 入住申请ID */
    private Long checkInId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 分配的床位ID */
    @Excel(name = "床位ID")
    private Long bedId;

    /** 申请日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "申请日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date applyDate;

    /** 期望入住日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "期望入住日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expectedCheckInDate;

    /** 实际入住日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "实际入住日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date actualCheckInDate;

    /** 入住状态(0申请中 1已入住 2已拒绝 3已取消) */
    @Excel(name = "入住状态", readConverterExp = "0=申请中,1=已入住,2=已拒绝,3=已取消")
    private String checkInStatus;

    /** 护理等级(1自理 2半护理 3全护理) */
    @Excel(name = "护理等级", readConverterExp = "1=自理,2=半护理,3=全护理")
    private String careLevel;

    /** 月费用(元) */
    @Excel(name = "月费用")
    private BigDecimal monthlyFee;

    /** 押金金额(元) */
    @Excel(name = "押金金额")
    private BigDecimal depositAmount;

    /** 支付方式 */
    @Excel(name = "支付方式")
    private String paymentMethod;

    /** 面谈时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "面谈时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date interviewDate;

    /** 面谈结果 */
    @Excel(name = "面谈结果")
    private String interviewResult;

    /** 审批人 */
    @Excel(name = "审批人")
    private String approvalUser;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审批时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date approvalTime;

    /** 审批意见 */
    @Excel(name = "审批意见")
    private String approvalRemark;

    /** 关联的老人姓名 */
    @Excel(name = "老人姓名")
    private String elderName;

    /** 关联的机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 关联的房间号 */
    @Excel(name = "房间号")
    private String roomNumber;

    /** 关联的床位号 */
    @Excel(name = "床位号")
    private String bedNumber;

    public void setCheckInId(Long checkInId)
    {
        this.checkInId = checkInId;
    }

    public Long getCheckInId()
    {
        return checkInId;
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
    public void setBedId(Long bedId)
    {
        this.bedId = bedId;
    }

    public Long getBedId()
    {
        return bedId;
    }
    public void setApplyDate(Date applyDate)
    {
        this.applyDate = applyDate;
    }

    public Date getApplyDate()
    {
        return applyDate;
    }
    public void setExpectedCheckInDate(Date expectedCheckInDate)
    {
        this.expectedCheckInDate = expectedCheckInDate;
    }

    public Date getExpectedCheckInDate()
    {
        return expectedCheckInDate;
    }
    public void setActualCheckInDate(Date actualCheckInDate)
    {
        this.actualCheckInDate = actualCheckInDate;
    }

    public Date getActualCheckInDate()
    {
        return actualCheckInDate;
    }
    public void setCheckInStatus(String checkInStatus)
    {
        this.checkInStatus = checkInStatus;
    }

    public String getCheckInStatus()
    {
        return checkInStatus;
    }
    public void setCareLevel(String careLevel)
    {
        this.careLevel = careLevel;
    }

    public String getCareLevel()
    {
        return careLevel;
    }
    public void setMonthlyFee(BigDecimal monthlyFee)
    {
        this.monthlyFee = monthlyFee;
    }

    public BigDecimal getMonthlyFee()
    {
        return monthlyFee;
    }
    public void setDepositAmount(BigDecimal depositAmount)
    {
        this.depositAmount = depositAmount;
    }

    public BigDecimal getDepositAmount()
    {
        return depositAmount;
    }
    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod()
    {
        return paymentMethod;
    }
    public void setInterviewDate(Date interviewDate)
    {
        this.interviewDate = interviewDate;
    }

    public Date getInterviewDate()
    {
        return interviewDate;
    }
    public void setInterviewResult(String interviewResult)
    {
        this.interviewResult = interviewResult;
    }

    public String getInterviewResult()
    {
        return interviewResult;
    }
    public void setApprovalUser(String approvalUser)
    {
        this.approvalUser = approvalUser;
    }

    public String getApprovalUser()
    {
        return approvalUser;
    }
    public void setApprovalTime(Date approvalTime)
    {
        this.approvalTime = approvalTime;
    }

    public Date getApprovalTime()
    {
        return approvalTime;
    }
    public void setApprovalRemark(String approvalRemark)
    {
        this.approvalRemark = approvalRemark;
    }

    public String getApprovalRemark()
    {
        return approvalRemark;
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
    public void setRoomNumber(String roomNumber)
    {
        this.roomNumber = roomNumber;
    }

    public String getRoomNumber()
    {
        return roomNumber;
    }
    public void setBedNumber(String bedNumber)
    {
        this.bedNumber = bedNumber;
    }

    public String getBedNumber()
    {
        return bedNumber;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("checkInId", getCheckInId())
            .append("elderId", getElderId())
            .append("institutionId", getInstitutionId())
            .append("bedId", getBedId())
            .append("applyDate", getApplyDate())
            .append("expectedCheckInDate", getExpectedCheckInDate())
            .append("actualCheckInDate", getActualCheckInDate())
            .append("checkInStatus", getCheckInStatus())
            .append("careLevel", getCareLevel())
            .append("monthlyFee", getMonthlyFee())
            .append("depositAmount", getDepositAmount())
            .append("paymentMethod", getPaymentMethod())
            .append("interviewDate", getInterviewDate())
            .append("interviewResult", getInterviewResult())
            .append("approvalUser", getApprovalUser())
            .append("approvalTime", getApprovalTime())
            .append("approvalRemark", getApprovalRemark())
            .append("elderName", getElderName())
            .append("institutionName", getInstitutionName())
            .append("roomNumber", getRoomNumber())
            .append("bedNumber", getBedNumber())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}