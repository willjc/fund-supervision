package com.ruoyi.mapper.pension;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.domain.pension.BalanceWarning;

/**
 * 余额预警Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public interface BalanceWarningMapper
{
    /**
     * 查询余额预警
     *
     * @param warningId 余额预警主键
     * @return 余额预警
     */
    public BalanceWarning selectBalanceWarningByWarningId(Long warningId);

    /**
     * 查询余额预警列表
     *
     * @param balanceWarning 余额预警
     * @return 余额预警集合
     */
    public List<BalanceWarning> selectBalanceWarningList(BalanceWarning balanceWarning);

    /**
     * 根据机构ID查询预警记录
     *
     * @param institutionId 机构ID
     * @return 余额预警集合
     */
    public List<BalanceWarning> selectBalanceWarningByInstitutionId(@Param("institutionId") Long institutionId);

    /**
     * 新增余额预警
     *
     * @param balanceWarning 余额预警
     * @return 结果
     */
    public int insertBalanceWarning(BalanceWarning balanceWarning);

    /**
     * 修改余额预警
     *
     * @param balanceWarning 余额预警
     * @return 结果
     */
    public int updateBalanceWarning(BalanceWarning balanceWarning);

    /**
     * 删除余额预警
     *
     * @param warningId 余额预警主键
     * @return 结果
     */
    public int deleteBalanceWarningByWarningId(Long warningId);

    /**
     * 批量删除余额预警
     *
     * @param warningIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBalanceWarningByWarningIds(Long[] warningIds);

    /**
     * 按预警类型统计
     *
     * @param institutionId 机构ID
     * @return 统计结果
     */
    public List<Map<String, Object>> selectWarningStatisticsByType(@Param("institutionId") Long institutionId);

    /**
     * 按预警状态统计
     *
     * @param institutionId 机构ID
     * @return 统计结果
     */
    public List<Map<String, Object>> selectWarningStatisticsByStatus(@Param("institutionId") Long institutionId);

    /**
     * 查询严重预警记录
     *
     * @param institutionId 机构ID
     * @return 严重预警列表
     */
    public List<BalanceWarning> selectCriticalWarnings(@Param("institutionId") Long institutionId);

    /**
     * 删除已处理的预警记录
     *
     * @param institutionId 机构ID
     * @return 结果
     */
    public int deleteHandledWarnings(@Param("institutionId") Long institutionId);

    /**
     * 查询未处理的预警记录
     *
     * @param accountId 账户ID
     * @param warningType 预警类型
     * @return 预警记录
     */
    public BalanceWarning selectUnhandledWarning(@Param("accountId") Long accountId, @Param("warningType") String warningType);
}