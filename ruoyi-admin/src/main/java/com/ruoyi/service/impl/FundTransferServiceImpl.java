package com.ruoyi.service.impl;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.mapper.pension.FundTransferMapper;
import com.ruoyi.mapper.pension.AccountInfoMapper;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.TransferRuleConfig;
import com.ruoyi.service.pension.IFundTransferService;
import com.ruoyi.service.pension.ITransferRuleConfigService;

/**
 * 资金划拨记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@Service
public class FundTransferServiceImpl implements IFundTransferService
{

    @Autowired
    private FundTransferMapper fundTransferMapper;

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    @Autowired
    private ITransferRuleConfigService transferRuleConfigService;

    @Autowired
    private com.ruoyi.service.bank.impl.BankPayoutService bankPayoutService;
    @Autowired
    private com.ruoyi.mapper.bank.BankSettlementMapper settlementMapper;

    /**
     * 查询资金划拨记录
     *
     * @param transferId 资金划拨记录主键
     * @return 资金划拨记录
     */
    @Override
    public FundTransfer selectFundTransferByTransferId(Long transferId)
    {
        FundTransfer transfer = fundTransferMapper.selectFundTransferByTransferId(transferId);
        if (transfer != null) { bankPayoutService.checkScope(transfer.getInstitutionId()); }
        return transfer;
    }

    /**
     * 查询资金划拨记录列表
     *
     * @param fundTransfer 资金划拨记录
     * @return 资金划拨记录
     */
    @Override
    public List<FundTransfer> selectFundTransferList(FundTransfer fundTransfer)
    {
        Long user = com.ruoyi.common.utils.SecurityUtils.getUserId();
        fundTransfer.setCurrentUserId(com.ruoyi.common.utils.SecurityUtils.isAdmin(user) ? null : user);
        return fundTransferMapper.selectFundTransferList(fundTransfer);
    }

    /**
     * 根据机构ID查询划拨记录
     *
     * @param institutionId 机构ID
     * @return 资金划拨记录集合
     */
    @Override
    public List<FundTransfer> selectFundTransferByInstitutionId(Long institutionId)
    {
        bankPayoutService.checkScope(institutionId);
        return fundTransferMapper.selectFundTransferByInstitutionId(institutionId);
    }

    /**
     * 新增资金划拨记录
     *
     * @param fundTransfer 资金划拨记录
     * @return 结果
     */
    @Override
    @Transactional
    public int insertFundTransfer(FundTransfer fundTransfer)
    {
        fundTransfer.setCreateTime(DateUtils.getNowDate());
        return fundTransferMapper.insertFundTransfer(fundTransfer);
    }

    /**
     * 修改资金划拨记录
     *
     * @param fundTransfer 资金划拨记录
     * @return 结果
     */
    @Override
    @Transactional
    public int updateFundTransfer(FundTransfer fundTransfer)
    {
        FundTransfer existing = fundTransferMapper.selectFundTransferByTransferId(fundTransfer.getTransferId());
        if (existing != null && (Integer.valueOf(1).equals(existing.getBankEligible()) || existing.getBankTransactionId() != null))
        { throw new com.ruoyi.common.exception.ServiceException("银行拨付单禁止通用编辑，请使用审批或结果查询"); }
        fundTransfer.setUpdateTime(DateUtils.getNowDate());
        return fundTransferMapper.updateFundTransfer(fundTransfer);
    }

    /**
     * 批量删除资金划拨记录
     *
     * @param transferIds 需要删除的资金划拨记录主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteFundTransferByTransferIds(Long[] transferIds)
    {
        for (Long id : transferIds) { requireDeletable(id); }
        return fundTransferMapper.deleteFundTransferByTransferIds(transferIds);
    }

    /**
     * 删除资金划拨记录信息
     *
     * @param transferId 资金划拨记录主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteFundTransferByTransferId(Long transferId)
    {
        requireDeletable(transferId);
        return fundTransferMapper.deleteFundTransferByTransferId(transferId);
    }

    private void requireDeletable(Long id)
    {
        FundTransfer transfer = settlementMapper.lockTransfer(id);
        if (transfer == null) { throw new com.ruoyi.common.exception.ServiceException("拨付单不存在"); }
        bankPayoutService.checkScope(transfer.getInstitutionId());
        if (Integer.valueOf(1).equals(transfer.getBankEligible()) || transfer.getBankTransactionId() != null)
        { throw new com.ruoyi.common.exception.ServiceException("银行拨付单禁止删除，须保留资金审计记录"); }
    }

    /**
     * 生成月度自动划拨
     *
     * @param institutionId 机构ID
     * @param transferPeriod 划拨期间
     * @return 划拨记录
     */
    @Override
    @Transactional
    public FundTransfer generateMonthlyTransfer(Long institutionId, String transferPeriod)
    {
        throw new com.ruoyi.common.exception.ServiceException("请使用支付订单生成的逐老人拨付明细，不能按固定金额汇总生成");
    }

    /**
     * 审批资金划拨
     *
     * @param transferId 划拨ID
     * @param approveUser 审批人
     * @param approveResult 审批结果（1通过 2拒绝）
     * @param approveRemark 审批意见
     * @return 结果
     */
    @Override
    @Transactional
    public int approveFundTransfer(Long transferId, String approveUser, String approveResult, String approveRemark)
    {
        return bankPayoutService.approve(transferId, approveUser, "1".equals(approveResult), approveRemark);
    }

    /**
     * 执行资金划拨
     *
     * @param transferId 划拨ID
     * @param executeUser 执行人
     * @return 结果
     */
    @Override
    @Transactional
    public int executeFundTransfer(Long transferId, String executeUser)
    {
        return bankPayoutService.queue(transferId, executeUser);
    }

    /**
     * 查询待处理的划拨记录
     *
     * @return 待处理的划拨记录集合
     */
    @Override
    public List<FundTransfer> selectPendingTransfers()
    {
        return fundTransferMapper.selectPendingTransfers();
    }

    /**
     * 批量执行自动划拨（定时任务调用）
     *
     * @return 执行结果
     */
    @Override
    @Transactional
    public Map<String, Object> executeAutoTransfer()
    {
        return executeTransferByRule(null, null, null);
    }

    /**
     * 手动发起划拨申请
     *
     * @param institutionId 机构ID
     * @param transferAmount 划拨金额
     * @param transferType 划拨类型
     * @param remark 申请原因
     * @return 结果
     */
    @Override
    @Transactional
    public int applyManualTransfer(Long institutionId, BigDecimal transferAmount, String transferType, String remark)
    {
        throw new com.ruoyi.common.exception.ServiceException("请通过资金拨付申请选择老人和待拨付明细");
    }

    /**
     * 统计划拨金额
     *
     * @param institutionId 机构ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    @Override
    public Map<String, Object> selectTransferStatistics(Long institutionId, Date startDate, Date endDate)
    {
        return fundTransferMapper.selectTransferStatistics(institutionId, startDate, endDate);
    }

    /**
     * 查询余额不足的老人账户
     *
     * @param months 不足月数
     * @return 账户列表
     */
    @Override
    public List<AccountInfo> selectLowBalanceAccounts(Integer months)
    {
        return accountInfoMapper.selectLowBalanceAccounts(months);
    }

    /**
     * 根据入住单生成划拨单
     *
     * @param checkInId 入住单ID
     * @param orderId 订单ID
     * @param institutionId 机构ID
     * @param elderId 老人ID
     * @param monthCount 月数
     * @param startDate 起始日期
     */
    @Override
    @Transactional
    public void generateTransferOrderFromCheckIn(Long checkInId, Long orderId, Long institutionId,
                                                  Long elderId, Integer monthCount, Date startDate)
    {
        // 查询老人账户获取月费用
        AccountInfo account = accountInfoMapper.selectAccountInfoByElderId(elderId);
        if (account == null) {
            // 如果账户不存在，先创建账户
            account = new AccountInfo();
            account.setElderId(elderId);
            account.setInstitutionId(institutionId);
            account.setAccountNo("ACC" + System.currentTimeMillis());
            account.setAccountName("账户-" + elderId);
            account.setAccountStatus("1");
            account.setTotalBalance(BigDecimal.ZERO);
            account.setServiceBalance(BigDecimal.ZERO);
            account.setDepositBalance(BigDecimal.ZERO);
            account.setMemberBalance(BigDecimal.ZERO);
            account.setCreateTime(DateUtils.getNowDate());
            accountInfoMapper.insertAccountInfo(account);
        }

        // 计算月费用（从账户余额推算，或使用默认值）
        // 这里简化处理，实际应该从入住单获取月费用
        BigDecimal monthlyFee = new BigDecimal("3000.00"); // 默认月费用，可以从配置获取

        generateMonthlyTransfersForOrder(orderId, institutionId, elderId, monthCount, startDate, monthlyFee, false);
    }

    /**
     * 根据老人ID和月数生成划拨单
     *
     * @param orderId 订单ID
     * @param institutionId 机构ID
     * @param elderId 老人ID
     * @param monthCount 月数
     * @param startDate 起始日期
     * @param monthlyFee 月费用
     * @param startFromCurrentMonth 是否从当月开始生成（true-从当月开始，false-从次月开始）
     */
    @Override
    @Transactional
    public void generateMonthlyTransfersForOrder(Long orderId, Long institutionId, Long elderId,
                                                  Integer monthCount, Date startDate, BigDecimal monthlyFee,
                                                  boolean startFromCurrentMonth)
    {
        if (monthCount == null || monthCount <= 0) {
            monthCount = 1;
        }

        // 获取有效的划拨规则配置
        TransferRuleConfig ruleConfig = getActiveTransferRule(institutionId);
        if (ruleConfig == null) {
            // 没有配置规则，使用默认值：每月1号划拨
            ruleConfig = new TransferRuleConfig();
            ruleConfig.setTransferCycle("monthly");
            ruleConfig.setTransferDay(1);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        // 根据划拨周期计算起始月份
        int cycleMonths = getCycleMonths(ruleConfig.getTransferCycle());
        // 如果不是从当月开始，则跳过首月
        if (!startFromCurrentMonth) {
            cal.add(Calendar.MONTH, cycleMonths);
        }

        // 生成monthCount个月的划拨单
        for (int i = 0; i < monthCount; i++) {
            String billingMonth = sdf.format(cal.getTime());
            String sourceKey = "MONTH:" + orderId + ":" + billingMonth;
            if (settlementMapper.bySource(sourceKey) != null)
            {
                cal.add(Calendar.MONTH, cycleMonths);
                continue;
            }

            // 设置划拨日期（使用规则配置的日期）
            Calendar transferDateCal = Calendar.getInstance();
            transferDateCal.setTime(cal.getTime());
            int transferDay = ruleConfig.getTransferDay() != null ? ruleConfig.getTransferDay() : 1;
            // 确保日期在当月有效范围内
            int maxDay = transferDateCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            transferDay = Math.min(transferDay, maxDay);
            transferDateCal.set(Calendar.DAY_OF_MONTH, transferDay);

            // 生成划拨单号
            String transferNo = "TRF" + System.currentTimeMillis() + i + String.format("%02d", (int)(Math.random() * 100));

            // 创建划拨单
            FundTransfer fundTransfer = new FundTransfer();
            fundTransfer.setInstitutionId(institutionId);
            fundTransfer.setSourceKey(sourceKey);
            fundTransfer.setElderId(elderId);
            fundTransfer.setOrderId(orderId);
            fundTransfer.setTransferNo(transferNo);
            fundTransfer.setTransferType("1"); // 自动划拨
            fundTransfer.setTransferAmount(monthlyFee);
            fundTransfer.setTransferDate(transferDateCal.getTime());
            fundTransfer.setTransferPeriod(billingMonth);
            fundTransfer.setBillingMonth(billingMonth);
            fundTransfer.setElderCount(1);
            fundTransfer.setTransferStatus("0"); // 待处理
            fundTransfer.setIsPaid("0"); // 未划拨
            fundTransfer.setStatus("pending"); // 待划拨
            fundTransfer.setCreateBy("system");
            fundTransfer.setCreateTime(new Date());
            fundTransfer.setRemark("订单支付后自动生成-" + billingMonth);

            fundTransferMapper.insertFundTransfer(fundTransfer);

            // 根据划拨周期增加月份
            cal.add(Calendar.MONTH, cycleMonths);
        }
    }

    /**
     * 获取有效的划拨规则配置
     *
     * @param institutionId 机构ID（保留参数以备将来扩展，当前规则为全局）
     * @return 划拨规则配置
     */
    private TransferRuleConfig getActiveTransferRule(Long institutionId)
    {
        try {
            TransferRuleConfig query = new TransferRuleConfig();
            query.setStatus("0"); // 0-正常
            List<TransferRuleConfig> rules = transferRuleConfigService.selectTransferRuleConfigList(query);
            return rules != null && !rules.isEmpty() ? rules.get(0) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据划拨周期获取月数间隔
     *
     * @param transferCycle 划拨周期
     * @return 月数间隔
     */
    private int getCycleMonths(String transferCycle)
    {
        if ("quarterly".equals(transferCycle)) {
            return 3; // 按季度：3个月
        } else if ("yearly".equals(transferCycle)) {
            return 12; // 按年：12个月
        } else {
            return 1; // 默认按月：1个月
        }
    }

    /**
     * 根据老人ID和支付方式查询已完成的划拨记录（用于H5费用查询）
     *
     * @param elderId 老人ID
     * @param paidMethods ���付方式数组（如：auto, manual, deposit）
     * @return 划拨记录集合
     */
    @Override
    public List<FundTransfer> selectByElderIdAndPaidMethods(Long elderId, String[] paidMethods)
    {
        return fundTransferMapper.selectByElderIdAndPaidMethods(elderId, paidMethods);
    }

    /**
     * 根据账单月份查询待划付的划拨单
     *
     * @param billingMonth 账单月份（格式：2026-02）
     * @return 待划付的划拨单集合
     */
    @Override
    public List<FundTransfer> selectPendingTransfersByBillingMonth(String billingMonth)
    {
        return fundTransferMapper.selectPendingTransfersByBillingMonth(billingMonth);
    }

    /**
     * 批量执行划拨（按划付规则配置）
     *
     * @param billingMonth 账单月份
     * @param transferDay 划付日期
     * @param transferTime 划付时间
     * @return 执行结果
     */
    @Override
    @Transactional
    public Map<String, Object> executeTransferByRule(String billingMonth, Integer transferDay, String transferTime)
    {
        // 旧任务不再修改账务；唯一自动发送入口为 bankSettlementTask.dispatch。
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("message", "由银行到期拨付任务统一处理，旧入口不发送资金");
        result.put("totalCount", 0);
        result.put("successCount", 0);
        result.put("failCount", 0);
        result.put("skippedCount", 0);
        return result;
    }

    /**
     * 批量更新划拨单为已划付状态
     *
     * @param transferIds 划拨单ID列表
     * @param paidTime 划付时间
     * @param transferStatus 划拨状态（1成功 2失败）
     * @param failureReason 失败原因
     * @return 更新数量
     */
    @Override
    public int batchUpdatePaidStatus(List<Long> transferIds, Date paidTime, String transferStatus, String failureReason)
    {
        throw new com.ruoyi.common.exception.ServiceException("禁止直接修改拨付完成状态，须由银行结果记账");
    }
}
