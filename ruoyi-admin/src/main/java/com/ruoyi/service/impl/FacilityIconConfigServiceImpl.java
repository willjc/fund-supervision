package com.ruoyi.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.FacilityIconConfigMapper;
import com.ruoyi.domain.FacilityIconConfig;
import com.ruoyi.service.IFacilityIconConfigService;

/**
 * 设施图标配置Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-07
 */
@Service
public class FacilityIconConfigServiceImpl implements IFacilityIconConfigService
{
    @Autowired
    private FacilityIconConfigMapper facilityIconConfigMapper;

    /**
     * 查询设施图标配置
     *
     * @param id 设施图标配置主键
     * @return 设施图标配置
     */
    @Override
    public FacilityIconConfig selectFacilityIconConfigById(Long id)
    {
        return facilityIconConfigMapper.selectFacilityIconConfigById(id);
    }

    /**
     * 查询设施图标配置列表
     *
     * @param facilityIconConfig 设施图标配置
     * @return 设施图标配置
     */
    @Override
    public List<FacilityIconConfig> selectFacilityIconConfigList(FacilityIconConfig facilityIconConfig)
    {
        return facilityIconConfigMapper.selectFacilityIconConfigList(facilityIconConfig);
    }

    /**
     * 根据设施类型查询配置列表
     *
     * @param facilityType 设施类型
     * @return 设施图标配置集合
     */
    @Override
    public List<FacilityIconConfig> selectByType(String facilityType)
    {
        return facilityIconConfigMapper.selectByType(facilityType);
    }

    /**
     * 新增设施图标配置
     *
     * @param facilityIconConfig 设施图标配置
     * @return 结果
     */
    @Override
    public int insertFacilityIconConfig(FacilityIconConfig facilityIconConfig)
    {
        return facilityIconConfigMapper.insertFacilityIconConfig(facilityIconConfig);
    }

    /**
     * 修改设施图标配置
     *
     * @param facilityIconConfig 设施图标配置
     * @return 结果
     */
    @Override
    public int updateFacilityIconConfig(FacilityIconConfig facilityIconConfig)
    {
        return facilityIconConfigMapper.updateFacilityIconConfig(facilityIconConfig);
    }

    /**
     * 批量删除设施图标配置
     *
     * @param ids 需要删除的设施图标配置主键
     * @return 结果
     */
    @Override
    public int deleteFacilityIconConfigByIds(Long[] ids)
    {
        return facilityIconConfigMapper.deleteFacilityIconConfigByIds(ids);
    }

    /**
     * 删除设施图标配置信息
     *
     * @param id 设施图标配置主键
     * @return 结果
     */
    @Override
    public int deleteFacilityIconConfigById(Long id)
    {
        return facilityIconConfigMapper.deleteFacilityIconConfigById(id);
    }
}