package com.ruoyi.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ruoyi.domain.PensionInstitution;
import com.ruoyi.mapper.PensionInstitutionMapper;
import com.ruoyi.mapper.bank.BankMerchantConfigMapper;

@ExtendWith(MockitoExtension.class)
class PensionInstitutionServiceImplTest
{
    @Mock
    private PensionInstitutionMapper institutionMapper;

    @Mock
    private BankMerchantConfigMapper merchantConfigMapper;

    @InjectMocks
    private PensionInstitutionServiceImpl service;

    @Test
    void accountChangeShouldDisableMerchantBindingForReverification()
    {
        PensionInstitution existing = institution(36L, "OLD-SUP", "OLD-BASIC");
        PensionInstitution update = institution(36L, "NEW-SUP", "NEW-BASIC");
        update.setSuperviseBank("郑州银行测试支行");
        update.setBasicBank("郑州银行测试支行");
        update.setUpdateBy("admin");
        existing.setSuperviseBank("郑州银行原支行");
        existing.setBasicBank("郑州银行原支行");

        when(institutionMapper.selectPensionInstitutionByInstitutionId(36L)).thenReturn(existing);
        when(institutionMapper.updatePensionInstitution(update)).thenReturn(1);

        assertEquals(1, service.updatePensionInstitution(update));
        verify(merchantConfigMapper).invalidateByInstitutionAccountChange(
                36L, "NEW-SUP", "NEW-BASIC", "admin");
    }

    private PensionInstitution institution(Long id, String supervisionAccount, String basicAccount)
    {
        PensionInstitution institution = new PensionInstitution();
        institution.setInstitutionId(id);
        institution.setSuperviseAccount(supervisionAccount);
        institution.setBankAccount(basicAccount);
        return institution;
    }
}
