package com.ruoyi.mapper.bank;

import org.apache.ibatis.annotations.Param;

import java.util.Date;

import com.ruoyi.domain.bank.BankTransaction;

public interface BankTransactionMapper
{
    BankTransaction selectByBusiness(@Param("businessType") String businessType,
                                     @Param("businessId") Long businessId);

    BankTransaction selectByRequestNo(String requestNo);

    BankTransaction selectByRequestNoForUpdate(String requestNo);

    int insert(BankTransaction transaction);

    int updateResult(BankTransaction transaction);

    int markSuccess(@Param("transactionId") Long transactionId,
                    @Param("bankSerialNo") String bankSerialNo,
                    @Param("responseCode") String responseCode,
                    @Param("responseMessage") String responseMessage,
                    @Param("completeTime") Date completeTime,
                    @Param("updateTime") Date updateTime);
}
