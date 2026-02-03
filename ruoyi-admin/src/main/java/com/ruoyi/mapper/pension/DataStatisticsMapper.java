package com.ruoyi.mapper.pension;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

/**
 * 数据统计Mapper接口
 *
 * @author ruoyi
 * @date 2025-02-04
 */
public interface DataStatisticsMapper
{
    /**
     * 查询总体概览列表
     *
     * @param districtCode 区县代码
     * @return 总体概览数据
     */
    List<Map<String, Object>> selectOverallList(@Param("districtCode") String districtCode);

    /**
     * 查询机构情况列表
     *
     * @param districtCode 区县代码
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 机构情况数据
     */
    List<Map<String, Object>> selectInstitutionList(@Param("districtCode") String districtCode,
                                                     @Param("startDate") String startDate,
                                                     @Param("endDate") String endDate);

    /**
     * 查询资金情况列表
     *
     * @param districtCode 区县代码
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 资金情况数据
     */
    List<Map<String, Object>> selectFundList(@Param("districtCode") String districtCode,
                                             @Param("startDate") String startDate,
                                             @Param("endDate") String endDate);

    /**
     * 查询预警情况列表
     *
     * @param districtCode 区县代码
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 预警情况数据
     */
    List<Map<String, Object>> selectWarningList(@Param("districtCode") String districtCode,
                                                @Param("startDate") String startDate,
                                                @Param("endDate") String endDate);

    /**
     * 查询机构详细列表
     *
     * @param districtCode 区县代码
     * @param institutionName 机构名称
     * @return 机构详细数据
     */
    List<Map<String, Object>> selectInstitutionDetailList(@Param("districtCode") String districtCode,
                                                           @Param("institutionName") String institutionName);

    /**
     * 查询老人详细列表
     *
     * @param districtCode 区县代码
     * @param elderName 老人姓名
     * @param institutionId 机构ID
     * @return 老人详细数据
     */
    List<Map<String, Object>> selectElderDetailList(@Param("districtCode") String districtCode,
                                                    @Param("elderName") String elderName,
                                                    @Param("institutionId") String institutionId);

    /**
     * 查询资金详细列表
     *
     * @param districtCode 区县代码
     * @param elderName 老人姓名
     * @return 资金详细数据
     */
    List<Map<String, Object>> selectFundDetailList(@Param("districtCode") String districtCode,
                                                   @Param("elderName") String elderName);
}
