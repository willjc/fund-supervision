package com.ruoyi.mapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.HashMap;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

class BankMerchantConfigMapperXmlTest
{
    @Test
    void institutionAccountChangeShouldPreserveAgreementAndRequireReverification() throws Exception
    {
        Configuration configuration = new Configuration();
        String resource = "mapper/bank/BankMerchantConfigMapper.xml";
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource))
        {
            new XMLMapperBuilder(stream, configuration, resource, configuration.getSqlFragments()).parse();
        }

        String sql = configuration.getMappedStatement(
                "com.ruoyi.mapper.bank.BankMerchantConfigMapper.invalidateByInstitutionAccountChange")
                .getBoundSql(new HashMap<>()).getSql().toLowerCase().replaceAll("\\s+", " ");

        assertTrue(sql.contains("settlement_account_no = ?"));
        assertTrue(sql.contains("basic_account_no = ?"));
        assertTrue(sql.contains("verify_status = '0'"));
        assertTrue(sql.contains("status = '0'"));
        assertTrue(sql.contains("payout_enabled=0"));
        assertFalse(sql.contains("supervision_agreement_no"),
                "机构账户资料变更必须保留原协议号，后续通过704同步银行");
    }

    @Test
    void cancelContractShouldClearAgreementAndResetStatuses() throws Exception
    {
        Configuration configuration = new Configuration();
        String resource = "mapper/bank/BankMerchantConfigMapper.xml";
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource))
        {
            new XMLMapperBuilder(stream, configuration, resource, configuration.getSqlFragments()).parse();
        }

        String sql = configuration.getMappedStatement(
                "com.ruoyi.mapper.bank.BankMerchantConfigMapper.clearSupervisionContract")
                .getBoundSql(new HashMap<>()).getSql().toLowerCase().replaceAll("\\s+", " ");

        assertTrue(sql.contains("supervision_agreement_no = null"));
        assertTrue(sql.contains("verify_status = '0'"));
        assertTrue(sql.contains("status = '0'"));
        assertTrue(sql.contains("payout_enabled = 0"));
        assertTrue(sql.contains("where config_id = ?"));
    }
}
