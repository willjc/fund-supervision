package com.ruoyi.service.bank.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.bank.gateway.*;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.bank.*;
import com.ruoyi.domain.pension.*;
import com.ruoyi.mapper.bank.*;
import com.ruoyi.mapper.pension.*;
import com.ruoyi.mapper.PensionInstitutionMapper;
import com.ruoyi.service.bank.IBankPaymentService;
import com.ruoyi.service.bank.IBankPaymentCompletionService;
import com.ruoyi.service.pension.*;
import com.ruoyi.task.BankSettlementTask;

class BankSettlementSafetyTest
{
    @Test
    void institutionEditCannotBypassBankAccountChangeGuard()
    {
        com.ruoyi.service.impl.PensionInstitutionServiceImpl service=new com.ruoyi.service.impl.PensionInstitutionServiceImpl();
        PensionInstitutionMapper institutions=mock(PensionInstitutionMapper.class);
        BankSettlementMapper settlement=mock(BankSettlementMapper.class);
        ReflectionTestUtils.setField(service,"pensionInstitutionMapper",institutions);
        ReflectionTestUtils.setField(service,"settlementMapper",settlement);
        com.ruoyi.domain.PensionInstitution existing=new com.ruoyi.domain.PensionInstitution();
        existing.setInstitutionId(1L);existing.setSuperviseAccount("original");
        when(institutions.selectPensionInstitutionForUpdate(1L)).thenReturn(existing);
        when(settlement.hasBankFunds(1L)).thenReturn(1);
        com.ruoyi.domain.PensionInstitution update=new com.ruoyi.domain.PensionInstitution();
        update.setInstitutionId(1L);update.setSuperviseAccount("another");
        assertThrows(ServiceException.class,()->service.updatePensionInstitution(update));
        verify(institutions,never()).updatePensionInstitution(any());
    }

    @Test
    void depositSubmissionPreservesFamilyWorkflowButCannotForgeApproval()
    {
        com.ruoyi.service.pension.impl.DepositApplyServiceImpl service=new com.ruoyi.service.pension.impl.DepositApplyServiceImpl();
        DepositApplyMapper mapper=mock(DepositApplyMapper.class);
        ReflectionTestUtils.setField(service,"depositApplyMapper",mapper);
        ReflectionTestUtils.setField(service,"bankPayoutService",mock(BankPayoutService.class));
        when(mapper.insertDepositApply(any())).thenReturn(1);
        DepositApply apply=new DepositApply();apply.setInstitutionId(1L);apply.setApplyStatus("pending_family");
        apply.setApprover("forged");apply.setActualAmount(BigDecimal.TEN);
        assertEquals(1,service.insertDepositApply(apply));
        assertEquals("pending_family",apply.getApplyStatus());assertNull(apply.getApprover());assertNull(apply.getActualAmount());
        apply.setApplyStatus("approved");
        assertThrows(ServiceException.class,()->service.insertDepositApply(apply));
        verify(mapper,times(1)).insertDepositApply(any());
    }

    @Test
    void blockedFirstPageDoesNotStarveLaterTransfers()
    {
        BankSettlementTask task = new BankSettlementTask();
        BankPayoutService payouts = mock(BankPayoutService.class);
        BankSettlementMapper mapper = mock(BankSettlementMapper.class);
        ITransferRuleConfigService rules = mock(ITransferRuleConfigService.class);
        ReflectionTestUtils.setField(task,"payouts",payouts);
        ReflectionTestUtils.setField(task,"mapper",mapper);
        ReflectionTestUtils.setField(task,"rules",rules);
        when(payouts.isEnabled()).thenReturn(true);
        when(mapper.lastTransferId()).thenReturn(101L);
        when(rules.selectTransferRuleConfigList(any())).thenReturn(java.util.Collections.emptyList());
        java.util.List<FundTransfer> first = new java.util.ArrayList<>();
        for(long id=1;id<=101;id++)
        {
            FundTransfer f=new FundTransfer(); f.setTransferId(id); f.setSourceKey("FIRST:"+id); f.setBankEligible(1); f.setStatus("pending");
            if(id<=100) {first.add(f);when(payouts.submit(eq(id),anyString(),eq(false))).thenThrow(new ServiceException("waiting"));}
            else {when(mapper.dueTransfers(100L,101L)).thenReturn(java.util.Collections.singletonList(f));}
        }
        when(mapper.dueTransfers(0L,101L)).thenReturn(first);
        task.dispatch();
        verify(payouts).submit(101L,"bank-task",false);
    }

    @Test
    void genericCreateCannotGrantBankEligibility()
    {
        com.ruoyi.web.controller.pension.FundTransferController controller = new com.ruoyi.web.controller.pension.FundTransferController();
        FundTransfer crafted=new FundTransfer();crafted.setBankEligible(1);crafted.setSourceKey("FIRST:10");
        assertEquals(500,controller.add(crafted).get("code"));
    }

    @Test
    void linkedApplicationsCannotBeEditedOrDeleted()
    {
        BankSettlementMapper mapper=mock(BankSettlementMapper.class);
        BankPayoutService scope=mock(BankPayoutService.class);
        com.ruoyi.service.pension.impl.DepositApplyServiceImpl deposits=new com.ruoyi.service.pension.impl.DepositApplyServiceImpl();
        ReflectionTestUtils.setField(deposits,"settlementMapper",mapper);ReflectionTestUtils.setField(deposits,"bankPayoutService",scope);
        DepositApply deposit=new DepositApply();deposit.setApplyId(1L);deposit.setInstitutionId(1L);deposit.setApplyStatus("approved");
        when(mapper.lockDeposit(1L)).thenReturn(deposit);
        assertThrows(ServiceException.class,()->deposits.deleteDepositApplyByApplyId(1L));
        assertThrows(ServiceException.class,()->deposits.updateDepositApply(deposit));
        com.ruoyi.service.pension.impl.FundTransferApplyServiceImpl applies=new com.ruoyi.service.pension.impl.FundTransferApplyServiceImpl();
        ReflectionTestUtils.setField(applies,"settlementMapper",mapper);ReflectionTestUtils.setField(applies,"bankPayoutService",scope);
        FundTransferApply apply=new FundTransferApply();apply.setApplyId(2L);apply.setInstitutionId(1L);apply.setApplyStatus("approved");
        when(mapper.lockApply(2L)).thenReturn(apply);
        assertThrows(ServiceException.class,()->applies.deleteFundTransferApplyByApplyId(2L));
        assertThrows(ServiceException.class,()->applies.updateFundTransferApply(apply));
    }

    @Test
    void wrongOrMissingRequestIdentityCannotReleaseOrDebit()
    {
        Fixture f = new Fixture();
        for (String status : new String[]{"SUCCESS","FAILED"})
        {
            for(String identity : new String[]{null,"ANOTHER"})
            {
                BankResult bank=f.success();bank.setStatus(status);bank.setRequestNo(identity);
                when(f.gateway.queryPayout(any())).thenReturn(bank);
                assertThrows(ServiceException.class,()->f.service.reconcile("BT1"));
            }
        }
        verify(f.mapper,never()).observe(any());
        verify(f.mapper,never()).settleBalance(any(),any(),any(),any(),any());
    }

    @Test
    void monthlyJobCatchesUpAndDoesNotSendHistory()
    {
        FundTransfer transfer = new FundTransfer();
        transfer.setSourceKey("MONTH:1:2026-02");
        transfer.setBillingMonth("2026-02");
        transfer.setStatus("pending");
        TransferRuleConfig rule = new TransferRuleConfig();
        rule.setTransferDay(31);
        rule.setTransferTime("00:35");
        assertFalse(BankSettlementTask.due(transfer, rule, LocalDateTime.of(2026,3,1,9,0)));
        transfer.setBankEligible(1);
        assertFalse(BankSettlementTask.due(transfer, rule, LocalDateTime.of(2026,2,28,0,34)));
        assertTrue(BankSettlementTask.due(transfer, rule, LocalDateTime.of(2026,2,28,0,35)));
        assertTrue(BankSettlementTask.due(transfer, rule, LocalDateTime.of(2026,3,1,9,0)));
        transfer.setStatus("completed");
        assertFalse(BankSettlementTask.due(transfer, rule, LocalDateTime.of(2026,3,1,9,0)));
    }

    @Test
    void reservationsAreSeparateFromBookBalance()
    {
        AccountInfo account = new AccountInfo();
        account.setTotalBalance(new BigDecimal("100.00"));
        account.setServiceBalance(new BigDecimal("80.00"));
        account.setDepositBalance(new BigDecimal("20.00"));
        account.setServiceReserved(new BigDecimal("30.00"));
        account.setDepositReserved(new BigDecimal("5.00"));
        assertEquals(new BigDecimal("100.00"), account.getTotalBalance());
        assertEquals(new BigDecimal("65.00"), account.getAvailableBalance());
        assertEquals(new BigDecimal("50.00"), account.getServiceAvailable());
    }

    @Test
    void paymentFactsPersistBeforeLocalCompletionAndReplayDoesNotRequery()
    {
        BankPaymentReconciler service = new BankPaymentReconciler();
        IBankPaymentService payments = mock(IBankPaymentService.class);
        IBankPaymentCompletionService completion = mock(IBankPaymentCompletionService.class);
        BankTransactionMapper transactions = mock(BankTransactionMapper.class);
        BankSettlementMapper mapper = mock(BankSettlementMapper.class);
        ReflectionTestUtils.setField(service,"payments",payments);
        ReflectionTestUtils.setField(service,"completion",completion);
        ReflectionTestUtils.setField(service,"transactions",transactions);
        ReflectionTestUtils.setField(service,"settlement",mapper);
        BankTransaction tx = transaction();
        tx.setBusinessType("PAY");
        when(transactions.selectByBusiness("PAY",10L)).thenReturn(tx);
        when(transactions.selectByRequestNo("BT1")).thenReturn(tx);
        when(mapper.claim(1L)).thenReturn(1);
        BankResult bank = BankResult.success("BANK1");
        bank.setPaidAmount(new BigDecimal("10.00"));
        bank.setBankTransactionTime("20260905120000");
        when(payments.queryPayment(10L)).thenReturn(bank);
        when(completion.completePayment(any(),any(),any(),any(),any()))
                .thenThrow(new ServiceException("local failure")).thenReturn(null);
        assertThrows(ServiceException.class, () -> service.queryAndComplete(10L));
        org.mockito.InOrder order = inOrder(mapper, completion);
        order.verify(mapper).claim(1L);
        order.verify(mapper).observe(tx);
        order.verify(completion).completePayment(any(),any(),any(),any(),any());
        service.queryAndComplete(10L);
        verify(payments,times(1)).queryPayment(10L);
        verify(completion,times(2)).completePayment(any(),any(),any(),any(),any());
    }

    @Test
    void pendingDoesNotDebitAndSuccessIsBookedOnceThenReturnReversesOnce()
    {
        Fixture f = new Fixture();
        when(f.gateway.queryPayout(any())).thenReturn(BankResult.pending(null,null));
        f.service.reconcile("BT1");
        verify(f.mapper,never()).settleBalance(any(),any(),any(),any(),any());
        when(f.gateway.queryPayout(any())).thenReturn(f.success());
        f.service.reconcile("BT1");
        f.service.reconcile("BT1");
        verify(f.mapper,times(1)).settleBalance(40L,new BigDecimal("10.00"),BigDecimal.ZERO,new BigDecimal("10.00"),BigDecimal.ZERO);
        assertEquals("SUCCESS", f.tx.getStatus());
        f.tx.setReturnTime("2026-09-05T12:01:00");
        f.tx.setBankStatus("RETURNED");
        f.tx.setBookingStatus("RETURN_PENDING");
        f.service.reconcile("BT1");
        f.service.reconcile("BT1");
        verify(f.mapper,times(1)).reverseBalance(40L,new BigDecimal("10.00"),BigDecimal.ZERO);
        assertEquals("REVERSED",f.tx.getBookingStatus());
        verify(f.gateway,times(2)).queryPayout(any());
    }

    @Test
    void returnBeforeQuerySuccessReleasesReservationWithoutDebit()
    {
        Fixture f = new Fixture();
        f.tx.setReturnTime("2026-09-05T12:01:00");
        f.tx.setBankStatus("RETURNED");
        f.service.reconcile("BT1");
        verify(f.mapper).settleBalance(40L,new BigDecimal("10.00"),BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO);
        verify(f.mapper,never()).reverseBalance(any(),any(),any());
        verifyNoInteractions(f.gateway);
        assertEquals("RETURNED",f.tx.getStatus());
    }

    @Test
    void wrongAmountOrAccountNeverBooksAndDisabledNeverCallsBank()
    {
        Fixture f = new Fixture();
        BankResult bank = f.success();
        bank.setPaidAmount(new BigDecimal("9.99"));
        when(f.gateway.queryPayout(any())).thenReturn(bank);
        assertThrows(ServiceException.class, () -> f.service.reconcile("BT1"));
        bank.setPaidAmount(new BigDecimal("10.00"));
        bank.setPayeeAccountNo("other");
        assertThrows(ServiceException.class, () -> f.service.reconcile("BT1"));
        verify(f.mapper,never()).observe(any());
        verify(f.mapper,never()).settleBalance(any(),any(),any(),any(),any());
        clearInvocations(f.gateway);
        assertThrows(ServiceException.class, () -> f.service.submit(10L,"test",false));
        verifyNoInteractions(f.gateway);
    }

    @Test
    void uncertainQueryNeverReleasesReservationAndDayTimeoutRequiresReview()
    {
        Fixture f = new Fixture();
        when(f.gateway.queryPayout(any())).thenThrow(new ServiceException("timeout"));
        assertThrows(ServiceException.class, () -> f.service.reconcile("BT1"));
        verify(f.mapper,never()).settleBalance(any(),any(),any(),any(),any());
        BankPaymentReconciler schedule = new BankPaymentReconciler();
        ReflectionTestUtils.setField(schedule,"settlement",f.mapper);
        f.tx.setCreateTime(new Date(System.currentTimeMillis()-86400001L));
        schedule.reschedule(f.tx);
        verify(f.mapper).schedule(1L,null,1);
        assertEquals("PENDING",f.tx.getStatus());
        clearInvocations(f.mapper);
        f.tx.setBankStatus("FAILED");
        schedule.reschedule(f.tx);
        verify(f.mapper).schedule(eq(1L),any(Date.class),eq(0));
    }

    private static BankTransaction transaction()
    {
        BankTransaction tx = new BankTransaction();
        tx.setTransactionId(1L);tx.setBusinessId(10L);tx.setInstitutionId(20L);tx.setRequestNo("BT1");
        tx.setBusinessType("TRANSFER");tx.setStatus("PENDING");tx.setAmount(new BigDecimal("10.00"));tx.setCreateTime(new Date());
        return tx;
    }

    private static class Fixture
    {
        final BankPayoutService service = new BankPayoutService();
        final BankGateway gateway = mock(BankGateway.class);
        final BankTransactionMapper transactions = mock(BankTransactionMapper.class);
        final BankSettlementMapper mapper = mock(BankSettlementMapper.class);
        final BankTransaction tx = transaction();
        final FundTransfer transfer = new FundTransfer();
        Fixture()
        {
            AccountInfoMapper accounts = mock(AccountInfoMapper.class);
            PlatformTransactionManager tm = mock(PlatformTransactionManager.class);
            when(tm.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
            IExpenseRecordService expenses = mock(IExpenseRecordService.class);
            ISupervisionAccountLogService ledger = mock(ISupervisionAccountLogService.class);
            ReflectionTestUtils.setField(service,"gateway",gateway);
            ReflectionTestUtils.setField(service,"transactions",transactions);
            ReflectionTestUtils.setField(service,"settlement",mapper);
            ReflectionTestUtils.setField(service,"accounts",accounts);
            ReflectionTestUtils.setField(service,"institutions",mock(PensionInstitutionMapper.class));
            ReflectionTestUtils.setField(service,"expenses",expenses);
            ReflectionTestUtils.setField(service,"ledger",ledger);
            ReflectionTestUtils.setField(service,"transactionManager",tm);
            BankPayoutRequest req = new BankPayoutRequest();
            req.setPayerAccountNo("payer");req.setPayeeAccountNo("payee");
            tx.setSnapshotJson(JSON.toJSONString(req));
            transfer.setTransferId(10L);transfer.setInstitutionId(20L);transfer.setElderId(30L);transfer.setBankTransactionId(1L);
            transfer.setTransferAmount(new BigDecimal("10.00"));transfer.setIsPaid("0");
            AccountInfo account = new AccountInfo();account.setAccountId(40L);account.setTotalBalance(new BigDecimal("100.00"));
            when(transactions.selectByRequestNo("BT1")).thenReturn(tx);
            when(transactions.selectByRequestNoForUpdate("BT1")).thenReturn(tx);
            when(mapper.claim(1L)).thenReturn(1);
            when(mapper.lockTransfer(10L)).thenReturn(transfer);
            when(accounts.selectAccountInfoForUpdate(30L,20L)).thenReturn(account);
            when(mapper.settleBalance(any(),any(),any(),any(),any())).thenReturn(1);
            when(mapper.reverseBalance(any(),any(),any())).thenReturn(1);
            when(mapper.transferResult(any(),any(),any(),any(),any(),any())).thenAnswer(i -> {transfer.setIsPaid(i.getArgument(2)); return 1;});
            when(mapper.finish(any())).thenReturn(1);
            when(expenses.createExpenseRecord(any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(1);
            SupervisionAccountLog log = new SupervisionAccountLog();log.setLogId(1L);
            when(ledger.recordTransferOut(any(),any(),any(),any(),any())).thenReturn(log);
            when(ledger.recordIncome(any(),any(),any(),any(),any())).thenReturn(log);
        }
        BankResult success()
        {
            BankResult r=BankResult.success("BANK1");r.setRequestNo("BT1");r.setPaidAmount(new BigDecimal("10.00"));
            r.setPayerAccountNo("payer");r.setPayeeAccountNo("payee");r.setBankTransactionTime("20260905120000");return r;
        }
    }
}
