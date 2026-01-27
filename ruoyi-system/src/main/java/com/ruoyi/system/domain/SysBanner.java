package com.ruoyi.system.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.xss.Xss;

/**
 * 幻灯片管理表 sys_banner
 *
 * @author ruoyi
 */
public class SysBanner extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 幻灯片ID */
    private Long bannerId;

    /** 标题 */
    private String title;

    /** 图片URL */
    private String imageUrl;

    /** 链接类型：1-内部 2-外部 */
    private String linkType;

    /** 链接值（内部路径或外部URL） */
    private String linkValue;

    /** 排序号 */
    private Integer sort;

    /** 状态：0-正常 1-停用 */
    private String status;

    public Long getBannerId()
    {
        return bannerId;
    }

    public void setBannerId(Long bannerId)
    {
        this.bannerId = bannerId;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @Xss(message = "标题不能包含脚本字符")
    @Size(min = 0, max = 100, message = "标题不能超过100个字符")
    public String getTitle()
    {
        return title;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    @NotBlank(message = "图片URL不能为空")
    @Size(min = 0, max = 500, message = "图片URL不能超过500个字符")
    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setLinkType(String linkType)
    {
        this.linkType = linkType;
    }

    public String getLinkType()
    {
        return linkType;
    }

    public void setLinkValue(String linkValue)
    {
        this.linkValue = linkValue;
    }

    @Size(min = 0, max = 200, message = "链接值不能超过200个字符")
    public String getLinkValue()
    {
        return linkValue;
    }

    public void setSort(Integer sort)
    {
        this.sort = sort;
    }

    public Integer getSort()
    {
        return sort;
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
            .append("bannerId", getBannerId())
            .append("title", getTitle())
            .append("imageUrl", getImageUrl())
            .append("linkType", getLinkType())
            .append("linkValue", getLinkValue())
            .append("sort", getSort())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
