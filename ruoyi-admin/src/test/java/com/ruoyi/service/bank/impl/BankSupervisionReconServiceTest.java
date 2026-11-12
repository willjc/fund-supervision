package com.ruoyi.service.bank.impl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

@ExtendWith(MockitoExtension.class)
class BankSupervisionReconServiceTest
{
    @Mock private ZhengzhouBankGateway gateway;
    @InjectMocks private BankSupervisionReconService service;
    @TempDir Path tempDir;

    private void useTempDir()
    {
        ReflectionTestUtils.setField(service, "reconDir", tempDir.toString());
    }

    @Test
    void probeShouldDownloadAndSaveFile() throws Exception
    {
        useTempDir();
        JSONObject response = new JSONObject();
        response.put("respcd", "SEAI0000");
        response.put("resptx", "success");
        response.put("filena", "sub/20260911.txt");
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
        JSONObject response = new JSONObject();
        response.put("respcd", "9999");
        response.put("resptx", "失败");
        when(gateway.applySupervisionRecon("20260911")).thenReturn(response);

        ServiceException error = assertThrows(ServiceException.class, () -> service.probe("20260911"));
        assertTrue(error.getMessage().contains("9999"));
        verify(gateway, never()).downloadFile(anyString());
    }

    @Test
    void probeShouldFailWhenFileNameMissing()
    {
        JSONObject response = new JSONObject();
        response.put("respcd", "0000");
        response.put("resptx", "success");
        when(gateway.applySupervisionRecon("20260911")).thenReturn(response);

        ServiceException error = assertThrows(ServiceException.class, () -> service.probe("20260911"));
        assertTrue(error.getMessage().contains("filena"));
        verify(gateway, never()).downloadFile(anyString());
    }

    @Test
    void probeShouldKeepPathTraversalFileNameInsideTargetDir() throws Exception
    {
        useTempDir();
        JSONObject response = new JSONObject();
        response.put("respcd", "0000");
        response.put("resptx", "success");
        response.put("filena", "../../escape.txt");
        byte[] content = "x".getBytes(StandardCharsets.UTF_8);
        when(gateway.applySupervisionRecon("20260911")).thenReturn(response);
        when(gateway.downloadFile("../../escape.txt")).thenReturn(content);

        Map<String, Object> result = service.probe("20260911");

        Path saved = Paths.get(result.get("savedPath").toString()).normalize();
        assertTrue(saved.startsWith(tempDir.normalize()));
        assertEquals("escape.txt", saved.getFileName().toString());
    }
}
