package com.ruoyi.service.impl.pension;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.pension.WarningRuleConfigMapper;
import com.ruoyi.domain.pension.WarningRuleConfig;
import com.ruoyi.service.pension.IWarningRuleConfigService;

/**
 * 预警规则配置Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-29
 */
@Service
public class WarningRuleConfigServiceImpl implements IWarningRuleConfigService
{
    @Autowired
    private WarningRuleConfigMapper warningRuleConfigMapper;

    /**
     * 查询预警规则配置
     *
     * @param ruleId 预警规则配置主键
     * @return 预警规则配置
     */
    @Override
    public WarningRuleConfig selectWarningRuleConfigByRuleId(Long ruleId)
    {
        return warningRuleConfigMapper.selectWarningRuleConfigByRuleId(ruleId);
    }

    /**
     * 根据规则编码查询预警规则配置
     *
     * @param ruleCode 规则编码
     * @return 预警规则配置
     */
    @Override
    public WarningRuleConfig selectWarningRuleConfigByRuleCode(String ruleCode)
    {
        return warningRuleConfigMapper.selectWarningRuleConfigByRuleCode(ruleCode);
    }

    /**
     * 查询预警规则配置列表
     *
     * @param warningRuleConfig 预警规则配置
     * @return 预警规则配置
     */
    @Override
    public List<WarningRuleConfig> selectWarningRuleConfigList(WarningRuleConfig warningRuleConfig)
    {
        return warningRuleConfigMapper.selectWarningRuleConfigList(warningRuleConfig);
    }

    /**
     * 新增预警规则配置
     *
     * @param warningRuleConfig 预警规则配置
     * @return 结果
     */
    @Override
    public int insertWarningRuleConfig(WarningRuleConfig warningRuleConfig)
    {
        return warningRuleConfigMapper.insertWarningRuleConfig(warningRuleConfig);
    }

    /**
     * 修改预警规则配置
     *
     * @param warningRuleConfig 预警规则配置
     * @return 结果
     */
    @Override
    public int updateWarningRuleConfig(WarningRuleConfig warningRuleConfig)
    {
        return warningRuleConfigMapper.updateWarningRuleConfig(warningRuleConfig);
    }

    /**
     * 批量删除预警规则配置
     *
     * @param ruleIds 需要删除的预警规则配置主键
     * @return 结果
     */
    @Override
    public int deleteWarningRuleConfigByRuleIds(Long[] ruleIds)
    {
        return warningRuleConfigMapper.deleteWarningRuleConfigByRuleIds(ruleIds);
    }

    /**
     * 删除预警规则配置信息
     *
     * @param ruleId 预警规则配置主键
     * @return 结果
     */
    @Override
    public int deleteWarningRuleConfigByRuleId(Long ruleId)
    {
        return warningRuleConfigMapper.deleteWarningRuleConfigByRuleId(ruleId);
    }

    /**
     * 批量更新预警规则配置
     *
     * @param configList 预警规则配置集合
     * @return 结果
     */
    @Override
    public int batchUpdateWarningRuleConfig(List<WarningRuleConfig> configList)
    {
        int count = 0;
        for (WarningRuleConfig config : configList)
        {
            count += warningRuleConfigMapper.updateWarningRuleConfig(config);
        }
        return count;
    }
}
