package com.ruoyi.service.pension;

import java.util.List;
import com.ruoyi.domain.pension.DepositApply;

/**
 * 押金使用申请Service接口
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public interface IDepositApplyService
{
    /**
     * 查询押金使用申请
     *
     * @param applyId 押金使用申请主键
     * @return 押金使用申请
     */
    public DepositApply selectDepositApplyByApplyId(Long applyId);

    /**
     * 查询押金使用申请列表
     *
     * @param depositApply 押金使用申请
     * @return 押金使用申请集合
     */
    public List<DepositApply> selectDepositApplyList(DepositApply depositApply);

    /**
     * 根据老人ID查询押金使用申请列表
     *
     * @param elderId 老人ID
     * @return 押金使用申请集合
     */
    public List<DepositApply> selectDepositApplyByElderId(Long elderId);

    /**
     * 根据机构ID查询押金使用申请列表
     *
     * @param institutionId 机构ID
     * @return 押金使用申请集合
     */
    public List<DepositApply> selectDepositApplyByInstitutionId(Long institutionId);

    /**
     * 新增押金使用申请
     *
     * @param depositApply 押金使用申请
     * @return 结果
     */
    public int insertDepositApply(DepositApply depositApply);

    /**
     * 修改押金使用申请
     *
     * @param depositApply 押金使用申请
     * @return 结果
     */
    public int updateDepositApply(DepositApply depositApply);

    /**
     * 批量删除押金使用申请
     *
     * @param applyIds 需要删除的押金使用申请主键集合
     * @return 结果
     */
    public int deleteDepositApplyByApplyIds(Long[] applyIds);

    /**
     * 删除押金使用申请信息
     *
     * @param applyId 押金使用申请主键
     * @return 结果
     */
    public int deleteDepositApplyByApplyId(Long applyId);

    /**
     * 家属审批押金使用申请
     *
     * @param applyId 押金使用申请主键
     * @param opinion 审批意见 (approved-同意, rejected-拒绝)
     * @param approver 审批人姓名
     * @param rejectReason 拒绝原因（拒绝时必填）
     * @return 结果
     */
    public int familyApprove(Long applyId, String opinion, String approver, String rejectReason);

    /**
     * 监管部门审批押金使用申请
     *
     * @param applyId 押金使用申请主键
     * @param approved 是否通过
     * @param remark 审批意见
     * @param approver 审批人
     * @return 结果
     */
    public int supervisionApprove(Long applyId, boolean approved, String remark, String approver);

    /**
     * 撤回押金使用申请
     *
     * @param applyId 押金使用申请主键
     * @return 结果
     */
    public int withdrawApply(Long applyId);
}
