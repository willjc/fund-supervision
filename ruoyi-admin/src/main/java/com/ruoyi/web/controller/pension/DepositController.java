package com.ruoyi.web.controller.pension;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 押金管理Controller
 *
 * @author ruoyi
 * @date 2025-01-03
 */
@RestController
@RequestMapping("/pension/deposit")
public class DepositController extends BaseController
{
    /**
     * 获取押金余额
     */
    @GetMapping("/balance")
    public AjaxResult getDepositBalance()
    {
        Map<String, Object> data = new HashMap<>();
        // TODO: 从数据库查询真实数据
        data.put("depositBalance", 456789.00);
        return success(data);
    }

    /**
     * 查询押金申请列表
     */
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String applyType,
                               @RequestParam(required = false) String status,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();

        // TODO: 从数据库查询真实数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("applyNo", "YJ202501001");
        item1.put("applyType", "设备维修");
        item1.put("amount", 50000);
        item1.put("reason", "中央空调系统故障，需要紧急维修");
        item1.put("purpose", "支付维修费用及更换配件");
        item1.put("createTime", "2025-01-03 10:30:00");
        item1.put("status", "待审批");
        item1.put("approvalComment", null);
        item1.put("approvalTime", null);
        list.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("applyNo", "YJ202501002");
        item2.put("applyType", "设施改造");
        item2.put("amount", 80000);
        item2.put("reason", "无障碍设施升级改造");
        item2.put("purpose", "增设无障碍扶手、坡道等设施");
        item2.put("createTime", "2025-01-02 14:20:00");
        item2.put("status", "已通过");
        item2.put("approvalComment", "同意使用押金进行设施改造");
        item2.put("approvalTime", "2025-01-03 09:00:00");
        list.add(item2);

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setCode(200);
        dataInfo.setMsg("查询成功");
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 提交押金使用申请
     */
    @PostMapping("/apply")
    public AjaxResult applyDeposit(@RequestBody Map<String, Object> params)
    {
        // TODO: 保存到数据库
        logger.info("提交押金使用申请: {}", params);
        return success("申请提交成功，等待审批");
    }

    /**
     * 撤销押金申请
     */
    @DeleteMapping("/cancel/{applyNo}")
    public AjaxResult cancelApply(@PathVariable String applyNo)
    {
        // TODO: 更新数据库状态
        logger.info("撤销押金申请: {}", applyNo);
        return success("撤销成功");
    }

    /**
     * 查询申请详情
     */
    @GetMapping("/detail/{applyNo}")
    public AjaxResult getDetail(@PathVariable String applyNo)
    {
        // TODO: 从数据库查询真实数据
        Map<String, Object> data = new HashMap<>();
        data.put("applyNo", applyNo);
        data.put("applyType", "设备维修");
        data.put("amount", 50000);
        data.put("reason", "中央空调系统故障，需要紧急维修");
        data.put("purpose", "支付维修费用及更换配件");
        data.put("createTime", "2025-01-03 10:30:00");
        data.put("status", "待审批");
        data.put("approvalComment", null);
        data.put("approvalTime", null);
        return success(data);
    }
}
