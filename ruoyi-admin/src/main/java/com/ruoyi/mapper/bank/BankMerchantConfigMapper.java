package com.ruoyi.mapper.bank;

import java.util.List;
import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.domain.bank.BankMerchantConfig;

public interface BankMerchantConfigMapper
{
    BankMerchantConfig selectById(Long configId);

    BankMerchantConfig selectByMerId(@Param("bankCode") String bankCode, @Param("merId") String merId);

    BankMerchantConfig selectEnabledByInstitutionId(Long institutionId);

    List<BankMerchantConfig> selectList(BankMerchantConfig config);

    int insert(BankMerchantConfig config);

    int update(BankMerchantConfig config);

    int clearDefault(@Param("institutionId") Long institutionId,
                     @Param("excludeConfigId") Long excludeConfigId);

    int updateVerification(@Param("configId") Long configId,
                           @Param("verifyStatus") String verifyStatus,
                           @Param("lastVerifiedTime") Date lastVerifiedTime,
                           @Param("operator") String operator,
                           @Param("verifyMessage") String verifyMessage);

    int deleteById(Long configId);
}
