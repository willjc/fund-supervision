package com.ruoyi.service.bank.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.domain.bank.BankReconDiff;
import com.ruoyi.domain.bank.BankReconRun;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.mapper.bank.BankReconMapper;

/** 收单对账比对逻辑：xlsx 解析 + 四类差异识别。 */
@ExtendWith(MockitoExtension.class)
class BankAcqReconServiceTest
{
    @Mock private com.ruoyi.bank.gateway.ZhengzhouBankGateway gateway;
    @Mock private BankReconMapper reconMapper;

    @InjectMocks
    private BankAcqReconService service;

    private byte[] bill(String[][] rows) throws Exception
    {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream())
        {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("交易明细");
            String[] header = { "清算日期", "交易时间", "主商户号", "渠道", "交易类型", "清算金额", "商户订单号", "借贷记标识", "结算状态", "交易流水号" };
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int i = 0; i < header.length; i++)
            {
                headerRow.createCell(i).setCellValue(header[i]);
            }
            for (int r = 0; r < rows.length; r++)
            {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(r + 1);
                for (int c = 0; c < rows[r].length; c++)
                {
                    row.createCell(c).setCellValue(rows[r][c]);
                }
            }
            workbook.write(output);
            return output.toByteArray();
        }
    }

    private BankTransaction local(String requestNo, String amount, String serial, String bankTime)
    {
        BankTransaction tx = new BankTransaction();
        tx.setRequestNo(requestNo);
        tx.setAmount(new BigDecimal(amount));
        tx.setBankSerialNo(serial);
        tx.setBankTime(bankTime);
        tx.setStatus("SUCCESS");
        return tx;
    }

    private java.util.Map<String, String> bankRow(String orderNo, String amount, String serial)
    {
        java.util.Map<String, String> row = new java.util.HashMap<>();
        row.put("商户订单号", orderNo);
        row.put("清算金额", amount);
        row.put("交易流水号", serial);
        row.put("交易类型", "支付");
        return row;
    }

    @Test
    void shouldParseBillRowsByHeader() throws Exception
    {
        List<Map<String, String>> rows = service.parseBill(bill(new String[][] {
                { "20260911", "220001", "8202106040000001", "支付宝", "支付", "0.05", "BP96CA812036874DD38D96BD11CDD4E5", "借记", "已结算", "20026091121594085435415079232303" }
        }));

        assertEquals(1, rows.size());
        assertEquals("BP96CA812036874DD38D96BD11CDD4E5", rows.get(0).get("商户订单号"));
        assertEquals("0.05", rows.get(0).get("清算金额"));
        assertEquals("支付", rows.get(0).get("交易类型"));
    }

    @Test
    void shouldDetectAllDiffTypesAndMatch()
    {
        when(reconMapper.selectSuccessTransactions("8202106040000001")).thenReturn(java.util.List.of(
                local("BP_MATCH", "0.05", "SERIAL_MATCH", "20260911215940"),
                local("BP_AMOUNT", "0.05", "SERIAL_A", "20260911215940"),
                local("BP_SERIAL", "0.01", "SERIAL_OLD", "20260911215940"),
                local("BP_LOCAL_ONLY", "0.09", "SERIAL_L", "20260911215940"),
                local("BP_OTHER_DAY", "0.07", "SERIAL_D", "20260910215940")
        ));
        BankReconRun run = new BankReconRun();
        run.setRunId(1L);
        run.setMerId("8202106040000001");
        run.setClearingDate(new java.util.Date(0));

        service.compareAndStore(run, java.util.List.of(
                bankRow("BP_MATCH", "0.05", "SERIAL_MATCH"),
                bankRow("BP_AMOUNT", "0.06", "SERIAL_A"),
                bankRow("BP_SERIAL", "0.01", "SERIAL_NEW"),
                bankRow("BP_BANK_ONLY", "0.02", "SERIAL_B")
        ));

        ArgumentCaptor<BankReconDiff> captor = ArgumentCaptor.forClass(BankReconDiff.class);
        verify(reconMapper, org.mockito.Mockito.times(3)).insertDiff(captor.capture());
        Map<String, String> types = new java.util.HashMap<>();
        captor.getAllValues().forEach(diff -> types.put(diff.getRequestNo(), diff.getDiffType()));
        assertEquals("AMOUNT_MISMATCH", types.get("BP_AMOUNT"));
        assertEquals("SERIAL_MISMATCH", types.get("BP_SERIAL"));
        assertEquals("BANK_ONLY", types.get("BP_BANK_ONLY"));

        assertEquals(1, run.getMatchedRows());
    }

    @Test
    void shouldFlagLocalOnlyOnlyForSameClearingDate()
    {
        when(reconMapper.selectSuccessTransactions("8202106040000001")).thenReturn(java.util.List.of(
                local("BP_LOCAL_ONLY", "0.09", "SERIAL_L", "20260911215940"),
                local("BP_OTHER_DAY", "0.07", "SERIAL_D", "20260910215940")
        ));
        BankReconRun run = new BankReconRun();
        run.setRunId(2L);
        run.setMerId("8202106040000001");
        run.setClearingDate(java.sql.Date.valueOf("2026-09-11"));

        service.compareAndStore(run, List.of());

        ArgumentCaptor<BankReconDiff> captor = ArgumentCaptor.forClass(BankReconDiff.class);
        verify(reconMapper).insertDiff(captor.capture());
        assertEquals("BP_LOCAL_ONLY", captor.getValue().getRequestNo());
        assertEquals("LOCAL_ONLY", captor.getValue().getDiffType());
    }

    @Test
    void runReconShouldFailFastWhenApplyRejected()
    {
        com.alibaba.fastjson2.JSONObject rejected = new com.alibaba.fastjson2.JSONObject();
        rejected.put("respCode", "1081");
        rejected.put("respMsg", "当前清算日期下跑批未完成");
        when(gateway.applyBill("8202106040000001", "20260910")).thenReturn(rejected);
        when(reconMapper.insertRun(any(BankReconRun.class))).thenAnswer(invocation -> {
            invocation.getArgument(0, BankReconRun.class).setRunId(9L);
            return 1;
        });

        BankReconRun result = service.runRecon("8202106040000001", "20260910", null, "admin");

        assertEquals("failed", result.getStatus());
        assertEquals("1081", result.getRespCode());
        org.mockito.Mockito.verify(gateway, org.mockito.Mockito.never()).queryBillStatus(anyString(), anyString());
        org.mockito.Mockito.verify(gateway, org.mockito.Mockito.never()).downloadBillFile(anyString());
    }
}
