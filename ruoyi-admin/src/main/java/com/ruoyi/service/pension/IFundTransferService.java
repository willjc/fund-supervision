package com.ruoyi.service.pension;

import java.util.List;
import java.util.Date;
import com.ruoyi.domain.pension.FundTransfer;

/**
 * 资金划拨记录Service接口
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public interface IFundTransferService
{
    /**
     * 查询资金划拨记录
     *
     * @param transferId 资金划拨记录主键
     * @return 资金划拨记录
     */
    public FundTransfer selectFundTransferByTransferId(Long transferId);

    /**
     * 查询资金划拨记录列表
     *
     * @param fundTransfer 资金划拨记录
     * @return 资金划拨记录集合
     */
    public List<FundTransfer> selectFundTransferList(FundTransfer fundTransfer);

    /**
     * 根据机构ID查询划拨记录
     *
     * @param institutionId 机构ID
     * @return 资金划拨记录集合
     */
    public List<FundTransfer> selectFundTransferByInstitutionId(Long institutionId);

    /**
     * 新增资金划拨记录
     *
     * @param fundTransfer 资金划拨记录
     * @return 结果
     */
    public int insertFundTransfer(FundTransfer fundTransfer);

    /**
     * 修改资金划拨记录
     *
     * @param fundTransfer 资金划拨记录
     * @return 结果
     */
    public int updateFundTransfer(FundTransfer fundTransfer);

    /**
     * 批量删除资金划拨记录
     *
     * @param transferIds 需要删除的资金划拨记录主键集合
     * @return 结果
     */
    public int deleteFundTransferByTransferIds(Long[] transferIds);

    /**
     * 删除资金划拨记录信息
     *
     * @param transferId 资金划拨记录主键
     * @return 结果
     */
    public int deleteFundTransferByTransferId(Long transferId);

    /**
     * 生成月度自动划拨
     *
     * @param institutionId 机构ID
     * @param transferPeriod 划拨期间
     * @return 划拨记录
     */
    public FundTransfer generateMonthlyTransfer(Long institutionId, String transferPeriod);

    /**
     * 审批资金划拨
     *
     * @param transferId 划拨ID
     * @param approveUser 审批人
     * @param approveResult 审批结果（1通过 2拒绝）
     * @param approveRemark 审批意见
     * @return 结果
     */
    public int approveFundTransfer(Long transferId, String approveUser, String approveResult, String approveRemark);

    /**
     * 执行资金划拨
     *
     * @param transferId 划拨ID
     * @param executeUser 执行人
     * @return 结果
     */
    public int executeFundTransfer(Long transferId, String executeUser);

    /**
     * 查询待处理的划拨记录
     *
     * @return 待处理的划拨记录集合
     */
    public List<FundTransfer> selectPendingTransfers();

    /**
     * 批量执行自动划拨（定时任务调用）
     *
     * @return 执行结果
     */
    public java.util.Map<String, Object> executeAutoTransfer();

    /**
     * 手动发起划拨申请
     *
     * @param institutionId 机构ID
     * @param transferAmount 划拨金额
     * @param transferType 划拨类型
     * @param remark 申请原因
     * @return 结果
     */
    public int applyManualTransfer(Long institutionId, java.math.BigDecimal transferAmount, String transferType, String remark);

    /**
     * 统计划拨金额
     *
     * @param institutionId 机构ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    public java.util.Map<String, Object> selectTransferStatistics(Long institutionId, Date startDate, Date endDate);

    /**
     * 查询余额不足的老人账户
     *
     * @param months 不足月数
     * @return 账户列表
     */
    public java.util.List<com.ruoyi.domain.pension.AccountInfo> selectLowBalanceAccounts(Integer months);

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
    public void generateTransferOrderFromCheckIn(Long checkInId, Long orderId, Long institutionId,
                                                 Long elderId, Integer monthCount, Date startDate);

    /**
     * 根据老人ID和月数生成划拨单
     *
     * @param orderId 订单ID
     * @param institutionId 机构ID
     * @param elderId 老人ID
     * @param monthCount 月数
     * @param startDate 起始日期
     * @param monthlyFee 月费用
     */
    public void generateMonthlyTransfersForOrder(Long orderId, Long institutionId, Long elderId,
                                                 Integer monthCount, Date startDate, java.math.BigDecimal monthlyFee);

    /**
     * 根据老人ID和支付方式查询已完成的划拨记录（用于H5费用查询）
     *
     * @param elderId 老人ID
     * @param paidMethods 支付方式数组（如：auto, manual, deposit）
     * @return 划拨记录集合
     */
    public List<FundTransfer> selectByElderIdAndPaidMethods(Long elderId, String[] paidMethods);
}