package com.ruoyi.mapper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import com.ruoyi.domain.PensionInstitution;

class PensionInstitutionMapperXmlTest
{
    @Test
    void accountLockQueryMustReturnAllAccountColumns() throws Exception
    {
        Configuration configuration = new Configuration();
        configuration.getTypeAliasRegistry().registerAlias("PensionInstitution", PensionInstitution.class);
        String resource = "mapper/PensionInstitutionMapper.xml";
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(resource))
        {
            new XMLMapperBuilder(stream, configuration, resource, configuration.getSqlFragments()).parse();
        }

        String sql = configuration.getMappedStatement(
                "com.ruoyi.mapper.PensionInstitutionMapper.selectPensionInstitutionForUpdate")
                .getBoundSql(1L).getSql().toLowerCase();

        assertTrue(sql.contains("supervise_account"), "锁行查询必须返回监管账户，避免误判账户变更");
        assertTrue(sql.contains("supervise_bank"), "锁行查询必须返回监管开户行，避免误判账户变更");
        assertTrue(sql.contains("bank_account"), "锁行查询必须返回基本账户，避免误判账户变更");
        assertTrue(sql.contains("basic_bank"), "锁行查询必须返回基本开户行，避免误判账户变更");
        assertTrue(sql.contains("for update"), "锁行语义必须保留");
    }
}
