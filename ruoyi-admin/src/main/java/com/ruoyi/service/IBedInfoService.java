package com.ruoyi.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.domain.BedInfo;

/**
 * 床位信息Service接口
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public interface IBedInfoService
{
    /**
     * 查询床位信息
     *
     * @param bedId 床位信息主键
     * @return 床位信息
     */
    public BedInfo selectBedInfoByBedId(Long bedId);

    /**
     * 查询床位信息列表
     *
     * @param bedInfo 床位信息
     * @return 床位信息集合
     */
    public List<BedInfo> selectBedInfoList(BedInfo bedInfo);

    /**
     * 查询可用床位列表
     *
     * @param institutionId 机构ID
     * @return 可用床位集合
     */
    public List<BedInfo> selectAvailableBeds(Long institutionId);

    /**
     * 新增床位信息
     *
     * @param bedInfo 床位信息
     * @return 结果
     */
    public int insertBedInfo(BedInfo bedInfo);

    /**
     * 修改床位信息
     *
     * @param bedInfo 床位信息
     * @return 结果
     */
    public int updateBedInfo(BedInfo bedInfo);

    /**
     * 批量删除床位信息
     *
     * @param bedIds 需要删除的床位信息主键集合
     * @return 结果
     */
    public int deleteBedInfoByBedIds(Long[] bedIds);

    /**
     * 删除床位信息信息
     *
     * @param bedId 床位信息主键
     * @return 结果
     */
    public int deleteBedInfoByBedId(Long bedId);

    /**
     * 导出床位信息
     *
     * @param bedInfo 查询条件
     * @return 床位信息列表
     */
    public List<BedInfo> exportBedInfo(BedInfo bedInfo);

    /**
     * 批量导入床位信息
     *
     * @param bedList 床位信息列表
     * @param institutionId 机构ID
     * @param updateSupport 是否更新已存在的床位
     * @return 导入结果消息
     */
    public String importBedInfo(List<BedInfo> bedList, Long institutionId, boolean updateSupport);

    /**
     * 获取机构床位统计信息
     *
     * @param institutionId 机构ID
     * @return 床位统计信息 {totalBeds: 总床位, availableBeds: 可定床位}
     */
    public Map<String, Object> getBedStatistics(Long institutionId);
}