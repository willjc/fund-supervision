package com.ruoyi.service.bank.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.bank.gateway.ZhengzhouBankGateway;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.domain.bank.BankSupervisionReconRun;
import com.ruoyi.mapper.bank.BankSupervisionReconMapper;

/**
 * 监管对账：OBK0071706 取 filena，按开放规范 §4.5.2 公共文件下载。
 * 文件解析/差异比对待银行样例确认；当前记录批次、文件元数据和下载结果。
 */
@Service
public class BankSupervisionReconService
{
    @Autowired
    private ZhengzhouBankGateway gateway;

    @Autowired
    private BankSupervisionReconMapper reconMapper;

    @Value("${bank.integration.supervision-recon-dir:/tmp/fund-supervision-supervision-recon}")
    private String reconDir;

    public BankSupervisionReconRun run(BankMerchantConfig account, String trandt, String fileNameOverride,
            String operator)
    {
        if (account == null || account.getSettlementAccountNo() == null
                || account.getSettlementAccountNo().trim().isEmpty())
        {
            throw new ServiceException("机构未配置监管账号，无法对账");
        }
        String date = normalizeDate(trandt);
        BankSupervisionReconRun run = new BankSupervisionReconRun();
        run.setInstitutionId(account.getInstitutionId());
        run.setAccountNo(account.getSettlementAccountNo());
        run.setAccountName(account.getSettlementAccountName());
        run.setTrandt(parseDate(date));
        run.setStatus("running");
        run.setFileName(blankToNull(fileNameOverride));
        run.setRetryCount(0);
        run.setCreateBy(operator);
        if (reconMapper.insertRun(run) != 1)
        {
            throw new ServiceException("创建监管对账批次失败");
        }
        execute(run, run.getFileName());
        return run;
    }

    public BankSupervisionReconRun retry(BankSupervisionReconRun run, String fileNameOverride, String operator)
    {
        if (run == null)
        {
            throw new ServiceException("对账批次不存在");
        }
        run.setStatus("running");
        run.setRespCode(null);
        run.setRespMsg(null);
        run.setRetryCount(run.getRetryCount() == null ? 1 : run.getRetryCount() + 1);
        if (!isBlank(fileNameOverride))
        {
            run.setFileName(fileNameOverride.trim());
        }
        reconMapper.updateRun(run);
        execute(run, fileNameOverride);
        return run;
    }

    public BankSupervisionReconRun getRun(Long runId)
    {
        return reconMapper.selectRun(runId);
    }

    public List<BankSupervisionReconRun> listRuns(List<Long> institutionIds, int limit)
    {
        return reconMapper.selectRuns(institutionIds, limit);
    }

    /** 通道探针保留：不落批次，只验证 706 与公共下载。 */
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
        if (isBlank(fileName))
        {
            throw new ServiceException("银行未返回对账文件名（filena）");
        }
        String requestedName = downloadPath(parseDate(date), fileName.trim());
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

    private void execute(BankSupervisionReconRun run, String fileNameOverride)
    {
        try
        {
            String fileName = blankToNull(fileNameOverride);
            if (fileName == null)
            {
                JSONObject response = gateway.applySupervisionRecon(formatDate(run.getTrandt()));
                String code = response.getString("respcd");
                run.setRespCode(code);
                run.setRespMsg(truncate(response.getString("resptx"), 500));
                if (code == null || !code.trim().endsWith("0000"))
                {
                    finish(run, "failed");
                    return;
                }
                fileName = blankToNull(response.getString("filena"));
                if (fileName == null)
                {
                    finish(run, "processing");
                    return;
                }
            }
            String requestedName = downloadPath(run.getTrandt(), fileName);
            run.setFileName(requestedName);
            byte[] content = gateway.downloadFile(requestedName);
            saveFile(run, requestedName, content);
            finish(run, "success");
        }
        catch (ServiceException e)
        {
            run.setRespMsg(truncate(e.getMessage(), 500));
            finish(run, "failed");
        }
    }

    private void saveFile(BankSupervisionReconRun run, String fileName, byte[] content)
    {
        Path targetDir = Paths.get(reconDir, formatDate(run.getTrandt())).normalize();
        try
        {
            Files.createDirectories(targetDir);
            String safeName = Paths.get(fileName).getFileName().toString();
            Path target = targetDir.resolve(run.getRunId() + "-" + safeName).normalize();
            if (!target.startsWith(targetDir))
            {
                throw new ServiceException("银行返回的对账文件名不合法");
            }
            Files.write(target, content);
            run.setFileName(fileName);
            run.setFileSize((long) content.length);
            run.setFileMd5(DigestUtils.md5DigestAsHex(content));
            run.setSavedPath(target.toString());
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("对账文件保存失败：" + e.getMessage());
        }
    }

    private void finish(BankSupervisionReconRun run, String status)
    {
        run.setStatus(status);
        run.setUpdateTime(new Date());
        reconMapper.updateRun(run);
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

    private Date parseDate(String normalized)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            format.setLenient(false);
            return format.parse(normalized);
        }
        catch (Exception e)
        {
            throw new ServiceException("对账日期格式必须为 yyyyMMdd 或 yyyy-MM-dd");
        }
    }

    private String formatDate(Date date)
    {
        return new SimpleDateFormat("yyyyMMdd").format(date);
    }

    /** 706 只返回文件名，公共下载要求带目录：MZYL/downdir/{yyyyMMdd}/{filena}。 */
    private String downloadPath(Date trandt, String fileName)
    {
        String name = fileName.trim();
        if (name.contains("/"))
        {
            return name;
        }
        return "MZYL/downdir/" + formatDate(trandt) + "/" + name;
    }

    private String blankToNull(String value)
    {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }

    private String truncate(String value, int max)
    {
        if (value == null)
        {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
