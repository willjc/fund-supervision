package com.ruoyi.web.controller.supervision;

import java.util.List;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.pension.InstitutionReview;
import com.ruoyi.service.pension.IInstitutionReviewService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 机构评价审核Controller
 *
 * @author ruoyi
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/supervision/review")
public class InstitutionReviewController extends BaseController
{
    @Autowired
    private IInstitutionReviewService institutionReviewService;

    /**
     * 查询机构评价审核列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:review:list')")
    @GetMapping("/list")
    public TableDataInfo list(InstitutionReview institutionReview)
    {
        startPage();
        List<InstitutionReview> list = institutionReviewService.selectInstitutionReviewWithRelationsList(institutionReview);
        return getDataTable(list);
    }

    /**
     * 导出机构评价审核列表
     */
    @PreAuthorize("@ss.hasPermi('supervision:review:export')")
    @Log(title = "机构评价审核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InstitutionReview institutionReview)
    {
        List<InstitutionReview> list = institutionReviewService.selectInstitutionReviewWithRelationsList(institutionReview);
        ExcelUtil<InstitutionReview> util = new ExcelUtil<InstitutionReview>(InstitutionReview.class);
        util.exportExcel(response, list, "机构评价审核数据");
    }

    /**
     * 获取机构评价审核详细信息
     */
    @PreAuthorize("@ss.hasPermi('supervision:review:query')")
    @GetMapping(value = "/{reviewId}")
    public AjaxResult getInfo(@PathVariable("reviewId") Long reviewId)
    {
        return success(institutionReviewService.selectInstitutionReviewWithRelationsByReviewId(reviewId));
    }

    /**
     * 审核通过机构评价
     */
    @PreAuthorize("@ss.hasPermi('supervision:review:approve')")
    @Log(title = "机构评价审核通过", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{reviewId}")
    public AjaxResult approve(@PathVariable Long reviewId, @RequestBody(required = false) InstitutionReview review)
    {
        // 查询评价信息
        InstitutionReview existReview = institutionReviewService.selectInstitutionReviewByReviewId(reviewId);
        if (existReview == null) {
            return error("评价信息不存在");
        }

        // 检查是否已审核
        if (existReview.isReviewed()) {
            return error("该评价已被审核，无法重复操作");
        }

        // 更新审核状态
        existReview.setStatus(InstitutionReview.STATUS_APPROVED);
        existReview.setReviewTime(new Date());
        existReview.setReviewBy(SecurityUtils.getUsername());

        // 设置审核意见
        if (review != null && review.getReviewRemark() != null) {
            existReview.setReviewRemark(review.getReviewRemark());
        }

        return toAjax(institutionReviewService.updateInstitutionReview(existReview));
    }

    /**
     * 审核拒绝机构评价
     */
    @PreAuthorize("@ss.hasPermi('supervision:review:reject')")
    @Log(title = "机构评价审核拒绝", businessType = BusinessType.UPDATE)
    @PutMapping("/reject/{reviewId}")
    public AjaxResult reject(@PathVariable Long reviewId, @RequestBody InstitutionReview review)
    {
        // 验证审核意见
        if (review == null || review.getReviewRemark() == null || review.getReviewRemark().trim().isEmpty()) {
            return error("拒绝审核时必须填写审核意见");
        }

        // 查询评价信息
        InstitutionReview existReview = institutionReviewService.selectInstitutionReviewByReviewId(reviewId);
        if (existReview == null) {
            return error("评价信息不存在");
        }

        // 检查是否已审核
        if (existReview.isReviewed()) {
            return error("该评价已被审核，无法重复操作");
        }

        // 更新审核状态
        existReview.setStatus(InstitutionReview.STATUS_REJECTED);
        existReview.setReviewTime(new Date());
        existReview.setReviewBy(SecurityUtils.getUsername());
        existReview.setReviewRemark(review.getReviewRemark().trim());

        return toAjax(institutionReviewService.updateInstitutionReview(existReview));
    }

    /**
     * 批量审核通过
     */
    @PreAuthorize("@ss.hasPermi('supervision:review:approve')")
    @Log(title = "批量审核通过", businessType = BusinessType.UPDATE)
    @PutMapping("/batchApprove")
    public AjaxResult batchApprove(@RequestBody Long[] reviewIds)
    {
        if (reviewIds == null || reviewIds.length == 0) {
            return error("请选择要审核的评价");
        }

        int successCount = 0;
        int skipCount = 0;
        String currentReviewBy = SecurityUtils.getUsername();
        Date currentTime = new Date();

        for (Long reviewId : reviewIds) {
            InstitutionReview existReview = institutionReviewService.selectInstitutionReviewByReviewId(reviewId);
            if (existReview != null && !existReview.isReviewed()) {
                existReview.setStatus(InstitutionReview.STATUS_APPROVED);
                existReview.setReviewTime(currentTime);
                existReview.setReviewBy(currentReviewBy);
                existReview.setReviewRemark("批量审核通过");

                int result = institutionReviewService.updateInstitutionReview(existReview);
                if (result > 0) {
                    successCount++;
                }
            } else {
                skipCount++;
            }
        }

        return success("批量审核完成，成功：" + successCount + "条，跳过：" + skipCount + "条");
    }

    /**
     * 删除机构评价审核
     */
    @PreAuthorize("@ss.hasPermi('supervision:review:remove')")
    @Log(title = "机构评价审核", businessType = BusinessType.DELETE)
    @DeleteMapping("/{reviewIds}")
    public AjaxResult remove(@PathVariable Long[] reviewIds)
    {
        return toAjax(institutionReviewService.deleteInstitutionReviewByReviewIds(reviewIds));
    }

    /**
     * 获取审核统计信息
     */
    @PreAuthorize("@ss.hasPermi('supervision:review:list')")
    @GetMapping("/statistics")
    public AjaxResult getStatistics()
    {
        InstitutionReview query = new InstitutionReview();
        List<InstitutionReview> allReviews = institutionReviewService.selectInstitutionReviewList(query);

        int totalCount = allReviews.size();
        int pendingCount = 0;
        int approvedCount = 0;
        int rejectedCount = 0;

        for (InstitutionReview review : allReviews) {
            if (InstitutionReview.STATUS_PENDING.equals(review.getStatus())) {
                pendingCount++;
            } else if (InstitutionReview.STATUS_APPROVED.equals(review.getStatus())) {
                approvedCount++;
            } else if (InstitutionReview.STATUS_REJECTED.equals(review.getStatus())) {
                rejectedCount++;
            }
        }

        java.util.Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("totalCount", totalCount);
        statistics.put("pendingCount", pendingCount);
        statistics.put("approvedCount", approvedCount);
        statistics.put("rejectedCount", rejectedCount);

        return success().put("data", statistics);
    }
}