package com.ruoyi.common.core.domain;

/**
 * 老人关联信息（用于H5用户管理显示关联的老人）
 *
 * @author ruoyi
 */
public class ElderRelationInfo
{
    /** 老人ID */
    private Long elderId;

    /** 老人姓名 */
    private String elderName;

    /** 关系名称（女儿、儿子、配偶等） */
    private String relationName;

    /** 身份证号 */
    private String idCard;

    /** 年龄 */
    private Long age;

    /** 性别(1男 2女) */
    private String gender;

    /** 联系电话 */
    private String phone;

    /** 入住状态(0未入住 1已入住 2已退住) */
    private String status;

    public Long getElderId()
    {
        return elderId;
    }

    public void setElderId(Long elderId)
    {
        this.elderId = elderId;
    }

    public String getElderName()
    {
        return elderName;
    }

    public void setElderName(String elderName)
    {
        this.elderName = elderName;
    }

    public String getRelationName()
    {
        return relationName;
    }

    public void setRelationName(String relationName)
    {
        this.relationName = relationName;
    }

    public String getIdCard()
    {
        return idCard;
    }

    public void setIdCard(String idCard)
    {
        this.idCard = idCard;
    }

    public Long getAge()
    {
        return age;
    }

    public void setAge(Long age)
    {
        this.age = age;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
}
