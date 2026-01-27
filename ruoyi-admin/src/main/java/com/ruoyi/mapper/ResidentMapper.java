package com.ruoyi.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.PaymentRecord;
import com.ruoyi.domain.vo.ResidentVO;
import com.ruoyi.domain.pension.FundTransfer;

/**
 * 入住人Mapper接口
 *
 * @author ruoyi
 * @date 2025-11-11
 */
public interface ResidentMapper
{
    /**
     * 查询入住人列表
     *
     * @param queryVO 查询条件
     * @return 入住人列表
     */
    public List<ResidentVO> selectResidentList(ResidentVO queryVO);

    /**
     * 查询入住人详细信息
     *
     * @param elderId 老人ID
     * @return 入住人详细信息
     */
    public ResidentVO selectResidentDetail(Long elderId);

    /**
     * 查询入住人的所有订单
     *
     * @param elderId 老人ID
     * @return 订单列表
     */
    public List<OrderInfo> selectOrdersByElderId(Long elderId);

    /**
     * 查询入住人的所有支付记录
     *
     * @param elderId 老人ID
     * @return 支付记录列表
     */
    public List<PaymentRecord> selectPaymentsByElderId(Long elderId);

    /**
     * 查询入住人统计数据
     *
     * @return 统计数据(totalResidents, totalServiceBalance, totalDepositBalance, totalMemberBalance)
     */
    public Map<String, Object> selectResidentStatistics();

    /**
     * 查询老人的拨付单列表
     *
     * @param elderId 老人ID
     * @return 拨付单列表
     */
    public List<FundTransfer> selectTransfersByElderId(Long elderId);
}
