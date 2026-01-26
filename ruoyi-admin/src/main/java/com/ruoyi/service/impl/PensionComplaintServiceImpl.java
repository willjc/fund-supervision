package com.ruoyi.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.domain.pension.PensionComplaint;
import com.ruoyi.mapper.pension.PensionComplaintMapper;
import com.ruoyi.service.IPensionComplaintService;

/**
 * 投诉建议Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-26
 */
@Service
public class PensionComplaintServiceImpl implements IPensionComplaintService
{
    @Autowired
    private PensionComplaintMapper pensionComplaintMapper;

    /**
     * 查询投诉建议列表
     *
     * @param complaint 投诉建议
     * @return 投诉建议
     */
    @Override
    public List<PensionComplaint> selectPensionComplaintList(PensionComplaint complaint)
    {
        return pensionComplaintMapper.selectPensionComplaintList(complaint);
    }

    /**
     * 查询投诉建议详情
     *
     * @param complaintId 投诉ID
     * @return 投诉建议
     */
    @Override
    public PensionComplaint selectPensionComplaintById(Long complaintId)
    {
        return pensionComplaintMapper.selectPensionComplaintById(complaintId);
    }

    /**
     * 根据投诉编号查询
     *
     * @param complaintNo 投诉编号
     * @return 投诉建议
     */
    @Override
    public PensionComplaint selectPensionComplaintByNo(String complaintNo)
    {
        return pensionComplaintMapper.selectPensionComplaintByNo(complaintNo);
    }

    /**
     * 新增投诉建议
     *
     * @param complaint 投诉建议
     * @return 结果
     */
    @Override
    @Transactional
    public int insertPensionComplaint(PensionComplaint complaint)
    {
        // 生成投诉编号
        if (complaint.getComplaintNo() == null || complaint.getComplaintNo().isEmpty())
        {
            complaint.setComplaintNo(generateComplaintNo());
        }
        // 默认状态为处理中
        if (complaint.getStatus() == null || complaint.getStatus().isEmpty())
        {
            complaint.setStatus(PensionComplaint.STATUS_PROCESSING);
        }
        return pensionComplaintMapper.insertPensionComplaint(complaint);
    }

    /**
     * 处理投诉建议
     *
     * @param complaintId 投诉ID
     * @param status 状态
     * @param replyContent 回复内容
     * @param handleUserId 处理人ID
     * @param handleUserName 处理人姓名
     * @return 结果
     */
    @Override
    @Transactional
    public int handleComplaint(Long complaintId, String status, String replyContent, Long handleUserId, String handleUserName)
    {
        PensionComplaint complaint = new PensionComplaint();
        complaint.setComplaintId(complaintId);
        complaint.setStatus(status);
        complaint.setReplyContent(replyContent);
        complaint.setHandleUserId(handleUserId);
        complaint.setHandleUserName(handleUserName);
        complaint.setHandleTime(new Date());
        return pensionComplaintMapper.updatePensionComplaint(complaint);
    }

    /**
     * 生成投诉编号
     * 格式: TS + 年月日(8位) + 序号(4位)
     *
     * @return 投诉编号
     */
    @Override
    public String generateComplaintNo()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String prefix = "TS" + dateStr;

        // 查询今日已有投诉数量
        int count = pensionComplaintMapper.countTodayComplaint(prefix);
        int seq = count + 1;

        // 生成4位序号
        String seqStr = String.format("%04d", seq);
        return prefix + seqStr;
    }
}
