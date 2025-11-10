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
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.service.pension.IFundTransferService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 民政监管-资金划拨审批Controller
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@RestController
@RequestMapping("/pension/supervision/fundTransfer")
public class SupervisionFundTransferController extends BaseController
{
    @Autowired
    private IFundTransferService fundTransferService;

    /**
     * 查询资金划拨审批列表
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:list')")
    @GetMapping("/approval/list")
    public TableDataInfo list(FundTransfer fundTransfer)
    {
        startPage();
        // 默认查询待审批的划拨记录
        fundTransfer.setTransferStatus("0");
        List<FundTransfer> list = fundTransferService.selectFundTransferList(fundTransfer);
        return getDataTable(list);
    }

    /**
     * 查询所有状态的划拨记录
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:list')")
    @GetMapping("/approval/all")
    public TableDataInfo listAll(FundTransfer fundTransfer)
    {
        startPage();
        List<FundTransfer> list = fundTransferService.selectFundTransferList(fundTransfer);
        return getDataTable(list);
    }

    /**
     * 导出资金划拨审批列表
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:export')")
    @Log(title = "资金划拨审批", businessType = BusinessType.EXPORT)
    @PostMapping("/approval/export")
    public void export(HttpServletResponse response, FundTransfer fundTransfer)
    {
        List<FundTransfer> list = fundTransferService.selectFundTransferList(fundTransfer);
        ExcelUtil<FundTransfer> util = new ExcelUtil<FundTransfer>(FundTransfer.class);
        util.exportExcel(response, list, "资金划拨审批数据");
    }

    /**
     * 获取资金划拨详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:query')")
    @GetMapping(value = "/approval/{transferId}")
    public AjaxResult getInfo(@PathVariable("transferId") Long transferId)
    {
        return AjaxResult.success(fundTransferService.selectFundTransferByTransferId(transferId));
    }

    /**
     * 审批通过资金划拨
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:approve')")
    @Log(title = "资金划拨审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/approve/{transferId}")
    public AjaxResult approve(@PathVariable Long transferId)
    {
        FundTransfer fundTransfer = fundTransferService.selectFundTransferByTransferId(transferId);
        if (fundTransfer == null) {
            return AjaxResult.error("划拨记录不存在");
        }

        if (!"0".equals(fundTransfer.getTransferStatus())) {
            return AjaxResult.error("只能审批待处理状态的划拨");
        }

        // 更新状态为成功
        fundTransfer.setTransferStatus("1");
        fundTransfer.setApproveUser(getUsername());
        fundTransfer.setApproveTime(new java.util.Date());

        return toAjax(fundTransferService.updateFundTransfer(fundTransfer));
    }

    /**
     * 审批拒绝资金划拨
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:reject')")
    @Log(title = "资金划拨审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/reject/{transferId}")
    public AjaxResult reject(@PathVariable Long transferId, @RequestBody FundTransfer fundTransfer)
    {
        FundTransfer existing = fundTransferService.selectFundTransferByTransferId(transferId);
        if (existing == null) {
            return AjaxResult.error("划拨记录不存在");
        }

        if (!"0".equals(existing.getTransferStatus())) {
            return AjaxResult.error("只能审批待处理状态的划拨");
        }

        // 更新状态为失败
        existing.setTransferStatus("2");
        existing.setApproveUser(getUsername());
        existing.setApproveTime(new java.util.Date());
        existing.setRemark(fundTransfer.getRemark()); // 拒绝原因

        return toAjax(fundTransferService.updateFundTransfer(existing));
    }

    /**
     * 获取审批统计信息
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:query')")
    @GetMapping("/approval/statistics")
    public AjaxResult getApprovalStatistics()
    {
        FundTransfer query = new FundTransfer();

        // 待处理数量
        query.setTransferStatus("0");
        int pendingCount = fundTransferService.selectFundTransferList(query).size();

        // 成功数量
        query.setTransferStatus("1");
        int approvedCount = fundTransferService.selectFundTransferList(query).size();

        // 失败数量
        query.setTransferStatus("2");
        int rejectedCount = fundTransferService.selectFundTransferList(query).size();

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
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:approve')")
    @Log(title = "资金划拨审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approval/batchApprove")
    public AjaxResult batchApprove(@RequestBody Long[] transferIds)
    {
        int successCount = 0;
        int failCount = 0;

        for (Long transferId : transferIds) {
            try {
                FundTransfer fundTransfer = fundTransferService.selectFundTransferByTransferId(transferId);
                if (fundTransfer != null && "0".equals(fundTransfer.getTransferStatus())) {
                    fundTransfer.setTransferStatus("1");
                    fundTransfer.setApproveUser(getUsername());
                    fundTransfer.setApproveTime(new java.util.Date());
                    fundTransferService.updateFundTransfer(fundTransfer);
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
     * 获取今日待处理划拨
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:query')")
    @GetMapping("/approval/todayPending")
    public AjaxResult getTodayPending()
    {
        FundTransfer query = new FundTransfer();
        query.setTransferStatus("0");
        List<FundTransfer> allPending = fundTransferService.selectFundTransferList(query);

        // 筛选今日的划拨记录
        List<FundTransfer> todayPending = allPending.stream()
            .filter(transfer -> {
                java.util.Date createTime = transfer.getCreateTime();
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
     * 获取紧急划拨（金额较大的）
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:query')")
    @GetMapping("/approval/urgent")
    public AjaxResult getUrgentTransfers()
    {
        FundTransfer query = new FundTransfer();
        query.setTransferStatus("0");
        List<FundTransfer> allPending = fundTransferService.selectFundTransferList(query);

        // 筛选金额大于50000的划拨记录
        List<FundTransfer> urgentTransfers = allPending.stream()
            .filter(transfer -> {
                if (transfer.getTransferAmount() != null) {
                    return transfer.getTransferAmount().doubleValue() > 50000.0;
                }
                return false;
            })
            .collect(java.util.stream.Collectors.toList());

        return AjaxResult.success(urgentTransfers);
    }
}