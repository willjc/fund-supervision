package com.ruoyi.web.controller.bank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.ruoyi.domain.bank.BankReconDiff;
import com.ruoyi.domain.bank.BankReconRun;
import com.ruoyi.mapper.bank.BankReconMapper;
import com.ruoyi.service.bank.IBankMerchantConfigService;
import com.ruoyi.service.bank.impl.BankAcqReconService;

/** 对账页面的机构数据范围：机构用户只能看/执行/处理自己商户的批次。 */
@ExtendWith(MockitoExtension.class)
class BankReconControllerAccessTest
{
    @Mock private BankReconMapper reconMapper;
    @Mock private BankAcqReconService reconService;
    @Mock private IBankMerchantConfigService merchantConfigService;
    @InjectMocks private BankReconController controller;

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

    private BankMerchantConfig merchant(String merId)
    {
        BankMerchantConfig config = new BankMerchantConfig();
        config.setMerId(merId);
        config.setStatus("1");
        config.setVerifyStatus("1");
        return config;
    }

    private Map<String, String> runParams(String merId)
    {
        Map<String, String> params = new HashMap<>();
        params.put("merId", merId);
        params.put("clearingDate", "2026-09-10");
        return params;
    }

    private BankReconRun run(String merId)
    {
        BankReconRun run = new BankReconRun();
        run.setRunId(9L);
        run.setMerId(merId);
        return run;
    }

    @Test
    void runShouldRejectOtherInstitutionMerchant()
    {
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(merchant("8202106040000001")));

        AjaxResult result = controller.run(runParams("8202012140000003"));

        assertEquals(500, result.get("code"));
        verify(reconService, never()).runRecon(anyString(), anyString(), any(), anyString());
    }

    @Test
    void runShouldAllowOwnMerchant()
    {
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(merchant("8202106040000001")));
        BankReconRun completed = run("8202106040000001");
        completed.setStatus("success");
        when(reconService.runRecon("8202106040000001", "2026-09-10", null, "jguser")).thenReturn(completed);

        AjaxResult result = controller.run(runParams("8202106040000001"));

        assertTrue(result.isSuccess());
        verify(reconService).runRecon("8202106040000001", "2026-09-10", null, "jguser");
    }

    @Test
    void runsShouldFilterByUserMerchants()
    {
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(merchant("8202106040000001")));
        when(reconMapper.selectRuns(Arrays.asList("8202106040000001"), 20))
                .thenReturn(Collections.emptyList());

        AjaxResult result = controller.runs(20);

        assertTrue(result.isSuccess());
        verify(reconMapper).selectRuns(Arrays.asList("8202106040000001"), 20);
    }

    @Test
    void diffsShouldRejectOtherInstitutionRun()
    {
        when(reconMapper.selectRun(9L)).thenReturn(run("8202012140000003"));
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(merchant("8202106040000001")));

        AjaxResult result = controller.diffs(9L);

        assertEquals(500, result.get("code"));
        verify(reconMapper, never()).selectDiffs(anyLong());
    }

    @Test
    void handleDiffShouldRejectOtherInstitutionDiff()
    {
        BankReconDiff diff = new BankReconDiff();
        diff.setDiffId(7L);
        diff.setRunId(9L);
        when(reconMapper.selectDiff(7L)).thenReturn(diff);
        when(reconMapper.selectRun(9L)).thenReturn(run("8202012140000003"));
        when(merchantConfigService.selectList(any(BankMerchantConfig.class)))
                .thenReturn(Collections.singletonList(merchant("8202106040000001")));

        Map<String, String> params = new HashMap<>();
        params.put("diffId", "7");
        params.put("remark", "核实");
        AjaxResult result = controller.handleDiff(params);

        assertEquals(500, result.get("code"));
        verify(reconMapper, never()).markDiffHandled(anyLong(), anyString());
    }
}
