package com.ruoyi.service.pension.impl;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.pension.ExpenseRecordMapper;
import com.ruoyi.mapper.pension.AccountInfoMapper;
import com.ruoyi.mapper.OrderItemMapper;
import com.ruoyi.domain.pension.ExpenseRecord;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.OrderItem;
import com.ruoyi.service.pension.IExpenseRecordService;

/**
 * 费用记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-12-18
 */
@Service
public class ExpenseRecordServiceImpl implements IExpenseRecordService
{
    @Autowired
    private ExpenseRecordMapper expenseRecordMapper;

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 查询费用记录
     *
     * @param recordId 费用记录主键
     * @return 费用记录
     */
    @Override
    public ExpenseRecord selectExpenseRecordByRecordId(Long recordId)
    {
        return expenseRecordMapper.selectExpenseRecordByRecordId(recordId);
    }

    /**
     * 查询费用记录列表
     *
     * @param expenseRecord 费用记录
     * @return 费用记录
     */
    @Override
    public List<ExpenseRecord> selectExpenseRecordList(ExpenseRecord expenseRecord)
    {
        return expenseRecordMapper.selectExpenseRecordList(expenseRecord);
    }

    /**
     * 新增费用记录
     *
     * @param expenseRecord 费用记录
     * @return 结果
     */
    @Override
    public int insertExpenseRecord(ExpenseRecord expenseRecord)
    {
        expenseRecord.setCreateTime(DateUtils.getNowDate());
        return expenseRecordMapper.insertExpenseRecord(expenseRecord);
    }

    /**
     * 修改费用记录
     *
     * @param expenseRecord 费用记录
     * @return 结果
     */
    @Override
    public int updateExpenseRecord(ExpenseRecord expenseRecord)
    {
        expenseRecord.setUpdateTime(DateUtils.getNowDate());
        return expenseRecordMapper.updateExpenseRecord(expenseRecord);
    }

    /**
     * 批量删除费用记录
     *
     * @param recordIds 需要删除的费用记录主键
     * @return 结果
     */
    @Override
    public int deleteExpenseRecordByRecordIds(Long[] recordIds)
    {
        return expenseRecordMapper.deleteExpenseRecordByRecordIds(recordIds);
    }

    /**
     * 删除费用记录信息
     *
     * @param recordId 费用记录主键
     * @return 结果
     */
    @Override
    public int deleteExpenseRecordByRecordId(Long recordId)
    {
        return expenseRecordMapper.deleteExpenseRecordByRecordId(recordId);
    }

    /**
     * 根据老人ID查询费用记录列表
     *
     * @param elderId 老人ID
     * @return 费用记录集合
     */
    @Override
    public List<ExpenseRecord> selectExpenseRecordByElderId(Long elderId)
    {
        return expenseRecordMapper.selectExpenseRecordByElderId(elderId);
    }

    /**
     * 根据账户ID查询费用记录列表
     *
     * @param accountId 账户ID
     * @return 费用记录集合
     */
    @Override
    public List<ExpenseRecord> selectExpenseRecordByAccountId(Long accountId)
    {
        return expenseRecordMapper.selectExpenseRecordByAccountId(accountId);
    }

    /**
     * 创建费用记录（用于支付、押金扣除等场景）
     *
     * @param elderId 老人ID
     * @param accountId 账户ID
     * @param expenseType 费用类型
     * @param transactionType 交易类型
     * @param amount 金额
     * @param description 描述
     * @param relatedId 关联ID
     * @param relatedType 关联类型
     * @param balanceBefore 交易前余额
     * @param balanceAfter 交易后余额
     * @return 结果
     */
    @Override
    public int createExpenseRecord(Long elderId, Long accountId, String expenseType,
                                  String transactionType, java.math.BigDecimal amount,
                                  String description, Long relatedId, String relatedType,
                                  java.math.BigDecimal balanceBefore, java.math.BigDecimal balanceAfter)
    {
        ExpenseRecord record = new ExpenseRecord();
        record.setElderId(elderId);
        record.setAccountId(accountId);
        record.setExpenseType(expenseType);
        record.setTransactionType(transactionType);
        record.setAmount(amount);
        record.setDescription(description);
        record.setRelatedId(relatedId);
        record.setRelatedType(relatedType);
        record.setBalanceBefore(balanceBefore);
        record.setBalanceAfter(balanceAfter);
        record.setCreateBy("system");

        return insertExpenseRecord(record);
    }

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
    @Override
    public int createDepositExpenseRecord(Long elderId, Long accountId, Long depositApplyId,
                                         java.math.BigDecimal amount, java.math.BigDecimal balanceBefore,
                                         java.math.BigDecimal balanceAfter)
    {
        // 对于押金使用记录，balance_before和balance_after应该记录押金余额的变化
        // 这里需要获取账户的押金余额来计算
        AccountInfo account = accountInfoMapper.selectAccountInfoByAccountId(accountId);
        if (account != null) {
            java.math.BigDecimal currentDepositBalance = account.getDepositBalance() != null ?
                account.getDepositBalance() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal newDepositBalance = currentDepositBalance.subtract(amount);

            return createExpenseRecord(
                elderId,
                accountId,
                "deposit",
                "expense",
                amount,
                "押金使用申请审批通过",
                depositApplyId,
                "deposit_apply",
                currentDepositBalance,
                newDepositBalance
            );
        }

        // 如果账户不存在，使用传入的余额（总余额）
        return createExpenseRecord(
            elderId,
            accountId,
            "deposit",
            "expense",
            amount,
            "押金使用申请审批通过",
            depositApplyId,
            "deposit_apply",
            balanceBefore,
            balanceAfter
        );
    }

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
    @Override
    public int createOrderExpenseRecords(Long elderId, Long accountId, Long orderId, String orderType,
                                        java.math.BigDecimal depositAmount, java.math.BigDecimal serviceAmount,
                                        java.math.BigDecimal memberAmount, java.math.BigDecimal otherAmount,
                                        java.math.BigDecimal balanceBefore, java.math.BigDecimal balanceAfter)
    {
        int result = 0;
        String description = "订单支付：" + getTranslatedOrderType(orderType);

        // 生成押金缴纳记录（收入）
        if (depositAmount != null && depositAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
            result += createExpenseRecord(elderId, accountId, "deposit", "income",
                depositAmount, description + "-押金缴纳", orderId, "order_info", balanceBefore, balanceAfter);
        }

        // 生成服务费预存记录（收入）
        if (serviceAmount != null && serviceAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
            result += createExpenseRecord(elderId, accountId, "service", "income",
                serviceAmount, description + "-服务费预存", orderId, "order_info", balanceBefore, balanceAfter);
        }

        // 生成会员费缴纳记录（收入）
        if (memberAmount != null && memberAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
            result += createExpenseRecord(elderId, accountId, "member", "income",
                memberAmount, description + "-会员费缴纳", orderId, "order_info", balanceBefore, balanceAfter);
        }

        // 生成其他费用记录（收入）
        if (otherAmount != null && otherAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
            result += createExpenseRecord(elderId, accountId, "other", "income",
                otherAmount, description + "-其他费用", orderId, "order_info", balanceBefore, balanceAfter);
        }

        // 如果是入驻订单，立即扣除首月服务费（支出）
        if ("1".equals(orderType) && serviceAmount != null && serviceAmount.compareTo(java.math.BigDecimal.ZERO) > 0) {
            // 计算首月服务费：床位费 + 护理费
            BigDecimal firstMonthServiceFee = calculateFirstMonthServiceFee(orderId);
            if (firstMonthServiceFee.compareTo(java.math.BigDecimal.ZERO) > 0) {
                result += createExpenseRecord(elderId, accountId, "service", "expense",
                    firstMonthServiceFee, description + "-首月服务费扣除", orderId, "order_info", balanceAfter, balanceAfter.subtract(firstMonthServiceFee));
            }
        }

        return result;
    }

    /**
     * 获取翻译后的订单类型
     *
     * @param orderType 订单类型代码
     * @return 翻译后的订单类型
     */
    private String getTranslatedOrderType(String orderType) {
        switch (orderType) {
            case "1":
                return "入驻订单";
            case "2":
                return "续费订单";
            case "3":
                return "服务购买";
            default:
                return "其他订单";
        }
    }

    /**
     * 计算首月服务费
     * 根据订单明细中的床位费和护理费计算首月费用
     *
     * @param orderId 订单ID
     * @return 首月服务费金额
     */
    private java.math.BigDecimal calculateFirstMonthServiceFee(Long orderId) {
        try {
            List<OrderItem> orderItems = orderItemMapper.selectOrderItemsByOrderId(orderId);
            java.math.BigDecimal firstMonthFee = java.math.BigDecimal.ZERO;

            if (orderItems != null && !orderItems.isEmpty()) {
                for (OrderItem item : orderItems) {
                    String itemType = item.getItemType();
                    java.math.BigDecimal totalAmount = item.getTotalAmount() != null ? item.getTotalAmount() : java.math.BigDecimal.ZERO;
                    Integer quantity = item.getQuantity() != null ? item.getQuantity().intValue() : 1;

                    if ("bed_fee".equals(itemType) || "care_fee".equals(itemType)) {
                        // 计算单月费用：总金额 / 数量（月份数）
                        if (quantity > 0) {
                            java.math.BigDecimal monthlyFee = totalAmount.divide(new java.math.BigDecimal(quantity), 2, java.math.BigDecimal.ROUND_HALF_UP);
                            firstMonthFee = firstMonthFee.add(monthlyFee);
                        }
                    }
                }
            }

            return firstMonthFee;
        } catch (Exception e) {
            // 计算失败时返回0，避免影响主流程
            System.err.println("计算首月服务费异常：" + e.getMessage());
            return java.math.BigDecimal.ZERO;
        }
    }
}