package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.domain.ElderFamily;

/**
 * 用户-老人关联(家属管理)Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-14
 */
public interface ElderFamilyMapper
{
    /**
     * 查询家属关联
     *
     * @param familyId 家属关联主键
     * @return 家属关联
     */
    public ElderFamily selectElderFamilyById(Long familyId);

    /**
     * 查询家属关联列表
     *
     * @param elderFamily 家属关联
     * @return 家属关联集合
     */
    public List<ElderFamily> selectElderFamilyList(ElderFamily elderFamily);

    /**
     * 新增家属关联
     *
     * @param elderFamily 家属关联
     * @return 结果
     */
    public int insertElderFamily(ElderFamily elderFamily);

    /**
     * 修改家属关联
     *
     * @param elderFamily 家属关联
     * @return 结果
     */
    public int updateElderFamily(ElderFamily elderFamily);

    /**
     * 删除家属关联
     *
     * @param familyId 家属关联主键
     * @return 结果
     */
    public int deleteElderFamilyById(Long familyId);

    /**
     * 批量删除家属关联
     *
     * @param familyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteElderFamilyByIds(Long[] familyIds);
}
