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
 * 投诉建议管理Controller
 *
 * @author ruoyi
 * @date 2025-01-03
 */
@RestController
@RequestMapping("/pension/feedback")
public class FeedbackController extends BaseController
{
    /**
     * 查询投诉建议列表
     */
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String type,
                               @RequestParam(required = false) String status,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();

        // TODO: 从数据库查询真实数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("feedbackNo", "FB202501001");
        item1.put("type", "投诉");
        item1.put("title", "服务态度问题");
        item1.put("content", "护理员服务态度不好，希望改进");
        item1.put("submitter", "张三");
        item1.put("contact", "13800138000");
        item1.put("submitTime", "2025-01-03 10:30:00");
        item1.put("status", "已完成");
        item1.put("handleComment", "已对相关人员进行培训教育");
        item1.put("handleTime", "2025-01-04 14:00:00");
        list.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("feedbackNo", "FB202501002");
        item2.put("type", "建议");
        item2.put("title", "增加文娱活动");
        item2.put("content", "建议增加老年人的文娱活动项目");
        item2.put("submitter", "李四");
        item2.put("contact", "13900139000");
        item2.put("submitTime", "2025-01-02 09:15:00");
        item2.put("status", "处理中");
        item2.put("handleComment", null);
        item2.put("handleTime", null);
        list.add(item2);

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setCode(200);
        dataInfo.setMsg("查询成功");
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 提交投诉建议
     */
    @PostMapping("/submit")
    public AjaxResult submitFeedback(@RequestBody Map<String, Object> params)
    {
        // TODO: 保存到数据库
        logger.info("提交投诉建议: {}", params);
        return success("提交成功");
    }

    /**
     * 查询反馈详情
     */
    @GetMapping("/detail/{feedbackNo}")
    public AjaxResult getDetail(@PathVariable String feedbackNo)
    {
        // TODO: 从数据库查询真实数据
        Map<String, Object> data = new HashMap<>();
        data.put("feedbackNo", feedbackNo);
        data.put("type", "投诉");
        data.put("title", "服务态度问题");
        data.put("content", "护理员服务态度不好，希望改进");
        data.put("submitter", "张三");
        data.put("contact", "13800138000");
        data.put("submitTime", "2025-01-03 10:30:00");
        data.put("status", "已完成");
        data.put("handleComment", "已对相关人员进行培训教育");
        data.put("handleTime", "2025-01-04 14:00:00");
        return success(data);
    }

    /**
     * 上传附件
     */
    @PostMapping("/upload-attachment")
    public AjaxResult uploadAttachment(@RequestParam("file") String fileUrl)
    {
        // TODO: 处理文件上传
        Map<String, Object> data = new HashMap<>();
        data.put("url", fileUrl);
        data.put("name", "附件文件");
        return success(data);
    }
}
