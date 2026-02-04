package com.ruoyi.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import com.ruoyi.mapper.MealFeeConfigMapper;
import com.ruoyi.domain.pension.MealFeeConfig;
import com.ruoyi.service.IMealFeeConfigService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;

/**
 * 餐费配置Service业务层处理
 *
 * @author ruoyi
 * @date 2026-02-04
 */
@Service
public class MealFeeConfigServiceImpl implements IMealFeeConfigService
{
    @Autowired
    private MealFeeConfigMapper mealFeeConfigMapper;

    /**
     * 查询餐费配置
     *
     * @param configId 餐费配置主键
     * @return 餐费配置
     */
    @Override
    public MealFeeConfig selectMealFeeConfigByConfigId(Long configId)
    {
        return mealFeeConfigMapper.selectMealFeeConfigByConfigId(configId);
    }

    /**
     * 查询餐费配置列表
     *
     * @param mealFeeConfig 餐费配置
     * @return 餐费配置
     */
    @Override
    public List<MealFeeConfig> selectMealFeeConfigList(MealFeeConfig mealFeeConfig)
    {
        return mealFeeConfigMapper.selectMealFeeConfigList(mealFeeConfig);
    }

    /**
     * 根据机构ID查询启用的餐费配置列表
     *
     * @param institutionId 机构ID
     * @return 餐费配置集合
     */
    @Override
    public List<MealFeeConfig> selectAvailableMealsByInstitutionId(Long institutionId)
    {
        return mealFeeConfigMapper.selectAvailableMealsByInstitutionId(institutionId);
    }

    /**
     * 根据机构ID和档次代码查询餐费配置
     *
     * @param institutionId 机构ID
     * @param mealLevelCode 档次代码
     * @return 餐费配置
     */
    @Override
    public MealFeeConfig selectMealByLevelCode(Long institutionId, String mealLevelCode)
    {
        return mealFeeConfigMapper.selectMealByLevelCode(institutionId, mealLevelCode);
    }

    /**
     * 新增餐费配置
     *
     * @param mealFeeConfig 餐费配置
     * @return 结果
     */
    @Override
    public int insertMealFeeConfig(MealFeeConfig mealFeeConfig)
    {
        // 校验价格不能为负数
        if (mealFeeConfig.getPrice() != null && mealFeeConfig.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("餐费价格不能为负数");
        }

        // 检查档次代码是否已存在
        MealFeeConfig existMeal = mealFeeConfigMapper.selectMealByLevelCode(
            mealFeeConfig.getInstitutionId(),
            mealFeeConfig.getMealLevelCode()
        );
        if (existMeal != null) {
            throw new ServiceException("该餐费档次已存在，请检查后重新提交");
        }

        mealFeeConfig.setCreateTime(DateUtils.getNowDate());

        try {
            return mealFeeConfigMapper.insertMealFeeConfig(mealFeeConfig);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("该餐费档次已存在，请检查后重新提交");
        }
    }

    /**
     * 修改餐费配置
     *
     * @param mealFeeConfig 餐费配置
     * @return 结果
     */
    @Override
    public int updateMealFeeConfig(MealFeeConfig mealFeeConfig)
    {
        // 校验价格不能为负数
        if (mealFeeConfig.getPrice() != null && mealFeeConfig.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException("餐费价格不能为负数");
        }

        // 检查档次代码是否已存在（排除当前记录）
        MealFeeConfig existMeal = mealFeeConfigMapper.selectMealByLevelCode(
            mealFeeConfig.getInstitutionId(),
            mealFeeConfig.getMealLevelCode()
        );
        if (existMeal != null && !existMeal.getConfigId().equals(mealFeeConfig.getConfigId())) {
            throw new ServiceException("该餐费档次已存在，请检查后重新提交");
        }

        mealFeeConfig.setUpdateTime(DateUtils.getNowDate());

        try {
            return mealFeeConfigMapper.updateMealFeeConfig(mealFeeConfig);
        } catch (DuplicateKeyException e) {
            throw new ServiceException("该餐费档次已存在，请检查后重新提交");
        }
    }

    /**
     * 批量删除餐费配置
     *
     * @param configIds 需要删除的餐费配置主键
     * @return 结果
     */
    @Override
    public int deleteMealFeeConfigByConfigIds(Long[] configIds)
    {
        return mealFeeConfigMapper.deleteMealFeeConfigByConfigIds(configIds);
    }

    /**
     * 删除餐费配置信息
     *
     * @param configId 餐费配置主键
     * @return 结果
     */
    @Override
    public int deleteMealFeeConfigByConfigId(Long configId)
    {
        return mealFeeConfigMapper.deleteMealFeeConfigByConfigId(configId);
    }
}
