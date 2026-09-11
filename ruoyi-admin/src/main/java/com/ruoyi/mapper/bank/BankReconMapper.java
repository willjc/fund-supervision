package com.ruoyi.mapper.bank;

import java.util.List;
import org.apache.ibatis.annotations.*;
import com.ruoyi.domain.bank.BankReconDiff;
import com.ruoyi.domain.bank.BankReconRun;
import com.ruoyi.domain.bank.BankTransaction;

/** 收单对账持久化。查询比对所需的本地交易也在此。 */
public interface BankReconMapper
{
    @Insert("INSERT INTO bank_recon_run (mer_id, clearing_date, status, file_name, bill_stat, total_rows, matched_rows, diff_rows, resp_code, resp_message, download_md5, create_by, create_time) VALUES (#{merId}, #{clearingDate}, #{status}, #{fileName}, #{billStat}, #{totalRows}, #{matchedRows}, #{diffRows}, #{respCode}, #{respMessage}, #{downloadMd5}, #{createBy}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "runId")
    int insertRun(BankReconRun run);

    @Update("UPDATE bank_recon_run SET status=#{status}, file_name=#{fileName}, bill_stat=#{billStat}, total_rows=#{totalRows}, matched_rows=#{matchedRows}, diff_rows=#{diffRows}, resp_code=#{respCode}, resp_message=#{respMessage}, download_md5=#{downloadMd5}, update_time=NOW() WHERE run_id=#{runId}")
    int updateRun(BankReconRun run);

    @Select("SELECT * FROM bank_recon_run WHERE run_id=#{id}")
    @Results(id = "BankReconRunResult", value = {
            @Result(property = "runId", column = "run_id"),
            @Result(property = "merId", column = "mer_id"),
            @Result(property = "clearingDate", column = "clearing_date"),
            @Result(property = "status", column = "status"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "billStat", column = "bill_stat"),
            @Result(property = "totalRows", column = "total_rows"),
            @Result(property = "matchedRows", column = "matched_rows"),
            @Result(property = "diffRows", column = "diff_rows"),
            @Result(property = "respCode", column = "resp_code"),
            @Result(property = "respMessage", column = "resp_message"),
            @Result(property = "downloadMd5", column = "download_md5"),
            @Result(property = "createBy", column = "create_by"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")
    })
    BankReconRun selectRun(Long id);

    @Select("SELECT * FROM bank_recon_run ORDER BY run_id DESC LIMIT #{limit}")
    @ResultMap("com.ruoyi.mapper.bank.BankReconMapper.BankReconRunResult")
    List<BankReconRun> selectRuns(int limit);

    @Insert("INSERT INTO bank_recon_diff (run_id, diff_type, request_no, bank_serial_no, bank_amount, bank_detail, local_amount, local_serial_no, local_status, handled) VALUES (#{runId}, #{diffType}, #{requestNo}, #{bankSerialNo}, #{bankAmount}, #{bankDetail}, #{localAmount}, #{localSerialNo}, #{localStatus}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "diffId")
    int insertDiff(BankReconDiff diff);

    @Select("SELECT * FROM bank_recon_diff WHERE run_id=#{runId} ORDER BY diff_id")
    @Results(id = "BankReconDiffResult", value = {
            @Result(property = "diffId", column = "diff_id"),
            @Result(property = "runId", column = "run_id"),
            @Result(property = "diffType", column = "diff_type"),
            @Result(property = "requestNo", column = "request_no"),
            @Result(property = "bankSerialNo", column = "bank_serial_no"),
            @Result(property = "bankAmount", column = "bank_amount"),
            @Result(property = "bankDetail", column = "bank_detail"),
            @Result(property = "localAmount", column = "local_amount"),
            @Result(property = "localSerialNo", column = "local_serial_no"),
            @Result(property = "localStatus", column = "local_status"),
            @Result(property = "handled", column = "handled"),
            @Result(property = "handleRemark", column = "handle_remark"),
            @Result(property = "createTime", column = "create_time")
    })
    List<BankReconDiff> selectDiffs(Long runId);

    /** 本地已确认成功的收单交易（支付+退款），比对数据源。 */
    @Select("SELECT transaction_id, request_no, business_type, amount, bank_serial_no, bank_time, status FROM bank_transaction WHERE business_type IN ('PAY','REFUND') AND status='SUCCESS' AND mer_id=#{merId} AND bank_time IS NOT NULL AND bank_serial_no IS NOT NULL")
    @ResultMap("com.ruoyi.mapper.bank.BankTransactionMapper.BankTransactionResult")
    List<BankTransaction> selectSuccessTransactions(String merId);
}
