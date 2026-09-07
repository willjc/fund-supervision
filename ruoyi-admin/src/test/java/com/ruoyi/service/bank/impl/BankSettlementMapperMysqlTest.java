package com.ruoyi.service.bank.impl;

import static org.junit.jupiter.api.Assertions.*;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.*;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.mapper.bank.BankTransactionMapper;
import com.ruoyi.mapper.bank.BankSettlementMapper;
import com.ruoyi.mapper.OrderInfoMapper;
import com.ruoyi.bank.gateway.BankGateway;
import com.ruoyi.bank.gateway.BankResult;
import com.ruoyi.domain.bank.BankMerchantConfig;
import com.ruoyi.service.bank.IBankMerchantConfigService;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@EnabledIfEnvironmentVariable(named="BANK_PAYOUT_TEST_PORT",matches="[0-9]{4,5}")
class BankSettlementMapperMysqlTest
{
    @Test
    void realMysqlChecksMigrationMappingsReservationConcurrencyRollbackAndIdempotency() throws Exception
    {
        String url="jdbc:mysql://127.0.0.1:"+System.getenv("BANK_PAYOUT_TEST_PORT")+"/bank_payout_test?useSSL=false&allowPublicKeyRetrieval=true";
        try(Connection c=DriverManager.getConnection(url,"root",""); Statement s=c.createStatement())
        {
            s.executeUpdate("INSERT INTO account_info(account_id,elder_id,institution_id,account_status,total_balance,service_balance,deposit_balance,member_balance,bank_service_balance) VALUES(1,1,1,'1',100,100,0,0,100),(2,2,1,'1',100,100,0,0,0)");
            s.executeUpdate("INSERT INTO fund_transfer(transfer_id,institution_id,elder_id,transfer_amount,transfer_date,status,is_paid,transfer_status) VALUES(1,1,1,10,CURDATE(),'pending','0','0')");
        }
        Configuration config=new Configuration(new Environment("isolated-test",new JdbcTransactionFactory(),
                new UnpooledDataSource("com.mysql.cj.jdbc.Driver",url,"root","")));
        config.getTypeAliasRegistry().registerAliases("com.ruoyi.domain.pension");
        config.getTypeAliasRegistry().registerAlias("OrderInfo", com.ruoyi.domain.OrderInfo.class);
        for(String resource:new String[]{"mapper/bank/BankTransactionMapper.xml","mapper/pension/FundTransferMapper.xml",
                "mapper/pension/DepositApplyMapper.xml","mapper/pension/FundTransferApplyMapper.xml","mapper/pension/AccountInfoMapper.xml",
                "mapper/OrderInfoMapper.xml"})
        {
            try(InputStream stream=getClass().getClassLoader().getResourceAsStream(resource))
            { new XMLMapperBuilder(stream,config,resource,config.getSqlFragments()).parse(); }
        }
        config.addMapper(BankSettlementMapper.class);
        config.getMappedStatementNames(); // 解析所有 resultMap 引用，不能只验证 Java 编译。
        SqlSessionFactory factory=new SqlSessionFactoryBuilder().build(config);
        ExecutorService workers=Executors.newFixedThreadPool(2);
        try
        {
            CountDownLatch start=new CountDownLatch(1);
            Callable<Integer> reserve=() -> { start.await();try(SqlSession session=factory.openSession(true))
                {return session.getMapper(BankSettlementMapper.class).reserve(1L,new BigDecimal("70.00"),BigDecimal.ZERO);} };
            Future<Integer> a=workers.submit(reserve), b=workers.submit(reserve);start.countDown();
            assertEquals(1,a.get(10,TimeUnit.SECONDS)+b.get(10,TimeUnit.SECONDS),"并发只能预占一次，不能透支");
        }
        finally { workers.shutdownNow(); }
        try(SqlSession session=factory.openSession(false))
        {
            BankSettlementMapper mapper=session.getMapper(BankSettlementMapper.class);
            assertEquals(0,mapper.reserve(2L,BigDecimal.ONE,BigDecimal.ZERO),"历史余额没有银行来源资格");
            assertEquals(1,mapper.settleBalance(1L,new BigDecimal("70.00"),BigDecimal.ZERO,new BigDecimal("70.00"),BigDecimal.ZERO));
            session.rollback();
            assertEquals(1,mapper.settleBalance(1L,new BigDecimal("70.00"),BigDecimal.ZERO,new BigDecimal("70.00"),BigDecimal.ZERO),"事务回滚应保留预占");
            session.commit();
            assertEquals(0,mapper.settleBalance(1L,new BigDecimal("70.00"),BigDecimal.ZERO,new BigDecimal("70.00"),BigDecimal.ZERO),"不能重复扣除");
            assertTrue(mapper.dueTransfers(0L,Long.MAX_VALUE).isEmpty(),"历史待拨付默认隔离");
            BankTransaction tx=new BankTransaction();tx.setRequestNo("TEST1");tx.setBusinessType("TRANSFER");tx.setBusinessId(1L);
            tx.setInstitutionId(1L);tx.setMerId("TEST");tx.setBankCode("ZZBANK");tx.setAmount(BigDecimal.TEN);
            tx.setStatus("PENDING");tx.setCreateTime(new Date());tx.setUpdateTime(new Date());tx.setNextQueryTime(new Date());
            BankTransactionMapper transactions=session.getMapper(BankTransactionMapper.class);
            assertEquals(1,transactions.insert(tx));session.commit();
            tx.setRequestNo("TEST2");tx.setAttemptNo(2);
            assertThrows(Exception.class,()->transactions.insert(tx),"同业务不允许并存两个未决尝试");session.rollback();
            BankTransaction saved=transactions.selectByRequestNo("TEST1");
            assertEquals(1,saved.getAttemptNo());assertEquals("PENDING",saved.getBookingStatus());
            assertEquals(1,mapper.claim(saved.getTransactionId()));assertEquals(0,mapper.claim(saved.getTransactionId()));
            saved.setBankStatus("SUCCESS");saved.setBankSerialNo("BANKTEST");saved.setBankTime("20260905120000");
            assertEquals(1,mapper.observe(saved));
            saved.setBankStatus("FAILED");assertEquals(0,mapper.observe(saved),"晚到响应不能覆盖已确认成功");
            session.commit();
            try(Statement s=session.getConnection().createStatement())
            {
                s.executeUpdate("UPDATE bank_transaction SET status='SUCCESS',booking_status='DONE',manual_review=1 WHERE request_no='TEST1'");
                s.executeUpdate("UPDATE fund_transfer SET bank_transaction_id="+saved.getTransactionId()+" WHERE transfer_id=1");
            }
            assertEquals(1,session.getMapper(com.ruoyi.mapper.pension.FundTransferMapper.class)
                    .selectFundTransferByTransferId(1L).getManualReview(),"主列表应返回当前银行交易的人工核查状态");
            assertEquals(1,mapper.recordReturn("TEST1","2026-09-05T12:01:00","synthetic return"));
            mapper.releaseClaim(saved.getTransactionId());
            session.commit();
            BankTransaction returned=transactions.selectByRequestNo("TEST1");
            assertEquals(0,returned.getManualReview());
            assertEquals("RETURN_PENDING",returned.getBookingStatus());
            assertEquals(1,mapper.dueTransactions().size(),"退汇即使原单进入人工核查仍可恢复");
            assertEquals(1,mapper.schedule(saved.getTransactionId(),new Date(),0));
        }
        verifyConcurrentPaymentRetry(factory);
    }

    private void verifyConcurrentPaymentRetry(SqlSessionFactory factory) throws Exception
    {
        try(SqlSession session=factory.openSession(true); Statement s=session.getConnection().createStatement())
        {
            s.executeUpdate("INSERT INTO order_info(order_id,institution_id,order_amount,paid_amount,order_status) VALUES(114,1,7500,0,'5')");
            BankTransaction failed=new BankTransaction();
            failed.setRequestNo("PAY-FAILED");failed.setBusinessType("PAY");failed.setBusinessId(114L);
            failed.setInstitutionId(1L);failed.setMerId("TEST");failed.setBankCode("ZZBANK");
            failed.setAmount(new BigDecimal("7500.00"));failed.setStatus("FAILED");
            failed.setCreateTime(new Date());failed.setUpdateTime(new Date());
            session.getMapper(BankTransactionMapper.class).insert(failed);
        }
        BankGateway gateway=mock(BankGateway.class);
        when(gateway.createPayment(any())).thenReturn(BankResult.pending(null,"https://bank.example/retry"));
        IBankMerchantConfigService merchants=mock(IBankMerchantConfigService.class);
        BankMerchantConfig merchant=new BankMerchantConfig();merchant.setMerId("TEST");merchant.setBankCode("ZZBANK");
        when(merchants.selectEnabledByInstitutionId(1L)).thenReturn(merchant);
        ExecutorService workers=Executors.newFixedThreadPool(2);
        try
        {
            CountDownLatch start=new CountDownLatch(1);
            Callable<String> retry=() -> {
                start.await();
                try(SqlSession session=factory.openSession(false))
                {
                    BankPaymentServiceImpl service=new BankPaymentServiceImpl();
                    ReflectionTestUtils.setField(service,"orderInfoMapper",session.getMapper(OrderInfoMapper.class));
                    ReflectionTestUtils.setField(service,"transactionMapper",session.getMapper(BankTransactionMapper.class));
                    ReflectionTestUtils.setField(service,"merchantConfigService",merchants);
                    ReflectionTestUtils.setField(service,"bankGateway",gateway);
                    BankResult result=service.createPayment(114L,1L,new BigDecimal("7500.00"),"wechat","测试订单");
                    session.commit();
                    return result.getRequestNo();
                }
            };
            Future<String> a=workers.submit(retry),b=workers.submit(retry);start.countDown();
            String requestNo=a.get(10,TimeUnit.SECONDS);
            assertEquals(requestNo,b.get(10,TimeUnit.SECONDS),"并发重试必须复用同一个新请求");
            verify(gateway,times(1)).createPayment(any());
            try(SqlSession session=factory.openSession(true))
            {
                BankTransactionMapper mapper=session.getMapper(BankTransactionMapper.class);
                BankTransaction latest=mapper.selectByBusiness("PAY",114L);
                assertEquals(2,latest.getAttemptNo());assertEquals(requestNo,latest.getRequestNo());
                assertEquals("PENDING",latest.getStatus());
                assertEquals("FAILED",mapper.selectByRequestNo("PAY-FAILED").getStatus(),"旧失败流水必须保留");
            }
        }
        finally { workers.shutdownNow(); }
    }
}
