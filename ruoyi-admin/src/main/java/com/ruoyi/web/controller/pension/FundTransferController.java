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
 * 资金划拨管理Controller
 *
 * @author ruoyi
 * @date 2025-01-25
 */
@RestController
@RequestMapping("/pension/fundTransfer")
public class FundTransferController extends BaseController
{
    @Autowired
    private IFundTransferService fundTransferService;

    /**
     * 查询资金划拨列表
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:list')")
    @GetMapping("/list")
    public TableDataInfo list(FundTransfer fundTransfer)
    {
        startPage();
        List<FundTransfer> list = fundTransferService.selectFundTransferList(fundTransfer);
        return getDataTable(list);
    }

    /**
     * 导出资金划拨列表
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:export')")
    @Log(title = "资金划拨", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FundTransfer fundTransfer)
    {
        List<FundTransfer> list = fundTransferService.selectFundTransferList(fundTransfer);
        ExcelUtil<FundTransfer> util = new ExcelUtil<FundTransfer>(FundTransfer.class);
        util.exportExcel(response, list, "资金划拨数据");
    }

    /**
     * 获取资金划拨详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:query')")
    @GetMapping(value = "/{transferId}")
    public AjaxResult getInfo(@PathVariable("transferId") Long transferId)
    {
        return success(fundTransferService.selectFundTransferByTransferId(transferId));
    }

    /**
     * 生成月度划拨单
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:generate')")
    @Log(title = "生成划拨单", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody Map<String, Object> params)
    {
        try {
            Long institutionId = 1L; // 这里应该从登录用户获取机构ID
            String transferPeriod = (String) params.get("transferPeriod");
            String remark = (String) params.get("remark");

            FundTransfer fundTransfer = fundTransferService.generateMonthlyTransfer(institutionId, transferPeriod);
            if (fundTransfer != null) {
                return success(fundTransfer);
            } else {
                return error("生成划拨单失败，可能没有符合条件的账户");
            }
        } catch (Exception e) {
            return error("生成划拨单失败：" + e.getMessage());
        }
    }

    /**
     * 审批划拨单
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:approve')")
    @Log(title = "审批划拨单", businessType = BusinessType.UPDATE)
    @PutMapping("/approve")
    public AjaxResult approve(@RequestBody Map<String, Object> params)
    {
        try {
            Long transferId = Long.parseLong(params.get("transferId").toString());
            String approvalBy = getUsername();
            String approvalStatus = (String) params.get("approvalStatus");
            String approvalRemark = (String) params.get("approvalRemark");

            int result = fundTransferService.approveFundTransfer(transferId, approvalBy, approvalStatus, approvalRemark);
            return toAjax(result);
        } catch (Exception e) {
            return error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 执行划拨
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:execute')")
    @Log(title = "执行划拨", businessType = BusinessType.UPDATE)
    @PutMapping("/execute/{transferId}")
    public AjaxResult execute(@PathVariable Long transferId)
    {
        try {
            String executeBy = getUsername();
            int result = fundTransferService.executeFundTransfer(transferId, executeBy);
            return toAjax(result);
        } catch (Exception e) {
            return error("执行划拨失败：" + e.getMessage());
        }
    }

    /**
     * 新增资金划拨
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:add')")
    @Log(title = "资金划拨", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FundTransfer fundTransfer)
    {
        return toAjax(fundTransferService.insertFundTransfer(fundTransfer));
    }

    /**
     * 修改资金划拨
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:edit')")
    @Log(title = "资金划拨", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FundTransfer fundTransfer)
    {
        return toAjax(fundTransferService.updateFundTransfer(fundTransfer));
    }

    /**
     * 删除资金划拨
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:remove')")
    @Log(title = "资金划拨", businessType = BusinessType.DELETE)
	@DeleteMapping("/{transferIds}")
    public AjaxResult remove(@PathVariable Long[] transferIds)
    {
        return toAjax(fundTransferService.deleteFundTransferByTransferIds(transferIds));
    }

    /**
     * 根据机构ID查询划拨记录
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:query')")
    @GetMapping("/institution/{institutionId}")
    public AjaxResult getByInstitution(@PathVariable Long institutionId)
    {
        List<FundTransfer> list = fundTransferService.selectFundTransferByInstitutionId(institutionId);
        return success(list);
    }

    /**
     * 查询待处理的划拨记录
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:query')")
    @GetMapping("/pending")
    public AjaxResult getPending()
    {
        List<FundTransfer> list = fundTransferService.selectPendingTransfers();
        return success(list);
    }

    /**
     * 执行自动划拨
     */
    @PreAuthorize("@ss.hasPermi('pension:fundTransfer:execute')")
    @Log(title = "自动划拨", businessType = BusinessType.UPDATE)
    @PostMapping("/auto")
    public AjaxResult autoTransfer()
    {
        try {
            Map<String, Object> result = fundTransferService.executeAutoTransfer();
            return success(result);
        } catch (Exception e) {
            return error("自动划拨失败：" + e.getMessage());
        }
    }

}
