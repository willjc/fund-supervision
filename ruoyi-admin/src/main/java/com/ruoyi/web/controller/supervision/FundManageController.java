package com.ruoyi.web.controller.supervision;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.domain.pension.TransferRuleConfig;
import com.ruoyi.domain.pension.SupervisionAccountLog;
import com.ruoyi.service.pension.ITransferRuleConfigService;
import com.ruoyi.service.pension.ISupervisionAccountLogService;

/**
 * 民政监管-资金管理Controller
 *
 * @author ruoyi
 * @date 2025-01-27
 */
@RestController
@RequestMapping("/supervision/fund")
public class FundManageController extends BaseController
{
    @Autowired
    private ITransferRuleConfigService transferRuleConfigService;

    @Autowired
    private ISupervisionAccountLogService supervisionAccountLogService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ==================== 划付规则配置接口 ====================

    /**
     * 查询划付规则配置列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:transferRule:list')")
    @GetMapping("/transfer-rule/list")
    public TableDataInfo getTransferRuleList(TransferRuleConfig transferRuleConfig)
    {
        startPage();
        List<TransferRuleConfig> list = transferRuleConfigService.selectTransferRuleConfigList(transferRuleConfig);
        return getDataTable(list);
    }

    /**
     * 获取划付规则配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('supervision:transferRule:query')")
    @GetMapping(value = "/transfer-rule/{ruleId}")
    public AjaxResult getTransferRule(@PathVariable("ruleId") Long ruleId)
    {
        return success(transferRuleConfigService.selectTransferRuleConfigByRuleId(ruleId));
    }

    /**
     * 新增划付规则配置
     */
    @PreAuthorize("@ss.hasPermi('supervision:transferRule:add')")
    @Log(title = "划付规则配置", businessType = BusinessType.INSERT)
    @PostMapping("/transfer-rule")
    public AjaxResult addTransferRule(@RequestBody TransferRuleConfig transferRuleConfig)
    {
        transferRuleConfig.setCreateBy(getUsername());
        return toAjax(transferRuleConfigService.insertTransferRuleConfig(transferRuleConfig));
    }

    /**
     * 修改划付规则配置
     */
    @PreAuthorize("@ss.hasPermi('supervision:transferRule:edit')")
    @Log(title = "划付规则配置", businessType = BusinessType.UPDATE)
    @PutMapping("/transfer-rule")
    public AjaxResult updateTransferRule(@RequestBody TransferRuleConfig transferRuleConfig)
    {
        transferRuleConfig.setUpdateBy(getUsername());
        return toAjax(transferRuleConfigService.updateTransferRuleConfig(transferRuleConfig));
    }

    /**
     * 删除划付规则配置
     */
    @PreAuthorize("@ss.hasPermi('supervision:transferRule:remove')")
    @Log(title = "划付规则配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/transfer-rule/{ruleIds}")
    public AjaxResult deleteTransferRule(@PathVariable Long[] ruleIds)
    {
        return toAjax(transferRuleConfigService.deleteTransferRuleConfigByRuleIds(ruleIds));
    }

    // ==================== 资金划付记录接口 ====================

    /**
     * 查询资金划付记录列表（所有机构汇总，无数据权限限制）
     */
    @PreAuthorize("@ss.hasPermi('supervision:transferRecord:list')")
    @GetMapping("/transfer-record/list")
    public TableDataInfo getTransferRecordList(
            SupervisionAccountLog supervisionAccountLog,
            @RequestParam(required = false) Long institutionId,
            @RequestParam(required = false) String beginTime,
            @RequestParam(required = false) String endTime)
    {
        startPage();

        // 不设置 currentUserId，使用无数据权限限制的查询方法
        if (institutionId != null) {
            supervisionAccountLog.setInstitutionId(institutionId);
        }

        // 设置时间范围参数
        if (beginTime != null || endTime != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("beginTime", beginTime);
            params.put("endTime", endTime);
            supervisionAccountLog.setParams(params);
        }

        List<SupervisionAccountLog> list = supervisionAccountLogService.selectAllSupervisionAccountLogList(supervisionAccountLog);

        // 转换为前端期望的格式
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (SupervisionAccountLog log : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("logId", log.getLogId());
            item.put("institutionName", log.getInstitutionName());
            item.put("transactionTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, log.getTransactionTime()));
            item.put("transactionType", log.getTransactionType());
            item.put("amount", log.getAmount());
            item.put("businessDesc", log.getBusinessDesc());
            resultList.add(item);
        }

        return getDataTable(resultList);
    }

    /**
     * 获取所有机构列表（用于筛选）
     */
    @PreAuthorize("@ss.hasPermi('supervision:transferRecord:list')")
    @GetMapping("/institutions/all")
    public AjaxResult getAllInstitutions()
    {
        String sql = "SELECT institution_id, institution_name FROM pension_institution ORDER BY institution_name";
        List<Map<String, Object>> institutions = jdbcTemplate.queryForList(sql);
        return success(institutions);
    }
}
