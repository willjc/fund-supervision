package com.ruoyi.web.controller.bank;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.service.IPensionInstitutionService;
import com.ruoyi.service.bank.IBankMerchantConfigService;

@RestController
@RequestMapping("/supervision/account/bank-merchant")
public class BankMerchantConfigController extends BaseController
{
    @Autowired
    private IBankMerchantConfigService merchantConfigService;

    @Autowired
    private IPensionInstitutionService institutionService;

    @PreAuthorize("@ss.hasPermi('supervision:account:supervision')")
    @GetMapping("/list")
    public TableDataInfo list(BankMerchantConfig config)
    {
        startPage();
        applyDataScope(config);
        return getDataTable(merchantConfigService.selectList(config));
    }

    @PreAuthorize("@ss.hasPermi('supervision:account:supervision')")
    @GetMapping("/{configId}")
    public AjaxResult detail(@PathVariable Long configId)
    {
        BankMerchantConfig config = findAccessible(configId);
        return config == null ? AjaxResult.error("商户配置不存在或无权查看") : AjaxResult.success(config);
    }

    @PreAuthorize("@ss.hasPermi('supervision:account:supervision')")
    @Log(title = "新增银行商户号绑定", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BankMerchantConfig config)
    {
        if (!hasInstitutionAccess(config.getInstitutionId()))
        {
            return AjaxResult.error("养老机构不存在或无权配置");
        }
        return toAjax(merchantConfigService.insert(config, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('supervision:account:supervision')")
    @Log(title = "修改银行商户号绑定", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BankMerchantConfig config)
    {
        if (config.getConfigId() == null || findAccessible(config.getConfigId()) == null)
        {
            return AjaxResult.error("商户配置不存在或无权修改");
        }
        if (!hasInstitutionAccess(config.getInstitutionId()))
        {
            return AjaxResult.error("养老机构不存在或无权配置");
        }
        return toAjax(merchantConfigService.update(config, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('supervision:account:supervision')")
    @Log(title = "删除银行商户号绑定", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configId}")
    public AjaxResult remove(@PathVariable Long configId)
    {
        if (findAccessible(configId) == null)
        {
            return AjaxResult.error("商户配置不存在或无权删除");
        }
        return toAjax(merchantConfigService.delete(configId));
    }

    private BankMerchantConfig findAccessible(Long configId)
    {
        BankMerchantConfig query = new BankMerchantConfig();
        query.setConfigId(configId);
        applyDataScope(query);
        List<BankMerchantConfig> records = merchantConfigService.selectList(query);
        return records.isEmpty() ? null : records.get(0);
    }

    private void applyDataScope(BankMerchantConfig config)
    {
        if (!Long.valueOf(1L).equals(getUserId()))
        {
            config.setCurrentUserId(getUserId());
        }
    }

    private boolean hasInstitutionAccess(Long institutionId)
    {
        if (institutionId == null)
        {
            return false;
        }
        PensionInstitution query = new PensionInstitution();
        query.setInstitutionId(institutionId);
        if (!Long.valueOf(1L).equals(getUserId()))
        {
            query.setCurrentUserId(getUserId());
        }
        return !institutionService.selectPensionInstitutionList(query).isEmpty();
    }
}
