package com.ruoyi.web.controller.pension;

import java.util.Map;
import java.util.HashMap;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 机构公示信息Controller
 *
 * @author ruoyi
 * @date 2025-01-03
 */
@RestController
@RequestMapping("/pension/publicity")
public class InstitutionPublicityController extends BaseController
{
    /**
     * 获取机构公示信息
     */
    @GetMapping("/info")
    public AjaxResult getPublicityInfo()
    {
        Map<String, Object> data = new HashMap<>();

        // TODO: 从数据库查询真实数据
        data.put("institutionName", "幸福养老院");
        data.put("creditCode", "91410100MA44XXXX01");
        data.put("legalPerson", "张三");
        data.put("contactPhone", "13800138000");
        data.put("address", "郑州市金水区XX路XX号");
        data.put("approvedBeds", 200);
        data.put("actualElders", 168);
        data.put("serviceItems", new String[]{"生活照料", "医疗护理", "康复训练"});
        data.put("chargeStandard", "护理费：2000-5000元/月\n床位费：800-1500元/月\n餐费：600元/月");
        data.put("businessLicenseExpiry", "2026-12-31");
        data.put("foodLicenseExpiry", "2025-12-31");
        data.put("doctorCount", 5);
        data.put("nurseCount", 15);
        data.put("caregiverCount", 30);
        data.put("facilities", "配备医疗器械、康复器材、无障碍设施等");
        data.put("isPublic", true);
        data.put("publicNotice", "本机构为民政部门登记备案的合法养老服务机构");

        return success(data);
    }

    /**
     * 保存机构公示信息
     */
    @PostMapping("/save")
    public AjaxResult savePublicityInfo(@RequestBody Map<String, Object> params)
    {
        // TODO: 保存到数据库
        logger.info("保存机构公示信息: {}", params);
        return success("公示信息保存成功");
    }

    /**
     * 上传公示图片
     */
    @PostMapping("/upload-picture")
    public AjaxResult uploadPicture(@RequestParam("file") String fileUrl)
    {
        // TODO: 处理图片上传
        Map<String, Object> data = new HashMap<>();
        data.put("url", fileUrl);
        data.put("name", "环境图片");
        return success(data);
    }
}
