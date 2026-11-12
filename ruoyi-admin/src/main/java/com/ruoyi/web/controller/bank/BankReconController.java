package com.ruoyi.web.controller.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.domain.bank.BankReconDiff;
import com.ruoyi.domain.bank.BankReconRun;
import com.ruoyi.mapper.bank.BankReconMapper;
import com.ruoyi.service.bank.IBankMerchantConfigService;
import com.ruoyi.service.bank.impl.BankAcqReconService;

/** 收单对账：批次查询、差异查询、手动执行（可带文件名直接下载，沙箱人工放文件场景）。 */
@RestController
@RequestMapping("/bank/recon")
public class BankReconController extends BaseController
{
    @Autowired private BankReconMapper reconMapper;
    @Autowired private BankAcqReconService reconService;
    @Autowired private IBankMerchantConfigService merchantConfigService;

    @PreAuthorize("@ss.hasPermi('bank:recon:list')")
    @GetMapping("/runs")
    public AjaxResult runs(@RequestParam(defaultValue = "20") int limit)
    {
        List<String> merIds = accessibleMerchantIds();
        if (merIds != null && merIds.isEmpty())
        {
            return AjaxResult.success(Collections.emptyList());
        }
        return AjaxResult.success(reconMapper.selectRuns(merIds, Math.min(limit, 100)));
    }

    /** 执行对账时可选的商户：管理员/监管员看全部启用商户，机构用户只看本机构。 */
    @PreAuthorize("@ss.hasPermi('bank:recon:list')")
    @GetMapping("/merchants")
    public AjaxResult merchants()
    {
        List<Map<String, Object>> result = new ArrayList<>();
        for (BankMerchantConfig merchant : accessibleEnabledMerchants())
        {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("merId", merchant.getMerId());
            item.put("institutionId", merchant.getInstitutionId());
            item.put("institutionName", merchant.getInstitutionName());
            item.put("environment", merchant.getEnvironment());
            result.add(item);
        }
        return AjaxResult.success(result);
    }

    @PreAuthorize("@ss.hasPermi('bank:recon:list')")
    @GetMapping("/diffs")
    public AjaxResult diffs(@RequestParam Long runId)
    {
        BankReconRun run = reconMapper.selectRun(runId);
        if (run == null)
        {
            return AjaxResult.error("对账批次不存在");
        }
        if (!canAccessMerchant(run.getMerId()))
        {
            return AjaxResult.error("无权查看该批次");
        }
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
            Long id = Long.parseLong(diffId.trim());
            BankReconDiff diff = reconMapper.selectDiff(id);
            if (diff == null)
            {
                return AjaxResult.error("差异不存在或已处理");
            }
            if (!canViewAllMerchants())
            {
                BankReconRun run = reconMapper.selectRun(diff.getRunId());
                if (run == null || !canAccessMerchant(run.getMerId()))
                {
                    return AjaxResult.error("无权处理该差异");
                }
            }
            int updated = reconMapper.markDiffHandled(id, remark.trim());
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
        String targetMerId = merId.trim();
        boolean allowed = accessibleEnabledMerchants().stream()
                .anyMatch(merchant -> targetMerId.equals(merchant.getMerId()));
        if (!allowed)
        {
            return AjaxResult.error("商户号不在当前用户可对账范围内，或商户未启用/未验证");
        }
        try
        {
            BankReconRun run = reconService.runRecon(targetMerId, clearingDate.trim(), fileName, getUsername());
            return AjaxResult.success(run);
        }
        catch (Exception e)
        {
            return AjaxResult.error("对账执行失败：" + e.getMessage());
        }
    }

    /** 管理员和监管员看全局；机构用户只看 sys_user_institution 关联的商户。 */
    private boolean canViewAllMerchants()
    {
        return SecurityUtils.isAdmin(getUserId())
                || SecurityUtils.hasPermi("pension:fundTransferApply:supervisionApprove")
                || SecurityUtils.hasPermi("pension:bank:supervision:list");
    }

    private List<BankMerchantConfig> accessibleEnabledMerchants()
    {
        BankMerchantConfig query = new BankMerchantConfig();
        query.setStatus("1");
        query.setVerifyStatus("1");
        if (!canViewAllMerchants())
        {
            query.setCurrentUserId(getUserId());
        }
        return merchantConfigService.selectList(query);
    }

    /** 历史批次可见范围：机构用户包含已停用商户，避免旧批次消失；null 表示不限。 */
    private List<String> accessibleMerchantIds()
    {
        if (canViewAllMerchants())
        {
            return null;
        }
        BankMerchantConfig query = new BankMerchantConfig();
        query.setCurrentUserId(getUserId());
        return merchantConfigService.selectList(query).stream()
                .map(BankMerchantConfig::getMerId)
                .filter(merId -> merId != null && !merId.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean canAccessMerchant(String merId)
    {
        if (canViewAllMerchants())
        {
            return true;
        }
        List<String> merIds = accessibleMerchantIds();
        return merIds != null && merIds.contains(merId);
    }
}
