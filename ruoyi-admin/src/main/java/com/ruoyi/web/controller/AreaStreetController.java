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
import com.ruoyi.domain.AreaStreet;
import com.ruoyi.service.IAreaStreetService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 区域街道信息Controller
 *
 * @author ruoyi
 * @date 2025-12-04
 */
@RestController
@RequestMapping("/pension/area")
public class AreaStreetController extends BaseController
{
    @Autowired
    private IAreaStreetService areaStreetService;

    /**
     * 查询区域街道信息列表
     */
    @PreAuthorize("@ss.hasPermi('pension:area:list')")
    @GetMapping("/list")
    public TableDataInfo list(AreaStreet areaStreet)
    {
        startPage();
        List<AreaStreet> list = areaStreetService.selectAreaStreetList(areaStreet);
        return getDataTable(list);
    }

    /**
     * 查询所有区域列表
     */
    @GetMapping("/areas")
    public AjaxResult listAreas()
    {
        List<AreaStreet> areas = areaStreetService.selectAllAreas();
        return AjaxResult.success(areas);
    }

    /**
     * 根据区域获取街道列表
     */
    @GetMapping("/{areaCode}/streets")
    public AjaxResult listStreetsByArea(@PathVariable String areaCode)
    {
        List<AreaStreet> streets = areaStreetService.selectStreetsByAreaCode(areaCode);
        return AjaxResult.success(streets);
    }

    /**
     * 导出区域街道信息列表
     */
    @PreAuthorize("@ss.hasPermi('pension:area:export')")
    @Log(title = "区域街道信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AreaStreet areaStreet)
    {
        List<AreaStreet> list = areaStreetService.selectAreaStreetList(areaStreet);
        ExcelUtil<AreaStreet> util = new ExcelUtil<AreaStreet>(AreaStreet.class);
        util.exportExcel(response, list, "区域街道信息数据");
    }

    /**
     * 获取区域街道信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:area:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(areaStreetService.selectAreaStreetById(id));
    }

    /**
     * 新增区域街道信息
     */
    @PreAuthorize("@ss.hasPermi('pension:area:add')")
    @Log(title = "区域街道信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AreaStreet areaStreet)
    {
        return toAjax(areaStreetService.insertAreaStreet(areaStreet));
    }

    /**
     * 修改区域街道信息
     */
    @PreAuthorize("@ss.hasPermi('pension:area:edit')")
    @Log(title = "区域街道信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AreaStreet areaStreet)
    {
        return toAjax(areaStreetService.updateAreaStreet(areaStreet));
    }

    /**
     * 删除区域街道信息
     */
    @PreAuthorize("@ss.hasPermi('pension:area:remove')")
    @Log(title = "区域街道信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(areaStreetService.deleteAreaStreetByIds(ids));
    }

    /**
     * 导入区域街道数据
     */
    @PostMapping("/import")
    public AjaxResult importData(@RequestBody List<AreaStreet> areaStreetList)
    {
        try
        {
            String message = areaStreetService.importAreaStreet(areaStreetList);
            return AjaxResult.success(message);
        }
        catch (Exception e)
        {
            return AjaxResult.error(e.getMessage());
        }
    }
}