package com.ruoyi.service.pension;

import java.util.List;
import com.ruoyi.domain.pension.WarningRuleConfig;

/**
 * 预警规则配置Service接口
 *
 * @author ruoyi
 * @date 2025-01-29
 */
public interface IWarningRuleConfigService
{
    /**
     * 查询预警规则配置
     *
     * @param ruleId 预警规则配置主键
     * @return 预警规则配置
     */
    public WarningRuleConfig selectWarningRuleConfigByRuleId(Long ruleId);

    /**
     * 根据规则编码查询预警规则配置
     *
     * @param ruleCode 规则编码
     * @return 预警规则配置
     */
    public WarningRuleConfig selectWarningRuleConfigByRuleCode(String ruleCode);

    /**
     * 查询预警规则配置列表
     *
     * @param warningRuleConfig 预警规则配置
     * @return 预警规则配置集合
     */
    public List<WarningRuleConfig> selectWarningRuleConfigList(WarningRuleConfig warningRuleConfig);

    /**
     * 新增预警规则配置
     *
     * @param warningRuleConfig 预警规则配置
     * @return 结果
     */
    public int insertWarningRuleConfig(WarningRuleConfig warningRuleConfig);

    /**
     * 修改预警规则配置
     *
     * @param warningRuleConfig 预警规则配置
     * @return 结果
     */
    public int updateWarningRuleConfig(WarningRuleConfig warningRuleConfig);

    /**
     * 批量删除预警规则配置
     *
     * @param ruleIds 需要删除的预警规则配置主键集合
     * @return 结果
     */
    public int deleteWarningRuleConfigByRuleIds(Long[] ruleIds);

    /**
     * 删除预警规则配置信息
     *
     * @param ruleId 预警规则配置主键
     * @return 结果
     */
    public int deleteWarningRuleConfigByRuleId(Long ruleId);

    /**
     * 批量更新预警规则配置
     *
     * @param configList 预警规则配置集合
     * @return 结果
     */
    public int batchUpdateWarningRuleConfig(List<WarningRuleConfig> configList);
}
