package com.ruoyi.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 养老机构信息对象 pension_institution
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public class PensionInstitution extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 机构ID */
    private Long institutionId;

    /** 机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 统一信用代码 */
    @Excel(name = "统一信用代码")
    private String creditCode;

    /** 注册资金 */
    @Excel(name = "注册资金")
    private Double registeredCapital;

    /** 注册地址 */
    @Excel(name = "注册地址")
    private String registeredAddress;

    /** 所属街道/区域 */
    @Excel(name = "所属街道/区域")
    private String street;

    /** 实际经营地址 */
    @Excel(name = "实际经营地址")
    private String actualAddress;

    /** 法定代表人 */
    @Excel(name = "法定代表人")
    private String legalPerson;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactPerson;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 联系邮箱 */
    @Excel(name = "联系邮箱")
    private String contactEmail;

    /** 经营范围 */
    @Excel(name = "经营范围")
    private String businessScope;

    /** 机构类型 */
    @Excel(name = "机构类型", readConverterExp = "1=民办,2=公办,3=公建民营")
    private String institutionType;

    /** 床位数 */
    @Excel(name = "床位数")
    private Long bedCount;

    /** 收费区间 */
    @Excel(name = "收费区间")
    private String feeRange;

    /** 成立时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "成立时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date establishedDate;

    /** 兴办机构 */
    @Excel(name = "兴办机构")
    private String organizer;

    /** 负责人姓名 */
    @Excel(name = "负责人姓名")
    private String responsibleName;

    /** 负责人身份证号 */
    @Excel(name = "负责人身份证号")
    private String responsibleIdCard;

    /** 负责人居住地 */
    @Excel(name = "负责人居住地")
    private String responsibleAddress;

    /** 负责人电话 */
    @Excel(name = "负责人电话")
    private String responsiblePhone;

    /** 备案号 */
    @Excel(name = "备案号")
    private String recordNumber;

    /** 固定资产净额 */
    @Excel(name = "固定资产净额")
    private Double fixedAssets;

    /** 营业执照文件路径 */
    @Excel(name = "营业执照")
    private String businessLicense;

    /** 批准证书文件路径 */
    @Excel(name = "批准证书")
    private String approvalCertificate;

    /** 监管协议文件路径 */
    @Excel(name = "监管协议")
    private String supervisionAgreement;

    /** 基本结算账户 */
    @Excel(name = "基本结算账户")
    private String bankAccount;

    /** 监管账户 */
    @Excel(name = "监管账户")
    private String superviseAccount;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=待审批,1=已入驻,2=已驳回,3=解除监管,4=草稿,5=维护中,6=维护待审批")
    private String status;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审批时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    /** 审批人 */
    @Excel(name = "审批人")
    private String approveUser;

    /** 审批意见 */
    @Excel(name = "审批意见")
    private String approveRemark;

    /** 黑名单标志 */
    @Excel(name = "黑名单标志", readConverterExp = "0=正常,1=黑名单")
    private String blacklistFlag;

    /** 当前用户ID(用于数据权限过滤,不映射到数据库) */
    private Long currentUserId;

    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
    }

    public void setInstitutionName(String institutionName)
    {
        this.institutionName = institutionName;
    }

    public String getInstitutionName()
    {
        return institutionName;
    }

    public void setCreditCode(String creditCode)
    {
        this.creditCode = creditCode;
    }

    public String getCreditCode()
    {
        return creditCode;
    }

    public void setRegisteredCapital(Double registeredCapital)
    {
        this.registeredCapital = registeredCapital;
    }

    public Double getRegisteredCapital()
    {
        return registeredCapital;
    }

    public void setRegisteredAddress(String registeredAddress)
    {
        this.registeredAddress = registeredAddress;
    }

    public String getRegisteredAddress()
    {
        return registeredAddress;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getStreet()
    {
        return street;
    }

    public void setActualAddress(String actualAddress)
    {
        this.actualAddress = actualAddress;
    }

    public String getActualAddress()
    {
        return actualAddress;
    }

    public void setLegalPerson(String legalPerson)
    {
        this.legalPerson = legalPerson;
    }

    public String getLegalPerson()
    {
        return legalPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }

    public String getContactEmail()
    {
        return contactEmail;
    }

    public void setBusinessScope(String businessScope)
    {
        this.businessScope = businessScope;
    }

    public String getBusinessScope()
    {
        return businessScope;
    }

    public void setInstitutionType(String institutionType)
    {
        this.institutionType = institutionType;
    }

    public String getInstitutionType()
    {
        return institutionType;
    }

    public void setBedCount(Long bedCount)
    {
        this.bedCount = bedCount;
    }

    public Long getBedCount()
    {
        return bedCount;
    }

    public void setFeeRange(String feeRange)
    {
        this.feeRange = feeRange;
    }

    public String getFeeRange()
    {
        return feeRange;
    }

    public void setEstablishedDate(Date establishedDate)
    {
        this.establishedDate = establishedDate;
    }

    public Date getEstablishedDate()
    {
        return establishedDate;
    }

    public void setOrganizer(String organizer)
    {
        this.organizer = organizer;
    }

    public String getOrganizer()
    {
        return organizer;
    }

    public void setResponsibleName(String responsibleName)
    {
        this.responsibleName = responsibleName;
    }

    public String getResponsibleName()
    {
        return responsibleName;
    }

    public void setResponsibleIdCard(String responsibleIdCard)
    {
        this.responsibleIdCard = responsibleIdCard;
    }

    public String getResponsibleIdCard()
    {
        return responsibleIdCard;
    }

    public void setResponsibleAddress(String responsibleAddress)
    {
        this.responsibleAddress = responsibleAddress;
    }

    public String getResponsibleAddress()
    {
        return responsibleAddress;
    }

    public void setResponsiblePhone(String responsiblePhone)
    {
        this.responsiblePhone = responsiblePhone;
    }

    public String getResponsiblePhone()
    {
        return responsiblePhone;
    }

    public void setRecordNumber(String recordNumber)
    {
        this.recordNumber = recordNumber;
    }

    public String getRecordNumber()
    {
        return recordNumber;
    }

    public void setFixedAssets(Double fixedAssets)
    {
        this.fixedAssets = fixedAssets;
    }

    public Double getFixedAssets()
    {
        return fixedAssets;
    }

    public void setBusinessLicense(String businessLicense)
    {
        this.businessLicense = businessLicense;
    }

    public String getBusinessLicense()
    {
        return businessLicense;
    }

    public void setApprovalCertificate(String approvalCertificate)
    {
        this.approvalCertificate = approvalCertificate;
    }

    public String getApprovalCertificate()
    {
        return approvalCertificate;
    }

    public void setSupervisionAgreement(String supervisionAgreement)
    {
        this.supervisionAgreement = supervisionAgreement;
    }

    public String getSupervisionAgreement()
    {
        return supervisionAgreement;
    }

    public void setBankAccount(String bankAccount)
    {
        this.bankAccount = bankAccount;
    }

    public String getBankAccount()
    {
        return bankAccount;
    }

    public void setSuperviseAccount(String superviseAccount)
    {
        this.superviseAccount = superviseAccount;
    }

    public String getSuperviseAccount()
    {
        return superviseAccount;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setApplyTime(Date applyTime)
    {
        this.applyTime = applyTime;
    }

    public Date getApplyTime()
    {
        return applyTime;
    }

    public void setApproveTime(Date approveTime)
    {
        this.approveTime = approveTime;
    }

    public Date getApproveTime()
    {
        return approveTime;
    }

    public void setApproveUser(String approveUser)
    {
        this.approveUser = approveUser;
    }

    public String getApproveUser()
    {
        return approveUser;
    }

    public void setApproveRemark(String approveRemark)
    {
        this.approveRemark = approveRemark;
    }

    public String getApproveRemark()
    {
        return approveRemark;
    }

    public void setBlacklistFlag(String blacklistFlag)
    {
        this.blacklistFlag = blacklistFlag;
    }

    public String getBlacklistFlag()
    {
        return blacklistFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("institutionId", getInstitutionId())
            .append("institutionName", getInstitutionName())
            .append("creditCode", getCreditCode())
            .append("registeredCapital", getRegisteredCapital())
            .append("registeredAddress", getRegisteredAddress())
            .append("street", getStreet())
            .append("actualAddress", getActualAddress())
            .append("legalPerson", getLegalPerson())
            .append("contactPerson", getContactPerson())
            .append("contactPhone", getContactPhone())
            .append("contactEmail", getContactEmail())
            .append("businessScope", getBusinessScope())
            .append("institutionType", getInstitutionType())
            .append("bedCount", getBedCount())
            .append("feeRange", getFeeRange())
            .append("establishedDate", getEstablishedDate())
            .append("organizer", getOrganizer())
            .append("responsibleName", getResponsibleName())
            .append("responsibleIdCard", getResponsibleIdCard())
            .append("responsibleAddress", getResponsibleAddress())
            .append("responsiblePhone", getResponsiblePhone())
            .append("recordNumber", getRecordNumber())
            .append("fixedAssets", getFixedAssets())
            .append("businessLicense", getBusinessLicense())
            .append("approvalCertificate", getApprovalCertificate())
            .append("supervisionAgreement", getSupervisionAgreement())
            .append("bankAccount", getBankAccount())
            .append("superviseAccount", getSuperviseAccount())
            .append("status", getStatus())
            .append("applyTime", getApplyTime())
            .append("approveTime", getApproveTime())
            .append("approveUser", getApproveUser())
            .append("approveRemark", getApproveRemark())
            .append("blacklistFlag", getBlacklistFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }

    public Long getCurrentUserId()
    {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId)
    {
        this.currentUserId = currentUserId;
    }
}
