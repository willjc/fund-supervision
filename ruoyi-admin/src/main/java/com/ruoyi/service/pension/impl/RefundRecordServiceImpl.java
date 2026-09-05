package com.ruoyi.service.pension.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.mapper.pension.AccountInfoMapper;
import com.ruoyi.mapper.pension.RefundRecordMapper;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.RefundRecord;
import com.ruoyi.service.pension.IExpenseRecordService;
import com.ruoyi.service.pension.IRefundRecordService;
import com.ruoyi.service.pension.ISupervisionAccountLogService;

/**
 * 退款记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@Service
public class RefundRecordServiceImpl implements IRefundRecordService
{
    @org.springframework.beans.factory.annotation.Value("${bank.integration.mode:disabled}")
    private String integrationMode;
    @Autowired
    private RefundRecordMapper refundRecordMapper;

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    @Autowired
    private IExpenseRecordService expenseRecordService;

    @Autowired
    private ISupervisionAccountLogService supervisionAccountLogService;

    /**
     * 查询退款记录
     *
     * @param refundId 退款记录主键
     * @return 退款记录
     */
    @Override
    public RefundRecord selectRefundRecordByRefundId(Long refundId)
    {
        return refundRecordMapper.selectRefundRecordByRefundId(refundId);
    }

    /**
     * 查询退款记录列表
     *
     * @param refundRecord 退款记录
     * @return 退款记录
     */
    @Override
    public List<RefundRecord> selectRefundRecordList(RefundRecord refundRecord)
    {
        return refundRecordMapper.selectRefundRecordList(refundRecord);
    }

    /**
     * 新增退款记录
     *
     * @param refundRecord 退款记录
     * @return 结果
     */
    @Override
    public int insertRefundRecord(RefundRecord refundRecord)
    {
        refundRecord.setCreateTime(DateUtils.getNowDate());
        return refundRecordMapper.insertRefundRecord(refundRecord);
    }

    /**
     * 修改退款记录
     *
     * @param refundRecord 退款记录
     * @return 结果
     */
    @Override
    public int updateRefundRecord(RefundRecord refundRecord)
    {
        refundRecord.setUpdateTime(DateUtils.getNowDate());
        return refundRecordMapper.updateRefundRecord(refundRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approveRefund(Long refundId, String approver, Long currentUserId)
    {
        if ("zzbank".equals(integrationMode))
        {
            throw new ServiceException("原路退款尚未接通，不能扣账或标记已退款；申请保留待处理");
        }
        RefundRecord refundRecord = refundRecordMapper.selectRefundRecordForUpdate(refundId, currentUserId);
        if (refundRecord == null)
        {
            throw new ServiceException("退款记录不存在或无权操作");
        }
        if (!"0".equals(refundRecord.getRefundStatus()))
        {
            throw new ServiceException("只能审批待处理状态的退款");
        }

        BigDecimal serviceAmount = amountOrZero(refundRecord.getServiceRefundAmount());
        BigDecimal depositAmount = amountOrZero(refundRecord.getDepositRefundAmount());
        BigDecimal memberAmount = amountOrZero(refundRecord.getMemberRefundAmount());
        BigDecimal totalAmount = amountOrZero(refundRecord.getRefundAmount());
        BigDecimal componentTotal = serviceAmount.add(depositAmount).add(memberAmount);

        if (serviceAmount.signum() < 0 || depositAmount.signum() < 0 || memberAmount.signum() < 0
                || totalAmount.signum() <= 0)
        {
            throw new ServiceException("退款金额不合法");
        }
        if (totalAmount.compareTo(componentTotal) != 0)
        {
            throw new ServiceException("退款总金额与分类金额不一致");
        }

        AccountInfo account = accountInfoMapper.selectAccountInfoForUpdate(
                refundRecord.getElderId(), refundRecord.getInstitutionId());
        if (account == null)
        {
            throw new ServiceException("未找到对应的老人账户信息");
        }
        if (!"1".equals(account.getAccountStatus()))
        {
            throw new ServiceException("老人账户不是正常状态");
        }

        BigDecimal serviceBalance = amountOrZero(account.getServiceBalance());
        BigDecimal depositBalance = amountOrZero(account.getDepositBalance());
        BigDecimal memberBalance = amountOrZero(account.getMemberBalance());
        BigDecimal totalBalance = amountOrZero(account.getTotalBalance());
        ensureSufficientBalance("服务费", serviceBalance, serviceAmount);
        ensureSufficientBalance("押金", depositBalance, depositAmount);
        ensureSufficientBalance("会员费", memberBalance, memberAmount);
        ensureSufficientBalance("账户总", totalBalance, totalAmount);

        BigDecimal newServiceBalance = serviceBalance.subtract(serviceAmount);
        BigDecimal newDepositBalance = depositBalance.subtract(depositAmount);
        BigDecimal newMemberBalance = memberBalance.subtract(memberAmount);
        BigDecimal newTotalBalance = totalBalance.subtract(totalAmount);

        int accountUpdated = accountInfoMapper.updateAccountBalance(account.getAccountId(), newTotalBalance,
                newServiceBalance, newDepositBalance, newMemberBalance);
        if (accountUpdated != 1)
        {
            throw new ServiceException("更新账户余额失败");
        }

        createExpense(refundRecord, account, "service", "服务费", serviceAmount,
                totalBalance, newTotalBalance);
        createExpense(refundRecord, account, "deposit", "押金", depositAmount,
                totalBalance, newTotalBalance);
        createExpense(refundRecord, account, "member", "会员费", memberAmount,
                totalBalance, newTotalBalance);

        supervisionAccountLogService.recordTransferOut(refundRecord.getInstitutionId(), refundId,
                totalAmount, "退款划拨-" + refundRecord.getRefundNo(), "基本账户");

        Date now = DateUtils.getNowDate();
        refundRecord.setRefundStatus("1");
        refundRecord.setRefundTime(now);
        refundRecord.setApprover(approver);
        refundRecord.setApproveTime(now);
        refundRecord.setUpdateBy(approver);
        refundRecord.setUpdateTime(now);
        int refundUpdated = refundRecordMapper.updateRefundRecord(refundRecord);
        if (refundUpdated != 1)
        {
            throw new ServiceException("更新退款状态失败");
        }
        return refundUpdated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int rejectRefund(Long refundId, String approver, String approveRemark, Long currentUserId)
    {
        RefundRecord refundRecord = refundRecordMapper.selectRefundRecordForUpdate(refundId, currentUserId);
        if (refundRecord == null)
        {
            throw new ServiceException("退款记录不存在或无权操作");
        }
        if (!"0".equals(refundRecord.getRefundStatus()))
        {
            throw new ServiceException("只能审批待处理状态的退款");
        }

        refundRecord.setRefundStatus("2");
        refundRecord.setApprover(approver);
        refundRecord.setApproveTime(DateUtils.getNowDate());
        refundRecord.setApproveRemark(approveRemark);
        refundRecord.setUpdateBy(approver);
        refundRecord.setUpdateTime(DateUtils.getNowDate());
        int updated = refundRecordMapper.updateRefundRecord(refundRecord);
        if (updated != 1)
        {
            throw new ServiceException("更新退款状态失败");
        }
        return updated;
    }

    private void createExpense(RefundRecord refundRecord, AccountInfo account, String expenseType,
            String expenseName, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter)
    {
        if (amount.signum() <= 0)
        {
            return;
        }
        int inserted = expenseRecordService.createExpenseRecord(refundRecord.getElderId(),
                account.getAccountId(), expenseType, "expense", amount,
                expenseName + "退款-" + refundRecord.getRefundNo(), refundRecord.getRefundId(),
                "refund", balanceBefore, balanceAfter);
        if (inserted <= 0)
        {
            throw new ServiceException("记录" + expenseName + "退款流水失败");
        }
    }

    private void ensureSufficientBalance(String balanceName, BigDecimal balance, BigDecimal amount)
    {
        if (balance.compareTo(amount) < 0)
        {
            throw new ServiceException(balanceName + "余额不足，当前余额：" + balance + "元");
        }
    }

    private BigDecimal amountOrZero(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    /**
     * 批量删除退款记录
     *
     * @param refundIds 需要删除的退款记录主键
     * @return 结果
     */
    @Override
    public int deleteRefundRecordByRefundIds(Long[] refundIds)
    {
        return refundRecordMapper.deleteRefundRecordByRefundIds(refundIds);
    }

    /**
     * 删除退款记录信息
     *
     * @param refundId 退款记录主键
     * @return 结果
     */
    @Override
    public int deleteRefundRecordByRefundId(Long refundId)
    {
        return refundRecordMapper.deleteRefundRecordByRefundId(refundId);
    }

    /**
     * 按订单ID查询退款记录
     *
     * @param orderId 订单ID
     * @return 退款记录集合
     */
    @Override
    public List<RefundRecord> selectRefundRecordByOrderId(Long orderId)
    {
        return refundRecordMapper.selectRefundRecordByOrderId(orderId);
    }

    /**
     * 按老人ID查询退款记录
     *
     * @param elderId 老人ID
     * @return 退款记录集合
     */
    @Override
    public List<RefundRecord> selectRefundRecordByElderId(Long elderId)
    {
        return refundRecordMapper.selectRefundRecordByElderId(elderId);
    }

    /**
     * 按机构ID查询退款记录
     *
     * @param institutionId 机构ID
     * @return 退款记录集合
     */
    @Override
    public List<RefundRecord> selectRefundRecordByInstitutionId(Long institutionId)
    {
        return refundRecordMapper.selectRefundRecordByInstitutionId(institutionId);
    }
}
