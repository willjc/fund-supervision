package com.ruoyi.service.pension.impl;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.pension.DepositApplyMapper;
import com.ruoyi.domain.pension.DepositApply;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.service.pension.IDepositApplyService;
import com.ruoyi.service.pension.IAccountInfoService;

/**
 * 押金使用申请Service业务层处理
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
     * @param opinion 审批意见
     * @param approver 审批人
     * @return 结果
     */
    @Override
    public int familyApprove(Long applyId, String opinion, String approver)
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

        // 3. 更新审批信息
        DepositApply updateApply = new DepositApply();
        updateApply.setApplyId(applyId);
        updateApply.setFamilyApproveOpinion(opinion);
        updateApply.setFamilyApproveTime(DateUtils.getNowDate());
        updateApply.setApplyStatus("family_approved"); // 家属已审批,等待监管部门审批
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

            // 记录实际使用金额
            apply.setActualAmount(applyAmount);
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
