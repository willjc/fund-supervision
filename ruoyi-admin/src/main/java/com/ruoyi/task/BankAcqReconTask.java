package com.ruoyi.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ruoyi.service.bank.impl.BankAcqReconService;

/** 收单对账日终任务：默认核对上一清算日（生产账单工作日 10 点左右生成）。 */
@Component("bankAcqReconTask")
public class BankAcqReconTask
{
    private static final Logger log = LoggerFactory.getLogger(BankAcqReconTask.class);

    @Autowired private BankAcqReconService reconService;

    @Value("${bank.integration.acq-recon-enabled:false}") private boolean enabled;
    @Value("${bank.integration.mer-id:8202106040000001}") private String merId;

    public void run()
    {
        if (!enabled) { return; }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Date yesterday = calendar.getTime();
        try
        {
            reconService.runRecon(merId, format.format(yesterday), null, "system");
        }
        catch (Exception e)
        {
            log.warn("收单对账未完成 clearingDate={}", format.format(yesterday));
        }
    }
}
