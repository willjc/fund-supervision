package com.ruoyi.web.controller.pension;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 数据大屏Controller
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@RestController
@RequestMapping("/pension/bigscreen")
public class BigScreenController extends BaseController
{
    /**
     * 获取养老机构分布统计
     */
    @Anonymous
    @GetMapping("/institution/distribution")
    public AjaxResult getInstitutionDistribution()
    {
        // 郑州市各区机构数量数据
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();

        String[] zhengzhouDistricts = {
            "金水区", "二七区", "中原区", "管城回族区", "惠济区",
            "上街区", "中牟县", "巩义市", "荥阳市", "新密市",
            "新郑市", "登封市", "郑州航空港经济综合实验区"
        };

        int[] institutionCounts = {
            45, 32, 28, 25, 18, 12, 35, 22, 28, 19, 24, 16, 15
        };

        for (int i = 0; i < zhengzhouDistricts.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", zhengzhouDistricts[i]);
            item.put("value", institutionCounts[i] + random.nextInt(10)); // 基础数据+随机波动
            data.add(item);
        }

        return AjaxResult.success(data);
    }

    /**
     * 获取机构数量统计
     */
    @Anonymous
    @GetMapping("/institution/statistics")
    public AjaxResult getInstitutionStatistics()
    {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random();

        // 郑州市养老机构统计数据
        data.put("totalInstitutions", 326 + random.nextInt(20)); // 总机构数
        data.put("approvedInstitutions", 285 + random.nextInt(15)); // 已审批
        data.put("pendingInstitutions", 25 + random.nextInt(10)); // 待审批
        data.put("rejectedInstitutions", 16 + random.nextInt(8)); // 已驳回

        data.put("totalElders", 15680 + random.nextInt(800)); // 总老人数
        data.put("totalBeds", 22500 + random.nextInt(1500)); // 总床位数
        data.put("occupiedBeds", 19800 + random.nextInt(1000)); // 已占用床位

        return AjaxResult.success(data);
    }

    /**
     * 获取机构等级分布
     */
    @Anonymous
    @GetMapping("/institution/levels")
    public AjaxResult getInstitutionLevels()
    {
        List<Map<String, Object>> data = new ArrayList<>();

        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "五星级");
        item1.put("value", 25);
        data.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "四星级");
        item2.put("value", 45);
        data.add(item2);

        Map<String, Object> item3 = new HashMap<>();
        item3.put("name", "三星级");
        item3.put("value", 68);
        data.add(item3);

        Map<String, Object> item4 = new HashMap<>();
        item4.put("name", "二星级");
        item4.put("value", 18);
        data.add(item4);

        return AjaxResult.success(data);
    }

    /**
     * 获取床位使用情况
     */
    @Anonymous
    @GetMapping("/bed/usage")
    public AjaxResult getBedUsage()
    {
        List<Map<String, Object>> data = new ArrayList<>();

        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "已占用");
        item1.put("value", 9856);
        data.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "空闲");
        item2.put("value", 2644);
        data.add(item2);

        Map<String, Object> item3 = new HashMap<>();
        item3.put("name", "维护中");
        item3.put("value", 234);
        data.add(item3);

        return AjaxResult.success(data);
    }

    /**
     * 获取老人年龄分布
     */
    @Anonymous
    @GetMapping("/elder/age-distribution")
    public AjaxResult getElderAgeDistribution()
    {
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();

        // 郑州市老人年龄分布数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "60-70岁");
        item1.put("value", 4856 + random.nextInt(200));
        data.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "70-80岁");
        item2.put("value", 6789 + random.nextInt(300));
        data.add(item2);

        Map<String, Object> item3 = new HashMap<>();
        item3.put("name", "80-90岁");
        item3.put("value", 3245 + random.nextInt(200));
        data.add(item3);

        Map<String, Object> item4 = new HashMap<>();
        item4.put("name", "90岁以上");
        item4.put("value", 790 + random.nextInt(100));
        data.add(item4);

        return AjaxResult.success(data);
    }

    /**
     * 获取实时数据
     */
    @Anonymous
    @GetMapping("/realtime")
    public AjaxResult getRealtimeData()
    {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random();

        data.put("currentTime", System.currentTimeMillis());
        data.put("todayCheckins", 28 + random.nextInt(15)); // 今日入住
        data.put("todayCheckouts", 8 + random.nextInt(8)); // 今日退住
        data.put("todayPayments", 89 + random.nextInt(30)); // 今日缴费
        data.put("totalAmount", 356789.45 + random.nextInt(80000)); // 今日总金额
        data.put("activeWarnings", 5 + random.nextInt(8)); // 活跃预警数

        return AjaxResult.success(data);
    }

    // ==================== 资金监管相关接口 ====================

    /**
     * 获取资金监管总览数据
     */
    @Anonymous
    @GetMapping("/fund/overview")
    public AjaxResult getFundOverview()
    {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random();

        // 资金总览数据
        data.put("totalBalance", 89234567.89 + random.nextInt(5000000)); // 总余额
        data.put("todayIncome", 1234567.89 + random.nextInt(200000)); // 今日收入
        data.put("todayExpense", 234567.89 + random.nextInt(50000)); // 今日支出
        data.put("pendingAmount", 5678901.23 + random.nextInt(800000)); // 待审批金额
        data.put("warningAmount", 123456.78 + random.nextInt(30000)); // 预警金额

        // 资金流转数据
        data.put("todayTransferIn", 234567.89 + random.nextInt(50000)); // 今日转入
        data.put("todayTransferOut", 123456.78 + random.nextInt(30000)); // 今日转出
        data.put("todayRefund", 45678.90 + random.nextInt(15000)); // 今日退款

        return AjaxResult.success(data);
    }

    /**
     * 获取资金流向数据（桑基图格式）
     */
    @Anonymous
    @GetMapping("/fund/flow")
    public AjaxResult getFundFlow()
    {
        Map<String, Object> result = new HashMap<>();
        Random random = new Random();

        // 资金来源节点
        List<Map<String, Object>> sources = new ArrayList<>();
        sources.add(createFlowNode("家属缴费", "source", "#10b981"));
        sources.add(createFlowNode("政府补贴", "source", "#10b981"));
        sources.add(createFlowNode("医保支付", "source", "#10b981"));
        sources.add(createFlowNode("社会捐赠", "source", "#10b981"));

        // 资金去向节点
        List<Map<String, Object>> targets = new ArrayList<>();
        targets.add(createFlowNode("护理服务费", "target", "#ef4444"));
        targets.add(createFlowNode("医疗费用", "target", "#ef4444"));
        targets.add(createFlowNode("餐饮费用", "target", "#ef4444"));
        targets.add(createFlowNode("住宿费用", "target", "#ef4444"));
        targets.add(createFlowNode("娱乐活动", "target", "#ef4444"));
        targets.add(createFlowNode("应急储备", "target", "#f59e0b"));

        // 资金流向连接
        List<Map<String, Object>> flows = new ArrayList<>();

        // 家属缴费流向
        flows.add(createFlowLink("家属缴费", "护理服务费", (234567.89 + random.nextInt(50000))));
        flows.add(createFlowLink("家属缴费", "餐饮费用", (123456.78 + random.nextInt(30000))));
        flows.add(createFlowLink("家属缴费", "住宿费用", (156789.12 + random.nextInt(40000))));
        flows.add(createFlowLink("家属缴费", "娱乐活动", (23456.78 + random.nextInt(10000))));

        // 政府补贴流向
        flows.add(createFlowLink("政府补贴", "护理服务费", (123456.78 + random.nextInt(30000))));
        flows.add(createFlowLink("政府补贴", "医疗费用", (234567.89 + random.nextInt(50000))));
        flows.add(createFlowLink("政府补贴", "应急储备", (345678.90 + random.nextInt(60000))));

        // 医保支付流向
        flows.add(createFlowLink("医保支付", "医疗费用", (345678.90 + random.nextInt(60000))));
        flows.add(createFlowLink("医保支付", "护理服务费", (45678.90 + random.nextInt(15000))));

        // 社会捐赠流向
        flows.add(createFlowLink("社会捐赠", "娱乐活动", (12345.67 + random.nextInt(8000))));
        flows.add(createFlowLink("社会捐赠", "餐饮费用", (23456.78 + random.nextInt(10000))));
        flows.add(createFlowLink("社会捐赠", "应急储备", (56789.12 + random.nextInt(20000))));

        result.put("sources", sources);
        result.put("targets", targets);
        result.put("flows", flows);

        return AjaxResult.success(result);
    }

    /**
     * 创建流向节点
     */
    private Map<String, Object> createFlowNode(String name, String type, String color)
    {
        Map<String, Object> node = new HashMap<>();
        node.put("name", name);
        node.put("type", type);
        node.put("color", color);
        return node;
    }

    /**
     * 创建流向连接
     */
    private Map<String, Object> createFlowLink(String source, String target, double value)
    {
        Map<String, Object> link = new HashMap<>();
        link.put("source", source);
        link.put("target", target);
        link.put("value", value);
        return link;
    }

    /**
     * 获取各机构资金分布
     */
    @Anonymous
    @GetMapping("/fund/institution-distribution")
    public AjaxResult getFundInstitutionDistribution()
    {
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();

        String[] institutions = {
            "郑州人民医院颐养院", "金水区养老中心", "二七区老年公寓",
            "中原区养老院", "管城回族区敬老院", "惠济区养老服务中心",
            "中牟县老年康养中心", "巩义市颐养院", "荥阳市养老院",
            "新密市老年公寓", "新郑市养老中心", "登封市敬老院"
        };

        for (int i = 0; i < institutions.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", institutions[i]);
            item.put("balance", 1234567.89 + random.nextInt(2000000));
            item.put("todayIncome", 12345.67 + random.nextInt(20000));
            item.put("todayExpense", 5678.90 + random.nextInt(10000));
            item.put("elderCount", 150 + random.nextInt(300));
            data.add(item);
        }

        return AjaxResult.success(data);
    }

    /**
     * 获取资金趋势数据（近30天）
     */
    @Anonymous
    @GetMapping("/fund/trends")
    public AjaxResult getFundTrends()
    {
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();

        for (int i = 29; i >= 0; i--) {
            Map<String, Object> item = new HashMap<>();
            LocalDate date = LocalDate.now().minusDays(i);

            item.put("date", date.toString());
            item.put("income", 123456.78 + random.nextInt(50000));
            item.put("expense", 56789.12 + random.nextInt(30000));
            item.put("transfer", 23456.78 + random.nextInt(20000));
            item.put("balance", 89000000.00 + random.nextInt(5000000));

            data.add(item);
        }

        return AjaxResult.success(data);
    }

    /**
     * 获取资金审批数据
     */
    @Anonymous
    @GetMapping("/fund/approval-stats")
    public AjaxResult getFundApprovalStats()
    {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random();

        // 审批统计
        data.put("totalApplications", 156 + random.nextInt(50)); // 总申请数
        data.put("pendingApplications", 23 + random.nextInt(20)); // 待审批数
        data.put("approvedApplications", 89 + random.nextInt(30)); // 已审批数
        data.put("rejectedApplications", 12 + random.nextInt(10)); // 已驳回数

        // 按类型统计
        List<Map<String, Object>> typeStats = new ArrayList<>();
        String[] types = {"资金划拨", "退款申请", "押金申请"};

        for (String type : types) {
            Map<String, Object> typeItem = new HashMap<>();
            typeItem.put("type", type);
            typeItem.put("pending", 5 + random.nextInt(10));
            typeItem.put("approved", 15 + random.nextInt(20));
            typeItem.put("rejected", 2 + random.nextInt(5));
            typeStats.add(typeItem);
        }
        data.put("typeStats", typeStats);

        return AjaxResult.success(data);
    }

    /**
     * 获取预警资金数据
     */
    @Anonymous
    @GetMapping("/fund/warnings")
    public AjaxResult getFundWarnings()
    {
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();

        String[] warningTypes = {"余额不足", "异常支出", "大额资金变动", "逾期未处理"};
        String[] levels = {"高", "中", "低", "高"};

        for (int i = 0; i < warningTypes.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", warningTypes[i]);
            item.put("level", levels[i]);
            item.put("count", 5 + random.nextInt(15));
            item.put("amount", 12345.67 + random.nextInt(50000));
            item.put("lastUpdate", System.currentTimeMillis() - random.nextInt(86400000));
            data.add(item);
        }

        return AjaxResult.success(data);
    }

    /**
     * 获取预警总览数据
     */
    @Anonymous
    @GetMapping("/warning/overview")
    public AjaxResult getWarningOverview()
    {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random();

        // 预警总览数据
        data.put("todayWarnings", 12 + random.nextInt(8)); // 今日预警数
        data.put("processingWarnings", 5 + random.nextInt(5)); // 处理中预警
        data.put("processedWarnings", 8 + random.nextInt(10)); // 已处理预警
        data.put("highRiskWarnings", 2 + random.nextInt(3)); // 高风险预警
        data.put("totalWarnings", 156 + random.nextInt(50)); // 总预警数

        // 处理率统计
        int total = (Integer)data.get("todayWarnings");
        int processed = (Integer)data.get("processedWarnings");
        double processingRate = total > 0 ? (double)processed / total * 100 : 0;
        data.put("processingRate", Math.round(processingRate * 100) / 100.0); // 处理率

        // 平均处理时间（分钟）
        data.put("avgProcessTime", 45 + random.nextInt(60));

        return AjaxResult.success(data);
    }

    /**
     * 获取预警类型分布
     */
    @Anonymous
    @GetMapping("/warning/types")
    public AjaxResult getWarningTypes()
    {
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();

        String[] types = {"资金异常", "人员安全", "设备故障", "服务投诉", "健康风险", "其他"};
        String[] levels = {"高", "中", "低", "高", "中", "低"};
        int[] colors = {0xef4444, 0xf59e0b, 0x10b981, 0xef4444, 0xf59e0b, 0x6b7280};

        for (int i = 0; i < types.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", types[i]);
            item.put("level", levels[i]);
            item.put("count", 5 + random.nextInt(20));
            item.put("todayCount", random.nextInt(8));
            item.put("color", colors[i]);
            data.add(item);
        }

        return AjaxResult.success(data);
    }

    /**
     * 获取预警趋势数据
     */
    @Anonymous
    @GetMapping("/warning/trends")
    public AjaxResult getWarningTrends()
    {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random();

        // 生成最近7天的日期
        List<String> dates = new ArrayList<>();
        List<Integer> totalCounts = new ArrayList<>();
        List<Integer> processedCounts = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.toString());
            totalCounts.add(8 + random.nextInt(15));
            processedCounts.add(5 + random.nextInt(12));
        }

        data.put("dates", dates);
        data.put("totalCounts", totalCounts);
        data.put("processedCounts", processedCounts);

        return AjaxResult.success(data);
    }

    /**
     * 获取机构预警排行
     */
    @Anonymous
    @GetMapping("/warning/institution-rank")
    public AjaxResult getWarningInstitutionRank()
    {
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();

        String[] institutions = {
            "郑州人民医院颐养院", "金水区养老中心", "二七区老年公寓",
            "中原区养老院", "管城回族区敬老院", "惠济区养老服务中心",
            "中牟县老年康养中心", "巩义市颐养院", "荥阳市养老院",
            "新密市老年公寓", "新郑市养老中心", "登封市敬老院"
        };

        for (int i = 0; i < institutions.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("institutionName", institutions[i]);
            item.put("warningCount", 15 + random.nextInt(25));
            item.put("highRiskCount", random.nextInt(5));
            item.put("processedCount", 10 + random.nextInt(20));
            item.put("processingRate", 60 + random.nextInt(35));
            data.add(item);
        }

        // 按预警数量排序
        data.sort((a, b) -> Integer.compare((Integer)b.get("warningCount"), (Integer)a.get("warningCount")));

        return AjaxResult.success(data);
    }

    /**
     * 获取实时预警列表
     */
    @Anonymous
    @GetMapping("/warning/realtime")
    public AjaxResult getWarningRealtime()
    {
        List<Map<String, Object>> data = new ArrayList<>();
        Random random = new Random();

        String[] institutions = {
            "郑州人民医院颐养院", "金水区养老中心", "二七区老年公寓",
            "中原区养老院", "管城回族区敬老院", "惠济区养老服务中心"
        };

        String[] warningTypes = {"资金异常", "人员安全", "设备故障", "服务投诉", "健康风险"};
        String[] levels = {"高", "中", "低"};
        String[] statuses = {"待处理", "处理中", "已处理"};

        for (int i = 0; i < 20; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", "W" + String.format("%04d", i + 1));
            item.put("institutionName", institutions[random.nextInt(institutions.length)]);
            item.put("warningType", warningTypes[random.nextInt(warningTypes.length)]);
            item.put("level", levels[random.nextInt(levels.length)]);
            item.put("status", statuses[random.nextInt(statuses.length)]);
            item.put("description", generateWarningDescription(warningTypes[random.nextInt(warningTypes.length)]));
            item.put("createTime", System.currentTimeMillis() - random.nextInt(86400000));
            item.put("processTime", random.nextBoolean() ? System.currentTimeMillis() - random.nextInt(3600000) : null);
            data.add(item);
        }

        // 按时间排序
        data.sort((a, b) -> Long.compare((Long)b.get("createTime"), (Long)a.get("createTime")));

        return AjaxResult.success(data);
    }

    /**
     * 获取预警处理统计
     */
    @Anonymous
    @GetMapping("/warning/process-stats")
    public AjaxResult getWarningProcessStats()
    {
        Map<String, Object> data = new HashMap<>();
        Random random = new Random();

        // 按处理状态统计
        List<Map<String, Object>> statusStats = new ArrayList<>();
        String[] statuses = {"待处理", "处理中", "已处理", "已忽略"};
        int[] statusCounts = {8 + random.nextInt(10), 3 + random.nextInt(5), 12 + random.nextInt(15), 2 + random.nextInt(3)};

        for (int i = 0; i < statuses.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("status", statuses[i]);
            item.put("count", statusCounts[i]);
            statusStats.add(item);
        }
        data.put("statusStats", statusStats);

        // 按预警级别统计
        List<Map<String, Object>> levelStats = new ArrayList<>();
        String[] levels = {"高", "中", "低"};
        int[] levelCounts = {3 + random.nextInt(5), 8 + random.nextInt(10), 12 + random.nextInt(15)};

        for (int i = 0; i < levels.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("level", levels[i]);
            item.put("count", levelCounts[i]);
            levelStats.add(item);
        }
        data.put("levelStats", levelStats);

        // 处理时效统计
        data.put("avgProcessTime", 45 + random.nextInt(60)); // 平均处理时间（分钟）
        data.put("fastProcessTime", 15 + random.nextInt(20)); // 最快处理时间
        data.put("slowProcessTime", 120 + random.nextInt(180)); // 最慢处理时间

        return AjaxResult.success(data);
    }

    /**
     * 生成预警描述
     */
    private String generateWarningDescription(String type) {
        Random random = new Random();
        switch (type) {
            case "资金异常":
                String[] moneyDescs = {"账户余额低于警戒线", "出现异常大额支出", "资金流水异常", "费用缴纳逾期"};
                return moneyDescs[random.nextInt(moneyDescs.length)];
            case "人员安全":
                String[] safetyDescs = {"老人长时间未活动", "紧急呼叫未响应", "夜间异常活动", "离开安全区域"};
                return safetyDescs[random.nextInt(safetyDescs.length)];
            case "设备故障":
                String[] deviceDescs = {"医疗设备故障", "监控设备离线", "紧急呼叫系统故障", "生命体征监测异常"};
                return deviceDescs[random.nextInt(deviceDescs.length)];
            case "服务投诉":
                String[] serviceDescs = {"服务质量投诉", "服务态度问题", "服务响应超时", "服务流程异常"};
                return serviceDescs[random.nextInt(serviceDescs.length)];
            case "健康风险":
                String[] healthDescs = {"生命体征异常", "用药提醒未响应", "体检报告异常", "健康状况下降"};
                return healthDescs[random.nextInt(healthDescs.length)];
            default:
                return "系统预警信息";
        }
    }

    // ==================== 首页工作台数据接口 ====================

    /**
     * 获取首页核心业务统计数据
     */
    @Anonymous
    @GetMapping("/dashboard/overview")
    public AjaxResult getDashboardOverview()
    {
        Map<String, Object> result = new HashMap<>();

        // 核心业务统计数据
        result.put("institutionCount", 289);
        result.put("elderlyCount", 3567);
        result.put("totalFunds", 52345000.00);
        result.put("bedUsageRate", 78.5);
        result.put("todayWarnings", 12);
        result.put("pendingApplications", 8);

        return success(result);
    }

    /**
     * 获取首页资金监管概览数据
     */
    @Anonymous
    @GetMapping("/dashboard/funds-overview")
    public AjaxResult getFundsOverview()
    {
        Map<String, Object> result = new HashMap<>();

        // 资金流入流出趋势（近30天）
        List<Map<String, Object>> fundTrend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        Random random = new Random();

        for (int i = 29; i >= 0; i--) {
            Map<String, Object> dayData = new HashMap<>();
            LocalDate date = today.minusDays(i);
            dayData.put("date", date.toString());
            dayData.put("income", 500000 + random.nextInt(300000));
            dayData.put("expense", 200000 + random.nextInt(150000));
            fundTrend.add(dayData);
        }
        result.put("fundTrend", fundTrend);

        // 各机构资金余额分布
        List<Map<String, Object>> institutionFunds = new ArrayList<>();
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心", "金色年华家园", "绿野仙踪疗养院"};
        double[] funds = {5230000, 3890000, 2950000, 1780000, 960000};

        for (int i = 0; i < institutions.length; i++) {
            Map<String, Object> fundData = new HashMap<>();
            fundData.put("name", institutions[i]);
            fundData.put("balance", funds[i]);
            fundData.put("change", (random.nextDouble() - 0.5) * 200000);
            institutionFunds.add(fundData);
        }
        result.put("institutionFunds", institutionFunds);

        // 大额资金异动提醒
        List<Map<String, Object>> largeTransactions = new ArrayList<>();
        Map<String, Object> transaction1 = new HashMap<>();
        transaction1.put("institution", "幸福养老院");
        transaction1.put("amount", 1500000);
        transaction1.put("type", "收入");
        transaction1.put("time", "2025-10-31 10:30");
        transaction1.put("status", "正常");
        largeTransactions.add(transaction1);

        Map<String, Object> transaction2 = new HashMap<>();
        transaction2.put("institution", "夕阳红公寓");
        transaction2.put("amount", 800000);
        transaction2.put("type", "支出");
        transaction2.put("time", "2025-10-31 09:15");
        transaction2.put("status", "需关注");
        largeTransactions.add(transaction2);
        result.put("largeTransactions", largeTransactions);

        return success(result);
    }

    /**
     * 获取首页机构运营状态数据
     */
    @Anonymous
    @GetMapping("/dashboard/operations-overview")
    public AjaxResult getOperationsOverview()
    {
        Map<String, Object> result = new HashMap<>();

        // 机构入驻申请审批进度
        result.put("pendingApplications", 8);
        result.put("approvedToday", 3);
        result.put("rejectedToday", 1);

        // 床位使用情况
        List<Map<String, Object>> bedUsage = new ArrayList<>();
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心", "金色年华家园", "绿野仙踪疗养院"};
        int[] totalBeds = {200, 150, 180, 120, 90};
        int[] usedBeds = {168, 125, 141, 87, 69};

        for (int i = 0; i < institutions.length; i++) {
            Map<String, Object> bedData = new HashMap<>();
            bedData.put("name", institutions[i]);
            bedData.put("total", totalBeds[i]);
            bedData.put("used", usedBeds[i]);
            bedData.put("rate", Math.round((double) usedBeds[i] / totalBeds[i] * 100 * 10) / 10.0);
            bedUsage.add(bedData);
        }
        result.put("bedUsage", bedUsage);

        // 老人入住退住趋势（近7天）
        List<Map<String, Object>> elderlyTrend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            Map<String, Object> dayData = new HashMap<>();
            LocalDate date = today.minusDays(i);
            dayData.put("date", date.toString());
            dayData.put("checkIn", 3 + (int)(Math.random() * 8));
            dayData.put("checkOut", 1 + (int)(Math.random() * 4));
            elderlyTrend.add(dayData);
        }
        result.put("elderlyTrend", elderlyTrend);

        return success(result);
    }

    /**
     * 获取首页风险监控预警数据
     */
    @Anonymous
    @GetMapping("/dashboard/risk-overview")
    public AjaxResult getRiskOverview()
    {
        Map<String, Object> result = new HashMap<>();

        // 预警等级分布
        Map<String, Object> warningLevels = new HashMap<>();
        warningLevels.put("high", 5);
        warningLevels.put("medium", 18);
        warningLevels.put("low", 9);
        result.put("warningLevels", warningLevels);

        // 待处理预警列表
        List<Map<String, Object>> pendingWarnings = new ArrayList<>();

        Map<String, Object> warning1 = new HashMap<>();
        warning1.put("id", "W001");
        warning1.put("institution", "夕阳红公寓");
        warning1.put("type", "资金异常");
        warning1.put("level", "高");
        warning1.put("content", "账户余额低于警戒线");
        warning1.put("time", "2025-10-31 08:30");
        warning1.put("status", "待处理");
        pendingWarnings.add(warning1);

        Map<String, Object> warning2 = new HashMap<>();
        warning2.put("id", "W002");
        warning2.put("institution", "康乐养老中心");
        warning2.put("type", "入住超员");
        warning2.put("level", "中");
        warning2.put("content", "实际入住人数超过核定床位");
        warning2.put("time", "2025-10-31 07:45");
        warning2.put("status", "处理中");
        pendingWarnings.add(warning2);

        Map<String, Object> warning3 = new HashMap<>();
        warning3.put("id", "W003");
        warning3.put("institution", "金色年华家园");
        warning3.put("type", "资质过期");
        warning3.put("level", "高");
        warning3.put("content", "食品经营许可证已过期");
        warning3.put("time", "2025-10-30 16:20");
        warning3.put("status", "待处理");
        pendingWarnings.add(warning3);
        result.put("pendingWarnings", pendingWarnings);

        // 高风险机构提醒
        List<Map<String, Object>> highRiskInstitutions = new ArrayList<>();

        Map<String, Object> risk1 = new HashMap<>();
        risk1.put("name", "夕阳红公寓");
        risk1.put("riskScore", 85);
        risk1.put("warningCount", 5);
        risk1.put("lastWarning", "资金异常");
        highRiskInstitutions.add(risk1);

        Map<String, Object> risk2 = new HashMap<>();
        risk2.put("name", "康乐养老中心");
        risk2.put("riskScore", 72);
        risk2.put("warningCount", 3);
        risk2.put("lastWarning", "入住超员");
        highRiskInstitutions.add(risk2);
        result.put("highRiskInstitutions", highRiskInstitutions);

        return success(result);
    }
}