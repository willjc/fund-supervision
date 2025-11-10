package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.domain.ElderInfo;

/**
 * 老人基础信息Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public interface ElderInfoMapper
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
     * 删除老人基础信息
     *
     * @param elderId 老人基础信息主键
     * @return 结果
     */
    public int deleteElderInfoByElderId(Long elderId);

    /**
     * 批量删除老人基础信息
     *
     * @param elderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteElderInfoByElderIds(Long[] elderIds);

    /**
     * 根据身份证号查询老人信息
     *
     * @param idCard 身份证号
     * @return 老人信息
     */
    public ElderInfo selectElderInfoByIdCard(String idCard);
}