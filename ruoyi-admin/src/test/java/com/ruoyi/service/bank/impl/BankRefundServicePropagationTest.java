package com.ruoyi.service.bank.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.bank.gateway.BankGateway;
import com.ruoyi.bank.gateway.BankRefundRequest;
import com.ruoyi.bank.gateway.BankResult;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.mapper.bank.BankSettlementMapper;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.service.bank.impl.BankRefundService.BankRefundTxWorker;

/**
 * 审批入口 RefundRecordServiceImpl.approveRefund 带 @Transactional，会在外层事务内调用 approveAndSubmit。
 * 若 approveAndSubmit 沿用外层事务，claim/observe 会在该事务里锁住 bank_transaction 行，
 * 随后 book/fail 的 REQUIRES_NEW 对同一行 select...for update 与挂起的外层事务自锁，触发 Lock wait timeout。
 * 本测试用真实 Spring 事务拦截器验证：银行 I/O 必须运行在外层审批事务之外。
 */
class BankRefundServicePropagationTest
{
    private BankTransaction refundTx()
    {
        BankTransaction tx = new BankTransaction();
        tx.setTransactionId(7L);
        tx.setRequestNo("BR123456789012345678901234567890");
        tx.setBusinessType("REFUND");
        tx.setBusinessId(5L);
        tx.setInstitutionId(32L);
        tx.setMerId("8202106040000001");
        tx.setAmount(new BigDecimal("0.05"));
        tx.setStatus("PENDING");
        tx.setCreateTime(new Date());
        BankRefundRequest snapshot = new BankRefundRequest();
        snapshot.setRequestNo("BR123456789012345678901234567890");
        snapshot.setRequestTime(new Date());
        snapshot.setMerId("8202106040000001");
        snapshot.setAmount(new BigDecimal("0.05"));
        snapshot.setOriginalRequestNo("BP1A746389E4594FA49A896413E726E7");
        snapshot.setOriginalBankSerialNo("20026091017164457253353180271553");
        snapshot.setOriginalBankTime("20260910171644");
        tx.setSnapshotJson(JSON.toJSONString(snapshot));
        return tx;
    }

    @Test
    void approveAndSubmitMustRunBankIoOutsideOuterApprovalTransaction() throws Exception
    {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        when(dataSource.getConnection()).thenReturn(connection);
        DataSourceTransactionManager txManager = new DataSourceTransactionManager(dataSource);

        BankRefundService target = new BankRefundService();
        BankGateway gateway = mock(BankGateway.class);
        BankTransactionMapper transactions = mock(BankTransactionMapper.class);
        BankSettlementMapper settlement = mock(BankSettlementMapper.class);
        OrderInfoMapper orders = mock(OrderInfoMapper.class);
        BankRefundTxWorker worker = mock(BankRefundTxWorker.class);
        ReflectionTestUtils.setField(target, "gateway", gateway);
        ReflectionTestUtils.setField(target, "transactions", transactions);
        ReflectionTestUtils.setField(target, "settlement", settlement);
        ReflectionTestUtils.setField(target, "orders", orders);
        ReflectionTestUtils.setField(target, "worker", worker);

        when(worker.prepare(5L, "admin", null)).thenReturn(refundTx());

        AtomicBoolean bankIoInsideTransaction = new AtomicBoolean(true);
        when(gateway.refundPayment(any())).thenAnswer(invocation -> {
            bankIoInsideTransaction.set(TransactionSynchronizationManager.isActualTransactionActive());
            return BankResult.pending("BANKR1", null);
        });

        // reconcile 立即命中终态返回，避免依赖完整查询编排；本测试只关心传播边界。
        BankTransaction done = refundTx();
        done.setBookingStatus("DONE");
        when(transactions.selectByRequestNo("BR123456789012345678901234567890")).thenReturn(done);

        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionManager(txManager);
        interceptor.setTransactionAttributeSource(new AnnotationTransactionAttributeSource());
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.addAdvice(interceptor);
        BankRefundService proxied = (BankRefundService) proxyFactory.getProxy();

        // 模拟 approveRefund 的外层 @Transactional 事务。
        new TransactionTemplate(txManager).execute(status -> {
            proxied.approveAndSubmit(5L, "admin", null);
            return null;
        });

        assertFalse(bankIoInsideTransaction.get(),
                "银行退款 I/O 必须在外层审批事务之外执行，否则 claim/observe 持锁与 book/fail 的 REQUIRES_NEW 自锁导致 Lock wait timeout");
    }
}