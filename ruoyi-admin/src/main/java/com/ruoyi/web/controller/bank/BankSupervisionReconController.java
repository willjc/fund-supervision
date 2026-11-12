package com.ruoyi.web.controller.bank;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.service.bank.impl.BankSupervisionReconService;

/** 监管对账通道探针：706 取文件名并下载文件，当前只返回文件元数据，不解析、不比对。 */
@RestController
@RequestMapping("/bank/supervision/recon")
public class BankSupervisionReconController extends BaseController
{
    @Autowired
    private BankSupervisionReconService reconService;

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
}
