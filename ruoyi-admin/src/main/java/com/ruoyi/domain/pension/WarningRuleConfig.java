package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 预警规则配置对象 warning_rule_config
 *
 * @author ruoyi
 * @date 2025-01-29
 */
public class WarningRuleConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long ruleId;

    /** 规则编码 */
    @Excel(name = "规则编码")
    private String ruleCode;

    /** 规则名称 */
    @Excel(name = "规则名称")
    private String ruleName;

    /** 预警类型(1-预收费用超额 2-押金超额 3-入住人数超额 4-预收总额超额 5-风险保证金超低 6-大额支出 7-交易对方风险) */
    @Excel(name = "预警类型", readConverterExp = "1=预收费用超额,2=押金超额,3=入住人数超额,4=预收总额超额,5=风险保证金超低,6=大额支出,7=交易对方风险")
    private String warningType;

    /** 阈值参数(倍数或金额) */
    @Excel(name = "阈值参数")
    private BigDecimal thresholdValue;

    /** 阈值单位(倍/元/百分比/开关) */
    @Excel(name = "阈值单位")
    private String thresholdUnit;

    /** 最小值限制 */
    @Excel(name = "最小值")
    private BigDecimal minValue;

    /** 最大值限制 */
    @Excel(name = "最大值")
    private BigDecimal maxValue;

    /** 启用状态(0-禁用 1-启用) */
    @Excel(name = "启用状态", readConverterExp = "0=禁用,1=启用")
    private String enabled;

    /** 排序号 */
    @Excel(name = "排序")
    private Integer sortOrder;

    public void setRuleId(Long ruleId)
    {
        this.ruleId = ruleId;
    }

    public Long getRuleId()
    {
        return ruleId;
    }

    public void setRuleCode(String ruleCode)
    {
        this.ruleCode = ruleCode;
    }

    public String getRuleCode()
    {
        return ruleCode;
    }

    public void setRuleName(String ruleName)
    {
        this.ruleName = ruleName;
    }

    public String getRuleName()
    {
        return ruleName;
    }

    public void setWarningType(String warningType)
    {
        this.warningType = warningType;
    }

    public String getWarningType()
    {
        return warningType;
    }

    public void setThresholdValue(BigDecimal thresholdValue)
    {
        this.thresholdValue = thresholdValue;
    }

    public BigDecimal getThresholdValue()
    {
        return thresholdValue;
    }

    public void setThresholdUnit(String thresholdUnit)
    {
        this.thresholdUnit = thresholdUnit;
    }

    public String getThresholdUnit()
    {
        return thresholdUnit;
    }

    public void setMinValue(BigDecimal minValue)
    {
        this.minValue = minValue;
    }

    public BigDecimal getMinValue()
    {
        return minValue;
    }

    public void setMaxValue(BigDecimal maxValue)
    {
        this.maxValue = maxValue;
    }

    public BigDecimal getMaxValue()
    {
        return maxValue;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }

    public String getEnabled()
    {
        return enabled;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("ruleId", getRuleId())
            .append("ruleCode", getRuleCode())
            .append("ruleName", getRuleName())
            .append("warningType", getWarningType())
            .append("thresholdValue", getThresholdValue())
            .append("thresholdUnit", getThresholdUnit())
            .append("minValue", getMinValue())
            .append("maxValue", getMaxValue())
            .append("enabled", getEnabled())
            .append("sortOrder", getSortOrder())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
