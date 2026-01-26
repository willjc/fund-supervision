package com.ruoyi.web.controller.pension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.domain.pension.SupervisionAccountLog;
import com.ruoyi.domain.PaymentRecord;
import com.ruoyi.service.pension.ISupervisionAccountLogService;
import com.ruoyi.service.IPaymentRecordService;

/**
 * 银行对账Controller
 *
 * @author ruoyi
 * @date 2025-01-03
 */
@RestController
@RequestMapping("/pension/bank")
public class BankReconciliationController extends BaseController
{
    @Autowired
    private ISupervisionAccountLogService supervisionAccountLogService;

    @Autowired
    private IPaymentRecordService paymentRecordService;

    /**
     * 获取账户信息
     */
    @GetMapping("/account-info")
    public AjaxResult getAccountInfo()
    {
        // 获取当前月份第一天和最后一天
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDay = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDay = calendar.getTime();

        // 统计本月收入和支出
        String startDate = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, firstDay);
        String endDate = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, lastDay);

        // 默认查询所有机构的数据
        Map<String, Object> statistics = supervisionAccountLogService.getStatisticsByDateRange(null, startDate, endDate);

        // 获取当前总余额（从最新的一条流水记录获取）
        // 这里简化处理，返回统计数据
        Map<String, Object> data = new HashMap<>();

        @SuppressWarnings("unchecked")
        BigDecimal totalIncome = (BigDecimal) statistics.get("totalIncome");
        @SuppressWarnings("unchecked")
        BigDecimal totalExpense = (BigDecimal) statistics.get("totalExpense");

        // 计算当前余额（总收入 - 总支出，或从最新流水获取）
        // 这里简化处理，假设初始余额为0
        BigDecimal balance = BigDecimal.ZERO;
        if (totalIncome != null) {
            balance = balance.add(totalIncome);
        }
        if (totalExpense != null) {
            balance = balance.subtract(totalExpense);
        }

        data.put("balance", balance);
        data.put("monthIncome", totalIncome != null ? totalIncome : BigDecimal.ZERO);
        data.put("monthExpense", totalExpense != null ? totalExpense : BigDecimal.ZERO);

        return success(data);
    }

    /**
     * 查询监管账户交易流水
     */
    @GetMapping("/supervision/list")
    public TableDataInfo getSupervisionList(
            SupervisionAccountLog supervisionAccountLog,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        startPage();
        List<SupervisionAccountLog> list = supervisionAccountLogService.selectSupervisionAccountLogList(supervisionAccountLog);

        // 转换为前端期望的格式
        List<Map<String, Object>> resultList = new java.util.ArrayList<>();
        for (SupervisionAccountLog log : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("logId", log.getLogId());
            item.put("transactionNo", log.getTransactionNo());
            item.put("transactionTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, log.getTransactionTime()));
            item.put("transactionType", "收入".equals(log.getTransactionType()) ? "转入" : "转出");
            item.put("amount", log.getAmount());
            item.put("balance", log.getBalanceAfter());
            item.put("description", log.getBusinessDesc());
            item.put("counterpartyName", log.getCounterparty());
            resultList.add(item);
        }

        return getDataTable(resultList);
    }

    /**
     * 查询收单交易流水
     */
    @GetMapping("/payment/list")
    public TableDataInfo getPaymentList(
            PaymentRecord paymentRecord,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        startPage();
        List<PaymentRecord> list = paymentRecordService.selectPaymentRecordList(paymentRecord);

        // 转换为前端期望的格式
        List<Map<String, Object>> resultList = new java.util.ArrayList<>();
        for (PaymentRecord record : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("paymentId", record.getPaymentId());
            item.put("orderNo", record.getOrderNo());
            item.put("transactionNo", record.getTransactionId());
            item.put("paymentTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, record.getPaymentTime()));
            item.put("amount", record.getPaymentAmount());
            item.put("paymentMethod", record.getPaymentMethod());
            item.put("paymentChannel", record.getPaymentMethod()); // 简化处理
            item.put("fee", BigDecimal.ZERO); // 手续费暂无字段
            item.put("actualAmount", record.getPaymentAmount());

            // 支付状态转换
            String status = "处理中";
            if ("1".equals(record.getPaymentStatus())) {
                status = "已完成";
            } else if ("2".equals(record.getPaymentStatus())) {
                status = "失败";
            }
            item.put("status", status);
            item.put("remark", record.getRemark());
            resultList.add(item);
        }

        return getDataTable(resultList);
    }

    /**
     * 获取收单交易统计
     */
    @GetMapping("/payment/statistics")
    public AjaxResult getPaymentStatistics()
    {
        // 获取今日统计
        String today = DateUtils.getDate();
        Date todayStart = DateUtils.parseDate(today);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todayStart);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date todayEnd = calendar.getTime();

        // 获取本月第一天
        Calendar monthCalendar = Calendar.getInstance();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        Date monthStart = monthCalendar.getTime();

        Map<String, Object> data = new HashMap<>();

        // TODO: 从 payment_record 表统计数据
        // 目前返回示例数据
        data.put("todayCount", 0);
        data.put("todayAmount", BigDecimal.ZERO);
        data.put("monthCount", 0);
        data.put("monthAmount", BigDecimal.ZERO);

        return success(data);
    }

    /**
     * 导出交易流水
     */
    @GetMapping("/export")
    public AjaxResult exportTransactions(@RequestParam String type)
    {
        // TODO: 实现导出功能
        logger.info("导出交易流水，类型: {}", type);
        return success("导出功能开发中");
    }
}
