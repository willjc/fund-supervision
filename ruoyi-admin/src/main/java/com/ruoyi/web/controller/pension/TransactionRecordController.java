package com.ruoyi.web.controller.pension;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.pension.TransactionRecord;
import com.ruoyi.service.pension.ITransactionRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 交易记录Controller
 *
 * @author ruoyi
 * @date 2025-01-25
 */
@RestController
@RequestMapping("/pension/transaction")
public class TransactionRecordController extends BaseController
{
    @Autowired
    private ITransactionRecordService transactionRecordService;

    /**
     * 查询交易记录列表
     */
    @PreAuthorize("@ss.hasPermi('pension:transaction:list')")
    @GetMapping("/list")
    public TableDataInfo list(TransactionRecord transactionRecord)
    {
        startPage();
        List<TransactionRecord> list = transactionRecordService.selectTransactionRecordList(transactionRecord);
        return getDataTable(list);
    }

    /**
     * 导出交易记录列表
     */
    @PreAuthorize("@ss.hasPermi('pension:transaction:export')")
    @Log(title = "交易记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TransactionRecord transactionRecord)
    {
        List<TransactionRecord> list = transactionRecordService.selectTransactionRecordList(transactionRecord);
        ExcelUtil<TransactionRecord> util = new ExcelUtil<TransactionRecord>(TransactionRecord.class);
        util.exportExcel(response, list, "交易记录数据");
    }

    /**
     * 获取交易记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:transaction:query')")
    @GetMapping(value = "/{transactionId}")
    public AjaxResult getInfo(@PathVariable("transactionId") Long transactionId)
    {
        return success(transactionRecordService.selectTransactionRecordByTransactionId(transactionId));
    }

    /**
     * 根据账户ID查询交易记录
     */
    @PreAuthorize("@ss.hasPermi('pension:transaction:query')")
    @GetMapping("/account/{accountId}")
    public AjaxResult getByAccountId(@PathVariable Long accountId)
    {
        List<TransactionRecord> list = transactionRecordService.selectTransactionRecordByAccountId(accountId);
        return success(list);
    }

    /**
     * 根据机构ID查询交易记录
     */
    @PreAuthorize("@ss.hasPermi('pension:transaction:query')")
    @GetMapping("/institution/{institutionId}")
    public AjaxResult getByInstitutionId(@PathVariable Long institutionId)
    {
        List<TransactionRecord> list = transactionRecordService.selectTransactionRecordByInstitutionId(institutionId);
        return success(list);
    }

    /**
     * 交易统计分析
     */
    @PreAuthorize("@ss.hasPermi('pension:transaction:query')")
    @GetMapping("/statistics")
    public AjaxResult getStatistics(@RequestParam Map<String, Object> params)
    {
        try {
            Long institutionId = params.get("institutionId") != null ? Long.parseLong(params.get("institutionId").toString()) : null;
            String startDate = (String) params.get("startDate");
            String endDate = (String) params.get("endDate");

            Map<String, Object> statistics = transactionRecordService.getTransactionStatistics(institutionId, startDate, endDate);
            return success(statistics);
        } catch (Exception e) {
            return error("获取统计信息失败：" + e.getMessage());
        }
    }

    /**
     * 新增交易记录
     */
    @PreAuthorize("@ss.hasPermi('pension:transaction:add')")
    @Log(title = "交易记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TransactionRecord transactionRecord)
    {
        return toAjax(transactionRecordService.insertTransactionRecord(transactionRecord));
    }

    /**
     * 修改交易记录
     */
    @PreAuthorize("@ss.hasPermi('pension:transaction:edit')")
    @Log(title = "交易记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TransactionRecord transactionRecord)
    {
        return toAjax(transactionRecordService.updateTransactionRecord(transactionRecord));
    }

    /**
     * 删除交易记录
     */
    @PreAuthorize("@ss.hasPermi('pension:transaction:remove')")
    @Log(title = "交易记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{transactionIds}")
    public AjaxResult remove(@PathVariable Long[] transactionIds)
    {
        return toAjax(transactionRecordService.deleteTransactionRecordByTransactionIds(transactionIds));
    }

    /**
     * 批量创建交易记录（内部使用）
     */
    @PreAuthorize("@ss.hasPermi('pension:transaction:add')")
    @Log(title = "批量创建交易记录", businessType = BusinessType.INSERT)
    @PostMapping("/batch")
    public AjaxResult batchCreate(@RequestBody List<TransactionRecord> transactionRecords)
    {
        try {
            int result = transactionRecordService.batchCreateTransactionRecords(transactionRecords);
            return success(result);
        } catch (Exception e) {
            return error("批量创建交易记录失败：" + e.getMessage());
        }
    }

    /**
     * 查询今日交易汇总
     */
    @PreAuthorize("@ss.hasPermi('pension:transaction:query')")
    @GetMapping("/today")
    public AjaxResult getTodaySummary()
    {
        try {
            Long institutionId = 1L; // 这里应该从登录用户获取机构ID
            Map<String, Object> summary = transactionRecordService.getTodayTransactionSummary(institutionId);
            return success(summary);
        } catch (Exception e) {
            return error("获取今日交易汇总失败：" + e.getMessage());
        }
    }

    /**
     * 查询异常交易记录
     */
    @PreAuthorize("@ss.hasPermi('pension:transaction:query')")
    @GetMapping("/abnormal")
    public AjaxResult getAbnormalTransactions()
    {
        try {
            List<TransactionRecord> list = transactionRecordService.selectAbnormalTransactions();
            return success(list);
        } catch (Exception e) {
            return error("获取异常交易记录失败：" + e.getMessage());
        }
    }
}