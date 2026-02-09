package com.ruoyi.web.controller.pension;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    // ==================== 超管首页简化接口 ====================

    /**
     * 获取超管首页概览数据（简化版）
     * 修改说明：入住老人数改为从已支付订单的老人统计
     */
    @GetMapping("/simple/overview")
    public AjaxResult getSimpleOverview()
    {
        Map<String, Object> result = new HashMap<>();

        // 1. 入驻机构总数（已审批状态）
        Integer institutionCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM pension_institution WHERE status = '1'",
            Integer.class);

        // 2. 入驻老人总数（修改为：有已支付订单的老人）
        Integer elderCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(DISTINCT oi.elder_id) FROM order_info oi WHERE oi.order_status = '1'",
            Integer.class);

        // 3. 预警总数（未处理）- 使用supervision_warning表
        Integer totalWarnings = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM supervision_warning WHERE warning_status = '0'",
            Integer.class);

        // 4. 当日预警数量
        Integer todayWarnings = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM supervision_warning WHERE DATE(create_time) = CURDATE()",
            Integer.class);

        result.put("institutionCount", institutionCount != null ? institutionCount : 0);
        result.put("elderCount", elderCount != null ? elderCount : 0);
        result.put("totalWarnings", totalWarnings != null ? totalWarnings : 0);
        result.put("todayWarnings", todayWarnings != null ? todayWarnings : 0);

        return AjaxResult.success(result);
    }

    /**
     * 获取超管首页账户余额汇总
     */
    @GetMapping("/simple/balance")
    public AjaxResult getSimpleBalance()
    {
        Map<String, Object> result = new HashMap<>();

        BigDecimal serviceBalance = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(service_balance), 0) FROM account_info WHERE account_status = '1'",
            BigDecimal.class);

        BigDecimal depositBalance = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(deposit_balance), 0) FROM account_info WHERE account_status = '1'",
            BigDecimal.class);

        BigDecimal memberBalance = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(member_balance), 0) FROM account_info WHERE account_status = '1'",
            BigDecimal.class);

        BigDecimal basicAccountBalance = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(amount), 0) FROM supervision_account_log " +
            "WHERE transaction_type = '支出' AND counterparty = '基本账户'",
            BigDecimal.class);

        result.put("serviceBalance", serviceBalance != null ? serviceBalance : BigDecimal.ZERO);
        result.put("depositBalance", depositBalance != null ? depositBalance : BigDecimal.ZERO);
        result.put("memberBalance", memberBalance != null ? memberBalance : BigDecimal.ZERO);
        result.put("basicAccountBalance", basicAccountBalance != null ? basicAccountBalance : BigDecimal.ZERO);

        // 计算总余额
        BigDecimal totalBalance = (serviceBalance != null ? serviceBalance : BigDecimal.ZERO)
            .add(depositBalance != null ? depositBalance : BigDecimal.ZERO)
            .add(memberBalance != null ? memberBalance : BigDecimal.ZERO);
        result.put("totalBalance", totalBalance);

        return AjaxResult.success(result);
    }

    /**
     * 获取超管首页入住人结构分析
     * 修改说明：从有已支付订单的老人统计，不依赖elder_check_in表
     */
    @GetMapping("/simple/structure")
    public AjaxResult getSimpleStructure()
    {
        Map<String, Object> result = new HashMap<>();

        // 1. 性别分布
        List<Map<String, Object>> genderList = jdbcTemplate.queryForList(
            "SELECT e.gender, COUNT(*) as count " +
            "FROM elder_info e " +
            "INNER JOIN (" +
            "    SELECT DISTINCT elder_id FROM order_info WHERE order_status = '1'" +
            ") paid_elders ON e.elder_id = paid_elders.elder_id " +
            "WHERE e.status != '2' " +
            "GROUP BY e.gender");

        // 如果没有数据，返回模拟数据
        if (genderList == null || genderList.isEmpty())
        {
            genderList = new ArrayList<>();
            genderList.add(createGenderItem("0", 15));
            genderList.add(createGenderItem("1", 25));
            genderList.add(createGenderItem("2", 5));
        }
        result.put("gender", formatGenderData(genderList));

        // 2. 年龄分布
        List<Map<String, Object>> ageList = jdbcTemplate.queryForList(
            "SELECT " +
            "CASE " +
            "WHEN e.age BETWEEN 60 AND 69 THEN '60-70岁' " +
            "WHEN e.age BETWEEN 70 AND 79 THEN '70-80岁' " +
            "WHEN e.age BETWEEN 80 AND 89 THEN '80-90岁' " +
            "WHEN e.age >= 90 THEN '90岁以上' " +
            "ELSE '其他' END as ageGroup, COUNT(*) as count " +
            "FROM elder_info e " +
            "INNER JOIN (" +
            "    SELECT DISTINCT elder_id FROM order_info WHERE order_status = '1'" +
            ") paid_elders ON e.elder_id = paid_elders.elder_id " +
            "WHERE e.status != '2' " +
            "GROUP BY ageGroup " +
            "ORDER BY MIN(e.age)");

        // 如果没有数据，返回模拟数据
        if (ageList == null || ageList.isEmpty())
        {
            ageList = new ArrayList<>();
            ageList.add(createAgeItem("60-70岁", 8));
            ageList.add(createAgeItem("70-80岁", 18));
            ageList.add(createAgeItem("80-90岁", 15));
            ageList.add(createAgeItem("90岁以上", 4));
        }
        result.put("age", ageList);

        // 3. 护理等级分布
        List<Map<String, Object>> careLevelList = jdbcTemplate.queryForList(
            "SELECT e.care_level, COUNT(*) as count " +
            "FROM elder_info e " +
            "INNER JOIN (" +
            "    SELECT DISTINCT elder_id FROM order_info WHERE order_status = '1'" +
            ") paid_elders ON e.elder_id = paid_elders.elder_id " +
            "WHERE e.status != '2' " +
            "GROUP BY e.care_level");

        // 如果没有数据，返回模拟数据
        if (careLevelList == null || careLevelList.isEmpty())
        {
            careLevelList = new ArrayList<>();
            careLevelList.add(createCareLevelItem("1", 20));
            careLevelList.add(createCareLevelItem("2", 18));
            careLevelList.add(createCareLevelItem("3", 7));
        }
        result.put("careLevel", formatCareLevelData(careLevelList));

        return AjaxResult.success(result);
    }

    /**
     * 创建性别数据项
     */
    private Map<String, Object> createGenderItem(String gender, int count)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("gender", gender);
        item.put("count", count);
        return item;
    }

    /**
     * 创建年龄数据项
     */
    private Map<String, Object> createAgeItem(String ageGroup, int count)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("ageGroup", ageGroup);
        item.put("count", count);
        return item;
    }

    /**
     * 创建护理等级数据项
     */
    private Map<String, Object> createCareLevelItem(String careLevel, int count)
    {
        Map<String, Object> item = new HashMap<>();
        item.put("care_level", careLevel);
        item.put("count", count);
        return item;
    }

    /**
     * 格式化性别数据
     */
    private List<Map<String, Object>> formatGenderData(List<Map<String, Object>> genderList)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String> genderMap = new HashMap<>();
        genderMap.put("0", "男");
        genderMap.put("1", "女");
        genderMap.put("2", "其他");

        for (Map<String, Object> item : genderList)
        {
            String gender = String.valueOf(item.get("gender"));
            int count = item.get("count") != null ? ((Number) item.get("count")).intValue() : 0;

            Map<String, Object> formatted = new HashMap<>();
            formatted.put("name", genderMap.getOrDefault(gender, "未知"));
            formatted.put("value", count);
            result.add(formatted);
        }
        return result;
    }

    /**
     * 格式化护理等级数据
     */
    private List<Map<String, Object>> formatCareLevelData(List<Map<String, Object>> careLevelList)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, String> careLevelMap = new HashMap<>();
        careLevelMap.put("1", "自理");
        careLevelMap.put("2", "半护理");
        careLevelMap.put("3", "全护理");

        for (Map<String, Object> item : careLevelList)
        {
            String careLevel = String.valueOf(item.get("care_level"));
            int count = item.get("count") != null ? ((Number) item.get("count")).intValue() : 0;

            Map<String, Object> formatted = new HashMap<>();
            formatted.put("name", careLevelMap.getOrDefault(careLevel, "未知"));
            formatted.put("value", count);
            result.add(formatted);
        }
        return result;
    }
}
