package com.ruoyi.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.mapper.pension.SupervisionAccountLogMapper;
import com.ruoyi.domain.pension.SupervisionAccountLog;
import com.ruoyi.service.pension.ISupervisionAccountLogService;

/**
 * 监管账户流水Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-26
 */
@Service
public class SupervisionAccountLogServiceImpl implements ISupervisionAccountLogService
{
    @Autowired
    private SupervisionAccountLogMapper supervisionAccountLogMapper;

    /**
     * 查询监管账户流水
     *
     * @param logId 监管账户流水主键
     * @return 监管账户流水
     */
    @Override
    public SupervisionAccountLog selectSupervisionAccountLogByLogId(Long logId)
    {
        return supervisionAccountLogMapper.selectSupervisionAccountLogByLogId(logId);
    }

    /**
     * 查询监管账户流水列表
     *
     * @param supervisionAccountLog 监管账户流水
     * @return 监管账户流水
     */
    @Override
    public List<SupervisionAccountLog> selectSupervisionAccountLogList(SupervisionAccountLog supervisionAccountLog)
    {
        return supervisionAccountLogMapper.selectSupervisionAccountLogList(supervisionAccountLog);
    }

    /**
     * 根据机构ID查询流水记录
     *
     * @param institutionId 机构ID
     * @return 流水记录集合
     */
    @Override
    public List<SupervisionAccountLog> selectSupervisionAccountLogByInstitutionId(Long institutionId)
    {
        return supervisionAccountLogMapper.selectSupervisionAccountLogByInstitutionId(institutionId);
    }

    /**
     * 新增监管账户流水
     *
     * @param supervisionAccountLog 监管账户流水
     * @return 结果
     */
    @Override
    public int insertSupervisionAccountLog(SupervisionAccountLog supervisionAccountLog)
    {
        return supervisionAccountLogMapper.insertSupervisionAccountLog(supervisionAccountLog);
    }

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
    @Override
    @Transactional
    public SupervisionAccountLog recordIncome(Long institutionId, Long orderId,
                                              BigDecimal amount, String businessDesc, String counterparty)
    {
        // 获取当前余额
        BigDecimal balanceBefore = supervisionAccountLogMapper.selectCurrentBalance(institutionId);
        if (balanceBefore == null) {
            balanceBefore = BigDecimal.ZERO;
        }

        // 计算交易后余额
        BigDecimal balanceAfter = balanceBefore.add(amount);

        // 创建流水记录
        SupervisionAccountLog log = new SupervisionAccountLog();
        log.setInstitutionId(institutionId);
        log.setTransactionNo(generateTransactionNo());
        log.setTransactionType("收入");
        log.setAmount(amount);
        log.setBalanceBefore(balanceBefore);
        log.setBalanceAfter(balanceAfter);
        log.setTransactionTime(new Date());
        log.setBusinessType("用户支付");
        log.setRelatedOrderId(orderId);
        log.setBusinessDesc(businessDesc);
        log.setCounterparty(counterparty);
        log.setOperator("系统");
        log.setCreateTime(new Date());

        supervisionAccountLogMapper.insertSupervisionAccountLog(log);

        return log;
    }

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
    @Override
    @Transactional
    public SupervisionAccountLog recordTransferOut(Long institutionId, Long transferId,
                                                    BigDecimal amount, String businessDesc, String counterparty)
    {
        // 获取当前余额
        BigDecimal balanceBefore = supervisionAccountLogMapper.selectCurrentBalance(institutionId);
        if (balanceBefore == null) {
            balanceBefore = BigDecimal.ZERO;
        }

        // 计算交易后余额
        BigDecimal balanceAfter = balanceBefore.subtract(amount);

        // 创建流水记录
        SupervisionAccountLog log = new SupervisionAccountLog();
        log.setInstitutionId(institutionId);
        log.setTransactionNo(generateTransactionNo());
        log.setTransactionType("支出");
        log.setAmount(amount);
        log.setBalanceBefore(balanceBefore);
        log.setBalanceAfter(balanceAfter);
        log.setTransactionTime(new Date());
        log.setBusinessType("押金划拨");
        log.setRelatedTransferId(transferId);
        log.setBusinessDesc(businessDesc);
        log.setCounterparty(counterparty);
        log.setOperator("系统");
        log.setCreateTime(new Date());

        supervisionAccountLogMapper.insertSupervisionAccountLog(log);

        return log;
    }

    /**
     * 获取监管账户当前余额
     *
     * @param institutionId 机构ID
     * @return 当前余额
     */
    @Override
    public BigDecimal getCurrentBalance(Long institutionId)
    {
        BigDecimal balance = supervisionAccountLogMapper.selectCurrentBalance(institutionId);
        return balance != null ? balance : BigDecimal.ZERO;
    }

    /**
     * 统计指定时间段的收入和支出
     *
     * @param institutionId 机构ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    @Override
    public Map<String, Object> getStatisticsByDateRange(Long institutionId, String startDate, String endDate)
    {
        Date start = DateUtils.parseDate(startDate);
        Date end = DateUtils.parseDate(endDate);
        return supervisionAccountLogMapper.selectStatisticsByDateRange(institutionId, start, end);
    }

    /**
     * 生成交易流水号
     *
     * @return 交易流水号
     */
    @Override
    public String generateTransactionNo()
    {
        // 格式: LSH + yyyyMMddHHmmss + 4位随机数
        String timestamp = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int random = (int) (Math.random() * 10000);
        return "LSH" + timestamp + String.format("%04d", random);
    }

    /**
     * 计算指定时间点的基本账户余额
     * 基本账户余额 = 该时间点之前所有支出金额的累计总和
     *
     * @param institutionId 机构ID
     * @param transactionTime 交易时间
     * @return 基本账户余额
     */
    @Override
    public BigDecimal selectBasicBalanceByTime(Long institutionId, Date transactionTime)
    {
        BigDecimal balance = supervisionAccountLogMapper.selectBasicBalanceByTime(institutionId, transactionTime);
        return balance != null ? balance : BigDecimal.ZERO;
    }

    /**
     * 查询所有机构的监管账户流水（无数据权限限制，用于超管查看）
     *
     * @param supervisionAccountLog 监管账户流水
     * @return 监管账户流水集合
     */
    @Override
    public List<SupervisionAccountLog> selectAllSupervisionAccountLogList(SupervisionAccountLog supervisionAccountLog)
    {
        return supervisionAccountLogMapper.selectAllSupervisionAccountLogList(supervisionAccountLog);
    }

    /**
     * 查询拨付单流水列表（以fund_transfer为主表）
     * 用于监管账户流水页面展示划拨单记录
     *
     * @param params 查询参数
     * @return 拨付单流水列表
     */
    @Override
    public List<Map<String, Object>> selectTransferFlowList(Map<String, Object> params)
    {
        return supervisionAccountLogMapper.selectTransferFlowList(params);
    }
}
