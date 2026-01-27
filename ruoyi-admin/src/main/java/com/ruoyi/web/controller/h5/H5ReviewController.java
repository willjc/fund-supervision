package com.ruoyi.web.controller.h5;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.domain.pension.InstitutionReview;
import com.ruoyi.service.pension.IInstitutionReviewService;
import com.ruoyi.service.IOrderInfoService;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * H5评价Controller
 *
 * @author ruoyi
 * @date 2025-12-20
 */
@RestController
@RequestMapping("/h5/review")
public class H5ReviewController extends BaseController
{
    @Autowired
    private IInstitutionReviewService institutionReviewService;

    @Autowired
    private IOrderInfoService orderInfoService;

    /**
     * 查询订单评价状态
     */
    @GetMapping("/order/{orderId}")
    public AjaxResult getReviewByOrderId(@PathVariable("orderId") String orderId)
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return AjaxResult.error("用户未登录或身份验证失败");
        }

        InstitutionReview review = institutionReviewService.selectInstitutionReviewByOrderId(orderId);
        if (review != null && !review.getUserId().equals(userId)) {
            return AjaxResult.error("无权限查看该评价");
        }

        return AjaxResult.success(review);
    }

    /**
     * 创建评价
     */
    @Log(title = "创建评价", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult createReview(@RequestBody InstitutionReview institutionReview)
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return AjaxResult.error("用户未登录或身份验证失败");
        }

        // 设置用户ID
        institutionReview.setUserId(userId);

        // 从订单中获取机构ID和老人ID
        // 将前端传入的orderId转换为Long类型
        Long orderId = null;
        try {
            orderId = Long.valueOf(institutionReview.getOrderId());
        } catch (NumberFormatException e) {
            return AjaxResult.error("订单ID格式错误");
        }

        OrderInfo order = orderInfoService.selectOrderInfoByOrderId(orderId);
        if (order == null) {
            return AjaxResult.error("订单不存在");
        }
        institutionReview.setInstitutionId(order.getInstitutionId());
        institutionReview.setElderId(order.getElderId());

        // 检查该订单是否已经评价过
        InstitutionReview existingReview = institutionReviewService.selectInstitutionReviewByOrderId(institutionReview.getOrderId());
        if (existingReview != null) {
            return AjaxResult.error("该订单已经评价过了");
        }

        // 验证评分范围
        if (institutionReview.getEnvironmentRating() == null ||
            institutionReview.getServiceRating() == null ||
            institutionReview.getPriceRating() == null ||
            institutionReview.getEnvironmentRating() < 1 || institutionReview.getEnvironmentRating() > 5 ||
            institutionReview.getServiceRating() < 1 || institutionReview.getServiceRating() > 5 ||
            institutionReview.getPriceRating() < 1 || institutionReview.getPriceRating() > 5) {
            return AjaxResult.error("评分必须在1-5星之间");
        }

        // 验证图片数量（最多9张）
        if (institutionReview.getImageList() != null && institutionReview.getImageList().size() > 9) {
            return AjaxResult.error("最多只能上传9张图片");
        }

        // 提��评价
        int result = institutionReviewService.submitReview(institutionReview);
        if (result > 0) {
            return AjaxResult.success("评价提交成功，等待审核");
        } else {
            return AjaxResult.error("评价提交失败");
        }
    }

    /**
     * 获取��构评价列表（仅显示已通过的）
     */
    @GetMapping("/list/{institutionId}")
    public AjaxResult getApprovedReviewList(@PathVariable("institutionId") Long institutionId)
    {
        try {
            InstitutionReview query = new InstitutionReview();
            query.setInstitutionId(institutionId);
            query.setStatus(1); // 只查询已通过的

            List<InstitutionReview> list = institutionReviewService.selectInstitutionReviewWithRelationsList(query);

            // 与用户评价API保持一致的返回格式
            Map<String, Object> result = new HashMap<>();
            result.put("rows", list);
            result.put("total", list.size());

            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("获取评价列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取机构评价统计信息
     */
    @GetMapping("/statistics/{institutionId}")
    public AjaxResult getReviewStatistics(@PathVariable("institutionId") Long institutionId)
    {
        Map<String, Object> statistics = institutionReviewService.getInstitutionReviewStatistics(institutionId);
        return AjaxResult.success(statistics);
    }

    /**
     * 获取机构最新评价（限制数量）
     */
    @GetMapping("/latest/{institutionId}")
    public AjaxResult getLatestReviews(@PathVariable("institutionId") Long institutionId, Integer limit)
    {
        if (limit == null || limit <= 0) {
            limit = 5;
        }
        if (limit > 20) {
            limit = 20; // 最大限制20条
        }

        List<InstitutionReview> reviews = institutionReviewService.selectApprovedReviewsByInstitutionId(institutionId, limit);
        return AjaxResult.success(reviews);
    }

    /**
     * 获取用户的评价记录
     */
    @GetMapping("/user/list")
    public AjaxResult getUserReviewList()
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return AjaxResult.error("用户未登录");
        }

        try {
            InstitutionReview query = new InstitutionReview();
            query.setUserId(userId);
            query.setStatus(null); // 查询所有状态的评价

            List<InstitutionReview> list = institutionReviewService.selectInstitutionReviewWithRelationsList(query);

            // 直接返回AjaxResult，包装成前端期望的格式
            Map<String, Object> result = new HashMap<>();
            result.put("rows", list);
            result.put("total", list.size());

            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("获取评价记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的待评价订单列表
     */
    @GetMapping("/pending/list")
    public AjaxResult getPendingEvaluationList()
    {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return AjaxResult.error("用户未登录");
        }

        try {
            // 查询用户已支付但未评价的订单
            List<Map<String, Object>> pendingOrders = institutionReviewService.selectPendingEvaluationOrders(userId);

            Map<String, Object> result = new HashMap<>();
            result.put("rows", pendingOrders);
            result.put("total", pendingOrders.size());

            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("获取待评价订单失败: " + e.getMessage());
        }
    }

    /**
     * 导出机构评价列表
     */
    @Log(title = "机构评价", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void exportReview(HttpServletResponse response, InstitutionReview institutionReview)
    {
        List<InstitutionReview> list = institutionReviewService.selectInstitutionReviewList(institutionReview);
        ExcelUtil<InstitutionReview> util = new ExcelUtil<InstitutionReview>(InstitutionReview.class);
        util.exportExcel(response, list, "机构评价数据");
    }
}