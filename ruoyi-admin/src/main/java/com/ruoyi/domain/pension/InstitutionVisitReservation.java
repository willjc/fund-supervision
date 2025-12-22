package com.ruoyi.domain.pension;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 机构预约参观对象 institution_visit_reservation
 *
 * @author ruoyi
 * @date 2025-12-23
 */
public class InstitutionVisitReservation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 状态常量 */
    public static final String STATUS_PENDING = "0";    // 待参观
    public static final String STATUS_COMPLETED = "1";  // 已完成
    public static final String STATUS_CANCELLED = "2";  // 已取消

    /** 预约ID */
    private Long reservationId;

    /** 预约编号 */
    @Excel(name = "预约编号")
    private String reservationNo;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 预约用户ID */
    @Excel(name = "预约用户ID")
    private Long userId;

    /** 参观人姓名 */
    @Excel(name = "参观人姓名")
    private String visitorName;

    /** 参观人电话 */
    @Excel(name = "参观人电话")
    private String visitorPhone;

    /** 预计到访日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预计到访日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date visitDate;

    /** 预计到访时间 */
    @Excel(name = "预计到访时间")
    private String visitTime;

    /** 参观人数 */
    @Excel(name = "参观人数")
    private Integer visitorCount;

    /** 备注说明 */
    @Excel(name = "备注说明")
    private String remark;

    /** 状态(0-待参观 1-已完成 2-已取消) */
    @Excel(name = "状态")
    private String status;

    /** 处理人 */
    @Excel(name = "处理人")
    private String handleUser;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    /** 处理备注 */
    @Excel(name = "处理备注")
    private String handleRemark;

    /** 关联查询字段 */
    /** 机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 机构封面图 */
    @Excel(name = "机构封面图")
    private String institutionCover;

    /** 用户名 */
    @Excel(name = "用户名")
    private String userName;

    public void setReservationId(Long reservationId)
    {
        this.reservationId = reservationId;
    }

    public Long getReservationId()
    {
        return reservationId;
    }

    public void setReservationNo(String reservationNo)
    {
        this.reservationNo = reservationNo;
    }

    public String getReservationNo()
    {
        return reservationNo;
    }

    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setVisitorName(String visitorName)
    {
        this.visitorName = visitorName;
    }

    public String getVisitorName()
    {
        return visitorName;
    }

    public void setVisitorPhone(String visitorPhone)
    {
        this.visitorPhone = visitorPhone;
    }

    public String getVisitorPhone()
    {
        return visitorPhone;
    }

    public void setVisitDate(Date visitDate)
    {
        this.visitDate = visitDate;
    }

    public Date getVisitDate()
    {
        return visitDate;
    }

    public void setVisitTime(String visitTime)
    {
        this.visitTime = visitTime;
    }

    public String getVisitTime()
    {
        return visitTime;
    }

    public void setVisitorCount(Integer visitorCount)
    {
        this.visitorCount = visitorCount;
    }

    public Integer getVisitorCount()
    {
        return visitorCount;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setHandleUser(String handleUser)
    {
        this.handleUser = handleUser;
    }

    public String getHandleUser()
    {
        return handleUser;
    }

    public void setHandleTime(Date handleTime)
    {
        this.handleTime = handleTime;
    }

    public Date getHandleTime()
    {
        return handleTime;
    }

    public void setHandleRemark(String handleRemark)
    {
        this.handleRemark = handleRemark;
    }

    public String getHandleRemark()
    {
        return handleRemark;
    }

    public void setInstitutionName(String institutionName)
    {
        this.institutionName = institutionName;
    }

    public String getInstitutionName()
    {
        return institutionName;
    }

    public void setInstitutionCover(String institutionCover)
    {
        this.institutionCover = institutionCover;
    }

    public String getInstitutionCover()
    {
        return institutionCover;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }

    /**
     * 获取状态文本
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        if (STATUS_PENDING.equals(status)) {
            return "待参观";
        } else if (STATUS_COMPLETED.equals(status)) {
            return "已完成";
        } else if (STATUS_CANCELLED.equals(status)) {
            return "已取消";
        } else {
            return "未知状态";
        }
    }

    /**
     * 判断是否为待参观状态
     */
    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }

    /**
     * 判断是否已完成
     */
    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status);
    }

    /**
     * 判断是否已取消
     */
    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("reservationId", getReservationId())
            .append("reservationNo", getReservationNo())
            .append("institutionId", getInstitutionId())
            .append("userId", getUserId())
            .append("visitorName", getVisitorName())
            .append("visitorPhone", getVisitorPhone())
            .append("visitDate", getVisitDate())
            .append("visitTime", getVisitTime())
            .append("visitorCount", getVisitorCount())
            .append("remark", getRemark())
            .append("status", getStatus())
            .append("handleUser", getHandleUser())
            .append("handleTime", getHandleTime())
            .append("handleRemark", getHandleRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("institutionName", getInstitutionName())
            .append("userName", getUserName())
            .toString();
    }
}
