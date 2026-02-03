package com.ruoyi.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 机构解除监管申请对象 release_supervision
 *
 * @author ruoyi
 * @date 2025-01-04
 */
public class ReleaseSupervision extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 解除监管申请ID */
    private Long releaseId;

    /** 申请编号 */
    @Excel(name = "申请编号")
    private String applyNo;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 统一信用代码 */
    @Excel(name = "统一信用代码")
    private String creditCode;

    /** 法定代表人 */
    @Excel(name = "法定代表人")
    private String legalPerson;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactPerson;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 解除原因 */
    @Excel(name = "解除原因")
    private String releaseReason;

    /** 监管账户总余额 */
    @Excel(name = "监管账户总余额")
    private BigDecimal supervisionBalance;

    /** 预收服务费余额 */
    @Excel(name = "预收服务费余额")
    private BigDecimal serviceFeeBalance;

    /** 预收押金余额 */
    @Excel(name = "预收押金余额")
    private BigDecimal depositBalance;

    /** 预收会员费余额 */
    @Excel(name = "预收会员费余额")
    private BigDecimal memberFeeBalance;

    /** 监管开户银行 */
    @Excel(name = "监管开户银行")
    private String supervisionBank;

    /** 监管账号 */
    @Excel(name = "监管账号")
    private String supervisionAccount;

    /** 基本账户银行 */
    @Excel(name = "基本账户银行")
    private String basicBank;

    /** 基本账号 */
    @Excel(name = "基本账号")
    private String basicAccount;

    /** 申请状态（0待审批 1已批准 2已驳回） */
    @Excel(name = "申请状态", readConverterExp = "0=待审批,1=已批准,2=已驳回")
    private String applyStatus;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /** 审批人 */
    @Excel(name = "审批人")
    private String approver;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审批时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    /** 审批意见 */
    @Excel(name = "审批意见")
    private String approveRemark;

    /** 驳回原因 */
    @Excel(name = "驳回原因")
    private String rejectReason;

    /** 附件列表（不映射到数据库，仅用于接收前端���据） */
    private List<Map<String, String>> attachments;

    public void setReleaseId(Long releaseId)
    {
        this.releaseId = releaseId;
    }

    public Long getReleaseId()
    {
        return releaseId;
    }

    public void setApplyNo(String applyNo)
    {
        this.applyNo = applyNo;
    }

    public String getApplyNo()
    {
        return applyNo;
    }

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

    public void setReleaseReason(String releaseReason)
    {
        this.releaseReason = releaseReason;
    }

    public String getReleaseReason()
    {
        return releaseReason;
    }

    public void setSupervisionBalance(BigDecimal supervisionBalance)
    {
        this.supervisionBalance = supervisionBalance;
    }

    public BigDecimal getSupervisionBalance()
    {
        return supervisionBalance;
    }

    public void setServiceFeeBalance(BigDecimal serviceFeeBalance)
    {
        this.serviceFeeBalance = serviceFeeBalance;
    }

    public BigDecimal getServiceFeeBalance()
    {
        return serviceFeeBalance;
    }

    public void setDepositBalance(BigDecimal depositBalance)
    {
        this.depositBalance = depositBalance;
    }

    public BigDecimal getDepositBalance()
    {
        return depositBalance;
    }

    public void setMemberFeeBalance(BigDecimal memberFeeBalance)
    {
        this.memberFeeBalance = memberFeeBalance;
    }

    public BigDecimal getMemberFeeBalance()
    {
        return memberFeeBalance;
    }

    public void setSupervisionBank(String supervisionBank)
    {
        this.supervisionBank = supervisionBank;
    }

    public String getSupervisionBank()
    {
        return supervisionBank;
    }

    public void setSupervisionAccount(String supervisionAccount)
    {
        this.supervisionAccount = supervisionAccount;
    }

    public String getSupervisionAccount()
    {
        return supervisionAccount;
    }

    public void setBasicBank(String basicBank)
    {
        this.basicBank = basicBank;
    }

    public String getBasicBank()
    {
        return basicBank;
    }

    public void setBasicAccount(String basicAccount)
    {
        this.basicAccount = basicAccount;
    }

    public String getBasicAccount()
    {
        return basicAccount;
    }

    public void setApplyStatus(String applyStatus)
    {
        this.applyStatus = applyStatus;
    }

    public String getApplyStatus()
    {
        return applyStatus;
    }

    public void setApplyTime(Date applyTime)
    {
        this.applyTime = applyTime;
    }

    public Date getApplyTime()
    {
        return applyTime;
    }

    public void setApprover(String approver)
    {
        this.approver = approver;
    }

    public String getApprover()
    {
        return approver;
    }

    public void setApproveTime(Date approveTime)
    {
        this.approveTime = approveTime;
    }

    public Date getApproveTime()
    {
        return approveTime;
    }

    public void setApproveRemark(String approveRemark)
    {
        this.approveRemark = approveRemark;
    }

    public String getApproveRemark()
    {
        return approveRemark;
    }

    public void setRejectReason(String rejectReason)
    {
        this.rejectReason = rejectReason;
    }

    public String getRejectReason()
    {
        return rejectReason;
    }

    public void setAttachments(List<Map<String, String>> attachments)
    {
        this.attachments = attachments;
    }

    public List<Map<String, String>> getAttachments()
    {
        return attachments;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("releaseId", getReleaseId())
            .append("applyNo", getApplyNo())
            .append("institutionId", getInstitutionId())
            .append("institutionName", getInstitutionName())
            .append("creditCode", getCreditCode())
            .append("legalPerson", getLegalPerson())
            .append("contactPerson", getContactPerson())
            .append("contactPhone", getContactPhone())
            .append("releaseReason", getReleaseReason())
            .append("supervisionBalance", getSupervisionBalance())
            .append("serviceFeeBalance", getServiceFeeBalance())
            .append("depositBalance", getDepositBalance())
            .append("memberFeeBalance", getMemberFeeBalance())
            .append("supervisionBank", getSupervisionBank())
            .append("supervisionAccount", getSupervisionAccount())
            .append("basicBank", getBasicBank())
            .append("basicAccount", getBasicAccount())
            .append("applyStatus", getApplyStatus())
            .append("applyTime", getApplyTime())
            .append("approver", getApprover())
            .append("approveTime", getApproveTime())
            .append("approveRemark", getApproveRemark())
            .append("rejectReason", getRejectReason())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
