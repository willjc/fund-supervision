package com.ruoyi.web.controller.pension;

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
import com.ruoyi.domain.PensionInstitutionPublic;
import com.ruoyi.service.IPensionInstitutionPublicService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 养老机构公示信息Controller
 *
 * @author ruoyi
 * @date 2025-11-10
 */
@RestController
@RequestMapping("/pension/publicity")
public class PublicityController extends BaseController
{
    @Autowired
    private IPensionInstitutionPublicService pensionInstitutionPublicService;

    /**
     * 查询养老机构公示信息列表
     */
    @PreAuthorize("@ss.hasPermi('pension:publicity:list')")
    @GetMapping("/list")
    public TableDataInfo list(PensionInstitutionPublic pensionInstitutionPublic)
    {
        startPage();
        // 数据权限: 机构管理员只能查看自己关联的机构的公示信息
        pensionInstitutionPublic.setCurrentUserId(getUserId());
        List<PensionInstitutionPublic> list = pensionInstitutionPublicService.selectPensionInstitutionPublicList(pensionInstitutionPublic);
        return getDataTable(list);
    }

    /**
     * 导出养老机构公示信息列表
     */
    @PreAuthorize("@ss.hasPermi('pension:publicity:export')")
    @Log(title = "养老机构公示信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PensionInstitutionPublic pensionInstitutionPublic)
    {
        List<PensionInstitutionPublic> list = pensionInstitutionPublicService.selectPensionInstitutionPublicList(pensionInstitutionPublic);
        ExcelUtil<PensionInstitutionPublic> util = new ExcelUtil<PensionInstitutionPublic>(PensionInstitutionPublic.class);
        util.exportExcel(response, list, "养老机构公示信息数据");
    }

    /**
     * 获取养老机构公示信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:publicity:query')")
    @GetMapping(value = "/{publicId}")
    public AjaxResult getInfo(@PathVariable("publicId") Long publicId)
    {
        return AjaxResult.success(pensionInstitutionPublicService.selectPensionInstitutionPublicByPublicId(publicId));
    }

    /**
     * 根据机构ID获取公示信息
     */
    @PreAuthorize("@ss.hasPermi('pension:publicity:query')")
    @GetMapping(value = "/institution/{institutionId}")
    public AjaxResult getByInstitutionId(@PathVariable("institutionId") Long institutionId)
    {
        return AjaxResult.success(pensionInstitutionPublicService.selectPensionInstitutionPublicByInstitutionId(institutionId));
    }

    /**
     * 新增养老机构公示信息
     */
    @PreAuthorize("@ss.hasPermi('pension:publicity:add')")
    @Log(title = "养老机构公示信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PensionInstitutionPublic pensionInstitutionPublic)
    {
        // 检查该机构是否已存在公示信息
        PensionInstitutionPublic existing = pensionInstitutionPublicService.selectPensionInstitutionPublicByInstitutionId(pensionInstitutionPublic.getInstitutionId());
        if (existing != null) {
            return AjaxResult.error("该机构已存在公示信息，请使用编辑功能");
        }

        // 默认未公示状态
        if (pensionInstitutionPublic.getIsPublished() == null) {
            pensionInstitutionPublic.setIsPublished("0");
        }

        return toAjax(pensionInstitutionPublicService.insertPensionInstitutionPublic(pensionInstitutionPublic));
    }

    /**
     * 修改养老机构公示信息
     */
    @PreAuthorize("@ss.hasPermi('pension:publicity:edit')")
    @Log(title = "养老机构公示信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PensionInstitutionPublic pensionInstitutionPublic)
    {
        return toAjax(pensionInstitutionPublicService.updatePensionInstitutionPublic(pensionInstitutionPublic));
    }

    /**
     * 删除养老机构公示信息
     */
    @PreAuthorize("@ss.hasPermi('pension:publicity:remove')")
    @Log(title = "养老机构公示信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{publicIds}")
    public AjaxResult remove(@PathVariable Long[] publicIds)
    {
        return toAjax(pensionInstitutionPublicService.deletePensionInstitutionPublicByPublicIds(publicIds));
    }

    /**
     * 发布公示
     */
    @PreAuthorize("@ss.hasPermi('pension:publicity:publish')")
    @Log(title = "养老机构公示信息", businessType = BusinessType.UPDATE)
    @PutMapping("/publish/{publicId}")
    public AjaxResult publish(@PathVariable Long publicId)
    {
        PensionInstitutionPublic publicInfo = pensionInstitutionPublicService.selectPensionInstitutionPublicByPublicId(publicId);
        if (publicInfo == null) {
            return AjaxResult.error("公示信息不存在");
        }

        if ("1".equals(publicInfo.getIsPublished())) {
            return AjaxResult.error("该公示信息已发布");
        }

        return toAjax(pensionInstitutionPublicService.publishPublicity(publicId));
    }

    /**
     * 取消公示
     */
    @PreAuthorize("@ss.hasPermi('pension:publicity:unpublish')")
    @Log(title = "养老机构公示信息", businessType = BusinessType.UPDATE)
    @PutMapping("/unpublish/{publicId}")
    public AjaxResult unpublish(@PathVariable Long publicId)
    {
        PensionInstitutionPublic publicInfo = pensionInstitutionPublicService.selectPensionInstitutionPublicByPublicId(publicId);
        if (publicInfo == null) {
            return AjaxResult.error("公示信息不存在");
        }

        if ("0".equals(publicInfo.getIsPublished())) {
            return AjaxResult.error("该公示信息未发布");
        }

        return toAjax(pensionInstitutionPublicService.unpublishPublicity(publicId));
    }

    /**
     * 批量发布公示
     */
    @PreAuthorize("@ss.hasPermi('pension:publicity:publish')")
    @Log(title = "养老机构公示信息", businessType = BusinessType.UPDATE)
    @PutMapping("/batchPublish")
    public AjaxResult batchPublish(@RequestBody Long[] publicIds)
    {
        int successCount = 0;
        int failCount = 0;

        for (Long publicId : publicIds) {
            try {
                PensionInstitutionPublic publicInfo = pensionInstitutionPublicService.selectPensionInstitutionPublicByPublicId(publicId);
                if (publicInfo != null && "0".equals(publicInfo.getIsPublished())) {
                    pensionInstitutionPublicService.publishPublicity(publicId);
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
        }

        String message = String.format("批量发布完成：成功 %d 个，失败 %d 个", successCount, failCount);
        return AjaxResult.success(message);
    }
}
