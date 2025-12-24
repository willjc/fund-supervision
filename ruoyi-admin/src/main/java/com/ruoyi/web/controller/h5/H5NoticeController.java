package com.ruoyi.web.controller.h5;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.ISysNoticeService;

/**
 * H5通知公告Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/notice")
public class H5NoticeController extends BaseController
{
    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 获取通知公告列表（H5端）
     * @return 通知公告列表
     */
    @GetMapping("/list")
    public AjaxResult list()
    {
        try
        {
            SysNotice query = new SysNotice();
            // 只查询正常状态的公告
            query.setStatus("0"); // 0-正常 1-关闭

            List<SysNotice> noticeList = noticeService.selectNoticeList(query);

            // 按创建时间倒序
            noticeList.sort((a, b) -> {
                if (a.getCreateTime() == null) return 1;
                if (b.getCreateTime() == null) return -1;
                return b.getCreateTime().compareTo(a.getCreateTime());
            });

            // 转换为前端期望的格式
            List<Map<String, Object>> result = new ArrayList<>();
            for (SysNotice notice : noticeList)
            {
                Map<String, Object> item = new HashMap<>();
                item.put("noticeId", notice.getNoticeId());
                item.put("noticeTitle", notice.getNoticeTitle());
                item.put("noticeType", notice.getNoticeType());
                item.put("noticeContent", notice.getNoticeContent());
                item.put("createTime", notice.getCreateTime());
                // H5端默认所有通知为已读（后续可以扩展已读未读功能）
                item.put("isRead", true);
                result.add(item);
            }

            return success(result);
        }
        catch (Exception e)
        {
            logger.error("获取通知公告列表失败", e);
            return error("获取通知公告列表失败");
        }
    }

    /**
     * 获取通知公告详情（H5端）
     * @param noticeId 公告ID
     * @return 公告详情
     */
    @GetMapping("/{noticeId}")
    public AjaxResult getInfo(@PathVariable Long noticeId)
    {
        try
        {
            SysNotice notice = noticeService.selectNoticeById(noticeId);
            if (notice == null)
            {
                return error("通知公告不存在");
            }

            // 只返回正常状态的公告
            if ("1".equals(notice.getStatus()))
            {
                return error("通知公告已关闭");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("noticeId", notice.getNoticeId());
            result.put("noticeTitle", notice.getNoticeTitle());
            result.put("noticeType", notice.getNoticeType());
            result.put("noticeContent", notice.getNoticeContent());
            result.put("createTime", notice.getCreateTime());

            return success(result);
        }
        catch (Exception e)
        {
            logger.error("获取通知公告详情失败", e);
            return error("获取通知公告详情失败");
        }
    }
}
