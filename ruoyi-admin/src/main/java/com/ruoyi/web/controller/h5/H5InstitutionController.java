package com.ruoyi.web.controller.h5;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.service.IPensionInstitutionService;

/**
 * H5养老机构Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/institution")
public class H5InstitutionController extends BaseController
{
    @Autowired
    private IPensionInstitutionService pensionInstitutionService;

    /**
     * 查询养老机构列表 (H5端,不需要权限)
     */
    @GetMapping("/list")
    public TableDataInfo list(PensionInstitution pensionInstitution)
    {
        startPage();
        // H5端只显示审核通过且正常运营的机构
        pensionInstitution.setStatus("1");  // 1-正常运营
        List<PensionInstitution> list = pensionInstitutionService.selectPensionInstitutionList(pensionInstitution);
        return getDataTable(list);
    }

    /**
     * 获取养老机构详细信息 (H5端)
     */
    @GetMapping("/{institutionId}")
    public AjaxResult getInfo(@PathVariable("institutionId") Long institutionId)
    {
        PensionInstitution institution = pensionInstitutionService.selectPensionInstitutionByInstitutionId(institutionId);
        if (institution == null)
        {
            return AjaxResult.error("机构不存在");
        }
        // 只返回正常运营的机构
        if (!"1".equals(institution.getStatus()))
        {
            return AjaxResult.error("该机构暂不可用");
        }
        return AjaxResult.success(institution);
    }

    /**
     * 获取推荐机构列表 (首页展示)
     */
    @GetMapping("/recommend")
    public AjaxResult getRecommendList()
    {
        PensionInstitution queryInstitution = new PensionInstitution();
        queryInstitution.setStatus("1");  // 只查询正常运营的机构

        // 查询前6个推荐机构
        List<PensionInstitution> list = pensionInstitutionService.selectPensionInstitutionList(queryInstitution);

        // 只返回前6条
        if (list != null && list.size() > 6) {
            list = list.subList(0, 6);
        }

        return AjaxResult.success(list);
    }
}
