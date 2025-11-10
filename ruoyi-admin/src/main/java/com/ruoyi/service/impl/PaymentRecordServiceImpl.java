package com.ruoyi.service.impl;

import java.util.List;
import java.util.Date;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.mapper.PaymentRecordMapper;
import com.ruoyi.domain.PaymentRecord;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.service.IPaymentRecordService;
import com.ruoyi.service.IOrderInfoService;

/**
 * 支付记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-28
 */
@Service
public class PaymentRecordServiceImpl implements IPaymentRecordService
{
    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private IOrderInfoService orderInfoService;

    /**
     * 查询支付记录
     *
     * @param paymentId 支付记录主键
     * @return 支付记录
     */
    @Override
    public PaymentRecord selectPaymentRecordByPaymentId(Long paymentId)
    {
        return paymentRecordMapper.selectPaymentRecordByPaymentId(paymentId);
    }

    /**
     * 查询支付记录列表
     *
     * @param paymentRecord 支付记录
     * @return 支付记录
     */
    @Override
    public List<PaymentRecord> selectPaymentRecordList(PaymentRecord paymentRecord)
    {
        return paymentRecordMapper.selectPaymentRecordList(paymentRecord);
    }

    /**
     * 新增支付记录
     *
     * @param paymentRecord 支付记录
     * @return 结果
     */
    @Override
    @Transactional
    public int insertPaymentRecord(PaymentRecord paymentRecord)
    {
        paymentRecord.setCreateTime(DateUtils.getNowDate());
        paymentRecord.setPaymentStatus("0"); // 默认状态为处理中
        return paymentRecordMapper.insertPaymentRecord(paymentRecord);
    }

    /**
     * 修改支付记录
     *
     * @param paymentRecord 支付记录
     * @return 结果
     */
    @Override
    public int updatePaymentRecord(PaymentRecord paymentRecord)
    {
        paymentRecord.setUpdateTime(DateUtils.getNowDate());
        return paymentRecordMapper.updatePaymentRecord(paymentRecord);
    }

    /**
     * 批量删除支付记录
     *
     * @param paymentIds 需要删除的支付记录主键
     * @return 结果
     */
    @Override
    public int deletePaymentRecordByPaymentIds(Long[] paymentIds)
    {
        return paymentRecordMapper.deletePaymentRecordByPaymentIds(paymentIds);
    }

    /**
     * 删除支付记录信息
     *
     * @param paymentId 支付记录主键
     * @return 结果
     */
    @Override
    public int deletePaymentRecordByPaymentId(Long paymentId)
    {
        return paymentRecordMapper.deletePaymentRecordByPaymentId(paymentId);
    }

    /**
     * 模拟支付处理
     *
     * @param orderId 订单ID
     * @param paymentMethod 支付方式
     * @return 支付记录
     */
    @Override
    @Transactional
    public PaymentRecord simulatePayment(Long orderId, String paymentMethod)
    {
        OrderInfo orderInfo = orderInfoService.selectOrderInfoByOrderId(orderId);
        if (orderInfo == null) {
            return null;
        }

        // 创建支付记录
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentNo(generatePaymentNo());
        paymentRecord.setOrderId(orderId);
        paymentRecord.setOrderNo(orderInfo.getOrderNo());
        paymentRecord.setElderId(orderInfo.getElderId());
        paymentRecord.setInstitutionId(orderInfo.getInstitutionId());
        paymentRecord.setPaymentAmount(orderInfo.getUnpaidAmount());
        paymentRecord.setPaymentMethod(paymentMethod);
        paymentRecord.setPaymentStatus("1"); // 模拟直接成功
        paymentRecord.setPaymentTime(new Date());
        paymentRecord.setTransactionId(generateTransactionId());
        paymentRecord.setGatewayResponse("模拟支付成功");
        paymentRecord.setOperator("system");
        paymentRecord.setCreateTime(DateUtils.getNowDate());

        paymentRecordMapper.insertPaymentRecord(paymentRecord);

        // 更新订单支付状态
        orderInfoService.processOrderPayment(orderId, paymentMethod);

        return paymentRecord;
    }

    /**
     * 更新支付状态
     *
     * @param paymentId 支付记录ID
     * @param paymentStatus 支付状态
     * @return 结果
     */
    @Override
    public int updatePaymentStatus(Long paymentId, String paymentStatus)
    {
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentId(paymentId);
        paymentRecord.setPaymentStatus(paymentStatus);
        paymentRecord.setUpdateTime(DateUtils.getNowDate());

        return paymentRecordMapper.updatePaymentRecord(paymentRecord);
    }

    /**
     * 导出支付记录
     *
     * @param paymentRecord 查询条件
     * @return 支付记录列表
     */
    @Override
    public List<PaymentRecord> exportPaymentRecord(PaymentRecord paymentRecord)
    {
        return paymentRecordMapper.selectPaymentRecordList(paymentRecord);
    }

    /**
     * 统计支付金额
     *
     * @param paymentRecord 查询条件
     * @return 总金额
     */
    @Override
    public java.math.BigDecimal getPaymentAmountTotal(PaymentRecord paymentRecord)
    {
        return paymentRecordMapper.selectPaymentAmountTotal(paymentRecord);
    }

    /**
     * 根据支付流水号查询支付记录
     *
     * @param paymentNo 支付流水号
     * @return 支付记录
     */
    @Override
    public PaymentRecord selectPaymentRecordByPaymentNo(String paymentNo)
    {
        return paymentRecordMapper.selectPaymentRecordByPaymentNo(paymentNo);
    }

    /**
     * 生成支付流水号
     *
     * @return 支付流水号
     */
    private String generatePaymentNo()
    {
        return "PAY" + DateUtils.dateTimeNow("yyyyMMddHHmmssSSS");
    }

    /**
     * 生成第三方交易号
     *
     * @return 交易号
     */
    private String generateTransactionId()
    {
        return "TXN" + DateUtils.dateTimeNow("yyyyMMddHHmmssSSS");
    }
}