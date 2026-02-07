package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 退款记录对象 refund_record
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public class RefundRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 退款ID */
    private Long refundId;

    /** 退款单号 */
    @Excel(name = "退款单号")
    private String refundNo;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 支付记录ID */
    @Excel(name = "支付记录ID")
    private Long paymentId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 退款金额 */
    @Excel(name = "退款金额")
    private BigDecimal refundAmount;

    /** 退款原因 */
    @Excel(name = "退款原因")
    private String refundReason;

    /** 退款状态(0待处理 1成功 2失败) */
    @Excel(name = "退款状态", readConverterExp = "0=待处理,1=成功,2=失败")
    private String refundStatus;

    /** 退款方式 */
    @Excel(name = "退款方式")
    private String refundMethod;

    /** 退款时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "退款时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date refundTime;

    /** 审批人 */
    @Excel(name = "审批人")
    private String approver;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审批时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    /** 审批意见 */
    @Excel(name = "审批意见")
    private String approveRemark;

    /** 服务费退款金额 */
    @Excel(name = "服务费退款金额")
    private BigDecimal serviceRefundAmount;

    /** 押金退��金额 */
    @Excel(name = "押金退款金额")
    private BigDecimal depositRefundAmount;

    /** 会员费退款金额 */
    @Excel(name = "会员费退款金额")
    private BigDecimal memberRefundAmount;

    /** 退款说明 */
    @Excel(name = "退款说明")
    private String refundDesc;

    /** 上传凭证（JSON数组） */
    @Excel(name = "上传凭证")
    private String evidenceImages;

    // 关联字段（用于展示）
    @Excel(name = "老人姓名")
    private String elderName;

    @Excel(name = "机构名称")
    private String institutionName;

    @Excel(name = "订单号")
    private String orderNo;

    @Excel(name = "支付流水号")
    private String paymentNo;

    /** 当前用户ID(用于数据权限过滤) */
    private Long currentUserId;

    public void setRefundId(Long refundId)
    {
        this.refundId = refundId;
    }

    public Long getRefundId()
    {
        return refundId;
    }

    public void setRefundNo(String refundNo)
    {
        this.refundNo = refundNo;
    }

    public String getRefundNo()
    {
        return refundNo;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setPaymentId(Long paymentId)
    {
        this.paymentId = paymentId;
    }

    public Long getPaymentId()
    {
        return paymentId;
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

    public void setRefundAmount(BigDecimal refundAmount)
    {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getRefundAmount()
    {
        return refundAmount;
    }

    public void setRefundReason(String refundReason)
    {
        this.refundReason = refundReason;
    }

    public String getRefundReason()
    {
        return refundReason;
    }

    public void setRefundStatus(String refundStatus)
    {
        this.refundStatus = refundStatus;
    }

    public String getRefundStatus()
    {
        return refundStatus;
    }

    public void setRefundMethod(String refundMethod)
    {
        this.refundMethod = refundMethod;
    }

    public String getRefundMethod()
    {
        return refundMethod;
    }

    public void setRefundTime(Date refundTime)
    {
        this.refundTime = refundTime;
    }

    public Date getRefundTime()
    {
        return refundTime;
    }

    public void setApprover(String approver)
    {
        this.approver = approver;
    }

    public String getApprover()
    {
        return approver;
    }

    public void setApproveTime(Date approveTime)
    {
        this.approveTime = approveTime;
    }

    public Date getApproveTime()
    {
        return approveTime;
    }

    public void setApproveRemark(String approveRemark)
    {
        this.approveRemark = approveRemark;
    }

    public String getApproveRemark()
    {
        return approveRemark;
    }

    public BigDecimal getServiceRefundAmount() {
        return serviceRefundAmount;
    }

    public void setServiceRefundAmount(BigDecimal serviceRefundAmount) {
        this.serviceRefundAmount = serviceRefundAmount;
    }

    public BigDecimal getDepositRefundAmount() {
        return depositRefundAmount;
    }

    public void setDepositRefundAmount(BigDecimal depositRefundAmount) {
        this.depositRefundAmount = depositRefundAmount;
    }

    public BigDecimal getMemberRefundAmount() {
        return memberRefundAmount;
    }

    public void setMemberRefundAmount(BigDecimal memberRefundAmount) {
        this.memberRefundAmount = memberRefundAmount;
    }

    public String getRefundDesc() {
        return refundDesc;
    }

    public void setRefundDesc(String refundDesc) {
        this.refundDesc = refundDesc;
    }

    public String getEvidenceImages() {
        return evidenceImages;
    }

    public void setEvidenceImages(String evidenceImages) {
        this.evidenceImages = evidenceImages;
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPaymentNo() {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo) {
        this.paymentNo = paymentNo;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("refundId", getRefundId())
            .append("refundNo", getRefundNo())
            .append("orderId", getOrderId())
            .append("paymentId", getPaymentId())
            .append("elderId", getElderId())
            .append("institutionId", getInstitutionId())
            .append("refundAmount", getRefundAmount())
            .append("refundReason", getRefundReason())
            .append("refundStatus", getRefundStatus())
            .append("refundMethod", getRefundMethod())
            .append("refundTime", getRefundTime())
            .append("approver", getApprover())
            .append("approveTime", getApproveTime())
            .append("approveRemark", getApproveRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}