package com.ruoyi.service.pension;

import java.util.List;
import java.util.Map;
import com.ruoyi.domain.pension.TransactionRecord;

/**
 * 交易记录Service接口
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public interface ITransactionRecordService
{
    /**
     * 查询交易记录
     *
     * @param transactionId 交易记录主键
     * @return 交易记录
     */
    public TransactionRecord selectTransactionRecordByTransactionId(Long transactionId);

    /**
     * 查询交易记录列表
     *
     * @param transactionRecord 交易记录
     * @return 交易记录集合
     */
    public List<TransactionRecord> selectTransactionRecordList(TransactionRecord transactionRecord);

    /**
     * 根据账户ID查询交易记录
     *
     * @param accountId 账户ID
     * @return 交易记录集合
     */
    public List<TransactionRecord> selectTransactionRecordByAccountId(Long accountId);

    /**
     * 根据机构ID查询交易记录
     *
     * @param institutionId 机构ID
     * @return 交易记录集合
     */
    public List<TransactionRecord> selectTransactionRecordByInstitutionId(Long institutionId);

    /**
     * 新增交易记录
     *
     * @param transactionRecord 交易记录
     * @return 结果
     */
    public int insertTransactionRecord(TransactionRecord transactionRecord);

    /**
     * 修改交易记录
     *
     * @param transactionRecord 交易记录
     * @return 结果
     */
    public int updateTransactionRecord(TransactionRecord transactionRecord);

    /**
     * 批量删除交易记录
     *
     * @param transactionIds 需要删除的交易记录主键集合
     * @return 结果
     */
    public int deleteTransactionRecordByTransactionIds(Long[] transactionIds);

    /**
     * 删除交易记录信息
     *
     * @param transactionId 交易记录主键
     * @return 结果
     */
    public int deleteTransactionRecordByTransactionId(Long transactionId);

    /**
     * 交易统计分析
     *
     * @param institutionId 机构ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    public Map<String, Object> getTransactionStatistics(Long institutionId, String startDate, String endDate);

    /**
     * 批量创建交易记录
     *
     * @param transactionRecords 交易记录列表
     * @return 结果
     */
    public int batchCreateTransactionRecords(List<TransactionRecord> transactionRecords);

    /**
     * 获取今日交易汇总
     *
     * @param institutionId 机构ID
     * @return 汇总结果
     */
    public Map<String, Object> getTodayTransactionSummary(Long institutionId);

    /**
     * 查询异常交易记录
     *
     * @return 异常交易记录列表
     */
    public List<TransactionRecord> selectAbnormalTransactions();

    /**
     * 创建交易记录（内部使用）
     *
     * @param accountId 账户ID
     * @param elderId 老人ID
     * @param institutionId 机构ID
     * @param transactionType 交易类型
     * @param businessType 业务类型
     * @param amount 交易金额
     * @param balanceBefore 交易前余额
     * @param balanceAfter 交易后余额
     * @param serviceBalance 服务费余额
     * @param depositBalance 押金余额
     * @param memberBalance 会员费余额
     * @param businessDesc 业务描述
     * @param operator 操作人
     * @return 交易记录
     */
    public TransactionRecord createTransactionRecord(Long accountId, Long elderId, Long institutionId,
                                                    String transactionType, String businessType,
                                                    java.math.BigDecimal amount,
                                                    java.math.BigDecimal balanceBefore,
                                                    java.math.BigDecimal balanceAfter,
                                                    java.math.BigDecimal serviceBalance,
                                                    java.math.BigDecimal depositBalance,
                                                    java.math.BigDecimal memberBalance,
                                                    String businessDesc, String operator);
}