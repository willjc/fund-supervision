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
 * 民政监管-核验预警Controller
 *
 * @author ruoyi
 * @date 2025-01-03
 */
@RestController
@RequestMapping("/supervision/warning")
public class WarningController extends BaseController
{
    /**
     * 预警列表（所有类型）
     */
    @GetMapping("/list")
    public TableDataInfo getWarningList(
            @RequestParam(required = false) String warningType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询
        String[] types = {"费用超额", "押金超额", "入驻超额", "监管超额", "风险保证金", "大额支付", "突发风险"};
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心"};

        for (int i = 0; i < 10; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("warningNo", "YJ2025010" + (i + 1));
            item.put("warningType", types[random.nextInt(types.length)]);
            item.put("institutionName", institutions[random.nextInt(institutions.length)]);
            item.put("warningContent", "检测到异常情况，请及时处理");
            item.put("warningLevel", random.nextInt(3) == 0 ? "高" : "中");
            item.put("warningTime", "2025-01-03 " + (10 + i) + ":30:00");
            item.put("status", random.nextBoolean() ? "待处理" : "已处理");
            item.put("contactPerson", "张经理");
            item.put("contactPhone", "138001380" + i);
            list.add(item);
        }

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 核验费用超额预警
     */
    @GetMapping("/fee-excess")
    public TableDataInfo getFeeExcessWarning(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        // TODO: 查询费用超额预警（入住人预收费超过月费用12倍）
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> item = new HashMap<>();
        item.put("institutionName", "幸福养老院");
        item.put("elderName", "李奶奶");
        item.put("monthlyFee", 3000);
        item.put("prepaidFee", 50000);
        item.put("excessAmount", 14000);
        item.put("warningTime", "2025-01-03 10:00:00");
        list.add(item);

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 核验押金超额预警
     */
    @GetMapping("/deposit-excess")
    public TableDataInfo getDepositExcessWarning(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        // TODO: 查询押金超额（押金高于床位费用12倍）
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> item = new HashMap<>();
        item.put("institutionName", "夕阳红公寓");
        item.put("elderName", "王爷爷");
        item.put("bedFee", 1500);
        item.put("depositAmount", 25000);
        item.put("excessAmount", 7000);
        item.put("warningTime", "2025-01-03 09:00:00");
        list.add(item);

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 核验入驻超额预警
     */
    @GetMapping("/checkin-excess")
    public TableDataInfo getCheckinExcessWarning(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        // TODO: 查询入驻超额（超过备案床位总数）
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> item = new HashMap<>();
        item.put("institutionName", "康乐养老中心");
        item.put("approvedBeds", 200);
        item.put("actualElders", 215);
        item.put("excessCount", 15);
        item.put("warningTime", "2025-01-03 11:00:00");
        list.add(item);

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 核验监管超额预警
     */
    @GetMapping("/supervision-excess")
    public TableDataInfo getSupervisionExcessWarning(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        // TODO: 查询监管超额（监管账户余额超出固定资产净额）
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> item = new HashMap<>();
        item.put("institutionName", "金色年华养老院");
        item.put("supervisionBalance", 5000000);
        item.put("fixedAssets", 3000000);
        item.put("excessAmount", 2000000);
        item.put("warningTime", "2025-01-02 15:00:00");
        list.add(item);

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 风险保证金超额预警
     */
    @GetMapping("/risk-deposit-excess")
    public TableDataInfo getRiskDepositExcessWarning(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        // TODO: 查询风险保证金超额
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> item = new HashMap<>();
        item.put("institutionName", "绿野仙踪疗养院");
        item.put("accountBalance", 800000);
        item.put("riskDepositLimit", 500000);
        item.put("excessAmount", 300000);
        item.put("warningTime", "2025-01-03 14:00:00");
        list.add(item);

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 大额支付预警
     */
    @GetMapping("/large-payment")
    public TableDataInfo getLargePaymentWarning(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        // TODO: 查询大额支付预警
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> item = new HashMap<>();
        item.put("institutionName", "幸福养老院");
        item.put("paymentAmount", 500000);
        item.put("paymentPurpose", "设备采购");
        item.put("paymentTime", "2025-01-03 10:30:00");
        item.put("status", "待审核");
        list.add(item);

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 突发对方风险预警
     */
    @GetMapping("/emergency-risk")
    public TableDataInfo getEmergencyRiskWarning(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        // TODO: 查询突发风险预警
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> item = new HashMap<>();
        item.put("institutionName", "夕阳红公寓");
        item.put("riskType", "资金异常流动");
        item.put("riskLevel", "高");
        item.put("riskDescription", "检测到短时间内大额资金异常流动");
        item.put("warningTime", "2025-01-03 12:00:00");
        list.add(item);

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 处理预警
     */
    @PostMapping("/handle")
    public AjaxResult handleWarning(@RequestBody Map<String, Object> data)
    {
        // TODO: 处理预警
        logger.info("处理预警: {}", data);
        return success("预警处理成功");
    }
}
