package com.ruoyi.web.controller.pension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 民政监管-仪表板Controller
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@RestController
@RequestMapping("/pension/supervision/dashboard")
public class SupervisionDashboardController extends BaseController
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取仪表板概览数据 - 使用SQL直接查询统计
     */
    @PreAuthorize("@ss.hasPermi('pension:dashboard:query')")
    @GetMapping("/overview")
    public AjaxResult getOverview()
    {
        Map<String, Object> overview = new HashMap<>();

        // 1. 机构统计
        Map<String, Object> institutionStats = new HashMap<>();
        institutionStats.put("total", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM pension_institution", Integer.class));
        institutionStats.put("pending", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM pension_institution WHERE status = '0'", Integer.class));
        institutionStats.put("approved", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM pension_institution WHERE status = '1'", Integer.class));
        overview.put("institution", institutionStats);

        // 2. 老人统计
        Map<String, Object> elderStats = new HashMap<>();
        elderStats.put("total", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM elder_info", Integer.class));
        elderStats.put("active", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM elder_info WHERE status = '0'", Integer.class));
        overview.put("elder", elderStats);

        // 3. 资金划拨统计
        Map<String, Object> transferStats = new HashMap<>();
        transferStats.put("pending", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM fund_transfer WHERE transfer_status = '0'", Integer.class));
        transferStats.put("approved", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM fund_transfer WHERE transfer_status = '1'", Integer.class));

        BigDecimal totalTransferAmount = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(transfer_amount), 0) FROM fund_transfer WHERE transfer_status = '1'",
            BigDecimal.class);
        transferStats.put("totalAmount", totalTransferAmount);
        overview.put("transfer", transferStats);

        // 4. 退款统计
        Map<String, Object> refundStats = new HashMap<>();
        refundStats.put("pending", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM refund_record WHERE refund_status = '0'", Integer.class));
        refundStats.put("success", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM refund_record WHERE refund_status = '1'", Integer.class));

        BigDecimal totalRefundAmount = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(refund_amount), 0) FROM refund_record WHERE refund_status = '1'",
            BigDecimal.class);
        refundStats.put("totalAmount", totalRefundAmount);
        overview.put("refund", refundStats);

        // 5. 押金申请统计
        Map<String, Object> depositStats = new HashMap<>();
        depositStats.put("pending", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM deposit_apply WHERE apply_status = '0'", Integer.class));
        depositStats.put("approved", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM deposit_apply WHERE apply_status = '1'", Integer.class));
        overview.put("deposit", depositStats);

        // 6. 预警统计
        Map<String, Object> warningStats = new HashMap<>();
        warningStats.put("active", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM balance_warning WHERE warning_status = '0'", Integer.class));
        warningStats.put("resolved", jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM balance_warning WHERE warning_status = '1'", Integer.class));
        overview.put("warning", warningStats);

        return AjaxResult.success(overview);
    }

    /**
     * 获取待办事项统计
     */
    @PreAuthorize("@ss.hasPermi('pension:dashboard:query')")
    @GetMapping("/todos")
    public AjaxResult getTodos()
    {
        Map<String, Object> todos = new HashMap<>();

        // 待审批机构数量
        int pendingInstitutions = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM pension_institution WHERE status = '0'",
            Integer.class);
        todos.put("institutionCount", pendingInstitutions);

        // 待审批资金划拨数量
        int pendingTransfers = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM fund_transfer WHERE transfer_status = '0'",
            Integer.class);
        todos.put("transferCount", pendingTransfers);

        // 待审批退款数量
        int pendingRefunds = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM refund_record WHERE refund_status = '0'",
            Integer.class);
        todos.put("refundCount", pendingRefunds);

        // 待审批押金申请数量
        int pendingDeposits = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM deposit_apply WHERE apply_status = '0'",
            Integer.class);
        todos.put("depositCount", pendingDeposits);

        // 未处理预警数量
        int activeWarnings = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM balance_warning WHERE warning_status = '0'",
            Integer.class);
        todos.put("warningCount", activeWarnings);

        // 总待办数
        todos.put("totalCount", pendingInstitutions + pendingTransfers + pendingRefunds + pendingDeposits + activeWarnings);

        return AjaxResult.success(todos);
    }

    /**
     * 获取统计图表数据
     */
    @PreAuthorize("@ss.hasPermi('pension:dashboard:query')")
    @GetMapping("/charts")
    public AjaxResult getCharts()
    {
        Map<String, Object> charts = new HashMap<>();

        // 1. 机构状态分布
        List<Map<String, Object>> institutionByStatus = jdbcTemplate.queryForList(
            "SELECT " +
            "CASE status " +
            "WHEN '0' THEN '待审批' " +
            "WHEN '1' THEN '运营中' " +
            "WHEN '2' THEN '已暂停' " +
            "WHEN '3' THEN '已关闭' " +
            "ELSE '未知' END AS name, " +
            "COUNT(*) AS value " +
            "FROM pension_institution " +
            "GROUP BY status");
        charts.put("institutionByStatus", institutionByStatus);

        // 2. 资金划拨按类型统计
        List<Map<String, Object>> transferByType = jdbcTemplate.queryForList(
            "SELECT " +
            "CASE transfer_type " +
            "WHEN '1' THEN '自动划拨' " +
            "WHEN '2' THEN '手动划拨' " +
            "WHEN '3' THEN '特殊申请' " +
            "ELSE '其他' END AS name, " +
            "COALESCE(SUM(transfer_amount), 0) AS value " +
            "FROM fund_transfer " +
            "WHERE transfer_status = '1' " +
            "GROUP BY transfer_type");
        charts.put("transferByType", transferByType);

        // 3. 预警类型分布
        List<Map<String, Object>> warningByType = jdbcTemplate.queryForList(
            "SELECT " +
            "CASE warning_type " +
            "WHEN '1' THEN '余额不足' " +
            "WHEN '2' THEN '即将到期' " +
            "WHEN '3' THEN '异常划拨' " +
            "ELSE '其他' END AS name, " +
            "COUNT(*) AS value " +
            "FROM balance_warning " +
            "GROUP BY warning_type");
        charts.put("warningByLevel", warningByType);

        // 4. 押金申请类型分布
        List<Map<String, Object>> depositByType = jdbcTemplate.queryForList(
            "SELECT apply_type AS name, COUNT(*) AS value " +
            "FROM deposit_apply " +
            "WHERE apply_status = '0' " +
            "GROUP BY apply_type");
        charts.put("depositByType", depositByType);

        return AjaxResult.success(charts);
    }

    /**
     * 获取近7天审批趋势
     */
    @PreAuthorize("@ss.hasPermi('pension:dashboard:query')")
    @GetMapping("/trends")
    public AjaxResult getTrends()
    {
        Map<String, Object> trends = new HashMap<>();

        // 近7天资金划拨审批趋势
        List<Map<String, Object>> transferTrend = jdbcTemplate.queryForList(
            "SELECT DATE(approve_time) AS date, COUNT(*) AS count " +
            "FROM fund_transfer " +
            "WHERE approve_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "AND transfer_status = '1' " +
            "GROUP BY DATE(approve_time) " +
            "ORDER BY date");
        trends.put("transfers", transferTrend);

        // 近7天退款审批趋势
        List<Map<String, Object>> refundTrend = jdbcTemplate.queryForList(
            "SELECT DATE(approve_time) AS date, COUNT(*) AS count " +
            "FROM refund_record " +
            "WHERE approve_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "AND refund_status = '1' " +
            "GROUP BY DATE(approve_time) " +
            "ORDER BY date");
        trends.put("refunds", refundTrend);

        // 近7天押金审批趋势
        List<Map<String, Object>> depositTrend = jdbcTemplate.queryForList(
            "SELECT DATE(approve_time) AS date, COUNT(*) AS count " +
            "FROM deposit_apply " +
            "WHERE approve_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
            "AND apply_status = '1' " +
            "GROUP BY DATE(approve_time) " +
            "ORDER BY date");
        trends.put("deposits", depositTrend);

        return AjaxResult.success(trends);
    }
}
