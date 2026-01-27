package com.ruoyi.service.pension;

import java.util.List;
import com.ruoyi.domain.pension.RefundRecord;

/**
 * 退款记录Service接口
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public interface IRefundRecordService
{
    /**
     * 查询退款记录
     *
     * @param refundId 退款记录主键
     * @return 退款记录
     */
    public RefundRecord selectRefundRecordByRefundId(Long refundId);

    /**
     * 查询退款记录列表
     *
     * @param refundRecord 退款记录
     * @return 退款记录集合
     */
    public List<RefundRecord> selectRefundRecordList(RefundRecord refundRecord);

    /**
     * 新增退款记录
     *
     * @param refundRecord 退款记录
     * @return 结果
     */
    public int insertRefundRecord(RefundRecord refundRecord);

    /**
     * 修改退款记录
     *
     * @param refundRecord 退款记录
     * @return 结果
     */
    public int updateRefundRecord(RefundRecord refundRecord);

    /**
     * 批量删除退款记录
     *
     * @param refundIds 需要删除的退款记录主键集合
     * @return 结果
     */
    public int deleteRefundRecordByRefundIds(Long[] refundIds);

    /**
     * 删除退款记录信息
     *
     * @param refundId 退款记录主键
     * @return 结果
     */
    public int deleteRefundRecordByRefundId(Long refundId);

    /**
     * 按订单ID查询退款记录
     *
     * @param orderId 订单ID
     * @return 退款记录集合
     */
    public List<RefundRecord> selectRefundRecordByOrderId(Long orderId);

    /**
     * 按老人ID查询退款记录
     *
     * @param elderId 老人ID
     * @return 退款记录集合
     */
    public List<RefundRecord> selectRefundRecordByElderId(Long elderId);

    /**
     * 按机构ID查询退款记录
     *
     * @param institutionId 机构ID
     * @return 退款记录集合
     */
    public List<RefundRecord> selectRefundRecordByInstitutionId(Long institutionId);
}