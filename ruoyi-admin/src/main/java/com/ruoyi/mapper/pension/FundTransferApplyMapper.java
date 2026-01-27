package com.ruoyi.mapper.pension;

import java.util.List;
import com.ruoyi.domain.pension.FundTransferApply;
import org.apache.ibatis.annotations.Param;

/**
 * 资金划拨申请Mapper接口
 *
 * @author ruoyi
 * @date 2026-01-28
 */
public interface FundTransferApplyMapper
{
    /**
     * 查询资金划拨申请
     *
     * @param applyId 资金划拨申请主键
     * @return 资金划拨申请
     */
    public FundTransferApply selectFundTransferApplyByApplyId(Long applyId);

    /**
     * 查询资金划拨申请列表
     *
     * @param fundTransferApply 资金划拨申请
     * @return 资金划拨申请集合
     */
    public List<FundTransferApply> selectFundTransferApplyList(FundTransferApply fundTransferApply);

    /**
     * 根据老人ID查询资金划拨申请列表
     *
     * @param elderId 老人ID
     * @return 资金划拨申请集合
     */
    public List<FundTransferApply> selectFundTransferApplyByElderId(@Param("elderId") Long elderId);

    /**
     * 根据机构ID查询资金划拨申请列表
     *
     * @param institutionId 机构ID
     * @return 资金划拨申请集合
     */
    public List<FundTransferApply> selectFundTransferApplyByInstitutionId(@Param("institutionId") Long institutionId);

    /**
     * 新增资金划拨申请
     *
     * @param fundTransferApply 资金划拨申请
     * @return 结果
     */
    public int insertFundTransferApply(FundTransferApply fundTransferApply);

    /**
     * 修改资金划拨申请
     *
     * @param fundTransferApply 资金划拨申请
     * @return 结果
     */
    public int updateFundTransferApply(FundTransferApply fundTransferApply);

    /**
     * 删除资金划拨申请
     *
     * @param applyId 资金划拨申请主键
     * @return 结果
     */
    public int deleteFundTransferApplyByApplyId(Long applyId);

    /**
     * 批量删除资金划拨申请
     *
     * @param applyIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFundTransferApplyByApplyIds(Long[] applyIds);

    /**
     * 查询老人的待划拨划拨单列表
     *
     * @param elderId 老人ID
     * @return 划拨单集合
     */
    public List<com.ruoyi.domain.pension.FundTransfer> selectPendingTransferByElderId(@Param("elderId") Long elderId);

    /**
     * 生成申请单号
     *
     * @return 申请单号
     */
    public String generateApplyNo();
}
