package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.domain.InstitutionRating;

/**
 * 机构评级Mapper接口
 *
 * @author ruoyi
 * @date 2025-12-07
 */
public interface InstitutionRatingMapper
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
     * 删除机构评级
     *
     * @param ratingId 机构评级主键
     * @return 结果
     */
    public int deleteInstitutionRatingByRatingId(Long ratingId);

    /**
     * 批量删除机构评级
     *
     * @param ratingIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteInstitutionRatingByRatingIds(Long[] ratingIds);

    /**
     * 根据机构ID查询评级记录
     *
     * @param institutionId 机构ID
     * @return 机构评级集合
     */
    public List<InstitutionRating> selectInstitutionRatingByInstitutionId(Long institutionId);

    /**
     * 检查机构是否已有评级记录
     *
     * @param institutionId 机构ID
     * @return 结果
     */
    public int checkInstitutionRatingExists(Long institutionId);
}