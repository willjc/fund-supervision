package com.ruoyi.web.controller.bank;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.bank.BankReconRun;
import com.ruoyi.mapper.bank.BankReconMapper;
import com.ruoyi.service.bank.impl.BankAcqReconService;

/** 收单对账：批次查询、差异查询、手动执行（可带文件名直接下载，沙箱人工放文件场景）。 */
@RestController
@RequestMapping("/bank/recon")
public class BankReconController extends BaseController
{
    @Autowired private BankReconMapper reconMapper;
    @Autowired private BankAcqReconService reconService;

    @PreAuthorize("@ss.hasPermi('bank:recon:list')")
    @GetMapping("/runs")
    public AjaxResult runs(@RequestParam(defaultValue = "20") int limit)
    {
        return AjaxResult.success(reconMapper.selectRuns(Math.min(limit, 100)));
    }

    @PreAuthorize("@ss.hasPermi('bank:recon:list')")
    @GetMapping("/diffs")
    public AjaxResult diffs(@RequestParam Long runId)
    {
        return AjaxResult.success(reconMapper.selectDiffs(runId));
    }

    /** 标记差异已处理：备注必填留审计（0→1 幂等）。 */
    @PreAuthorize("@ss.hasPermi('bank:recon:handle')")
    @Log(title = "收单对账差异处理", businessType = BusinessType.UPDATE)
    @PostMapping("/diffs/handle")
    public AjaxResult handleDiff(@RequestBody Map<String, String> params)
    {
        String diffId = params.get("diffId");
        String remark = params.get("remark");
        if (diffId == null || diffId.trim().isEmpty())
        {
            return AjaxResult.error("差异记录必填");
        }
        if (remark == null || remark.trim().isEmpty())
        {
            return AjaxResult.error("处理说明必填");
        }
        try
        {
            int updated = reconMapper.markDiffHandled(Long.parseLong(diffId.trim()), remark.trim());
            return updated == 1 ? AjaxResult.success() : AjaxResult.error("差异不存在或已处理");
        }
        catch (NumberFormatException e)
        {
            return AjaxResult.error("差异记录不合法");
        }
    }

    @PreAuthorize("@ss.hasPermi('bank:recon:run')")
    @Log(title = "收单对账执行", businessType = BusinessType.OTHER)
    @PostMapping("/run")
    public AjaxResult run(@RequestBody Map<String, String> params)
    {
        String merId = params.get("merId");
        String clearingDate = params.get("clearingDate");
        String fileName = params.get("fileName");
        if (merId == null || merId.trim().isEmpty() || clearingDate == null || clearingDate.trim().isEmpty())
        {
            return AjaxResult.error("商户号与清算日期必填");
        }
        try
        {
            BankReconRun run = reconService.runRecon(merId.trim(), clearingDate.trim(), fileName, getUsername());
            return AjaxResult.success(run);
        }
        catch (Exception e)
        {
            return AjaxResult.error("对账执行失败：" + e.getMessage());
        }
    }
}
