package com.ruoyi.web.controller.pension;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.domain.pension.PensionComplaint;
import com.ruoyi.service.IPensionComplaintService;

/**
 * 投诉建议管理Controller
 * 用于管理端处理H5端用户提交的投诉
 *
 * @author ruoyi
 * @date 2025-01-26
 */
@RestController
@RequestMapping("/pension/feedback")
public class FeedbackController extends BaseController
{
    @Autowired
    private IPensionComplaintService complaintService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询投诉建议列表
     */
    @PreAuthorize("@ss.hasPermi('pension:feedback:list')")
    @GetMapping("/list")
    public TableDataInfo list(PensionComplaint complaint)
    {
        // 数据权限过滤：获取当前用户关联的机构ID列表
        List<Long> institutionIds = getUserInstitutionIds(jdbcTemplate);
        if (institutionIds != null && !institutionIds.isEmpty())
        {
            complaint.setInstitutionIds(institutionIds);
        }

        startPage();
        List<PensionComplaint> list = complaintService.selectPensionComplaintList(complaint);
        List<Map<String, Object>> resultList = list.stream()
                .map(this::convertToListFormat)
                .collect(Collectors.toList());

        TableDataInfo dataTable = getDataTable(list);
        dataTable.setRows(resultList);
        return dataTable;
    }

    /**
     * 查询投诉详情
     */
    @PreAuthorize("@ss.hasPermi('pension:feedback:query')")
    @GetMapping("/detail/{complaintId}")
    public AjaxResult getDetail(@PathVariable Long complaintId)
    {
        PensionComplaint complaint = complaintService.selectPensionComplaintById(complaintId);
        if (complaint == null)
        {
            return AjaxResult.error("投诉不存在");
        }
        return AjaxResult.success(convertToDetailFormat(complaint));
    }

    /**
     * 处理投诉
     */
    @PreAuthorize("@ss.hasPermi('pension:feedback:handle')")
    @Log(title = "处理投诉", businessType = BusinessType.UPDATE)
    @PostMapping("/handle")
    public AjaxResult handleComplaint(@RequestBody Map<String, Object> params)
    {
        Long complaintId = params.get("complaintId") != null ?
                Long.parseLong(params.get("complaintId").toString()) : null;
        String status = (String) params.get("status");
        String replyContent = (String) params.get("replyContent");

        if (complaintId == null)
        {
            return AjaxResult.error("投诉ID不能为空");
        }
        if (status == null || status.isEmpty())
        {
            return AjaxResult.error("请选择处理状态");
        }
        if (replyContent == null || replyContent.trim().isEmpty())
        {
            return AjaxResult.error("请填写回复内容");
        }

        // 验证投诉是否存在
        PensionComplaint existing = complaintService.selectPensionComplaintById(complaintId);
        if (existing == null)
        {
            return AjaxResult.error("投诉不存在");
        }

        // 获取当前处理人信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        Long handleUserId = user.getUserId();
        String handleUserName = user.getNickName();

        int result = complaintService.handleComplaint(complaintId, status, replyContent, handleUserId, handleUserName);
        if (result > 0)
        {
            return AjaxResult.success("处理成功");
        }
        return AjaxResult.error("处理失败");
    }

    /**
     * 获取投诉统计信息
     */
    @PreAuthorize("@ss.hasPermi('pension:feedback:list')")
    @GetMapping("/statistics")
    public AjaxResult getStatistics()
    {
        PensionComplaint query = new PensionComplaint();

        // 数据权限过滤：获取当前用户关联的机构ID列表
        List<Long> institutionIds = getUserInstitutionIds(jdbcTemplate);
        if (institutionIds != null && !institutionIds.isEmpty())
        {
            query.setInstitutionIds(institutionIds);
        }

        // 总数
        List<PensionComplaint> allList = complaintService.selectPensionComplaintList(query);

        // 处理中
        query.setStatus(PensionComplaint.STATUS_PROCESSING);
        List<PensionComplaint> processingList = complaintService.selectPensionComplaintList(query);

        // 已处理
        query.setStatus(PensionComplaint.STATUS_PROCESSED);
        List<PensionComplaint> processedList = complaintService.selectPensionComplaintList(query);

        // 已拒绝
        query.setStatus(PensionComplaint.STATUS_REJECTED);
        List<PensionComplaint> rejectedList = complaintService.selectPensionComplaintList(query);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", allList.size());
        stats.put("processing", processingList.size());
        stats.put("processed", processedList.size());
        stats.put("rejected", rejectedList.size());

        return AjaxResult.success(stats);
    }

    /**
     * 转换为列表格式
     */
    private Map<String, Object> convertToListFormat(PensionComplaint complaint)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("complaintId", complaint.getComplaintId());
        result.put("complaintNo", complaint.getComplaintNo());
        result.put("institutionName", complaint.getInstitutionName());
        result.put("title", complaint.getTitle());
        result.put("complaintType", complaint.getComplaintType());
        result.put("submitter", complaint.getContactName());
        result.put("contact", complaint.getContactPhone());
        // 格式化提交时间
        result.put("submitTime", complaint.getCreateTime() != null
            ? DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, complaint.getCreateTime())
            : "");
        result.put("status", complaint.getStatus());
        result.put("statusText", complaint.getStatusText());
        result.put("hasImages", complaint.getImages() != null && !complaint.getImages().isEmpty());
        return result;
    }

    /**
     * 转换为详情格式
     */
    private Map<String, Object> convertToDetailFormat(PensionComplaint complaint)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("complaintId", complaint.getComplaintId());
        result.put("complaintNo", complaint.getComplaintNo());
        result.put("institutionId", complaint.getInstitutionId());
        result.put("institutionName", complaint.getInstitutionName());
        result.put("title", complaint.getTitle());
        result.put("complaintType", complaint.getComplaintType());
        result.put("content", complaint.getContent());

        // 图片数组
        result.put("images", complaint.getImageArray());

        result.put("submitter", complaint.getContactName());
        result.put("contact", complaint.getContactPhone());
        // 格式化提交时间
        result.put("submitTime", complaint.getCreateTime() != null
            ? com.ruoyi.common.utils.DateUtils.parseDateToStr(com.ruoyi.common.utils.DateUtils.YYYY_MM_DD_HH_MM_SS, complaint.getCreateTime())
            : "");
        result.put("status", complaint.getStatus());
        result.put("statusText", complaint.getStatusText());
        result.put("replyContent", complaint.getReplyContent());
        result.put("handleUserName", complaint.getHandleUserName());
        // 格式化处理时间
        result.put("handleTime", complaint.getHandleTime() != null
            ? com.ruoyi.common.utils.DateUtils.parseDateToStr(com.ruoyi.common.utils.DateUtils.YYYY_MM_DD_HH_MM_SS, complaint.getHandleTime())
            : "");
        return result;
    }
}
