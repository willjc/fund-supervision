package com.ruoyi.domain.pension;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 机构附件对象 pension_institution_attach
 *
 * @author ruoyi
 * @date 2025-01-25
 */
public class PensionInstitutionAttach extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 附件类型常量 */
    public static final String TYPE_VR = "0";         // VR全景图
    public static final String TYPE_MAIN = "1";       // 主图
    public static final String TYPE_ENVIRONMENT = "2"; // 环境图片
    public static final String TYPE_ROOM = "3";       // 房间图片
    public static final String TYPE_BASIC = "4";      // 基础设施
    public static final String TYPE_PARK = "5";       // 园址设施

    /** 附件ID */
    private Long attachId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 附件类型(0-VR全景图 1-主图 2-环境图片 3-房间图片 4-基础设施 5-园址设施) */
    @Excel(name = "附件类型")
    private String attachType;

    /** 附件名称 */
    @Excel(name = "附件名称")
    private String attachName;

    /** 附件路��� */
    @Excel(name = "附件路径")
    private String attachPath;

    /** 文件大小(字节) */
    @Excel(name = "文件大小")
    private Long fileSize;

    /** 文件类型 */
    @Excel(name = "文件类型")
    private String fileType;

    public void setAttachId(Long attachId)
    {
        this.attachId = attachId;
    }

    public Long getAttachId()
    {
        return attachId;
    }

    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
    }

    public void setAttachType(String attachType)
    {
        this.attachType = attachType;
    }

    public String getAttachType()
    {
        return attachType;
    }

    public void setAttachName(String attachName)
    {
        this.attachName = attachName;
    }

    public String getAttachName()
    {
        return attachName;
    }

    public void setAttachPath(String attachPath)
    {
        this.attachPath = attachPath;
    }

    public String getAttachPath()
    {
        return attachPath;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    public String getFileType()
    {
        return fileType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("attachId", getAttachId())
            .append("institutionId", getInstitutionId())
            .append("attachType", getAttachType())
            .append("attachName", getAttachName())
            .append("attachPath", getAttachPath())
            .append("fileSize", getFileSize())
            .append("fileType", getFileType())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
