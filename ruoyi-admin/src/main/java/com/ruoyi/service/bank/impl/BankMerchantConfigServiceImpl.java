package com.ruoyi.service.bank.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.mapper.PensionInstitutionMapper;
import com.ruoyi.mapper.bank.BankMerchantConfigMapper;
import com.ruoyi.service.bank.IBankMerchantConfigService;
import com.ruoyi.bank.gateway.BankGateway;
import com.ruoyi.bank.gateway.BankResult;

@Service
public class BankMerchantConfigServiceImpl implements IBankMerchantConfigService
{
    private static final String BANK_CODE = "ZZBANK";
    private static final String BANK_NAME = "郑州银行";

    @Autowired
    private BankMerchantConfigMapper merchantConfigMapper;

    @Autowired
    private PensionInstitutionMapper institutionMapper;

    @Autowired
    private BankGateway bankGateway;

    @Override
    public BankMerchantConfig selectById(Long configId)
    {
        return merchantConfigMapper.selectById(configId);
    }

    @Override
    public BankMerchantConfig selectEnabledByInstitutionId(Long institutionId)
    {
        return merchantConfigMapper.selectEnabledByInstitutionId(institutionId);
    }

    @Override
    public List<BankMerchantConfig> selectList(BankMerchantConfig config)
    {
        return merchantConfigMapper.selectList(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(BankMerchantConfig config, String operator)
    {
        normalizeAndValidate(config, null);
        config.setBankCode(BANK_CODE);
        config.setBankName(BANK_NAME);
        config.setVerifyStatus("0");
        config.setStatus("0");
        config.setCreateBy(operator);
        config.setCreateTime(new Date());
        if ("1".equals(config.getIsDefault()))
        {
            merchantConfigMapper.clearDefault(config.getInstitutionId(), null);
        }
        return merchantConfigMapper.insert(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(BankMerchantConfig config, String operator)
    {
        if (config.getConfigId() == null)
        {
            throw new ServiceException("配置ID不能为空");
        }
        BankMerchantConfig existing = merchantConfigMapper.selectById(config.getConfigId());
        if (existing == null)
        {
            throw new ServiceException("商户配置不存在");
        }

        normalizeAndValidate(config, config.getConfigId());
        boolean bindingChanged = !Objects.equals(existing.getInstitutionId(), config.getInstitutionId())
                || !Objects.equals(existing.getMerId(), config.getMerId())
                || !Objects.equals(existing.getSettlementAccountNo(), config.getSettlementAccountNo())
                || !Objects.equals(existing.getBasicAccountNo(), config.getBasicAccountNo())
                || !Objects.equals(existing.getChannelType(), config.getChannelType())
                || !Objects.equals(existing.getEnvironment(), config.getEnvironment());
        if (bindingChanged)
        {
            config.setVerifyStatus("0");
            config.setVerifyMessage(null);
            config.setStatus("0");
        }
        else
        {
            config.setVerifyStatus(existing.getVerifyStatus());
            config.setVerifyMessage(existing.getVerifyMessage());
            if ("1".equals(config.getStatus()) && !"1".equals(existing.getVerifyStatus()))
            {
                throw new ServiceException("商户号尚未通过银行环境验证，不能启用");
            }
        }
        config.setUpdateBy(operator);
        config.setUpdateTime(new Date());
        if ("1".equals(config.getIsDefault()))
        {
            merchantConfigMapper.clearDefault(config.getInstitutionId(), config.getConfigId());
        }
        return merchantConfigMapper.update(config);
    }

    @Override
    public int delete(Long configId)
    {
        BankMerchantConfig existing = merchantConfigMapper.selectById(configId);
        if (existing == null)
        {
            return 0;
        }
        if ("1".equals(existing.getStatus()))
        {
            throw new ServiceException("已启用的商户配置不能删除，请先停用");
        }
        return merchantConfigMapper.deleteById(configId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BankResult verify(Long configId, String operator)
    {
        BankMerchantConfig config = merchantConfigMapper.selectById(configId);
        if (config == null)
        {
            throw new ServiceException("商户配置不存在");
        }

        BankResult result;
        try
        {
            result = bankGateway.verifyMerchant(config.getMerId(), config.getSettlementAccountNo());
        }
        catch (Exception e)
        {
            result = BankResult.failed("VERIFY_EXCEPTION", e.getMessage());
        }
        String verifyStatus = result != null && "SUCCESS".equals(result.getStatus()) ? "1" : "2";
        String message = normalizeVerificationMessage(result == null ? "银行验证返回为空"
                : result.getResponseMessage());
        int updated = merchantConfigMapper.updateVerification(configId, verifyStatus,
                new Date(), operator, message);
        if (updated != 1)
        {
            throw new ServiceException("更新商户验证状态失败");
        }
        return result == null ? BankResult.failed("EMPTY_RESPONSE", message) : result;
    }

    private String normalizeVerificationMessage(String message)
    {
        String normalized = StringUtils.isEmpty(message) ? "银行未返回验证说明" : message;
        return normalized.length() > 500 ? normalized.substring(0, 500) : normalized;
    }

    private void normalizeAndValidate(BankMerchantConfig config, Long currentConfigId)
    {
        if (config.getInstitutionId() == null)
        {
            throw new ServiceException("请选择养老机构");
        }
        if (StringUtils.isEmpty(config.getMerId()))
        {
            throw new ServiceException("银行商户号不能为空");
        }
        config.setMerId(config.getMerId().trim());
        if (config.getMerId().length() > 64)
        {
            throw new ServiceException("银行商户号长度不能超过64位");
        }

        BankMerchantConfig duplicate = merchantConfigMapper.selectByMerId(BANK_CODE, config.getMerId());
        if (duplicate != null && !duplicate.getConfigId().equals(currentConfigId))
        {
            throw new ServiceException("该郑州银行商户号已绑定其他配置");
        }

        PensionInstitution institution = institutionMapper.selectPensionInstitutionByInstitutionId(
                config.getInstitutionId());
        if (institution == null)
        {
            throw new ServiceException("养老机构不存在");
        }
        if (StringUtils.isEmpty(institution.getSuperviseAccount()))
        {
            throw new ServiceException("该机构尚未配置监管账户");
        }
        if (StringUtils.isEmpty(institution.getBankAccount()))
        {
            throw new ServiceException("该机构尚未配置基本账户");
        }

        config.setBankCode(BANK_CODE);
        config.setBankName(BANK_NAME);
        config.setMerchantName(StringUtils.isEmpty(config.getMerchantName())
                ? institution.getInstitutionName() : config.getMerchantName().trim());
        config.setSettlementAccountNo(institution.getSuperviseAccount());
        config.setSettlementAccountName(institution.getInstitutionName() + "监管账户");
        config.setBasicAccountNo(institution.getBankAccount());
        config.setChannelType(StringUtils.isEmpty(config.getChannelType()) ? "H5" : config.getChannelType());
        config.setEnvironment("prod".equals(config.getEnvironment()) ? "prod" : "sandbox");
        config.setStatus("1".equals(config.getStatus()) ? "1" : "0");
        config.setIsDefault("1".equals(config.getIsDefault()) ? "1" : "0");
    }
}
