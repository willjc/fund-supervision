package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 资金划拨记录对象 fund_transfer
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public class FundTransfer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 划拨ID */
    private Long transferId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 划拨单号 */
    @Excel(name = "划拨单号")
    private String transferNo;

    /** 划拨类型(1自动划拨 2手动划拨 3特殊申请) */
    @Excel(name = "划拨类型", readConverterExp = "1=自动划拨,2=手动划拨,3=特殊申请")
    private String transferType;

    /** 划拨金额 */
    @Excel(name = "划拨金额")
    private BigDecimal transferAmount;

    /** 划拨日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "划拨日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date transferDate;

    /** 划拨期间(如2025-12) */
    @Excel(name = "划拨期间")
    private String transferPeriod;

    /** 涉及老人数量 */
    @Excel(name = "涉及老人数量")
    private Integer elderCount;

    /** 划拨状态(0待处理 1成功 2失败) */
    @Excel(name = "划拨状态", readConverterExp = "0=待处理,1=成功,2=失败")
    private String transferStatus;

    /** 银行订单号 */
    @Excel(name = "银行订单号")
    private String bankOrderNo;

    /** 失败原因 */
    @Excel(name = "失败原因")
    private String failureReason;

    /** 审���人 */
    @Excel(name = "审批人")
    private String approveUser;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审批时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    /** 执行人 */
    @Excel(name = "执行人")
    private String executeUser;

    /** 执行时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "执行时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date executeTime;

    /** 是否已划拨 0-否 1-是 */
    @Excel(name = "是否已划拨", readConverterExp = "0=否,1=是")
    private String isPaid;

    /** 实际划拨时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "实际划拨时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date paidTime;

    /** 划拨方式 auto-自动 manual-手动申请 */
    @Excel(name = "划拨方式", readConverterExp = "auto=自动,manual=手动申请")
    private String paidMethod;

    /** 关联的申请ID（手动申请时） */
    private Long applyId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 关联订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 账单月份 (格式: 2025-02) */
    @Excel(name = "账单月份")
    private String billingMonth;

    /** 划拨单状态 pending-待划拨 processing-划拨中 completed-已完成 cancelled-已取消 */
    @Excel(name = "划拨单状态")
    private String status;

    // 关联字段（用于展示）
    @Excel(name = "机构名称")
    private String institutionName;

    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderName;

    public void setTransferId(Long transferId)
    {
        this.transferId = transferId;
    }

    public Long getTransferId()
    {
        return transferId;
    }

    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
    }

    public void setTransferNo(String transferNo)
    {
        this.transferNo = transferNo;
    }

    public String getTransferNo()
    {
        return transferNo;
    }

    public void setTransferType(String transferType)
    {
        this.transferType = transferType;
    }

    public String getTransferType()
    {
        return transferType;
    }

    public void setTransferAmount(BigDecimal transferAmount)
    {
        this.transferAmount = transferAmount;
    }

    public BigDecimal getTransferAmount()
    {
        return transferAmount;
    }

    public void setTransferDate(Date transferDate)
    {
        this.transferDate = transferDate;
    }

    public Date getTransferDate()
    {
        return transferDate;
    }

    public void setTransferPeriod(String transferPeriod)
    {
        this.transferPeriod = transferPeriod;
    }

    public String getTransferPeriod()
    {
        return transferPeriod;
    }

    public void setElderCount(Integer elderCount)
    {
        this.elderCount = elderCount;
    }

    public Integer getElderCount()
    {
        return elderCount;
    }

    public void setTransferStatus(String transferStatus)
    {
        this.transferStatus = transferStatus;
    }

    public String getTransferStatus()
    {
        return transferStatus;
    }

    public void setBankOrderNo(String bankOrderNo)
    {
        this.bankOrderNo = bankOrderNo;
    }

    public String getBankOrderNo()
    {
        return bankOrderNo;
    }

    public void setFailureReason(String failureReason)
    {
        this.failureReason = failureReason;
    }

    public String getFailureReason()
    {
        return failureReason;
    }

    public void setApproveUser(String approveUser)
    {
        this.approveUser = approveUser;
    }

    public String getApproveUser()
    {
        return approveUser;
    }

    public void setApproveTime(Date approveTime)
    {
        this.approveTime = approveTime;
    }

    public Date getApproveTime()
    {
        return approveTime;
    }

    public void setExecuteUser(String executeUser)
    {
        this.executeUser = executeUser;
    }

    public String getExecuteUser()
    {
        return executeUser;
    }

    public void setExecuteTime(Date executeTime)
    {
        this.executeTime = executeTime;
    }

    public Date getExecuteTime()
    {
        return executeTime;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(String isPaid) {
        this.isPaid = isPaid;
    }

    public Date getPaidTime() {
        return paidTime;
    }

    public void setPaidTime(Date paidTime) {
        this.paidTime = paidTime;
    }

    public String getPaidMethod() {
        return paidMethod;
    }

    public void setPaidMethod(String paidMethod) {
        this.paidMethod = paidMethod;
    }

    public Long getApplyId() {
        return applyId;
    }

    public void setApplyId(Long applyId) {
        this.applyId = applyId;
    }

    public Long getElderId() {
        return elderId;
    }

    public void setElderId(Long elderId) {
        this.elderId = elderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        this.billingMonth = billingMonth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("transferId", getTransferId())
            .append("institutionId", getInstitutionId())
            .append("transferNo", getTransferNo())
            .append("transferType", getTransferType())
            .append("transferAmount", getTransferAmount())
            .append("transferDate", getTransferDate())
            .append("transferPeriod", getTransferPeriod())
            .append("elderCount", getElderCount())
            .append("transferStatus", getTransferStatus())
            .append("bankOrderNo", getBankOrderNo())
            .append("failureReason", getFailureReason())
            .append("approveUser", getApproveUser())
            .append("approveTime", getApproveTime())
            .append("executeUser", getExecuteUser())
            .append("executeTime", getExecuteTime())
            .append("isPaid", getIsPaid())
            .append("paidTime", getPaidTime())
            .append("paidMethod", getPaidMethod())
            .append("applyId", getApplyId())
            .append("elderId", getElderId())
            .append("orderId", getOrderId())
            .append("billingMonth", getBillingMonth())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}