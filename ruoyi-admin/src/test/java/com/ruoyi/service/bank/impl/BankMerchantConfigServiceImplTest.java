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
import com.ruoyi.bank.gateway.BankSupervisionSignRequest;
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

    @Mock private com.ruoyi.mapper.bank.BankSettlementMapper settlementMapper;

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
        config.setSettlementAccountName(" 银行登记的精确户名 ");
        when(merchantConfigMapper.selectByMerId("ZZBANK", "MER001")).thenReturn(null);
        when(institutionMapper.selectPensionInstitutionByInstitutionId(36L)).thenReturn(institution);
        when(merchantConfigMapper.insert(config)).thenReturn(1);

        int result = service.insert(config, "admin");

        assertEquals(1, result);
        assertEquals("MER001", config.getMerId());
        assertEquals("郑州银行", config.getBankName());
        assertEquals("测试养老院", config.getMerchantName());
        assertEquals("619900001111", config.getSettlementAccountNo());
        assertEquals("银行登记的精确户名", config.getSettlementAccountName());
        assertEquals("618800002222", config.getBasicAccountNo());
        assertEquals("0", config.getVerifyStatus());
        assertEquals("0", config.getStatus());
        verify(merchantConfigMapper).insert(config);
    }

    @Test
    void paymentOnlyConfigurationMustNotWriteNullAccountName()
    {
        when(institutionMapper.selectPensionInstitutionByInstitutionId(36L)).thenReturn(institution);
        when(merchantConfigMapper.insert(config)).thenReturn(1);
        assertEquals(1,service.insert(config,"admin"));
        assertEquals("",config.getSettlementAccountName(),"兼容户名 NOT NULL，未核实时不伪造名称");
        assertEquals(0,config.getPayoutEnabled());
    }

    @Test
    void updateCannotMoveConfigurationToAnotherInstitution()
    {
        config.setConfigId(1L);
        BankMerchantConfig existing=new BankMerchantConfig();
        existing.setInstitutionId(37L);
        when(merchantConfigMapper.selectById(1L)).thenReturn(existing);
        assertThrows(ServiceException.class,()->service.update(config,"admin"));
        org.mockito.Mockito.verify(merchantConfigMapper,org.mockito.Mockito.never()).update(org.mockito.ArgumentMatchers.any());
        org.mockito.Mockito.verifyNoInteractions(institutionMapper,settlementMapper);
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

    @Test
    void signContractShouldPersistAgreementOnSuccess()
    {
        config.setConfigId(1L);
        config.setSettlementAccountNo("619900001111");
        config.setSettlementAccountName("监管账户户名");
        config.setBasicAccountNo("618800002222");
        config.setBasicAccountName("基本账户户名");
        config.setCrossBank(1);
        config.setBasicBankCode("313491000011");
        institution.setCreditCode("91410100MA9XXXXX00");
        when(merchantConfigMapper.selectById(1L)).thenReturn(config);
        when(institutionMapper.selectPensionInstitutionByInstitutionId(36L)).thenReturn(institution);
        BankResult signed = BankResult.success(null);
        signed.setAgreementNo("JGXY0001");
        when(bankGateway.signSupervisionContract(org.mockito.ArgumentMatchers.any(BankSupervisionSignRequest.class)))
                .thenReturn(signed);
        when(merchantConfigMapper.updateAgreement(1L, "JGXY0001", "admin")).thenReturn(1);

        BankResult result = service.signContract(1L, "admin");

        assertEquals("SUCCESS", result.getStatus());
        assertEquals("JGXY0001", result.getAgreementNo());
        org.mockito.ArgumentCaptor<BankSupervisionSignRequest> captor =
                org.mockito.ArgumentCaptor.forClass(BankSupervisionSignRequest.class);
        verify(bankGateway).signSupervisionContract(captor.capture());
        assertEquals("619900001111", captor.getValue().getSuperviseAccountNo());
        assertEquals("监管账户户名", captor.getValue().getSuperviseAccountName());
        assertEquals("618800002222", captor.getValue().getPayeeAccountNo());
        assertEquals("基本账户户名", captor.getValue().getPayeeAccountName());
        assertEquals("91410100MA9XXXXX00", captor.getValue().getUnifiedCreditCode());
        verify(merchantConfigMapper).updateAgreement(1L, "JGXY0001", "admin");
    }

    @Test
    void signContractShouldNotPersistAgreementWhenBankFails()
    {
        config.setConfigId(1L);
        config.setSettlementAccountNo("619900001111");
        config.setSettlementAccountName("监管账户户名");
        config.setBasicAccountNo("618800002222");
        config.setBasicAccountName("基本账户户名");
        when(merchantConfigMapper.selectById(1L)).thenReturn(config);
        when(bankGateway.signSupervisionContract(org.mockito.ArgumentMatchers.any(BankSupervisionSignRequest.class)))
                .thenReturn(BankResult.failed("9999", "签约失败"));

        BankResult result = service.signContract(1L, "admin");

        assertEquals("FAILED", result.getStatus());
        org.mockito.Mockito.verify(merchantConfigMapper, org.mockito.Mockito.never()).updateAgreement(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void signContractShouldRejectMissingBasicAccountNameBeforeCallingBank()
    {
        config.setConfigId(1L);
        config.setSettlementAccountName("监管账户户名");
        config.setBasicAccountNo("618800002222");
        when(merchantConfigMapper.selectById(1L)).thenReturn(config);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.signContract(1L, "admin"));

        assertEquals("请先完善基本账户名称（结算账户名称）后再发起银行签约", exception.getMessage());
        org.mockito.Mockito.verifyNoInteractions(bankGateway);
    }

    @Test
    void signContractShouldRejectAlreadySignedBinding()
    {
        config.setConfigId(1L);
        config.setSupervisionAgreementNo("JGXY-OLD");
        when(merchantConfigMapper.selectById(1L)).thenReturn(config);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.signContract(1L, "admin"));

        assertEquals("该绑定已存在监管签约协议编号（JGXY-OLD），无需重复签约", exception.getMessage());
        org.mockito.Mockito.verifyNoInteractions(bankGateway);
    }

    @Test
    void signContractShouldReturnFailureWhenGatewayThrows()
    {
        config.setConfigId(1L);
        config.setSettlementAccountNo("619900001111");
        config.setSettlementAccountName("监管账户户名");
        config.setBasicAccountNo("618800002222");
        config.setBasicAccountName("基本账户户名");
        when(merchantConfigMapper.selectById(1L)).thenReturn(config);
        when(bankGateway.signSupervisionContract(org.mockito.ArgumentMatchers.any(BankSupervisionSignRequest.class)))
                .thenThrow(new ServiceException("银行对接尚未启用"));

        BankResult result = service.signContract(1L, "admin");

        assertEquals("FAILED", result.getStatus());
        assertEquals("银行对接尚未启用", result.getResponseMessage());
        org.mockito.Mockito.verify(merchantConfigMapper, org.mockito.Mockito.never()).updateAgreement(
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    @Test
    void updateShouldPreserveAgreementWhenFormOmitsIt()
    {
        config.setConfigId(1L);
        config.setMerId("MER001");
        config.setSettlementAccountName("户名");
        config.setBasicAccountName("基本户名");
        BankMerchantConfig existing = new BankMerchantConfig();
        existing.setConfigId(1L);
        existing.setInstitutionId(36L);
        existing.setMerId("MER001");
        existing.setSettlementAccountNo("619900001111");
        existing.setSettlementAccountName("户名");
        existing.setBasicAccountNo("618800002222");
        existing.setBasicAccountName("基本户名");
        existing.setChannelType("H5");
        existing.setEnvironment("sandbox");
        existing.setIsDefault("0");
        existing.setVerifyStatus("1");
        existing.setSupervisionAgreementNo("JGXY0001");
        when(merchantConfigMapper.selectById(1L)).thenReturn(existing);
        when(institutionMapper.selectPensionInstitutionByInstitutionId(36L)).thenReturn(institution);
        when(merchantConfigMapper.update(config)).thenReturn(1);

        service.update(config, "admin");

        assertEquals("JGXY0001", config.getSupervisionAgreementNo());
    }
}
