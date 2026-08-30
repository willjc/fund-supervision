package com.ruoyi.service.bank.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.bank.gateway.BankGateway;
import com.ruoyi.bank.gateway.BankResult;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.mapper.PensionInstitutionMapper;
import com.ruoyi.mapper.bank.BankMerchantConfigMapper;

@ExtendWith(MockitoExtension.class)
class BankMerchantConfigServiceImplTest
{
    @Mock
    private BankMerchantConfigMapper merchantConfigMapper;

    @Mock
    private PensionInstitutionMapper institutionMapper;

    @Mock
    private BankGateway bankGateway;

    @InjectMocks
    private BankMerchantConfigServiceImpl service;

    private BankMerchantConfig config;
    private PensionInstitution institution;

    @BeforeEach
    void setUp()
    {
        config = new BankMerchantConfig();
        config.setInstitutionId(36L);
        config.setMerId(" MER001 ");
        config.setChannelType("H5");
        config.setEnvironment("sandbox");
        config.setStatus("1");

        institution = new PensionInstitution();
        institution.setInstitutionId(36L);
        institution.setInstitutionName("测试养老院");
        institution.setSuperviseAccount("619900001111");
        institution.setBankAccount("618800002222");
    }

    @Test
    void insertShouldDeriveInstitutionAccountsAndRemainDisabled()
    {
        when(merchantConfigMapper.selectByMerId("ZZBANK", "MER001")).thenReturn(null);
        when(institutionMapper.selectPensionInstitutionByInstitutionId(36L)).thenReturn(institution);
        when(merchantConfigMapper.insert(config)).thenReturn(1);

        int result = service.insert(config, "admin");

        assertEquals(1, result);
        assertEquals("MER001", config.getMerId());
        assertEquals("郑州银行", config.getBankName());
        assertEquals("测试养老院", config.getMerchantName());
        assertEquals("619900001111", config.getSettlementAccountNo());
        assertEquals("618800002222", config.getBasicAccountNo());
        assertEquals("0", config.getVerifyStatus());
        assertEquals("0", config.getStatus());
        verify(merchantConfigMapper).insert(config);
    }

    @Test
    void insertShouldRejectDuplicateMerchantId()
    {
        BankMerchantConfig duplicate = new BankMerchantConfig();
        duplicate.setConfigId(9L);
        when(merchantConfigMapper.selectByMerId("ZZBANK", "MER001")).thenReturn(duplicate);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.insert(config, "admin"));

        assertEquals("该郑州银行商户号已绑定其他配置", exception.getMessage());
    }

    @Test
    void updateShouldRejectEnableBeforeBankVerification()
    {
        config.setConfigId(1L);
        config.setMerId("MER001");
        BankMerchantConfig existing = new BankMerchantConfig();
        existing.setConfigId(1L);
        existing.setInstitutionId(36L);
        existing.setMerId("MER001");
        existing.setSettlementAccountNo("619900001111");
        existing.setBasicAccountNo("618800002222");
        existing.setChannelType("H5");
        existing.setEnvironment("sandbox");
        existing.setVerifyStatus("0");
        existing.setStatus("0");

        when(merchantConfigMapper.selectById(1L)).thenReturn(existing);
        when(merchantConfigMapper.selectByMerId("ZZBANK", "MER001")).thenReturn(existing);
        when(institutionMapper.selectPensionInstitutionByInstitutionId(36L)).thenReturn(institution);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.update(config, "admin"));

        assertEquals("商户号尚未通过银行环境验证，不能启用", exception.getMessage());
    }

    @Test
    void verifyShouldPersistSuccessfulResult()
    {
        config.setConfigId(1L);
        config.setMerId("MER001");
        config.setSettlementAccountNo("619900001111");
        when(merchantConfigMapper.selectById(1L)).thenReturn(config);
        when(bankGateway.verifyMerchant("MER001", "619900001111"))
                .thenReturn(BankResult.success("VERIFY001"));
        when(merchantConfigMapper.updateVerification(
                org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq("1"),
                org.mockito.ArgumentMatchers.any(Date.class), org.mockito.ArgumentMatchers.eq("admin"),
                org.mockito.ArgumentMatchers.eq("成功"))).thenReturn(1);

        BankResult result = service.verify(1L, "admin");

        assertEquals("SUCCESS", result.getStatus());
    }

    @Test
    void verifyShouldReturnFailureAfterPersistingFailedStatus()
    {
        config.setConfigId(1L);
        config.setMerId("MER001");
        config.setSettlementAccountNo("619900001111");
        when(merchantConfigMapper.selectById(1L)).thenReturn(config);
        when(bankGateway.verifyMerchant("MER001", "619900001111"))
                .thenThrow(new ServiceException("银行对接尚未启用"));
        when(merchantConfigMapper.updateVerification(
                org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq("2"),
                org.mockito.ArgumentMatchers.any(Date.class), org.mockito.ArgumentMatchers.eq("admin"),
                org.mockito.ArgumentMatchers.eq("银行对接尚未启用"))).thenReturn(1);

        BankResult result = service.verify(1L, "admin");

        assertEquals("FAILED", result.getStatus());
        assertEquals("银行对接尚未启用", result.getResponseMessage());
    }
}
