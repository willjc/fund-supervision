package com.ruoyi.service.bank.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.ElderFamily;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.bank.BankPaymentCompletionResult;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.service.IElderFamilyService;
import com.ruoyi.service.bank.IBankPaymentCompletionService;

@ExtendWith(MockitoExtension.class)
class MockBankPaymentServiceImplTest
{
    @Mock private BankTransactionMapper transactionMapper;
    @Mock private OrderInfoMapper orderInfoMapper;
    @Mock private IElderFamilyService elderFamilyService;
    @Mock private IBankPaymentCompletionService completionService;
    @InjectMocks private MockBankPaymentServiceImpl service;

    private BankTransaction transaction;
    private OrderInfo order;

    @BeforeEach
    void setUp()
    {
        transaction = new BankTransaction();
        transaction.setRequestNo("BP001");
        transaction.setBusinessType("PAY");
        transaction.setBusinessId(10L);
        transaction.setBankSerialNo("MOCK-BP001");
        transaction.setPayUrl("mock-bank://checkout/BP001");

        order = new OrderInfo();
        order.setOrderId(10L);
        order.setElderId(30L);
    }

    @Test
    void shouldAuthorizeOwnerAndCompleteFromPersistedTransaction()
    {
        BankPaymentCompletionResult expected = BankPaymentCompletionResult.completed(
                10L, "ORD001", new BigDecimal("100.00"), new java.util.Date(), false);
        when(transactionMapper.selectByRequestNo("BP001")).thenReturn(transaction);
        when(orderInfoMapper.selectOrderInfoByOrderId(10L)).thenReturn(order);
        when(elderFamilyService.selectElderFamilyList(any(ElderFamily.class)))
                .thenReturn(Collections.singletonList(new ElderFamily()));
        when(completionService.completePayment("BP001", "MOCK-BP001", "MOCK_SUCCESS",
                "模拟银行支付成功", "99")).thenReturn(expected);

        assertEquals(expected, service.completeMockPayment("BP001", 99L));
        verify(completionService).completePayment("BP001", "MOCK-BP001", "MOCK_SUCCESS",
                "模拟银行支付成功", "99");
    }

    @Test
    void shouldRejectUserWithoutElderRelationship()
    {
        when(transactionMapper.selectByRequestNo("BP001")).thenReturn(transaction);
        when(orderInfoMapper.selectOrderInfoByOrderId(10L)).thenReturn(order);
        when(elderFamilyService.selectElderFamilyList(any(ElderFamily.class)))
                .thenReturn(Collections.emptyList());

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.completeMockPayment("BP001", 99L));

        assertEquals("您没有权限完成该订单支付", exception.getMessage());
        verify(completionService, never()).completePayment(any(), any(), any(), any(), any());
    }

    @Test
    void shouldRejectNonMockTransaction()
    {
        transaction.setBankSerialNo("REAL001");
        transaction.setPayUrl("https://bank.example/pay");
        when(transactionMapper.selectByRequestNo("BP001")).thenReturn(transaction);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.completeMockPayment("BP001", 99L));

        assertEquals("该交易不是模拟银行支付", exception.getMessage());
        verify(completionService, never()).completePayment(any(), any(), any(), any(), any());
    }
}
