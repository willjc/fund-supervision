package com.ruoyi.service.bank.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.bank.gateway.BankGateway;
import com.ruoyi.bank.gateway.BankQueryRequest;
import com.ruoyi.bank.gateway.BankResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.bank.BankSettlementMapper;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.service.bank.impl.BankRefundService.BankRefundTxWorker;

@ExtendWith(MockitoExtension.class)
class BankRefundServiceTest
{
    @Mock private BankGateway gateway;
    @Mock private BankTransactionMapper transactions;
    @Mock private BankSettlementMapper settlement;
    @Mock private OrderInfoMapper orders;
    @Mock private BankRefundTxWorker worker;

    @InjectMocks
    private BankRefundService service;

    private BankTransaction refundTx()
    {
        BankTransaction tx = new BankTransaction();
        tx.setTransactionId(7L);
        tx.setRequestNo("BR123456789012345678901234567890");
        tx.setBusinessType("REFUND");
        tx.setBusinessId(5L);
        tx.setInstitutionId(32L);
        tx.setMerId("8202106040000001");
        tx.setAmount(new BigDecimal("0.05"));
        tx.setStatus("PENDING");
        tx.setCreateTime(new Date());
        com.ruoyi.bank.gateway.BankRefundRequest snapshot = new com.ruoyi.bank.gateway.BankRefundRequest();
        snapshot.setRequestNo("BR123456789012345678901234567890");
        snapshot.setRequestTime(new Date());
        snapshot.setMerId("8202106040000001");
        snapshot.setAmount(new BigDecimal("0.05"));
        snapshot.setOriginalRequestNo("BP1A746389E4594FA49A896413E726E7");
        snapshot.setOriginalBankSerialNo("20026091017164457253353180271553");
        snapshot.setOriginalBankTime("20260910171644");
        tx.setSnapshotJson(com.alibaba.fastjson2.JSON.toJSONString(snapshot));
        return tx;
    }

    @Test
    void assertRefundableShouldRejectMissingOrder()
    {
        ServiceException error = assertThrows(ServiceException.class,
                () -> service.assertRefundable(null, 112L, 32L, new BigDecimal("0.01")));
        assertEquals("银行退款必须关联原支付订单", error.getMessage());
    }

    @Test
    void assertRefundableShouldRejectUnpaidOrder()
    {
        OrderInfo order = new OrderInfo();
        order.setElderId(112L);
        order.setInstitutionId(32L);
        order.setOrderStatus("0");
        when(orders.selectOrderInfoByOrderId(145L)).thenReturn(order);

        ServiceException error = assertThrows(ServiceException.class,
                () -> service.assertRefundable(145L, 112L, 32L, new BigDecimal("0.01")));
        assertEquals("只有已支付订单可以申请原路退款", error.getMessage());
    }

    @Test
    void assertRefundableShouldRejectWhenPaymentNotBankConfirmed()
    {
        OrderInfo order = new OrderInfo();
        order.setElderId(112L);
        order.setInstitutionId(32L);
        order.setOrderStatus("1");
        when(orders.selectOrderInfoByOrderId(145L)).thenReturn(order);
        BankTransaction payment = new BankTransaction();
        payment.setStatus("PENDING");
        payment.setBankStatus("PENDING");
        when(transactions.selectByBusiness("PAY", 145L)).thenReturn(payment);

        ServiceException error = assertThrows(ServiceException.class,
                () -> service.assertRefundable(145L, 112L, 32L, new BigDecimal("0.01")));
        assertEquals("该订单没有银行成功支付记录，不能原路退款", error.getMessage());
    }

    @Test
    void assertRefundableShouldRejectWhenHeadroomExceeded()
    {
        OrderInfo order = new OrderInfo();
        order.setElderId(112L);
        order.setInstitutionId(32L);
        order.setOrderStatus("1");
        when(orders.selectOrderInfoByOrderId(145L)).thenReturn(order);
        BankTransaction payment = new BankTransaction();
        payment.setStatus("SUCCESS");
        payment.setBankStatus("SUCCESS");
        payment.setAmount(new BigDecimal("0.05"));
        when(transactions.selectByBusiness("PAY", 145L)).thenReturn(payment);
        when(settlement.refundOccupied(145L)).thenReturn(new BigDecimal("0.04"));

        ServiceException error = assertThrows(ServiceException.class,
                () -> service.assertRefundable(145L, 112L, 32L, new BigDecimal("0.03")));
        assertEquals("退款金额超过订单剩余可退额度", error.getMessage());
    }

    @Test
    void assertRefundableShouldPassHappyPath()
    {
        OrderInfo order = new OrderInfo();
        order.setElderId(112L);
        order.setInstitutionId(32L);
        order.setOrderStatus("1");
        when(orders.selectOrderInfoByOrderId(145L)).thenReturn(order);
        BankTransaction payment = new BankTransaction();
        payment.setStatus("SUCCESS");
        payment.setBankStatus("SUCCESS");
        payment.setAmount(new BigDecimal("0.05"));
        when(transactions.selectByBusiness("PAY", 145L)).thenReturn(payment);
        when(settlement.refundOccupied(145L)).thenReturn(new BigDecimal("0.00"));

        assertDoesNotThrow(() -> service.assertRefundable(145L, 112L, 32L, new BigDecimal("0.03")));
    }

    @Test
    void approveShouldSchedulePollingWhenGatewayThrows()
    {
        BankTransaction tx = refundTx();
        when(worker.prepare(5L, "admin", null)).thenReturn(tx);
        when(gateway.refundPayment(any())).thenThrow(new ServiceException("连接超时"));

        assertEquals(1, service.approveAndSubmit(5L, "admin", null));

        verify(settlement).schedule(eq(7L), any(), anyInt());
        verify(worker, never()).failFast(any(), any());
        verify(transactions, never()).selectByRequestNo(any());
    }

    @Test
    void approveShouldFailFastOnSynchronousBusinessRejection()
    {
        BankTransaction tx = refundTx();
        when(worker.prepare(5L, "admin", null)).thenReturn(tx);
        BankResult rejected = BankResult.failed("1088", "退款金额超限");
        when(gateway.refundPayment(any())).thenReturn(rejected);
        when(worker.failFast(any(), any())).thenReturn(1);

        assertEquals(1, service.approveAndSubmit(5L, "admin", null));

        verify(worker).failFast(eq("BR123456789012345678901234567890"), same(rejected));
        verify(transactions, never()).selectByRequestNo(any());
    }

    @Test
    void approveShouldPersistAcceptedSerialBeforeReconcile()
    {
        BankTransaction tx = refundTx();
        when(worker.prepare(5L, "admin", null)).thenReturn(tx);
        BankResult accepted = BankResult.pending("BANKR1", null);
        when(gateway.refundPayment(any())).thenReturn(accepted);

        BankTransaction confirmed = refundTx();
        confirmed.setBankStatus("SUCCESS");
        confirmed.setStatus("SUCCESS");
        confirmed.setBookingStatus("DONE");
        confirmed.setBankSerialNo("BANKR1");
        when(transactions.selectByRequestNo("BR123456789012345678901234567890"))
                .thenReturn(tx, confirmed, confirmed, confirmed);
        when(settlement.claim(7L)).thenReturn(1);
        BankResult queried = BankResult.success("BANKR1");
        queried.setPaidAmount(new BigDecimal("0.05"));
        queried.setBankTransactionTime("20260911101500");
        when(gateway.queryPayment(any(BankQueryRequest.class))).thenAnswer(invocation -> {
            BankQueryRequest q = invocation.getArgument(0);
            org.junit.jupiter.api.Assertions.assertEquals("BANKR1", q.getBankSerialNo());
            return queried;
        });

        assertEquals(1, service.approveAndSubmit(5L, "admin", null));

        verify(settlement, org.mockito.Mockito.atLeastOnce()).observe(any(BankTransaction.class));
        verify(worker).book("BR123456789012345678901234567890");
        verify(settlement).releaseClaim(7L);
    }

    @Test
    void approveShouldReconcileAfterAcceptedSubmission()
    {
        BankTransaction tx = refundTx();
        when(worker.prepare(5L, "admin", null)).thenReturn(tx);
        BankResult accepted = BankResult.pending("BANKR1", null);
        when(gateway.refundPayment(any())).thenReturn(accepted);

        BankTransaction confirmed = refundTx();
        confirmed.setBankStatus("SUCCESS");
        confirmed.setStatus("SUCCESS");
        confirmed.setBookingStatus("DONE");
        when(transactions.selectByRequestNo("BR123456789012345678901234567890"))
                .thenReturn(tx, confirmed, confirmed, confirmed);
        when(settlement.claim(7L)).thenReturn(1);
        BankResult queried = BankResult.success("BANKR1");
        queried.setPaidAmount(new BigDecimal("0.05"));
        queried.setBankTransactionTime("20260911101500");
        when(gateway.queryPayment(any(BankQueryRequest.class))).thenReturn(queried);

        assertEquals(1, service.approveAndSubmit(5L, "admin", null));

        verify(worker).book("BR123456789012345678901234567890");
        verify(settlement).releaseClaim(7L);
    }

    @Test
    void reconcileShouldForbidBookingWhenAmountMismatches()
    {
        BankTransaction tx = refundTx();
        when(transactions.selectByRequestNo("BR123456789012345678901234567890")).thenReturn(tx, tx, tx);
        when(settlement.claim(7L)).thenReturn(1);
        BankResult mismatch = BankResult.success("BANKR1");
        mismatch.setPaidAmount(new BigDecimal("0.04"));
        mismatch.setBankTransactionTime("20260911101500");
        when(gateway.queryPayment(any(BankQueryRequest.class))).thenReturn(mismatch);

        assertThrows(ServiceException.class,
                () -> service.reconcile("BR123456789012345678901234567890"));

        verify(worker, never()).book(any());
        verify(settlement).releaseClaim(7L);
    }

    @Test
    void reconcileShouldMarkFailedWhenBankReportsFailure()
    {
        BankTransaction tx = refundTx();
        when(transactions.selectByRequestNo("BR123456789012345678901234567890")).thenReturn(tx, tx, tx);
        when(settlement.claim(7L)).thenReturn(1);
        BankResult failed = BankResult.failed("1027", "原交易状态不允许退款");
        when(gateway.queryPayment(any(BankQueryRequest.class))).thenReturn(failed);

        service.reconcile("BR123456789012345678901234567890");

        verify(worker).fail("BR123456789012345678901234567890");
        verify(worker, never()).book(any());
    }

    @Test
    void reconcileShouldSkipWhenAlreadyDone()
    {
        BankTransaction tx = refundTx();
        tx.setBookingStatus("DONE");
        lenient().when(transactions.selectByRequestNo(any())).thenReturn(tx);

        assertEquals(tx, service.reconcile("BR123456789012345678901234567890"));

        verify(settlement, never()).claim(anyLong());
        verify(gateway, never()).queryPayment(any());
    }
}
