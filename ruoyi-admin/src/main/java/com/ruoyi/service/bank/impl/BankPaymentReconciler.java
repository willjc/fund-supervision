package com.ruoyi.service.bank.impl;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.bank.gateway.BankResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.mapper.bank.BankSettlementMapper;
import com.ruoyi.service.bank.IBankPaymentService;
import com.ruoyi.service.bank.IBankPaymentCompletionService;

/** 页面查询和后台补查共享。先持久化银行事实，再执行可重入的本地入账事务。 */
@Service
public class BankPaymentReconciler
{
    @Autowired private IBankPaymentService payments;
    @Autowired private IBankPaymentCompletionService completion;
    @Autowired private BankTransactionMapper transactions;
    @Autowired private BankSettlementMapper settlement;

    public BankResult queryAndComplete(Long orderId)
    {
        BankTransaction tx = transactions.selectByBusiness("PAY", orderId);
        if (tx == null) { throw new ServiceException("支付交易不存在"); }
        if ("SUCCESS".equals(tx.getStatus())) { return payments.queryPayment(orderId); }
        if ("FAILED".equals(tx.getStatus())) { return payments.queryPayment(orderId); }
        if (settlement.claim(tx.getTransactionId()) != 1)
        {
            BankResult busy = BankResult.pending(tx.getBankSerialNo(), null);
            busy.setRequestNo(tx.getRequestNo());
            return busy;
        }
        try
        {
            if (!"SUCCESS".equals(tx.getBankStatus()) && !"FAILED".equals(tx.getBankStatus()))
            {
                BankResult result = payments.queryPayment(orderId);
                tx.setBankStatus(result.getStatus());
                tx.setBankSerialNo(result.getBankSerialNo());
                tx.setBankTime(result.getBankTransactionTime());
                tx.setResponseCode(bounded(result.getResponseCode(), 64));
                tx.setResponseMessage(bounded(result.getResponseMessage(), 500));
                if ("SUCCESS".equals(result.getStatus()) && (result.getBankSerialNo() == null
                        || result.getBankSerialNo().trim().isEmpty() || result.getPaidAmount() == null
                        || result.getPaidAmount().compareTo(tx.getAmount()) != 0))
                {
                    throw new ServiceException("银行确认缺少流水或金额不匹配，禁止入账");
                }
                settlement.observe(tx);
                tx = transactions.selectByRequestNo(tx.getRequestNo());
            }
            if ("SUCCESS".equals(tx.getBankStatus()))
            {
                completion.completePayment(tx.getRequestNo(), tx.getBankSerialNo(), tx.getResponseCode(),
                        tx.getResponseMessage(), "bank-query");
            }
            else if ("FAILED".equals(tx.getBankStatus()))
            {
                tx.setStatus("FAILED");
                tx.setBookingStatus("DONE");
                settlement.finish(tx);
            }
            return resultOf(tx);
        }
        finally
        {
            reschedule(transactions.selectByRequestNo(tx.getRequestNo()));
            settlement.releaseClaim(tx.getTransactionId());
        }
    }

    public void reschedule(BankTransaction tx)
    {
        schedule(settlement, tx);
    }

    static void schedule(BankSettlementMapper settlement, BankTransaction tx)
    {
        if (tx == null || "DONE".equals(tx.getBookingStatus()) || "REVERSED".equals(tx.getBookingStatus())) { return; }
        Date now = new Date();
        long age = now.getTime() - tx.getCreateTime().getTime();
        boolean unresolved = !"SUCCESS".equals(tx.getBankStatus()) && !"FAILED".equals(tx.getBankStatus())
                && !"RETURNED".equals(tx.getBankStatus());
        int review = unresolved && age >= 86400000L ? 1 : 0;
        settlement.schedule(tx.getTransactionId(), review == 1 ? null : new Date(now.getTime()
                + (age <= 150000L ? 15000L : 300000L)), review);
    }

    private BankResult resultOf(BankTransaction tx)
    {
        BankResult result = new BankResult();
        result.setRequestNo(tx.getRequestNo());
        result.setStatus(tx.getBankStatus() == null ? "UNKNOWN" : tx.getBankStatus());
        result.setBankSerialNo(tx.getBankSerialNo());
        result.setBankTransactionTime(tx.getBankTime());
        result.setPaidAmount(tx.getAmount());
        result.setResponseCode(tx.getResponseCode());
        result.setResponseMessage(tx.getResponseMessage());
        return result;
    }

    private String bounded(String value, int length)
    {
        return value == null ? null : value.substring(0, Math.min(length, value.length()));
    }
}
