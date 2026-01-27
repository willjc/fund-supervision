package com.ruoyi.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.pension.TransactionRecordMapper;
import com.ruoyi.domain.pension.TransactionRecord;
import com.ruoyi.service.pension.ITransactionRecordService;

/**
 * 交易记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@Service
public class TransactionRecordServiceImpl implements ITransactionRecordService
{
    @Autowired
    private TransactionRecordMapper transactionRecordMapper;

    /**
     * 查询交易记录
     *
     * @param transactionId 交易记录主键
     * @return 交易记录
     */
    @Override
    public TransactionRecord selectTransactionRecordByTransactionId(Long transactionId)
    {
        return transactionRecordMapper.selectTransactionRecordByTransactionId(transactionId);
    }

    /**
     * 查询交易记录列表
     *
     * @param transactionRecord 交易记录
     * @return 交易记录
     */
    @Override
    public List<TransactionRecord> selectTransactionRecordList(TransactionRecord transactionRecord)
    {
        return transactionRecordMapper.selectTransactionRecordList(transactionRecord);
    }

    /**
     * 根据账户ID查询交易记录
     *
     * @param accountId 账户ID
     * @return 交易记录集合
     */
    @Override
    public List<TransactionRecord> selectTransactionRecordByAccountId(Long accountId)
    {
        return transactionRecordMapper.selectTransactionRecordByAccountId(accountId);
    }

    /**
     * 根据机构ID查询交易记录
     *
     * @param institutionId 机构ID
     * @return 交易记录集合
     */
    @Override
    public List<TransactionRecord> selectTransactionRecordByInstitutionId(Long institutionId)
    {
        return transactionRecordMapper.selectTransactionRecordByInstitutionId(institutionId);
    }

    /**
     * 新增交易记录
     *
     * @param transactionRecord 交易记录
     * @return 结果
     */
    @Override
    public int insertTransactionRecord(TransactionRecord transactionRecord)
    {
        transactionRecord.setCreateTime(DateUtils.getNowDate());
        return transactionRecordMapper.insertTransactionRecord(transactionRecord);
    }

    /**
     * 修改交易记录
     *
     * @param transactionRecord 交易记录
     * @return 结果
     */
    @Override
    public int updateTransactionRecord(TransactionRecord transactionRecord)
    {
        transactionRecord.setUpdateTime(DateUtils.getNowDate());
        return transactionRecordMapper.updateTransactionRecord(transactionRecord);
    }

    /**
     * 批量���除交易记录
     *
     * @param transactionIds 需要删除的交易记录主键
     * @return 结果
     */
    @Override
    public int deleteTransactionRecordByTransactionIds(Long[] transactionIds)
    {
        return transactionRecordMapper.deleteTransactionRecordByTransactionIds(transactionIds);
    }

    /**
     * 删除交易记录信息
     *
     * @param transactionId 交易记录主键
     * @return 结果
     */
    @Override
    public int deleteTransactionRecordByTransactionId(Long transactionId)
    {
        return transactionRecordMapper.deleteTransactionRecordByTransactionId(transactionId);
    }

    /**
     * 交易统计分析
     *
     * @param institutionId 机构ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    @Override
    public Map<String, Object> getTransactionStatistics(Long institutionId, String startDate, String endDate)
    {
        Map<String, Object> statistics = new HashMap<>();

        // 按交易类型统计
        List<Map<String, Object>> typeStats = transactionRecordMapper.selectTransactionStatisticsByType(
            institutionId, startDate, endDate);
        BigDecimal incomeTotal = BigDecimal.ZERO;
        BigDecimal expenseTotal = BigDecimal.ZERO;

        for (Map<String, Object> stat : typeStats) {
            String transactionType = (String) stat.get("transactionType");
            BigDecimal amount = (BigDecimal) stat.get("totalAmount");

            if ("1".equals(transactionType)) { // 入账
                incomeTotal = incomeTotal.add(amount);
            } else if ("2".equals(transactionType)) { // 出账
                expenseTotal = expenseTotal.add(amount);
            }
        }

        // 按业务类型统计
        List<Map<String, Object>> businessStats = transactionRecordMapper.selectTransactionStatisticsByBusinessType(
            institutionId, startDate, endDate);

        // 今日交易汇总
        Map<String, Object> todaySummary = getTodayTransactionSummary(institutionId);

        statistics.put("incomeTotal", incomeTotal);
        statistics.put("expenseTotal", expenseTotal);
        statistics.put("netAmount", incomeTotal.subtract(expenseTotal));
        statistics.put("typeStatistics", typeStats);
        statistics.put("businessStatistics", businessStats);
        statistics.put("todaySummary", todaySummary);

        return statistics;
    }

    /**
     * 批量创建交易记录
     *
     * @param transactionRecords 交易记录列表
     * @return 结果
     */
    @Override
    public int batchCreateTransactionRecords(List<TransactionRecord> transactionRecords)
    {
        int result = 0;
        for (TransactionRecord record : transactionRecords) {
            result += insertTransactionRecord(record);
        }
        return result;
    }

    /**
     * 获取今日交易汇总
     *
     * @param institutionId 机构ID
     * @return 汇总结果
     */
    @Override
    public Map<String, Object> getTodayTransactionSummary(Long institutionId)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());

        Map<String, Object> summary = new HashMap<>();

        // 今日交易笔数和金额
        Map<String, Object> todayStats = transactionRecordMapper.selectTodayTransactionSummary(
            institutionId, today);

        if (todayStats != null) {
            summary.put("totalCount", todayStats.get("totalCount"));
            summary.put("totalAmount", todayStats.get("totalAmount"));
            summary.put("incomeCount", todayStats.get("incomeCount"));
            summary.put("incomeAmount", todayStats.get("incomeAmount"));
            summary.put("expenseCount", todayStats.get("expenseCount"));
            summary.put("expenseAmount", todayStats.get("expenseAmount"));
        } else {
            summary.put("totalCount", 0L);
            summary.put("totalAmount", BigDecimal.ZERO);
            summary.put("incomeCount", 0L);
            summary.put("incomeAmount", BigDecimal.ZERO);
            summary.put("expenseCount", 0L);
            summary.put("expenseAmount", BigDecimal.ZERO);
        }

        return summary;
    }

    /**
     * 查询异常交易记录
     *
     * @return 异常交易记录列表
     */
    @Override
    public List<TransactionRecord> selectAbnormalTransactions()
    {
        return transactionRecordMapper.selectAbnormalTransactions();
    }

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
    @Override
    public TransactionRecord createTransactionRecord(Long accountId, Long elderId, Long institutionId,
                                                    String transactionType, String businessType,
                                                    BigDecimal amount, BigDecimal balanceBefore,
                                                    BigDecimal balanceAfter, BigDecimal serviceBalance,
                                                    BigDecimal depositBalance, BigDecimal memberBalance,
                                                    String businessDesc, String operator)
    {
        TransactionRecord record = new TransactionRecord();
        record.setAccountId(accountId);
        record.setElderId(elderId);
        record.setInstitutionId(institutionId);
        record.setTransactionNo(generateTransactionNo());
        record.setTransactionType(transactionType);
        record.setBusinessType(businessType);
        record.setAmount(amount);
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setServiceBalance(serviceBalance);
        record.setDepositBalance(depositBalance);
        record.setMemberBalance(memberBalance);
        record.setTransactionDate(new Date());
        record.setBusinessDesc(businessDesc);
        record.setOperator(operator);

        insertTransactionRecord(record);
        return record;
    }

    /**
     * 生成交易流水号
     *
     * @return 交易流水号
     */
    private String generateTransactionNo()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        int random = (int) (Math.random() * 10000);
        return "TXN" + timestamp + String.format("%04d", random);
    }
}