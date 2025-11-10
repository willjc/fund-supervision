package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 余额预警对象 balance_warning
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public class BalanceWarning extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 预警ID */
    private Long warningId;

    /** 账户ID */
    @Excel(name = "账户ID")
    private Long accountId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 预警类型(1严重 2警告 3提示) */
    @Excel(name = "预警类型", readConverterExp = "1=严重,2=警告,3=提示")
    private String warningType;

    /** 当前余额 */
    @Excel(name = "当前余额")
    private BigDecimal currentBalance;

    /** 可用月数 */
    @Excel(name = "可用月数")
    private Integer availableMonths;

    /** 月费用 */
    @Excel(name = "月费用")
    private BigDecimal monthlyFee;

    /** 预警金额阈值 */
    @Excel(name = "预警金额阈值")
    private BigDecimal warningThreshold;

    /** 预警消息 */
    @Excel(name = "预警消息")
    private String warningMessage;

    /** 预警状态(0未处理 1已处理 2已忽略) */
    @Excel(name = "预警状态", readConverterExp = "0=未处理,1=已处理,2=已忽略")
    private String warningStatus;

    /** 处理人 */
    @Excel(name = "处理人")
    private String handler;

    /** 处理时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "处理时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date handleTime;

    /** 处理意见 */
    @Excel(name = "处理意见")
    private String handleRemark;

    /** 通知状态(0未通知 1已通知) */
    @Excel(name = "通知状态", readConverterExp = "0=未通知,1=已通知")
    private String notifyStatus;

    /** 最后通知时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "最后通知时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastNotifyTime;

    // 关联字段（用于展示）
    @Excel(name = "老人姓名")
    private String elderName;

    @Excel(name = "机构名称")
    private String institutionName;

    @Excel(name = "账户编号")
    private String accountNo;

    public void setWarningId(Long warningId)
    {
        this.warningId = warningId;
    }

    public Long getWarningId()
    {
        return warningId;
    }

    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }

    public Long getAccountId()
    {
        return accountId;
    }

    public void setElderId(Long elderId)
    {
        this.elderId = elderId;
    }

    public Long getElderId()
    {
        return elderId;
    }

    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
    }

    public void setWarningType(String warningType)
    {
        this.warningType = warningType;
    }

    public String getWarningType()
    {
        return warningType;
    }

    public void setCurrentBalance(BigDecimal currentBalance)
    {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getCurrentBalance()
    {
        return currentBalance;
    }

    public void setAvailableMonths(Integer availableMonths)
    {
        this.availableMonths = availableMonths;
    }

    public Integer getAvailableMonths()
    {
        return availableMonths;
    }

    public void setMonthlyFee(BigDecimal monthlyFee)
    {
        this.monthlyFee = monthlyFee;
    }

    public BigDecimal getMonthlyFee()
    {
        return monthlyFee;
    }

    public void setWarningThreshold(BigDecimal warningThreshold)
    {
        this.warningThreshold = warningThreshold;
    }

    public BigDecimal getWarningThreshold()
    {
        return warningThreshold;
    }

    public void setWarningMessage(String warningMessage)
    {
        this.warningMessage = warningMessage;
    }

    public String getWarningMessage()
    {
        return warningMessage;
    }

    public void setWarningStatus(String warningStatus)
    {
        this.warningStatus = warningStatus;
    }

    public String getWarningStatus()
    {
        return warningStatus;
    }

    public void setHandler(String handler)
    {
        this.handler = handler;
    }

    public String getHandler()
    {
        return handler;
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

    public void setNotifyStatus(String notifyStatus)
    {
        this.notifyStatus = notifyStatus;
    }

    public String getNotifyStatus()
    {
        return notifyStatus;
    }

    public void setLastNotifyTime(Date lastNotifyTime)
    {
        this.lastNotifyTime = lastNotifyTime;
    }

    public Date getLastNotifyTime()
    {
        return lastNotifyTime;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("warningId", getWarningId())
            .append("accountId", getAccountId())
            .append("elderId", getElderId())
            .append("institutionId", getInstitutionId())
            .append("warningType", getWarningType())
            .append("currentBalance", getCurrentBalance())
            .append("availableMonths", getAvailableMonths())
            .append("monthlyFee", getMonthlyFee())
            .append("warningThreshold", getWarningThreshold())
            .append("warningMessage", getWarningMessage())
            .append("warningStatus", getWarningStatus())
            .append("handler", getHandler())
            .append("handleTime", getHandleTime())
            .append("handleRemark", getHandleRemark())
            .append("notifyStatus", getNotifyStatus())
            .append("lastNotifyTime", getLastNotifyTime())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}