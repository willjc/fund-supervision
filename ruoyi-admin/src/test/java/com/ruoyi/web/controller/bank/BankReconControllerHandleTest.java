package com.ruoyi.web.controller.bank;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.mapper.bank.BankReconMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 对账差异"标记已处理"：备注必填、0→1 幂等语义、参数校验。 */
class BankReconControllerHandleTest
{
    private static Map<String, String> params(String diffId, String remark)
    {
        Map<String, String> params = new HashMap<>();
        params.put("diffId", diffId);
        params.put("remark", remark);
        return params;
    }

    private static BankReconController controller(BankReconMapper mapper)
    {
        BankReconController controller = new BankReconController();
        ReflectionTestUtils.setField(controller, "reconMapper", mapper);
        return controller;
    }

    @Test
    void shouldMarkHandledWithRemark()
    {
        BankReconMapper mapper = mock(BankReconMapper.class);
        when(mapper.markDiffHandled(7L, "已线下核实")).thenReturn(1);

        AjaxResult result = controller(mapper).handleDiff(params("7", "已线下核实"));

        assertEquals(200, result.get("code"));
        verify(mapper).markDiffHandled(7L, "已线下核实");
    }

    @Test
    void shouldRejectMissingRemarkOrDiffId()
    {
        BankReconMapper mapper = mock(BankReconMapper.class);
        BankReconController controller = controller(mapper);

        assertEquals("处理说明必填", controller.handleDiff(params("7", " ")).get("msg"));
        assertEquals("差异记录必填", controller.handleDiff(params(null, "备注")).get("msg"));
        assertEquals("差异记录不合法", controller.handleDiff(params("x", "备注")).get("msg"));
        verify(mapper, never()).markDiffHandled(anyLong(), anyString());
    }

    @Test
    void alreadyHandledShouldReturnError()
    {
        BankReconMapper mapper = mock(BankReconMapper.class);
        when(mapper.markDiffHandled(7L, "重复处理")).thenReturn(0);

        AjaxResult result = controller(mapper).handleDiff(params("7", "重复处理"));

        assertEquals("差异不存在或已处理", result.get("msg"));
    }
}
