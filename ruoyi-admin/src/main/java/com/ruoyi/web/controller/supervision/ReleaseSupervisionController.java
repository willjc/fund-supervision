package com.ruoyi.web.controller.supervision;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.domain.ReleaseSupervision;
import com.ruoyi.service.IReleaseSupervisionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 机构解除监管申请Controller
 *
 * @author ruoyi
 * @date 2025-01-04
 */
@RestController
@RequestMapping("/supervision/institution/release")
public class ReleaseSupervisionController extends BaseController
{
    @Autowired
    private IReleaseSupervisionService releaseSupervisionService;

    /**
     * 查询机构解除监管申请列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:release:list')")
    @GetMapping("/list")
    public TableDataInfo list(ReleaseSupervision releaseSupervision)
    {
        startPage();
        List<ReleaseSupervision> list = releaseSupervisionService.selectReleaseSupervisionList(releaseSupervision);
        return getDataTable(list);
    }

    /**
     * 导出机构解除监管申请列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:release:export')")
    @Log(title = "机构解除监管申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ReleaseSupervision releaseSupervision)
    {
        List<ReleaseSupervision> list = releaseSupervisionService.selectReleaseSupervisionList(releaseSupervision);
        ExcelUtil<ReleaseSupervision> util = new ExcelUtil<ReleaseSupervision>(ReleaseSupervision.class);
        util.exportExcel(response, list, "机构解除监管申请数据");
    }

    /**
     * 获取机构解除监管申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('supervision:release:query')")
    @GetMapping(value = "/{releaseId}")
    public AjaxResult getInfo(@PathVariable("releaseId") Long releaseId)
    {
        return success(releaseSupervisionService.selectReleaseSupervisionByReleaseId(releaseId));
    }

    /**
     * 新增机构解除监管申请
     */
    @PreAuthorize("@ss.hasPermi('supervision:release:add')")
    @Log(title = "机构解除监管申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ReleaseSupervision releaseSupervision)
    {
        return toAjax(releaseSupervisionService.insertReleaseSupervision(releaseSupervision));
    }

    /**
     * 修改机构解除监管申请
     */
    @PreAuthorize("@ss.hasPermi('supervision:release:edit')")
    @Log(title = "机构解除监管申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ReleaseSupervision releaseSupervision)
    {
        return toAjax(releaseSupervisionService.updateReleaseSupervision(releaseSupervision));
    }

    /**
     * 删除机构解除监管申请
     */
    @PreAuthorize("@ss.hasPermi('supervision:release:remove')")
    @Log(title = "机构解除监管申请", businessType = BusinessType.DELETE)
	@DeleteMapping("/{releaseIds}")
    public AjaxResult remove(@PathVariable Long[] releaseIds)
    {
        return toAjax(releaseSupervisionService.deleteReleaseSupervisionByReleaseIds(releaseIds));
    }

    /**
     * 批准解除监管
     */
    @PreAuthorize("@ss.hasPermi('supervision:release:approve')")
    @Log(title = "批准解除监管", businessType = BusinessType.UPDATE)
    @PostMapping("/approve/{releaseId}")
    public AjaxResult approve(@PathVariable Long releaseId, @RequestBody ReleaseSupervision releaseSupervision)
    {
        int result = releaseSupervisionService.approveRelease(releaseId, releaseSupervision);
        if (result > 0)
        {
            return AjaxResult.success("批准成功，系统已通知银行划拨监管资金至机构基本账户");
        }
        return AjaxResult.error("批准失败");
    }

    /**
     * 驳回解除监管申请
     */
    @PreAuthorize("@ss.hasPermi('supervision:release:reject')")
    @Log(title = "驳回解除监管申请", businessType = BusinessType.UPDATE)
    @PostMapping("/reject/{releaseId}")
    public AjaxResult reject(@PathVariable Long releaseId, @RequestBody ReleaseSupervision releaseSupervision)
    {
        int result = releaseSupervisionService.rejectRelease(releaseId, releaseSupervision);
        if (result > 0)
        {
            return AjaxResult.success("驳回成功，已通知机构");
        }
        return AjaxResult.error("驳回失败");
    }

    /**
     * 获取统计数据
     */
    @PreAuthorize("@ss.hasPermi('supervision:release:list')")
    @GetMapping("/statistics")
    public AjaxResult statistics()
    {
        Map<String, Object> data = releaseSupervisionService.getReleaseStatistics();
        return success(data);
    }
}
