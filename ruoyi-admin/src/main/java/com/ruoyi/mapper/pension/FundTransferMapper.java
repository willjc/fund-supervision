package com.ruoyi.mapper.pension;

import java.util.List;
import java.util.Date;
import com.ruoyi.domain.pension.FundTransfer;
import org.apache.ibatis.annotations.Param;

/**
 * 资金划拨记录Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public interface FundTransferMapper
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
     * 查询待处理的划拨记录
     *
     * @return 待处理的划拨记录集合
     */
    public List<FundTransfer> selectPendingTransfers();

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
     * 审批划拨记录
     *
     * @param transferId 划拨ID
     * @param transferStatus 划拨状态
     * @param approveUser 审批人
     * @param approveTime 审批时间
     * @param failureReason 失败原因
     * @return 结果
     */
    public int approveFundTransfer(@Param("transferId") Long transferId,
                                  @Param("transferStatus") String transferStatus,
                                  @Param("approveUser") String approveUser,
                                  @Param("approveTime") Date approveTime,
                                  @Param("failureReason") String failureReason);

    /**
     * 执行划拨
     *
     * @param transferId 划拨ID
     * @param transferStatus 划拨状态
     * @param executeUser 执行人
     * @param executeTime 执行时间
     * @param bankOrderNo 银行订单号
     * @return 结果
     */
    public int executeFundTransfer(@Param("transferId") Long transferId,
                                  @Param("transferStatus") String transferStatus,
                                  @Param("executeUser") String executeUser,
                                  @Param("executeTime") Date executeTime,
                                  @Param("bankOrderNo") String bankOrderNo);

    /**
     * 删除资金划拨记录
     *
     * @param transferId 资金划拨记录主键
     * @return 结果
     */
    public int deleteFundTransferByTransferId(Long transferId);

    /**
     * 批量删除资金划拨记录
     *
     * @param transferIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFundTransferByTransferIds(Long[] transferIds);

    /**
     * 统计划拨金额
     *
     * @param institutionId 机构ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果
     */
    public java.util.Map<String, Object> selectTransferStatistics(@Param("institutionId") Long institutionId,
                                                               @Param("startDate") Date startDate,
                                                               @Param("endDate") Date endDate);

    /**
     * 根据老人ID和支付方式查询已完成的划拨记录（用于H5费用查询）
     *
     * @param elderId 老人ID
     * @param paidMethods 支付方式数组（如：auto, manual, deposit）
     * @return 划拨记录集合
     */
    public List<FundTransfer> selectByElderIdAndPaidMethods(@Param("elderId") Long elderId,
                                                           @Param("paidMethods") String[] paidMethods);
}