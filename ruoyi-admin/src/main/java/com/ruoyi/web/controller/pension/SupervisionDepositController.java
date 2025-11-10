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
        // 默认查询待审批的押金申请
        depositApply.setApplyStatus("0");
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
        DepositApply depositApply = depositApplyService.selectDepositApplyByApplyId(applyId);
        if (depositApply == null) {
            return AjaxResult.error("押金申请记录不存在");
        }

        if (!"0".equals(depositApply.getApplyStatus())) {
            return AjaxResult.error("只能审批待处理状态的申请");
        }

        // 更新状态为已批准
        depositApply.setApplyStatus("1");
        depositApply.setApprover(getUsername());
        depositApply.setApproveTime(new java.util.Date());

        return toAjax(depositApplyService.updateDepositApply(depositApply));
    }

    /**
     * 审批拒绝押金使用申请
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:reject')")
    @Log(title = "押金使用审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/reject/{applyId}")
    public AjaxResult reject(@PathVariable Long applyId, @RequestBody DepositApply depositApply)
    {
        DepositApply existing = depositApplyService.selectDepositApplyByApplyId(applyId);
        if (existing == null) {
            return AjaxResult.error("押金申请记录不存在");
        }

        if (!"0".equals(existing.getApplyStatus())) {
            return AjaxResult.error("只能审批待处理状态的申请");
        }

        // 更新状态为已拒绝
        existing.setApplyStatus("2");
        existing.setApprover(getUsername());
        existing.setApproveTime(new java.util.Date());
        existing.setApproveRemark(depositApply.getApproveRemark()); // 拒绝原因

        return toAjax(depositApplyService.updateDepositApply(existing));
    }

    /**
     * 获取审批统计信息
     */
    @PreAuthorize("@ss.hasPermi('pension:deposit:query')")
    @GetMapping("/approval/statistics")
    public AjaxResult getApprovalStatistics()
    {
        DepositApply query = new DepositApply();

        // 待处理数量
        query.setApplyStatus("0");
        int pendingCount = depositApplyService.selectDepositApplyList(query).size();

        // 已批准数量
        query.setApplyStatus("1");
        int approvedCount = depositApplyService.selectDepositApplyList(query).size();

        // 已拒绝数量
        query.setApplyStatus("2");
        int rejectedCount = depositApplyService.selectDepositApplyList(query).size();

        // 已撤销数量
        query.setApplyStatus("3");
        int cancelledCount = depositApplyService.selectDepositApplyList(query).size();

        // 构建统计结果
        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("pendingCount", pendingCount);
        statistics.put("approvedCount", approvedCount);
        statistics.put("rejectedCount", rejectedCount);
        statistics.put("cancelledCount", cancelledCount);
        statistics.put("totalCount", pendingCount + approvedCount + rejectedCount + cancelledCount);

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
                DepositApply depositApply = depositApplyService.selectDepositApplyByApplyId(applyId);
                if (depositApply != null && "0".equals(depositApply.getApplyStatus())) {
                    depositApply.setApplyStatus("1");
                    depositApply.setApprover(getUsername());
                    depositApply.setApproveTime(new java.util.Date());
                    depositApplyService.updateDepositApply(depositApply);
                    successCount++;
                } else {
                    failCount++;
                }
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
        query.setApplyStatus("0");
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
        query.setApplyStatus("0");
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
        query.setApplyStatus("0");
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
        query.setApplyStatus("0");
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
