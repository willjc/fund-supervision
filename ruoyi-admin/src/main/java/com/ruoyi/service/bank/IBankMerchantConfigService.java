package com.ruoyi.service.bank;

import java.util.List;

import com.ruoyi.bank.gateway.BankResult;
import com.ruoyi.domain.bank.BankMerchantConfig;

public interface IBankMerchantConfigService
{
    BankMerchantConfig selectById(Long configId);

    BankMerchantConfig selectEnabledByInstitutionId(Long institutionId);

    List<BankMerchantConfig> selectList(BankMerchantConfig config);

    int insert(BankMerchantConfig config, String operator);

    int update(BankMerchantConfig config, String operator);

    BankResult verify(Long configId, String operator);

    int delete(Long configId);
}
