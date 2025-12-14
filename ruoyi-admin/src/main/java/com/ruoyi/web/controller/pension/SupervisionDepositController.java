package com.ruoyi.web.controller.pension;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ruoyi.domain.pension.DepositApply;
import com.ruoyi.service.pension.IDepositApplyService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 民政监管-押金使用审批Controller
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@RestController
@RequestMapping("/pension/supervision/deposit")
public class SupervisionDepositController extends BaseController
{
    @Autowired
    private IDepositApplyService depositApplyService;

    /**
     * 查询押金使用审批列表
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:list')")
    @GetMapping("/approval/list")
    public TableDataInfo list(DepositApply depositApply)
    {
        startPage();
        // 默认查询家属已审批的押金申请（等待监管审批）
        if (depositApply.getApplyStatus() == null || depositApply.getApplyStatus().isEmpty()) {
            depositApply.setApplyStatus("family_approved");
        }
        List<DepositApply> list = depositApplyService.selectDepositApplyList(depositApply);
        return getDataTable(list);
    }

    /**
     * 查询所有状态的押金申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:list')")
    @GetMapping("/approval/all")
    public TableDataInfo listAll(DepositApply depositApply)
    {
        startPage();
        List<DepositApply> list = depositApplyService.selectDepositApplyList(depositApply);
        return getDataTable(list);
    }

    /**
     * 导出押金使用审批列表
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:export')")
    @Log(title = "押金使用审批", businessType = BusinessType.EXPORT)
    @PostMapping("/approval/export")
    public void export(HttpServletResponse response, DepositApply depositApply)
    {
        List<DepositApply> list = depositApplyService.selectDepositApplyList(depositApply);
        ExcelUtil<DepositApply> util = new ExcelUtil<DepositApply>(DepositApply.class);
        util.exportExcel(response, list, "押金使用审批数据");
    }

    /**
     * 获取押金申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:query')")
    @GetMapping(value = "/approval/{applyId}")
    public AjaxResult getInfo(@PathVariable("applyId") Long applyId)
    {
        return AjaxResult.success(depositApplyService.selectDepositApplyByApplyId(applyId));
    }

    /**
     * 审批通过押金使用申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:approve')")
    @Log(title = "押金使用审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/approve/{applyId}")
    public AjaxResult approve(@PathVariable Long applyId)
    {
        try {
            // 调用监管审批方法（包含扣除押金余额的逻辑）
            int result = depositApplyService.supervisionApprove(applyId, true, "审批通过", getUsername());
            if (result > 0) {
                return AjaxResult.success("审批通过成功，押金已扣除");
            } else {
                return AjaxResult.error("审批失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 审批拒绝押金使用申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:reject')")
    @Log(title = "押金使用审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/reject/{applyId}")
    public AjaxResult reject(@PathVariable Long applyId, @RequestBody DepositApply depositApply)
    {
        try {
            String rejectReason = depositApply.getApproveRemark();
            if (rejectReason == null || rejectReason.isEmpty()) {
                return AjaxResult.error("请输入拒绝原因");
            }

            // 调用监管审批方法（拒绝）
            int result = depositApplyService.supervisionApprove(applyId, false, rejectReason, getUsername());
            if (result > 0) {
                return AjaxResult.success("审批拒绝成功");
            } else {
                return AjaxResult.error("审批失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 获取审批统计信息
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:query')")
    @GetMapping("/approval/statistics")
    public AjaxResult getApprovalStatistics()
    {
        DepositApply query = new DepositApply();

        // 待监管审批数量（家属已审批）
        query.setApplyStatus("family_approved");
        int pendingCount = depositApplyService.selectDepositApplyList(query).size();

        // 已批准数量
        query.setApplyStatus("approved");
        int approvedCount = depositApplyService.selectDepositApplyList(query).size();

        // 已拒绝数量
        query.setApplyStatus("rejected");
        int rejectedCount = depositApplyService.selectDepositApplyList(query).size();

        // 构建统计结果
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("pendingCount", pendingCount);
        statistics.put("approvedCount", approvedCount);
        statistics.put("rejectedCount", rejectedCount);
        statistics.put("totalCount", pendingCount + approvedCount + rejectedCount);

        return AjaxResult.success(statistics);
    }

    /**
     * 批量审批
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:approve')")
    @Log(title = "押金使用审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/batchApprove")
    public AjaxResult batchApprove(@RequestBody Long[] applyIds)
    {
        int successCount = 0;
        int failCount = 0;

        for (Long applyId : applyIds) {
            try {
                depositApplyService.supervisionApprove(applyId, true, "批量审批通过", getUsername());
                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }

        String message = String.format("批量审批完成：成功 %d 个，失败 %d 个", successCount, failCount);
        return AjaxResult.success(message);
    }

    /**
     * 获取今日待处理押金申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:query')")
    @GetMapping("/approval/todayPending")
    public AjaxResult getTodayPending()
    {
        DepositApply query = new DepositApply();
        query.setApplyStatus("family_approved");
        List<DepositApply> allPending = depositApplyService.selectDepositApplyList(query);

        // 筛选今日的押金申请
        List<DepositApply> todayPending = allPending.stream()
            .filter(apply -> {
                java.util.Date createTime = apply.getCreateTime();
                if (createTime != null) {
                    java.time.LocalDate createLocalDate = createTime.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
                    return createLocalDate.equals(java.time.LocalDate.now());
                }
                return false;
            })
            .collect(java.util.stream.Collectors.toList());

        return AjaxResult.success(todayPending);
    }

    /**
     * 获取紧急押金申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:query')")
    @GetMapping("/approval/urgentApplies")
    public AjaxResult getUrgentApplies()
    {
        DepositApply query = new DepositApply();
        query.setApplyStatus("family_approved");
        query.setUrgencyLevel("1"); // 紧急
        List<DepositApply> urgentList = depositApplyService.selectDepositApplyList(query);

        return AjaxResult.success(urgentList);
    }

    /**
     * 获取大额押金申请（金额>5000元）
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:query')")
    @GetMapping("/approval/largeAmount")
    public AjaxResult getLargeAmountApplies()
    {
        DepositApply query = new DepositApply();
        query.setApplyStatus("family_approved");
        List<DepositApply> allPending = depositApplyService.selectDepositApplyList(query);

        // 筛选金额大于5000的押金申请
        List<DepositApply> largeAmountApplies = allPending.stream()
            .filter(apply -> {
                if (apply.getApplyAmount() != null) {
                    return apply.getApplyAmount().doubleValue() > 5000.0;
                }
                return false;
            })
            .collect(java.util.stream.Collectors.toList());

        return AjaxResult.success(largeAmountApplies);
    }

    /**
     * 按申请类型统计
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:query')")
    @GetMapping("/approval/statisticsByType")
    public AjaxResult getStatisticsByType()
    {
        DepositApply query = new DepositApply();
        query.setApplyStatus("family_approved");
        List<DepositApply> allPending = depositApplyService.selectDepositApplyList(query);

        // 按申请类型分组统计
        java.util.Map<String, Long> typeStatistics = allPending.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                apply -> apply.getApplyType() != null ? apply.getApplyType() : "其他",
                java.util.stream.Collectors.counting()
            ));

        return AjaxResult.success(typeStatistics);
    }
}
