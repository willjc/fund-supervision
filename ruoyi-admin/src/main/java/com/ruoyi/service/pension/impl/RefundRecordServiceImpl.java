package com.ruoyi.service.pension.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.pension.RefundRecordMapper;
import com.ruoyi.domain.pension.RefundRecord;
import com.ruoyi.service.pension.IRefundRecordService;

/**
 * 退款记录Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@Service
public class RefundRecordServiceImpl implements IRefundRecordService
{
    @Autowired
    private RefundRecordMapper refundRecordMapper;

    /**
     * 查询退款记录
     *
     * @param refundId 退款记录主键
     * @return 退款记录
     */
    @Override
    public RefundRecord selectRefundRecordByRefundId(Long refundId)
    {
        return refundRecordMapper.selectRefundRecordByRefundId(refundId);
    }

    /**
     * 查询退款记录列表
     *
     * @param refundRecord 退款记录
     * @return 退款记录
     */
    @Override
    public List<RefundRecord> selectRefundRecordList(RefundRecord refundRecord)
    {
        return refundRecordMapper.selectRefundRecordList(refundRecord);
    }

    /**
     * 新增退款记录
     *
     * @param refundRecord 退款记录
     * @return 结果
     */
    @Override
    public int insertRefundRecord(RefundRecord refundRecord)
    {
        refundRecord.setCreateTime(DateUtils.getNowDate());
        return refundRecordMapper.insertRefundRecord(refundRecord);
    }

    /**
     * 修改退款记录
     *
     * @param refundRecord 退款记录
     * @return 结果
     */
    @Override
    public int updateRefundRecord(RefundRecord refundRecord)
    {
        refundRecord.setUpdateTime(DateUtils.getNowDate());
        return refundRecordMapper.updateRefundRecord(refundRecord);
    }

    /**
     * 批量删除退款记录
     *
     * @param refundIds 需要删除的退款记录主键
     * @return 结果
     */
    @Override
    public int deleteRefundRecordByRefundIds(Long[] refundIds)
    {
        return refundRecordMapper.deleteRefundRecordByRefundIds(refundIds);
    }

    /**
     * 删除退款记录信息
     *
     * @param refundId 退款记录主键
     * @return 结果
     */
    @Override
    public int deleteRefundRecordByRefundId(Long refundId)
    {
        return refundRecordMapper.deleteRefundRecordByRefundId(refundId);
    }

    /**
     * 按订单ID查询退款记录
     *
     * @param orderId 订单ID
     * @return 退款记录集合
     */
    @Override
    public List<RefundRecord> selectRefundRecordByOrderId(Long orderId)
    {
        return refundRecordMapper.selectRefundRecordByOrderId(orderId);
    }

    /**
     * 按老人ID查询退款记录
     *
     * @param elderId 老人ID
     * @return 退款记录集合
     */
    @Override
    public List<RefundRecord> selectRefundRecordByElderId(Long elderId)
    {
        return refundRecordMapper.selectRefundRecordByElderId(elderId);
    }

    /**
     * 按机构ID查询退款记录
     *
     * @param institutionId 机构ID
     * @return 退款记录集合
     */
    @Override
    public List<RefundRecord> selectRefundRecordByInstitutionId(Long institutionId)
    {
        return refundRecordMapper.selectRefundRecordByInstitutionId(institutionId);
    }
}