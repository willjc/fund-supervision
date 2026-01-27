package com.ruoyi.mapper.pension;

import java.util.List;
import java.util.Map;
import java.util.Date;
import com.ruoyi.domain.pension.SupervisionAccountLog;
import org.apache.ibatis.annotations.Param;

/**
 * 监管账户流水Mapper接口
 *
 * @author ruoyi
 * @date 2025-01-26
 */
public interface SupervisionAccountLogMapper
{
    /**
     * 查询监管账户流水
     *
     * @param logId 监管账户流水主键
     * @return 监管账户流水
     */
    public SupervisionAccountLog selectSupervisionAccountLogByLogId(Long logId);

    /**
     * 查询监管账户流水列表
     *
     * @param supervisionAccountLog 监管账户流水
     * @return 监管账户流水集合
     */
    public List<SupervisionAccountLog> selectSupervisionAccountLogList(SupervisionAccountLog supervisionAccountLog);

    /**
     * 根据机构ID查询流水记录
     *
     * @param institutionId 机构ID
     * @return 流水记录集合
     */
    public List<SupervisionAccountLog> selectSupervisionAccountLogByInstitutionId(@Param("institutionId") Long institutionId);

    /**
     * 新增监管账户流水
     *
     * @param supervisionAccountLog 监管账户流水
     * @return 结果
     */
    public int insertSupervisionAccountLog(SupervisionAccountLog supervisionAccountLog);

    /**
     * 获取监管账户当前余额
     *
     * @param institutionId 机构ID
     * @return 当前余额
     */
    public java.math.BigDecimal selectCurrentBalance(@Param("institutionId") Long institutionId);

    /**
     * 统计指定时间段的收入和支出
     *
     * @param institutionId 机构ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    public java.util.Map<String, Object> selectStatisticsByDateRange(
        @Param("institutionId") Long institutionId,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate);

    /**
     * 根据订单ID查询流水记录
     *
     * @param orderId 订单ID
     * @return 流水记录集合
     */
    public List<SupervisionAccountLog> selectSupervisionAccountLogByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据划拨ID查询流水记录
     *
     * @param transferId 划拨ID
     * @return 流水记录集合
     */
    public List<SupervisionAccountLog> selectSupervisionAccountLogByTransferId(@Param("transferId") Long transferId);

    /**
     * 计算指定时间点的基本账户余额
     * 基本账户余额 = 该时间点之前所有支出金额的累计总和
     *
     * @param institutionId 机构ID
     * @param transactionTime 交易时间
     * @return 基本账户余额
     */
    public java.math.BigDecimal selectBasicBalanceByTime(@Param("institutionId") Long institutionId, @Param("transactionTime") java.util.Date transactionTime);

    /**
     * 查询所有机构的监管账户流水（无数据权限限制，用于超管查看）
     *
     * @param supervisionAccountLog 监管账户流水
     * @return 监管账户流水集合
     */
    public List<SupervisionAccountLog> selectAllSupervisionAccountLogList(SupervisionAccountLog supervisionAccountLog);

    /**
     * 查询拨付单流水列表（以fund_transfer为主表）
     * 用于监管账户流水页面展示划拨单记录
     *
     * @param params 查询参数（包含institutionId, status, transferType, beginTime, endTime, currentUserId等）
     * @return 拨付单流水列表
     */
    public List<Map<String, Object>> selectTransferFlowList(@Param("params") Map<String, Object> params);
}
