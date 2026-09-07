package com.ruoyi.service.bank.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.bank.gateway.BankGateway;
import com.ruoyi.bank.gateway.BankPaymentRequest;
import com.ruoyi.bank.gateway.BankQueryRequest;
import com.ruoyi.bank.gateway.BankResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.domain.OrderInfo;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.service.bank.IBankMerchantConfigService;
import com.ruoyi.service.bank.IBankPaymentService;

@Service
public class BankPaymentServiceImpl implements IBankPaymentService
{
    @Autowired
    private BankGateway bankGateway;

    @Autowired
    private IBankMerchantConfigService merchantConfigService;

    @Autowired
    private BankTransactionMapper transactionMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BankResult createPayment(Long orderId, Long institutionId, BigDecimal amount,
            String channelType, String subject)
    {
        if (orderId == null || institutionId == null || amount == null || amount.signum() <= 0)
        {
            throw new ServiceException("银行支付参数不完整");
        }

        // 同一订单的首次支付和失败重试串行执行，等待锁后重新读取订单和最新尝试。
        OrderInfo order = orderInfoMapper.selectOrderInfoByOrderIdForUpdate(orderId);
        if (order == null || (!"0".equals(order.getOrderStatus()) && !"5".equals(order.getOrderStatus()))
                || (order.getPaidAmount() != null && order.getPaidAmount().signum() > 0))
        {
            throw new ServiceException("订单不是未支付状态，不能发起银行支付");
        }
        if (!institutionId.equals(order.getInstitutionId()) || order.getOrderAmount() == null
                || amount.compareTo(order.getOrderAmount()) != 0)
        {
            throw new ServiceException("订单机构或金额已变更，请刷新后重试");
        }

        BankTransaction existing = transactionMapper.selectByBusiness("PAY", orderId);
        // 未决交易继续使用原请求；只有明确失败才能新建，旧流水保留供审计。
        if (existing != null && !"FAILED".equals(existing.getStatus()))
        {
            return toResult(existing);
        }

        BankMerchantConfig merchant = merchantConfigService.selectEnabledByInstitutionId(institutionId);
        if (merchant == null)
        {
            throw new ServiceException("该养老机构没有已验证、已启用的默认银行商户号");
        }

        String requestNo = "BP" + IdUtils.fastSimpleUUID().substring(0, 30).toUpperCase();
        Date now = new Date();
        BankTransaction transaction = new BankTransaction();
        transaction.setRequestNo(requestNo);
        transaction.setBusinessType("PAY");
        transaction.setBusinessId(orderId);
        transaction.setAttemptNo(existing == null ? 1 : existing.getAttemptNo() + 1);
        transaction.setInstitutionId(institutionId);
        transaction.setMerId(merchant.getMerId());
        transaction.setBankCode(merchant.getBankCode());
        transaction.setChannelType(channelType);
        transaction.setAmount(amount);
        transaction.setStatus("PENDING");
        transaction.setEnvironment(merchant.getEnvironment());
        transaction.setSnapshotJson(com.alibaba.fastjson2.JSON.toJSONString(merchant));
        transaction.setNextQueryTime(new Date(now.getTime() + 60000L));
        transaction.setCreateTime(now);
        transaction.setUpdateTime(now);
        if (transactionMapper.insert(transaction) != 1)
        {
            throw new ServiceException("创建银行交易记录失败");
        }

        BankPaymentRequest request = new BankPaymentRequest();
        request.setRequestNo(requestNo);
        request.setBusinessId(orderId);
        request.setInstitutionId(institutionId);
        request.setMerId(merchant.getMerId());
        request.setAmount(amount);
        request.setChannelType(channelType);
        request.setSubject(subject);
        request.setRequestTime(now);

        BankResult result;
        try
        {
            result = bankGateway.createPayment(request);
        }
        catch (ServiceException e)
        {
            // 配置未启用或真实网关尚未验收时回滚本地 PENDING 记录，避免占用订单幂等键。
            throw e;
        }
        catch (Exception e)
        {
            result = BankResult.unknown("GATEWAY_EXCEPTION", "支付结果未知，请查询原交易");
        }
        if (result == null || result.getStatus() == null)
        {
            result = BankResult.unknown("EMPTY_RESPONSE", "银行网关返回为空");
        }

        result.setRequestNo(requestNo);
        // 同步成功也必须走统一结算服务。此处仍保留 PENDING，避免先把银行交易
        // 标成成功、后续订单入账却失败而形成不一致状态。
        transaction.setStatus("SUCCESS".equals(result.getStatus()) ? "PENDING" : result.getStatus());
        transaction.setBankSerialNo(result.getBankSerialNo());
        transaction.setPayUrl(result.getPayUrl());
        transaction.setResponseCode(result.getResponseCode());
        transaction.setResponseMessage(result.getResponseMessage());
        transaction.setUpdateTime(new Date());
        if ("FAILED".equals(result.getStatus()))
        {
            transaction.setCompleteTime(new Date());
        }
        if (transactionMapper.updateResult(transaction) != 1)
        {
            throw new ServiceException("更新银行交易结果失败");
        }
        return result;
    }

    @Override
    public BankResult queryPayment(Long orderId)
    {
        BankTransaction transaction = transactionMapper.selectByBusiness("PAY", orderId);
        if (transaction == null)
        {
            throw new ServiceException("该订单没有银行支付记录");
        }
        if (!"PENDING".equals(transaction.getStatus()) && !"UNKNOWN".equals(transaction.getStatus()))
        {
            return toResult(transaction);
        }

        BankQueryRequest request = new BankQueryRequest();
        request.setMerId(transaction.getMerId());
        request.setOriginalRequestNo(transaction.getRequestNo());
        request.setOriginalRequestTime(transaction.getCreateTime());
        request.setBankSerialNo(transaction.getBankSerialNo());
        BankResult result = bankGateway.queryPayment(request);
        if (result == null || result.getStatus() == null)
        {
            throw new ServiceException("银行查单返回为空");
        }
        if ("SUCCESS".equals(result.getStatus())
                && (result.getPaidAmount() == null || result.getPaidAmount().compareTo(transaction.getAmount()) != 0))
        {
            throw new ServiceException("银行支付金额与订单金额不一致，禁止入账");
        }
        result.setRequestNo(transaction.getRequestNo());
        return result;
    }

    private BankResult toResult(BankTransaction transaction)
    {
        BankResult result = new BankResult();
        result.setRequestNo(transaction.getRequestNo());
        result.setStatus(transaction.getStatus());
        result.setBankSerialNo(transaction.getBankSerialNo());
        result.setPayUrl(transaction.getPayUrl());
        result.setResponseCode(transaction.getResponseCode());
        result.setResponseMessage(transaction.getResponseMessage());
        result.setPaidAmount(transaction.getAmount());
        result.setBankTransactionTime(transaction.getBankTime());
        return result;
    }
}
