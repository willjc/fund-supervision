package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.domain.ElderCheckIn;
import org.apache.ibatis.annotations.Param;

/**
 * 老人入住申请Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public interface ElderCheckInMapper
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
     * 审批入住申请
     *
     * @param checkInId 申请ID
     * @param checkInStatus 审批状态
     * @param approvalUser 审批人
     * @param approvalRemark 审批意见
     * @return 结果
     */
    public int approveCheckIn(@Param("checkInId") Long checkInId, @Param("checkInStatus") String checkInStatus, @Param("approvalUser") String approvalUser, @Param("approvalRemark") String approvalRemark);

    /**
     * 删除老人入住申请
     *
     * @param checkInId 老人入住申请主键
     * @return 结果
     */
    public int deleteElderCheckInByCheckInId(Long checkInId);

    /**
     * 批量删除老人入住申请
     *
     * @param checkInIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteElderCheckInByCheckInIds(Long[] checkInIds);

    /**
     * 根据老人ID查询入住申请
     *
     * @param elderId 老人ID
     * @return 入住申请列表
     */
    public List<ElderCheckIn> selectCheckInByElderId(Long elderId);
}