package com.ruoyi.web.controller.pension;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;

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
}
