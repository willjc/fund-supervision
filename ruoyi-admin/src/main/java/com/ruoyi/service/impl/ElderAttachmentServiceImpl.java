package com.ruoyi.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.ElderAttachmentMapper;
import com.ruoyi.domain.ElderAttachment;
import com.ruoyi.service.IElderAttachmentService;

/**
 * 老人附件Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-02
 */
@Service
public class ElderAttachmentServiceImpl implements IElderAttachmentService
{
    @Autowired
    private ElderAttachmentMapper elderAttachmentMapper;

    /**
     * 查询老人附件
     *
     * @param attachmentId 老人附件主键
     * @return 老人附件
     */
    @Override
    public ElderAttachment selectElderAttachmentByAttachmentId(Long attachmentId)
    {
        return elderAttachmentMapper.selectElderAttachmentByAttachmentId(attachmentId);
    }

    /**
     * 查询老人附件列表
     *
     * @param elderAttachment 老人附件
     * @return 老人附件
     */
    @Override
    public List<ElderAttachment> selectElderAttachmentList(ElderAttachment elderAttachment)
    {
        return elderAttachmentMapper.selectElderAttachmentList(elderAttachment);
    }

    /**
     * 根据老人ID查询附件列表
     *
     * @param elderId 老人ID
     * @return 附件列表
     */
    @Override
    public List<ElderAttachment> selectAttachmentsByElderId(Long elderId)
    {
        return elderAttachmentMapper.selectAttachmentsByElderId(elderId);
    }

    /**
     * 根据老人ID和附件类型查询附件
     *
     * @param elderId 老人ID
     * @param attachmentType 附件类型
     * @return 附件信息
     */
    @Override
    public ElderAttachment selectAttachmentByElderIdAndType(Long elderId, String attachmentType)
    {
        return elderAttachmentMapper.selectAttachmentByElderIdAndType(elderId, attachmentType);
    }

    /**
     * 新增老人附件
     *
     * @param elderAttachment 老人附件
     * @return 结果
     */
    @Override
    public int insertElderAttachment(ElderAttachment elderAttachment)
    {
        elderAttachment.setCreateTime(DateUtils.getNowDate());
        return elderAttachmentMapper.insertElderAttachment(elderAttachment);
    }

    /**
     * 修改老人附件
     *
     * @param elderAttachment 老人附件
     * @return 结果
     */
    @Override
    public int updateElderAttachment(ElderAttachment elderAttachment)
    {
        return elderAttachmentMapper.updateElderAttachment(elderAttachment);
    }

    /**
     * 批量删除老人附件
     *
     * @param attachmentIds 需要删除的老人附件主键
     * @return 结果
     */
    @Override
    public int deleteElderAttachmentByAttachmentIds(Long[] attachmentIds)
    {
        return elderAttachmentMapper.deleteElderAttachmentByAttachmentIds(attachmentIds);
    }

    /**
     * 删除老人附件信息
     *
     * @param attachmentId 老人附件主键
     * @return 结果
     */
    @Override
    public int deleteElderAttachmentByAttachmentId(Long attachmentId)
    {
        return elderAttachmentMapper.deleteElderAttachmentByAttachmentId(attachmentId);
    }

    /**
     * 根据老人ID删除附件
     *
     * @param elderId 老人ID
     * @return 结果
     */
    @Override
    public int deleteAttachmentsByElderId(Long elderId)
    {
        return elderAttachmentMapper.deleteAttachmentsByElderId(elderId);
    }
}