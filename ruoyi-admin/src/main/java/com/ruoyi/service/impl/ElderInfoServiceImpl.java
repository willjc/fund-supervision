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

    /**
     * 导入老人信息
     *
     * @param elderList 老人信息列表
     * @param operName 操作人
     * @return 导入结果消息
     */
    @Override
    public String importElderInfo(List<ElderInfo> elderList, String operName)
    {
        if (StringUtils.isNull(elderList) || elderList.size() == 0)
        {
            throw new RuntimeException("请输入导入数据！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (ElderInfo elderInfo : elderList)
        {
            // 验证必填字段
            if (StringUtils.isEmpty(elderInfo.getElderName()))
            {
                failureNum++;
                failureMsg.append("<br/>" + failureNum + "、老人姓名为空");
                continue;
            }
            if (StringUtils.isEmpty(elderInfo.getIdCard()))
            {
                failureNum++;
                failureMsg.append("<br/>" + failureNum + "、身份证号为空");
                continue;
            }
            // 检查身份证号是否已存在
            ElderInfo existing = elderInfoMapper.selectElderInfoByIdCard(elderInfo.getIdCard());
            if (existing != null)
            {
                failureNum++;
                failureMsg.append("<br/>" + failureNum + "、身份证号 " + elderInfo.getIdCard() + " 已存在");
                continue;
            }
            // 设置默认值
            elderInfo.setCreateBy(operName);
            elderInfo.setCreateTime(DateUtils.getNowDate());
            elderInfo.setStatus("0"); // 默认状态为未入住
            // 如果密码为空，设置默认密码：123456（MD5加密）
            if (StringUtils.isEmpty(elderInfo.getPassword()))
            {
                elderInfo.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
            }
            // 插入数据
            try
            {
                elderInfoMapper.insertElderInfo(elderInfo);
                successNum++;
                successMsg.append("<br/>" + successNum + "、" + elderInfo.getElderName() + " 导入成功");
            }
            catch (Exception e)
            {
                failureNum++;
                failureMsg.append("<br/>" + failureNum + "、" + elderInfo.getElderName() + " 导入失败：" + e.getMessage());
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new RuntimeException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条数据");
        }
        return successMsg.toString();
    }
}