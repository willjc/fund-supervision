package com.ruoyi.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 支付记录对象 payment_record
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public class PaymentRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 支付记录ID */
    private Long paymentId;

    /** 支付流水号 */
    @Excel(name = "支付流水号")
    private String paymentNo;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 订单编号 */
    @Excel(name = "订单编号")
    private String orderNo;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 支付金额(元) */
    @Excel(name = "支付金额")
    private BigDecimal paymentAmount;

    /** 支付方式(微信 支付宝 银行卡转账 现金) */
    @Excel(name = "支付方式")
    private String paymentMethod;

    /** 支付状态(0处理中 1成功 2失败) */
    @Excel(name = "支付状态", readConverterExp = "0=处理中,1=成功,2=失败")
    private String paymentStatus;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

    /** 支付凭证图片URL */
    @Excel(name = "支付凭证")
    private String paymentProof;

    /** 支付凭证备注 */
    @Excel(name = "凭证备注")
    private String paymentProofRemark;

    /** 第三方交易号 */
    @Excel(name = "第三方交易号")
    private String transactionId;

    /** 支付网关响应信息 */
    private String gatewayResponse;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operator;

    /** 关联的老人姓名 */
    @Excel(name = "老人姓名")
    private String elderName;

    /** 关联的机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 监管账户余额（该笔收单完成时的余额） */
    private BigDecimal supervisionBalance;

    public void setPaymentId(Long paymentId)
    {
        this.paymentId = paymentId;
    }

    public Long getPaymentId()
    {
        return paymentId;
    }
    public void setPaymentNo(String paymentNo)
    {
        this.paymentNo = paymentNo;
    }

    public String getPaymentNo()
    {
        return paymentNo;
    }
    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Long getOrderId()
    {
        return orderId;
    }
    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo()
    {
        return orderNo;
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
    public void setPaymentAmount(BigDecimal paymentAmount)
    {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getPaymentAmount()
    {
        return paymentAmount;
    }
    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod()
    {
        return paymentMethod;
    }
    public void setPaymentStatus(String paymentStatus)
    {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatus()
    {
        return paymentStatus;
    }
    public void setPaymentTime(Date paymentTime)
    {
        this.paymentTime = paymentTime;
    }

    public Date getPaymentTime()
    {
        return paymentTime;
    }

    public String getPaymentProof()
    {
        return paymentProof;
    }

    public void setPaymentProof(String paymentProof)
    {
        this.paymentProof = paymentProof;
    }

    public String getPaymentProofRemark()
    {
        return paymentProofRemark;
    }

    public void setPaymentProofRemark(String paymentProofRemark)
    {
        this.paymentProofRemark = paymentProofRemark;
    }

    public void setTransactionId(String transactionId)
    {
        this.transactionId = transactionId;
    }

    public String getTransactionId()
    {
        return transactionId;
    }
    public void setGatewayResponse(String gatewayResponse)
    {
        this.gatewayResponse = gatewayResponse;
    }

    public String getGatewayResponse()
    {
        return gatewayResponse;
    }
    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getOperator()
    {
        return operator;
    }
    public void setElderName(String elderName)
    {
        this.elderName = elderName;
    }

    public String getElderName()
    {
        return elderName;
    }
    public void setInstitutionName(String institutionName)
    {
        this.institutionName = institutionName;
    }

    public String getInstitutionName()
    {
        return institutionName;
    }

    public void setSupervisionBalance(BigDecimal supervisionBalance)
    {
        this.supervisionBalance = supervisionBalance;
    }

    public BigDecimal getSupervisionBalance()
    {
        return supervisionBalance;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("paymentId", getPaymentId())
            .append("paymentNo", getPaymentNo())
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("elderId", getElderId())
            .append("institutionId", getInstitutionId())
            .append("paymentAmount", getPaymentAmount())
            .append("paymentMethod", getPaymentMethod())
            .append("paymentStatus", getPaymentStatus())
            .append("paymentTime", getPaymentTime())
            .append("transactionId", getTransactionId())
            .append("gatewayResponse", getGatewayResponse())
            .append("operator", getOperator())
            .append("elderName", getElderName())
            .append("institutionName", getInstitutionName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}