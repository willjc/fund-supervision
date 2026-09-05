package com.ruoyi.service.bank.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import com.ruoyi.bank.gateway.*;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.domain.bank.*;
import com.ruoyi.domain.pension.*;
import com.ruoyi.mapper.bank.*;
import com.ruoyi.mapper.pension.*;
import com.ruoyi.mapper.PensionInstitutionMapper;
import com.ruoyi.service.bank.IBankMerchantConfigService;
import com.ruoyi.service.pension.*;

/** 一个拨付单对应一个老人/资金类别。所有银行 I/O 均在本地事务之外。 */
@Service
public class BankPayoutService
{
    @Autowired private BankGateway gateway;
    @Autowired private BankTransactionMapper transactions;
    @Autowired private BankSettlementMapper settlement;
    @Autowired private FundTransferMapper transfers;
    @Autowired private AccountInfoMapper accounts;
    @Autowired private PensionInstitutionMapper institutions;
    @Autowired private IBankMerchantConfigService merchants;
    @Autowired private IExpenseRecordService expenses;
    @Autowired private ISupervisionAccountLogService ledger;
    @Autowired private PlatformTransactionManager transactionManager;
    @Value("${bank.integration.payout-enabled:false}") private boolean enabled;

    public boolean isEnabled() { return enabled && gateway.supportsPayout(); }

    public void checkScope(Long institutionId)
    {
        Long user = com.ruoyi.common.utils.SecurityUtils.getUserId();
        if (!com.ruoyi.common.utils.SecurityUtils.isAdmin(user)
                && settlement.hasScope(user, institutionId) == 0)
        { throw new ServiceException("无权操作该机构银行资金"); }
    }

    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public int queue(Long id, String operator)
    {
        FundTransfer transfer = settlement.lockTransfer(id);
        if (transfer == null) { throw new ServiceException("拨付单不存在"); }
        checkScope(transfer.getInstitutionId());
        if (!Integer.valueOf(1).equals(transfer.getBankEligible()) || !"pending".equals(transfer.getStatus())
                || !"0".equals(transfer.getIsPaid()) || transfer.getBankTransactionId() != null)
        { throw new ServiceException("历史或非待处理拨付不能提交银行；已发起交易请查原单"); }
        return 1; // 已持久化的待拨付单由任务发送；审批事务内不调用银行。
    }

    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public int approve(Long id, String operator, boolean approved, String reason)
    {
        queue(id, operator);
        return settlement.approveTransfer(id, operator, approved ? "pending" : "cancelled", reason);
    }

    public BankTransaction submit(Long transferId, String operator, boolean retry)
    {
        if (!isEnabled()) { throw new ServiceException("真实拨付未启用或银行监管协议尚未验收"); }
        FundTransfer original = transfers.selectFundTransferByTransferId(transferId);
        if (original == null) { throw new ServiceException("拨付单不存在"); }
        BankMerchantConfig merchant = merchants.selectEnabledByInstitutionId(original.getInstitutionId());
        validateMerchant(merchant);
        BankResult balance = gateway.queryBalance(merchant.getSettlementAccountNo(), merchant.getSettlementAccountName());
        if (balance == null || !"SUCCESS".equals(balance.getStatus()) || balance.getAvailableBalance() == null
                || balance.getAvailableBalance().compareTo(original.getTransferAmount()) < 0)
        { throw new ServiceException("银行可用余额不足或余额未能确认，保留待拨付"); }

        BankTransaction tx = new TransactionTemplate(transactionManager).execute(status -> prepare(transferId, merchant, operator, retry));
        // 请求号及预占已提交；进程即使在此中断，恢复也只查询原单，不重新发送。
        try
        {
            BankResult accepted = gateway.submitPayout(request(tx));
            // 受理返回不是完成凭证。所有最终状态均通过原请求查询确认。
            if (accepted != null && "FAILED".equals(accepted.getStatus()))
            {
                // submit 的失败也查原请求；不能把通信/网关拒绝误作业务最终失败。
                return reconcile(tx.getRequestNo());
            }
        }
        catch (Exception e)
        {
            BankPaymentReconciler.schedule(settlement, tx);
            return transactions.selectByRequestNo(tx.getRequestNo());
        }
        return reconcile(tx.getRequestNo());
    }

    private BankTransaction prepare(Long id, BankMerchantConfig expected, String operator, boolean retry)
    {
        institutions.selectPensionInstitutionForUpdate(expected.getInstitutionId());
        BankMerchantConfig merchant = merchants.selectEnabledByInstitutionId(expected.getInstitutionId());
        validateMerchant(merchant);
        if (!Objects.equals(JSON.toJSONString(expected), JSON.toJSONString(merchant)))
        { throw new ServiceException("银行绑定已变更，请重新发起"); }
        FundTransfer transfer = settlement.lockTransfer(id);
        if (transfer == null || !Objects.equals(transfer.getInstitutionId(), merchant.getInstitutionId())
                || !Integer.valueOf(1).equals(transfer.getBankEligible()) || transfer.getSourceKey() == null
                || transfer.getElderId() == null || transfer.getTransferAmount() == null
                || transfer.getTransferAmount().signum() <= 0 || "1".equals(transfer.getIsPaid()))
        { throw new ServiceException("拨付单无银行执行资格或已经完成，历史资金不得补发"); }
        BankTransaction previous = transactions.selectByBusiness("TRANSFER", id);
        if (previous != null && (!retry || !"FAILED".equals(previous.getStatus())
                || !"DONE".equals(previous.getBookingStatus()) || !"failed".equals(transfer.getStatus())))
        { throw new ServiceException("已有银行请求，只能查询原请求；仅明确失败可重试"); }
        if (previous == null && !"pending".equals(transfer.getStatus()))
        { throw new ServiceException("当前拨付单不允许执行"); }
        validateOrigin(transfer, merchant);
        AccountInfo account = accounts.selectAccountInfoForUpdate(transfer.getElderId(), transfer.getInstitutionId());
        if (account == null) { throw new ServiceException("老人机构账户不存在"); }
        BigDecimal service = serviceAmount(transfer), deposit = depositAmount(transfer);
        require(settlement.reserve(account.getAccountId(), service, deposit), "可用银行来源余额不足，不能预占");
        BankPayoutRequest request = new BankPayoutRequest();
        request.setRequestNo("BT" + IdUtils.fastSimpleUUID().toUpperCase());
        request.setRequestTime(new Date());
        request.setPayerAccountNo(merchant.getSettlementAccountNo());
        request.setPayerAccountName(merchant.getSettlementAccountName());
        request.setPayeeAccountNo(merchant.getBasicAccountNo());
        request.setPayeeAccountName(merchant.getBasicAccountName());
        request.setCrossBank(Integer.valueOf(1).equals(merchant.getCrossBank()));
        request.setPayeeBankNo(merchant.getBasicBankCode());
        request.setAmount(transfer.getTransferAmount());
        request.setRemark("养老资金拨付-" + transfer.getTransferNo());
        request.validate();
        BankTransaction tx = new BankTransaction();
        tx.setRequestNo(request.getRequestNo());
        tx.setBusinessType("TRANSFER");
        tx.setBusinessId(id);
        tx.setInstitutionId(transfer.getInstitutionId());
        tx.setMerId(merchant.getMerId());
        tx.setBankCode("ZZBANK");
        tx.setChannelType("SUPERVISION");
        tx.setEnvironment(merchant.getEnvironment());
        tx.setAmount(transfer.getTransferAmount());
        tx.setSnapshotJson(JSON.toJSONString(request));
        tx.setStatus("PENDING");
        tx.setAttemptNo(previous == null ? 1 : previous.getAttemptNo() + 1);
        tx.setCreateTime(request.getRequestTime());
        tx.setUpdateTime(request.getRequestTime());
        tx.setNextQueryTime(new Date(request.getRequestTime().getTime() + 60000L));
        require(transactions.insert(tx), "保存银行拨付请求失败");
        require(settlement.attach(id, tx.getTransactionId(), operator), "拨付单已被处理");
        return tx;
    }

    public BankTransaction reconcile(String requestNo)
    {
        BankTransaction tx = transactions.selectByRequestNo(requestNo);
        if (tx == null || !"TRANSFER".equals(tx.getBusinessType())) { throw new ServiceException("银行拨付交易不存在"); }
        if (("DONE".equals(tx.getBookingStatus()) && tx.getReturnTime() == null)
                || "REVERSED".equals(tx.getBookingStatus())) { return tx; }
        if (settlement.claim(tx.getTransactionId()) != 1) { return tx; }
        try
        {
            if (tx.getReturnTime() == null && !"SUCCESS".equals(tx.getBankStatus()) && !"FAILED".equals(tx.getBankStatus()))
            {
                BankResult result = gateway.queryPayout(request(tx));
                validateResult(tx, result);
                tx.setBankStatus(result.getStatus());
                tx.setBankSerialNo(result.getBankSerialNo());
                tx.setBankTime(result.getBankTransactionTime());
                tx.setResponseCode(bounded(result.getResponseCode(), 64));
                tx.setResponseMessage(bounded(result.getResponseMessage(), 500));
                settlement.observe(tx);
            }
            final String savedRequest = requestNo;
            new TransactionTemplate(transactionManager).execute(status -> { book(savedRequest); return null; });
            return transactions.selectByRequestNo(requestNo);
        }
        finally
        {
            BankPaymentReconciler.schedule(settlement, transactions.selectByRequestNo(requestNo));
            settlement.releaseClaim(tx.getTransactionId());
        }
    }

    private void book(String requestNo)
    {
        BankTransaction snapshot = transactions.selectByRequestNo(requestNo);
        institutions.selectPensionInstitutionForUpdate(snapshot.getInstitutionId());
        FundTransfer transfer = settlement.lockTransfer(snapshot.getBusinessId());
        BankTransaction tx = transactions.selectByRequestNoForUpdate(requestNo);
        if (!Objects.equals(transfer.getBankTransactionId(), tx.getTransactionId()))
        { throw new ServiceException("非当前拨付交易，需人工核查"); }
        boolean returned = tx.getReturnTime() != null;
        if ("REVERSED".equals(tx.getBookingStatus()) || (!returned && "DONE".equals(tx.getBookingStatus()))) { return; }
        boolean success = "SUCCESS".equals(tx.getBankStatus());
        boolean failed = "FAILED".equals(tx.getBankStatus());
        if (!returned && !success && !failed) { return; }
        AccountInfo account = accounts.selectAccountInfoForUpdate(transfer.getElderId(), transfer.getInstitutionId());
        if (account == null) { throw new ServiceException("拨付账户不存在"); }
        BigDecimal service = serviceAmount(transfer), deposit = depositAmount(transfer);
        boolean wasDebited = "1".equals(transfer.getIsPaid());
        if (returned && wasDebited)
        {
            require(settlement.reverseBalance(account.getAccountId(), service, deposit), "退汇冲正余额失败");
            recordMovement(transfer, tx, account, true);
        }
        else
        {
            require(settlement.settleBalance(account.getAccountId(), service, deposit,
                    success && !returned ? service : BigDecimal.ZERO,
                    success && !returned ? deposit : BigDecimal.ZERO), "预占或账户金额不一致");
            if (success && !returned) { recordMovement(transfer, tx, account, false); }
        }
        String state = returned ? "returned" : success ? "completed" : "failed";
        require(settlement.transferResult(transfer.getTransferId(), state, success && !returned ? "1" : "0",
                success && !returned ? "1" : "2", tx.getBankSerialNo(),
                returned ? tx.getReturnReason() : tx.getResponseMessage()), "拨付结果保存失败");
        tx.setStatus(returned ? "RETURNED" : success ? "SUCCESS" : "FAILED");
        tx.setBookingStatus(returned ? "REVERSED" : "DONE");
        require(settlement.finish(tx), "银行记账状态保存失败");
        if (transfer.getApplyId() != null)
        {
            if ("DEPOSIT".equals(transfer.getBalanceType()))
            {
                require(settlement.depositResult(transfer.getApplyId(), state,
                        success && !returned ? transfer.getTransferAmount() : BigDecimal.ZERO), "押金申请结果更新失败");
            }
            else { require(settlement.refreshApply(transfer.getApplyId()), "拨付申请汇总失败"); }
        }
    }

    /** 仅供未来已验证的银行通知适配器调用，不能直接绑定 HTTP 参数。 */
    public void recordVerifiedReturn(String requestNo, String time, String reason)
    {
        if (time == null || time.length() > 30 || reason == null || reason.length() > 256)
        { throw new ServiceException("退汇通知字段不完整"); }
        BankTransaction tx = transactions.selectByRequestNo(requestNo);
        if (tx == null || !"TRANSFER".equals(tx.getBusinessType())) { throw new ServiceException("原拨付不存在"); }
        settlement.recordReturn(requestNo, time, reason);
        reconcile(requestNo);
    }

    private void recordMovement(FundTransfer transfer, BankTransaction tx, AccountInfo account, boolean reverse)
    {
        BigDecimal amount = transfer.getTransferAmount();
        String description = (reverse ? "银行退汇冲正-" : "银行拨付-") + tx.getRequestNo();
        require(expenses.createExpenseRecord(transfer.getElderId(), account.getAccountId(),
                "DEPOSIT".equals(transfer.getBalanceType()) ? "deposit" : "service", reverse ? "income" : "expense",
                amount, description, tx.getTransactionId(), "bank_transfer", account.getTotalBalance(),
                reverse ? account.getTotalBalance().add(amount) : account.getTotalBalance().subtract(amount)), "费用流水保存失败");
        SupervisionAccountLog log = reverse ? ledger.recordIncome(transfer.getInstitutionId(), null, amount, description, "bank-return")
                : ledger.recordTransferOut(transfer.getInstitutionId(), transfer.getTransferId(), amount, description, "基本账户");
        if (log == null || log.getLogId() == null) { throw new ServiceException("监管流水保存失败"); }
    }

    private void validateMerchant(BankMerchantConfig m)
    {
        if (m == null || !Integer.valueOf(1).equals(m.getPayoutEnabled())
                || m.getSupervisionAgreementNo() == null || m.getSupervisionAgreementNo().trim().isEmpty())
        { throw new ServiceException("机构银行拨付未启用或监管签约未确认"); }
    }

    private BankPayoutRequest request(BankTransaction tx) { return JSON.parseObject(tx.getSnapshotJson(), BankPayoutRequest.class); }

    private void validateResult(BankTransaction tx, BankResult result)
    {
        if (result == null) { throw new ServiceException("银行拨付查单结果为空"); }
        if (("SUCCESS".equals(result.getStatus()) || "FAILED".equals(result.getStatus()))
                && !Objects.equals(tx.getRequestNo(), result.getRequestNo()))
        { throw new ServiceException("银行拨付结果请求号不匹配，禁止记账或释放预占"); }
        if ("SUCCESS".equals(result.getStatus()))
        {
            BankPayoutRequest original = request(tx);
            if (result.getBankSerialNo() == null || result.getBankSerialNo().trim().isEmpty()
                    || result.getBankTransactionTime() == null || result.getPaidAmount() == null
                    || tx.getAmount().compareTo(result.getPaidAmount()) != 0
                    || !Objects.equals(original.getPayerAccountNo(), result.getPayerAccountNo())
                    || !Objects.equals(original.getPayeeAccountNo(), result.getPayeeAccountNo()))
            { throw new ServiceException("银行拨付结果的流水、时间、金额或账户不匹配，禁止扣账"); }
        }
    }

    private void validateOrigin(FundTransfer transfer, BankMerchantConfig merchant)
    {
        if ("DEPOSIT".equals(transfer.getBalanceType()))
        {
            DepositApply apply = settlement.lockDeposit(transfer.getApplyId());
            if (apply == null || !("approved".equals(apply.getApplyStatus()) || "failed".equals(apply.getApplyStatus()))
                    || !Objects.equals("DEPOSIT:" + apply.getApplyId(), transfer.getSourceKey())
                    || !Objects.equals(apply.getInstitutionId(), transfer.getInstitutionId())
                    || !Objects.equals(apply.getElderId(), transfer.getElderId())
                    || apply.getApplyAmount().compareTo(transfer.getTransferAmount()) != 0)
            { throw new ServiceException("押金拨付缺少对应审批来源"); }
            return;
        }
        BankTransaction payment = transactions.selectByBusiness("PAY", transfer.getOrderId());
        if (!(Objects.equals("FIRST:" + transfer.getOrderId(), transfer.getSourceKey())
                || Objects.equals("MONTH:" + transfer.getOrderId() + ":" + transfer.getBillingMonth(), transfer.getSourceKey()))
                || payment == null || !"SUCCESS".equals(payment.getStatus()) || !"SUCCESS".equals(payment.getBankStatus())
                || !Objects.equals(payment.getInstitutionId(), transfer.getInstitutionId())
                || !Objects.equals(payment.getMerId(), merchant.getMerId())
                || !Objects.equals(payment.getEnvironment(), merchant.getEnvironment()))
        { throw new ServiceException("服务费拨付缺少匹配的新银行支付来源"); }
    }

    private BigDecimal serviceAmount(FundTransfer transfer)
    {
        if (!"SERVICE".equals(transfer.getBalanceType()) && !"DEPOSIT".equals(transfer.getBalanceType()))
        { throw new ServiceException("不支持的拨付资金类别"); }
        return "SERVICE".equals(transfer.getBalanceType()) ? transfer.getTransferAmount() : BigDecimal.ZERO;
    }
    private BigDecimal depositAmount(FundTransfer transfer) { return "DEPOSIT".equals(transfer.getBalanceType()) ? transfer.getTransferAmount() : BigDecimal.ZERO; }
    private void require(int rows, String message) { if (rows != 1) { throw new ServiceException(message); } }
    private String bounded(String value, int max) { return value == null ? null : value.substring(0, Math.min(max, value.length())); }
}
