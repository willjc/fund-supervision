package com.ruoyi.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 区域街道信息对象 area_street
 *
 * @author ruoyi
 * @date 2025-12-04
 */
public class AreaStreet extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 区县代码 */
    @Excel(name = "区县代码")
    private String areaCode;

    /** 区县名称 */
    @Excel(name = "区县名称")
    private String areaName;

    /** 街道代码(可选) */
    @Excel(name = "街道代码")
    private String streetCode;

    /** 街道名称 */
    @Excel(name = "街道名称")
    private String streetName;

    /** 排序 */
    @Excel(name = "排序")
    private Long sortOrder;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setAreaCode(String areaCode)
    {
        this.areaCode = areaCode;
    }

    public String getAreaCode()
    {
        return areaCode;
    }
    public void setAreaName(String areaName)
    {
        this.areaName = areaName;
    }

    public String getAreaName()
    {
        return areaName;
    }
    public void setStreetCode(String streetCode)
    {
        this.streetCode = streetCode;
    }

    public String getStreetCode()
    {
        return streetCode;
    }
    public void setStreetName(String streetName)
    {
        this.streetName = streetName;
    }

    public String getStreetName()
    {
        return streetName;
    }
    public void setSortOrder(Long sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public Long getSortOrder()
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
            .append("areaCode", getAreaCode())
            .append("areaName", getAreaName())
            .append("streetCode", getStreetCode())
            .append("streetName", getStreetName())
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