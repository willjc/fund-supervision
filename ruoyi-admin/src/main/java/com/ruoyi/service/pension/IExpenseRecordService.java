package com.ruoyi.service.pension;

import java.util.List;
import com.ruoyi.domain.pension.ExpenseRecord;

/**
 * 费用记录Service接口
 *
 * @author ruoyi
 * @date 2025-12-18
 */
public interface IExpenseRecordService
{
    /**
     * 查询费用记录
     *
     * @param recordId 费用记录主键
     * @return 费用记录
     */
    public ExpenseRecord selectExpenseRecordByRecordId(Long recordId);

    /**
     * 查询费用记录列表
     *
     * @param expenseRecord 费用记录
     * @return 费用记录集合
     */
    public List<ExpenseRecord> selectExpenseRecordList(ExpenseRecord expenseRecord);

    /**
     * 新增费用记录
     *
     * @param expenseRecord 费用记录
     * @return 结果
     */
    public int insertExpenseRecord(ExpenseRecord expenseRecord);

    /**
     * 修改费用记录
     *
     * @param expenseRecord 费用记录
     * @return 结果
     */
    public int updateExpenseRecord(ExpenseRecord expenseRecord);

    /**
     * 批量删除费用记录
     *
     * @param recordIds 需要删除的费用记录主键集合
     * @return 结果
     */
    public int deleteExpenseRecordByRecordIds(Long[] recordIds);

    /**
     * 删除费用记录信息
     *
     * @param recordId 费用记录主键
     * @return 结果
     */
    public int deleteExpenseRecordByRecordId(Long recordId);

    /**
     * 根据老人ID查询费用记录列表
     *
     * @param elderId 老人ID
     * @return 费用记录集合
     */
    public List<ExpenseRecord> selectExpenseRecordByElderId(Long elderId);

    /**
     * 根据账户ID查询费用记录列表
     *
     * @param accountId 账户ID
     * @return 费用记录集合
     */
    public List<ExpenseRecord> selectExpenseRecordByAccountId(Long accountId);

    /**
     * 创建费用记录（用于支付、押金扣除等场景）
     *
     * @param elderId 老人ID
     * @param accountId 账户ID
     * @param expenseType 费用类型(deposit:押金,service:服务费,member:会员费,other:其他)
     * @param transactionType 交易类型(income:收入,expense:支出)
     * @param amount 金额
     * @param description 描述
     * @param relatedId 关联ID(如订单ID、押金申请ID等)
     * @param relatedType 关联类型
     * @param balanceBefore 交易前余额
     * @param balanceAfter 交易后余额
     * @return 结果
     */
    public int createExpenseRecord(Long elderId, Long accountId, String expenseType,
                                  String transactionType, java.math.BigDecimal amount,
                                  String description, Long relatedId, String relatedType,
                                  java.math.BigDecimal balanceBefore, java.math.BigDecimal balanceAfter);

    /**
     * 生成押金使用费用记录
     *
     * @param elderId 老人ID
     * @param accountId 账户ID
     * @param depositApplyId 押金申请ID
     * @param amount 使用金额
     * @param balanceBefore 使用前余额
     * @param balanceAfter 使用后余额
     * @return 结果
     */
    public int createDepositExpenseRecord(Long elderId, Long accountId, Long depositApplyId,
                                         java.math.BigDecimal amount, java.math.BigDecimal balanceBefore,
                                         java.math.BigDecimal balanceAfter);

    /**
     * 生成订单支付费用记录
     *
     * @param elderId 老人ID
     * @param accountId 账户ID
     * @param orderId 订单ID
     * @param orderType 订单类型
     * @param depositAmount 押金金额
     * @param serviceAmount 服务费金额
     * @param memberAmount 会员费金额
     * @param otherAmount 其他费用金额
     * @param balanceBefore 支付前余额
     * @param balanceAfter 支付后余额
     * @return 结果
     */
    public int createOrderExpenseRecords(Long elderId, Long accountId, Long orderId, String orderType,
                                        java.math.BigDecimal depositAmount, java.math.BigDecimal serviceAmount,
                                        java.math.BigDecimal memberAmount, java.math.BigDecimal otherAmount,
                                        java.math.BigDecimal balanceBefore, java.math.BigDecimal balanceAfter);
}