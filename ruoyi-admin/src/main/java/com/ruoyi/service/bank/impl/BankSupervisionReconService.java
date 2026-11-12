package com.ruoyi.service.bank.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.bank.gateway.ZhengzhouBankGateway;
import com.ruoyi.common.exception.ServiceException;

/**
 * 监管对账通道探针：OBK0071706 取 filena，再按开放规范 §4.5.2 公共文件下载。
 * 暂不解析文件，只落盘并返回文件名/大小/MD5，供确认银行文件版式。
 */
@Service
public class BankSupervisionReconService
{
    @Autowired
    private ZhengzhouBankGateway gateway;

    @Value("${bank.integration.supervision-recon-dir:/tmp/fund-supervision-supervision-recon}")
    private String reconDir;

    public Map<String, Object> probe(String trandt)
    {
        String date = normalizeDate(trandt);
        JSONObject response = gateway.applySupervisionRecon(date);
        String code = response.getString("respcd");
        if (code == null || !code.trim().endsWith("0000"))
        {
            throw new ServiceException("监管对账通知失败：" + code + " " + response.getString("resptx"));
        }
        String fileName = response.getString("filena");
        if (fileName == null || fileName.trim().isEmpty())
        {
            throw new ServiceException("银行未返回对账文件名（filena）");
        }
        String requestedName = fileName.trim();
        byte[] content = gateway.downloadFile(requestedName);

        Path targetDir = Paths.get(reconDir, date).normalize();
        Path target;
        try
        {
            Files.createDirectories(targetDir);
            String safeName = Paths.get(requestedName).getFileName().toString();
            target = targetDir.resolve(safeName).normalize();
            if (!target.startsWith(targetDir))
            {
                throw new ServiceException("银行返回的对账文件名不合法");
            }
            Files.write(target, content);
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("对账文件保存失败：" + e.getMessage());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("trandt", date);
        result.put("fileName", requestedName);
        result.put("savedPath", target.toString());
        result.put("size", content.length);
        result.put("md5", DigestUtils.md5DigestAsHex(content));
        result.put("respcd", code);
        result.put("resptx", response.getString("resptx"));
        return result;
    }

    private String normalizeDate(String trandt)
    {
        if (trandt == null || trandt.trim().isEmpty())
        {
            throw new ServiceException("对账日期不能为空");
        }
        String normalized = trandt.trim().replace("-", "").replace("/", "");
        if (!normalized.matches("[0-9]{8}"))
        {
            throw new ServiceException("对账日期格式必须为 yyyyMMdd 或 yyyy-MM-dd");
        }
        return normalized;
    }
}
