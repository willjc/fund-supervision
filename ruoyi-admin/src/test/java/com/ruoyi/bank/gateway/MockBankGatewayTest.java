package com.ruoyi.bank.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Date;

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

    @Test
    void payoutAcceptanceMustRemainPendingUntilExplicitBankResult()
    {
        BankPayoutRequest request = new BankPayoutRequest();
        request.setRequestNo("PAYOUT001");
        request.setRequestTime(new Date());
        request.setPayerAccountNo("PAYER");
        request.setPayerAccountName("监管户");
        request.setPayeeAccountNo("PAYEE");
        request.setPayeeAccountName("基本户");
        request.setAmount(new BigDecimal("12.34"));

        assertEquals("UNKNOWN", gateway.queryPayout(request).getStatus());
        assertEquals("UNKNOWN", gateway.queryBalance("PAYER", "监管户").getStatus());
        gateway.setAvailableBalance("PAYER", new BigDecimal("20.00"));
        assertEquals(new BigDecimal("20.00"), gateway.queryBalance("PAYER", "监管户").getAvailableBalance());
        BankResult submitted = gateway.submitPayout(request);
        assertEquals("PENDING", submitted.getStatus());
        assertEquals("PAYER", submitted.getPayerAccountNo());
        assertEquals("PAYEE", submitted.getPayeeAccountNo());
        assertEquals(request.getAmount(), submitted.getPaidAmount());
        assertEquals("PENDING", gateway.queryPayout(request).getStatus());

        BankResult confirmed = BankResult.success("MOCK-FINAL001");
        confirmed.setRequestNo(request.getRequestNo());
        confirmed.setPaidAmount(request.getAmount());
        confirmed.setPayerAccountNo(request.getPayerAccountNo());
        confirmed.setPayeeAccountNo(request.getPayeeAccountNo());
        gateway.setPayoutResult(request.getRequestNo(), confirmed);
        assertEquals("SUCCESS", gateway.queryPayout(request).getStatus());
        assertEquals("MOCK-FINAL001", gateway.submitPayout(request).getBankSerialNo());
    }

    @Test
    void queryEchoesMissingRequestNumberWithoutHidingMismatchFixtures()
    {
        BankPayoutRequest request = new BankPayoutRequest();
        request.setRequestNo("PAYOUT002");
        request.setRequestTime(new Date());
        for (String requestNo : new String[] {null, "", "  "})
        {
            BankResult confirmed = BankResult.success("MOCK-FINAL002");
            confirmed.setRequestNo(requestNo);
            gateway.setPayoutResult(request.getRequestNo(), confirmed);
            assertEquals(request.getRequestNo(), gateway.queryPayout(request).getRequestNo());
        }
        BankResult mismatched = BankResult.success("MOCK-FINAL003");
        mismatched.setRequestNo("OTHER-PAYOUT");
        gateway.setPayoutResult(request.getRequestNo(), mismatched);
        assertEquals("OTHER-PAYOUT", gateway.queryPayout(request).getRequestNo());
    }
}
