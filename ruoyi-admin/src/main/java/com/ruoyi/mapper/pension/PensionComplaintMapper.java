package com.ruoyi.mapper.pension;

import java.util.List;
import com.ruoyi.domain.pension.PensionComplaint;

/**
 * 投诉建议Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-26
 */
public interface PensionComplaintMapper
{
    /**
     * 查询投诉建议列表
     *
     * @param complaint 投诉建议
     * @return 投诉建议集合
     */
    public List<PensionComplaint> selectPensionComplaintList(PensionComplaint complaint);

    /**
     * 查询投诉建议详情（带关联信息）
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
     * 修改投诉建议
     *
     * @param complaint 投诉建议
     * @return 结果
     */
    public int updatePensionComplaint(PensionComplaint complaint);

    /**
     * 删除投诉建议
     *
     * @param complaintId 投诉ID
     * @return 结果
     */
    public int deletePensionComplaintById(Long complaintId);

    /**
     * 查询今日投诉数量（用于生成编号）
     *
     * @param datePrefix 日期前缀（如 TS20250126）
     * @return 数量
     */
    public int countTodayComplaint(String datePrefix);
}
