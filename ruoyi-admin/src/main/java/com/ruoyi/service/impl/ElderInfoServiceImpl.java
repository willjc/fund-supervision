package com.ruoyi.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.ElderInfoMapper;
import com.ruoyi.domain.ElderInfo;
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

    /**
     * 查询老人基础信息
     *
     * @param elderId 老人基础信息主键
     * @return 老人基础信息
     */
    @Override
    public ElderInfo selectElderInfoByElderId(Long elderId)
    {
        return elderInfoMapper.selectElderInfoByElderId(elderId);
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