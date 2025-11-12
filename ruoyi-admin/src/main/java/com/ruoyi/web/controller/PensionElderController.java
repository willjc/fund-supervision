package com.ruoyi.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.mapper.ElderInfoMapper;

/**
 * 老人信息Controller
 *
 * @author ruoyi
 * @date 2025-11-11
 */
@RestController
@RequestMapping("/pension/elder/info")
public class PensionElderController extends BaseController
{
    @Autowired
    private ElderInfoMapper elderInfoMapper;

    /**
     * 获取老人详细信息
     */
    @PreAuthorize("@ss.hasPermi('elder:info:query')")
    @GetMapping(value = "/{elderId}")
    public AjaxResult getInfo(@PathVariable("elderId") Long elderId)
    {
        return AjaxResult.success(elderInfoMapper.selectElderInfoByElderId(elderId));
    }

    /**
     * 修改老人信息
     */
    @PreAuthorize("@ss.hasPermi('elder:info:edit')")
    @Log(title = "老人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ElderInfo elderInfo)
    {
        return toAjax(elderInfoMapper.updateElderInfo(elderInfo));
    }
}
