package com.ruoyi.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.mapper.pension.BalanceWarningMapper;
import com.ruoyi.domain.pension.BalanceWarning;
import com.ruoyi.service.pension.IBalanceWarningService;

/**
 * 余额预警Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-29
 */
@Service
public class BalanceWarningServiceImpl implements IBalanceWarningService
{
    @Autowired
    private BalanceWarningMapper balanceWarningMapper;

    /**
     * 查询余额预警
     *
     * @param warningId 余额预警主键
     * @return 余额预警
     */
    @Override
    public BalanceWarning selectBalanceWarningByWarningId(Long warningId)
    {
        return balanceWarningMapper.selectBalanceWarningByWarningId(warningId);
    }

    /**
     * 查询余额预警列表
     *
     * @param balanceWarning 余额预警
     * @return 余额预警
     */
    @Override
    public List<BalanceWarning> selectBalanceWarningList(BalanceWarning balanceWarning)
    {
        return balanceWarningMapper.selectBalanceWarningList(balanceWarning);
    }

    /**
     * 根据机构ID查询预警记录
     *
     * @param institutionId 机构ID
     * @return 余额预警集合
     */
    @Override
    public List<BalanceWarning> selectBalanceWarningByInstitutionId(Long institutionId)
    {
        return balanceWarningMapper.selectBalanceWarningByInstitutionId(institutionId);
    }

    /**
     * 新增余额预警
     *
     * @param balanceWarning 余额预警
     * @return 结果
     */
    @Override
    public int insertBalanceWarning(BalanceWarning balanceWarning)
    {
        balanceWarning.setCreateTime(DateUtils.getNowDate());
        return balanceWarningMapper.insertBalanceWarning(balanceWarning);
    }

    /**
     * 修改余额预警
     *
     * @param balanceWarning 余额预警
     * @return 结果
     */
    @Override
    public int updateBalanceWarning(BalanceWarning balanceWarning)
    {
        balanceWarning.setUpdateTime(DateUtils.getNowDate());
        return balanceWarningMapper.updateBalanceWarning(balanceWarning);
    }

    /**
     * 批量删除余额预警
     *
     * @param warningIds 需要删除的余额预警主键
     * @return 结果
     */
    @Override
    public int deleteBalanceWarningByWarningIds(Long[] warningIds)
    {
        return balanceWarningMapper.deleteBalanceWarningByWarningIds(warningIds);
    }

    /**
     * 删除余额预警信息
     *
     * @param warningId 余额预警主键
     * @return 结果
     */
    @Override
    public int deleteBalanceWarningByWarningId(Long warningId)
    {
        return balanceWarningMapper.deleteBalanceWarningByWarningId(warningId);
    }

    /**
     * 获取预警统计数据
     *
     * @param institutionId 机构ID
     * @return 统计结果
     */
    @Override
    public Map<String, Object> getWarningStatistics(Long institutionId)
    {
        Map<String, Object> statistics = new HashMap<>();

        // 统计各类型预警数量
        List<Map<String, Object>> typeStats = balanceWarningMapper.selectWarningStatisticsByType(institutionId);
        int criticalCount = 0;
        int warningCount = 0;
        int noticeCount = 0;

        for (Map<String, Object> stat : typeStats) {
            String warningType = (String) stat.get("warningType");
            Long count = (Long) stat.get("count");

            if ("1".equals(warningType)) {
                criticalCount = count.intValue();
            } else if ("2".equals(warningType)) {
                warningCount = count.intValue();
            } else if ("3".equals(warningType)) {
                noticeCount = count.intValue();
            }
        }

        // 统计处理状态
        List<Map<String, Object>> statusStats = balanceWarningMapper.selectWarningStatisticsByStatus(institutionId);
        int unhandledCount = 0;
        int handledCount = 0;

        for (Map<String, Object> stat : statusStats) {
            String warningStatus = (String) stat.get("warningStatus");
            Long count = (Long) stat.get("count");

            if ("0".equals(warningStatus)) {
                unhandledCount = count.intValue();
            } else if ("1".equals(warningStatus)) {
                handledCount = count.intValue();
            }
        }

        statistics.put("totalWarnings", criticalCount + warningCount + noticeCount);
        statistics.put("criticalWarnings", criticalCount);
        statistics.put("warningCount", warningCount);
        statistics.put("noticeCount", noticeCount);
        statistics.put("unhandledCount", unhandledCount);
        statistics.put("handledCount", handledCount);

        return statistics;
    }

    /**
     * 获取严重预警账户
     *
     * @param institutionId 机构ID
     * @return 严重预警列表
     */
    @Override
    public List<BalanceWarning> selectCriticalWarnings(Long institutionId)
    {
        return balanceWarningMapper.selectCriticalWarnings(institutionId);
    }

    /**
     * 批量处理预警
     *
     * @param warningIds 预警ID列表
     * @param handleStatus 处理状态
     * @param handleRemark 处理意见
     * @param handleBy 处理人
     * @return 结果
     */
    @Override
    public int batchHandleWarnings(List<Long> warningIds, String handleStatus, String handleRemark, String handleBy)
    {
        int result = 0;
        for (Long warningId : warningIds) {
            BalanceWarning warning = new BalanceWarning();
            warning.setWarningId(warningId);
            warning.setWarningStatus(handleStatus);
            warning.setHandleRemark(handleRemark);
            warning.setHandler(handleBy);
            warning.setHandleTime(new Date());

            result += balanceWarningMapper.updateBalanceWarning(warning);
        }
        return result;
    }

    /**
     * 发送预警通知
     *
     * @param warningId 预警ID
     * @return 结果
     */
    @Override
    public int sendWarningNotification(Long warningId)
    {
        BalanceWarning warning = balanceWarningMapper.selectBalanceWarningByWarningId(warningId);
        if (warning != null) {
            // TODO: 实现通知发送逻辑（短信、邮件、微信等）

            // 更新通知状态
            warning.setNotifyStatus("1");
            warning.setLastNotifyTime(new Date());
            return balanceWarningMapper.updateBalanceWarning(warning);
        }
        return 0;
    }

    /**
     * 自动生成预警记录
     *
     * @param institutionId 机构ID
     * @return 结果
     */
    @Override
    public int autoGenerateWarnings(Long institutionId)
    {
        // TODO: 实现自动预警生成逻辑
        // 1. 查询余额不足的账户
        // 2. 计算可用月数
        // 3. 根据规则生成预警记录
        return 0;
    }

    /**
     * 清除已处理预警
     *
     * @param institutionId 机构ID
     * @return 结果
     */
    @Override
    public int clearHandledWarnings(Long institutionId)
    {
        return balanceWarningMapper.deleteHandledWarnings(institutionId);
    }

    /**
     * 检查并生成预警记录
     *
     * @param accountId 账户ID
     * @param elderId 老人ID
     * @param institutionId 机构ID
     * @param currentBalance 当前余额
     * @param monthlyFee 月费用
     * @return 预警记录
     */
    @Override
    public BalanceWarning checkAndGenerateWarning(Long accountId, Long elderId, Long institutionId,
                                                BigDecimal currentBalance, BigDecimal monthlyFee)
    {
        if (monthlyFee == null || monthlyFee.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        // 计算可用月数
        BigDecimal monthsDecimal = currentBalance.divide(monthlyFee, 2, BigDecimal.ROUND_DOWN);
        int availableMonths = monthsDecimal.intValue();

        String warningType = null;
        String warningMessage = null;

        if (availableMonths <= 0) {
            warningType = "1"; // 严重
            warningMessage = "账户余额已用尽，请立即充值";
        } else if (availableMonths <= 1) {
            warningType = "1"; // 严重
            warningMessage = "账户余额不足1个月，请尽快充值";
        } else if (availableMonths <= 2) {
            warningType = "2"; // 警告
            warningMessage = "账户余额不足2个月，建议充值";
        } else if (availableMonths <= 3) {
            warningType = "3"; // 提示
            warningMessage = "账户余额不足3个月，请关注";
        } else {
            return null; // 无需预警
        }

        // 检查是否已存在未处理的相同预警
        BalanceWarning existingWarning = balanceWarningMapper.selectUnhandledWarning(accountId, warningType);
        if (existingWarning != null) {
            return existingWarning;
        }

        // 创建新的预警记录
        BalanceWarning warning = new BalanceWarning();
        warning.setAccountId(accountId);
        warning.setElderId(elderId);
        warning.setInstitutionId(institutionId);
        warning.setWarningType(warningType);
        warning.setCurrentBalance(currentBalance);
        warning.setAvailableMonths(availableMonths);
        warning.setMonthlyFee(monthlyFee);
        warning.setWarningThreshold(monthlyFee.multiply(new BigDecimal(2))); // 默认2个月费用作为阈值
        warning.setWarningMessage(warningMessage);
        warning.setWarningStatus("0"); // 未处理
        warning.setNotifyStatus("0"); // 未通知

        insertBalanceWarning(warning);
        return warning;
    }
}