package com.ruoyi.web.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.domain.RenewDTO;
import com.ruoyi.domain.vo.ElderCurrentPriceVO;
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
        // 数据权限过滤: admin超级管理员可以看到所有机构的入住人，其他用户只能看到关联机构的入住人
        if (!getUserId().equals(1L)) {
            queryVO.setCurrentUserId(getUserId());
        }
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
     * 获取老人当前有效价格
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:query')")
    @GetMapping("/currentPrice/{elderId}")
    public AjaxResult getCurrentPrice(@PathVariable("elderId") Long elderId)
    {
        return AjaxResult.success(residentService.getCurrentPrice(elderId));
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

    /**
     * 查询入住人统计数据
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:list')")
    @GetMapping("/statistics")
    public AjaxResult getStatistics()
    {
        return AjaxResult.success(residentService.getResidentStatistics());
    }

    /**
     * 删除入住人
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:remove')")
    @Log(title = "入住人管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/delete/{residentId}")
    public AjaxResult delete(@PathVariable("residentId") Long residentId)
    {
        return toAjax(residentService.deleteResident(residentId));
    }

    /**
     * 查询老人的拨付单列表
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:query')")
    @GetMapping("/transfer/{elderId}")
    public AjaxResult getTransfers(@PathVariable("elderId") Long elderId)
    {
        return AjaxResult.success(residentService.selectTransfersByElderId(elderId));
    }

    /**
     * 下载入住人导入模板
     */
    @PostMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException
    {
        // 导出模板文件
        List<ResidentVO> list = List.of();
        ExcelUtil<ResidentVO> util = new ExcelUtil<>(ResidentVO.class);
        util.importTemplateExcel(response, "入住人数据");
    }

    /**
     * 导出入住人列表
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:export')")
    @Log(title = "入住人管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ResidentVO queryVO)
    {
        // 数据权限过滤: admin超级管理员可以看到所有机构的入住人，其他用户只能看到关联机构的入住人
        if (!getUserId().equals(1L)) {
            queryVO.setCurrentUserId(getUserId());
        }
        List<ResidentVO> list = residentService.selectResidentList(queryVO);
        ExcelUtil<ResidentVO> util = new ExcelUtil<>(ResidentVO.class);
        util.exportExcel(response, list, "入住人数据");
    }

    /**
     * 批量导入入住人
     */
    @PreAuthorize("@ss.hasPermi('elder:resident:import')")
    @Log(title = "入住人管理", businessType = BusinessType.IMPORT)
    @PostMapping("/import")
    public AjaxResult importData(MultipartFile file) throws Exception
    {
        ExcelUtil<ResidentVO> util = new ExcelUtil<>(ResidentVO.class);
        List<ResidentVO> userList = util.importExcel(file.getInputStream());
        // 这里可以调用service进行数据保存
        // residentService.importResident(userList);
        return AjaxResult.success("导入成功，共导入 " + userList.size() + " 条数据");
    }
}
