package com.ruoyi.domain.pension;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 划付规则配置对象 transfer_rule_config
 *
 * @author ruoyi
 * @date 2025-01-27
 */
public class TransferRuleConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long ruleId;

    /** 规则名称 */
    @Excel(name = "规则名称")
    private String ruleName;

    /** 划付周期(monthly-按月,quarterly-按季度,yearly-按年) */
    @Excel(name = "划付周期")
    private String transferCycle;

    /** 划付日期(1-31) */
    @Excel(name = "划付日期")
    private Integer transferDay;

    /** 划付时间(HH:mm格式) */
    @Excel(name = "划付时���")
    private String transferTime;

    /** 状态(0-正常 1-停用) */
    @Excel(name = "状态")
    private String status;

    public void setRuleId(Long ruleId)
    {
        this.ruleId = ruleId;
    }

    public Long getRuleId()
    {
        return ruleId;
    }

    public void setRuleName(String ruleName)
    {
        this.ruleName = ruleName;
    }

    public String getRuleName()
    {
        return ruleName;
    }

    public void setTransferCycle(String transferCycle)
    {
        this.transferCycle = transferCycle;
    }

    public String getTransferCycle()
    {
        return transferCycle;
    }

    public void setTransferDay(Integer transferDay)
    {
        this.transferDay = transferDay;
    }

    public Integer getTransferDay()
    {
        return transferDay;
    }

    public void setTransferTime(String transferTime)
    {
        this.transferTime = transferTime;
    }

    public String getTransferTime()
    {
        return transferTime;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("ruleId", getRuleId())
            .append("ruleName", getRuleName())
            .append("transferCycle", getTransferCycle())
            .append("transferDay", getTransferDay())
            .append("transferTime", getTransferTime())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
