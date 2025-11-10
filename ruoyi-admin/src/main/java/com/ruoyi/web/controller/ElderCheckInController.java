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
import com.ruoyi.domain.ElderCheckIn;
import com.ruoyi.service.IElderCheckInService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 老人入住申请Controller
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@RestController
@RequestMapping("/elder/checkin")
public class ElderCheckInController extends BaseController
{
    @Autowired
    private IElderCheckInService elderCheckInService;

    /**
     * 查询老人入住申请列表
     */
    @PreAuthorize("@ss.hasPermi('elder:checkin:list')")
    @GetMapping("/list")
    public TableDataInfo list(ElderCheckIn elderCheckIn)
    {
        startPage();
        List<ElderCheckIn> list = elderCheckInService.selectElderCheckInList(elderCheckIn);
        return getDataTable(list);
    }

    /**
     * 导出老人入住申请列表
     */
    @PreAuthorize("@ss.hasPermi('elder:checkin:export')")
    @Log(title = "老人入住申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ElderCheckIn elderCheckIn)
    {
        List<ElderCheckIn> list = elderCheckInService.exportElderCheckIn(elderCheckIn);
        ExcelUtil<ElderCheckIn> util = new ExcelUtil<ElderCheckIn>(ElderCheckIn.class);
        util.exportExcel(response, list, "老人入住申请数据");
    }

    /**
     * 获取老人入住申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('elder:checkin:query')")
    @GetMapping(value = "/{checkInId}")
    public AjaxResult getInfo(@PathVariable("checkInId") Long checkInId)
    {
        return AjaxResult.success(elderCheckInService.selectElderCheckInByCheckInId(checkInId));
    }

    /**
     * 新增老人入住申请
     */
    @PreAuthorize("@ss.hasPermi('elder:checkin:add')")
    @Log(title = "老人入住申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ElderCheckIn elderCheckIn)
    {
        return toAjax(elderCheckInService.insertElderCheckIn(elderCheckIn));
    }

    /**
     * 修改老人入住申请
     */
    @PreAuthorize("@ss.hasPermi('elder:checkin:edit')")
    @Log(title = "老人入住申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ElderCheckIn elderCheckIn)
    {
        return toAjax(elderCheckInService.updateElderCheckIn(elderCheckIn));
    }

    /**
     * 审批入住申请
     */
    @PreAuthorize("@ss.hasPermi('elder:checkin:approve')")
    @Log(title = "老人入住申请审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve")
    public AjaxResult approve(@RequestBody ElderCheckIn elderCheckIn)
    {
        return toAjax(elderCheckInService.approveCheckIn(
            elderCheckIn.getCheckInId(),
            elderCheckIn.getCheckInStatus(),
            elderCheckIn.getApprovalUser(),
            elderCheckIn.getApprovalRemark()
        ));
    }

    /**
     * 删除老人入住申请
     */
    @PreAuthorize("@ss.hasPermi('elder:checkin:remove')")
    @Log(title = "老人入住申请", businessType = BusinessType.DELETE)
	@DeleteMapping("/{checkInIds}")
    public AjaxResult remove(@PathVariable Long[] checkInIds)
    {
        return toAjax(elderCheckInService.deleteElderCheckInByCheckInIds(checkInIds));
    }
}