package com.ruoyi.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户-老人关联(家属管理)对象 elder_family
 *
 * @author ruoyi
 * @date 2025-01-14
 */
public class ElderFamily extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 家属关系ID */
    private Long familyId;

    /** 用户ID(关联sys_user表) */
    @Excel(name = "用户ID")
    private Long userId;

    /** 老人ID(关联elder_info表) */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 关系类型(1:子女 2:配偶 3:兄弟姐妹 4:其他亲属 5:朋友) */
    @Excel(name = "关系类型", readConverterExp = "1=子女,2=配偶,3=兄弟姐妹,4=其他亲属,5=朋友")
    private String relationType;

    /** 关系描述(如:儿子、女儿、配偶等) */
    @Excel(name = "关系描述")
    private String relationName;

    /** 是否默认老人(0:否 1:是) */
    @Excel(name = "是否默认老人", readConverterExp = "0=否,1=是")
    private String isDefault;

    /** 是否主要联系人(0:否 1:是) */
    @Excel(name = "是否主要联系人", readConverterExp = "0=否,1=是")
    private String isMainContact;

    /** 关联状态(0:正常 1:已解除) */
    @Excel(name = "关联状态", readConverterExp = "0=正常,1=已解除")
    private String status;

    // 关联查询字段
    /** 家属姓名 */
    private String nickName;

    /** 家属手机号 */
    private String phonenumber;

    /** 老人姓名 */
    private String elderName;

    public void setFamilyId(Long familyId)
    {
        this.familyId = familyId;
    }

    public Long getFamilyId()
    {
        return familyId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setElderId(Long elderId)
    {
        this.elderId = elderId;
    }

    public Long getElderId()
    {
        return elderId;
    }

    public void setRelationType(String relationType)
    {
        this.relationType = relationType;
    }

    public String getRelationType()
    {
        return relationType;
    }

    public void setRelationName(String relationName)
    {
        this.relationName = relationName;
    }

    public String getRelationName()
    {
        return relationName;
    }

    public void setIsDefault(String isDefault)
    {
        this.isDefault = isDefault;
    }

    public String getIsDefault()
    {
        return isDefault;
    }

    public void setIsMainContact(String isMainContact)
    {
        this.isMainContact = isMainContact;
    }

    public String getIsMainContact()
    {
        return isMainContact;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getPhonenumber()
    {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }

    public String getElderName()
    {
        return elderName;
    }

    public void setElderName(String elderName)
    {
        this.elderName = elderName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("familyId", getFamilyId())
            .append("userId", getUserId())
            .append("elderId", getElderId())
            .append("relationType", getRelationType())
            .append("relationName", getRelationName())
            .append("isDefault", getIsDefault())
            .append("isMainContact", getIsMainContact())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
