package com.ruoyi.domain.bank;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 银行商户号与养老机构监管账户绑定。
 */
public class BankMerchantConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long configId;
    private Long institutionId;
    private String institutionName;
    private String bankCode;
    private String bankName;
    private String merId;
    private String merchantName;
    private String settlementAccountNo;
    private String settlementAccountName;
    private String basicAccountNo;
    private String channelType;
    private String environment;
    private String verifyStatus;
    private String isDefault;
    private String status;
    private Long currentUserId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastVerifiedTime;

    public Long getConfigId() { return configId; }
    public void setConfigId(Long configId) { this.configId = configId; }
    public Long getInstitutionId() { return institutionId; }
    public void setInstitutionId(Long institutionId) { this.institutionId = institutionId; }
    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }
    public String getBankCode() { return bankCode; }
    public void setBankCode(String bankCode) { this.bankCode = bankCode; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getMerId() { return merId; }
    public void setMerId(String merId) { this.merId = merId; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public String getSettlementAccountNo() { return settlementAccountNo; }
    public void setSettlementAccountNo(String settlementAccountNo) { this.settlementAccountNo = settlementAccountNo; }
    public String getSettlementAccountName() { return settlementAccountName; }
    public void setSettlementAccountName(String settlementAccountName) { this.settlementAccountName = settlementAccountName; }
    public String getBasicAccountNo() { return basicAccountNo; }
    public void setBasicAccountNo(String basicAccountNo) { this.basicAccountNo = basicAccountNo; }
    public String getChannelType() { return channelType; }
    public void setChannelType(String channelType) { this.channelType = channelType; }
    public String getEnvironment() { return environment; }
    public void setEnvironment(String environment) { this.environment = environment; }
    public String getVerifyStatus() { return verifyStatus; }
    public void setVerifyStatus(String verifyStatus) { this.verifyStatus = verifyStatus; }
    public String getIsDefault() { return isDefault; }
    public void setIsDefault(String isDefault) { this.isDefault = isDefault; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(Long currentUserId) { this.currentUserId = currentUserId; }
    public Date getLastVerifiedTime() { return lastVerifiedTime; }
    public void setLastVerifiedTime(Date lastVerifiedTime) { this.lastVerifiedTime = lastVerifiedTime; }
}
