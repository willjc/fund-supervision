package com.ruoyi.domain.pension;

import java.util.Date;
import java.util.Arrays;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 投诉建议对象 pension_complaint
 *
 * @author ruoyi
 * @date 2025-01-26
 */
public class PensionComplaint extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 状态常量 */
    public static final String STATUS_REJECTED = "0";   // 已拒绝
    public static final String STATUS_PROCESSING = "1"; // 处理中
    public static final String STATUS_PROCESSED = "2";  // 已处理

    /** 投诉ID */
    private Long complaintId;

    /** 投诉编号 */
    @Excel(name = "投诉编号")
    private String complaintNo;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 机构ID列表（用于数据权限过滤，多个机构ID的IN查询） */
    private List<Long> institutionIds;

    /** 机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 提交用户ID */
    @Excel(name = "提交用户ID")
    private Long userId;

    /** 用户名 */
    @Excel(name = "用户名")
    private String userName;

    /** 投诉类型 */
    @Excel(name = "投诉类型")
    private String complaintType;

    /** 投诉标题 */
    @Excel(name = "投诉标题")
    private String title;

    /** 投诉内容 */
    @Excel(name = "投诉内容")
    private String content;

    /** 图片附件(JSON数组) */
    private String images;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactName;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 状态(0-已拒绝 1-处理中 2-已处理) */
    @Excel(name = "状态")
    private String status;

    /** 回复内容 */
    private String replyContent;

    /** 处理人ID */
    private Long handleUserId;

    /** 处理人姓名 */
    @Excel(name = "处理人")
    private String handleUserName;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    public void setComplaintId(Long complaintId)
    {
        this.complaintId = complaintId;
    }

    public Long getComplaintId()
    {
        return complaintId;
    }

    public void setComplaintNo(String complaintNo)
    {
        this.complaintNo = complaintNo;
    }

    public String getComplaintNo()
    {
        return complaintNo;
    }

    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
    }

    public void setInstitutionIds(List<Long> institutionIds)
    {
        this.institutionIds = institutionIds;
    }

    public List<Long> getInstitutionIds()
    {
        return institutionIds;
    }

    public void setInstitutionName(String institutionName)
    {
        this.institutionName = institutionName;
    }

    public String getInstitutionName()
    {
        return institutionName;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setComplaintType(String complaintType)
    {
        this.complaintType = complaintType;
    }

    public String getComplaintType()
    {
        return complaintType;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getImages()
    {
        return images;
    }

    /**
     * 获取图片URL数组
     */
    public String[] getImageArray()
    {
        if (images == null || images.isEmpty())
        {
            return new String[0];
        }
        // 尝试解析JSON数组
        try
        {
            String trimmed = images.trim();
            if (trimmed.startsWith("[") && trimmed.endsWith("]"))
            {
                String content = trimmed.substring(1, trimmed.length() - 1);
                if (content.isEmpty())
                {
                    return new String[0];
                }
                // 使用Jackson ObjectMapper正确解析JSON
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                String[] urls = mapper.readValue("[" + content + "]", String[].class);
                return urls;
            }
            // 如果不是JSON格式，尝试逗号分隔
            return images.split(",");
        }
        catch (Exception e)
        {
            // JSON解析失败，尝试简单分割处理
            try
            {
                String trimmed = images.trim();
                if (trimmed.startsWith("[") && trimmed.endsWith("]"))
                {
                    String content = trimmed.substring(1, trimmed.length() - 1);
                    if (content.isEmpty())
                    {
                        return new String[0];
                    }
                    String[] parts = content.split(",");
                    String[] result = new String[parts.length];
                    for (int i = 0; i < parts.length; i++)
                    {
                        String url = parts[i].trim();
                        // 去掉首尾的引号
                        if (url.startsWith("\""))
                        {
                            url = url.substring(1);
                        }
                        if (url.endsWith("\""))
                        {
                            url = url.substring(0, url.length() - 1);
                        }
                        result[i] = url;
                    }
                    return result;
                }
            }
            catch (Exception ex)
            {
                // ignore
            }
            return new String[0];
        }
    }

    /**
     * 设置图片URL数组
     */
    public void setImageArray(String[] imageArray)
    {
        if (imageArray == null || imageArray.length == 0)
        {
            this.images = null;
        }
        else
        {
            this.images = "[" + String.join(",", imageArray) + "]";
        }
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setReplyContent(String replyContent)
    {
        this.replyContent = replyContent;
    }

    public String getReplyContent()
    {
        return replyContent;
    }

    public void setHandleUserId(Long handleUserId)
    {
        this.handleUserId = handleUserId;
    }

    public Long getHandleUserId()
    {
        return handleUserId;
    }

    public void setHandleUserName(String handleUserName)
    {
        this.handleUserName = handleUserName;
    }

    public String getHandleUserName()
    {
        return handleUserName;
    }

    public void setHandleTime(Date handleTime)
    {
        this.handleTime = handleTime;
    }

    public Date getHandleTime()
    {
        return handleTime;
    }

    /**
     * 获取状态文本
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        if (STATUS_REJECTED.equals(status)) {
            return "已拒绝";
        } else if (STATUS_PROCESSING.equals(status)) {
            return "处理中";
        } else if (STATUS_PROCESSED.equals(status)) {
            return "已处理";
        } else {
            return "未知状态";
        }
    }

    /**
     * 判断是否为已拒绝状态
     */
    public boolean isRejected() {
        return STATUS_REJECTED.equals(status);
    }

    /**
     * 判断是否为处理中状态
     */
    public boolean isProcessing() {
        return STATUS_PROCESSING.equals(status);
    }

    /**
     * 判断是否为已处理状态
     */
    public boolean isProcessed() {
        return STATUS_PROCESSED.equals(status);
    }

    /**
     * 判断是否已处理（已拒绝或已处理）
     */
    public boolean isHandled() {
        return isRejected() || isProcessed();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("complaintId", getComplaintId())
            .append("complaintNo", getComplaintNo())
            .append("institutionId", getInstitutionId())
            .append("institutionName", getInstitutionName())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("complaintType", getComplaintType())
            .append("title", getTitle())
            .append("content", getContent())
            .append("images", getImages())
            .append("contactName", getContactName())
            .append("contactPhone", getContactPhone())
            .append("status", getStatus())
            .append("replyContent", getReplyContent())
            .append("handleUserId", getHandleUserId())
            .append("handleUserName", getHandleUserName())
            .append("handleTime", getHandleTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
