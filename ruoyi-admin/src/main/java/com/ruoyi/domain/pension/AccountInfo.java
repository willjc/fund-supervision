package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 老人账户信息对象 account_info
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public class AccountInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 账户ID */
    private Long accountId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 账户编号 */
    @Excel(name = "账户编号")
    private String accountNo;

    /** 账户名称 */
    @Excel(name = "账户名称")
    private String accountName;

    /** 账户状态(0冻结 1正常 2销户) */
    @Excel(name = "账户状态", readConverterExp = "0=冻结,1=正常,2=销户")
    private String accountStatus;

    /** 总余额 */
    @Excel(name = "总余额")
    private BigDecimal totalBalance;

    /** 服务费余额 */
    @Excel(name = "服务费余额")
    private BigDecimal serviceBalance;

    /** 押金余额 */
    @Excel(name = "押金余额")
    private BigDecimal depositBalance;

    /** 会员费余额 */
    @Excel(name = "会员费余额")
    private BigDecimal memberBalance;

    // 关联字段（用于展示）
    @Excel(name = "老人姓名")
    private String elderName;

    @Excel(name = "身份证号")
    private String idCard;

    @Excel(name = "机构名称")
    private String institutionName;

    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }

    public Long getAccountId()
    {
        return accountId;
    }

    public void setElderId(Long elderId)
    {
        this.elderId = elderId;
    }

    public Long getElderId()
    {
        return elderId;
    }

    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
    }

    public void setAccountNo(String accountNo)
    {
        this.accountNo = accountNo;
    }

    public String getAccountNo()
    {
        return accountNo;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountStatus(String accountStatus)
    {
        this.accountStatus = accountStatus;
    }

    public String getAccountStatus()
    {
        return accountStatus;
    }

    public void setTotalBalance(BigDecimal totalBalance)
    {
        this.totalBalance = totalBalance;
    }

    public BigDecimal getTotalBalance()
    {
        return totalBalance;
    }

    public void setServiceBalance(BigDecimal serviceBalance)
    {
        this.serviceBalance = serviceBalance;
    }

    public BigDecimal getServiceBalance()
    {
        return serviceBalance;
    }

    public void setDepositBalance(BigDecimal depositBalance)
    {
        this.depositBalance = depositBalance;
    }

    public BigDecimal getDepositBalance()
    {
        return depositBalance;
    }

    public void setMemberBalance(BigDecimal memberBalance)
    {
        this.memberBalance = memberBalance;
    }

    public BigDecimal getMemberBalance()
    {
        return memberBalance;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("accountId", getAccountId())
            .append("elderId", getElderId())
            .append("institutionId", getInstitutionId())
            .append("accountNo", getAccountNo())
            .append("accountName", getAccountName())
            .append("accountStatus", getAccountStatus())
            .append("totalBalance", getTotalBalance())
            .append("serviceBalance", getServiceBalance())
            .append("depositBalance", getDepositBalance())
            .append("memberBalance", getMemberBalance())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}