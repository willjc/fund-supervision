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

    /**
     * 查询拨付单流水列表（以fund_transfer为主表）
     * 用于监管账户流水页面展示划拨单记录
     *
     * @param params 查询参数（包含institutionId, status, transferType, beginTime, endTime, currentUserId等）
     * @return 拨付单流水列表
     */
    public List<Map<String, Object>> selectTransferFlowList(Map<String, Object> params);

    /**
     * 查询资金划拨记录列表（所有状态）
     * 用于资金划拨记录页面展示，包括待处理、处理中、已完成、已取消等所有状态
     *
     * @param params 查询参数（包含transferNo, transferType, elderName, status, beginTime, endTime, currentUserId等）
     * @return 资金划拨记录列表
     */
    public List<Map<String, Object>> selectTransferRecordList(Map<String, Object> params);

    /**
     * 计算机构的监管账户余额
     * 监管账户余额 = 未拨付的拨付单金额 + 所有老人押金总和 - 已拨付的押金金额
     *
     * @param institutionId 机构ID
     * @return 监管账户余额
     */
    public java.math.BigDecimal calculateSupervisionBalance(Long institutionId);

    /**
     * 计算机构的预收费余额（未拨付的拨付单金额，不含押金划拨）
     *
     * @param institutionId 机构ID
     * @return 预收费余额
     */
    public java.math.BigDecimal calculatePrepaidBalance(Long institutionId);

    /**
     * 计算机构的押金余额（该机构所有老人的 deposit_balance 总和）
     *
     * @param institutionId 机构ID
     * @return 押金余额
     */
    public java.math.BigDecimal calculateDepositBalance(Long institutionId);

    /**
     * 获取所有机构的账户统计列表
     * 用于管理端机构账户查询页面
     *
     * @param params 查询参数（包含institutionName, accountStatus等）
     * @return 机构账户列表
     */
    public List<Map<String, Object>> selectInstitutionAccountList(Map<String, Object> params);

    /**
     * 获取所有机构的账户统计数据
     * 包含：机构总数、监管账户总余额、冻结账户数、异常账户数
     *
     * @return 统计数据
     */
    public Map<String, Object> getInstitutionStatistics();
}
