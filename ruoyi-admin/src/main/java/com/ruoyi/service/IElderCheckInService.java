package com.ruoyi.service;

import java.util.List;
import com.ruoyi.domain.ElderCheckIn;

/**
 * 老人入住申请Service接口
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public interface IElderCheckInService
{
    /**
     * 查询老人入住申请
     *
     * @param checkInId 老人入住申请主键
     * @return 老人入住申请
     */
    public ElderCheckIn selectElderCheckInByCheckInId(Long checkInId);

    /**
     * 查询老人入住申请列表
     *
     * @param elderCheckIn 老人入住申请
     * @return 老人入住申请集合
     */
    public List<ElderCheckIn> selectElderCheckInList(ElderCheckIn elderCheckIn);

    /**
     * 新增老人入住申请
     *
     * @param elderCheckIn 老人入住申请
     * @return 结果
     */
    public int insertElderCheckIn(ElderCheckIn elderCheckIn);

    /**
     * 修改老人入住申请
     *
     * @param elderCheckIn 老人入住申请
     * @return 结果
     */
    public int updateElderCheckIn(ElderCheckIn elderCheckIn);

    /**
     * 批量删除老人入住申请
     *
     * @param checkInIds 需要删除的老人入住申请主键集合
     * @return 结果
     */
    public int deleteElderCheckInByCheckInIds(Long[] checkInIds);

    /**
     * 删除老人入住申请信息
     *
     * @param checkInId 老人入住申请主键
     * @return 结果
     */
    public int deleteElderCheckInByCheckInId(Long checkInId);

    /**
     * 审批入住申请
     *
     * @param checkInId 申请ID
     * @param checkInStatus 审批状态
     * @param approvalUser 审批人
     * @param approvalRemark 审批意见
     * @return 结果
     */
    public int approveCheckIn(Long checkInId, String checkInStatus, String approvalUser, String approvalRemark);

    /**
     * 导出老人入住申请
     *
     * @param elderCheckIn 查询条件
     * @return 老人入住申请列表
     */
    public List<ElderCheckIn> exportElderCheckIn(ElderCheckIn elderCheckIn);
}