package com.ruoyi.web.controller.supervision;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 民政监管-资金管理Controller
 *
 * @author ruoyi
 * @date 2025-01-03
 */
@RestController
@RequestMapping("/supervision/fund")
public class FundManageController extends BaseController
{
    /**
     * 资金记录查看
     */
    @GetMapping("/record/list")
    public TableDataInfo getFundRecordList(
            @RequestParam(required = false) String institutionName,
            @RequestParam(required = false) String fundType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询资金记录信息
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心"};
        String[] fundTypes = {"预收款", "押金", "会员费", "政府补贴", "其他资金"};
        String[] directions = {"收入", "支出"};

        for (int i = 0; i < 15; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("recordNo", "ZJ20250103" + String.format("%04d", i + 1));
            item.put("institutionName", institutions[random.nextInt(institutions.length)]);
            item.put("fundType", fundTypes[random.nextInt(fundTypes.length)]);
            item.put("amount", random.nextDouble() * 100000 + 10000); // 1万-11万
            item.put("direction", directions[random.nextInt(directions.length)]);
            item.put("balance", random.nextDouble() * 5000000 + 1000000); // 100万-600万
            item.put("transactionTime", "2025-01-03 " + (8 + i) + ":" + (random.nextInt(60)) + ":00");
            item.put("description", "资金流动记录");
            item.put("operator", "管理员" + (i % 3 + 1));
            list.add(item);
        }

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 资金统计概览
     */
    @GetMapping("/statistics")
    public AjaxResult getFundStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate)
    {
        // TODO: 查询资金统计数据
        Map<String, Object> data = new HashMap<>();

        // 总体统计
        Map<String, Object> totalStats = new HashMap<>();
        totalStats.put("totalBalance", 12567890.50); // 总余额
        totalStats.put("todayIncome", 234567.80); // 今日收入
        totalStats.put("todayExpenditure", 123456.30); // 今日支出
        totalStats.put("monthIncome", 3456789.00); // 本月收入
        totalStats.put("monthExpenditure", 2234567.50); // 本月支出
        data.put("totalStats", totalStats);

        // 资金类型分布
        List<Map<String, Object>> fundTypeDistribution = new ArrayList<>();
        String[] types = {"预收款", "押金", "会员费", "政府补贴"};
        double[] amounts = {4567890.00, 2345678.50, 3456789.00, 2197543.00};

        for (int i = 0; i < types.length; i++) {
            Map<String, Object> type = new HashMap<>();
            type.put("fundType", types[i]);
            type.put("amount", amounts[i]);
            type.put("percentage", amounts[i] / 12567890.50 * 100);
            fundTypeDistribution.add(type);
        }
        data.put("fundTypeDistribution", fundTypeDistribution);

        // 机构资金排行
        List<Map<String, Object>> institutionRanking = new ArrayList<>();
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心"};

        for (int i = 0; i < institutions.length; i++) {
            Random random = new Random();
            Map<String, Object> institution = new HashMap<>();
            institution.put("institutionName", institutions[i]);
            institution.put("balance", (3 - i) * 2000000 + random.nextInt(500000));
            institution.put("rank", i + 1);
            institutionRanking.add(institution);
        }
        data.put("institutionRanking", institutionRanking);

        return success(data);
    }

    /**
     * 资金流动明细
     */
    @GetMapping("/flow/detail")
    public TableDataInfo getFundFlowDetail(
            @RequestParam(required = false) String institutionName,
            @RequestParam(required = false) String fundType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询资金流动明细
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心"};
        String[] fundTypes = {"预收款", "押金", "会员费"};
        String[] directions = {"收入", "支出"};

        for (int i = 0; i < 20; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("flowId", "FL20250103" + String.format("%04d", i + 1));
            item.put("institutionName", institutions[random.nextInt(institutions.length)]);
            item.put("fundType", fundTypes[random.nextInt(fundTypes.length)]);
            item.put("direction", directions[random.nextInt(directions.length)]);
            item.put("amount", random.nextDouble() * 50000 + 5000); // 5000-55000
            item.put("balanceBefore", random.nextDouble() * 1000000 + 500000);
            item.put("balanceAfter", random.nextDouble() * 1000000 + 500000);
            item.put("transactionTime", "2025-01-03 " + (8 + i) + ":" + (random.nextInt(60)) + ":00");
            item.put("description", "资金流动明细");
            item.put("relatedOrderNo", "DD" + String.format("%08d", random.nextInt(100000)));
            list.add(item);
        }

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 分配规则配置列表
     */
    @GetMapping("/allocation-rule/list")
    public TableDataInfo getAllocationRuleList(
            @RequestParam(required = false) String ruleName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();

        // TODO: 从数据库查询分配规则配置
        Map<String, Object> rule1 = new HashMap<>();
        rule1.put("ruleId", 1);
        rule1.put("ruleName", "预收款分配规则");
        rule1.put("ruleType", "预收款");
        rule1.put("allocationRatio", "70%监管账户, 30%基本账户");
        rule1.put("maxAmount", 100000.00);
        rule1.put("status", "启用");
        rule1.put("createTime", "2024-12-01 10:00:00");
        rule1.put("updateTime", "2025-01-02 15:30:00");
        list.add(rule1);

        Map<String, Object> rule2 = new HashMap<>();
        rule2.put("ruleId", 2);
        rule2.put("ruleName", "押金分配规则");
        rule2.put("ruleType", "押金");
        rule2.put("allocationRatio", "100%监管账户");
        rule2.put("maxAmount", 50000.00);
        rule2.put("status", "启用");
        rule2.put("createTime", "2024-12-01 10:00:00");
        rule2.put("updateTime", "2025-01-02 15:30:00");
        list.add(rule2);

        Map<String, Object> rule3 = new HashMap<>();
        rule3.put("ruleId", 3);
        rule3.put("ruleName", "会员费分配规则");
        rule3.put("ruleType", "会员费");
        rule3.put("allocationRatio", "50%监管账户, 50%基本账户");
        rule3.put("maxAmount", 20000.00);
        rule3.put("status", "启用");
        rule3.put("createTime", "2024-12-01 10:00:00");
        rule3.put("updateTime", "2025-01-02 15:30:00");
        list.add(rule3);

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 新增分配规则
     */
    @PostMapping("/allocation-rule/add")
    public AjaxResult addAllocationRule(@RequestBody Map<String, Object> data)
    {
        // TODO: 保存新的分配规则
        logger.info("新增分配规则: {}", data);
        return success("规则添加成功");
    }

    /**
     * 修改分配规则
     */
    @PutMapping("/allocation-rule/update/{ruleId}")
    public AjaxResult updateAllocationRule(@PathVariable Long ruleId, @RequestBody Map<String, Object> data)
    {
        // TODO: 更新分配规则
        logger.info("更新分配规则 {}: {}", ruleId, data);
        return success("规则更新成功");
    }

    /**
     * 删除分配规则
     */
    @DeleteMapping("/allocation-rule/delete/{ruleId}")
    public AjaxResult deleteAllocationRule(@PathVariable Long ruleId)
    {
        // TODO: 删除分配规则
        logger.info("删除分配规则: {}", ruleId);
        return success("规则删除成功");
    }

    /**
     * 分配规则详情
     */
    @GetMapping("/allocation-rule/detail/{ruleId}")
    public AjaxResult getAllocationRuleDetail(@PathVariable Long ruleId)
    {
        // TODO: 查询分配规则详细信息
        Map<String, Object> data = new HashMap<>();
        data.put("ruleId", ruleId);
        data.put("ruleName", "预收款分配规则");
        data.put("ruleType", "预收款");
        data.put("allocationRatio", "70%监管账户, 30%基本账户");
        data.put("supervisionRatio", 70);
        data.put("basicRatio", 30);
        data.put("maxAmount", 100000.00);
        data.put("minAmount", 1000.00);
        data.put("status", "启用");
        data.put("description", "预收款资金自动分配到监管账户和基本账户");
        data.put("createTime", "2024-12-01 10:00:00");
        data.put("updateTime", "2025-01-02 15:30:00");

        // 规则条件
        List<Map<String, Object>> conditions = new ArrayList<>();
        Map<String, Object> condition1 = new HashMap<>();
        condition1.put("conditionType", "金额范围");
        condition1.put("conditionValue", "1000-100000");
        condition1.put("allocationRule", "70%监管账户, 30%基本账户");
        conditions.add(condition1);

        Map<String, Object> condition2 = new HashMap<>();
        condition2.put("conditionType", "机构类型");
        condition2.put("conditionValue", "所有机构");
        condition2.put("allocationRule", "70%监管账户, 30%基本账户");
        conditions.add(condition2);
        data.put("conditions", conditions);

        return success(data);
    }

    /**
     * 规则执行历史
     */
    @GetMapping("/allocation-rule/history/{ruleId}")
    public TableDataInfo getRuleExecutionHistory(
            @PathVariable Long ruleId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询规则执行历史
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心"};

        for (int i = 0; i < 10; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("executionId", "EX20250103" + String.format("%04d", i + 1));
            item.put("institutionName", institutions[random.nextInt(institutions.length)]);
            item.put("totalAmount", random.nextDouble() * 50000 + 10000);
            item.put("supervisionAmount", random.nextDouble() * 35000 + 7000);
            item.put("basicAmount", random.nextDouble() * 15000 + 3000);
            item.put("executionTime", "2025-01-03 " + (8 + i) + ":" + (random.nextInt(60)) + ":00");
            item.put("status", "执行成功");
            item.put("relatedOrderNo", "DD" + String.format("%08d", random.nextInt(100000)));
            list.add(item);
        }

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }
}