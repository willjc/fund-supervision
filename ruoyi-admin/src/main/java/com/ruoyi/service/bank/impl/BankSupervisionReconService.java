package com.ruoyi.service.bank.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.bank.gateway.ZhengzhouBankGateway;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.domain.bank.BankSupervisionReconRun;
import com.ruoyi.domain.bank.BankTransaction;
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

    public Path locateFile(BankSupervisionReconRun run)
    {
        if (run == null || isBlank(run.getSavedPath()))
        {
            throw new ServiceException("该批次没有已保存的对账文件");
        }
        Path base = Paths.get(reconDir).toAbsolutePath().normalize();
        Path file = Paths.get(run.getSavedPath()).toAbsolutePath().normalize();
        if (!file.startsWith(base) || !Files.isRegularFile(file))
        {
            throw new ServiceException("对账文件不存在或已被清理");
        }
        return file;
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
        if (content.length == 0)
        {
            throw new ServiceException("银行该日期无对账文件");
        }
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
            if (content.length == 0)
            {
                run.setRespMsg("银行该日期无对账文件");
                finish(run, "empty");
                return;
            }
            saveFile(run, requestedName, content);
            compareAndStore(run, content);
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

    /** 解析 GBK 管道文件并做简单比对：只统计总行数/匹配/差异，不落差异明细。 */
    private void compareAndStore(BankSupervisionReconRun run, byte[] content)
    {
        List<String[]> rows = parseFile(content);
        Map<String, BankTransaction> localByRequestNo = new HashMap<>();
        for (BankTransaction local : reconMapper.selectSuccessTransfers(run.getInstitutionId()))
        {
            if (local.getRequestNo() != null)
            {
                localByRequestNo.put(local.getRequestNo(), local);
            }
        }
        Set<String> seen = new HashSet<>();
        int matched = 0;
        int diff = 0;
        for (String[] row : rows)
        {
            String status = row[14];
            String requestNo = row[3] == null ? null : row[3].trim();
            if (!"1".equals(status) || requestNo == null || requestNo.isEmpty())
            {
                // 非成功/非交易行（如冲正）计入差异，交人工判断。
                diff++;
                continue;
            }
            BankTransaction local = localByRequestNo.get(requestNo);
            if (local == null)
            {
                diff++;
                continue;
            }
            BigDecimal bankAmount = parseAmount(row[8]);
            if (bankAmount == null || local.getAmount() == null
                    || bankAmount.compareTo(local.getAmount()) != 0)
            {
                diff++;
                continue;
            }
            String bankSerial = row[13] == null ? null : row[13].trim();
            if (bankSerial != null && !bankSerial.isEmpty()
                    && !bankSerial.equals(local.getBankSerialNo()))
            {
                diff++;
                continue;
            }
            matched++;
            seen.add(requestNo);
        }
        String date = formatDate(run.getTrandt());
        for (BankTransaction local : localByRequestNo.values())
        {
            if (!seen.contains(local.getRequestNo()) && local.getBankTime() != null
                    && local.getBankTime().length() >= 8
                    && date.equals(local.getBankTime().substring(0, 8)))
            {
                diff++;
            }
        }
        run.setTotalRows(rows.size());
        run.setMatchedRows(matched);
        run.setDiffRows(diff);
    }

    private List<String[]> parseFile(byte[] content)
    {
        String text = new String(content, Charset.forName("GBK"));
        List<String[]> rows = new ArrayList<>();
        for (String line : text.split("\\r?\\n"))
        {
            String trimmed = line.trim();
            if (trimmed.isEmpty())
            {
                continue;
            }
            String[] fields = trimmed.split("\\|", -1);
            if (fields.length < 15)
            {
                throw new ServiceException("对账文件格式不完整");
            }
            rows.add(fields);
        }
        return rows;
    }

    private BigDecimal parseAmount(String value)
    {
        if (value == null || value.trim().isEmpty())
        {
            return null;
        }
        try
        {
            return new BigDecimal(value.trim()).setScale(2, RoundingMode.HALF_UP);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
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
