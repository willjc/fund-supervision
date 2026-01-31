package com.ruoyi.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 机构黑名单对象 institution_blacklist
 *
 * @author ruoyi
 * @date 2025-01-31
 */
public class InstitutionBlacklist extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 黑���单类型 */
    @Excel(name = "黑名单类型")
    private String blacklistType;

    /** 原因描述 */
    @Excel(name = "原因描述")
    private String reason;

    /** 状态:1生效中 2已解除 */
    @Excel(name = "状态", readConverterExp = "1=生效中,2=已解除")
    private String status;

    /** 加入时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "加入时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date addTime;

    /** 移除时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "移除时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date removeTime;

    /** 处理意见 */
    @Excel(name = "处理意见")
    private String handleOpinion;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
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

    public void setBlacklistType(String blacklistType)
    {
        this.blacklistType = blacklistType;
    }

    public String getBlacklistType()
    {
        return blacklistType;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getReason()
    {
        return reason;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setAddTime(Date addTime)
    {
        this.addTime = addTime;
    }

    public Date getAddTime()
    {
        return addTime;
    }

    public void setRemoveTime(Date removeTime)
    {
        this.removeTime = removeTime;
    }

    public Date getRemoveTime()
    {
        return removeTime;
    }

    public void setHandleOpinion(String handleOpinion)
    {
        this.handleOpinion = handleOpinion;
    }

    public String getHandleOpinion()
    {
        return handleOpinion;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("institutionId", getInstitutionId())
            .append("institutionName", getInstitutionName())
            .append("blacklistType", getBlacklistType())
            .append("reason", getReason())
            .append("status", getStatus())
            .append("addTime", getAddTime())
            .append("removeTime", getRemoveTime())
            .append("handleOpinion", getHandleOpinion())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
