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

    /** 机构主图 */
    private String mainPicture;

    /** VR全景图片 */
    private String vrImage;

    /** 评级(1-5星) */
    @Excel(name = "评级")
    private String rating;

    /** 收费区间最小值 */
    @Excel(name = "最低收费")
    private java.math.BigDecimal feeRangeMin;

    /** 收费区间最大值 */
    @Excel(name = "最高收费")
    private java.math.BigDecimal feeRangeMax;

    /** 护理费最低价 */
    @Excel(name = "护理费最低价")
    private java.math.BigDecimal nursingFeeMin;

    /** 护理费最高价 */
    @Excel(name = "护理费最高价")
    private java.math.BigDecimal nursingFeeMax;

    /** 床位费最低价 */
    @Excel(name = "床位费最低价")
    private java.math.BigDecimal bedFeeMin;

    /** 床位费最高价 */
    @Excel(name = "床位费最高价")
    private java.math.BigDecimal bedFeeMax;

    /** 膳食费最低价 */
    @Excel(name = "膳食费最低价")
    private java.math.BigDecimal mealFeeMin;

    /** 膳食费最高价 */
    @Excel(name = "膳食费最高价")
    private java.math.BigDecimal mealFeeMax;

    /** 收住老人类型(多选,逗号分隔: self_care,semi_disabled,disabled,dementia) */
    @Excel(name = "收住对象")
    private String acceptElderType;

    /** 是否已公示(0=未公示,1=已公示) */
    @Excel(name = "公示状态", readConverterExp = "0=未公示,1=已公示")
    private String isPublished;

    /** 房间设施图片(JSON数组) */
    private String roomFacilities;

    /** 基础设施图片(JSON数组) */
    private String basicFacilities;

    /** 园址设施图片(JSON数组) */
    private String parkFacilities;

    /** 生活设施选项(JSON数组) */
    private String lifeFacilities;

    /** 医疗设施选项(JSON数组) */
    private String medicalFacilities;

    /** 每日服务安排(JSON数组) */
    private String dailyServices;

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

    /** 联系人(关联查询字段，不映射到数据库) */
    private String contactPerson;

    /** 联系电话(关联查询字段，不映射到数据库) */
    private String contactPhone;

    /** 联系邮箱(关联查询字段，不映射到数据库) */
    private String contactEmail;

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

    public String getMainPicture()
    {
        return mainPicture;
    }

    public void setMainPicture(String mainPicture)
    {
        this.mainPicture = mainPicture;
    }

    public String getVrImage()
    {
        return vrImage;
    }

    public void setVrImage(String vrImage)
    {
        this.vrImage = vrImage;
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

    public void setNursingFeeMin(java.math.BigDecimal nursingFeeMin)
    {
        this.nursingFeeMin = nursingFeeMin;
    }

    public java.math.BigDecimal getNursingFeeMin()
    {
        return nursingFeeMin;
    }

    public void setNursingFeeMax(java.math.BigDecimal nursingFeeMax)
    {
        this.nursingFeeMax = nursingFeeMax;
    }

    public java.math.BigDecimal getNursingFeeMax()
    {
        return nursingFeeMax;
    }

    public void setBedFeeMin(java.math.BigDecimal bedFeeMin)
    {
        this.bedFeeMin = bedFeeMin;
    }

    public java.math.BigDecimal getBedFeeMin()
    {
        return bedFeeMin;
    }

    public void setBedFeeMax(java.math.BigDecimal bedFeeMax)
    {
        this.bedFeeMax = bedFeeMax;
    }

    public java.math.BigDecimal getBedFeeMax()
    {
        return bedFeeMax;
    }

    public void setMealFeeMin(java.math.BigDecimal mealFeeMin)
    {
        this.mealFeeMin = mealFeeMin;
    }

    public java.math.BigDecimal getMealFeeMin()
    {
        return mealFeeMin;
    }

    public void setMealFeeMax(java.math.BigDecimal mealFeeMax)
    {
        this.mealFeeMax = mealFeeMax;
    }

    public java.math.BigDecimal getMealFeeMax()
    {
        return mealFeeMax;
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

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getRoomFacilities() {
        return roomFacilities;
    }

    public void setRoomFacilities(String roomFacilities) {
        this.roomFacilities = roomFacilities;
    }

    public String getBasicFacilities() {
        return basicFacilities;
    }

    public void setBasicFacilities(String basicFacilities) {
        this.basicFacilities = basicFacilities;
    }

    public String getParkFacilities() {
        return parkFacilities;
    }

    public void setParkFacilities(String parkFacilities) {
        this.parkFacilities = parkFacilities;
    }

    public String getLifeFacilities() {
        return lifeFacilities;
    }

    public void setLifeFacilities(String lifeFacilities) {
        this.lifeFacilities = lifeFacilities;
    }

    public String getMedicalFacilities() {
        return medicalFacilities;
    }

    public void setMedicalFacilities(String medicalFacilities) {
        this.medicalFacilities = medicalFacilities;
    }

    public String getDailyServices() {
        return dailyServices;
    }

    public void setDailyServices(String dailyServices) {
        this.dailyServices = dailyServices;
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
            .append("nursingFeeMin", getNursingFeeMin())
            .append("nursingFeeMax", getNursingFeeMax())
            .append("bedFeeMin", getBedFeeMin())
            .append("bedFeeMax", getBedFeeMax())
            .append("mealFeeMin", getMealFeeMin())
            .append("mealFeeMax", getMealFeeMax())
            .append("acceptElderType", getAcceptElderType())
            .append("isPublished", getIsPublished())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
