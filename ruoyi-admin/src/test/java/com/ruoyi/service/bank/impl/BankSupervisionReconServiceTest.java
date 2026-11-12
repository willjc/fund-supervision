package com.ruoyi.service.bank.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.bank.gateway.ZhengzhouBankGateway;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.domain.bank.BankSupervisionReconRun;
import com.ruoyi.mapper.bank.BankSupervisionReconMapper;

@ExtendWith(MockitoExtension.class)
class BankSupervisionReconServiceTest
{
    @Mock private ZhengzhouBankGateway gateway;
    @Mock private BankSupervisionReconMapper reconMapper;
    @InjectMocks private BankSupervisionReconService service;
    @TempDir Path tempDir;

    private void useTempDir()
    {
        ReflectionTestUtils.setField(service, "reconDir", tempDir.toString());
    }

    private BankMerchantConfig account()
    {
        BankMerchantConfig account = new BankMerchantConfig();
        account.setInstitutionId(33L);
        account.setSettlementAccountNo("999156000240015553");
        account.setSettlementAccountName("郑州市养老机构监管账号");
        return account;
    }

    private void stubInsert()
    {
        when(reconMapper.insertRun(any(BankSupervisionReconRun.class))).thenAnswer(invocation -> {
            invocation.getArgument(0, BankSupervisionReconRun.class).setRunId(1L);
            return 1;
        });
    }

    private JSONObject response(String code, String msg, String fileName)
    {
        JSONObject response = new JSONObject();
        response.put("respcd", code);
        response.put("resptx", msg);
        if (fileName != null)
        {
            response.put("filena", fileName);
        }
        return response;
    }

    @Test
    void probeShouldDownloadAndSaveFile() throws Exception
    {
        useTempDir();
        JSONObject response = response("SEAI0000", "success", "sub/20260911.txt");
        byte[] content = "hello".getBytes(StandardCharsets.UTF_8);
        when(gateway.applySupervisionRecon("20260911")).thenReturn(response);
        when(gateway.downloadFile("sub/20260911.txt")).thenReturn(content);

        Map<String, Object> result = service.probe("2026-09-11");

        assertEquals("20260911", result.get("trandt"));
        assertEquals("sub/20260911.txt", result.get("fileName"));
        Path saved = Paths.get(result.get("savedPath").toString());
        assertTrue(Files.exists(saved));
        assertArrayEquals(content, Files.readAllBytes(saved));
        assertEquals(DigestUtils.md5DigestAsHex(content), result.get("md5"));
        assertEquals(content.length, result.get("size"));
    }

    @Test
    void probeShouldFailWhenBankRejects()
    {
        when(gateway.applySupervisionRecon("20260911")).thenReturn(response("9999", "失败", null));

        ServiceException error = assertThrows(ServiceException.class, () -> service.probe("20260911"));
        assertTrue(error.getMessage().contains("9999"));
        verify(gateway, never()).downloadFile(anyString());
    }

    @Test
    void probeShouldFailWhenFileNameMissing()
    {
        when(gateway.applySupervisionRecon("20260911")).thenReturn(response("0000", "success", null));

        ServiceException error = assertThrows(ServiceException.class, () -> service.probe("20260911"));
        assertTrue(error.getMessage().contains("filena"));
        verify(gateway, never()).downloadFile(anyString());
    }

    @Test
    void probeShouldKeepPathTraversalFileNameInsideTargetDir() throws Exception
    {
        useTempDir();
        JSONObject response = response("0000", "success", "../../escape.txt");
        byte[] content = "x".getBytes(StandardCharsets.UTF_8);
        when(gateway.applySupervisionRecon("20260911")).thenReturn(response);
        when(gateway.downloadFile("../../escape.txt")).thenReturn(content);

        Map<String, Object> result = service.probe("20260911");

        Path saved = Paths.get(result.get("savedPath").toString()).normalize();
        assertTrue(saved.startsWith(tempDir.normalize()));
        assertEquals("escape.txt", saved.getFileName().toString());
    }

    @Test
    void runShouldDownloadAndPersistFile() throws Exception
    {
        useTempDir();
        stubInsert();
        byte[] content = "hello".getBytes(StandardCharsets.UTF_8);
        when(gateway.applySupervisionRecon("20251224")).thenReturn(response("SEAI0000", "业务受理成功", "test.txt"));
        when(gateway.downloadFile("test.txt")).thenReturn(content);

        BankSupervisionReconRun run = service.run(account(), "2025-12-24", null, "admin");

        assertEquals("success", run.getStatus());
        assertEquals("test.txt", run.getFileName());
        assertEquals(5L, run.getFileSize().longValue());
        assertEquals(DigestUtils.md5DigestAsHex(content), run.getFileMd5());
        assertTrue(Files.exists(Paths.get(run.getSavedPath())));
        verify(reconMapper).updateRun(run);
    }

    @Test
    void runShouldStayProcessingWhenBankReturnsNoFileName()
    {
        stubInsert();
        when(gateway.applySupervisionRecon("20251224")).thenReturn(response("0000", "业务受理成功", null));

        BankSupervisionReconRun run = service.run(account(), "20251224", null, "admin");

        assertEquals("processing", run.getStatus());
        assertEquals("业务受理成功", run.getRespMsg());
        verify(gateway, never()).downloadFile(anyString());
    }

    @Test
    void runShouldFailWhenBankRejects()
    {
        stubInsert();
        when(gateway.applySupervisionRecon("20251224")).thenReturn(response("SEAI9999", "失败", null));

        BankSupervisionReconRun run = service.run(account(), "20251224", null, "admin");

        assertEquals("failed", run.getStatus());
        assertEquals("失败", run.getRespMsg());
        verify(gateway, never()).downloadFile(anyString());
    }

    @Test
    void runShouldDownloadManualFileNameWithoutCalling706() throws Exception
    {
        useTempDir();
        stubInsert();
        byte[] content = "manual".getBytes(StandardCharsets.UTF_8);
        when(gateway.downloadFile("manual.txt")).thenReturn(content);

        BankSupervisionReconRun run = service.run(account(), "20251224", "manual.txt", "admin");

        assertEquals("success", run.getStatus());
        assertEquals("manual.txt", run.getFileName());
        verify(gateway, never()).applySupervisionRecon(anyString());
        verify(gateway).downloadFile("manual.txt");
    }

    @Test
    void retryShouldCall706AgainAndDownload() throws Exception
    {
        useTempDir();
        BankSupervisionReconRun run = new BankSupervisionReconRun();
        run.setRunId(1L);
        run.setInstitutionId(33L);
        run.setTrandt(java.sql.Date.valueOf("2025-12-24"));
        run.setStatus("processing");
        run.setRetryCount(0);
        byte[] content = "retry".getBytes(StandardCharsets.UTF_8);
        when(gateway.applySupervisionRecon("20251224")).thenReturn(response("0000", "业务受理成功", "retry.txt"));
        when(gateway.downloadFile("retry.txt")).thenReturn(content);

        service.retry(run, null, "admin");

        assertEquals("success", run.getStatus());
        assertEquals(1, run.getRetryCount().intValue());
        verify(gateway).applySupervisionRecon("20251224");
        verify(gateway).downloadFile("retry.txt");
    }

    @Test
    void runShouldRejectMissingAccount()
    {
        ServiceException error = assertThrows(ServiceException.class,
                () -> service.run(null, "20251224", null, "admin"));
        assertTrue(error.getMessage().contains("监管账号"));
        verify(reconMapper, never()).insertRun(any(BankSupervisionReconRun.class));
    }
}
