package com.ruoyi.web.controller.pension;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import com.ruoyi.mapper.pension.DataStatisticsMapper;

/**
 * 数据统计Controller
 *
 * @author ruoyi
 * @date 2025-02-04
 */
@RestController
@RequestMapping("/pension/dataStatistics")
public class DataStatisticsController extends BaseController
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataStatisticsMapper dataStatisticsMapper;

    /**
     * 总体概览列表
     */
    @PreAuthorize("@ss.hasPermi('data:statistics:overall')")
    @GetMapping("/overall/list")
    public TableDataInfo overallList(
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate)
    {
        startPage();

        List<Map<String, Object>> list = dataStatisticsMapper.selectOverallList(districtCode);

        // 添加大额资金变化记录
        for (Map<String, Object> row : list) {
            String dCode = (String) row.get("district_code");
            StringBuilder fundSql = new StringBuilder();
            fundSql.append("SELECT COUNT(*) as large_change_count, ");
            fundSql.append("COALESCE(SUM(CASE WHEN transaction_type = '收入' THEN amount ELSE 0 END), 0) as large_income, ");
            fundSql.append("COALESCE(SUM(CASE WHEN transaction_type = '支出' THEN amount ELSE 0 END), 0) as large_outcome ");
            fundSql.append("FROM supervision_account_log sal ");
            fundSql.append("INNER JOIN pension_institution pi ON pi.institution_id = sal.institution_id ");
            fundSql.append("WHERE pi.area_code COLLATE utf8mb4_general_ci = ? AND sal.amount >= 10000 ");

            List<Object> fundArgs = new ArrayList<>();
            fundArgs.add(dCode);
            if (StringUtils.isNotEmpty(startDate)) {
                fundSql.append("AND DATE(transaction_time) >= ? ");
                fundArgs.add(startDate);
            }
            if (StringUtils.isNotEmpty(endDate)) {
                fundSql.append("AND DATE(transaction_time) <= ? ");
                fundArgs.add(endDate);
            }

            try {
                Map<String, Object> fundData = jdbcTemplate.queryForMap(fundSql.toString(), fundArgs.toArray());
                row.put("large_change_count", fundData.get("large_change_count"));
                row.put("large_income", fundData.get("large_income"));
                row.put("large_outcome", fundData.get("large_outcome"));
            } catch (Exception e) {
                row.put("large_change_count", 0);
                row.put("large_income", BigDecimal.ZERO);
                row.put("large_outcome", BigDecimal.ZERO);
            }
        }

        return getDataTable(list);
    }

    /**
     * 机构情况列表
     */
    @PreAuthorize("@ss.hasPermi('data:statistics:institution')")
    @GetMapping("/institution/list")
    public TableDataInfo institutionList(
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate)
    {
        startPage();
        List<Map<String, Object>> list = dataStatisticsMapper.selectInstitutionList(districtCode, startDate, endDate);
        return getDataTable(list);
    }

    /**
     * 资金情况列表
     */
    @PreAuthorize("@ss.hasPermi('data:statistics:fund')")
    @GetMapping("/fund/list")
    public TableDataInfo fundList(
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate)
    {
        startPage();
        List<Map<String, Object>> list = dataStatisticsMapper.selectFundList(districtCode, startDate, endDate);
        return getDataTable(list);
    }

    /**
     * 预警情况列表
     */
    @PreAuthorize("@ss.hasPermi('data:statistics:warning')")
    @GetMapping("/warning/list")
    public TableDataInfo warningList(
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate)
    {
        startPage();
        List<Map<String, Object>> list = dataStatisticsMapper.selectWarningList(districtCode, startDate, endDate);
        return getDataTable(list);
    }

    /**
     * 机构详细列表
     */
    @PreAuthorize("@ss.hasPermi('data:statistics:institutionList')")
    @GetMapping("/institutionList/list")
    public TableDataInfo institutionDetailList(
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String institutionName)
    {
        startPage();
        List<Map<String, Object>> list = dataStatisticsMapper.selectInstitutionDetailList(districtCode, institutionName);
        return getDataTable(list);
    }

    /**
     * 老人详细列表
     */
    @PreAuthorize("@ss.hasPermi('data:statistics:elderList')")
    @GetMapping("/elderList/list")
    public TableDataInfo elderDetailList(
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String elderName,
            @RequestParam(required = false) String institutionId)
    {
        startPage();
        List<Map<String, Object>> list = dataStatisticsMapper.selectElderDetailList(districtCode, elderName, institutionId);
        return getDataTable(list);
    }

    /**
     * 资金详细列表
     */
    @PreAuthorize("@ss.hasPermi('data:statistics:fundList')")
    @GetMapping("/fundList/list")
    public TableDataInfo fundDetailList(
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String elderName)
    {
        startPage();
        List<Map<String, Object>> list = dataStatisticsMapper.selectFundDetailList(districtCode, elderName);
        return getDataTable(list);
    }

    /**
     * 导出总体概览CSV
     */
    @Log(title = "数据统计-总体概览", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('data:statistics:overall')")
    @PostMapping("/overall/export")
    public void exportOverall(HttpServletResponse response,
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ar.area_code as district_code, ar.area_name as district_name, ");
        sql.append("COUNT(DISTINCT p.institution_id) as institution_count, ");
        sql.append("COALESCE(SUM(ai.total_balance), 0) as total_fund, ");
        sql.append("COALESCE(SUM(ai.service_balance), 0) as service_balance, ");
        sql.append("COALESCE(SUM(ai.deposit_balance), 0) as deposit_balance, ");
        sql.append("COALESCE(SUM(ai.member_balance), 0) as member_balance, ");
        sql.append("COALESCE(COUNT(DISTINCT eci.elder_id), 0) as elder_count ");
        sql.append("FROM area_street ar ");
        sql.append("LEFT JOIN pension_institution p ON p.area_code COLLATE utf8mb4_general_ci = ar.area_code COLLATE utf8mb4_general_ci ");
        sql.append("LEFT JOIN account_info ai ON ai.institution_id = p.institution_id ");
        sql.append("LEFT JOIN elder_check_in eci ON eci.institution_id = p.institution_id ");
        sql.append("WHERE ar.area_name IS NOT NULL ");

        List<Object> args = new ArrayList<>();
        if (StringUtils.isNotEmpty(districtCode)) {
            sql.append("AND ar.area_code COLLATE utf8mb4_general_ci = ? ");
            args.add(districtCode);
        }

        sql.append("GROUP BY ar.area_code, ar.area_name ");
        sql.append("ORDER BY MIN(ar.sort_order)");

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), args.toArray());

        exportCsv(response, list, "总体概览",
            new String[]{"区县代码", "区县名称", "机构数量", "资金总额", "服务费余额", "押金余额", "会员费余额", "老人数量"},
            new String[]{"district_code", "district_name", "institution_count", "total_fund", "service_balance", "deposit_balance", "member_balance", "elder_count"});
    }

    /**
     * 导出机构情况CSV
     */
    @Log(title = "数据统计-机构情况", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('data:statistics:institution')")
    @PostMapping("/institution/export")
    public void exportInstitution(HttpServletResponse response,
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ar.area_code as district_code, ar.area_name as district_name, ");
        sql.append("COUNT(DISTINCT p.institution_id) as institution_count, ");
        sql.append("COALESCE(COUNT(DISTINCT eci.elder_id), 0) as elder_count, ");
        sql.append("COALESCE(SUM(CASE WHEN p.status = '3' THEN 1 ELSE 0 END), 0) as rejected_count, ");
        sql.append("COALESCE(SUM(CASE WHEN p.blacklist_flag = '1' THEN 1 ELSE 0 END), 0) as blacklist_count, ");
        sql.append("COALESCE(COUNT(DISTINCT CASE WHEN p.supervise_bank IS NOT NULL AND p.supervise_bank != '' THEN p.supervise_bank END), 0) as bank_count, ");
        sql.append("COALESCE(COUNT(DISTINCT pc.complaint_id), 0) as complaint_count ");
        sql.append("FROM area_street ar ");
        sql.append("LEFT JOIN pension_institution p ON p.area_code COLLATE utf8mb4_general_ci = ar.area_code COLLATE utf8mb4_general_ci ");
        sql.append("LEFT JOIN elder_check_in eci ON eci.institution_id = p.institution_id ");
        sql.append("LEFT JOIN pension_complaint pc ON pc.institution_id = p.institution_id ");
        sql.append("WHERE ar.area_name IS NOT NULL ");

        List<Object> args = new ArrayList<>();
        if (StringUtils.isNotEmpty(districtCode)) {
            sql.append("AND ar.area_code COLLATE utf8mb4_general_ci = ? ");
            args.add(districtCode);
        }
        if (StringUtils.isNotEmpty(startDate)) {
            sql.append("AND (p.create_time >= ? OR pc.create_time >= ?) ");
            args.add(startDate);
            args.add(startDate);
        }
        if (StringUtils.isNotEmpty(endDate)) {
            sql.append("AND (p.create_time <= ? OR pc.create_time <= ?) ");
            args.add(endDate);
            args.add(endDate);
        }

        sql.append("GROUP BY ar.area_code, ar.area_name ");
        sql.append("ORDER BY MIN(ar.sort_order)");

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), args.toArray());

        exportCsv(response, list, "机构情况",
            new String[]{"区县代码", "区县名称", "机构数量", "老人数量", "驳回数量", "黑名单数量", "银行数量", "投诉数量"},
            new String[]{"district_code", "district_name", "institution_count", "elder_count", "rejected_count", "blacklist_count", "bank_count", "complaint_count"});
    }

    /**
     * 导出资金情况CSV
     */
    @Log(title = "数据统计-资金情况", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('data:statistics:fund')")
    @PostMapping("/fund/export")
    public void exportFund(HttpServletResponse response,
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ar.area_code as district_code, ar.area_name as district_name, ");
        sql.append("COALESCE(SUM(CASE WHEN sal.transaction_type = '收入' THEN sal.amount ELSE 0 END), 0) as total_income, ");
        sql.append("COALESCE(SUM(CASE WHEN sal.transaction_type = '支出' THEN sal.amount ELSE 0 END), 0) as total_outcome, ");
        sql.append("COALESCE(SUM(sal.amount), 0) as total_flow, ");
        sql.append("COALESCE(COUNT(DISTINCT sal.log_id), 0) as transaction_count, ");
        sql.append("COALESCE(SUM(CASE WHEN pr.payment_method = '微信' THEN 1 ELSE 0 END), 0) as wechat_count, ");
        sql.append("COALESCE(SUM(CASE WHEN pr.payment_method = '支付宝' THEN 1 ELSE 0 END), 0) as alipay_count, ");
        sql.append("COALESCE(SUM(CASE WHEN pr.payment_method = '银行' THEN 1 ELSE 0 END), 0) as bank_count, ");
        sql.append("COALESCE(SUM(CASE WHEN pr.payment_method = '现金' THEN 1 ELSE 0 END), 0) as cash_count ");
        sql.append("FROM area_street ar ");
        sql.append("LEFT JOIN pension_institution p ON p.area_code COLLATE utf8mb4_general_ci = ar.area_code COLLATE utf8mb4_general_ci ");
        sql.append("LEFT JOIN supervision_account_log sal ON sal.institution_id = p.institution_id ");
        sql.append("LEFT JOIN payment_record pr ON pr.institution_id = p.institution_id ");
        sql.append("WHERE ar.area_name IS NOT NULL ");

        List<Object> args = new ArrayList<>();
        if (StringUtils.isNotEmpty(districtCode)) {
            sql.append("AND ar.area_code COLLATE utf8mb4_general_ci = ? ");
            args.add(districtCode);
        }
        if (StringUtils.isNotEmpty(startDate)) {
            sql.append("AND (DATE(sal.transaction_time) >= ? OR DATE(pr.payment_time) >= ?) ");
            args.add(startDate);
            args.add(startDate);
        }
        if (StringUtils.isNotEmpty(endDate)) {
            sql.append("AND (DATE(sal.transaction_time) <= ? OR DATE(pr.payment_time) <= ?) ");
            args.add(endDate);
            args.add(endDate);
        }

        sql.append("GROUP BY ar.area_code, ar.area_name ");
        sql.append("ORDER BY MIN(ar.sort_order)");

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), args.toArray());

        exportCsv(response, list, "资金情况",
            new String[]{"区县代码", "区县名称", "总收入", "总支出", "资金流量", "交易数量", "微信支付", "支付宝支付", "银行支付", "现金支付"},
            new String[]{"district_code", "district_name", "total_income", "total_outcome", "total_flow", "transaction_count", "wechat_count", "alipay_count", "bank_count", "cash_count"});
    }

    /**
     * 导出预警情况CSV
     */
    @Log(title = "数据统计-预警情况", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('data:statistics:warning')")
    @PostMapping("/warning/export")
    public void exportWarning(HttpServletResponse response,
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ar.area_code as district_code, ar.area_name as district_name, ");
        sql.append("COALESCE(SUM(CASE WHEN bw.warning_type = '最低余额' THEN 1 ELSE 0 END), 0) as low_balance_count, ");
        sql.append("COALESCE(SUM(CASE WHEN sw.warning_type = '大额动账' THEN 1 ELSE 0 END), 0) as large_transaction_count, ");
        sql.append("COALESCE(SUM(CASE WHEN bw.warning_type = '可用余额不足' THEN 1 ELSE 0 END), 0) as insufficient_balance_count, ");
        sql.append("COALESCE(COUNT(DISTINCT bw.warning_id), 0) + COALESCE(COUNT(DISTINCT sw.warning_id), 0) as total_warning_count, ");
        sql.append("COALESCE(SUM(CASE WHEN bw.warning_status = '0' THEN 1 ELSE 0 END), 0) + ");
        sql.append("COALESCE(SUM(CASE WHEN sw.warning_status = '0' THEN 1 ELSE 0 END), 0) as pending_count, ");
        sql.append("COALESCE(COUNT(DISTINCT rr.refund_id), 0) as high_refund_count ");
        sql.append("FROM area_street ar ");
        sql.append("LEFT JOIN pension_institution p ON p.area_code COLLATE utf8mb4_general_ci = ar.area_code COLLATE utf8mb4_general_ci ");
        sql.append("LEFT JOIN balance_warning bw ON bw.institution_id = p.institution_id ");
        sql.append("LEFT JOIN supervision_warning sw ON sw.institution_id = p.institution_id ");
        sql.append("LEFT JOIN refund_record rr ON rr.institution_id = p.institution_id AND rr.refund_amount >= 5000 ");
        sql.append("WHERE ar.area_name IS NOT NULL ");

        List<Object> args = new ArrayList<>();
        if (StringUtils.isNotEmpty(districtCode)) {
            sql.append("AND ar.area_code COLLATE utf8mb4_general_ci = ? ");
            args.add(districtCode);
        }
        if (StringUtils.isNotEmpty(startDate)) {
            sql.append("AND (bw.create_time >= ? OR sw.create_time >= ? OR rr.create_time >= ?) ");
            args.add(startDate);
            args.add(startDate);
            args.add(startDate);
        }
        if (StringUtils.isNotEmpty(endDate)) {
            sql.append("AND (bw.create_time <= ? OR sw.create_time <= ? OR rr.create_time <= ?) ");
            args.add(endDate);
            args.add(endDate);
            args.add(endDate);
        }

        sql.append("GROUP BY ar.area_code, ar.area_name ");
        sql.append("ORDER BY MIN(ar.sort_order)");

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), args.toArray());

        exportCsv(response, list, "预警情况",
            new String[]{"区县代码", "区县名称", "最低余额预警", "大额动账预警", "余额不足预警", "总预警数", "待处理数", "高额退款数"},
            new String[]{"district_code", "district_name", "low_balance_count", "large_transaction_count", "insufficient_balance_count", "total_warning_count", "pending_count", "high_refund_count"});
    }

    /**
     * 导出机构列表CSV
     */
    @Log(title = "数据统计-机构列表", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('data:statistics:institutionList')")
    @PostMapping("/institutionList/export")
    public void exportInstitutionList(HttpServletResponse response,
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String institutionName)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.institution_id, p.institution_name, ar.area_name as district_name, ");
        sql.append("p.area_code, p.supervise_bank, p.basic_bank, p.contact_person, p.contact_phone, ");
        sql.append("p.status, p.blacklist_flag, p.bed_count, ");
        sql.append("COALESCE(SUM(ai.total_balance), 0) as total_supervision_fund, ");
        sql.append("COALESCE(COUNT(DISTINCT eci.elder_id), 0) as elder_count ");
        sql.append("FROM pension_institution p ");
        sql.append("LEFT JOIN area_street ar ON p.area_code COLLATE utf8mb4_general_ci = ar.area_code COLLATE utf8mb4_general_ci ");
        sql.append("LEFT JOIN account_info ai ON ai.institution_id = p.institution_id ");
        sql.append("LEFT JOIN elder_check_in eci ON eci.institution_id = p.institution_id ");

        List<Object> args = new ArrayList<>();
        sql.append("WHERE 1=1 ");

        if (StringUtils.isNotEmpty(districtCode)) {
            sql.append("AND p.area_code COLLATE utf8mb4_general_ci = ? ");
            args.add(districtCode);
        }
        if (StringUtils.isNotEmpty(institutionName)) {
            sql.append("AND p.institution_name LIKE ? ");
            args.add("%" + institutionName + "%");
        }

        sql.append("GROUP BY p.institution_id, p.institution_name, ar.area_name, p.area_code, ");
        sql.append("p.supervise_bank, p.basic_bank, p.contact_person, p.contact_phone, p.status, p.blacklist_flag, p.bed_count ");
        sql.append("ORDER BY MIN(ar.sort_order), p.institution_id");

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), args.toArray());

        exportCsv(response, list, "机构列表",
            new String[]{"机构ID", "机构名称", "区县", "区县代码", "监管银行", "基本银行", "联系人", "联系电话", "状态", "黑名单", "床位数", "监管金额", "老人数量"},
            new String[]{"institution_id", "institution_name", "district_name", "area_code", "supervise_bank", "basic_bank", "contact_person", "contact_phone", "status", "blacklist_flag", "bed_count", "total_supervision_fund", "elder_count"});
    }

    /**
     * 导出老人列表CSV
     */
    @Log(title = "数据统计-老人列表", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('data:statistics:elderList')")
    @PostMapping("/elderList/export")
    public void exportElderList(HttpServletResponse response,
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String elderName,
            @RequestParam(required = false) String institutionId)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT e.elder_id, e.elder_name, e.gender, e.id_card, e.age, e.phone, ");
        sql.append("e.health_status, e.care_level, e.status as elder_status, ");
        sql.append("ar.area_name as district_name, ");
        sql.append("p.institution_id, p.institution_name, ");
        sql.append("eci.actual_check_in_date, eci.check_in_status, ");
        sql.append("ai.total_balance, ai.service_balance, ai.deposit_balance, ai.member_balance ");
        sql.append("FROM elder_info e ");
        sql.append("LEFT JOIN elder_check_in eci ON eci.elder_id = e.elder_id ");
        sql.append("LEFT JOIN pension_institution p ON p.institution_id = eci.institution_id ");
        sql.append("LEFT JOIN area_street ar ON p.area_code COLLATE utf8mb4_general_ci = ar.area_code COLLATE utf8mb4_general_ci ");
        sql.append("LEFT JOIN account_info ai ON ai.elder_id = e.elder_id ");

        List<Object> args = new ArrayList<>();
        sql.append("WHERE 1=1 ");

        if (StringUtils.isNotEmpty(districtCode)) {
            sql.append("AND p.area_code COLLATE utf8mb4_general_ci = ? ");
            args.add(districtCode);
        }
        if (StringUtils.isNotEmpty(elderName)) {
            sql.append("AND e.elder_name LIKE ? ");
            args.add("%" + elderName + "%");
        }
        if (StringUtils.isNotEmpty(institutionId)) {
            sql.append("AND p.institution_id = ? ");
            args.add(institutionId);
        }

        sql.append("ORDER BY eci.actual_check_in_date DESC");

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), args.toArray());

        exportCsv(response, list, "老人列表",
            new String[]{"老人ID", "老人姓名", "性别", "身份证号", "年龄", "联系电话", "健康状况", "护理等级", "状态", "区县", "机构ID", "机构名称", "入住日期", "入住状态", "总余额", "服务费余额", "押金余额", "会员费余额"},
            new String[]{"elder_id", "elder_name", "gender", "id_card", "age", "phone", "health_status", "care_level", "elder_status", "district_name", "institution_id", "institution_name", "actual_check_in_date", "check_in_status", "total_balance", "service_balance", "deposit_balance", "member_balance"});
    }

    /**
     * 导出资金列表CSV
     */
    @Log(title = "数据统计-资金列表", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('data:statistics:fundList')")
    @PostMapping("/fundList/export")
    public void exportFundList(HttpServletResponse response,
            @RequestParam(required = false) String districtCode,
            @RequestParam(required = false) String elderName)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT e.elder_id, e.elder_name, e.id_card, ");
        sql.append("ar.area_name as district_name, ");
        sql.append("p.institution_id, p.institution_name, ");
        sql.append("ai.total_balance, ai.service_balance, ai.deposit_balance, ai.member_balance, ");
        sql.append("ai.account_no, ai.account_status, ai.create_time as account_create_time ");
        sql.append("FROM elder_info e ");
        sql.append("LEFT JOIN elder_check_in eci ON eci.elder_id = e.elder_id ");
        sql.append("LEFT JOIN pension_institution p ON p.institution_id = eci.institution_id ");
        sql.append("LEFT JOIN area_street ar ON p.area_code COLLATE utf8mb4_general_ci = ar.area_code COLLATE utf8mb4_general_ci ");
        sql.append("INNER JOIN account_info ai ON ai.elder_id = e.elder_id ");

        List<Object> args = new ArrayList<>();
        sql.append("WHERE 1=1 ");

        if (StringUtils.isNotEmpty(districtCode)) {
            sql.append("AND p.area_code COLLATE utf8mb4_general_ci = ? ");
            args.add(districtCode);
        }
        if (StringUtils.isNotEmpty(elderName)) {
            sql.append("AND e.elder_name LIKE ? ");
            args.add("%" + elderName + "%");
        }

        sql.append("ORDER BY ai.total_balance DESC");

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), args.toArray());

        exportCsv(response, list, "资金列表",
            new String[]{"老人ID", "老人姓名", "身份证号", "区县", "机构ID", "机构名称", "总余额", "服务费余额", "押金余额", "会员费余额", "账户号", "账户状态", "开户时间"},
            new String[]{"elder_id", "elder_name", "id_card", "district_name", "institution_id", "institution_name", "total_balance", "service_balance", "deposit_balance", "member_balance", "account_no", "account_status", "account_create_time"});
    }

    /**
     * 通用CSV导出方法
     */
    private void exportCsv(HttpServletResponse response, List<Map<String, Object>> list,
            String fileName, String[] headers, String[] keys)
    {
        try {
            response.setContentType("text/csv;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition",
                "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8") + "_" +
                new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".csv");

            java.io.PrintWriter writer = response.getWriter();

            // 写入BOM头以支持Excel正确打开UTF-8编码的CSV
            writer.print("\uFEFF");

            // 写入表头
            for (int i = 0; i < headers.length; i++) {
                writer.print(headers[i]);
                if (i < headers.length - 1) {
                    writer.print(",");
                }
            }
            writer.println();

            // 写入数据
            for (Map<String, Object> row : list) {
                for (int i = 0; i < keys.length; i++) {
                    Object value = row.get(keys[i]);
                    String strValue = value == null ? "" : value.toString();
                    // 处理包含逗号或引号的字段
                    if (strValue.contains(",") || strValue.contains("\"") || strValue.contains("\n")) {
                        strValue = "\"" + strValue.replace("\"", "\"\"") + "\"";
                    }
                    writer.print(strValue);
                    if (i < keys.length - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }

            writer.flush();
        } catch (Exception e) {
            logger.error("导出CSV失败", e);
        }
    }
}
