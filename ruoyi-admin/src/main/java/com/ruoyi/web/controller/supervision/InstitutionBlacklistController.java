package com.ruoyi.web.controller.supervision;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.domain.InstitutionBlacklist;
import com.ruoyi.service.IInstitutionBlacklistService;

/**
 * 机构黑名单Controller
 *
 * @author ruoyi
 * @date 2025-01-31
 */
@RestController
@RequestMapping("/supervision/institution/blacklist")
public class InstitutionBlacklistController extends BaseController
{
    @Autowired
    private IInstitutionBlacklistService institutionBlacklistService;

    /**
     * 查询机构黑名单列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:blacklist:list')")
    @GetMapping("/list")
    public TableDataInfo list(InstitutionBlacklist institutionBlacklist)
    {
        startPage();
        List<InstitutionBlacklist> list = institutionBlacklistService.selectInstitutionBlacklistList(institutionBlacklist);
        return getDataTable(list);
    }

    /**
     * 导出机构黑名单列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:blacklist:export')")
    @Log(title = "机构黑名单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InstitutionBlacklist institutionBlacklist)
    {
        List<InstitutionBlacklist> list = institutionBlacklistService.selectInstitutionBlacklistList(institutionBlacklist);
        ExcelUtil<InstitutionBlacklist> util = new ExcelUtil<InstitutionBlacklist>(InstitutionBlacklist.class);
        util.exportExcel(response, list, "机构黑名单数据");
    }

    /**
     * 获取机构黑名单详细信息
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:blacklist:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(institutionBlacklistService.selectInstitutionBlacklistById(id));
    }

    /**
     * 根据机构ID获取黑名单信息
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:blacklist:query')")
    @GetMapping(value = "/institution/{institutionId}")
    public AjaxResult getByInstitutionId(@PathVariable("institutionId") Long institutionId)
    {
        return success(institutionBlacklistService.selectByInstitutionId(institutionId));
    }

    /**
     * 新增机构黑名单
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:blacklist:add')")
    @Log(title = "机构黑名单", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody InstitutionBlacklist institutionBlacklist)
    {
        return toAjax(institutionBlacklistService.insertInstitutionBlacklist(institutionBlacklist));
    }

    /**
     * 修改机构黑名单
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:blacklist:edit')")
    @Log(title = "机构黑名单", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    public AjaxResult edit(@RequestBody InstitutionBlacklist institutionBlacklist)
    {
        return toAjax(institutionBlacklistService.updateInstitutionBlacklist(institutionBlacklist));
    }

    /**
     * 删除机构黑名单
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:blacklist:remove')")
    @Log(title = "机构黑名单", businessType = BusinessType.DELETE)
    @DeleteMapping("/remove/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(institutionBlacklistService.deleteInstitutionBlacklistByIds(ids));
    }

    /**
     * 移除黑名单（将状态改为已解除）
     */
    @PreAuthorize("@ss.hasPermi('supervision:institution:blacklist:remove')")
    @Log(title = "移除黑名单", businessType = BusinessType.UPDATE)
    @PutMapping("/removeFromBlacklist/{id}")
    public AjaxResult removeFromBlacklist(@PathVariable Long id)
    {
        return toAjax(institutionBlacklistService.removeFromBlacklist(id));
    }
}
