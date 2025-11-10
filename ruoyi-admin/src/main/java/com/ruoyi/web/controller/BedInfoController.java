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
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.BedInfo;
import com.ruoyi.service.IBedInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 床位信息Controller
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@RestController
@RequestMapping("/elder/bed")
public class BedInfoController extends BaseController
{
    @Autowired
    private IBedInfoService bedInfoService;

    /**
     * 查询床位信息列表
     */
    @PreAuthorize("@ss.hasPermi('elder:bed:list')")
    @GetMapping("/list")
    public TableDataInfo list(BedInfo bedInfo)
    {
        startPage();
        // 数据权限过滤: 只查询当前用户关联的机构的床位
        bedInfo.setCurrentUserId(getUserId());
        List<BedInfo> list = bedInfoService.selectBedInfoList(bedInfo);
        return getDataTable(list);
    }

    /**
     * 查询可用床位列表
     */
    @PreAuthorize("@ss.hasPermi('elder:bed:query')")
    @GetMapping("/available/{institutionId}")
    public AjaxResult availableBeds(@PathVariable("institutionId") Long institutionId)
    {
        List<BedInfo> list = bedInfoService.selectAvailableBeds(institutionId);
        return AjaxResult.success(list);
    }

    /**
     * 导出床位信息列表
     */
    @PreAuthorize("@ss.hasPermi('elder:bed:export')")
    @Log(title = "床位信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BedInfo bedInfo)
    {
        List<BedInfo> list = bedInfoService.exportBedInfo(bedInfo);
        ExcelUtil<BedInfo> util = new ExcelUtil<BedInfo>(BedInfo.class);
        util.exportExcel(response, list, "床位信息数据");
    }

    /**
     * 获取床位信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('elder:bed:query')")
    @GetMapping(value = "/{bedId}")
    public AjaxResult getInfo(@PathVariable("bedId") Long bedId)
    {
        return AjaxResult.success(bedInfoService.selectBedInfoByBedId(bedId));
    }

    /**
     * 新增床位信息
     */
    @PreAuthorize("@ss.hasPermi('elder:bed:add')")
    @Log(title = "床位信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BedInfo bedInfo)
    {
        return toAjax(bedInfoService.insertBedInfo(bedInfo));
    }

    /**
     * 修改床位信息
     */
    @PreAuthorize("@ss.hasPermi('elder:bed:edit')")
    @Log(title = "床位信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BedInfo bedInfo)
    {
        return toAjax(bedInfoService.updateBedInfo(bedInfo));
    }

    /**
     * 删除床位信息
     */
    @PreAuthorize("@ss.hasPermi('elder:bed:remove')")
    @Log(title = "床位信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{bedIds}")
    public AjaxResult remove(@PathVariable Long[] bedIds)
    {
        return toAjax(bedInfoService.deleteBedInfoByBedIds(bedIds));
    }

    /**
     * 下载床位导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<BedInfo> util = new ExcelUtil<BedInfo>(BedInfo.class);
        util.importTemplateExcel(response, "床位信息");
    }

    /**
     * 批量导入床位信息
     */
    @PreAuthorize("@ss.hasPermi('elder:bed:import')")
    @Log(title = "床位信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<BedInfo> util = new ExcelUtil<BedInfo>(BedInfo.class);
        List<BedInfo> bedList = util.importExcel(file.getInputStream());
        String message = bedInfoService.importBedInfo(bedList, updateSupport);
        return AjaxResult.success(message);
    }
}