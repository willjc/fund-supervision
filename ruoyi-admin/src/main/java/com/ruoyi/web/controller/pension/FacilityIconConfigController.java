package com.ruoyi.web.controller.pension;

import java.util.List;
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
import com.ruoyi.domain.FacilityIconConfig;
import com.ruoyi.service.IFacilityIconConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 设施图标配置Controller
 *
 * @author ruoyi
 * @date 2025-01-07
 */
@RestController
@RequestMapping("/pension/facility/icon")
public class FacilityIconConfigController extends BaseController
{
    @Autowired
    private IFacilityIconConfigService facilityIconConfigService;

    /**
     * 查询设施图标配置列表
     */
    @PreAuthorize("@ss.hasPermi('pension:facility:icon:list')")
    @GetMapping("/list")
    public TableDataInfo list(FacilityIconConfig facilityIconConfig)
    {
        startPage();
        List<FacilityIconConfig> list = facilityIconConfigService.selectFacilityIconConfigList(facilityIconConfig);
        return getDataTable(list);
    }

    /**
     * 导出设施图标配置列表
     */
    @PreAuthorize("@ss.hasPermi('pension:facility:icon:export')")
    @Log(title = "设施图标配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FacilityIconConfig facilityIconConfig)
    {
        List<FacilityIconConfig> list = facilityIconConfigService.selectFacilityIconConfigList(facilityIconConfig);
        ExcelUtil<FacilityIconConfig> util = new ExcelUtil<FacilityIconConfig>(FacilityIconConfig.class);
        util.exportExcel(response, list, "设施图标配置数据");
    }

    /**
     * 获取设施图标配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:facility:icon:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(facilityIconConfigService.selectFacilityIconConfigById(id));
    }

    /**
     * 新增设施图标配置
     */
    @PreAuthorize("@ss.hasPermi('pension:facility:icon:add')")
    @Log(title = "设施图标配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FacilityIconConfig facilityIconConfig)
    {
        return toAjax(facilityIconConfigService.insertFacilityIconConfig(facilityIconConfig));
    }

    /**
     * 修改设施图标配置
     */
    @PreAuthorize("@ss.hasPermi('pension:facility:icon:edit')")
    @Log(title = "设施图标配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FacilityIconConfig facilityIconConfig)
    {
        return toAjax(facilityIconConfigService.updateFacilityIconConfig(facilityIconConfig));
    }

    /**
     * 删除设施图标配置
     */
    @PreAuthorize("@ss.hasPermi('pension:facility:icon:remove')")
    @Log(title = "设施图标配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(facilityIconConfigService.deleteFacilityIconConfigByIds(ids));
    }

    /**
     * 获取生活设施图标配置（用于H5）
     */
    @GetMapping("/life")
    public AjaxResult getLifeFacilities()
    {
        List<FacilityIconConfig> list = facilityIconConfigService.selectByType("life");
        return AjaxResult.success(list);
    }

    /**
     * 获取医疗设施图标配置（用于H5）
     */
    @GetMapping("/medical")
    public AjaxResult getMedicalFacilities()
    {
        List<FacilityIconConfig> list = facilityIconConfigService.selectByType("medical");
        return AjaxResult.success(list);
    }
}