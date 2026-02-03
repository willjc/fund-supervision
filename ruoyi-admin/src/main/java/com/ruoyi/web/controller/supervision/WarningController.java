package com.ruoyi.web.controller.supervision;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.domain.pension.SupervisionWarning;
import com.ruoyi.service.pension.ISupervisionWarningService;

/**
 * 民政监管-预警信息Controller
 *
 * @author ruoyi
 * @date 2026-02-03
 */
@RestController
@RequestMapping("/supervision/warning")
public class WarningController extends BaseController
{
    @Autowired
    private ISupervisionWarningService supervisionWarningService;

    /**
     * 预警类型映射
     */
    private static final Map<String, String> WARNING_TYPE_MAP = new HashMap<String, String>() {{
        put("1", "预收费用超额");
        put("2", "押金超额");
        put("3", "入住人数超额");
        put("4", "预收总额超额");
        put("5", "风险保证金超低");
        put("6", "大额支出");
        put("7", "交易对方风险");
    }};

    /**
     * 预警列表（所有类型）
     */
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) String warningType,
            @RequestParam(required = false) String status)
    {
        startPage();
        SupervisionWarning query = new SupervisionWarning();
        query.setWarningType(warningType);
        query.setWarningStatus(status);
        List<SupervisionWarning> list = supervisionWarningService.selectSupervisionWarningList(query);

        // 转换为前端需要的格式
        List<Map<String, Object>> resultList = convertToFrontendFormat(list);

        TableDataInfo dataInfo = getDataTable(resultList);
        return dataInfo;
    }

    /**
     * 获取预警详细信息
     */
    @GetMapping("/detail/{warningNo}")
    public AjaxResult getInfo(@PathVariable("warningNo") String warningNo)
    {
        SupervisionWarning warning = supervisionWarningService.selectSupervisionWarningByWarningNo(warningNo);
        if (warning == null)
        {
            return error("预警信息不存在");
        }
        Map<String, Object> result = convertToFrontendFormat(warning);
        return success(result);
    }

    /**
     * 处理预警
     */
    @PostMapping("/handle")
    public AjaxResult handle(@RequestBody Map<String, Object> data)
    {
        String warningNo = (String) data.get("warningNo");
        String handleRemark = (String) data.get("handleRemark");

        if (warningNo == null || warningNo.isEmpty())
        {
            return error("预警编号不能为空");
        }

        String handler = SecurityUtils.getUsername();
        int result = supervisionWarningService.handleWarning(warningNo, handler, handleRemark);

        if (result > 0)
        {
            return success("预警处理成功");
        }
        return error("预警处理失败，预警信息不存在");
    }

    /**
     * 转换为前端需要的格式
     */
    private Map<String, Object> convertToFrontendFormat(SupervisionWarning warning)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("warningId", warning.getWarningId());
        map.put("warningNo", warning.getWarningNo());
        map.put("institutionId", warning.getInstitutionId());
        map.put("institutionName", warning.getInstitutionName());
        map.put("warningType", WARNING_TYPE_MAP.getOrDefault(warning.getWarningType(), "未知类型"));
        map.put("warningTypeCode", warning.getWarningType());
        map.put("warningContent", warning.getWarningContent());
        map.put("warningLevel", warning.getWarningLevel());
        map.put("status", "0".equals(warning.getWarningStatus()) ? "待处理" : "已处理");
        map.put("warningStatus", warning.getWarningStatus());
        map.put("contactPerson", warning.getContactPerson());
        map.put("contactPhone", warning.getContactPhone());
        map.put("handler", warning.getHandler());
        map.put("handleTime", warning.getHandleTime());
        map.put("handleRemark", warning.getHandleRemark());
        map.put("createTime", warning.getCreateTime());
        return map;
    }

    /**
     * 批量转换
     */
    private List<Map<String, Object>> convertToFrontendFormat(List<SupervisionWarning> list)
    {
        List<Map<String, Object>> resultList = new java.util.ArrayList<>();
        for (SupervisionWarning warning : list)
        {
            resultList.add(convertToFrontendFormat(warning));
        }
        return resultList;
    }
}
