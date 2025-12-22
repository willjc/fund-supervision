package com.ruoyi.mapper.pension;

import java.util.List;
import java.util.Map;
import com.ruoyi.domain.pension.InstitutionReview;

/**
 * 机构评价Mapper接口
 *
 * @author ruoyi
 * @date 2025-12-20
 */
public interface InstitutionReviewMapper
{
    /**
     * 查询机构评价
     *
     * @param reviewId 机构评价主键
     * @return 机构评价
     */
    public InstitutionReview selectInstitutionReviewByReviewId(Long reviewId);

    /**
     * 根据订单ID查询机构评价
     *
     * @param orderId 订单ID
     * @return 机构评价
     */
    public InstitutionReview selectInstitutionReviewByOrderId(String orderId);

    /**
     * 查询机构评价列表
     *
     * @param institutionReview 机构评价
     * @return 机构评价集合
     */
    public List<InstitutionReview> selectInstitutionReviewList(InstitutionReview institutionReview);

    /**
     * 查询机构评价列表（关联查询）
     *
     * @param institutionReview 机构评价
     * @return 机构评价集合
     */
    public List<InstitutionReview> selectInstitutionReviewWithRelationsList(InstitutionReview institutionReview);

    /**
     * 根据机构ID查询已通过的评价列表
     *
     * @param institutionId 机构ID
     * @param limit 限制数量
     * @return 评价列表
     */
    public List<InstitutionReview> selectApprovedReviewsByInstitutionId(Long institutionId, Integer limit);

    /**
     * 新增机构评价
     *
     * @param institutionReview 机构评价
     * @return 结果
     */
    public int insertInstitutionReview(InstitutionReview institutionReview);

    /**
     * 修改机构评价
     *
     * @param institutionReview 机构评价
     * @return 结果
     */
    public int updateInstitutionReview(InstitutionReview institutionReview);

    /**
     * 删除机构评价
     *
     * @param reviewId 机构评价主键
     * @return 结果
     */
    public int deleteInstitutionReviewByReviewId(Long reviewId);

    /**
     * 批量删除机构评价
     *
     * @param reviewIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteInstitutionReviewByReviewIds(Long[] reviewIds);

    /**
     * 统计机构评价数量
     *
     * @param institutionId 机构ID
     * @param status 状态（可选）
     * @return 评价数量
     */
    public int countReviewsByInstitutionId(Long institutionId, Integer status);

    /**
     * 计算机构平均评分
     *
     * @param institutionId 机构ID
     * @param status 状态（可选，默认只计算已通过的）
     * @return 平均评分
     */
    public Double calculateAverageRatingByInstitutionId(Long institutionId, Integer status);

    /**
     * 批量更新评价审核状态
     *
     * @param reviewIds 评价ID数组
     * @param status 新状态
     * @param reviewRemark 审核备注
     * @param reviewBy 审核人
     * @return 结果
     */
    public int batchUpdateReviewStatus(Long[] reviewIds, Integer status, String reviewRemark, String reviewBy);

    /**
     * 查询用户的待评价订单列表
     *
     * @param userId 用户ID
     * @return 待评价订单列表
     */
    public List<Map<String, Object>> selectPendingEvaluationOrders(Long userId);
}