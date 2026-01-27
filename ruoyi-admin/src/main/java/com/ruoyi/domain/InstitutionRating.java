package com.ruoyi.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 机构评级对象 institution_rating
 *
 * @author ruoyi
 * @date 2025-12-07
 */
public class InstitutionRating extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评级ID */
    private Long ratingId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 统一信用代码 */
    @Excel(name = "统一信用代码")
    private String creditCode;

    /** 评级等级(1-5星) */
    @Excel(name = "评级等级", readConverterExp = "1=一星,2=二星,3=三星,4=四星,5=五星")
    private Integer ratingLevel;

    /** 总分(0-100分) */
    @Excel(name = "总分")
    private BigDecimal totalScore;

    /** 服务质量得分(0-25分) */
    @Excel(name = "服务质量得分")
    private BigDecimal serviceScore;

    /** 设施环境得分(0-25分) */
    @Excel(name = "设施环境得分")
    private BigDecimal facilityScore;

    /** 管理水平得分(0-25分) */
    @Excel(name = "管理水平得分")
    private BigDecimal managementScore;

    /** 安全卫生得分(0-25分) */
    @Excel(name = "安全卫生得分")
    private BigDecimal safetyScore;

    /** 评级日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "评级日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date ratingDate;

    /** 有效期至 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期至", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expireDate;

    /** 有效期(月) */
    @Excel(name = "有效期(月)")
    private Integer validityPeriod;

    /** 评级状态(1-有效,0-已过期) */
    @Excel(name = "评级状态", readConverterExp = "1=有效,0=已过期")
    private String ratingStatus;

    /** 评级意见 */
    @Excel(name = "评级意见")
    private String ratingRemark;

    public void setRatingId(Long ratingId)
    {
        this.ratingId = ratingId;
    }

    public Long getRatingId()
    {
        return ratingId;
    }

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

    public void setRatingLevel(Integer ratingLevel)
    {
        this.ratingLevel = ratingLevel;
    }

    public Integer getRatingLevel()
    {
        return ratingLevel;
    }

    public void setTotalScore(BigDecimal totalScore)
    {
        this.totalScore = totalScore;
    }

    public BigDecimal getTotalScore()
    {
        return totalScore;
    }

    public void setServiceScore(BigDecimal serviceScore)
    {
        this.serviceScore = serviceScore;
    }

    public BigDecimal getServiceScore()
    {
        return serviceScore;
    }

    public void setFacilityScore(BigDecimal facilityScore)
    {
        this.facilityScore = facilityScore;
    }

    public BigDecimal getFacilityScore()
    {
        return facilityScore;
    }

    public void setManagementScore(BigDecimal managementScore)
    {
        this.managementScore = managementScore;
    }

    public BigDecimal getManagementScore()
    {
        return managementScore;
    }

    public void setSafetyScore(BigDecimal safetyScore)
    {
        this.safetyScore = safetyScore;
    }

    public BigDecimal getSafetyScore()
    {
        return safetyScore;
    }

    public void setRatingDate(Date ratingDate)
    {
        this.ratingDate = ratingDate;
    }

    public Date getRatingDate()
    {
        return ratingDate;
    }

    public void setExpireDate(Date expireDate)
    {
        this.expireDate = expireDate;
    }

    public Date getExpireDate()
    {
        return expireDate;
    }

    public void setValidityPeriod(Integer validityPeriod)
    {
        this.validityPeriod = validityPeriod;
    }

    public Integer getValidityPeriod()
    {
        return validityPeriod;
    }

    public void setRatingStatus(String ratingStatus)
    {
        this.ratingStatus = ratingStatus;
    }

    public String getRatingStatus()
    {
        return ratingStatus;
    }

    public void setRatingRemark(String ratingRemark)
    {
        this.ratingRemark = ratingRemark;
    }

    public String getRatingRemark()
    {
        return ratingRemark;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("ratingId", getRatingId())
            .append("institutionId", getInstitutionId())
            .append("institutionName", getInstitutionName())
            .append("creditCode", getCreditCode())
            .append("ratingLevel", getRatingLevel())
            .append("totalScore", getTotalScore())
            .append("serviceScore", getServiceScore())
            .append("facilityScore", getFacilityScore())
            .append("managementScore", getManagementScore())
            .append("safetyScore", getSafetyScore())
            .append("ratingDate", getRatingDate())
            .append("expireDate", getExpireDate())
            .append("validityPeriod", getValidityPeriod())
            .append("ratingStatus", getRatingStatus())
            .append("ratingRemark", getRatingRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}