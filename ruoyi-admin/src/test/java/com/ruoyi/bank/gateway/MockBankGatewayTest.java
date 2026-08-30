package com.ruoyi.bank.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class MockBankGatewayTest
{
    private final MockBankGateway gateway = new MockBankGateway();

    @Test
    void createPaymentShouldRemainPendingUntilCallback()
    {
        BankPaymentRequest request = new BankPaymentRequest();
        request.setRequestNo("REQ001");
        request.setAmount(new BigDecimal("100.00"));

        BankResult result = gateway.createPayment(request);

        assertEquals("PENDING", result.getStatus());
        assertEquals("MOCK-REQ001", result.getBankSerialNo());
        assertEquals("mock-bank://checkout/REQ001", result.getPayUrl());
    }
}
