package com.ruoyi.service.impl;

import java.util.List;
import java.util.Date;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.mapper.pension.AccountInfoMapper;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.service.pension.IAccountInfoService;
import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

/**
 * 老人账户信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-01-25
 */
@Service
public class AccountInfoServiceImpl implements IAccountInfoService
{
    @Autowired
    private AccountInfoMapper accountInfoMapper;

    /**
     * 查询老人账户信息
     *
     * @param accountId 老人账户信息主键
     * @return 老人账户信息
     */
    @Override
    public AccountInfo selectAccountInfoByAccountId(Long accountId)
    {
        return accountInfoMapper.selectAccountInfoByAccountId(accountId);
    }

    /**
     * 查询老人账户信息列表
     *
     * @param accountInfo 老人账户信息
     * @return 老人账户信息
     */
    @Override
    public List<AccountInfo> selectAccountInfoList(AccountInfo accountInfo)
    {
        return accountInfoMapper.selectAccountInfoList(accountInfo);
    }

    /**
     * 新增老人账户信息
     *
     * @param accountInfo 老人账户信息
     * @return 结果
     */
    @Override
    public int insertAccountInfo(AccountInfo accountInfo)
    {
        accountInfo.setCreateTime(DateUtils.getNowDate());
        return accountInfoMapper.insertAccountInfo(accountInfo);
    }

    /**
     * 修改老人账户信息
     *
     * @param accountInfo 老人账户信息
     * @return 结果
     */
    @Override
    public int updateAccountInfo(AccountInfo accountInfo)
    {
        accountInfo.setUpdateTime(DateUtils.getNowDate());
        return accountInfoMapper.updateAccountInfo(accountInfo);
    }

    /**
     * 批量删除老人账户信息
     *
     * @param accountIds 需要删除的老人账户信息主键
     * @return 结果
     */
    @Override
    public int deleteAccountInfoByAccountIds(Long[] accountIds)
    {
        return accountInfoMapper.deleteAccountInfoByAccountIds(accountIds);
    }

    /**
     * 删除老人账户信息信息
     *
     * @param accountId 老人账户信息主键
     * @return 结果
     */
    @Override
    public int deleteAccountInfoByAccountId(Long accountId)
    {
        return accountInfoMapper.deleteAccountInfoByAccountId(accountId);
    }

    /**
     * 根据老人ID查询账户信息
     *
     * @param elderId 老人ID
     * @return 老人账户信息
     */
    @Override
    public AccountInfo selectAccountInfoByElderId(Long elderId) {
        return accountInfoMapper.selectAccountInfoByElderId(elderId);
    }

    /**
     * 根据机构ID查询账户列表
     *
     * @param institutionId 机构ID
     * @return 账户列表
     */
    @Override
    public List<AccountInfo> selectAccountInfoByInstitutionId(Long institutionId) {
        return accountInfoMapper.selectAccountInfoByInstitutionId(institutionId);
    }

    /**
     * 创建老人账户（入住时调用）
     *
     * @param elderId 老人ID
     * @param institutionId 机构ID
     * @param initialBalance 初始余额
     * @return 老人账户信息
     */
    @Override
    @Transactional
    public AccountInfo createAccountInfo(Long elderId, Long institutionId, BigDecimal initialBalance) {
        // 生成账户编号
        String accountNo = "ACC" + System.currentTimeMillis() + String.format("%03d", (int)(Math.random() * 1000));

        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setElderId(elderId);
        accountInfo.setInstitutionId(institutionId);
        accountInfo.setAccountNo(accountNo);
        accountInfo.setAccountName("账户-" + elderId);
        accountInfo.setAccountStatus("1"); // 正常状态
        accountInfo.setTotalBalance(initialBalance);
        accountInfo.setServiceBalance(initialBalance);
        accountInfo.setDepositBalance(BigDecimal.ZERO);
        accountInfo.setMemberBalance(BigDecimal.ZERO);
        accountInfo.setRemark("老人入住时自动创建");
        accountInfo.setCreateTime(new Date());

        accountInfoMapper.insertAccountInfo(accountInfo);
        return accountInfo;
    }

    /**
     * 更新账户余额（交易时调用）
     *
     * @param accountId 账户ID
     * @param serviceBalance 服务费余额变动
     * @param depositBalance 押金余额变动
     * @param memberBalance 会员费余额变动
     * @param isIncrease 是否为增加
     * @return 结果
     */
    @Override
    @Transactional
    public int updateAccountBalance(Long accountId, BigDecimal serviceBalance,
                                   BigDecimal depositBalance, BigDecimal memberBalance, boolean isIncrease) {
        // 先获取当前账户信息
        AccountInfo currentAccount = accountInfoMapper.selectAccountInfoByAccountId(accountId);
        if (currentAccount == null) {
            return 0;
        }

        // 计算新余额
        BigDecimal newServiceBalance = isIncrease ?
            currentAccount.getServiceBalance().add(serviceBalance) :
            currentAccount.getServiceBalance().subtract(serviceBalance);
        BigDecimal newDepositBalance = isIncrease ?
            currentAccount.getDepositBalance().add(depositBalance) :
            currentAccount.getDepositBalance().subtract(depositBalance);
        BigDecimal newMemberBalance = isIncrease ?
            currentAccount.getMemberBalance().add(memberBalance) :
            currentAccount.getMemberBalance().subtract(memberBalance);

        // 检查余额不能为负数
        if (newServiceBalance.compareTo(BigDecimal.ZERO) < 0 ||
            newDepositBalance.compareTo(BigDecimal.ZERO) < 0 ||
            newMemberBalance.compareTo(BigDecimal.ZERO) < 0) {
            return 0;
        }

        // 更新余额
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(accountId);
        accountInfo.setServiceBalance(newServiceBalance);
        accountInfo.setDepositBalance(newDepositBalance);
        accountInfo.setMemberBalance(newMemberBalance);
        accountInfo.setTotalBalance(newServiceBalance.add(newDepositBalance).add(newMemberBalance));
        accountInfo.setUpdateTime(new Date());

        return accountInfoMapper.updateAccountInfo(accountInfo);
    }

    /**
     * 统计机构账户余额
     *
     * @param institutionId 机构ID
     * @return 余额统计
     */
    @Override
    public Map<String, Object> selectAccountBalanceSummary(Long institutionId) {
        Map<String, Object> summary = new HashMap<>();
        try {
            List<AccountInfo> accounts = accountInfoMapper.selectAccountInfoByInstitutionId(institutionId);

            BigDecimal totalBalance = BigDecimal.ZERO;
            BigDecimal totalServiceBalance = BigDecimal.ZERO;
            BigDecimal totalDepositBalance = BigDecimal.ZERO;
            BigDecimal totalMemberBalance = BigDecimal.ZERO;

            for (AccountInfo account : accounts) {
                if (account.getTotalBalance() != null) {
                    totalBalance = totalBalance.add(account.getTotalBalance());
                }
                if (account.getServiceBalance() != null) {
                    totalServiceBalance = totalServiceBalance.add(account.getServiceBalance());
                }
                if (account.getDepositBalance() != null) {
                    totalDepositBalance = totalDepositBalance.add(account.getDepositBalance());
                }
                if (account.getMemberBalance() != null) {
                    totalMemberBalance = totalMemberBalance.add(account.getMemberBalance());
                }
            }

            summary.put("totalBalance", totalBalance);
            summary.put("totalServiceBalance", totalServiceBalance);
            summary.put("totalDepositBalance", totalDepositBalance);
            summary.put("totalMemberBalance", totalMemberBalance);
            summary.put("accountCount", accounts.size());

        } catch (Exception e) {
            summary.put("totalBalance", BigDecimal.ZERO);
            summary.put("totalServiceBalance", BigDecimal.ZERO);
            summary.put("totalDepositBalance", BigDecimal.ZERO);
            summary.put("totalMemberBalance", BigDecimal.ZERO);
            summary.put("accountCount", 0);
        }

        return summary;
    }

    /**
     * 查询余额不足的老人账户
     *
     * @param months 不足月数
     * @return 账户列表
     */
    @Override
    public List<AccountInfo> selectLowBalanceAccounts(Integer months) {
        return accountInfoMapper.selectLowBalanceAccounts(months);
    }

    /**
     * 冻结/解冻账户
     *
     * @param accountId 账户ID
     * @param accountStatus 账户状态
     * @return 结果
     */
    @Override
    @Transactional
    public int changeAccountStatus(Long accountId, String accountStatus) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(accountId);
        accountInfo.setAccountStatus(accountStatus);
        accountInfo.setUpdateTime(new Date());

        return accountInfoMapper.updateAccountInfo(accountInfo);
    }

    /**
     * 销户（退费时调用）
     *
     * @param accountId 账户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int closeAccount(Long accountId) {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setAccountId(accountId);
        accountInfo.setAccountStatus("2"); // 销户状态
        accountInfo.setUpdateTime(new Date());

        return accountInfoMapper.updateAccountInfo(accountInfo);
    }
}