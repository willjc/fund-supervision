package com.ruoyi.service.bank.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.domain.ElderFamily;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.bank.BankPaymentCompletionResult;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.service.IElderFamilyService;
import com.ruoyi.service.bank.IBankPaymentCompletionService;
import com.ruoyi.service.bank.IMockBankPaymentService;

@Service
@ConditionalOnProperty(prefix = "bank.integration", name = "mode", havingValue = "mock")
public class MockBankPaymentServiceImpl implements IMockBankPaymentService
{
    @Autowired
    private BankTransactionMapper transactionMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private IElderFamilyService elderFamilyService;

    @Autowired
    private IBankPaymentCompletionService completionService;

    @Override
    public BankPaymentCompletionResult completeMockPayment(String requestNo, Long userId)
    {
        if (!StringUtils.hasText(requestNo) || userId == null)
        {
            throw new ServiceException("模拟支付请求号和当前用户不能为空");
        }

        BankTransaction transaction = transactionMapper.selectByRequestNo(requestNo);
        if (transaction == null || !"PAY".equals(transaction.getBusinessType())
                || transaction.getBusinessId() == null)
        {
            throw new ServiceException("模拟支付交易不存在");
        }
        if (!StringUtils.hasText(transaction.getBankSerialNo())
                || !transaction.getBankSerialNo().startsWith("MOCK-")
                || !StringUtils.hasText(transaction.getPayUrl())
                || !transaction.getPayUrl().startsWith("mock-bank://"))
        {
            throw new ServiceException("该交易不是模拟银行支付");
        }

        OrderInfo order = orderInfoMapper.selectOrderInfoByOrderId(transaction.getBusinessId());
        if (order == null || order.getElderId() == null)
        {
            throw new ServiceException("模拟支付关联订单不存在");
        }

        ElderFamily query = new ElderFamily();
        query.setUserId(userId);
        query.setElderId(order.getElderId());
        query.setStatus("0");
        List<ElderFamily> families = elderFamilyService.selectElderFamilyList(query);
        if (families == null || families.isEmpty())
        {
            throw new ServiceException("您没有权限完成该订单支付");
        }

        return completionService.completePayment(requestNo, transaction.getBankSerialNo(),
                "MOCK_SUCCESS", "模拟银行支付成功", userId.toString());
    }
}
