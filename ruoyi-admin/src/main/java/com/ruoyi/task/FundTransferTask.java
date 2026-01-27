package com.ruoyi.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.service.pension.IFundTransferService;
import java.util.Map;

/**
 * 资金划拨定时任务
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@Component("fundTransferTask")
public class FundTransferTask
{
    private static final Logger log = LoggerFactory.getLogger(FundTransferTask.class);

    @Autowired
    private IFundTransferService fundTransferService;

    /**
     * 每月1日凌晨2点执行自动划拨
     */
    public void executeAutoTransfer()
    {
        log.info("开始执行资金自动划拨任务");

        try {
            Map<String, Object> result = fundTransferService.executeAutoTransfer();

            if ((Boolean) result.get("success")) {
                log.info("资金自动划拨任务执行成功: {}", result.get("message"));
                if (result.containsKey("transferAmount")) {
                    log.info("划拨金额: {} 元, 涉及老人: {} 人", result.get("transferAmount"), result.get("elderCount"));
                }
            } else {
                log.error("资金自动划拨任务执行失败: {}", result.get("message"));
            }
        } catch (Exception e) {
            log.error("资金自动划拨任务执行异常", e);
        }

        log.info("资金自动划拨任务执行结束");
    }

    /**
     * 每天凌晨3点检查余额不足的账户
     */
    public void checkLowBalanceAccounts()
    {
        log.info("开始检查余额不足的账户");

        try {
            // 检查余额不足2个月的账户
            java.util.List<com.ruoyi.domain.pension.AccountInfo> lowBalanceAccounts =
                fundTransferService.selectLowBalanceAccounts(2);

            if (!lowBalanceAccounts.isEmpty()) {
                log.warn("发现 {} 个余额不足的账户", lowBalanceAccounts.size());

                for (com.ruoyi.domain.pension.AccountInfo account : lowBalanceAccounts) {
                    log.warn("账户 {} (老人: {}) 余额不足，服务费余额: {} 元",
                            account.getAccountNo(),
                            account.getElderName(),
                            account.getServiceBalance());
                }

                // 这里可以添加发送通知的逻辑
                // 例如：发送短信、邮件或站内消息给管理员和家属
            } else {
                log.info("所有账户余额充足");
            }
        } catch (Exception e) {
            log.error("检查余额不足账户时发生异常", e);
        }

        log.info("检查余额不足的账户结束");
    }
}