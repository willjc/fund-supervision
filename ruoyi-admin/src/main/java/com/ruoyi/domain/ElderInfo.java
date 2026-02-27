package com.ruoyi.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 老人基础信息对象 elder_info
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public class ElderInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 老人ID */
    private Long elderId;

    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderName;

    /** 性别(1男 2女) */
    @Excel(name = "性别", readConverterExp = "1=男,2=女")
    private String gender;

    /** 身份证号 */
    @Excel(name = "身份证号")
    private String idCard;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthDate;

    /** 年龄 */
    @Excel(name = "年龄")
    private Long age;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String phone;

    /** 家庭住址 */
    @Excel(name = "家庭住址")
    private String address;

    /** 紧急联系人 */
    @Excel(name = "紧急联系人")
    private String emergencyContact;

    /** 紧急联系电话 */
    @Excel(name = "紧急联系电话")
    private String emergencyPhone;

    /** 健康状况描述 */
    @Excel(name = "健康状况")
    private String healthStatus;

    /** 护理等级(1自理 2半护理 3全护理) */
    @Excel(name = "护理等级", readConverterExp = "1=自理,2=半护理,3=全护理")
    private String careLevel;

    /** 餐费等级代码(1一级餐 2二级餐等) */
    @Excel(name = "餐费等级", readConverterExp = "1=一级餐,2=二级餐")
    private String mealLevelCode;

    /** 特殊需求说明 */
    @Excel(name = "特殊需求")
    private String specialNeeds;

    /** 照片路径 */
    private String photoPath;

    /** 身份证正面照片路径 */
    private String idCardFrontPath;

    /** 身份证反面照片路径 */
    private String idCardBackPath;

    /** 登录密码(MD5加密) */
    private String password;

    /** 状态(0未入住 1已入住 2已退住) */
    @Excel(name = "状态", readConverterExp = "0=未入住,1=已入住,2=已退住")
    private String status;

    /** 当前用户ID(用于数据权限过滤,不映射到数据库) */
    private transient Long currentUserId;

    public Long getCurrentUserId()
    {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId)
    {
        this.currentUserId = currentUserId;
    }

    public void setElderId(Long elderId)
    {
        this.elderId = elderId;
    }

    public Long getElderId()
    {
        return elderId;
    }
    public void setElderName(String elderName)
    {
        this.elderName = elderName;
    }

    public String getElderName()
    {
        return elderName;
    }
    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getGender()
    {
        return gender;
    }
    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    public String getIdCard()
    {
        return idCard;
    }
    public void setBirthDate(Date birthDate)
    {
        this.birthDate = birthDate;
    }

    public Date getBirthDate()
    {
        return birthDate;
    }
    public void setAge(Long age)
    {
        this.age = age;
    }

    public Long getAge()
    {
        return age;
    }
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPhone()
    {
        return phone;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getAddress()
    {
        return address;
    }
    public void setEmergencyContact(String emergencyContact)
    {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyContact()
    {
        return emergencyContact;
    }
    public void setEmergencyPhone(String emergencyPhone)
    {
        this.emergencyPhone = emergencyPhone;
    }

    public String getEmergencyPhone()
    {
        return emergencyPhone;
    }
    public void setHealthStatus(String healthStatus)
    {
        this.healthStatus = healthStatus;
    }

    public String getHealthStatus()
    {
        return healthStatus;
    }
    public void setCareLevel(String careLevel)
    {
        this.careLevel = careLevel;
    }

    public String getCareLevel()
    {
        return careLevel;
    }

    public void setMealLevelCode(String mealLevelCode)
    {
        this.mealLevelCode = mealLevelCode;
    }

    public String getMealLevelCode()
    {
        return mealLevelCode;
    }

    public void setSpecialNeeds(String specialNeeds)
    {
        this.specialNeeds = specialNeeds;
    }

    public String getSpecialNeeds()
    {
        return specialNeeds;
    }
    public void setPhotoPath(String photoPath)
    {
        this.photoPath = photoPath;
    }

    public String getPhotoPath()
    {
        return photoPath;
    }

    public void setIdCardFrontPath(String idCardFrontPath)
    {
        this.idCardFrontPath = idCardFrontPath;
    }

    public String getIdCardFrontPath()
    {
        return idCardFrontPath;
    }

    public void setIdCardBackPath(String idCardBackPath)
    {
        this.idCardBackPath = idCardBackPath;
    }

    public String getIdCardBackPath()
    {
        return idCardBackPath;
    }

  public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

  public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("elderId", getElderId())
            .append("elderName", getElderName())
            .append("gender", getGender())
            .append("idCard", getIdCard())
            .append("birthDate", getBirthDate())
            .append("age", getAge())
            .append("phone", getPhone())
            .append("address", getAddress())
            .append("emergencyContact", getEmergencyContact())
            .append("emergencyPhone", getEmergencyPhone())
            .append("healthStatus", getHealthStatus())
            .append("careLevel", getCareLevel())
            .append("mealLevelCode", getMealLevelCode())
            .append("specialNeeds", getSpecialNeeds())
            .append("photoPath", getPhotoPath())
            .append("idCardFrontPath", getIdCardFrontPath())
            .append("idCardBackPath", getIdCardBackPath())
            .append("password", getPassword())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}