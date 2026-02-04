package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 餐费配置对象 meal_fee_config
 *
 * @author ruoyi
 * @date 2026-02-04
 */
public class MealFeeConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long configId;

    /** 机构ID */
    private Long institutionId;

    /** 机构名称（用于显示） */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 餐费档次(低档/中档/高档) */
    @Excel(name = "餐费档次")
    private String mealLevel;

    /** 档次代码(L/M/H) */
    private String mealLevelCode;

    /** 餐费价格(元/月) */
    @Excel(name = "餐费价格(元/月)")
    private BigDecimal price;

    /** 餐费说明 */
    @Excel(name = "餐费说明")
    private String mealDescription;

    /** 是否启用(0禁用 1启用) */
    @Excel(name = "是否启用", readConverterExp = "0=禁用,1=启用")
    private String isAvailable;

    /** 排序 */
    private Integer sortOrder;

    /** 当前用户ID（用于数据权限过滤） */
    private Long currentUserId;

    public void setConfigId(Long configId)
    {
        this.configId = configId;
    }

    public Long getConfigId()
    {
        return configId;
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

    public void setMealLevel(String mealLevel)
    {
        this.mealLevel = mealLevel;
    }

    public String getMealLevel()
    {
        return mealLevel;
    }

    public void setMealLevelCode(String mealLevelCode)
    {
        this.mealLevelCode = mealLevelCode;
    }

    public String getMealLevelCode()
    {
        return mealLevelCode;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setMealDescription(String mealDescription)
    {
        this.mealDescription = mealDescription;
    }

    public String getMealDescription()
    {
        return mealDescription;
    }

    public void setIsAvailable(String isAvailable)
    {
        this.isAvailable = isAvailable;
    }

    public String getIsAvailable()
    {
        return isAvailable;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public Long getCurrentUserId()
    {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId)
    {
        this.currentUserId = currentUserId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("configId", getConfigId())
            .append("institutionId", getInstitutionId())
            .append("mealLevel", getMealLevel())
            .append("mealLevelCode", getMealLevelCode())
            .append("price", getPrice())
            .append("mealDescription", getMealDescription())
            .append("isAvailable", getIsAvailable())
            .append("sortOrder", getSortOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
