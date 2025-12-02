package com.ruoyi.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 老人附件对象 elder_attachment
 *
 * @author ruoyi
 * @date 2025-12-02
 */
public class ElderAttachment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 附件ID */
    private Long attachmentId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 附件类型(1身份证正面 2身份证反面) */
    @Excel(name = "附件类型", readConverterExp = "1=身份证正面,2=身份证反面")
    private String attachmentType;

    /** 文件路径 */
    @Excel(name = "文件路径")
    private String filePath;

    /** 文件名 */
    @Excel(name = "文件名")
    private String fileName;

    /** 文件大小 */
    @Excel(name = "文件大小")
    private Long fileSize;

    /** 创建时间 */
    private java.util.Date createTime;

    public void setAttachmentId(Long attachmentId)
    {
        this.attachmentId = attachmentId;
    }

    public Long getAttachmentId()
    {
        return attachmentId;
    }

    public void setElderId(Long elderId)
    {
        this.elderId = elderId;
    }

    public Long getElderId()
    {
        return elderId;
    }

    public void setAttachmentType(String attachmentType)
    {
        this.attachmentType = attachmentType;
    }

    public String getAttachmentType()
    {
        return attachmentType;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }

    public Long getFileSize()
    {
        return fileSize;
    }

    public void setCreateTime(java.util.Date createTime)
    {
        this.createTime = createTime;
    }

    public java.util.Date getCreateTime()
    {
        return createTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("attachmentId", getAttachmentId())
            .append("elderId", getElderId())
            .append("attachmentType", getAttachmentType())
            .append("filePath", getFilePath())
            .append("fileName", getFileName())
            .append("fileSize", getFileSize())
            .append("createTime", getCreateTime())
            .toString();
    }
}