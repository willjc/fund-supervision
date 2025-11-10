package com.ruoyi.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单主表对象 order_info
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public class OrderInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    private Long orderId;

    /** 订单编号 */
    @Excel(name = "订单编号")
    private String orderNo;

    /** 订单类型(1床位费 2护理费 3餐饮费 4医疗费 5其他费用) */
    @Excel(name = "订单类型", readConverterExp = "1=床位费,2=护理费,3=餐饮费,4=医疗费,5=其他费用")
    private String orderType;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 入住申请ID */
    @Excel(name = "入住申请ID")
    private Long checkInId;

    /** 床位ID */
    @Excel(name = "床位ID")
    private Long bedId;

    /** 订单总金额(元) */
    @Excel(name = "订单总金额")
    private BigDecimal orderAmount;

    /** 已付金额(元) */
    @Excel(name = "已付金额")
    private BigDecimal paidAmount;

    /** 订单状态(0待支付 1已支付 2已取消 3已退款) */
    @Excel(name = "订单状态", readConverterExp = "0=待支付,1=已支付,2=已取消,3=已退款")
    private String orderStatus;

    /** 支付方式 */
    @Excel(name = "支付方式")
    private String paymentMethod;

    /** 支付时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "支付时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

    /** 订单日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "订单日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date orderDate;

    /** 服务开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "服务开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date serviceStartDate;

    /** 服务结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "服务结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date serviceEndDate;

    /** 计费周期 */
    @Excel(name = "计费周期")
    private String billingCycle;

    /** 到期日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "到期日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date dueDate;

    /** 关联的老人姓名 */
    @Excel(name = "老人姓名")
    private String elderName;

    /** 关联的机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 关联的房间号 */
    @Excel(name = "房间号")
    private String roomNumber;

    /** 关联的床位号 */
    @Excel(name = "床位号")
    private String bedNumber;

    /** 未付金额 */
    @Excel(name = "未付金额")
    private BigDecimal unpaidAmount;

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
    public void setOrderType(String orderType)
    {
        this.orderType = orderType;
    }

    public String getOrderType()
    {
        return orderType;
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
    public void setCheckInId(Long checkInId)
    {
        this.checkInId = checkInId;
    }

    public Long getCheckInId()
    {
        return checkInId;
    }
    public void setBedId(Long bedId)
    {
        this.bedId = bedId;
    }

    public Long getBedId()
    {
        return bedId;
    }
    public void setOrderAmount(BigDecimal orderAmount)
    {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getOrderAmount()
    {
        return orderAmount;
    }
    public void setPaidAmount(BigDecimal paidAmount)
    {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getPaidAmount()
    {
        return paidAmount;
    }
    public void setOrderStatus(String orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus()
    {
        return orderStatus;
    }
    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod()
    {
        return paymentMethod;
    }
    public void setPaymentTime(Date paymentTime)
    {
        this.paymentTime = paymentTime;
    }

    public Date getPaymentTime()
    {
        return paymentTime;
    }
    public void setOrderDate(Date orderDate)
    {
        this.orderDate = orderDate;
    }

    public Date getOrderDate()
    {
        return orderDate;
    }
    public void setServiceStartDate(Date serviceStartDate)
    {
        this.serviceStartDate = serviceStartDate;
    }

    public Date getServiceStartDate()
    {
        return serviceStartDate;
    }
    public void setServiceEndDate(Date serviceEndDate)
    {
        this.serviceEndDate = serviceEndDate;
    }

    public Date getServiceEndDate()
    {
        return serviceEndDate;
    }
    public void setBillingCycle(String billingCycle)
    {
        this.billingCycle = billingCycle;
    }

    public String getBillingCycle()
    {
        return billingCycle;
    }
    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public Date getDueDate()
    {
        return dueDate;
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
    public void setRoomNumber(String roomNumber)
    {
        this.roomNumber = roomNumber;
    }

    public String getRoomNumber()
    {
        return roomNumber;
    }
    public void setBedNumber(String bedNumber)
    {
        this.bedNumber = bedNumber;
    }

    public String getBedNumber()
    {
        return bedNumber;
    }
    public void setUnpaidAmount(BigDecimal unpaidAmount)
    {
        this.unpaidAmount = unpaidAmount;
    }

    public BigDecimal getUnpaidAmount()
    {
        if (unpaidAmount != null) {
            return unpaidAmount;
        }
        // 计算未付金额 = 订单总金额 - 已付金额
        if (orderAmount != null && paidAmount != null) {
            return orderAmount.subtract(paidAmount);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("orderType", getOrderType())
            .append("elderId", getElderId())
            .append("institutionId", getInstitutionId())
            .append("checkInId", getCheckInId())
            .append("bedId", getBedId())
            .append("orderAmount", getOrderAmount())
            .append("paidAmount", getPaidAmount())
            .append("orderStatus", getOrderStatus())
            .append("paymentMethod", getPaymentMethod())
            .append("paymentTime", getPaymentTime())
            .append("orderDate", getOrderDate())
            .append("serviceStartDate", getServiceStartDate())
            .append("serviceEndDate", getServiceEndDate())
            .append("billingCycle", getBillingCycle())
            .append("dueDate", getDueDate())
            .append("elderName", getElderName())
            .append("institutionName", getInstitutionName())
            .append("roomNumber", getRoomNumber())
            .append("bedNumber", getBedNumber())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}