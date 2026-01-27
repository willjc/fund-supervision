package com.ruoyi.service.pension;

import java.util.List;
import com.ruoyi.domain.pension.FundTransferApply;
import com.ruoyi.domain.pension.FundTransfer;

/**
 * 资金划拨申请Service接口
 *
 * @author ruoyi
 * @date 2026-01-28
 */
public interface IFundTransferApplyService
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
    public List<FundTransferApply> selectFundTransferApplyByElderId(Long elderId);

    /**
     * 根据机构ID查询资金划拨申请列表
     *
     * @param institutionId 机构ID
     * @return 资金划拨申请集合
     */
    public List<FundTransferApply> selectFundTransferApplyByInstitutionId(Long institutionId);

    /**
     * 查询老人的待划拨划拨单列表
     *
     * @param elderId 老人ID
     * @return 划拨单集合
     */
    public List<FundTransfer> selectPendingTransferByElderId(Long elderId);

    /**
     * 新增资金划拨申请
     *
     * @param fundTransferApply 资金划拨申请
     * @return 结果
     */
    public int insertFundTransferApply(FundTransferApply fundTransferApply);

    /**
     * 创建资金划拨申请（包含明细）
     *
     * @param fundTransferApply 资金划拨申请
     * @param transferIds 选择的划拨单ID列表
     * @return 结果
     */
    public int createFundTransferApply(FundTransferApply fundTransferApply, List<Long> transferIds);

    /**
     * 修改资金划拨申请
     *
     * @param fundTransferApply 资金划拨申请
     * @return 结果
     */
    public int updateFundTransferApply(FundTransferApply fundTransferApply);

    /**
     * 批量删除资金划拨申请
     *
     * @param applyIds 需要删除的资金划拨申请主键集合
     * @return 结果
     */
    public int deleteFundTransferApplyByApplyIds(Long[] applyIds);

    /**
     * 删除资金划拨申请信息
     *
     * @param applyId 资金划拨申请主键
     * @return 结果
     */
    public int deleteFundTransferApplyByApplyId(Long applyId);

    /**
     * 家属审批资金划拨申请
     *
     * @param applyId 资金划拨申请主键
     * @param approved 是否通过
     * @param opinion 审批意见
     * @param approverName 审批人姓名
     * @param relation 关系
     * @param phone 电话
     * @return 结果
     */
    public int familyApprove(Long applyId, boolean approved, String opinion, String approverName, String relation, String phone);

    /**
     * 监管部门审批资金划拨申请
     *
     * @param applyId 资金划拨申请主键
     * @param approved 是否通过
     * @param remark 审批意见
     * @param approver 审批人
     * @return 结果
     */
    public int supervisionApprove(Long applyId, boolean approved, String remark, String approver);

    /**
     * 执行划拨
     *
     * @param applyId 资金划拨申请主键
     * @return 结果
     */
    public int executeTransfer(Long applyId);
}
