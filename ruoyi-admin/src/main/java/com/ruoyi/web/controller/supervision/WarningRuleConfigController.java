package com.ruoyi.web.controller.supervision;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.domain.pension.WarningRuleConfig;
import com.ruoyi.service.pension.IWarningRuleConfigService;

/**
 * 预警规则配置Controller
 *
 * @author ruoyi
 * @date 2025-01-29
 */
@RestController
@RequestMapping("/supervision/warningConfig")
public class WarningRuleConfigController extends BaseController
{
    @Autowired
    private IWarningRuleConfigService warningRuleConfigService;

    /**
     * 查询预警规则配置列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:warningConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(WarningRuleConfig warningRuleConfig)
    {
        startPage();
        List<WarningRuleConfig> list = warningRuleConfigService.selectWarningRuleConfigList(warningRuleConfig);
        return getDataTable(list);
    }

    /**
     * 获取预警规则配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('supervision:warningConfig:query')")
    @GetMapping(value = "/{ruleId}")
    public AjaxResult getInfo(@PathVariable("ruleId") Long ruleId)
    {
        return success(warningRuleConfigService.selectWarningRuleConfigByRuleId(ruleId));
    }

    /**
     * 根据规则编码获取配置
     */
    @GetMapping(value = "/code/{ruleCode}")
    public AjaxResult getInfoByCode(@PathVariable("ruleCode") String ruleCode)
    {
        return success(warningRuleConfigService.selectWarningRuleConfigByRuleCode(ruleCode));
    }

    /**
     * 新增预警规则配置
     */
    @PreAuthorize("@ss.hasPermi('supervision:warningConfig:add')")
    @Log(title = "预警规则配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WarningRuleConfig warningRuleConfig)
    {
        return toAjax(warningRuleConfigService.insertWarningRuleConfig(warningRuleConfig));
    }

    /**
     * 修改预警规则配置
     */
    @PreAuthorize("@ss.hasPermi('supervision:warningConfig:edit')")
    @Log(title = "预警规则配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WarningRuleConfig warningRuleConfig)
    {
        return toAjax(warningRuleConfigService.updateWarningRuleConfig(warningRuleConfig));
    }

    /**
     * 批量修改预警规则配置
     */
    @PreAuthorize("@ss.hasPermi('supervision:warningConfig:edit')")
    @Log(title = "预警规则配置", businessType = BusinessType.UPDATE)
    @PutMapping("/batch")
    public AjaxResult batchEdit(@RequestBody List<WarningRuleConfig> configList)
    {
        return toAjax(warningRuleConfigService.batchUpdateWarningRuleConfig(configList));
    }

    /**
     * 删除预警规则配置
     */
    @PreAuthorize("@ss.hasPermi('supervision:warningConfig:remove')")
    @Log(title = "预警规则配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds)
    {
        return toAjax(warningRuleConfigService.deleteWarningRuleConfigByRuleIds(ruleIds));
    }

    /**
     * 导出预警规则配置列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:warningConfig:export')")
    @Log(title = "预警规则配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WarningRuleConfig warningRuleConfig)
    {
        List<WarningRuleConfig> list = warningRuleConfigService.selectWarningRuleConfigList(warningRuleConfig);
        ExcelUtil<WarningRuleConfig> util = new ExcelUtil<>(WarningRuleConfig.class);
        util.exportExcel(response, list, "预警规则配置数据");
    }
}
