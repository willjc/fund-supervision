package com.ruoyi.service.pension;

import java.util.List;
import java.util.Map;
import com.ruoyi.domain.pension.BalanceWarning;

/**
 * 余额预警Service接口
 *
 * @author ruoyi
 * @date 2025-10-29
 */
public interface IBalanceWarningService
{
    /**
     * 查询余额预警
     *
     * @param warningId 余额预警主键
     * @return 余额预警
     */
    public BalanceWarning selectBalanceWarningByWarningId(Long warningId);

    /**
     * 查询余额预警列表
     *
     * @param balanceWarning 余额预警
     * @return 余额预警集合
     */
    public List<BalanceWarning> selectBalanceWarningList(BalanceWarning balanceWarning);

    /**
     * 根据机构ID查询预警记录
     *
     * @param institutionId 机构ID
     * @return 余额预警集合
     */
    public List<BalanceWarning> selectBalanceWarningByInstitutionId(Long institutionId);

    /**
     * 新增余额预警
     *
     * @param balanceWarning 余额预警
     * @return 结果
     */
    public int insertBalanceWarning(BalanceWarning balanceWarning);

    /**
     * 修改余额预警
     *
     * @param balanceWarning 余额预警
     * @return 结果
     */
    public int updateBalanceWarning(BalanceWarning balanceWarning);

    /**
     * 批量删除余额预警
     *
     * @param warningIds 需要删除的余额预警主键集合
     * @return 结果
     */
    public int deleteBalanceWarningByWarningIds(Long[] warningIds);

    /**
     * 删除余额预警信息
     *
     * @param warningId 余额预警主键
     * @return 结果
     */
    public int deleteBalanceWarningByWarningId(Long warningId);

    /**
     * 获取预警统计数据
     *
     * @param institutionId 机构ID
     * @return 统计结果
     */
    public Map<String, Object> getWarningStatistics(Long institutionId);

    /**
     * 获取严重预警账户
     *
     * @param institutionId 机构ID
     * @return 严重预警列表
     */
    public List<BalanceWarning> selectCriticalWarnings(Long institutionId);

    /**
     * 批量处理预警
     *
     * @param warningIds 预警ID列表
     * @param handleStatus 处理状态
     * @param handleRemark 处理意见
     * @param handleBy 处理人
     * @return 结果
     */
    public int batchHandleWarnings(List<Long> warningIds, String handleStatus, String handleRemark, String handleBy);

    /**
     * 发送预警通知
     *
     * @param warningId 预警ID
     * @return 结果
     */
    public int sendWarningNotification(Long warningId);

    /**
     * 自动生成预警记录
     *
     * @param institutionId 机构ID
     * @return 结果
     */
    public int autoGenerateWarnings(Long institutionId);

    /**
     * 清除已处理预警
     *
     * @param institutionId 机构ID
     * @return 结果
     */
    public int clearHandledWarnings(Long institutionId);

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
    public BalanceWarning checkAndGenerateWarning(Long accountId, Long elderId, Long institutionId,
                                                  java.math.BigDecimal currentBalance, java.math.BigDecimal monthlyFee);
}