package com.ruoyi.service.pension;

import java.util.List;
import com.ruoyi.domain.pension.InstitutionReview;

/**
 * 机构评价Service接口
 *
 * @author ruoyi
 * @date 2025-12-20
 */
public interface IInstitutionReviewService
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
     * 批量删除机构评价
     *
     * @param reviewIds 需要删除的机构评价主键集合
     * @return 结果
     */
    public int deleteInstitutionReviewByReviewIds(Long[] reviewIds);

    /**
     * 删除机构评价信息
     *
     * @param reviewId 机构评价主键
     * @return 结果
     */
    public int deleteInstitutionReviewByReviewId(Long reviewId);

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
     * 提交评价（创建待审核的评价和对应的反馈）
     *
     * @param institutionReview 评价信息
     * @return 结果
     */
    public int submitReview(InstitutionReview institutionReview);

    /**
     * 审核评价
     *
     * @param reviewIds 评价ID数组
     * @param approved 是否通过
     * @param reviewRemark 审核备注
     * @param reviewBy 审核人
     * @return 结果
     */
    public int approveReviews(Long[] reviewIds, boolean approved, String reviewRemark, String reviewBy);

    /**
     * 获取机构评价统计信息
     *
     * @param institutionId 机构ID
     * @return 统计信息
     */
    public java.util.Map<String, Object> getInstitutionReviewStatistics(Long institutionId);

    /**
     * 查询用户的待评价订单列表
     *
     * @param userId 用户ID
     * @return 待评价订单列表
     */
    public java.util.List<java.util.Map<String, Object>> selectPendingEvaluationOrders(Long userId);
}