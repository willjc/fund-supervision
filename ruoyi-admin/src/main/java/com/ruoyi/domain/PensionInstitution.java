package com.ruoyi.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 养老机构信息对象 pension_institution
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public class PensionInstitution extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 机构ID */
    private Long institutionId;

    /** 机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 统一信用代码 */
    @Excel(name = "统一信用代码")
    private String creditCode;

    /** 注册资金 */
    @Excel(name = "注册资金")
    private Double registeredCapital;

    /** 注册地址 */
    @Excel(name = "注册地址")
    private String registeredAddress;

    /** 所属街道/区域 */
    @Excel(name = "所属街道/区域")
    private String street;

    /** 实际经营地址 */
    @Excel(name = "实际经营地址")
    private String actualAddress;

    /** 法定代表人 */
    @Excel(name = "法定代表人")
    private String legalPerson;

    /** 联系人 */
    @Excel(name = "联系人")
    private String contactPerson;

    /** 联系电话 */
    @Excel(name = "联系电话")
    private String contactPhone;

    /** 联系邮箱 */
    @Excel(name = "联系邮箱")
    private String contactEmail;

    /** 经营范围 */
    @Excel(name = "经营范围")
    private String businessScope;

    /** 机构类型 */
    @Excel(name = "机构类型", readConverterExp = "1=民办机构,2=公办机构,3=公建民营")
    private String institutionType;

    /** 机构类型中文描述 */
    private String institutionTypeText;

    /** 床位数 */
    @Excel(name = "床位数")
    private Long bedCount;

    /** 区县代码(关联pension_district字典) */
    @Excel(name = "区县代码")
    private String districtCode;

    /** 区域代码(标准化行政区划代码) */
    @Excel(name = "区域代码")
    private String areaCode;

    /** 机构性质: 1-民办 2-公办 3-公建民营 */
    @Excel(name = "机构性质", readConverterExp = "1=民办,2=公办,3=公建民营")
    private String institutionNature;

    /** 收住类型(多选,逗号分隔): 1-自理,2-半护理,3-全护理,4-失能,5-失智 */
    @Excel(name = "收住类型")
    private String careLevels;

    /** 医疗条件: 1-内设医疗机构,2-与医疗机构合作,3-自营医疗机构,4-无医养结合 */
    @Excel(name = "医疗条件", readConverterExp = "1=内设医疗机构,2=与医疗机构合作,3=自营医疗机构,4=无医养结合")
    private String medicalCondition;

    /** 星级评分: 1-5星 */
    @Excel(name = "星级评分")
    private Integer ratingLevel;

    /** 最低价格(元/月) */
    @Excel(name = "最低价格")
    private java.math.BigDecimal priceRangeMin;

    /** 最高价格(元/月) */
    @Excel(name = "最高价格")
    private java.math.BigDecimal priceRangeMax;

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

    /** 机构封面图片(JSON格式) */
    private String coverImages;

    /** 是否支持免费试住: 0-否 1-是 */
    @Excel(name = "免费试住", readConverterExp = "0=否,1=是")
    private String freeTrial;

    /** 收费区间 */
    @Excel(name = "收费区间")
    private String feeRange;

    /** 成立时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "成立时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date establishedDate;

    /** 兴办机构 */
    @Excel(name = "兴办机构")
    private String organizer;

    /** 负责人姓名 */
    @Excel(name = "负责人姓名")
    private String responsibleName;

    /** 负责人身份证号 */
    @Excel(name = "负责人身份证号")
    private String responsibleIdCard;

    /** 负责人居住地 */
    @Excel(name = "负责人居住地")
    private String responsibleAddress;

    /** 负责人电话 */
    @Excel(name = "负责人电话")
    private String responsiblePhone;

    /** 备案号 */
    @Excel(name = "备案号")
    private String recordNumber;

    /** 固定资产净额 */
    @Excel(name = "固定资产净额")
    private Double fixedAssets;

    /** 营业执照文件路径 */
    @Excel(name = "营业执照")
    private String businessLicense;

    /** 批准证书文件路径 */
    @Excel(name = "批准证书")
    private String approvalCertificate;

    /** 监管协议文件路径 */
    @Excel(name = "监管协议")
    private String supervisionAgreement;

    /** 基本结算账户 */
    @Excel(name = "基本结算账户")
    private String bankAccount;

    /** 基本账户开户行 */
    @Excel(name = "基本账户开户行")
    private String basicBank;

    /** 监管账户 */
    @Excel(name = "监管账户")
    private String superviseAccount;

    /** 监管账户开户行 */
    @Excel(name = "监管账户开户行")
    private String superviseBank;

    /** 状态 */
    @Excel(name = "状态", readConverterExp = "0=待审批,1=已入驻,2=已驳回,3=解除监管,4=草稿,5=维护中,6=维护待审批")
    private String status;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /** 审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审批时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    /** 审批人 */
    @Excel(name = "审批人")
    private String approveUser;

    /** 审批意见 */
    @Excel(name = "审批意见")
    private String approveRemark;

    /** 黑名单标志 */
    @Excel(name = "黑名单标志", readConverterExp = "0=正常,1=黑名单")
    private String blacklistFlag;

    /** 当前用户ID(用于数据权限过滤,不映射到数据库) */
    private Long currentUserId;

    /** 区域代码多选参数(用于筛选,不映射到数据库) */
    private transient List<String> areaCodes;

    /** 街道名称多选参数(用于筛选,不映射到数据库) */
    private transient List<String> streetNames;

    /** 是否已开户(不映射到数据库,通过superviseAccount和bankAccount计算) */
    private transient Boolean hasSupervisionAccount;

    /** 预收服务费余额(不映射到数据库,从account_info统计) */
    private transient java.math.BigDecimal serviceFeeBalance;

    /** 预收押金余额(不映射到数据库,从account_info统计) */
    private transient java.math.BigDecimal depositBalance;

    /** 预收会员费余额(不映射到数据库,从account_info统��) */
    private transient java.math.BigDecimal memberFeeBalance;

    /** 实际入住老人数(不映射到数据库,从elder_check_in统计) */
    private transient Integer actualElders;

    /** 申请时间筛选-开始时间(仅用于查询条件,不映射到数据库) */
    private transient String beginTime;

    /** 申请时间筛选-结束���间(仅用于查询条件,不映射到数据库) */
    private transient String endTime;

    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
    }

    public void setInstitutionName(String institutionName)
    {
        this.institutionName = institutionName;
    }

    public String getInstitutionName()
    {
        return institutionName;
    }

    public void setCreditCode(String creditCode)
    {
        this.creditCode = creditCode;
    }

    public String getCreditCode()
    {
        return creditCode;
    }

    public void setRegisteredCapital(Double registeredCapital)
    {
        this.registeredCapital = registeredCapital;
    }

    public Double getRegisteredCapital()
    {
        return registeredCapital;
    }

    public void setRegisteredAddress(String registeredAddress)
    {
        this.registeredAddress = registeredAddress;
    }

    public String getRegisteredAddress()
    {
        return registeredAddress;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getStreet()
    {
        return street;
    }

    public void setActualAddress(String actualAddress)
    {
        this.actualAddress = actualAddress;
    }

    public String getActualAddress()
    {
        return actualAddress;
    }

    public void setLegalPerson(String legalPerson)
    {
        this.legalPerson = legalPerson;
    }

    public String getLegalPerson()
    {
        return legalPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }

    public String getContactEmail()
    {
        return contactEmail;
    }

    public void setBusinessScope(String businessScope)
    {
        this.businessScope = businessScope;
    }

    public String getBusinessScope()
    {
        return businessScope;
    }

    public void setInstitutionType(String institutionType)
    {
        this.institutionType = institutionType;
    }

    public String getInstitutionType()
    {
        return institutionType;
    }

    /**
     * 获取机构类型中文描述
     */
    public String getInstitutionTypeText()
    {
        if (institutionType == null) {
            return "";
        }
        switch (institutionType) {
            case "1":
                return "民办机构";
            case "2":
                return "公办机构";
            case "3":
                return "公建民营";
            default:
                // 如果已经是中文描述，直接返回
                if (institutionType.contains("机构") || institutionType.contains("养老")) {
                    return institutionType;
                }
                return "未知类型(" + institutionType + ")";
        }
    }

    public void setInstitutionTypeText(String institutionTypeText)
    {
        this.institutionTypeText = institutionTypeText;
    }

    public void setBedCount(Long bedCount)
    {
        this.bedCount = bedCount;
    }

    public Long getBedCount()
    {
        return bedCount;
    }

    public void setDistrictCode(String districtCode)
    {
        this.districtCode = districtCode;
    }

    public String getDistrictCode()
    {
        return districtCode;
    }

    public void setAreaCode(String areaCode)
    {
        this.areaCode = areaCode;
    }

    public String getAreaCode()
    {
        return areaCode;
    }

    public void setInstitutionNature(String institutionNature)
    {
        this.institutionNature = institutionNature;
    }

    public String getInstitutionNature()
    {
        return institutionNature;
    }

    public void setCareLevels(String careLevels)
    {
        this.careLevels = careLevels;
    }

    public String getCareLevels()
    {
        return careLevels;
    }

    public void setMedicalCondition(String medicalCondition)
    {
        this.medicalCondition = medicalCondition;
    }

    public String getMedicalCondition()
    {
        return medicalCondition;
    }

    public void setRatingLevel(Integer ratingLevel)
    {
        this.ratingLevel = ratingLevel;
    }

    public Integer getRatingLevel()
    {
        return ratingLevel;
    }

    public void setPriceRangeMin(java.math.BigDecimal priceRangeMin)
    {
        this.priceRangeMin = priceRangeMin;
    }

    public java.math.BigDecimal getPriceRangeMin()
    {
        return priceRangeMin;
    }

    public void setPriceRangeMax(java.math.BigDecimal priceRangeMax)
    {
        this.priceRangeMax = priceRangeMax;
    }

    public java.math.BigDecimal getPriceRangeMax()
    {
        return priceRangeMax;
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

    public void setCoverImages(String coverImages)
    {
        this.coverImages = coverImages;
    }

    public String getCoverImages()
    {
        return coverImages;
    }

    public void setFreeTrial(String freeTrial)
    {
        this.freeTrial = freeTrial;
    }

    public String getFreeTrial()
    {
        return freeTrial;
    }

    public void setFeeRange(String feeRange)
    {
        this.feeRange = feeRange;
    }

    public String getFeeRange()
    {
        return feeRange;
    }

    public void setEstablishedDate(Date establishedDate)
    {
        this.establishedDate = establishedDate;
    }

    public Date getEstablishedDate()
    {
        return establishedDate;
    }

    public void setOrganizer(String organizer)
    {
        this.organizer = organizer;
    }

    public String getOrganizer()
    {
        return organizer;
    }

    public void setResponsibleName(String responsibleName)
    {
        this.responsibleName = responsibleName;
    }

    public String getResponsibleName()
    {
        return responsibleName;
    }

    public void setResponsibleIdCard(String responsibleIdCard)
    {
        this.responsibleIdCard = responsibleIdCard;
    }

    public String getResponsibleIdCard()
    {
        return responsibleIdCard;
    }

    public void setResponsibleAddress(String responsibleAddress)
    {
        this.responsibleAddress = responsibleAddress;
    }

    public String getResponsibleAddress()
    {
        return responsibleAddress;
    }

    public void setResponsiblePhone(String responsiblePhone)
    {
        this.responsiblePhone = responsiblePhone;
    }

    public String getResponsiblePhone()
    {
        return responsiblePhone;
    }

    public void setRecordNumber(String recordNumber)
    {
        this.recordNumber = recordNumber;
    }

    public String getRecordNumber()
    {
        return recordNumber;
    }

    public void setFixedAssets(Double fixedAssets)
    {
        this.fixedAssets = fixedAssets;
    }

    public Double getFixedAssets()
    {
        return fixedAssets;
    }

    public void setBusinessLicense(String businessLicense)
    {
        this.businessLicense = businessLicense;
    }

    public String getBusinessLicense()
    {
        return businessLicense;
    }

    public void setApprovalCertificate(String approvalCertificate)
    {
        this.approvalCertificate = approvalCertificate;
    }

    public String getApprovalCertificate()
    {
        return approvalCertificate;
    }

    public void setSupervisionAgreement(String supervisionAgreement)
    {
        this.supervisionAgreement = supervisionAgreement;
    }

    public String getSupervisionAgreement()
    {
        return supervisionAgreement;
    }

    public void setBankAccount(String bankAccount)
    {
        this.bankAccount = bankAccount;
    }

    public String getBankAccount()
    {
        return bankAccount;
    }

    public void setSuperviseAccount(String superviseAccount)
    {
        this.superviseAccount = superviseAccount;
    }

    public String getSuperviseAccount()
    {
        return superviseAccount;
    }

    public void setSuperviseBank(String superviseBank)
    {
        this.superviseBank = superviseBank;
    }

    public String getSuperviseBank()
    {
        return superviseBank;
    }

    public void setBasicBank(String basicBank)
    {
        this.basicBank = basicBank;
    }

    public String getBasicBank()
    {
        return basicBank;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setApplyTime(Date applyTime)
    {
        this.applyTime = applyTime;
    }

    public Date getApplyTime()
    {
        return applyTime;
    }

    public void setApproveTime(Date approveTime)
    {
        this.approveTime = approveTime;
    }

    public Date getApproveTime()
    {
        return approveTime;
    }

    public void setApproveUser(String approveUser)
    {
        this.approveUser = approveUser;
    }

    public String getApproveUser()
    {
        return approveUser;
    }

    public void setApproveRemark(String approveRemark)
    {
        this.approveRemark = approveRemark;
    }

    public String getApproveRemark()
    {
        return approveRemark;
    }

    public void setBlacklistFlag(String blacklistFlag)
    {
        this.blacklistFlag = blacklistFlag;
    }

    public String getBlacklistFlag()
    {
        return blacklistFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("institutionId", getInstitutionId())
            .append("institutionName", getInstitutionName())
            .append("creditCode", getCreditCode())
            .append("registeredCapital", getRegisteredCapital())
            .append("registeredAddress", getRegisteredAddress())
            .append("street", getStreet())
            .append("actualAddress", getActualAddress())
            .append("legalPerson", getLegalPerson())
            .append("contactPerson", getContactPerson())
            .append("contactPhone", getContactPhone())
            .append("contactEmail", getContactEmail())
            .append("businessScope", getBusinessScope())
            .append("institutionType", getInstitutionType())
            .append("bedCount", getBedCount())
            .append("districtCode", getDistrictCode())
            .append("areaCode", getAreaCode())
            .append("institutionNature", getInstitutionNature())
            .append("careLevels", getCareLevels())
            .append("medicalCondition", getMedicalCondition())
            .append("ratingLevel", getRatingLevel())
            .append("priceRangeMin", getPriceRangeMin())
            .append("priceRangeMax", getPriceRangeMax())
            .append("nursingFeeMin", getNursingFeeMin())
            .append("nursingFeeMax", getNursingFeeMax())
            .append("bedFeeMin", getBedFeeMin())
            .append("bedFeeMax", getBedFeeMax())
            .append("mealFeeMin", getMealFeeMin())
            .append("mealFeeMax", getMealFeeMax())
            .append("coverImages", getCoverImages())
            .append("freeTrial", getFreeTrial())
            .append("feeRange", getFeeRange())
            .append("establishedDate", getEstablishedDate())
            .append("organizer", getOrganizer())
            .append("responsibleName", getResponsibleName())
            .append("responsibleIdCard", getResponsibleIdCard())
            .append("responsibleAddress", getResponsibleAddress())
            .append("responsiblePhone", getResponsiblePhone())
            .append("recordNumber", getRecordNumber())
            .append("fixedAssets", getFixedAssets())
            .append("businessLicense", getBusinessLicense())
            .append("approvalCertificate", getApprovalCertificate())
            .append("supervisionAgreement", getSupervisionAgreement())
            .append("bankAccount", getBankAccount())
            .append("basicBank", getBasicBank())
            .append("superviseAccount", getSuperviseAccount())
            .append("superviseBank", getSuperviseBank())
            .append("status", getStatus())
            .append("applyTime", getApplyTime())
            .append("approveTime", getApproveTime())
            .append("approveUser", getApproveUser())
            .append("approveRemark", getApproveRemark())
            .append("blacklistFlag", getBlacklistFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }

    public Long getCurrentUserId()
    {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId)
    {
        this.currentUserId = currentUserId;
    }

    public List<String> getAreaCodes()
    {
        return areaCodes;
    }

    public void setAreaCodes(List<String> areaCodes)
    {
        this.areaCodes = areaCodes;
    }

    public List<String> getStreetNames()
    {
        return streetNames;
    }

    public void setStreetNames(List<String> streetNames)
    {
        this.streetNames = streetNames;
    }

    public Boolean getHasSupervisionAccount()
    {
        return hasSupervisionAccount;
    }

    public void setHasSupervisionAccount(Boolean hasSupervisionAccount)
    {
        this.hasSupervisionAccount = hasSupervisionAccount;
    }

    public java.math.BigDecimal getServiceFeeBalance()
    {
        return serviceFeeBalance;
    }

    public void setServiceFeeBalance(java.math.BigDecimal serviceFeeBalance)
    {
        this.serviceFeeBalance = serviceFeeBalance;
    }

    public java.math.BigDecimal getDepositBalance()
    {
        return depositBalance;
    }

    public void setDepositBalance(java.math.BigDecimal depositBalance)
    {
        this.depositBalance = depositBalance;
    }

    public java.math.BigDecimal getMemberFeeBalance()
    {
        return memberFeeBalance;
    }

    public void setMemberFeeBalance(java.math.BigDecimal memberFeeBalance)
    {
        this.memberFeeBalance = memberFeeBalance;
    }

    public Integer getActualElders()
    {
        return actualElders;
    }

    public void setActualElders(Integer actualElders)
    {
        this.actualElders = actualElders;
    }

    public String getBeginTime()
    {
        return beginTime;
    }

    public void setBeginTime(String beginTime)
    {
        this.beginTime = beginTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }
}
