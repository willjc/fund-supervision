package com.ruoyi.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.domain.BedInfo;
import org.apache.ibatis.annotations.Param;

/**
 * 床位信息Mapper接口
 *
 * @author ruoyi
 * @date 2025-10-28
 */
public interface BedInfoMapper
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
     * 修改床位状态
     *
     * @param bedId 床位ID
     * @param bedStatus 床位状态
     * @return 结果
     */
    public int updateBedStatus(@Param("bedId") Long bedId, @Param("bedStatus") String bedStatus);

    /**
     * 删除床位信息
     *
     * @param bedId 床位信息主键
     * @return 结果
     */
    public int deleteBedInfoByBedId(Long bedId);

    /**
     * 批量删除床位信息
     *
     * @param bedIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBedInfoByBedIds(Long[] bedIds);

    /**
     * 根据机构ID、房间号和床位号查询床位
     *
     * @param institutionId 机构ID
     * @param roomNumber 房间号
     * @param bedNumber 床位号
     * @return 床位信息
     */
    public BedInfo selectBedByRoomAndBedNumber(@Param("institutionId") Long institutionId,
                                                @Param("roomNumber") String roomNumber,
                                                @Param("bedNumber") String bedNumber);

    /**
     * 统计机构床位信息
     *
     * @param institutionId 机构ID
     * @return 床位统计Map {totalBeds: 总数, availableBeds: 可定数}
     */
    public Map<String, Object> selectBedStatistics(Long institutionId);
}