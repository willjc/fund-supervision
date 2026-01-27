package com.ruoyi.service;

import java.util.List;
import com.ruoyi.domain.pension.PensionComplaint;

/**
 * 投诉建议Service接口
 *
 * @author ruoyi
 * @date 2025-01-26
 */
public interface IPensionComplaintService
{
    /**
     * 查询投诉建议列表
     *
     * @param complaint 投诉建议
     * @return 投诉建议集合
     */
    public List<PensionComplaint> selectPensionComplaintList(PensionComplaint complaint);

    /**
     * 查询投诉建议详情
     *
     * @param complaintId 投诉ID
     * @return 投诉建议
     */
    public PensionComplaint selectPensionComplaintById(Long complaintId);

    /**
     * 根据投诉编号查询
     *
     * @param complaintNo 投诉编号
     * @return 投诉建议
     */
    public PensionComplaint selectPensionComplaintByNo(String complaintNo);

    /**
     * 新增投诉建议
     *
     * @param complaint 投诉建议
     * @return 结果
     */
    public int insertPensionComplaint(PensionComplaint complaint);

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
    public int handleComplaint(Long complaintId, String status, String replyContent, Long handleUserId, String handleUserName);

    /**
     * 生成投诉编号
     * 格式: TS + 年月日(8位) + 序号(4位)
     *
     * @return 投诉编号
     */
    public String generateComplaintNo();
}
