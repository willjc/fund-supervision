package com.ruoyi.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.domain.pension.MealFeeConfig;

/**
 * 餐费配置Mapper接口
 *
 * @author ruoyi
 * @date 2026-02-04
 */
public interface MealFeeConfigMapper
{
    /**
     * 查询餐费配置
     *
     * @param configId 餐费配���主键
     * @return 餐费配置
     */
    public MealFeeConfig selectMealFeeConfigByConfigId(Long configId);

    /**
     * 查询餐费配置列表
     *
     * @param mealFeeConfig 餐费配置
     * @return 餐费配置集合
     */
    public List<MealFeeConfig> selectMealFeeConfigList(MealFeeConfig mealFeeConfig);

    /**
     * 根据机构ID查询启用的餐费配置列表
     *
     * @param institutionId 机构ID
     * @return 餐费配置集合
     */
    public List<MealFeeConfig> selectAvailableMealsByInstitutionId(Long institutionId);

    /**
     * 新增餐费配置
     *
     * @param mealFeeConfig 餐费配置
     * @return 结果
     */
    public int insertMealFeeConfig(MealFeeConfig mealFeeConfig);

    /**
     * 修改餐费配置
     *
     * @param mealFeeConfig 餐费配置
     * @return 结果
     */
    public int updateMealFeeConfig(MealFeeConfig mealFeeConfig);

    /**
     * 删除餐费配置
     *
     * @param configId 餐费配置主键
     * @return 结果
     */
    public int deleteMealFeeConfigByConfigId(Long configId);

    /**
     * 批量删除餐费配置
     *
     * @param configIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMealFeeConfigByConfigIds(Long[] configIds);

    /**
     * 检查餐费档次代码是否已存在
     *
     * @param institutionId 机构ID
     * @param mealLevelCode 档次代码
     * @return 餐费配置
     */
    public MealFeeConfig selectMealByLevelCode(@Param("institutionId") Long institutionId, @Param("mealLevelCode") String mealLevelCode);
}
