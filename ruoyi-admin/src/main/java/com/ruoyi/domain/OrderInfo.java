package com.ruoyi.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ruoyi.domain.OrderItem;
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

    /** 订单类型(1入驻 2续费) */
    @Excel(name = "订单类型", readConverterExp = "1=入驻,2=续费")
    private String orderType;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 下单人ID */
    @Excel(name = "下单人ID")
    private Long creatorUserId;

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

    /** 入驻月数 */
    @Excel(name = "入驻月数")
    private Integer monthCount;

    /** 订单总金额(元) - 实收总计 */
    // orderAmount已存在,这里的是实收金额

    /** 应收总计(元) - 优惠前金额 */
    @Excel(name = "应收总计")
    private BigDecimal originalAmount;

    /** 优惠金额(元) */
    @Excel(name = "优惠金额")
    private BigDecimal discountAmount;

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

    /** 当前用户ID(用于数据权限过滤) */
    private Long currentUserId;

    /** 关联的房间号 */
    @Excel(name = "房间号")
    private String roomNumber;

    /** 关联的床位号 */
    @Excel(name = "床位号")
    private String bedNumber;

    /** 未付金额 */
    @Excel(name = "未付金额")
    private BigDecimal unpaidAmount;

    /** 订单明细列表 */
    private List<OrderItem> orderItems;

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
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
    public void setCreatorUserId(Long creatorUserId)
    {
        this.creatorUserId = creatorUserId;
    }

    public Long getCreatorUserId()
    {
        return creatorUserId;
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
    public void setMonthCount(Integer monthCount)
    {
        this.monthCount = monthCount;
    }

    public Integer getMonthCount()
    {
        return monthCount;
    }

    public void setOriginalAmount(BigDecimal originalAmount)
    {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getOriginalAmount()
    {
        return originalAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount)
    {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountAmount()
    {
        return discountAmount;
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

    public Long getCurrentUserId()
    {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId)
    {
        this.currentUserId = currentUserId;
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