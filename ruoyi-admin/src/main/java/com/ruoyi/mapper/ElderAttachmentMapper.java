package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.domain.ElderAttachment;
import org.apache.ibatis.annotations.Param;

/**
 * 老人附件Mapper接口
 *
 * @author ruoyi
 * @date 2025-12-02
 */
public interface ElderAttachmentMapper
{
    /**
     * 查询老人附件
     *
     * @param attachmentId 老人附件主键
     * @return 老人附件
     */
    public ElderAttachment selectElderAttachmentByAttachmentId(Long attachmentId);

    /**
     * 查询老人附件列表
     *
     * @param elderAttachment 老人附件
     * @return 老人附件集合
     */
    public List<ElderAttachment> selectElderAttachmentList(ElderAttachment elderAttachment);

    /**
     * 根据老人ID查询附件列表
     *
     * @param elderId 老人ID
     * @return 附件列表
     */
    public List<ElderAttachment> selectAttachmentsByElderId(Long elderId);

    /**
     * 根据老人ID和附件类型查询附件
     *
     * @param elderId 老人ID
     * @param attachmentType 附件类型
     * @return 附件信息
     */
    public ElderAttachment selectAttachmentByElderIdAndType(@Param("elderId") Long elderId, @Param("attachmentType") String attachmentType);

    /**
     * 新增老人附件
     *
     * @param elderAttachment 老人附件
     * @return 结果
     */
    public int insertElderAttachment(ElderAttachment elderAttachment);

    /**
     * 修改老人附件
     *
     * @param elderAttachment 老人附件
     * @return 结果
     */
    public int updateElderAttachment(ElderAttachment elderAttachment);

    /**
     * 删除老人附件
     *
     * @param attachmentId 老人附件主键
     * @return 结果
     */
    public int deleteElderAttachmentByAttachmentId(Long attachmentId);

    /**
     * 批量删除老人附件
     *
     * @param attachmentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteElderAttachmentByAttachmentIds(Long[] attachmentIds);

    /**
     * 根据老人ID删除附件
     *
     * @param elderId 老人ID
     * @return 结果
     */
    public int deleteAttachmentsByElderId(Long elderId);
}