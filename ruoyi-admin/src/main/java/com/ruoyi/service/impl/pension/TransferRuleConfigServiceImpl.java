package com.ruoyi.service.impl.pension;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.pension.TransferRuleConfigMapper;
import com.ruoyi.domain.pension.TransferRuleConfig;
import com.ruoyi.service.pension.ITransferRuleConfigService;

/**
 * 划付规则配置Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-27
 */
@Service
public class TransferRuleConfigServiceImpl implements ITransferRuleConfigService
{
    @Autowired
    private TransferRuleConfigMapper transferRuleConfigMapper;

    /**
     * 查询划付规则配置
     *
     * @param ruleId 划付规则配置主键
     * @return 划付规则配置
     */
    @Override
    public TransferRuleConfig selectTransferRuleConfigByRuleId(Long ruleId)
    {
        return transferRuleConfigMapper.selectTransferRuleConfigByRuleId(ruleId);
    }

    /**
     * 查询划付规则配置列表
     *
     * @param transferRuleConfig 划付规则配置
     * @return 划付规则配置
     */
    @Override
    public List<TransferRuleConfig> selectTransferRuleConfigList(TransferRuleConfig transferRuleConfig)
    {
        return transferRuleConfigMapper.selectTransferRuleConfigList(transferRuleConfig);
    }

    /**
     * 新增划付规则配置
     *
     * @param transferRuleConfig 划付规则配置
     * @return 结果
     */
    @Override
    public int insertTransferRuleConfig(TransferRuleConfig transferRuleConfig)
    {
        return transferRuleConfigMapper.insertTransferRuleConfig(transferRuleConfig);
    }

    /**
     * 修改划付规则配置
     *
     * @param transferRuleConfig 划付规则配置
     * @return 结果
     */
    @Override
    public int updateTransferRuleConfig(TransferRuleConfig transferRuleConfig)
    {
        return transferRuleConfigMapper.updateTransferRuleConfig(transferRuleConfig);
    }

    /**
     * 批量删除划付规则配置
     *
     * @param ruleIds 需要删除的划付规则配置主键
     * @return 结果
     */
    @Override
    public int deleteTransferRuleConfigByRuleIds(Long[] ruleIds)
    {
        return transferRuleConfigMapper.deleteTransferRuleConfigByRuleIds(ruleIds);
    }

    /**
     * 删除划付规则配置信息
     *
     * @param ruleId 划付规则配置主键
     * @return 结果
     */
    @Override
    public int deleteTransferRuleConfigByRuleId(Long ruleId)
    {
        return transferRuleConfigMapper.deleteTransferRuleConfigByRuleId(ruleId);
    }
}
