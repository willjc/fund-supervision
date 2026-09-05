package com.ruoyi.task;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.domain.pension.TransferRuleConfig;
import com.ruoyi.mapper.bank.BankSettlementMapper;
import com.ruoyi.service.bank.impl.BankPaymentReconciler;
import com.ruoyi.service.bank.impl.BankPayoutService;
import com.ruoyi.service.pension.ITransferRuleConfigService;

@Component("bankSettlementTask")
public class BankSettlementTask
{
    private static final Logger log = LoggerFactory.getLogger(BankSettlementTask.class);
    @Autowired private BankSettlementMapper mapper;
    @Autowired private BankPaymentReconciler payments;
    @Autowired private BankPayoutService payouts;
    @Autowired private ITransferRuleConfigService rules;
    @Value("${bank.integration.reconciliation-enabled:false}") private boolean reconciliationEnabled;

    public void reconcile()
    {
        if (!reconciliationEnabled) { return; }
        for (BankTransaction tx : mapper.dueTransactions())
        {
            try
            {
                if ("PAY".equals(tx.getBusinessType())) { payments.queryAndComplete(tx.getBusinessId()); }
                else if ("TRANSFER".equals(tx.getBusinessType())) { payouts.reconcile(tx.getRequestNo()); }
            }
            catch (Exception e)
            {
                log.warn("银行交易补查未完成 requestNo={}, type={}", tx.getRequestNo(), e.getClass().getSimpleName());
            }
        }
    }

    public void dispatch()
    {
        if (!payouts.isEnabled()) { return; }
        TransferRuleConfig query = new TransferRuleConfig();
        query.setStatus("0");
        query.setTransferCycle("monthly");
        List<TransferRuleConfig> active = rules.selectTransferRuleConfigList(query);
        TransferRuleConfig rule = active.size() == 1 ? active.get(0) : null;
        Long cursor = 0L;
        Long last = mapper.lastTransferId();
        while (cursor < last)
        {
            List<FundTransfer> candidates = mapper.dueTransfers(cursor, last);
            if (candidates.isEmpty()) { break; }
            for (FundTransfer transfer : candidates)
            {
                cursor = transfer.getTransferId();
                try
                {
                    if (due(transfer, rule, LocalDateTime.now()))
                    { payouts.submit(transfer.getTransferId(), "bank-task", false); }
                }
                catch (Exception e)
                {
                    log.warn("拨付仍待处理 transferId={}, type={}", transfer.getTransferId(), e.getClass().getSimpleName());
                }
            }
        }
    }

    public static boolean due(FundTransfer transfer, TransferRuleConfig rule, LocalDateTime now)
    {
        if (!Integer.valueOf(1).equals(transfer.getBankEligible()) || !"pending".equals(transfer.getStatus())) { return false; }
        if (transfer.getSourceKey() == null) { return false; }
        if (!transfer.getSourceKey().startsWith("MONTH:") || "manual".equals(transfer.getPaidMethod())) { return true; }
        if (rule == null || rule.getTransferDay() == null || rule.getTransferDay() < 1 || rule.getTransferDay() > 31
                || rule.getTransferTime() == null) { return false; }
        YearMonth month = YearMonth.parse(transfer.getBillingMonth());
        LocalDateTime due = month.atDay(Math.min(rule.getTransferDay(), month.lengthOfMonth()))
                .atTime(LocalTime.parse(rule.getTransferTime()));
        return !now.isBefore(due);
    }
}
