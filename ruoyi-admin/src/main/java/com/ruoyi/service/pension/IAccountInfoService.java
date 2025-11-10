package com.ruoyi.service.pension;

import java.util.List;
import com.ruoyi.domain.pension.AccountInfo;

/**
 * 老人账户信息Service接口
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public interface IAccountInfoService
{
    /**
     * 查询老人账户信息
     *
     * @param accountId 老人账户信息主键
     * @return 老人账户信息
     */
    public AccountInfo selectAccountInfoByAccountId(Long accountId);

    /**
     * 查询老人账户信息列表
     *
     * @param accountInfo 老人账户信息
     * @return 老人账户信息集合
     */
    public List<AccountInfo> selectAccountInfoList(AccountInfo accountInfo);

    /**
     * 根据老人ID查询账户信息
     *
     * @param elderId 老人ID
     * @return 老人账户信息
     */
    public AccountInfo selectAccountInfoByElderId(Long elderId);

    /**
     * 根据机构ID查询账户列表
     *
     * @param institutionId 机构ID
     * @return 老人账户信息集合
     */
    public List<AccountInfo> selectAccountInfoByInstitutionId(Long institutionId);

    /**
     * 新增老人账户信息
     *
     * @param accountInfo 老人账户信息
     * @return 结果
     */
    public int insertAccountInfo(AccountInfo accountInfo);

    /**
     * 修改老人账户信息
     *
     * @param accountInfo 老人账户信息
     * @return 结果
     */
    public int updateAccountInfo(AccountInfo accountInfo);

    /**
     * 批量删除老人账户信息
     *
     * @param accountIds 需要删除的老人账户信息主键集合
     * @return 结果
     */
    public int deleteAccountInfoByAccountIds(Long[] accountIds);

    /**
     * 删除老人账户信息信息
     *
     * @param accountId 老人账户信息主键
     * @return 结果
     */
    public int deleteAccountInfoByAccountId(Long accountId);

    /**
     * 创建老人账户（入住时调用）
     *
     * @param elderId 老人ID
     * @param institutionId 机构ID
     * @param initialBalance 初始余额
     * @return 老人账户信息
     */
    public AccountInfo createAccountInfo(Long elderId, Long institutionId, java.math.BigDecimal initialBalance);

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
    public int updateAccountBalance(Long accountId, java.math.BigDecimal serviceBalance,
                                   java.math.BigDecimal depositBalance, java.math.BigDecimal memberBalance, boolean isIncrease);

    /**
     * 统计机构账户余额
     *
     * @param institutionId 机构ID
     * @return 余额统计
     */
    public java.util.Map<String, Object> selectAccountBalanceSummary(Long institutionId);

    /**
     * 查询余额不足的老人账户
     *
     * @param months 不足月数
     * @return 账户列表
     */
    public List<AccountInfo> selectLowBalanceAccounts(Integer months);

    /**
     * 冻结/解冻账户
     *
     * @param accountId 账户ID
     * @param accountStatus 账户状态
     * @return 结果
     */
    public int changeAccountStatus(Long accountId, String accountStatus);

    /**
     * 销户（退费时调用）
     *
     * @param accountId 账户ID
     * @return 结果
     */
    public int closeAccount(Long accountId);
}