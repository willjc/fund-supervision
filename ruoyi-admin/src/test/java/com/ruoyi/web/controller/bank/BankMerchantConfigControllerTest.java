package com.ruoyi.web.controller.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ruoyi.bank.gateway.BankResult;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.service.bank.IBankMerchantConfigService;

@ExtendWith(MockitoExtension.class)
class BankMerchantConfigControllerTest
{
    @Mock
    private IBankMerchantConfigService merchantConfigService;

    @Mock
    private IPensionInstitutionService institutionService;

    @InjectMocks
    private BankMerchantConfigController controller;

    @BeforeEach
    void setUp()
    {
        SysUser user = new SysUser();
        user.setUserName("admin");
        LoginUser loginUser = new LoginUser(1L, 1L, user, new HashSet<>());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null));
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addShouldSaveWithoutSigning()
    {
        BankMerchantConfig config = new BankMerchantConfig();
        config.setInstitutionId(36L);
        when(institutionService.selectPensionInstitutionList(any(PensionInstitution.class)))
                .thenReturn(Collections.singletonList(new PensionInstitution()));
        when(merchantConfigService.insert(config, "admin")).thenReturn(1);

        AjaxResult result = controller.add(config);

        assertTrue(result.isSuccess());
        verify(merchantConfigService).insert(config, "admin");
        verify(merchantConfigService, never()).signContract(any(), anyString());
        verify(merchantConfigService, never()).selectById(any());
    }

    @Test
    void editShouldSaveWithoutSigning()
    {
        BankMerchantConfig config = new BankMerchantConfig();
        config.setConfigId(1L);
        config.setInstitutionId(36L);
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(config));
        when(institutionService.selectPensionInstitutionList(any(PensionInstitution.class)))
                .thenReturn(Collections.singletonList(new PensionInstitution()));
        when(merchantConfigService.update(config, "admin")).thenReturn(1);

        AjaxResult result = controller.edit(config);

        assertTrue(result.isSuccess());
        assertEquals("绑定已更新", result.get(AjaxResult.MSG_TAG));
        verify(merchantConfigService).update(config, "admin");
        verify(merchantConfigService, never()).signContract(any(), anyString());
        verify(merchantConfigService, never()).selectById(any());
    }

    @Test
    void maintainContractShouldReturnSuccessWithUnchangedAgreement()
    {
        BankMerchantConfig config = new BankMerchantConfig();
        config.setConfigId(1L);
        config.setInstitutionId(36L);
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(config));
        BankResult maintained = BankResult.success(null);
        maintained.setAgreementNo("JGXY0001");
        when(merchantConfigService.maintainContract(1L, "admin")).thenReturn(maintained);

        AjaxResult result = controller.maintainContract(1L);

        assertTrue(result.isSuccess());
        assertEquals("监管签约维护成功，协议编号 JGXY0001", result.get(AjaxResult.MSG_TAG));
        verify(merchantConfigService).maintainContract(1L, "admin");
    }
}