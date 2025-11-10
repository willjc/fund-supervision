package com.ruoyi.service.impl;

import java.util.List;
import java.util.Date;
import java.util.Map;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.mapper.pension.FundTransferMapper;
import com.ruoyi.mapper.pension.AccountInfoMapper;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.service.pension.IFundTransferService;
import com.ruoyi.common.utils.StringUtils;

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
}