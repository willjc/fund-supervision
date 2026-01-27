package com.ruoyi.service.pension;

import java.util.List;
import java.util.Map;
import com.ruoyi.domain.pension.SupervisionAccountLog;

/**
 * 监管账户流水Service接口
 *
 * @author ruoyi
 * @date 2025-01-26
 */
public interface ISupervisionAccountLogService
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
    public List<SupervisionAccountLog> selectSupervisionAccountLogByInstitutionId(Long institutionId);

    /**
     * 新增监管账户流水
     *
     * @param supervisionAccountLog 监管账户流水
     * @return 结果
     */
    public int insertSupervisionAccountLog(SupervisionAccountLog supervisionAccountLog);

    /**
     * 记录收入流水
     *
     * @param institutionId 机构ID
     * @param orderId 关联订单ID
     * @param amount 交易金额
     * @param businessDesc 业务描述
     * @param counterparty 交易对手
     * @return 流水记录
     */
    public SupervisionAccountLog recordIncome(Long institutionId, Long orderId,
                                              java.math.BigDecimal amount,
                                              String businessDesc, String counterparty);

    /**
     * 记录支出流水（划拨到基本账户）
     *
     * @param institutionId 机构ID
     * @param transferId 关联划拨ID
     * @param amount 交易金额
     * @param businessDesc 业务描述
     * @param counterparty 交易对手
     * @return 流水记录
     */
    public SupervisionAccountLog recordTransferOut(Long institutionId, Long transferId,
                                                    java.math.BigDecimal amount,
                                                    String businessDesc, String counterparty);

    /**
     * 获取监管账户当前余额
     *
     * @param institutionId 机构ID
     * @return 当前余额
     */
    public java.math.BigDecimal getCurrentBalance(Long institutionId);

    /**
     * 统计指定时间段的收入和支出
     *
     * @param institutionId 机构ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    public Map<String, Object> getStatisticsByDateRange(Long institutionId, String startDate, String endDate);

    /**
     * 生成交易流水号
     *
     * @return 交易流水号
     */
    public String generateTransactionNo();

    /**
     * 计算指定时间点的基本账户余额
     * 基本账户余额 = 该时间点之前所有支出金额的累计总和
     *
     * @param institutionId 机构ID
     * @param transactionTime 交易时间
     * @return 基本账户余额
     */
    public java.math.BigDecimal selectBasicBalanceByTime(Long institutionId, java.util.Date transactionTime);

    /**
     * 查询所有机构的监管账户流水（无数据权限限制，用于超管查看）
     *
     * @param supervisionAccountLog 监管账户流水
     * @return 监管账户流水集合
     */
    public List<SupervisionAccountLog> selectAllSupervisionAccountLogList(SupervisionAccountLog supervisionAccountLog);
}
