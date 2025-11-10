package com.ruoyi.mapper.pension;

import java.util.List;
import java.util.Date;
import com.ruoyi.domain.pension.TransactionRecord;
import org.apache.ibatis.annotations.Param;

/**
 * 交易流水记录Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public interface TransactionRecordMapper
{
    /**
     * 查询交易流水记录
     *
     * @param transactionId 交易流水记录主键
     * @return 交易流水记录
     */
    public TransactionRecord selectTransactionRecordByTransactionId(Long transactionId);

    /**
     * 查询交易流水记录列表
     *
     * @param transactionRecord 交易流水记录
     * @return 交易流水记录集合
     */
    public List<TransactionRecord> selectTransactionRecordList(TransactionRecord transactionRecord);

    /**
     * 根据账户ID查询交易记录
     *
     * @param accountId 账户ID
     * @return 交易流水记录集合
     */
    public List<TransactionRecord> selectTransactionRecordByAccountId(Long accountId);

    /**
     * 根据机构ID查询交易记录
     *
     * @param institutionId 机构ID
     * @return 交易流水记录集合
     */
    public List<TransactionRecord> selectTransactionRecordByInstitutionId(@Param("institutionId") Long institutionId);

    /**
     * 根据老人ID查询交易记录
     *
     * @param elderId 老人ID
     * @return 交易流水记录集合
     */
    public List<TransactionRecord> selectTransactionRecordByElderId(Long elderId);

    /**
     * 根据划拨ID查询交易记录
     *
     * @param transferId 划拨ID
     * @return 交易流水记录集合
     */
    public List<TransactionRecord> selectTransactionRecordByTransferId(Long transferId);

    /**
     * 查询交易统计
     *
     * @param accountId 账户ID
     * @param businessType 业务类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    public java.util.Map<String, Object> selectTransactionStatistics(@Param("accountId") Long accountId,
                                                                    @Param("businessType") String businessType,
                                                                    @Param("startDate") Date startDate,
                                                                    @Param("endDate") Date endDate);

    /**
     * 新增交易流水记录
     *
     * @param transactionRecord 交易流水记录
     * @return 结果
     */
    public int insertTransactionRecord(TransactionRecord transactionRecord);

    /**
     * 修改交易流水记录
     *
     * @param transactionRecord 交易流水记录
     * @return 结果
     */
    public int updateTransactionRecord(TransactionRecord transactionRecord);

    /**
     * 删除交易流水记录
     *
     * @param transactionId 交易流水记录主键
     * @return 结果
     */
    public int deleteTransactionRecordByTransactionId(Long transactionId);

    /**
     * 批量删除交易流水记录
     *
     * @param transactionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTransactionRecordByTransactionIds(Long[] transactionIds);

    /**
     * 查询最新交易记录
     *
     * @param accountId 账户ID
     * @param limit 限制条数
     * @return 交易流水记录集合
     */
    public List<TransactionRecord> selectLatestTransactions(@Param("accountId") Long accountId,
                                                          @Param("limit") Integer limit);

    /**
     * 按交易类型统计
     *
     * @param institutionId 机构ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    public List<java.util.Map<String, Object>> selectTransactionStatisticsByType(
        @Param("institutionId") Long institutionId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate);

    /**
     * 按业务类型统计
     *
     * @param institutionId 机构ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    public List<java.util.Map<String, Object>> selectTransactionStatisticsByBusinessType(
        @Param("institutionId") Long institutionId,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate);

    /**
     * 今日交易汇总
     *
     * @param institutionId 机构ID
     * @param today 日期
     * @return 汇总结果
     */
    public java.util.Map<String, Object> selectTodayTransactionSummary(
        @Param("institutionId") Long institutionId,
        @Param("today") String today);

    /**
     * 查询异常交易记录
     *
     * @return 异常交易记录列表
     */
    public List<TransactionRecord> selectAbnormalTransactions();
}