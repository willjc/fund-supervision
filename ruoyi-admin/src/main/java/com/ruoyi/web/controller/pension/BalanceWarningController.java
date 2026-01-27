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
import com.ruoyi.domain.pension.BalanceWarning;
import com.ruoyi.service.pension.IBalanceWarningService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 余额预警Controller
 *
 * @author ruoyi
 * @date 2025-01-25
 */
@RestController
@RequestMapping("/pension/balanceWarning")
public class BalanceWarningController extends BaseController
{
    @Autowired
    private IBalanceWarningService balanceWarningService;

    /**
     * 查询余额预警列表
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:list')")
    @GetMapping("/list")
    public TableDataInfo list(BalanceWarning balanceWarning)
    {
        startPage();
        List<BalanceWarning> list = balanceWarningService.selectBalanceWarningList(balanceWarning);
        return getDataTable(list);
    }

    /**
     * 导出余额预警列表
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:export')")
    @Log(title = "余额预警", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BalanceWarning balanceWarning)
    {
        List<BalanceWarning> list = balanceWarningService.selectBalanceWarningList(balanceWarning);
        ExcelUtil<BalanceWarning> util = new ExcelUtil<BalanceWarning>(BalanceWarning.class);
        util.exportExcel(response, list, "余额预警数据");
    }

    /**
     * 获取余额预警详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:query')")
    @GetMapping(value = "/{warningId}")
    public AjaxResult getInfo(@PathVariable("warningId") Long warningId)
    {
        return success(balanceWarningService.selectBalanceWarningByWarningId(warningId));
    }

    /**
     * 根据机构ID查询预警记录
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:query')")
    @GetMapping("/institution/{institutionId}")
    public AjaxResult getByInstitutionId(@PathVariable Long institutionId)
    {
        List<BalanceWarning> list = balanceWarningService.selectBalanceWarningByInstitutionId(institutionId);
        return success(list);
    }

    /**
     * 获取预警统计数据
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:query')")
    @GetMapping("/statistics")
    public AjaxResult getWarningStatistics(@RequestParam(required = false) Long institutionId)
    {
        try {
            if (institutionId == null) {
                institutionId = 1L; // 这里应该从登录用户获取机构ID
            }
            Map<String, Object> statistics = balanceWarningService.getWarningStatistics(institutionId);
            return success(statistics);
        } catch (Exception e) {
            return error("获取预警统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取严重预警账户
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:query')")
    @GetMapping("/critical")
    public AjaxResult getCriticalWarnings(@RequestParam(required = false) Long institutionId)
    {
        try {
            if (institutionId == null) {
                institutionId = 1L; // 这里应该从登录用户获取机构ID
            }
            List<BalanceWarning> list = balanceWarningService.selectCriticalWarnings(institutionId);
            return success(list);
        } catch (Exception e) {
            return error("获取严重预警失败：" + e.getMessage());
        }
    }

    /**
     * 新增余额预警
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:add')")
    @Log(title = "余额预警", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BalanceWarning balanceWarning)
    {
        return toAjax(balanceWarningService.insertBalanceWarning(balanceWarning));
    }

    /**
     * 修改余额预警
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:edit')")
    @Log(title = "余额预警", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BalanceWarning balanceWarning)
    {
        return toAjax(balanceWarningService.updateBalanceWarning(balanceWarning));
    }

    /**
     * 删除余额预警
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:remove')")
    @Log(title = "余额预警", businessType = BusinessType.DELETE)
	@DeleteMapping("/{warningIds}")
    public AjaxResult remove(@PathVariable Long[] warningIds)
    {
        return toAjax(balanceWarningService.deleteBalanceWarningByWarningIds(warningIds));
    }

    /**
     * 批量处理预警
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:handle')")
    @Log(title = "批量处理预警", businessType = BusinessType.UPDATE)
    @PostMapping("/batchHandle")
    public AjaxResult batchHandle(@RequestBody Map<String, Object> params)
    {
        try {
            @SuppressWarnings("unchecked")
            List<Long> warningIds = (List<Long>) params.get("warningIds");
            String handleStatus = (String) params.get("handleStatus");
            String handleRemark = (String) params.get("handleRemark");
            String handleBy = getUsername();

            int result = balanceWarningService.batchHandleWarnings(warningIds, handleStatus, handleRemark, handleBy);
            return success(result);
        } catch (Exception e) {
            return error("批量处理预警失败：" + e.getMessage());
        }
    }

    /**
     * 发送预警通知
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:notify')")
    @Log(title = "发送预警通知", businessType = BusinessType.UPDATE)
    @PostMapping("/notify/{warningId}")
    public AjaxResult sendNotification(@PathVariable Long warningId)
    {
        try {
            int result = balanceWarningService.sendWarningNotification(warningId);
            return success(result);
        } catch (Exception e) {
            return error("发送预警通知失败：" + e.getMessage());
        }
    }

    /**
     * 自动生成预警记录
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:generate')")
    @Log(title = "自动生成预警", businessType = BusinessType.INSERT)
    @PostMapping("/autoGenerate")
    public AjaxResult autoGenerateWarnings()
    {
        try {
            Long institutionId = 1L; // 这里应该从登录用户获取机构ID
            int result = balanceWarningService.autoGenerateWarnings(institutionId);
            return success(result);
        } catch (Exception e) {
            return error("自动生成预警失败：" + e.getMessage());
        }
    }

    /**
     * 清除已处理预警
     */
    @PreAuthorize("@ss.hasPermi('pension:warning:clear')")
    @Log(title = "清除已处理预警", businessType = BusinessType.DELETE)
    @DeleteMapping("/clear")
    public AjaxResult clearHandledWarnings(@RequestParam(required = false) Long institutionId)
    {
        try {
            if (institutionId == null) {
                institutionId = 1L; // 这里应该从登录用户获取机构ID
            }
            int result = balanceWarningService.clearHandledWarnings(institutionId);
            return success(result);
        } catch (Exception e) {
            return error("清除已处理预警失败：" + e.getMessage());
        }
    }
}