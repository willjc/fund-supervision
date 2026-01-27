package com.ruoyi.web.controller.elder;

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
import com.ruoyi.domain.ElderFamily;
import com.ruoyi.service.IElderFamilyService;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户-老人关联(家属管理)Controller
 *
 * @author ruoyi
 * @date 2025-01-14
 */
@RestController
@RequestMapping("/elder/family")
public class ElderFamilyController extends BaseController
{
    @Autowired
    private IElderFamilyService elderFamilyService;

    /**
     * 查询家属关联列表
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:query')")
    @GetMapping("/list")
    public TableDataInfo list(ElderFamily elderFamily)
    {
        startPage();
        List<ElderFamily> list = elderFamilyService.selectElderFamilyList(elderFamily);
        return getDataTable(list);
    }

    /**
     * 获取家属关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:query')")
    @GetMapping(value = "/{familyId}")
    public AjaxResult getInfo(@PathVariable("familyId") Long familyId)
    {
        return success(elderFamilyService.selectElderFamilyById(familyId));
    }

    /**
     * 新增家属关联
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:add')")
    @Log(title = "家属管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ElderFamily elderFamily)
    {
        return toAjax(elderFamilyService.insertElderFamily(elderFamily));
    }

    /**
     * 修改家属关联
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:edit')")
    @Log(title = "家属管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ElderFamily elderFamily)
    {
        return toAjax(elderFamilyService.updateElderFamily(elderFamily));
    }

    /**
     * 删除家属关联
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:remove')")
    @Log(title = "家属管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{familyIds}")
    public AjaxResult remove(@PathVariable Long[] familyIds)
    {
        return toAjax(elderFamilyService.deleteElderFamilyByIds(familyIds));
    }
}
