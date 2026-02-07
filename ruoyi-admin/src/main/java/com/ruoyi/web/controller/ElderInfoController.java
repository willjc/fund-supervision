package com.ruoyi.web.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.service.IElderInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.util.DigestUtils;

/**
 * 老人基础信息Controller
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@RestController
@RequestMapping("/elder/info")
public class ElderInfoController extends BaseController
{
    @Autowired
    private IElderInfoService elderInfoService;

    /**
     * 查询老人基础信息列表
     */
    @PreAuthorize("@ss.hasPermi('elder:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(ElderInfo elderInfo)
    {
        startPage();
        List<ElderInfo> list = elderInfoService.selectElderInfoList(elderInfo);
        return getDataTable(list);
    }

    /**
     * 导出老人基础信息列表
     */
    @PreAuthorize("@ss.hasPermi('elder:info:export')")
    @Log(title = "老人基础信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ElderInfo elderInfo)
    {
        List<ElderInfo> list = elderInfoService.exportElderInfo(elderInfo);
        ExcelUtil<ElderInfo> util = new ExcelUtil<ElderInfo>(ElderInfo.class);
        util.exportExcel(response, list, "老人基础信息数据");
    }

    /**
     * 获取老人基础信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('elder:info:query')")
    @GetMapping(value = "/{elderId}")
    public AjaxResult getInfo(@PathVariable("elderId") Long elderId)
    {
        return AjaxResult.success(elderInfoService.selectElderInfoByElderId(elderId));
    }

    /**
     * 根据身份证号查询老人信息
     */
    @PreAuthorize("@ss.hasPermi('elder:info:query')")
    @GetMapping(value = "/idcard/{idCard}")
    public AjaxResult getByIdCard(@PathVariable("idCard") String idCard)
    {
        ElderInfo elderInfo = elderInfoService.selectElderInfoByIdCard(idCard);
        return AjaxResult.success(elderInfo);
    }

    /**
     * 新增老人基础信息
     */
    @PreAuthorize("@ss.hasPermi('elder:info:add')")
    @Log(title = "老人基础信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ElderInfo elderInfo)
    {
        // 检查身份证号是否已存在
        ElderInfo existingElder = elderInfoService.selectElderInfoByIdCard(elderInfo.getIdCard());
        if (existingElder != null) {
            return AjaxResult.error("该身份证号已存在，请勿重复添加");
        }

        return toAjax(elderInfoService.insertElderInfo(elderInfo));
    }

    /**
     * 修改老人基础信息
     */
    @PreAuthorize("@ss.hasPermi('elder:info:edit')")
    @Log(title = "老人基础信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ElderInfo elderInfo)
    {
        return toAjax(elderInfoService.updateElderInfo(elderInfo));
    }

    /**
     * 设置老人密码
     */
    @PreAuthorize("@ss.hasPermi('elder:info:edit')")
    @Log(title = "设置老人密码", businessType = BusinessType.UPDATE)
    @PostMapping("/setPassword")
    public AjaxResult setPassword(@RequestBody Map<String, Object> params)
    {
        Long elderId = Long.valueOf(params.get("elderId").toString());
        String password = params.get("password").toString();

        // 参数校验
        if (StringUtils.isEmpty(password)) {
            return error("密码不能为空");
        }
        if (password.length() < 6) {
            return error("密码长度不能少于6位");
        }

        // 查询老人是否存在
        ElderInfo elderInfo = elderInfoService.selectElderInfoByElderId(elderId);
        if (elderInfo == null) {
            return error("老人信息不存在");
        }

        // MD5加密密码
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 更新密码
        ElderInfo updateInfo = new ElderInfo();
        updateInfo.setElderId(elderId);
        updateInfo.setPassword(md5Password);

        int result = elderInfoService.updateElderInfo(updateInfo);
        if (result > 0) {
            return success("密码设置成功");
        } else {
            return error("密码设置失败");
        }
    }

    /**
     * 删除老人基础信息
     */
    @PreAuthorize("@ss.hasPermi('elder:info:remove')")
    @Log(title = "老人基础信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{elderIds}")
    public AjaxResult remove(@PathVariable Long[] elderIds)
    {
        return toAjax(elderInfoService.deleteElderInfoByElderIds(elderIds));
    }

    /**
     * 下载老人信息导入模板
     */
    @PreAuthorize("@ss.hasPermi('elder:info:import')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<ElderInfo> util = new ExcelUtil<ElderInfo>(ElderInfo.class);
        util.importTemplateExcel(response, "老人信息数据");
    }

    /**
     * 导入老人信息
     */
    @PreAuthorize("@ss.hasPermi('elder:info:import')")
    @Log(title = "老人基础信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file) throws Exception
    {
        ExcelUtil<ElderInfo> util = new ExcelUtil<ElderInfo>(ElderInfo.class);
        List<ElderInfo> elderList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = elderInfoService.importElderInfo(elderList, operName);
        return success(message);
    }
}