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
 * 民政监管-反馈管理Controller
 *
 * @author ruoyi
 * @date 2025-01-03
 */
@RestController
@RequestMapping("/supervision/feedback")
public class SupervisionFeedbackController extends BaseController
{
    /**
     * 反馈列表
     */
    @GetMapping("/list")
    public TableDataInfo getFeedbackList(
            @RequestParam(required = false) String feedbackType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String institutionName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询反馈列表
        String[] types = {"投诉举报", "意见建议", "政策咨询", "其他"};
        String[] statuses = {"待处理", "处理中", "已处理", "已关闭"};
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心"};

        for (int i = 0; i < 12; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("feedbackId", i + 1);
            item.put("feedbackNo", "FK20250103" + String.format("%04d", i + 1));
            item.put("feedbackType", types[random.nextInt(types.length)]);
            item.put("status", statuses[random.nextInt(statuses.length)]);
            item.put("title", "关于" + institutions[random.nextInt(institutions.length)] + "的反馈");
            item.put("submitterName", "用户" + (i + 1));
            item.put("submitterPhone", "138****" + String.format("%04d", 1000 + i));
            item.put("institutionName", institutions[random.nextInt(institutions.length)]);
            item.put("submitTime", "2025-01-0" + (random.nextInt(3) + 1) + " " + (8 + random.nextInt(10)) + ":" + (random.nextInt(60)) + ":00");
            item.put("priority", random.nextInt(3) + 1); // 1-3级优先级
            item.put("hasAttachment", random.nextBoolean());
            list.add(item);
        }

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 反馈详情
     */
    @GetMapping("/detail/{feedbackId}")
    public AjaxResult getFeedbackDetail(@PathVariable Long feedbackId)
    {
        // TODO: 查询反馈详细信息
        Map<String, Object> data = new HashMap<>();
        data.put("feedbackId", feedbackId);
        data.put("feedbackNo", "FK2025010001");
        data.put("feedbackType", "投诉举报");
        data.put("status", "处理中");
        data.put("title", "关于幸福养老院收费不透明的投诉");
        data.put("content", "我是幸福养老院的家属，发现养老院在收费方面不够透明，存在乱收费现象，希望相关部门能够调查处理。");
        data.put("submitterName", "张先生");
        data.put("submitterPhone", "13800138001");
        data.put("submitterEmail", "zhang@example.com");
        data.put("institutionName", "幸福养老院");
        data.put("submitTime", "2025-01-03 10:30:00");
        data.put("priority", 1);

        // 附件信息
        List<Map<String, Object>> attachments = new ArrayList<>();
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("fileName", "收费清单.jpg");
        attachment.put("fileUrl", "/uploads/2025/01/03/收费清单.jpg");
        attachment.put("fileSize", "1.2MB");
        attachments.add(attachment);
        data.put("attachments", attachments);

        // 处理记录
        List<Map<String, Object>> handleRecords = new ArrayList<>();
        Map<String, Object> record1 = new HashMap<>();
        record1.put("handleTime", "2025-01-03 11:00:00");
        record1.put("handler", "管理员1");
        record1.put("action", "受理投诉");
        record1.put("remark", "已受理，正在调查处理中");
        handleRecords.add(record1);

        Map<String, Object> record2 = new HashMap<>();
        record2.put("handleTime", "2025-01-03 14:30:00");
        record2.put("handler", "管理员2");
        record2.put("action", "联系机构");
        record2.put("remark", "已联系幸福养老院负责人，要求提供相关收费明细");
        handleRecords.add(record2);
        data.put("handleRecords", handleRecords);

        return success(data);
    }

    /**
     * 处理反馈
     */
    @PostMapping("/handle/{feedbackId}")
    public AjaxResult handleFeedback(@PathVariable Long feedbackId, @RequestBody Map<String, Object> data)
    {
        // TODO: 处理反馈
        logger.info("处理反馈 {}: {}", feedbackId, data);
        return success("处理成功");
    }

    /**
     * 转交反馈
     */
    @PostMapping("/transfer/{feedbackId}")
    public AjaxResult transferFeedback(@PathVariable Long feedbackId, @RequestBody Map<String, Object> data)
    {
        // TODO: 转交反馈
        logger.info("转交反馈 {}: {}", feedbackId, data);
        return success("转交成功");
    }

    /**
     * 关闭反馈
     */
    @PostMapping("/close/{feedbackId}")
    public AjaxResult closeFeedback(@PathVariable Long feedbackId, @RequestBody Map<String, Object> data)
    {
        // TODO: 关闭反馈
        logger.info("关闭反馈 {}: {}", feedbackId, data);
        return success("关闭成功");
    }

    /**
     * 反馈统计
     */
    @GetMapping("/statistics")
    public AjaxResult getFeedbackStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate)
    {
        // TODO: 查询反馈统计数据
        Map<String, Object> data = new HashMap<>();

        // 总体统计
        Map<String, Object> totalStats = new HashMap<>();
        totalStats.put("totalCount", 156);
        totalStats.put("pendingCount", 23);
        totalStats.put("processingCount", 45);
        totalStats.put("completedCount", 78);
        totalStats.put("closedCount", 10);
        data.put("totalStats", totalStats);

        // 按类型统计
        List<Map<String, Object>> typeStats = new ArrayList<>();
        String[] types = {"投诉举报", "意见建议", "政策咨询", "其他"};
        int[] counts = {67, 45, 32, 12};

        for (int i = 0; i < types.length; i++) {
            Map<String, Object> type = new HashMap<>();
            type.put("feedbackType", types[i]);
            type.put("count", counts[i]);
            type.put("percentage", counts[i] * 100.0 / 156);
            typeStats.add(type);
        }
        data.put("typeStats", typeStats);

        // 按机构统计
        List<Map<String, Object>> institutionStats = new ArrayList<>();
        String[] institutions = {"幸福养老院", "夕阳红公寓", "康乐养老中心"};
        int[] institutionCounts = {78, 56, 22};

        for (int i = 0; i < institutions.length; i++) {
            Map<String, Object> institution = new HashMap<>();
            institution.put("institutionName", institutions[i]);
            institution.put("count", institutionCounts[i]);
            institution.put("pendingCount", institutionCounts[i] / 3);
            institutionStats.add(institution);
        }
        data.put("institutionStats", institutionStats);

        // 按时间统计（最近7天）
        List<Map<String, Object>> timeStats = new ArrayList<>();
        Random random = new Random();
        for (int i = 6; i >= 0; i--) {
            Map<String, Object> time = new HashMap<>();
            time.put("date", "2025-01-" + String.format("%02d", 3 - i));
            time.put("count", 15 + random.nextInt(20));
            timeStats.add(time);
        }
        data.put("timeStats", timeStats);

        return success(data);
    }

    /**
     * 批量处理反馈
     */
    @PostMapping("/batch-handle")
    public AjaxResult batchHandleFeedback(@RequestBody Map<String, Object> data)
    {
        // TODO: 批量处理反馈
        logger.info("批量处理反馈: {}", data);
        return success("批量处理成功");
    }

    /**
     * 导出反馈数据
     */
    @PostMapping("/export")
    public AjaxResult exportFeedback(@RequestBody Map<String, Object> params)
    {
        // TODO: 导出反馈数据
        logger.info("导出反馈数据: {}", params);
        Map<String, Object> data = new HashMap<>();
        data.put("downloadUrl", "/downloads/feedback_export_" + System.currentTimeMillis() + ".xlsx");
        return AjaxResult.success("导出成功", data);
    }

    /**
     * 反馈回复列表
     */
    @GetMapping("/reply/list/{feedbackId}")
    public TableDataInfo getReplyList(
            @PathVariable Long feedbackId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();

        // TODO: 从数据库查询回复列表
        Map<String, Object> reply1 = new HashMap<>();
        reply1.put("replyId", 1);
        reply1.put("replyType", "用户回复");
        reply1.put("replierName", "张先生");
        reply1.put("content", "感谢处理，问题已得到解决。");
        reply1.put("replyTime", "2025-01-03 16:00:00");
        reply1.put("hasAttachment", false);
        list.add(reply1);

        Map<String, Object> reply2 = new HashMap<>();
        reply2.put("replyId", 2);
        reply2.put("replyType", "官方回复");
        reply2.put("replierName", "管理员1");
        reply2.put("content", "经过调查，养老院已整改收费透明度问题，我们将继续监督。");
        reply2.put("replyTime", "2025-01-03 15:30:00");
        reply2.put("hasAttachment", true);
        list.add(reply2);

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 添加回复
     */
    @PostMapping("/reply/add/{feedbackId}")
    public AjaxResult addReply(@PathVariable Long feedbackId, @RequestBody Map<String, Object> data)
    {
        // TODO: 添加回复
        logger.info("添加回复到反馈 {}: {}", feedbackId, data);
        return success("回复添加成功");
    }

    /**
     * 热点反馈
     */
    @GetMapping("/hot-feedback")
    public TableDataInfo getHotFeedback(
            @RequestParam(defaultValue = "5") Integer limit)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        Random random = new Random();

        // TODO: 从数据库查询热点反馈
        String[] titles = {
            "养老院收费不透明问题",
            "服务质量待提升",
            "床位紧张问题",
            "设施老化需要更新",
            "工作人员服务态度问题"
        };

        for (int i = 0; i < Math.min(titles.length, limit); i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("feedbackId", i + 1);
            item.put("title", titles[i]);
            item.put("feedbackCount", 20 + random.nextInt(30));
            item.put("lastUpdateTime", "2025-01-0" + (random.nextInt(3) + 1) + " " + (10 + random.nextInt(8)) + ":00:00");
            list.add(item);
        }

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 反馈满意度评价
     */
    @PostMapping("/satisfaction/{feedbackId}")
    public AjaxResult submitSatisfaction(@PathVariable Long feedbackId, @RequestBody Map<String, Object> data)
    {
        // TODO: 提交满意度评价
        logger.info("提交反馈 {} 满意度评价: {}", feedbackId, data);
        return success("评价提交成功");
    }
}