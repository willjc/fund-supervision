package com.ruoyi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.BedAllocation;
import com.ruoyi.domain.BedInfo;
import com.ruoyi.domain.ElderInfo;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.pension.AccountInfo;
import com.ruoyi.mapper.BedAllocationMapper;
import com.ruoyi.mapper.BedInfoMapper;
import com.ruoyi.mapper.ElderInfoMapper;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.bank.BankSettlementMapper;
import com.ruoyi.service.pension.IAccountInfoService;

/** 退住强校验与状态流转。 */
@ExtendWith(MockitoExtension.class)
class PensionCheckinCheckoutTest
{
    @Mock private ElderInfoMapper elderInfoMapper;
    @Mock private BedAllocationMapper bedAllocationMapper;
    @Mock private BedInfoMapper bedInfoMapper;
    @Mock private OrderInfoMapper orderInfoMapper;
    @Mock private BankSettlementMapper settlementMapper;
    @Mock private com.ruoyi.service.pension.IAccountInfoService accountInfoService;

    @InjectMocks
    private PensionCheckinServiceImpl service;

    private BedAllocation activeAllocation()
    {
        BedAllocation allocation = new BedAllocation();
        allocation.setAllocationId(111L);
        allocation.setElderId(108L);
        allocation.setBedId(167L);
        allocation.setInstitutionId(32L);
        allocation.setAllocationStatus("0");
        return allocation;
    }

    private AccountInfo accountWith(String service, String deposit, String member)
    {
        AccountInfo account = new AccountInfo();
        account.setServiceBalance(new BigDecimal(service));
        account.setDepositBalance(new BigDecimal(deposit));
        account.setMemberBalance(new BigDecimal(member));
        return account;
    }

    @Test
    void checkoutShouldRejectWhenScopeMissing()
    {
        when(settlementMapper.hasScope(130L, 32L)).thenReturn(0);

        ServiceException error = assertThrows(ServiceException.class,
                () -> service.checkoutElder(108L, 32L, "jg", 130L));
        assertEquals("无权操作该机构的入住记录", error.getMessage());
        verify(bedAllocationMapper, never()).updateBedAllocation(any());
    }

    @Test
    void checkoutShouldRejectWhenNoActiveAllocation()
    {
        when(settlementMapper.hasScope(130L, 32L)).thenReturn(1);
        when(bedAllocationMapper.selectActiveByElderAndInstitution(108L, 32L)).thenReturn(null);

        ServiceException error = assertThrows(ServiceException.class,
                () -> service.checkoutElder(108L, 32L, "jg", 130L));
        assertTrue(error.getMessage().contains("没有在住记录"));
    }

    @Test
    void checkoutShouldRejectWhenUnpaidOrdersExist()
    {
        when(settlementMapper.hasScope(130L, 32L)).thenReturn(1);
        when(bedAllocationMapper.selectActiveByElderAndInstitution(108L, 32L)).thenReturn(activeAllocation());
        OrderInfo unpaid = new OrderInfo();
        unpaid.setOrderStatus("5");
        when(orderInfoMapper.selectOrderInfoList(any())).thenReturn(List.of(unpaid));

        ServiceException error = assertThrows(ServiceException.class,
                () -> service.checkoutElder(108L, 32L, "jg", 130L));
        assertTrue(error.getMessage().contains("待支付订单"));
        verify(bedAllocationMapper, never()).updateBedAllocation(any());
    }

    @Test
    void checkoutShouldRejectWhenBalanceRemains()
    {
        when(settlementMapper.hasScope(130L, 32L)).thenReturn(1);
        when(bedAllocationMapper.selectActiveByElderAndInstitution(108L, 32L)).thenReturn(activeAllocation());
        when(orderInfoMapper.selectOrderInfoList(any())).thenReturn(Collections.emptyList());
        when(accountInfoService.selectAccountInfoList(any()))
                .thenReturn(List.of(accountWith("0.01", "0", "0")));

        ServiceException error = assertThrows(ServiceException.class,
                () -> service.checkoutElder(108L, 32L, "jg", 130L));
        assertTrue(error.getMessage().contains("余额未清零"));
        verify(bedAllocationMapper, never()).updateBedAllocation(any());
    }

    @Test
    void checkoutShouldReleaseBedAndMarkElderCheckedOut()
    {
        when(bedAllocationMapper.selectActiveByElderAndInstitution(108L, 32L)).thenReturn(activeAllocation());
        when(orderInfoMapper.selectOrderInfoList(any())).thenReturn(Collections.emptyList());
        when(accountInfoService.selectAccountInfoList(any()))
                .thenReturn(List.of(accountWith("0", "0", "0")));
        when(bedAllocationMapper.updateBedAllocation(any(BedAllocation.class))).thenReturn(1);
        BedInfo bed = new BedInfo();
        bed.setBedStatus("1");
        when(bedInfoMapper.selectBedInfoByBedId(167L)).thenReturn(bed);
        when(bedInfoMapper.updateBedInfo(any(BedInfo.class))).thenReturn(1);
        ElderInfo elder = new ElderInfo();
        elder.setStatus("1");
        when(elderInfoMapper.selectElderInfoByElderId(108L)).thenReturn(elder);
        when(elderInfoMapper.updateElderInfo(any(ElderInfo.class))).thenReturn(1);

        assertEquals(1, service.checkoutElder(108L, 32L, "admin", null));

        ArgumentCaptor<BedAllocation> allocationCaptor = ArgumentCaptor.forClass(BedAllocation.class);
        verify(bedAllocationMapper).updateBedAllocation(allocationCaptor.capture());
        assertEquals("2", allocationCaptor.getValue().getAllocationStatus());
        assertEquals("0", bed.getBedStatus());
        ArgumentCaptor<ElderInfo> elderCaptor = ArgumentCaptor.forClass(ElderInfo.class);
        verify(elderInfoMapper).updateElderInfo(elderCaptor.capture());
        assertEquals("2", elderCaptor.getValue().getStatus());
    }
}
