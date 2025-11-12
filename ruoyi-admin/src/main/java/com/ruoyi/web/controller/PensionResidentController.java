package com.ruoyi.web.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.RenewDTO;
import com.ruoyi.domain.vo.ResidentVO;
import com.ruoyi.service.IResidentService;

/**
 * 入住人管理Controller
 *
 * @author ruoyi
 * @date 2025-11-11
 */
@RestController
@RequestMapping("/pension/resident")
public class PensionResidentController extends BaseController
{
    @Autowired
    private IResidentService residentService;

    /**
     * 查询入住人列表
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:list')")
    @GetMapping("/list")
    public TableDataInfo list(ResidentVO queryVO)
    {
        startPage();
        List<ResidentVO> list = residentService.selectResidentList(queryVO);
        return getDataTable(list);
    }

    /**
     * 获取入住人详细信息
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:query')")
    @GetMapping("/detail/{elderId}")
    public AjaxResult getDetail(@PathVariable("elderId") Long elderId)
    {
        return AjaxResult.success(residentService.selectResidentDetail(elderId));
    }

    /**
     * 入住人续费
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:renew')")
    @Log(title = "入住人续费", businessType = BusinessType.INSERT)
    @PostMapping("/renew")
    public AjaxResult renew(@RequestBody RenewDTO renewDTO)
    {
        Long userId = SecurityUtils.getUserId();
        return toAjax(residentService.renewResident(renewDTO, userId));
    }
}
