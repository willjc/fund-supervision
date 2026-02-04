package com.ruoyi.web.controller;

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
import com.ruoyi.domain.pension.MealFeeConfig;
import com.ruoyi.service.IMealFeeConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 餐费配置Controller
 *
 * @author ruoyi
 * @date 2026-02-04
 */
@RestController
@RequestMapping("/elder/meal")
public class MealFeeConfigController extends BaseController
{
    @Autowired
    private IMealFeeConfigService mealFeeConfigService;

    /**
     * 查询餐费配置列表
     */
    @PreAuthorize("@ss.hasPermi('elder:meal:list')")
    @GetMapping("/list")
    public TableDataInfo list(MealFeeConfig mealFeeConfig)
    {
        startPage();
        // 数据权限过滤: 只查询当前用户关联的机构的餐费配置
        mealFeeConfig.setCurrentUserId(getUserId());
        List<MealFeeConfig> list = mealFeeConfigService.selectMealFeeConfigList(mealFeeConfig);
        return getDataTable(list);
    }

    /**
     * 根据机构ID查询启用的餐费配置列表
     */
    @PreAuthorize("@ss.hasPermi('elder:meal:query')")
    @GetMapping("/available/{institutionId}")
    public AjaxResult availableMeals(@PathVariable("institutionId") Long institutionId)
    {
        List<MealFeeConfig> list = mealFeeConfigService.selectAvailableMealsByInstitutionId(institutionId);
        return AjaxResult.success(list);
    }

    /**
     * 导出餐费配置列表
     */
    @PreAuthorize("@ss.hasPermi('elder:meal:export')")
    @Log(title = "餐费配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MealFeeConfig mealFeeConfig)
    {
        // 数据权限过滤: 只导出当前用户有权限的机构的餐费配置
        mealFeeConfig.setCurrentUserId(getUserId());
        List<MealFeeConfig> list = mealFeeConfigService.selectMealFeeConfigList(mealFeeConfig);
        ExcelUtil<MealFeeConfig> util = new ExcelUtil<MealFeeConfig>(MealFeeConfig.class);
        util.exportExcel(response, list, "餐费配置数据");
    }

    /**
     * 获取餐费配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('elder:meal:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable("configId") Long configId)
    {
        return AjaxResult.success(mealFeeConfigService.selectMealFeeConfigByConfigId(configId));
    }

    /**
     * 新增餐费配置
     */
    @PreAuthorize("@ss.hasPermi('elder:meal:add')")
    @Log(title = "餐费配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MealFeeConfig mealFeeConfig)
    {
        return toAjax(mealFeeConfigService.insertMealFeeConfig(mealFeeConfig));
    }

    /**
     * 修改餐费配置
     */
    @PreAuthorize("@ss.hasPermi('elder:meal:edit')")
    @Log(title = "餐费配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MealFeeConfig mealFeeConfig)
    {
        return toAjax(mealFeeConfigService.updateMealFeeConfig(mealFeeConfig));
    }

    /**
     * 删除餐费配置
     */
    @PreAuthorize("@ss.hasPermi('elder:meal:remove')")
    @Log(title = "餐费配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        return toAjax(mealFeeConfigService.deleteMealFeeConfigByConfigIds(configIds));
    }
}
