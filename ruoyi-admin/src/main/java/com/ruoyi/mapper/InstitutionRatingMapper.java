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

    /**
     * 查询机构最新有效评级（评级日期最新的）
     *
     * @param institutionId 机构ID
     * @return 机构评级
     */
    public InstitutionRating selectLatestValidRatingByInstitutionId(Long institutionId);

    /**
     * 更新过期的评级状态
     * 将有效期已过但状态仍为有效的评级记录更新��已过期状态
     *
     * @return 更新的记录数
     */
    public int updateExpiredRatingStatus();

    /**
     * 查询需要更新的过期评级数量
     *
     * @return 过期评级数量
     */
    public int countExpiredRatings();
}