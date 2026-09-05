package com.ruoyi.service.pension.impl;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.mapper.pension.FundTransferApplyMapper;
import com.ruoyi.mapper.pension.FundTransferApplyDetailMapper;
import com.ruoyi.mapper.pension.FundTransferMapper;
import com.ruoyi.domain.pension.FundTransferApply;
import com.ruoyi.domain.pension.FundTransferApplyDetail;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.service.pension.IFundTransferApplyService;

/**
 * 资金划拨申请Service业务层处理
 *
 * @author ruoyi
 * @date 2026-01-28
 */
@Service
public class FundTransferApplyServiceImpl implements IFundTransferApplyService
{
    @Autowired private com.ruoyi.mapper.bank.BankSettlementMapper settlementMapper;
    @Autowired private com.ruoyi.service.bank.impl.BankPayoutService bankPayoutService;

    @Autowired
    private FundTransferApplyMapper fundTransferApplyMapper;

    @Autowired
    private FundTransferApplyDetailMapper fundTransferApplyDetailMapper;

    @Autowired
    private FundTransferMapper fundTransferMapper;



    /**
     * 查询资金划拨申请
     *
     * @param applyId 资金划拨申请主键
     * @return 资金划拨申请
     */
    @Override
    public FundTransferApply selectFundTransferApplyByApplyId(Long applyId)
    {
        return fundTransferApplyMapper.selectFundTransferApplyByApplyId(applyId);
    }

    /**
     * 查询资金划拨申请列表
     *
     * @param fundTransferApply 资金划拨申请
     * @return 资金划拨申请
     */
    @Override
    public List<FundTransferApply> selectFundTransferApplyList(FundTransferApply fundTransferApply)
    {
        return fundTransferApplyMapper.selectFundTransferApplyList(fundTransferApply);
    }

    /**
     * 根据老人ID查询资金划拨申请列表
     *
     * @param elderId 老人ID
     * @return 资金划拨申请集合
     */
    @Override
    public List<FundTransferApply> selectFundTransferApplyByElderId(Long elderId)
    {
        return fundTransferApplyMapper.selectFundTransferApplyByElderId(elderId);
    }

    /**
     * 根据机构ID查询资金划拨申请列表
     *
     * @param institutionId 机构ID
     * @return 资金划拨申请集合
     */
    @Override
    public List<FundTransferApply> selectFundTransferApplyByInstitutionId(Long institutionId)
    {
        return fundTransferApplyMapper.selectFundTransferApplyByInstitutionId(institutionId);
    }

    /**
     * 查询老人的待划拨划拨单列表
     *
     * @param elderId 老人ID
     * @return 划拨单集合
     */
    @Override
    public List<FundTransfer> selectPendingTransferByElderId(Long elderId)
    {
        return fundTransferApplyMapper.selectPendingTransferByElderId(elderId);
    }

    /**
     * 新增资金划拨申请
     *
     * @param fundTransferApply 资金划拨申请
     * @return 结果
     */
    @Override
    public int insertFundTransferApply(FundTransferApply fundTransferApply)
    {
        bankPayoutService.checkScope(fundTransferApply.getInstitutionId());
        fundTransferApply.setApplyStatus("draft");
        clearApproval(fundTransferApply);
        fundTransferApply.setCreateTime(DateUtils.getNowDate());
        return fundTransferApplyMapper.insertFundTransferApply(fundTransferApply);
    }

    /**
     * 创建资金划拨申请（包含明细）
     *
     * @param fundTransferApply 资金划拨申请
     * @param transferIds 选择的划拨单ID列表
     * @return 结果
     */
    @Override
    @Transactional
    public int createFundTransferApply(FundTransferApply fundTransferApply, List<Long> transferIds)
    {
        bankPayoutService.checkScope(fundTransferApply.getInstitutionId());
        clearApproval(fundTransferApply);
        if (transferIds == null || transferIds.isEmpty()
                || new java.util.HashSet<>(transferIds).size() != transferIds.size())
        { throw new com.ruoyi.common.exception.ServiceException("必须选择不重复的拨付明细"); }
        // 1. 生成申请单号
        String applyNo = fundTransferApplyMapper.generateApplyNo();
        if (applyNo == null || applyNo.isEmpty()) {
            applyNo = "SQF" + System.currentTimeMillis();
        }
        fundTransferApply.setApplyNo(applyNo);
        fundTransferApply.setApplyStatus("pending_family");
        fundTransferApply.setCreateTime(DateUtils.getNowDate());

        // 2. 计算申请总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<FundTransferApplyDetail> details = new java.util.ArrayList<>();

        for (Long transferId : transferIds) {
            FundTransfer transfer = settlementMapper.lockTransfer(transferId);
            if (transfer == null || !java.util.Objects.equals(transfer.getInstitutionId(), fundTransferApply.getInstitutionId())
                    || !java.util.Objects.equals(transfer.getElderId(), fundTransferApply.getElderId())
                    || !Integer.valueOf(1).equals(transfer.getBankEligible()) || transfer.getBankTransactionId() != null
                    || transfer.getApplyId() != null || !"0".equals(transfer.getIsPaid()) || !"pending".equals(transfer.getStatus()))
            { throw new com.ruoyi.common.exception.ServiceException("选择了其他机构、老人、历史或已处理的拨付明细"); }
            if (transfer != null && "0".equals(transfer.getIsPaid()) && "pending".equals(transfer.getStatus())) {
                totalAmount = totalAmount.add(transfer.getTransferAmount());

                FundTransferApplyDetail detail = new FundTransferApplyDetail();
                detail.setTransferId(transferId);
                detail.setElderId(fundTransferApply.getElderId());
                detail.setTransferAmount(transfer.getTransferAmount());
                detail.setBillingMonth(transfer.getBillingMonth());
                details.add(detail);
            }
        }

        fundTransferApply.setApplyAmount(totalAmount);

        // 3. 插入申请主表
        int result = fundTransferApplyMapper.insertFundTransferApply(fundTransferApply);
        if (result <= 0) {
            throw new RuntimeException("创建划拨申请失败");
        }

        Long applyId = fundTransferApply.getApplyId();

        // 4. 插入申请明细
        for (FundTransferApplyDetail detail : details) {
            detail.setApplyId(applyId);
        }
        fundTransferApplyDetailMapper.batchInsertFundTransferApplyDetail(details);

        return result;
    }

    /**
     * 修改资金划拨申请
     *
     * @param fundTransferApply 资金划拨申请
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFundTransferApply(FundTransferApply fundTransferApply)
    {
        FundTransferApply existing = editable(fundTransferApply.getApplyId());
        if (fundTransferApply.getApplyStatus() != null && !"draft".equals(fundTransferApply.getApplyStatus())
                && !"pending_family".equals(fundTransferApply.getApplyStatus()))
        { throw new com.ruoyi.common.exception.ServiceException("不能通过通用编辑修改审批结果"); }
        fundTransferApply.setInstitutionId(existing.getInstitutionId());
        fundTransferApply.setElderId(existing.getElderId());
        clearApproval(fundTransferApply);
        fundTransferApply.setUpdateTime(DateUtils.getNowDate());
        return fundTransferApplyMapper.updateFundTransferApply(fundTransferApply);
    }

    /**
     * 批量删除资金划拨申请
     *
     * @param applyIds 需要删除的资金划拨申请主键集合
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFundTransferApplyByApplyIds(Long[] applyIds)
    {
        for (Long id : applyIds) { editable(id); }
        return fundTransferApplyMapper.deleteFundTransferApplyByApplyIds(applyIds);
    }

    /**
     * 删除资金划拨申请信息
     *
     * @param applyId 资金划拨申请主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFundTransferApplyByApplyId(Long applyId)
    {
        editable(applyId);
        return fundTransferApplyMapper.deleteFundTransferApplyByApplyId(applyId);
    }

    private FundTransferApply editable(Long id)
    {
        FundTransferApply apply = settlementMapper.lockApply(id);
        if (apply == null) { throw new com.ruoyi.common.exception.ServiceException("拨付申请不存在"); }
        bankPayoutService.checkScope(apply.getInstitutionId());
        if (!("draft".equals(apply.getApplyStatus()) || "withdrawn".equals(apply.getApplyStatus()))
                || settlementMapper.linkedApplication(id, "SERVICE") > 0)
        { throw new com.ruoyi.common.exception.ServiceException("已提交或银行关联申请禁止编辑删除"); }
        return apply;
    }

    private void clearApproval(FundTransferApply apply)
    {
        apply.setApprover(null); apply.setApproveTime(null); apply.setApproveRemark(null);
        apply.setFamilyApproveTime(null); apply.setFamilyApproveOpinion(null); apply.setFamilyConfirmName(null);
        apply.setActualAmount(null); apply.setUseTime(null);
    }

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
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int familyApprove(Long applyId, boolean approved, String opinion, String approverName, String relation, String phone)
    {
        // 1. 查询申请信息
        FundTransferApply apply = settlementMapper.lockApply(applyId);
        if (apply == null) {
            throw new RuntimeException("资金划拨申请不存在");
        }

        // 2. 验证状态
        if (!"pending_family".equals(apply.getApplyStatus())) {
            throw new RuntimeException("当前状态不允许家属审批");
        }

        // 3. 更新审批信息
        FundTransferApply updateApply = new FundTransferApply();
        updateApply.setApplyId(applyId);
        updateApply.setFamilyApproveOpinion(opinion);
        updateApply.setFamilyConfirmName(approverName);
        updateApply.setFamilyRelation(relation);
        updateApply.setFamilyPhone(phone);
        updateApply.setFamilyApproveTime(DateUtils.getNowDate());
        updateApply.setApplyStatus(approved ? "pending_supervision" : "rejected");
        updateApply.setUpdateTime(DateUtils.getNowDate());

        return fundTransferApplyMapper.updateFundTransferApply(updateApply);
    }

    /**
     * 监管部门审批资金划拨申请
     *
     * @param applyId 资金划拨申请主键
     * @param approved 是否通过
     * @param remark 审批意见
     * @param approver 审批人
     * @return 结果
     */
    @Override
    @Transactional
    public int supervisionApprove(Long applyId, boolean approved, String remark, String approver)
    {
        FundTransferApply apply = settlementMapper.lockApply(applyId);
        if (apply == null) { throw new com.ruoyi.common.exception.ServiceException("拨付申请不存在"); }
        bankPayoutService.checkScope(apply.getInstitutionId());
        if (!"pending_supervision".equals(apply.getApplyStatus()))
        { throw new com.ruoyi.common.exception.ServiceException("当前申请状态不允许审批"); }
        if (approved)
        {
            List<FundTransferApplyDetail> details = fundTransferApplyDetailMapper.selectFundTransferApplyDetailByApplyId(applyId);
            if (details == null || details.isEmpty()) { throw new com.ruoyi.common.exception.ServiceException("缺少拨付明细"); }
            BigDecimal total = BigDecimal.ZERO;
            java.util.Set<Long> seen = new java.util.HashSet<>();
            for (FundTransferApplyDetail detail : details)
            {
                FundTransfer transfer = settlementMapper.lockTransfer(detail.getTransferId());
                if (transfer == null || !seen.add(transfer.getTransferId())
                        || !java.util.Objects.equals(transfer.getInstitutionId(), apply.getInstitutionId())
                        || !java.util.Objects.equals(transfer.getElderId(), detail.getElderId())
                        || !"SERVICE".equals(transfer.getBalanceType())
                        || detail.getTransferAmount() == null
                        || detail.getTransferAmount().compareTo(transfer.getTransferAmount()) != 0
                        || settlementMapper.linkApply(transfer.getTransferId(), applyId, approver) != 1)
                { throw new com.ruoyi.common.exception.ServiceException("拨付明细不一致、已占用或属于历史资金"); }
                total = total.add(detail.getTransferAmount());
            }
            if (apply.getApplyAmount() == null || total.compareTo(apply.getApplyAmount()) != 0)
            { throw new com.ruoyi.common.exception.ServiceException("申请金额与拨付明细合计不一致"); }
        }
        FundTransferApply update = new FundTransferApply();
        update.setApplyId(applyId);
        update.setApplyStatus(approved ? "approved" : "rejected");
        update.setApprover(approver);
        update.setApproveTime(DateUtils.getNowDate());
        update.setApproveRemark(remark);
        update.setActualAmount(BigDecimal.ZERO);
        return fundTransferApplyMapper.updateFundTransferApply(update);
    }

    /**
     * 执行划拨
     *
     * @param applyId 资金划拨申请主键
     * @return 结果
     */
    @Override
    @Transactional
    public int executeTransfer(Long applyId)
    {
        FundTransferApply apply = settlementMapper.lockApply(applyId);
        if (apply == null || !"approved".equals(apply.getApplyStatus()))
        { throw new com.ruoyi.common.exception.ServiceException("申请未批准"); }
        bankPayoutService.checkScope(apply.getInstitutionId());
        for (FundTransferApplyDetail detail : fundTransferApplyDetailMapper.selectFundTransferApplyDetailByApplyId(applyId))
        { bankPayoutService.queue(detail.getTransferId(), com.ruoyi.common.utils.SecurityUtils.getUsername()); }
        return 1;
    }

    /**
     * 内部执行划拨逻辑
     *
     * @param apply 划拨申请
     */

}
