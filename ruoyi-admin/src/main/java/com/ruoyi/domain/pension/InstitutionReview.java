package com.ruoyi.domain.pension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 机构评价对象 institution_review
 *
 * @author ruoyi
 * @date 2025-12-20
 */
public class InstitutionReview extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 审核状态常量 */
    public static final Integer STATUS_PENDING = 0;    // 待审核
    public static final Integer STATUS_APPROVED = 1;   // 已通过
    public static final Integer STATUS_REJECTED = 2;   // 已拒绝

    /** 评价ID */
    private Long reviewId;

    /** 关联订单号 */
    @Excel(name = "关联订单号")
    private String orderId;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long institutionId;

    /** 老人ID */
    @Excel(name = "老人ID")
    private Long elderId;

    /** 评价用户ID */
    @Excel(name = "评价用户ID")
    private Long userId;

    /** 环境评分(1-5星) */
    @Excel(name = "环境评分")
    private Integer environmentRating;

    /** 服务评分(1-5星) */
    @Excel(name = "服务评分")
    private Integer serviceRating;

    /** 价格评分(1-5星) */
    @Excel(name = "价格评分")
    private Integer priceRating;

    /** 平均评分 */
    @Excel(name = "平均评分")
    private BigDecimal averageRating;

    /** 评价内容 */
    @Excel(name = "评价内容")
    private String content;

    /** 评价图片(JSON数组) */
    private String images;

    /** 状态(0-待审核, 1-已通过, 2-已拒绝) */
    @Excel(name = "状态")
    private Integer status;

    /** 审核备注 */
    @Excel(name = "审核备注")
    private String reviewRemark;

    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "审核时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    /** 审核人 */
    @Excel(name = "审核人")
    private String reviewBy;

    /** 关联反馈ID（用于审核） */
    @Excel(name = "关联反馈ID")
    private Long feedbackId;

    /** 关联查询字段 */
    /** 机构名称 */
    @Excel(name = "机构名称")
    private String institutionName;

    /** 机构图片 */
    private String institutionImage;

    /** 老人姓名 */
    @Excel(name = "老人姓名")
    private String elderName;

    /** 用户名 */
    @Excel(name = "用户名")
    private String userName;

    /** 订单号 */
    @Excel(name = "订单号")
    private String orderNo;

    /** 图片列表（解析后的列表） */
    private List<ReviewImage> imageList;

    /** 图片对象类 */
    public static class ReviewImage {
        private String name;
        private String url;
        private String uid;

        public ReviewImage() {}

        public ReviewImage(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getUid() { return uid; }
        public void setUid(String uid) { this.uid = uid; }
    }

    public void setReviewId(Long reviewId)
    {
        this.reviewId = reviewId;
    }

    public Long getReviewId()
    {
        return reviewId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setInstitutionId(Long institutionId)
    {
        this.institutionId = institutionId;
    }

    public Long getInstitutionId()
    {
        return institutionId;
    }

    public void setElderId(Long elderId)
    {
        this.elderId = elderId;
    }

    public Long getElderId()
    {
        return elderId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setEnvironmentRating(Integer environmentRating)
    {
        this.environmentRating = environmentRating;
    }

    public Integer getEnvironmentRating()
    {
        return environmentRating;
    }

    public void setServiceRating(Integer serviceRating)
    {
        this.serviceRating = serviceRating;
    }

    public Integer getServiceRating()
    {
        return serviceRating;
    }

    public void setPriceRating(Integer priceRating)
    {
        this.priceRating = priceRating;
    }

    public Integer getPriceRating()
    {
        return priceRating;
    }

    public void setAverageRating(BigDecimal averageRating)
    {
        this.averageRating = averageRating;
    }

    public BigDecimal getAverageRating()
    {
        return averageRating;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }

    public void setImages(String images)
    {
        this.images = images;
    }

    public String getImages()
    {
        return images;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setReviewRemark(String reviewRemark)
    {
        this.reviewRemark = reviewRemark;
    }

    public String getReviewRemark()
    {
        return reviewRemark;
    }

    public void setReviewTime(Date reviewTime)
    {
        this.reviewTime = reviewTime;
    }

    public Date getReviewTime()
    {
        return reviewTime;
    }

    public void setReviewBy(String reviewBy)
    {
        this.reviewBy = reviewBy;
    }

    public String getReviewBy()
    {
        return reviewBy;
    }

    public void setFeedbackId(Long feedbackId)
    {
        this.feedbackId = feedbackId;
    }

    public Long getFeedbackId()
    {
        return feedbackId;
    }

    public void setInstitutionName(String institutionName)
    {
        this.institutionName = institutionName;
    }

    public String getInstitutionName()
    {
        return institutionName;
    }

    public void setInstitutionImage(String institutionImage)
    {
        this.institutionImage = institutionImage;
    }

    public String getInstitutionImage()
    {
        return institutionImage;
    }

    public void setElderName(String elderName)
    {
        this.elderName = elderName;
    }

    public String getElderName()
    {
        return elderName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo()
    {
        return orderNo;
    }

    public List<ReviewImage> getImageList() {
        return imageList;
    }

    public void setImageList(List<ReviewImage> imageList) {
        this.imageList = imageList;
    }

    /**
     * 获取审核状态文本
     */
    public String getStatusText() {
        if (status == null) {
            return "未知";
        }
        if (status.equals(STATUS_PENDING)) {
            return "待审核";
        } else if (status.equals(STATUS_APPROVED)) {
            return "已通过";
        } else if (status.equals(STATUS_REJECTED)) {
            return "已拒绝";
        } else {
            return "未知状态";
        }
    }

    /**
     * 判断是否已审核（通过或拒绝）
     */
    public boolean isReviewed() {
        return STATUS_APPROVED.equals(status) || STATUS_REJECTED.equals(status);
    }

    /**
     * 判断是否已通过审核
     */
    public boolean isApproved() {
        return STATUS_APPROVED.equals(status);
    }

    /**
     * 判断是否被拒绝
     */
    public boolean isRejected() {
        return STATUS_REJECTED.equals(status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("reviewId", getReviewId())
            .append("orderId", getOrderId())
            .append("institutionId", getInstitutionId())
            .append("elderId", getElderId())
            .append("userId", getUserId())
            .append("environmentRating", getEnvironmentRating())
            .append("serviceRating", getServiceRating())
            .append("priceRating", getPriceRating())
            .append("averageRating", getAverageRating())
            .append("content", getContent())
            .append("images", getImages())
            .append("status", getStatus())
            .append("reviewRemark", getReviewRemark())
            .append("reviewTime", getReviewTime())
            .append("reviewBy", getReviewBy())
            .append("feedbackId", getFeedbackId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("institutionName", getInstitutionName())
            .append("elderName", getElderName())
            .append("userName", getUserName())
            .toString();
    }
}