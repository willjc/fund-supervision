package com.ruoyi.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.service.ISysCleanTestDataService;

/**
 * 清除测试数据Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/cleanTestData")
public class SysCleanTestDataController extends BaseController
{
    @Autowired
    private ISysCleanTestDataService cleanTestDataService;

    /**
     * 获取测试数据统计
     */
    @GetMapping("/status")
    public AjaxResult getStatus()
    {
        return cleanTestDataService.getTestDataStatus();
    }

    /**
     * 清除所有测试数据
     */
    @PreAuthorize("@ss.hasPermi('system:clean:execute')")
    @Log(title = "清除测试数据", businessType = BusinessType.CLEAN)
    @PostMapping("/execute")
    public AjaxResult execute()
    {
        return cleanTestDataService.cleanAllTestData();
    }
}
