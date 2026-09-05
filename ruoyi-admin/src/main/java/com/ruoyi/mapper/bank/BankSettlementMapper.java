package com.ruoyi.mapper.bank;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.domain.bank.BankTransaction;
import com.ruoyi.domain.pension.FundTransfer;

/** V2 资金条件更新。由服务事务持有机构、业务单、账户锁后调用。 */
public interface BankSettlementMapper
{
    @Select("SELECT * FROM deposit_apply WHERE apply_id=#{id} FOR UPDATE")
    @ResultMap("com.ruoyi.mapper.pension.DepositApplyMapper.DepositApplyResult")
    com.ruoyi.domain.pension.DepositApply lockDeposit(Long id);

    @Select("SELECT * FROM fund_transfer_apply WHERE apply_id=#{id} FOR UPDATE")
    @ResultMap("com.ruoyi.mapper.pension.FundTransferApplyMapper.FundTransferApplyResult")
    com.ruoyi.domain.pension.FundTransferApply lockApply(Long id);

    @Update("UPDATE fund_transfer SET approve_user=#{operator},approve_time=NOW(),status=#{status}, "
          + "failure_reason=#{reason} WHERE transfer_id=#{id} AND status='pending' AND bank_transaction_id IS NULL")
    int approveTransfer(@Param("id") Long id,@Param("operator") String operator,@Param("status") String status,@Param("reason") String reason);

    @Update("UPDATE fund_transfer SET apply_id=#{apply},paid_method='manual',transfer_date=CURDATE(),approve_time=NOW(), "
          + "approve_user=#{operator} WHERE transfer_id=#{id} AND status='pending' AND bank_eligible=1 "
          + "AND bank_transaction_id IS NULL AND (apply_id IS NULL OR apply_id=#{apply})")
    int linkApply(@Param("id") Long id,@Param("apply") Long apply,@Param("operator") String operator);
    @Select("SELECT * FROM bank_transaction WHERE next_query_time <= NOW() AND manual_review=0 "
          + "AND (status IN ('PENDING','UNKNOWN') OR booking_status='RETURN_PENDING') "
          + "AND (lease_until IS NULL OR lease_until<NOW()) ORDER BY next_query_time LIMIT 100")
    @ResultMap("com.ruoyi.mapper.bank.BankTransactionMapper.BankTransactionResult")
    List<BankTransaction> dueTransactions();

    @Update("UPDATE bank_transaction SET lease_until=DATE_ADD(NOW(), INTERVAL 10 MINUTE) "
          + "WHERE transaction_id=#{id} AND (lease_until IS NULL OR lease_until<NOW())")
    int claim(Long id);

    @Update("UPDATE bank_transaction SET lease_until=NULL, query_count=query_count+1, "
          + "next_query_time=#{next}, manual_review=#{review} WHERE transaction_id=#{id} "
          + "AND (status IN ('PENDING','UNKNOWN','RETURNED') OR booking_status='RETURN_PENDING') "
          + "AND booking_status NOT IN ('DONE','REVERSED')")
    int schedule(@Param("id") Long id, @Param("next") Date next, @Param("review") int review);

    @Update("UPDATE bank_transaction SET lease_until=NULL WHERE transaction_id=#{id}")
    int releaseClaim(Long id);

    @Update("UPDATE bank_transaction SET bank_status=#{bankStatus}, bank_serial_no=#{bankSerialNo}, "
          + "bank_time=#{bankTime}, response_code=#{responseCode}, response_message=#{responseMessage}, update_time=NOW() "
          + "WHERE transaction_id=#{transactionId} AND status IN ('PENDING','UNKNOWN') "
          + "AND (bank_status IS NULL OR bank_status NOT IN ('SUCCESS','FAILED')) AND return_time IS NULL")
    int observe(BankTransaction transaction);

    @Update("UPDATE bank_transaction SET status=#{status}, booking_status=#{bookingStatus}, "
          + "complete_time=NOW(), next_query_time=NULL, manual_review=0, lease_until=NULL WHERE transaction_id=#{transactionId}")
    int finish(BankTransaction transaction);

    @Update("UPDATE bank_transaction SET return_time=#{time},return_reason=#{reason},bank_status='RETURNED', "
          + "next_query_time=NOW(),manual_review=0,booking_status=CASE WHEN booking_status='REVERSED' THEN 'REVERSED' ELSE 'RETURN_PENDING' END "
          + "WHERE request_no=#{request} AND business_type='TRANSFER' AND return_time IS NULL")
    int recordReturn(@Param("request") String request, @Param("time") String time, @Param("reason") String reason);

    @Select("SELECT * FROM fund_transfer WHERE transfer_id=#{id} FOR UPDATE")
    @ResultMap("com.ruoyi.mapper.pension.FundTransferMapper.FundTransferResult")
    FundTransfer lockTransfer(Long id);

    @Select("SELECT * FROM fund_transfer WHERE source_key=#{key}")
    @ResultMap("com.ruoyi.mapper.pension.FundTransferMapper.FundTransferResult")
    FundTransfer bySource(String key);

    @Select("SELECT * FROM fund_transfer WHERE bank_eligible=1 AND status='pending' AND is_paid='0' "
          + "AND bank_transaction_id IS NULL AND transfer_date<=CURDATE() "
          + "AND transfer_id>#{after} AND transfer_id<=#{last} ORDER BY transfer_id LIMIT 100")
    @ResultMap("com.ruoyi.mapper.pension.FundTransferMapper.FundTransferResult")
    List<FundTransfer> dueTransfers(@Param("after") Long after, @Param("last") Long last);

    @Select("SELECT COALESCE(MAX(transfer_id),0) FROM fund_transfer")
    Long lastTransferId();

    @Select("SELECT COUNT(*) FROM fund_transfer WHERE apply_id=#{id} AND balance_type=#{type}")
    int linkedApplication(@Param("id") Long id,@Param("type") String type);

    @Update("UPDATE fund_transfer SET bank_transaction_id=#{tx},status='processing',failure_reason=NULL,"
          + "execute_user=#{operator},execute_time=NOW() WHERE transfer_id=#{id} AND is_paid='0' "
          + "AND bank_eligible=1 AND status IN ('pending','failed')")
    int attach(@Param("id") Long id,@Param("tx") Long tx,@Param("operator") String operator);

    @Update("UPDATE fund_transfer SET status=#{status},is_paid=#{paid},transfer_status=#{legacy}, "
          + "bank_order_no=#{serial},failure_reason=#{reason},paid_time=CASE WHEN #{paid}='1' THEN NOW() ELSE paid_time END,"
          + "update_time=NOW() WHERE transfer_id=#{id}")
    int transferResult(@Param("id") Long id,@Param("status") String status,@Param("paid") String paid,
            @Param("legacy") String legacy,@Param("serial") String serial,@Param("reason") String reason);

    @Update("UPDATE fund_transfer SET bank_eligible=1 WHERE order_id=#{order} AND source_key IS NOT NULL "
          + "AND status='pending' AND is_paid='0'")
    int qualifyOrder(Long order);

    @Update("UPDATE account_info SET bank_service_balance=bank_service_balance+#{service}, "
          + "bank_deposit_balance=bank_deposit_balance+#{deposit} WHERE account_id=#{id}")
    int creditSource(@Param("id") Long id,@Param("service") BigDecimal service,@Param("deposit") BigDecimal deposit);

    @Update("UPDATE account_info SET service_reserved=service_reserved+#{service},deposit_reserved=deposit_reserved+#{deposit} "
          + "WHERE account_id=#{id} AND account_status='1' AND service_balance-service_reserved>=#{service} "
          + "AND deposit_balance-deposit_reserved>=#{deposit} AND bank_service_balance-service_reserved>=#{service} "
          + "AND bank_deposit_balance-deposit_reserved>=#{deposit} AND total_balance-service_reserved-deposit_reserved>=#{service}+#{deposit}")
    int reserve(@Param("id") Long id,@Param("service") BigDecimal service,@Param("deposit") BigDecimal deposit);

    @Update("UPDATE account_info SET service_reserved=service_reserved-#{service},deposit_reserved=deposit_reserved-#{deposit}, "
          + "service_balance=service_balance-#{debitService},deposit_balance=deposit_balance-#{debitDeposit}, "
          + "bank_service_balance=bank_service_balance-#{debitService},bank_deposit_balance=bank_deposit_balance-#{debitDeposit}, "
          + "total_balance=total_balance-#{debitService}-#{debitDeposit},update_time=NOW() WHERE account_id=#{id} "
          + "AND service_reserved>=#{service} AND deposit_reserved>=#{deposit} "
          + "AND service_balance>=#{debitService} AND deposit_balance>=#{debitDeposit} "
          + "AND bank_service_balance>=#{debitService} AND bank_deposit_balance>=#{debitDeposit} "
          + "AND total_balance>=#{debitService}+#{debitDeposit}")
    int settleBalance(@Param("id") Long id,@Param("service") BigDecimal service,@Param("deposit") BigDecimal deposit,
            @Param("debitService") BigDecimal debitService,@Param("debitDeposit") BigDecimal debitDeposit);

    @Update("UPDATE account_info SET service_balance=service_balance+#{service},deposit_balance=deposit_balance+#{deposit}, "
          + "bank_service_balance=bank_service_balance+#{service},bank_deposit_balance=bank_deposit_balance+#{deposit}, "
          + "total_balance=total_balance+#{service}+#{deposit},update_time=NOW() WHERE account_id=#{id}")
    int reverseBalance(@Param("id") Long id,@Param("service") BigDecimal service,@Param("deposit") BigDecimal deposit);

    @Select("SELECT COUNT(*) FROM sys_user_institution WHERE user_id=#{user} AND institution_id=#{institution}")
    int hasScope(@Param("user") Long user,@Param("institution") Long institution);

    @Select("SELECT COUNT(*) FROM account_info WHERE institution_id=#{institution} "
          + "AND (bank_service_balance>0 OR bank_deposit_balance>0 OR service_reserved>0 OR deposit_reserved>0)")
    int hasBankFunds(Long institution);

    @Select("SELECT COUNT(*) FROM bank_transaction WHERE institution_id=#{institution} AND status IN ('PENDING','UNKNOWN')")
    int hasUnresolved(Long institution);

    @Update("UPDATE fund_transfer_apply a SET a.apply_status=CASE "
          + "WHEN NOT EXISTS(SELECT 1 FROM fund_transfer_apply_detail d JOIN fund_transfer f ON f.transfer_id=d.transfer_id "
          + "WHERE d.apply_id=a.apply_id AND f.status<>'completed') THEN 'completed' ELSE 'approved' END, "
          + "a.actual_amount=(SELECT COALESCE(SUM(d.transfer_amount),0) FROM fund_transfer_apply_detail d "
          + "JOIN fund_transfer f ON f.transfer_id=d.transfer_id WHERE d.apply_id=a.apply_id AND f.status='completed'), "
          + "a.update_time=NOW() WHERE a.apply_id=#{id}")
    int refreshApply(Long id);

    @Update("UPDATE deposit_apply SET apply_status=#{status},actual_amount=#{amount},update_time=NOW() WHERE apply_id=#{id}")
    int depositResult(@Param("id") Long id,@Param("status") String status,@Param("amount") BigDecimal amount);
}
