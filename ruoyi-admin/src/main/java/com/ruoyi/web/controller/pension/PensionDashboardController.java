package com.ruoyi.web.controller.pension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.mapper.pension.DashboardMapper;

/**
 * 养老机构首页Controller
 *
 * @author ruoyi
 * @date 2025-01-03
 */
@RestController
@RequestMapping("/pension/dashboard")
public class PensionDashboardController extends BaseController
{
    @Autowired
    private DashboardMapper dashboardMapper;

    /**
     * 获取当前用户关联的机构ID列表
     *
     * @return 机构ID列表，超管返回null表示查询所有机构
     */
    private List<Long> getUserInstitutionIds()
    {
        Long userId = SecurityUtils.getUserId();
        // 超级管理员不限制
        if (SecurityUtils.isAdmin(userId))
        {
            return null;
        }
        return dashboardMapper.selectInstitutionIdsByUserId(userId);
    }
    /**
     * 获取核心统计数据
     */
    @GetMapping("/statistics")
    public AjaxResult getStatistics()
    {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random();

        // TODO: 从数据库查询真实数据
        data.put("elderCount", 156 + random.nextInt(10));
        data.put("orderCount", 89 + random.nextInt(20));
        data.put("accountBalance", 2345678.90 + random.nextInt(100000));
        data.put("monthIncome", 456789.00 + random.nextInt(50000));

        return success(data);
    }

    /**
     * 获取资金数据
     */
    @GetMapping("/fund-data")
    public AjaxResult getFundData()
    {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random();

        // TODO: 从数据库查询真实数据
        data.put("supervisionBalance", 1234567.89 + random.nextInt(100000));
        data.put("depositBalance", 456789.00 + random.nextInt(50000));
        data.put("memberBalance", 654321.01 + random.nextInt(30000));
        data.put("todayIncome", 45678.90 + random.nextInt(10000));
        data.put("todayExpense", 12345.67 + random.nextInt(5000));

        return success(data);
    }

    /**
     * 获取待处理事项
     */
    @GetMapping("/pending-items")
    public AjaxResult getPendingItems()
    {
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询真实数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 1);
        item1.put("icon", "el-icon-money");
        item1.put("color", "#f56c6c");
        item1.put("title", "押金使用申请");
        item1.put("time", "2小时前");
        item1.put("count", 3);
        item1.put("type", "danger");
        data.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 2);
        item2.put("icon", "el-icon-wallet");
        item2.put("color", "#e6a23c");
        item2.put("title", "资金划拨申请");
        item2.put("time", "5小时前");
        item2.put("count", 2);
        item2.put("type", "warning");
        data.add(item2);

        Map<String, Object> item3 = new HashMap<>();
        item3.put("id", 3);
        item3.put("icon", "el-icon-bell");
        item3.put("color", "#409eff");
        item3.put("title", "新公告通知");
        item3.put("time", "1天前");
        item3.put("count", 1);
        item3.put("type", "info");
        data.add(item3);

        return success(data);
    }

    /**
     * 获取申请列表
     */
    @GetMapping("/applications")
    public AjaxResult getApplications()
    {
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询真实数据
        Map<String, Object> app1 = new HashMap<>();
        app1.put("id", "YJ202501001");
        app1.put("type", "押金使用");
        app1.put("amount", 50000);
        app1.put("reason", "设备维修费用");
        app1.put("createTime", "2025-01-03 10:30:00");
        app1.put("status", "待审批");
        data.add(app1);

        Map<String, Object> app2 = new HashMap<>();
        app2.put("id", "ZJ202501002");
        app2.put("type", "资金划拨");
        app2.put("amount", 100000);
        app2.put("reason", "员工工资发放");
        app2.put("createTime", "2025-01-02 14:20:00");
        app2.put("status", "审批中");
        data.add(app2);

        return success(data);
    }

    // ==================== 机构管理员首页接口 ====================

    /**
     * 获取机构管理员首页概览数据（订单统计）
     */
    @GetMapping("/institution/overview")
    public AjaxResult getInstitutionOverview()
    {
        List<Long> institutionIds = getUserInstitutionIds();
        Map<String, Object> result = new HashMap<>();
        result.put("orderStats", dashboardMapper.selectTodayOrderStats(institutionIds));
        return success(result);
    }

    /**
     * 获取账户余额汇总
     */
    @GetMapping("/institution/balance")
    public AjaxResult getInstitutionBalance()
    {
        List<Long> institutionIds = getUserInstitutionIds();

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> accountBalance = dashboardMapper.selectAccountBalanceSummary(institutionIds);
        BigDecimal basicBalance = dashboardMapper.selectBasicAccountBalance(institutionIds);

        result.put("accountBalance", accountBalance);
        result.put("basicAccountBalance", basicBalance);
        return success(result);
    }

    /**
     * 获取服务费拨付统计
     */
    @GetMapping("/institution/transfer")
    public AjaxResult getInstitutionTransfer()
    {
        List<Long> institutionIds = getUserInstitutionIds();
        Map<String, Object> result = dashboardMapper.selectMonthTransferStats(institutionIds);
        return success(result);
    }

    /**
     * 获取押金申请统计
     */
    @GetMapping("/institution/deposit")
    public AjaxResult getInstitutionDeposit()
    {
        List<Long> institutionIds = getUserInstitutionIds();
        List<Map<String, Object>> stats = dashboardMapper.selectDepositApplyStats(institutionIds);

        Map<String, Object> result = new HashMap<>();

        // 待审批（status=0 或 draft）
        Map<String, Object> pending = new HashMap<>();
        pending.put("count", 0);
        pending.put("amount", BigDecimal.ZERO);
        result.put("pending", pending);

        // 已批准（status=1 或 approved）
        Map<String, Object> approved = new HashMap<>();
        approved.put("count", 0);
        approved.put("amount", BigDecimal.ZERO);
        result.put("approved", approved);

        // 已拒绝（status=2 或 rejected）
        Map<String, Object> rejected = new HashMap<>();
        rejected.put("count", 0);
        rejected.put("amount", BigDecimal.ZERO);
        result.put("rejected", rejected);

        for (Map<String, Object> stat : stats)
        {
            String status = String.valueOf(stat.get("apply_status"));
            int count = stat.get("count") != null ? ((Number) stat.get("count")).intValue() : 0;
            BigDecimal amount = stat.get("amount") != null ? new BigDecimal(stat.get("amount").toString()) : BigDecimal.ZERO;

            Map<String, Object> target;
            if ("0".equals(status) || "draft".equals(status))
            {
                target = pending;
            }
            else if ("1".equals(status) || "approved".equals(status))
            {
                target = approved;
            }
            else if ("2".equals(status) || "rejected".equals(status))
            {
                target = rejected;
            }
            else
            {
                continue;
            }

            target.put("count", count);
            target.put("amount", amount);
        }

        return success(result);
    }

    /**
     * 获取入驻人结构分析
     */
    @GetMapping("/institution/resident-structure")
    public AjaxResult getResidentStructure()
    {
        List<Long> institutionIds = getUserInstitutionIds();

        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> genderList = dashboardMapper.selectGenderDistribution(institutionIds);
        List<Map<String, Object>> ageList = dashboardMapper.selectAgeDistribution(institutionIds);
        List<Map<String, Object>> careLevelList = dashboardMapper.selectCareLevelDistribution(institutionIds);

        // 如果没有数据，返回模拟数据
        if (genderList == null || genderList.isEmpty())
        {
            genderList = new ArrayList<>();
            genderList.add(createGenderItem("0", 15));
            genderList.add(createGenderItem("1", 25));
            genderList.add(createGenderItem("2", 5));
        }

        if (ageList == null || ageList.isEmpty())
        {
            ageList = new ArrayList<>();
            ageList.add(createAgeItem("60-70岁", 8));
            ageList.add(createAgeItem("70-80岁", 18));
            ageList.add(createAgeItem("80-90岁", 15));
            ageList.add(createAgeItem("90岁以上", 4));
        }

        if (careLevelList == null || careLevelList.isEmpty())
        {
            careLevelList = new ArrayList<>();
            careLevelList.add(createCareLevelItem("1", 20));
            careLevelList.add(createCareLevelItem("2", 18));
            careLevelList.add(createCareLevelItem("3", 7));
        }

        result.put("gender", formatGenderData(genderList));
        result.put("age", ageList);
        result.put("careLevel", formatCareLevelData(careLevelList));

        return success(result);
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

        // 性别映射: 0=男, 1=女, 2=其他
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

        // 护理等级映射: 1=自理, 2=半护理, 3=全护理
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
