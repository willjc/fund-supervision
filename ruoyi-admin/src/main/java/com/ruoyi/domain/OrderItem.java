package com.ruoyi.domain;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单明细对象 order_item
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public class OrderItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long itemId;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 订单编号 */
    @Excel(name = "订单编号")
    private String orderNo;

    /** 项目名称 */
    @Excel(name = "项目名称")
    private String itemName;

    /** 项目类型 */
    @Excel(name = "项目类型")
    private String itemType;

    /** 项目描述 */
    @Excel(name = "项目描述")
    private String itemDescription;

    /** 单价(元) */
    @Excel(name = "单价")
    private BigDecimal unitPrice;

    /** 原始单价(元) - 审核修改前保存 */
    @Excel(name = "原始单价")
    private BigDecimal originalUnitPrice;

    /** 价格是否被修改过 0-否 1-是 */
    @Excel(name = "价格是否修改", readConverterExp = "0=否,1=是")
    private String isPriceModified;

    /** 数量 */
    @Excel(name = "数量")
    private Long quantity;

    /** 小计金额(元) */
    @Excel(name = "小计金额")
    private BigDecimal totalAmount;

    /** 服务周期 */
    @Excel(name = "服务周期")
    private String servicePeriod;

    public void setItemId(Long itemId)
    {
        this.itemId = itemId;
    }

    public Long getItemId()
    {
        return itemId;
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
    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getItemName()
    {
        return itemName;
    }
    public void setItemType(String itemType)
    {
        this.itemType = itemType;
    }

    public String getItemType()
    {
        return itemType;
    }
    public void setItemDescription(String itemDescription)
    {
        this.itemDescription = itemDescription;
    }

    public String getItemDescription()
    {
        return itemDescription;
    }
    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setOriginalUnitPrice(BigDecimal originalUnitPrice)
    {
        this.originalUnitPrice = originalUnitPrice;
    }

    public BigDecimal getOriginalUnitPrice()
    {
        return originalUnitPrice;
    }

    public void setIsPriceModified(String isPriceModified)
    {
        this.isPriceModified = isPriceModified;
    }

    public String getIsPriceModified()
    {
        return isPriceModified;
    }

    public void setQuantity(Long quantity)
    {
        this.quantity = quantity;
    }

    public Long getQuantity()
    {
        return quantity;
    }
    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }
    public void setServicePeriod(String servicePeriod)
    {
        this.servicePeriod = servicePeriod;
    }

    public String getServicePeriod()
    {
        return servicePeriod;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("itemId", getItemId())
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("itemName", getItemName())
            .append("itemType", getItemType())
            .append("itemDescription", getItemDescription())
            .append("unitPrice", getUnitPrice())
            .append("originalUnitPrice", getOriginalUnitPrice())
            .append("isPriceModified", getIsPriceModified())
            .append("quantity", getQuantity())
            .append("totalAmount", getTotalAmount())
            .append("servicePeriod", getServicePeriod())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}