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

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
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
import com.ruoyi.domain.bank.BankTransaction;
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

    private byte[] bankFile(String... lines)
    {
        return (String.join("\n", lines) + "\n").getBytes(Charset.forName("GBK"));
    }

    private String bankLine(String requestNo, String amount, String serial, String status)
    {
        return "YLPT|20251224|094512|" + requestNo
                + "|999156000240015553|313491000232|6217002000000000002|105100001036|"
                + amount + "|156||||" + serial + "|" + status + "|交易成功|养老资金拨付";
    }

    private BankTransaction local(String requestNo, String amount, String serial)
    {
        BankTransaction tx = new BankTransaction();
        tx.setRequestNo(requestNo);
        tx.setAmount(new BigDecimal(amount));
        tx.setBankSerialNo(serial);
        tx.setBankTime("20251224094512");
        tx.setStatus("SUCCESS");
        return tx;
    }

    @Test
    void probeShouldDownloadAndSaveFile() throws Exception
    {
        useTempDir();
        JSONObject response = response("SEAI0000", "success", "20260911.txt");
        byte[] content = "hello".getBytes(StandardCharsets.UTF_8);
        when(gateway.applySupervisionRecon("20260911")).thenReturn(response);
        when(gateway.downloadFile("MZYL/downdir/20260911/20260911.txt")).thenReturn(content);

        Map<String, Object> result = service.probe("2026-09-11");

        assertEquals("20260911", result.get("trandt"));
        assertEquals("MZYL/downdir/20260911/20260911.txt", result.get("fileName"));
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
        byte[] content = bankFile(bankLine("BT1", "0.10", "202512240000A003672061", "1"));
        when(gateway.applySupervisionRecon("20251224")).thenReturn(response("SEAI0000", "业务受理成功", "test.txt"));
        when(gateway.downloadFile("MZYL/downdir/20251224/test.txt")).thenReturn(content);
        when(reconMapper.selectSuccessTransfers(33L))
                .thenReturn(Collections.singletonList(local("BT1", "0.10", "202512240000A003672061")));

        BankSupervisionReconRun run = service.run(account(), "2025-12-24", null, "admin");

        assertEquals("success", run.getStatus());
        assertEquals("MZYL/downdir/20251224/test.txt", run.getFileName());
        assertEquals((long) content.length, run.getFileSize().longValue());
        assertEquals(DigestUtils.md5DigestAsHex(content), run.getFileMd5());
        assertEquals(1, run.getTotalRows().intValue());
        assertEquals(1, run.getMatchedRows().intValue());
        assertEquals(0, run.getDiffRows().intValue());
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
        byte[] content = bankFile(bankLine("BT1", "0.10", "SERIAL1", "1"));
        when(gateway.downloadFile("MZYL/downdir/20251224/manual.txt")).thenReturn(content);
        when(reconMapper.selectSuccessTransfers(33L)).thenReturn(Collections.emptyList());

        BankSupervisionReconRun run = service.run(account(), "20251224", "manual.txt", "admin");

        assertEquals("success", run.getStatus());
        assertEquals("MZYL/downdir/20251224/manual.txt", run.getFileName());
        assertEquals(1, run.getTotalRows().intValue());
        assertEquals(0, run.getMatchedRows().intValue());
        assertEquals(1, run.getDiffRows().intValue());
        verify(gateway, never()).applySupervisionRecon(anyString());
        verify(gateway).downloadFile("MZYL/downdir/20251224/manual.txt");
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
        byte[] content = bankFile(bankLine("BT1", "0.10", "SERIAL1", "1"));
        when(gateway.applySupervisionRecon("20251224")).thenReturn(response("0000", "业务受理成功", "retry.txt"));
        when(gateway.downloadFile("MZYL/downdir/20251224/retry.txt")).thenReturn(content);
        when(reconMapper.selectSuccessTransfers(33L)).thenReturn(Collections.emptyList());

        service.retry(run, null, "admin");

        assertEquals("success", run.getStatus());
        assertEquals(1, run.getRetryCount().intValue());
        verify(gateway).applySupervisionRecon("20251224");
        verify(gateway).downloadFile("MZYL/downdir/20251224/retry.txt");
    }

    @Test
    void runShouldMarkEmptyWhenBankHasNoFile()
    {
        stubInsert();
        when(gateway.applySupervisionRecon("20251226"))
                .thenReturn(response("0000", "业务受理成功", "313491000232YLPT0000120251226.txt"));
        when(gateway.downloadFile("MZYL/downdir/20251226/313491000232YLPT0000120251226.txt"))
                .thenReturn(new byte[0]);

        BankSupervisionReconRun run = service.run(account(), "2025-12-26", null, "admin");

        assertEquals("empty", run.getStatus());
        assertTrue(run.getRespMsg().contains("无对账文件"));
        assertEquals(0, run.getTotalRows().intValue());
        verify(reconMapper).updateRun(run);
    }

    @Test
    void probeShouldReportNoFileWhenBankReturnsEmpty()
    {
        when(gateway.applySupervisionRecon("20260912")).thenReturn(response("0000", "success", "empty.txt"));
        when(gateway.downloadFile("MZYL/downdir/20260912/empty.txt")).thenReturn(new byte[0]);

        ServiceException error = assertThrows(ServiceException.class, () -> service.probe("20260912"));
        assertTrue(error.getMessage().contains("无对账文件"));
    }

    @Test
    void locateFileShouldReturnSavedFileInsideReconDir() throws Exception
    {
        useTempDir();
        Path file = tempDir.resolve("20251224").resolve("10-test.txt");
        Files.createDirectories(file.getParent());
        Files.write(file, "x".getBytes(StandardCharsets.UTF_8));
        BankSupervisionReconRun run = new BankSupervisionReconRun();
        run.setSavedPath(file.toString());

        assertEquals(file.toAbsolutePath().normalize(), service.locateFile(run));
    }

    @Test
    void locateFileShouldRejectMissingFile()
    {
        useTempDir();
        BankSupervisionReconRun run = new BankSupervisionReconRun();
        run.setSavedPath(tempDir.resolve("missing.txt").toString());

        ServiceException error = assertThrows(ServiceException.class, () -> service.locateFile(run));
        assertTrue(error.getMessage().contains("不存在"));
    }

    @Test
    void locateFileShouldRejectPathOutsideReconDir() throws Exception
    {
        useTempDir();
        Path outside = Files.createTempFile("outside-", ".txt");
        try
        {
            BankSupervisionReconRun run = new BankSupervisionReconRun();
            run.setSavedPath(outside.toString());

            assertThrows(ServiceException.class, () -> service.locateFile(run));
        }
        finally
        {
            Files.deleteIfExists(outside);
        }
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
