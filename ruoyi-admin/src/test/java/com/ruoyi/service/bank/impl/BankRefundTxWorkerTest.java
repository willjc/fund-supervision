package com.ruoyi.service.bank.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.RefundRecord;
import com.ruoyi.domain.pension.SupervisionAccountLog;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.PensionInstitutionMapper;
import com.ruoyi.mapper.bank.BankSettlementMapper;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.mapper.pension.AccountInfoMapper;
import com.ruoyi.mapper.pension.RefundRecordMapper;
import com.ruoyi.service.bank.IBankMerchantConfigService;
import com.ruoyi.service.bank.impl.BankRefundService.BankRefundTxWorker;
import com.ruoyi.service.pension.IExpenseRecordService;
import com.ruoyi.service.pension.ISupervisionAccountLogService;

@ExtendWith(MockitoExtension.class)
class BankRefundTxWorkerTest
{
    @Mock private BankTransactionMapper transactions;
    @Mock private BankSettlementMapper settlement;
    @Mock private RefundRecordMapper refunds;
    @Mock private AccountInfoMapper accounts;
    @Mock private OrderInfoMapper orders;
    @Mock private PensionInstitutionMapper institutions;
    @Mock private IBankMerchantConfigService merchants;
    @Mock private IExpenseRecordService expenses;
    @Mock private ISupervisionAccountLogService ledger;

    @InjectMocks
    private BankRefundTxWorker worker;

    private RefundRecord pendingRefund()
    {
        RefundRecord refund = new RefundRecord();
        refund.setRefundId(5L);
        refund.setRefundNo("REF20260911ABCD0001");
        refund.setOrderId(145L);
        refund.setElderId(112L);
        refund.setInstitutionId(32L);
        refund.setRefundStatus("0");
        refund.setRefundAmount(new BigDecimal("0.05"));
        refund.setServiceRefundAmount(new BigDecimal("0.03"));
        refund.setDepositRefundAmount(new BigDecimal("0.01"));
        refund.setMemberRefundAmount(new BigDecimal("0.01"));
        return refund;
    }

    private OrderInfo paidOrder()
    {
        OrderInfo order = new OrderInfo();
        order.setOrderId(145L);
        order.setElderId(112L);
        order.setInstitutionId(32L);
        order.setOrderStatus("1");
        return order;
    }

    private BankTransaction successfulPayment()
    {
        BankTransaction payment = new BankTransaction();
        payment.setTransactionId(12L);
        payment.setRequestNo("BP1A746389E4594FA49A896413E726E7");
        payment.setBusinessType("PAY");
        payment.setInstitutionId(32L);
        payment.setMerId("8202106040000001");
        payment.setAmount(new BigDecimal("0.05"));
        payment.setStatus("SUCCESS");
        payment.setBankStatus("SUCCESS");
        payment.setBankSerialNo("20026091017164457253353180271553");
        payment.setBankTime("20260910171644");
        payment.setChannelType("支付宝");
        payment.setEnvironment("sandbox");
        return payment;
    }

    private AccountInfo normalAccount()
    {
        AccountInfo account = new AccountInfo();
        account.setAccountId(63L);
        account.setAccountStatus("1");
        account.setTotalBalance(new BigDecimal("6529.05"));
        account.setServiceBalance(new BigDecimal("5099.03"));
        account.setDepositBalance(new BigDecimal("930.01"));
        account.setMemberBalance(new BigDecimal("500.01"));
        return account;
    }

    private void mockApproveHappyPath(RefundRecord refund)
    {
        when(refunds.selectRefundRecordForUpdate(5L, null)).thenReturn(refund);
        when(orders.selectOrderInfoByOrderIdForUpdate(145L)).thenReturn(paidOrder());
        when(transactions.selectByBusiness("PAY", 145L)).thenReturn(successfulPayment());
        when(settlement.refundOccupied(145L)).thenReturn(new BigDecimal("0.00"));
        when(settlement.refundAttemptCount(145L)).thenReturn(0);
        when(transactions.selectByBusiness("REFUND", 5L)).thenReturn(null);
        BankMerchantConfig merchant = new BankMerchantConfig();
        merchant.setMerId("8202106040000001");
        merchant.setMerchantName("郑州夕阳红集团金水区园区");
        when(merchants.selectEnabledByInstitutionId(32L)).thenReturn(merchant);
        when(accounts.selectAccountInfoForUpdate(112L, 32L)).thenReturn(normalAccount());
        when(transactions.insert(any(BankTransaction.class))).thenAnswer(invocation -> {
            invocation.getArgument(0, BankTransaction.class).setTransactionId(21L);
            return 1;
        });
        when(refunds.updateRefundRecord(any(RefundRecord.class))).thenReturn(1);
    }

    @Test
    void prepareShouldRejectRefundWithoutOrder()
    {
        RefundRecord refund = pendingRefund();
        refund.setOrderId(0L);
        when(refunds.selectRefundRecordForUpdate(5L, null)).thenReturn(refund);

        ServiceException error = assertThrows(ServiceException.class,
                () -> worker.prepare(5L, "admin", null));
        assertTrue(error.getMessage().contains("关联原支付订单"));
        verify(transactions, never()).insert(any(BankTransaction.class));
    }

    @Test
    void prepareShouldRejectWhenPaymentLacksBankFacts()
    {
        RefundRecord refund = pendingRefund();
        when(refunds.selectRefundRecordForUpdate(5L, null)).thenReturn(refund);
        when(orders.selectOrderInfoByOrderIdForUpdate(145L)).thenReturn(paidOrder());
        BankTransaction payment = successfulPayment();
        payment.setBankSerialNo(null);
        when(transactions.selectByBusiness("PAY", 145L)).thenReturn(payment);

        ServiceException error = assertThrows(ServiceException.class,
                () -> worker.prepare(5L, "admin", null));
        assertTrue(error.getMessage().contains("银行成功事实"));
    }

    @Test
    void prepareShouldRejectWhenHeadroomExceeded()
    {
        RefundRecord refund = pendingRefund();
        when(refunds.selectRefundRecordForUpdate(5L, null)).thenReturn(refund);
        when(orders.selectOrderInfoByOrderIdForUpdate(145L)).thenReturn(paidOrder());
        when(transactions.selectByBusiness("PAY", 145L)).thenReturn(successfulPayment());
        when(settlement.refundOccupied(145L)).thenReturn(new BigDecimal("0.04"));

        ServiceException error = assertThrows(ServiceException.class,
                () -> worker.prepare(5L, "admin", null));
        assertEquals("退款累计超过原支付可退金额", error.getMessage());
    }

    @Test
    void prepareShouldRejectDuplicateInFlightRefund()
    {
        RefundRecord refund = pendingRefund();
        when(refunds.selectRefundRecordForUpdate(5L, null)).thenReturn(refund);
        when(orders.selectOrderInfoByOrderIdForUpdate(145L)).thenReturn(paidOrder());
        when(transactions.selectByBusiness("PAY", 145L)).thenReturn(successfulPayment());
        when(settlement.refundOccupied(145L)).thenReturn(new BigDecimal("0.00"));
        when(settlement.refundAttemptCount(145L)).thenReturn(0);
        BankTransaction previous = new BankTransaction();
        previous.setStatus("PENDING");
        when(transactions.selectByBusiness("REFUND", 5L)).thenReturn(previous);

        ServiceException error = assertThrows(ServiceException.class,
                () -> worker.prepare(5L, "admin", null));
        assertTrue(error.getMessage().contains("不能重复发起"));
    }

    @Test
    void prepareShouldPersistPendingRefundTransactionAndMarkProcessing()
    {
        RefundRecord refund = pendingRefund();
        mockApproveHappyPath(refund);

        BankTransaction tx = worker.prepare(5L, "admin", null);

        assertEquals("REFUND", tx.getBusinessType());
        assertEquals("PENDING", tx.getStatus());
        assertEquals(5L, tx.getBusinessId());
        assertEquals(new BigDecimal("0.05"), tx.getAmount());
        assertTrue(tx.getSnapshotJson().contains("20026091017164457253353180271553"));
        assertTrue(tx.getRequestNo().startsWith("BR"));

        ArgumentCaptor<RefundRecord> captor = ArgumentCaptor.forClass(RefundRecord.class);
        verify(refunds).updateRefundRecord(captor.capture());
        assertEquals("3", captor.getValue().getRefundStatus());
        assertEquals(21L, captor.getValue().getBankTransactionId());
        assertEquals("admin", captor.getValue().getApprover());
    }

    @Test
    void prepareShouldAllowRetryAfterExplicitBankFailure()
    {
        RefundRecord refund = pendingRefund();
        refund.setRefundStatus("4");
        when(refunds.selectRefundRecordForUpdate(5L, null)).thenReturn(refund);
        when(orders.selectOrderInfoByOrderIdForUpdate(145L)).thenReturn(paidOrder());
        when(transactions.selectByBusiness("PAY", 145L)).thenReturn(successfulPayment());
        when(settlement.refundOccupied(145L)).thenReturn(new BigDecimal("0.00"));
        when(settlement.refundAttemptCount(145L)).thenReturn(1);
        BankTransaction previous = new BankTransaction();
        previous.setStatus("FAILED");
        previous.setBookingStatus("DONE");
        previous.setAttemptNo(1);
        when(transactions.selectByBusiness("REFUND", 5L)).thenReturn(previous);
        BankMerchantConfig merchant = new BankMerchantConfig();
        merchant.setMerId("8202106040000001");
        merchant.setMerchantName("郑州夕阳红集团金水区园区");
        when(merchants.selectEnabledByInstitutionId(32L)).thenReturn(merchant);
        when(accounts.selectAccountInfoForUpdate(112L, 32L)).thenReturn(normalAccount());
        when(transactions.insert(any(BankTransaction.class))).thenAnswer(invocation -> {
            invocation.getArgument(0, BankTransaction.class).setTransactionId(22L);
            return 1;
        });
        when(refunds.updateRefundRecord(any(RefundRecord.class))).thenReturn(1);

        BankTransaction retry = worker.prepare(5L, "admin", null);

        assertEquals("PENDING", retry.getStatus());
        assertEquals(2, retry.getAttemptNo());
        verify(refunds).updateRefundRecord(org.mockito.ArgumentMatchers.argThat(
                r -> "3".equals(r.getRefundStatus())));
    }

    @Test
    void bookShouldRejectWhenBankStatusNotSuccess()
    {
        BankTransaction tx = new BankTransaction();
        tx.setTransactionId(21L);
        tx.setRequestNo("BR123456789012345678901234567890");
        tx.setBusinessType("REFUND");
        tx.setBusinessId(5L);
        tx.setStatus("PENDING");
        tx.setBankStatus("PENDING");
        tx.setBookingStatus("PENDING");
        when(transactions.selectByRequestNoForUpdate(any())).thenReturn(tx);
        when(refunds.selectRefundRecordByRefundId(5L)).thenReturn(pendingRefund());

        ServiceException error = assertThrows(ServiceException.class,
                () -> worker.book("BR123456789012345678901234567890"));
        assertTrue(error.getMessage().contains("禁止扣账"));
        verify(accounts, never()).updateAccountBalance(anyLong(), any(), any(), any(), any());
    }

    @Test
    void bookShouldDeductBalancesAndCompleteRefund()
    {
        BankTransaction tx = new BankTransaction();
        tx.setTransactionId(21L);
        tx.setRequestNo("BR123456789012345678901234567890");
        tx.setBusinessType("REFUND");
        tx.setBusinessId(5L);
        tx.setInstitutionId(32L);
        tx.setStatus("PENDING");
        tx.setBankStatus("SUCCESS");
        tx.setBookingStatus("PENDING");
        tx.setBankTime("20260911101500");
        when(transactions.selectByRequestNoForUpdate(any())).thenReturn(tx);
        RefundRecord refund = pendingRefund();
        refund.setRefundStatus("3");
        refund.setBankTransactionId(21L);
        when(refunds.selectRefundRecordByRefundId(5L)).thenReturn(refund);
        when(institutions.selectPensionInstitutionForUpdate(32L)).thenReturn(new com.ruoyi.domain.PensionInstitution());
        when(refunds.selectRefundRecordForUpdate(eq(5L), isNull())).thenReturn(refund);
        when(accounts.selectAccountInfoForUpdate(112L, 32L)).thenReturn(normalAccount());
        when(accounts.refundBankBalance(eq(63L), any(), any(), any())).thenReturn(1);
        when(expenses.createExpenseRecord(anyLong(), anyLong(), any(), any(), any(),
                any(), any(), any(), any(), any())).thenReturn(1);
        when(ledger.recordTransferOut(anyLong(), anyLong(), any(), any(), any()))
                .thenReturn(new SupervisionAccountLog());
        when(refunds.updateRefundRecord(any(RefundRecord.class))).thenReturn(1);

        worker.book("BR123456789012345678901234567890");

        ArgumentCaptor<RefundRecord> captor = ArgumentCaptor.forClass(RefundRecord.class);
        verify(refunds).updateRefundRecord(captor.capture());
        assertEquals("1", captor.getValue().getRefundStatus());
        assertEquals("SUCCESS", tx.getStatus());
        assertEquals("DONE", tx.getBookingStatus());
        verify(settlement).finish(tx);
        verify(ledger).recordTransferOut(eq(32L), eq(5L), eq(new BigDecimal("0.05")), any(), any());
    }
}
