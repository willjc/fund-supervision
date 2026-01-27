package com.ruoyi.mapper.pension;

import java.util.List;
import com.ruoyi.domain.pension.FundTransferApplyDetail;
import org.apache.ibatis.annotations.Param;

/**
 * 资金划拨申请明细Mapper接口
 *
 * @author ruoyi
 * @date 2026-01-28
 */
public interface FundTransferApplyDetailMapper
{
    /**
     * 查询资金划拨申请明细
     *
     * @param detailId 资金划拨申请明细主键
     * @return 资金划拨申请明细
     */
    public FundTransferApplyDetail selectFundTransferApplyDetailByDetailId(Long detailId);

    /**
     * 查询资金划拨申请明细列表
     *
     * @param fundTransferApplyDetail 资金划拨申请明细
     * @return 资金划拨申请明细集合
     */
    public List<FundTransferApplyDetail> selectFundTransferApplyDetailList(FundTransferApplyDetail fundTransferApplyDetail);

    /**
     * 根据申请ID查询明细列表
     *
     * @param applyId 申请ID
     * @return 资金划拨申请明细集合
     */
    public List<FundTransferApplyDetail> selectFundTransferApplyDetailByApplyId(@Param("applyId") Long applyId);

    /**
     * 根据划拨单ID查询明细
     *
     * @param transferId 划拨单ID
     * @return 资金划拨申请明细
     */
    public FundTransferApplyDetail selectFundTransferApplyDetailByTransferId(@Param("transferId") Long transferId);

    /**
     * 新增资金划拨申请明细
     *
     * @param fundTransferApplyDetail 资金划拨申请明细
     * @return 结果
     */
    public int insertFundTransferApplyDetail(FundTransferApplyDetail fundTransferApplyDetail);

    /**
     * 批量新增资金划拨申请明细
     *
     * @param list 资金划拨申请明细集合
     * @return 结果
     */
    public int batchInsertFundTransferApplyDetail(@Param("list") List<FundTransferApplyDetail> list);

    /**
     * 修改资金划拨申请明细
     *
     * @param fundTransferApplyDetail 资金划拨申请明细
     * @return 结果
     */
    public int updateFundTransferApplyDetail(FundTransferApplyDetail fundTransferApplyDetail);

    /**
     * 删除资金划拨申请明细
     *
     * @param detailId 资金划拨申请明细主键
     * @return 结果
     */
    public int deleteFundTransferApplyDetailByDetailId(Long detailId);

    /**
     * 批量删除资金划拨申请明细
     *
     * @param detailIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFundTransferApplyDetailByDetailIds(Long[] detailIds);

    /**
     * 根据申请ID删除明细
     *
     * @param applyId 申请ID
     * @return 结果
     */
    public int deleteFundTransferApplyDetailByApplyId(@Param("applyId") Long applyId);
}
