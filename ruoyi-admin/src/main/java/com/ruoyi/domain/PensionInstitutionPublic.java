package com.ruoyi.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 养老机构公示信息对象 pension_institution_public
 *
 * @author ruoyi
 * @date 2025-11-10
 */
public class PensionInstitutionPublic extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 公示ID */
    private Long publicId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 机构简介 */
    @Excel(name = "机构简介")
    private String institutionIntro;

    /** 服务范围 */
    @Excel(name = "服务范围")
    private String serviceScope;

    /** 服务特色 */
    @Excel(name = "服务特色")
    private String serviceFeatures;

    /** 占地面积(平方米) */
    @Excel(name = "占地面积")
    private java.math.BigDecimal landArea;

    /** 建筑面积(平方米) */
    @Excel(name = "建筑面积")
    private java.math.BigDecimal buildingArea;

    /** 环境图片(JSON数组) */
    private String environmentImgs;

    /** 评级(1-5星) */
    @Excel(name = "评级")
    private String rating;

    /** 收费区间最小值 */
    @Excel(name = "最低收费")
    private java.math.BigDecimal feeRangeMin;

    /** 收费区间最大值 */
    @Excel(name = "最高收费")
    private java.math.BigDecimal feeRangeMax;

    /** 收住老人类型(多选,逗号分隔: self_care,semi_disabled,disabled,dementia) */
    @Excel(name = "收住对象")
    private String acceptElderType;

    /** 是否已公示(0=未公示,1=已公示) */
    @Excel(name = "公示状态", readConverterExp = "0=未公示,1=已公示")
    private String isPublished;

    // 关联查询字段(不映射到数据库)
    /** 机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 统一信用代码 */
    @Excel(name = "统一信用代码")
    private String creditCode;

    /** 机构备案号 */
    @Excel(name = "机构备案号")
    private String recordNumber;

    /** 机构地址 */
    private String actualAddress;

    /** 床位数 */
    @Excel(name = "床位数")
    private Integer bedCount;

    /** 监管账户 */
    private String superviseAccount;

    /** 收费区间(来自机构表) */
    private String feeRange;

    /** 当前用户ID (用于数据权限过滤,不映射到数据库) */
    private Long currentUserId;

    public void setPublicId(Long publicId)
    {
        this.publicId = publicId;
    }

    public Long getPublicId()
    {
        return publicId;
    }

    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
    }

    public void setInstitutionIntro(String institutionIntro)
    {
        this.institutionIntro = institutionIntro;
    }

    public String getInstitutionIntro()
    {
        return institutionIntro;
    }

    public void setServiceScope(String serviceScope)
    {
        this.serviceScope = serviceScope;
    }

    public String getServiceScope()
    {
        return serviceScope;
    }

    public void setServiceFeatures(String serviceFeatures)
    {
        this.serviceFeatures = serviceFeatures;
    }

    public String getServiceFeatures()
    {
        return serviceFeatures;
    }

    public void setLandArea(java.math.BigDecimal landArea)
    {
        this.landArea = landArea;
    }

    public java.math.BigDecimal getLandArea()
    {
        return landArea;
    }

    public void setBuildingArea(java.math.BigDecimal buildingArea)
    {
        this.buildingArea = buildingArea;
    }

    public java.math.BigDecimal getBuildingArea()
    {
        return buildingArea;
    }

    public void setEnvironmentImgs(String environmentImgs)
    {
        this.environmentImgs = environmentImgs;
    }

    public String getEnvironmentImgs()
    {
        return environmentImgs;
    }

    public void setRating(String rating)
    {
        this.rating = rating;
    }

    public String getRating()
    {
        return rating;
    }

    public void setFeeRangeMin(java.math.BigDecimal feeRangeMin)
    {
        this.feeRangeMin = feeRangeMin;
    }

    public java.math.BigDecimal getFeeRangeMin()
    {
        return feeRangeMin;
    }

    public void setFeeRangeMax(java.math.BigDecimal feeRangeMax)
    {
        this.feeRangeMax = feeRangeMax;
    }

    public java.math.BigDecimal getFeeRangeMax()
    {
        return feeRangeMax;
    }

    public void setAcceptElderType(String acceptElderType)
    {
        this.acceptElderType = acceptElderType;
    }

    public String getAcceptElderType()
    {
        return acceptElderType;
    }

    public void setIsPublished(String isPublished)
    {
        this.isPublished = isPublished;
    }

    public String getIsPublished()
    {
        return isPublished;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getActualAddress() {
        return actualAddress;
    }

    public void setActualAddress(String actualAddress) {
        this.actualAddress = actualAddress;
    }

    public Integer getBedCount() {
        return bedCount;
    }

    public void setBedCount(Integer bedCount) {
        this.bedCount = bedCount;
    }

    public String getSuperviseAccount() {
        return superviseAccount;
    }

    public void setSuperviseAccount(String superviseAccount) {
        this.superviseAccount = superviseAccount;
    }

    public String getFeeRange() {
        return feeRange;
    }

    public void setFeeRange(String feeRange) {
        this.feeRange = feeRange;
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
            .append("publicId", getPublicId())
            .append("institutionId", getInstitutionId())
            .append("institutionIntro", getInstitutionIntro())
            .append("serviceScope", getServiceScope())
            .append("serviceFeatures", getServiceFeatures())
            .append("landArea", getLandArea())
            .append("buildingArea", getBuildingArea())
            .append("environmentImgs", getEnvironmentImgs())
            .append("rating", getRating())
            .append("feeRangeMin", getFeeRangeMin())
            .append("feeRangeMax", getFeeRangeMax())
            .append("acceptElderType", getAcceptElderType())
            .append("isPublished", getIsPublished())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
