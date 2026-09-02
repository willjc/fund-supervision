package com.ruoyi.service.bank.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.bank.gateway.BankGateway;
import com.ruoyi.bank.gateway.BankPaymentRequest;
import com.ruoyi.bank.gateway.BankQueryRequest;
import com.ruoyi.bank.gateway.BankResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.service.bank.IBankMerchantConfigService;

@ExtendWith(MockitoExtension.class)
class BankPaymentServiceImplTest
{
    @Mock
    private BankGateway bankGateway;

    @Mock
    private IBankMerchantConfigService merchantConfigService;

    @Mock
    private BankTransactionMapper transactionMapper;

    @InjectMocks
    private BankPaymentServiceImpl service;

    @Test
    void createPaymentShouldReturnExistingTransactionIdempotently()
    {
        BankTransaction existing = new BankTransaction();
        existing.setStatus("PENDING");
        existing.setBankSerialNo("BANK001");
        existing.setPayUrl("https://bank.example/pay");
        when(transactionMapper.selectByBusiness("PAY", 1L)).thenReturn(existing);

        BankResult result = service.createPayment(1L, 36L, new BigDecimal("100.00"), "wechat", "订单");

        assertEquals("PENDING", result.getStatus());
        assertEquals("BANK001", result.getBankSerialNo());
        verify(bankGateway, never()).createPayment(any());
    }

    @Test
    void createPaymentShouldRequireEnabledVerifiedMerchant()
    {
        when(transactionMapper.selectByBusiness("PAY", 1L)).thenReturn(null);
        when(merchantConfigService.selectEnabledByInstitutionId(36L)).thenReturn(null);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.createPayment(1L, 36L, new BigDecimal("100.00"), "wechat", "订单"));

        assertEquals("该养老机构没有已验证、已启用的默认银行商户号", exception.getMessage());
    }

    @Test
    void createPaymentShouldPersistGatewayResult()
    {
        BankMerchantConfig merchant = new BankMerchantConfig();
        merchant.setMerId("MER001");
        merchant.setBankCode("ZZBANK");
        when(transactionMapper.selectByBusiness("PAY", 1L)).thenReturn(null);
        when(merchantConfigService.selectEnabledByInstitutionId(36L)).thenReturn(merchant);
        when(transactionMapper.insert(any(BankTransaction.class))).thenReturn(1);
        when(bankGateway.createPayment(any(BankPaymentRequest.class)))
                .thenReturn(BankResult.pending("BANK001", "https://bank.example/pay"));
        when(transactionMapper.updateResult(any(BankTransaction.class))).thenReturn(1);

        BankResult result = service.createPayment(1L, 36L, new BigDecimal("100.00"), "wechat", "订单");

        assertEquals("PENDING", result.getStatus());
        assertEquals("https://bank.example/pay", result.getPayUrl());
        ArgumentCaptor<BankPaymentRequest> requestCaptor = ArgumentCaptor.forClass(BankPaymentRequest.class);
        verify(bankGateway).createPayment(requestCaptor.capture());
        assertEquals(32, requestCaptor.getValue().getRequestNo().length());
        verify(transactionMapper).insert(any(BankTransaction.class));
        verify(transactionMapper).updateResult(any(BankTransaction.class));
    }

    @Test
    void createPaymentShouldPropagateDisabledGatewayAndAvoidFinalizingTransaction()
    {
        BankMerchantConfig merchant = new BankMerchantConfig();
        merchant.setMerId("MER001");
        merchant.setBankCode("ZZBANK");
        when(transactionMapper.selectByBusiness("PAY", 1L)).thenReturn(null);
        when(merchantConfigService.selectEnabledByInstitutionId(36L)).thenReturn(merchant);
        when(transactionMapper.insert(any(BankTransaction.class))).thenReturn(1);
        when(bankGateway.createPayment(any(BankPaymentRequest.class)))
                .thenThrow(new ServiceException("银行支付尚未启用"));

        ServiceException exception = assertThrows(ServiceException.class,
                () -> service.createPayment(1L, 36L, new BigDecimal("100.00"), "wechat", "订单"));

        assertEquals("银行支付尚未启用", exception.getMessage());
        verify(transactionMapper, never()).updateResult(any(BankTransaction.class));
    }

    @Test
    void queryPaymentShouldKeepOriginalRequestAndVerifyAmount()
    {
        BankTransaction transaction = pendingTransaction();
        when(transactionMapper.selectByBusiness("PAY", 1L)).thenReturn(transaction);
        BankResult bankResult = BankResult.success("BANK001");
        bankResult.setPaidAmount(new BigDecimal("100.00"));
        when(bankGateway.queryPayment(any(BankQueryRequest.class))).thenReturn(bankResult);

        BankResult result = service.queryPayment(1L);

        assertEquals("REQ001", result.getRequestNo());
        assertEquals("SUCCESS", result.getStatus());
    }

    @Test
    void queryPaymentShouldRejectMismatchedBankAmount()
    {
        BankTransaction transaction = pendingTransaction();
        when(transactionMapper.selectByBusiness("PAY", 1L)).thenReturn(transaction);
        BankResult bankResult = BankResult.success("BANK001");
        bankResult.setPaidAmount(new BigDecimal("0.01"));
        when(bankGateway.queryPayment(any(BankQueryRequest.class))).thenReturn(bankResult);

        ServiceException exception = assertThrows(ServiceException.class, () -> service.queryPayment(1L));

        assertEquals("银行支付金额与订单金额不一致，禁止入账", exception.getMessage());
    }

    private BankTransaction pendingTransaction()
    {
        BankTransaction transaction = new BankTransaction();
        transaction.setRequestNo("REQ001");
        transaction.setMerId("8202106040000001");
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setStatus("PENDING");
        transaction.setCreateTime(new Date());
        return transaction;
    }
}
