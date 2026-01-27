package com.ruoyi.mapper.pension;

import java.util.List;
import com.ruoyi.domain.pension.TransferRuleConfig;

/**
 * 划付规则配置Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-27
 */
public interface TransferRuleConfigMapper
{
    /**
     * 查询划付规则配置
     *
     * @param ruleId 划付规则配置主键
     * @return 划付规则配置
     */
    public TransferRuleConfig selectTransferRuleConfigByRuleId(Long ruleId);

    /**
     * 查询划付规则配置列表
     *
     * @param transferRuleConfig 划付规则配置
     * @return 划付规则配置集合
     */
    public List<TransferRuleConfig> selectTransferRuleConfigList(TransferRuleConfig transferRuleConfig);

    /**
     * 新增划付规则配置
     *
     * @param transferRuleConfig 划付规则配置
     * @return 结果
     */
    public int insertTransferRuleConfig(TransferRuleConfig transferRuleConfig);

    /**
     * 修改划付规则配置
     *
     * @param transferRuleConfig 划付规则配置
     * @return 结果
     */
    public int updateTransferRuleConfig(TransferRuleConfig transferRuleConfig);

    /**
     * 删除划付规则配置
     *
     * @param ruleId 划付规则配置主键
     * @return 结果
     */
    public int deleteTransferRuleConfigByRuleId(Long ruleId);

    /**
     * 批量删除划付规则配置
     *
     * @param ruleIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTransferRuleConfigByRuleIds(Long[] ruleIds);
}
