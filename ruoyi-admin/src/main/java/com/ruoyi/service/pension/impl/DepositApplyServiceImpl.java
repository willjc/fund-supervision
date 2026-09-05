package com.ruoyi.service.pension.impl;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.pension.DepositApplyMapper;
import com.ruoyi.domain.pension.DepositApply;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.service.pension.IDepositApplyService;
import com.ruoyi.service.pension.IAccountInfoService;
import com.ruoyi.service.pension.IFundTransferService;

/**
 * 押金使用申请Service业���层处理
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@Service
public class DepositApplyServiceImpl implements IDepositApplyService
{
    @Autowired
    private DepositApplyMapper depositApplyMapper;

    @Autowired
    private IAccountInfoService accountInfoService;



    @Autowired
    private IFundTransferService fundTransferService;
    @Autowired private com.ruoyi.mapper.bank.BankSettlementMapper settlementMapper;
    @Autowired private com.ruoyi.service.bank.impl.BankPayoutService bankPayoutService;

    /**
     * 查询押金使用申请
     *
     * @param applyId 押金使用申请主键
     * @return 押金使用申请
     */
    @Override
    public DepositApply selectDepositApplyByApplyId(Long applyId)
    {
        return depositApplyMapper.selectDepositApplyByApplyId(applyId);
    }

    /**
     * 查询押金使用申请列表
     *
     * @param depositApply 押金使用申请
     * @return 押金使用申请
     */
    @Override
    public List<DepositApply> selectDepositApplyList(DepositApply depositApply)
    {
        return depositApplyMapper.selectDepositApplyList(depositApply);
    }

    /**
     * 根据老人ID查询押金使用申请列表
     *
     * @param elderId 老人ID
     * @return 押金使用申请集合
     */
    @Override
    public List<DepositApply> selectDepositApplyByElderId(Long elderId)
    {
        return depositApplyMapper.selectDepositApplyByElderId(elderId);
    }

    /**
     * 根据机构ID查询押金使用申请列表
     *
     * @param institutionId 机构ID
     * @return 押金使用申请集合
     */
    @Override
    public List<DepositApply> selectDepositApplyByInstitutionId(Long institutionId)
    {
        return depositApplyMapper.selectDepositApplyByInstitutionId(institutionId);
    }

    /**
     * 新增押金使用申请
     *
     * @param depositApply 押金使用申请
     * @return 结果
     */
    @Override
    public int insertDepositApply(DepositApply depositApply)
    {
        bankPayoutService.checkScope(depositApply.getInstitutionId());
        if (depositApply.getApplyStatus() == null) { depositApply.setApplyStatus("draft"); }
        if (!"draft".equals(depositApply.getApplyStatus()) && !"pending_family".equals(depositApply.getApplyStatus()))
        { throw new com.ruoyi.common.exception.ServiceException("新申请仅允许草稿或待家属审批，不能指定审批结果"); }
        clearApproval(depositApply);
        depositApply.setCreateTime(DateUtils.getNowDate());
        return depositApplyMapper.insertDepositApply(depositApply);
    }

    /**
     * 修改押金使用申请
     *
     * @param depositApply 押金使用申请
     * @return 结果
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public int updateDepositApply(DepositApply depositApply)
    {
        DepositApply existing = editable(depositApply.getApplyId());
        if (depositApply.getApplyStatus() != null && !"draft".equals(depositApply.getApplyStatus())
                && !"pending_family".equals(depositApply.getApplyStatus()))
        { throw new com.ruoyi.common.exception.ServiceException("不能通过通用编辑修改审批结果"); }
        depositApply.setInstitutionId(existing.getInstitutionId());
        depositApply.setElderId(existing.getElderId());
        depositApply.setAccountId(existing.getAccountId());
        clearApproval(depositApply);
        depositApply.setUpdateTime(DateUtils.getNowDate());
        return depositApplyMapper.updateDepositApply(depositApply);
    }

    /**
     * 批量删除押金使用申请
     *
     * @param applyIds 需要删除的押金使用申请主键
     * @return 结果
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public int deleteDepositApplyByApplyIds(Long[] applyIds)
    {
        for (Long id : applyIds) { editable(id); }
        return depositApplyMapper.deleteDepositApplyByApplyIds(applyIds);
    }

    /**
     * 删除押金使用申请信息
     *
     * @param applyId 押金使用申请主键
     * @return 结果
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public int deleteDepositApplyByApplyId(Long applyId)
    {
        editable(applyId);
        return depositApplyMapper.deleteDepositApplyByApplyId(applyId);
    }

    private DepositApply editable(Long id)
    {
        DepositApply apply = settlementMapper.lockDeposit(id);
        if (apply == null) { throw new com.ruoyi.common.exception.ServiceException("押金申请不存在"); }
        bankPayoutService.checkScope(apply.getInstitutionId());
        if (!("draft".equals(apply.getApplyStatus()) || "withdrawn".equals(apply.getApplyStatus()))
                || settlementMapper.linkedApplication(id, "DEPOSIT") > 0)
        { throw new com.ruoyi.common.exception.ServiceException("已提交或银行关联申请禁止编辑删除"); }
        return apply;
    }

    private void clearApproval(DepositApply apply)
    {
        apply.setApprover(null); apply.setApproveTime(null); apply.setApproveRemark(null);
        apply.setFamilyApproveTime(null); apply.setFamilyApproveOpinion(null); apply.setFamilyConfirmName(null);
        apply.setActualAmount(null); apply.setUseTime(null);
    }

    /**
     * 家属审批押金使用申请
     *
     * @param applyId 押金使用申请主键
     * @param opinion 审批意见 (approved-同意, rejected-拒绝)
     * @param approver 审批人姓名
     * @param rejectReason 拒绝原因（拒绝时必填）
     * @return 结果
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public int familyApprove(Long applyId, String opinion, String approver, String rejectReason)
    {
        // 1. 查询申请信息
        DepositApply apply = settlementMapper.lockDeposit(applyId);
        if (apply == null) {
            throw new RuntimeException("押金使用申请不存在");
        }

        // 2. 验证状态(只有待家属审批状态才能审批)
        if (!"pending_family".equals(apply.getApplyStatus())) {
            throw new RuntimeException("当前状态不允许家属审批");
        }

        // 3. 判断审批结果（简化逻辑，直接根据明确标志判断）
        String approveStatus;
        String familyOpinion; // 实际存储的审批意见
        if ("approved".equals(opinion)) {
            approveStatus = "family_approved"; // 家属同意，等待监管部门审批
            familyOpinion = "同意"; // 家属同意时的意见
        } else {
            approveStatus = "rejected"; // 家属拒绝
            familyOpinion = (rejectReason != null && !rejectReason.trim().isEmpty()) ? rejectReason.trim() : "拒绝"; // 使用用户输入的拒绝原因
        }

        // 4. 更新审批信息
        DepositApply updateApply = new DepositApply();
        updateApply.setApplyId(applyId);
        updateApply.setFamilyApproveOpinion(familyOpinion); // 存储实际的审批意见/拒绝原因
        updateApply.setFamilyConfirmName(approver); // 记录家属审批人
        updateApply.setFamilyApproveTime(DateUtils.getNowDate());
        updateApply.setApplyStatus(approveStatus);
        updateApply.setUpdateTime(DateUtils.getNowDate());

        return depositApplyMapper.updateDepositApply(updateApply);
    }

    /**
     * 监管部门审批押金使用申请
     *
     * @param applyId 押金使用申请主键
     * @param approved 是否通过
     * @param remark 审批意见
     * @param approver 审批人
     * @return 结果
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public int supervisionApprove(Long applyId, boolean approved, String remark, String approver)
    {
        DepositApply apply = settlementMapper.lockDeposit(applyId);
        if (apply == null) { throw new com.ruoyi.common.exception.ServiceException("押金申请不存在"); }
        bankPayoutService.checkScope(apply.getInstitutionId());
        if (!"family_approved".equals(apply.getApplyStatus()) && !"pending_supervision".equals(apply.getApplyStatus()))
        { throw new com.ruoyi.common.exception.ServiceException("当前申请状态不允许审批"); }
        if (approved)
        {
            AccountInfo account = accountInfoService.selectAccountInfoByAccountId(apply.getAccountId());
            if (account == null || !java.util.Objects.equals(account.getInstitutionId(), apply.getInstitutionId())
                    || !java.util.Objects.equals(account.getElderId(), apply.getElderId())
                    || apply.getApplyAmount() == null || apply.getApplyAmount().signum() <= 0)
            { throw new com.ruoyi.common.exception.ServiceException("押金申请账户或金额不合法"); }
            if (account.getBankDepositBalance().subtract(account.getDepositReserved()).compareTo(apply.getApplyAmount()) < 0)
            { throw new com.ruoyi.common.exception.ServiceException("新银行押金来源不足，历史资金需人工核查"); }
            FundTransfer transfer = new FundTransfer();
            transfer.setTransferNo("TRF-DEP-" + applyId);
            transfer.setSourceKey("DEPOSIT:" + applyId);
            transfer.setBalanceType("DEPOSIT");
            transfer.setBankEligible(1);
            transfer.setTransferType("3");
            transfer.setTransferAmount(apply.getApplyAmount());
            transfer.setTransferDate(DateUtils.getNowDate());
            transfer.setTransferStatus("0");
            transfer.setIsPaid("0");
            transfer.setStatus("pending");
            transfer.setPaidMethod("deposit");
            transfer.setApplyId(applyId);
            transfer.setElderId(apply.getElderId());
            transfer.setInstitutionId(apply.getInstitutionId());
            transfer.setElderCount(1);
            transfer.setCreateBy(approver);
            transfer.setApproveUser(approver);
            transfer.setApproveTime(DateUtils.getNowDate());
            transfer.setRemark("押金使用审批通过，待银行拨付");
            if (fundTransferService.insertFundTransfer(transfer) != 1)
            { throw new com.ruoyi.common.exception.ServiceException("生成待拨付单失败"); }
        }
        DepositApply update = new DepositApply();
        update.setApplyId(applyId);
        update.setApplyStatus(approved ? "approved" : "rejected");
        update.setApprover(approver);
        update.setApproveTime(DateUtils.getNowDate());
        update.setApproveRemark(remark);
        update.setActualAmount(BigDecimal.ZERO);
        return depositApplyMapper.updateDepositApply(update);
    }

    /**
     * 撤回押金使用申请
     *
     * @param applyId 押金使用申请主键
     * @return 结果
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public int withdrawApply(Long applyId)
    {
        // 1. 查询申请信息
        DepositApply apply = settlementMapper.lockDeposit(applyId);
        if (apply == null) {
            throw new RuntimeException("押金使用申请不存在");
        }

        // 2. 验证状态(已通过、已驳回、已撤回状态不能撤回)
        bankPayoutService.checkScope(apply.getInstitutionId());
        if (settlementMapper.linkedApplication(applyId, "DEPOSIT") > 0)
        { throw new com.ruoyi.common.exception.ServiceException("银行关联申请不能撤回"); }
        String status = apply.getApplyStatus();
        if ("approved".equals(status) || "rejected".equals(status) || "withdrawn".equals(status)) {
            throw new RuntimeException("当前状态不允许撤回");
        }

        // 3. 更新状态为已撤回,清空审批信息
        DepositApply updateApply = new DepositApply();
        updateApply.setApplyId(applyId);
        updateApply.setApplyStatus("withdrawn");
        updateApply.setFamilyApproveTime(null);
        updateApply.setFamilyApproveOpinion(null);
        updateApply.setApprover(null);
        updateApply.setApproveTime(null);
        updateApply.setApproveRemark(null);
        updateApply.setUpdateTime(DateUtils.getNowDate());

        return depositApplyMapper.updateDepositApply(updateApply);
    }
}
