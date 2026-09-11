package com.ruoyi.service.bank.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.bank.gateway.BankGateway;
import com.ruoyi.bank.gateway.BankQueryRequest;
import com.ruoyi.bank.gateway.BankRefundRequest;
import com.ruoyi.bank.gateway.BankResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.RefundRecord;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.PensionInstitutionMapper;
import com.ruoyi.mapper.bank.BankSettlementMapper;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.mapper.pension.AccountInfoMapper;
import com.ruoyi.mapper.pension.RefundRecordMapper;
import com.ruoyi.service.bank.IBankMerchantConfigService;
import com.ruoyi.service.pension.IExpenseRecordService;
import com.ruoyi.service.pension.ISupervisionAccountLogService;

/**
 * 原路退款编排（uTxnRefund + uTxnQuery 查退款请求）。
 * 审批事务内只落退款单与 REFUND 交易；银行 I/O 一律在事务之外，
 * 终态以查询原退款请求为准，先持久化银行事实再本地记账。
 */
@Service
public class BankRefundService
{
    private static final int MAX_REFUND_ATTEMPTS = 40;

    @Autowired private BankGateway gateway;
    @Autowired private BankTransactionMapper transactions;
    @Autowired private BankSettlementMapper settlement;
    @Autowired private OrderInfoMapper orders;
    @Autowired private BankRefundTxWorker worker;

    /** 审批通过并发起银行退款：事务一落单转处理中，事务外提交银行并立即确认一次。 */
    public int approveAndSubmit(Long refundId, String approver, Long currentUserId)
    {
        BankTransaction tx = worker.prepare(refundId, approver, currentUserId);
        try
        {
            BankResult accepted = gateway.refundPayment(request(tx));
            if (accepted != null && "FAILED".equals(accepted.getStatus()))
            {
                // 银行业务明确拒绝（如头寸不足、金额超限）为终态失败；通信/网关异常走 catch 排队补查。
                return worker.failFast(tx.getRequestNo(), accepted);
            }
            if (accepted != null && accepted.getBankSerialNo() != null)
            {
                // 受理返回的 respTxnSsn 先落库，补查时可作为 origRespTxnSsn 供银行定位。
                tx.setBankStatus("UNKNOWN");
                tx.setBankSerialNo(accepted.getBankSerialNo());
                settlement.observe(tx);
            }
        }
        catch (Exception e)
        {
            BankPaymentReconciler.schedule(settlement, tx);
            return 1;
        }
        reconcile(tx.getRequestNo());
        return 1;
    }

    /** H5 申请时预检：订单可原路退款且剩余额度足够；权威校验仍在审批事务内。 */
    public void assertRefundable(Long orderId, Long elderId, Long institutionId, BigDecimal amount)
    {
        if (orderId == null || orderId == 0L)
        {
            throw new ServiceException("银行退款必须关联原支付订单");
        }
        OrderInfo order = orders.selectOrderInfoByOrderId(orderId);
        if (order == null || !elderId.equals(order.getElderId()) || !institutionId.equals(order.getInstitutionId()))
        {
            throw new ServiceException("退款订单与老人或机构不匹配");
        }
        if (!"1".equals(order.getOrderStatus()))
        {
            throw new ServiceException("只有已支付订单可以申请原路退款");
        }
        BankTransaction payment = transactions.selectByBusiness("PAY", orderId);
        if (payment == null || !"SUCCESS".equals(payment.getStatus())
                || !"SUCCESS".equals(payment.getBankStatus()))
        {
            throw new ServiceException("该订单没有银行成功支付记录，不能原路退款");
        }
        BigDecimal occupied = settlement.refundOccupied(orderId);
        if (occupied.add(amount).compareTo(payment.getAmount()) > 0)
        {
            throw new ServiceException("退款金额超过订单剩余可退额度");
        }
    }

    /** 查询退款请求并按终态记账；与后台补查任务共用。 */
    public BankTransaction reconcile(String requestNo)
    {
        BankTransaction tx = transactions.selectByRequestNo(requestNo);
        if (tx == null || !"REFUND".equals(tx.getBusinessType()))
        {
            throw new ServiceException("银行退款交易不存在");
        }
        if ("DONE".equals(tx.getBookingStatus()) || "REVERSED".equals(tx.getBookingStatus()))
        {
            return tx;
        }
        if (settlement.claim(tx.getTransactionId()) != 1)
        {
            return tx;
        }
        try
        {
            if (!"SUCCESS".equals(tx.getBankStatus()) && !"FAILED".equals(tx.getBankStatus()))
            {
                BankResult result = gateway.queryPayment(queryRequest(tx));
                if ("SUCCESS".equals(result.getStatus())
                        && (result.getPaidAmount() == null || tx.getAmount().compareTo(result.getPaidAmount()) != 0))
                {
                    throw new ServiceException("银行退款确认金额与退款单不一致，禁止扣账，转人工核查");
                }
                tx.setBankStatus(result.getStatus());
                tx.setBankSerialNo(result.getBankSerialNo());
                tx.setBankTime(result.getBankTransactionTime());
                tx.setResponseCode(bounded(result.getResponseCode(), 64));
                tx.setResponseMessage(bounded(result.getResponseMessage(), 500));
                settlement.observe(tx);
                tx = transactions.selectByRequestNo(requestNo);
            }
            if ("SUCCESS".equals(tx.getBankStatus()))
            {
                worker.book(requestNo);
            }
            else if ("FAILED".equals(tx.getBankStatus()))
            {
                worker.fail(requestNo);
            }
            return transactions.selectByRequestNo(requestNo);
        }
        finally
        {
            BankPaymentReconciler.schedule(settlement, transactions.selectByRequestNo(requestNo));
            settlement.releaseClaim(tx.getTransactionId());
        }
    }

    private BankQueryRequest queryRequest(BankTransaction tx)
    {
        BankRefundRequest snapshot = request(tx);
        BankQueryRequest query = new BankQueryRequest();
        query.setMerId(tx.getMerId());
        query.setOriginalRequestNo(tx.getRequestNo());
        query.setOriginalRequestTime(snapshot.getRequestTime());
        query.setBankSerialNo(tx.getBankSerialNo());
        return query;
    }

    private BankRefundRequest request(BankTransaction tx)
    {
        return JSON.parseObject(tx.getSnapshotJson(), BankRefundRequest.class);
    }

    private String bounded(String value, int maxLength)
    {
        if (value == null)
        {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    /** 事务边界工作类：所有写库方法必须在事务内执行，由外层编排按序调用。 */
    @Service
    public static class BankRefundTxWorker
    {
        @Autowired public BankTransactionMapper transactions;
        @Autowired public BankSettlementMapper settlement;
        @Autowired public RefundRecordMapper refunds;
        @Autowired public AccountInfoMapper accounts;
        @Autowired public OrderInfoMapper orders;
        @Autowired public PensionInstitutionMapper institutions;
        @Autowired public IBankMerchantConfigService merchants;
        @Autowired public IExpenseRecordService expenses;
        @Autowired public ISupervisionAccountLogService ledger;

        @Transactional(rollbackFor = Exception.class)
        public BankTransaction prepare(Long refundId, String approver, Long currentUserId)
        {
            RefundRecord refund = refunds.selectRefundRecordForUpdate(refundId, currentUserId);
            if (refund == null)
            {
                throw new ServiceException("退款记录不存在或无权操作");
            }
            // 0=待处理首退；4=银行明确失败后的授权重试（旧交易必须已 FAILED/DONE，新请求号递增）。
            if (!"0".equals(refund.getRefundStatus()) && !"4".equals(refund.getRefundStatus()))
            {
                throw new ServiceException("只能审批待处理或银行退款失败状态的退款");
            }
            if (refund.getOrderId() == null || refund.getOrderId() == 0L)
            {
                throw new ServiceException("银行退款必须关联原支付订单，请撤回后从订单重新发起");
            }
            OrderInfo order = orders.selectOrderInfoByOrderIdForUpdate(refund.getOrderId());
            if (order == null || !refund.getElderId().equals(order.getElderId())
                    || !refund.getInstitutionId().equals(order.getInstitutionId()))
            {
                throw new ServiceException("退款订单与老人或机构不匹配");
            }
            if (!"1".equals(order.getOrderStatus()))
            {
                throw new ServiceException("订单不是已支付状态，不能原路退款");
            }
            BankTransaction payment = transactions.selectByBusiness("PAY", order.getOrderId());
            if (payment == null || !"SUCCESS".equals(payment.getStatus())
                    || !"SUCCESS".equals(payment.getBankStatus())
                    || isBlank(payment.getBankSerialNo()) || isBlank(payment.getBankTime())
                    || !payment.getBankTime().matches("[0-9]{14}")
                    || !refund.getInstitutionId().equals(payment.getInstitutionId()))
            {
                throw new ServiceException("原支付缺少银行成功事实（流水或时间），无法原路退款");
            }
            BigDecimal total = amountOrZero(refund.getRefundAmount());
            BigDecimal serviceAmount = amountOrZero(refund.getServiceRefundAmount());
            BigDecimal depositAmount = amountOrZero(refund.getDepositRefundAmount());
            BigDecimal memberAmount = amountOrZero(refund.getMemberRefundAmount());
            if (total.signum() <= 0 || total.compareTo(serviceAmount.add(depositAmount).add(memberAmount)) != 0)
            {
                throw new ServiceException("退款金额不合法或与分类金额不一致");
            }
            if (settlement.refundOccupied(order.getOrderId()).add(total).compareTo(payment.getAmount()) > 0)
            {
                throw new ServiceException("退款累计超过原支付可退金额");
            }
            if (settlement.refundAttemptCount(order.getOrderId()) >= MAX_REFUND_ATTEMPTS)
            {
                throw new ServiceException("该订单退款次数已达银行单笔交易上限40次");
            }
            BankTransaction previous = transactions.selectByBusiness("REFUND", refundId);
            if (previous != null && !"FAILED".equals(previous.getStatus()))
            {
                throw new ServiceException("该退款已有银行请求，请等待结果确认，不能重复发起");
            }
            BankMerchantConfig merchant = merchants.selectEnabledByInstitutionId(refund.getInstitutionId());
            if (merchant == null || isBlank(merchant.getMerId()))
            {
                throw new ServiceException("该养老机构没有已验证、已启用的默认银行商户号");
            }
            if (!merchant.getMerId().equals(payment.getMerId()))
            {
                throw new ServiceException("机构当前商户号与原支付商户号不一致，不能原路退款");
            }
            AccountInfo account = accounts.selectAccountInfoForUpdate(refund.getElderId(), refund.getInstitutionId());
            if (account == null || !"1".equals(account.getAccountStatus()))
            {
                throw new ServiceException("老人账户不存在或不是正常状态");
            }
            ensureSufficient("服务费", account.getServiceBalance(), serviceAmount);
            ensureSufficient("押金", account.getDepositBalance(), depositAmount);
            ensureSufficient("会员费", account.getMemberBalance(), memberAmount);
            ensureSufficient("账户总", account.getTotalBalance(), total);

            Date now = new Date();
            BankRefundRequest request = new BankRefundRequest();
            request.setRequestNo(String.format("BR%s", IdUtils.fastSimpleUUID().substring(0, 30).toUpperCase()));
            request.setRequestTime(now);
            request.setMerId(payment.getMerId());
            request.setMerName(merchant.getMerchantName());
            request.setAmount(total);
            request.setOriginalRequestNo(payment.getRequestNo());
            request.setOriginalBankSerialNo(payment.getBankSerialNo());
            request.setOriginalBankTime(payment.getBankTime());
            request.validate();

            BankTransaction tx = new BankTransaction();
            tx.setRequestNo(request.getRequestNo());
            tx.setBusinessType("REFUND");
            tx.setBusinessId(refundId);
            tx.setInstitutionId(refund.getInstitutionId());
            tx.setMerId(payment.getMerId());
            tx.setBankCode("ZZBANK");
            tx.setChannelType(payment.getChannelType());
            tx.setEnvironment(payment.getEnvironment());
            tx.setAmount(total);
            tx.setSnapshotJson(JSON.toJSONString(request));
            tx.setStatus("PENDING");
            tx.setAttemptNo(previous == null ? 1 : previous.getAttemptNo() + 1);
            tx.setCreateTime(now);
            tx.setUpdateTime(now);
            tx.setNextQueryTime(new Date(now.getTime() + 60000L));
            if (transactions.insert(tx) != 1)
            {
                throw new ServiceException("保存银行退款请求失败");
            }

            refund.setRefundStatus("3");
            refund.setBankTransactionId(tx.getTransactionId());
            refund.setApprover(approver);
            refund.setApproveTime(now);
            refund.setUpdateBy(approver);
            refund.setUpdateTime(now);
            if (refunds.updateRefundRecord(refund) != 1)
            {
                throw new ServiceException("退款单转银行处理中失败");
            }
            return tx;
        }

        /** 银行受理同步明确拒绝：直接终态失败，退款单转失败（未扣账，可重新发起申请）。 */
        @Transactional(rollbackFor = Exception.class)
        public int failFast(String requestNo, BankResult accepted)
        {
            BankTransaction tx = transactions.selectByRequestNoForUpdate(requestNo);
            if (tx == null || !"REFUND".equals(tx.getBusinessType()))
            {
                throw new ServiceException("银行退款交易不存在");
            }
            if ("DONE".equals(tx.getBookingStatus()))
            {
                return 1;
            }
            tx.setBankStatus("FAILED");
            tx.setBankSerialNo(bounded(accepted.getBankSerialNo(), 64));
            tx.setResponseCode(bounded(accepted.getResponseCode(), 64));
            tx.setResponseMessage(bounded(accepted.getResponseMessage(), 500));
            settlement.observe(tx);
            tx.setStatus("FAILED");
            tx.setBookingStatus("DONE");
            settlement.finish(tx);
            markRefundFailed(tx.getBusinessId(),
                    bounded(joinCodeMessage(accepted.getResponseCode(), accepted.getResponseMessage()), 500));
            return 1;
        }

        @Transactional(rollbackFor = Exception.class)
        public void book(String requestNo)
        {
            BankTransaction tx = transactions.selectByRequestNoForUpdate(requestNo);
            if (tx == null || !"REFUND".equals(tx.getBusinessType()))
            {
                throw new ServiceException("银行退款交易不存在");
            }
            RefundRecord booked = refunds.selectRefundRecordByRefundId(tx.getBusinessId());
            if ("DONE".equals(tx.getBookingStatus()) || (booked != null && "1".equals(booked.getRefundStatus())))
            {
                return;
            }
            if (!"SUCCESS".equals(tx.getBankStatus()))
            {
                throw new ServiceException("银行退款未确认成功，禁止扣账");
            }
            if (institutions.selectPensionInstitutionForUpdate(tx.getInstitutionId()) == null)
            {
                throw new ServiceException("退款关联的养老机构不存在");
            }
            RefundRecord refund = refunds.selectRefundRecordForUpdate(tx.getBusinessId(), null);
            if (refund == null)
            {
                throw new ServiceException("退款记录不存在");
            }
            if (!tx.getTransactionId().equals(refund.getBankTransactionId()))
            {
                throw new ServiceException("退款单与银行交易不对应，需人工核查");
            }
            if (!"3".equals(refund.getRefundStatus()))
            {
                throw new ServiceException(String.format("退款单状态不允许入账：%s", refund.getRefundStatus()));
            }
            AccountInfo account = accounts.selectAccountInfoForUpdate(refund.getElderId(), refund.getInstitutionId());
            if (account == null || !"1".equals(account.getAccountStatus()))
            {
                throw new ServiceException("老人账户不存在或不是正常状态");
            }
            BigDecimal serviceAmount = amountOrZero(refund.getServiceRefundAmount());
            BigDecimal depositAmount = amountOrZero(refund.getDepositRefundAmount());
            BigDecimal memberAmount = amountOrZero(refund.getMemberRefundAmount());
            BigDecimal total = amountOrZero(refund.getRefundAmount());
            BigDecimal serviceBalance = amountOrZero(account.getServiceBalance());
            BigDecimal depositBalance = amountOrZero(account.getDepositBalance());
            BigDecimal memberBalance = amountOrZero(account.getMemberBalance());
            BigDecimal totalBalance = amountOrZero(account.getTotalBalance());
            ensureSufficient("服务费", serviceBalance, serviceAmount);
            ensureSufficient("押金", depositBalance, depositAmount);
            ensureSufficient("会员费", memberBalance, memberAmount);
            ensureSufficient("账户总", totalBalance, total);
            // 原路退款扣账必须连银行资金来源一起减，否则撞"余额不得低于银行入账"守卫。
            if (accounts.refundBankBalance(account.getAccountId(), serviceAmount,
                    depositAmount, memberAmount) != 1)
            {
                throw new ServiceException("更新老人账户余额失败");
            }
            createExpense(refund, account, "service", "服务费", serviceAmount, totalBalance, totalBalance.subtract(total));
            createExpense(refund, account, "deposit", "押金", depositAmount, totalBalance, totalBalance.subtract(total));
            createExpense(refund, account, "member", "会员费", memberAmount, totalBalance, totalBalance.subtract(total));
            if (ledger.recordTransferOut(refund.getInstitutionId(), refund.getRefundId(), total,
                    String.format("银行退款-%s", refund.getRefundNo()), "付款人") == null)
            {
                throw new ServiceException("监管账户退款流水保存失败");
            }
            Date now = new Date();
            refund.setRefundStatus("1");
            refund.setRefundTime(parseBankTime(tx.getBankTime(), now));
            refund.setUpdateBy("system");
            refund.setUpdateTime(now);
            if (refunds.updateRefundRecord(refund) != 1)
            {
                throw new ServiceException("退款单完成状态保存失败");
            }
            tx.setStatus("SUCCESS");
            tx.setBookingStatus("DONE");
            settlement.finish(tx);
        }

        @Transactional(rollbackFor = Exception.class)
        public void fail(String requestNo)
        {
            BankTransaction tx = transactions.selectByRequestNoForUpdate(requestNo);
            if (tx == null || !"REFUND".equals(tx.getBusinessType()))
            {
                throw new ServiceException("银行退款交易不存在");
            }
            if ("DONE".equals(tx.getBookingStatus()))
            {
                return;
            }
            if (!"FAILED".equals(tx.getBankStatus()))
            {
                throw new ServiceException("银行退款未确认失败，不能按失败处理");
            }
            markRefundFailed(tx.getBusinessId(),
                    bounded(joinCodeMessage(tx.getResponseCode(), tx.getResponseMessage()), 500));
            tx.setStatus("FAILED");
            tx.setBookingStatus("DONE");
            settlement.finish(tx);
        }

        private void markRefundFailed(Long refundId, String reason)
        {
            RefundRecord refund = refunds.selectRefundRecordForUpdate(refundId, null);
            if (refund == null || !"3".equals(refund.getRefundStatus()))
            {
                return;
            }
            Date now = new Date();
            refund.setRefundStatus("4");
            refund.setApproveRemark(reason);
            refund.setUpdateBy("system");
            refund.setUpdateTime(now);
            if (refunds.updateRefundRecord(refund) != 1)
            {
                throw new ServiceException("退款单失败状态保存失败");
            }
        }

        private void createExpense(RefundRecord refund, AccountInfo account, String expenseType,
                String expenseName, BigDecimal amount, BigDecimal balanceBefore, BigDecimal balanceAfter)
        {
            if (amount.signum() <= 0)
            {
                return;
            }
            int inserted = expenses.createExpenseRecord(refund.getElderId(), account.getAccountId(),
                    expenseType, "expense", amount,
                    String.format("%s退款-%s", expenseName, refund.getRefundNo()),
                    refund.getRefundId(), "refund", balanceBefore, balanceAfter);
            if (inserted <= 0)
            {
                throw new ServiceException(String.format("记录%s退款流水失败", expenseName));
            }
        }

        private void ensureSufficient(String name, BigDecimal balance, BigDecimal amount)
        {
            if (amountOrZero(balance).compareTo(amountOrZero(amount)) < 0)
            {
                throw new ServiceException(String.format("%s余额不足，当前余额：%s元", name, amountOrZero(balance)));
            }
        }

        private String joinCodeMessage(String code, String message)
        {
            return String.format("%s:%s", code == null ? "" : code, message == null ? "" : message);
        }

        private BigDecimal amountOrZero(BigDecimal amount)
        {
            return amount == null ? BigDecimal.ZERO : amount;
        }

        private Date parseBankTime(String bankTime, Date fallback)
        {
            if (bankTime == null || !bankTime.matches("[0-9]{14}"))
            {
                return fallback;
            }
            try
            {
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                format.setLenient(false);
                return format.parse(bankTime);
            }
            catch (java.text.ParseException e)
            {
                return fallback;
            }
        }

        private String bounded(String value, int maxLength)
        {
            if (value == null)
            {
                return null;
            }
            return value.length() <= maxLength ? value : value.substring(0, maxLength);
        }

        private boolean isBlank(String value)
        {
            return value == null || value.trim().isEmpty();
        }
    }
}
