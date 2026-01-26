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
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.service.IElderInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;

/**
 * 机构端老人档案管理Controller
 *
 * @author ruoyi
 * @date 2026-01-26
 */
@RestController
@RequestMapping("/pension/elder/manage")
public class PensionElderManageController extends BaseController
{
    @Autowired
    private IElderInfoService elderInfoService;

    /**
     * 查询老人档案列表
     */
    @PreAuthorize("@ss.hasPermi('pension:elder:manage:list')")
    @GetMapping("/list")
    public TableDataInfo list(ElderInfo elderInfo)
    {
        startPage();
        // 设置当前用户ID用于数据权限过滤
        elderInfo.setCurrentUserId(SecurityUtils.getUserId());
        List<ElderInfo> list = elderInfoService.selectElderInfoList(elderInfo);
        return getDataTable(list);
    }

    /**
     * 获取老人档案详细信息
     */
    @PreAuthorize("@ss.hasPermi('pension:elder:manage:query')")
    @GetMapping(value = "/{elderId}")
    public AjaxResult getInfo(@PathVariable("elderId") Long elderId)
    {
        return success(elderInfoService.selectElderInfoByElderId(elderId));
    }

    /**
     * 获取老人档案选项列表(用于下拉选择)
     */
    @PreAuthorize("@ss.hasPermi('pension:elder:manage:list')")
    @GetMapping("/options")
    public AjaxResult getOptions()
    {
        ElderInfo query = new ElderInfo();
        query.setCurrentUserId(SecurityUtils.getUserId());
        query.setStatus("0"); // 只查询未入住的老人
        List<ElderInfo> list = elderInfoService.selectElderInfoList(query);
        return success(list);
    }

    /**
     * 新增老人档案
     */
    @PreAuthorize("@ss.hasPermi('pension:elder:manage:add')")
    @Log(title = "老人档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ElderInfo elderInfo)
    {
        // 参数校验：紧急联系人和电话为必填
        if (StringUtils.isEmpty(elderInfo.getEmergencyContact())) {
            return error("紧急联系人不能为空");
        }
        if (StringUtils.isEmpty(elderInfo.getEmergencyPhone())) {
            return error("紧急联系电话不能为空");
        }
        if (!elderInfo.getEmergencyPhone().matches("^1[3-9]\\d{9}$")) {
            return error("紧急联系电话格式不正确");
        }

        // 检查身份证号是否已存在
        ElderInfo existingElder = elderInfoService.selectElderInfoByIdCard(elderInfo.getIdCard());
        if (existingElder != null) {
            return error("该身份证号已存在");
        }

        // 设置创建者
        elderInfo.setCreateBy(SecurityUtils.getUsername());
        return toAjax(elderInfoService.insertElderInfo(elderInfo));
    }

    /**
     * 修改老人档案
     */
    @PreAuthorize("@ss.hasPermi('pension:elder:manage:edit')")
    @Log(title = "老人档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ElderInfo elderInfo)
    {
        // 参数校验：紧急联系人和电话为必填
        if (StringUtils.isEmpty(elderInfo.getEmergencyContact())) {
            return error("紧急联系人不能为空");
        }
        if (StringUtils.isEmpty(elderInfo.getEmergencyPhone())) {
            return error("紧急联系电话不能为空");
        }
        if (!elderInfo.getEmergencyPhone().matches("^1[3-9]\\d{9}$")) {
            return error("紧急联系电话格式不正确");
        }

        // 设置修改者
        elderInfo.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(elderInfoService.updateElderInfo(elderInfo));
    }

    /**
     * 删除老人档案
     */
    @PreAuthorize("@ss.hasPermi('pension:elder:manage:remove')")
    @Log(title = "老人档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{elderIds}")
    public AjaxResult remove(@PathVariable Long[] elderIds)
    {
        return toAjax(elderInfoService.deleteElderInfoByElderIds(elderIds));
    }

    /**
     * 导出老人档案
     */
    @PreAuthorize("@ss.hasPermi('pension:elder:manage:export')")
    @Log(title = "老人档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ElderInfo elderInfo)
    {
        // 设置当前用户ID用于数据权限过滤
        elderInfo.setCurrentUserId(SecurityUtils.getUserId());
        List<ElderInfo> list = elderInfoService.selectElderInfoList(elderInfo);
        ExcelUtil<ElderInfo> util = new ExcelUtil<ElderInfo>(ElderInfo.class);
        util.exportExcel(response, list, "老人档案数据");
    }
}
