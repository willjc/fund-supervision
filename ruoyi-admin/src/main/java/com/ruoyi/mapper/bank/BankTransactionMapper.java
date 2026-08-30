package com.ruoyi.mapper.bank;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.domain.bank.BankTransaction;

public interface BankTransactionMapper
{
    BankTransaction selectByBusiness(@Param("businessType") String businessType,
                                     @Param("businessId") Long businessId);

    BankTransaction selectByRequestNo(String requestNo);

    int insert(BankTransaction transaction);

    int updateResult(BankTransaction transaction);
}
