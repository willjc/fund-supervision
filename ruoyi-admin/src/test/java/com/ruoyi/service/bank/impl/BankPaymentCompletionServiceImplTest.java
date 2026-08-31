package com.ruoyi.service.bank.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.OrderItem;
import com.ruoyi.domain.PaymentRecord;
import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.domain.bank.BankPaymentCompletionResult;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.FundTransfer;
import com.ruoyi.domain.pension.SupervisionAccountLog;
import com.ruoyi.mapper.BedAllocationMapper;
import com.ruoyi.mapper.ElderInfoMapper;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.OrderItemMapper;
import com.ruoyi.mapper.PaymentRecordMapper;
import com.ruoyi.mapper.PensionInstitutionMapper;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.mapper.pension.AccountInfoMapper;
import com.ruoyi.service.pension.IExpenseRecordService;
import com.ruoyi.service.pension.IFundTransferService;
import com.ruoyi.service.pension.ISupervisionAccountLogService;

@ExtendWith(MockitoExtension.class)
class BankPaymentCompletionServiceImplTest
{
    @Mock private BankTransactionMapper transactionMapper;
    @Mock private OrderInfoMapper orderInfoMapper;
    @Mock private PaymentRecordMapper paymentRecordMapper;
    @Mock private PensionInstitutionMapper institutionMapper;
    @Mock private ElderInfoMapper elderInfoMapper;
    @Mock private AccountInfoMapper accountInfoMapper;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private IExpenseRecordService expenseRecordService;
    @Mock private ISupervisionAccountLogService supervisionAccountLogService;
    @Mock private IFundTransferService fundTransferService;
    @Mock private BedAllocationMapper bedAllocationMapper;

    @InjectMocks
    private BankPaymentCompletionServiceImpl service;

    private BankTransaction transaction;
    private OrderInfo order;

    @BeforeEach
    void setUp()
    {
        transaction = new BankTransaction();
        transaction.setTransactionId(1L);
        transaction.setRequestNo("BP001");
        transaction.setBusinessType("PAY");
        transaction.setBusinessId(10L);
        transaction.setInstitutionId(20L);
        transaction.setChannelType("wechat");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setStatus("PENDING");

        order = new OrderInfo();
        order.setOrderId(10L);
        order.setOrderNo("ORD001");
        order.setOrderType("1");
        order.setOrderStatus("0");
        order.setElderId(30L);
        order.setInstitutionId(20L);
        order.setOrderAmount(new BigDecimal("100.00"));
        order.setMonthCount(1);
        order.setServiceStartDate(new Date());
    }

    @Test
    void completePaymentShouldSettleFirstPendingPaymentExactlyOnce()
    {
        when(transactionMapper.selectByRequestNoForUpdate("BP001")).thenReturn(transaction);
        when(orderInfoMapper.selectOrderInfoByOrderIdForUpdate(10L)).thenReturn(order);
        when(institutionMapper.selectPensionInstitutionForUpdate(20L)).thenReturn(new PensionInstitution());

        ElderInfo elder = new ElderInfo();
        elder.setElderId(30L);
        when(elderInfoMapper.selectElderInfoForUpdate(30L)).thenReturn(elder);

        AccountInfo account = new AccountInfo();
        account.setAccountId(40L);
        account.setElderId(30L);
        account.setInstitutionId(20L);
        account.setAccountStatus("1");
        account.setTotalBalance(new BigDecimal("10.00"));
        account.setServiceBalance(new BigDecimal("10.00"));
        account.setDepositBalance(BigDecimal.ZERO);
        account.setMemberBalance(BigDecimal.ZERO);
        when(accountInfoMapper.selectAccountInfoForUpdate(30L, 20L)).thenReturn(account);

        OrderItem serviceItem = item("bed_fee", "60.00", 1L);
        OrderItem depositItem = item("deposit", "40.00", 1L);
        when(orderItemMapper.selectOrderItemsByOrderId(10L))
                .thenReturn(Arrays.asList(serviceItem, depositItem));

        when(orderInfoMapper.markOrderPaid(eq(10L), eq(new BigDecimal("100.00")),
                eq("微信"), any(Date.class), eq("99"))).thenReturn(1);
        when(paymentRecordMapper.insertPaymentRecord(any(PaymentRecord.class))).thenReturn(1);
        when(accountInfoMapper.updateAccountBalance(eq(40L), any(), any(), any(), any())).thenReturn(1);
        when(expenseRecordService.createOrderExpenseRecords(eq(30L), eq(40L), eq(10L), eq("1"),
                eq(new BigDecimal("40.00")), eq(new BigDecimal("60.00")), eq(BigDecimal.ZERO),
                eq(BigDecimal.ZERO), eq(new BigDecimal("10.00")), eq(new BigDecimal("110.00")),
                eq(new BigDecimal("60.00"))))
                .thenReturn(3);
        when(supervisionAccountLogService.recordIncome(eq(20L), eq(10L), eq(new BigDecimal("100.00")),
                eq("用户支付订单-ORD001"), eq("99"))).thenReturn(log(1L));
        when(fundTransferService.insertFundTransfer(any(FundTransfer.class))).thenAnswer(invocation -> {
            FundTransfer transfer = invocation.getArgument(0);
            transfer.setTransferId(50L);
            return 1;
        });
        when(supervisionAccountLogService.recordTransferOut(eq(20L), any(Long.class),
                eq(new BigDecimal("60.00")), eq("首月服务费划拨-ORD001"), eq("基本账户")))
                .thenReturn(log(2L));
        when(transactionMapper.markSuccess(eq(1L), eq("BANK001"), eq("0000"), eq("成功"),
                any(Date.class), any(Date.class))).thenReturn(1);

        BankPaymentCompletionResult result = service.completePayment(
                "BP001", "BANK001", "0000", "成功", "99");

        assertFalse(result.isAlreadyProcessed());
        assertEquals(10L, result.getOrderId());
        assertEquals(new BigDecimal("100.00"), result.getPaidAmount());

        ArgumentCaptor<PaymentRecord> paymentCaptor = ArgumentCaptor.forClass(PaymentRecord.class);
        verify(paymentRecordMapper).insertPaymentRecord(paymentCaptor.capture());
        assertEquals("PAY" + DigestUtils.md5DigestAsHex("BP001".getBytes(StandardCharsets.UTF_8)),
                paymentCaptor.getValue().getPaymentNo());
        assertEquals("BANK001", paymentCaptor.getValue().getTransactionId());

        verify(accountInfoMapper).updateAccountBalance(40L,
                new BigDecimal("50.00"), new BigDecimal("10.00"),
                new BigDecimal("40.00"), BigDecimal.ZERO);
        verify(transactionMapper).markSuccess(eq(1L), eq("BANK001"), eq("0000"), eq("成功"),
                any(Date.class), any(Date.class));
    }

    @Test
    void completePaymentShouldReturnAlreadyProcessedWithoutDuplicateWrites()
    {
        transaction.setStatus("SUCCESS");
        transaction.setBankSerialNo("BANK001");
        order.setOrderStatus("1");
        order.setPaidAmount(new BigDecimal("100.00"));
        order.setPaymentMethod("微信");
        order.setPaymentTime(new Date());

        PaymentRecord payment = new PaymentRecord();
        payment.setPaymentNo("PAY" + DigestUtils.md5DigestAsHex("BP001".getBytes(StandardCharsets.UTF_8)));
        payment.setOrderId(10L);
        payment.setInstitutionId(20L);
        payment.setPaymentAmount(new BigDecimal("100.00"));
        payment.setTransactionId("BANK001");
        payment.setPaymentStatus("1");

        when(transactionMapper.selectByRequestNoForUpdate("BP001")).thenReturn(transaction);
        when(orderInfoMapper.selectOrderInfoByOrderIdForUpdate(10L)).thenReturn(order);
        when(paymentRecordMapper.selectPaymentRecordByPaymentNo(payment.getPaymentNo())).thenReturn(payment);

        BankPaymentCompletionResult result = service.completePayment(
                "BP001", "BANK001", "0000", "成功", "99");

        assertTrue(result.isAlreadyProcessed());
        verify(orderInfoMapper, never()).markOrderPaid(any(), any(), any(), any(), any());
        verify(paymentRecordMapper, never()).insertPaymentRecord(any());
        verify(accountInfoMapper, never()).updateAccountBalance(any(), any(), any(), any(), any());
        verify(expenseRecordService, never()).createOrderExpenseRecords(any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any());
        verify(expenseRecordService, never()).createOrderExpenseRecords(any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any());
        verify(supervisionAccountLogService, never()).recordIncome(any(), any(), any(), any(), any());
        verify(fundTransferService, never()).insertFundTransfer(any());
        verify(transactionMapper, never()).markSuccess(any(), any(), any(), any(), any(), any());
    }

    @Test
    void completePaymentShouldRejectAmountMismatchBeforeSettlement()
    {
        transaction.setAmount(new BigDecimal("99.99"));
        stubLockedTransactionAndOrder();

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.completePayment("BP001", "BANK001", "0000", "成功", "99"));

        assertEquals("银行交易金额与订单金额不一致", exception.getMessage());
        verifyNoSettlementWrites();
    }

    @Test
    void completePaymentShouldRejectInstitutionMismatchBeforeSettlement()
    {
        transaction.setInstitutionId(21L);
        stubLockedTransactionAndOrder();

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.completePayment("BP001", "BANK001", "0000", "成功", "99"));

        assertEquals("银行交易机构与订单机构不一致", exception.getMessage());
        verifyNoSettlementWrites();
    }

    @Test
    void completePaymentShouldRejectFailedStateBeforeSettlement()
    {
        transaction.setStatus("FAILED");
        stubLockedTransactionAndOrder();

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.completePayment("BP001", "BANK001", "0000", "成功", "99"));

        assertEquals("失败状态的银行交易不能完成支付", exception.getMessage());
        verifyNoSettlementWrites();
    }

    @Test
    void completePaymentMustRollbackForAnyException() throws Exception
    {
        Transactional transactional = BankPaymentCompletionServiceImpl.class
                .getMethod("completePayment", String.class, String.class, String.class, String.class, String.class)
                .getAnnotation(Transactional.class);

        assertEquals(Exception.class, transactional.rollbackFor()[0]);
    }

    private void stubLockedTransactionAndOrder()
    {
        when(transactionMapper.selectByRequestNoForUpdate("BP001")).thenReturn(transaction);
        when(orderInfoMapper.selectOrderInfoByOrderIdForUpdate(10L)).thenReturn(order);
    }

    private void verifyNoSettlementWrites()
    {
        verify(orderInfoMapper, never()).markOrderPaid(any(), any(), any(), any(), any());
        verify(paymentRecordMapper, never()).insertPaymentRecord(any());
        verify(accountInfoMapper, never()).updateAccountBalance(any(), any(), any(), any(), any());
        verify(transactionMapper, never()).markSuccess(any(), any(), any(), any(), any(), any());
    }

    private static OrderItem item(String type, String amount, Long quantity)
    {
        OrderItem item = new OrderItem();
        item.setItemType(type);
        item.setTotalAmount(new BigDecimal(amount));
        item.setQuantity(quantity);
        return item;
    }

    private static SupervisionAccountLog log(Long id)
    {
        SupervisionAccountLog log = new SupervisionAccountLog();
        log.setLogId(id);
        return log;
    }
}
