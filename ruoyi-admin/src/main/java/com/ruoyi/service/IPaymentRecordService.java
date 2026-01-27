package com.ruoyi.service;

import java.util.List;
import com.ruoyi.domain.PaymentRecord;

/**
 * 支付记录Service接口
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public interface IPaymentRecordService
{
    /**
     * 查询支付记录
     *
     * @param paymentId 支付记录主键
     * @return 支付记录
     */
    public PaymentRecord selectPaymentRecordByPaymentId(Long paymentId);

    /**
     * 查询支付记录���表
     *
     * @param paymentRecord 支付记录
     * @return 支付记录集合
     */
    public List<PaymentRecord> selectPaymentRecordList(PaymentRecord paymentRecord);

    /**
     * 新增支付记录
     *
     * @param paymentRecord 支付记录
     * @return 结果
     */
    public int insertPaymentRecord(PaymentRecord paymentRecord);

    /**
     * 修改支付记录
     *
     * @param paymentRecord 支付记录
     * @return 结果
     */
    public int updatePaymentRecord(PaymentRecord paymentRecord);

    /**
     * 批量删除支付记录
     *
     * @param paymentIds 需要删除的支付记录主键
     * @return 结果
     */
    public int deletePaymentRecordByPaymentIds(Long[] paymentIds);

    /**
     * 删除支付记录信息
     *
     * @param paymentId 支付记录主键
     * @return 结果
     */
    public int deletePaymentRecordByPaymentId(Long paymentId);

    /**
     * 模拟支付处理
     *
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     * @return 支付记录
     */
    public PaymentRecord simulatePayment(Long orderId, String paymentMethod);

    /**
     * 更新支付状态
     *
     * @param paymentId 支付记录ID
     * @param paymentStatus 支付状态
     * @return 结果
     */
    public int updatePaymentStatus(Long paymentId, String paymentStatus);

    /**
     * 导出支付记录
     *
     * @param paymentRecord 查询条件
     * @return 支付记录列表
     */
    public List<PaymentRecord> exportPaymentRecord(PaymentRecord paymentRecord);

    /**
     * 统计支付金额
     *
     * @param paymentRecord 查询条件
     * @return 总金额
     */
    public java.math.BigDecimal getPaymentAmountTotal(PaymentRecord paymentRecord);

    /**
     * 根据支付流水号查询支付记录
     *
     * @param paymentNo 支付流水号
     * @return 支付记录
     */
    public PaymentRecord selectPaymentRecordByPaymentNo(String paymentNo);
}