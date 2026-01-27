package com.ruoyi.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.ReleaseSupervisionMapper;
import com.ruoyi.domain.ReleaseSupervision;
import com.ruoyi.service.IReleaseSupervisionService;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 机构解除监管申请Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-04
 */
@Service
public class ReleaseSupervisionServiceImpl implements IReleaseSupervisionService
{
    @Autowired
    private ReleaseSupervisionMapper releaseSupervisionMapper;

    /**
     * 查询机构解除监管申请
     *
     * @param releaseId 机构解除监管申请主键
     * @return 机构解除监管申请
     */
    @Override
    public ReleaseSupervision selectReleaseSupervisionByReleaseId(Long releaseId)
    {
        return releaseSupervisionMapper.selectReleaseSupervisionByReleaseId(releaseId);
    }

    /**
     * 查询机构解除监管申请列表
     *
     * @param releaseSupervision 机构解除监管申请
     * @return 机构解除监管申请
     */
    @Override
    public List<ReleaseSupervision> selectReleaseSupervisionList(ReleaseSupervision releaseSupervision)
    {
        return releaseSupervisionMapper.selectReleaseSupervisionList(releaseSupervision);
    }

    /**
     * 新增机构解除监管申请
     *
     * @param releaseSupervision 机构解除监管申请
     * @return 结果
     */
    @Override
    public int insertReleaseSupervision(ReleaseSupervision releaseSupervision)
    {
        releaseSupervision.setCreateTime(new Date());
        return releaseSupervisionMapper.insertReleaseSupervision(releaseSupervision);
    }

    /**
     * 修改机构解除监管申请
     *
     * @param releaseSupervision 机构解除监管申请
     * @return 结果
     */
    @Override
    public int updateReleaseSupervision(ReleaseSupervision releaseSupervision)
    {
        releaseSupervision.setUpdateTime(new Date());
        return releaseSupervisionMapper.updateReleaseSupervision(releaseSupervision);
    }

    /**
     * 批量删除机构解除监管申请
     *
     * @param releaseIds 需要删除的机构解除监管申请主键
     * @return 结果
     */
    @Override
    public int deleteReleaseSupervisionByReleaseIds(Long[] releaseIds)
    {
        return releaseSupervisionMapper.deleteReleaseSupervisionByReleaseIds(releaseIds);
    }

    /**
     * 删除机构解除监管申请信息
     *
     * @param releaseId 机构解除监管申请主键
     * @return 结果
     */
    @Override
    public int deleteReleaseSupervisionByReleaseId(Long releaseId)
    {
        return releaseSupervisionMapper.deleteReleaseSupervisionByReleaseId(releaseId);
    }

    /**
     * 批准解除监管
     *
     * @param releaseId 申请ID
     * @param releaseSupervision 审批信息
     * @return 结果
     */
    @Override
    public int approveRelease(Long releaseId, ReleaseSupervision releaseSupervision)
    {
        ReleaseSupervision update = new ReleaseSupervision();
        update.setReleaseId(releaseId);
        update.setApplyStatus("1"); // 已批准
        update.setApprover(SecurityUtils.getUsername());
        update.setApproveTime(new Date());
        update.setApproveRemark(releaseSupervision.getApproveRemark());
        update.setUpdateTime(new Date());

        // TODO: 这里应该调用银行接口通知资金划拨
        // bankService.notifyTransferFunds(releaseId);

        return releaseSupervisionMapper.updateReleaseSupervision(update);
    }

    /**
     * 驳回解除监管申请
     *
     * @param releaseId 申请ID
     * @param releaseSupervision 驳回信息
     * @return 结果
     */
    @Override
    public int rejectRelease(Long releaseId, ReleaseSupervision releaseSupervision)
    {
        ReleaseSupervision update = new ReleaseSupervision();
        update.setReleaseId(releaseId);
        update.setApplyStatus("2"); // 已驳回
        update.setApprover(SecurityUtils.getUsername());
        update.setApproveTime(new Date());
        update.setRejectReason(releaseSupervision.getRejectReason());
        update.setUpdateTime(new Date());

        return releaseSupervisionMapper.updateReleaseSupervision(update);
    }

    /**
     * 获取统计数据
     *
     * @return 统计数据
     */
    @Override
    public Map<String, Object> getReleaseStatistics()
    {
        return releaseSupervisionMapper.selectReleaseStatistics();
    }
}
