package com.ruoyi.service;

import java.util.List;
import com.ruoyi.domain.ElderInfo;

/**
 * 老人基础信息Service接口
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public interface IElderInfoService
{
    /**
     * 查询老人基础信息
     *
     * @param elderId 老人基础信息主键
     * @return 老人基础信息
     */
    public ElderInfo selectElderInfoByElderId(Long elderId);

    /**
     * 查询老人基础信息列表
     *
     * @param elderInfo 老人基础信息
     * @return 老人基础信息集合
     */
    public List<ElderInfo> selectElderInfoList(ElderInfo elderInfo);

    /**
     * 新增老人基础信息
     *
     * @param elderInfo 老人基础信息
     * @return 结果
     */
    public int insertElderInfo(ElderInfo elderInfo);

    /**
     * 修改老人基础信息
     *
     * @param elderInfo 老人基础信息
     * @return 结果
     */
    public int updateElderInfo(ElderInfo elderInfo);

    /**
     * 批量删除老人基础信息
     *
     * @param elderIds 需要删除的老人基础信息主键集合
     * @return 结果
     */
    public int deleteElderInfoByElderIds(Long[] elderIds);

    /**
     * 删除老人基础信息信息
     *
     * @param elderId 老人基础信息主键
     * @return 结果
     */
    public int deleteElderInfoByElderId(Long elderId);

    /**
     * 根据身份证号查询老人信息
     *
     * @param idCard 身份证号
     * @return 老人信息
     */
    public ElderInfo selectElderInfoByIdCard(String idCard);

    /**
     * 导出老人基础信息
     *
     * @param elderInfo 查询条件
     * @return 老人基础信息列表
     */
    public List<ElderInfo> exportElderInfo(ElderInfo elderInfo);
}