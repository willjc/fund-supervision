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
 * 公告查询Controller
 *
 * @author ruoyi
 * @date 2025-01-03
 */
@RestController
@RequestMapping("/pension/announcement")
public class AnnouncementController extends BaseController
{
    /**
     * 查询公告列表
     */
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) String title,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize)
    {
        List<Map<String, Object>> list = new ArrayList<>();

        // TODO: 从数据库查询真实数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 1);
        item1.put("title", "关于加强养老机构安全管理的通知");
        item1.put("summary", "为进一步加强养老机构安全管理工作，保障老年人生命财产安全，现就有关事项通知如下...");
        item1.put("content", "<p>各养老机构：</p><p>为进一步加强养老机构安全管理工作，保障老年人生命财产安全，现就有关事项通知如下：</p><p>一、提高安全意识...</p><p>二、加强消防安全...</p><p>三、强化食品安全...</p>");
        item1.put("publishTime", "2025-01-03 10:00:00");
        item1.put("publisher", "民政监管部门");
        item1.put("isRead", false);
        item1.put("isImportant", true);

        List<Map<String, Object>> attachments1 = new ArrayList<>();
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("name", "安全管理规范.pdf");
        attachment.put("url", "/files/safety.pdf");
        attachments1.add(attachment);
        item1.put("attachments", attachments1);
        list.add(item1);

        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 2);
        item2.put("title", "2025年度养老机构年检工作安排");
        item2.put("summary", "根据相关规定，现将2025年度养老机构年检工作有关事项通知如下...");
        item2.put("content", "<p>各养老机构：</p><p>根据相关规定，现将2025年度养老机构年检工作有关事项通知如下：</p><p>一、年检时间：2025年3月1日至5月31日...</p>");
        item2.put("publishTime", "2025-01-02 09:00:00");
        item2.put("publisher", "民政监管部门");
        item2.put("isRead", true);
        item2.put("isImportant", false);
        item2.put("attachments", new ArrayList<>());
        list.add(item2);

        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setCode(200);
        dataInfo.setMsg("查询成功");
        dataInfo.setRows(list);
        dataInfo.setTotal(list.size());
        return dataInfo;
    }

    /**
     * 获取公告详情
     */
    @GetMapping("/detail/{id}")
    public AjaxResult getDetail(@PathVariable Long id)
    {
        // TODO: 从数据库查询真实数据
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("title", "关于加强养老机构安全管理的通知");
        data.put("content", "<p>各养老机构：</p><p>为进一步加强养老机构安全管理工作，保障老年人生命财产安全，现就有关事项通知如下：</p><p>一、提高安全意识...</p>");
        data.put("publishTime", "2025-01-03 10:00:00");
        data.put("publisher", "民政监管部门");

        List<Map<String, Object>> attachments = new ArrayList<>();
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("name", "安全管理规范.pdf");
        attachment.put("url", "/files/safety.pdf");
        attachments.add(attachment);
        data.put("attachments", attachments);

        return success(data);
    }

    /**
     * 标记公告为已读
     */
    @PostMapping("/mark-read/{id}")
    public AjaxResult markAsRead(@PathVariable Long id)
    {
        // TODO: 更新数据库状态
        logger.info("标记公告为已读: {}", id);
        return success("标记成功");
    }

    /**
     * 下载公告附件
     */
    @GetMapping("/download/{fileId}")
    public AjaxResult downloadAttachment(@PathVariable Long fileId)
    {
        // TODO: 实现文件下载
        logger.info("下载附件: {}", fileId);
        return success("下载功能开发中");
    }
}
