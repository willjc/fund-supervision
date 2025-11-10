package com.ruoyi.mapper;

import java.util.List;
import com.ruoyi.domain.PaymentRecord;

/**
 * 支付记录Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public interface PaymentRecordMapper
{
    /**
     * 查询支付记录
     *
     * @param paymentId 支付记录主键
     * @return 支付记录
     */
    public PaymentRecord selectPaymentRecordByPaymentId(Long paymentId);

    /**
     * 查询支付记录列表
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
     * 删除支付记录
     *
     * @param paymentId 支付记录主键
     * @return 结果
     */
    public int deletePaymentRecordByPaymentId(Long paymentId);

    /**
     * 批量删除支付记录
     *
     * @param paymentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePaymentRecordByPaymentIds(Long[] paymentIds);

    /**
     * 根据订单ID查询支付记录
     *
     * @param orderId 订单ID
     * @return 支付记录列表
     */
    public List<PaymentRecord> selectPaymentsByOrderId(Long orderId);

    /**
     * 根据老人ID查询支付记录
     *
     * @param elderId 老人ID
     * @return 支付记录列表
     */
    public List<PaymentRecord> selectPaymentsByElderId(Long elderId);

    /**
     * 根据支付流水号查询支付记录
     *
     * @param paymentNo 支付流水号
     * @return 支付记录
     */
    public PaymentRecord selectPaymentRecordByPaymentNo(String paymentNo);

    /**
     * 统计支付金额
     *
     * @param paymentRecord 查询条件
     * @return 总金额
     */
    public java.math.BigDecimal selectPaymentAmountTotal(PaymentRecord paymentRecord);
}