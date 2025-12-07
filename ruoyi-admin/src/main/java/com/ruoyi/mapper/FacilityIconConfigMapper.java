package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.domain.FacilityIconConfig;

/**
 * 设施图标配置Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-07
 */
public interface FacilityIconConfigMapper
{
    /**
     * 查询设施图标配置
     *
     * @param id 设施图标配置主键
     * @return 设施图标配置
     */
    public FacilityIconConfig selectFacilityIconConfigById(Long id);

    /**
     * 查询设施图标配置列表
     *
     * @param facilityIconConfig 设施图标配置
     * @return 设施图标配置集合
     */
    public List<FacilityIconConfig> selectFacilityIconConfigList(FacilityIconConfig facilityIconConfig);

    /**
     * 根据设施类型查询配置列表
     *
     * @param facilityType 设施类型
     * @return 设施图标配置集合
     */
    public List<FacilityIconConfig> selectByType(String facilityType);

    /**
     * 新增设施图标配置
     *
     * @param facilityIconConfig 设施图标配置
     * @return 结果
     */
    public int insertFacilityIconConfig(FacilityIconConfig facilityIconConfig);

    /**
     * 修改设施图标配置
     *
     * @param facilityIconConfig 设施图标配置
     * @return 结果
     */
    public int updateFacilityIconConfig(FacilityIconConfig facilityIconConfig);

    /**
     * 删除设施图标配置
     *
     * @param id 设施图标配置主键
     * @return 结果
     */
    public int deleteFacilityIconConfigById(Long id);

    /**
     * 批量删除设施图标配置
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFacilityIconConfigByIds(Long[] ids);
}