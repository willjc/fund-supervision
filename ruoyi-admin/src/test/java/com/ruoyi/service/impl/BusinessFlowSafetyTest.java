package com.ruoyi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.ExpenseRecord;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.pension.AccountInfoMapper;
import com.ruoyi.mapper.pension.ExpenseRecordMapper;
import com.ruoyi.mapper.pension.FundTransferMapper;
import com.ruoyi.service.bank.impl.BankPayoutService;
import com.ruoyi.service.pension.impl.ExpenseRecordServiceImpl;
import com.ruoyi.service.pension.impl.FundTransferApplyServiceImpl;

class BusinessFlowSafetyTest
{
    @Test
    void accountProfileEditCannotChangeBalancesOrOwnership()
    {
        AccountInfoServiceImpl service = new AccountInfoServiceImpl();
        AccountInfoMapper mapper = mock(AccountInfoMapper.class);
        ReflectionTestUtils.setField(service, "accountInfoMapper", mapper);
        AccountInfo existing = new AccountInfo();
        existing.setAccountId(1L);
        existing.setInstitutionId(10L);
        existing.setElderId(20L);
        existing.setTotalBalance(new BigDecimal("100.00"));
        existing.setServiceBalance(new BigDecimal("80.00"));
        when(mapper.selectAccountInfoByAccountId(1L)).thenReturn(existing);
        when(mapper.updateAccountInfo(any())).thenReturn(1);

        AccountInfo input = new AccountInfo();
        input.setAccountId(1L);
        input.setAccountName("新名称");
        input.setTotalBalance(new BigDecimal("99999.00"));
        input.setServiceBalance(new BigDecimal("99999.00"));
        input.setInstitutionId(99L);
        input.setElderId(98L);
        assertEquals(1, service.updateAccountProfile(input));

        ArgumentCaptor<AccountInfo> captor = ArgumentCaptor.forClass(AccountInfo.class);
        verify(mapper).updateAccountInfo(captor.capture());
        AccountInfo saved = captor.getValue();
        assertEquals("新名称", saved.getAccountName());
        assertNull(saved.getTotalBalance());
        assertNull(saved.getServiceBalance());
        assertNull(saved.getInstitutionId());
        assertNull(saved.getElderId());
    }

    @Test
    void genericAccountCreateCannotBringMoneyIn()
    {
        AccountInfoServiceImpl service = new AccountInfoServiceImpl();
        AccountInfoMapper mapper = mock(AccountInfoMapper.class);
        ReflectionTestUtils.setField(service, "accountInfoMapper", mapper);
        when(mapper.insertAccountInfo(any())).thenReturn(1);
        AccountInfo input = new AccountInfo();
        input.setElderId(1L);
        input.setInstitutionId(2L);
        input.setTotalBalance(new BigDecimal("500.00"));
        input.setServiceBalance(new BigDecimal("500.00"));
        input.setDepositBalance(new BigDecimal("100.00"));
        input.setAccountStatus("2");
        assertEquals(1, service.insertAccountInfo(input));
        assertEquals(0, input.getTotalBalance().signum());
        assertEquals(0, input.getServiceBalance().signum());
        assertEquals(0, input.getDepositBalance().signum());
        assertEquals("1", input.getAccountStatus());
    }

    @Test
    void accountWithMoneyCannotBeDeletedOrClosed()
    {
        AccountInfoServiceImpl service = new AccountInfoServiceImpl();
        AccountInfoMapper mapper = mock(AccountInfoMapper.class);
        ReflectionTestUtils.setField(service, "accountInfoMapper", mapper);
        AccountInfo account = new AccountInfo();
        account.setAccountId(1L);
        account.setTotalBalance(new BigDecimal("0.01"));
        when(mapper.selectAccountInfoByAccountId(1L)).thenReturn(account);
        assertThrows(ServiceException.class, () -> service.deleteAccountInfoByAccountId(1L));
        assertThrows(ServiceException.class, () -> service.closeAccount(1L));
        verify(mapper, never()).deleteAccountInfoByAccountId(any());
        verify(mapper, never()).updateAccountInfo(any());
    }

    @Test
    void orderCannotBeMarkedPaidByGenericEdit()
    {
        OrderInfoServiceImpl service = new OrderInfoServiceImpl();
        OrderInfoMapper mapper = mock(OrderInfoMapper.class);
        ReflectionTestUtils.setField(service, "orderInfoMapper", mapper);
        OrderInfo existing = new OrderInfo();
        existing.setOrderId(1L);
        existing.setInstitutionId(10L);
        existing.setElderId(20L);
        existing.setOrderNo("ORD1");
        existing.setOrderStatus("5");
        existing.setPaidAmount(BigDecimal.ZERO);
        when(mapper.selectOrderInfoByOrderId(1L)).thenReturn(existing);
        OrderInfo input = new OrderInfo();
        input.setOrderId(1L);
        input.setOrderStatus("1");
        assertThrows(ServiceException.class, () -> service.updateOrderInfo(input));
        verify(mapper, never()).updateOrderInfo(any());
    }

    @Test
    void paidOrderEditPreservesPaymentFactsAndOwnership()
    {
        OrderInfoServiceImpl service = new OrderInfoServiceImpl();
        OrderInfoMapper mapper = mock(OrderInfoMapper.class);
        ReflectionTestUtils.setField(service, "orderInfoMapper", mapper);
        OrderInfo existing = new OrderInfo();
        existing.setOrderId(1L);
        existing.setInstitutionId(10L);
        existing.setElderId(20L);
        existing.setOrderNo("ORD1");
        existing.setOrderStatus("1");
        existing.setOrderAmount(new BigDecimal("100.00"));
        existing.setPaidAmount(new BigDecimal("100.00"));
        Date paidTime = new Date();
        existing.setPaymentTime(paidTime);
        existing.setPaymentMethod("wechat");
        when(mapper.selectOrderInfoByOrderId(1L)).thenReturn(existing);
        when(mapper.updateOrderInfo(any())).thenReturn(1);
        OrderInfo input = new OrderInfo();
        input.setOrderId(1L);
        input.setOrderStatus("5");
        input.setOrderAmount(new BigDecimal("200.00"));
        input.setInstitutionId(99L);
        input.setElderId(98L);
        input.setOrderNo("HACK");
        assertEquals(1, service.updateOrderInfo(input));
        ArgumentCaptor<OrderInfo> captor = ArgumentCaptor.forClass(OrderInfo.class);
        verify(mapper).updateOrderInfo(captor.capture());
        OrderInfo saved = captor.getValue();
        assertEquals(10L, saved.getInstitutionId());
        assertEquals(20L, saved.getElderId());
        assertEquals("ORD1", saved.getOrderNo());
        assertEquals(new BigDecimal("100.00"), saved.getPaidAmount());
        assertEquals(paidTime, saved.getPaymentTime());
        assertEquals("wechat", saved.getPaymentMethod());
        assertNull(saved.getOrderAmount());
    }

    @Test
    void orderAmountCannotDropBelowPaidAmountAndPaidOrderCannotBeDeleted()
    {
        OrderInfoServiceImpl service = new OrderInfoServiceImpl();
        OrderInfoMapper mapper = mock(OrderInfoMapper.class);
        ReflectionTestUtils.setField(service, "orderInfoMapper", mapper);
        OrderInfo existing = new OrderInfo();
        existing.setOrderId(1L);
        existing.setOrderStatus("5");
        existing.setPaidAmount(new BigDecimal("50.00"));
        when(mapper.selectOrderInfoByOrderId(1L)).thenReturn(existing);
        OrderInfo input = new OrderInfo();
        input.setOrderId(1L);
        input.setOrderAmount(new BigDecimal("40.00"));
        assertThrows(ServiceException.class, () -> service.updateOrderInfo(input));
        existing.setOrderStatus("1");
        assertThrows(ServiceException.class, () -> service.deleteOrderInfoByOrderId(1L));
        verify(mapper, never()).deleteOrderInfoByOrderId(any());
    }

    @Test
    void fundTransferGenericEditCannotMoveOwnerOrFakePaymentState()
    {
        FundTransferServiceImpl service = new FundTransferServiceImpl();
        FundTransferMapper mapper = mock(FundTransferMapper.class);
        BankPayoutService payouts = mock(BankPayoutService.class);
        ReflectionTestUtils.setField(service, "fundTransferMapper", mapper);
        ReflectionTestUtils.setField(service, "bankPayoutService", payouts);
        FundTransfer existing = new FundTransfer();
        existing.setTransferId(1L);
        existing.setInstitutionId(10L);
        existing.setElderId(20L);
        existing.setStatus("pending");
        existing.setIsPaid("0");
        existing.setTransferAmount(new BigDecimal("10.00"));
        when(mapper.selectFundTransferByTransferId(1L)).thenReturn(existing);
        when(mapper.updateFundTransfer(any())).thenReturn(1);
        FundTransfer input = new FundTransfer();
        input.setTransferId(1L);
        input.setInstitutionId(99L);
        input.setElderId(98L);
        input.setApplyId(5L);
        input.setStatus("completed");
        input.setTransferStatus("1");
        input.setIsPaid("1");
        input.setPaidTime(new Date());
        input.setTransferAmount(new BigDecimal("999.00"));
        assertEquals(1, service.updateFundTransfer(input));
        ArgumentCaptor<FundTransfer> captor = ArgumentCaptor.forClass(FundTransfer.class);
        verify(mapper).updateFundTransfer(captor.capture());
        FundTransfer saved = captor.getValue();
        assertNull(saved.getInstitutionId());
        assertNull(saved.getElderId());
        assertNull(saved.getApplyId());
        assertNull(saved.getStatus());
        assertNull(saved.getTransferStatus());
        assertNull(saved.getIsPaid());
        assertNull(saved.getPaidTime());
        assertEquals(new BigDecimal("999.00"), saved.getTransferAmount());
    }

    @Test
    void monthlyTransfersAreGeneratedForEachFuturePeriod()
    {
        FundTransferServiceImpl service = new FundTransferServiceImpl();
        FundTransferMapper transfers = mock(FundTransferMapper.class);
        com.ruoyi.mapper.bank.BankSettlementMapper settlement =
                mock(com.ruoyi.mapper.bank.BankSettlementMapper.class);
        com.ruoyi.service.pension.ITransferRuleConfigService rules =
                mock(com.ruoyi.service.pension.ITransferRuleConfigService.class);
        ReflectionTestUtils.setField(service, "fundTransferMapper", transfers);
        ReflectionTestUtils.setField(service, "settlementMapper", settlement);
        ReflectionTestUtils.setField(service, "transferRuleConfigService", rules);
        com.ruoyi.domain.pension.TransferRuleConfig rule =
                new com.ruoyi.domain.pension.TransferRuleConfig();
        rule.setTransferCycle("monthly");
        rule.setTransferDay(5);
        when(rules.selectTransferRuleConfigList(any())).thenReturn(java.util.List.of(rule));
        when(settlement.bySource(any())).thenReturn(null);
        when(transfers.insertFundTransfer(any())).thenReturn(1);
        java.util.Date start = java.sql.Timestamp.valueOf("2026-01-10 00:00:00");
        service.generateMonthlyTransfersForOrder(9L, 10L, 20L, 3, start,
                new BigDecimal("100.00"), false);
        ArgumentCaptor<FundTransfer> captor = ArgumentCaptor.forClass(FundTransfer.class);
        verify(transfers, times(3)).insertFundTransfer(captor.capture());
        java.util.List<FundTransfer> saved = captor.getAllValues();
        assertEquals("2026-02", saved.get(0).getBillingMonth());
        assertEquals("2026-03", saved.get(1).getBillingMonth());
        assertEquals("2026-04", saved.get(2).getBillingMonth());
        assertEquals(new BigDecimal("100.00"), saved.get(0).getTransferAmount());
        assertEquals("MONTH:9:2026-02", saved.get(0).getSourceKey());
        assertEquals("SERVICE", saved.get(0).getBalanceType());
        assertEquals("pending", saved.get(0).getStatus());
    }

    @Test
    void approvedApplicationExecutionSubmitsEachTransferToBank()
    {
        FundTransferApplyServiceImpl service = new FundTransferApplyServiceImpl();
        com.ruoyi.mapper.pension.FundTransferApplyMapper applies =
                mock(com.ruoyi.mapper.pension.FundTransferApplyMapper.class);
        com.ruoyi.mapper.pension.FundTransferApplyDetailMapper details =
                mock(com.ruoyi.mapper.pension.FundTransferApplyDetailMapper.class);
        BankPayoutService payouts = mock(BankPayoutService.class);
        ReflectionTestUtils.setField(service, "fundTransferApplyMapper", applies);
        ReflectionTestUtils.setField(service, "fundTransferApplyDetailMapper", details);
        ReflectionTestUtils.setField(service, "bankPayoutService", payouts);
        com.ruoyi.domain.pension.FundTransferApply apply = new com.ruoyi.domain.pension.FundTransferApply();
        apply.setApplyId(1L);
        apply.setInstitutionId(10L);
        apply.setApplyStatus("approved");
        when(applies.selectFundTransferApplyByApplyId(1L)).thenReturn(apply);
        com.ruoyi.domain.pension.FundTransferApplyDetail detail = new com.ruoyi.domain.pension.FundTransferApplyDetail();
        detail.setTransferId(20L);
        when(details.selectFundTransferApplyDetailByApplyId(1L)).thenReturn(java.util.List.of(detail));
        com.ruoyi.common.core.domain.model.LoginUser login =
                new com.ruoyi.common.core.domain.model.LoginUser();
        com.ruoyi.common.core.domain.entity.SysUser user = new com.ruoyi.common.core.domain.entity.SysUser();
        user.setUserName("tester");
        login.setUser(user);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        login, null, java.util.Collections.emptyList()));
        try
        {
            assertEquals(1, service.executeTransfer(1L));
            verify(payouts).submit(20L, "tester", false);
        }
        finally
        {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
        }
    }

    @Test
    void unapprovedApplicationExecutionCannotSend()
    {
        FundTransferApplyServiceImpl service = new FundTransferApplyServiceImpl();
        com.ruoyi.mapper.pension.FundTransferApplyMapper applies =
                mock(com.ruoyi.mapper.pension.FundTransferApplyMapper.class);
        BankPayoutService payouts = mock(BankPayoutService.class);
        ReflectionTestUtils.setField(service, "fundTransferApplyMapper", applies);
        ReflectionTestUtils.setField(service, "bankPayoutService", payouts);
        com.ruoyi.domain.pension.FundTransferApply apply = new com.ruoyi.domain.pension.FundTransferApply();
        apply.setApplyId(1L);
        apply.setInstitutionId(10L);
        apply.setApplyStatus("pending_supervision");
        when(applies.selectFundTransferApplyByApplyId(1L)).thenReturn(apply);
        assertThrows(ServiceException.class, () -> service.executeTransfer(1L));
        verifyNoInteractions(payouts);
    }

    @Test
    void linkedExpenseRecordsCannotBeEditedOrDeleted()
    {
        ExpenseRecordServiceImpl service = new ExpenseRecordServiceImpl();
        ExpenseRecordMapper mapper = mock(ExpenseRecordMapper.class);
        ReflectionTestUtils.setField(service, "expenseRecordMapper", mapper);
        ExpenseRecord record = new ExpenseRecord();
        record.setRecordId(1L);
        record.setRelatedId(9L);
        record.setRelatedType("order_info");
        when(mapper.selectExpenseRecordByRecordId(1L)).thenReturn(record);
        assertThrows(ServiceException.class, () -> service.updateExpenseRecord(record));
        assertThrows(ServiceException.class, () -> service.deleteExpenseRecordByRecordId(1L));
        verify(mapper, never()).updateExpenseRecord(any());
        verify(mapper, never()).deleteExpenseRecordByRecordId(any());
    }
}
