package com.ruoyi.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.service.IPensionCheckinService;
import com.ruoyi.domain.PensionCheckinDTO;

/**
 * 养老机构入驻管理Controller
 *
 * @author ruoyi
 * @date 2025-11-11
 */
@RestController
@RequestMapping("/pension/checkin")
public class PensionCheckinController extends BaseController
{
    @Autowired
    private IPensionCheckinService pensionCheckinService;

    /**
     * 创建入驻申请
     * 一次性完成:
     * 1. 创建elder_info记录
     * 2. 创建bed_allocation记录
     * 3. 创建order_info订单记录
     * 4. 创建order_item费用明细记录
     * 5. 更新床位状态
     */
    @PreAuthorize("@ss.hasPermi('pension:checkin:add')")
    @Log(title = "养老机构入驻", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult create(@RequestBody PensionCheckinDTO dto)
    {
        return toAjax(pensionCheckinService.createCheckin(dto, getUserId()));
    }
}
