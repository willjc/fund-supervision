package com.ruoyi.domain.pension;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 预警信息对象 supervision_warning
 *
 * @author ruoyi
 * @date 2026-02-03
 */
public class SupervisionWarning extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 预警ID */
    private Long warningId;

    /** 预警编号 */
    @Excel(name = "预警编号")
    private String warningNo;

    /** 机构ID */
    private Long institutionId;

    /** 机构名称（冗余） */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 预警类型(1-预收费用超额 2-押金超额 3-入住人数超额 4-预收总额超额 5-风险保证金超低 6-大额支出 7-交易对方风险) */
    @Excel(name = "预警类型", readConverterExp = "1=预收费用超额,2=押金超额,3=入住人数超额,4=预收总额超额,5=风险保证金超低,6=大额支出,7=交易对方风险")
    private String warningType;

    /** 预警内容 */
    @Excel(name = "预警内容")
    private String warningContent;

    /** 预警级别(高/中/低) */
    @Excel(name = "预警级别")
    private String warningLevel;

    /** 预警状态(0-待处理 1-已处理) */
    @Excel(name = "���警状态", readConverterExp = "0=待处理,1=已处理")
    private String warningStatus;

    /** 机构联系人 */
    @Excel(name = "机构联系人")
    private String contactPerson;

    /** 机构联系电话 */
    @Excel(name = "机构联系电话")
    private String contactPhone;

    /** 处理人 */
    @Excel(name = "处理人")
    private String handler;

    /** 处理时间 */
    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    /** 处理备注 */
    @Excel(name = "处理备注")
    private String handleRemark;

    /** 是否已通知(0-否 1-是) */
    private String isNotified;

    /** 通知时间 */
    private Date notifyTime;

    public void setWarningId(Long warningId)
    {
        this.warningId = warningId;
    }

    public Long getWarningId()
    {
        return warningId;
    }

    public void setWarningNo(String warningNo)
    {
        this.warningNo = warningNo;
    }

    public String getWarningNo()
    {
        return warningNo;
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

    public void setWarningType(String warningType)
    {
        this.warningType = warningType;
    }

    public String getWarningType()
    {
        return warningType;
    }

    public void setWarningContent(String warningContent)
    {
        this.warningContent = warningContent;
    }

    public String getWarningContent()
    {
        return warningContent;
    }

    public void setWarningLevel(String warningLevel)
    {
        this.warningLevel = warningLevel;
    }

    public String getWarningLevel()
    {
        return warningLevel;
    }

    public void setWarningStatus(String warningStatus)
    {
        this.warningStatus = warningStatus;
    }

    public String getWarningStatus()
    {
        return warningStatus;
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

    public void setHandler(String handler)
    {
        this.handler = handler;
    }

    public String getHandler()
    {
        return handler;
    }

    public void setHandleTime(Date handleTime)
    {
        this.handleTime = handleTime;
    }

    public Date getHandleTime()
    {
        return handleTime;
    }

    public void setHandleRemark(String handleRemark)
    {
        this.handleRemark = handleRemark;
    }

    public String getHandleRemark()
    {
        return handleRemark;
    }

    public void setIsNotified(String isNotified)
    {
        this.isNotified = isNotified;
    }

    public String getIsNotified()
    {
        return isNotified;
    }

    public void setNotifyTime(Date notifyTime)
    {
        this.notifyTime = notifyTime;
    }

    public Date getNotifyTime()
    {
        return notifyTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("warningId", getWarningId())
            .append("warningNo", getWarningNo())
            .append("institutionId", getInstitutionId())
            .append("institutionName", getInstitutionName())
            .append("warningType", getWarningType())
            .append("warningContent", getWarningContent())
            .append("warningLevel", getWarningLevel())
            .append("warningStatus", getWarningStatus())
            .append("contactPerson", getContactPerson())
            .append("contactPhone", getContactPhone())
            .append("handler", getHandler())
            .append("handleTime", getHandleTime())
            .append("handleRemark", getHandleRemark())
            .append("isNotified", getIsNotified())
            .append("notifyTime", getNotifyTime())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
