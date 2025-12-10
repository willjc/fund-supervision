package com.ruoyi.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 床位信息对象 bed_info
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public class BedInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 床位ID */
    private Long bedId;

    /** 机构ID */
    private Long institutionId;

    /** 房间号 */
    @Excel(name = "房间号", prompt = "必填,如:101、201")
    private String roomNumber;

    /** 床位号 */
    @Excel(name = "床位号", prompt = "必填,如:01、02")
    private String bedNumber;

    /** 床位类型(1普通 2豪华 3医疗) */
    @Excel(name = "床位类型", readConverterExp = "1=普通床位,2=豪华床位,3=医疗床位", prompt = "必填,填写:普通床位、豪华床位、医疗床位")
    private String bedType;

    /** 床位状态(0空置 1占用 2维修) */
    @Excel(name = "床位状态", readConverterExp = "0=空置,1=占用,2=维修", prompt = "必填,填写:空置、占用、维修")
    private String bedStatus;

    /** 床位价格(元/月) */
    @Excel(name = "价格(元/月)", prompt = "必填,单位:元,如:2000")
    private BigDecimal price;

    /** 自理护理价格(元/月) */
    @Excel(name = "自理护理价格(元/月)", prompt = "选填,自理等级护理费,如:500")
    private BigDecimal selfCarePrice;

    /** 半护理价格(元/月) */
    @Excel(name = "半护理价格(元/月)", prompt = "选填,半护理等级护理费,如:800")
    private BigDecimal halfCarePrice;

    /** 全护理价格(元/月) */
    @Excel(name = "全护理价格(元/月)", prompt = "选填,全护理等级护理费,如:1200")
    private BigDecimal fullCarePrice;

    /** 会员费(一次性) */
    @Excel(name = "会员费(元)", prompt = "选填,一次性会员费,如:5000")
    private BigDecimal memberFee;

    /** 押金(一次性) */
    @Excel(name = "押金(元)", prompt = "选填,一次性押金,如:10000")
    private BigDecimal depositFee;

    /** 楼层 */
    @Excel(name = "楼层", prompt = "选填,如:1、2、3")
    private Long floorNumber;

    /** 房间面积(平方米) */
    @Excel(name = "房间面积(㎡)", prompt = "选填,如:25、30.5")
    private BigDecimal roomArea;

    /** 是否有独立卫生间(0否 1是) */
    @Excel(name = "独立卫浴", readConverterExp = "0=否,1=是", prompt = "选填,填写:是、否")
    private String hasBathroom;

    /** 是否有阳台(0否 1是) */
    @Excel(name = "阳台", readConverterExp = "0=否,1=是", prompt = "选填,填写:是、否")
    private String hasBalcony;

    /** 设施配置 */
    @Excel(name = "设施配置", prompt = "选填,如:电视、空调、衣柜")
    private String facilities;

    /** 机构名称 */
    @Excel(name = "机构名称", prompt = "选填,仅用于核对")
    private String institutionName;

    /** 当前用户ID(用于数据权限过滤,不映射到数据库) */
    private Long currentUserId;

    public void setBedId(Long bedId)
    {
        this.bedId = bedId;
    }

    public Long getBedId()
    {
        return bedId;
    }
    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
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
    public void setBedType(String bedType)
    {
        this.bedType = bedType;
    }

    public String getBedType()
    {
        return bedType;
    }
    public void setBedStatus(String bedStatus)
    {
        this.bedStatus = bedStatus;
    }

    public String getBedStatus()
    {
        return bedStatus;
    }
    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getPrice()
    {
        return price;
    }
    public void setFloorNumber(Long floorNumber)
    {
        this.floorNumber = floorNumber;
    }

    public Long getFloorNumber()
    {
        return floorNumber;
    }
    public void setRoomArea(BigDecimal roomArea)
    {
        this.roomArea = roomArea;
    }

    public BigDecimal getRoomArea()
    {
        return roomArea;
    }
    public void setHasBathroom(String hasBathroom)
    {
        this.hasBathroom = hasBathroom;
    }

    public String getHasBathroom()
    {
        return hasBathroom;
    }
    public void setHasBalcony(String hasBalcony)
    {
        this.hasBalcony = hasBalcony;
    }

    public String getHasBalcony()
    {
        return hasBalcony;
    }
    public void setFacilities(String facilities)
    {
        this.facilities = facilities;
    }

    public String getFacilities()
    {
        return facilities;
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

    public BigDecimal getSelfCarePrice()
    {
        return selfCarePrice;
    }

    public void setSelfCarePrice(BigDecimal selfCarePrice)
    {
        this.selfCarePrice = selfCarePrice;
    }

    public BigDecimal getHalfCarePrice()
    {
        return halfCarePrice;
    }

    public void setHalfCarePrice(BigDecimal halfCarePrice)
    {
        this.halfCarePrice = halfCarePrice;
    }

    public BigDecimal getFullCarePrice()
    {
        return fullCarePrice;
    }

    public void setFullCarePrice(BigDecimal fullCarePrice)
    {
        this.fullCarePrice = fullCarePrice;
    }

    public BigDecimal getMemberFee()
    {
        return memberFee;
    }

    public void setMemberFee(BigDecimal memberFee)
    {
        this.memberFee = memberFee;
    }

    public BigDecimal getDepositFee()
    {
        return depositFee;
    }

    public void setDepositFee(BigDecimal depositFee)
    {
        this.depositFee = depositFee;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("bedId", getBedId())
            .append("institutionId", getInstitutionId())
            .append("roomNumber", getRoomNumber())
            .append("bedNumber", getBedNumber())
            .append("bedType", getBedType())
            .append("bedStatus", getBedStatus())
            .append("price", getPrice())
            .append("selfCarePrice", getSelfCarePrice())
            .append("halfCarePrice", getHalfCarePrice())
            .append("fullCarePrice", getFullCarePrice())
            .append("memberFee", getMemberFee())
            .append("depositFee", getDepositFee())
            .append("floorNumber", getFloorNumber())
            .append("roomArea", getRoomArea())
            .append("hasBathroom", getHasBathroom())
            .append("hasBalcony", getHasBalcony())
            .append("facilities", getFacilities())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}