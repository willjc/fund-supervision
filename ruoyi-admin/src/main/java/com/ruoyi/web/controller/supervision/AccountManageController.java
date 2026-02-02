package com.ruoyi.web.controller.supervision;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.service.pension.ISupervisionAccountLogService;

/**
 * 民政监管-账户管理Controller
 *
 * @author ruoyi
 * @date 2025-01-03
 */
@RestController
@RequestMapping("/supervision/account")
public class AccountManageController extends BaseController
{
    @Autowired
    private ISupervisionAccountLogService supervisionAccountLogService;

    /**
     * 获取机构账户统计数据
     */
    @GetMapping("/institution/statistics")
    public AjaxResult getInstitutionStatistics()
    {
        Map<String, Object> statistics = supervisionAccountLogService.getInstitutionStatistics();
        return success(statistics);
    }

    /**
     * 机构账户查询列表
     */
    @GetMapping("/institution/list")
    public TableDataInfo getInstitutionAccountList(
            @RequestParam(required = false) String institutionName,
            @RequestParam(required = false) String accountStatus,
            @RequestParam(required = false) String accountType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        startPage();

        Map<String, Object> params = new HashMap<>();
        if (institutionName != null && !institutionName.isEmpty()) {
            params.put("institutionName", institutionName);
        }
        if (accountStatus != null && !accountStatus.isEmpty()) {
            params.put("accountStatus", accountStatus);
        }

        List<Map<String, Object>> list = supervisionAccountLogService.selectInstitutionAccountList(params);
        return getDataTable(list);
    }

    /**
     * 会员费管理列表
     */
    @GetMapping("/member-fee/list")
    public TableDataInfo getMemberFeeList(
            @RequestParam(required = false) String institutionName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询会员费管理信息
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心"};

        for (int i = 0; i < 8; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("institutionName", institutions[random.nextInt(institutions.length)]);
            item.put("memberFeeRate", random.nextDouble() * 0.02 + 0.01); // 1%-3%
            item.put("monthlyIncome", random.nextDouble() * 100000 + 50000); // 5万-15万
            item.put("memberFeeCollected", random.nextDouble() * 3000 + 1500); // 1500-4500
            item.put("status", random.nextBoolean() ? "正常" : "异常");
            item.put("lastUpdateTime", "2025-01-03 " + (9 + i) + ":30:00");
            list.add(item);
        }

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 会员费收取配置
     */
    @PostMapping("/member-fee/config")
    public AjaxResult configMemberFee(@RequestBody Map<String, Object> data)
    {
        // TODO: 配置会员费收取规则
        logger.info("配置会员费收取规则: {}", data);
        return success("配置成功");
    }

    /**
     * 监管账户维护列表
     */
    @GetMapping("/supervision/list")
    public TableDataInfo getSupervisionAccountList(
            @RequestParam(required = false) String institutionName,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询监管账户信息
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心"};

        for (int i = 0; i < 8; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("institutionName", institutions[random.nextInt(institutions.length)]);
            item.put("supervisionAccountNo", "4101234567890" + (1000 + i));
            item.put("accountName", institutions[random.nextInt(institutions.length)] + "监管账户");
            item.put("accountBalance", random.nextDouble() * 10000000 + 2000000); // 200万-1200万
            item.put("todayIncome", random.nextDouble() * 100000 + 20000); // 2万-12万
            item.put("todayExpenditure", random.nextDouble() * 50000 + 10000); // 1万-6万
            item.put("status", random.nextBoolean() ? "正常" : "异常");
            item.put("lastCheckTime", "2025-01-03 " + (8 + i) + ":00:00");
            list.add(item);
        }

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 监管账户资金划转
     */
    @PostMapping("/supervision/transfer")
    public AjaxResult transferSupervisionFunds(@RequestBody Map<String, Object> data)
    {
        // TODO: 处理监管账户资金划转
        logger.info("监管账户资金划转: {}", data);
        return success("划转申请已提交");
    }

    /**
     * 订单列表
     */
    @GetMapping("/order/list")
    public TableDataInfo getOrderList(
            @RequestParam(required = false) String institutionName,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询订单信息
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心"};
        String[] statuses = {"待支付", "已支付", "已完成", "已取消"};

        for (int i = 0; i < 10; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("orderNo", "DD20250103" + String.format("%03d", i + 1));
            item.put("institutionName", institutions[random.nextInt(institutions.length)]);
            item.put("elderName", "老人" + (i + 1) + "号");
            item.put("orderType", "床位费");
            item.put("orderAmount", random.nextDouble() * 5000 + 2000); // 2000-7000
            item.put("paidAmount", random.nextDouble() * 5000 + 2000);
            item.put("status", statuses[random.nextInt(statuses.length)]);
            item.put("orderTime", "2025-01-03 " + (8 + i) + ":" + (random.nextInt(60)) + ":00");
            list.add(item);
        }

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 订单详情
     */
    @GetMapping("/order/detail/{orderNo}")
    public AjaxResult getOrderDetail(@PathVariable String orderNo)
    {
        // TODO: 查询订单详细信息
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", orderNo);
        data.put("institutionName", "幸福养老院");
        data.put("elderName", "李奶奶");
        data.put("idCard", "410101194001010001");
        data.put("orderType", "床位费");
        data.put("orderAmount", 3500.00);
        data.put("paidAmount", 3500.00);
        data.put("orderTime", "2025-01-03 10:30:00");
        data.put("status", "已支付");
        data.put("paymentMethod", "银行转账");
        data.put("paymentTime", "2025-01-03 10:35:00");
        return success(data);
    }

    /**
     * 账户余额列表
     */
    @GetMapping("/balance/list")
    public TableDataInfo getBalanceList(
            @RequestParam(required = false) String institutionName,
            @RequestParam(required = false) String accountType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询账户余额信息
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心"};

        for (int i = 0; i < 8; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("institutionName", institutions[random.nextInt(institutions.length)]);
            item.put("elderName", "老人" + (i + 1) + "号");
            item.put("accountType", random.nextBoolean() ? "个人账户" : "机构账户");
            item.put("accountBalance", random.nextDouble() * 100000 + 10000); // 1万-11万
            item.put("availableBalance", random.nextDouble() * 80000 + 8000); // 8000-8.8万
            item.put("freezeAmount", random.nextDouble() * 20000); // 0-2万
            item.put("lastUpdateTime", "2025-01-03 " + (10 + i) + ":00:00");
            list.add(item);
        }

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 余额变动明细
     */
    @GetMapping("/balance/detail/{accountType}/{accountId}")
    public TableDataInfo getBalanceDetail(
            @PathVariable String accountType,
            @PathVariable String accountId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询余额变动明细
        String[] types = {"充值", "消费", "退款", "冻结", "解冻"};

        for (int i = 0; i < 10; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("transactionNo", "TX20250103" + String.format("%03d", i + 1));
            item.put("transactionType", types[random.nextInt(types.length)]);
            item.put("amount", random.nextDouble() * 10000 - 5000); // -5000到5000
            item.put("balanceBefore", random.nextDouble() * 100000 + 10000);
            item.put("balanceAfter", random.nextDouble() * 100000 + 10000);
            item.put("description", "账户资金变动");
            item.put("transactionTime", "2025-01-03 " + (8 + i) + ":" + (random.nextInt(60)) + ":00");
            list.add(item);
        }

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }
}