package com.ruoyi.mapper.pension;

import java.util.List;
import com.ruoyi.domain.pension.SupervisionWarning;

/**
 * 预警信息Mapper接口
 *
 * @author ruoyi
 * @date 2026-02-03
 */
public interface SupervisionWarningMapper
{
    /**
     * 查询预警信息
     *
     * @param warningId 预警信息主键
     * @return 预警信息
     */
    public SupervisionWarning selectSupervisionWarningByWarningId(Long warningId);

    /**
     * 根据预警编号查询预警信息
     *
     * @param warningNo 预警编号
     * @return 预警信息
     */
    public SupervisionWarning selectSupervisionWarningByWarningNo(String warningNo);

    /**
     * 查询预警信息列表
     *
     * @param supervisionWarning 预警信息
     * @return 预警信息集合
     */
    public List<SupervisionWarning> selectSupervisionWarningList(SupervisionWarning supervisionWarning);

    /**
     * 新增预警信息
     *
     * @param supervisionWarning 预警信息
     * @return 结果
     */
    public int insertSupervisionWarning(SupervisionWarning supervisionWarning);

    /**
     * 修改预警信息
     *
     * @param supervisionWarning 预警信息
     * @return 结果
     */
    public int updateSupervisionWarning(SupervisionWarning supervisionWarning);

    /**
     * 删除预警信息
     *
     * @param warningId 预警信息主键
     * @return 结果
     */
    public int deleteSupervisionWarningByWarningId(Long warningId);

    /**
     * 批量删除预警信息
     *
     * @param warningIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSupervisionWarningByWarningIds(Long[] warningIds);
}
