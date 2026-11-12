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
import org.springframework.web.bind.annotation.PathVariable;
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
import com.ruoyi.domain.bank.BankSupervisionReconRun;
import com.ruoyi.service.bank.IBankMerchantConfigService;
import com.ruoyi.service.bank.impl.BankSupervisionReconService;

/** 监管对账（706）：账号列表、批次、执行、重新获取文件；差异解析待银行样例。 */
@RestController
@RequestMapping("/bank/supervision/recon")
public class BankSupervisionReconController extends BaseController
{
    @Autowired
    private BankSupervisionReconService reconService;

    @Autowired
    private IBankMerchantConfigService merchantConfigService;

    @PreAuthorize("@ss.hasPermi('bank:recon:list')")
    @GetMapping("/accounts")
    public AjaxResult accounts()
    {
        List<Map<String, Object>> result = new ArrayList<>();
        for (BankMerchantConfig account : accessibleAccounts())
        {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("institutionId", account.getInstitutionId());
            item.put("institutionName", account.getInstitutionName());
            item.put("accountNo", account.getSettlementAccountNo());
            item.put("accountName", account.getSettlementAccountName());
            item.put("merId", account.getMerId());
            item.put("environment", account.getEnvironment());
            result.add(item);
        }
        return AjaxResult.success(result);
    }

    @PreAuthorize("@ss.hasPermi('bank:recon:list')")
    @GetMapping("/runs")
    public AjaxResult runs(@RequestParam(defaultValue = "20") int limit)
    {
        List<Long> institutionIds = accessibleInstitutionIds();
        if (institutionIds != null && institutionIds.isEmpty())
        {
            return AjaxResult.success(Collections.emptyList());
        }
        return AjaxResult.success(reconService.listRuns(institutionIds, Math.min(limit, 100)));
    }

    @PreAuthorize("@ss.hasPermi('bank:recon:run')")
    @Log(title = "监管对账执行", businessType = BusinessType.OTHER)
    @PostMapping("/run")
    public AjaxResult run(@RequestBody Map<String, String> params)
    {
        String institutionIdText = params.get("institutionId");
        String accountNo = params.get("accountNo");
        String trandt = params.get("trandt");
        String fileName = params.get("fileName");
        if (institutionIdText == null || accountNo == null || accountNo.trim().isEmpty()
                || trandt == null || trandt.trim().isEmpty())
        {
            return AjaxResult.error("机构、监管账号与对账日期必填");
        }
        Long institutionId;
        try
        {
            institutionId = Long.valueOf(institutionIdText.trim());
        }
        catch (NumberFormatException e)
        {
            return AjaxResult.error("机构参数不合法");
        }
        BankMerchantConfig account = accessibleAccounts().stream()
                .filter(item -> institutionId.equals(item.getInstitutionId())
                        && accountNo.trim().equals(item.getSettlementAccountNo()))
                .findFirst().orElse(null);
        if (account == null)
        {
            return AjaxResult.error("监管账号不在当前用户可对账范围内，或账户未启用/未验证");
        }
        try
        {
            BankSupervisionReconRun run = reconService.run(account, trandt.trim(), fileName, getUsername());
            return AjaxResult.success(run);
        }
        catch (Exception e)
        {
            return AjaxResult.error("监管对账执行失败：" + e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('bank:recon:run')")
    @Log(title = "监管对账重新获取文件", businessType = BusinessType.OTHER)
    @PostMapping("/runs/{runId}/retry")
    public AjaxResult retry(@PathVariable Long runId,
            @RequestBody(required = false) Map<String, String> params)
    {
        BankSupervisionReconRun run = reconService.getRun(runId);
        if (run == null)
        {
            return AjaxResult.error("对账批次不存在");
        }
        if (!canAccessInstitution(run.getInstitutionId()))
        {
            return AjaxResult.error("无权操作该批次");
        }
        String fileName = params == null ? null : params.get("fileName");
        try
        {
            return AjaxResult.success(reconService.retry(run, fileName, getUsername()));
        }
        catch (Exception e)
        {
            return AjaxResult.error("重新获取文件失败：" + e.getMessage());
        }
    }

    @PreAuthorize("@ss.hasPermi('bank:recon:run')")
    @Log(title = "监管对账探针", businessType = BusinessType.OTHER)
    @PostMapping("/probe")
    public AjaxResult probe(@RequestBody Map<String, String> params)
    {
        String trandt = params.get("trandt");
        if (trandt == null || trandt.trim().isEmpty())
        {
            return AjaxResult.error("对账日期必填");
        }
        try
        {
            return AjaxResult.success(reconService.probe(trandt.trim()));
        }
        catch (Exception e)
        {
            return AjaxResult.error("监管对账探针失败：" + e.getMessage());
        }
    }

    private boolean canViewAll()
    {
        return SecurityUtils.isAdmin(getUserId())
                || SecurityUtils.hasPermi("pension:fundTransferApply:supervisionApprove")
                || SecurityUtils.hasPermi("pension:bank:supervision:list");
    }

    private List<BankMerchantConfig> accessibleAccounts()
    {
        BankMerchantConfig query = new BankMerchantConfig();
        query.setStatus("1");
        query.setVerifyStatus("1");
        if (!canViewAll())
        {
            query.setCurrentUserId(getUserId());
        }
        return merchantConfigService.selectList(query).stream()
                .filter(item -> item.getSettlementAccountNo() != null
                        && !item.getSettlementAccountNo().trim().isEmpty())
                .collect(Collectors.toList());
    }

    /** 历史批次可见范围包含已停用账户，避免旧批次消失；null 表示不限。 */
    private List<Long> accessibleInstitutionIds()
    {
        if (canViewAll())
        {
            return null;
        }
        BankMerchantConfig query = new BankMerchantConfig();
        query.setCurrentUserId(getUserId());
        return merchantConfigService.selectList(query).stream()
                .map(BankMerchantConfig::getInstitutionId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean canAccessInstitution(Long institutionId)
    {
        if (canViewAll())
        {
            return true;
        }
        List<Long> ids = accessibleInstitutionIds();
        return ids != null && ids.contains(institutionId);
    }
}
