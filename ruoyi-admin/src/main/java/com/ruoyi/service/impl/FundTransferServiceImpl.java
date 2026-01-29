package com.ruoyi.service.impl;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.ruoyi.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.ruoyi.service.pension.ISupervisionAccountLogService;

/**
 * 资金划拨记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@Service
public class FundTransferServiceImpl implements IFundTransferService
{
    private static final Logger log = LoggerFactory.getLogger(FundTransferServiceImpl.class);

    @Autowired
    private FundTransferMapper fundTransferMapper;

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    @Autowired
    private ITransferRuleConfigService transferRuleConfigService;

    @Autowired
    private ISupervisionAccountLogService supervisionAccountLogService;

    /**
     * 查询资金划拨记录
     *
     * @param transferId 资金划拨记录主键
     * @return 资金划拨记录
     */
    @Override
    public FundTransfer selectFundTransferByTransferId(Long transferId)
    {
        return fundTransferMapper.selectFundTransferByTransferId(transferId);
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
        return fundTransferMapper.deleteFundTransferByTransferId(transferId);
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
        // 生成划拨单号
        String transferNo = "TRF" + System.currentTimeMillis() + String.format("%03d", (int)(Math.random() * 1000));

        // 查询该机构的所有正常账户
        List<AccountInfo> accounts = accountInfoMapper.selectAccountInfoByInstitutionId(institutionId);

        // 计算划拨金额（这里简化处理，实际应该根据每个老人的月费用计算）
        BigDecimal totalAmount = BigDecimal.ZERO;
        int elderCount = 0;

        for (AccountInfo account : accounts) {
            if ("1".equals(account.getAccountStatus()) && account.getServiceBalance().compareTo(BigDecimal.ZERO) > 0) {
                // 这里应该根据老人的实际月费用计算，简化处理：假设每人每月扣除3000元
                BigDecimal monthlyFee = new BigDecimal("3000.00");
                if (account.getServiceBalance().compareTo(monthlyFee) >= 0) {
                    totalAmount = totalAmount.add(monthlyFee);
                    elderCount++;
                }
            }
        }

        FundTransfer fundTransfer = new FundTransfer();
        fundTransfer.setInstitutionId(institutionId);
        fundTransfer.setTransferNo(transferNo);
        fundTransfer.setTransferType("1"); // 自动划拨
        fundTransfer.setTransferAmount(totalAmount);
        fundTransfer.setTransferDate(new Date());
        fundTransfer.setTransferPeriod(transferPeriod);
        fundTransfer.setElderCount(elderCount);
        fundTransfer.setTransferStatus("0"); // 待处理
        fundTransfer.setCreateBy("system");
        fundTransfer.setCreateTime(new Date());
        fundTransfer.setRemark("系统自动生成的月度划拨");

        fundTransferMapper.insertFundTransfer(fundTransfer);
        return fundTransfer;
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
        String transferStatus = "1".equals(approveResult) ? "1" : "2";
        String failureReason = "1".equals(approveResult) ? null : approveRemark;

        return fundTransferMapper.approveFundTransfer(transferId, transferStatus, approveUser, new Date(), failureReason);
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
        // 查询划拨记录
        FundTransfer transfer = fundTransferMapper.selectFundTransferByTransferId(transferId);
        if (transfer == null || !"1".equals(transfer.getTransferStatus())) {
            return 0;
        }

        // 生成银行订单号（模拟）
        String bankOrderNo = "BANK" + System.currentTimeMillis();

        // 执行扣费操作
        List<AccountInfo> accounts = accountInfoMapper.selectAccountInfoByInstitutionId(transfer.getInstitutionId());

        for (AccountInfo account : accounts) {
            if ("1".equals(account.getAccountStatus()) && account.getServiceBalance().compareTo(BigDecimal.ZERO) > 0) {
                // 扣除月费（这里简化处理）
                BigDecimal monthlyFee = new BigDecimal("3000.00");
                if (account.getServiceBalance().compareTo(monthlyFee) >= 0) {
                    // 更新账户余额
                    accountInfoMapper.updateAccountBalance(
                        account.getAccountId(),
                        account.getTotalBalance().subtract(monthlyFee),
                        account.getServiceBalance().subtract(monthlyFee),
                        account.getDepositBalance(),
                        account.getMemberBalance()
                    );
                }
            }
        }

        // 更新划拨状态为成功
        return fundTransferMapper.executeFundTransfer(transferId, "1", executeUser, new Date(), bankOrderNo);
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
        Map<String, Object> result = new java.util.HashMap<>();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            String currentPeriod = sdf.format(new Date());

            // 获取所有需要划拨的机构（这里简化处理，实际应该查询所有已入驻的机构）
            // 这里假设只有一个机构，ID为1
            Long institutionId = 1L;

            // 生成月度划拨
            FundTransfer transfer = generateMonthlyTransfer(institutionId, currentPeriod);

            if (transfer.getTransferAmount().compareTo(BigDecimal.ZERO) > 0) {
                // 自动审批
                approveFundTransfer(transfer.getTransferId(), "system", "1", "系统自动审批");

                // 自动执行
                executeFundTransfer(transfer.getTransferId(), "system");

                result.put("success", true);
                result.put("message", "自动划拨执行成功");
                result.put("transferId", transfer.getTransferId());
                result.put("transferAmount", transfer.getTransferAmount());
                result.put("elderCount", transfer.getElderCount());
            } else {
                result.put("success", true);
                result.put("message", "本期无需执行划拨");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "自动划拨执行失败：" + e.getMessage());
        }

        return result;
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
        String transferNo = "TRF" + System.currentTimeMillis() + String.format("%03d", (int)(Math.random() * 1000));

        FundTransfer fundTransfer = new FundTransfer();
        fundTransfer.setInstitutionId(institutionId);
        fundTransfer.setTransferNo(transferNo);
        fundTransfer.setTransferType(transferType);
        fundTransfer.setTransferAmount(transferAmount);
        fundTransfer.setTransferDate(new Date());
        fundTransfer.setTransferPeriod(new SimpleDateFormat("yyyy-MM").format(new Date()));
        fundTransfer.setElderCount(0); // 手动申请时先设为0
        fundTransfer.setTransferStatus("0"); // 待处理
        fundTransfer.setCreateBy("admin");
        fundTransfer.setCreateTime(new Date());
        fundTransfer.setRemark(remark);

        return fundTransferMapper.insertFundTransfer(fundTransfer);
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

        generateMonthlyTransfersForOrder(orderId, institutionId, elderId, monthCount, startDate, monthlyFee);
    }

    /**
     * 根据老人ID和月数生成划拨单（从次月开始，按划拨规则配置）
     *
     * @param orderId 订单ID
     * @param institutionId 机构ID
     * @param elderId 老人ID
     * @param monthCount 月数
     * @param startDate 起始日期
     * @param monthlyFee 月费用
     */
    @Override
    @Transactional
    public void generateMonthlyTransfersForOrder(Long orderId, Long institutionId, Long elderId,
                                                  Integer monthCount, Date startDate, BigDecimal monthlyFee)
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

        // 根据划拨周期计算首月划拨后的下一个划拨月份
        int cycleMonths = getCycleMonths(ruleConfig.getTransferCycle());
        cal.add(Calendar.MONTH, cycleMonths);

        // 生成monthCount个月的划拨单
        for (int i = 0; i < monthCount; i++) {
            String billingMonth = sdf.format(cal.getTime());

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
        Map<String, Object> result = new java.util.HashMap<>();

        try {
            log.info("开始执行按月划拨：账单月份={}, 划付日期={}, 划付时间={}", billingMonth, transferDay, transferTime);

            // 查询该账单月份待划付的划拨单
            List<FundTransfer> pendingTransfers = fundTransferMapper.selectPendingTransfersByBillingMonth(billingMonth);

            if (pendingTransfers == null || pendingTransfers.isEmpty()) {
                result.put("success", true);
                result.put("message", "本期无待划付的划拨单");
                result.put("totalCount", 0);
                result.put("successCount", 0);
                result.put("failCount", 0);
                return result;
            }

            // 收集需要划拨的ID
            List<Long> successIds = new java.util.ArrayList<>();
            List<Long> failIds = new java.util.ArrayList<>();
            Date paidTime = new Date();

            // 按机构分组处理
            Map<Long, List<FundTransfer>> groupByInstitution = new java.util.HashMap<>();
            for (FundTransfer transfer : pendingTransfers) {
                Long institutionId = transfer.getInstitutionId();
                if (!groupByInstitution.containsKey(institutionId)) {
                    groupByInstitution.put(institutionId, new java.util.ArrayList<>());
                }
                groupByInstitution.get(institutionId).add(transfer);
            }

            // 对每个机构的划拨单进行处理
            for (Map.Entry<Long, List<FundTransfer>> entry : groupByInstitution.entrySet()) {
                Long institutionId = entry.getKey();
                List<FundTransfer> transfers = entry.getValue();
                List<Long> transferIds = new java.util.ArrayList<>();

                for (FundTransfer transfer : transfers) {
                    transferIds.add(transfer.getTransferId());
                }

                try {
                    // 批量更新为已划付状态
                    int updated = fundTransferMapper.batchUpdatePaidStatus(
                        transferIds,
                        paidTime,
                        "1", // 成功
                        null
                    );

                    if (updated > 0) {
                        successIds.addAll(transferIds);
                        log.info("机构ID={} 成功划拨 {} 单", institutionId, updated);

                        // 为每个划拨单生成监管账户流水
                        for (FundTransfer transfer : transfers) {
                            try {
                                supervisionAccountLogService.recordTransferOut(
                                    institutionId,
                                    transfer.getTransferId(),
                                    transfer.getTransferAmount(),
                                    "自动划拨-" + billingMonth + "账单-" + transfer.getTransferNo(),
                                    "基本账户"
                                );
                                log.info("生成划拨流水成功：划拨单号={}, 金额={}", transfer.getTransferNo(), transfer.getTransferAmount());
                            } catch (Exception ex) {
                                log.error("生成划拨流水失败：划拨单号={}, 错误={}", transfer.getTransferNo(), ex.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("机构ID={} 划拨失败: {}", institutionId, e.getMessage());
                    failIds.addAll(transferIds);

                    // 记录失败状态
                    try {
                        fundTransferMapper.batchUpdatePaidStatus(
                            transferIds,
                            paidTime,
                            "2", // 失败
                            "系统异常: " + e.getMessage()
                        );
                    } catch (Exception ex) {
                        log.error("记录失败状态时发生异常: {}", ex.getMessage());
                    }
                }
            }

            result.put("success", true);
            result.put("message", "划拨处理完成");
            result.put("totalCount", pendingTransfers.size());
            result.put("successCount", successIds.size());
            result.put("failCount", failIds.size());

            log.info("按月划拨执行完成：总单数={}, 成功={}, 失败={}",
                pendingTransfers.size(), successIds.size(), failIds.size());

        } catch (Exception e) {
            log.error("按月划拨执行异常", e);
            result.put("success", false);
            result.put("message", "划拨执行异常: " + e.getMessage());
        }

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
        return fundTransferMapper.batchUpdatePaidStatus(transferIds, paidTime, transferStatus, failureReason);
    }
}
