package com.ruoyi.mapper.pension;

import java.util.List;
import com.ruoyi.domain.pension.AccountInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 老人账户信息Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public interface AccountInfoMapper
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
     * 更新账户余额
     *
     * @param accountId 账户ID
     * @param totalBalance 总余额
     * @param serviceBalance 服务费余额
     * @param depositBalance 押金余额
     * @param memberBalance 会员费余额
     * @return 结果
     */
    public int updateAccountBalance(@Param("accountId") Long accountId,
                                  @Param("totalBalance") java.math.BigDecimal totalBalance,
                                  @Param("serviceBalance") java.math.BigDecimal serviceBalance,
                                  @Param("depositBalance") java.math.BigDecimal depositBalance,
                                  @Param("memberBalance") java.math.BigDecimal memberBalance);

    /**
     * 删除老人账户信息
     *
     * @param accountId 老人账户信息主键
     * @return 结果
     */
    public int deleteAccountInfoByAccountId(Long accountId);

    /**
     * 批量删除老人账户信息
     *
     * @param accountIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAccountInfoByAccountIds(Long[] accountIds);

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
    public List<AccountInfo> selectLowBalanceAccounts(@Param("months") Integer months);
}