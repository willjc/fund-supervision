package com.ruoyi.web.controller.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.domain.bank.BankSupervisionReconRun;
import com.ruoyi.service.bank.IBankMerchantConfigService;
import com.ruoyi.service.bank.impl.BankSupervisionReconService;

@ExtendWith(MockitoExtension.class)
class BankSupervisionReconControllerTest
{
    @Mock private BankSupervisionReconService reconService;
    @Mock private IBankMerchantConfigService merchantConfigService;
    @InjectMocks private BankSupervisionReconController controller;

    @BeforeEach
    void setUp()
    {
        SysUser user = new SysUser();
        user.setUserName("jguser");
        LoginUser loginUser = new LoginUser(2L, 2L, user, new HashSet<>());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null));
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    private BankMerchantConfig account()
    {
        BankMerchantConfig account = new BankMerchantConfig();
        account.setInstitutionId(33L);
        account.setInstitutionName("郑州市港区养老集团有限公司");
        account.setSettlementAccountNo("999156000240015553");
        account.setSettlementAccountName("郑州市养老机构监管账号");
        account.setStatus("1");
        account.setVerifyStatus("1");
        return account;
    }

    private BankSupervisionReconRun run(Long institutionId)
    {
        BankSupervisionReconRun run = new BankSupervisionReconRun();
        run.setRunId(9L);
        run.setInstitutionId(institutionId);
        return run;
    }

    private Map<String, String> runParams(String institutionId, String accountNo)
    {
        Map<String, String> params = new HashMap<>();
        params.put("institutionId", institutionId);
        params.put("accountNo", accountNo);
        params.put("trandt", "20251224");
        return params;
    }

    @Test
    void accountsShouldReturnOwnEnabledAccounts()
    {
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(account()));

        AjaxResult result = controller.accounts();

        assertTrue(result.isSuccess());
        assertEquals(1, ((java.util.List<?>) result.get(AjaxResult.DATA_TAG)).size());
    }

    @Test
    void runShouldRejectOtherInstitutionAccount()
    {
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(account()));

        AjaxResult result = controller.run(runParams("33", "OTHER"));

        assertEquals(500, result.get("code"));
        verify(reconService, never()).run(any(), anyString(), any(), anyString());
    }

    @Test
    void runShouldAllowOwnAccount()
    {
        BankMerchantConfig account = account();
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(account));
        BankSupervisionReconRun run = run(33L);
        when(reconService.run(account, "20251224", null, "jguser")).thenReturn(run);

        AjaxResult result = controller.run(runParams("33", "999156000240015553"));

        assertTrue(result.isSuccess());
        verify(reconService).run(account, "20251224", null, "jguser");
    }

    @Test
    void runsShouldFilterByOwnInstitutions()
    {
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(account()));
        when(reconService.listRuns(Arrays.asList(33L), 20)).thenReturn(Collections.emptyList());

        AjaxResult result = controller.runs(20);

        assertTrue(result.isSuccess());
        verify(reconService).listRuns(Arrays.asList(33L), 20);
    }

    @Test
    void retryShouldRejectOtherInstitutionRun()
    {
        when(reconService.getRun(9L)).thenReturn(run(41L));
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(account()));

        AjaxResult result = controller.retry(9L, null);

        assertEquals(500, result.get("code"));
        verify(reconService, never()).retry(any(), any(), anyString());
    }

    @Test
    void retryShouldAllowOwnRun()
    {
        BankSupervisionReconRun run = run(33L);
        when(reconService.getRun(9L)).thenReturn(run);
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(account()));
        when(reconService.retry(run, null, "jguser")).thenReturn(run);

        AjaxResult result = controller.retry(9L, null);

        assertTrue(result.isSuccess());
        verify(reconService).retry(run, null, "jguser");
    }
}
