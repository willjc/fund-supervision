package com.ruoyi.service.pension.impl;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.mapper.pension.InstitutionReviewMapper;
import com.ruoyi.domain.pension.InstitutionReview;
import com.ruoyi.service.pension.IInstitutionReviewService;
import com.ruoyi.common.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 机构评价Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-20
 */
@Service
public class InstitutionReviewServiceImpl implements IInstitutionReviewService
{
    @Autowired
    private InstitutionReviewMapper institutionReviewMapper;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 查询机构评价
     *
     * @param reviewId 机构评价主键
     * @return 机构评价
     */
    @Override
    public InstitutionReview selectInstitutionReviewByReviewId(Long reviewId)
    {
        return institutionReviewMapper.selectInstitutionReviewByReviewId(reviewId);
    }

    /**
     * 根据评价ID查询机构评价（关联查询）
     *
     * @param reviewId 机构评价主键
     * @return 机构评价
     */
    @Override
    public InstitutionReview selectInstitutionReviewWithRelationsByReviewId(Long reviewId)
    {
        InstitutionReview review = institutionReviewMapper.selectInstitutionReviewWithRelationsByReviewId(reviewId);

        // 解析图片JSON
        if (review != null && StringUtils.isNotEmpty(review.getImages())) {
            try {
                List<InstitutionReview.ReviewImage> imageList = objectMapper.readValue(
                    review.getImages(),
                    new TypeReference<List<InstitutionReview.ReviewImage>>() {}
                );
                review.setImageList(imageList);
            } catch (Exception e) {
                System.err.println("解析评价图片JSON失败: " + e.getMessage());
            }
        }

        return review;
    }

    /**
     * 根据订单ID查询机构评价
     *
     * @param orderId 订单ID
     * @return 机构评价
     */
    @Override
    public InstitutionReview selectInstitutionReviewByOrderId(String orderId)
    {
        InstitutionReview review = institutionReviewMapper.selectInstitutionReviewByOrderId(orderId);
        if (review != null && StringUtils.isNotEmpty(review.getImages())) {
            try {
                List<InstitutionReview.ReviewImage> imageList = objectMapper.readValue(
                    review.getImages(),
                    new TypeReference<List<InstitutionReview.ReviewImage>>() {}
                );
                review.setImageList(imageList);
            } catch (Exception e) {
                System.err.println("解析评价图片JSON失败: " + e.getMessage());
            }
        }
        return review;
    }

    /**
     * 查询机构评价列表
     *
     * @param institutionReview 机构评价
     * @return 机构评价
     */
    @Override
    public List<InstitutionReview> selectInstitutionReviewList(InstitutionReview institutionReview)
    {
        return institutionReviewMapper.selectInstitutionReviewList(institutionReview);
    }

    /**
     * 查询机构评价列表（关联查询）
     *
     * @param institutionReview 机构评价
     * @return 机构评价集合
     */
    @Override
    public List<InstitutionReview> selectInstitutionReviewWithRelationsList(InstitutionReview institutionReview)
    {
        List<InstitutionReview> reviews = institutionReviewMapper.selectInstitutionReviewWithRelationsList(institutionReview);

        // 解析图片JSON
        for (InstitutionReview review : reviews) {
            if (StringUtils.isNotEmpty(review.getImages())) {
                try {
                    List<InstitutionReview.ReviewImage> imageList = objectMapper.readValue(
                        review.getImages(),
                        new TypeReference<List<InstitutionReview.ReviewImage>>() {}
                    );
                    review.setImageList(imageList);
                } catch (Exception e) {
                    System.err.println("解析评价图片JSON失败: " + e.getMessage());
                }
            }
        }

        return reviews;
    }

    /**
     * 根据机构ID查询已通过的评价列表
     *
     * @param institutionId 机构ID
     * @param limit 限制数量
     * @return 评价列表
     */
    @Override
    public List<InstitutionReview> selectApprovedReviewsByInstitutionId(Long institutionId, Integer limit)
    {
        List<InstitutionReview> reviews = institutionReviewMapper.selectApprovedReviewsByInstitutionId(institutionId, limit);

        // 解析图片JSON
        for (InstitutionReview review : reviews) {
            if (StringUtils.isNotEmpty(review.getImages())) {
                try {
                    List<InstitutionReview.ReviewImage> imageList = objectMapper.readValue(
                        review.getImages(),
                        new TypeReference<List<InstitutionReview.ReviewImage>>() {}
                    );
                    review.setImageList(imageList);
                } catch (Exception e) {
                    System.err.println("解析评价图片JSON失败: " + e.getMessage());
                }
            }
        }

        return reviews;
    }

    /**
     * 新增机构评价
     *
     * @param institutionReview 机构评价
     * @return 结果
     */
    @Override
    public int insertInstitutionReview(InstitutionReview institutionReview)
    {
        institutionReview.setCreateTime(DateUtils.getNowDate());
        return institutionReviewMapper.insertInstitutionReview(institutionReview);
    }

    /**
     * 修改机构评价
     *
     * @param institutionReview 机构评价
     * @return 结果
     */
    @Override
    public int updateInstitutionReview(InstitutionReview institutionReview)
    {
        institutionReview.setUpdateTime(DateUtils.getNowDate());
        return institutionReviewMapper.updateInstitutionReview(institutionReview);
    }

    /**
     * 批量删除机构评价
     *
     * @param reviewIds 需要删除的机构评价主键
     * @return 结果
     */
    @Override
    public int deleteInstitutionReviewByReviewIds(Long[] reviewIds)
    {
        return institutionReviewMapper.deleteInstitutionReviewByReviewIds(reviewIds);
    }

    /**
     * 删除机构评价信息
     *
     * @param reviewId 机构评价主键
     * @return 结果
     */
    @Override
    public int deleteInstitutionReviewByReviewId(Long reviewId)
    {
        return institutionReviewMapper.deleteInstitutionReviewByReviewId(reviewId);
    }

    /**
     * 统计机构评价数量
     *
     * @param institutionId 机构ID
     * @param status 状态（可选）
     * @return 评价数量
     */
    @Override
    public int countReviewsByInstitutionId(Long institutionId, Integer status)
    {
        return institutionReviewMapper.countReviewsByInstitutionId(institutionId, status);
    }

    /**
     * 计算机构平均评分
     *
     * @param institutionId 机构ID
     * @param status 状态（可选，默认只计算已通过的）
     * @return 平均评分
     */
    @Override
    public Double calculateAverageRatingByInstitutionId(Long institutionId, Integer status)
    {
        // 如果不指定状态，默认只计算已通过的评价
        if (status == null) {
            status = 1;
        }
        return institutionReviewMapper.calculateAverageRatingByInstitutionId(institutionId, status);
    }

    /**
     * 提交评价（创建待审核的评价）
     *
     * @param institutionReview 评价信息
     * @return 结果
     */
    @Override
    @Transactional
    public int submitReview(InstitutionReview institutionReview)
    {
        // 设置初始状态为待审核
        institutionReview.setStatus(0);

        // 处理图片数据
        if (institutionReview.getImageList() != null && !institutionReview.getImageList().isEmpty()) {
            try {
                String imagesJson = objectMapper.writeValueAsString(institutionReview.getImageList());
                institutionReview.setImages(imagesJson);
            } catch (Exception e) {
                System.err.println("转换评价图片为JSON失败: " + e.getMessage());
                institutionReview.setImages("[]");
            }
        } else {
            institutionReview.setImages("[]");
        }

        return insertInstitutionReview(institutionReview);
    }

    /**
     * 审核评价
     *
     * @param reviewIds 评价ID数组
     * @param approved 是否通过
     * @param reviewRemark 审核备注
     * @param reviewBy 审核人
     * @return 结果
     */
    @Override
    @Transactional
    public int approveReviews(Long[] reviewIds, boolean approved, String reviewRemark, String reviewBy)
    {
        Integer status = approved ? 1 : 2;
        return institutionReviewMapper.batchUpdateReviewStatus(reviewIds, status, reviewRemark, reviewBy);
    }

    /**
     * 获取机构评价统计信息
     *
     * @param institutionId 机构ID
     * @return 统计信息
     */
    @Override
    public Map<String, Object> getInstitutionReviewStatistics(Long institutionId)
    {
        Map<String, Object> statistics = new HashMap<>();

        // 总评价数（已通过的）
        int totalReviews = countReviewsByInstitutionId(institutionId, 1);
        statistics.put("totalReviews", totalReviews);

        // 平均评分（已通过的）
        Double averageRating = calculateAverageRatingByInstitutionId(institutionId, 1);
        statistics.put("averageRating", averageRating != null ? averageRating : 0.0);

        // 获取最新的几条评价
        List<InstitutionReview> recentReviews = selectApprovedReviewsByInstitutionId(institutionId, 5);
        statistics.put("recentReviews", recentReviews);

        // 五星评价数量（简化的评分分布）
        Map<String, Integer> ratingDistribution = new HashMap<>();
        ratingDistribution.put("5", 0);
        ratingDistribution.put("4", 0);
        ratingDistribution.put("3", 0);
        ratingDistribution.put("2", 0);
        ratingDistribution.put("1", 0);

        List<InstitutionReview> allApprovedReviews = selectApprovedReviewsByInstitutionId(institutionId, null);
        for (InstitutionReview review : allApprovedReviews) {
            int rating = (int) Math.round(review.getAverageRating().doubleValue());
            String ratingKey = String.valueOf(Math.max(1, Math.min(5, rating)));
            ratingDistribution.put(ratingKey, ratingDistribution.get(ratingKey) + 1);
        }

        statistics.put("ratingDistribution", ratingDistribution);

        return statistics;
    }

    @Override
    public List<Map<String, Object>> selectPendingEvaluationOrders(Long userId)
    {
        return institutionReviewMapper.selectPendingEvaluationOrders(userId);
    }
}