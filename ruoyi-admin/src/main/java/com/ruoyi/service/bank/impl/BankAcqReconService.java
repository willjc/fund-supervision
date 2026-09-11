package com.ruoyi.service.bank.impl;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.bank.gateway.ZhengzhouBankGateway;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.bank.BankReconDiff;
import com.ruoyi.domain.bank.BankReconRun;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.mapper.bank.BankReconMapper;

/**
 * 收单对账：uMtBillApply → uMtBillQuery → newsddzfiledown 下载，
 * 按"商户订单号"与本地 bank_transaction(PAY/REFUND, SUCCESS) 比对，差异落库不自动改账。
 * 沙箱跑批不可用时允许直接按文件名下载（银行人工放置模板）。
 */
@Service
public class BankAcqReconService
{
    /** 银行对账单命名规则：appId_商户号_清算日期.xlsx。 */
    private static final String KEY_ORDER_NO = "商户订单号";
    private static final String KEY_SERIAL = "交易流水号";
    private static final String KEY_AMOUNT = "清算金额";
    private static final String KEY_TXN_TYPE = "交易类型";

    @Autowired private ZhengzhouBankGateway gateway;
    @Autowired private BankReconMapper reconMapper;

    @Value("${bank.integration.app-id:}")
    private String appId;

    /** 执行一次对账。fileName 为空走 apply/query 流程，否则直接下载（沙箱人工放文件场景）。 */
    public BankReconRun runRecon(String merId, String clearingDate, String fileNameOverride, String operator)
    {
        BankReconRun run = new BankReconRun();
        run.setMerId(merId);
        run.setClearingDate(parseDate(clearingDate));
        run.setStatus("running");
        run.setFileName(fileNameOverride);
        run.setTotalRows(0);
        run.setMatchedRows(0);
        run.setDiffRows(0);
        run.setCreateBy(operator);
        reconMapper.insertRun(run);

        try
        {
            String fileName = fileNameOverride;
            if (fileName == null || fileName.trim().isEmpty())
            {
                JSONObject apply = gateway.applyBill(merId, clearingDate);
                String applyCode = apply.getString("respCode");
                run.setRespCode(applyCode);
                run.setRespMessage(apply.getString("respMsg"));
                if (!"0000".equals(applyCode))
                {
                    finish(run, "failed");
                    return run;
                }
                JSONObject query = gateway.queryBillStatus(merId, clearingDate);
                String billStat = query.getString("billStat");
                run.setBillStat(billStat);
                run.setRespCode(query.getString("respCode"));
                run.setRespMessage(query.getString("respMsg"));
                if (!"01".equals(billStat))
                {
                    finish(run, "no_bill");
                    return run;
                }
                fileName = String.format("%s_%s_%s.xlsx", appId, merId, clearingDate);
                run.setFileName(fileName);
            }

            byte[] content = gateway.downloadBillFile(fileName);
            run.setDownloadMd5(org.springframework.util.DigestUtils.md5DigestAsHex(content));
            List<Map<String, String>> rows = parseBill(content);
            run.setTotalRows(rows.size());

            compareAndStore(run, rows);
            run.setDiffRows(reconMapper.selectDiffs(run.getRunId()).size());
            finish(run, "success");
            return run;
        }
        catch (ServiceException e)
        {
            run.setRespMessage(truncate(e.getMessage(), 500));
            finish(run, "failed");
            return run;
        }
    }

    /** 与本地成功交易比对：银行有本地无 / 本地有银行无 / 金额不符 / 流水不符。 */
    Set<String> compareAndStore(BankReconRun run, List<Map<String, String>> rows)
    {
        Map<String, BankTransaction> localByOrderNo = new HashMap<>();
        for (BankTransaction tx : reconMapper.selectSuccessTransactions(run.getMerId()))
        {
            localByOrderNo.put(tx.getRequestNo(), tx);
        }
        Set<String> seen = new HashSet<>();
        int matched = 0;
        for (Map<String, String> row : rows)
        {
            String orderNo = row.get(KEY_ORDER_NO);
            if (orderNo == null || orderNo.trim().isEmpty() || !seen.add(orderNo))
            {
                continue;
            }
            BigDecimal bankAmount = parseAmount(row.get(KEY_AMOUNT));
            String bankSerial = trim(row.get(KEY_SERIAL));
            BankTransaction local = localByOrderNo.get(orderNo);
            if (local == null)
            {
                save(run.getRunId(), "BANK_ONLY", orderNo, bankSerial, bankAmount, row.get(KEY_TXN_TYPE),
                        null, null, null);
                continue;
            }
            if (bankAmount != null && local.getAmount() != null
                    && bankAmount.compareTo(local.getAmount()) != 0)
            {
                save(run.getRunId(), "AMOUNT_MISMATCH", orderNo, bankSerial, bankAmount, row.get(KEY_TXN_TYPE),
                        local.getAmount(), local.getBankSerialNo(), local.getStatus());
                continue;
            }
            if (bankSerial != null && !bankSerial.equals(local.getBankSerialNo()))
            {
                save(run.getRunId(), "SERIAL_MISMATCH", orderNo, bankSerial, bankAmount, row.get(KEY_TXN_TYPE),
                        local.getAmount(), local.getBankSerialNo(), local.getStatus());
                continue;
            }
            matched++;
        }
        for (BankTransaction local : localByOrderNo.values())
        {
            if (!seen.contains(local.getRequestNo()) && sameClearingDate(local.getBankTime(), run.getClearingDate()))
            {
                save(run.getRunId(), "LOCAL_ONLY", local.getRequestNo(), local.getBankSerialNo(), null, null,
                        local.getAmount(), local.getBankSerialNo(), local.getStatus());
            }
        }
        run.setMatchedRows(matched);
        return seen;
    }

    /** 解析对账 xlsx：首行为表头，按列名取值。 */
    List<Map<String, String>> parseBill(byte[] content)
    {
        List<Map<String, String>> rows = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(content)))
        {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(sheet.getFirstRowNum());
            Map<Integer, String> columns = new HashMap<>();
            for (Cell cell : header)
            {
                String name = trim(cell);
                if (!name.isEmpty())
                {
                    columns.put(cell.getColumnIndex(), name);
                }
            }
            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++)
            {
                Row row = sheet.getRow(i);
                if (row == null)
                {
                    continue;
                }
                Map<String, String> values = new HashMap<>();
                boolean hasOrderNo = false;
                for (Map.Entry<Integer, String> column : columns.entrySet())
                {
                    String value = trim(row.getCell(column.getKey()));
                    values.put(column.getValue(), value);
                    if (KEY_ORDER_NO.equals(column.getValue()) && !value.isEmpty())
                    {
                        hasOrderNo = true;
                    }
                }
                if (hasOrderNo)
                {
                    rows.add(values);
                }
            }
            return rows;
        }
        catch (Exception e)
        {
            throw new ServiceException("对账文件解析失败：" + e.getMessage());
        }
    }

    private void save(Long runId, String type, String orderNo, String bankSerial, BigDecimal bankAmount,
            String txnType, BigDecimal localAmount, String localSerial, String localStatus)
    {
        BankReconDiff diff = new BankReconDiff();
        diff.setRunId(runId);
        diff.setDiffType(type);
        diff.setRequestNo(orderNo);
        diff.setBankSerialNo(bankSerial);
        diff.setBankAmount(bankAmount);
        diff.setBankDetail(txnType);
        diff.setLocalAmount(localAmount);
        diff.setLocalSerialNo(localSerial);
        diff.setLocalStatus(localStatus);
        reconMapper.insertDiff(diff);
    }

    private void finish(BankReconRun run, String status)
    {
        run.setStatus(status);
        run.setUpdateTime(new Date());
        reconMapper.updateRun(run);
    }

    private boolean sameClearingDate(String bankTime, Date clearingDate)
    {
        if (bankTime == null || bankTime.length() < 8 || clearingDate == null)
        {
            return false;
        }
        return new SimpleDateFormat("yyyyMMdd").format(clearingDate).equals(bankTime.substring(0, 8));
    }

    private BigDecimal parseAmount(String value)
    {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty())
        {
            return null;
        }
        try
        {
            return new BigDecimal(trimmed).setScale(2, java.math.RoundingMode.HALF_UP);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private String trim(Cell cell)
    {
        if (cell == null)
        {
            return "";
        }
        if (cell.getCellType() == CellType.NUMERIC)
        {
            double number = cell.getNumericCellValue();
            if (number == Math.floor(number))
            {
                return String.valueOf((long) number);
            }
            return BigDecimal.valueOf(number).toPlainString();
        }
        return cell.getStringCellValue() == null ? "" : cell.getStringCellValue().trim();
    }

    private String trim(String value)
    {
        return value == null ? "" : value.trim();
    }

    private Date parseDate(String clearingDate)
    {
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            format.setLenient(false);
            return format.parse(clearingDate);
        }
        catch (Exception e)
        {
            throw new ServiceException("清算日期格式必须为 yyyyMMdd");
        }
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
