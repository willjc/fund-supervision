package com.ruoyi.web.controller.pension;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.pension.ExpenseRecord;
import com.ruoyi.service.pension.IExpenseRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 费用记录Controller
 *
 * @author ruoyi
 * @date 2025-12-18
 */
@RestController
@RequestMapping("/pension/expense")
public class ExpenseRecordController extends BaseController
{
    @Autowired
    private IExpenseRecordService expenseRecordService;

    /**
     * 查询费用记录列表
     */
    @PreAuthorize("@ss.hasPermi('pension:expense:list')")
    @GetMapping("/list")
    public TableDataInfo list(ExpenseRecord expenseRecord)
    {
        startPage();
        List<ExpenseRecord> list = expenseRecordService.selectExpenseRecordList(expenseRecord);
        return getDataTable(list);
    }

    /**
     * 根据老人ID查询费用记录
     */
    @PreAuthorize("@ss.hasPermi('pension:expense:query')")
    @GetMapping("/elder/{elderId}")
    public AjaxResult getExpenseByElderId(@PathVariable("elderId") Long elderId)
    {
        List<ExpenseRecord> list = expenseRecordService.selectExpenseRecordByElderId(elderId);
        return AjaxResult.success(list);
    }

    /**
     * 根据账户ID查询费用记录
     */
    @PreAuthorize("@ss.hasPermi('pension:expense:query')")
    @GetMapping("/account/{accountId}")
    public AjaxResult getExpenseByAccountId(@PathVariable("accountId") Long accountId)
    {
        List<ExpenseRecord> list = expenseRecordService.selectExpenseRecordByAccountId(accountId);
        return AjaxResult.success(list);
    }

    /**
     * 导出费用记录列表
     */
    @PreAuthorize("@ss.hasPermi('pension:expense:export')")
    @Log(title = "费用记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ExpenseRecord expenseRecord)
    {
        List<ExpenseRecord> list = expenseRecordService.selectExpenseRecordList(expenseRecord);
        ExcelUtil<ExpenseRecord> util = new ExcelUtil<ExpenseRecord>(ExpenseRecord.class);
        util.exportExcel(response, list, "费用记录数据");
    }

    /**
     * 获取费用记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:expense:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return AjaxResult.success(expenseRecordService.selectExpenseRecordByRecordId(recordId));
    }

    /**
     * 新增费用记录
     */
    @PreAuthorize("@ss.hasPermi('pension:expense:add')")
    @Log(title = "费用记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ExpenseRecord expenseRecord)
    {
        return toAjax(expenseRecordService.insertExpenseRecord(expenseRecord));
    }

    /**
     * 修改费用记录
     */
    @PreAuthorize("@ss.hasPermi('pension:expense:edit')")
    @Log(title = "费用记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ExpenseRecord expenseRecord)
    {
        return toAjax(expenseRecordService.updateExpenseRecord(expenseRecord));
    }

    /**
     * 删除费用记录
     */
    @PreAuthorize("@ss.hasPermi('pension:expense:remove')")
    @Log(title = "费用记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(expenseRecordService.deleteExpenseRecordByRecordIds(recordIds));
    }

    /**
     * 获取费用统计信息
     */
    @PreAuthorize("@ss.hasPermi('pension:expense:query')")
    @GetMapping("/statistics")
    public AjaxResult getStatistics()
    {
        // 这里可以添加统计逻辑，比如按费用类型统计等
        ExpenseRecord query = new ExpenseRecord();

        // 查询所有记录进行统计
        List<ExpenseRecord> allRecords = expenseRecordService.selectExpenseRecordList(query);

        // 按费用类型统计
        java.util.Map<String, java.math.BigDecimal> typeStatistics = new java.util.HashMap<>();
        java.util.Map<String, Long> typeCount = new java.util.HashMap<>();

        for (ExpenseRecord record : allRecords) {
            String type = record.getExpenseType();
            if (type != null) {
                typeStatistics.put(type, typeStatistics.getOrDefault(type, java.math.BigDecimal.ZERO).add(record.getAmount()));
                typeCount.put(type, typeCount.getOrDefault(type, 0L) + 1);
            }
        }

        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("typeStatistics", typeStatistics);
        statistics.put("typeCount", typeCount);
        statistics.put("totalRecords", allRecords.size());

        return AjaxResult.success(statistics);
    }
}