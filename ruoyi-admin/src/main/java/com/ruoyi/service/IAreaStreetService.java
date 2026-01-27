package com.ruoyi.service;

import java.util.List;
import com.ruoyi.domain.AreaStreet;

/**
 * 区域街道信息Service接口
 *
 * @author ruoyi
 * @date 2025-12-04
 */
public interface IAreaStreetService
{
    /**
     * 查询区域街道信息
     *
     * @param id 区域街道信息主键
     * @return 区域街道信息
     */
    public AreaStreet selectAreaStreetById(Long id);

    /**
     * 查询区域街道信息列表
     *
     * @param areaStreet 区域街道信息
     * @return 区域街道信息集合
     */
    public List<AreaStreet> selectAreaStreetList(AreaStreet areaStreet);

    /**
     * 查询所有区域列表
     *
     * @return 区域列表
     */
    public List<AreaStreet> selectAllAreas();

    /**
     * 根据区域代码查询街道列表
     *
     * @param areaCode 区域代码
     * @return 街道列表
     */
    public List<AreaStreet> selectStreetsByAreaCode(String areaCode);

    /**
     * 新增区域街道信息
     *
     * @param areaStreet 区域街道信息
     * @return 结果
     */
    public int insertAreaStreet(AreaStreet areaStreet);

    /**
     * 修改区域街道信息
     *
     * @param areaStreet 区域街道信息
     * @return 结果
     */
    public int updateAreaStreet(AreaStreet areaStreet);

    /**
     * 批量删除区域街道信息
     *
     * @param ids 需要删除的区域街道信息主键集合
     * @return 结果
     */
    public int deleteAreaStreetByIds(Long[] ids);

    /**
     * 删除区域街道信息信息
     *
     * @param id 区域街道信息主键
     * @return 结果
     */
    public int deleteAreaStreetById(Long id);

    /**
     * 批量导入区域街道数据
     *
     * @param areaStreetList 区域街道信息列表
     * @return 导入结果
     */
    public String importAreaStreet(List<AreaStreet> areaStreetList);
}