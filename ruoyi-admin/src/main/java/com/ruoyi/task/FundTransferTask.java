package com.ruoyi.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.service.pension.IFundTransferService;
import com.ruoyi.service.pension.ITransferRuleConfigService;
import com.ruoyi.domain.pension.TransferRuleConfig;
import java.util.Map;
import java.util.Calendar;
import java.util.List;

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

    @Autowired
    private ITransferRuleConfigService transferRuleConfigService;

    /**
     * 每天执行自动划拨（根据划付规则配置判断是否执行）
     * 每天早上9点执行一次，检查当前日期是否匹配规则配置
     */
    public void executeAutoTransfer()
    {
        log.info("========== 开始执行资金自动划拨任务 ==========");

        try {
            // 获取当前日期时间
            Calendar now = Calendar.getInstance();
            int currentDay = now.get(Calendar.DAY_OF_MONTH);
            int currentHour = now.get(Calendar.HOUR_OF_DAY);
            int currentMinute = now.get(Calendar.MINUTE);

            // 获取当前账单月份
            String billingMonth = String.format("%tY-%tm", now, now);

            log.info("当前时间: {}月{}日 {:02d}:{:02d}, 账单月份: {}",
                now.get(Calendar.MONTH) + 1, currentDay, currentHour, currentMinute, billingMonth);

            // 查询有效的划付规则配置（目前只考虑按月划付）
            TransferRuleConfig query = new TransferRuleConfig();
            query.setStatus("0"); // 0-正常
            query.setTransferCycle("monthly"); // 只考虑按月划付
            List<TransferRuleConfig> rules = transferRuleConfigService.selectTransferRuleConfigList(query);

            if (rules == null || rules.isEmpty()) {
                log.info("未找到有效的划付规则配置，跳过本次执行");
                return;
            }

            TransferRuleConfig rule = rules.get(0);
            Integer ruleDay = rule.getTransferDay();
            String ruleTime = rule.getTransferTime();

            log.info("划付规则配置: 每月{}日 {}", ruleDay, ruleTime);

            // 解析规则时间
            String[] timeParts = ruleTime.split(":");
            int ruleHour = Integer.parseInt(timeParts[0]);
            int ruleMinute = timeParts.length > 1 ? Integer.parseInt(timeParts[1]) : 0;

            // 判断当前日期和时间是否匹配规则
            if (currentDay != ruleDay) {
                log.info("当前日期({}号)与规则配置({}号)不匹配，跳过本次执行", currentDay, ruleDay);
                return;
            }

            // 判断当前时间是否在规则时间前后30分钟内（允许有一定的执行窗口）
            int currentMinutes = currentHour * 60 + currentMinute;
            int ruleMinutes = ruleHour * 60 + ruleMinute;
            int timeDiff = Math.abs(currentMinutes - ruleMinutes);

            if (timeDiff > 30) {
                log.info("当前时间({:02d}:{:02d})与规则配置时间({}:{})相差较大，跳过本次执行",
                    currentHour, currentMinute, ruleHour, String.format("%02d", ruleMinute));
                return;
            }

            // 执行划拨
            log.info("当前时间匹配划付规则，开始执行划拨...");
            Map<String, Object> result = fundTransferService.executeTransferByRule(billingMonth, ruleDay, ruleTime);

            if ((Boolean) result.get("success")) {
                Integer totalCount = (Integer) result.get("totalCount");
                Integer successCount = (Integer) result.get("successCount");
                Integer failCount = (Integer) result.get("failCount");

                log.info("资金自动划拨任务执行成功: {}", result.get("message"));
                log.info("划拨统计: 总单数={}, 成功={}, 失败={}", totalCount, successCount, failCount);
            } else {
                log.error("资金自动划拨任务执行失败: {}", result.get("message"));
            }

        } catch (Exception e) {
            log.error("资金自动划拨任务执行异常", e);
        }

        log.info("========== 资金自动划拨任务执行结束 ==========");
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