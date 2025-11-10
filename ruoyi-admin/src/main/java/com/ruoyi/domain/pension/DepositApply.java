package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 押金使用申请对象 deposit_apply
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public class DepositApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 申请ID */
    private Long applyId;

    /** 申请单号 */
    @Excel(name = "申请单号")
    private String applyNo;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 账户ID */
    @Excel(name = "账户ID")
    private Long accountId;

    /** 申请金额 */
    @Excel(name = "申请金额")
    private BigDecimal applyAmount;

    /** 申请原因 */
    @Excel(name = "申请原因")
    private String applyReason;

    /** 申请类型 */
    @Excel(name = "申请类型", dictType = "deposit_apply_type")
    private String applyType;

    /** 紧急程度(1-紧急 2-普通 3-一般) */
    @Excel(name = "紧急程度", dictType = "urgency_level")
    private String urgencyLevel;

    /** 申请状态(0-待审批 1-已批准 2-已拒绝 3-已撤销) */
    @Excel(name = "申请状态", dictType = "deposit_apply_status")
    private String applyStatus;

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

    /** 实际使用金额 */
    @Excel(name = "实际使用金额")
    private BigDecimal actualAmount;

    /** 使用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "使用时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date useTime;

    // 关联查询字段
    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderName;

    /** 机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 账户余额 */
    @Excel(name = "账户余额")
    private BigDecimal accountBalance;

    public void setApplyId(Long applyId)
    {
        this.applyId = applyId;
    }

    public Long getApplyId()
    {
        return applyId;
    }

    public void setApplyNo(String applyNo)
    {
        this.applyNo = applyNo;
    }

    public String getApplyNo()
    {
        return applyNo;
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

    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }

    public Long getAccountId()
    {
        return accountId;
    }

    public void setApplyAmount(BigDecimal applyAmount)
    {
        this.applyAmount = applyAmount;
    }

    public BigDecimal getApplyAmount()
    {
        return applyAmount;
    }

    public void setApplyReason(String applyReason)
    {
        this.applyReason = applyReason;
    }

    public String getApplyReason()
    {
        return applyReason;
    }

    public void setApplyType(String applyType)
    {
        this.applyType = applyType;
    }

    public String getApplyType()
    {
        return applyType;
    }

    public void setUrgencyLevel(String urgencyLevel)
    {
        this.urgencyLevel = urgencyLevel;
    }

    public String getUrgencyLevel()
    {
        return urgencyLevel;
    }

    public void setApplyStatus(String applyStatus)
    {
        this.applyStatus = applyStatus;
    }

    public String getApplyStatus()
    {
        return applyStatus;
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

    public void setActualAmount(BigDecimal actualAmount)
    {
        this.actualAmount = actualAmount;
    }

    public BigDecimal getActualAmount()
    {
        return actualAmount;
    }

    public void setUseTime(Date useTime)
    {
        this.useTime = useTime;
    }

    public Date getUseTime()
    {
        return useTime;
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

    public void setAccountBalance(BigDecimal accountBalance)
    {
        this.accountBalance = accountBalance;
    }

    public BigDecimal getAccountBalance()
    {
        return accountBalance;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("applyId", getApplyId())
            .append("applyNo", getApplyNo())
            .append("elderId", getElderId())
            .append("institutionId", getInstitutionId())
            .append("accountId", getAccountId())
            .append("applyAmount", getApplyAmount())
            .append("applyReason", getApplyReason())
            .append("applyType", getApplyType())
            .append("urgencyLevel", getUrgencyLevel())
            .append("applyStatus", getApplyStatus())
            .append("approver", getApprover())
            .append("approveTime", getApproveTime())
            .append("approveRemark", getApproveRemark())
            .append("actualAmount", getActualAmount())
            .append("useTime", getUseTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("elderName", getElderName())
            .append("institutionName", getInstitutionName())
            .append("accountBalance", getAccountBalance())
            .toString();
    }
}
