package com.ruoyi.service.impl;

import java.util.List;
import java.util.Date;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.mapper.ElderCheckInMapper;
import com.ruoyi.mapper.ElderInfoMapper;
import com.ruoyi.mapper.BedInfoMapper;
import com.ruoyi.domain.ElderCheckIn;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.BedInfo;
import com.ruoyi.service.IElderCheckInService;

/**
 * 老人入住申请Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@Service
public class ElderCheckInServiceImpl implements IElderCheckInService
{
    @Autowired
    private ElderCheckInMapper elderCheckInMapper;

    @Autowired
    private ElderInfoMapper elderInfoMapper;

    @Autowired
    private BedInfoMapper bedInfoMapper;

    /**
     * 查询老人入住申请
     *
     * @param checkInId 老人入住申请主键
     * @return 老人入住申请
     */
    @Override
    public ElderCheckIn selectElderCheckInByCheckInId(Long checkInId)
    {
        return elderCheckInMapper.selectElderCheckInByCheckInId(checkInId);
    }

    /**
     * 查询老人入住申请列表
     *
     * @param elderCheckIn 老人入住申请
     * @return 老人入住申请
     */
    @Override
    public List<ElderCheckIn> selectElderCheckInList(ElderCheckIn elderCheckIn)
    {
        return elderCheckInMapper.selectElderCheckInList(elderCheckIn);
    }

    /**
     * 新增老人入住申请
     *
     * @param elderCheckIn 老人入住申请
     * @return 结果
     */
    @Override
    @Transactional
    public int insertElderCheckIn(ElderCheckIn elderCheckIn)
    {
        elderCheckIn.setApplyDate(DateUtils.getNowDate());
        elderCheckIn.setCheckInStatus("0"); // 默认状态为申请中
        return elderCheckInMapper.insertElderCheckIn(elderCheckIn);
    }

    /**
     * 修改老人入住申请
     *
     * @param elderCheckIn 老人入住申请
     * @return 结果
     */
    @Override
    public int updateElderCheckIn(ElderCheckIn elderCheckIn)
    {
        elderCheckIn.setUpdateTime(DateUtils.getNowDate());
        return elderCheckInMapper.updateElderCheckIn(elderCheckIn);
    }

    /**
     * 批量删除老人入住申请
     *
     * @param checkInIds 需要删除的老人入住申请主键
     * @return 结果
     */
    @Override
    public int deleteElderCheckInByCheckInIds(Long[] checkInIds)
    {
        return elderCheckInMapper.deleteElderCheckInByCheckInIds(checkInIds);
    }

    /**
     * 删除老人入住申请信息
     *
     * @param checkInId 老人入住申请主键
     * @return 结果
     */
    @Override
    public int deleteElderCheckInByCheckInId(Long checkInId)
    {
        return elderCheckInMapper.deleteElderCheckInByCheckInId(checkInId);
    }

    /**
     * 审批入住申请
     *
     * @param checkInId 申请ID
     * @param checkInStatus 审批状态
     * @param approvalUser 审批人
     * @param approvalRemark 审批意见
     * @return 结果
     */
    @Override
    @Transactional
    public int approveCheckIn(Long checkInId, String checkInStatus, String approvalUser, String approvalRemark)
    {
        ElderCheckIn checkIn = elderCheckInMapper.selectElderCheckInByCheckInId(checkInId);
        if (checkIn == null) {
            return 0;
        }

        // 审批通过，更新老人状态和床位状态
        if ("1".equals(checkInStatus)) {
            // 更新老人状态为已入住
            ElderInfo elderInfo = new ElderInfo();
            elderInfo.setElderId(checkIn.getElderId());
            elderInfo.setStatus("1");
            elderInfo.setUpdateTime(DateUtils.getNowDate());
            elderInfoMapper.updateElderInfo(elderInfo);

            // 更新床位状态为占用
            if (checkIn.getBedId() != null) {
                bedInfoMapper.updateBedStatus(checkIn.getBedId(), "1");
            }

            // 设置实际入住日期
            checkIn.setActualCheckInDate(new Date());
        } else if ("2".equals(checkInStatus)) {
            // 审批拒绝，释放床位
            if (checkIn.getBedId() != null) {
                bedInfoMapper.updateBedStatus(checkIn.getBedId(), "0");
            }
        }

        // 更新入住申请状态
        return elderCheckInMapper.approveCheckIn(checkInId, checkInStatus, approvalUser, approvalRemark);
    }

    /**
     * 导出老人入住申请
     *
     * @param elderCheckIn 查询条件
     * @return 老人入住申请列表
     */
    @Override
    public List<ElderCheckIn> exportElderCheckIn(ElderCheckIn elderCheckIn)
    {
        return elderCheckInMapper.selectElderCheckInList(elderCheckIn);
    }
}