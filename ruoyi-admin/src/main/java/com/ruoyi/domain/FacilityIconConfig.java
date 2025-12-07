package com.ruoyi.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设施图标配置对象 facility_icon_config
 *
 * @author ruoyi
 * @date 2025-01-07
 */
public class FacilityIconConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long id;

    /** 设施名称 */
    @Excel(name = "设施名称")
    private String facilityName;

    /** 图标名称 */
    @Excel(name = "图标名称")
    private String iconName;

    /** 设施类型：life-生活设施，medical-医疗设施 */
    @Excel(name = "设施类型", readConverterExp = "life=生活设施,medical=医疗设施")
    private String facilityType;

    /** 排序序号 */
    @Excel(name = "排序序号")
    private Integer sortOrder;

    /** 状态：0-启用，1-停用 */
    @Excel(name = "状态", readConverterExp = "0=启用,1=停用")
    private String status;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setFacilityName(String facilityName)
    {
        this.facilityName = facilityName;
    }

    public String getFacilityName()
    {
        return facilityName;
    }
    public void setIconName(String iconName)
    {
        this.iconName = iconName;
    }

    public String getIconName()
    {
        return iconName;
    }
    public void setFacilityType(String facilityType)
    {
        this.facilityType = facilityType;
    }

    public String getFacilityType()
    {
        return facilityType;
    }
    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
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
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("facilityName", getFacilityName())
            .append("iconName", getIconName())
            .append("facilityType", getFacilityType())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}