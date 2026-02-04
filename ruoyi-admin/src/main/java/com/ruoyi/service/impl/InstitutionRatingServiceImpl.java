package com.ruoyi.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.InstitutionRatingMapper;
import com.ruoyi.mapper.PensionInstitutionMapper;
import com.ruoyi.domain.InstitutionRating;
import com.ruoyi.service.IInstitutionRatingService;

/**
 * 机构评级Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-07
 */
@Service
public class InstitutionRatingServiceImpl implements IInstitutionRatingService
{
    @Autowired
    private InstitutionRatingMapper institutionRatingMapper;

    @Autowired
    private PensionInstitutionMapper pensionInstitutionMapper;

    /**
     * 查询机构评级
     *
     * @param ratingId 机构评级主键
     * @return 机构评级
     */
    @Override
    public InstitutionRating selectInstitutionRatingByRatingId(Long ratingId)
    {
        return institutionRatingMapper.selectInstitutionRatingByRatingId(ratingId);
    }

    /**
     * 查询机构评级列表
     *
     * @param institutionRating 机构评级
     * @return 机构评级
     */
    @Override
    public List<InstitutionRating> selectInstitutionRatingList(InstitutionRating institutionRating)
    {
        return institutionRatingMapper.selectInstitutionRatingList(institutionRating);
    }

    /**
     * 新增机构评级
     *
     * @param institutionRating 机构评级
     * @return 结果
     */
    @Override
    public int insertInstitutionRating(InstitutionRating institutionRating)
    {
        // 验证评分数据
        if (!validateRatingScores(institutionRating)) {
            throw new RuntimeException("评分数据不合法，各维度评分应在0-25分之间");
        }

        // 计算总分
        BigDecimal totalScore = calculateTotalScore(institutionRating);
        institutionRating.setTotalScore(totalScore);

        // 如果前端未传递评级等级，则自动计算；否则使用前端传递的值
        if (institutionRating.getRatingLevel() == null || institutionRating.getRatingLevel() < 1 || institutionRating.getRatingLevel() > 5) {
            Integer ratingLevel = calculateRatingLevel(totalScore);
            institutionRating.setRatingLevel(ratingLevel);
        }

        // 计算有效期至
        if (institutionRating.getRatingDate() != null && institutionRating.getValidityPeriod() != null) {
            Date expireDate = calculateExpireDate(institutionRating.getRatingDate(), institutionRating.getValidityPeriod());
            institutionRating.setExpireDate(expireDate);
        }

        institutionRating.setCreateTime(DateUtils.getNowDate());
        institutionRating.setRatingStatus("1"); // 默认有效状态
        int result = institutionRatingMapper.insertInstitutionRating(institutionRating);

        // 同步更新 pension_institution 表的 rating_level
        if (result > 0 && institutionRating.getInstitutionId() != null) {
            syncRatingToInstitution(institutionRating.getInstitutionId(), institutionRating.getRatingLevel());
        }

        return result;
    }

    /**
     * 修改机构评级
     *
     * @param institutionRating 机构评级
     * @return 结果
     */
    @Override
    public int updateInstitutionRating(InstitutionRating institutionRating)
    {
        // 验证评分数据
        if (!validateRatingScores(institutionRating)) {
            throw new RuntimeException("评分数据不合法，各维度评分应在0-25分之间");
        }

        // 计算总分
        BigDecimal totalScore = calculateTotalScore(institutionRating);
        institutionRating.setTotalScore(totalScore);

        // 如果前端未传递评级等级，则自动计算；否则使用前端传递的值
        if (institutionRating.getRatingLevel() == null || institutionRating.getRatingLevel() < 1 || institutionRating.getRatingLevel() > 5) {
            Integer ratingLevel = calculateRatingLevel(totalScore);
            institutionRating.setRatingLevel(ratingLevel);
        }

        // 计算有效期至
        if (institutionRating.getRatingDate() != null && institutionRating.getValidityPeriod() != null) {
            Date expireDate = calculateExpireDate(institutionRating.getRatingDate(), institutionRating.getValidityPeriod());
            institutionRating.setExpireDate(expireDate);
        }

        institutionRating.setUpdateTime(DateUtils.getNowDate());
        int result = institutionRatingMapper.updateInstitutionRating(institutionRating);

        // 同步更新 pension_institution 表的 rating_level
        if (result > 0 && institutionRating.getInstitutionId() != null) {
            syncRatingToInstitution(institutionRating.getInstitutionId(), institutionRating.getRatingLevel());
        }

        return result;
    }

    /**
     * 批量删除机构评级
     *
     * @param ratingIds 需要删除的机构评级主键
     * @return 结果
     */
    @Override
    public int deleteInstitutionRatingByRatingIds(Long[] ratingIds)
    {
        // 获取要删除的评级记录（获取institution_id）
        for (Long ratingId : ratingIds) {
            InstitutionRating rating = institutionRatingMapper.selectInstitutionRatingByRatingId(ratingId);
            if (rating != null && rating.getInstitutionId() != null) {
                // 先执行删除
                institutionRatingMapper.deleteInstitutionRatingByRatingId(ratingId);

                // 查找该机构最新的有效评级（评级日期最新的）
                InstitutionRating latestRating = institutionRatingMapper.selectLatestValidRatingByInstitutionId(rating.getInstitutionId());

                if (latestRating != null) {
                    // 有其他有效评级，更新为最新的评级
                    syncRatingToInstitution(rating.getInstitutionId(), latestRating.getRatingLevel());
                } else {
                    // 没有其他有效评级，恢复默认值3
                    syncRatingToInstitution(rating.getInstitutionId(), 3);
                }
            }
        }
        return ratingIds.length;
    }

    /**
     * 删除机构评级信息
     *
     * @param ratingId 机构评级主键
     * @return 结果
     */
    @Override
    public int deleteInstitutionRatingByRatingId(Long ratingId)
    {
        return institutionRatingMapper.deleteInstitutionRatingByRatingId(ratingId);
    }

    /**
     * 根据机构ID查询评级记录
     *
     * @param institutionId 机构ID
     * @return 机构评级集合
     */
    @Override
    public List<InstitutionRating> selectInstitutionRatingByInstitutionId(Long institutionId)
    {
        return institutionRatingMapper.selectInstitutionRatingByInstitutionId(institutionId);
    }

    /**
     * 计算评级等级
     *
     * @param totalScore 总分
     * @return 评级等级
     */
    @Override
    public Integer calculateRatingLevel(BigDecimal totalScore)
    {
        if (totalScore == null) {
            return 1;
        }

        double score = totalScore.doubleValue();
        if (score >= 90) {
            return 5; // 五星
        } else if (score >= 80) {
            return 4; // 四星
        } else if (score >= 70) {
            return 3; // 三星
        } else if (score >= 60) {
            return 2; // 二星
        } else {
            return 1; // 一星
        }
    }

    /**
     * 验证评分数据
     *
     * @param institutionRating 机构评级
     * @return 验证结果
     */
    @Override
    public boolean validateRatingScores(InstitutionRating institutionRating)
    {
        if (institutionRating == null) {
            return false;
        }

        // 验证各维度评分在0-25分之间
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal maxScore = new BigDecimal("25.0");

        return validateScore(institutionRating.getServiceScore(), zero, maxScore) &&
               validateScore(institutionRating.getFacilityScore(), zero, maxScore) &&
               validateScore(institutionRating.getManagementScore(), zero, maxScore) &&
               validateScore(institutionRating.getSafetyScore(), zero, maxScore);
    }

    /**
     * 计算总分
     */
    private BigDecimal calculateTotalScore(InstitutionRating institutionRating)
    {
        BigDecimal totalScore = BigDecimal.ZERO;

        if (institutionRating.getServiceScore() != null) {
            totalScore = totalScore.add(institutionRating.getServiceScore());
        }
        if (institutionRating.getFacilityScore() != null) {
            totalScore = totalScore.add(institutionRating.getFacilityScore());
        }
        if (institutionRating.getManagementScore() != null) {
            totalScore = totalScore.add(institutionRating.getManagementScore());
        }
        if (institutionRating.getSafetyScore() != null) {
            totalScore = totalScore.add(institutionRating.getSafetyScore());
        }

        return totalScore;
    }

    /**
     * 计算有效期至
     */
    private Date calculateExpireDate(Date ratingDate, Integer validityPeriod)
    {
        if (ratingDate == null || validityPeriod == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ratingDate);
        calendar.add(Calendar.MONTH, validityPeriod);
        return calendar.getTime();
    }

    /**
     * 验证单个评分
     */
    private boolean validateScore(BigDecimal score, BigDecimal min, BigDecimal max)
    {
        if (score == null) {
            return false;
        }
        return score.compareTo(min) >= 0 && score.compareTo(max) <= 0;
    }

    /**
     * 同步评级到机构表
     *
     * @param institutionId 机构ID
     * @param ratingLevel 评级等级
     */
    private void syncRatingToInstitution(Long institutionId, Integer ratingLevel)
    {
        try {
            pensionInstitutionMapper.updateRatingLevel(institutionId, ratingLevel);
        } catch (Exception e) {
            // 同步失败不应影响主流程，记录日志即可
            e.printStackTrace();
        }
    }

    /**
     * 查询机构最新有效评级（评级日期最新的）
     *
     * @param institutionId 机构ID
     * @return 机构评级
     */
    @Override
    public InstitutionRating selectLatestValidRatingByInstitutionId(Long institutionId)
    {
        return institutionRatingMapper.selectLatestValidRatingByInstitutionId(institutionId);
    }
}