package com.ruoyi.service.pension.impl;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.mapper.pension.FundTransferApplyMapper;
import com.ruoyi.mapper.pension.FundTransferApplyDetailMapper;
import com.ruoyi.mapper.pension.FundTransferMapper;
import com.ruoyi.domain.pension.FundTransferApply;
import com.ruoyi.domain.pension.FundTransferApplyDetail;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.service.pension.IFundTransferApplyService;
import com.ruoyi.service.pension.IAccountInfoService;
import com.ruoyi.service.pension.ISupervisionAccountLogService;

/**
 * 资金划拨申请Service业务层处理
 *
 * @author ruoyi
 * @date 2026-01-28
 */
@Service
public class FundTransferApplyServiceImpl implements IFundTransferApplyService
{
    private static final Logger log = LoggerFactory.getLogger(FundTransferApplyServiceImpl.class);

    @Autowired
    private FundTransferApplyMapper fundTransferApplyMapper;

    @Autowired
    private FundTransferApplyDetailMapper fundTransferApplyDetailMapper;

    @Autowired
    private FundTransferMapper fundTransferMapper;

    @Autowired
    private IAccountInfoService accountInfoService;

    @Autowired
    private ISupervisionAccountLogService supervisionAccountLogService;

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
            FundTransfer transfer = fundTransferMapper.selectFundTransferByTransferId(transferId);
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
    public int updateFundTransferApply(FundTransferApply fundTransferApply)
    {
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
    public int deleteFundTransferApplyByApplyIds(Long[] applyIds)
    {
        return fundTransferApplyMapper.deleteFundTransferApplyByApplyIds(applyIds);
    }

    /**
     * 删除资金划拨申请信息
     *
     * @param applyId 资金划拨申请主键
     * @return 结果
     */
    @Override
    public int deleteFundTransferApplyByApplyId(Long applyId)
    {
        return fundTransferApplyMapper.deleteFundTransferApplyByApplyId(applyId);
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
    public int familyApprove(Long applyId, boolean approved, String opinion, String approverName, String relation, String phone)
    {
        // 1. 查询申请信息
        FundTransferApply apply = fundTransferApplyMapper.selectFundTransferApplyByApplyId(applyId);
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
        // 1. 查询申请信息
        FundTransferApply apply = fundTransferApplyMapper.selectFundTransferApplyByApplyId(applyId);
        if (apply == null) {
            throw new RuntimeException("资金划拨申请不存在");
        }

        // 2. 验证状态
        if (!"pending_supervision".equals(apply.getApplyStatus())) {
            throw new RuntimeException("当前状态不允许监管部门审批");
        }

        // 3. 如果审批通过，执行划拨
        if (approved) {
            executeTransferInternal(apply);
        }

        // 4. 更新审批信息
        FundTransferApply updateApply = new FundTransferApply();
        updateApply.setApplyId(applyId);
        updateApply.setApprover(approver);
        updateApply.setApproveTime(DateUtils.getNowDate());
        updateApply.setApproveRemark(remark);
        updateApply.setApplyStatus(approved ? "approved" : "rejected");
        updateApply.setUpdateTime(DateUtils.getNowDate());
        updateApply.setActualAmount(apply.getApplyAmount());

        return fundTransferApplyMapper.updateFundTransferApply(updateApply);
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
        FundTransferApply apply = fundTransferApplyMapper.selectFundTransferApplyByApplyId(applyId);
        if (apply == null) {
            throw new RuntimeException("资金划拨申请不存在");
        }

        if (!"approved".equals(apply.getApplyStatus())) {
            throw new RuntimeException("申请状态不正确，无法执行划拨");
        }

        executeTransferInternal(apply);

        // 更新申请状态为已完成
        FundTransferApply updateApply = new FundTransferApply();
        updateApply.setApplyId(applyId);
        updateApply.setApplyStatus("completed");
        updateApply.setUseTime(DateUtils.getNowDate());
        updateApply.setUpdateTime(DateUtils.getNowDate());

        return fundTransferApplyMapper.updateFundTransferApply(updateApply);
    }

    /**
     * 内部执行划拨逻辑
     *
     * @param apply 划拨申请
     */
    private void executeTransferInternal(FundTransferApply apply)
    {
        // 获取申请明细
        List<FundTransferApplyDetail> details = fundTransferApplyDetailMapper.selectFundTransferApplyDetailByApplyId(apply.getApplyId());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (FundTransferApplyDetail detail : details) {
            // 查询划拨单
            FundTransfer transfer = fundTransferMapper.selectFundTransferByTransferId(detail.getTransferId());
            if (transfer == null) {
                log.warn("划拨单不存在，跳过：{}", detail.getTransferId());
                continue;
            }

            // 检查是否已划拨
            if ("1".equals(transfer.getIsPaid())) {
                log.warn("划拨单已划拨，跳过：{}", detail.getTransferId());
                continue;
            }

            // 查询老人账户
            AccountInfo account = accountInfoService.selectAccountInfoByElderId(detail.getElderId());
            if (account == null) {
                throw new RuntimeException("老人账户不存在，elderId：" + detail.getElderId());
            }

            // 检查余额是否足够
            BigDecimal serviceBalance = account.getServiceBalance() != null ? account.getServiceBalance() : BigDecimal.ZERO;
            BigDecimal transferAmount = detail.getTransferAmount();

            if (serviceBalance.compareTo(transferAmount) < 0) {
                throw new RuntimeException("老人账户余额不足，当前余额：" + serviceBalance + "，需要划拨：" + transferAmount);
            }

            // 扣除服务余额
            BigDecimal newServiceBalance = serviceBalance.subtract(transferAmount);
            BigDecimal newTotalBalance = account.getTotalBalance().subtract(transferAmount);

            account.setServiceBalance(newServiceBalance);
            account.setTotalBalance(newTotalBalance);
            account.setUpdateTime(DateUtils.getNowDate());

            accountInfoService.updateAccountInfo(account);

            // 记录监管账户流水
            supervisionAccountLogService.recordTransferOut(
                apply.getInstitutionId(),
                apply.getApplyId(),
                transferAmount,
                "月度服务费划拨-" + detail.getBillingMonth() + "-" + transfer.getTransferNo(),
                "基本账户"
            );

            // 更新划拨单状态
            FundTransfer updateTransfer = new FundTransfer();
            updateTransfer.setTransferId(detail.getTransferId());
            updateTransfer.setIsPaid("1");
            updateTransfer.setPaidTime(DateUtils.getNowDate());
            updateTransfer.setPaidMethod("manual");
            updateTransfer.setApplyId(apply.getApplyId());
            updateTransfer.setStatus("completed");
            updateTransfer.setUpdateTime(DateUtils.getNowDate());

            fundTransferMapper.updateFundTransfer(updateTransfer);

            totalAmount = totalAmount.add(transferAmount);
        }

        log.info("执行划拨申请 {} 完成，总划拨金额：{}", apply.getApplyNo(), totalAmount);
    }
}
