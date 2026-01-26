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
     * 获取监管账户流水详情
     */
    @GetMapping("/supervision/detail/{logId}")
    public AjaxResult getSupervisionDetail(@PathVariable Long logId)
    {
        SupervisionAccountLog log = supervisionAccountLogService.selectSupervisionAccountLogByLogId(logId);
        if (log == null) {
            return error("流水记录不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("logId", log.getLogId());
        data.put("transactionNo", log.getTransactionNo());
        data.put("transactionTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, log.getTransactionTime()));
        data.put("transactionType", "收入".equals(log.getTransactionType()) ? "转入" : "转出");
        data.put("amount", log.getAmount());
        data.put("balanceBefore", log.getBalanceBefore());
        data.put("balanceAfter", log.getBalanceAfter());
        data.put("balance", log.getBalanceAfter());
        data.put("businessType", log.getBusinessType());
        data.put("description", log.getBusinessDesc());
        data.put("counterparty", log.getCounterparty());
        data.put("operator", log.getOperator());
        data.put("relatedTransferId", log.getRelatedTransferId());
        data.put("relatedOrderId", log.getRelatedOrderId());

        return success(data);
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

        // 获取当前监管账户余额
        BigDecimal currentBalance = supervisionAccountLogService.getCurrentBalance(null);

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
            item.put("supervisionBalance", currentBalance); // 监管账户余额

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
     * 获取收单交易详情
     */
    @GetMapping("/payment/detail/{paymentId}")
    public AjaxResult getPaymentDetail(@PathVariable Long paymentId)
    {
        PaymentRecord record = paymentRecordService.selectPaymentRecordByPaymentId(paymentId);
        if (record == null) {
            return error("支付记录不存在");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("paymentId", record.getPaymentId());
        data.put("paymentNo", record.getPaymentNo());
        data.put("orderNo", record.getOrderNo());
        data.put("transactionNo", record.getTransactionId());
        data.put("paymentTime", record.getPaymentTime() != null ?
            DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, record.getPaymentTime()) : "");
        data.put("amount", record.getPaymentAmount());
        data.put("paymentMethod", record.getPaymentMethod());
        data.put("actualAmount", record.getPaymentAmount());
        data.put("paymentProof", record.getPaymentProof());
        data.put("paymentProofRemark", record.getPaymentProofRemark());

        // 支付状态转换
        String status = "处理中";
        if ("1".equals(record.getPaymentStatus())) {
            status = "已完成";
        } else if ("2".equals(record.getPaymentStatus())) {
            status = "失败";
        }
        data.put("status", status);
        data.put("remark", record.getRemark());

        // 获取关联的老人和机构名称（需要查询）
        data.put("elderName", ""); // TODO: 从elder_info表查询
        data.put("institutionName", ""); // TODO: 从pension_institution表查询

        return success(data);
    }

    /**
     * 获取收单交易统计
     */
    @GetMapping("/payment/statistics")
    public AjaxResult getPaymentStatistics()
    {
        // 获取今日统计
        String today = DateUtils.getDate();

        // 获取本月第一天
        Calendar monthCalendar = Calendar.getInstance();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        String monthStart = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, monthCalendar.getTime());

        Map<String, Object> data = new HashMap<>();

        // 查询今日已支付记录
        PaymentRecord todayQuery = new PaymentRecord();
        todayQuery.setPaymentStatus("1"); // 已支付
        Map<String, Object> todayParams = new HashMap<>();
        todayParams.put("beginTime", today);
        todayParams.put("endTime", today);
        todayQuery.setParams(todayParams);
        List<PaymentRecord> todayList = paymentRecordService.selectPaymentRecordList(todayQuery);

        int todayCount = todayList.size();
        BigDecimal todayAmount = BigDecimal.ZERO;
        for (PaymentRecord record : todayList) {
            if (record.getPaymentAmount() != null) {
                todayAmount = todayAmount.add(record.getPaymentAmount());
            }
        }

        // 查询本月已支付记录
        PaymentRecord monthQuery = new PaymentRecord();
        monthQuery.setPaymentStatus("1");
        Map<String, Object> monthParams = new HashMap<>();
        monthParams.put("beginTime", monthStart);
        monthQuery.setParams(monthParams);
        List<PaymentRecord> monthList = paymentRecordService.selectPaymentRecordList(monthQuery);

        int monthCount = monthList.size();
        BigDecimal monthAmount = BigDecimal.ZERO;
        for (PaymentRecord record : monthList) {
            if (record.getPaymentAmount() != null) {
                monthAmount = monthAmount.add(record.getPaymentAmount());
            }
        }

        data.put("todayCount", todayCount);
        data.put("todayAmount", todayAmount);
        data.put("monthCount", monthCount);
        data.put("monthAmount", monthAmount);

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
