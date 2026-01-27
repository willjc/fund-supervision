package com.ruoyi.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import com.ruoyi.mapper.ElderInfoMapper;
import com.ruoyi.mapper.ElderAttachmentMapper;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.ElderAttachment;
import com.ruoyi.service.IElderInfoService;

/**
 * 老人基础信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@Service
public class ElderInfoServiceImpl implements IElderInfoService
{
    @Autowired
    private ElderInfoMapper elderInfoMapper;

    @Autowired
    private ElderAttachmentMapper elderAttachmentMapper;

    /**
     * 查询老人基础信息
     *
     * @param elderId 老人基础信息主键
     * @return 老人基础信息
     */
    @Override
    public ElderInfo selectElderInfoByElderId(Long elderId)
    {
        ElderInfo elderInfo = elderInfoMapper.selectElderInfoByElderId(elderId);
        if (elderInfo != null) {
            // 查询身份证正面照片（attachment_type = '1'）
            ElderAttachment frontAttachment = elderAttachmentMapper.selectAttachmentByElderIdAndType(elderId, "1");
            if (frontAttachment != null && frontAttachment.getFilePath() != null) {
                elderInfo.setIdCardFrontPath(frontAttachment.getFilePath());
            }

            // 查询身份证反面照片（attachment_type = '2'）
            ElderAttachment backAttachment = elderAttachmentMapper.selectAttachmentByElderIdAndType(elderId, "2");
            if (backAttachment != null && backAttachment.getFilePath() != null) {
                elderInfo.setIdCardBackPath(backAttachment.getFilePath());
            }
        }
        return elderInfo;
    }

    /**
     * 查询老人基础信息列表
     *
     * @param elderInfo 老人基础信息
     * @return 老人基础信息
     */
    @Override
    public List<ElderInfo> selectElderInfoList(ElderInfo elderInfo)
    {
        return elderInfoMapper.selectElderInfoList(elderInfo);
    }

    /**
     * 新增老人基础信息
     *
     * @param elderInfo 老人基础信息
     * @return 结果
     */
    @Override
    public int insertElderInfo(ElderInfo elderInfo)
    {
        elderInfo.setCreateTime(DateUtils.getNowDate());
        elderInfo.setStatus("0"); // 默认状态为未入住
        // 如果密码为空，设置默认密码：123456（MD5加密），用于老人登录H5端
        if (StringUtils.isEmpty(elderInfo.getPassword())) {
            elderInfo.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        }
        return elderInfoMapper.insertElderInfo(elderInfo);
    }

    /**
     * 修改老人基础信息
     *
     * @param elderInfo 老人基础信息
     * @return 结果
     */
    @Override
    public int updateElderInfo(ElderInfo elderInfo)
    {
        elderInfo.setUpdateTime(DateUtils.getNowDate());
        return elderInfoMapper.updateElderInfo(elderInfo);
    }

    /**
     * 批量删除老人基础信息
     *
     * @param elderIds 需要删除的老人基础信息主键
     * @return 结果
     */
    @Override
    public int deleteElderInfoByElderIds(Long[] elderIds)
    {
        return elderInfoMapper.deleteElderInfoByElderIds(elderIds);
    }

    /**
     * 删除老人基础信息信息
     *
     * @param elderId 老人基础信息主键
     * @return 结果
     */
    @Override
    public int deleteElderInfoByElderId(Long elderId)
    {
        return elderInfoMapper.deleteElderInfoByElderId(elderId);
    }

    /**
     * 根据身份证号查询老人信息
     *
     * @param idCard 身份证号
     * @return 老人信息
     */
    @Override
    public ElderInfo selectElderInfoByIdCard(String idCard)
    {
        return elderInfoMapper.selectElderInfoByIdCard(idCard);
    }

    /**
     * 导出老人基础信息
     *
     * @param elderInfo 查询条件
     * @return 老人基础信息列表
     */
    @Override
    public List<ElderInfo> exportElderInfo(ElderInfo elderInfo)
    {
        return elderInfoMapper.selectElderInfoList(elderInfo);
    }
}