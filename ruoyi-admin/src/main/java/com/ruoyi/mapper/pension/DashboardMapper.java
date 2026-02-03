package com.ruoyi.mapper.pension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 首页数据查询Mapper
 *
 * @author ruoyi
 * @date 2025-02-04
 */
public interface DashboardMapper
{
    /**
     * 根据用户ID获取关联的机构ID列表
     *
     * @param userId 用户ID
     * @return 机构ID列表
     */
    List<Long> selectInstitutionIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询当日订单统计（订单数、总额）
     *
     * @param institutionIds 机构ID列表（null表示查询所有）
     * @return 订单统计数据
     */
    Map<String, Object> selectTodayOrderStats(@Param("institutionIds") List<Long> institutionIds);

    /**
     * 查询账户余额汇总（服务费、押金、会员费）
     *
     * @param institutionIds 机构ID列表
     * @return 账户余额汇总数据
     */
    Map<String, Object> selectAccountBalanceSummary(@Param("institutionIds") List<Long> institutionIds);

    /**
     * 查询基本户余额（监管账户支出到基本账户的累计金额）
     *
     * @param institutionIds 机构ID列表
     * @return 基本户余额
     */
    BigDecimal selectBasicAccountBalance(@Param("institutionIds") List<Long> institutionIds);

    /**
     * 查询当月服务费拨付统计
     *
     * @param institutionIds 机构ID列表
     * @return 拨付统计数据
     */
    Map<String, Object> selectMonthTransferStats(@Param("institutionIds") List<Long> institutionIds);

    /**
     * 查询押金申请统计（按状态分组）
     *
     * @param institutionIds 机构ID列表
     * @return 押金申请统计列表
     */
    List<Map<String, Object>> selectDepositApplyStats(@Param("institutionIds") List<Long> institutionIds);

    /**
     * 查询性别分布
     *
     * @param institutionIds 机构ID列表
     * @return 性别分布数据
     */
    List<Map<String, Object>> selectGenderDistribution(@Param("institutionIds") List<Long> institutionIds);

    /**
     * 查询年龄分布
     *
     * @param institutionIds 机构ID列表
     * @return 年龄分布数据
     */
    List<Map<String, Object>> selectAgeDistribution(@Param("institutionIds") List<Long> institutionIds);

    /**
     * 查询护理等级分布
     *
     * @param institutionIds 机构ID列表
     * @return 护理等级分布数据
     */
    List<Map<String, Object>> selectCareLevelDistribution(@Param("institutionIds") List<Long> institutionIds);
}
