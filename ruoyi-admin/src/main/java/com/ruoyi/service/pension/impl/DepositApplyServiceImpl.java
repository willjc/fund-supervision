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
import com.ruoyi.service.pension.IExpenseRecordService;
import com.ruoyi.service.pension.ISupervisionAccountLogService;
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
    private IExpenseRecordService expenseRecordService;

    @Autowired
    private ISupervisionAccountLogService supervisionAccountLogService;

    @Autowired
    private IFundTransferService fundTransferService;

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
    public int updateDepositApply(DepositApply depositApply)
    {
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
    public int deleteDepositApplyByApplyIds(Long[] applyIds)
    {
        return depositApplyMapper.deleteDepositApplyByApplyIds(applyIds);
    }

    /**
     * 删除押金使用申请信息
     *
     * @param applyId 押金使用申请主键
     * @return 结果
     */
    @Override
    public int deleteDepositApplyByApplyId(Long applyId)
    {
        return depositApplyMapper.deleteDepositApplyByApplyId(applyId);
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
    public int familyApprove(Long applyId, String opinion, String approver, String rejectReason)
    {
        // 1. 查询申请信息
        DepositApply apply = depositApplyMapper.selectDepositApplyByApplyId(applyId);
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
    public int supervisionApprove(Long applyId, boolean approved, String remark, String approver)
    {
        // 1. 查询申请信息
        DepositApply apply = depositApplyMapper.selectDepositApplyByApplyId(applyId);
        if (apply == null) {
            throw new RuntimeException("押金使用申请不存在");
        }

        // 2. 验证状态(只有家属已审批或待监管审批状态才能审批)
        if (!"family_approved".equals(apply.getApplyStatus()) &&
            !"pending_supervision".equals(apply.getApplyStatus())) {
            throw new RuntimeException("当前状态不允许监管部门审批");
        }

        // 3. 如果审批通过，扣除押金余额
        if (approved) {
            Long accountId = apply.getAccountId();
            if (accountId == null) {
                throw new RuntimeException("申请中未关联账户信息");
            }

            AccountInfo account = accountInfoService.selectAccountInfoByAccountId(accountId);
            if (account == null) {
                throw new RuntimeException("账户不存在");
            }

            // 获取申请金额
            BigDecimal applyAmount = apply.getApplyAmount();
            if (applyAmount == null || applyAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("申请金额无效");
            }

            // 获取当前押金余额
            BigDecimal currentDepositBalance = account.getDepositBalance() != null ?
                account.getDepositBalance() : BigDecimal.ZERO;
            BigDecimal currentTotalBalance = account.getTotalBalance() != null ?
                account.getTotalBalance() : BigDecimal.ZERO;

            // 验证押金余额是否足够
            if (currentDepositBalance.compareTo(applyAmount) < 0) {
                throw new RuntimeException("押金余额不足，当前押金余额：" + currentDepositBalance + "元，申请金额：" + applyAmount + "元");
            }

            // 扣除押金余额（只扣押金，不影响服务费余额和会员余额）
            BigDecimal newDepositBalance = currentDepositBalance.subtract(applyAmount);
            BigDecimal newTotalBalance = currentTotalBalance.subtract(applyAmount);

            account.setDepositBalance(newDepositBalance);
            account.setTotalBalance(newTotalBalance);
            account.setUpdateTime(DateUtils.getNowDate());

            // 更新账户余额
            int accountUpdateResult = accountInfoService.updateAccountInfo(account);
            if (accountUpdateResult <= 0) {
                throw new RuntimeException("更新账户余额失败");
            }

            // 生成押金使用费用记录
            try {
                int recordResult = expenseRecordService.createDepositExpenseRecord(
                    apply.getElderId(),
                    accountId,
                    applyId,
                    applyAmount,
                    currentTotalBalance, // 扣除前余额
                    newTotalBalance    // 扣除后余额
                );
                if (recordResult <= 0) {
                    // 费用记录创建失败不影响主流程，只记录日志
                    System.err.println("创建费用记录失败，但审批流程继续执行");
                }
            } catch (Exception e) {
                // 费用记录创建失败不影响主流程，只记录日志
                System.err.println("创建费用记录异常：" + e.getMessage());
            }

            // 记录实际使用金额
            apply.setActualAmount(applyAmount);

            // 创建fund_transfer记录（统一管理所有划拨）
            Long transferId = null;
            try {
                String transferNo = "TRF" + System.currentTimeMillis() + String.format("%03d", (int)(Math.random() * 1000));
                String billingMonth = new java.text.SimpleDateFormat("yyyy-MM").format(apply.getCreateTime());

                FundTransfer fundTransfer = new FundTransfer();
                fundTransfer.setTransferNo(transferNo);
                fundTransfer.setTransferType("3"); // 3-特殊划拨（押金使用）
                fundTransfer.setTransferAmount(applyAmount);
                fundTransfer.setTransferDate(DateUtils.getNowDate());
                fundTransfer.setBillingMonth(billingMonth); // 使用申请日期作为账单月份
                fundTransfer.setElderCount(1);
                fundTransfer.setTransferStatus("1"); // 1-成功
                fundTransfer.setIsPaid("1"); // 已划拨
                fundTransfer.setPaidTime(DateUtils.getNowDate());
                fundTransfer.setPaidMethod("deposit"); // 标识来源为押金使用
                fundTransfer.setApplyId(applyId); // 关联押金申请ID
                fundTransfer.setElderId(apply.getElderId());
                fundTransfer.setInstitutionId(apply.getInstitutionId());
                fundTransfer.setStatus("completed"); // 已完成
                fundTransfer.setCreateBy(approver);
                fundTransfer.setCreateTime(DateUtils.getNowDate());
                fundTransfer.setRemark("押金使用-" + apply.getPurpose());

                fundTransferService.insertFundTransfer(fundTransfer);
                transferId = fundTransfer.getTransferId();
            } catch (Exception e) {
                // 创建fund_transfer失败不影响主流程，只记录日志
                System.err.println("创建划拨记录异常：" + e.getMessage());
            }

            // 记录监管账户流水（划拨到基本账户，关联transfer_id）
            try {
                supervisionAccountLogService.recordTransferOut(
                    apply.getInstitutionId(),
                    transferId,
                    applyAmount,
                    "押金使用划拨-" + apply.getPurpose(),
                    "基本账户"
                );
            } catch (Exception e) {
                // 流水记录创建失败不影响主流程，只记录日志
                System.err.println("记录监管账户流水异常：" + e.getMessage());
            }
        }

        // 4. 更新审批信息
        DepositApply updateApply = new DepositApply();
        updateApply.setApplyId(applyId);
        updateApply.setApprover(approver);
        updateApply.setApproveTime(DateUtils.getNowDate());
        updateApply.setApproveRemark(remark);
        updateApply.setApplyStatus(approved ? "approved" : "rejected");
        updateApply.setUpdateTime(DateUtils.getNowDate());

        // 如果审批通过，记录实际使用金额
        if (approved) {
            updateApply.setActualAmount(apply.getApplyAmount());
        }

        return depositApplyMapper.updateDepositApply(updateApply);
    }

    /**
     * 撤回押金使用申请
     *
     * @param applyId 押金使用申请主键
     * @return 结果
     */
    @Override
    public int withdrawApply(Long applyId)
    {
        // 1. 查询申请信息
        DepositApply apply = depositApplyMapper.selectDepositApplyByApplyId(applyId);
        if (apply == null) {
            throw new RuntimeException("押金使用申请不存在");
        }

        // 2. 验证状态(已通过、已驳回、已撤回状态不能撤回)
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
