package com.ruoyi.service;

import java.util.List;
import com.ruoyi.domain.InstitutionRating;

/**
 * 机构评级Service接口
 *
 * @author ruoyi
 * @date 2025-12-07
 */
public interface IInstitutionRatingService
{
    /**
     * 查询机构评级
     *
     * @param ratingId 机构评级主键
     * @return 机构评级
     */
    public InstitutionRating selectInstitutionRatingByRatingId(Long ratingId);

    /**
     * 查询机构评级列表
     *
     * @param institutionRating 机构评级
     * @return 机构评级集合
     */
    public List<InstitutionRating> selectInstitutionRatingList(InstitutionRating institutionRating);

    /**
     * 新增机构评级
     *
     * @param institutionRating 机构评级
     * @return 结果
     */
    public int insertInstitutionRating(InstitutionRating institutionRating);

    /**
     * 修改机构评级
     *
     * @param institutionRating 机构评级
     * @return 结果
     */
    public int updateInstitutionRating(InstitutionRating institutionRating);

    /**
     * 批量删除机构评级
     *
     * @param ratingIds 需要删除的机构评级主键集合
     * @return 结果
     */
    public int deleteInstitutionRatingByRatingIds(Long[] ratingIds);

    /**
     * 删除机构评级信息
     *
     * @param ratingId 机构评级主键
     * @return 结果
     */
    public int deleteInstitutionRatingByRatingId(Long ratingId);

    /**
     * 根据机构ID查询评级记录
     *
     * @param institutionId 机构ID
     * @return 机构评级集合
     */
    public List<InstitutionRating> selectInstitutionRatingByInstitutionId(Long institutionId);

    /**
     * 计算评级等级
     *
     * @param totalScore 总分
     * @return 评级等级
     */
    public Integer calculateRatingLevel(java.math.BigDecimal totalScore);

    /**
     * 验证评分数据
     *
     * @param institutionRating 机构评级
     * @return 验证结果
     */
    public boolean validateRatingScores(InstitutionRating institutionRating);
}