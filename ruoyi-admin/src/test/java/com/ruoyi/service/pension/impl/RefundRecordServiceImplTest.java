package com.ruoyi.service.pension.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.domain.pension.RefundRecord;
import com.ruoyi.mapper.pension.AccountInfoMapper;
import com.ruoyi.mapper.pension.RefundRecordMapper;
import com.ruoyi.service.pension.IExpenseRecordService;
import com.ruoyi.service.pension.ISupervisionAccountLogService;

@ExtendWith(MockitoExtension.class)
class RefundRecordServiceImplTest
{
    @Mock
    private RefundRecordMapper refundRecordMapper;

    @Mock
    private AccountInfoMapper accountInfoMapper;

    @Mock
    private IExpenseRecordService expenseRecordService;

    @Mock
    private ISupervisionAccountLogService supervisionAccountLogService;

    @InjectMocks
    private RefundRecordServiceImpl refundRecordService;

    private RefundRecord refund;
    private AccountInfo account;

    @BeforeEach
    void setUp()
    {
        refund = new RefundRecord();
        refund.setRefundId(1L);
        refund.setRefundNo("REF001");
        refund.setElderId(10L);
        refund.setInstitutionId(20L);
        refund.setRefundStatus("0");
        refund.setServiceRefundAmount(new BigDecimal("30.00"));
        refund.setDepositRefundAmount(new BigDecimal("20.00"));
        refund.setMemberRefundAmount(BigDecimal.ZERO);
        refund.setRefundAmount(new BigDecimal("50.00"));

        account = new AccountInfo();
        account.setAccountId(100L);
        account.setElderId(10L);
        account.setInstitutionId(20L);
        account.setAccountStatus("1");
        account.setServiceBalance(new BigDecimal("80.00"));
        account.setDepositBalance(new BigDecimal("60.00"));
        account.setMemberBalance(new BigDecimal("40.00"));
        account.setTotalBalance(new BigDecimal("180.00"));
    }

    @Test
    void approveRefundShouldUpdateBalanceAndAllRecords()
    {
        when(refundRecordMapper.selectRefundRecordForUpdate(1L, 99L)).thenReturn(refund);
        when(accountInfoMapper.selectAccountInfoForUpdate(10L, 20L)).thenReturn(account);
        when(accountInfoMapper.updateAccountBalance(eq(100L), any(), any(), any(), any())).thenReturn(1);
        when(expenseRecordService.createExpenseRecord(eq(10L), eq(100L), anyString(),
                eq("expense"), any(), anyString(), eq(1L), eq("refund"), any(), any())).thenReturn(1);
        when(refundRecordMapper.updateRefundRecord(refund)).thenReturn(1);

        int result = refundRecordService.approveRefund(1L, "admin", 99L);

        assertEquals(1, result);
        assertEquals("1", refund.getRefundStatus());
        assertEquals("admin", refund.getApprover());
        verify(accountInfoMapper).updateAccountBalance(100L, new BigDecimal("130.00"),
                new BigDecimal("50.00"), new BigDecimal("40.00"), new BigDecimal("40.00"));
        verify(expenseRecordService).createExpenseRecord(eq(10L), eq(100L), eq("service"),
                eq("expense"), eq(new BigDecimal("30.00")), anyString(), eq(1L), eq("refund"),
                eq(new BigDecimal("180.00")), eq(new BigDecimal("130.00")));
        verify(expenseRecordService).createExpenseRecord(eq(10L), eq(100L), eq("deposit"),
                eq("expense"), eq(new BigDecimal("20.00")), anyString(), eq(1L), eq("refund"),
                eq(new BigDecimal("180.00")), eq(new BigDecimal("130.00")));
        verify(supervisionAccountLogService).recordTransferOut(20L, 1L,
                new BigDecimal("50.00"), "退款划拨-REF001", "基本账户");
        verify(refundRecordMapper).updateRefundRecord(refund);
    }

    @Test
    void approveRefundShouldRejectAlreadyProcessedRecord()
    {
        refund.setRefundStatus("1");
        when(refundRecordMapper.selectRefundRecordForUpdate(1L, 99L)).thenReturn(refund);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> refundRecordService.approveRefund(1L, "admin", 99L));

        assertEquals("只能审批待处理状态的退款", exception.getMessage());
        verify(accountInfoMapper, never()).selectAccountInfoForUpdate(any(), any());
    }

    @Test
    void approveRefundShouldRejectInsufficientComponentBalance()
    {
        account.setServiceBalance(new BigDecimal("10.00"));
        when(refundRecordMapper.selectRefundRecordForUpdate(1L, 99L)).thenReturn(refund);
        when(accountInfoMapper.selectAccountInfoForUpdate(10L, 20L)).thenReturn(account);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> refundRecordService.approveRefund(1L, "admin", 99L));

        assertEquals("服务费余额不足，当前余额：10.00元", exception.getMessage());
        verify(accountInfoMapper, never()).updateAccountBalance(any(), any(), any(), any(), any());
    }

    @Test
    void approveRefundShouldPropagateExpenseFailureForTransactionRollback()
    {
        when(refundRecordMapper.selectRefundRecordForUpdate(1L, 99L)).thenReturn(refund);
        when(accountInfoMapper.selectAccountInfoForUpdate(10L, 20L)).thenReturn(account);
        when(accountInfoMapper.updateAccountBalance(eq(100L), any(), any(), any(), any())).thenReturn(1);
        when(expenseRecordService.createExpenseRecord(eq(10L), eq(100L), eq("service"),
                eq("expense"), any(), anyString(), eq(1L), eq("refund"), any(), any())).thenReturn(0);

        ServiceException exception = assertThrows(ServiceException.class,
                () -> refundRecordService.approveRefund(1L, "admin", 99L));

        assertEquals("记录服务费退款流水失败", exception.getMessage());
        verify(refundRecordMapper, never()).updateRefundRecord(any());
        verify(supervisionAccountLogService, never()).recordTransferOut(any(), any(), any(), any(), any());
    }
}
