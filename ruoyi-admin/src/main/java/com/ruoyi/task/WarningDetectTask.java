package com.ruoyi.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.ruoyi.domain.pension.WarningRuleConfig;
import com.ruoyi.domain.pension.SupervisionWarning;
import com.ruoyi.mapper.pension.WarningRuleConfigMapper;
import com.ruoyi.mapper.pension.SupervisionWarningMapper;

/**
 * 预警检测定时任务
 *
 * @author ruoyi
 */
@Component("warningDetectTask")
public class WarningDetectTask
{
    private static final Logger log = LoggerFactory.getLogger(WarningDetectTask.class);

    @Autowired
    private WarningRuleConfigMapper warningRuleConfigMapper;

    @Autowired
    private SupervisionWarningMapper supervisionWarningMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 预警检测任务（每30分钟执行一次）
     * 检测各种预警规则，生成预警记录
     */
    public void detectWarnings()
    {
        log.info("========== 开始执行预警检测任务 ==========");

        try
        {
            // 获取所有启用的预警规则
            WarningRuleConfig query = new WarningRuleConfig();
            query.setEnabled("1");
            List<WarningRuleConfig> rules = warningRuleConfigMapper.selectWarningRuleConfigList(query);

            if (rules == null || rules.isEmpty())
            {
                log.info("没有启用的预警规则，跳过本次检测");
                return;
            }

            int warningCount = 0;

            for (WarningRuleConfig rule : rules)
            {
                int count = detectByRule(rule);
                warningCount += count;
            }

            log.info("========== 预警检测任务执行完成，共生成 {} 条预警记录 ==========", warningCount);
        }
        catch (Exception e)
        {
            log.error("预警检测任务执行失败", e);
        }
    }

    /**
     * 根据规则类型进行预警检测
     */
    private int detectByRule(WarningRuleConfig rule)
    {
        String warningType = rule.getWarningType();

        switch (warningType)
        {
            case "1": // 预收费用超额
                return detectPrepaidFeeExcess(rule);
            case "2": // 押金超额
                return detectDepositExcess(rule);
            case "3": // 入住人数超额
                return detectCheckinExcess(rule);
            case "4": // 预收总额超额
                return detectSupervisionExcess(rule);
            case "5": // 风险保证金超低
                return detectRiskDepositLow(rule);
            case "6": // 大额支出
                return detectLargePayment(rule);
            case "7": // 交易对方风险（需手动触发）
                return 0;
            default:
                log.warn("未知的预警类型: {}", warningType);
                return 0;
        }
    }

    /**
     * 检测预收费用超额
     * 规则：老人预收费余额超过月费用N倍
     */
    private int detectPrepaidFeeExcess(WarningRuleConfig rule)
    {
        int count = 0;
        BigDecimal threshold = rule.getThresholdValue();

        log.info("开始检测预收费用超额预警，阈值: {} 倍", threshold);

        try
        {
            // 查询所有有账户的老人及其月费用、账户余额（仅限已入驻机构）
            String sql = "SELECT ai.account_id, ai.elder_id, ai.institution_id, ai.total_balance, " +
                        "ei.elder_name, eci.monthly_fee, pi.institution_name, pi.contact_person, pi.contact_phone " +
                        "FROM account_info ai " +
                        "LEFT JOIN elder_info ei ON ai.elder_id = ei.elder_id " +
                        "LEFT JOIN elder_check_in eci ON ai.elder_id = eci.elder_id AND eci.check_in_status = '1' " +
                        "LEFT JOIN pension_institution pi ON ai.institution_id = pi.institution_id " +
                        "WHERE pi.status = '1' AND eci.monthly_fee > 0 AND ai.total_balance > 0";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

            for (Map<String, Object> row : resultList)
            {
                try
                {
                    BigDecimal totalBalance = getBigDecimal(row.get("total_balance"));
                    BigDecimal monthlyFee = getBigDecimal(row.get("monthly_fee"));

                    if (monthlyFee.compareTo(BigDecimal.ZERO) <= 0)
                    {
                        continue;
                    }

                    // 计算预收费倍数
                    BigDecimal ratio = totalBalance.divide(monthlyFee, 2, RoundingMode.HALF_UP);

                    if (ratio.compareTo(threshold) > 0)
                    {
                        Long institutionId = getLong(row.get("institution_id"));
                        String institutionName = getString(row.get("institution_name"));
                        String elderName = getString(row.get("elder_name"));
                        String contactPerson = getString(row.get("contact_person"));
                        String contactPhone = getString(row.get("contact_phone"));

                        String warningContent = String.format("老人[%s]预收费余额%.2f元超过月费用%.2f元的%.1f倍（阈值%.0f倍）",
                                elderName, totalBalance, monthlyFee, ratio, threshold);

                        if (createWarning(institutionId, institutionName, "1", warningContent, "高", contactPerson, contactPhone))
                        {
                            count++;
                        }
                    }
                }
                catch (Exception e)
                {
                    log.warn("处理预收费超额预警数据时出错: {}", e.getMessage());
                }
            }

            log.info("预收费用超额检测完成，生成 {} 条预警", count);
        }
        catch (Exception e)
        {
            log.error("预收费用超额检测失败", e);
        }

        return count;
    }

    /**
     * 检测押金超额
     * 规则：老人押金余额超过床位费N倍
     */
    private int detectDepositExcess(WarningRuleConfig rule)
    {
        int count = 0;
        BigDecimal threshold = rule.getThresholdValue();

        log.info("开始检测押金超额预警，阈值: {} 倍", threshold);

        try
        {
            // 查询所有有账户的老人及其押金余额、床位费（仅限已入驻机构）
            String sql = "SELECT ai.account_id, ai.elder_id, ai.institution_id, ai.deposit_balance, " +
                        "ei.elder_name, bi.price as bed_price, pi.institution_name, pi.contact_person, pi.contact_phone " +
                        "FROM account_info ai " +
                        "LEFT JOIN elder_info ei ON ai.elder_id = ei.elder_id " +
                        "LEFT JOIN elder_check_in eci ON ai.elder_id = eci.elder_id AND eci.check_in_status = '1' " +
                        "LEFT JOIN bed_info bi ON eci.bed_id = bi.bed_id " +
                        "LEFT JOIN pension_institution pi ON ai.institution_id = pi.institution_id " +
                        "WHERE pi.status = '1' AND ai.deposit_balance > 0 AND bi.price > 0";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

            for (Map<String, Object> row : resultList)
            {
                try
                {
                    BigDecimal depositBalance = getBigDecimal(row.get("deposit_balance"));
                    BigDecimal bedPrice = getBigDecimal(row.get("bed_price"));

                    if (bedPrice.compareTo(BigDecimal.ZERO) <= 0)
                    {
                        continue;
                    }

                    // 计算押金倍数
                    BigDecimal ratio = depositBalance.divide(bedPrice, 2, RoundingMode.HALF_UP);

                    if (ratio.compareTo(threshold) > 0)
                    {
                        Long institutionId = getLong(row.get("institution_id"));
                        String institutionName = getString(row.get("institution_name"));
                        String elderName = getString(row.get("elder_name"));
                        String contactPerson = getString(row.get("contact_person"));
                        String contactPhone = getString(row.get("contact_phone"));

                        String warningContent = String.format("老人[%s]押金余额%.2f元超过床位费%.2f元的%.1f倍（阈值%.0f倍）",
                                elderName, depositBalance, bedPrice, ratio, threshold);

                        if (createWarning(institutionId, institutionName, "2", warningContent, "中", contactPerson, contactPhone))
                        {
                            count++;
                        }
                    }
                }
                catch (Exception e)
                {
                    log.warn("处理押金超额预警数据时出错: {}", e.getMessage());
                }
            }

            log.info("押金超额检测完成，生成 {} 条预警", count);
        }
        catch (Exception e)
        {
            log.error("押金超额检测失败", e);
        }

        return count;
    }

    /**
     * 检测入住人数超额
     * 规则：实际入住人数超过备案床位总数N倍
     */
    private int detectCheckinExcess(WarningRuleConfig rule)
    {
        int count = 0;
        BigDecimal threshold = rule.getThresholdValue();

        log.info("开始检测入住人数超额预警，阈值: {} 倍", threshold);

        try
        {
            // 查询每个机构的床位数和实际入住人数（仅限已入驻机构）
            String sql = "SELECT pi.institution_id, pi.institution_name, pi.contact_person, pi.contact_phone, " +
                        "COUNT(DISTINCT bi.bed_id) as total_beds, " +
                        "COUNT(DISTINCT CASE WHEN eci.check_in_status = '1' THEN eci.check_in_id END) as occupied_beds " +
                        "FROM pension_institution pi " +
                        "LEFT JOIN bed_info bi ON pi.institution_id = bi.institution_id " +
                        "LEFT JOIN elder_check_in eci ON pi.institution_id = eci.institution_id " +
                        "WHERE pi.status = '1' " +
                        "GROUP BY pi.institution_id, pi.institution_name, pi.contact_person, pi.contact_phone";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

            for (Map<String, Object> row : resultList)
            {
                try
                {
                    Long institutionId = getLong(row.get("institution_id"));
                    String institutionName = getString(row.get("institution_name"));
                    String contactPerson = getString(row.get("contact_person"));
                    String contactPhone = getString(row.get("contact_phone"));
                    Integer totalBeds = getInteger(row.get("total_beds"));
                    Integer occupiedBeds = getInteger(row.get("occupied_beds"));

                    if (totalBeds == null || totalBeds == 0)
                    {
                        continue;
                    }

                    // 计算入住倍数
                    BigDecimal ratio = new BigDecimal(occupiedBeds).divide(new BigDecimal(totalBeds), 2, RoundingMode.HALF_UP);

                    if (ratio.compareTo(threshold) > 0)
                    {
                        String warningContent = String.format("实际入住人数%d人超过备案床位总数%d位的%.1f倍（阈值%.0f倍）",
                                occupiedBeds, totalBeds, ratio, threshold);

                        if (createWarning(institutionId, institutionName, "3", warningContent, "高", contactPerson, contactPhone))
                        {
                            count++;
                        }
                    }
                }
                catch (Exception e)
                {
                    log.warn("处理入住人数超额预警数据时出错: {}", e.getMessage());
                }
            }

            log.info("入住人数超额检测完成，生成 {} 条预警", count);
        }
        catch (Exception e)
        {
            log.error("入住人数超额检测失败", e);
        }

        return count;
    }

    /**
     * 检测预收总额超额
     * 规则：机构账户总额超过固定资产净额N倍
     */
    private int detectSupervisionExcess(WarningRuleConfig rule)
    {
        int count = 0;
        BigDecimal threshold = rule.getThresholdValue();

        log.info("开始检测预收总额超额预警，阈值: {} 倍", threshold);

        try
        {
            // 查询每个机构的账户总额和固定资产净额（仅限已入驻机构）
            String sql = "SELECT pi.institution_id, pi.institution_name, pi.contact_person, pi.contact_phone, " +
                        "pi.fixed_assets, " +
                        "COALESCE(SUM(ai.total_balance), 0) as total_balance " +
                        "FROM pension_institution pi " +
                        "LEFT JOIN account_info ai ON pi.institution_id = ai.institution_id " +
                        "WHERE pi.status = '1' " +
                        "GROUP BY pi.institution_id, pi.institution_name, pi.contact_person, pi.contact_phone, pi.fixed_assets";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

            for (Map<String, Object> row : resultList)
            {
                try
                {
                    Long institutionId = getLong(row.get("institution_id"));
                    String institutionName = getString(row.get("institution_name"));
                    String contactPerson = getString(row.get("contact_person"));
                    String contactPhone = getString(row.get("contact_phone"));
                    BigDecimal fixedAssets = getBigDecimal(row.get("fixed_assets"));
                    BigDecimal totalBalance = getBigDecimal(row.get("total_balance"));

                    if (fixedAssets.compareTo(BigDecimal.ZERO) <= 0)
                    {
                        continue;
                    }

                    // fixed_assets 单位是万元，需要转换为元（乘以10000）
                    BigDecimal fixedAssetsInYuan = fixedAssets.multiply(new BigDecimal("10000"));
                    if (fixedAssetsInYuan.compareTo(BigDecimal.ZERO) <= 0)
                    {
                        continue;
                    }

                    // 计算账户余额与固定资产净额的倍数
                    BigDecimal ratio = totalBalance.divide(fixedAssetsInYuan, 2, RoundingMode.HALF_UP);

                    if (ratio.compareTo(threshold) > 0)
                    {
                        String warningContent = String.format("机构账户总额%.2f元超过固定资产净额%.2f万元的%.1f倍（阈值%.0f倍）",
                                totalBalance, fixedAssets, ratio, threshold);

                        if (createWarning(institutionId, institutionName, "4", warningContent, "高", contactPerson, contactPhone))
                        {
                            count++;
                        }
                    }
                }
                catch (Exception e)
                {
                    log.warn("处理预收总额超额预警数据时出错: {}", e.getMessage());
                }
            }

            log.info("预收总额超额检测完成，生成 {} 条预警", count);
        }
        catch (Exception e)
        {
            log.error("预收总额超额检测失败", e);
        }

        return count;
    }

    /**
     * 检测风险保证金超低
     * 规则：账户余额低于风险保证金比例
     */
    private int detectRiskDepositLow(WarningRuleConfig rule)
    {
        int count = 0;
        BigDecimal threshold = rule.getThresholdValue();

        log.info("开始检测风险保证金超低预警，阈值: {} 百分比", threshold);

        try
        {
            // 查询每个机构的账户余额和风险保证金（仅限已入驻机构）
            // 这里简化处理：假设固定资产的80%为风险保证金最低额度
            String sql = "SELECT pi.institution_id, pi.institution_name, pi.contact_person, pi.contact_phone, " +
                        "pi.fixed_assets, " +
                        "COALESCE(SUM(ai.total_balance), 0) as total_balance " +
                        "FROM pension_institution pi " +
                        "LEFT JOIN account_info ai ON pi.institution_id = ai.institution_id " +
                        "WHERE pi.status = '1' " +
                        "GROUP BY pi.institution_id, pi.institution_name, pi.contact_person, pi.contact_phone, pi.fixed_assets";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);

            for (Map<String, Object> row : resultList)
            {
                try
                {
                    Long institutionId = getLong(row.get("institution_id"));
                    String institutionName = getString(row.get("institution_name"));
                    String contactPerson = getString(row.get("contact_person"));
                    String contactPhone = getString(row.get("contact_phone"));
                    BigDecimal fixedAssets = getBigDecimal(row.get("fixed_assets"));
                    BigDecimal totalBalance = getBigDecimal(row.get("total_balance"));

                    if (fixedAssets.compareTo(BigDecimal.ZERO) <= 0)
                    {
                        continue;
                    }

                    // fixed_assets 单位是万元，需要转换为元（乘以10000）
                    BigDecimal fixedAssetsInYuan = fixedAssets.multiply(new BigDecimal("10000"));

                    // 计算风险保证金最低额度（固定资产的 threshold%）
                    BigDecimal minRiskDeposit = fixedAssetsInYuan.multiply(threshold).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                    // 判断账户余额是否接近风险保证金
                    BigDecimal ratio = totalBalance.divide(minRiskDeposit, 2, RoundingMode.HALF_UP);

                    // 如果账户余额低于风险保证金的1.2倍，触发预警
                    if (ratio.compareTo(new BigDecimal("1.2")) < 0)
                    {
                        String warningContent = String.format("机构账户余额%.2f元接近风险保证金最低额度%.2f万元（固定资产%.2f万的%.1f%%），当前比例为%.1f%%",
                                totalBalance, minRiskDeposit.divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP),
                                fixedAssets, threshold, ratio.multiply(new BigDecimal("100")));

                        if (createWarning(institutionId, institutionName, "5", warningContent, "高", contactPerson, contactPhone))
                        {
                            count++;
                        }
                    }
                }
                catch (Exception e)
                {
                    log.warn("处理风险保证金超低预警数据时出错: {}", e.getMessage());
                }
            }

            log.info("风险保证金超低检测完成，生成 {} 条预警", count);
        }
        catch (Exception e)
        {
            log.error("风险保证金超低检测失败", e);
        }

        return count;
    }

    /**
     * 检测大额支出
     * 规则：单笔支付金额超过N元
     */
    private int detectLargePayment(WarningRuleConfig rule)
    {
        int count = 0;
        BigDecimal threshold = rule.getThresholdValue();

        log.info("开始检测大额支出预警，阈值: {} 元", threshold);

        try
        {
            // 查询最近30分钟内的支付记录（仅限已入驻机构）
            String sql = "SELECT oi.order_id, oi.institution_id, oi.order_no, oi.order_amount, oi.order_type, " +
                        "oi.payment_time, pi.institution_name, pi.contact_person, pi.contact_phone " +
                        "FROM order_info oi " +
                        "LEFT JOIN pension_institution pi ON oi.institution_id = pi.institution_id " +
                        "WHERE pi.status = '1' " +
                        "AND oi.order_status = '1' " +
                        "AND oi.payment_time >= DATE_SUB(NOW(), INTERVAL 30 MINUTE) " +
                        "AND oi.order_amount >= ?";

            List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql, threshold);

            for (Map<String, Object> row : resultList)
            {
                try
                {
                    Long institutionId = getLong(row.get("institution_id"));
                    String institutionName = getString(row.get("institution_name"));
                    String contactPerson = getString(row.get("contact_person"));
                    String contactPhone = getString(row.get("contact_phone"));
                    String orderNo = getString(row.get("order_no"));
                    BigDecimal orderAmount = getBigDecimal(row.get("order_amount"));

                    String warningContent = String.format("大额支出预警：订单[%s]支付金额%.2f元超过阈值%.0f元",
                            orderNo, orderAmount, threshold);

                    if (createWarning(institutionId, institutionName, "6", warningContent, "高", contactPerson, contactPhone))
                    {
                        count++;
                    }
                }
                catch (Exception e)
                {
                    log.warn("处理大额支出预警数据时出错: {}", e.getMessage());
                }
            }

            log.info("大额支出检测完成，生成 {} 条预警", count);
        }
        catch (Exception e)
        {
            log.error("大额支出检测失败", e);
        }

        return count;
    }

    /**
     * 生成预警编号
     */
    private String generateWarningNo()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "YJ" + sdf.format(new Date());
    }

    /**
     * 判断是否已存在相同预警（最近24小时内）
     * 避免重复生成
     */
    private boolean isWarningExists(Long institutionId, String warningType, String warningContent)
    {
        try
        {
            String sql = "SELECT COUNT(*) FROM supervision_warning " +
                        "WHERE institution_id = ? " +
                        "AND warning_type = ? " +
                        "AND warning_status = '0' " +
                        "AND create_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
                        "AND warning_content = ?";

            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, institutionId, warningType, warningContent);
            return count != null && count > 0;
        }
        catch (DataAccessException e)
        {
            log.warn("检查预警是否存在时出错: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 创建预警记录
     */
    private boolean createWarning(Long institutionId, String institutionName, String warningType,
            String warningContent, String warningLevel, String contactPerson, String contactPhone)
    {
        try
        {
            // 避免重复生成
            if (isWarningExists(institutionId, warningType, warningContent))
            {
                return false;
            }

            SupervisionWarning warning = new SupervisionWarning();
            warning.setWarningNo(generateWarningNo());
            warning.setInstitutionId(institutionId);
            warning.setInstitutionName(institutionName);
            warning.setWarningType(warningType);
            warning.setWarningContent(warningContent);
            warning.setWarningLevel(warningLevel);
            warning.setWarningStatus("0");
            warning.setContactPerson(contactPerson);
            warning.setContactPhone(contactPhone);
            warning.setIsNotified("0");

            supervisionWarningMapper.insertSupervisionWarning(warning);
            log.info("生成预警记录: 机构={}, 类型={}, 内容={}", institutionName, warningType, warningContent);
            return true;
        }
        catch (Exception e)
        {
            log.error("创建预警记录失败", e);
            return false;
        }
    }

    // 辅助方法
    private String getString(Object value)
    {
        if (value == null)
        {
            return "";
        }
        return value.toString();
    }

    private Long getLong(Object value)
    {
        if (value == null)
        {
            return 0L;
        }
        if (value instanceof Long)
        {
            return (Long) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        try
        {
            return Long.parseLong(value.toString());
        }
        catch (NumberFormatException e)
        {
            return 0L;
        }
    }

    private Integer getInteger(Object value)
    {
        if (value == null)
        {
            return 0;
        }
        if (value instanceof Integer)
        {
            return (Integer) value;
        }
        if (value instanceof Number)
        {
            return ((Number) value).intValue();
        }
        try
        {
            return Integer.parseInt(value.toString());
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    private BigDecimal getBigDecimal(Object value)
    {
        if (value == null)
        {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal)
        {
            return (BigDecimal) value;
        }
        if (value instanceof Number)
        {
            return new BigDecimal(value.toString());
        }
        try
        {
            return new BigDecimal(value.toString());
        }
        catch (NumberFormatException e)
        {
            return BigDecimal.ZERO;
        }
    }
}
